package co.pointred.fx.util
{
	import co.pointred.fx.comp.container.PrModule;
	import co.pointred.modules.ModuleContants;
	
	import mx.controls.Alert;
	import mx.core.FlexGlobals;
	import mx.modules.ModuleLoader;
	
	/**
	 *	Class responsible for loading/unloading of modules
	 *  
	 *	@author kandeepasundaram
	 *	@date Feb 16, 2011
	 *	
	 *	@class ModuleMgr
	 **/
	public class ModuleMgr
	{
		private static var INSTANCE:ModuleMgr = null;
		public function ModuleMgr(privateClass:PrivateClass)
		{
			
		}
		
		public static function getInstance():ModuleMgr
		{
			if(INSTANCE == null)
				INSTANCE = new ModuleMgr(new PrivateClass());
			return INSTANCE;
		}
		
		public function loadModule(moduleContainer:ModuleLoader, module_context:String):void
		{
			var module:PrModule = null;
			if(FlexGlobals.topLevelApplication.currentModule == null || module_context != FlexGlobals.topLevelApplication.currentModule.prModuleName)
			{
				if(module_context == ModuleContants.LOGIN_MODULE){
					unloadModules(moduleContainer, true);	
				}
				else{
					unloadModules(moduleContainer, false);
				}
				var test:String = ModuleContants.hashOfModules.getValue(module_context);
				moduleContainer.loadModule(test);
			}
		}
		
		public function unloadModules(moduleContainer:ModuleLoader, goToLogin:Boolean):void{
			var currentModule:PrModule = FlexGlobals.topLevelApplication.currentModule;
			if(null != currentModule && goToLogin == false && currentModule.prModuleName != ModuleContants.DASHBOARD_MODULE){
				// Unsubscribe all server-push subscribed before unloading a module
				// currentModule.unsubscribeAll();	
			}
			moduleContainer.unloadModule();
		}
	}
}
class PrivateClass
{
	public function PrivateClass()
	{}
}