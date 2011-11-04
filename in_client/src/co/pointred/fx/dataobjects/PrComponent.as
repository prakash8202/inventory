package co.pointred.fx.dataobjects
{
	import mx.core.IVisualElement;

	public class PrComponent
	{
		private var _component:IVisualElement;
		private var _validator:String;
		private var _required:Boolean;
		private var _params:String;
		private var _property:String;
		
		public function PrComponent(component:IVisualElement, validator:String, property:String="text",params:String = "", required:Boolean = false)
		{
			this.component = component;
			this.validator = validator;
			this.params = params;
			this.required = required;
			this.property = property;
		}

		public function get component():IVisualElement
		{
			return _component;
		}

		public function set component(value:IVisualElement):void
		{
			_component = value;
		}

		public function get validator():String
		{
			return _validator;
		}

		public function set validator(value:String):void
		{
			_validator = value;
		}

		public function get required():Boolean
		{
			return _required;
		}

		public function set required(value:Boolean):void
		{
			_required = value;
		}

		public function get params():String
		{
			return _params;
		}

		public function set params(value:String):void
		{
			_params = value;
		}

		public function get property():String
		{
			return _property;
		}

		public function set property(value:String):void
		{
			_property = value;
		}
	}
}