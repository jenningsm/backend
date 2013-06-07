package Exception;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class MyException extends Exception {

	String error_message;
	int error_code;
	
	public MyException(String message, int code){
		error_message = message;
		error_code = code;
	}
	
	public String getErrorMessage(){
		return error_message;
	}
	
	public int getErrorCode(){
		return error_code;
	}
	
	public void respond(HttpServletResponse response){
		response.setStatus(error_code);
		
		JSONObject ret = new JSONObject();
		ret.put("error", error_message);
		
		try {
			response.getWriter().println(ret.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
