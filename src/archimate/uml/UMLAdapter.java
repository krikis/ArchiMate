package archimate.uml;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.*;
import org.eclipse.uml2.uml.internal.impl.*;

import archimate.patterns.mvc.MVCPattern;

/**
 * Utility class reading out UML models
 * 
 * @author Samuel Esposito
 */
public class UMLAdapter {
	// package subject to reading
	private org.eclipse.uml2.uml.Package umlPackage;

	/**
	 * Creates new UMLAdapter for the given package
	 * 
	 * @param umlPackage
	 *            The package to read
	 */
	public UMLAdapter(org.eclipse.uml2.uml.Package umlPackage) {
		this.umlPackage = umlPackage;
	}

	/**
	 * Searches for the first UML element which has the given stereotype applied
	 * to it
	 * 
	 * @param stereotypeName
	 *            The name of the stereotype to match
	 * @return The found element
	 */
	public NamedElement getElement(String stereotypeName) {
		NamedElement namedElement = null;
		EList<NamedElement> elements = umlPackage.getOwnedMembers();
		for (Iterator<NamedElement> iter = elements.iterator(); iter.hasNext();) {
			NamedElement element = iter.next();
			EList<Stereotype> stereotypes = element.getAppliedStereotypes();
			for (Iterator<Stereotype> ite2 = stereotypes.iterator(); ite2
					.hasNext();) {
				Stereotype stereotype = ite2.next();
				if (stereotype.getName().equals(stereotypeName)) {
					return element;
				}
			}
			if (element instanceof Namespace) {
				namedElement = traverseSomeElements((Namespace) element,
						stereotypeName);
				if (namedElement != null)
					return namedElement;
			}
		}
		return namedElement;
	}

	// Recursively traverses the UML tree until a desired element was found
	private NamedElement traverseSomeElements(Namespace umlElement,
			String stereotypeName) {
		NamedElement namedElement = null;
		EList<NamedElement> elements = umlElement.getOwnedMembers();
		for (Iterator<NamedElement> iter = elements.iterator(); iter.hasNext();) {
			NamedElement element = iter.next();
			EList<Stereotype> stereotypes = element.getAppliedStereotypes();
			for (Iterator<Stereotype> ite2 = stereotypes.iterator(); ite2
					.hasNext();) {
				Stereotype stereotype = ite2.next();
				if (stereotype.getName().equals(stereotypeName)) {
					return element;
				}
			}
			if (element instanceof Namespace) {
				namedElement = traverseSomeElements((Namespace) element,
						stereotypeName);
				if (namedElement != null)
					return namedElement;
			}
		}
		return namedElement;
	}

	/**
	 * Searches for the first UML element which has the given stereotype applied
	 * to it
	 * 
	 * @param stereotypeName
	 *            The name of the stereotype to match
	 * @return The name of the found UML element
	 */
	public String getElementName(String stereotypeName) {
		String name = "";
		EList<NamedElement> elements = umlPackage.getOwnedMembers();
		for (Iterator<NamedElement> iter = elements.iterator(); iter.hasNext();) {
			NamedElement element = iter.next();
			EList<Stereotype> stereotypes = element.getAppliedStereotypes();
			for (Iterator<Stereotype> ite2 = stereotypes.iterator(); ite2
					.hasNext();) {
				Stereotype stereotype = ite2.next();
				if (stereotype.getName().equals(stereotypeName)) {
					name = element.getName();
					if ((name == null || name.equals(""))
							&& element instanceof TypedElement) {
						TypedElement typedElement = (TypedElement) element;
						Type type = typedElement.getType();
						if (type.getName() != null) {
							name = type.getName();
						}
					}
					if (name != null)
						return name;
					return "";
				}
			}
			if (element instanceof Namespace) {
				name = traverseSome((Namespace) element, stereotypeName);
				if (!name.equals(""))
					return name;
			}
		}
		return name;
	}

	// Recursively traverses the UML tree until a desired element was found
	private String traverseSome(Namespace umlElement, String stereotypeName) {
		String name = "";
		EList<NamedElement> elements = umlElement.getOwnedMembers();
		for (Iterator<NamedElement> iter = elements.iterator(); iter.hasNext();) {
			NamedElement element = iter.next();
			EList<Stereotype> stereotypes = element.getAppliedStereotypes();
			for (Iterator<Stereotype> ite2 = stereotypes.iterator(); ite2
					.hasNext();) {
				Stereotype stereotype = ite2.next();
				if (stereotype.getName().equals(stereotypeName)) {
					name = element.getName();
					if ((name == null || name.equals(""))
							&& element instanceof TypedElement) {
						TypedElement typedElement = (TypedElement) element;
						Type type = typedElement.getType();
						if (type.getName() != null) {
							name = type.getName();
						}
					}
					if (name != null)
						return name;
					return "";
				}
			}
			if (element instanceof Namespace) {
				name = traverseSome((Namespace) element, stereotypeName);
				if (!name.equals(""))
					return name;
			}
		}
		return name;
	}

	/**
	 * Searches for all UML elements which have the given stereotype applied to
	 * it
	 * 
	 * @param stereotypeName
	 *            The name of the stereotype to match
	 * @return The names of the found UML elements
	 */
	public ArrayList<String> getElementNames(String stereotypeName) {
		ArrayList<String> names = new ArrayList<String>();
		EList<NamedElement> elements = umlPackage.getOwnedMembers();
		for (Iterator<NamedElement> iter = elements.iterator(); iter.hasNext();) {
			NamedElement element = iter.next();
			if (element.getName() != null) {
				EList<Stereotype> stereotypes = element.getAppliedStereotypes();
				for (Iterator<Stereotype> ite2 = stereotypes.iterator(); ite2
						.hasNext();) {
					Stereotype stereotype = ite2.next();
					if (stereotype.getName().equals(stereotypeName)) {
						names.add(element.getName());
					}
				}
			}
			if (element instanceof Namespace) {
				names.addAll(traverseAll((Namespace) element, stereotypeName));
			}
		}
		return names;
	}

