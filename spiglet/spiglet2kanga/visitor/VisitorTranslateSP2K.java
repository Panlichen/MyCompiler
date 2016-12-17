package spiglet.spiglet2kanga.visitor;

import java.util.*;

import java.io.*;

import spiglet.visitor.*;
import spiglet.syntaxtree.*;
import spiglet.spiglet2kanga.codesketch.*;
import spiglet.spiglet2kanga.flowgraph.*;

public class VisitorTranslateSP2K extends GJNoArguDepthFirst<KangaCodeSet>{
	private Vector<FlowGraph> vecFG;
	private int curFGIdx = 0;
	private String curMethodLabel;
	private InterferenceGraph curIG;
	private boolean needToRecordTemp = false;
	private Vector<KangaCodeSet> vecRecordTemp;
	private boolean endOfStatement = true;
	private OutputStream out;
	
	public VisitorTranslateSP2K(Vector<FlowGraph> vecFG, OutputStream out)
	{
		this.vecFG = vecFG;
		this.vecRecordTemp = new Vector<KangaCodeSet>();
		this.out = out;
	}
	
	public KangaCodeSet get_temp_info(int tempNum)
	{
		KangaCodeSet ret = new KangaCodeSet();
		if(this.curIG.isSpilled(tempNum))
		{
			ret.emit("ALOAD v1 SPILLEDARG " + this.curIG.getColor(tempNum));
			ret.set_reg_address("v1");
			ret.set_exp_address("v1");
			ret.set_memory_address("SPILLEDARG " + this.curIG.getColor(tempNum));
		}
		else
		{
			int temp = this.curIG.getColor(tempNum);
			String reg;
			if( temp > 10)
			{
				reg = "s" + (temp - 11);
			}
			else
			{
				reg = "t" + (temp - 1);
			}
			ret.set_reg_address(reg);
			ret.set_exp_address(reg);
			//ret.set_memory_address(null);
		}
		return ret;
	}
	
	/**
	 * Represents a sequence of nodes nested within a choice, list,
	 * optional list, or optional, e.g. ( A B )+ or [ C D E ]
	 */
	public KangaCodeSet visit(NodeSequence n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		for(Enumeration<Node> e = n.elements(); e.hasMoreElements();)
		{
			KangaCodeSet temp = e.nextElement().accept(this);
			ret.merge_code_set(temp);
		}
		return ret;
	}
	
	/**
	 * Represents a grammar list, e.g. ( A )+
	 */
	public KangaCodeSet visit(NodeList n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		for(Enumeration<Node> e = n.elements(); e.hasMoreElements();)
		{
			KangaCodeSet temp = e.nextElement().accept(this);
			ret.merge_code_set(temp);
		}
		return ret;
	}
	
	/**
	 * Represents an optional grammar list, e.g. ( A )*
	 */
	public KangaCodeSet visit(NodeListOptional n)
	{
		if(n.present())
		{
			KangaCodeSet ret = new KangaCodeSet();
			for(Enumeration<Node> e = n.elements(); e.hasMoreElements();)
			{
				KangaCodeSet temp = e.nextElement().accept(this);
				ret.merge_code_set(temp);
			}
			return ret;
		}
		else
			return null;
	}
	
	/**
	* f0 -> "MAIN"
	* f1 -> StmtList()
	* f2 -> "END"
	* f3 -> ( Procedure() )*
	* f4 -> <EOF>
	*/
	public KangaCodeSet visit(Goal n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		this.curIG = new InterferenceGraph(this.vecFG.elementAt(curFGIdx));
		
		//System.out.println(this.curIG.get_numSpilled());
		//System.out.println(this.curIG.get_numTReg());
		//System.out.println(this.curIG.get_numSReg());
		
		int numStackSlot = this.curIG.get_numSpilled() + this.curIG.get_numTReg() + this.curIG.get_numSReg();
		ret.emit("MAIN\t[ 0 ] [ " + numStackSlot + " ] [ " + this.curIG.get_maxCalledNumPara() +" ]");
		this.curMethodLabel = "MAIN";
		KangaCodeSet sl = n.f1.accept(this);
		ret.merge_code_set(sl);
		ret.emit("END");
		ret.print_all(out);
		
		this.curFGIdx++;
		n.f3.accept(this);
		return null;
	}
	
	/**
	* f0 -> ( ( Label() )? Stmt() )*
	*/
	public KangaCodeSet visit(StmtList n)
	{
		return  n.f0.accept(this);
	}
	
