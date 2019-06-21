package com.dub.spring.stronglyConnectedComponents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** Graph has Vertices and Adjacency Lists */
public class Graph implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected List<Vertex> vertices;
	
	
	public Graph() {
		vertices = new ArrayList<>();
	}
	
	public List<Vertex> getVertices() {
		return vertices;
	}
	public void setVertices(List<Vertex> vertices) {
		this.vertices = vertices;
	}
	

	public void display() {// used for debugging only
		for (Vertex v : vertices) {
			System.out.println(v.getName());
		}
		System.out.println();
	}
	
	public void display2() {// used for debugging only
		System.out.println("display2");
		for (int i1 = 0; i1 < vertices.size(); i1++) {// for each vertex
			System.out.print(vertices.get(i1).getAdjacency().size() + " ");
			System.out.print(vertices.get(i1).getName() + " -> ");
			for (int i2 = 0; i2 < vertices.get(i1).getAdjacency().size(); i2++) {
				int lind = this.vertices.get(i1).getAdjacency().get(i2);
				System.out.print(this.vertices.get(lind).getName() + " ");
			}
			System.out.println();
		}
		System.out.println();	
	}	
	
}
