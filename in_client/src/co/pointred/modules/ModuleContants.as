package co.pointred.modules
{
	import co.pointred.fx.comp.collection.HashMap;

	public class ModuleContants
	{
		private static var INSTANCE:ModuleContants = null;
		
		// List Of Modules
		public static const LOGIN_MODULE:String = "Login";
		public static const DASHBOARD_MODULE:String = "Dashboard";
		
		public static var hashOfModules:HashMap = new HashMap();
		
		/**
		 * Private Constructor
		 * @param privateClass
		 */
		public function ModuleContants(privateClass:PrivateClass)
		{
			addModules();
		}
		
		/**
		 * 
		 * @return Singleton Instance Of ProjectContants
		 */
		public static function getInstance():ModuleContants
		{
			if(INSTANCE == null)
				INSTANCE = new ModuleContants(new PrivateClass());
			return INSTANCE;
		}
		
		
		private function addModules():void
		{
			// APP ROOT MODULES
			hashOfModules.put(LOGIN_MODULE, "co/pointred/modules/LoginModule.swf");
			hashOfModules.put(DASHBOARD_MODULE, "co/pointred/modules/DashboardModule.swf");
		}
	}
}

class PrivateClass
{
	/**
	 * Singleton Implementation
	 */
	public function PrivateClass()
	{
	}	
}