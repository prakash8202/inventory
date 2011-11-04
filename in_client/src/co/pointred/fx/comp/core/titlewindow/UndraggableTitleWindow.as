package co.pointred.fx.comp.core.titlewindow
{
	import co.pointred.fx.comp.container.skins.UnDraggableTitleWindowSkin;
	
	import flash.events.MouseEvent;
	import flash.geom.Rectangle;
	
	import mx.containers.TitleWindow;
	import mx.events.CloseEvent;
	import mx.events.MoveEvent;
	import mx.managers.PopUpManager;
	
	import spark.events.TitleWindowBoundsEvent;
	
	public class UndraggableTitleWindow extends TitleWindow
	{
		[Bindable]
		private var _isDraggable:Boolean = true;
		
		public function UndraggableTitleWindow()
		{
			super();
			this.isPopUp = false;
			this.showCloseButton = false;
			this.setStyle("skinClass", Class(UnDraggableTitleWindowSkin));
			this.addEventListener(TitleWindowBoundsEvent.WINDOW_MOVING , windowMovingHandler);
			this.addEventListener(CloseEvent.CLOSE, closePrTitleWindow);
		}
		
		public function set isDraggable(_isDraggable:Boolean):void
		{
			this._isDraggable = _isDraggable;
			this.isPopUp = _isDraggable;
		}
		
		public function windowMovingHandler(evt:TitleWindowBoundsEvent):void
		{
			if( false == _isDraggable)
			{
				evt.stopImmediatePropagation();
				evt.preventDefault();
			}			
		}
		
		public function closePrTitleWindow(evt:CloseEvent):void
		{
			PopUpManager.removePopUp(this);
		}
		public function closePrPopup():void
		{
			PopUpManager.removePopUp(this);
		}
	
//		protected override function startDragging(event:MouseEvent):void {
//			//isPopUp = this._isDraggable;
//		}
	}
}