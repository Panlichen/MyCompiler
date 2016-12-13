package piglet.piglet2spiglet.codesketch;



public class PigletCode extends PigletCodeAbstract{
	private String code;
	public PigletCode(String str)
	{
		this.code = str;
	}
	
	public String toString()
	{
		return this.code;
	}
	
	public static PigletCode get_labeled_code(String label)
	{
		return new PigletCode(label + " NOOP");
	}
}
