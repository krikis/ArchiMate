package archimate.primitives.callback;

import archimate.codegen.ICodeGenerator;
import archimate.primitives.Primitive;

public class CallbackPrimitive extends Primitive implements ICodeGenerator {
	
	public CallbackPrimitive () {}
	
	public void generate_code () {
		System.out.println("Generating CallbackPrimitive");
	}
	
}
