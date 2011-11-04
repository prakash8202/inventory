package co.pointred.fx.comp.parser
{
	import co.pointred.fx.comp.container.ICntnr;
	import co.pointred.fx.comp.core.IComp;
	import co.pointred.fx.comp.event.PrEventDispatcher;
	import co.pointred.fx.comp.event.PrICntnrEvent;
	import co.pointred.fx.comp.singleton.ICntnrFactory;
	import co.pointred.fx.comp.singleton.ICompFactory;
	import co.pointred.fx.comp.singleton.UserPrivilege;
	
	import flash.events.Event;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	
	import mx.containers.Form;
	
	import spark.components.HGroup;
	import spark.components.VGroup;
	import spark.layouts.HorizontalLayout;
	import spark.layouts.VerticalLayout;
	import spark.layouts.supportClasses.LayoutBase;
	
	public class XmlComponentGenerator
	{
		include "XmlProcessor.as";
		
		private var userPriv:UserPrivilege = UserPrivilege.getInstance();
		
		public function XmlComponentGenerator()
		{
		}
		
		/**
		 * Init API for FlexFramework 
		 * 
		 **/
		public function buildComponents(xmlFileLocation:String):void
		{
			var iCntnr:ICntnr;
			var myLoader:URLLoader = new URLLoader();
			myLoader.load(new URLRequest(xmlFileLocation));
			myLoader.addEventListener(Event.COMPLETE, 
				function(evt:Event):void
				{
					// Once the XML is loaded to the App create myXML [FF Comp Build Step - 1]
					var myXML:XML = new XML(evt.target.data);
					var uid:String = myXML.uid;
					
					// Send myXML to ICntnrFactory to proceed with component building [FF Comp Build Step - 2]
					iCntnr= ICntnrFactory.getInstance().buildICntnr(myXML);
					// Continue with iComp Creation [FF Comp Build Step - 3]
					var isUiCreated:Boolean  = processXML(myXML,iCntnr);
					if(true == isUiCreated)
					{
						iCntnr.setICntnrId(uid);
						PrEventDispatcher.Dispatcher.dispatchEvent(new PrICntnrEvent(uid, iCntnr,false,false));
					}
				}
			);
		}
		
		/**
		 * API to provide containers based on the layout specified
		 **/
		private function getFormLayout(layout:String, form:Object):Object
		{
			if(layout=='form')
			{
				form=new Form();
				form.setStyle("paddingTop",0);
				form.setStyle("paddingBottom",0);
				form.setStyle("paddingLeft",0);
				form.setStyle("paddingRight",0);
			}
			else if(layout=='form_full')
			{
				form=new Form();
				form.percentHeight = 100;
				form.percentWidth = 100;
				form.setStyle("paddingTop",0);
				form.setStyle("paddingBottom",0);
				form.setStyle("paddingLeft",0);
				form.setStyle("paddingRight",0);
			}
			else if(layout=='vertical')
			{
				form=new VGroup();
				var vg:VGroup = form as VGroup;
				vg.percentHeight = 100;
				vg.percentWidth = 100;
				vg.paddingLeft=3;
				vg.paddingRight=3;
				vg.paddingBottom=3;
				vg.paddingTop=3;
			}
			else if(layout=='horizontal')
			{
				form=new HGroup();
				var hg:HGroup = form as HGroup;
				hg.percentHeight = 100;
				hg.percentWidth = 100;
				hg.paddingLeft=3;
				hg.paddingRight=3;
				hg.paddingBottom=3;
				hg.paddingTop=3;
			}
			
			return form;
		}
		
		private function getLayout(layout:String):LayoutBase
		{
			return (layout == "horizontal")?new HorizontalLayout():new VerticalLayout();
		}
		
		// Construct the UI - each iComp comes here..
		private function constructUi(xmlComp:XML,form:Object, privFlag:String):IComp
		{
			return ICompFactory.getInstance().buildIComp(xmlComp,form,privFlag);
		}
	}
}