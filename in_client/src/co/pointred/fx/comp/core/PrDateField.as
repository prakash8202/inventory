package co.pointred.fx.comp.core
{
	import co.pointred.fx.comp.core.validation.IValidator;
	
	import flash.events.Event;
	
	import mx.controls.DateField;
	import mx.formatters.DateFormatter;
	
	public class PrDateField extends DateField implements IComp
	{
		private var attrName:String;
		private var prevAttrVal:String;
		private var retainPrevValue:Boolean=false;
		private var validated:Boolean=true;
		private var validationErrMsg:String="OK";
		private var dateFormatterForServer:DateFormatter = new DateFormatter();
		private var dateFormatterForClient:DateFormatter = new DateFormatter();
		
		public function PrDateField(icomp:XML)
		{
			super();
//			this.labelFunction = doLabel;
			super.formatString = "DD-MM-YYYY";
			dateFormatterForServer.formatString = "YYYY-MM-DD";
			dateFormatterForClient.formatString = "DD-MM-YYYY";		
			super.yearNavigationEnabled = true;
			
			var startRange:int = icomp.before;
			var endRange:int = icomp.after;
			setSelectableRange(startRange,endRange);
			
		}
		
		public function setSelectableRange(startRange:int, endRange:int):void
		{
			var sR:Object = new Object();
			if(startRange != -1)
			{
				var rangeStartDate:Date = getPreviousDates(null, startRange);
				sR.rangeStart = rangeStartDate;
			}
			if(endRange != -1)
			{
				var rangeEndDate:Date = getAfterDates(null, endRange);
				sR.rangeEnd = rangeEndDate;
			}
				this.selectableRange = sR;	
		}
		
		public function getAttrName():String
		{
			return this.attrName;
		}
		
		public function getAttrValue():Object
		{
			var dateText:String = super.text;
			var obj:Date = DateField.stringToDate(dateText,"DD-MM-YYYY");;
			var formattedDate:String =  dateFormatterForServer.format(obj);
			return formattedDate;
		}
		
		public function setAttrName(attrName:String):void
		{
			this.attrName=attrName;
		}
		
		public function setAttrValue(attrValue:Object):void
		{
			var selectedDate:Date = DateField.stringToDate(attrValue.toString(), "YYYY-MM-DD");
			super.selectedDate = selectedDate;
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
			if(getAttrValue()=="")
			{
				this.validated=false;
				this.validationErrMsg="Date cannot be Empty";	
			}
			
			if(this.validated==false)
			{
				super.errorString=this.validationErrMsg;
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
		
		private function dateAdd(datepart:String = "", number:Number = 0, date:Date = null):Date {
			if (date == null) {
				/* Default to current date. */
				date = new Date();
			}
			
			var returnDate:Date = new Date(date.time);;
			
			switch (datepart.toLowerCase()) {
				case "fullyear":
				case "month":
				case "date":
				case "hours":
				case "minutes":
				case "seconds":
				case "milliseconds":
					returnDate[datepart] += number;
					break;
				default:
					/* Unknown date part, do nothing. */
					break;
			}
			return returnDate;
		}
		
		private function getPreviousDates(startDate:Date, startRange:int):Date
		{
			
			var adjustedStartDate:Date = new Date();
			adjustedStartDate =	dateAdd("date", -startRange, startDate);
			return adjustedStartDate;
		}
		
		private function getAfterDates(endDate:Date, endRange:int):Date
		{
			var adjustedEndDate:Date = new Date();
			adjustedEndDate = dateAdd("date", endRange, endDate);
			return adjustedEndDate;
		}
		
		private function doLabel(item:Date):String {
			if (item == null) {
				return "";
			}
			return dateFormatterForClient.format(item);
		}
		
		public function addEventListeners(evt:Event):void{
			super.addEventListener(evt.type, null);
		}
		
		public function getValidator():IValidator
		{
			return null;	
		}


	}
}