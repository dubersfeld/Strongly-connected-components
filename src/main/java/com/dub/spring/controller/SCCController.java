package com.dub.spring.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dub.spring.stronglyConnectedComponents.DFSGraph;
import com.dub.spring.stronglyConnectedComponents.DFSVertex;
import com.dub.spring.stronglyConnectedComponents.GraphInitRequest;
import com.dub.spring.stronglyConnectedComponents.JSONEdge;
import com.dub.spring.stronglyConnectedComponents.JSONVertex;
import com.dub.spring.stronglyConnectedComponents.SearchRequest;
import com.dub.spring.stronglyConnectedComponents.StepResult;


@Controller
public class SCCController {
	
	/** Initialize graph for SCC search */
	@RequestMapping(value="/initGraph")
	@ResponseBody
	public StepResult initGraph(@RequestBody GraphInitRequest message, 
				HttpServletRequest request) 
	{	
		List<JSONEdge> jsonEdges = message.getJsonEdges();
		List<JSONVertex> jsonVertices = message.getJsonVertices();
		
		HttpSession session = request.getSession();
		
		if (session.getAttribute("graph") != null) {
			session.removeAttribute("graph");
		}
	
		DFSGraph graph = new DFSGraph(jsonVertices.size());
			
		for (int i = 0; i < jsonVertices.size(); i++) {
			DFSVertex v = new DFSVertex();
			v.setName(jsonVertices.get(i).getName());
			v.setColor(DFSVertex.Color.BLACK);
			graph.getVertices().add(v);
		}
		
		for (int i1 = 0; i1 < jsonEdges.size(); i1++) {
			JSONEdge edge = jsonEdges.get(i1);
			int from = edge.getFrom();
			int to = edge.getTo();
			DFSVertex v1 = (DFSVertex)graph.getVertices().get(from);
			
			v1.getAdjacency().add(to);
		}
		
		Integer[] indices = new Integer[graph.getVertices().size()];
		for (int i = 0; i < graph.getVertices().size(); i++) {
			indices[i] = i;
		}
	
		graph.setIndices(indices);
		
		session.setAttribute("graph", graph);
			
		StepResult sccResponse = new StepResult();
		sccResponse.setStatus(StepResult.Status.OK);
		
		graph.display();
		graph.display2();
	
		DFSGraph tGraph = graph.transpose();
		tGraph.display();
		tGraph.display2();
	
		// here the graph is ready for the search loop
	
		System.out.println("initGraph completed");
			
		return sccResponse;
	}
	

	@RequestMapping(value="/findSCC")
	@ResponseBody
	public StepResult findSCC(@RequestBody SearchRequest message, 
											HttpServletRequest request) 
	{	
		System.out.println("controller: findSCC begin");
		StepResult result;
		
		/** retrieve the graph from the session context */
		HttpSession session = request.getSession();
		DFSGraph graph = (DFSGraph)session.getAttribute("graph");
				
		result = graph.findSCC();
		
		System.out.println("controller: findSCC return");
		return result;
	}// findSCC
	

}
