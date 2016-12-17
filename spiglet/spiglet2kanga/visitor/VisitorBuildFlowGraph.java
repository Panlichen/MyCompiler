package spiglet.spiglet2kanga.visitor;

import java.util.*;
import spiglet.syntaxtree.*;
import spiglet.visitor.*;
import spiglet.spiglet2kanga.flowgraph.*;

public class VisitorBuildFlowGraph extends DepthFirstVisitor{
	
	public Vector<FlowGraph> vecFlowGraph = new Vector<FlowGraph>();
	public Vector<TempInfo> curStmt = new Vector<TempInfo>();
	public BasicBlock curBB;
	public FlowGraph curFG;
	public TempInfo curTI;
	public int curNumPara;
	public boolean isLinkedToPre = false;
	public boolean needToBuildBB = false;//for Stmt's visitor to build a new BB
	public boolean endOfStmt = true;//for Label's Visitor
	public int needToRecord = 0;
	
	public Vector<FlowGraph> get_vecFlowGraph()
	{
		return this.vecFlowGraph;
	}
	
	/**
	* f0 -> "MAIN"
	* f1 -> StmtList()
	* f2 -> "END"
	* f3 -> ( Procedure() )*
	* f4 -> <EOF>
	*/
	public void visit(Goal n)
	{
		curFG = new FlowGraph(null, 0);
		this.needToBuildBB = true;
		n.f1.accept(this);
		//this.curFG.add_BB(curBB, isLinkedToPre);
		this.vecFlowGraph.addElement(curFG);
		n.f3.accept(this);
	}
	
	/**
	* f0 -> ( ( Label() )? Stmt() )*
	*/
	public void visit(StmtList n)
	{
		n.f0.accept(this);
	}
	
	/**
	* f0 -> Label()
	* f1 -> "["
	* f2 -> IntegerLiteral()
	* f3 -> "]"
	* f4 -> StmtExp()
	*/
	public void visit(Procedure n)
	{
		this.curNumPara = Integer.parseInt(n.f2.f0.tokenImage);
		n.f4.accept(this);
		this.vecFlowGraph.addElement(curFG);
	}
	
	/**
	* f0 -> NoOpStmt()
	*       | ErrorStmt()
	*       | CJumpStmt()
	*       | JumpStmt()
	*       | HStoreStmt()
	*       | HLoadStmt()
	*       | MoveStmt()
	*       | PrintStmt()
	*/
	public void visit(Stmt n)
	{
		this.endOfStmt = true;
		if(this.needToBuildBB)
		{
			this.curBB = new BasicBlock();
			this.curFG.add_BB(curBB, this.isLinkedToPre);//add the bb to the fg when the bb is built
			this.needToBuildBB = false;
			this.isLinkedToPre = true;
		}
		n.f0.accept(this);
		this.endOfStmt = true;
	}
	
	/**
	* f0 -> "NOOP"
	*/
	public void visit(NoOpStmt n)
	{
		n.f0.accept(this);
		Vector<TempInfo> statement = new Vector<TempInfo>();
		this.curBB.add_statement(statement);
	}
	
	/**
	* f0 -> "ERROR"
	*/
	public void visit(ErrorStmt n)
	{
		n.f0.accept(this);
		this.curStmt.clear();
		this.curBB.add_statement(curStmt);
	}
	
	/**
	* f0 -> "CJUMP"
	* f1 -> Temp()
	* f2 -> Label()
	*/
	public void visit(CJumpStmt n)
	{
		this.curStmt.clear();
		n.f1.accept(this);
		this.curStmt.addElement(this.curTI);
		
		this.curBB.add_jump_label(n.f2.f0.tokenImage);
		this.curBB.add_statement(curStmt);
		
		this.needToBuildBB = true;
		this.isLinkedToPre = true;
	}
	
	/**
	* f0 -> "JUMP"
	* f1 -> Label()
	*/
	public void visit(JumpStmt n)
	{
		this.curStmt.clear();
		this.curBB.add_jump_label(n.f1.f0.tokenImage);
		this.curBB.add_statement(curStmt);
		
		this.needToBuildBB = true;
		this.isLinkedToPre = false;
	}
	
	/**
	* f0 -> "HSTORE"
	* f1 -> Temp()
	* f2 -> IntegerLiteral()
	* f3 -> Temp()
	*/
	public void visit(HStoreStmt n)
	{
		this.curStmt.clear();
		n.f1.accept(this);
		this.curStmt.addElement(this.curTI);
		n.f3.accept(this);
		this.curStmt.addElement(curTI);
		
		this.curBB.add_statement(curStmt);
	}
	
