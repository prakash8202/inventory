package co.inventory.modules.report
{
	import co.inventory.pojo.Vendor;
	import co.inventory.utils.SharedConstants;
	import co.pointred.fx.comp.container.ICntnr;
	import co.pointred.fx.comp.core.PrAlert;
	import co.pointred.fx.comp.core.PrButton;
	import co.pointred.fx.comp.core.PrComboBox;
	import co.pointred.fx.comp.core.PrDateField;
	import co.pointred.fx.comp.core.PrTextField;
	import co.pointred.fx.comp.core.dataGrid.PrDataGrid;
	import co.pointred.fx.comp.event.PrEventDispatcher;
	import co.pointred.fx.comp.event.PrICntnrEvent;
	import co.pointred.fx.comp.parser.XmlComponentGenerator;
	import co.pointred.fx.comp.resizabletw.PrPopupManager;
	import co.pointred.fx.rpc.RemoteGateway;
	
	import flash.events.MouseEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.DateField;
	import mx.controls.dataGridClasses.DataGridColumn;
	import mx.events.CalendarLayoutChangeEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;
	
	import spark.components.VGroup;
	import spark.events.IndexChangeEvent;
	
	public class PurchaseSummaryManagement extends VGroup
	{
		private var iCntnr:ICntnr;
		private var xmlCompGen:XmlComponentGenerator = new XmlComponentGenerator();
		
		private var dataGrid:PrDataGrid;
		private var vendorCombo:PrComboBox;
		private var selectedVendor:Vendor;
		
		private var fromDateField:PrDateField;
		private var toDateField:PrDateField;
		private var clearBtn:PrButton;
		
		public function PurchaseSummaryManagement()
		{
			PrEventDispatcher.registerForEvent("purchasesummary.grid",uiCreated);
			xmlCompGen.buildComponents("xml/report/purchasesummary.xml");
		}
		
		private function uiCreated(event:PrICntnrEvent):void
		{
			this.iCntnr = event.iCntnr;
			
			vendorCombo = this.iCntnr.getIComp("formContainer", "vendor_id") as PrComboBox;
			vendorCombo.labelField = "vendor_name";
			vendorCombo.addEventListener(IndexChangeEvent.CHANGE,onVendorSelect);
			
			dataGrid = iCntnr.getIComp("purchasesummaryGrid","purchasesummaryList") as PrDataGrid;
			
			PrPopupManager.showICntnrInPopup(iCntnr,"Purchase Summary");
			
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
				
				vendorCombo.selectedIndex = 0;
				vendorCombo.selectedItem = null;
				selectedVendor = null;
				
				clearBtn.enabled = false;
				updateGrid(true);
			}
			);
			
			updateVendorCombo();
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
		
		private function updateGrid(clear:Boolean = false):void
		{
			var sqlStr:String = "";
			sqlStr = "SELECT p.product_name,pt.product_type,p.rate,SUM(pp.quantity) as purchased, (p.rate * SUM(pp.quantity)) as cost_price FROM purchase pp, product p, producttype pt WHERE p.product_pid = pp.product_id AND p.product_type_id = pt.product_type_pid";
			
			var from:String = ""
			var to:String = ""
			var timeStr:String = " 00:00:00";
			
			if(selectedVendor != null)
			{
				sqlStr = sqlStr + " AND pp.vendor_id = " + selectedVendor.vendor_pid;
			}
			if(fromDateField.selectedDate != null && toDateField.selectedDate == null)
			{
				from = DateField.dateToString(fromDateField.selectedDate, "YYYY-MM-DD") + timeStr;
				sqlStr = sqlStr + " AND pp.purchase_date >= '" + from + "'";
			}else if(fromDateField.selectedDate == null && toDateField.selectedDate != null)
			{
				to = DateField.dateToString(toDateField.selectedDate, "YYYY-MM-DD") + timeStr;
				sqlStr = sqlStr + " AND pp.purchase_date <= '" + to + "'";
			}else if(fromDateField.selectedDate != null && toDateField.selectedDate != null)
			{
				if(fromDateField.selectedDate.getTime() > toDateField.selectedDate.getTime())
				{
					PrAlert.show("From Date should be less than To date",PrAlert.WARNING_MESSAGE);
				}else
				{
					from = DateField.dateToString(fromDateField.selectedDate, "YYYY-MM-DD") + timeStr; 
					to = DateField.dateToString(toDateField.selectedDate, "YYYY-MM-DD") + timeStr;
					
					sqlStr = sqlStr + " AND pp.purchase_date BETWEEN '" + from + "' AND '" + to + "'";
				}
			}
			
			sqlStr = sqlStr + " ORDER BY pp.quantity DESC"
				
			if(clear)
				sqlStr = "SELECT * FROM purchase WHERE vendor_id = 0";
			
			dataGrid.sql = sqlStr;
			dataGrid.navigationConfig.sqlString = sqlStr;
			dataGrid.refreshGrid();
		}
		
	}
}