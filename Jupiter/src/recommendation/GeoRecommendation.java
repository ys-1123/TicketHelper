package recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation {
	
	public List<Item> recommendItems(String userId, double lat, double lon) {
		List<Item> recommendedItems = new ArrayList<>();
		// step 1. retrive favroite item id from db
		DBConnection connection = DBConnectionFactory.getConnection();
		Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
		
		// get all categories, sort by count
		Map<String, Integer> allCategories = new HashMap<>();
		for(String itemId : favoritedItemIds) {
			Set<String> categories = connection.getCategories(itemId);
			for (String category : categories) {
				allCategories.put(category,  allCategories.getOrDefault(categories, 0) + 1);
			}
		}
//		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
//		Collections.checkedSortedMap(categoryList, (Entrty<String, Integer> e1), Entry<String, Integer>
//		e2) -> {
//			return Integer.compare(e2.getValue(),  e1.getValue());
//		});
		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
		Collections.sort(categoryList, (Entry<String, Integer> e1, Entry<String, Integer> e2) -> {
			return Integer.compare(e2.getValue(), e1.getValue());
		});
		// Step 3 search based on category, filter out favorite items
		// some item have multiple category, will cause same item return for different category
		// use hashset to deduplicate
		Set<String> visitedItemIds = new HashSet<>();
		for (Entry<String, Integer> categoryEntry : categoryList) {
			List<Item> items = connection.searchItems(lat,  lon,  categoryEntry.getKey());
			
			for (Item item : items) {
				// avoid recommend items the user already liked && avoid items with multiple category
				if (!favoritedItemIds.contains(item.getitemId()) && !visitedItemIds.contains(item.getitemId())) {
					recommendedItems.add(item);
					visitedItemIds.add(item.getitemId());
				}
			}
		}
		connection.close();
		return recommendedItems;
	}
}
