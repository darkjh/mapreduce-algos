package projection;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * FP-Tree node implementation
 * 
 * Based on article: www.cs.uiuc.edu/~hanj/pdf/sigmod00.pdf
 * 
 * @author Han JU
 *
 */
public class FPTreeNode implements Comparable<FPTreeNode> {
	private final Long item;
	private int count;
	private boolean isRoot;

	private FPTreeNode parent;
	// TODO replace this with memory efficient structure
	private Map<Long, FPTreeNode> children;

	/** ctor for normal node */
	public FPTreeNode(long i, int c, FPTreeNode p) {
		isRoot = false;
		item = i;
		count = c;
		parent = p;
	}

	/** ctor for a root node */
	public FPTreeNode() {
		item = -1L;
		count = -1;
		isRoot = true;
	}
	
	/** add a node as a child, also add to header table */
	public FPTreeNode addChild(Long childItem, List<FPTreeNode> headerList) { 
		if (children == null) {
			children = Maps.newHashMap();
		}
		
		FPTreeNode child;
		if (children.containsKey(childItem)) {
			child = children.get(childItem);
			child.incrementCount();
		} else {
			child = new FPTreeNode(childItem, 1, this);
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
