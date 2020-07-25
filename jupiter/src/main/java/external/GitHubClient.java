package external;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;

public class GitHubClient {
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";
	
	// use GitHub client to request job info
	public JSONArray search(double lat, double lon, String keyword) {
		// corner case: user does not provide keyword, then use the default
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			// encode the keyword
			keyword = URLEncoder.encode(keyword, "UTF-8");
		}
	    catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    }
		// step1: : Create a HttpClient object
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// step2: Create a HTTP request: specify method and URL
		String url = String.format(URL_TEMPLATE, keyword, lat, lon);
		HttpGet httpget = new HttpGet(url);
	    /*
	     * Create a custom response handler
	     * return JSONArray
	    */
		ResponseHandler<JSONArray> responseHandler = new ResponseHandler<JSONArray>() {
			@Override
			public JSONArray handleResponse(final HttpResponse response) throws IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status != 200) {
					return new JSONArray();
				}
				HttpEntity entity = response.getEntity();
				if (entity == null) {
					return new JSONArray();
				}
				String responseBody = EntityUtils.toString(entity);
				return new JSONArray(responseBody);
			}
		};
	    try {
	        // step3: get response body directly via a custom response handler
	        JSONArray array = httpclient.execute(httpget, responseHandler);
	        return array;
	    } 
	    catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	    try {
	        // step3: get response body directly via a custom response handler
	        JSONArray array = httpclient.execute(new HttpGet(url), responseHandler);
	        return array;
	    } 
	    catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	    return new JSONArray();
	}
	
	// filter the data
	private List<Item> getItemList(JSONArray array) {
		List<Item> itemList = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			Item item = Item.builder()
					        .itemId(getStringFieldOrEmpty(object, "id"))
					        .name(getStringFieldOrEmpty(object, "title"))
					        .address(getStringFieldOrEmpty(object, "location"))
					        .url(getStringFieldOrEmpty(object, "url"))
					        .imageUrl(getStringFieldOrEmpty(object, "company_logo"))
					        .build();
			itemList.add(item);
		}
		return itemList;	
	}
	
	private String getStringFieldOrEmpty(JSONObject obj, String field) {
		return obj.isNull(field) ? "" : obj.getString(field);
	}
}
