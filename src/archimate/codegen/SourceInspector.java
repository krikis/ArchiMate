package archimate.codegen;

import java.util.ArrayList;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

import archimate.util.FileHandler;

public class SourceInspector {

	ICodeGenerator generator;
	private ASTEngine astEngine;

	public SourceInspector(ICodeGenerator generator) {
		this.generator = generator;
	}

	public void inspect() {
		FileHandler handler = new FileHandler();
		IGenModel model = generator.model();
		IContainer container = handler.findOrCreateContainer(model
				.targetFolder(), model.packageBase());
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
				astEngine = new ASTEngine((IFile) resource, generator);
				astEngine.traverseSource();
			}
		}
	}

}
