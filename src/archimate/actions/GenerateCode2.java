package archimate.actions;

import java.util.*;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.command.*;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.CommandActionDelegate;
import org.eclipse.emf.edit.domain.*;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.ValueSpecification;
import org.eclipse.uml2.uml.editor.UMLEditorPlugin;
import org.eclipse.uml2.uml.editor.presentation.UMLEditor;
import org.eclipse.uml2.uml.resource.UMLResource;

import archimate.patterns.mvc.MVCPattern;
import archimate.templates.*;

import org.eclipse.emf.codegen.ecore.genmodel.impl.GenPackageImpl;

import org.eclipse.gmf.runtime.diagram.ui.editparts.*;
import org.eclipse.uml2.diagram.component.part.UMLDiagramEditor;
import org.eclipse.uml2.diagram.csd.edit.parts.*;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class GenerateCode2 extends ActionDelegate implements
		IEditorActionDelegate {

	protected static IWorkbenchWindow window;

	/**
	 * This records the editor or view with which the action is currently
	 * associated.
	 * 
	 * @since 2.1.0
	 */
	protected IWorkbenchPart workbenchPart;

	/**
	 * If this action delegate is associated with an editor, it is also recorded
	 * here. This field was retained for backwards compatibility.
	 * 
	 * @deprecated As of EMF 2.1.0, replaced by {@link #workbenchPart}.
	 */
	@Deprecated
	protected IEditorPart editorPart;

	/**
	 * This records the proxy action created by the platform.
	 */
	protected IAction action;

	/**
	 * This records the editing domain of the current editor. For global popups,
	 * we try to determine the editing domain from the selected objects
	 * themselves.
	 */
	protected EditingDomain editingDomain;

	/**
	 * This records the collection of selected objects so that a new command can
	 * be easily constructed after the execution of the command previously
	 * constructed from this selection.
	 */
	protected Collection<Object> collection;

	protected org.eclipse.uml2.uml.Package myPackage = null;

	/**
	 * This records the command that is created each time the selection changes.
	 */
	protected Command command;

	/**
	 * The constructor.
	 */
	public GenerateCode2() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		if (command != UnexecutableCommand.INSTANCE) {
			readProfiles(myPackage);
		}
	}

	public void readProfiles(org.eclipse.uml2.uml.Package myPack) {
		EList<Profile> profiles = myPack.getAppliedProfiles();
		for (int i = 0; i < profiles.size(); ++i) {
			Profile profile = profiles.get(i);
			Pattern pattern;

			// Get editor file path
			IPath root = getEditorFile().getProject().getFullPath();

			// IJavaProject jproject = (IJavaProject)
			// file.getProject().getAdapter(IJavaProject.class);
			// System.out.println(jproject);

			String name = profile.getName();
			if (name.equals("MVC")) {
				pattern = new MVCPattern(myPack, root);
			} else if (name.equals("MVCSeq")) {
				pattern = new MVCSeqPattern();
			} else if (name.equals("Callback")) {
				pattern = new CallbackPrimitive();
			} else if (name.equals("CallbackSeq")) {
				pattern = new CallbackSeqPrimitive(root);
			} else {
				pattern = new Pattern();
			}
			pattern.generate_code();
		}
	}

	protected IEditorPart getEditor() {
		return workbenchPart.getSite().getPage().getActiveEditor();
	}

	protected IFile getEditorFile() {
		return (IFile) getEditor().getEditorInput().getAdapter(IFile.class);
	}

	protected org.eclipse.uml2.uml.Package getSelectedPackage() {
		if (getEditor() instanceof UMLDiagramEditor) {
			UMLDiagramEditor editor = (UMLDiagramEditor) getEditor();
			// PartSite site = (PartSite) editor.getEditorSite();
			// System.out.println(site.getContextMenuIds()[0]);
			// System.out.println(site.getContextMenuIds()[1]);
			return (org.eclipse.uml2.uml.Package) editor.getDiagram()
					.getElement();
		}

		if (collection.iterator().hasNext()){
			Object element = collection.iterator().next();
			if (element instanceof org.eclipse.uml2.uml.Package) {
				return (org.eclipse.uml2.uml.Package) element;
			}
			if (element instanceof UMLResource) {
				UMLResource resource = (UMLResource) element;
				EList<EObject> contents = resource.getContents();
				EObject object = contents.get(0);
				if (object instanceof org.eclipse.uml2.uml.Package) {
					return (org.eclipse.uml2.uml.Package) object;
				}
			}
		}
		if (getEditor() instanceof UMLEditor) {
			UMLEditor editor = (UMLEditor) getEditor();
			PartSite site = (PartSite) editor.getEditorSite();
			// System.out.println(site.getContextMenuIds()[0]);

			// UMLActionBarContributor actionbarcontributor =
			// (UMLActionBarContributor) editor
			// .getActionBarContributor();
			// MenuManager menuManager = new
			// MenuManager(UMLEditorPlugin.INSTANCE
			// .getString("_UI_UMLEditor_menu"),
			// "org.eclipse.uml2.umlMenuID");
			// menuManager.add(action);
			// actionbarcontributor.contributeToMenu(menuManager);
		}

		// If all else fails
		URI fileUri = URI.createFileURI(getEditorFile().getRawLocation()
				.toString());
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(fileUri, true);
		try {
			return (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(
					resource.getContents(), UMLPackage.Literals.PACKAGE);
		} catch (Exception we) {
			we.printStackTrace();
		}

		return null;
	}

	protected Command createActionCommand(EditingDomain editingDomain,
			Collection<?> collection) {
		if (myPackage != null) {
			return IdentityCommand.INSTANCE;
		}
		return UnexecutableCommand.INSTANCE;
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {

		// ArchiMateActionBarContributor contributor = new
		// ArchiMateActionBarContributor();
		// contributor.init((EditorActionBars) ((PartSite)
		// workbenchPart.getSite())
		// .getActionBars());

		// We will only deal with structured selections.
		//
		if (selection instanceof IStructuredSelection) {
			// Convert the selection to a collection of the selected objects.
			//
			List<?> list = ((IStructuredSelection) selection).toList();
			collection = new ArrayList<Object>(list);

			myPackage = getSelectedPackage();

			// IJavaElement jelem = getInitialJavaElement((IStructuredSelection)
			// selection);
			// System.out.println(jelem);

			// If we aren't getting the domain from the workbench part...
			// This happens when this action is used for a global popup action.
			// We try to get the editing domain from one of the objects in the
			// selection.
			//
			if (workbenchPart == null && editorPart == null) {
				for (Object object : collection) {
					editingDomain = AdapterFactoryEditingDomain
							.getEditingDomainFor(object);
					if (editingDomain != null) {
						break;
					}
				}
			}

			// If we have a good editing domain...
			//
			if (editingDomain != null) {
				// Delegate the action for this object to the editing domain.
				//
				command = createActionCommand(editingDomain, collection);

				// We can enable the action as indicated by the command,
				// and we can set all the other values from the command.
				//
				((Action) action).setEnabled(command.canExecute());

				if (command instanceof CommandActionDelegate) {
					CommandActionDelegate commandActionDelegate = (CommandActionDelegate) command;
					Object object = commandActionDelegate.getImage();
					ImageDescriptor imageDescriptor = objectToImageDescriptor(object);
					if (imageDescriptor != null) {
						((Action) action).setImageDescriptor(imageDescriptor);
					} else if (getDefaultImageDescriptor() != null) {
						((Action) action)
								.setImageDescriptor(getDefaultImageDescriptor());
					}

					if (commandActionDelegate.getText() != null) {
						((Action) action).setText(commandActionDelegate
								.getText());
					}

					if (commandActionDelegate.getDescription() != null) {
						((Action) action).setDescription(commandActionDelegate
								.getDescription());
					}

					if (commandActionDelegate.getToolTipText() != null) {
						((Action) action).setToolTipText(commandActionDelegate
								.getToolTipText());
					}
				}

				// Nothing more to do and we don't want to do the default stuff
				// below.
				//
				return;
			}
		}

		// We just can't do it.
		//
		((Action) action).setEnabled(false);

		// No point in keeping garbage.
		//
		command = null;
		collection = null;

		// Show the colourless image.
		//
		if (getDefaultImageDescriptor() != null) {
			((Action) action).setImageDescriptor(getDefaultImageDescriptor());
		}
	}

	/**
	 * For editor actions, the framework calls this when the active editor
	 * changes, so that we can connect with it. We call
	 * {@link #setActiveWorkbenchPart} to record it and its editing domain, if
	 * it can provide one.
	 */
	public void setActiveEditor(IAction action, IEditorPart editorPart) {
		setActiveWorkbenchPart(editorPart);
		this.editorPart = editorPart;
		this.action = action;
	}

	/**
	 * This records the specified workbench part, and if it is an editing domain
	 * provider, its editing domain.
	 * 
	 * @since 2.1.0
	 */
	public void setActiveWorkbenchPart(IWorkbenchPart workbenchPart) {
		// If the workbench part changes...
		//
		if (this.workbenchPart != workbenchPart) {
			// Discard the old editing domain.
			//
			editingDomain = null;

			// If there is a new one...
			//
			if (workbenchPart != null) {
				// Does this part provide an editing domain?
				//
				if (workbenchPart instanceof IEditingDomainProvider) {
					editingDomain = ((IEditingDomainProvider) workbenchPart)
							.getEditingDomain();
				}
			}

			// Record the part.
			//
			this.workbenchPart = workbenchPart;
		}
	}
	
	/**
	 * This returns the image descriptor if the command does not provide an
	 * override.
	 */
	protected ImageDescriptor getDefaultImageDescriptor() {
		return null;
	}

	protected ImageDescriptor objectToImageDescriptor(Object object) {
		return ExtendedImageRegistry.getInstance().getImageDescriptor(object);
	}
}