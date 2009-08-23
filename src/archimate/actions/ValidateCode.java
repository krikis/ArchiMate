package archimate.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.command.*;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.*;
import org.eclipse.uml2.uml.Profile;

import archimate.Activator;
import archimate.patterns.Pattern;
import archimate.patterns.mvc.MVCPattern;
import archimate.patterns.primitives.callback.CallbackPrimitive;
import archimate.util.FileHandler;

/**
 * This class implements the Validate Code action. The code in the source folder
 * of the java project is validated with respect to the selected UML package.
 * 
 * @author Samuel Esposito
 */
public class ValidateCode extends ArchiMateAction {
	// Dialog displaying the code generation report
	private ErrorDialog error = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * archimate.actions.ArchiMateAction#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if (command != UnexecutableCommand.INSTANCE) {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(
					workbenchPart.getSite().getShell());
			try {
				dialog.run(true, true, new IRunnableWithProgress() {
					public void run(final IProgressMonitor monitor) {
						error = readProfiles(myPackage, monitor);
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

	// Reads out the profiles and creates a Pattern object for each one of them
	private ErrorDialog readProfiles(org.eclipse.uml2.uml.Package myPack,
			final IProgressMonitor monitor) {
		EList<Profile> profiles = myPack.getAppliedProfiles();
		// Calculating number of tasks
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		int tasks = 0;
		for (int i = 0; i < profiles.size(); ++i) {
			if (monitor.isCanceled()) {
				return null;
			}
			Profile profile = profiles.get(i);
			Pattern pattern = null;
			String name = profile.getName();
			if (name.equals("MVC")) {
				pattern = new MVCPattern(myPack);
			} else if (name.equals("Callback")) {
				pattern = new CallbackPrimitive(myPack);
			} else {
				break;
			}
			tasks += pattern.estimateTasks();
			patterns.add(pattern);
		}
		// Initializing the status
		MultiStatus status = new MultiStatus(Activator.PLUGIN_ID, 1,
				"Temporary Status", null);
		// Setting up progressmonitor
		monitor.beginTask("Initializing...", tasks);
		// Processing patterns
		for (Iterator<Pattern> iter = patterns.iterator(); iter.hasNext();) {
			Pattern pattern = iter.next();
			monitor
					.setTaskName("Validating Code for " + pattern.name()
							+ "...");
			pattern.validate_code(monitor, status);
		}
		return processStatus(monitor, status);
	}

	// Generates the error dialog
	private ErrorDialog processStatus(final IProgressMonitor monitor,
			MultiStatus status) {
		MultiStatus newStatus = null;
		if (status.getSeverity() == IStatus.INFO) {
			newStatus = new MultiStatus(Activator.PLUGIN_ID, 1,
					"The code validation completed successfully.", null);
		} else if (status.getSeverity() == IStatus.WARNING) {
			IStatus[] children = status.getChildren();
			int count = 0;
			for (int index = 0; index < children.length; ++index) {
				if (children[index].getSeverity() == IStatus.WARNING)
					++count;
			}
			newStatus = new MultiStatus(Activator.PLUGIN_ID, 1,
					"The source code is not in sync with the UML model. "
							+ count + " source code "
							+ (count == 1 ? "element" : "elements")
							+ " deviate from the UML model.", null);
		} else if (status.getSeverity() == IStatus.ERROR) {
			IStatus[] children = status.getChildren();
			int count = 0;
			for (int index = 0; index < children.length; ++index) {
				if (children[index].getSeverity() == IStatus.ERROR)
					++count;
			}
			newStatus = new MultiStatus(Activator.PLUGIN_ID, 1,
					"Some patterns or primitives are not correctly implemented. "
							+ count + (count == 1 ? " error" : " errors")
							+ " encountered during validation.", null);
		} else {
			newStatus = new MultiStatus(Activator.PLUGIN_ID, 1,
					"The source code validation completed successfully.", null);
			status
					.add(new Status(IStatus.INFO, status.getPlugin(), 1, "",
							null));
		}
		newStatus.addAll(status);
		ErrorDialog dialog = null;
		if (!monitor.isCanceled()) {
			dialog = new ErrorDialog(window.getShell(),
					"Archimate Source Code Validation", null, newStatus, status
							.getSeverity());
		}
		return dialog;
	}
}