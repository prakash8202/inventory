package co.inventory.modules.sales.customer
{
	import co.pointred.fx.comp.container.ICntnr;
	import co.pointred.fx.comp.core.dataGrid.PrDataGrid;
	import co.pointred.fx.comp.event.PrDatagridEvent;
	import co.pointred.fx.comp.event.PrEventDispatcher;
	import co.pointred.fx.comp.event.PrICntnrEvent;
	import co.pointred.fx.comp.parser.XmlComponentGenerator;
	import co.pointred.fx.comp.resizabletw.PrPopupManager;
	
	import spark.components.VGroup;

	public class CustomerManagement extends VGroup
	{
		private var iCntnr:ICntnr;
		private var xmlCompGen:XmlComponentGenerator = new XmlComponentGenerator();
		
		private var selectedGroup:Object;
		private var dataGrid:PrDataGrid;
		
		public function CustomerManagement()
		{
			PrEventDispatcher.registerForEvent("customermgmt.grid",uiCreated);
			xmlCompGen.buildComponents("xml/sales/customer/customermgmt.xml");
		}
		
		private function uiCreated(event:PrICntnrEvent):void
		{
			this.iCntnr = event.iCntnr;
			
			dataGrid = iCntnr.getIComp("customerGrid","customerList") as PrDataGrid;
			
			// add lsnr for ADD clicked
			dataGrid.addEventListener(PrDatagridEvent.ADD_CLICKED,gridAddBtnClicked);
			// add lsnr for MODIFY clicked
			dataGrid.addEventListener(PrDatagridEvent.EDIT_CLICKED,gridEditBtnClicked);
			//add lsnr for DELETE clicked
			dataGrid.addEventListener(PrDatagridEvent.DELETE_CLICKED,gridDeleteBtnClicked);
			
			PrPopupManager.showICntnrInPopup(iCntnr,"Customer Management");
		}
		
		public function gridAddBtnClicked(evt:PrDatagridEvent):void
		{
			var customerMgmt:CustomerMgmt = new CustomerMgmt(this);
			customerMgmt.addCustomer();
			customerMgmt.parentGrid = dataGrid;
		}
		
		// Modify btn of Grid Clicked
		public function gridEditBtnClicked(evt:PrDatagridEvent):void
		{
			var selectedObj:Object=evt.dataObject;
			var customerMgmt:CustomerMgmt = new CustomerMgmt(this);
			customerMgmt.editCustomer(selectedObj);
			customerMgmt.parentGrid = dataGrid;
		}
		
		// Delete btn of Grid Clicked
		public function gridDeleteBtnClicked(evt:PrDatagridEvent):void
		{
			var selectedObj:Object=evt.dataObject;
			var customerMgmt:CustomerMgmt = new CustomerMgmt(this);
			customerMgmt.deleteCustomer(selectedObj);
			customerMgmt.parentGrid = dataGrid;
		}
	}
}