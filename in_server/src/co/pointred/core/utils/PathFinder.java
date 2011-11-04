package co.pointred.core.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This Class used to configure the project path from the property file
 * [configPath.properties]
 * 
 * @author
 * 
 */
public class PathFinder
{
	private static final String BUNDLE_NAME = "configPath"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private PathFinder()
	{
		
	}

	public static String getString(String key)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e)
		{
			return '!' + key + '!';
		}
	}
}
