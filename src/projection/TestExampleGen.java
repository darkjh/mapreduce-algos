package projection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * Generate test examples
 * 
 * @author Han JU
 *
 */
public class TestExampleGen {
	
	private static String alphabet = "_abcdefghijklmnopqrstuvwxyz";
	
	public static void generateFromTransacFile(File file) throws Exception {
		Scanner scanner = new Scanner(file);
		FileWriter fstream = new FileWriter("./resources/TestExampleAutoGen");
		BufferedWriter out = new BufferedWriter(fstream);
		while (scanner.hasNextLine()) {
			String[] line = scanner.nextLine().split("\t");
			String user = line[0];
			String[] items = line[1].split(",");
			
			for (String i : items) {
				out.write(user + "\t" + alphabet.indexOf(i) + "\t1\n");
			}
		}
		out.close();
	}
	
	public static void main(String[] args) throws Exception {
		generateFromTransacFile(new File("./resources/tinyPaperTransac"));
	}
}