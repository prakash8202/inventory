package co.pointred.fx.rpc
{
	import co.pointred.fx.comp.core.PrAlert;
	import co.pointred.fx.comp.preloader.PrPreloader;
	import co.pointred.fx.comp.singleton.TimeOutService;
	
	import mx.controls.Alert;
	import mx.managers.CursorManager;
	import mx.messaging.Channel;
	import mx.messaging.ChannelSet;
	import mx.messaging.channels.AMFChannel;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.remoting.RemoteObject;

	public class RemoteGateway
	{
		private var remotingChannelSet:ChannelSet;
		private var messagingChannelSet:ChannelSet;
		private var messagingChannel:Channel;
		private var remotingChannel:Channel;
		private var currentServerIp:String;
		
		private static var INSTANCE:RemoteGateway = null;
		
		public static function getInstance():RemoteGateway
		{
			if(INSTANCE == null)
				INSTANCE = new RemoteGateway(new PrivateClass());
			return INSTANCE;
		}
		
		public function RemoteGateway(privateClass:PrivateClass)
		{
			remotingChannelSet = new ChannelSet();
			messagingChannelSet = new ChannelSet();
		}
		
		/**
		 * API to initiate channel settings in an AIR project
		 * Not Necessary for Flex Projects
		 **/
		public function initiateChannelSettings(url:String):void{
			remotingChannel = new AMFChannel("my-amf","http://" + url + ":8080/inventory/messagebroker/amf");
			messagingChannel = new AMFChannel("my-polling-amf","http://" + url + ":8080/inventory/messagebroker/amfpolling");
			messagingChannelSet.addChannel(messagingChannel);
			remotingChannelSet.addChannel(remotingChannel);
			currentUrl(url);	

		}
		
		private function currentUrl(urlValue:String):void
		{
			currentServerIp = urlValue; 
		}
		
		public function getCurrentServerIp():String
		{
			return this.currentServerIp;
		}
		
		private function onFail(event:FaultEvent):void
		{
			CursorManager.removeAllCursors();
			PrPreloader.getInstance().hidePreloader();
			PrAlert.show("Some ERROR (line no.61, RemoteGateway.as..(why not to have a specific msg ?) - "+event.message,PrAlert.ERROR_MESSAGE);
		}
		public function testGateway():RemoteObject
		{
			TimeOutService.getInstance().resetTimeOut();
			var testGateway:RemoteObject = new RemoteObject();
			testGateway.destination = "TestGateway";
			testGateway.channelSet = this.remotingChannelSet;
			testGateway.addEventListener(FaultEvent.FAULT, onFail);
			return testGateway;
		}
		public function getCrudGateway():RemoteObject
		{
			TimeOutService.getInstance().resetTimeOut();	
			var crudGateway:RemoteObject = new RemoteObject();
			crudGateway.destination = "CrudGateway";
			crudGateway.channelSet = this.remotingChannelSet;
			crudGateway.addEventListener(FaultEvent.FAULT, onFail);
			return crudGateway;
		}
		
		public function getProductMgmtGateway():RemoteObject
		{
			TimeOutService.getInstance().resetTimeOut();
			var productMgmtGateway:RemoteObject = new RemoteObject();
			productMgmtGateway.destination = "ProductManagementGateway";
			productMgmtGateway.channelSet = this.remotingChannelSet;
			productMgmtGateway.addEventListener(FaultEvent.FAULT, onFail);
			return productMgmtGateway;
		}
		
		public function getExpenseMgmtGateway():RemoteObject
		{
			TimeOutService.getInstance().resetTimeOut();
			var expenseMgmtGateway:RemoteObject = new RemoteObject();
			expenseMgmtGateway.destination = "ExpenseManagementGateway";
			expenseMgmtGateway.channelSet = this.remotingChannelSet;
			expenseMgmtGateway.addEventListener(FaultEvent.FAULT, onFail);
			return expenseMgmtGateway;
		}
		
		public function getPurchaseMgmtGateway():RemoteObject
		{
			TimeOutService.getInstance().resetTimeOut();
			var purchaseMgmtGateway:RemoteObject = new RemoteObject();
			purchaseMgmtGateway.destination = "PurchaseManagementGateway";
			purchaseMgmtGateway.channelSet = this.remotingChannelSet;
			purchaseMgmtGateway.addEventListener(FaultEvent.FAULT, onFail);
			return purchaseMgmtGateway;
		}
		
		public function getSalesMgmtGateway():RemoteObject
		{
			TimeOutService.getInstance().resetTimeOut();
			var salesMgmtGateway:RemoteObject = new RemoteObject();
			salesMgmtGateway.destination = "SalesManagementGateway";
			salesMgmtGateway.channelSet = this.remotingChannelSet;
			salesMgmtGateway.addEventListener(FaultEvent.FAULT, onFail);
			return salesMgmtGateway;
		}
		
		public function getReportMgmtGateway():RemoteObject
		{
			TimeOutService.getInstance().resetTimeOut();
			var reportMgmtGateway:RemoteObject = new RemoteObject();
			reportMgmtGateway.destination = "ReportManagementGateway";
			reportMgmtGateway.channelSet = this.remotingChannelSet;
			reportMgmtGateway.addEventListener(FaultEvent.FAULT, onFail);
			return reportMgmtGateway;
		}
	}
}

class PrivateClass
{
	public function PrivateClass()
	{}
}