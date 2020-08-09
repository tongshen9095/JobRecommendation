package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import db.MySQLConnection;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// check whether session exists for the request
		HttpSession session = request.getSession(false);
		JSONObject obj = new JSONObject();
		if (session != null) {
			MySQLConnection conn = new MySQLConnection();
			String userId = session.getAttribute("user_id").toString();
			obj.put("status", "ok")
			.put("user_id", userId)
			.put("name", conn.getFullname(userId));	
			conn.close();
		}
		else {
			obj.put("status", "Invalid session.");
			response.setStatus(403);
		}
		RpcHelper.writeJsonObject(response, obj);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
		String userId = input.getString("uer_id");
		String password = input.getString("password");
		MySQLConnection conn = new MySQLConnection();
		JSONObject obj = new JSONObject();
		if (conn.verifyLogin(userId, password)) {
			// create a new session
			HttpSession session = request.getSession();
			// store use_id into the session
			session.setAttribute("user_id", userId);
			obj.put("status", "ok")
			.put("user_id", userId)
			.put("name", conn.getFullname(userId));	
		}
		else {
			obj.put("status", "Login failed, user id and password does not match.");
			response.setStatus(401);
		}
		conn.close();
		RpcHelper.writeJsonObject(response, obj);
	}

}
