package co.pointred.fx.comp.singleton
{
	import co.pointred.fx.comp.collection.HashMap;
	import co.pointred.fx.comp.notification.Notification;
	import co.pointred.fx.comp.notification.NotificationLsnr;
	
	import mx.collections.ArrayCollection;
	import mx.collections.ArrayList;

	public class NotificationService
	{
		
		private var notificationHash:HashMap = new HashMap();
		private var notifArray:ArrayList = new ArrayList();
		private static var instance:NotificationService;
		
		public static function getInstance():NotificationService
		{
			if(instance==null)
			{
				instance=new NotificationService();
			}
			
			return instance;
		}
		
		
		function NotificationService()
		{
			
		}
		
		public  function registerForNotification(context:String,notifLsnr:NotificationLsnr):void
		{
			if(notificationHash.getValue(context)==null)
			{
				notificationHash.put(context,new ArrayList());	
			}
			
			var list:ArrayList= notificationHash.getValue(context) as ArrayList;
			list.addItem(notifLsnr);
		}
		
		public function unregisterForNotification(context:String,notifLsnr:NotificationLsnr):void
		{
			var list:ArrayList= notificationHash.getValue(context) as ArrayList;
			list.removeItem(notifLsnr);
			
			if(list.length==0)
			{
				notificationHash.remove(context);
			}
		}
		
		public function notify(context:String,notifObject:Notification):void
		{
			var nlist:ArrayList= notificationHash.getValue(context) as ArrayList;
			
			for ( var ii:Number=0;ii<nlist.length;ii++)
			{
				var nLsnr:NotificationLsnr = nlist.getItemAt(ii) as NotificationLsnr;
				nLsnr.updateNotification(notifObject);
			}
		}
	}
}