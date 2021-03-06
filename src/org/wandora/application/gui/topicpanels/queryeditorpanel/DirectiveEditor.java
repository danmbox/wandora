/*
 * WANDORA
 * Knowledge Extraction, Management, and Publishing Application
 * http://wandora.org
 * 
 * Copyright (C) 2004-2015 Wandora Team
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wandora.application.gui.topicpanels.queryeditorpanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.wandora.application.Wandora;
import org.wandora.query2.Directive;
import org.wandora.query2.DirectiveUIHints;
import org.wandora.query2.DirectiveUIHints.Addon;
import org.wandora.query2.DirectiveUIHints.BoundParameter;
import org.wandora.query2.DirectiveUIHints.Constructor;
import org.wandora.query2.DirectiveUIHints.Parameter;
import org.wandora.query2.Operand;
import org.wandora.query2.TopicOperand;

/**
 *
 * @author olli
 */


public class DirectiveEditor extends javax.swing.JPanel {

    protected DirectiveUIHints.Constructor selectedConstructor;
    protected AbstractTypePanel[] constructorParamPanels;
    
    protected final ArrayList<AddonPanel> addonPanels=new ArrayList<AddonPanel>();
    
    protected DirectivePanel directivePanel;
    
    protected DirectiveUIHints hints;
    
    /**
     * Creates new form DirectiveEditor
     */
    public DirectiveEditor(DirectivePanel directivePanel,DirectiveUIHints hints) {
        this.directivePanel=directivePanel;
        initComponents();
        setDirective(hints);
        setDirectiveParameters(directivePanel.getDirectiveParameters());
    }
    
    public DirectivePanel getDirectivePanel(){
        return directivePanel;
    }
    
    public void setDirective(DirectiveUIHints hints){
        this.hints=hints;
        
        this.directiveLabel.setText(hints.getLabel());
        for(DirectiveUIHints.Constructor c : hints.getConstructors()){
            String label="(";
            boolean first=true;
            for(DirectiveUIHints.Parameter p : c.getParameters()){
                if(!first) label+=",";
                else first=false;
                label+=p.getLabel();
            }
            label+=")";
            constructorComboBox.addItem(new ConstructorComboItem(c,label));
        }
     
        DirectiveUIHints.Addon[] addons=hints.getAddons();
        if(addons!=null){
            for(DirectiveUIHints.Addon a : addons) {
                String label=a.getLabel()+"(";
                boolean first=true;
                for(DirectiveUIHints.Parameter p : a.getParameters()){
                    if(!first) label+=",";
                    else first=false;
                    label+=p.getLabel();
                }
                label+=")";
                addonComboBox.addItem(new AddonComboItem(a,label));            
            }
        }
    }    
    

