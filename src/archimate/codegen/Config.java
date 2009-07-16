package archimate.codegen;

import org.eclipse.core.runtime.Platform;

/**
 * Meta-data for code generation.
 * 
 * @author Remko Popma
 * @version $Revision: 1.1 $ ($Date: 2004/05/31 21:35:35 $)
 */
public class Config {

	public static final int CLASS = 0;

	public static final int INTERFACE = 1;

	private Object mModel;

	private String mPluginId;

	private String mClasspathVariable;

	private int mModelType;

	private String mClassTemplateRelativeUri;

	private String mInterfaceTemplateRelativeUri;

	private String mTemplateRelativeUri;

	private String mMergeXmlRelativeUri;

	private String mTargetFolder;

	private String mTargetFile;

	private String mAuthor;

	private String mPackageBase;

	private String mPackageName;

	private boolean mForceOverwrite = true;

	/**
	 * Constructs an uninitialized instance.
	 */
	public Config() {
	}

	/**
	 * Constructs an instance initialized by the settings of the given config.
	 */
	public Config(Config config) {
		mModelType = 0;
		mPluginId = config.getPluginId();
		mClasspathVariable = config.getClasspathVariable();
		mClassTemplateRelativeUri = config.getClassTemplateRelativeUri();
		mInterfaceTemplateRelativeUri = config
				.getInterfaceTemplateRelativeUri();
		mTargetFolder = config.getTargetFolder();
		mTargetFile = config.getTargetFile();
		mPackageBase = config.getPackageBase();
		mPackageName = config.getPackageName();
	}

	/**
	 * Returns the model object to pass to the JET template.
	 * 
	 * @return the model object to pass to the JET template
	 */
	public Object getModel() {
		return mModel;
	}

	/**
	 * Sets the model object to pass to the JET template.
	 * 
	 * @param the
	 *            model object to pass to the JET template
	 */
	public void setModel(Object object) {
		mModel = object;
	}

	/**
	 * Returns the plugin id of the plugin containing the JET template file, the
	 * JMerge control model XML file, and the runtime library JAR file
	 * containing any classes necessary to compile the translated JET template
	 * implementation class.
	 * 
	 * @return the plugin id
	 */
	public String getPluginId() {
		return mPluginId;
	}

	/**
	 * Sets the plugin id of the plugin containing the JET template file, the
	 * JMerge control model XML file, and the runtime library JAR file
	 * containing any classes necessary to compile the translated JET template
	 * implementation class.
	 * 
	 * @param id
	 *            the plugin id
	 */
	public void setPluginId(String id) {
		mPluginId = id;
	}

	/**
	 * Returns the relative URI of the XML file containing the settings for the
	 * JMerge control model.
	 * 
	 * @return the relative URI of the jmerge settings XML file
	 */
	public String getMergeXmlRelativeUri() {
		return mMergeXmlRelativeUri;
	}

	/**
	 * Sets the relative URI of the XML file containing the settings for the
	 * JMerge control model.
	 * 
	 * @param uri
	 *            the relative URI of the jmerge settings XML file
	 */
	public void setMergeXmlRelativeUri(String uri) {
		mMergeXmlRelativeUri = uri;
	}

	/**
	 * Returns the relative uri of the JET Class template file.
	 * 
	 * @return the relative uri of the JET Class template file
	 */
	public String getClassTemplateRelativeUri() {
		return mClassTemplateRelativeUri;
	}

	/**
	 * Returns the relative uri of the JET Class template file.
	 * 
	 * @param uri
	 *            the relative uri of the JET Class template file
	 */
	public void setClassTemplateRelativeUri(String uri) {
		mClassTemplateRelativeUri = uri;
	}

	/**
	 * Returns the relative uri of the JET Interface template file.
	 * 
	 * @return the relative uri of the JET Interface template file
	 */
	public String getInterfaceTemplateRelativeUri() {
		return mInterfaceTemplateRelativeUri;
	}

	/**
	 * Returns the relative uri of the JET Interface template file.
	 * 
	 * @param uri
	 *            the relative uri of the JET Interface template file
	 */
	public void setInterfaceTemplateRelativeUri(String uri) {
		mInterfaceTemplateRelativeUri = uri;
	}

	/**
	 * Sets the type of the model. The constants CLASS and INTERFACE are valid
	 * options.
	 * 
	 * @param modelType
	 */
	public void setModelType(int modelType) {
		mModelType = modelType;
	}

	/**
	 * Returns the relative uri of the JET template file.
	 * 
	 * @return the relative uri of the JET template file
	 */
	public String getTemplateRelativeUri() {
		switch (mModelType) {
		case CLASS:
			return mClassTemplateRelativeUri;
		case INTERFACE:
			return mInterfaceTemplateRelativeUri;
		}
		return mTemplateRelativeUri;
	}

