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
import java.util.Scanner;

public class APriori {
	static private String DATA_PATH = 
			"/home/darkjh/course/stanford-CS246-winter-2013/datasets/browsing.txt";
	
	static public void main(String[] args) {
		String file;
		if (args.length == 0)
			file = DATA_PATH;
		else
			file = args[0];
		
		Path path = Paths.get(file);
		try (Scanner scanner =  new Scanner(path, StandardCharsets.UTF_8.name())) {
			while (scanner.hasNextLine()){
				
			}      
		} catch (IOException e) {}
	}
}
