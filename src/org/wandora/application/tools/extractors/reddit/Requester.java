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

package org.wandora.application.tools.extractors.reddit;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 
 * A simple helper class to handle rate limited requests
 * 
 * This class wraps the Unirest async request API into a queue of requests, that
 * are executed at most once per two seconds.
 * 
 * @author Eero Lehtonen <eero.lehtonen@gripstudios.com>
 */
class Requester {
  
  private static final int REQUEST_DELAY = 2000; // 1 req / 2 s
  private static final int INTERVAL = 100;

  private Timer timer;
  private RequestChecker checker;
  
  private int nRunning;

  public Requester(String ua) {
    Unirest.clearDefaultHeaders();
    Unirest.setDefaultHeader("User-Agent", ua);
    this.timer = new Timer();
    this.checker = new RequestChecker();
    timer.schedule(this.checker, 0, INTERVAL);
  }

  protected void doRequest(BaseRequest hr, ParseCallback<JsonNode> callback) {

    Request r;
    r = new Request(hr, callback);
    
    this.checker.addRequest(r); // Queue up the request.
  }
  
  protected void cancel(){
    timer.cancel();
  }
  
  protected boolean hasJobs(){
    return !this.checker.requests.isEmpty() || nRunning > 0;
  }

  class Request {

    private BaseRequest request;
    private ParseCallback<JsonNode> callback;
    
    Request(BaseRequest r, ParseCallback<JsonNode> callback) {
      this.request = r;
      this.callback = callback;
    }
    
    protected void run(){
      
      this.request.asJsonAsync(new Callback<JsonNode>() {

        @Override
        public void completed(HttpResponse<JsonNode> response) {
          callback.run(response);
        }

        @Override
        public void failed(UnirestException e) {
          try {
            HttpResponse<String> s = request.asString();
            callback.error(e, s.getBody());
          } catch (Exception e2) {
            callback.error(e2);
          }
        }

        @Override
        public void cancelled() {
          callback.error(new Exception("Request was cancelled"));
        }
      });
    }
    
  }
  
  
  class RequestChecker extends TimerTask{

    private ArrayList<Request> requests;
    private long lastRequest;
    
    RequestChecker(){
      this.requests = new ArrayList<>();
      this.lastRequest = System.currentTimeMillis();
    }
    
    public void addRequest(Request r){
      this.requests.add(r);
    }
    
    @Override
    public void run(){
            
      long currentTime = System.currentTimeMillis();
      long timeDelta = currentTime - this.lastRequest;
      
      // Skip execution if last request was done recently
      if(this.requests.isEmpty() || timeDelta < REQUEST_DELAY) return;
      
      nRunning++;
      Request r = this.requests.remove(0);
      
      r.run();
      this.lastRequest = System.currentTimeMillis();
      
      nRunning--;
    }
    
  }
  
}
