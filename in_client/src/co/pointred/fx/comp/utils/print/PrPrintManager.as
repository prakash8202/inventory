package co.pointred.fx.comp.utils.print
{
	import mx.core.UIComponent;
	import mx.printing.FlexPrintJob;

	public class PrPrintManager
	{
		private static var INSTANCE:PrPrintManager = null;
		public function PrPrintManager(privateClass:PrivateClass)
		{
		}
		
		public static function getInstance():PrPrintManager
		{
			if(INSTANCE == null)
				INSTANCE = new PrPrintManager(new PrivateClass());
			return INSTANCE;
		}
		
		public function printPage(uiComponent:UIComponent):Boolean
		{
			var retVal:Boolean = false;
			var printJob:FlexPrintJob = new FlexPrintJob();
			
			if(true == printJob.start())
			{
				printJob.addObject(uiComponent);
				printJob.send();
				retVal = true;
			}
			return retVal;			
		}
	}
}
class PrivateClass
{
	public function PrivateClass()
	{}
}