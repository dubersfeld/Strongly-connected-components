package com.dub.site.stronglyConnectedComponents;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.dub.site.stronglyConnectedComponents.DFSVertex.Color;


public class DFSGraph extends Graph implements Serializable {
	
	/**
	 * This subclass implements the Strongly Connected Algorithm.
	 * Once the graph is built the method Search is called a first time with natural indices sequence 
	 * to compute the f time stamp for all vertices.
	 * Then the indices are reordered according to decreasing d and search is called a second time
	 * on the transposed graph to find strongly connected components.  
	 */
	private static final long serialVersionUID = 1L;

	private Stack<Integer> stack;
	
	private int N;
	
	private Integer index = 0;// main search loop current index
	private int lastFound = 0;
	
	private int time = 0;
	
	private int treeNumber = 0;
	
	Integer[] indices;
	
	
	public DFSGraph(int N) {
		super();
		System.out.println("Constructor begin");
		stack = new SimpleStack<>();
		this.N = N;
	}
	
	// deep copy
	public DFSGraph(DFSGraph source) {
		this.N = source.N;
		this.stack = new SimpleStack<>();
	 		
		for (Vertex vertex : source.getVertices()) {
			DFSVertex dfsVertex = (DFSVertex)vertex;
			DFSVertex v = new DFSVertex(dfsVertex);// deep copy
			this.getVertices().add(v);
		}
	}
	
	
	public Integer[] getIndices() {
		return indices;
	}

	public void setIndices(Integer[] indices) {
		this.indices = indices;
	}

	/** return a new graph transposed from the original */
	public DFSGraph transpose() {
		DFSGraph tGraph = new DFSGraph(this.N);
		
		for (Vertex vertex : this.getVertices()) {
			DFSVertex dfsVertex = (DFSVertex)vertex;
			DFSVertex v = new DFSVertex(dfsVertex);// deep copy
			v.getAdjacency().clear();// needed
			tGraph.getVertices().add(v);
		}
		
		for (int i1 = 0; i1 < this.N; i1++) {// for each vertex
			Vertex vertex = this.vertices.get(i1);// original
			for (Integer i2 : vertex.adjacency) {
				tGraph.vertices.get(i2).adjacency.add(i1);
			}
		}
		
		// reset all vertices
		for (Vertex v : tGraph.vertices) {
			DFSVertex dfsv = (DFSVertex)v;
			dfsv.setColor(Color.BLACK);
			dfsv.setD(0);
			dfsv.setF(0);
			dfsv.setTree(0);
			dfsv.setParent(null);
		}
		
		tGraph.indices = new Integer[this.N];
		for (int i = 0; i < this.N; i++) {
			tGraph.indices[i] = this.indices[i];
		}
	
		return tGraph;
	}
	
	
	public Stack<Integer> getStack() {
		return stack;
	}