	// Recursively traverses the UML tree until all matching elements are found
	private ArrayList<String> traverseAll(Namespace umlElement,
			String stereotypeName) {
		ArrayList<String> names = new ArrayList<String>();
		EList<NamedElement> elements = umlElement.getOwnedMembers();
		for (Iterator<NamedElement> iter = elements.iterator(); iter.hasNext();) {
			NamedElement element = iter.next();
			if (element.getName() != null) {
				EList<Stereotype> stereotypes = element.getAppliedStereotypes();
				for (Iterator<Stereotype> ite2 = stereotypes.iterator(); ite2
						.hasNext();) {
					Stereotype stereotype = ite2.next();
					if (stereotype.getName().equals(stereotypeName)) {
						names.add(element.getName());
					}
				}
			}
			if (element instanceof Namespace) {
				names.addAll(traverseAll((Namespace) element, stereotypeName));
			}
		}
		return names;
	}

	private Interaction getInteraction() {
		EList<NamedElement> elements = umlPackage.getOwnedMembers();
		for (Iterator<NamedElement> iter = elements.iterator(); iter.hasNext();) {
			NamedElement element = iter.next();
			if (element instanceof Interaction) {
				return (Interaction) element;
			}
		}
		return null;
	}

	private Stereotype getStereotype(String name) {
		for (Profile profile : umlPackage.getAllAppliedProfiles()) {
			for (Stereotype stereotype : profile.getOwnedStereotypes()) {
				if (stereotype.getName().equals(name)) {
					return stereotype;
				}
			}
		}
		return null;
	}

	public String addMessage(String archiMateTag, String name) {
		ArrayList<String> stereotypes = getStereotypes(archiMateTag);
		if (stereotypes.size() == 3) {
			NamedElement start = getElement(stereotypes.get(0));
			NamedElement stop = getElement(stereotypes.get(1));
			if (start instanceof Lifeline && stop instanceof Lifeline) {
				Lifeline sender = (Lifeline) start;
				Lifeline receiver = (Lifeline) stop;
				addMessage(sender, receiver, name, stereotypes.get(2));
			}
		}
		return getMessageTag(archiMateTag);
	}

	private void addMessage(Lifeline sender, Lifeline receiver, String name, String stereotype) {
		Interaction interaction = getInteraction();
		if (interaction != null) {
			Message message = interaction.createMessage(name);
			message.applyStereotype(getStereotype(stereotype));
			interaction.getMessages().add(message);
			MessageOccurrenceSpecification messOcc1 = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification();
			messOcc1.setName("invocation-start");
			messOcc1.setEnclosingInteraction(interaction);
			messOcc1.setMessage(message);
			messOcc1.getCovereds().add(sender);
			MessageOccurrenceSpecification messOcc2 = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification();
			messOcc2.setName("execution-start");
			messOcc2.setEnclosingInteraction(interaction);
			messOcc2.setMessage(message);
			messOcc2.getCovereds().add(receiver);
			BehaviorExecutionSpecification behEx1 = UMLFactory.eINSTANCE.createBehaviorExecutionSpecification();
			behEx1.setName("invocation-body");
			behEx1.setEnclosingInteraction(interaction);
			behEx1.setStart(messOcc1);
			BehaviorExecutionSpecification behEx2 = UMLFactory.eINSTANCE.createBehaviorExecutionSpecification();
			behEx2.setName("execution-body");
			behEx2.setEnclosingInteraction(interaction);
			behEx2.setStart(messOcc2);
			MessageOccurrenceSpecification messOcc3 = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification();
			messOcc3.setName("invocation-end");
			messOcc3.setEnclosingInteraction(interaction);
			messOcc3.getCovereds().add(sender);
			behEx1.setFinish(messOcc3);
			behEx1.getCovereds().add(sender);
			MessageOccurrenceSpecification messOcc4 = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification();
			messOcc4.setName("execution-end");
			messOcc4.setEnclosingInteraction(interaction);
			messOcc4.getCovereds().add(receiver);
			behEx2.setFinish(messOcc4);
			behEx2.getCovereds().add(receiver);
			message.setSendEvent(messOcc1);
			message.setReceiveEvent(messOcc2);
			
		}		
	}

	private String getMessageTag(String archiMateTag) {
		if (archiMateTag.equals(MVCPattern.DATA_INTERFACE)) {
			return MVCPattern.DATA_MESSAGE;
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INTERFACE)) {
			return MVCPattern.UPDATE_MESSAGE;
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INTERFACE)) {
			return MVCPattern.COMMAND_MESSAGE;
		}
		return "";
	}

	private ArrayList<String> getStereotypes(String archiMateTag) {
		ArrayList<String> stereotypes = new ArrayList<String>();
		if (archiMateTag.equals(MVCPattern.DATA_INTERFACE)) {
			stereotypes.add("ControlDataInstance");
			stereotypes.add("ModelDataInstance");
			stereotypes.add("DataMessage");
		}
		if (archiMateTag.equals(MVCPattern.UPDATE_INTERFACE)) {
			stereotypes.add("ModelUpdateInstance");
			stereotypes.add("ViewUpdateInstance");
			stereotypes.add("UpdateMessage");
		}
		if (archiMateTag.equals(MVCPattern.COMMAND_INTERFACE)) {
			stereotypes.add("ViewCommandInstance");
			stereotypes.add("ControlCommandInstance");
			stereotypes.add("CommandMessage");
		}
		return stereotypes;
	}

}
