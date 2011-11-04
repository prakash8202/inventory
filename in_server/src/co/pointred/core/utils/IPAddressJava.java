package co.pointred.core.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IPAddressJava
{
    private static List<String> mainIpList = new ArrayList<String>();
    private static List<String> subIpList = new ArrayList<String>();

    public void getInterfaces()
    {
	try
	{
	    Enumeration allNetworkInterfaces = NetworkInterface.getNetworkInterfaces();

	    while (allNetworkInterfaces.hasMoreElements())
	    {
		mainIpList = new ArrayList<String>();
		subIpList = new ArrayList<String>();
		NetworkInterface networkInterface = (NetworkInterface) allNetworkInterfaces.nextElement();
		mainIpList = getIpAddr(networkInterface.getInetAddresses());
		Enumeration<NetworkInterface> enumeration = networkInterface.getSubInterfaces();

		if (enumeration.hasMoreElements() == true)
		{
		    while (enumeration.hasMoreElements())
		    {
			Enumeration sunInterfaceAddresses = enumeration.nextElement().getInetAddresses();
			subIpList = getIpAddr(sunInterfaceAddresses);
		    }
		}
		for (String string : subIpList)
		{
		    if (mainIpList.contains(string))
		    {
			mainIpList.remove(string);
		    }
		}
		for (String string : mainIpList)
		{
		    Console.print(string);
		}
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    private List<String> getIpAddr(Enumeration enumeration)
    {
	List<String> ipList = new ArrayList<String>();
	while (enumeration.hasMoreElements())
	{
	    InetAddress ip = (InetAddress) enumeration.nextElement();
	    if (false == (ip.toString().contains("%") || ip.toString().contains(":")))
	    {
		String parsedIp = ip.toString();

		if (false == ip.isLoopbackAddress())
		{
		    parsedIp = removeCharAt(parsedIp, 0);
		    ipList.add(parsedIp);
		}
	    }
	}
	return ipList;
    }

    String removeCharAt(String s, int pos)
    {
	return s.substring(0, pos) + s.substring(pos + 1);
    }

    public static void main(String[] args)
    {
	IPAddressJava ip = new IPAddressJava();
	ip.getInterfaces();
    }
}