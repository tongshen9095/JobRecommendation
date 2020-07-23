package rpc;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;


// RpcHelper contains helper functions which generalize writing JSON response
public class RpcHelper {

	// write a JSONArray to http response
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException {
		response.setContentType("application/json");
		response.getWriter().print(array);
	}

	// write a JSONObject to http response
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setContentType("application/json");
		response.getWriter().print(obj);
	}
}
