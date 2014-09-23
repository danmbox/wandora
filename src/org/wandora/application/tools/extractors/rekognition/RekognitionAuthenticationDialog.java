/*
 * WANDORA
 * Knowledge Extraction, Management, and Publishing Application
 * http://wandora.org
 *
 * Copyright (C) 2004-2014 Wandora Team
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


package org.wandora.application.tools.extractors.rekognition;

import java.util.HashMap;
import javax.swing.JDialog;
import org.wandora.application.Wandora;
import org.wandora.application.gui.UIBox;
import org.wandora.application.gui.simple.SimpleButton;
import org.wandora.application.gui.simple.SimpleField;
import org.wandora.application.gui.simple.SimpleLabel;
import org.wandora.application.tools.extractors.rekognition.RekognitionConfiguration.AUTH_KEY;

/**
 *
 * @author Eero Lehtonen <eero.lehtonen@gripstudios.com>
 */
public class RekognitionAuthenticationDialog extends javax.swing.JPanel {

    private JDialog dialog;
    
    private HashMap<AUTH_KEY,String> auth;
    
    private boolean wasAccepted = false;
    
    /**
     * Creates new form RekognitionAuthenticationDialog
     */
    public RekognitionAuthenticationDialog() {
        initComponents();
    }

    public void open(Wandora w){
        dialog = new JDialog(w, true);
        dialog.setSize(400, 180);
        dialog.setResizable(false);
        dialog.add(this);
        dialog.setTitle("Rekognition API key and secret");
        UIBox.centerWindow(dialog, w);
        wasAccepted = false;

        dialog.setVisible(true); 
    }
    
    //Populate auth params and hide dialog on submit
    private void submit() {
        wasAccepted = true;
        this.auth = new HashMap<>();
        this.auth.put(AUTH_KEY.KEY,this.keyField.getText());
        this.auth.put(AUTH_KEY.SECRET,this.secretField.getText());
        
        this.dialog.setVisible(false);
    }
    
    private void cancel() {
        wasAccepted = false;
        this.dialog.setVisible(false);
    }
    
    
    public boolean wasAccepted() {
        return wasAccepted;
    }
    
    
    //Let the main UI get the auth params
    protected HashMap<AUTH_KEY,String> getAuth(){        
        return this.auth;
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

        keyLabel = new SimpleLabel();
        secretLabel = new SimpleLabel();
        descriptionLabel = new SimpleLabel();
        keyField = new SimpleField();
        secretField = new SimpleField();
        buttonPanel = new javax.swing.JPanel();
        submitButton = new SimpleButton();
        cancelButton = new SimpleButton();

        setLayout(new java.awt.GridBagLayout());

        keyLabel.setText("API key");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        add(keyLabel, gridBagConstraints);

        secretLabel.setText("API secret");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        add(secretLabel, gridBagConstraints);

        descriptionLabel.setText("<html>Please input the API key and API secret associated with your Rekognition account.</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 8, 4);
        add(descriptionLabel, gridBagConstraints);

        keyField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(keyField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(secretField, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.GridBagLayout());

        submitButton.setText("Submit");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        buttonPanel.add(submitButton, gridBagConstraints);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(buttonPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void keyFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keyFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_keyFieldActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        this.submit();
    }//GEN-LAST:event_submitButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.cancel();
    }//GEN-LAST:event_cancelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JTextField keyField;
    private javax.swing.JLabel keyLabel;
    private javax.swing.JTextField secretField;
    private javax.swing.JLabel secretLabel;
    private javax.swing.JButton submitButton;
    // End of variables declaration//GEN-END:variables
}
