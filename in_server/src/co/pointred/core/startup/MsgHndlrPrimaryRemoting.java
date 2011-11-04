package co.pointred.core.startup;

import org.jboss.remoting.InvocationRequest;


public class MsgHndlrPrimaryRemoting implements MessageHandlerIface
{
	@Override
	public Object invoke(InvocationRequest invocation) throws Throwable
	{
		RemotingMsg remotingMsg = (RemotingMsg) invocation.getParameter();
		RemotingMsg respMsg = RemotingMsg.ERROR;
		switch (remotingMsg)
		{
		case PING:
			respMsg = RemotingMsg.PONG;
			break;
		default:
			break;
		}
		return respMsg;
	}
}
