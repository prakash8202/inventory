package co.pointred.fx.comp.test
{
	import co.pointred.fx.comp.container.ICntnr;
	import co.pointred.fx.comp.core.IComp;
	import co.pointred.fx.comp.core.PrAlert;
	import co.pointred.fx.comp.core.PrTextField;
	import co.pointred.fx.comp.notification.Notification;
	import co.pointred.fx.comp.notification.NotificationLsnr;
	import co.pointred.fx.comp.singleton.NotificationService;

	public class TestNotification implements NotificationLsnr
	{
		public var test:String;
		private var prt:PrTextField;

		public function TestNotification()
		{
			
		}
		
		public function registerForNotification():void
		{
			NotificationService.getInstance().registerForNotification("Employee Details",this);
		}
		
		public function update(notification:Notification):void
		{
			var iCntnr:ICntnr = notification.getNotificationObject() as ICntnr;
			var iComp:IComp= iCntnr.getIComp("empBtns","okbtn") as IComp;
			PrAlert.show("Attr Name of Icomp==" + iComp.getAttrName(),PrAlert.INFO_MESSAGE);
		}
	}
}