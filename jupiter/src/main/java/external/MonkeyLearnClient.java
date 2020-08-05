package external;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class MonkeyLearnClient {
	
	/**
	 * MonkeyLearn output:
	 * [
	 *  [{"keyword": "elon musk"}, {"keyword": "tesla"}, {"keyword": "rocket"}],
	 *  [{"keyword": "facebook"}, {"keyword": "mark"}, {"keyword": "social network"}],
	 *  [{"keyword": "google"}, {"keyword": "brain"}, {"keyword": "machine learning"}]
	 */
	
	private static List<List<String>> getKeywords(JSONArray mlResultArray) {
		List<List<String>> topKeywords = new ArrayList<>();
		for (int i = 0; i < mlResultArray.size(); i++) {
			List<String> keywordsOut = new ArrayList<>();
			JSONArray keywordsIn = (JSONArray) mlResultArray.get(i);
			for (int j = 0; j < keywordsIn.size(); j++) {
				JSONObject keywordObject = (JSONObject) keywordsIn.get(j);
				String keyword = (String) keywordObject.get("keyword");
				keywordsOut.add(keyword);
			}
			topKeywords.add(keywordsOut);
		}
		return topKeywords;
	}

}
