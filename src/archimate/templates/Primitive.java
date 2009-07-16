package archimate.templates;

public class Primitive extends Pattern implements ICodeGenerator{
	
	public Primitive () {}

	public void generate_code () {
		System.out.println("Generating Pattern");
	}
	
	public void check_code () {}
	
}
