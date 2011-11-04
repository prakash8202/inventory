
import co.inventory.pojo.Product;
import co.inventory.pojo.Vendor;
import co.pointred.fx.comp.container.ICntnr;
import co.pointred.fx.comp.core.PrAlert;
import co.pointred.fx.comp.core.PrComboBox;
import co.pointred.fx.comp.core.PrDateField;
import co.pointred.fx.comp.core.PrLabelField;
import co.pointred.fx.comp.core.PrTextField;
import co.pointred.fx.comp.resizabletw.PrPopupBase;
import co.pointred.fx.dataobjects.UserObject;
import co.pointred.fx.rpc.RemoteGateway;

import mx.collections.ArrayCollection;
import mx.controls.DateField;
import mx.managers.PopUpManager;
import mx.rpc.events.ResultEvent;
import mx.rpc.remoting.RemoteObject;

private var isModify:Boolean = false;
private var isDelete:Boolean = false;
private var title:String;
private var selectedObj:Object;
private var pop:PrPopupBase;
private var formCntr:ICntnr;

private var formProductCombo:PrComboBox;
private var selectedFormProduct:Product;
private var formVendorCombo:PrLabelField;
private var costLbl:PrLabelField;
private var purchaseDateField:PrDateField;
private var quntityField:PrTextField;

private var selectedModifyVendor:Vendor;

public function gridAddBtnClicked(evt:PrDatagridEvent):void
{
	if(selectedVendor != null)
	{
		this.isModify = false;
		this.isDelete = false;
		this.title = "Add Purchase Order";
		loadUI();
	}else{
		PrAlert.show("Select a Vendor to add purchase details",PrAlert.WARNING_MESSAGE);
	}
}

// Modify btn of Grid Clicked
public function gridEditBtnClicked(evt:PrDatagridEvent):void
{
	var selectedObj:Object=evt.dataObject;
	this.selectedObj=selectedObj;
	this.isModify = true;
	this.isDelete = false;
	this.title = "Modify Purchase Order";
	loadUI();
}

// Delete btn of Grid Clicked
public function gridDeleteBtnClicked(evt:PrDatagridEvent):void
{
	var selectedObj:Object=evt.dataObject;
	this.selectedObj=selectedObj;
	this.isDelete = true;
	this.isModify = false;
	var asObject:Object = new Object;
	asObject['purchase_pid'] = ""+selectedObj['purchase_pid'];
	var userObject:UserObject = new UserObject;
	var purchaseMgmtGateway:RemoteObject = RemoteGateway.getInstance().getPurchaseMgmtGateway();
	purchaseMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
	userObject.dataObject = asObject;
	purchaseMgmtGateway.deletePurchase(userObject);
}

private function loadUI():void
{
	PrEventDispatcher.registerForEvent("purchase.create",userUiCreated);
	var xmlCg:XmlComponentGenerator = new XmlComponentGenerator();
	xmlCg.buildComponents("xml/purchase/addpurchase.xml");
}

private function userUiCreated(evt:PrICntnrEvent):void
{
	this.formCntr = evt.iCntnr;
	var uid:String = iCntnr.getICntnrId();
	
	costLbl = this.formCntr.getIComp("formContainer", "cost") as PrLabelField;
	costLbl.text = "0";
	
	formProductCombo = this.formCntr.getIComp("formContainer", "product_id") as PrComboBox;
	formProductCombo.addEventListener(IndexChangeEvent.CHANGE,onProductComboSelect);
	formProductCombo.labelField = "product_name";
	
	formVendorCombo = this.formCntr.getIComp("formContainer", "vendor_id") as PrLabelField;
	
	quntityField = this.formCntr.getIComp("formContainer", "quantity") as PrTextField;
	
	pop = PrPopupManager.showICntnrInPopup(formCntr, this.title, true);
	
	purchaseDateField = formCntr.getIComp("formContainer","purchase_date") as PrDateField;
	var startDateRange:Object = new Object();
	startDateRange["rangeEnd"] = new Date; 
	purchaseDateField.selectableRange =  startDateRange;
	purchaseDateField.enabled = false;
	
	if(isModify)
	{
		this.formCntr.setAttrVal(this.selectedObj);
		purchaseDateField.selectedDate = DateField.stringToDate(selectedObj['purchase_date_val'].toString(), "DD-MM-YYYY");
		
		if(selectedVendor != null)
		{
			formVendorCombo.text = selectedVendor.vendor_name;
			selectedModifyVendor = selectedVendor;
		}else
		{
			var purchaseMgmtGateway:RemoteObject = RemoteGateway.getInstance().getPurchaseMgmtGateway();
			purchaseMgmtGateway.addEventListener(ResultEvent.RESULT, onVendorValueFetch);
			purchaseMgmtGateway.selectVendor(""+selectedObj['vendor_id']);
		}
	}else{
		formVendorCombo.text = selectedVendor.vendor_name;
	}
	
	var saveBtn:PrButton = formCntr.getIComp("formContainer","save_btn") as PrButton;
	saveBtn.addEventListener(MouseEvent.CLICK, function(evt:MouseEvent):void
	{
		saveBtnClick(formCntr);
	}
	);
	
	updateFormProductCombo();
}

