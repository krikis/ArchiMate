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
import archimate.codegen.Config;

public class FileHandler {

	public FileHandler() {

	}

	/**
	 * Gets the contents of a file and returns it as a string.
	 * 
	 * @param file
	 * @return
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
	 * Finds or creates a container for the specified directory and package.
	 * 
	 * @param targetDirectory
	 * @param packageName
	 * @return
	 */
	public IContainer findOrCreateContainer(String targetDirectory,
			String packageName) {
		IPath outputPath = new Path(targetDirectory + "/"
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
	 *            the byte contents of the file to save
	 * @param targetFolder
	 * @param packageName
	 * @param targetFile
	 * @return
	 */
	public IFile save(byte[] contents, String targetFolder, String packageName,
			String targetFile) {
		IContainer container = findOrCreateContainer(targetFolder, packageName);
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

		InputStream newContents = new ByteArrayInputStream(contents);
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
	
	public IFile save(byte[] contents, IFile file) {
		IFile result = null;
		try {
			result = getWritableTargetFile(file, file.getParent(), file.getName());
		} catch (CoreException e) {
			System.out.println("Could not open the targetfile.");
			e.printStackTrace();
		}

		InputStream newContents = new ByteArrayInputStream(contents);
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
	 * read-only and {@link Config#isForceOverwrite()}returns <code>true</code>,
	 * the file is made writable, otherwise a new file is returned in the
	 * specified container with filename <code>"." + fileName + ".new"</code>.
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
	 */
	public void selectAndReveal(IResource newResource) {
		BasicNewResourceWizard.selectAndReveal(newResource,
				archimate.actions.ArchiMateAction.getWindow());
	}

	/**
	 * Opens the newly created file in a new editor in Eclipse
	 * 
	 * @param resource
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
