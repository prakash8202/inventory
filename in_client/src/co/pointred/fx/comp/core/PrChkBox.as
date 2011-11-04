package co.pointred.fx.comp.core
{
	import co.pointred.fx.comp.core.validation.IValidator;
	
	import spark.components.CheckBox;
	
	public class PrChkBox extends CheckBox implements IComp
	{
		private var attrName:String;
		private var prevAttrVal:String;
		private var retainPrevValue:Boolean=false;
		private var validated:Boolean=true;
		private var validationErrMsg:String="OK";
		private var _data:String;
		
		public function PrChkBox()
		{
			super();
		}
		
		public function getAttrName():String
		{
			return this.attrName;
		}
		
		public function getAttrValue():Object
		{
			return super.selected;
		}
		
		public function set data(_data:String):void
		{
			this._data = _data;
		}
		
		public function get data():String
		{
			return this._data;
		}
		
		public function setAttrName(attrName:String):void
		{
			this.attrName=attrName;
		}
		
		public function setAttrValue(attrValue:Object):void
		{
			if(attrValue == "1")
			{
				this.selected = true;
			}
			else
			{
				this.selected = false;	
			}
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
			super.selected=false;
		}
		
		public function getValueForDb(objToInject:Object):Object
		{
			var attrName:String=getAttrName();
			var attrVal:Boolean=getAttrValue();
			var convertedAttrVal:String="0";
			if(attrVal==true)
				convertedAttrVal="1";
			
			objToInject[attrName]=convertedAttrVal;
			
			if(isRetainPreviousValue()==true)
			{
				attrName=attrName+"_previous_value";
				attrVal=getPreviousValue();
				if(attrVal==true)
					convertedAttrVal="1";
				else
					convertedAttrVal="0";
				objToInject[attrName]=convertedAttrVal;
			}
			return objToInject;
		}
		
		public function getValidator():IValidator
		{
			return null;	
		}

	}
}