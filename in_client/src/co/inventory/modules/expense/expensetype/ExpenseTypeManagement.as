package co.inventory.modules.expense.expensetype
{
	import co.pointred.fx.comp.container.ICntnr;
	import co.pointred.fx.comp.core.PrAlert;
	import co.pointred.fx.comp.core.dataGrid.PrDataGrid;
	import co.pointred.fx.comp.event.PrDatagridEvent;
	import co.pointred.fx.comp.event.PrEventDispatcher;
	import co.pointred.fx.comp.event.PrICntnrEvent;
	import co.pointred.fx.comp.parser.XmlComponentGenerator;
	import co.pointred.fx.comp.resizabletw.PrPopupManager;
	
	import spark.components.VGroup;

	public class ExpenseTypeManagement extends VGroup
	{
		private var iCntnr:ICntnr;
		private var xmlCompGen:XmlComponentGenerator = new XmlComponentGenerator();
		
		private var selectedGroup:Object;
		private var dataGrid:PrDataGrid;
		
		public function ExpenseTypeManagement()
		{
			PrEventDispatcher.registerForEvent("expensetypemgmt.grid",uiCreated);
			xmlCompGen.buildComponents("xml/expense/expensetype/expensetypemgmt.xml");
		}
		
		private function uiCreated(event:PrICntnrEvent):void
		{
			this.iCntnr = event.iCntnr;
			
			dataGrid = iCntnr.getIComp("expensetypeGrid","expensetypeList") as PrDataGrid;
			
			// add lsnr for ADD clicked
			dataGrid.addEventListener(PrDatagridEvent.ADD_CLICKED,gridAddBtnClicked);
			// add lsnr for MODIFY clicked
			dataGrid.addEventListener(PrDatagridEvent.EDIT_CLICKED,gridEditBtnClicked);
			//add lsnr for DELETE clicked
			dataGrid.addEventListener(PrDatagridEvent.DELETE_CLICKED,gridDeleteBtnClicked);
			
			PrPopupManager.showICntnrInPopup(iCntnr,"Expense Type Management");
		}
		
		public function gridAddBtnClicked(evt:PrDatagridEvent):void
		{
			var expenseTypeMgmt:ExpenseTypeMgmt = new ExpenseTypeMgmt(this);
			expenseTypeMgmt.addExpenseType();
			expenseTypeMgmt.parentGrid = dataGrid;
		}
		
		// Modify btn of Grid Clicked
		public function gridEditBtnClicked(evt:PrDatagridEvent):void
		{
			var selectedObj:Object=evt.dataObject;
			var expenseTypeMgmt:ExpenseTypeMgmt = new ExpenseTypeMgmt(this);
			expenseTypeMgmt.editExpenseType(selectedObj);
			expenseTypeMgmt.parentGrid = dataGrid;
		}
		
		// Delete btn of Grid Clicked
		public function gridDeleteBtnClicked(evt:PrDatagridEvent):void
		{
			var selectedObj:Object=evt.dataObject;
			var expenseTypeMgmt:ExpenseTypeMgmt = new ExpenseTypeMgmt(this);
			expenseTypeMgmt.deleteExpenseType(selectedObj);
			expenseTypeMgmt.parentGrid = dataGrid;
		}
	}
}