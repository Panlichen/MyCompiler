package spiglet.spiglet2kanga.flowgraph;

import java.util.*;

public class BasicBlock {
	private Vector<Vector<TempInfo>> vecStatement;
	private int curIdx;
	private int curIdx1;
	
	private Vector<BasicBlock> vecSuccessor;
	private Vector<BasicBlock> vecPredecessor;
	
	private Vector<String> vecJumpLabel;
	
	private String blockLabel;
	private MyBitSet inSet;
	private MyBitSet outSet;
	private MyBitSet defSet;
	private MyBitSet useSet;
	
	private MyBitSet livenessState;

	public BasicBlock()
	{
		this.vecStatement = new Vector<Vector<TempInfo>>();
		this.vecSuccessor = new Vector<BasicBlock>();
		this.vecPredecessor = new Vector<BasicBlock>();
		this.vecJumpLabel = new Vector<String>();
	}
	

	public void add_successor(BasicBlock bb)
	{
		if(this.vecSuccessor.contains(bb))
			return;
		this.vecSuccessor.addElement(bb);
	}
	public Vector<BasicBlock> getVecSuccessor()
	{
		return this.vecSuccessor;
	}
	
	public void add_predecessor(BasicBlock bb)
	{
		if(this.vecPredecessor.contains(bb))
			return;
		this.vecPredecessor.addElement(bb);
	}
	public Vector<BasicBlock> getVecPredecessor()
	{
		return this.vecPredecessor;
	}
	
	public void set_block_label(String label)
	{
		this.blockLabel = label;
	}
	public String get_block_label()
	{
		return this.blockLabel;
	}
	
	public void add_jump_label(String label)
	{
		this.vecJumpLabel.addElement(label);
	}
	public Vector<String> get_vec_jump_label()
	{
		return this.vecJumpLabel;
	}
	 
	public void add_statement(Vector<TempInfo> statement)
	{
		Vector<TempInfo> tempStmt = new Vector<TempInfo>();
		for(int i = 0; i < statement.size(); i++)
		{
			tempStmt.addElement(statement.elementAt(i));
		}
		this.vecStatement.addElement(tempStmt);
	}
	
	public void set_inSet(MyBitSet inSet)
	{
		this.inSet = inSet;
	}
	public MyBitSet get_inSet()
	{
		return this.inSet;
	}
	
	public void set_outSet(MyBitSet outSet)
	{
		this.outSet = outSet;
	}
	public MyBitSet get_outSet()
	{
		return this.outSet;
	}
	
	public MyBitSet get_defSet()
	{
		return this.defSet;
	}
	
	public MyBitSet get_useSet()
	{
		return this.useSet;
	}
	
	public void init_inSet_outSet()
	{
		this.inSet = new MyBitSet();
		this.outSet = new MyBitSet();
	}
	
	public void make_defSet_useSet()
	{
		this.useSet = new MyBitSet();
		this.defSet = new MyBitSet();
		for(int i = this.vecStatement.size(); i >= 0; i--)
		{
			Vector<TempInfo> statement = this.vecStatement.elementAt(i);
			for(int j = 0; j < statement.size(); j++)
			{
				this.update_defSet_useSet(statement.elementAt(j));
			}
		}
	}
	
	public void update_defSet_useSet(TempInfo v)
	{
		if(v.is_use())
		{
			this.useSet.set(v.get_temp_num());
		}
		else
		{
			this.useSet.set(v.get_temp_num(), false);
			this.defSet.set(v.get_temp_num());
		}
	}
	
	//above are for the flow graph and liveness test
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
