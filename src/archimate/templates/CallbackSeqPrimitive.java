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
		PPackage pack = new PPackage();
		try {
			pack.finishPage(new NullProgressMonitor(), root);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
