package com.ems.fx.comp.tab.components {
	
	import com.ems.fx.comp.skins.PrTabBarSkin;
	import com.ems.fx.event.PrTabBarEvent;
	
	import mx.collections.IList;
	import mx.containers.ViewStack;
	import mx.controls.ButtonBar;
	
	import spark.components.TabBar;
	
	public class PrTabBar extends TabBar {
		public function PrTabBar() {
			super();
			this.setStyle("skinClass",Class(PrTabBarSkin));
		}

		public function setCloseableTab(index:int, value:Boolean):void {
			if (index >= 0 && index < dataGroup.numElements) {
				var btn:PrTabBarButton = dataGroup.getElementAt(index) as PrTabBarButton;
				btn.closeable = value;
			}
		}
		public function getCloseableTab(index:int):Boolean {
			if (index >= 0 && index < dataGroup.numElements) {
				var btn:PrTabBarButton = dataGroup.getElementAt(index) as PrTabBarButton;
				return btn.closeable;
			}
			return false;
		}

		private function closeHandler(e:PrTabBarEvent):void {
			closeTab(e.index, selectedIndex);
		}

		public function closeTab(closedTab:int, selectedTab:int):void {
			if (dataProvider.length == 0) return;
			
			if (dataProvider is IList) {
				dataProvider.removeItemAt(closedTab);
			} else if (dataProvider is ViewStack){
				//remove the entire child from the dataProvider, which also removes it from the ViewStack
				(dataProvider as ViewStack).removeChildAt(closedTab);
			}
			
			//adjust selectedIndex appropriately
			if (dataProvider.length == 0) {
				selectedIndex = -1;
			} else if (closedTab < selectedTab) {
				selectedIndex = selectedTab - 1;
			} else if (closedTab == selectedTab) {
				selectedIndex = (selectedTab == 0 ? 0 : selectedTab - 1);
			} else {
				selectedIndex = selectedTab;
			}
		}

		protected override function partAdded(partName:String, instance:Object):void {
			super.partAdded(partName, instance);

			if (instance == dataGroup) {
				dataGroup.addEventListener(PrTabBarEvent.CLOSE_TAB, closeHandler);
			}
		}

		protected override function partRemoved(partName:String, instance:Object):void {
			super.partRemoved(partName, instance);

			if (instance == dataGroup) {
				dataGroup.removeEventListener(PrTabBarEvent.CLOSE_TAB, closeHandler);
				
			}
		}
	}
}