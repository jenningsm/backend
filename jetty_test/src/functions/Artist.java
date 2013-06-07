package functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.PreparedStatement;

public class Artist {
	Artist(){
	}
	
	public static JSONObject setArtistInfo(JSONObject param) throws SQLException {
		
		String pic = param.getString("image");
		String bio = param.getString("bio");
		String yturl = param.getString("youtube_url");
		String scurl = param.getString("soundcloud_url");
		
		Connection con = null;
    	PreparedStatement pst = null;
        ResultSet rs = null;
	    con = DbManager.getConnection(false);
	    
	    pst = (PreparedStatement) con.prepareStatement("INSERT INTO Artist (name) " +
	    	    "VALUES (?)");
	            pst.setString(1, param.getString("name"));

	            //execute query
	            pst.executeUpdate();
	            
	            pst = (PreparedStatement) con.prepareStatement("SELECT artistID " +
	            		"FROM Artist" +
	            		" WHERE name = ?");//use inner join for query
	            		
	            		pst.setString(1,param.getString("name"));//set the eventID
	            		
	            		//execute query
	            		pst.execute();//execute query
	            			
	            		rs=pst.getResultSet();
	            		rs.next();
	            		
	            		int artistID = rs.getInt("artistID");     
	    
	    
	    //create query
		pst = (PreparedStatement) con.prepareStatement("INSERT INTO ArtistEvent (eventID, artistID, biography, primary_picture, soundcloud_url, youtube_url) " +
	    "VALUES (?,?,?,?,?,?)"+
		" ON DUPLICATE KEY UPDATE biography = ?, primary_picture = ?, soundcloud_url = ?, youtube_url = ?");
        pst.setInt(1, param.getInt("eventID"));
        pst.setInt(2, artistID);
        pst.setString(3, bio);
        pst.setString(4, pic);
        pst.setString(5, scurl);
        pst.setString(6, yturl);
        pst.setString(7, bio);
        pst.setString(8, pic);
        pst.setString(9, scurl);
        pst.setString(10, yturl);
        //execute query
        pst.executeUpdate();
        
        //close the connection
        DbManager.closeConnection(con); 
		
		return new JSONObject("{\"status\":\"success\"}");
		
	}
	
	public static JSONObject getArtistInfo(Map<String,String[]> params) throws SQLException {
		
		int eventID = Integer.parseInt(params.get("eventID")[0]);
		int artistID = Integer.parseInt(params.get("artistID")[0]);
		
		//setup connection
    	Connection con = null;
    	PreparedStatement pst = null;
        ResultSet rs = null;   	
		con = DbManager.getConnection(true);
		
		//create query		
		pst = (PreparedStatement) con.prepareStatement("SELECT biography, primary_picture, youtube_url, soundcloud_url " + 
		"FROM ArtistEvent" +
		" WHERE ArtistEvent.eventID = ? AND ArtistEvent.artistID = ?");//use inner join for query
		
		pst.setInt(1,eventID);//set the eventID
		pst.setInt(2,artistID);
		
		JSONObject ret = new JSONObject();
		
		//execute query
		pst.execute();//execute query
			
		rs=pst.getResultSet();
		rs.next();
		
		ret.put("bio", rs.getString("biography"));
		ret.put("image", rs.getString("primary_picture"));
		ret.put("video", rs.getString("youtube_url"));
		ret.put("sound", rs.getString("soundcloud_url"));
		
		pst = (PreparedStatement) con.prepareStatement("SELECT name" +
				" FROM Artist" +
				" WHERE artistID = ?");
				
				pst.setInt(1,artistID);//set the eventID
				
				//execute query
			pst.execute();//execute query
					
				rs=pst.getResultSet();
				rs.next();
            ret.put("name", rs.getString("name"));
				
          //close connection
            DbManager.closeConnection(con);
		
		return ret;
	}
	
