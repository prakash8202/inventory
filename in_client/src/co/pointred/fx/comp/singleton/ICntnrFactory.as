package co.pointred.fx.comp.singleton
{
	import co.pointred.fx.comp.container.ICntnr;
	import co.pointred.fx.comp.container.PrBorderContainer;
	import co.pointred.fx.comp.container.PrPanel;
	import co.pointred.fx.comp.container.PrSkinnableContainer;
	import co.pointred.fx.comp.container.PrTitleWindow;
	import co.pointred.fx.comp.container.skins.PrHeaderlessPanel;
	import co.pointred.fx.comp.container.skins.PrHeaderlessTitleWindow;
	
	import spark.layouts.HorizontalLayout;
	import spark.layouts.VerticalLayout;
	import spark.layouts.supportClasses.LayoutBase;
	
	public class ICntnrFactory
	{
		
		private static var instance:ICntnrFactory;
		
		public static function getInstance():ICntnrFactory
		{
			if(instance==null)
			{
				instance=new ICntnrFactory();
			}
			
			return instance;
		}
		
		function ICntnrFactory()
		{
			
		}
		
		/**
		 * API to Create the ICntnr based on the XML def (myXML.type).
		 * [The Container may be PrPanel or PrTitleWindow or PrBorderContainer or PrSkinnableContainer]
		 * 
		 **/ 
		public function buildICntnr(myXML:XML):ICntnr
		{
			var iCntnr:ICntnr;
			var type:String=myXML.type;
			var ht:Number=new Number(myXML.height);
			var wdth:Number=new Number(myXML.width);
			var layout:String=myXML.layout;
			
			if(type=="panel")
			{
				// If type is panel
				var prPanel:PrPanel=new PrPanel();
				prPanel.percentHeight=ht;
				prPanel.percentWidth=wdth;
				var panelTitle:String = "";
				panelTitle = myXML.title;
				
				if(panelTitle.length == 0)
				{
					// When the xml is shown as a popup the default header in PrPanel should be removed
					// !!!!!!!!!!!!!!NOTE : Do Not Give a Title in XML if the UI is going to be a Popup.!!!!!!!!!!!!!!
					prPanel.setStyle("skinClass", Class(PrHeaderlessPanel));
				}
				else
				{
					prPanel.title = myXML.title;
				}
				prPanel.layout=getLayout(layout);
				
				iCntnr=prPanel;
			}
			else if(type=="titlewindow")
			{
				// If type is titlewindow
				var prTitleWindow:PrTitleWindow = new PrTitleWindow();
				prTitleWindow.percentHeight=ht;
				prTitleWindow.percentWidth=wdth;
				var twTitle:String = "";
				twTitle = myXML.title;
				if(twTitle.length == 0)
				{
					// When the xml is shown as a popup the default header in PrTitleWindow should be removed
					// !!!!!!!!!!!!!!NOTE : Do Not Give a Title in XML if the UI is going to be a Popup.!!!!!!!!!!!!!!
					prTitleWindow.setStyle("skinClass", Class(PrHeaderlessTitleWindow));
				}
				else
				{
					prTitleWindow.title = myXML.title;
				}
				prTitleWindow.layout=getLayout(layout);
				iCntnr=prTitleWindow;
			}else if(type == "bordercontainer")
			{
				// If type is bordercontainer
				var prBorderContainer:PrBorderContainer = new PrBorderContainer();
				prBorderContainer.percentHeight = ht;
				prBorderContainer.percentWidth = wdth;
				prBorderContainer.layout = getLayout(layout);
				iCntnr=prBorderContainer;
			}else if(type == "skinnablecontainer")
			{
				// If type is skinnablecontainer
				var prSkinnableContainer:PrSkinnableContainer = new PrSkinnableContainer();
				prSkinnableContainer.percentHeight = ht;
				prSkinnableContainer.percentWidth = wdth;
				prSkinnableContainer.layout = getLayout(layout);
				iCntnr=prSkinnableContainer;
			}
			return iCntnr;
		}
		
		/**
		 * API to get the Layout
		 * 
		 **/
		private function getLayout(layout:String):LayoutBase
		{
			return (layout == "horizontal")?new HorizontalLayout():new VerticalLayout();
		}
	}
}