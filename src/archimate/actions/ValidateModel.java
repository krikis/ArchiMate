package archimate.actions;

import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.ocl.uml.OCL;
import org.eclipse.ocl.uml.OCLExpression;
import org.eclipse.ocl.uml.OCL.Helper;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.ValueSpecification;

import archimate.Activator;

/**
 * This class implements the Validate Model action. The selected UML package is
 * validated.
 * 
 * @author Samuel Esposito
 */
public class ValidateModel extends ArchiMateAction {
	// Dialog displaying the model validation report
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
	private ErrorDialog readPack(org.eclipse.uml2.uml.Package umlPackage,
			final IProgressMonitor monitor) {
		// Initializing the status
		MultiStatus status = new MultiStatus(Activator.PLUGIN_ID, 1,
				"Temporary Status", null);
		monitor.beginTask("Checking OCL Constraints...",
				estimateRules(umlPackage));
		// Executing the action
		readProfiles(umlPackage, status, monitor);
		readStereotypes(umlPackage, status, monitor);
		// Rendering the error dialog
		return processStatus(monitor, status);
	}

	// Generates the error dialog
	private ErrorDialog processStatus(final IProgressMonitor monitor,
			MultiStatus status) {
		MultiStatus newStatus = null;
		if (status.getSeverity() == IStatus.INFO) {
			int count = status.getChildren().length;
			newStatus = new MultiStatus(Activator.PLUGIN_ID, 1,
					"The validation of the UML model completed succesfully. "
							+ count
							+ (count == 1 ? " constraint" : " constraints")
							+ " checked.", null);
		} else if (status.getSeverity() == IStatus.ERROR) {
			IStatus[] children = status.getChildren();
			int count = 0;
			for (int index = 0; index < children.length; ++index) {
				if (children[index].getSeverity() == IStatus.ERROR)
					++count;
			}
			count /= 2;
			newStatus = new MultiStatus(Activator.PLUGIN_ID, 1, count
					+ (count == 1 ? " error" : " errors")
					+ " encountered during UML model validation.", null);
		} else {
			newStatus = new MultiStatus(Activator.PLUGIN_ID, 1,
					"There were no constraints to check in the UML model.",
					null);
			status
					.add(new Status(IStatus.INFO, status.getPlugin(), 1, "",
							null));
		}
		newStatus.addAll(status);
		ErrorDialog dialog = null;
		if (!monitor.isCanceled()) { // return if cancel is requested
			dialog = new ErrorDialog(window.getShell(),
					"Archimate UML Model Validation", null, newStatus, status
							.getSeverity());
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
			if (monitor.isCanceled()) { // return if cancel is requested
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
			if (monitor.isCanceled()) { // return if cancel is requested
				return;
			}
			Element element = iter.next();
			EList<Stereotype> stereotypes = element.getAppliedStereotypes();
			for (Iterator<Stereotype> iter2 = stereotypes.iterator(); iter2
					.hasNext();) {
				if (monitor.isCanceled()) { // return if cancel is requested
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
			if (monitor.isCanceled()) { // return if cancel is requested
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
					"SUCCESS: " + comment, null));
			status.add(new Status(IStatus.INFO, status.getPlugin(), 1, "   \""
					+ oclExpr + "\"                                  ", null));
		} else {
			status.add(new Status(IStatus.ERROR, status.getPlugin(), 1,
					"ERROR: " + comment, null));
			status.add(new Status(IStatus.ERROR, status.getPlugin(), 1, "   \""
					+ oclExpr + "\"                                  ", null));
		}
	}
}