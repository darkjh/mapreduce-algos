/**
 * A-Priori algorithm for frequent itemset
 * 
 * Dataset:
 *   http://snap.stanford.edu/class/cs246-data/browsing.txt
 */

package hw1;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import com.google.common.collect.ImmutableSortedMap;

public class APriori {
	static private String DATA_PATH = "/home/darkjh/course/stanford-CS246-winter-2013/datasets/browsing.txt";
	static private int SUPPORT = 100;
	
	static Scanner readDataFile(String filename) throws IOException {	
		File file = new File(filename);
		return new Scanner(file);
	}
	
	static class ValueComparator<T> implements Comparator<T> {

	    Map<T, Double> base;
	    public ValueComparator(Map<T, Double> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(T a, T b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	
	static public void main(String[] args) {
		String file;
		if (args.length == 0)
			file = DATA_PATH;
		else
			file = args[0];
		
		HashMap<String, Integer> singletons = new HashMap<String, Integer>();
		HashMap<Entry<String, String>, Integer> pairs = new HashMap<Entry<String, String>, Integer>();
		
		try {
			Scanner scanner = readDataFile(file);
			while (scanner.hasNextLine()) {
				// 1st pass
				String items[] = scanner.nextLine().split("\\s"); // only scan lines with more than one item
				for (String item: items) {
					int count = singletons.containsKey(item) ? singletons.get(item) : 0;
					singletons.put(item, count + 1);
				}
			}
		} catch (IOException e) {}
		
		
		try {
			Scanner scanner = readDataFile(file);
			while (scanner.hasNextLine()) {
				// 2nd pass
				String items[] = scanner.nextLine().split("\\s");
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
		
		// confidence
		// TODO separate in function
		Map<Double, Entry<String, String>> confs = new HashMap<Double, Entry<String, String>>();
		
		// Map<Entry<String, String>, Double> confs = new HashMap<Entry<String, String>, Double>();
		for (Entry<String, String> k: freqPairs) {
			String i1 = k.getKey();
			String i2 = k.getValue();
			Double conf1 = (double) pairs.get(k) / singletons.get(i1);
			Double conf2 = (double) pairs.get(k) / singletons.get(i2);
			
			confs.put(conf1, k);
			confs.put(conf2, new SimpleEntry<String, String>(i2, i1));
		}
		
		ImmutableSortedMap<Double, Entry<String, String>> topConfs = ImmutableSortedMap.copyOf(confs);
		assert topConfs.size() == confs.size() * 2;
		
		int count = 1;
		for (Double d: topConfs.descendingKeySet()) {
			Entry<String, String> k = topConfs.get(d);
			System.out.println(k.getKey()+" => "+k.getValue()+" @ "+d.toString());
			if (++count > 10) {
				break;
			}
		}
		System.out.println("finished");
		
	}
}
