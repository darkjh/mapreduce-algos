package projection;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class Projection {
	
	public static void project(String filepath, int minSupport) throws Exception {
		FPTree fpt = new FPTree(filepath, minSupport);
		Multimap<Long, FPTreeNode> headerTable = fpt.getHeaderTable();
		
		for (Long item : Lists.reverse(fpt.getL())) {
			HashMap<Long, Integer> counter = new HashMap<Long, Integer>();
			List<FPTreeNode> list = (List<FPTreeNode>) headerTable.get(item);
			
			// visit all conditional path of the current item
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
					// TODO formal output layer
					String out = item < i ? 
							item.toString() + "," + i.toString()
							: i.toString() + "," + item.toString();
					
					System.out.println(out + " : " + Integer.toString(pairSupport));
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		// String file = "./resources/tinyRecomm";
		String file = "/home/port/datasets/KAR/mahout/transaction";
		Projection.project(file, 10);
	}
}
