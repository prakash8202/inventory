package co.pointred.fx.comp.core
{
	import co.pointred.fx.comp.core.validation.IValidator;

	/**
	 * The generic interface implemented by all the Non-Container level UI Components
	 * */
	
	public interface IComp extends IValidator
	{
		function getAttrName():String;
		function getAttrValue():Object;
		function setAttrName(attrName:String):void;
		function setAttrValue(attrValue:Object):void;
		
		/**
		 * resets the validation flag to true
		 * */
		function resetValidationFlag():void;
		
		/**
		 * api to set whether to retain the previous value
		 * */
		
		function setRetainPreviousValue(retainPrevValue:Boolean):void;
		
		/**
		 * A Boolean flag to say whether previous value is retained
		 * */
		function isRetainPreviousValue():Boolean;
		/**
		 * returns the previous value that was stored in the IComp.  Updated while calling setAttrValue()
		 * */
		function getPreviousValue():Object;
		/**
		 * appends the key and Value to the incoming Object. If isRetainPreviousValue() is true, then getPreviousValue() is invoked and appended to the incoming Object with key as getAttrName() + _previous_value
		 * */
		function getValueForDb(objToInject:Object):Object
		
		function resetAttrVal():void;
		
		function getValidator():IValidator;
		
	}
}