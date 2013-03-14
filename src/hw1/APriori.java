/**
 * A-Priori algorithm for frequent itemset
 * 
 * Dataset:
 *   http://snap.stanford.edu/class/cs246-data/browsing.txt
 */

package hw1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class APriori {
	static private String DATA_PATH = "/home/darkjh/course/stanford-CS246-winter-2013/datasets/browsing.txt";
	static private int SUPPORT = 100;

	static public void main(String[] args) {
		String file;
		if (args.length == 0)
			file = DATA_PATH;
		else
			file = args[0];
		
		Path path = Paths.get(file);
		HashMap<String, Integer> singletons = new HashMap<String, Integer>();
		HashMap<Entry<String, String>, Integer> pairs = new HashMap<Entry<String, String>, Integer>();
		try (Scanner scanner =  new Scanner(path, StandardCharsets.UTF_8.name())) {
			scanner.skip("^[^\\s]+$");
			while (scanner.hasNextLine()){
				// 1st pass
				String items[] = scanner.next().split("\\s"); // only scan lines with more than one item
				for (String item: items) {
					int count = singletons.containsKey(item) ? singletons.get(item) : 0;
					singletons.put(item, count + 1);
				}
			}
		} catch (IOException e) {}
		
		try (Scanner scanner =  new Scanner(path, StandardCharsets.UTF_8.name())) {
			while (scanner.hasNextLine()){
				// 2nd pass
				String items[] = scanner.next().split("\\s");
				for (String item1: items) {
					for (String item2: items) {
						if (singletons.get(item1) >= SUPPORT && singletons.get(item2) >= SUPPORT 
								&& item1.compareTo(item2) > 0) {
							Entry<String, String> pair = new SimpleEntry<String, String>(item1, item2);
							int count = pairs.containsKey(pair) ? pairs.get(pair) : 0;
							pairs.put(pair, count + 1);
						}
					}
				}
			}      
		} catch (IOException e) {}
		// frequent pairs
		List<Entry<String, String>> freqPairs = new ArrayList<Entry<String, String>>();
		for (Entry<String, String> k: pairs.keySet()) {
			if (pairs.get(k)>= SUPPORT) {
				freqPairs.add(k);
			}
		}
		
		System.out.println(freqPairs.size());
	}
}