	/**
	* f0 -> Label()
	* f1 -> "["
	* f2 -> IntegerLiteral()
	* f3 -> "]"
	* f4 -> StmtExp()
	*/
	public KangaCodeSet visit(Procedure n)
	{
		this.curMethodLabel = n.f0.f0.tokenImage;
		KangaCodeSet ret = new KangaCodeSet();
		this.curIG = new InterferenceGraph(this.vecFG.elementAt(curFGIdx));
		int numPara = Integer.parseInt(n.f2.f0.tokenImage);
		int morePara = (numPara > 4) ? (numPara - 4) : 0;
		this.curIG.set_numMorePara(morePara);
		int numStackSlot = morePara + this.curIG.get_numSReg() + this.curIG.get_numTReg() + this.curIG.get_numSpilled();
		ret.emit(this.curMethodLabel + "\t[ " + numPara + " ] [ " + numStackSlot + " ] [ " + this.curIG.get_maxCalledNumPara() + " ]");
		
		for(int i = 0; i < this.curIG.get_numSReg(); i++)//callee save
		{
			ret.emit("ASTORE SPILLEDARG " + (morePara + i) + " s" + i);
		}
		
		for(int i = 0; i < numPara; i++)//the idx of a para is exactly the "temp num" of the temp variable
		{
			if(this.curIG.get_tempIdxUsed().get(i))
			{
				if(i < 4)
				{
					KangaCodeSet tempInfo = this.get_temp_info(i);
					ret.emit("MOVE " + tempInfo.get_reg_address() + " a" + i);
					if(tempInfo.get_memory_address() != null)
					{
						ret.emit("ASTORE " + tempInfo.get_memory_address() + " " + tempInfo.get_reg_address());//here the reg_add should be v1
					}
				}
				else
				{
					KangaCodeSet tempInfo = this.get_temp_info(i);
					ret.emit("ALOAD " + tempInfo.get_reg_address() + " SPILLEDARG " + (i - 4));
					if(tempInfo.get_memory_address() != null)
					{
						ret.emit("ASTORE " + tempInfo.get_memory_address() + " " + tempInfo.get_reg_address());
					}
				}
			}
		}
		
		KangaCodeSet sl = n.f4.accept(this);
		ret.merge_code_set(sl);
		
		for(int i = 0; i < this.curIG.get_numSReg(); i++)
		{
			ret.emit("ALOAD s" + i + " SPILLEDARG " + (morePara + i));
		}
		
		ret.emit("END");
		ret.print_all(out);
		this.curFGIdx++;
		
		return null;
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
	public KangaCodeSet visit(Stmt n)
	{
		return n.f0.accept(this);
	}
	
	/**
	* f0 -> "NOOP"
	*/
	public KangaCodeSet visit(NoOpStmt n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		ret.emit("NOOP");
		return ret;
	}
	
	/**
	* f0 -> "ERROR"
	*/
	public KangaCodeSet visit(ErrorStmt n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		ret.emit("ERROR");
		return ret;
	}
	
	/**
	* f0 -> "CJUMP"
	* f1 -> Temp()
	* f2 -> Label()
	*/
	public KangaCodeSet visit(CJumpStmt n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		KangaCodeSet temp = n.f1.accept(this);
		ret.merge_code_set(temp);
		ret.emit("CJUMP " + temp.get_reg_address() + " " + this.curMethodLabel + n.f2.f0.tokenImage);
		return ret;
	}
	
	/**
	* f0 -> "JUMP"
	* f1 -> Label()
	*/
	public KangaCodeSet visit(JumpStmt n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		ret.emit("JUMP " + this.curMethodLabel + n.f1.f0.tokenImage);
		return ret;
	}
	
	/**
	* f0 -> "HSTORE"
	* f1 -> Temp()
	* f2 -> IntegerLiteral()
	* f3 -> Temp()
	*/
	public KangaCodeSet visit(HStoreStmt n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		KangaCodeSet temp1 = n.f1.accept(this);
		KangaCodeSet temp2 = n.f3.accept(this);
		ret.merge_code_set(temp1);
		ret.merge_code_set(temp2);
		ret.emit("HSTORE " + temp1.get_reg_address() + " " + n.f2.f0.tokenImage + " " + temp2.get_reg_address());
		return ret;
	}
	
	/**
	* f0 -> "HLOAD"
	* f1 -> Temp()
	* f2 -> Temp()
	* f3 -> IntegerLiteral()
	*/
	public KangaCodeSet visit(HLoadStmt n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		KangaCodeSet temp1 = n.f1.accept(this);
		KangaCodeSet temp2 = n.f2.accept(this);
		ret.emit("HLOAD " + temp1.get_reg_address() + " " + temp2.get_reg_address() + " " + n.f3.f0.tokenImage);
		if(temp1.get_memory_address() != null)
		{
			ret.emit("ASTORE " + temp1.get_memory_address() + " " + temp1.get_reg_address());
		}
		return ret;
	}
	
	/**
	* f0 -> "MOVE"
	* f1 -> Temp()
	* f2 -> Exp()
	*/
	public KangaCodeSet visit(MoveStmt n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		this.endOfStatement = false;
		KangaCodeSet temp = n.f1.accept(this);
		KangaCodeSet exp = n.f2.accept(this);
		this.endOfStatement = true;
		
		ret.merge_code_set(exp);
		ret.emit("MOVE " + temp.get_reg_address() + " " + exp.get_exp_address());
		if(temp.get_memory_address() != null)
		{
			ret.emit("ASTORE " + temp.get_memory_address() + " " + temp.get_reg_address());
		}
		return ret;
	}
	
	/**
	* f0 -> "PRINT"
	* f1 -> SimpleExp()
	*/
	public KangaCodeSet visit(PrintStmt n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		this.endOfStatement = false;
		KangaCodeSet se = n.f1.accept(this);
		this.endOfStatement = true;
		ret.merge_code_set(se);
		ret.emit("PRINT " + se.get_exp_address());
		return ret;
	}
	
	/**
	* f0 -> Call()
	*       | HAllocate()
	*       | BinOp()
	*       | SimpleExp()
	*/
	public KangaCodeSet visit(Exp n)
	{
		this.endOfStatement = false;
		return n.f0.accept(this);
	}
	
	/**
	* f0 -> "BEGIN"
	* f1 -> StmtList()
	* f2 -> "RETURN"
	* f3 -> SimpleExp()
	* f4 -> "END"
	*/
	public KangaCodeSet visit(StmtExp n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		this.endOfStatement = true;
		KangaCodeSet sl = n.f1.accept(this);
		KangaCodeSet se = n.f3.accept(this);
		ret.merge_code_set(sl);
		ret.merge_code_set(se);
		ret.emit("MOVE v0 " + se.get_exp_address());
		return ret;
	}
	
	/**
	* f0 -> "CALL"
	* f1 -> SimpleExp()
	* f2 -> "("
	* f3 -> ( Temp() )*
	* f4 -> ")"
	*/
	public KangaCodeSet visit(Call n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		KangaCodeSet se = n.f1.accept(this);
		for(int i = 0; i < this.curIG.get_numTReg(); i++)//caller save
		{
			ret.emit("ASTORE SPILLEDARG " + (this.curIG.get_numMorePara() + this.curIG.get_numSReg() + i) + " t" + i);
		}
		this.needToRecordTemp = true;
		this.vecRecordTemp.clear();
		n.f3.accept(this);
		this.needToRecordTemp = false;
		for(int i = 0; i < this.vecRecordTemp.size(); i++)
		{
			ret.merge_code_set(this.vecRecordTemp.elementAt(i));
			if(i < 4)
			{
				ret.emit("MOVE a" + i + " " + this.vecRecordTemp.elementAt(i).get_reg_address());
			}
			else
			{
				ret.emit("PASSARG " + (i - 3) + " " + this.vecRecordTemp.elementAt(i).get_reg_address());
			}
		}
		ret.merge_code_set(se);
		ret.emit("CALL " + se.get_exp_address());
		for(int i = 0; i < this.curIG.get_numTReg(); i++)
		{
			ret.emit("ALOAD t" + i + " SPILLEDARG " + (this.curIG.get_numMorePara() + this.curIG.get_numSReg() + i));
		}
				
		//ret.set_reg_address(null);
		ret.set_exp_address("v0");
		//ret.set_memory_address(null);
		return ret;
	}
	
	/**
	* f0 -> "HALLOCATE"
	* f1 -> SimpleExp()
	*/
	public KangaCodeSet visit(HAllocate n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		KangaCodeSet se = n.f1.accept(this);
		ret.merge_code_set(se);
		ret.set_exp_address("HALLOCATE " + se.get_exp_address());
		return ret;
	}
	
	/**
	* f0 -> Operator()
	* f1 -> Temp()
	* f2 -> SimpleExp()
	*/
	public KangaCodeSet visit(BinOp n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		KangaCodeSet temp = n.f1.accept(this);
		KangaCodeSet se = n.f2.accept(this);
		ret.merge_code_set(temp);
		ret.merge_code_set(se);
		ret.set_exp_address(n.f0.f0.choice.toString() + " " + temp.get_reg_address() + " " + se.get_exp_address());
		return ret;
	}
	
	/**
	* f0 -> Temp()
	*       | IntegerLiteral()
	*       | Label()
	*/
	public KangaCodeSet visit(SimpleExp n)
	{
		this.endOfStatement = false;
		return n.f0.accept(this);
	}
	
	/**
	* f0 -> "TEMP"
	* f1 -> IntegerLiteral()
	*/
	public KangaCodeSet visit(Temp n)
	{
		KangaCodeSet ret = this.get_temp_info(Integer.parseInt(n.f1.f0.tokenImage));
		if(this.needToRecordTemp)
		{
			this.vecRecordTemp.addElement(ret);
		}
		return ret;
	}
	
	/**
	* f0 -> <INTEGER_LITERAL>
	*/
	public KangaCodeSet visit(IntegerLiteral n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		ret.set_exp_address(n.f0.tokenImage);
		return ret;
	}
	
	/**
	* f0 -> <IDENTIFIER>
	*/
	public KangaCodeSet visit(Label n)
	{
		KangaCodeSet ret = new KangaCodeSet();
		if(this.endOfStatement)
		{
			ret.emit(this.curMethodLabel + n.f0.tokenImage);
		}
		else
		{
			ret.set_exp_address(n.f0.tokenImage);
		}
		return ret;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
