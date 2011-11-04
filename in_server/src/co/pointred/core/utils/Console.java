package co.pointred.core.utils;

/**
 * Prints the output on the console
 * @author shajee
 *
 */
public class Console
{

    /**
     * prints the msg on the console.  This will be uncommented when we go on to production.  Avoid calling sys out and use this api
     * @param msg
     */
    public static void print(Object msg)
    {
	System.out.println(msg);
    }
}
