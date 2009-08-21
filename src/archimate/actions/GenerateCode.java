package archimate.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.core.runtime.IProgressMonitor;
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
 * This class implements the Generate Code action. Code is generated for the
 * selected UML package.
 * 
 * @author Samuel Esposito
 */
public class GenerateCode extends ArchiMateAction {

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
						readProfiles(myPackage, monitor);
						monitor.done();
					}
				});
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Reads out the profiles and creates a Pattern object for each one of them
	private void readProfiles(org.eclipse.uml2.uml.Package myPack,
			final IProgressMonitor monitor) {
		EList<Profile> profiles = myPack.getAppliedProfiles();
		// Calculating number of tasks
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		int tasks = 0;
		for (int i = 0; i < profiles.size(); ++i) {
			if (monitor.isCanceled()) {
				return;
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
		// Setting up progressmonitor
		monitor.beginTask("Initializing...", tasks);
		// Processing patterns
		for (Iterator<Pattern> iter = patterns.iterator(); iter.hasNext();) {
			Pattern pattern = iter.next();
			monitor
					.setTaskName("Generating Code for " + pattern.name()
							+ "...");
			pattern.generate_code(monitor);
		}
	}
}