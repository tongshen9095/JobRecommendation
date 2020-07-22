package rpc;

import java.io.IOException;
// import PrintWriter
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// import JSONArray
import org.json.JSONArray;
// import JSONObject
import org.json.JSONObject;

/**
 * Servlet implementation class SearchItem
 */
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// return JSON format data as response
		// tell the front-end the content type
		response.setContentType("application/json");
		// use JSONArray to return multiple users
		JSONArray array = new JSONArray();
		array.put(new JSONObject().put("username", "abcd"));
		array.put(new JSONObject().put("username", "1234"));
		// return the JSONObject as the result to the front-end
		PrintWriter writer = response.getWriter();
		writer.print(array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
