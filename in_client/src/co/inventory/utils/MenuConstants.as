package co.inventory.utils
{
	/**
	 * Class contains constants for Main Menu
	 * 
	 * @author Prakash R
	 * 
	 * */
	
	import co.inventory.modules.expense.ExpenseManagement;
	import co.inventory.modules.expense.expensetype.ExpenseTypeManagement;
	import co.inventory.modules.home.HomePage;
	import co.inventory.modules.products.ProductManagement;
	import co.inventory.modules.products.producttype.ProductTypeManagement;
	import co.inventory.modules.purchase.PurchaseManagement;
	import co.inventory.modules.purchase.vendor.VendorManagement;
	import co.inventory.modules.report.ProductSummaryManagement;
	import co.inventory.modules.report.PurchaseSummaryManagement;
	import co.inventory.modules.report.SalesSummaryManagement;
	import co.inventory.modules.sales.SalesManagement;
	import co.inventory.modules.sales.customer.CustomerManagement;
	import co.pointred.fx.comp.collection.HashMap;

	public class MenuConstants
	{
		private static var INSTANCE:MenuConstants = null;
		private var hashOfContxtClass:HashMap = new HashMap();
		
		public static function getInstance():MenuConstants
		{
			if(INSTANCE == null)
				INSTANCE = new MenuConstants(new PrivateClass());
			return INSTANCE;
		}
		
		public function MenuConstants(privateClass:PrivateClass)
		{
			//get class name based on menu context
			loadHashOfCntxtClass();
		}
		
		//get class name based on menu context
		private function loadHashOfCntxtClass():void
		{
			hashOfContxtClass.put("home",HomePage);
			hashOfContxtClass.put("puchase",PurchaseManagement);
			hashOfContxtClass.put("vendor",VendorManagement);
			hashOfContxtClass.put("customer",CustomerManagement);
			hashOfContxtClass.put("sales",SalesManagement);
			hashOfContxtClass.put("product_type",ProductTypeManagement);
			hashOfContxtClass.put("product",ProductManagement);
			hashOfContxtClass.put("expense_type",ExpenseTypeManagement);
			hashOfContxtClass.put("expense",ExpenseManagement);
			hashOfContxtClass.put("purchase_report",PurchaseSummaryManagement);
			hashOfContxtClass.put("sales_report",SalesSummaryManagement);
			hashOfContxtClass.put("product_report",ProductSummaryManagement);
		}
		
		public function getHashOfCntxtClass():HashMap
		{
			return this.hashOfContxtClass;	
		}
		
		public const MAIN_MENU:String = "<menu>" +
			"<menuitem index='0' icon='' context='home' label='Home'/>" +
			"<menuitem index='' icon='subMenu' context='' label='Products'><menuitem index='' icon='' context='product_type' label='Product Type'/><menuitem index='' icon='' context='product' label='Product Management'/></menuitem>" +
			"<menuitem index='' icon='subMenu' context='' label='Expense'><menuitem index='' icon='' context='expense_type' label='Expense Type'/><menuitem index='' icon='' context='expense' label='Expense Management'/></menuitem>" +
			"<menuitem index='' icon='subMenu' context='' label='Purchase'><menuitem index='' icon='' context='vendor' label='Vendor Management'/><menuitem index='' icon='' context='puchase' label='Purchase Management'/></menuitem>" +
			"<menuitem index='' icon='subMenu' context='' label='Sales'><menuitem index='' icon='' context='customer' label='Customer Management'/><menuitem index='' icon='' context='sales' label='Sales Management'/></menuitem>" +
			"<menuitem index='' icon='subMenu' context='' label='Reports'><menuitem index='' icon='' context='purchase_report' label='Purchase Summary'/><menuitem index='' icon='' context='sales_report' label='Sales Summary'/><menuitem index='' icon='' context='product_report' label='Product Summary'/></menuitem>" +
			"</menu>";
			
		
	}
}

class PrivateClass
{
	/**
	 * Singleton Implementation
	 */
	public function PrivateClass()
	{
	}	
}