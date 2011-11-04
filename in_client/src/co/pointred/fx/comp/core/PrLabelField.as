package co.pointred.fx.comp.core
{
	import co.pointred.fx.comp.core.validation.IValidator;
	import co.pointred.fx.comp.core.validation.ValidatorService;
	
	import mx.containers.FormItem;
	import mx.utils.StringUtil;
	
	import spark.components.Label;
	import spark.components.TextInput;
	
	public class PrLabelField extends Label implements IComp
	{
		private var attrName:String;
		private var prevAttrVal:String;
		private var retainPrevValue:Boolean=false;
		private var validated:Boolean=true;
		private var validationErrMsg:String="OK";
		private var _iValidator:IValidator=null;
		private var _range:String;
		private var _required:String = "false";
		
		public function PrLabelField( )
		{
			super();
		}
		
		public function getAttrName():String
		{
			return this.attrName;
		}
		
		public function getAttrValue():Object
		{
			return super.text;
		}
		
		public function setAttrName(attrName:String):void
		{
			this.attrName=attrName;
		}
		
		public function setAttrValue(attrValue:Object):void
		{
			super.text=attrValue.toString();
			if(this.retainPrevValue==true)
			{
				this.prevAttrVal=attrValue.toString();
			}
		}
		
		public function getValidationError():Object
		{
			return this.validationErrMsg;
		}
		
		public function doValidate():Boolean
		{
			var lblTxt:String = this.attrName;
			if(this.parent is FormItem)
			{
				var fI:FormItem = this.parent as FormItem;
				if(fI != null)
				{
					lblTxt = fI.label;	
				}
			}
			var required:Boolean = "true" == this.required ? true:false;
			// Range Check
			if(null != this.range && "" != this.range && 0 < this.range.length)
			{
				
				this.validated = ValidatorService.getInstance().validateRange(this.range, int(this.text), required );
				if(false == this.validated)
				{
					this.validationErrMsg = "Value for " +  lblTxt + " must be within " + this.range;
					super.errorString=this.validationErrMsg;
					return this.validated;
				}
			}
			
			// Not Null Check
			if ("true" == this.required)
			{
				var txt:String = this.text + "";
				txt = StringUtil.trim(txt);
				if (null != txt && "" != txt && 0 < txt.length && txt != "null")
				{
					this.validated=true;
				}
				else
				{
					this.validated=false;
					this.validationErrMsg= lblTxt + " Cannot be Empty";
					this.errorString = this.validationErrMsg;
					super.errorString=this.errorString;
					return this.validated;
				}
			}
			
			if(this.iValidator != null)
			{
				this.validated= (true == this.validated)?this.iValidator.doValidate():this.validated;
				if(false == this.validated)
				{
					var validationErrorObj:String = this.iValidator.getValidationError().toString() + "";
					if(0 < StringUtil.trim(validationErrorObj).length)
					{
						this.validationErrMsg = validationErrorObj + " in " + lblTxt;	
					}
					else
					{
						this.validationErrMsg = "Invalid Input " + " in " + lblTxt;
					}
					return this.validated;
				}
			}
			return this.validated;
		}
		
		public function resetValidationFlag():void
		{
			this.validated=true;
			this.validationErrMsg="OK";
			super.errorString=null;
		}
		
		public function setRetainPreviousValue(retainPrevValue:Boolean):void
		{
			this.retainPrevValue=retainPrevValue;
		}
		
		public function isRetainPreviousValue():Boolean
		{
			return this.retainPrevValue;
		}
		
		public function getPreviousValue():Object
		{
			return this.prevAttrVal;	
		}
		
		public function resetAttrVal():void
		{
			super.text="";
		}
		
		public function getValueForDb(objToInject:Object):Object
		{
			var attrName:String=getAttrName();
			var attrVal:String=getAttrValue() as String;
			objToInject[attrName]=attrVal;
			
			if(isRetainPreviousValue()==true)
			{
				attrName=attrName+"_previous_value";
				attrVal=getPreviousValue() as String;
				if(attrVal!=null)
					objToInject[attrName]=attrVal;
			}
			return objToInject;
		}
		
		public function getValidator():IValidator
		{
			return this.iValidator;	
		}
		
		public function get range():String
		{
			return _range;
		}
		
		public function set range(value:String):void
		{
			_range = value;
		}
		
		public function get required():String
		{
			return _required;
		}
		
		public function set required(value:String):void
		{
			_required = value;
		}
		
		public function get iValidator():IValidator
		{
			return _iValidator;
		}
		
		public function set iValidator(value:IValidator):void
		{
			_iValidator = value;
		}
	}
}