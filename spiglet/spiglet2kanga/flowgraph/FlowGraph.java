package spiglet.spiglet2kanga.flowgraph;

import java.util.*;

public class FlowGraph {
	private Vector<BasicBlock> vecBB;
	private Vector<Boolean> isLinkedToPredecessor;
	private Hashtable<String, BasicBlock>lableBBMap;
	
	private TempInfo exitTemp;
	
	private int numPara;
	private int maxCalledNumPara;
	
	public FlowGraph(TempInfo exitTemp, int numPara)
	{
		this.exitTemp = exitTemp;
		this.numPara = numPara;
		this.maxCalledNumPara = -1;
		this.vecBB = new Vector<BasicBlock>();
		this.isLinkedToPredecessor = new Vector<Boolean>();
		this.lableBBMap = new Hashtable<String, BasicBlock>();
	}
	
	public void build_map()
	{
		for(int i = 0; i < this.vecBB.size(); i++)
		{
			if(this.vecBB.elementAt(i).get_block_label() != null)
			{
				this.lableBBMap.put(this.vecBB.elementAt(i).get_block_label(), this.vecBB.elementAt(i));
			}
		}
	}
	
	public int get_numPara()
	{
		return this.numPara;
	}
	
	public void update_maxCalledNumPara(int num)
	{
		if(num > this.maxCalledNumPara)
		{
			this.maxCalledNumPara = num;
		}
	}
	public int get_maxCalledNumPara()
	{
		return this.maxCalledNumPara;
	}
	
	public void add_BB(BasicBlock bb, boolean isLinkedToPre)
	{
		this.vecBB.addElement(bb);
		this.isLinkedToPredecessor.addElement(isLinkedToPre);
	}
	
	public BasicBlock BB_get(String label)
	{
		return this.lableBBMap.get(label);
	}
	
	public Vector<BasicBlock> get_vecBB()
	{
		return this.vecBB;
	}
	
	public void link_BB()
	{
		for(int i = 0; i < this.vecBB.size(); i++)
		{
			for(int j = 0; j < this.vecBB.elementAt(i).get_vec_jump_label().size(); j++)
			{
				this.add_edge(this.vecBB.elementAt(i), this.BB_get(this.vecBB.elementAt(i).get_vec_jump_label().elementAt(j)));
			}
			if(this.isLinkedToPredecessor.elementAt(i))
			{
				this.add_edge(this.vecBB.elementAt(i - 1), this.vecBB.elementAt(i));
			}
		}
	}
	
	public void add_edge(BasicBlock src, BasicBlock dst)
	{
		src.add_successor(dst);
		dst.add_predecessor(src);
	}
	
	public void liveness_algo()
	{
		if(this.vecBB.size() == 0)
		{
			return ;
		}
		for(int i = 0; i < this.vecBB.size(); i++)
		{
			this.vecBB.elementAt(i).init_inSet_outSet();
			this.vecBB.elementAt(i).make_defSet_useSet();
		}
		
		MyBitSet inSet = new MyBitSet();
		MyBitSet outSet = new MyBitSet();
		
		if(this.exitTemp != null)
		{
			outSet.set(this.exitTemp.get_temp_num());
		}
		BasicBlock last = this.vecBB.elementAt(this.vecBB.size() - 1);
		last.set_outSet(outSet);
		inSet = last.get_useSet().my_or(outSet.my_andNot(last.get_defSet()));
		last.set_inSet(inSet);
		
		Queue<BasicBlock> changed = new LinkedList<BasicBlock>();
		for(int i = 0; i < this.vecBB.size() - 1; i++)
		{
			changed.add(this.vecBB.elementAt(i));
		}
		
		while(!changed.isEmpty())
		{
			BasicBlock bb = changed.poll();
			MyBitSet out = new MyBitSet();
			MyBitSet in = bb.get_inSet();
			for(int i = 0; i < bb.getVecSuccessor().size(); i++)
			{
				out = out.my_or(bb.getVecSuccessor().elementAt(i).get_inSet());
			}
			bb.set_outSet(out);
			bb.set_inSet(in.my_or(bb.get_outSet().my_andNot(bb.get_defSet())));
			if(!in.equals(bb.get_inSet()))
			{
				for(int i = 0; i < bb.getVecPredecessor().size(); i++)
				{
					changed.add(bb.getVecPredecessor().elementAt(i));
				}
			}
		}
	}
	
}
