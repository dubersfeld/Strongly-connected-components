package com.dub.spring.stronglyConnectedComponents;


public class DFSVertex extends Vertex {

	/**
	 * Subclass used for BFS 
	 */
	// all additional fields    
	private Integer parent = null;
	private Color color = Color.BLACK;
	private int d = 0;
	private int f = 0;
	private int tree = 0;
	
	public DFSVertex(DFSVertex source) {// deep copy c'tor
		super(source);
		this.parent = source.parent;
		this.color = source.color;
		this.d = source.d;
		this.f = source.f;
	}
	

	public DFSVertex() {
		super();
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getParent() {
		return parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}
	
	public int getF() {
		return f;
	}

	public void setF(int f) {
		this.f = f;
	}
	
	public int getTree() {
		return tree;
	}


	public void setTree(int tree) {
		this.tree = tree;
	}


	public String toString() {
		return name + " " + parent + " " + color + " " 
										+ d + "/" + f + " " + tree;
	}


	public static enum Color {
		BLACK, GREEN, BLUE
	}

}
