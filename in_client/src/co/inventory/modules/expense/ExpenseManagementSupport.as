import co.pointred.fx.comp.container.ICntnr;
import co.pointred.fx.comp.core.PrButton;
import co.pointred.fx.comp.core.PrDateField;
import co.pointred.fx.comp.core.PrLabelField;
import co.pointred.fx.comp.resizabletw.PrPopupBase;
import co.pointred.fx.dataobjects.UserObject;
import co.pointred.fx.rpc.RemoteGateway;

import flash.events.MouseEvent;

import mx.managers.PopUpManager;

private var isModify:Boolean = false;
private var isDelete:Boolean = false;
private var title:String;
private var selectedObj:Object;
private var pop:PrPopupBase;
private var formCntr:ICntnr;
private var typeLbl:PrLabelField;

public function addExpense():void
{
	this.isModify = false;
	this.isDelete = false;
	this.title = "Add Expense";
	loadUI();
}

public function deleteExpense(selectedObj:Object):void
{
	this.selectedObj=selectedObj;
	this.isDelete = true;
	this.isModify = false;
	var asObject:Object = new Object;
	asObject['expense_pid'] = ""+selectedObj['expense_pid'];
	var userObject:UserObject = new UserObject;
	var expenseMgmtGateway:RemoteObject = RemoteGateway.getInstance().getExpenseMgmtGateway();
	expenseMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
	userObject.dataObject = asObject;
	expenseMgmtGateway.deleteExpense(userObject);
}

public function editExpense(selectedObj:Object):void
{
	this.selectedObj=selectedObj;
	this.isModify = true;
	this.isDelete = false;
	this.title = "Modify Expense";
	loadUI();
}

private function loadUI():void
{
	PrEventDispatcher.registerForEvent("expense.create",userUiCreated);
	var xmlCg:XmlComponentGenerator = new XmlComponentGenerator();
	xmlCg.buildComponents("xml/expense/addexpense.xml");
}

private function userUiCreated(evt:PrICntnrEvent):void
{
	this.formCntr = evt.iCntnr;
	var uid:String = iCntnr.getICntnrId();
	
	typeLbl = this.formCntr.getIComp("formContainer", "expense_type_lbl") as PrLabelField;
	typeLbl.text = selectedExpenseType.expense_type;
	
	pop = PrPopupManager.showICntnrInPopup(formCntr, this.title, true);
	
	var expenseDateField:PrDateField = formCntr.getIComp("formContainer","expense_date") as PrDateField;
	var startDateRange:Object = new Object();
	startDateRange["rangeEnd"] = new Date; 
	expenseDateField.selectableRange =  startDateRange;
	
	if(isModify)
	{
		this.formCntr.setAttrVal(this.selectedObj);
	}
	
	var saveBtn:PrButton = formCntr.getIComp("formContainer","save_btn") as PrButton;
	saveBtn.addEventListener(MouseEvent.CLICK, function(evt:MouseEvent):void
	{
		saveBtnClick(formCntr);
	}
	);
}

private function saveBtnClick(iCntnr:ICntnr):void
{
	var validation:Boolean= iCntnr.validate();
	
	if(validation==true)
	{
		var asObject:Object = iCntnr.getAllAttrVal();
		asObject['expense_type'] = ""+selectedExpenseType.expense_type_pid;
		var userObject:UserObject = new UserObject;
		var expenseMgmtGateway:RemoteObject = RemoteGateway.getInstance().getExpenseMgmtGateway();
		if(isModify)
		{
			asObject["IBATIS_STATEMENT"]="EXPENSE.UPDATE";
			expenseMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
			userObject.dataObject = asObject;
			expenseMgmtGateway.updateExpense(userObject);
		}else{
			asObject["IBATIS_STATEMENT"]="EXPENSE.CREATE_OBJECT";
			expenseMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
			userObject.dataObject = asObject;
			expenseMgmtGateway.createExpense(userObject);
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
			this.formCntr.resetAll();
			typeLbl.text = selectedExpenseType.expense_type;
		}
	}
	
	dataGrid.refreshGrid();
}