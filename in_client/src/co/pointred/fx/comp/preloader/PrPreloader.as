package co.pointred.fx.comp.preloader
{
	import flash.display.DisplayObject;
	
	import mx.controls.Image;
	import mx.controls.Label;
	import mx.controls.Text;
	import mx.core.FlexGlobals;
	import mx.managers.PopUpManager;
	
	import spark.components.BorderContainer;
	import spark.components.HGroup;
	import spark.components.SkinnableContainer;
	import spark.components.VGroup;
	

	public class PrPreloader
	{
		private var pop:VGroup;
		
		private static var instance:PrPreloader;
		private var preLoader:PreLoader = new PreLoader();
		
		
		public static function getInstance():PrPreloader
		{
			if(instance==null)
			{
				instance=new PrPreloader();
			}
			
			return instance;
		}
		
		public function showPreloader(text:String):void
		{
			this.preLoader.setMsg(text);
				
			PopUpManager.addPopUp(this.preLoader,FlexGlobals.topLevelApplication as DisplayObject, true);
			PopUpManager.centerPopUp(preLoader);
		}
		
		public function hidePreloader():void
		{
//			pop.closePrPopup();
			PopUpManager.removePopUp(preLoader);
		}
	}
}