	/*
	 * Function: getAllArtists
	 * example call: Artst.getAllArtists(Map object);
	 * Parameters: takes in "eventID" --> some integer in Map<String, Integer>
	 * Use: Function will retrieve all artist id's, names, and primary_picture URLS
	 * Data will be JSON encoded
	 * THROWS: SQLException, JSONException
	 * Sample output: {"artists":[{"artistID":1,"name":"TI","primary_picture":"dummyURL1"},{"artistID":2,"name":"Lupe Fiasco","primary_picture":"dummyURL2"},{"artistID":3,"name":"JayZ","primary_picture":"dummURL3"}]}
	 */
    public static JSONObject getAllArtists(Map<String,String[]> param) throws SQLException, JSONException
    {
    	//retrieve parameters from input Map
    	int eventID = Integer.parseInt(param.get("eventID")[0]);
    	
    	//setup connection
    	Connection con = null;
    	PreparedStatement pst = null;
        ResultSet rs = null;   	
		con = DbManager.getConnection(true);
		
		//create query		
		pst = (PreparedStatement) con.prepareStatement("SELECT Artist.name, `ArtistEvent`.artistID, `ArtistEvent`.primary_picture " +
		"FROM `ArtistEvent`" +
		"INNER JOIN `Artist` " +
		"ON `ArtistEvent`.artistID = `Artist`.artistID " +
		"WHERE `ArtistEvent`.eventID = ?");//use inner join for query
		
		pst.setInt(1,eventID);//set the eventID
		
		//execute query
		boolean isResult = pst.execute();//execute query
			
		//Fetch results and then JSON ENCODE them
			JSONArray jArray = new JSONArray();//create jArray
            do {
                rs = pst.getResultSet();//fetch results

                while (rs.next()) { //encode into JSONarray
                	String  name_json=rs.getString("name");
                    int id_json=rs.getInt("artistID");
                    String pic_json=rs.getString("primary_picture");
                    JSONObject jobj = new JSONObject();
                    jobj.put("artistID", id_json);
                    jobj.put("name", name_json);
                    jobj.put("primary_picture", pic_json);
                    jArray.put(jobj);
                }

                isResult = pst.getMoreResults();
            } while (isResult);
            
            
          //close connection
            DbManager.closeConnection(con);
            
            JSONObject jObjArtists= new JSONObject();
            jObjArtists.put("artists", jArray);
            return jObjArtists;
	
    }
    
	/* Function: setCurrentArtist
	 * example call: Artist.setCurrentArtist(JSONObject object);
	 * Parameters: "artistID"-->some integer, "eventID" --> some integer in JSONObject
	 * USE: Front end will call function with ID of artist to be new current artist.
	 * Function will then INSERT into the CurrentArtist table, this artist for the respective eventID and UPDATE on Duplicate key.
	 * Returns JSON "status" with a value of true if succeeded; JSON =  null if failed. 
	 * THROWS: SQLException, JSONException
	 *  */
   //sample output: {"status":"success"}
	public static JSONObject setCurrentArtist(JSONObject param) throws SQLException, JSONException 
	{
		//retrieve parameters from input JSON object
		int artistID = param.getInt("artistID");
		int eventID = param.getInt("eventID");
		
		//create connection and setup variables needed
		Connection con = null;
    	PreparedStatement pst = null;
        ResultSet rs = null;
        JSONObject job = new JSONObject();
	    con = DbManager.getConnection(false);
	    
	    //create query
		pst = (PreparedStatement) con.prepareStatement("INSERT INTO `CurrentArtist` (eventID, artistID) VALUES (?,?)"+
				" ON DUPLICATE KEY UPDATE artistID = ?");
        pst.setInt(1, eventID);
        pst.setInt(2, artistID);
        pst.setInt(3, artistID);//this will happen for updates!
        
        //execute query
        pst.executeUpdate();
        
        //close the connection
        DbManager.closeConnection(con); 
        
        //set status to success in output JSON object
        job.put("status","success");
        return job;
		
	}//end setCurrentArtist
	
