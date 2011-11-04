package co.pointred.fx.comp.core
{
	import mx.rpc.events.ResultEvent;
	
	/**
	 * Interface to get the Callback after sending data to the server
	 * */
	public interface CrudCallBack
	{
		function callBackCreate(evt:ResultEvent):void;
		function callBackUpdate(evt:ResultEvent):void;
		function callBackDelete(evt:ResultEvent):void;
	}
}