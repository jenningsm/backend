package functions;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import Exception.MyException;  // 400 = wrong input,  500 = ourside not working

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class TwitterWrapper {
    
    private Twitter twitter;
    
    public TwitterWrapper()  // init of twitter instance
    {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
        .setOAuthConsumerKey("mdatrN8YnVlOwxBb2vxsYg")
        .setOAuthConsumerSecret("BwNEYmamWAStvEMtwQmuAPgUrmIWEpMIauiVMWLHgI")
        .setOAuthAccessToken("36005075-clpFYkVZXmyCyRXnUuSfzqkIlrAFnTfrq40wOvbo")
        .setOAuthAccessTokenSecret("3fXn5riqkXnX0HmVAGR480t5BC62zx0eD89YEHxRo");
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }
    
    public JSONObject getTweetsByUser (String username ) throws MyException
    {
    
        
        List<Status> res = null;
        JSONObject json = new JSONObject();
        try {
             res = twitter.getUserTimeline(username);
            } 
        catch (TwitterException e) 
            { throw new MyException(e.getErrorMessage(), 500); }
        JSONArray arr = new JSONArray();
        JSONObject jo = null;
        try{
 
          for (Status status :  res) {
                 
            jo = new JSONObject();
            jo.put("name", status.getUser().getScreenName());
            jo.put("text", status.getText());
            jo.put("time", status.getCreatedAt().getTime());
            
            String URL = "http://twitter.com/" + status.getUser().getScreenName()
                           + "/status/" + status.getId();
            jo.put("url", URL);
            arr.put(jo);
          }
        
        
          json.put("tweets", arr );
        } catch(JSONException e)
               { throw new MyException(
                       "JSONException:"+e.getMessage(), 500 );}
        
        return json;
    }
    
    public JSONObject getTweetsBySearch (String query ) throws MyException
    {
        Query q = new Query(query);
        QueryResult result = null;
        JSONObject json = new JSONObject();
        try {
              result = twitter.search(q);
            } 
        catch (TwitterException e) { throw new MyException(e.getErrorMessage() 
                ,400); }
        
        JSONArray arr = new JSONArray();
        JSONObject jo = null;
        try{
 
          for (Status status :  result.getTweets()) {
                 
            jo = new JSONObject();
            jo.put("name", status.getUser().getScreenName());
            jo.put("text", status.getText());
            jo.put("time", status.getCreatedAt().getTime());
            
            String URL = "http://twitter.com/" + status.getUser().getScreenName()
                           + "/status/" + status.getId();
            jo.put("url", URL);
            arr.put(jo);
          }
        
        
          json.put("tweets", arr );
        } catch(JSONException e)
               { throw new MyException(
                       "JSONException:"+e.getMessage(), 500 );}
        return json;
    }
    
    
    //   Called by anything, just need to catch MyException
    // Give a Map object that has a single key value pair
    //  Key == "username" or "name", then gets timeline of twitter user
    //         with username given by the value 
    // else it will just do a generic serach using the value as the query value
    static public JSONObject getTweets (Map<String,String[]> m) throws MyException
    {
       try{
           TwitterWrapper tw = new TwitterWrapper();
       /* for (Map.Entry<String,String> entry : m.entrySet() ){
            String key = entry.getKey();
            String val = entry.getValue();
            if (key.equals("username" ) || key.equals("name"))
            {  return tw.getTweetsByUser(val);}
            else 
            {  return tw.getTweetsBySearch(val);}
               
        }*/
        String type = m.get("searchType")[0];
        if(type.equals("username") || type.equals("name")){     	
        	return tw.getTweetsByUser(m.get("searchValue")[0]);
        } else {
        	return tw.getTweetsBySearch(m.get("searchValue")[0]);
        }
        
       } catch (MyException e) {throw e;}

    }
    
}