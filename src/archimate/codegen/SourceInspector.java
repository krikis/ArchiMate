package archimate.codegen;

import java.util.ArrayList;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

import archimate.codegen.resource.FileHandler;

public class SourceInspector {

	Config config;
	private JavaState state;
	private ASTEngine astEngine;

	public SourceInspector(Config config, JavaState state) {
		this.config = config;
		this.state = state;
	}

	public void inspect() {
		FileHandler handler = new FileHandler();
		IContainer container = handler.findOrCreateContainer(config
				.getTargetFolder(), config.getPackage());
		IResource[] members = null;
		try {
			members = container.members();
		} catch (CoreException e) {
			System.out.println("Could not access members of the container "
					+ container.getFullPath() + ".");
			e.printStackTrace();
		}
		traverseSourceFiles(members);
	}

	private void traverseSourceFiles(IResource[] members) {
		for (int index = 0; index < members.length; index++) {
			IResource resource = members[index];
			if (resource instanceof IContainer) {
				IContainer container = (IContainer) resource;
				IResource[] newMembers = null;
				try {
					newMembers = container.members();
				} catch (CoreException e) {
					System.out.println("Could not access members "
							+ "of the container " + container.getFullPath()
							+ ".");
					e.printStackTrace();
				}
				traverseSourceFiles(newMembers);
			}
			if (resource instanceof IFile) {
				astEngine = new ASTEngine((IFile) resource, state);
				astEngine.getArchimateTags();
			}
		}
	}

}
