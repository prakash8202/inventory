package co.inventory.modules.purchase.vendor
{
	import co.pointred.fx.comp.container.ICntnr;
	import co.pointred.fx.comp.core.dataGrid.PrDataGrid;
	import co.pointred.fx.comp.event.PrDatagridEvent;
	import co.pointred.fx.comp.event.PrEventDispatcher;
	import co.pointred.fx.comp.event.PrICntnrEvent;
	import co.pointred.fx.comp.parser.XmlComponentGenerator;
	import co.pointred.fx.comp.resizabletw.PrPopupManager;
	
	import spark.components.VGroup;

	public class VendorManagement extends VGroup
	{
		private var iCntnr:ICntnr;
		private var xmlCompGen:XmlComponentGenerator = new XmlComponentGenerator();
		
		private var selectedGroup:Object;
		private var dataGrid:PrDataGrid;
		
		public function VendorManagement()
		{
			PrEventDispatcher.registerForEvent("vendormgmt.grid",uiCreated);
			xmlCompGen.buildComponents("xml/purchase/vendor/vendormgmt.xml");
		}
		
		private function uiCreated(event:PrICntnrEvent):void
		{
			this.iCntnr = event.iCntnr;
			
			dataGrid = iCntnr.getIComp("vendorGrid","vendorList") as PrDataGrid;
			
			// add lsnr for ADD clicked
			dataGrid.addEventListener(PrDatagridEvent.ADD_CLICKED,gridAddBtnClicked);
			// add lsnr for MODIFY clicked
			dataGrid.addEventListener(PrDatagridEvent.EDIT_CLICKED,gridEditBtnClicked);
			//add lsnr for DELETE clicked
			dataGrid.addEventListener(PrDatagridEvent.DELETE_CLICKED,gridDeleteBtnClicked);
			
			PrPopupManager.showICntnrInPopup(iCntnr,"Vendor Management");
		}
		
		public function gridAddBtnClicked(evt:PrDatagridEvent):void
		{
			var vendorMgmt:VendorMgmt = new VendorMgmt(this);
			vendorMgmt.addVendor();
			vendorMgmt.parentGrid = dataGrid;
		}
		
		// Modify btn of Grid Clicked
		public function gridEditBtnClicked(evt:PrDatagridEvent):void
		{
			var selectedObj:Object=evt.dataObject;
			var vendorMgmt:VendorMgmt = new VendorMgmt(this);
			vendorMgmt.editVendor(selectedObj);
			vendorMgmt.parentGrid = dataGrid;
		}
		
		// Delete btn of Grid Clicked
		public function gridDeleteBtnClicked(evt:PrDatagridEvent):void
		{
			var selectedObj:Object=evt.dataObject;
			var vendorMgmt:VendorMgmt = new VendorMgmt(this);
			vendorMgmt.deleteVendor(selectedObj);
			vendorMgmt.parentGrid = dataGrid;
		}
	}
}