package archimate.templates;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

public class CallbackSeqPrimitive extends Primitive {
	
	private IPath root;
	
	public CallbackSeqPrimitive (IPath root) {
		this.root = root;
	}
	
	public void generate_code () {
		System.out.println("Generating CallbackSeqPrimitive");
	}
	
}
