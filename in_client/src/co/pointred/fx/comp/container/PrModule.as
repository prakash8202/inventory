package co.pointred.fx.comp.container
{
	import mx.core.FlexGlobals;
	import mx.modules.Module;
	
	public class PrModule extends Module
	{
		public var prModuleName:String;
		public var currentContentContext:String;
		
		public function PrModule()
		{
			super();
			initiateModule();
		}
		
		public function initiateModule():void
		{
			if(null != FlexGlobals.topLevelApplication.currentModule)
			{
				var currentModuleName:String = FlexGlobals.topLevelApplication.currentModule.prModuleName;
				if(this.prModuleName != currentModuleName)
				{
					FlexGlobals.topLevelApplication.currentModule = this;
				}
			}
			else
			{
				FlexGlobals.topLevelApplication.currentModule = this;
			}
		}
		
		public function loadContent(contentContext:String):void
		{
			
		}
		
		protected function loadContextUi(contentContext:String):void
		{
			
		}
	}
}