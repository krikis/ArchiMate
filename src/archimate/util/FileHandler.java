package archimate.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import archimate.Activator;
import archimate.codegen.ASTEngine;

/**
 * Utility class for handling source files
 * 
 * @author Samuel Esposito
 * 
 */
public class FileHandler {
	// The source folder of the java project
	private String targetFolder;

	/**
	 * Creates a new {@link FileHandler} and sets the targetFolder
	 */
	public FileHandler() {
		targetFolder = Activator.projectRoot + "/src";
	}

	/**
	 * Counts the number of source files in a Java project source folder.
	 * 
	 * @return The number of files in a Java project source folder
	 */
	public int countFiles(String packageBase) {
		IContainer container = findOrCreateContainer(packageBase);
		IResource[] members = null;
		try {
			members = container.members();
		} catch (CoreException e) {
			System.out.println("Could not access members of the container "
					+ container.getFullPath() + ".");
			e.printStackTrace();
		}
		return countFiles(members);
	}

	// Recursively counts the number of files in a folder.
	private int countFiles(IResource[] members) {
		int files = 0;
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
				files += countFiles(newMembers);
			}
			if (resource instanceof IFile) {
				++files;
			}
		}
		return files;
	}

	/**
	 * Gets the contents of a file and returns it as a string.
	 * 
	 * @param file
	 *            The file to be read
	 * @return The contents of the file
	 */
	public String getSource(IFile file) {
		InputStream contents = null;
		try {
			contents = file.getContents();
		} catch (CoreException e) {
			System.out.println("Reading of file failed for file at "
					+ file.getFullPath() + ".");
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				contents));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			System.out.println("Reading of buffer failed for file at "
					+ file.getFullPath() + ".");
			e.printStackTrace();
		} finally {
			try {
				contents.close();
			} catch (IOException e) {
				System.out.println("Closing of file failed for file at "
						+ file.getFullPath() + ".");
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	/**
	 * Finds or creates a container for the specified directory and package
	 * 
	 * @param packageName
	 *            The package to create the container in
	 * @return The found or newly created container
	 */
	public IContainer findOrCreateContainer(String packageName) {
		IPath outputPath = new Path(targetFolder + "/"
				+ packageName.replace('.', '/'));
		IPath localLocation = null; // use default
		IContainer container = null;
		try {
			container = CodeGenUtil.EclipseUtil.findOrCreateContainer(
					outputPath, true, localLocation, new NullProgressMonitor());
		} catch (CoreException e) {
			System.out.println("Container at " + outputPath
					+ " could not be found or created.");
			e.printStackTrace();
		}
		return container;
	}

	/**
	 * Saves the specified contents to a location specified by the targetFolder,
	 * packageName and targetFile parameters. The location of the file to save
	 * is found by finding or creating the container (folder) for the package in
	 * the target folder. The name of the file to save is the target file.
	 * 
	 * @param contents
	 *            The contents of the file to save
	 * @param packageName
	 *            The package the file is saved in
	 * @param targetFile
	 *            The file name
	 * @return The saved file
	 */
	public IFile save(String contents, String packageName, String targetFile) {
		IContainer container = findOrCreateContainer(packageName);
		if (container == null) {
			System.out
					.println("Cound not find or create container for package "
							+ packageName + " in " + targetFolder);
		}
		IFile file = container.getFile(new Path(targetFile));
		IFile result = null;
		try {
			result = getWritableTargetFile(file, container, targetFile);
		} catch (CoreException e) {
			System.out.println("Could not open the targetfile.");
			e.printStackTrace();
		}

		InputStream newContents = new ByteArrayInputStream(contents.getBytes());
		try {
			if (result.exists()) {
				result.setContents(newContents, true, true,
						new NullProgressMonitor());
			} else {
				result.create(newContents, true, new NullProgressMonitor());
			}
		} catch (CoreException e) {
			System.out.println("Could not write to targetfile.");
		}
		return result;
	}

	/**
	 * Saves the contents to an already opened file
	 * 
	 * @param contents
	 *            The contents string
	 * @param file
	 *            The open file to write to
	 * @return The saved file
	 */
	public IFile save(String contents, IFile file) {
		IFile result = null;
		try {
			result = getWritableTargetFile(file, file.getParent(), file
					.getName());
		} catch (CoreException e) {
			System.out.println("Could not open the targetfile.");
			e.printStackTrace();
		}

		InputStream newContents = new ByteArrayInputStream(contents.getBytes());
		try {
			if (result.exists()) {
				result.setContents(newContents, true, true,
						new NullProgressMonitor());
			} else {
				result.create(newContents, true, new NullProgressMonitor());
			}
		} catch (CoreException e) {
			System.out.println("Could not write to targetfile.");
		}
		return result;
	}

	/**
	 * Returns a <code>IFile</code> that can be written to. If the specified
	 * file is read-write, it is returned unchanged. If the specified file is
	 * read-only, the file is made writable, otherwise a new file is returned in
	 * the specified container with filename
	 * <code>"." + fileName + ".new"</code>.
	 * 
	 * @param container
	 *            container to create the new file in if the specified file
	 *            cannot be made writable
	 * @param targetFile
	 *            the file to make writable
	 * @param fileName
	 *            used to create a new file name if the specified file cannot be
	 *            made writable
	 * @return a <code>IFile</code> that can be written to
	 * @throws CoreException
	 */
	private IFile getWritableTargetFile(IFile targetFile, IContainer container,
			String fileName) throws CoreException {
		boolean forceOverwrite = true;
		if (targetFile.isReadOnly()) {
			if (forceOverwrite) {
				ResourceAttributes attributes = new ResourceAttributes();
				attributes.setReadOnly(false);
				((IResource) targetFile).setResourceAttributes(attributes);
			} else {
				targetFile = container
						.getFile(new Path("." + fileName + ".new"));
			}
		}
		return targetFile;
	}

	/**
	 * Reveals the newly created file in the Eclipse Package Explorer
	 * 
	 * @param newResource
	 *            The file to reveal
	 */
	public void selectAndReveal(IResource newResource) {
		BasicNewResourceWizard.selectAndReveal(newResource,
				archimate.actions.ArchiMateAction.getWindow());
	}

	/**
	 * Opens the newly created file in a new editor in Eclipse
	 * 
	 * @param resource
	 *            The resource to open
	 */
	public void openResource(final IResource resource) {
		if (resource.getType() == IResource.FILE) {
			final IWorkbenchPage activePage = archimate.actions.ArchiMateAction
					.getWindow().getActivePage();
			if (activePage != null) {
				final Display display = archimate.actions.ArchiMateAction
						.getWindow().getShell().getDisplay();
				if (display != null) {
					display.asyncExec(new Runnable() {
						public void run() {
							try {
								IDE.openEditor(activePage, (IFile) resource,
										true);
							} catch (PartInitException e) {
								Activator.log(e);
							}
						}
					});
				}
			}
		}
	}

}
