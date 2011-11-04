package co.pointred.fx.comp.core
{
	import co.pointred.fx.comp.core.validation.IValidator;
	
	import flash.events.MouseEvent;
	
	import mx.containers.FormItem;
	
	import spark.components.CheckBox;
	import spark.components.Group;
	import spark.components.HGroup;
	import spark.components.VGroup;
	
	public class PrCheckBoxGroup extends FormItem implements IComp
	{
		private var attrName:String;
		private var arrayOfChkBox:Array = new Array();
		private var prevAttrVal:String;
		private var retainPrevValue:Boolean=false;
		private var validated:Boolean=true;
		private var validationErrMsg:String="OK";
		private var chkBoxesCntr:Group;
		private var multiSelect:Boolean = true;
		
		public function PrCheckBoxGroup(icomp:XML)
		{
			super();
			this.label = icomp.label;
			if(icomp.layout == "vertical"){
				chkBoxesCntr = new VGroup();
			}else{
				chkBoxesCntr = new HGroup();
			}
			if("true" == icomp.multiselect){
				multiSelect = true;
			}else{
				multiSelect = false;
			}
			
			for each(var data:XML in icomp.dataprovider.data)
			{
					var cB:PrChkBox = new PrChkBox();
					cB.toolTip = data.label;
					cB.label = data.label;
					cB.data = data.value;
					
					if(false == multiSelect){
						cB.addEventListener(MouseEvent.CLICK, checkMultiSelect);	
					}
					chkBoxesCntr.addElement(cB);
					arrayOfChkBox.push(cB);
			}
			this.addElement(chkBoxesCntr);
		}
		
		/**
		 * Get Selected List Of CheckBoxes as String seperated by ':'
		 **/ 
		private function getSelectedItems():String{
			var resultStr:String = "";
			for(var jj:Number=0;jj<arrayOfChkBox.length;jj++){
				var chkBx:PrChkBox = arrayOfChkBox[jj];
				if(true == chkBx.selected)
				{
					resultStr = resultStr + chkBx.data + ":";
				}
			}
			resultStr = resultStr.substr(0,resultStr.length-1);
			return resultStr;
		}
		
		/**
		 * Select a set of checkboxes
		 * 
		 **/ 
		private function setSelectedItems(selected:String):void
		{
			var selectedArr:Array = selected.split(":");
			uncheckAll();
			if(this.multiSelect == true)
			{
				for each(var chkLbl:String in selectedArr)
				{
					for(var kk:Number=0;kk<arrayOfChkBox.length - 1;kk++)
					{
						var chkBx:PrChkBox = arrayOfChkBox[kk];
						if(chkLbl == chkBx.data)
						{
							chkBx.selected = true;
							
						}
					}	
				}
			}
			else
			{
				var checked:Boolean = false;
				for each(var chkLbl1:String in selectedArr)
				{
					
					for(var ll:Number=0;ll<arrayOfChkBox.length - 1 && checked == false;ll++)
					{
						var chkBx1:PrChkBox = arrayOfChkBox[ll];
						if(chkLbl1 == chkBx1.data)
						{
							chkBx1.selected = true;
							checked = true;
						}
					}	
				}
			}
			
			getSelectedItems();
		}
		
		private function checkMultiSelect(evt:MouseEvent):void
		{
			uncheckAll();
			(evt.target as CheckBox).selected = true;
		}
		
		/**
		 * API to uncheck all checkboxes
		 **/ 
		private function uncheckAll():void{
			for(var kk:Number=0;kk<arrayOfChkBox.length;kk++){
				var chkBx:CheckBox = arrayOfChkBox[kk];
				chkBx.selected = false;
			}
		}
		
		/**
		 * API to check all checkboxes
		 **/ 
		private function checkAll():void{
			for(var kk:Number=0;kk<arrayOfChkBox.length;kk++){
				var chkBx:CheckBox = arrayOfChkBox[kk];
				chkBx.selected = true;
			}
		}
		
		public function getAttrName():String
		{
			return this.attrName;
		}
		
		public function getAttrValue():Object
		{
			return getSelectedItems();
		}
		
		public function setAttrName(attrName:String):void
		{
			this.attrName = attrName;
		}
		
		public function setAttrValue(attrValue:Object):void
		{
			setSelectedItems(attrValue as String);
		}
		
		public function getValidationError():Object
		{
			return this.validationErrMsg;
		}
		
		public function doValidate():Boolean
		{
			//private API to come here..
			if(isRetainPreviousValue()==true)
			{
				this.validated=false;
				this.validationErrMsg="Previous Value found to be Null etc etc";
			}
			
			if(this.validated==false)
			{
				super.errorString = "VALIDATION FAILED.. Why is this coming in PrCheckBoxGroup ?";
			}
			return this.validated;
		}
		
		public function resetValidationFlag():void
		{
			this.validated=true;
			this.validationErrMsg="OK";
		}
		
		public function setRetainPreviousValue(retainPrevValue:Boolean):void
		{
			this.retainPrevValue=retainPrevValue;
		}
		
		public function isRetainPreviousValue():Boolean
		{
			return this.retainPrevValue;
		}
		
		public function resetAttrVal():void
		{
			uncheckAll();
		}
		
		public function getPreviousValue():Object
		{
			return this.prevAttrVal;
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
			return null;	
		}
	
	
		
	}
}