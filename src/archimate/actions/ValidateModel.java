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
 * This class implements the Validate Model action. The selected UML package is
 * validated.
 * 
 * @author Samuel Esposito
 */
public class ValidateModel extends ArchiMateAction {

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
						error = readPack(myPackage, monitor);
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

	// validates the UML model and reports the errors
	private ErrorDialog readPack(org.eclipse.uml2.uml.Package myPack,
			final IProgressMonitor monitor) {
		MultiStatus status = new MultiStatus(Activator.PLUGIN_ID, 1,
				"Temporary Status", null);
		monitor.beginTask("Checking OCL Constraints...", estimateRules(myPack));
		readProfiles(myPack, status, monitor);
		readStereotypes(myPack, status, monitor);

		MultiStatus newStatus = null;
		if (status.getSeverity() == IStatus.INFO) {
			newStatus = new MultiStatus(Activator.PLUGIN_ID, 1,
					"The validation completed succesfully.", null);
		} else if (status.getSeverity() == IStatus.ERROR) {
			newStatus = new MultiStatus(Activator.PLUGIN_ID, 1,
					"Errors encountered during validation.", null);
		}
		newStatus.addAll(status);
		ErrorDialog dialog = null;
		if (!monitor.isCanceled()) {
			dialog = new ErrorDialog(window.getShell(), "Archimate Validation",
					null, newStatus, status.getSeverity());
		}
		return dialog;
	}

	// estimates the number of rules to check
	private int estimateRules(org.eclipse.uml2.uml.Package myPack) {
		int rules = 0;
		EList<Profile> profiles = myPack.getAppliedProfiles();
		for (Iterator<Profile> iter = profiles.iterator(); iter.hasNext();) {
			rules += iter.next().getOwnedRules().size();
		}
		EList<Element> elements = myPack.allOwnedElements();
		for (Iterator<Element> iter = elements.iterator(); iter.hasNext();) {
			EList<Stereotype> stereotypes = iter.next().getAppliedStereotypes();
			for (Iterator<Stereotype> iter2 = stereotypes.iterator(); iter2
					.hasNext();) {
				rules += iter2.next().getOwnedRules().size();
			}
		}
		return rules;
	}

	// reads out the profiles rules and checks them
	private void readProfiles(org.eclipse.uml2.uml.Package myPack,
			MultiStatus status, final IProgressMonitor monitor) {
		EList<Profile> profiles = myPack.getAppliedProfiles();
		for (Iterator<Profile> iter = profiles.iterator(); iter.hasNext();) {
			if (monitor.isCanceled()) {
				return;
			}
			Profile profile = iter.next();
			EList<Constraint> rules = profile.getOwnedRules();
			if (rules.size() > 0) {
				monitor.setTaskName("Checking OCL Constraints for "
						+ profile.getName() + "...");
			}
			handleRules(myPack, rules, status, monitor);
		}
	}

	// reads out the stereotypes rules and checks them
	private void readStereotypes(org.eclipse.uml2.uml.Package myPack,
			MultiStatus status, final IProgressMonitor monitor) {
		EList<Element> elements = myPack.allOwnedElements();
		for (Iterator<Element> iter = elements.iterator(); iter.hasNext();) {
			if (monitor.isCanceled()) {
				return;
			}
			Element element = iter.next();
			EList<Stereotype> stereotypes = element.getAppliedStereotypes();
			for (Iterator<Stereotype> iter2 = stereotypes.iterator(); iter2
					.hasNext();) {
				if (monitor.isCanceled()) {
					return;
				}
				Stereotype stereotype = iter2.next();
				EList<Constraint> rules = stereotype.getOwnedRules();
				if (rules.size() > 0) {
					monitor.setTaskName("Checking OCL Constraints for "
							+ stereotype.getName() + "...");
				}
				handleRules(element, stereotype.getOwnedRules(), status,
						monitor);
			}
		}
	}

	// reads out the OCL and comment of a rule and runs the check
	private void handleRules(Element element, EList<Constraint> rules,
			MultiStatus status, final IProgressMonitor monitor) {
		for (int k = 0; k < rules.size(); ++k) {
			if (monitor.isCanceled()) {
				return;
			}
			Constraint rule = rules.get(k);
			EList<Comment> comments = rule.getOwnedComments();
			String comment = "";
			for (Iterator<Comment> iter = comments.iterator(); iter.hasNext();) {
				comment += iter.next().getBody() + "\n";
			}
			ValueSpecification spec = rule.getSpecification();
			String ocl = spec.stringValue();
			checkOCL(element, ocl, comment, status);
			monitor.worked(1);
		}
	}

	// checks the given OCL constraint on the given element
	private void checkOCL(Element element, String oclExpr, String comment,
			MultiStatus status) {
		boolean valid = false;

		OCL myOcl = Activator.getOCL();

		Helper oclHelper = Activator.getOCLHelper();

		oclHelper.setInstanceContext(element);

		OCLExpression oclInv = null;
		try {
			oclInv = (OCLExpression) oclHelper.createQuery(oclExpr);
		} catch (Exception e) {
			System.out.println("Invalid OCL!\n");
			e.printStackTrace();
		}

		valid = myOcl.check(element, oclInv);
		oclInv.destroy();

		if (valid) {
			status.add(new Status(IStatus.INFO, status.getPlugin(), 1,
					"SUCCESS: " + comment + "\n" + oclExpr, null));
			status.add(new Status(IStatus.INFO, status.getPlugin(), 1, "\t\""
					+ oclExpr + "\"", null));
		} else {
			status.add(new Status(IStatus.ERROR, status.getPlugin(), 1,
					"ERROR: " + comment, null));
			status.add(new Status(IStatus.ERROR, status.getPlugin(), 1, "\t\""
					+ oclExpr + "\"", null));
		}
	}
}