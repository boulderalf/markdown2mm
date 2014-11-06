package com.boulderalf.markdown2mm;

import generated.Node;

public class MmNodeWrapper {

	private int level;
	private Node node;

	public MmNodeWrapper(Node node, int level) {
		this.node = node;
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
}
