package archimate.uml;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Stereotype;

/**
 * Utility class reading out UML models
 * 
 * @author Samuel Esposito
 * 
 */
public class UMLAdapter {
	// package subject to reading
	private org.eclipse.uml2.uml.Package myPackage;

	/**
	 * Creates new UMLAdapter for the given package
	 * 
	 * @param myPackage
	 *            The package to read
	 */
	public UMLAdapter(org.eclipse.uml2.uml.Package myPackage) {
		this.myPackage = myPackage;
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
		EList<NamedElement> elements = myPackage.getOwnedMembers();
		for (Iterator<NamedElement> iter = elements.iterator(); iter.hasNext();) {
			NamedElement element = iter.next();
			if (!(element.getName() == null || element.getName().equals(""))) {
				EList<Stereotype> stereotypes = element.getAppliedStereotypes();
				for (int inde2 = 0; inde2 < stereotypes.size(); inde2++) {
					Stereotype stereotype = stereotypes.get(inde2);
					if (stereotype.getName().equals(stereotypeName)) {
						return element.getName();
					}
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
	private String traverseSome(Namespace UMLElement, String stereotypeName) {
		String name = "";
		EList<NamedElement> elements = UMLElement.getOwnedMembers();
		for (Iterator<NamedElement> iter = elements.iterator(); iter.hasNext();) {
			NamedElement element = iter.next();
			if (!(element.getName() == null || element.getName().equals(""))) {
				EList<Stereotype> stereotypes = element.getAppliedStereotypes();
				for (int inde2 = 0; inde2 < stereotypes.size(); inde2++) {
					Stereotype stereotype = stereotypes.get(inde2);
					if (stereotype.getName().equals(stereotypeName)) {
						return element.getName();
					}
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
		EList<NamedElement> elements = myPackage.getOwnedMembers();
		for (Iterator<NamedElement> iter = elements.iterator(); iter.hasNext();) {
			NamedElement element = iter.next();
			if (!(element.getName() == null || element.getName().equals(""))) {
				EList<Stereotype> stereotypes = element.getAppliedStereotypes();
				for (int inde2 = 0; inde2 < stereotypes.size(); inde2++) {
					Stereotype stereotype = stereotypes.get(inde2);
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
	private ArrayList<String> traverseAll(Namespace UMLElement,
			String stereotypeName) {
		ArrayList<String> names = new ArrayList<String>();
		EList<NamedElement> elements = UMLElement.getOwnedMembers();
		for (Iterator<NamedElement> iter = elements.iterator(); iter.hasNext();) {
			NamedElement element = iter.next();
			if (!(element.getName() == null || element.getName().equals(""))) {
				EList<Stereotype> stereotypes = element.getAppliedStereotypes();
				for (int inde2 = 0; inde2 < stereotypes.size(); inde2++) {
					Stereotype stereotype = stereotypes.get(inde2);
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

}
