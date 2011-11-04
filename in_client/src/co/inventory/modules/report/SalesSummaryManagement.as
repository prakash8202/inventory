package co.inventory.modules.report
{
	import co.inventory.pojo.Customer;
	import co.inventory.utils.SharedConstants;
	import co.pointred.fx.comp.container.ICntnr;
	import co.pointred.fx.comp.core.PrAlert;
	import co.pointred.fx.comp.core.PrButton;
	import co.pointred.fx.comp.core.PrComboBox;
	import co.pointred.fx.comp.core.PrDateField;
	import co.pointred.fx.comp.core.dataGrid.PrDataGrid;
	import co.pointred.fx.comp.event.PrEventDispatcher;
	import co.pointred.fx.comp.event.PrICntnrEvent;
	import co.pointred.fx.comp.parser.XmlComponentGenerator;
	import co.pointred.fx.comp.resizabletw.PrPopupManager;
	import co.pointred.fx.rpc.RemoteGateway;
	
	import flash.events.MouseEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.DateField;
	import mx.events.CalendarLayoutChangeEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;
	
	import spark.components.VGroup;
	import spark.events.IndexChangeEvent;
	
	public class SalesSummaryManagement extends VGroup
	{
		private var iCntnr:ICntnr;
		private var xmlCompGen:XmlComponentGenerator = new XmlComponentGenerator();
		
		private var dataGrid:PrDataGrid;
		private var customerCombo:PrComboBox;
		private var selectedCustomer:Customer;
		
		private var fromDateField:PrDateField;
		private var toDateField:PrDateField;
		private var clearBtn:PrButton;
		
		public function SalesSummaryManagement()
		{
			PrEventDispatcher.registerForEvent("salessummary.grid",uiCreated);
			xmlCompGen.buildComponents("xml/report/salessummary.xml");
		}
		
		private function uiCreated(event:PrICntnrEvent):void
		{
			this.iCntnr = event.iCntnr;
			
			customerCombo = this.iCntnr.getIComp("formContainer", "customer_id") as PrComboBox;
			customerCombo.labelField = "customer_name";
			customerCombo.addEventListener(IndexChangeEvent.CHANGE,onCustomerSelect);
			
			dataGrid = iCntnr.getIComp("salessummaryGrid","salessummaryList") as PrDataGrid;
			
			PrPopupManager.showICntnrInPopup(iCntnr,"Sales Summary");
			
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
				
				customerCombo.selectedIndex = 0;
				customerCombo.selectedItem = null;
				selectedCustomer = null;
				
				clearBtn.enabled = false;
				updateGrid(true);
			}
			);
			
			updateCustomerCombo();
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
		
		private function updateGrid(clear:Boolean = false):void
		{
			var sqlStr:String = "";
			sqlStr = "SELECT p.product_name,pt.product_type,SUM(s.quantity) as sold, SUM(s.price * s.quantity) as sold_price FROM sales s, product p, producttype pt WHERE p.product_pid = s.product_id AND p.product_type_id = pt.product_type_pid";
			
			var from:String = ""
			var to:String = ""
			var timeStr:String = " 00:00:00";
			
			if(selectedCustomer != null)
			{
				sqlStr = sqlStr + " AND s.customer_id = " + selectedCustomer.customer_pid;
			}
			if(fromDateField.selectedDate != null && toDateField.selectedDate == null)
			{
				from = DateField.dateToString(fromDateField.selectedDate, "YYYY-MM-DD") + timeStr;
				sqlStr = sqlStr + " AND s.sales_date >= '" + from + "'";
			}else if(fromDateField.selectedDate == null && toDateField.selectedDate != null)
			{
				to = DateField.dateToString(toDateField.selectedDate, "YYYY-MM-DD") + timeStr;
				sqlStr = sqlStr + " AND s.sales_date <= '" + to + "'";
			}else if(fromDateField.selectedDate != null && toDateField.selectedDate != null)
			{
				if(fromDateField.selectedDate.getTime() > toDateField.selectedDate.getTime())
				{
					PrAlert.show("From Date should be less than To date",PrAlert.WARNING_MESSAGE);
				}else
				{
					from = DateField.dateToString(fromDateField.selectedDate, "YYYY-MM-DD") + timeStr; 
					to = DateField.dateToString(toDateField.selectedDate, "YYYY-MM-DD") + timeStr;
					
					sqlStr = sqlStr + " AND s.sales_date BETWEEN '" + from + "' AND '" + to + "'";
				}
			}
			
			sqlStr = sqlStr + " ORDER BY s.quantity DESC"
				
			if(clear)
				sqlStr = "SELECT * FROM sales WHERE customer_id = 0";
			
			dataGrid.sql = sqlStr;
			dataGrid.navigationConfig.sqlString = sqlStr;
			dataGrid.refreshGrid();
		}
	}
}