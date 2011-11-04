package co.pointred.fx.comp.core.fieldset
{
	import mx.containers.Form;
	import mx.core.IVisualElement;
	
	import spark.components.SkinnableContainer;
	import spark.components.supportClasses.TextBase;
	import spark.layouts.VerticalLayout;

	public class PrFieldSet extends SkinnableContainer
	{
		public function PrFieldSet()
		{
			super();
			this.setStyle("skinClass",Class(PrFieldSetSkin));
			this.layout = new VerticalLayout();
		}
		
		[SkinPart(required="false")]
		
		/**
		 *  The skin part that defines the appearance of the 
		 *  title text in the container.
		 *
		 *  @see spark.skins.spark.PanelSkin
		 *  
		 *  @langversion 3.0
		 *  @playerversion Flash 10
		 *  @playerversion AIR 1.5
		 *  @productversion Flex 4
		 */
		public var titleDisplay:TextBase;
		
		
		/**
		 *  @private
		 */
		override public function get baselinePosition():Number
		{
			return getBaselinePositionForPart(titleDisplay);
		} 
		
		
		private var _title : String;
		
		[Bindable]
		
		public function get title():String
		{
			return _title;
		}
		
		public function set title(value:String):void
		{
			_title = value;
			if (titleDisplay)
				titleDisplay.text = title;
		}
		
		/**
		 *  @private
		 */
		override protected function partAdded(partName:String, instance:Object):void
		{
			super.partAdded(partName, instance);
			
			if (instance == titleDisplay)
			{
				titleDisplay.text = title;
			}
		}
		
	}
}