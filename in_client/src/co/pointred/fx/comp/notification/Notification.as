package co.pointred.fx.comp.notification
{
	
	public class Notification
	{
		private var context:String;
		private var notificationObject:Object;
		public function Notification(context:String, notificationObject:Object)
		{
			this.context=context;
			this.notificationObject=notificationObject;
		}
		
		public function getContext():String
		{
			return this.context;
		}
		
		public function getNotificationObject():Object
		{
			return this.notificationObject;
		}
	}
}