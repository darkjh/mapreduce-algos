package projection;

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
	private static final String SEP = "\t";
	
	public static void project(String filepath, OutputLayer ol, 
			int minSupport) throws Exception {
		FPTree fpt = new FPTree(filepath, minSupport);
		Multimap<Long, FPTreeNode> headerTable = fpt.getHeaderTable();
		
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
					int count = counter.containsKey(currItem) ? counter.get(currItem) : 0;
					counter.put(currItem, count + condSupport);
					curr = curr.getParent();
				}
			}
			
			// generate pairs
			for (Long i : counter.keySet()) {
				int pairSupport = counter.get(i);
				if (pairSupport >= minSupport) {
					String out = item < i ? 
						item.toString() + SEP + i.toString()
							: i.toString() + SEP + item.toString();
					out = out + SEP + Integer.toString(pairSupport);
					ol.writeLine(out);
				}
			}
		}
		ol.close();
	}
	
	public static void main(String[] args) throws Exception {
		// String file = "./resources/tinyRecomm";
		// String file = "/home/port/datasets/msd-small/test_triples";
		// String file = "/home/port/datasets/ml-10M/triples";
		// String file = "./resources/TestExampleAutoGen";

		Stopwatch sw = new Stopwatch();	
		sw.start();
		Projection.project(args[0], new OutputLayer(args[1]), 
				Integer.parseInt(args[2]));
		sw.stop();
		
		System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));
	}
}
