package projection;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * Bipartite graph projection solution using a FP-Tree approach
 * 
 * Construct firstly a FP-Tree, then iterate all frequent items (frequent means its 
 * support >= threshold) by ascending frequency order, find and merge its conditional 
 * FP-Tree into a single path, then generate all possible frequent pairs
 * 
 * @author Han JU
 *
 */
public class Projection {
	
	public static void project(String filepath, int minSupport) throws Exception {
		FPTree fpt = new FPTree(filepath, minSupport);
		Multimap<Long, FPTreeNode> headerTable = fpt.getHeaderTable();
		
		// TODO formal output layer
		FileWriter fstream = new FileWriter("/home/port/outputs/project_test");
		BufferedWriter fout = new BufferedWriter(fstream);
		
		// for (Long item : fpt.getL()) {
		for (Long item : headerTable.keySet()) {
			HashMap<Long, Integer> counter = Maps.newHashMap();
			List<FPTreeNode> list = (List<FPTreeNode>) headerTable.get(item);
			
			// visit all conditional path of the current item
			// merge them by counting
			for (FPTreeNode node : list) {
				int condSupport = node.getCount();
				FPTreeNode curr = node.getParent();
				while (!curr.isRoot()) {
					Long currItem = curr.getItem();
					assert currItem != item;
					int count = counter.containsKey(currItem) ? counter.get(currItem) : 0;
					counter.put(currItem, count + condSupport);
					curr = curr.getParent();
				}
			}
			
			// generate pairs
			for (Long i : counter.keySet()) {
				int pairSupport = counter.get(i);
				if (pairSupport >= minSupport) {
					// TODO formal output layer
					String out = item < i ? 
							item.toString() + "\t" + i.toString()
							: i.toString() + "\t" + item.toString();
					out = out + "\t" + Integer.toString(pairSupport);
					
					// System.out.println(out);
					fout.write(out + "\n");
				}
			}
		}
		fout.close();
	}
	
	public static void main(String[] args) throws Exception {
		Stopwatch sw = new Stopwatch();
		// String file = "./resources/tinyRecomm";
		// String file = "/home/port/datasets/msd-small/test_triples";
		String file = "/home/port/datasets/ml-10M/triples";
		// String file = "./resources/TestExampleAutoGen";
		
		sw.start();
		Projection.project(file, 2);
		sw.stop();
		
		System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));
	}
}
