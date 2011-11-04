package co.pointred.fx.comp.core.tree
{
	import com.ems.userobjects.ProjectConstants;
	
	import flash.events.MouseEvent;
	
	import mx.controls.Image;
	import mx.controls.treeClasses.TreeItemRenderer;
	
	public class PrTreeItemRenderer extends TreeItemRenderer
	{
		[Bindable]
		public var rightClicker:Image = new Image();
		
		private var currentTreeItem:Object;
		
		[Embed("assets/navig/rightClick.png")]
		public static const rC:Class;
		
		public function PrTreeItemRenderer()
		{
			super();
//			mouseEnabled = false;
		}
		
		override protected function createChildren():void
		{
			super.createChildren();
			rightClicker = new Image();
			rightClicker.source = rC;
			rightClicker.addEventListener(MouseEvent.CLICK, rightClickHandler);
			this.addChild(rightClicker);
		}
		
		
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			this.rightClicker.visible = false;
			this.rightClicker.width = 15;
			this.rightClicker.height = 15;
			this.rightClicker.x = super.label.textWidth + super.label.x + 5;
		}
		
		private function rightClickHandler(evt:MouseEvent):void
		{
			
		}
		
		public function handleRollOver(currentItem:Object):void
		{
				if(ProjectConstants.currentTreeItemRenderer != null)
				{
					ProjectConstants.currentTreeItemRenderer.handleRollOut();	
				}
				ProjectConstants.currentTreeItemRenderer = this;
				this.rightClicker.visible = true;
				this.currentTreeItem = currentItem;
		}
		
		public function handleRollOut():void
		{
			// this.rightClicker.visible = false;
		}
	}
}