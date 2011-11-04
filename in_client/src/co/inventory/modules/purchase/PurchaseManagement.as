package co.inventory.modules.purchase
{
	import co.inventory.pojo.Product;
	import co.inventory.pojo.Vendor;
	import co.inventory.utils.SharedConstants;
	import co.pointred.fx.comp.container.ICntnr;
	import co.pointred.fx.comp.core.PrAlert;
	import co.pointred.fx.comp.core.PrButton;
	import co.pointred.fx.comp.core.PrComboBox;
	import co.pointred.fx.comp.core.PrDateField;
	import co.pointred.fx.comp.core.dataGrid.PrDataGrid;
	import co.pointred.fx.comp.event.PrDatagridEvent;
	import co.pointred.fx.comp.event.PrEventDispatcher;
	import co.pointred.fx.comp.event.PrICntnrEvent;
	import co.pointred.fx.comp.parser.XmlComponentGenerator;
	import co.pointred.fx.comp.resizabletw.PrPopupManager;
	import co.pointred.fx.rpc.RemoteGateway;
	
	import flash.events.MouseEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.DateField;
	import mx.core.IVisualElement;
	import mx.events.CalendarLayoutChangeEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;
	
	import spark.components.VGroup;
	import spark.events.IndexChangeEvent;

	public class PurchaseManagement extends VGroup
	{
		include "PurchaseManagementSupport.as";
		
		private var iCntnr:ICntnr;
		private var xmlCompGen:XmlComponentGenerator = new XmlComponentGenerator();
		
		private var dataGrid:PrDataGrid;
		private var productCombo:PrComboBox;
		private var selectedProduct:Product;
		private var vendorCombo:PrComboBox;
		private var selectedVendor:Vendor;
		
		private var fromDateField:PrDateField;
		private var toDateField:PrDateField;
		private var clearBtn:PrButton;
		
		public function PurchaseManagement()
		{
			PrEventDispatcher.registerForEvent("purchasemgmt.grid",uiCreated);
			xmlCompGen.buildComponents("xml/purchase/purchasemgmt.xml");
		}
		
		private function uiCreated(event:PrICntnrEvent):void
		{
			this.iCntnr = event.iCntnr;
			
			productCombo = this.iCntnr.getIComp("formContainer", "product_id") as PrComboBox;
			productCombo.labelField = "product_name";
			productCombo.addEventListener(IndexChangeEvent.CHANGE,onProductSelect);
			
			vendorCombo = this.iCntnr.getIComp("formContainer", "vendor_id") as PrComboBox;
			vendorCombo.labelField = "vendor_name";
			vendorCombo.addEventListener(IndexChangeEvent.CHANGE,onVendorSelect);
			
			dataGrid = iCntnr.getIComp("purchaseGrid","purchaseList") as PrDataGrid;
			
			// add lsnr for ADD clicked
			dataGrid.addEventListener(PrDatagridEvent.ADD_CLICKED,gridAddBtnClicked);
			// add lsnr for MODIFY clicked
			dataGrid.addEventListener(PrDatagridEvent.EDIT_CLICKED,gridEditBtnClicked);
			//add lsnr for DELETE clicked
			dataGrid.addEventListener(PrDatagridEvent.DELETE_CLICKED,gridDeleteBtnClicked);
			
			var cntr:PrPopupBase = new PrPopupBase;
			cntr.isClosable = false;
			cntr.percentHeight = 100;
			cntr.percentWidth = 100;
			cntr.title = "Purchase Order Management";
			cntr.addElement(iCntnr as IVisualElement);
			
			SharedConstants.getInstance().addToContainer(cntr);
			
			var endDateRange:Object = new Object();
			endDateRange["rangeEnd"] = new Date; 
			
			fromDateField = iCntnr.getIComp("formContainer","from_date") as PrDateField;
			fromDateField.selectableRange =  endDateRange;
			fromDateField.addEventListener(CalendarLayoutChangeEvent.CHANGE, onFromDateChange);
			
			toDateField = iCntnr.getIComp("formContainer","to_date") as PrDateField;
			toDateField.selectableRange =  endDateRange;
			toDateField.addEventListener(CalendarLayoutChangeEvent.CHANGE, onToDateChange);
			
			clearBtn = iCntnr.getIComp("formContainer","clear_btn") as PrButton;
			
			clearBtn.enabled = false;
			
			clearBtn.addEventListener(MouseEvent.CLICK, function(evt:MouseEvent):void
			{
				fromDateField.selectedDate = null;
				fromDateField.text = "";
				toDateField.selectedDate = null;
				toDateField.text = "";
				
				productCombo.selectedIndex = 0;
				productCombo.selectedItem = null;
				selectedProduct = null;
				
				vendorCombo.selectedIndex = 0;
				vendorCombo.selectedItem = null;
				selectedVendor = null;
				
				clearBtn.enabled = false;
				updateGrid();
			}
			);
			
			updateProductCombo();
			updateVendorCombo();
			updateGrid();
		}
		
		private function updateProductCombo():void
		{
			var asObject:Object = new Object;
			var productMgmtGateway:RemoteObject = RemoteGateway.getInstance().getProductMgmtGateway();
			productMgmtGateway.addEventListener(ResultEvent.RESULT, onProductFetch);
			productMgmtGateway.fetchProductList();
		}
		
		private function onProductFetch(event:ResultEvent):void
		{
			var productList:ArrayCollection = event.result as ArrayCollection;
			SharedConstants.getInstance().setDataProvider(productCombo,productList);
		}
		
		private function updateVendorCombo():void
		{
			var asObject:Object = new Object;
			var purchaseMgmtGateway:RemoteObject = RemoteGateway.getInstance().getPurchaseMgmtGateway();
			purchaseMgmtGateway.addEventListener(ResultEvent.RESULT, onVendorFetch);
			purchaseMgmtGateway.fetchVendorList();
		}
		
		private function onVendorFetch(event:ResultEvent):void
		{
			var vendorList:ArrayCollection = event.result as ArrayCollection;
			SharedConstants.getInstance().setDataProvider(vendorCombo,vendorList);
		}
		
		private function onProductSelect(event:IndexChangeEvent):void
		{
			if(event.newIndex > 0)
			{
				selectedProduct = productCombo.selectedItem as Product;
				updateGrid();
				clearBtn.enabled = true;
			}
		}
		
		private function onVendorSelect(event:IndexChangeEvent):void
		{
			if(event.newIndex > 0)
			{
				selectedVendor = vendorCombo.selectedItem as Vendor;
				updateGrid();
				clearBtn.enabled = true;
			}
		}
		
		private function onFromDateChange(event:CalendarLayoutChangeEvent):void 
		{
			clearBtn.enabled = true;
			
			var endDateRange:Object = new Object();
			endDateRange["rangeStart"] = fromDateField.selectedDate;
			endDateRange["rangeEnd"] = new Date; 
			toDateField.selectableRange =  endDateRange;	
			
			updateGrid();
		}
		
		private function onToDateChange(event:CalendarLayoutChangeEvent):void 
		{
			clearBtn.enabled = true;
			updateGrid();
		}
		
		private function updateGrid():void
		{
			var sqlStr:String = "";
			sqlStr = "SELECT p.purchase_pid,p.product_id,p.vendor_id,pr.product_name,pt.product_type,v.vendor_name, DATE_FORMAT(p.purchase_date,'%d %b %Y'), p.cost, p.quantity,DATE_FORMAT(p.purchase_date,'%d-%m-%Y') as purchase_date_val FROM purchase p, product pr, vendor v, producttype pt WHERE p.product_id = pr.product_pid AND pr.product_type_id = pt.product_type_pid AND p.vendor_id = v.vendor_pid";
			
			var from:String = ""
			var to:String = ""
			var timeStr:String = " 00:00:00";
			
			if(selectedProduct != null)
			{
				sqlStr = sqlStr + " AND product_id = "+selectedProduct.product_pid;
			}
			if(selectedVendor != null)
			{
				sqlStr = sqlStr + " AND vendor_id = " + selectedVendor.vendor_pid;
			}
			if(fromDateField.selectedDate != null && toDateField.selectedDate == null)
			{
				from = DateField.dateToString(fromDateField.selectedDate, "YYYY-MM-DD") + timeStr;
				sqlStr = sqlStr + " AND purchase_date >= '" + from + "'";
			}else if(fromDateField.selectedDate == null && toDateField.selectedDate != null)
			{
				to = DateField.dateToString(toDateField.selectedDate, "YYYY-MM-DD") + timeStr;
				sqlStr = sqlStr + " AND purchase_date <= '" + to + "'";
			}else if(fromDateField.selectedDate != null && toDateField.selectedDate != null)
			{
				if(fromDateField.selectedDate.getTime() > toDateField.selectedDate.getTime())
				{
					PrAlert.show("From Date should be less than To date",PrAlert.WARNING_MESSAGE);
				}else
				{
					from = DateField.dateToString(fromDateField.selectedDate, "YYYY-MM-DD") + timeStr; 
					to = DateField.dateToString(toDateField.selectedDate, "YYYY-MM-DD") + timeStr;
					
					sqlStr = sqlStr + " AND purchase_date BETWEEN '" + from + "' AND '" + to + "'";
				}
			}
			
			sqlStr = sqlStr + " ORDER BY purchase_date DESC"
			
			dataGrid.sql = sqlStr;
			dataGrid.navigationConfig.sqlString = sqlStr;
			dataGrid.refreshGrid();
		}
	}
}