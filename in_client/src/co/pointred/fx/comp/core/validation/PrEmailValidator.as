package co.pointred.fx.comp.core.validation
{
	import mx.events.ValidationResultEvent;
	import mx.validators.EmailValidator;
	
	/**
	 * Custom Validator for EMail Address Validation 
	 **/
	public class PrEmailValidator extends EmailValidator implements IValidator
	{
		private var validationErrors:String = "";
		public function PrEmailValidator()
		{
			super();
		}
		
		public function getValidationError():Object
		{
			return this.validationErrors;
		}
		
		public function doValidate():Boolean
		{
			var result:Boolean=false;
			// Required Check is done at a higher level .. no need to check here
			this.required = false;
			validationErrors= ""; 
			// Get Text from source[TextField]
			var objFromSrc:Object=getValueFromSource();
			var resultEvent:ValidationResultEvent=validate(objFromSrc, false);
			//			var str:String = objFromSrc + ""; 
			//			if (str.length > 0)
			//			{
			if(ValidationResultEvent.INVALID == resultEvent.type)
			{
				// If Validation Failed
				validationErrors=resultEvent.message;
				result = false;
			}
			else
			{
				result = true;
			}
			//			}
			return result;
		}
		
		override public function validate(value:Object=null, suppressEvents:Boolean=false):ValidationResultEvent
		{	
			var av:ValidationResultEvent = super.validate(value, suppressEvents);
			return super.validate(value, suppressEvents);
		}
		
		
	}
}