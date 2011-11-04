package co.pointred.core.startup;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import co.pointred.core.utils.Console;

/**
 * Servlet implementation class CemsStartupServlet
 */
public class PrStartupServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public PrStartupServlet()
    {
	super();
	// TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
	// TODO Auto-generated method stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
	// TODO Auto-generated method stub
    }

    @Override
    public void init() throws ServletException
    {
	super.init();
	PrStartupManager.instance.startServer();
    }

    @Override
    public void destroy()
    {
	Console.print("Startup Servlet getting destroyed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	try
	{
	    PrStartupManager.instance.shutdownServer();
	} catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	super.destroy();
    }

}
