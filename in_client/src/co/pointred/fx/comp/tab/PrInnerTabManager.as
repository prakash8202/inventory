package co.pointred.fx.comp.tab
{
	import co.pointred.fx.comp.tab.components.PrTab;
	
	import mx.containers.ViewStack;
	import mx.core.IVisualElement;
	
	import spark.components.HGroup;
	import spark.components.TabBar;
	import spark.layouts.VerticalLayout;
	
	public class PrInnerTabManager extends HGroup
	{
		private static var INSTANCE:PrInnerTabManager = null;
		
		
		[Bindable]
		public var tabBar:TabBar;
		
		[Bindable]
		public  var viewStack:ViewStack = new ViewStack();
		
		public function PrInnerTabManager(privateClass:PrivateClass)
		{
			initializePrInnerTabBar();
		}
		
		private function initializePrInnerTabBar():void{
			this.tabBar = new TabBar();
			tabBar.rotation=90;
			tabBar.height = 16;
			this.percentHeight = 100;
			this.percentWidth = 100;
			viewStack.percentHeight = 100;
			viewStack.percentWidth =100;
			this.addElement(tabBar);
			this.addElement(viewStack);
			this.tabBar.dataProvider = viewStack;
		}
		
		public function addDefaultTab(iVisualElement:IVisualElement,title:String,icon:Class=null):void
		{
			var navigatorContent:PrTab = constructNavigatorContent(iVisualElement,title,icon);
		}
		
		private function constructNavigatorContent(iVisualElement:IVisualElement,title:String,icon:Class=null):PrTab
		{
			var navigatorContent:PrTab = new PrTab();
			navigatorContent.layout = new VerticalLayout();
			navigatorContent.percentHeight = 100;
			navigatorContent.percentWidth = 100;
			navigatorContent.label = title;
			navigatorContent.icon = icon;
			navigatorContent.addElement(iVisualElement);
			viewStack.addElement(navigatorContent);
			return navigatorContent;
		}
		
		public static function getInstance():PrInnerTabManager
		{
			if(INSTANCE == null)
				INSTANCE = new PrInnerTabManager(new PrivateClass());
			return INSTANCE;
		}	
		
	}
}

class PrivateClass
{
	public function PrivateClass()
	{
	}	
}