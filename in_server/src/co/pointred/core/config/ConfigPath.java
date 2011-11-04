package co.pointred.core.config;

import java.util.HashMap;
import java.util.List;

import org.springframework.core.io.Resource;

/**
 * Bean Class to Provide Project File Resources @ Runtime
 * 
 * @author kandeepa
 * 
 */
public class ConfigPath
{
	private Resource defaultMenuXml;
	private HashMap<String, Resource> treeCtxMenuXmlHash;
	private List<Resource> privilegeFiles;
	private Resource masterDataFile;
	

	
	public Resource getMasterDataFile()
	{
		return masterDataFile;
	}

	public void setMasterDataFile(Resource masterDataFile)
	{
		this.masterDataFile = masterDataFile;
	}

	public List<Resource> getPrivilegeFiles()
	{
		return privilegeFiles;
	}

	public void setPrivilegeFiles(List<Resource> privilegeFiles)
	{
		this.privilegeFiles = privilegeFiles;
	}

	public Resource getDefaultMenuXml()
	{
		return defaultMenuXml;
	}

	public void setDefaultMenuXml(Resource defaultMenuXml)
	{
		this.defaultMenuXml = defaultMenuXml;
	}

	public void setTreeCtxMenuXmlHash(HashMap<String, Resource> treeCtxMenuXmlHash)
	{
		this.treeCtxMenuXmlHash = treeCtxMenuXmlHash;
	}

	public HashMap<String, Resource> getTreeCtxMenuXmlHash()
	{
		return treeCtxMenuXmlHash;
	}
}
