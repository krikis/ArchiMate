package archimate.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.uml2.common.edit.domain.UML2AdapterFactoryEditingDomain;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.uml2.uml.editor.presentation.UMLEditor;
import org.eclipse.uml2.uml.*;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.command.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.CommandActionDelegate;
import org.eclipse.emf.edit.domain.*;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

/**
 * This abstract class takes care of activating the action and setting the
 * necessary variables
 * 
 * @author Samuel Esposito
 */
public abstract class ArchiMateAction extends ActionDelegate implements
		IWorkbenchWindowActionDelegate, IEditorActionDelegate {

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

	public static IWorkbenchWindow getWindow() {
		return window;
	}

	public ArchiMateAction() {
	}

	public void run(IAction action) {
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

	// returns the active editor
	protected IEditorPart getEditor() {
		if (workbenchPart != null) {
			return workbenchPart.getSite().getPage().getActiveEditor();
		}
		return null;
	}

	// returns the file in the active editor
	protected IFile getEditorFile() {
		return (IFile) getEditor().getEditorInput().getAdapter(IFile.class);
	}

	// returns the target UML model
	protected org.eclipse.uml2.uml.Package getSelectedPackage() {
		if (getEditor() instanceof DiagramDocumentEditor) {
			DiagramDocumentEditor editor = (DiagramDocumentEditor) getEditor();
			return (org.eclipse.uml2.uml.Package) editor.getDiagram()
					.getElement();
		}
		if (getEditor() instanceof UMLEditor) {
			UMLEditor editor = (UMLEditor) getEditor();
			UML2AdapterFactoryEditingDomain domain = (UML2AdapterFactoryEditingDomain) editor
					.getEditingDomain();
			Resource resource = domain.getResourceSet().getResources()
					.iterator().next();
			try {
				return (org.eclipse.uml2.uml.Package) EcoreUtil
						.getObjectByType(resource.getContents(),
								UMLPackage.Literals.PACKAGE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	// checks if the action is currently available
	protected Command createActionCommand(EditingDomain editingDomain,
			Collection<?> collection) {
		if (myPackage != null) {
			return IdentityCommand.INSTANCE;
		}
		return UnexecutableCommand.INSTANCE;
	}

	/**
	 * Utility method to inspect a selection to find a Java element.
	 * 
	 * @param selection
	 *            the selection to be inspected
	 * @return a Java element to be used as the initial selection, or
	 *         <code>null</code>, if no Java element exists in the given
	 *         selection
	 */
	protected IJavaElement getInitialJavaElement(IStructuredSelection selection) {
		IJavaElement jelem = null;
		if (selection != null && !selection.isEmpty()) {
			Object selectedElement = selection.getFirstElement();
			if (selectedElement instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) selectedElement;

				jelem = (IJavaElement) adaptable.getAdapter(IJavaElement.class);
				if (jelem == null) {
					IResource resource = (IResource) adaptable
							.getAdapter(IResource.class);
					if (resource != null
							&& resource.getType() != IResource.ROOT) {
						while (jelem == null
								&& resource.getType() != IResource.PROJECT) {
							resource = resource.getParent();
							jelem = (IJavaElement) resource
									.getAdapter(IJavaElement.class);
						}
						if (jelem == null) {
							jelem = JavaCore.create(resource); // java project
						}
					}
				}
			}
		}
		return jelem;
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
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
				if (workbenchPart instanceof DiagramDocumentEditor) {
					editingDomain = ((DiagramDocumentEditor) workbenchPart)
							.getEditingDomain();
				}
			}

			this.window = workbenchPart.getSite().getWorkbenchWindow();

			// Record the part.
			//
			this.workbenchPart = workbenchPart;
		}
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
		this.editorPart = window.getActivePage().getActiveEditor();
		setActiveWorkbenchPart(window.getActivePage().getActiveEditor());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.actions.ActionDelegate#init(org.eclipse.jface.action.IAction
	 * )
	 */
	public void init(IAction action) {
		this.action = action;
	}

	/**
	 * This returns the image descriptor if the command does not provide an
	 * override.
	 */
	protected ImageDescriptor getDefaultImageDescriptor() {
		return null;
	}

	/**
	 * Returns the {@link ImageDescriptor} of the object
	 * 
	 * @param object
	 * @return The {@link ImageDescriptor} of the object
	 */
	protected ImageDescriptor objectToImageDescriptor(Object object) {
		return ExtendedImageRegistry.getInstance().getImageDescriptor(object);
	}
}
