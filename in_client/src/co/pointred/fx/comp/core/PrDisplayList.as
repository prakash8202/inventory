package co.pointred.fx.comp.core
{
	import co.pointred.fx.comp.core.validation.IValidator;
	
	import mx.collections.ArrayCollection;
	
	import spark.components.List;
	
	public class PrDisplayList extends List implements IComp
	{
		private var attrName:String;
		private var _dataProviderXml:XML;
		
		public function PrDisplayList()
		{
			super();
			this.enabled = false;
		}
		public function getAttrName():String
		{
			return this.attrName;
		}
		
		public function getAttrValue():Object
		{
			var resultStr:String = "";
			var arr:ArrayCollection = this.dataProvider as ArrayCollection;
			for each(var st:String in arr)
			{
				var value:String = dataProviderXml.data.(label==st).value;
				resultStr = resultStr + value + ",";
			}
			resultStr = resultStr.substring(0,resultStr.length-1);
			return resultStr;
		}
		
		public function setAttrName(attrName:String):void
		{
			this.attrName=attrName;
		}
		
		public function setAttrValue(attrValue:Object):void
		{
			var str:String  = attrValue.toString();
			var arr:Array = str.split(",");
			var dataproviderCollection:ArrayCollection = new ArrayCollection();
			for each(var st:String in arr)
			{
				var data:String = "" + dataProviderXml.data.(value==st).label;
				if(0 < data.length)
				{
					dataproviderCollection.addItem(data);					
				}
			}
			this.dataProvider = dataproviderCollection;
		}
		
		public function getValidationError():Object
		{
			return null;
		}
		
		public function doValidate():Boolean
		{
			return true;
		}
		
		public function resetValidationFlag():void
		{
			// do nothing
		}
		
		public function setRetainPreviousValue(retainPrevValue:Boolean):void
		{
			//do nothing
		}
		
		public function isRetainPreviousValue():Boolean
		{
			return false;
		}
		
		public function getPreviousValue():Object
		{
			return null;	
		}
		
		public function getValueForDb(objToInject:Object):Object
		{
			return objToInject;
		}
		
		public function resetAttrVal():void
		{
			//do nothing
		}
		
		public function getValidator():IValidator
		{
			return null;
		}

		public function get dataProviderXml():XML
		{
			return _dataProviderXml;
		}

		public function set dataProviderXml(value:XML):void
		{
			_dataProviderXml = value;
		}

	}
}