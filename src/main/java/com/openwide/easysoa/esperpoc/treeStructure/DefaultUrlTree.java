package com.openwide.easysoa.esperpoc.treeStructure;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

public class DefaultUrlTree implements Serializable, UrlTreeModel {
	
	/** Root of the tree. */
	protected TreeNode root;

	/**
	 * Determines how the <code>isLeaf</code> method figures out if a node is a
	 * leaf node. If true, a node is a leaf node if it does not allow children.
	 * (If it allows children, it is not a leaf node, even if no children are
	 * present.) That lets you distinguish between <i>folder</i> nodes and
	 * <i>file</i> nodes in a file system, for example.
	 * <p>
	 * If this value is false, then any node which has no children is a leaf
	 * node, and any node may acquire children.
	 * 
	 * @see TreeNode#getAllowsChildren
	 * @see UrlTreeModel#isLeaf
	 * @see #setAsksAllowsChildren
	 */
	protected boolean asksAllowsChildren;

	/**
	 * Creates a tree in which any node can have children.
	 * 
	 * @param root
	 *            a TreeNode object that is the root of the tree
	 * @see #DefaultTreeModel(TreeNode, boolean)
	 */
	public DefaultUrlTree(TreeNode root) {
		this(root, false);
	}

	/**
	 * Creates a tree specifying whether any node can have children, or whether
	 * only certain nodes can have children.
	 * 
	 * @param root
	 *            a TreeNode object that is the root of the tree
	 * @param asksAllowsChildren
	 *            a boolean, false if any node can have children, true if each
	 *            node is asked to see if it can have children
	 * @see #asksAllowsChildren
	 */
	public DefaultUrlTree(TreeNode root, boolean asksAllowsChildren) {
		super();
		this.root = root;
		this.asksAllowsChildren = asksAllowsChildren;
	}

	/**
	 * Sets whether or not to test leafness by asking getAllowsChildren() or
	 * isLeaf() to the TreeNodes. If newvalue is true, getAllowsChildren() is
	 * messaged, otherwise isLeaf() is messaged.
	 */
	public void setAsksAllowsChildren(boolean newValue) {
		asksAllowsChildren = newValue;
	}

	/**
	 * Tells how leaf nodes are determined.
	 * 
	 * @return true if only nodes which do not allow children are leaf nodes,
	 *         false if nodes which have no children (even if allowed) are leaf
	 *         nodes
	 * @see #asksAllowsChildren
	 */
	public boolean asksAllowsChildren() {
		return asksAllowsChildren;
	}

	/**
	 * Sets the root to <code>root</code>. A null <code>root</code> implies the
	 * tree is to display nothing, and is legal.
	 */
	public void setRoot(TreeNode root) {
		this.root = root;
	}

	/**
	 * Returns the root of the tree. Returns null only if the tree has no nodes.
	 * 
	 * @return the root of the tree
	 */
	public Object getRoot() {
		return root;
	}

