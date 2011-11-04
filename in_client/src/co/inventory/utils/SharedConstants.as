package co.inventory.utils
{
	import co.pointred.fx.comp.collection.HashMap;
	import co.pointred.fx.comp.core.PrAlert;
	import co.pointred.fx.comp.core.PrComboBox;
	import co.pointred.fx.comp.resizabletw.PrPopupManager;
	import co.pointred.fx.dataobjects.UserObject;
	import co.pointred.fx.rpc.RemoteGateway;
	
	import flash.display.Loader;
	import flash.display.LoaderInfo;
	import flash.events.Event;
	import flash.net.URLRequest;
	import flash.utils.ByteArray;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Image;
	import mx.core.FlexGlobals;
	import mx.core.IVisualElement;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;
	import mx.utils.URLUtil;
	
	import spark.components.VGroup;

	public class SharedConstants
	{
		private static var INSTANCE:SharedConstants = null;
		
		
		public static var SUCCESS:String = "SUCCESS";
		public static var FAILURE:String = "FAILURE";
		public static var WARNING_:String = "WARNING";
		
		public static const COMPANY_NAME:String = "Santhosh Agencies";
		
		//Constants for report
		public static const PDF_REPORT:String = "pdf";
		public static const EXCEL_REPORT:String = "excel";
		
		
		public static var content_container:VGroup;
		
		public static var hashOfAlertTypes:HashMap = new HashMap();
		
		public static function getInstance():SharedConstants
		{
			if(INSTANCE == null)
				INSTANCE = new SharedConstants(new PrivateClass());
			return INSTANCE;
		}
		
		public function SharedConstants(privateClass:PrivateClass)
		{
			//get Alert Type based on msg
			loadHashOfAlertTypes();
			
		}
		
		private function loadHashOfAlertTypes():void
		{
			hashOfAlertTypes.put(SUCCESS,PrAlert.SUCCESS_MESSAGE);
			hashOfAlertTypes.put(FAILURE,PrAlert.FAILURE_MESSAGE);
			hashOfAlertTypes.put(WARNING_,PrAlert.WARNING_MESSAGE);
		}
		
		public function addToContainer(iVisualElement:IVisualElement):void
		{
			content_container.removeAllElements();
			content_container.addElement(iVisualElement);
		}
		
		public function setDataProvider(comboBox:PrComboBox,coll:ArrayCollection):void
		{
			var obj:Object = new Object;
			obj[comboBox.labelField] = "Select";
			
			coll.addItemAt(obj,0);
			
			comboBox.dataProvider = coll;
			comboBox.setDataProvider(coll);
			comboBox.selectedIndex = 0;
		}
		
		public function setComboDisplayData(prCombo:PrComboBox, attrValue:String, attrName:String):void
		{
			var cmbList:ArrayCollection=prCombo.dataProvider as ArrayCollection;
			for (var key:String in cmbList)
			{
				var idx:Number=new Number(key);
				var object:Object=cmbList.getItemAt(idx);
				if(object[attrName]==attrValue)
					prCombo.selectedIndex=idx;		
			}
		}
	}
}
class PrivateClass
{
	/**
	 * Singleton Implementation
	 */
	public function PrivateClass()
	{
	}	
}
