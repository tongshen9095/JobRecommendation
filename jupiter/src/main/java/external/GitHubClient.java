package external;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
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
	public List<Item> search(double lat, double lon, String keyword) {
		// corner case: user does not provide keyword, then use the default
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			// encode the keyword
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// step1: : Create a HttpClient object
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// step2: Create a HTTP request: specify method and URL
		String url = String.format(URL_TEMPLATE, keyword, lat, lon);
		HttpGet httpget = new HttpGet(url);

		/*
		 * Create a custom response handler return JSONArray
		 */
		ResponseHandler<List<Item>> responseHandler = new ResponseHandler<List<Item>>() {
			@Override
			public List<Item> handleResponse(final HttpResponse response) throws IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status != 200) {
					return new ArrayList<>();
				}
				HttpEntity entity = response.getEntity();
				if (entity == null) {
					return new ArrayList<>();
				}
				String responseBody = EntityUtils.toString(entity);
				JSONArray array = new JSONArray(responseBody);
				return getItemList(array);
			}
		};

		try {
			// step3: get response body directly via a custom response handler
			List<Item> ans = httpclient.execute(httpget, responseHandler);
			return ans;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

	// helper function to filter the data
	private static List<Item> getItemList(JSONArray array) {
		List<Item> itemList = new ArrayList<>();
		List<List<String>> keywords = extractKeywords(array);
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			Item item = Item.builder().itemId(getStringFieldOrEmpty(object, "id"))
					.name(getStringFieldOrEmpty(object, "title"))
					.address(getStringFieldOrEmpty(object, "location"))
					.url(getStringFieldOrEmpty(object, "url"))
					.imageUrl(getStringFieldOrEmpty(object, "company_logo"))
					.keywords(new HashSet<String>(keywords.get(i)))
					.build();
			itemList.add(item);
		}
		return itemList;
	}
	
	private static List<List<String>> extractKeywords(JSONArray array) {
		List<String> descriptionList = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			String description = getStringFieldOrEmpty(array.getJSONObject(i), "description");
			if (description.equals("") || description.equals("\n")) {
				String title =  getStringFieldOrEmpty(array.getJSONObject(i), "title");
				descriptionList.add(title);
			}
			else {
				descriptionList.add(description);
			}
		}
		String[] text = descriptionList.toArray(new String[descriptionList.size()]);
		List<List<String>> keywords = MonkeyLearnClient.extractKeywords(text);
		return keywords;
	}

	private static String getStringFieldOrEmpty(JSONObject obj, String field) {
		// field does not exist or field is null return null
		return !obj.has(field) || obj.isNull(field) ? "" : obj.getString(field);
	}
}
