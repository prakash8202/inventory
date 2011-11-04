package co.pointred.fx.comp.singleton
{
	import flash.events.TimerEvent;
	import flash.utils.Timer;

	public class TimeOutService
	{
		private static var INSTANCE:TimeOutService = null;
		public var MAX_IDLE_TIME:Number = 60000;
		public var LAST_ACCESS_TIME:Date;
		
		private var myTimer:Timer;
		
		public function TimeOutService(privateClass:PrivateClass)
		{
		}
		
		public static function getInstance():TimeOutService
		{
			if(INSTANCE == null)
				INSTANCE = new TimeOutService(new PrivateClass());
			return INSTANCE;
		}
		
		public function startTimeOut():void
		{
			LAST_ACCESS_TIME = new Date();
			
			myTimer = new Timer(1000, 0);
			myTimer.addEventListener("timer", checkTimeout);
			myTimer.start();
		}
		
		public function resetTimeOut():void
		{
			LAST_ACCESS_TIME = new Date();
		}
		
		public function stopTimeOut():void
		{
			myTimer.stop();
		}
		
		private function checkTimeout(event:TimerEvent):void 
		{
			var diff:Number = (new Date().valueOf() - LAST_ACCESS_TIME.valueOf());
			if(diff/60000 >= MAX_IDLE_TIME)
			{
				stopTimeOut();
//				LogOutManager.logOut("Logged you out due to inactivity....");
			}
		}
	}
}
class PrivateClass
{
	public function PrivateClass()
	{
	}	
}