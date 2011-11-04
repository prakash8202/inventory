package co.pointred.core.startup;

public interface PrStartupIface 
{
	public String getState();
	public void reset();
	public String getContext();
	public boolean startService();
	public boolean stopService();
}
