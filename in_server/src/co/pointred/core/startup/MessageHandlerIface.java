package co.pointred.core.startup;

import org.jboss.remoting.InvocationRequest;

public interface MessageHandlerIface
{
	public Object invoke(InvocationRequest invocation) throws Throwable;
}
