package archimate.uml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Stereotype;

public class UMLAdapter {

	private org.eclipse.uml2.uml.Package myPackage;

	public UMLAdapter(org.eclipse.uml2.uml.Package myPackage) {
		this.myPackage = myPackage;
	}

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