	public void setStack(Stack<Integer> stack) {
		this.stack = stack;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
	
	public StepResult findSCC() {
		/** DFS on this with natural indices order 
		 * then DFS on this.transpose() with indices 
		 * ordered by decreasing f of this obtained by the first DFS
		 * */
		System.out.println("findSCC begin");
		StepResult result1 = this.search(false);// first DFS
		
		System.out.println("findSCC sator");
		
		Integer[] fin = new Integer[this.N];
		
		for (int i = 0; i < this.N; i++) {
			fin[i] = result1.getVertices()[i].getF();
			System.out.println(fin[i]);
		}
			
		DFSGraph tGraph = this.transpose();// build transposed graph
		
		System.out.println("findSCC arepo");
		
		// reorder indices of tGraph
		tGraph.reorder(fin);
		
		StepResult result2 = tGraph.search(true);// second DFS
		
		return result2;
		
	}// findSCC

	
	/** main search method */
	public StepResult search(boolean second) {
		System.out.println("search begin with index " + index);
				
		index = indices[0];
				
		StepResult result = null;
				
		boolean fin = false;
				
		while (!fin) { 
			result = searchStep(second);
				
			if (result.getStatus().equals(StepResult.Status.FINISHED)) {
				fin = true;
			}
					
		}// while
				
		System.out.println("search completed");
		return result;
	}// search
	

	
	public StepResult searchStep(boolean second) {
		/** one vertex is visited at each step  
		 */
	
		ResponseVertex[] stepVertices = new ResponseVertex[this.N];
		
		StepResult result = new StepResult();// empty container
		result.setStatus(StepResult.Status.STEP);// default
		
		DFSVertex u = (DFSVertex)this.vertices.get(index);
		
		// begin with coloring
		if (u.getColor().equals(DFSVertex.Color.BLACK)) {// vertex has just been discovered
			u.setColor(DFSVertex.Color.GREEN);// visited
			time++;
			u.setD(time);
		}
			
		List<Integer> conn = u.getAdjacency();// present vertex successors 
		
	    Integer first = null;// first successor index if present
	    boolean finish = false;
	    
	    if (conn.isEmpty() || (first = this.findNotVisitedAndMark(conn, index)) == null) {
	    	finish = true;
	    }
	       
	    if (!finish) {// prepare to descend
	    
	        ((DFSVertex)this.vertices.get(first)).setParent(index);// only change here
	             
	        stack.push(index);// push present vertex before descending 	
	        index = first;// save u for the next step
	        
	    } else {// finish present vertex
	    	u.setColor(DFSVertex.Color.BLUE);
	    	time++;
	    	u.setF(time);
	    	if (second) {
	    		u.setTree(treeNumber);
	    	}
	    	if (!stack.isEmpty()) {
	    		index = stack.pop(); 
	    	} else {
	    		index = this.findNotVisited();// can be null
	    		if (index == null) {
	    			result.setStatus(StepResult.Status.FINISHED);
	    		} else if (second) {
	    			// start a new tree
	    			treeNumber++;
	    		}	
	    	}
	    }
		
	    // prepare Ajax response 
	    for (int i = 0; i < this.N;i++) {
	    	stepVertices[i] = new ResponseVertex((DFSVertex)this.vertices.get(i));
	    }
	
	    result.setVertices(stepVertices);
		
		return result;
	        
	}// searchStep
	

	// helper methods
		
	public Integer findNotVisited() {
	
		int nind = 0;
		DFSVertex v = null;
		for (nind = this.lastFound + 1; nind < N; nind++) {
			v = (DFSVertex)this.vertices.get(this.indices[nind]);// only this changed
			if (v.getColor().equals(DFSVertex.Color.BLACK)) {
				break;
			}
		}
	
		if (nind < N) {
			this.lastFound = nind;// save as initial value for next lookup 
			return this.indices[nind];// next index
		} else {
			return null;
		}
				
	}// findNotVisited
	
		
	public Integer findNotVisitedAndMark(List<Integer> list, int from) {

		// successor look up		
		int nind = 0;
		DFSVertex v = null;
		
		for (nind = 0; nind < list.size(); nind++) {
			int to = list.get(nind);
			v = (DFSVertex)this.vertices.get(to);
				
			if (v.getColor().equals(DFSVertex.Color.BLACK)) {
				break;
			}
		}
		if (nind < list.size()) {
			return list.get(nind);
		} else {
			return null;
		}
		
	}// findNotVisited
	
	private void reorder(Integer[] fin) {
		
		/** reordering indices */
		Arrays.sort(this.indices, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				if (fin[o1] < fin[o2]) {
					return 1;
				} else if (fin[o1] > fin[o2]) {
					return -1;
				} else {
					return 0;
				}     
			}
		});
	}// reorder
	
	// used for debugging only
	public void displayIndices() {
		String display = "";
		
		for (int i = 0; i < this.N; i++) {
			display += indices[i] + " ";
		}
		System.out.println(display);
	}
	
	public void displayDFS() {
		for (Vertex v : this.vertices) {
			System.out.println((DFSVertex)v);
		}
	}	
}
