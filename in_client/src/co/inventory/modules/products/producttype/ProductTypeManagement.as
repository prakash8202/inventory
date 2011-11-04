package co.inventory.modules.products.producttype
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

	public class ProductTypeManagement extends VGroup
	{ 
		private var iCntnr:ICntnr;
		private var xmlCompGen:XmlComponentGenerator = new XmlComponentGenerator();
		
		private var selectedGroup:Object;
		private var dataGrid:PrDataGrid;

		
		public function ProductTypeManagement()
		{
			PrEventDispatcher.registerForEvent("producttypemgmt.grid",uiCreated);
			xmlCompGen.buildComponents("xml/products/producttype/producttypemgmt.xml");
		}
		
		private function uiCreated(event:PrICntnrEvent):void
		{
			this.iCntnr = event.iCntnr;
			
			dataGrid = iCntnr.getIComp("producttypeGrid","producttypeList") as PrDataGrid;
			
			// add lsnr for ADD clicked
			dataGrid.addEventListener(PrDatagridEvent.ADD_CLICKED,gridAddBtnClicked);
			// add lsnr for MODIFY clicked
			dataGrid.addEventListener(PrDatagridEvent.EDIT_CLICKED,gridEditBtnClicked);
			//add lsnr for DELETE clicked
			dataGrid.addEventListener(PrDatagridEvent.DELETE_CLICKED,gridDeleteBtnClicked);
			
			PrPopupManager.showICntnrInPopup(iCntnr,"Product Type Management");
		}
		
		public function gridAddBtnClicked(evt:PrDatagridEvent):void
		{
			var productTypeMgmt:ProductTypeMgmt = new ProductTypeMgmt(this);
			productTypeMgmt.addProductType();
			productTypeMgmt.parentGrid = dataGrid;
		}
		
		// Modify btn of Grid Clicked
		public function gridEditBtnClicked(evt:PrDatagridEvent):void
		{
			var selectedObj:Object=evt.dataObject;
			var productTypeMgmt:ProductTypeMgmt = new ProductTypeMgmt(this);
			productTypeMgmt.editProductType(selectedObj);
			productTypeMgmt.parentGrid = dataGrid;
		}
		
		// Delete btn of Grid Clicked
		public function gridDeleteBtnClicked(evt:PrDatagridEvent):void
		{
			var selectedObj:Object=evt.dataObject;
			var productTypeMgmt:ProductTypeMgmt = new ProductTypeMgmt(this);
			productTypeMgmt.deleteProductType(selectedObj);
			productTypeMgmt.parentGrid = dataGrid;
		}


	}
}