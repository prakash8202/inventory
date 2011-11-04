package co.pointred.fx.comp.container
{
	import co.pointred.fx.comp.collection.HashMap;
	import co.pointred.fx.comp.core.IComp;
	import co.pointred.fx.comp.core.PrUiError;
	
	import mx.collections.ArrayCollection;
	
	/**
	 * The contaner class which holds all the UiComponents (IComp) in a hashTable. Pass a unique String ContainerId as arg[0] to the constructor
	 * 
	 * */
	public class PrUiContainer
	{
		private var attrICompHash:HashMap = new HashMap();		
		private var containerId:String;
		private var _validationErrorList:ArrayCollection = new ArrayCollection();
		
		/**
		 * Pass one Unique Id for this container..
		 * */
		public function PrUiContainer(...args)
		{
			if(args[0]==null)
			{
				throw new PrUiError("Give a Unique Id in the constructor of the PrUiContainer");
			}
			this.containerId=args[0] as String;
		}
		
		public function getContainerId():String
		{
			return this.containerId;
		}
		
		/**
		 * Adds a IPrComp to the underlying HashTable
		 * */
		public function addIComp(iComp:IComp):void
		{
			attrICompHash.put(iComp.getAttrName(), iComp);
		}
		
		public function getIComp(attrName:String):IComp
		{
			return attrICompHash.getValue(attrName) as IComp;
		}
		
		
		/**
		 * gets the Key/Value in the attrValHash and appends them to the incoming dynamic object
		 * */
		public function getAttrKeyValHash(objToInject:Object):Object
		{
			var keys:Array = this.attrICompHash.getKeys();
			
			for each(var attrName:String in keys)
			{
				var iComp:IComp = this.attrICompHash.getValue(attrName)
				iComp.getValueForDb(objToInject)
			}
			return objToInject;
		}
		
		public function clear():void
		{
			var keyArr:Array= this.attrICompHash.getKeys();
			var jj:Number = keyArr.length;
			for(var ii:Number = 0;ii<jj;ii++)
			{
				var key:String = keyArr[ii];
				this.attrICompHash.remove(key);
			}
			this.attrICompHash=null;
		}
		
		public function reset():void
		{
			var keyArr:Array= this.attrICompHash.getKeys();
			var jj:Number = keyArr.length;
			for(var ii:Number = 0;ii<jj;ii++)
			{
				var key:String = keyArr[ii];
				var iComp:IComp=this.attrICompHash.getValue(key);
				iComp.resetAttrVal();
			}
		}
		
		public function validate():Boolean
		{
			// Reset the error list
			this.validationErrorList.removeAll();
			var keys:Array = this.attrICompHash.getKeys();
			var valFailedCnt:int=0;
			var validation:Boolean=true;
			for each(var attrName:String in keys)
			{
				var iComp:IComp = this.attrICompHash.getValue(attrName)
				iComp.resetValidationFlag();
			}
			
			for each(var attrName1:String in keys)
			{
				var iComp1:IComp = this.attrICompHash.getValue(attrName1)
				validation=iComp1.doValidate();
				
				if(validation==false)
				{
					valFailedCnt ++;
					this.validationErrorList.addItem(iComp1.getValidationError());
				}
			}
			
			if(valFailedCnt>0)
				return false;
			else
				return true;
		}
		
		public function setAttrVal(obj:Object):void
		{
			var keys:Array = this.attrICompHash.getKeys();
			var valFailedCnt:int=0;
			var validation:Boolean=true;
			for each(var attrName:String in keys)
			{
				var iComp:IComp = this.attrICompHash.getValue(attrName)
				var valueObj:Object=obj[iComp.getAttrName()];
				
				if(valueObj!=null)
					iComp.setAttrValue(valueObj);
			}
		}

		public function get validationErrorList():ArrayCollection
		{
			return _validationErrorList;
		}

			
	}
}