	/**
	 * Returns the index of child in parent. If either the parent or child is
	 * <code>null</code>, returns -1.
	 * 
	 * @param parent
	 *            a note in the tree, obtained from this data source
	 * @param child
	 *            the node we are interested in
	 * @return the index of the child in the parent, or -1 if either the parent
	 *         or the child is <code>null</code>
	 */
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == null || child == null)
			return -1;
		return ((TreeNode) parent).getIndex((TreeNode) child);
	}

	/**
	 * Returns the child of <I>parent</I> at index <I>index</I> in the parent's
	 * child array. <I>parent</I> must be a node previously obtained from this
	 * data source. This should not return null if <i>index</i> is a valid index
	 * for <i>parent</i> (that is <i>index</i> >= 0 && <i>index</i> <
	 * getChildCount(<i>parent</i>)).
	 * 
	 * @param parent
	 *            a node in the tree, obtained from this data source
	 * @return the child of <I>parent</I> at index <I>index</I>
	 */
	public Object getChild(Object parent, int index) {
		return ((TreeNode) parent).getChildAt(index);
	}

	/**
	 * Returns the number of children of <I>parent</I>. Returns 0 if the node is
	 * a leaf or if it has no children. <I>parent</I> must be a node previously
	 * obtained from this data source.
	 * 
	 * @param parent
	 *            a node in the tree, obtained from this data source
	 * @return the number of children of the node <I>parent</I>
	 */
	public int getChildCount(Object parent) {
		return ((TreeNode) parent).getChildCount();
	}

	/**
	 * Returns whether the specified node is a leaf node. The way the test is
	 * performed depends on the <code>askAllowsChildren</code> setting.
	 * 
	 * @param node
	 *            the node to check
	 * @return true if the node is a leaf node
	 * 
	 * @see #asksAllowsChildren
	 * @see UrlTreeModel#isLeaf
	 */
	public boolean isLeaf(Object node) {
		if (asksAllowsChildren)
			return !((TreeNode) node).getAllowsChildren();
		return ((TreeNode) node).isLeaf();
	}

	/**
	 * This sets the user object of the TreeNode identified by path and posts a
	 * node changed. If you use custom user objects in the UrlTreeModel you're
	 * going to need to subclass this and set the user object of the changed
	 * node to something meaningful.
	 */
	/*public void valueForPathChanged(TreePath path, Object newValue) {
		MutableTreeNode aNode = (MutableTreeNode) path.getLastPathComponent();
		aNode.setUserObject(newValue);
	}*/

	/**
	 * Invoked this to insert newChild at location index in parents children.
	 * This will then message nodesWereInserted to create the appropriate event.
	 * This is the preferred way to add children as it will create the
	 * appropriate event.
	 */
	public void insertNodeInto(MutableTreeNode newChild,
			MutableTreeNode parent, int index) {
		parent.insert(newChild, index);

		int[] newIndexs = new int[1];

		newIndexs[0] = index;
	}

	/**
	 * Message this to remove node from its parent. This will message
	 * nodesWereRemoved to create the appropriate event. This is the preferred
	 * way to remove a node as it handles the event creation for you.
	 */
	public void removeNodeFromParent(MutableTreeNode node) {
		MutableTreeNode parent = (MutableTreeNode) node.getParent();

		if (parent == null)
			throw new IllegalArgumentException("node does not have a parent.");

		int[] childIndex = new int[1];
		Object[] removedArray = new Object[1];

		childIndex[0] = parent.getIndex(node);
		parent.remove(childIndex[0]);
		removedArray[0] = node;
	}

	/**
	 * Builds the parents of node up to and including the root node, where the
	 * original node is the last element in the returned array. The length of
	 * the returned array gives the node's depth in the tree.
	 * 
	 * @param aNode
	 *            the TreeNode to get the path for
	 */
	public TreeNode[] getPathToRoot(TreeNode aNode) {
		return getPathToRoot(aNode, 0);
	}

	/**
	 * Builds the parents of node up to and including the root node, where the
	 * original node is the last element in the returned array. The length of
	 * the returned array gives the node's depth in the tree.
	 * 
	 * @param aNode
	 *            the TreeNode to get the path for
	 * @param depth
	 *            an int giving the number of steps already taken towards the
	 *            root (on recursive calls), used to size the returned array
	 * @return an array of TreeNodes giving the path from the root to the
	 *         specified node
	 */
	protected TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
		TreeNode[] retNodes;
		// This method recurses, traversing towards the root in order
		// size the array. On the way back, it fills in the nodes,
		// starting from the root and working back to the original node.

		/*
		 * Check for null, in case someone passed in a null node, or they passed
		 * in an element that isn't rooted at root.
		 */
		if (aNode == null) {
			if (depth == 0)
				return null;
			else
				retNodes = new TreeNode[depth];
		} else {
			depth++;
			if (aNode == root)
				retNodes = new TreeNode[depth];
			else
				retNodes = getPathToRoot(aNode.getParent(), depth);
			retNodes[retNodes.length - depth] = aNode;
		}
		return retNodes;
	}

	// Serialization support.
	private void writeObject(ObjectOutputStream s) throws IOException {
		Vector values = new Vector();

		s.defaultWriteObject();
		// Save the root, if its Serializable.
		if (root != null && root instanceof Serializable) {
			values.addElement("root");
			values.addElement(root);
		}
		s.writeObject(values);
	}

	private void readObject(ObjectInputStream s) throws IOException,
			ClassNotFoundException {
		s.defaultReadObject();

		Vector values = (Vector) s.readObject();
		int indexCounter = 0;
		int maxCounter = values.size();

		if (indexCounter < maxCounter
				&& values.elementAt(indexCounter).equals("root")) {
			root = (TreeNode) values.elementAt(++indexCounter);
			indexCounter++;
		}
	}

}