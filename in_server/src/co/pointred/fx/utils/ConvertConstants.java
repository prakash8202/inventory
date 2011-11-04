package co.pointred.fx.utils;

import java.util.HashMap;

public class ConvertConstants 
{
	public static final String STRING_VAR = "String";
	public static final String INT_VAR = "int";
	public static final String LONG_VAR = "Number";
	public static final String BOOLEAN_VAR = "Boolean";
	public static final String LIST_VAR = "ArrayCollection";
	public static final String DATE_VAR = "Date";
	
	public static final String DEFAULT_PACKAGE = "Default";
	public static final String STRING_PACKAGE = DEFAULT_PACKAGE;
	public static final String INT_PACKAGE = DEFAULT_PACKAGE;
	public static final String LONG_PACKAGE = DEFAULT_PACKAGE;
	public static final String DATE_PACKAGE = DEFAULT_PACKAGE;
	public static final String LIST_PACKAGE = "mx.collections.ArrayCollection";
	
	public static HashMap<String, String> hashOfJavaClass = new HashMap<String, String>();
	public static HashMap<String, String> hashOfASClass = new HashMap<String, String>();
	public static HashMap<String, String> hashOfVariables = new HashMap<String, String>();
	public static HashMap<String, String> hashOfPackages = new HashMap<String, String>();
	
	public static void setHashOfJavaClass(String className, String packageName) {
		ConvertConstants.hashOfJavaClass.put(className, packageName);
	}

	public static void setHashOfASClass(String className, String packageName) {
		ConvertConstants.hashOfASClass.put(className, packageName);
	}
	
	public static HashMap<String, String> getHashOfJavaClass(){
		return hashOfJavaClass;
	}
	
	public static HashMap<String, String> getHashOfASClass(){
		return hashOfASClass;
	}
	
	public static HashMap<String, String> getHashOfVariables(){
		//hash of variable constants
		hashOfVariables.put("String", STRING_VAR);
		hashOfVariables.put("string", STRING_VAR);
		hashOfVariables.put("Long", LONG_VAR);
		hashOfVariables.put("long", LONG_VAR);
		hashOfVariables.put("Integer", INT_VAR);
		hashOfVariables.put("int", INT_VAR);
		hashOfVariables.put("List", LIST_VAR);
		hashOfVariables.put("list", LIST_VAR);
		hashOfVariables.put("Set", LIST_VAR);
		hashOfVariables.put("set", LIST_VAR);
		hashOfVariables.put("Date", DATE_VAR);
		hashOfVariables.put("date", DATE_VAR);
		return hashOfVariables;
	}
	
	public static HashMap<String, String> getHashOfPackages(){
		//hash of packages to be imported
		hashOfPackages.put(STRING_VAR, STRING_PACKAGE);
		hashOfPackages.put(LONG_VAR, LONG_PACKAGE);
		hashOfPackages.put(INT_VAR, INT_PACKAGE);
		hashOfPackages.put(INT_VAR, INT_PACKAGE);
		hashOfPackages.put(LIST_VAR, LIST_PACKAGE);
		hashOfPackages.put(DATE_VAR, DATE_PACKAGE);
		return hashOfPackages;
	}
	
	public ConvertConstants() {
		
		
		
	}

}
