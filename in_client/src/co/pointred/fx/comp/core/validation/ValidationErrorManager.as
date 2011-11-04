package co.pointred.fx.comp.core.validation
{
	import co.pointred.fx.comp.utils.FrameworkConstants;
	
	import flash.display.DisplayObject;
	
	import mx.collections.ArrayCollection;
	import mx.core.FlexGlobals;
	import mx.managers.PopUpManager;

	public class ValidationErrorManager
	{
		private static var INSTANCE:ValidationErrorManager = null;
		private var errorDialog:ValidationErrorDialog = new ValidationErrorDialog();
		public function ValidationErrorManager(privateClass:PrivateClass)
		{
		}
		
		public static function getInstance():ValidationErrorManager
		{
			if(INSTANCE == null)
				INSTANCE = new ValidationErrorManager(new PrivateClass());
			return INSTANCE;
		}
		
		public function showErrorDialog(errorList:ArrayCollection):void
		{
			errorDialog.setErrorList(errorList);
			var parentObj:DisplayObject = FrameworkConstants.partialViewParent as DisplayObject;
			parentObj = (!parentObj)?(FlexGlobals.topLevelApplication as DisplayObject):parentObj; 
			PopUpManager.addPopUp(this.errorDialog,parentObj, true);
			PopUpManager.centerPopUp(this.errorDialog);
		}
	}
}
class PrivateClass
{
	public function PrivateClass()
	{}
}