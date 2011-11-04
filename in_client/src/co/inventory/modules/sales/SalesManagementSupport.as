import co.inventory.pojo.Customer;
import co.inventory.pojo.Product;
import co.pointred.fx.comp.container.ICntnr;
import co.pointred.fx.comp.core.PrAlert;
import co.pointred.fx.comp.core.PrComboBox;
import co.pointred.fx.comp.core.PrDateField;
import co.pointred.fx.comp.core.PrLabelField;
import co.pointred.fx.comp.core.PrTextField;
import co.pointred.fx.comp.resizabletw.PrPopupBase;
import co.pointred.fx.comp.resizabletw.PrPopupManager;
import co.pointred.fx.dataobjects.UserObject;
import co.pointred.fx.rpc.RemoteGateway;

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
private var formCustomerLabel:PrLabelField;
private var quantityField:PrTextField;
private var availableQuantityLbl:PrLabelField;
private var rateLbl:PrLabelField;
private var priceField:PrTextField;

private var selectedModifyCustomer:Customer;

private var salesDateField:PrDateField;

public function gridAddBtnClicked(evt:PrDatagridEvent):void
{
	if(selectedCustomer != null)
	{
		this.isModify = false;
		this.isDelete = false;
		this.title = "Add Sales Order";
		loadUI();
	}else{
		PrAlert.show("Select a Customer to add sales details",PrAlert.WARNING_MESSAGE);
	}
}

// Modify btn of Grid Clicked
public function gridEditBtnClicked(evt:PrDatagridEvent):void
{
	var selectedObj:Object=evt.dataObject;
	this.selectedObj=selectedObj;
	this.isModify = true;
	this.isDelete = false;
	this.title = "Modify Sales Order";
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
	asObject['sales_pid'] = ""+selectedObj['sales_pid'];
	var userObject:UserObject = new UserObject;
	var salesMgmtGateway:RemoteObject = RemoteGateway.getInstance().getSalesMgmtGateway();
	salesMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
	userObject.dataObject = asObject;
	salesMgmtGateway.deleteSales(userObject);
}

private function loadUI():void
{
	PrEventDispatcher.registerForEvent("sales.create",userUiCreated);
	var xmlCg:XmlComponentGenerator = new XmlComponentGenerator();
	xmlCg.buildComponents("xml/sales/addsales.xml");
}

private function userUiCreated(evt:PrICntnrEvent):void
{
	this.formCntr = evt.iCntnr;
	var uid:String = iCntnr.getICntnrId();
	
	formProductCombo = this.formCntr.getIComp("formContainer", "product_id") as PrComboBox;
	formProductCombo.addEventListener(IndexChangeEvent.CHANGE,onProductComboSelect);
	formProductCombo.labelField = "product_name";
	
	formCustomerLabel = this.formCntr.getIComp("formContainer", "customer_id") as PrLabelField;
	
	quantityField = this.formCntr.getIComp("formContainer", "quantity") as PrTextField;
	priceField = this.formCntr.getIComp("formContainer", "price") as PrTextField;
	
	availableQuantityLbl = this.formCntr.getIComp("formContainer", "available_quantity") as PrLabelField;
	availableQuantityLbl.text = "0";
	rateLbl = this.formCntr.getIComp("formContainer", "rate") as PrLabelField;
	rateLbl.text = "0";
	
	pop = PrPopupManager.showICntnrInPopup(formCntr, this.title, true);
	
	salesDateField = formCntr.getIComp("formContainer","sales_date") as PrDateField;
	var startDateRange:Object = new Object();
	startDateRange["rangeEnd"] = new Date; 
	salesDateField.selectableRange =  startDateRange;
	salesDateField.enabled = false;
	
	if(isModify)
	{
		this.formCntr.setAttrVal(this.selectedObj);
		salesDateField.selectedDate = DateField.stringToDate(selectedObj['sales_date_val'].toString(), "DD-MM-YYYY");
		if(selectedCustomer != null)
		{
			formCustomerLabel.text = selectedCustomer.customer_name;
			selectedModifyCustomer = selectedCustomer;
		}else
		{
			var salesMgmtGateway:RemoteObject = RemoteGateway.getInstance().getSalesMgmtGateway();
			salesMgmtGateway.addEventListener(ResultEvent.RESULT, onCustomerValueFetch);
			salesMgmtGateway.selectCustomer(""+selectedObj['customer_id']);
		}
	}else{
		formCustomerLabel.text = selectedCustomer.customer_name;
	}
	
	var saveBtn:PrButton = formCntr.getIComp("formContainer","save_btn") as PrButton;
	saveBtn.addEventListener(MouseEvent.CLICK, function(evt:MouseEvent):void
	{
		saveBtnClick(formCntr);
	}
	);
	
	updateFormProductCombo();
}

private function onCustomerValueFetch(event:ResultEvent):void
{
	var customer:Customer = event.result as Customer;
	
	if(customer != null)
	{
		selectedModifyCustomer = customer;
		formCustomerLabel.text = customer.customer_name;
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
				
				availableQuantityLbl.text = ""+selectedFormProduct.available;
				rateLbl.text = ""+selectedFormProduct.rate;
				
				var endDateRange:Object = new Object();
				endDateRange["rangeStart"] = selectedFormProduct.start_date;
				endDateRange["rangeEnd"] = new Date; 
				salesDateField.selectableRange =  endDateRange;
				salesDateField.enabled = true;
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
		}else if(false == isModify && parseInt(quantityField.text) > selectedFormProduct.available)
		{
			validation = false;
			PrAlert.show("Specified quantity is not available.|Available Quantity of " + selectedFormProduct.product_name.toUpperCase() + " is " + selectedFormProduct.available ,PrAlert.INFO_MESSAGE);
		}
		
	}
	
	if(validation==true)
	{
		var asObject:Object = iCntnr.getAllAttrVal();
		asObject['product_id'] = ""+selectedFormProduct.product_pid;
		
		var userObject:UserObject = new UserObject;
		var salesMgmtGateway:RemoteObject = RemoteGateway.getInstance().getSalesMgmtGateway();
		if(isModify)
		{
			asObject['customer_id'] = ""+selectedModifyCustomer.customer_pid;
			asObject["IBATIS_STATEMENT"]="SALES.UPDATE";
			salesMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
			userObject.dataObject = asObject;
			salesMgmtGateway.updateSales(userObject);
		}else{
			asObject['customer_id'] = ""+selectedCustomer.customer_pid;
			asObject["IBATIS_STATEMENT"]="SALES.CREATE_OBJECT";
			salesMgmtGateway.addEventListener(ResultEvent.RESULT,onSuccess);
			userObject.dataObject = asObject;
			salesMgmtGateway.createSales(userObject);
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
			formCustomerLabel.text = selectedCustomer.customer_name;
			quantityField.text = "";
			priceField.text = "";
		}
	}
	
	dataGrid.refreshGrid();
}

private function onProductComboSelect(event:IndexChangeEvent):void
{
	if(event.newIndex > 0)
	{
		selectedFormProduct = formProductCombo.selectedItem as Product;
		availableQuantityLbl.text = ""+selectedFormProduct.available;
		rateLbl.text = ""+selectedFormProduct.rate;
		
		var endDateRange:Object = new Object();
		endDateRange["rangeStart"] = selectedFormProduct.start_date;
		endDateRange["rangeEnd"] = new Date; 
		salesDateField.selectableRange =  endDateRange;
		salesDateField.enabled = true;
	}
}