package co.pointred.fx.comp.container
{
	import co.pointred.fx.comp.collection.HashMap;

	public interface IModule
	{
		function loadDefaultContent():void;
		function getContentHash():HashMap;
		function getDefaultContentId():String;
	}
}