package main;


import functions.*;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
 
public class JettyServer extends AbstractHandler {
 
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("applicaton/json");
        baseRequest.setHandled(true);
        
    	response.setHeader("Access-Control-Allow-Origin", "*");
        
        System.out.println(target);

        if(request.getMethod().equals("OPTIONS")){
        	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        	respond(response,"",  200);
        } else if(request.getMethod().equals("POST") || request.getMethod().equals("PUT")){
	        
        	if(target.equals("/event/artist")){
        		
        		try {
	        		
	        		JSONObject params = getBody(request);
	        		
	        		respond(response, Artist.setArtistInfo(params).toString(), 200);
	        	
	        	
	        	} catch (Exception e){
	        		e.printStackTrace();
	        		respond(response, "error17", 500);
	        	}
        		
        	} else if (target.equals("/event/currentartist")) {
	        	
	        	try {
	        		
	        		JSONObject params = getBody(request);
	        		
	        		if(Authentication.getAuthentication(params) != null){
	        		
	        			respond(response, Artist.setCurrentArtist(params).toString(), 200);
	        		
	        		} else {
	        			respond(response, "bad password", 400);
	        		}
	        	
	        	} catch (Exception e){
	        		e.printStackTrace();
	        		respond(response, "error7", 500);
	        	}
	        } else if (target.equals("/event/nextartist")){
	        	
	        	try {
	        		JSONObject params = getBody(request);
	        		
	        		if(Authentication.getAuthentication(params) != null){
	        		
	        			respond(response, Artist.setNextArtist(params).toString(), 200);
	        		
	        		} else {
	        			respond(response, "bad password", 400);
	        		}
	        	
	        	} catch (Exception e){
	        		e.printStackTrace();
	        		respond(response, "error", 500);
	        	}
	        	
	        } else {
	        	respond(response, "error1", 400);
	        }
	        
        } else if (request.getMethod().equals("GET")) {
        	
        	if(target.equals("/event/artist")){
        		try {
     			   
	        		respond(response, Artist.getArtistInfo(request.getParameterMap()).toString(), 200);
	        	
	        	} catch (Exception e){
	        		e.printStackTrace();
	        		respond(response, "errorx", 500);
	        	}
        	} else if(target.equals("/getArtistTwitterHandle")){
        		try {
        			   
	        		respond(response, Artist.getArtistTwitterHandle(request.getParameterMap()).toString(), 200);
	        	
	        	} catch (Exception e){
	        		respond(response, "errorx", 500);
	        	}
        	} else if(target.equals("/getTweets")){
        		try {
	   
	        		respond(response, TwitterWrapper.getTweets(request.getParameterMap()).toString(), 200);
	        	
	        	} catch (Exception e){
	        		respond(response, "error", 500);
	        	}
        	} else if(target.equals("/event/currentartist")){
        		try {
	        		
	        		respond(response, Artist.getCurrentArtist(request.getParameterMap()).toString(), 200);
	        	
	        	} catch (Exception e){
	        		respond(response, "error", 500);
	        	}
        	} else if (target.equals("/event/artists")){
        		try {
	        		
	        		respond(response, Artist.getAllArtists(request.getParameterMap()).toString(), 200);
	        	
	        	} catch (Exception e){
	        		e.printStackTrace();
	        		respond(response, "got error", 500);
	        	}
        	} else if (target.equals("/event/nextartist")) {
        		
        		try {
	        		
	        		respond(response, Artist.getNextArtist(request.getParameterMap()).toString(), 200);
	        	
	        	} catch (Exception e){
	        		e.printStackTrace();
	        		respond(response, "error1", 500);
	        	}
        		
        		
        	} else if (target.equals("/getArtistNames")) {
        		try {
	        		
	        		respond(response, EchoNestQuery.getArtistNames(request.getParameterMap()).toString(), 200);
	        	
	        	} catch (Exception e){
	        		e.printStackTrace();
	        		respond(response, "got error", 500);
	        	}
        	} else if (target.equals("/getArtistImages")){
        		try {
	        		
	        		respond(response, EchoNestQuery.getArtistImages(request.getParameterMap()).toString(), 200);
	        	
	        	} catch (Exception e){
	        		e.printStackTrace();
	        		respond(response, "got error", 500);
	        	}
        	} else if (target.equals("/getArtistBiographies")) {
        		try {
	        		
	        		respond(response, EchoNestQuery.getArtistBiographies(request.getParameterMap()).toString(), 200);
	        	
	        	} catch (Exception e){
	        		e.printStackTrace();
	        		respond(response, "got error", 500);
	        	}        		
        	} else if (target.equals("/getArtistNews")) {
        		try {
	        		
	        		respond(response, EchoNestQuery.getArtistNews(request.getParameterMap()).toString(), 200);
	        	
	        	} catch (Exception e){
	        		e.printStackTrace();
	        		respond(response, "got error", 500);
	        	}       		
        	} else {
        		respond(response, "error2", 500);
        	}
        	
        } else {
        	System.out.println(request.getMethod());
        	respond(response, "error", 400);
        }
        
    }
 
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new JettyServer());
        server.start();
        server.join();
    }
    
    public static JSONObject getBody(HttpServletRequest request)
    		  throws ServletException, IOException {


    		  StringBuffer jb = new StringBuffer();
    		  String line = null;
    		  try {
    		    BufferedReader reader = request.getReader();
    		    while ((line = reader.readLine()) != null)
    		      jb.append(line);
    		  } catch (Exception e) {
    			  System.out.println("burnin");
    			  e.printStackTrace();
    		  }

    		  
    		  JSONObject body = null;
    		  
    		  try {
    		    body = new JSONObject(jb.toString().trim());
    		  } catch (ParseException e) {
    		    // crash and burn
    			  System.out.println("crashin");
    		    throw new IOException("Error parsing JSON request string");
    		  }
    		  
      		  return body;

    		}


		public void respond(HttpServletResponse response, String body, int status){
			response.setStatus(status);
		
			try {
				response.getWriter().println(body);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

}
