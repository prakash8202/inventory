package co.pointred.fx.comp.core.dataGrid
{
	import spark.components.Button;
	import spark.primitives.BitmapImage;
	
	
	public class IconButton extends Button
	{
		//--------------------------------------------------------------------------
		//
		//    Constructor
		//
		//--------------------------------------------------------------------------
		
		public function IconButton()
		{
			super();
		}
		
		//--------------------------------------------------------------------------
		//
		//    Properties
		//
		//--------------------------------------------------------------------------
		
		//----------------------------------
		//  icon
		//----------------------------------
		
		/**
		 *  @private
		 *  Internal storage for the icon property.
		 */
		[Bindable]
		private var _icon:Class;
		
		[Bindable]
		private var _operation:String;
		
		/**
		 *  
		 */
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
		
		/**
		 *  
		 */
		[Bindable]
		public function get operation():String
		{
			return _operation;
		}
		
		/**
		 *  @private
		 */
		public function set operation(val:String):void
		{
			_operation = val;
		}
		
		//--------------------------------------------------------------------------
		//
		//  Skin Parts
		//
		//--------------------------------------------------------------------------
		
		[SkinPart("false")]
		public var iconElement:BitmapImage;
		
		
		//--------------------------------------------------------------------------
		//
		//  Overridden methods
		//
		//--------------------------------------------------------------------------
		
		/**
		 *  @private
		 */
		override protected function partAdded(partName:String, instance:Object):void
		{
			super.partAdded(partName, instance);
			
			if (icon !== null && instance == iconElement)
				iconElement.source = icon;
		}
	}
}