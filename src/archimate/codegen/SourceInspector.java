package archimate.codegen;

import java.util.ArrayList;

import org.eclipse.core.resources.*;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

public class SourceInspector {

	private IPath root;
	Config config;
	private JETEngine jetEngine;
	private JavaState state;
	private ASTEngine astEngine;
	private ArrayList<String> tags = new ArrayList<String>();

	public SourceInspector(IPath root, JavaState state) {
		this.root = root;
		config = new Config();
		config.setTargetFolder(root + "/src");
		config.setPackageName("");
		jetEngine = new JETEngine(config);
		this.state = state;
	}

	public void inspect() {
		IContainer container = null;
		try {
			container = jetEngine.findOrCreateContainer(new NullProgressMonitor(),
					config.getTargetFolder(), config.getPackageName());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IResource[] members = null;
		try {
			members = container.members();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		traverseSourceFiles(members);
	}

	private void traverseSourceFiles(IResource[] members) {
		for (int index = 0; index < members.length; index++) {
			IResource resource = members[index];
			if (resource instanceof IContainer){
				IContainer container = (IContainer) resource;
				IResource[] newMembers = null;
				try {
					newMembers = container.members();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				traverseSourceFiles(newMembers);
			}
			if (resource instanceof IFile){
				astEngine = new ASTEngine((IFile) resource, state);
				astEngine.getArchimateTags();
			}			
		}
	}

}
