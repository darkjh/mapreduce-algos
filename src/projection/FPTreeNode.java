package projection;

import java.util.List;

public class FPTreeNode {
	private final long item;
	private long count;
	private boolean isRoot;

	private FPTreeNode parent;
	private List<FPTreeNode> children;

	private FPTreeNode next;

	/** ctor for normal node */
	public FPTreeNode(long i, long c, FPTreeNode n, FPTreeNode p) {
		isRoot = false;
		item = i;
		count = c;
		next = n;
	}

	/** ctor for a root node */
	public FPTreeNode() {
		item = -1;
		count = -1;
		isRoot = true;
	}

}
