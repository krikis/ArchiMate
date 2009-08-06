package archimate.codegen;

import java.util.ArrayList;

public interface IGenModel {
	
	public ArrayList<String> imports(String archiMateTag);
	
	public String objectClass(String archiMateTag);
	
	public String objectName(String archiMateTag);
	
	public ArrayList<String> methods(String archiMateTag);
	
	public ArrayList<String> methodInvocations(String archiMateTag);

}
