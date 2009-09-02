package archimate;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ocl.uml.OCL;
import org.eclipse.ocl.uml.OCL.Helper;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ArchiMate";

	// The shared instance
	private static Activator plugin;

	// The OCL objects used for model validation
	private static OCL myOcl;
	private static Helper oclHelper;

	// The project root accessible to all plugin classes
	public static IPath projectRoot;
	// The folder containing the UML files, accessible to all plugin classes
	public static IContainer umlRoot;

	/**
	 * The constructor
	 */
	public Activator() {
		myOcl = OCL.newInstance();
		myOcl.setEvaluationTracingEnabled(false);
		myOcl.setParseTracingEnabled(false);
		oclHelper = myOcl.createOCLHelper();
	}

	public static OCL getOCL() {
		return myOcl;
	}

	public static Helper getOCLHelper() {
		return oclHelper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Logs the error
	 * 
	 * @param e
	 *            the error to log
	 */
	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, "Error", e)); //$NON-NLS-1$
	}

	/**
	 * Logs the status
	 * 
	 * @param status
	 *            the ststus to log
	 */
	private static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

}
