package co.pointred.fx.comp.notification
{
	public interface NotificationLsnr
	{
		function updateNotification(notification:Notification):void;
		function registerForNotification():void;
		function unregisterForNotification():void
	}
}