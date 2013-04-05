package projection;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

/**
 * A FP-Tree implementation
 * 
 * Based on article: www.cs.uiuc.edu/~hanj/pdf/sigmod00.pdf
 * 
 * @author Han JU
 *
 */
public class FPTree {
	
	private DataModel dataModel;
	private int supportThreshold;
	
	private FPTreeNode root;
	Multimap<Long, FPTreeNode> headerTable;
	private List<Long> L;
	private Map<Long, Integer> freq;
	
	private Ordering<Long> byFrequencyOrdering = new Ordering<Long>() {
		// reversed order! 
		// Array.sort() returns descending ordering
		@Override
		public int compare(Long left, Long right) {
			int freqComp = freq.get(right) - freq.get(left);
			// fixed order !!!
			return freqComp != 0 ? freqComp : (int) (right - left);		}
	};
	
	public FPTree(String filepath, int threshold) throws Exception {
		this(new File(filepath), threshold);
	}
	
	public FPTree(File file, int threshold) throws Exception {
		this(new FileDataModel(file), threshold);
	}
	
	public FPTree(DataModel model, int threshold) throws Exception {
		dataModel = model;
		supportThreshold = threshold;
		
		root = new FPTreeNode();
		headerTable = LinkedListMultimap.create();
		
		firstScan();
		constructTree();
		clean();
	}
	
	public FPTreeNode getTree() {
		return root;
	}
	
	public Multimap<Long, FPTreeNode> getHeaderTable() {
		return headerTable;
	}
	
	public List<Long> getL() {
		return L;
	}
	
	/** 
	 * First pass of data, construct L, a list of frequent item
	 * in descending order of their frequency
	 * TODO consider a priority queue
	 */
	public void firstScan() throws Exception {
		L = Lists.newArrayList();
		freq = Maps.newHashMap();
		
		LongPrimitiveIterator itemIter = dataModel.getItemIDs();
		while(itemIter.hasNext()) {
			long itemID = itemIter.next();
			int count = dataModel.getNumUsersWithPreferenceFor(itemID);
			
			if (count >= supportThreshold) {
				L.add(itemID);
				freq.put(itemID, count);
			}
		}
		
		// sort in descending order of frequency
		Collections.sort(L, byFrequencyOrdering);
	}
	
	private void constructTree() throws Exception {
		LongPrimitiveIterator transIter = dataModel.getUserIDs();
		
		// for every transaction
		while (transIter.hasNext()) {
			Long transID = transIter.next();
			List<Long> sortedItems = Lists.newArrayList();
			// filtered out infrequent item
			for (Long item : dataModel.getItemIDsFromUser(transID)) {
				if (freq.containsKey(item)) {
					sortedItems.add(item);
				}
			}
			// sort its items in descending frequency order
			Collections.sort(sortedItems, byFrequencyOrdering);
			
			insertTree(sortedItems, root);
		}
	}
	
	private void insertTree(List<Long> sorted, FPTreeNode curr) {
		if (!sorted.isEmpty()) {
			Long item = sorted.get(0);
			List<FPTreeNode> headerList = (List<FPTreeNode>) headerTable.get(item);
			FPTreeNode next = curr.addChild(item, headerList);
			insertTree(sorted.subList(1, sorted.size()), next);
		}
	}
	
	/** help GC, these are big */
	private void clean() {
		freq = null;
		dataModel = null;
	}
	
	public static void main(String[] args) throws Exception {
		// String file = "./resources/tinyRecomm";
		// String file = "/home/port/datasets/msd-small/test_triples";
		String file = "./resources/TestExampleAutoGen";
		Runtime rt = Runtime.getRuntime();
		
		FPTree fpt = new FPTree(file, 3);
		
		long m1 = rt.freeMemory();

		rt.gc();

		long m2 = rt.freeMemory();
		
		System.out.println(m2 - m1);
	}
}
