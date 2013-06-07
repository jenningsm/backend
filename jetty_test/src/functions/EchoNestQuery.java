package functions;

import java.util.List;
import java.util.Map;
import com.echonest.api.v4.Artist;
import com.echonest.api.v4.ArtistParams;
import com.echonest.api.v4.Biography;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Image;
import com.echonest.api.v4.News;
import com.echonest.api.v4.Params;
import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

import com.echonest.api.v4.EchoNestAPI;

public class EchoNestQuery {
	
	public EchoNestQuery(){}
	
	private static String API_KEY = "JRWTJDEGEQUEUGKMW";
	
	static EchoNestAPI getAPI(){
		 EchoNestAPI echoNest = new EchoNestAPI(API_KEY);
		 return echoNest;
	}
	
	public static JSONObject getArtistNames(Map<String, String[]> input) throws EchoNestException, JSONException{

		System.out.println("before api");
		EchoNestAPI en = getAPI();
		String artistName = input.get("searchName")[0];
		
		System.out.println(input.get("searchName")[0]);
		
		
		System.out.println("got here");
		
		if(artistName == null) return null; //invalid artistName
		if(artistName.isEmpty()) return null; //empty query
		
		int results = 0;
        JSONArray jArray = new JSONArray();
        List<Artist> artists = en.searchArtists(artistName);
        
        if(artists.isEmpty()) return null; //query returns nothing
        
        for (Artist artist : artists) {
        	if(results > 9) break;//we have 10 results(which is our max)
            System.out.println();
            String name_json = artist.getName();
            String id_json = artist.getID(); 
            JSONObject jobj = new JSONObject();
            jobj.put("name", name_json);
            jobj.put("id", id_json);
            jArray.put(jobj); 
            results++;
        }//end for loop
        
        JSONObject jObjArtists= new JSONObject();
        jObjArtists.put("artists", jArray);
        return jObjArtists;
	}

	
	public static JSONObject getArtistImages(Map<String, String[]> input) throws EchoNestException, JSONException{
		EchoNestAPI en = getAPI();
		String artistID = input.get("id")[0];
		String artistName = input.get("name")[0];
		
		//if(artistID.isEmpty()) return null; //empty query
		
		int results = 0;
        JSONArray jArray = new JSONArray();

        List<Artist> artists = en.searchArtists(artistName);
       // System.out.println(artists);
        Artist ourArtist= null;
        for (Artist artist : artists) {
        	if(artist.getID().equals(artistID)){
        		//we found our guy!
        		ourArtist = artist;
        		//System.out.println(ourArtist);
        		break;//get out of the for loop
        	}
        }
        //now, we have this artists pictures too
        if(ourArtist!=null){
	        List<Image> images = ourArtist.getImages();
	        results = 0;
	        for(Image image : images){
	        	if(results > 14) break;
	        	
	        	JSONObject jobj = new JSONObject();
	            jobj.put("url", image.getURL());
	            jArray.put(jobj); 
	            results++;
	        }
        
	        JSONObject jObjArtists= new JSONObject();
	        jObjArtists.put("images", jArray);
	        return jObjArtists;
        }
        else
        {
        	return null;//if we did not find the Artist we wanted
        }        		
	}//end function
	

public static JSONObject getArtistBiographies(Map<String, String[]> input) throws EchoNestException, JSONException{
	EchoNestAPI en = getAPI();
	String artistID = input.get("id")[0];
	String artistName = input.get("name")[0];
	int results = 0;
    JSONArray jArray = new JSONArray();

    List<Artist> artists = en.searchArtists(artistName);
   // System.out.println(artists);
    Artist ourArtist= null;
    for (Artist artist : artists) {
    	if(artist.getID().equals(artistID)){
    		//we found our guy!
    		ourArtist = artist;
    		//System.out.println(ourArtist);
    		break;//get out of the for loop
    	}
    }
    //now, we have this artists pictures too
    
    
    if(ourArtist!=null){
    	List<Biography> bios = ourArtist.getBiographies();
    	results = 0;
        for(Biography bio : bios){
        	if(results > 4) break;
        	
        	JSONObject jobj = new JSONObject();
            jobj.put("site", bio.getSite());
            jobj.put("text", bio.getText());
            jArray.put(jobj); 
            results++;
        }
    
        JSONObject jObjArtists= new JSONObject();
        jObjArtists.put("biographies", jArray);
        return jObjArtists;
    }
    else
    	return null;//if we did not find the Artist we wanted
	
}//end function
	
	



public static JSONObject getArtistNews(Map<String, String[]> input) throws EchoNestException, JSONException{
	EchoNestAPI en = getAPI();
	String artistID = input.get("id")[0];
	String artistName = input.get("name")[0];
	int results = 0;
    JSONArray jArray = new JSONArray();

    List<Artist> artists = en.searchArtists(artistName);
   // System.out.println(artists);
    Artist ourArtist= null;
    for (Artist artist : artists) {
    	if(artist.getID().equals(artistID)){
    		//we found our guy!
    		ourArtist = artist;
    		//System.out.println(ourArtist);
    		break;//get out of the for loop
    	}
    }
    //now, we have this artists pictures too
	
    

    if(ourArtist!=null){
    	List<News> news = ourArtist.getNews();
    	results = 0;
        for(News item : news){
        	if(results > 9) break;
        	
        	JSONObject jobj = new JSONObject();
            jobj.put("title", item.getName());
            jobj.put("url", item.getURL());
            jArray.put(jobj); 
            results++;
        }
    
        JSONObject jObjArtists= new JSONObject();
        jObjArtists.put("news", jArray);
        return jObjArtists;
    }
    else
    	return null;//if we did not find the Artist we wanted
    
	
	
	
}



	
}//end class