private function onVendorValueFetch(event:ResultEvent):void
{
	var vendor:Vendor = event.result as Vendor;
	
	if(vendor != null)
	{
		selectedModifyVendor = vendor;
		formVendorCombo.text = vendor.vendor_name;
	}
}

private function updateFormProductCombo():void
{
	var asObject:Object = new Object;
	var productMgmtGateway:RemoteObject = RemoteGateway.getInstance().getProductMgmtGateway();
	productMgmtGateway.addEventListener(ResultEvent.RESULT, onFormProductFetch);
	productMgmtGateway.fetchProductList();
}

private function onFormProductFetch(event:ResultEvent):void
{
	var productList:ArrayCollection = event.result as ArrayCollection;
	SharedConstants.getInstance().setDataProvider(formProductCombo,productList);
	
	if(isModify)
	{
		for(var i:int=0; i<productList.length;i++)
		{
			if(productList[i]['product_pid'] == this.selectedObj['product_id'])
			{
				formProductCombo.selectedIndex = i;
				formProductCombo.selectedItem = productList[i] as Object;
				selectedFormProduct = productList[i] as Product;
				
				costLbl.text = selectedFormProduct.rate;
				
				var endDateRange:Object = new Object();
				endDateRange["rangeStart"] = selectedFormProduct.start_date;
				endDateRange["rangeEnd"] = new Date; 
				purchaseDateField.selectableRange =  endDateRange;
				purchaseDateField.enabled = true;
				break;
			}
		}
	}
}

private function saveBtnClick(iCntnr:ICntnr):void
{
	var validation:Boolean= iCntnr.validate();
	
	if(validation == true)
	{
		if(formProductCombo.selectedIndex < 1)
		{
			validation = false;
			PrAlert.show("Select a Product",PrAlert.ERROR_MESSAGE);
		}
			
	}
	
	if(validation==true)
	{
		var asObject:Object = iCntnr.getAllAttrVal();
		asObject['product_id'] = ""+selectedFormProduct.product_pid;
		asObject['cost'] = ""+selectedFormProduct.rate;
		var userObject:UserObject = new UserObject;
		var purchaseMgmtGateway:RemoteObject = RemoteGateway.getInstance().getPurchaseMgmtGateway()
		if(isModify)
		{
			asObject['vendor_id'] = ""+selectedModifyVendor.vendor_pid;
			asObject["IBATIS_STATEMENT"]="PURCHASE.UPDATE";
			purchaseMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
			userObject.dataObject = asObject;
			purchaseMgmtGateway.updatePurchase(userObject);
		}else{
			asObject['vendor_id'] = ""+selectedVendor.vendor_pid;
			asObject["IBATIS_STATEMENT"]="PURCHASE.CREATE_OBJECT";
			purchaseMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
			userObject.dataObject = asObject;
			purchaseMgmtGateway.createPurchase(userObject);
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
			formVendorCombo.text = selectedVendor.vendor_name;
			purchaseDateField.selectedDate = null;
			purchaseDateField.text = "";
			quntityField.text = "";
		}
	}
	
	dataGrid.refreshGrid();
}

private function onProductComboSelect(event:IndexChangeEvent):void
{
	if(event.newIndex > 0)
	{
		selectedFormProduct = formProductCombo.selectedItem as Product;
		costLbl.text = selectedFormProduct.rate;
		
		var endDateRange:Object = new Object();
		endDateRange["rangeStart"] = selectedFormProduct.start_date;
		endDateRange["rangeEnd"] = new Date; 
		purchaseDateField.selectableRange =  endDateRange;
		purchaseDateField.enabled = true;
	}
}
