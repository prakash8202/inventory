package co.pointred.core.startup;


public interface PrStartupMgrIface
{
	public String getCurrentCtx();
	public String getCtxDescr();
	public void setCurrentCtx(String ctx);
	public void startServer();
	public void stopServer();
	public boolean suspendPinger();
	public boolean resumePinger();
	public RecoveryMsgCarrier sendRecoveryMsgToRemote(RecoveryMsgCarrier recoveryMsgCarrier);
	public String getServerType();
	public void startupDefault();
	public void startupIdle();

}
