package archimate.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ocl.uml.*;
import org.eclipse.ocl.uml.OCL.*;
import org.eclipse.emf.common.command.*;
import org.eclipse.emf.common.util.*;
import org.eclipse.ui.*;
import org.eclipse.uml2.uml.*;
import archimate.Activator;

/**
 * This class implements the Validate Code action. The code in the source folder
 * of the java project is validated with respect to the selected UML package.
 * 
 * @author Samuel Esposito
 */
public class UpdateModel extends ArchiMateAction {

	private ErrorDialog error = null;

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		if (command != UnexecutableCommand.INSTANCE) {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(
					workbenchPart.getSite().getShell());
			try {
				dialog.run(true, true, new IRunnableWithProgress() {
					public void run(final IProgressMonitor monitor) {
						// Code here
						monitor.done();
					}
				});
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (error != null) {
				error.open();
			}
		}
	}
}