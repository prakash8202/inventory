package co.inventory.modules.report
{
	import co.inventory.pojo.Product;
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
	
	public class ProductSummaryManagement extends VGroup
	{
		private var iCntnr:ICntnr;
		private var xmlCompGen:XmlComponentGenerator = new XmlComponentGenerator();
		
		private var purchaseDataGrid:PrDataGrid;
		private var salesDataGrid:PrDataGrid;
		private var productCombo:PrComboBox;
		private var selectedProduct:Product;
		
		private var fromDateField:PrDateField;
		private var toDateField:PrDateField;
		private var clearBtn:PrButton;
		
		public function ProductSummaryManagement()
		{
			PrEventDispatcher.registerForEvent("productsummary.grid",uiCreated);
			xmlCompGen.buildComponents("xml/report/productsummary.xml");
		}
		
		private function uiCreated(event:PrICntnrEvent):void
		{
			this.iCntnr = event.iCntnr;
			
			productCombo = this.iCntnr.getIComp("formContainer", "product_id") as PrComboBox;
			productCombo.labelField = "product_name";
			productCombo.addEventListener(IndexChangeEvent.CHANGE,onProductSelect);
			
			purchaseDataGrid = iCntnr.getIComp("productsummaryGrid","purchasesummaryList") as PrDataGrid;
			salesDataGrid = iCntnr.getIComp("productsummaryGrid","salessummaryList") as PrDataGrid;
			
			PrPopupManager.showICntnrInPopup(iCntnr,"Product Summary");
			
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
				
				clearBtn.enabled = false;
				updateGrid();
			}
			);
			
			updateProductCombo();
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
		
		private function onProductSelect(event:IndexChangeEvent):void
		{
			if(event.newIndex > 0)
			{
				selectedProduct = productCombo.selectedItem as Product;
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
			var purchaseSqlStr:String = "SELECT DISTINCT v.vendor_name, SUM(pp.quantity) as purchased, (p.rate * SUM(pp.quantity)) as cost_price FROM purchase pp, product p,vendor v WHERE p.product_pid = pp.product_id AND pp.vendor_id = v.vendor_pid";
			var salesSqlStr:String = "SELECT DISTINCT c.customer_name, SUM(s.quantity) as sold, SUM(s.price * s.quantity) as sold_price FROM sales s, product p,customer c WHERE p.product_pid = s.product_id AND s.customer_id = c.customer_pid";
			
			var from:String = ""
			var to:String = ""
			var timeStr:String = " 00:00:00";
			
			if(selectedProduct != null)
			{
				purchaseSqlStr = purchaseSqlStr + " AND p.product_pid = "+selectedProduct.product_pid;
				salesSqlStr = salesSqlStr + " AND p.product_pid = "+selectedProduct.product_pid;
			}
			if(fromDateField.selectedDate != null && toDateField.selectedDate == null)
			{
				from = DateField.dateToString(fromDateField.selectedDate, "YYYY-MM-DD") + timeStr;
				purchaseSqlStr = purchaseSqlStr + " AND pp.purchase_date >= '" + from + "'";
				salesSqlStr = salesSqlStr + " AND s.sales_date >= '" + from + "'";
			}else if(fromDateField.selectedDate == null && toDateField.selectedDate != null)
			{
				to = DateField.dateToString(toDateField.selectedDate, "YYYY-MM-DD") + timeStr;
				purchaseSqlStr = purchaseSqlStr + " AND pp.purchase_date <= '" + to + "'";
				salesSqlStr = salesSqlStr + " AND s.sales_date <= '" + to + "'";
			}else if(fromDateField.selectedDate != null && toDateField.selectedDate != null)
			{
				if(fromDateField.selectedDate.getTime() > toDateField.selectedDate.getTime())
				{
					PrAlert.show("From Date should be less than To date",PrAlert.WARNING_MESSAGE);
				}else
				{
					from = DateField.dateToString(fromDateField.selectedDate, "YYYY-MM-DD") + timeStr; 
					to = DateField.dateToString(toDateField.selectedDate, "YYYY-MM-DD") + timeStr;
					
					purchaseSqlStr = purchaseSqlStr + " AND pp.purchase_date BETWEEN '" + from + "' AND '" + to + "'";
					salesSqlStr = salesSqlStr + " AND s.sales_date BETWEEN '" + from + "' AND '" + to + "'";
				}
			}
			
			purchaseSqlStr = purchaseSqlStr + " GROUP BY v.vendor_name ORDER BY (p.rate * SUM(pp.quantity)) DESC"
			salesSqlStr = salesSqlStr + " GROUP BY c.customer_name ORDER BY SUM(s.price * s.quantity) DESC"
			
			purchaseDataGrid.sql = purchaseSqlStr;
			purchaseDataGrid.navigationConfig.sqlString = purchaseSqlStr;
			purchaseDataGrid.refreshGrid();
			
			salesDataGrid.sql = salesSqlStr;
			salesDataGrid.navigationConfig.sqlString = salesSqlStr;
			salesDataGrid.refreshGrid();
		}
	}
}