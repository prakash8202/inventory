package co.pointred.core.startup;

import java.io.Serializable;

public class RecoveryMsgCarrier implements Serializable
{
	private static final long serialVersionUID = 4153002458632533673L;
	private RecoveryMsg recoverymsg;
	private long value;

	public RecoveryMsgCarrier(RecoveryMsg recoverymsg)
	{
		this.recoverymsg = recoverymsg;
	}
	
	public RecoveryMsg getRecoverymsg()
	{
		return recoverymsg;
	}

	public void setRecoverymsg(RecoveryMsg recoverymsg)
	{
		this.recoverymsg = recoverymsg;
	}

	public long getValue()
	{
		return value;
	}

	public void setValue(long value)
	{
		this.value = value;
	}

}
