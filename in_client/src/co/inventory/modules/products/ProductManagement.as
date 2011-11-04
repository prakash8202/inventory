package co.inventory.modules.products
{
	import co.inventory.pojo.Product;
	import co.inventory.pojo.ProductType;
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
	import co.pointred.fx.dataobjects.UserObject;
	import co.pointred.fx.rpc.RemoteGateway;
	
	import mx.collections.ArrayCollection;
	import mx.events.IndexChangedEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;
	
	import spark.components.VGroup;
	import spark.events.IndexChangeEvent;

	public class ProductManagement extends VGroup
	{
		include "ProductManagementSupport.as";
		
		private var iCntnr:ICntnr;
		private var xmlCompGen:XmlComponentGenerator = new XmlComponentGenerator();
		
		private var selectedGroup:Object;
		private var dataGrid:PrDataGrid;
		private var productTypeCombo:PrComboBox;
		private var selectedProductType:ProductType;
		
		public function ProductManagement()
		{
			PrEventDispatcher.registerForEvent("productmgmt.grid",uiCreated);
			xmlCompGen.buildComponents("xml/products/productmgmt.xml");
		}
		
		private function uiCreated(event:PrICntnrEvent):void
		{
			this.iCntnr = event.iCntnr;
			
			productTypeCombo = this.iCntnr.getIComp("formContainer", "product_type_id") as PrComboBox;
			productTypeCombo.labelField = "product_type";
			productTypeCombo.addEventListener(IndexChangeEvent.CHANGE,onProductTypeSelect);
			
			dataGrid = iCntnr.getIComp("productGrid","productList") as PrDataGrid;
			
			// add lsnr for ADD clicked
			dataGrid.addEventListener(PrDatagridEvent.ADD_CLICKED,gridAddBtnClicked);
			// add lsnr for MODIFY clicked
			dataGrid.addEventListener(PrDatagridEvent.EDIT_CLICKED,gridEditBtnClicked);
			//add lsnr for DELETE clicked
			dataGrid.addEventListener(PrDatagridEvent.DELETE_CLICKED,gridDeleteBtnClicked);
			
			PrPopupManager.showICntnrInPopup(iCntnr,"Product Management");
			
			updateProductTypeCombo();
		}
		
		public function gridAddBtnClicked(evt:PrDatagridEvent):void
		{
			if(productTypeCombo.selectedIndex > -1)
			{
				addProduct();
			}else
			{
				PrAlert.show("Select a Product Type to add Product",PrAlert.ERROR_MESSAGE);
			}
		}
		
		// Modify btn of Grid Clicked
		public function gridEditBtnClicked(evt:PrDatagridEvent):void
		{
			var selectedObj:Object=evt.dataObject;
			editProduct(selectedObj);
		}
		
		// Delete btn of Grid Clicked
		public function gridDeleteBtnClicked(evt:PrDatagridEvent):void
		{
			var selectedObj:Object=evt.dataObject;
			deleteProduct(selectedObj);
		}
		
		private function updateProductTypeCombo():void
		{
			var asObject:Object = new Object;
			var productMgmtGateway:RemoteObject = RemoteGateway.getInstance().getProductMgmtGateway();
			productMgmtGateway.addEventListener(ResultEvent.RESULT, onProductTypeFetch);
			productMgmtGateway.fetchProductTypes();
		}
		
		private function onProductTypeFetch(event:ResultEvent):void
		{
			var productTypeList:ArrayCollection = event.result as ArrayCollection;
			SharedConstants.getInstance().setDataProvider(productTypeCombo,productTypeList);
		}
		
		private function onProductTypeSelect(event:IndexChangeEvent):void
		{
			if(event.newIndex > 0)
			{
				var combo:PrComboBox = event.target as PrComboBox;
				selectedProductType = combo.selectedItem as ProductType;
				
				var sqlStr:String = "";
				sqlStr = "SELECT product_pid, product_name, rate, available, DATE_FORMAT(start_date,'%d %b %Y'),DATE_FORMAT(start_date,'%d-%m-%Y') as start_date_val FROM product WHERE product_type_id="+selectedProductType.product_type_pid;
				
				dataGrid.sql = sqlStr;
				dataGrid.navigationConfig.sqlString = sqlStr;
				dataGrid.refreshGrid();
			}
		}
	}
}