	/**
	* f0 -> "HLOAD"
	* f1 -> Temp()
	* f2 -> Temp()
	* f3 -> IntegerLiteral()
	*/
	public void visit(HLoadStmt n)
	{
		this.curStmt.clear();
		n.f1.accept(this);
		this.curTI.change_typeDU();
		this.curStmt.addElement(curTI);
		
		n.f2.accept(this);
		this.curStmt.addElement(curTI);
		
		this.curBB.add_statement(curStmt);
	}
	
	/**
	* f0 -> "MOVE"
	* f1 -> Temp()
	* f2 -> Exp()
	*/
	public void visit(MoveStmt n)
	{
		this.curStmt.clear();
		n.f1.accept(this);
		this.curTI.change_typeDU();
		this.curStmt.addElement(curTI);
		
		this.endOfStmt = false;
		n.f2.accept(this);
		this.endOfStmt = true;
		this.curBB.add_statement(curStmt);
	}
	
	/**
	* f0 -> "PRINT"
	* f1 -> SimpleExp()
	*/
	public void visit(PrintStmt n)
	{
		this.curStmt.clear();
		this.endOfStmt = false;
		n.f1.accept(this);
		this.endOfStmt = true;
		
		this.curBB.add_statement(curStmt);
	}
	
	/**
	* f0 -> Call()
	*       | HAllocate()
	*       | BinOp()
	*       | SimpleExp()
	*/
	public void visit(Exp n)
	{
		this.endOfStmt = false;
		this.needToRecord++;
		n.f0.accept(this);
		this.needToRecord--;
	}
	
	/**
	* f0 -> "BEGIN"
	* f1 -> StmtList()
	* f2 -> "RETURN"
	* f3 -> SimpleExp()
	* f4 -> "END"
	*/
	public void visit(StmtExp n)
	{
		this.curTI = null;
		this.endOfStmt = false;
		n.f3.accept(this);
		this.endOfStmt = true;
		if(this.curTI != null)
		{
			this.curFG = new FlowGraph(this.curTI, this.curNumPara);
			this.curTI = null;
			this.curNumPara = 0;
		}
		else
		{
			this.curFG = new FlowGraph(null, this.curNumPara);
			this.curNumPara = 0;
		}
		
		this.needToBuildBB = true;
		this.isLinkedToPre = false;
		n.f1.accept(this);
	}
	
	/**
	* f0 -> "CALL"
	* f1 -> SimpleExp()
	* f2 -> "("
	* f3 -> ( Temp() )*
	* f4 -> ")"
	*/
	public void visit(Call n)
	{
		n.f1.accept(this);
		int notPara = this.curStmt.size();
		n.f3.accept(this);
		this.curFG.update_maxCalledNumPara(this.curStmt.size() - notPara);
	}
	
	/**
	* f0 -> "HALLOCATE"
	* f1 -> SimpleExp()
	*/
	public void visit(HAllocate n)
	{
		n.f0.accept(this);
		n.f1.accept(this);
	}
	
	/**
	* f0 -> Operator()
	* f1 -> Temp()
	* f2 -> SimpleExp()
	*/
	public void visit(BinOp n)
	{
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
	}
	
	/**
	* f0 -> "LT"
	*       | "PLUS"
	*       | "MINUS"
	*       | "TIMES"
	*/
	public void visit(Operator n)
	{
		n.f0.accept(this);
	}
	
	/**
	* f0 -> Temp()
	*       | IntegerLiteral()
	*       | Label()
	*/
	public void visit(SimpleExp n)
	{
		this.needToRecord++;
		n.f0.accept(this);
		this.needToRecord--;
	}
		
	/**
	* f0 -> "TEMP"
	* f1 -> IntegerLiteral()
	*/
	public void visit(Temp n)
	{
		int num = Integer.parseInt(n.f1.f0.tokenImage);
		this.curTI = new TempInfo(num);
		if(this.needToRecord > 0)
			this.curStmt.addElement(this.curTI);
	}
	
	/**
	* f0 -> <INTEGER_LITERAL>
	*/
	public void visit(IntegerLiteral n)
	{
		n.f0.accept(this);
	}
	
	/**
	* f0 -> <IDENTIFIER>
	*/
	public void visit(Label n)
	{
		if(this.endOfStmt)
		{
			this.curBB = new BasicBlock();
			this.curBB.set_block_label(n.f0.tokenImage);
			this.curFG.add_BB(curBB, isLinkedToPre);
			this.needToBuildBB =  false;
			this.isLinkedToPre = true;
		}
	}
	
}
