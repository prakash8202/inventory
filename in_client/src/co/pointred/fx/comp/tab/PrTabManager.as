package com.ems.fx.comp.tab
{
	
	import com.ems.fx.comp.tab.components.IPrTab;
	import com.ems.fx.comp.tab.components.PrTab;
	import com.ems.fx.comp.tab.components.PrTabBar;
	import co.pointred.fx.comp.collection.HashMap;
	
	import mx.containers.ViewStack;
	import mx.core.IVisualElement;
	
	import spark.components.NavigatorContent;
	import spark.components.VGroup;
	import spark.layouts.VerticalLayout;

	public class PrTabManager extends VGroup
	{
		private static var INSTANCE:PrTabManager = null;
		
		[Bindable]
		public  var tabBar:PrTabBar = new PrTabBar();
		
		[Bindable]
		public  var workSpaceContainer:PrTab = null;
		
		[Bindable]
		public  var viewStack:ViewStack = new ViewStack();
		
		private var _hashOfTabs:HashMap = new HashMap();
		
		public function PrTabManager(privateClass:PrivateClass)
		{
			initializePrTabBar();
		}
		
		public function get hashOfTabs():HashMap
		{
			return _hashOfTabs;
		}

		public function set hashOfTabs(value:HashMap):void
		{
			_hashOfTabs = value;
		}

		public static function getInstance():PrTabManager
		{
			if(INSTANCE == null)
				INSTANCE = new PrTabManager(new PrivateClass());
			return INSTANCE;
		}	
		
		private function initializePrTabBar():void{
			this.percentHeight = 100;
			this.percentWidth = 100;
			viewStack.percentHeight = 100;
			viewStack.percentWidth =100;
			this.addElement(viewStack);
			this.addElement(tabBar);
			tabBar.dataProvider = viewStack;
			
		}
		
		public function addDefaultTab(iVisualElement:IPrTab,title:String,icon:Class=null):void
		{
			iVisualElement.initUi(title);
			var navigatorContent:PrTab = constructNavigatorContent(iVisualElement,title,icon);
			hashOfTabs.put(title,navigatorContent);
		}
		
		public function getTab(tabName:String):PrTab
		{
			return hashOfTabs.getValue(tabName) as PrTab;
		}
		
		private function constructNavigatorContent(iVisualElement:IPrTab,title:String,icon:Class=null):PrTab
		{
			var navigatorContent:PrTab = new PrTab();
			navigatorContent.layout = new VerticalLayout();
			navigatorContent.percentHeight = 100;
			navigatorContent.percentWidth = 100;
			navigatorContent.label = title;
			navigatorContent.icon = icon;
			navigatorContent.addElement(iVisualElement as IVisualElement);
			navigatorContent.tabContent = iVisualElement;
			viewStack.addElement(navigatorContent);
			return navigatorContent;
		}
		
		public function currentWorkspaceTab(iVisualElement:IPrTab,title:String,icon:Class=null):void{
			if(null == workSpaceContainer)
			{
				workSpaceContainer = constructNavigatorContent(iVisualElement,title,icon);
				tabBar.setCloseableTab(viewStack.length -1,true);				
			}
			else
			{
				workSpaceContainer.removeAllElements();
				workSpaceContainer.label = title;
				workSpaceContainer.icon = icon;
				workSpaceContainer.addElement(iVisualElement as IVisualElement);
			}
			tabBar.selectedIndex = viewStack.length;
		}
	}
}

class PrivateClass
{
	public function PrivateClass()
	{
	}	
}