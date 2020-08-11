package recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.MySQLConnection;
import entity.Item;
import external.GitHubClient;

public class Recommendation {
	
	public List<Item> recommendItems(String userId, double lat, double lon) {
		List<Item> recomItems = new ArrayList<>();
		//step1: get all favorited item ids of the users
		MySQLConnection conn = new MySQLConnection();
		Set<String> favItemIds = conn.getFavItemIds(userId);
		// step2: get and count keywords;
		Map<String, Integer> keywordCnt = new HashMap<>();
		for (String itemId : favItemIds) {
			Set<String> keywords = conn.getKeywords(itemId);
			for (String keyword : keywords) {
				keywordCnt.put(keyword, keywordCnt.getOrDefault(keyword, 0) + 1);
			}
		}
		conn.close();
		// step3: sort keywords by count in descending order
		// example output: {"software engineer": 6, "backend": 4, "san francisco": 3, "remote": 1}
		List<Entry<String, Integer>> keywordList = new ArrayList<>(keywordCnt.entrySet());
		Collections.sort(keywordList, (Entry<String, Integer> e1, Entry<String, Integer> e2) -> {
			return Integer.compare(e2.getValue(), e1.getValue());
		});
		// step4: keep top3 keywords
		if (keywordList.size() > 3) {
			keywordList = keywordList.subList(0, 3);
		}
		// step5: search based on keywords and filter out favorited items
		Set<String> visitedItemIds = new HashSet<>();
		GitHubClient client = new GitHubClient();
		// {"software engineer": 6, "backend": 4, "san francisco": 3}
		for (Entry<String, Integer> keyword : keywordList) {
			List<Item> items = client.search(lat, lon, keyword.getKey());
			for (Item item : items) {
				String itemId = item.getItemId();
				if (!favItemIds.contains(itemId) && !visitedItemIds.contains(itemId)) {
					recomItems.add(item);
					visitedItemIds.add(itemId);
				}
			}
		}
		return recomItems;
	}
}
