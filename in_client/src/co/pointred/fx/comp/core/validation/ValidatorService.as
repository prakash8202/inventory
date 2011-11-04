
package co.pointred.fx.comp.core.validation
{
	import co.pointred.fx.comp.collection.HashMap;
	
	import mx.events.ValidationResultEvent;
	import mx.validators.NumberValidator;
	
	public class ValidatorService
	{
		private static var INSTANCE:ValidatorService = null;
		
		public function ValidatorService(privateClass:PrivateClass)
		{
			
		}
		
		public static function getInstance():ValidatorService
		{
			if(INSTANCE == null)
				INSTANCE = new ValidatorService(new PrivateClass());
			return INSTANCE;
		}
		
		private var validatorHash:HashMap=new HashMap();
		
		public function getValidator(validatorName:String):IValidator
		{
			switch (validatorName)
			{
				case "EMAIL":
					return new PrEmailValidator();
				case "IP":
					return new PrIpValidator();
				case "MAC":
					return new PrMacValidator();
				default:
					return null;
			}
		}
		
		public function validateRange(range:String, value:Number, required:Boolean):Boolean
		{
			var numberValidator:NumberValidator = new NumberValidator();
			var rangeArray:Array = range.split(":");
			var minValue:Number = int(rangeArray[0])
			var maxValue:Number = int(rangeArray[1])
			numberValidator.maxValue = maxValue;
			numberValidator.minValue = minValue;
			numberValidator.required = required;
			var validationResultEvent:ValidationResultEvent = numberValidator.validate(value);
			if(validationResultEvent.type == ValidationResultEvent.VALID)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}
class PrivateClass
{
	public function PrivateClass()
	{}
}
