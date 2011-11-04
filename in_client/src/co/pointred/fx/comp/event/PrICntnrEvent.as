package co.pointred.fx.comp.event
{
	import co.pointred.fx.comp.container.ICntnr;
	
	import flash.events.Event;
	
	public class PrICntnrEvent extends Event
	{
		private var _iCntnr:ICntnr;
		
		public static const ICOMP_CREATED:String = "ICOMP_CREATED";

		public function PrICntnrEvent(type:String, iCntnr:ICntnr = null, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this._iCntnr=iCntnr;
		}
		
		public function get iCntnr():ICntnr
		{
			return this._iCntnr;
		}
	}
}