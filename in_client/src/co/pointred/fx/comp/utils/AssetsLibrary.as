package co.pointred.fx.comp.utils
{
	import co.pointred.fx.comp.collection.HashMap;

	public class AssetsLibrary
	{
		private static var INSTANCE:AssetsLibrary = null;
		
		public function AssetsLibrary(privateClass:PrivateClass)
		{
			initMsgPoolHash();
		}
		
		public static function getInstance():AssetsLibrary
		{
			if(INSTANCE == null)
				INSTANCE = new AssetsLibrary(new PrivateClass());
			return INSTANCE;
		}
		
		//For Alert
		[Embed("assets/alert/error.png")]
		public static const errorIcon:Class;
		[Embed("assets/alert/warning.png")]
		public static const warning32Icon:Class;
		[Embed("assets/alert/info.png")]
		public static const infoIcon:Class;
		[Embed("assets/alert/question.png")]
		public static const questionIcon:Class;
		[Embed("assets/alert/message.png")]
		public static const messageIcon:Class;
		[Embed("assets/alert/success.png")]
		public static const successIcon:Class;
		
		[Embed("assets/alert/error_24.png")]
		public static const error_24Icon:Class;
		[Embed("assets/alert/warning_24.png")]
		public static const warning_24Icon:Class;
		[Embed("assets/alert/info_24.png")]
		public static const info_24Icon:Class;
		[Embed("assets/alert/question_24.png")]
		public static const question_24Icon:Class;
		[Embed("assets/alert/message_24.png")]
		public static const message_24Icon:Class;
		[Embed("assets/alert/success_24.png")]
		public static const success_24Icon:Class;
		
		/**
		 * 
		 * DATAGRID ICONS
		 * 
		 **/
		
		[Embed("assets/navig/first.png")]
		public static const firstIcon:Class;
		
		[Embed("assets/navig/prev.png")]
		public static const prevIcon:Class;
		
		[Embed("assets/navig/next.png")]
		public static const nextIcon:Class;
		
		[Embed("assets/navig/last.png")]
		public static const lastIcon:Class;
		
		[Embed("assets/datacontrol/add.png")]
		public static const addIcon:Class;
		
		[Embed("assets/datacontrol/edit.png")]
		public static const editIcon:Class;
		
		[Embed("assets/datacontrol/delete.png")]
		public static const deleteIcon:Class;
		
		[Embed("assets/datacontrol/refresh.png")]
		public static const refreshIcon:Class;
		
		[Embed("assets/datacontrol/filter.png")]
		public static const filterIcon:Class;
		
		[Embed("assets/datacontrol/print.png")]
		public static const printIcon:Class;
		
		[Embed("assets/datacontrol/removeFilter.png")]
		public static const removeFilterIcon:Class;
		
		/**
		 * Dual List Icons
		 **/
		[Embed("assets/navig/reset.png")]
		public static const resetIcon:Class;
		/*
		* Message Pool Icons
		*/
		[Embed("assets/alert/warning_green.png")]
		public static const warning_green:Class;
		
		[Embed("assets/alert/warning_yellow.png")]
		public static const warning_yellow:Class;
		
		[Embed("assets/alert/warning_red.png")]
		public static const warning_red:Class;
		
		/*
		* PopupBase
		*/
		[Embed("assets/x.png")]
		public static const close_button:Class;
		
		public static var messagePoolIconsHash:HashMap = new HashMap();
		
		public static function initMsgPoolHash():void
		{
			messagePoolIconsHash.put("DEFAULT",warning_yellow);
			messagePoolIconsHash.put("INFO",warning_yellow);
			messagePoolIconsHash.put("WARN",warning_green);
			messagePoolIconsHash.put("ERROR",warning_red);
		}
		
		public static function getMsgPoolIcon(ctx:String):Class
		{
			var cls:Class = messagePoolIconsHash.getValue(ctx);
			if(null == cls)
			{
				cls = infoIcon;
			}
			return cls;
		}
	}
}
class PrivateClass
{
	public function PrivateClass()
	{}
}