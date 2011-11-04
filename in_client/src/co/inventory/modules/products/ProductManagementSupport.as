import co.pointred.fx.comp.container.ICntnr;
import co.pointred.fx.comp.core.PrButton;
import co.pointred.fx.comp.core.PrDateField;
import co.pointred.fx.comp.core.PrLabelField;
import co.pointred.fx.comp.resizabletw.PrPopupBase;
import co.pointred.fx.comp.resizabletw.PrPopupManager;

import flash.events.MouseEvent;

import mx.controls.DateField;
import mx.managers.PopUpManager;

private var isModify:Boolean = false;
private var isDelete:Boolean = false;
private var title:String;
private var selectedObj:Object;
private var pop:PrPopupBase;
private var formCntr:ICntnr;

public function addProduct():void
{
	this.isModify = false;
	this.isDelete = false;
	this.title = "Add Product";
	loadUI();
}

public function editProduct(selectedObj:Object):void
{
	this.selectedObj=selectedObj;
	this.isModify = true;
	this.isDelete = false;
	this.title = "Modify Product";
	loadUI();
}

public function deleteProduct(selectedObj:Object):void
{
	this.selectedObj=selectedObj;
	this.isModify = false;
	this.isDelete = true;
	
	var asObject:Object = new Object;
	asObject['product_pid'] = ""+selectedObj['product_pid'];
	var userObject:UserObject = new UserObject;
	var productMgmtGateway:RemoteObject = RemoteGateway.getInstance().getProductMgmtGateway();
	productMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
	userObject.dataObject = asObject;
	productMgmtGateway.deleteProduct(userObject);
}

private function loadUI():void
{
	PrEventDispatcher.registerForEvent("product.create",userUiCreated);
	var xmlCg:XmlComponentGenerator = new XmlComponentGenerator();
	xmlCg.buildComponents("xml/products/addproduct.xml");
}

private function userUiCreated(evt:PrICntnrEvent):void
{
	this.formCntr = evt.iCntnr;
	var uid:String = iCntnr.getICntnrId();
	
	var typeLbl:PrLabelField = this.formCntr.getIComp("formContainer", "product_type_lbl") as PrLabelField;
	typeLbl.text = selectedProductType.product_type;
	
	pop = PrPopupManager.showICntnrInPopup(formCntr, this.title, true);
	
	var startDateField:PrDateField = formCntr.getIComp("formContainer","start_date") as PrDateField;
	var startDateRange:Object = new Object();
	startDateRange["rangeEnd"] = new Date; 
	startDateField.selectableRange =  startDateRange;
	
	if(isModify)
	{
		this.formCntr.setAttrVal(this.selectedObj);
		startDateField.selectedDate = DateField.stringToDate(selectedObj['start_date_val'].toString(), "DD-MM-YYYY");
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
		asObject['product_type'] = ""+selectedProductType.product_type_pid;
		var userObject:UserObject = new UserObject;
		var productMgmtGateway:RemoteObject = RemoteGateway.getInstance().getProductMgmtGateway();
		if(isModify)
		{
			asObject["IBATIS_STATEMENT"]="PRODUCT.UPDATE";
			productMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
			userObject.dataObject = asObject;
			productMgmtGateway.updateProduct(userObject);
		}else{
			asObject["IBATIS_STATEMENT"]="PRODUCT.CREATE_OBJECT";
			productMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
			userObject.dataObject = asObject;
			productMgmtGateway.createProduct(userObject);
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
		}
	}
	
	dataGrid.refreshGrid();
}