package co.pointred.core.startup;

import co.pointred.core.utils.Console;


public class StartServer
{
	public static void main(String[] args)
	{
		StartCemsServer();
	}

	public static void StartCemsServer()
	{
		PrStartupManager.instance.startServer();
		Console.print("======= 	CEMS SERVER STARTED ==========");
	}

}
