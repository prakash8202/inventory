package co.pointred.fx.comp.core.validation.custom
{
	import co.pointred.fx.comp.core.validation.ValidationErrorManager;
	
	import mx.collections.ArrayCollection;
	import mx.containers.FormItem;
	import mx.core.UIComponent;
	import mx.events.ValidationResultEvent;
	import mx.validators.EmailValidator;
	import mx.validators.NumberValidator;
	import mx.validators.RegExpValidator;
	import mx.validators.StringValidator;
	import mx.validators.Validator;

	public class ValidationMgr
	{
		public static const MAC_VALIDATION:String = "MAC";
		public static const IP_VALIDATION:String = "IP";
		public static const EMAIL_VALIDATION:String = "EMAIL";
		public static const STRLEN_VALIDATION:String = "STRLEN";
		public static const RANGE_VALIDATION:String = "RANGE";
		public static const SELECTED_VALIDATION:String = "SELECTED";
		private static var _validationErrorList:ArrayCollection = new ArrayCollection();
		
		private static var validatorArray:Array = new Array();
		public function ValidationMgr()
		{
		}
		
		public static function validateAll(components:Array):Boolean
		{
			// Reset the error list
			validationErrorList.removeAll();
			validatorArray = new Array();
			var validationResults:Array = new Array();
			components.forEach(createValidator);
			validationResults = Validator.validateAll(validatorArray);
			if(validationResults.length == 0)
			{
				return true;
			}
			else
			{
				for each (var validationResultEvent:ValidationResultEvent in validationResults)
				{
					var uiComponent:UIComponent = validationResultEvent.target.source as UIComponent;
					var lblTxt:String = uiComponent.name;
					var fI:FormItem = uiComponent.parent as FormItem;
					if(null!=fI )
					{
						lblTxt = fI.label;
					}
					var errorString:String = uiComponent.errorString as String;
					uiComponent.errorString = errorString.split("\n")[0];
					validationErrorList.addItem(lblTxt + " - " + uiComponent.errorString);
				}
			}
			ValidationErrorManager.getInstance().showErrorDialog(validationErrorList);
			return false;
		}
		
		private static function createValidator(element:*, index:int, arr:Array):void
		{
			switch(element.validator)
			{
				case "MAC":
					var regexValidator:RegExpValidator = new RegExpValidator();
					regexValidator.required = element.required;
					regexValidator.source = element.component;
					regexValidator.property = element.property;
					regexValidator.noMatchError = "Invalid Mac Address\n MAC Format is 11:AA:22:BB:33:CC";
					regexValidator.expression = "^[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}$";
					validatorArray.push(regexValidator);
					break;
				case "IP":
					var regexValidator1:RegExpValidator = new RegExpValidator();
					regexValidator1.required = element.required;
					regexValidator1.source = element.component;
					regexValidator1.property = element.property;
					regexValidator1.noMatchError="Invalid IP Address";
					regexValidator1.expression="^(?:(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$";
					validatorArray.push(regexValidator1);
					break;
				case "EMAIL":
					var emailValidator:EmailValidator = new EmailValidator();
					emailValidator.required = element.required;
					emailValidator.source = element.component;
					emailValidator.property = element.property;
					validatorArray.push(emailValidator);
					break;
				case "STRLEN":
					var strLenValidator:StringValidator = new StringValidator();
					strLenValidator.required = element.required;
					strLenValidator.source = element.component;
					strLenValidator.property = element.property;
					var param:String = element.params;
					var values:Array = param.split(",");
					var minLen:Number = Number(values[0]);
					var maxLen:Number = Number(values[1]);
					strLenValidator.minLength = minLen;
					strLenValidator.maxLength = maxLen;
					validatorArray.push(strLenValidator);
					break;
				case "RANGE":
					var numberValidator:NumberValidator = new NumberValidator();
					numberValidator.required = element.required;
					numberValidator.source = element.component;
					numberValidator.property = element.property;
					var param1:String = element.params;
					var values1:Array = param1.split("-");
					var minVal:Number = Number(values1[0]);
					var maxVal:Number = Number(values1[1]);
					numberValidator.minValue = minVal;
					numberValidator.maxValue = maxVal;
					validatorArray.push(numberValidator);
					break;
				case "SELECTED":
					var numberValidator1:NumberValidator = new NumberValidator();
					numberValidator1.required = element.required;
					numberValidator1.source = element.component;
					numberValidator1.property = element.property;
					numberValidator1.minValue = parseInt(element.params);
					numberValidator1.lowerThanMinError = "Select a value";
					numberValidator1.triggerEvent = 'change';
					validatorArray.push(numberValidator1);
					break;
			}
		}

		public static function get validationErrorList():ArrayCollection
		{
			return _validationErrorList;
		}

	}
}