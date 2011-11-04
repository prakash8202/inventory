package co.pointred.fx.comp.core
{
	import co.pointred.fx.comp.core.validation.IValidator;
	import co.pointred.fx.comp.utils.StringUtils;
	
	import mx.collections.ArrayCollection;
	import mx.containers.FormItem;
	import mx.utils.StringUtil;
	
	import spark.components.DropDownList;
	
	public class PrComboBox extends DropDownList implements IComp
	{
		private var attrName:String;
		private var prevAttrVal:String;
		private var retainPrevValue:Boolean=false;
		private var validated:Boolean=true;
		private var validationErrMsg:String="OK";
		private var required:Boolean = false;
		private var _selectedIndex:int;
		private var _enabled:Boolean;
		
		public function PrComboBox()
		{
			super();
		}
		
		public function getAttrName():String
		{
			return this.attrName;
		}
		
		public function getAttrValue():Object
		{
			var obj:String = ""; 
			obj = super.selectedItem;
			var isContains:Boolean = StringUtils.contains(obj,"<label>");
			if(isContains ==true)
				obj = super.selectedItem.value;
			if(obj+"" == "Select")
				obj = "";
			return obj;
		}
		
		public function setAttrName(attrName:String):void
		{
			this.attrName=attrName;
		}
		
		public function setRequired(required:Boolean):void
		{
			this.required = required;
		}
		
		public function setAttrValue(attrValue:Object):void
		{
			for (var i:int=0; i<this.dataProvider.length; i++) 
			{
				// Get this item's data 
				var item:String = this.dataProvider[i];
				var selectedObjectValue:String;
				var isContains:Boolean = StringUtils.contains(item,"<label>");
				if(isContains == true)
				{
				var xml:XML = new XML(item);
				selectedObjectValue = xml.value;
				}
				else
					selectedObjectValue = item;
				// Check if is selectedValue 
				if(selectedObjectValue == attrValue)
				{
					// Yes, set selectedIndex 
					super.selectedIndex = i;
					break;
				}
			}
		}
		
		/**
		 * the dp is an ArrayCollection of the Values to be set as List in the Combo
		 * */
		public function setDataProvider(dp:Object):void
		{
			super.dataProvider = dp as ArrayCollection;
			super.selectedIndex=0;	
			this.selectedIndex = 0;
		}
		
		public function getValidationError():Object
		{
			return null;
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
			// Not Null Check
			if (true == required)
			{
				var selectedStr:String = this.selectedItem + "";
				selectedStr = StringUtil.trim(selectedStr);
				if(this.selectedIndex > -1 && this.selectedItem != "Select" && 0 < selectedStr.length)
				{
					this.validated=true;
					this.validationErrMsg = "";
					super.errorString="";
					return true;
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
			else
			{
				this.validated=true;
				this.validationErrMsg = "";
				super.errorString="";
				return true;
			}
		}
		
		public function resetValidationFlag():void
		{
			this.validationErrMsg="OK";
			this.validated=true;
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
			super.selectedIndex=0;	
		}
		
		public function getValueForDb(objToInject:Object):Object
		{
			var attrName:String=getAttrName();
			
			var attrVal:String = getAttrValue() as String;
			objToInject[attrName]=attrVal;
			return objToInject;
		}
		
		public function getValidator():IValidator
		{
			return null;	
		}

		override public function get selectedIndex():int
		{
			return super.selectedIndex;
		}

		override public function set selectedIndex(value:int):void
		{
			_selectedIndex = value;
			super.selectedIndex = value;
		}

		override public function get enabled():Boolean
		{
			return super.enabled;
		}

		override public function set enabled(value:Boolean):void
		{
			_enabled = value;
			super.selectedIndex = 1;
			super.enabled = value;
		}
	}
}