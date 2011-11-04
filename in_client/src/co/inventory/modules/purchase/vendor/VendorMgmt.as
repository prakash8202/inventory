package co.inventory.modules.purchase.vendor
{
	import co.inventory.utils.SharedConstants;
	import co.pointred.fx.comp.container.ICntnr;
	import co.pointred.fx.comp.core.PrAlert;
	import co.pointred.fx.comp.core.PrButton;
	import co.pointred.fx.comp.core.dataGrid.PrDataGrid;
	import co.pointred.fx.comp.event.PrEventDispatcher;
	import co.pointred.fx.comp.event.PrICntnrEvent;
	import co.pointred.fx.comp.parser.XmlComponentGenerator;
	import co.pointred.fx.comp.resizabletw.PrPopupBase;
	import co.pointred.fx.comp.resizabletw.PrPopupManager;
	import co.pointred.fx.dataobjects.UserObject;
	import co.pointred.fx.rpc.RemoteGateway;
	
	import flash.events.MouseEvent;
	
	import mx.managers.PopUpManager;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;

	public class VendorMgmt
	{
		private var iCntnr:ICntnr;
		private var selectedObj:Object;
		public var parentGrid:PrDataGrid;
		
		private var parentObj:VendorManagement;
		private var isModify:Boolean = false;
		private var isDelete:Boolean = false;
		private var title:String;
		private var pop:PrPopupBase;
		
		public function VendorMgmt(parentObj:VendorManagement)
		{
			this.parentObj = parentObj;	
		}
		
		public function addVendor():void
		{
			this.isModify = false;
			this.isDelete = false;
			this.title = "Add Vendor";
			loadUI();
		}
		
		public function editVendor(selectedObj:Object):void
		{
			this.selectedObj=selectedObj;
			this.isModify = true;
			this.isDelete = false;
			this.title = "Modify Vendor";
			loadUI();
		}
		
		public function deleteVendor(selectedObj:Object):void
		{
			this.selectedObj=selectedObj;
			this.isModify = false;
			this.isDelete = true;
			
			var asObject:Object = new Object;
			asObject['vendor_pid'] = ""+selectedObj['vendor_pid'];
			var userObject:UserObject = new UserObject;
			var purchaseMgmtGateway:RemoteObject = RemoteGateway.getInstance().getPurchaseMgmtGateway();
			purchaseMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
			userObject.dataObject = asObject;
			purchaseMgmtGateway.deleteVendor(userObject);
		}
		
		private function loadUI():void
		{
			PrEventDispatcher.registerForEvent("vendor.create",userUiCreated);
			var xmlCg:XmlComponentGenerator = new XmlComponentGenerator();
			xmlCg.buildComponents("xml/purchase/vendor/addvendor.xml");
		}
		
		private function userUiCreated(evt:PrICntnrEvent):void
		{
			this.iCntnr = evt.iCntnr;
			var uid:String = iCntnr.getICntnrId();
			
			pop = PrPopupManager.showICntnrInPopup(iCntnr, this.title, true);
			
			if(isModify)
			{
				this.iCntnr.setAttrVal(this.selectedObj);
			}
			
			var saveBtn:PrButton = iCntnr.getIComp("formContainer","save_btn") as PrButton;
			saveBtn.addEventListener(MouseEvent.CLICK, function(evt:MouseEvent):void
			{
				saveBtnClick(iCntnr);
			}
			);
		}
		
		private function saveBtnClick(iCntnr:ICntnr):void
		{
			var validation:Boolean= iCntnr.validate();
			
			if(validation==true)
			{
				var asObject:Object = iCntnr.getAllAttrVal();
				var userObject:UserObject = new UserObject;
				var purchaseMgmtGateway:RemoteObject = RemoteGateway.getInstance().getPurchaseMgmtGateway();
				if(isModify)
				{
					asObject["IBATIS_STATEMENT"]="VENDOR.UPDATE";
					purchaseMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
					userObject.dataObject = asObject;
					purchaseMgmtGateway.updateVendor(userObject);
				}else{
					asObject["IBATIS_STATEMENT"]="VENDOR.CREATE_OBJECT";
					purchaseMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
					userObject.dataObject = asObject;
					purchaseMgmtGateway.createVendor(userObject);
				}
			}
		}
		
		public function onSuccess(evt:ResultEvent):void
		{
			var userObj:UserObject = evt.result as UserObject;
			PrAlert.show(userObj.statusMsg,SharedConstants.hashOfAlertTypes.getValue(userObj.status.toUpperCase()));
			
			if(!isDelete)
			{
				if(isModify)
				{
					PopUpManager.removePopUp(pop);
				}else
				{
					this.iCntnr.resetAll();
				}
			}
			
			parentGrid.refreshGrid();
		}
	}
}