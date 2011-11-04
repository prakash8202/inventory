package co.pointred.fx.comp.resizabletw
{
	import co.inventory.utils.SharedConstants;
	import co.pointred.fx.comp.container.ICntnr;
	
	import flash.display.DisplayObject;
	import flash.events.Event;
	
	import mx.core.FlexGlobals;
	import mx.core.IVisualElement;
	import mx.managers.PopUpManager;
	
	import spark.components.Group;
	import spark.components.Scroller;
	import spark.components.VGroup;

	public class PrPopupManager
	{
		public function PrPopupManager()
		{
		}
		
		private static function removeFromStage(event:Event, cntr:PrPopupBase):void
		{
			PopUpManager.removePopUp(cntr);
		}
		
		public static function showICntnrInPopup(iCntnr:ICntnr, title:String, isClosable:Boolean = true,icon:Class=null, isDraggable:Boolean = false,specificParentObject:DisplayObject = null):PrPopupBase
		{
			var perWidth:Number = iCntnr.getWidthPercent();
			var perHeight:Number = iCntnr.getHeightPercent();
			
			var vgrp:VGroup = new VGroup;
			vgrp.percentWidth = 100;
			vgrp.percentHeight = 100;
			
			var iVisualElement:IVisualElement = iCntnr as IVisualElement;
			iVisualElement.percentWidth=100;
			iVisualElement.percentHeight=100;
			vgrp.addElement(iVisualElement);
			
			return showPopup(vgrp,title,perWidth, perHeight,isClosable,icon,isDraggable,specificParentObject);
		}
		
		
		public static function showPopup(iVisualElement:IVisualElement, title:String,percentWidth:Number,percentHeight:Number, isClosable:Boolean = true,icon:Class=null, isDraggable:Boolean = false,specificParentObject:DisplayObject = null):PrPopupBase
		{
			
			var cntr:PrPopupBase;
			cntr= new PrPopupBase;
			cntr.percentHeight = 100;
			cntr.percentWidth = 100;
			cntr.title = title;
			cntr.icon = icon;
			cntr.isDraggable = isDraggable;
			cntr.isClosable = isClosable;
			
//			cntr.addEventListener("ClosethisContainer",function(e:Event):void{removeFromStage(e,cntr)});
			
			var contentGrp:VGroup = new VGroup;
			contentGrp.width=getWidth(percentWidth);
			contentGrp.height=getHeight(percentHeight);
			contentGrp.paddingBottom = 5;
			contentGrp.paddingLeft = 5;
			contentGrp.paddingRight = 15;
			contentGrp.paddingTop = 5;
			
			iVisualElement.percentWidth=100;
			iVisualElement.percentHeight=100;
			contentGrp.addElement(iVisualElement);
			
			var grp:Group = new Group;
			grp.horizontalScrollPosition = 50;
			grp.verticalScrollPosition = 50;
			grp.clipAndEnableScrolling = true;
			grp.addElement(contentGrp);
			
			var scroller:Scroller = new Scroller;
			scroller.width=getWidth(percentWidth);
			scroller.height=getHeight(percentHeight);
			scroller.viewport = grp;
			
			cntr.addElement(scroller);
			
			var parent:DisplayObject = SharedConstants.content_container;
			
			PopUpManager.addPopUp(cntr,parent,true );
			PopUpManager.centerPopUp(cntr);
			
			return cntr;
		}
		
		private static function getHeight(percentHt:Number):Number
		{
			var applicationHt:Number =FlexGlobals.topLevelApplication.height; //55 is for bottom status bar and menu bar
			if(percentHt>90)
				applicationHt=applicationHt-80;
				
			var ht:int = Math.round((applicationHt*percentHt)/100);
			return ht;
		}
		
		private static function getWidth(percentWidth:Number):Number
		{
			var applicationWidth:Number =FlexGlobals.topLevelApplication.width ; // 260 is for tree
			if(percentWidth>90)
				applicationWidth = applicationWidth-280
			var width:int = Math.round((applicationWidth*percentWidth)/100);
			return width;
		}
	}
}