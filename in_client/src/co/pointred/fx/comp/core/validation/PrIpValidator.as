package co.pointred.fx.comp.core.validation
{
	import mx.events.ValidationResultEvent;
	import mx.validators.RegExpValidator;
	
	/**
	 * Custom Validator for IP Address Validation 
	 **/
	public class PrIpValidator extends RegExpValidator implements IValidator
	{
		private var validationErrors:String="";
		
		public function PrIpValidator()
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
			return result;
		}
		
		override public function validate(value:Object=null, suppressEvents:Boolean=false):ValidationResultEvent
		{
			super.noMatchError="Invalid IP Address";
			// Regex Expression for IP ADDRESS
			// super.expression="^(?:(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$";
			super.expression = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";;
			return super.validate(value, suppressEvents);
		}
	}
}