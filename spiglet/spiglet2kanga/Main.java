package spiglet.spiglet2kanga;

import java.io.*;
import java.util.Vector;

import spiglet.ParseException;
import spiglet.SpigletParser;
import spiglet.TokenMgrError;
import spiglet.syntaxtree.Node;
import spiglet.visitor.GJDepthFirst;
import spiglet.spiglet2kanga.flowgraph.*;
import spiglet.spiglet2kanga.visitor.*;

public class Main { 
 
    public static void main(String[] args) {
    	try {
    		//Node root = new SpigletParser(System.in).Goal();
    		Node root = new SpigletParser(new FileInputStream("D:\\PKU\\compile_practice\\testCasesSpiglet\\QuickSort.spg")).Goal();

    		VisitorBuildFlowGraph vbfg = new VisitorBuildFlowGraph();
    		
    		root.accept(vbfg);
    		Vector<FlowGraph> vecFG = vbfg.get_vecFlowGraph();
    		for(int i = 0; i < vecFG.size(); i++)
    		{
    			vecFG.elementAt(i).build_map();
    			vecFG.elementAt(i).link_BB();
    			vecFG.elementAt(i).liveness_algo();
    			//System.out.println("here");
    		}
    		//System.out.println("FG build ok");
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		VisitorTranslateSP2K vtsk = new VisitorTranslateSP2K(vecFG, out);
    		root.accept(vtsk);
    		

    	}
    	catch(TokenMgrError e){
    		//Handle Lexical Errors
    		e.printStackTrace();
    	}
    	catch (ParseException e){
    		//Handle Grammar Errors
    		e.printStackTrace();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }
}