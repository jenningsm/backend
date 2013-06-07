package functions;

import org.json.JSONException;
import org.json.JSONObject;

public class Authentication {

	//Parameters: JSONObject which contains: "un"--> string, "pw"-->string
	//example call: Authentication.getAuthentication(JSONObject object);
	//THROWS: JSONException
	//Returns JSON: "authentication":"success" if true, else JSON = null
	//example output: {"authentication":"success"}
	public static JSONObject getAuthentication(JSONObject param) throws JSONException{
		
		String un = param.getString("un");
		String pw = param.getString("pw");
		
		String password = "jrf1919";
		String username = "JRF";
		JSONObject auth = new JSONObject();
		if(username.equals(un))
		{
			if(password.equals(pw))
			{
					auth.put("authentication", "success");
					return auth;	
			}
			else
				return null;
		}
		else
			return null;
		
	}
	
	
	
}
