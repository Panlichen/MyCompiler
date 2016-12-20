import java.io.*;
import java.util.*;

import kanga.KangaParser;
import kanga.kanga2mips.visitor.VisitorTranslateK2M;
import kanga.syntaxtree.*;
import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.TokenMgrError;
import minijava.minijava2piglet.symboltablem2p.SymbolTableM2P;
import minijava.syntaxtree.Goal;
import piglet.piglet2spiglet.symboltablem2sp.SymbolTableM2SP;
import piglet.piglet2spiglet.visitor.VisitorBuildSymbolTableM2SP;
import piglet.piglet2spiglet.visitor.VisitorTranslateM2SP;
import spiglet.SpigletParser;
import spiglet.spiglet2kanga.flowgraph.FlowGraph;
import spiglet.spiglet2kanga.visitor.VisitorBuildFlowGraph;
import spiglet.spiglet2kanga.visitor.VisitorTranslateSP2K;
import spiglet.syntaxtree.Node;
public class Main {

	public static void main(String[] args) {
		try
		{
			//===========minijava -> spiglet==========
			//MiniJavaParser parser = new minijava.MiniJavaParser(new FileInputStream("D:\\PKU\\compile_practice\\testCasesWOTE\\Factorial.java"));
			MiniJavaParser parser = new minijava.MiniJavaParser(System.in);
			Goal rootMJ = parser.Goal();
			ByteArrayOutputStream outMJ = new ByteArrayOutputStream();
			SymbolTableM2SP topTable = new SymbolTableM2SP();
    		VisitorBuildSymbolTableM2SP vbstm = new VisitorBuildSymbolTableM2SP();
    		VisitorTranslateM2SP vtm = new VisitorTranslateM2SP(outMJ);
    		
    		rootMJ.accept(vbstm, topTable);
    		topTable.inherit_from_ancestors();
    		
    		rootMJ.accept(vtm, topTable);
			//===========spiglet -> kanga==========
    		Node rootSP = new SpigletParser(new ByteArrayInputStream(outMJ.toByteArray())).Goal();
    		//Node root = new SpigletParser(new FileInputStream("D:\\PKU\\compile_practice\\testCasesSpiglet\\Factorial.spg")).Goal();

    		VisitorBuildFlowGraph vbfg = new VisitorBuildFlowGraph();
    		
    		rootSP.accept(vbfg);
    		Vector<FlowGraph> vecFG = vbfg.get_vecFlowGraph();
    		for(int i = 0; i < vecFG.size(); i++)
    		{
    			vecFG.elementAt(i).build_map();
    			vecFG.elementAt(i).link_BB();
    			vecFG.elementAt(i).liveness_algo();
    			//System.out.println("here");
    		}
    		//System.out.println("FG build ok");
    		ByteArrayOutputStream outSP = new ByteArrayOutputStream();
    		VisitorTranslateSP2K vtsk = new VisitorTranslateSP2K(vecFG, outSP);
    		rootSP.accept(vtsk);
    		
			//===========kanga -> MIPS==========
    		kanga.syntaxtree.Goal root = new KangaParser(new ByteArrayInputStream(outSP.toByteArray())).Goal();
			ByteArrayOutputStream outKG = new ByteArrayOutputStream();
			VisitorTranslateK2M vtkm = new VisitorTranslateK2M(outKG);
			root.accept(vtkm);
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