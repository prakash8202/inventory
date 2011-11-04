package co.pointred.fx.comp.event
{
	import flash.events.Event;
	
	public class PrDatagridEvent extends Event
	{
		private var _dataObject:Object;
		
		public static const ADD_CLICKED:String = "PrDatagridEvent.ADD_CLICKED";
		public static const EDIT_CLICKED:String = "PrDatagridEvent.EDIT_CLICKED";
		public static const DELETE_CLICKED:String = "PrDatagridEvent.DELETE_CLICKED";
		public static const GRID_ITEM_CLICKED:String = "PrDatagridEvent.GRID_ITEM_CLICKED";
		
		public function PrDatagridEvent(type:String, dataObject:Object,bubbles:Boolean=false, cancelable:Boolean=false)
		{
			this._dataObject = dataObject;
			super(type, bubbles, cancelable);
		}
		
		public function get dataObject():Object{
			return this._dataObject;
		}
		
		override public function clone():Event
		{
			return new PrDatagridEvent(type,dataObject,bubbles,cancelable);
		}
	}
}