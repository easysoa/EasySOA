/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@groups.google.com
 */

package com.openwide.easysoa.tree;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

public class DefaultUrlTree implements Serializable, UrlTreeModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Root of the tree. */
	protected TreeNode root;

	/**
	 * 
	 */
	protected boolean asksAllowsChildren;

	/**
	 * 
	 */
	public DefaultUrlTree(TreeNode root) {
		this(root, false);
	}

	/**
	 *
	 */
	public DefaultUrlTree(TreeNode root, boolean asksAllowsChildren) {
		super();
		this.root = root;
		this.asksAllowsChildren = asksAllowsChildren;
	}

	/**
	 *
	 */
	public void setAsksAllowsChildren(boolean newValue) {
		asksAllowsChildren = newValue;
	}

	/**
	 * 
	 */
	public boolean asksAllowsChildren() {
		return asksAllowsChildren;
	}

	/**
	 * 
	 */
	public void setRoot(TreeNode root) {
		this.root = root;
	}

	/**
	 * 
	 */
	public Object getRoot() {
		return root;
	}

	/**
	 *
	 */
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == null || child == null)
			return -1;
		return ((TreeNode) parent).getIndex((TreeNode) child);
	}

	/**
	 *
	 */
	public Object getChild(Object parent, int index) {
		return ((TreeNode) parent).getChildAt(index);
	}

	/**
	 *
	 */
	public int getChildCount(Object parent) {
		return ((TreeNode) parent).getChildCount();
	}

	/**
	 *
	 */
	public boolean isLeaf(Object node) {
		if (asksAllowsChildren)
			return !((TreeNode) node).getAllowsChildren();
		return ((TreeNode) node).isLeaf();
	}

	/**
	 *
	 */
	public void insertNodeInto(MutableTreeNode newChild,
			MutableTreeNode parent, int index) {
		parent.insert(newChild, index);

		int[] newIndexs = new int[1];

		newIndexs[0] = index;
	}

	/**
	 * 
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
	 * 
	 */
	public TreeNode[] getPathToRoot(TreeNode aNode) {
		return getPathToRoot(aNode, 0);
	}

	/**
	 * 
	 */
	protected TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
		TreeNode[] retNodes;
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
		Vector<Object> values = new Vector<Object>();

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

		@SuppressWarnings("unchecked")
        Vector<Object> values = (Vector<Object>) s.readObject();
		int indexCounter = 0;
		int maxCounter = values.size();

		if (indexCounter < maxCounter
				&& values.elementAt(indexCounter).equals("root")) {
			root = (TreeNode) values.elementAt(++indexCounter);
			indexCounter++;
		}
	}

}