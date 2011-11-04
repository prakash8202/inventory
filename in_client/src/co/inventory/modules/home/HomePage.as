package co.inventory.modules.home
{
	import co.inventory.utils.SharedConstants;
	import co.pointred.fx.comp.container.ICntnr;
	import co.pointred.fx.comp.core.PrButton;
	import co.pointred.fx.comp.core.PrDateField;
	import co.pointred.fx.comp.core.PrLabelField;
	import co.pointred.fx.comp.core.dataGrid.PrDataGrid;
	import co.pointred.fx.comp.event.PrEventDispatcher;
	import co.pointred.fx.comp.event.PrICntnrEvent;
	import co.pointred.fx.comp.parser.XmlComponentGenerator;
	import co.pointred.fx.comp.resizabletw.PrPopupBase;
	import co.pointred.fx.rpc.RemoteGateway;
	
	import flash.events.MouseEvent;
	
	import mx.controls.Alert;
	import mx.controls.dataGridClasses.DataGridColumn;
	import mx.core.IVisualElement;
	import mx.events.CalendarLayoutChangeEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;
	
	import org.flexunit.runner.Result;
	
	import spark.components.Label;
	import spark.components.VGroup;
	
	public class HomePage extends VGroup
	{
		private var iCntnr:ICntnr;
		private var xmlCompGen:XmlComponentGenerator = new XmlComponentGenerator();
		
		private var productSummaryGrid:PrDataGrid;
		private var expenseSummaryGrid:PrDataGrid;
		
		private var total_purchaseLbl:PrLabelField;
		private var total_salesLbl:PrLabelField;
		private var total_expenseLbl:PrLabelField;
		private var profit_lossLbl:PrLabelField;
		private var percentageLbl:PrLabelField;
		
		private var fromDateField:PrDateField;
		private var toDateField:PrDateField;
		private var clearBtn:PrButton;
		
		public function HomePage()
		{
			PrEventDispatcher.registerForEvent("homesummary.grid",uiCreated);
			xmlCompGen.buildComponents("xml/home/homesummary.xml");
		}
		
		private function uiCreated(event:PrICntnrEvent):void
		{
			this.iCntnr = event.iCntnr;
			
			productSummaryGrid = iCntnr.getIComp("productsummaryGrid","productsummaryList") as PrDataGrid;
			expenseSummaryGrid = iCntnr.getIComp("expensesummaryGrid","expensesummaryList") as PrDataGrid;
			
			total_purchaseLbl = iCntnr.getIComp("expensesummaryGrid","total_purchase") as PrLabelField;
			total_salesLbl = iCntnr.getIComp("expensesummaryGrid","total_sales") as PrLabelField;
			total_expenseLbl = iCntnr.getIComp("expensesummaryGrid","total_expense") as PrLabelField;
			profit_lossLbl = iCntnr.getIComp("expensesummaryGrid","profit_loss") as PrLabelField;
			percentageLbl = iCntnr.getIComp("expensesummaryGrid","percentage") as PrLabelField;
			
			var profitCol:DataGridColumn = productSummaryGrid.getGridColumn("profit") as DataGridColumn;
			profitCol.itemRenderer = new ComboRenderer;
			
			var endDateRange:Object = new Object();
			endDateRange["rangeEnd"] = new Date;
			
			fromDateField = iCntnr.getIComp("expensesummaryGrid","from_date") as PrDateField;
			fromDateField.selectableRange =  endDateRange;
			fromDateField.addEventListener(CalendarLayoutChangeEvent.CHANGE, onFromDateChange);
			
			toDateField = iCntnr.getIComp("expensesummaryGrid","to_date") as PrDateField;
			toDateField.selectableRange =  endDateRange;
			toDateField.addEventListener(CalendarLayoutChangeEvent.CHANGE, onToDateChange);
			
			clearBtn = iCntnr.getIComp("expensesummaryGrid","clear_btn") as PrButton;
			
			clearBtn.enabled = false;
			
			clearBtn.addEventListener(MouseEvent.CLICK, function(evt:MouseEvent):void
			{
				fromDateField.selectedDate = null;
				fromDateField.text = "";
				toDateField.selectedDate = null;
				toDateField.text = "";
				clearBtn.enabled = false;
				updateGrid();
			}
			);
			
			var cntr:PrPopupBase = new PrPopupBase;
			cntr.isClosable = false;
			cntr.percentHeight = 100;
			cntr.percentWidth = 100;
			cntr.title = "Overall Summary";
			
			var vgroup:VGroup = new VGroup;
			vgroup.percentWidth = 100;
			vgroup.percentHeight = 100;
			vgroup.addElement(iCntnr as IVisualElement);
			
			cntr.addElement(vgroup);
			
			SharedConstants.getInstance().addToContainer(cntr);
			
			updateGrid();
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
			var asObject:Object = new Object;
			asObject['fromDate'] = fromDateField.selectedDate;
			asObject['toDate'] = toDateField.selectedDate;
			
			var reportMgmtGateway:RemoteObject = RemoteGateway.getInstance().getReportMgmtGateway();
			reportMgmtGateway.addEventListener(ResultEvent.RESULT,onProductSuccess);
			reportMgmtGateway.createProductSummaryView(asObject);
			
			var reportMgmtGateway1:RemoteObject = RemoteGateway.getInstance().getReportMgmtGateway();
			reportMgmtGateway1.addEventListener(ResultEvent.RESULT,onExpenseSuccess);
			reportMgmtGateway1.createExpenseSummaryView(asObject);
			
			var reportMgmtGateway2:RemoteObject = RemoteGateway.getInstance().getReportMgmtGateway();
			reportMgmtGateway2.addEventListener(ResultEvent.RESULT,onDetailsSuccess);
			reportMgmtGateway2.fetchOverallDetails(asObject);
		}
		
		private function onProductSuccess(event:ResultEvent):void
		{
			var sqlStr:String ="SELECT product_name,rate,purchased,cost_price,sold,sold_price,profit,available FROM product_summary_view";
			
			productSummaryGrid.sql = sqlStr;
			productSummaryGrid.navigationConfig.sqlString = sqlStr;
			productSummaryGrid.refreshGrid();
		}
		
		private function onExpenseSuccess(event:ResultEvent):void
		{
			var sqlStr:String ="SELECT expense,amount FROM expense_summary_view";
			
			expenseSummaryGrid.sql = sqlStr;
			expenseSummaryGrid.navigationConfig.sqlString = sqlStr;
			expenseSummaryGrid.refreshGrid();
		}
		
		private function onDetailsSuccess(event:ResultEvent):void
		{
			var obj:Object = event.result as Object;
			
			if(obj != null)
			{
				total_purchaseLbl.text = obj['total_purchase'];
				total_salesLbl.text = obj['total_sales'];
				total_expenseLbl.text = obj['total_expense'];
				profit_lossLbl.text = obj['profit_loss'];
				percentageLbl.text = obj['percentage'];
				
				total_purchaseLbl.setStyle("paddingTop",30);
				total_purchaseLbl.setStyle("color","#051e6d");
				total_salesLbl.setStyle("color","#07740c");
				total_expenseLbl.setStyle("color","#051e6d");
				
				if(obj['profit_loss'] != null && obj['profit_loss'].toString().indexOf("-") > -1)
				{
					profit_lossLbl.setStyle("color","#961609");
					percentageLbl.setStyle("color","#961609");
				}else
				{
					profit_lossLbl.setStyle("color","#07740c");
					percentageLbl.setStyle("color","#07740c");
				}
			}
		}
	}
}