	public static JSONObject setNextArtist(JSONObject param) throws SQLException, JSONException 
	{
		//retrieve parameters from input JSON object
		int artistID = param.getInt("artistID");
		int eventID = param.getInt("eventID");
		
		//create connection and setup variables needed
		Connection con = null;
    	PreparedStatement pst = null;
        ResultSet rs = null;
        JSONObject job = new JSONObject();
	    con = DbManager.getConnection(false);
	    
	    //create query
		pst = (PreparedStatement) con.prepareStatement("SELECT ArtistEvent.order FROM ArtistEvent WHERE eventID = ? AND artistID = (SELECT artistID From CurrentArtist WHERE eventID = ?)");
        pst.setInt(1, eventID);
        pst.setInt(2, eventID);
        
        //execute query
        rs = pst.executeQuery();
        
        int currentorder;
        if(rs.next()){
	        currentorder = rs.getInt(1);
        } else {
        	return new JSONObject("{}");
        }
        
        pst = (PreparedStatement) con.prepareStatement("UPDATE ArtistEvent SET ArtistEvent.order = ? WHERE eventID = ? AND ArtistEvent.order = ?");
        pst.setInt(1, currentorder + 2);
        pst.setInt(2, eventID);
        pst.setInt(3, currentorder + 1);
        
        pst.executeUpdate();
        
        pst = (PreparedStatement) con.prepareStatement("UPDATE ArtistEvent SET ArtistEvent.order = ? WHERE eventID = ? AND ArtistID = ?");
        pst.setInt(1, currentorder + 1);
        pst.setInt(2, eventID);
        pst.setInt(3, artistID);
        
        pst.executeUpdate();
       
        
        //close the connection
        DbManager.closeConnection(con); 
        
        //set status to success in output JSON object
        job.put("status","success");
        return job;
		
	}//end setCurrentArtist
	
	
	/* Function: getCurrentArtist 
	 * example call: Artist.getCurrentArtist(Map object);
	 * Parameters: "eventID"--> integer in Map<String, Integer>
	 * USE: Front end will call function with ID of event.
	 * Function will search Current-Artist table for artistID associated with eventID.
	 * Then, function will query ArtistEvent table for the name associated with artistID
	 * Returns JSON object with "name" = artist name, "artistID" = artistID; JSON =  null if failed.  
	 * THROWS: SQLException, JSONException
	 * */
	//example output: {"artistID":2,"name":"Lupe Fiasco"}
	public static JSONObject getCurrentArtist(Map<String,String[]> param) throws SQLException, JSONException
	{
		//retrieve parameters from Map
		int eventID = Integer.parseInt(param.get("eventID")[0]);
		
		//setup connection and other variables
		Connection con = null;
    	PreparedStatement pst = null;
        ResultSet rs = null;
        JSONObject json_ret = new JSONObject();
		con = DbManager.getConnection(true);
		
		
		//create, execute query for artistID
		pst = (PreparedStatement) con.prepareStatement("SELECT artistID FROM CurrentArtist WHERE eventID = ?");
		pst.setInt(1,eventID);//set the eventID
        rs = pst.executeQuery();//execute query and retrieve artistID into rs
        
        //retrieve results and encode into JSON object
        int currArtistID;
        if(rs.next()){
	        currArtistID = rs.getInt(1);
	        json_ret.put("artistID", currArtistID);
        } else {
        	return new JSONObject("{}");
        }
        
        //create, execute query for name of artist
        pst = (PreparedStatement) con.prepareStatement("SELECT name FROM Artist WHERE artistID = ?");
        pst.setInt(1,currArtistID);//set the currentArtistID
        rs = pst.executeQuery();//execute query and retrieve artistID into rs
        
        //retrieve results and encode into JSON Object
        rs.next();
        String currArtist = rs.getString(1);
        json_ret.put("name", currArtist);
        
        //close the connection
        DbManager.closeConnection(con); 
        return json_ret;
		
		
		
	}
	
	public static JSONObject getNextArtist(Map<String,String[]> param) throws SQLException, JSONException
	{
		
		int eventID = Integer.parseInt(param.get("eventID")[0]);
		
		//setup connection and other variables
		Connection con = null;
    	PreparedStatement pst = null;
        ResultSet rs = null;
        JSONObject json_ret = new JSONObject();
		con = DbManager.getConnection(true);
		
		
		//create, execute query for artistID
		pst = (PreparedStatement) con.prepareStatement("SELECT ArtistEvent.order FROM ArtistEvent WHERE eventID = ? AND artistID = (SELECT artistID From CurrentArtist WHERE eventID = ?)");
		pst.setInt(1,eventID);//set the eventID
		pst.setInt(2,eventID);
        rs = pst.executeQuery();//execute query and retrieve artistID into rs
        
        //retrieve results and encode into JSON object
        int order;
        if(rs.next()){
        	order = rs.getInt(1);
        } else {
        	return new JSONObject("{\"haha\":\"testing\"}");
        }
        
        System.out.println(order);
        
        pst = (PreparedStatement) con.prepareStatement("SELECT ArtistEvent.artistID FROM ArtistEvent WHERE eventID = ? and ArtistEvent.order = ?");
        pst.setInt(1,eventID);//set the eventID
		pst.setInt(2,order + 1);
        rs = pst.executeQuery();//execute query and retrieve artistID into rs
        
        int artistID;
        if(rs.next()){
        	artistID = rs.getInt(1);
        } else {
        	return new JSONObject("{\"wtf\":\"don't make sense\"}");
        }
        
        json_ret.put("artistID", artistID);
        return json_ret;
        
        
	}
	
	public static JSONObject getArtistTwitterHandle(Map<String, String[]> input) {
		
		String artistID = input.get("id")[0];
		
		JSONObject twitterInfo = null;
		
		try {
			twitterInfo = readJsonFromUrl("http://developer.echonest.com/api/v4/artist/twitter?api_key=JRWTJDEGEQUEUGKMW&id=" + artistID + "&format=json");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return twitterInfo;
	}
	
	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }


}

