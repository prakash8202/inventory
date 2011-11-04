package co.pointred.fx.comp.container
{
	import co.pointred.fx.comp.collection.HashMap;
	import co.pointred.fx.comp.core.IComp;
	
	import mx.core.IVisualElement;

	public class PrComponentCntnr
	{
		public function PrComponentCntnr()
		{
		}
		
		private var _component:IVisualElement = null;
		private var _hashOfIComp:HashMap = new HashMap();

		public function get hashOfIComp():HashMap
		{
			return _hashOfIComp;
		}

		public function set hashOfIComp(value:HashMap):void
		{
			_hashOfIComp = value;
		}

		public function get component():IVisualElement
		{
			return _component;
		}

		public function set component(value:IVisualElement):void
		{
			_component = value;
		}
		
		public function addIComp(iComp:IComp):void
		{
			this.hashOfIComp.put(iComp.getAttrName(), iComp);	
		}
		
		public function getIComp(attrName:String):IComp
		{
			return this.hashOfIComp.getValue(attrName) as IComp;
		}
		
		/**
		 * gets the Key/Value in the attrValHash and appends them to the incoming dynamic object
		 * */
		public function getAttrKeyValHash(objToInject:Object):Object
		{
			var keys:Array = this.hashOfIComp.getKeys();
			
			for each(var attrName:String in keys)
			{
				var iComp:IComp = this.hashOfIComp.getValue(attrName)
				iComp.getValueForDb(objToInject)
			}
			return objToInject;
		}
		
		
		public function clear():void
		{
			var keyArr:Array= this.hashOfIComp.getKeys();
			var jj:Number = keyArr.length;
			for(var ii:Number = 0;ii<jj;ii++)
			{
				var key:String = keyArr[ii];
				this.hashOfIComp.remove(key);
			}
			this.hashOfIComp=null;
		}
		
		public function reset():void
		{
			var keyArr:Array= this.hashOfIComp.getKeys();
			var jj:Number = keyArr.length;
			for(var ii:Number = 0;ii<jj;ii++)
			{
				var key:String = keyArr[ii];
				var iComp:IComp=this.hashOfIComp.getValue(key);
				iComp.resetAttrVal();
			}
			this.hashOfIComp=null;
		}
		
		public function validate():Boolean
		{
			var keys:Array = this.hashOfIComp.getKeys();
			var valFailedCnt:int=0;
			var validation:Boolean=true;
			for each(var attrName:String in keys)
			{
				var iComp:IComp = this.hashOfIComp.getValue(attrName)
				iComp.resetValidationFlag();
			}
			
			for each(var attName:String in keys)
			{
				var iiComp:IComp = this.hashOfIComp.getValue(attName)
				validation=iiComp.doValidate();
				if(validation==false)
				{
					valFailedCnt ++;
				}
			}
			
			if(valFailedCnt>0)
				return false;
			else
				return true;
		}
		
		public function setAttrVal(obj:Object):void
		{
			var keys:Array = this.hashOfIComp.getKeys();
			var valFailedCnt:int=0;
			var validation:Boolean=true;
			for each(var attrName:String in keys)
			{
				var iComp:IComp = this.hashOfIComp.getValue(attrName)
				var valueObj:Object=obj[iComp.getAttrName()];
				
				if(valueObj!=null)
					iComp.setAttrValue(valueObj);
			}
		}
	}
}