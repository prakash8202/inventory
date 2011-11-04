package co.pointred.fx.comp.core
{
	import co.pointred.fx.comp.utils.AssetsLibrary;
	
	import flash.display.Sprite;
	
	import mx.controls.Alert;
	import mx.core.FlexGlobals;
	
	/**
	 *	The PrAlert control is a pop-up dialog box that can show the<b> error message, warning message, information message etc</b>.
	 * 
	 *	@author Prakash R
	 *	@date Aug 25, 2011
	 *	
	 *	@class PrAlert
	 **/
	public class PrAlert
	{
		
		public static const SUCCESS_MESSAGE:int = 0;
		public static const FAILURE_MESSAGE:int = 1;
		public static const ERROR_MESSAGE:int = 2;
		public static const INFO_MESSAGE:int = 3;
		public static const WARNING_MESSAGE:int = 4;
		
		public static const CONFIRM_YES:int = Alert.YES;
		public static const CONFIRM_NO:int = Alert.NO;
		
		public static const OK:String = "Ok";
		public static const CANCEL:String = "Cancel";
		
		public function PrAlert()
		{
		}
		
		/**
		 *  Static method that pops up the Message alert control. The Alert control 
		 *  closes when you select a button in the control, or press the Escape key.
		 * */
		public static function show(message:String, titleType:int,customTitle:String=null, parent:Sprite = null):Alert
		{
			var msgStr:String = "";
			
			if(message != null)
			{
				if(message.indexOf("|") > -1)
				{
					var messageArray:Array = message.split("|");
					
					for(var i:int=0;i<messageArray.length;i++)
					{
						msgStr = msgStr + " * " + messageArray[i] + "\n";
					}
				}else
				{
					msgStr = message
				}
			}
			
			var titleStr:String = "Message";
			var iconClass:Class = AssetsLibrary.messageIcon;
			var titleIconClass:Class = AssetsLibrary.message_24Icon;
			
			switch(titleType)
			{
				case 0:
					titleStr = "Sucess";
					iconClass = AssetsLibrary.successIcon;
					titleIconClass = AssetsLibrary.success_24Icon;
					break;
				case 1:
					titleStr = "Failure";
					iconClass = AssetsLibrary.errorIcon;
					titleIconClass = AssetsLibrary.error_24Icon;
					break;
				case 2:
					titleStr = "Error";
					iconClass = AssetsLibrary.errorIcon;
					titleIconClass = AssetsLibrary.error_24Icon;
					break;
				case 3:
					titleStr = "Info";
					iconClass = AssetsLibrary.infoIcon;
					titleIconClass = AssetsLibrary.info_24Icon;
					break;
				case 4:
					titleStr = "Warning";
					iconClass = AssetsLibrary.warning32Icon;
					titleIconClass = AssetsLibrary.warning_24Icon;
					break;
			}
			
			if(customTitle != null && customTitle.length > 0)
			{
				titleStr = customTitle;
			}
			
			if(parent == null)
				parent = FlexGlobals.topLevelApplication.currentModule;
			
			var alert:Alert = Alert.show(msgStr, titleStr, Alert.OK, parent, null, iconClass);
//			alert.titleIcon = titleIconClass;
			
			return alert;
		}
		
		public static function confirm(message:String, title:String,parent:Sprite = null, closeHandler:Function = null):Alert
		{
			if(message == null || title == null)
				throw new Error("Message and Title should not be NULL for question alert.");
			
			if(parent == null)
				parent = FlexGlobals.topLevelApplication.currentModule;
			
			var alert:Alert = Alert.show(message, title, Alert.YES | Alert.NO, parent, closeHandler, AssetsLibrary.questionIcon);
//			alert.titleIcon = AssetsLibrary.question_24Icon;
			
			return alert;
		}
	}//End of Class
}//End of Package