package co.inventory.modules.expense.expensetype
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

	public class ExpenseTypeMgmt
	{
		private var iCntnr:ICntnr;
		private var selectedObj:Object;
		public var parentGrid:PrDataGrid;
		
		private var parentObj:ExpenseTypeManagement;
		private var isModify:Boolean = false;
		private var isDelete:Boolean = false;
		private var title:String;
		private var pop:PrPopupBase;
		
		public function ExpenseTypeMgmt(parentObj:ExpenseTypeManagement)
		{
			this.parentObj = parentObj;	
		}
		
		public function addExpenseType():void
		{
			this.isModify = false;
			this.isDelete = false;
			this.title = "Add Expense Type";
			loadUI();
		}
		
		public function editExpenseType(selectedObj:Object):void
		{
			this.selectedObj=selectedObj;
			this.isModify = true;
			this.isDelete = false;
			this.title = "Modify Expense Type";
			loadUI();
		}
		
		public function deleteExpenseType(selectedObj:Object):void
		{
			this.selectedObj=selectedObj;
			this.isDelete = true;
			this.isModify = false;
			var asObject:Object = new Object;
			asObject['expense_type_pid'] = ""+selectedObj['expense_type_pid'];
			var userObject:UserObject = new UserObject;
			var expenseMgmtGateway:RemoteObject = RemoteGateway.getInstance().getExpenseMgmtGateway();
			expenseMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
			userObject.dataObject = asObject;
			expenseMgmtGateway.deleteExpenseType(userObject);
		}
		
		private function loadUI():void
		{
			PrEventDispatcher.registerForEvent("expensetype.create",userUiCreated);
			var xmlCg:XmlComponentGenerator = new XmlComponentGenerator();
			xmlCg.buildComponents("xml/expense/expensetype/addexpensetype.xml");
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
				var expenseMgmtGateway:RemoteObject = RemoteGateway.getInstance().getExpenseMgmtGateway();
				if(isModify)
				{
					asObject["IBATIS_STATEMENT"]="EXPENSE_TYPE.UPDATE";
					expenseMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
					userObject.dataObject = asObject;
					expenseMgmtGateway.updateExpenseType(userObject);
				}else{
					asObject["IBATIS_STATEMENT"]="EXPENSE_TYPE.CREATE_OBJECT";
					expenseMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
					userObject.dataObject = asObject;
					expenseMgmtGateway.createExpenseType(userObject);
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