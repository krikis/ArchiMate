package archimate.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.uml2.uml.Profile;

import archimate.Activator;
import archimate.patterns.Pattern;
import archimate.patterns.mvc.MVCPattern;
import archimate.patterns.primitives.callback.CallbackPrimitive;
import archimate.util.SourceInspector;

/**
 * This class implements the Update UML Model action. The elements in the UML
 * model are updated with respect to the current state of the source code.
 * 
 * @author Samuel Esposito
 */
public class UpdateModel extends ArchiMateAction {
	// Dialog displaying the code generation report
	private ErrorDialog error = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * archimate.actions.ArchiMateAction#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		setProjectRoot();
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
	private ErrorDialog readProfiles(org.eclipse.uml2.uml.Package umlPackage,
			final IProgressMonitor monitor) {
		EList<Profile> profiles = umlPackage.getAppliedProfiles();
		// Calculating number of tasks
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		int tasks = 0;
		int newtasks = collectPatterns(umlPackage, monitor, profiles, patterns);
		if (monitor.isCanceled()) { // return if cancel is requested
			return null;
		}
		tasks += newtasks;
		// If no pattern has been found, the primitives are processed separately
		if (newtasks == 0) {
			tasks += collectPrimitives(umlPackage, monitor, profiles, patterns);
		}
		if (monitor.isCanceled()) { // return if cancel is requested
			return null;
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
			pattern.update_model(monitor, status);
		}
		return processStatus(monitor, status);
	}

	// Goes through all applied profiles and collects the design patterns
	private int collectPatterns(org.eclipse.uml2.uml.Package umlPackage,
			final IProgressMonitor monitor, EList<Profile> profiles,
			ArrayList<Pattern> patterns) {
		int tasks = 0;
		for (Profile profile : profiles) {
			if (monitor.isCanceled()) { // return if cancel is requested
				return tasks;
			}
			Pattern pattern = null;
			String name = profile.getName();
			if (name.equals("MVC")) {
				pattern = new MVCPattern(umlPackage);
			}
			if (pattern != null) {
				tasks += pattern.estimateTasks(SourceInspector.UPDATE);
				patterns.add(pattern);
			}
		}
		return tasks;
	}

	// Goes through all applied profiles and collects the design primtives
	private int collectPrimitives(org.eclipse.uml2.uml.Package umlPackage,
			final IProgressMonitor monitor, EList<Profile> profiles,
			ArrayList<Pattern> patterns) {
		int tasks = 0;
		for (Profile profile : profiles) {
			if (monitor.isCanceled()) { // return if cancel is requested
				return tasks;
			}
			Pattern primitive = null;
			String name = profile.getName();
			if (name.equals("Callback")) {
				primitive = new CallbackPrimitive(umlPackage);
			}
			if (primitive != null) {
				tasks += primitive.estimateTasks(SourceInspector.UPDATE);
				patterns.add(primitive);
			}
		}
		return tasks;
	}

	// Generates the error dialog
	private ErrorDialog processStatus(final IProgressMonitor monitor,
			MultiStatus status) {
		MultiStatus newStatus = null;
		if (status.getSeverity() == IStatus.INFO) {
			int count = status.getChildren().length;
			newStatus = new MultiStatus(
					Activator.PLUGIN_ID,
					1,
					"The model was successfully updated. " + count + " UML "
							+ (count == 1 ? "element" : "elements") + " added.",
					null);
		} else if (status.getSeverity() == IStatus.WARNING) {
			IStatus[] children = status.getChildren();
			int count = 0;
			for (int index = 0; index < children.length; ++index) {
				if (children[index].getSeverity() == IStatus.WARNING)
					++count;
			}
			newStatus = new MultiStatus(Activator.PLUGIN_ID, 1, count
					+ (count == 1 ? " irregularity" : " irregularities")
					+ " encountered while updating the model.", null);
		} else {
			newStatus = new MultiStatus(Activator.PLUGIN_ID, 1,
					"The UML model is already up to date.", null);
			status
					.add(new Status(IStatus.INFO, status.getPlugin(), 1, "",
							null));
		}
		newStatus.addAll(status);
		ErrorDialog dialog = null;
		if (!monitor.isCanceled()) { // return if cancel is requested
			dialog = new ErrorDialog(window.getShell(),
					"Archimate UML Model Update", null, newStatus, status
							.getSeverity());
		}
		return dialog;
	}
}