package co.inventory.modules.sales
{
	import co.inventory.pojo.Customer;
	import co.inventory.pojo.Product;
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
	import co.pointred.fx.comp.resizabletw.PrPopupBase;
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
	
	public class SalesManagement extends VGroup
	{
		include "SalesManagementSupport.as";
		
		private var iCntnr:ICntnr;
		private var xmlCompGen:XmlComponentGenerator = new XmlComponentGenerator();
		
		private var dataGrid:PrDataGrid;
		private var productCombo:PrComboBox;
		private var selectedProduct:Product;
		private var customerCombo:PrComboBox;
		private var selectedCustomer:Customer;
		
		private var fromDateField:PrDateField;
		private var toDateField:PrDateField;
		private var clearBtn:PrButton;
		
		public function SalesManagement()
		{
			PrEventDispatcher.registerForEvent("salesmgmt.grid",uiCreated);
			xmlCompGen.buildComponents("xml/sales/salesmgmt.xml");
		}
		
		private function uiCreated(event:PrICntnrEvent):void
		{
			this.iCntnr = event.iCntnr;
			
			productCombo = this.iCntnr.getIComp("formContainer", "product_id") as PrComboBox;
			productCombo.labelField = "product_name";
			productCombo.addEventListener(IndexChangeEvent.CHANGE,onProductSelect);
			
			customerCombo = this.iCntnr.getIComp("formContainer", "customer_id") as PrComboBox;
			customerCombo.labelField = "customer_name";
			customerCombo.addEventListener(IndexChangeEvent.CHANGE,onCustomerSelect);
			
			dataGrid = iCntnr.getIComp("salesGrid","salesList") as PrDataGrid;
			
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
			cntr.title = "Sales Order Management";
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
				
				customerCombo.selectedIndex = 0;
				customerCombo.selectedItem = null;
				selectedCustomer = null;
				
				clearBtn.enabled = false;
				updateGrid();
			}
			);
			
			updateProductCombo();
			updateCustomerCombo();
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
		
		private function updateCustomerCombo():void
		{
			var asObject:Object = new Object;
			var salesMgmtGateway:RemoteObject = RemoteGateway.getInstance().getSalesMgmtGateway();
			salesMgmtGateway.addEventListener(ResultEvent.RESULT, onCustomerFetch);
			salesMgmtGateway.fetchCustomerList();
		}
		
		private function onCustomerFetch(event:ResultEvent):void
		{
			var customerList:ArrayCollection = event.result as ArrayCollection;
			SharedConstants.getInstance().setDataProvider(customerCombo,customerList);
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
		
		private function onCustomerSelect(event:IndexChangeEvent):void
		{
			if(event.newIndex > 0)
			{
				selectedCustomer = customerCombo.selectedItem as Customer;
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
			sqlStr = "SELECT s.sales_pid,s.product_id,s.customer_id,pr.product_name,pt.product_type,c.customer_name, DATE_FORMAT(s.sales_date,'%d %b %Y'), s.price, s.quantity,DATE_FORMAT(s.sales_date,'%d-%m-%Y') as sales_date_val FROM sales s, product pr, customer c, producttype pt WHERE s.product_id = pr.product_pid AND pr.product_type_id = pt.product_type_pid AND s.customer_id = c.customer_pid";
			
			var from:String = ""
			var to:String = ""
			var timeStr:String = " 00:00:00";
			
			if(selectedProduct != null)
			{
				sqlStr = sqlStr + " AND product_id = "+selectedProduct.product_pid;
			}
			if(selectedCustomer != null)
			{
				sqlStr = sqlStr + " AND customer_id = " + selectedCustomer.customer_pid;
			}
			if(fromDateField.selectedDate != null && toDateField.selectedDate == null)
			{
				from = DateField.dateToString(fromDateField.selectedDate, "YYYY-MM-DD") + timeStr;
				sqlStr = sqlStr + " AND sales_date >= '" + from + "'";
			}else if(fromDateField.selectedDate == null && toDateField.selectedDate != null)
			{
				to = DateField.dateToString(toDateField.selectedDate, "YYYY-MM-DD") + timeStr;
				sqlStr = sqlStr + " AND sales_date <= '" + to + "'";
			}else if(fromDateField.selectedDate != null && toDateField.selectedDate != null)
			{
				if(fromDateField.selectedDate.getTime() > toDateField.selectedDate.getTime())
				{
					PrAlert.show("From Date should be less than To date",PrAlert.WARNING_MESSAGE);
				}else
				{
					from = DateField.dateToString(fromDateField.selectedDate, "YYYY-MM-DD") + timeStr; 
					to = DateField.dateToString(toDateField.selectedDate, "YYYY-MM-DD") + timeStr;
					
					sqlStr = sqlStr + " AND sales_date BETWEEN '" + from + "' AND '" + to + "'";
				}
			}
			
			sqlStr = sqlStr + " ORDER BY sales_date DESC"
			
			dataGrid.sql = sqlStr;
			dataGrid.navigationConfig.sqlString = sqlStr;
			dataGrid.refreshGrid();
		}
	}
}