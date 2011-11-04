package co.pointred.fx.comp.resizabletw
{
	import co.pointred.fx.comp.utils.AssetsLibrary;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import mx.managers.PopUpManager;
	import spark.components.HGroup;
	import spark.components.Panel;
	import spark.primitives.BitmapImage;
	
	[Event(name="ClosethisContainer", type="flash.events.Event")]
	
	public class PrPopupBase extends Panel
	{
		[SkinPart("false")]
		public var _closer:BitmapImage;
		[SkinPart("true")]
		public var buttonHolder:HGroup;
		public var isDraggable:Boolean = false;
		public var isClosable:Boolean = false;
		
		[Bindable]
		private var _icon:Class;
		
		[SkinPart("false")]
		public var iconElement:BitmapImage;
		
		public function PrPopupBase()
		{
			super();
//			_closer.useHandCursor = false;
		}
		
		override public function stylesInitialized():void {
			super.stylesInitialized();
			this.setStyle("skinClass",Class(PrPopupBaseSkin));
		}
		
		protected override function createChildren():void
		{
			
			super.createChildren();
			
//			super.titleBar.addEventListener(MouseEvent.MOUSE_DOWN,handleDown);
//			super.titleBar.addEventListener(MouseEvent.MOUSE_UP,handleUp);
//			super.titleBar.addChild(_closer);
		}
		
		protected override function updateDisplayList(unscaledWidth:Number,
													  unscaledHeight:Number):void{
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			
//			_closer.width = 22;
//			_closer.height = 26;
//			_closer.x = super.titleBar.width - _closer.width - 8;
//			_closer.y = 4;
			
		}
		
		[Bindable]
		public function get icon():Class
		{
			return _icon;
		}
		
		/**
		 *  @private
		 */
		public function set icon(val:Class):void
		{
			_icon = val;
			
			if (iconElement != null)
				iconElement.source = _icon;
		}
		
		private function handleDown(e:Event):void{
			if(isDraggable)
				this.startDrag();
		}
		private function handleUp(e:Event):void{
			if(isDraggable)
				this.stopDrag();
		}
		
		
		protected override function measure():void{
			super.measure();
			//short circuit the component to add a default height and width
			//in case we forget to put a height and widht on the component.
			
			//			measuredMinHeight = measuredHeight = DEFAULT_HEIGHT;
			//			measuredMinWidth  = measuredWidth  = DEFAULT_WIDTH;
			
		}
		
		override protected function partAdded(partName:String, instance:Object):void
		{
			if(instance == _closer && true == isClosable)
			{
				_closer.source = AssetsLibrary.close_button;
			}
			if(instance == buttonHolder)
			{
				buttonHolder.addEventListener(MouseEvent.CLICK,RemoveThisContainer);
			}
			
			if (icon !== null && instance == iconElement)
			{
				iconElement.source = icon;
			}
			
			super.partAdded(partName, instance);
			
		}
		
		
		
		protected function RemoveThisContainer(event:MouseEvent):void
		{
			//let the application know we are ready to be removed.
//			var eventObj:Event = new Event("ClosethisContainer");
//			dispatchEvent(eventObj); 
			close();
		}
		
		public function close():void
		{
			PopUpManager.removePopUp(this);
		}
	}
}