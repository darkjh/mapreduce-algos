package projection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class OutputLayer {
	private PrintWriter pw;
	
	/** ctor for console output */
	public OutputLayer() {
		pw = new PrintWriter(System.out);
	}
	
	/** ctor for file output */
	public OutputLayer(String filepath) throws Exception {
		this(new File(filepath));
	}
	
	/** ctor for file output */
	public OutputLayer(File file) throws Exception {
		pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
	}
	
	public void writeLine(String s) {
		pw.println(s);
	}
	
	public void close() {
		pw.close();
	}
}
