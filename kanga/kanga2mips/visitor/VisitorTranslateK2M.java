package kanga.kanga2mips.visitor;

import kanga.kanga2mips.codesketch.*;
import kanga.syntaxtree.*;
import kanga.visitor.*;
import java.util.*;

public class VisitorTranslateK2M extends GJNoArguDepthFirst<MIPSCodeSet>{

	int curMorePara = 0;
	int curTotalKStack = 0;
	boolean endOfStmt = true;
	
	/**
	 * Represents a sequence of nodes nested within a choice, list,
	 * optional list, or optional, e.g. ( A B )+ or [ C D E ]
	 */
	public MIPSCodeSet visit(NodeSequence n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		for(Enumeration<Node> e = n.elements(); e.hasMoreElements();)
		{
			MIPSCodeSet temp = n.elements().nextElement().accept(this);
			ret.merge_code_set(temp);
		}
		return ret;
	}
	
	/**
	 * Represents a grammar list, e.g. ( A )+
	 */
	public MIPSCodeSet visit(NodeList n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		for(Enumeration<Node> e = n.elements(); e.hasMoreElements();)
		{
			MIPSCodeSet temp = n.elements().nextElement().accept(this);
			ret.merge_code_set(temp);
		}
		return ret;
	}
	
	/**
	 * Represents an optional grammar list, e.g. ( A )*
	 */
	public MIPSCodeSet visit(NodeListOptional n)
	{
		if(n.present())
		{
			MIPSCodeSet ret = new MIPSCodeSet();
			for(Enumeration<Node> e = n.elements(); e.hasMoreElements();)
			{
				MIPSCodeSet temp = n.elements().nextElement().accept(this);
				ret.merge_code_set(temp);
			}
			return ret;
		}
		else
			return null;
	}
	
	/**
	* f0 -> "MAIN"
	* f1 -> "["
	* f2 -> IntegerLiteral()
	* f3 -> "]"
	* f4 -> "["
	* f5 -> IntegerLiteral()
	* f6 -> "]"
	* f7 -> "["
	* f8 -> IntegerLiteral()
	* f9 -> "]"
	* f10 -> StmtList()
	* f11 -> "END"
	* f12 -> ( Procedure() )*
	* f13 -> <EOF>
	*/
	public MIPSCodeSet visit(Goal n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		int totalKStack = Integer.parseInt(n.f5.f0.tokenImage);
		int maxCalledPara = Integer.parseInt(n.f8.f0.tokenImage);
		
		this.curMorePara = 0;
		this.curTotalKStack = totalKStack;
		
		
		
		ret.emit(".text");
		ret.emit(".global main");
		ret.emit("main:");
		ret.emit("move $fp $sp");
		ret.emit("subu $sp, $sp, " + 4 * (totalKStack + maxCalledPara + 2));
		ret.emit("sw $ra, -4($fp)");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