    private class ConstructorComboItem {
        public DirectiveUIHints.Constructor c;
        public String label;
        public ConstructorComboItem(DirectiveUIHints.Constructor c,String label){
            this.c=c;
            this.label=label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
    
    private class AddonComboItem {
        public DirectiveUIHints.Addon a;
        public String label;
        public AddonComboItem(DirectiveUIHints.Addon a,String label){
            this.a=a;
            this.label=label;
        }

        @Override
        public String toString() {
            return label;
        }        
    }    
    
    public void saveChanges(){
        if(directivePanel!=null){
            directivePanel.saveDirectiveParameters(getDirectiveParameters());
        }
    }
    
    public void setDirectiveParameters(DirectiveParameters params){
        if(params==null){
            constructorComboBox.setSelectedIndex(0);
            populateParametersPanel(((ConstructorComboItem)constructorComboBox.getItemAt(0)).c);
        }
        else {
            Constructor c=params.constructor;
            for(int i=0;i<constructorComboBox.getItemCount();i++){
                Object o=constructorComboBox.getItemAt(i);
                if(o instanceof ConstructorComboItem){
                    if(((ConstructorComboItem)o).c.equals(c)) {
                        constructorComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

            this.selectedConstructor=c;
            populateParametersPanel(c);
            
            for(int i=0;i<constructorParamPanels.length;i++){
                AbstractTypePanel panel=constructorParamPanels[i];
                Object value=null;
                if(params.parameters.length>i && params.parameters[i]!=null) value=params.parameters[i].getValue();
                panel.setValue(value);
            }
            
            // TODO: Addon panels
        }        
    }
    
    public DirectiveParameters getDirectiveParameters(){
        Object o=constructorComboBox.getSelectedItem();
        if(o==null || !(o instanceof ConstructorComboItem)) return null;
        
        Constructor c=((ConstructorComboItem)o).c;
        BoundParameter[] constructorParams=getParameters(constructorParamPanels);
        
        AddonParameters[] addonParams=getAddonParameters();
        
        
        return new DirectiveParameters(c,constructorParams,addonParams);
    }
    
    public AddonParameters[] getAddonParameters(){
        AddonParameters[] ret=new AddonParameters[addonPanels.size()];
        for(int i=0;i<addonPanels.size();i++){
            AddonPanel panel=addonPanels.get(i);
            
            BoundParameter[] params=getParameters(panel.getParameterPanels());
            
            ret[i]=new AddonParameters(panel.getAddon(), params);
        }
        return ret;
    }
    
    public BoundParameter[] getParameters(AbstractTypePanel[] panels){
        BoundParameter[] ret=new BoundParameter[panels.length];
        for(int i=0;i<panels.length;i++){
            AbstractTypePanel paramPanel=panels[i];
            String s=paramPanel.getValueScript();
            
            Parameter param=paramPanel.getParameter();
            Object value=paramPanel.getValue();
            
            ret[i]=new BoundParameter(param, value);
        }
        return ret;
    }
    
    public void setParameters(AbstractTypePanel[] panels, BoundParameter[] values){
        for(int i=0;i<panels.length;i++){
            AbstractTypePanel paramPanel=panels[i];
            paramPanel.setValue(values[i]);
        }        
    }
    
    
    public static class DirectiveParameters {
        public Constructor constructor;
        public BoundParameter[] parameters;
        public AddonParameters[] addons;
        public DirectiveParameters(){}
        public DirectiveParameters(Constructor constructor, BoundParameter[] parameters, AddonParameters[] addons) {
            this.constructor = constructor;
            this.parameters = parameters;
            this.addons = addons;
        }
    }
    
    public static class AddonParameters {
        public Addon addon;
        public BoundParameter[] parameters;
        public AddonParameters(){}
        public AddonParameters(Addon addon, BoundParameter[] parameters) {
            this.addon = addon;
            this.parameters = parameters;
        }
    }

    
    public static AbstractTypePanel makeMultiplePanel(DirectiveUIHints.Parameter param,Class<? extends AbstractTypePanel> typePanel,String label,DirectivePanel directivePanel){
        MultipleParameterPanel p=new MultipleParameterPanel(param,typePanel,directivePanel);
        p.setLabel(label);
        return p;
    }
    
    public static Class<? extends AbstractTypePanel> getTypePanelClass(DirectiveUIHints.Parameter p){
        Class<?> cls=p.getType();
        if(cls.equals(Integer.class) || cls.equals(Integer.TYPE)) return IntegerParameterPanel.class;
        else if(cls.equals(String.class)) return StringParameterPanel.class;
        else if(cls.equals(TopicOperand.class)) return TopicOperandParameterPanel.class;
        else if(cls.equals(Operand.class)) return OperandParameterPanel.class;
        else if(Directive.class.isAssignableFrom(cls)) return DirectiveParameterPanel.class;
        // this is a guess really
        else if(cls.equals(Object.class)) return TopicOperandParameterPanel.class;

        else return UnknownParameterTypePanel.class;
    }
    
    public static AbstractTypePanel[] populateParametersPanel(JPanel panelContainer,DirectiveUIHints.Parameter[] parameters,AbstractTypePanel[] oldPanels,DirectivePanel directivePanel){
        if(oldPanels!=null){
            for(AbstractTypePanel p : oldPanels){
                p.disconnect();
            }
        }
        panelContainer.removeAll();
        
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1.0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        
        
        AbstractTypePanel[] panels=new AbstractTypePanel[parameters.length];
        
        for(int i=0;i<parameters.length;i++){
            DirectiveUIHints.Parameter p=parameters[i];
            Class<? extends AbstractTypePanel> panelCls=getTypePanelClass(p);
            if(panelCls==null) continue;
            
            AbstractTypePanel panel;
            if(p.isMultiple()){
                panel=makeMultiplePanel(p, panelCls, p.getLabel(),directivePanel);
                panel.setOrderingHint("0"+(i<10?"0":"")+i);
            }
            else {
                try{
                    java.lang.reflect.Constructor<? extends AbstractTypePanel> panelConstructor=panelCls.getConstructor(DirectiveUIHints.Parameter.class,DirectivePanel.class);
                    panel=panelConstructor.newInstance(p,directivePanel);
                    panel.setLabel(p.getLabel());
                    panel.setOrderingHint("0"+(i<10?"0":"")+i);
                    
                    if(panel instanceof DirectiveParameterPanel){
                        if(!p.getType().equals(Directive.class)){
                            Class<? extends Directive> cls=(Class<? extends Directive>)p.getType();
                            ((DirectiveParameterPanel)panel).setDirectiveType(cls);
                        }
                    }
                }catch(IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e){
                    Wandora.getWandora().handleError(e);
                    return null;
                }
            }
            
            panels[i]=panel;
            
            panelContainer.add(panel,gbc);
            gbc.gridy++;
        }
        
        return panels;
    }
    
    protected void populateParametersPanel(DirectiveUIHints.Constructor c){
        if(c==null) return;
        DirectiveUIHints.Parameter[] parameters=c.getParameters();
        this.constructorParamPanels=populateParametersPanel(constructorParameters,parameters,this.constructorParamPanels,directivePanel);
        this.revalidate();
        constructorParameters.repaint();        

    }
    
    public void removeAddon(AddonPanel addonPanel){
        synchronized(addonPanels){
            int index=addonPanels.indexOf(addonPanel);
            if(index<0) return;
            addonPanel.disconnect();
            GridBagLayout gbl=(GridBagLayout)addonPanelContainer.getLayout();
            for(int i=index+1;i<addonPanels.size();i++){
                AddonPanel p=addonPanels.get(i);
                GridBagConstraints gbc=gbl.getConstraints(p);
                gbc.gridy--;
                gbl.setConstraints(p, gbc);
            }
            addonPanels.remove(index);
            addonPanelContainer.remove(addonPanel);
        }
        this.revalidate();
        addonPanelContainer.repaint();        
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        directiveLabel = new javax.swing.JLabel();
        constructorComboBox = new javax.swing.JComboBox();
        constructorParametersScroll = new javax.swing.JScrollPane();
        constructorParameters = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        addAddonButton = new javax.swing.JButton();
        addonComboBox = new javax.swing.JComboBox();
        addonPanelContainer = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        directiveLabel.setText("Directive label");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(directiveLabel, gridBagConstraints);

        constructorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                constructorComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(constructorComboBox, gridBagConstraints);

        constructorParameters.setLayout(new java.awt.GridBagLayout());
        constructorParametersScroll.setViewportView(constructorParameters);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(constructorParametersScroll, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        addAddonButton.setText("Add addon");
        addAddonButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAddonButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(addAddonButton, gridBagConstraints);

        addonComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addonComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(addonComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(jPanel1, gridBagConstraints);

        addonPanelContainer.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(addonPanelContainer, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void constructorComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_constructorComboBoxActionPerformed
        Object o=constructorComboBox.getSelectedItem();
        ConstructorComboItem c=(ConstructorComboItem)o;
        this.selectedConstructor=c.c;
        populateParametersPanel(c.c);
    }//GEN-LAST:event_constructorComboBoxActionPerformed

    private void addAddonButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAddonButtonActionPerformed
        synchronized(addonPanels){
            Object o=addonComboBox.getSelectedItem();
            if(o==null || !(o instanceof AddonComboItem)) return;

            AddonComboItem aci=(AddonComboItem)o;
            Addon addon=aci.a;

            AddonPanel addonPanel=new AddonPanel(this,addon);
            GridBagConstraints gbc=new GridBagConstraints();
            gbc.fill=GridBagConstraints.HORIZONTAL;
            gbc.weightx=1.0;
            gbc.gridx=0;
            gbc.gridy=addonPanels.size();
            gbc.insets=new Insets(5, 0, 0, 0);
            addonPanelContainer.add(addonPanel,gbc);

            addonPanels.add(addonPanel);

        }
        this.revalidate();
        addonPanelContainer.repaint();

    }//GEN-LAST:event_addAddonButtonActionPerformed

    private void addonComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addonComboBoxActionPerformed

    }//GEN-LAST:event_addonComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAddonButton;
    private javax.swing.JComboBox addonComboBox;
    private javax.swing.JPanel addonPanelContainer;
    private javax.swing.JComboBox constructorComboBox;
    private javax.swing.JPanel constructorParameters;
    private javax.swing.JScrollPane constructorParametersScroll;
    private javax.swing.JLabel directiveLabel;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
