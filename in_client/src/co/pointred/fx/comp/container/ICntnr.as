package co.pointred.fx.comp.container
{
	import co.pointred.fx.comp.collection.HashMap;
	import co.pointred.fx.comp.core.IComp;
	
	import mx.core.IVisualElement;
	
	import spark.components.NavigatorContent;

	/**
	 * A generic interface to be implemented by all the Container level components like Panel, Window etc.  The ICntnr will be having one or many PrUiContainers.  The prUiContainers are injected inside by the UiParser
	 * */
	public interface ICntnr
	{
		/**
		 * returns the HashMap of prUiCntnrs.  Key=PrUiCntnrId, val=PruiCntnr Object
		 * */
		function getPrUiContainers():HashMap;
		
		/**
		 * adds a PrUiContainer to the existing hash of PruiCntnrs
		 * */
		function addPrUiContainer(pruiContainer:PrUiContainer):void;

		
		/**
		 *gets the PrUiContainer given the prCntnrId 
		 * */
		function getPrUiContainer(prCntnrId:String):PrUiContainer;
		
		/**
		 * returns a String Array of all the PrUiCntnr IDs
		 * */
		function getAllPrCntnrIds():Array
		
		/**
		 * calls the getAttrName() and getAttrValue() apis of the iComponents inside a PrUiCntnr.  Return type is a Dynamic Object, so that it is easily portable as an AsObject
		 * */
		function getAttrAndValue(prCntnrId:String):Object;
		
		/**
		 * Adds and IComp inside a PrUiCntnr
		 * */
		function addIComp(prCntrId:String,iComp:IComp):void;
		
		/**
		 * returns an IComp from the PrUiCntnr
		 * */
		function getIComp(prCntrId:String,attrName:String):IComp;

		/**
		 * returns the Attr val Obj for all the IComp inside this ICntnr
		 * */
		function getAllAttrVal():Object;
		
		/**
		 * injects the attr val of the dynamic obj to the Icomponents inside 
		 * */
		function setAttrVal(obj:Object):void;
		
		
		/**
		 * resets all the IComp in a given prUiCntnr
		 * */
		function reset(prCntrId:String):void;

		/**
		 * resets all the IComp in all the PrUiCntnrs
		 * */
		function resetAll():void;
		/**
		 * resets and removes all the PrCntnrs from the ICntnr
		 * */
		function removeAllPrCntnrs():void
			
		/**
		 * adds a Container (outer container like form, vGroup, HGroup etc).  The argument has to be an IVisualElement
		 * */			
		function addContainer(iVisualElement:Object):void;
		
		/**
		 * validates All the IComp inside the IContainer 
		 * */
		function validate():Boolean;
		
		function validateForm(componentId:String):Boolean;
		
		
		function setTitle(title:String):void;
		function getTitle():String;
		
		function setICntnrId(iCntnrId:String):void;
		function getICntnrId():String;
		
		function addComponent(compId:String, iVisualElement:IVisualElement):void;
		
		function addICompToComponent(compId:String, iComp:IComp):void;
		
		function getComponent(compId:String):IVisualElement;
		
		function getComponentBasedIComp(componentId:String, attrName:String):IComp;
		
		function getComponentBasedAllAttrVal(componentId:String, objToInject:Object):Object;
		
		function setComponentBasedAttrVal(componentId:String, obj:Object):void;
		
		function setHashOfTabs(hashOfTabs:HashMap):void;
		
		function getTab(tabName:String):NavigatorContent;
		function getWidthPercent():Number;
		function getHeightPercent():Number;
		
	}
}