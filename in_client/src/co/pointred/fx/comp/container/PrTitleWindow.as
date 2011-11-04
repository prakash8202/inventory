package co.pointred.fx.comp.container
{
	import co.pointred.fx.comp.collection.HashMap;
	import co.pointred.fx.comp.core.IComp;
	import co.pointred.fx.comp.core.PrUiError;
	import co.pointred.fx.comp.core.validation.ValidationErrorManager;
	
	import mx.collections.ArrayCollection;
	import mx.core.IVisualElement;
	
	import spark.components.NavigatorContent;
	import spark.components.TitleWindow;
	
	public class PrTitleWindow extends TitleWindow implements ICntnr
	{
		private var hashOfPrUiCntnr:HashMap = new HashMap();
		private var iCntnrId:String;
		private var hashOfComponents:HashMap = new HashMap();
		private var hashOfTabs:HashMap = new HashMap();
		private var validationErrorList:ArrayCollection = new ArrayCollection();
		
		public function PrTitleWindow()
		{
			super();
		}
				
		public function getPrUiContainers():HashMap
		{
			return this.hashOfPrUiCntnr;
		}
		
		public function addPrUiContainer(pruiContainer:PrUiContainer):void
		{
			this.hashOfPrUiCntnr.put(pruiContainer.getContainerId(),pruiContainer);	
		}
		
		public function getPrUiContainer(prCntnrId:String):PrUiContainer
		{
			var prUiCntr:PrUiContainer = this.hashOfPrUiCntnr.getValue(prCntnrId);
			if(prUiCntr==null)
			{
				throw new PrUiError("No container Exists in PrPanel for Container Id : " + prCntnrId);
			}
			return prUiCntr;
		}
		
		public function getAllPrCntnrIds():Array
		{
			return this.hashOfPrUiCntnr.getKeys();
		}
		
		public function addIComp(prCntrId:String,iComp:IComp):void
		{
			var prUiCntr:PrUiContainer = this.hashOfPrUiCntnr.getValue(prCntrId);
			prUiCntr.addIComp(iComp);
		}
		
		public function getIComp(prCntrId:String,attrName:String):IComp
		{
			var prUiCntnr:PrUiContainer = getPrUiContainer(prCntrId);
			return prUiCntnr.getIComp(attrName);
		}
		
		public function getAttrAndValue(prCntnrId:String):Object
		{
			var prUiCntr:PrUiContainer = this.hashOfPrUiCntnr.getValue(prCntnrId);
			var objToInject:Object = new Object;
			var injectedObj:Object=prUiCntr.getAttrKeyValHash(objToInject);
			return injectedObj;
		}
		
		public function getAllAttrVal():Object
		{
			var objWithData:Object=new Object();
			var cntnrArray:Array = this.getAllPrCntnrIds();
			var jj:Number=cntnrArray.length;
			for(var ii:Number=0;ii<jj;ii++)
			{
				var prUiCntnrId:String=cntnrArray[ii];
				var prUiCntnr:PrUiContainer= this.getPrUiContainer(prUiCntnrId);
				objWithData= prUiCntnr.getAttrKeyValHash(objWithData);
			}
			return objWithData;
		}
		
		public function reset(prUiCntrId:String):void
		{
			var prUiCntr:PrUiContainer = this.hashOfPrUiCntnr.getValue(prUiCntrId);
			prUiCntr.reset();
		}
		
		public function resetAll():void
		{
			var cntnrArray:Array = this.getAllPrCntnrIds();
			var jj:Number=cntnrArray.length;
			for(var ii:Number=0;ii<jj;ii++)
			{
				var prUiCntnrId:String=cntnrArray[ii];
				reset(prUiCntnrId);
			}
		}
		
		public function removeAllPrCntnrs():void
		{
			var obj:Object=new Object();
			var cntnrArray:Array = this.getAllPrCntnrIds();
			var jj:Number=cntnrArray.length;
			for(var ii:Number=0;ii<jj;ii++)
			{
				var prUiCntnrId:String=cntnrArray[ii];
				reset(prUiCntnrId);
				this.hashOfPrUiCntnr.remove(prUiCntnrId);
			}
		}
		
		public function addContainer(obj:Object):void
		{
			var iVisualElement:IVisualElement = obj as IVisualElement;
			super.addElement(iVisualElement);
		}
		
		public function validate():Boolean
		{
			// Removes all validation error messages 
			this.validationErrorList.removeAll();	
			var cntnrArray:Array = this.getAllPrCntnrIds();
			var jj:Number=cntnrArray.length;
			var validationResult:Boolean=true;
			var valFailedCnt:int=0;
			for(var ii:Number=0;ii<jj;ii++)
			{
				var prUiCntnrId:String=cntnrArray[ii];
				var prUiCntnr:PrUiContainer= this.getPrUiContainer(prUiCntnrId);
				validationResult= prUiCntnr.validate();
				if(validationResult==false)
				{
					valFailedCnt ++;
					validationErrorList.addAll(prUiCntnr.validationErrorList);
				}
			}
			if(valFailedCnt>0)
			{
				ValidationErrorManager.getInstance().showErrorDialog(this.validationErrorList);
				return false;
			}
			else
				return true;
		}
		
		public function validateForm(componentId:String):Boolean
		{
			// Removes all validation error messages 
			this.validationErrorList.removeAll();	
			var componentContainer:PrComponentCntnr = hashOfComponents.getValue(componentId) as PrComponentCntnr;
			
			
			var keys:Array = componentContainer.hashOfIComp.getKeys();
			var valFailedCnt:int=0;
			var validation:Boolean=true;
			for each(var attrName:String in keys)
			{
				var iComp:IComp = componentContainer.hashOfIComp.getValue(attrName)
				iComp.resetValidationFlag();
			}
			
			for each(var attrName1:String in keys)
			{
				var iComp1:IComp = componentContainer.hashOfIComp.getValue(attrName1)
				validation=iComp1.doValidate();
				if(validation==false)
				{
					valFailedCnt ++;
					validationErrorList.addItem(iComp1.getValidationError());
				}
			}
			
			if(valFailedCnt>0)
			{
				ValidationErrorManager.getInstance().showErrorDialog(this.validationErrorList);
				return false;
			}
			else
				return true;	
			
		}
		
		public function setAttrVal(obj:Object):void
		{
			var cntnrArray:Array = this.getAllPrCntnrIds();
			var jj:Number=cntnrArray.length;
			for(var ii:Number=0;ii<jj;ii++)
			{
				var prUiCntnrId:String=cntnrArray[ii];
				var prUiCntnr:PrUiContainer= this.getPrUiContainer(prUiCntnrId);
				prUiCntnr.setAttrVal(obj);
			}			
		}

		//////////////////////////////// component level attrs ////////////
		public function setTitle(title:String):void
		{
			super.title=title;
		}
		
		public function getTitle():String{
			return super.title;
		}
		
		public function setICntnrId(iCntnrId:String):void
		{
			this.iCntnrId=iCntnrId;
		}
		
		public function getICntnrId():String
		{
			return this.iCntnrId;
		}
		
		public function addComponent(compId:String, iVisualElement:IVisualElement):void
		{
			var componentCntnr:PrComponentCntnr = new PrComponentCntnr();
			componentCntnr.component = iVisualElement;
			hashOfComponents.put(compId, componentCntnr);
		}
		
		public function getComponent(compId:String):IVisualElement
		{
			return (hashOfComponents.getValue(compId) as PrComponentCntnr).component;
		}
		
		public function addICompToComponent(compId:String, iComp:IComp):void
		{
			(this.hashOfComponents.getValue(compId) as PrComponentCntnr).addIComp(iComp);	
		}
		
		public function getComponentBasedIComp(componentId:String, attrName:String):IComp
		{
			return (this.hashOfComponents.getValue(componentId) as PrComponentCntnr).getIComp(attrName);
		}
		
		public function getComponentBasedAllAttrVal(componentId:String, objToInject:Object):Object
		{
			return (this.hashOfComponents.getValue(componentId) as PrComponentCntnr).getAttrKeyValHash(objToInject);
		}
		
		public function setComponentBasedAttrVal(componentId:String, obj:Object):void
		{
			(this.hashOfComponents.getValue(componentId) as PrComponentCntnr).setAttrVal(obj);
		}
		
		public function setHashOfTabs(hashOfTabs:HashMap):void
		{
			this.hashOfTabs = hashOfTabs;
		}
		
		public function getTab(tabName:String):NavigatorContent
		{
			return this.hashOfTabs.getValue(tabName) as NavigatorContent;
		}
		
		public function getWidthPercent():Number
		{
			return this.percentWidth;	
		}
		
		public function getHeightPercent():Number
		{
			return this.percentHeight;
		}
	}
}