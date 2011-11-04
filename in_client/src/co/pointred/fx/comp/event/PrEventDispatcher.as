package co.pointred.fx.comp.event
{
	import co.pointred.fx.comp.collection.HashMap;
	
	import flash.events.EventDispatcher;
	
	import mx.collections.ArrayCollection;

	/**
	 * API to manage Custom Events
	 * 
	 **/
	public class PrEventDispatcher
	{
		public static var Dispatcher:EventDispatcher = new EventDispatcher();
		
		private static var hashOfListeners:HashMap = new HashMap();
		
		
		public function PrEventDispatcher()
		{
		}
		
		public static function registerForEvent(eventName:String, listenerFunction:Function):void
		{
			addListener(eventName, listenerFunction);
			addListener(eventName, listenerRemover);
		}
		
		private static function addListener(eventName:String, listenerFunction:Function):void
		{
			Dispatcher.addEventListener(eventName,listenerFunction);
			
			if(true == hashOfListeners.containsKey(eventName))
			{
				(hashOfListeners.getValue(eventName) as ArrayCollection).addItem(listenerFunction);	
			}
			else
			{
				var listenersList:ArrayCollection = new ArrayCollection();
				listenersList.addItem(listenerFunction);
				hashOfListeners.put(eventName, listenersList);
			}
		}
		
		private static function listenerRemover(event:PrICntnrEvent):void
		{
			var eventName:String = event.type;
			
			if(true == hashOfListeners.containsKey(eventName))
			{
				var functionsList:ArrayCollection = new ArrayCollection();
				functionsList = hashOfListeners.getValue(eventName);
				for each(var listernerFunction:Function in functionsList)
				{
					Dispatcher.removeEventListener(eventName, listernerFunction);	
				}
				hashOfListeners.remove(eventName);
			}
		}
	}
}