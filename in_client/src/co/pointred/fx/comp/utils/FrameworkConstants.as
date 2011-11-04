package co.pointred.fx.comp.utils
{
	import co.pointred.fx.comp.collection.HashMap;
	import co.pointred.fx.comp.core.PrAlert;
	
	import flash.display.DisplayObject;

	public class FrameworkConstants
	{
		public function FrameworkConstants()
		{
		}
		
		//Alert Types
		public static const INFO:String="INFO";
		public static const ERROR:String="ERROR";
		public static const WARN:String="WARN";
		
		// Navigation 
		public static const FIRST:String="FIRST";
		public static const LAST:String="LAST";
		public static const PREVIOUS:String="PREVIOUS";
		public static const NEXT:String="NEXT";
		public static const REFRESH:String="REFRESH";
		
		// Server Type
		public static var SERVER_TYPE:String;
		public static var PRIMARY_SERVER:String = "PRIMARY";
		public static var SECONDARY_SERVER:String = "SECONDARY";
		//AutoComplete
		private static const COLOR_GRAY:String 	= "#999999";
		public static const COLOR_TEXT_DISABLED:String 	= COLOR_GRAY;
		public static const BUTTON_WIDTH:uint 			= 80;		
		
		public static var SUCCESS:String = "SUCCESS";
		public static var FAILURE:String = "FAILURE";
		public static var WARNING_:String = "WARNING";
		
		//For partial view in PrPopupManager
		public static var partialViewParent:DisplayObject = null;
		
		public static var hashOfAlertTypes:HashMap = new HashMap();
		
		private function initAlertTypes():void
		{
			hashOfAlertTypes.put(SUCCESS,PrAlert.SUCCESS_MESSAGE);
			hashOfAlertTypes.put(FAILURE,PrAlert.FAILURE_MESSAGE);
			hashOfAlertTypes.put(WARNING_,PrAlert.WARNING_MESSAGE);
		}
	}
}