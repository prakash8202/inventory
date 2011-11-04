package co.pointred.fx.comp.core.validation
{
	import mx.events.ValidationResultEvent;
	import mx.validators.RegExpValidator;
	
	/**
	 * Custom Validator for MAC Address Validation 
	 **/
	public class PrMacValidator extends RegExpValidator implements IValidator
	{
		private var validationErrors:String = "";;
		public function PrMacValidator()
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
			//			var macAddr:String = objFromSrc + ""; 
			//			if (macAddr.length > 0)
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
			super.noMatchError = "Invalid Mac Address\n MAC Format is 11:AA:22:BB:33:CC";
			super.expression = "^[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}$";
			return super.validate(value, suppressEvents);
		}
	}
}