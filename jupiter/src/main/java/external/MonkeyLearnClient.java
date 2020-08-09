package external;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.monkeylearn.ExtraParam;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

public class MonkeyLearnClient {
	
	private static final String API_KEY = "1d39df726e01b19b0584463fd2316a7626c0f0c6";
	private static final String MODEL = "ex_YCya9nrn";
	
	// test
	public static void main(String[] args) {
		String[] textList = {
				"Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.", 
				"Former Auburn University football coach Tommy Tuberville defeated ex-US Attorney General Jeff Sessions in Tuesday nights runoff for the Republican nomination for the U.S. Senate. ",
				"The NEOWISE comet has been delighting skygazers around the world this month – with photographers turning their lenses upward and capturing it above landmarks across the Northern Hemisphere."		
		};
		List<List<String>> ans = extractKeywords(textList);
		for (List<String> words : ans) {
			for (String word : words) {
				System.out.println(word);
			}
			System.out.println();
		}
	}
	
	public static List<List<String>> extractKeywords(String[] text) {
		if (text == null || text.length == 0) {
			return new ArrayList<>();
		}
		MonkeyLearn ml = new MonkeyLearn(API_KEY);
		ExtraParam[] extraParams = {new ExtraParam("max_keywords", "3")};
		MonkeyLearnResponse response;
		try {
			response = ml.extractors.extract(MODEL, text, extraParams);
			JSONArray resultArray = response.arrayResult;
			return formatKeywords(resultArray);
		}
		catch (MonkeyLearnException e) {
			e.printStackTrace();	
		}
		return new ArrayList<>();
	}

	/**
	 * Convert JSONArray to List<List<String>>
	 * 
	 * MonkeyLearn output: 
	 * [ 
	 *  [{"keyword": "elon musk"}, {"keyword": "tesla"}, {"keyword": "rocket"}], 
	 *  [{"keyword": "facebook"}, {"keyword": "mark"}, {"keyword": "social network"}], 
	 *  [{"keyword": "google"}, {"keyword": "brain"}, {"keyword": "machine learning"}],
	 * ]
	 * 
	 * List<List<String>>
	 * [
	 *  ["elon musk", "tesla", "rocket"],
	 *  ["facebook", "mark", "social network"],
	 *  ["google", "brain", "machine learning"]
	 * ]
	 */
	private static List<List<String>> formatKeywords(JSONArray mlResultArray) {
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
