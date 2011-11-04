package co.pointred.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

import co.pointred.core.log.LoggerService;


/**
 * Class to Perform XML Marshalling & Unmarshalling using Castor XML
 * 
 * @author kandeepa
 * 
 */
public class CastorXmlUtil
{
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    /**
     * API To Convert Object To XML Using Castor XML
     * 
     * @param object
     * @return
     */
    public String convertObjectToXml(Object object)
    {
	ByteArrayOutputStream os = new ByteArrayOutputStream();
	try
	{
	    this.marshaller.marshal(object, new StreamResult(os));
	} catch (Exception e)
	{
	    LoggerService.instance.error("Error Converting Object To XML", CastorXmlUtil.class, e);
	}
	return null;
    }

    /**
     * API to Convert XML To Object using Castor XML
     * 
     * @param xmlFile
     * @return
     */
    public Object convertXmlToObject(Resource xmlFile)
    {
	Object convertedObj = null;
	try
	{
	    InputStream is = xmlFile.getInputStream();
	    convertedObj = this.unmarshaller.unmarshal(new StreamSource(is));
	} catch (Exception e)
	{
	    LoggerService.instance.error("Error Converting XML To Object", CastorXmlUtil.class, e);
	    e.printStackTrace();
	}
	return convertedObj;
    }

    public void setMarshaller(Marshaller marshaller)
    {
	this.marshaller = marshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller)
    {
	this.unmarshaller = unmarshaller;
    }
}
