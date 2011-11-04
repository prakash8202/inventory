package co.pointred.core.utils;

import java.io.File;

/**
 * Constants Class This Class describes the Project directory structure and influence all the file path
 * 
 * @author
 * 
 */
public class Config
{
	public static String rootDir;
	public static String serverHostAddress;
	public static String logFolderDir;
	public static String warDir;
	public static String uiXmlDir;
	public static String configSimulationDir;
	public static String xmlTreeDir;
	public static String dynmUIDir;
	public static String configDir;
	public static String fileDir;
	public static String imageDir;
	public static String helpDir;
	public static String pdfReportDir;
	public static String libDir;

	public static String buildDir = PathFinder.getString("Config.buildDir"); //$NON-NLS-1$
	public static String srcDir = PathFinder.getString("Config.srcDir"); //$NON-NLS-1$
	public static String flexDir = PathFinder.getString("Config.flexDir"); //$NON-NLS-1$
	public static String activitiesDir;

	static
	{
		rootDir = PathFinder.getString("Config.rootDir"); //$NON-NLS-1$
		configDir = rootDir + "WEB-INF" + File.separator + "config" + File.separator; //$NON-NLS-1$ //$NON-NLS-2$
		fileDir = rootDir + "WEB-INF" + File.separator + "files" + File.separator; //$NON-NLS-1$ //$NON-NLS-2$
		imageDir = rootDir + "WEB-INF" + File.separator + "img" + File.separator; //$NON-NLS-1$ //$NON-NLS-2$
		Config.warDir = rootDir + "WEB-INF" + File.separator; //$NON-NLS-1$
		libDir = rootDir + File.separator + "WEB-INF" + File.separator + "lib"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static final void setResourcePath(String rootDirectory)
	{
		rootDir = rootDirectory + File.separator;
		configDir = rootDir + "WEB-INF" + File.separator + "config" + File.separator; //$NON-NLS-1$ //$NON-NLS-2$
		fileDir = rootDir + "WEB-INF" + File.separator + "files" + File.separator; //$NON-NLS-1$ //$NON-NLS-2$
		imageDir = rootDir + "WEB-INF" + File.separator + "img" + File.separator; //$NON-NLS-1$ //$NON-NLS-2$
		Config.warDir = rootDir + "WEB-INF" + File.separator; //$NON-NLS-1$
		libDir = rootDir + File.separator + "WEB-INF" + File.separator + "lib"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
