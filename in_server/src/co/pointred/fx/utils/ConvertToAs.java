package co.pointred.fx.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;


public class ConvertToAs {

	
	private StringBuffer content_buffer;
	private StringBuffer import_buffer;
	private StringBuffer variable_buffer;
	
	private String javaParentFolder;
	private String asParentFolder;
	
	private String javaClassName;
	private String javaPackageName;
	private String asPackageName;
	private String asClassName;
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		ConvertToAs convertToAs = new ConvertToAs();
		convertToAs.setJavaParentFolder("co.pointred.ibatis.pojo");
		convertToAs.setAsParentFolder("co.inventory.pojo");
		convertToAs.convertFiles();
	}
	
	private void convertFiles() throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		searchForFiles();
		
		for(String classVal : ConvertConstants.getHashOfJavaClass().keySet()){
			
			content_buffer = new StringBuffer();
			import_buffer = new StringBuffer();
			variable_buffer = new StringBuffer();
			
			javaClassName = classVal;
			javaPackageName = ConvertConstants.getHashOfJavaClass().get(javaClassName);
			asPackageName = ConvertConstants.getHashOfASClass().get(javaClassName);
			asClassName = javaClassName;
			
			Class<?> clazz = Class.forName(javaPackageName + "." + javaClassName);
			Field[] fields = clazz.getDeclaredFields();
			
			HashMap<String, String> hashOfVariables = ConvertConstants.getHashOfVariables();
			HashMap<String, String> hashOfPackages = ConvertConstants.getHashOfPackages();
			
			for(Field field : fields)
			{
				if(Modifier.isPrivate(field.getModifiers()))
				{
					field.setAccessible(true);
					String fieldName = field.getName();
					String fieldType = field.getType().getSimpleName();
					if(fieldName != "hashCode")
					{
//						System.out.println(fieldType+" "+fieldName);
						String import_package = hashOfPackages.get(hashOfVariables.get(fieldType));
						
						if(import_package == null)
						{
							import_package = asPackageName;
						}
						
						if(import_buffer.indexOf(import_package) == -1 && !(import_package.equals(ConvertConstants.DEFAULT_PACKAGE))){
							import_buffer.append("\timport ");
							import_buffer.append(import_package);
							import_buffer.append(";\n");
						}
						
						
						String dataType = hashOfVariables.get(fieldType);
						
						if(dataType == null)
						{
							dataType = fieldType;
						}
						
						variable_buffer.append("\t\tpublic var ");
						variable_buffer.append(fieldName);
						variable_buffer.append(":");
						variable_buffer.append(dataType);
						variable_buffer.append(";\n");
					}
				}
			}
			
			construct_content();
			write_to_file();
		}
	}
	
	private void searchForFiles() {
		String javaFolder = "//home//prakash//projects//inventory//in_server/src//"+getJavaParentFolder().replace(".", "//");
		String flexFolder = "//home//prakash//projects//inventory//in_client//src//"+getAsParentFolder().replace(".", "//");
		File folder = new File(javaFolder);
	    File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isDirectory()) {
	        System.out.println("-" + listOfFiles[i].getName());
	        
	        String childFolderName = listOfFiles[i].getName();
	        String childFolder = javaFolder + File.separator + childFolderName;
	        File subFolder = new File(childFolder);
	        File[] listOfSubFiles = subFolder.listFiles();
	        
	        for (int ii = 0; ii < listOfSubFiles.length; ii++) {
	  	      if (listOfSubFiles[ii].isFile()) {
	  	    	  String fileName = listOfSubFiles[ii].getName().split(".java")[0];
	  	    	  if(fileName.contains("Base"))
	  	    	  {
	  	    		  System.out.println("\t-" + fileName);
	  	    		  new File(flexFolder + File.separator + childFolderName).mkdir();
	  	    		  ConvertConstants.setHashOfJavaClass(fileName, getJavaParentFolder()+"."+childFolderName);
	  	    		  ConvertConstants.setHashOfASClass(fileName, getAsParentFolder()+"."+childFolderName);
	  	    	  }
	  	      }
	        }
	      }else if (listOfFiles[i].isFile()) {
  	    	  String fileName = listOfFiles[i].getName().split(".java")[0];
    		  System.out.println("\t-" + fileName);
    		  new File(flexFolder).mkdir();
    		  ConvertConstants.setHashOfJavaClass(fileName, getJavaParentFolder());
    		  ConvertConstants.setHashOfASClass(fileName, getAsParentFolder());
  	      }
	    }
	    
	    System.out.println(ConvertConstants.getHashOfJavaClass().toString());
	    System.out.println(ConvertConstants.getHashOfASClass().toString());
	}

	private void construct_content()
	{
		StringBuffer comment =new StringBuffer();
		comment.append("\t/**\n");
		comment.append("\t* Class is equivalent to Remote class ");
		comment.append(javaPackageName);
		comment.append(".");
		comment.append(javaClassName);
		comment.append("\n");
		comment.append("\t* */\n");
		
		content_buffer.append("package ");
		content_buffer.append(asPackageName);
		content_buffer.append("\n");
		content_buffer.append("{\n");
		content_buffer.append(comment.toString());
		content_buffer.append(import_buffer.toString());
		content_buffer.append("\n");
		content_buffer.append("\t[Bindable]\n");
		content_buffer.append("\t[RemoteClass(alias=\"");
		content_buffer.append(javaPackageName);
		content_buffer.append(".");
		content_buffer.append(javaClassName);
		content_buffer.append("\")]\n");
		content_buffer.append("\tpublic class ");
		content_buffer.append(asClassName);
		content_buffer.append("\n");
		content_buffer.append("\t{\n");
		content_buffer.append(variable_buffer.toString());
		content_buffer.append("\n");
		content_buffer.append("\t\tpublic function ");
		content_buffer.append(asClassName);
		content_buffer.append("()\n");
		content_buffer.append("\t\t{\n");
		content_buffer.append("\t\t}\n");
		content_buffer.append("\t}\n");
		content_buffer.append("}\n");
	}
	
	private void write_to_file()
	{
		try {
			String filePath = "//home//prakash//projects//inventory//in_client/src//"+asPackageName.replace(".", "//")+"//";
			FileWriter fstream = new FileWriter(filePath+asClassName+".as");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(content_buffer.toString());
			//Close the output stream
			out.close();
			System.out.println("Created " + asClassName+".as");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setJavaParentFolder(String parentFolder) {
		this.javaParentFolder = parentFolder;
	}

	public String getJavaParentFolder() {
		return javaParentFolder;
	}
	
	public String getAsParentFolder() {
		return asParentFolder;
	}

	public void setAsParentFolder(String asParentFolder) {
		this.asParentFolder = asParentFolder;
	}
}