	/**
	 * Returns the relative uri of the JET template file.
	 * 
	 * @param uri
	 *            the relative uri of the JET template file
	 */
	public void setTemplateRelativeUri(String uri) {
		mTemplateRelativeUri = uri;
	}

	/**
	 * Returns the target folder (relative to the workspace root) where the
	 * generated code should be saved.
	 * 
	 * @return the target folder (relative to the workspace root) where the
	 *         generated code should be saved
	 */
	public String getTargetFolder() {
		return mTargetFolder;
	}

	/**
	 * Sets the target folder (relative to the workspace root) where the
	 * generated code should be saved.
	 * 
	 * @param folder
	 *            the target folder (relative to the workspace root) where the
	 *            generated code should be saved
	 */
	public void setTargetFolder(String folder) {
		mTargetFolder = folder;
	}

	/**
	 * Returns the package base of the resource to generate.
	 * 
	 * @return the package base of the resource to generate
	 */
	public String getPackageBase() {
		return mPackageBase;
	}

	/**
	 * Sets the package base of the resource to generate.
	 * 
	 * @param name
	 *            the package base of the resource to generate
	 */
	public void setPackageBase(String name) {
		mPackageBase = name;
	}

	/**
	 * Returns the package name of the resource to generate.
	 * 
	 * @return the package name of the resource to generate
	 */
	public String getPackageName() {
		return mPackageName;
	}

	/**
	 * Returns the package of the resource to generate.
	 * 
	 * @return the package of the resource to generate
	 */
	public String getPackage() {
		if (mPackageName != null && mPackageName != "")
			return mPackageBase + "." + mPackageName;
		return mPackageBase;
	}

	/**
	 * Sets the package name of the resource to generate.
	 * 
	 * @param name
	 *            the package name of the resource to generate
	 */
	public void setPackageName(String name) {
		mPackageName = name;
	}

	/**
	 * Returns whether existing read-only files should be overwritten. This
	 * method returns <code>true</code> by default.
	 * 
	 * @return whether existing read-only files should be overwritten
	 */
	public boolean isForceOverwrite() {
		return mForceOverwrite;
	}

	/**
	 * Returns whether existing read-only files should be overwritten. This
	 * method returns <code>true</code> by default.
	 * 
	 * @return whether existing read-only files should be overwritten
	 */
	public void setForceOverwrite(boolean force) {
		mForceOverwrite = force;
	}

	/**
	 * Returns the full URI of the JET template. This URI is found by appending
	 * the relative template URI to the installation URI of the plugin specified
	 * by the {@link #getPluginId() plugin id}.
	 * 
	 * @return the full URI of the JET template
	 */
	public String getTemplateFullUri() {
		return getUri(getPluginId(), getTemplateRelativeUri());
	}

	/**
	 * Returns the full URI of the the XML file containing the settings for the
	 * JMerge control model. This URI is found by appending the relative merge
	 * XML URI to the installation URI of the plugin specified by the
	 * {@link #getPluginId() plugin id}.
	 * 
	 * @return the full URI of the the XML file containing the settings for the
	 *         JMerge control model
	 */
	public String getMergeXmlFullUri() {
		return getUri(getPluginId(), getMergeXmlRelativeUri());
	}

	private String getUri(String pluginId, String relativeUri) {
		String base = Platform.getBundle(pluginId).getEntry("/").toString();
		String result = base + relativeUri;
		return result;
	}

	/**
	 * Returns the file name of the file where the generated code should be
	 * saved.
	 * 
	 * @return the file name of the file where the generated code should be
	 *         saved
	 */
	public String getTargetFile() {
		return mTargetFile;
	}

	/**
	 * Sets the file name of the file where the generated code should be saved.
	 * 
	 * @param name
	 *            the file name of the file where the generated code should be
	 *            saved
	 */
	public void setTargetFile(String name) {
		mTargetFile = name;
	}

	/**
	 * Returns the author of the generated code.
	 * 
	 * @return the author of the generated code
	 */
	public String getAuthor() {
		return mAuthor;
	}

	/**
	 * Sets the author of the generated code.
	 * 
	 * @param name
	 *            the name of the author of the generated code.
	 */
	public void setAuthor(String name) {
		mAuthor = name;
	}

	/**
	 * Returns the classpath variable name to bind to the first jar in the
	 * plugin identified by {@link #getPluginId()}.
	 * 
	 * @return classpath variable name
	 */
	public String getClasspathVariable() {
		return mClasspathVariable;
	}

	/**
	 * Sets the classpath variable name to bind to the first jar in the plugin
	 * identified by {@link #getPluginId()}.
	 * 
	 * @param name
	 *            classpath variable name
	 */
	public void setClasspathVariable(String name) {
		mClasspathVariable = name;
	}

}