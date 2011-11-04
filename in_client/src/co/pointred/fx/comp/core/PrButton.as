package co.pointred.fx.comp.core
{
	import co.pointred.fx.comp.core.validation.IValidator;
	
	import spark.components.Button;
	
	public class PrButton extends Button implements IComp
	{
		private var attrName:String;
		
		public function PrButton()
		{
			super();
		}
		public function getAttrName():String
		{
			return this.attrName;
		}
		
		public function getAttrValue():Object
		{
			return super.label;
		}
		
		public function setAttrName(attrName:String):void
		{
			this.attrName=attrName;
		}
		
		public function setAttrValue(attrValue:Object):void
		{
			super.label=attrValue.toString();
		}
		
		public function getValidationError():Object
		{
			return null;
		}
		
		public function doValidate():Boolean
		{
			return true;
		}
		
		public function resetValidationFlag():void
		{
			// do nothing
		}
		
		public function setRetainPreviousValue(retainPrevValue:Boolean):void
		{
			//do nothing
		}
		
		public function isRetainPreviousValue():Boolean
		{
			return false;
		}
		
		public function getPreviousValue():Object
		{
			return null;	
		}
		
		public function getValueForDb(objToInject:Object):Object
		{
			return objToInject;
		}
		
		public function resetAttrVal():void
		{
			//do nothing
		}
		
		public function getValidator():IValidator
		{
			return null;
		}
		

	}
}