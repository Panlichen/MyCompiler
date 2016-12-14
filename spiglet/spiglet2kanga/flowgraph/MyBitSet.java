package spiglet.spiglet2kanga.flowgraph;

import java.util.BitSet;

public class MyBitSet extends BitSet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyBitSet()
	{
		super();
	}
	
	public MyBitSet(MyBitSet that)
	{
		super(that.length());
		for(int i = 0; i < that.length(); i++)
		{
			set(i, that.get(i));
		}
	}
	
	public MyBitSet my_or(MyBitSet that)
	{
		MyBitSet ret = new MyBitSet(that);
		ret.or(this);
		return ret;
	}
	
	public MyBitSet my_andNot(MyBitSet that)
	{
		MyBitSet ret = new MyBitSet(this);
		ret.andNot(that);
		return ret;
	}
	
	public boolean equals(MyBitSet that)
	{
		int l = this.length();
		if(that.length() != l)
			return false;
		for(int i = 0; i < l; i++)
		{
			if(that.get(i) != this.get(i))
				return false;
		}
		return true;
	}
}
