package co.inventory.modules.expense
{
	import co.inventory.pojo.ExpenseType;
	import co.inventory.utils.SharedConstants;
	import co.pointred.fx.comp.container.ICntnr;
	import co.pointred.fx.comp.core.PrAlert;
	import co.pointred.fx.comp.core.PrComboBox;
	import co.pointred.fx.comp.core.dataGrid.PrDataGrid;
	import co.pointred.fx.comp.event.PrDatagridEvent;
	import co.pointred.fx.comp.event.PrEventDispatcher;
	import co.pointred.fx.comp.event.PrICntnrEvent;
	import co.pointred.fx.comp.parser.XmlComponentGenerator;
	import co.pointred.fx.comp.resizabletw.PrPopupManager;
	import co.pointred.fx.rpc.RemoteGateway;
	
	import mx.collections.ArrayCollection;
	import mx.controls.DateField;
	import mx.events.CalendarLayoutChangeEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;
	
	import spark.components.VGroup;
	import spark.events.IndexChangeEvent;

	public class ExpenseManagement extends VGroup
	{
		include "ExpenseManagementSupport.as";
		
		private var iCntnr:ICntnr;
		private var xmlCompGen:XmlComponentGenerator = new XmlComponentGenerator();
		
		private var selectedGroup:Object;
		private var dataGrid:PrDataGrid;
		private var expenseTypeCombo:PrComboBox;
		private var selectedExpenseType:ExpenseType;
		
		private var fromDateField:PrDateField;
		private var toDateField:PrDateField;
		private var clearBtn:PrButton;
		
		public function ExpenseManagement()
		{
			PrEventDispatcher.registerForEvent("expensemgmt.grid",uiCreated);
			xmlCompGen.buildComponents("xml/expense/expensemgmt.xml");
		}
		
		private function uiCreated(event:PrICntnrEvent):void
		{
			this.iCntnr = event.iCntnr;
			
			expenseTypeCombo = this.iCntnr.getIComp("formContainer", "expense_type_id") as PrComboBox;
			expenseTypeCombo.labelField = "expense_type";
			expenseTypeCombo.addEventListener(IndexChangeEvent.CHANGE,onExpenseTypeSelect);
			
			dataGrid = iCntnr.getIComp("expenseGrid","expenseList") as PrDataGrid;
			
			// add lsnr for ADD clicked
			dataGrid.addEventListener(PrDatagridEvent.ADD_CLICKED,gridAddBtnClicked);
			// add lsnr for MODIFY clicked
			dataGrid.addEventListener(PrDatagridEvent.EDIT_CLICKED,gridEditBtnClicked);
			//add lsnr for DELETE clicked
			dataGrid.addEventListener(PrDatagridEvent.DELETE_CLICKED,gridDeleteBtnClicked);
			
			PrPopupManager.showICntnrInPopup(iCntnr,"Expense Management");
			
			var endDateRange:Object = new Object();
			endDateRange["rangeEnd"] = new Date; 
			
			fromDateField = iCntnr.getIComp("formContainer","from_date") as PrDateField;
			fromDateField.selectableRange =  endDateRange;
			fromDateField.addEventListener(CalendarLayoutChangeEvent.CHANGE, onFromDateChange);
			
			toDateField = iCntnr.getIComp("formContainer","to_date") as PrDateField;
			toDateField.selectableRange =  endDateRange;
			toDateField.addEventListener(CalendarLayoutChangeEvent.CHANGE, onToDateChange);
			
			clearBtn = iCntnr.getIComp("formContainer","clear_btn") as PrButton;
			
			fromDateField.enabled = false;
			toDateField.enabled = false;
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
			
			updateExpenseTypeCombo();
		}
		
		public function gridAddBtnClicked(evt:PrDatagridEvent):void
		{
			if(expenseTypeCombo.selectedIndex > -1)
			{
				addExpense();
			}else
			{
				PrAlert.show("Select a Expense Type to add Expense",PrAlert.ERROR_MESSAGE);
			}
		}
		
		// Modify btn of Grid Clicked
		public function gridEditBtnClicked(evt:PrDatagridEvent):void
		{
			var selectedObj:Object=evt.dataObject;
			editExpense(selectedObj);
		}
		
		// Delete btn of Grid Clicked
		public function gridDeleteBtnClicked(evt:PrDatagridEvent):void
		{
			var selectedObj:Object=evt.dataObject;
			deleteExpense(selectedObj);
		}
		
		private function updateExpenseTypeCombo():void
		{
			var asObject:Object = new Object;
			var expenseMgmtGateway:RemoteObject = RemoteGateway.getInstance().getExpenseMgmtGateway();
			expenseMgmtGateway.addEventListener(ResultEvent.RESULT, onExpenseTypeFetch);
			expenseMgmtGateway.fetchExpenseTypes();
		}
		
		private function onExpenseTypeFetch(event:ResultEvent):void
		{
			var expenseTypeList:ArrayCollection = event.result as ArrayCollection;
			SharedConstants.getInstance().setDataProvider(expenseTypeCombo,expenseTypeList);
		}
		
		private function onExpenseTypeSelect(event:IndexChangeEvent):void
		{
			if(event.newIndex > 0)
			{
				fromDateField.enabled = true;
				toDateField.enabled = true;
				updateGrid();
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
			selectedExpenseType = expenseTypeCombo.selectedItem as ExpenseType;
			
			var sqlStr:String = "";
			sqlStr = "SELECT expense_pid, DATE_FORMAT(expense_date,'%d %b %Y'), amount FROM expense WHERE expense_type_id="+selectedExpenseType.expense_type_pid;
			
			var from:String = ""
			var to:String = ""
			var timeStr:String = " 00:00:00";
			
			if(fromDateField.selectedDate != null && toDateField.selectedDate == null)
			{
				from = DateField.dateToString(fromDateField.selectedDate, "YYYY-MM-DD") + timeStr;
				sqlStr = sqlStr + " AND expense_date >= '" + from + "'";
			}else if(fromDateField.selectedDate == null && toDateField.selectedDate != null)
			{
				to = DateField.dateToString(toDateField.selectedDate, "YYYY-MM-DD") + timeStr;
				sqlStr = sqlStr + " AND expense_date <= '" + to + "'";
			}else if(fromDateField.selectedDate != null && toDateField.selectedDate != null)
			{
				if(fromDateField.selectedDate.getTime() > toDateField.selectedDate.getTime())
				{
					PrAlert.show("From Date should be less than To date",PrAlert.WARNING_MESSAGE);
				}else
				{
					from = DateField.dateToString(fromDateField.selectedDate, "YYYY-MM-DD") + timeStr; 
					to = DateField.dateToString(toDateField.selectedDate, "YYYY-MM-DD") + timeStr;
					
					sqlStr = sqlStr + " AND expense_date BETWEEN '" + from + "' AND '" + to + "'";
				}
			}
			
			sqlStr = sqlStr + " ORDER BY expense_date DESC"
			
			dataGrid.sql = sqlStr;
			dataGrid.navigationConfig.sqlString = sqlStr;
			dataGrid.refreshGrid();
		}
	}
}