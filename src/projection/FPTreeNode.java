package projection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FPTreeNode implements Comparable<FPTreeNode> {
	private final Long item;
	private int count;
	private boolean isRoot;

	private FPTreeNode parent;
	private Map<Long, FPTreeNode> children;
	
	/** pointer to the next node of the same item in the tree */
	private FPTreeNode next;

	/** ctor for normal node */
	public FPTreeNode(long i, int c, FPTreeNode n, FPTreeNode p) {
		isRoot = false;
		item = i;
		count = c;
		next = n;
		parent = p;
	}
	
	/** ctor for a node in header table */
	public FPTreeNode(long i, int c) {
		isRoot = false;
		item = i;
		count = c;
	}

	/** ctor for a root node */
	public FPTreeNode() {
		item = -1L;
		count = -1;
		isRoot = true;
	}
	
	public FPTreeNode addChild(Long childItem, List<FPTreeNode> headerList) { 
		if (children == null) {
			children = new HashMap<Long, FPTreeNode>();
		}
		
		FPTreeNode child;
		if (children.containsKey(childItem)) {
			child = children.get(childItem);
			child.incrementCount();
		} else {
			child = new FPTreeNode(childItem, 1, null, this);
			children.put(childItem, child);
			headerList.add(child);
		}
		return child;
	}
	
	public boolean isRoot() {
		return isRoot;
	}
	
	public FPTreeNode getParent() {
		return parent;
	}

	public void setParent(FPTreeNode parent) {
		this.parent = parent;
	}

	public FPTreeNode getNext() {
		return next;
	}

	public void setNext(FPTreeNode next) {
		this.next = next;
	}

	public Long getItem() {
		return item;
	}

	public int getCount() {
		return count;
	}
	
	public int incrementCount() {
		return ++count;
	}

	@Override
	public int compareTo(FPTreeNode that) {
		// reversed order! 
		// Array.sort() returns descending ordering
		return that.getCount() - this.getCount();
	}
	
	@Override
	public String toString() {
		return Long.toString(item)+":"+Integer.toString(count);
	}
}
