package co.pointred.fx.comp.tab.components
{
	public interface IPrTab
	{
		function initUi(contentName:String = null):void;
		function updateUi(updateCtx:String, ...objects):void;
		function close():void;
	}
}