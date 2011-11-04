package com.ems.fx.comp.tab.components {
	
	import com.ems.fx.comp.tab.PrTabManager;
	import com.ems.fx.event.PrTabBarEvent;
	
	import flash.events.MouseEvent;
	
	import spark.components.Button;
	import spark.components.ButtonBarButton;
	
	[Event(name='closeTab',type='com.ems.fx.comp.tab.events.PrTabBarEvent')]
	
	public class PrTabBarButton extends ButtonBarButton {
		[SkinPart(required="false")]
		public var closeButton:Button;

		private var _closeable:Boolean = false;

		public function PrTabBarButton() {
			super();

			//NOTE: this enables the button's children (aka the close button) to receive mouse events
			this.mouseChildren = true;
		}

		[Bindable]
		public function get closeable():Boolean {
			return _closeable;
		}
		public function set closeable(val:Boolean):void {
			if (_closeable != val) {
				_closeable = val;
				closeButton.visible = val;
				labelDisplay.right = (val ? 30 : 14);
			}
		}

		private function closeHandler(e:MouseEvent):void {
			PrTabManager.getInstance().workSpaceContainer = null;		
			PrTabManager.getInstance().viewStack.selectedIndex = 0;
			dispatchEvent(new PrTabBarEvent(PrTabBarEvent.CLOSE_TAB, itemIndex, true));
		}

		override protected function partAdded(partName:String, instance:Object):void {
			super.partAdded(partName, instance);
			
			if (instance == closeButton) {
				closeButton.addEventListener(MouseEvent.CLICK, closeHandler);
				closeButton.visible = closeable;
			} else if (instance == labelDisplay) {
				labelDisplay.right = (closeable ? 30 : 14);
			}
		}

		override protected function partRemoved(partName:String, instance:Object):void {
			super.partRemoved(partName, instance);
			
			if (instance == closeButton) {
				closeButton.removeEventListener(MouseEvent.CLICK, closeHandler);
			}
		}
	}
}