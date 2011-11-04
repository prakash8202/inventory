package co.pointred.fx.comp.utils
{
	import co.pointred.fx.comp.collection.HashMap;
	import co.pointred.fx.comp.core.PrCheckBoxGroup;
	import co.pointred.fx.comp.core.PrComboBox;
	import co.pointred.fx.comp.core.PrDateField;
	import co.pointred.fx.comp.event.PrEventDispatcher;
	import co.pointred.fx.comp.event.PrICntnrEvent;
	import co.pointred.fx.dataobjects.UserObject;
	import co.pointred.fx.rpc.RemoteGateway;
	
	import flash.events.Event;
	
	import mx.collections.ArrayCollection;
	import mx.core.FlexGlobals;
	import mx.core.IVisualElement;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;
	import mx.utils.ObjectUtil;
	import mx.validators.EmailValidator;
	
	public class DataProviderService
	{
		public function DataProviderService()
		{
		}
		
		/**
		 * API to get data for PrCombobox
		 * 
		 **/
		public static function getDataForComponent(comboBox:PrComboBox, sqlQuery:String, auditContext:String, auditDescr:String, listenerFunction:Function):void
		{
			var crudGateway:RemoteObject= RemoteGateway.getInstance().getCrudGateway();
			if(listenerFunction != null)
				PrEventDispatcher.registerForEvent(auditContext, listenerFunction);
			crudGateway.addEventListener(ResultEvent.RESULT, function (resultEvent:ResultEvent):void
			{
				var arrayColl:ArrayCollection = resultEvent.result as ArrayCollection;
				arrayColl.addItemAt('Select',0);
				comboBox.dataProvider = arrayColl;
				comboBox.setDataProvider(arrayColl);
				if(listenerFunction != null)
				PrEventDispatcher.Dispatcher.dispatchEvent(new PrICntnrEvent(auditContext));
			}
			);
			var userObject:UserObject = ObjectUtil.copy(FlexGlobals.topLevelApplication.userObject) as UserObject;
			userObject.auditContext = auditContext;
			userObject.auditDescr = auditDescr;
			userObject.dataObject = sqlQuery;
			crudGateway.executeDirectSelect(userObject);
		}
		
		/**
		 * API to Get List Data for PrDualList Component
		 *
		 **/
		public static function getDataForList(resultCollection:ArrayCollection, sqlQuery:String, handleAsCsv:Boolean,auditContext:String, auditDescr:String, listenerFunction:Function):void
		{
			resultCollection.removeAll();
			var crudGateway:RemoteObject= RemoteGateway.getInstance().getCrudGateway();
			if(listenerFunction != null)
				PrEventDispatcher.registerForEvent(auditContext, listenerFunction);
			crudGateway.addEventListener(ResultEvent.RESULT, function (resultEvent:ResultEvent):void
			{
				var arrayColl:ArrayCollection = resultEvent.result as ArrayCollection;
				if(true == handleAsCsv)
				{
					var values:String = arrayColl.getItemAt(0) + "";
					var valuesArray:Array = values.split(",");
					var ac:ArrayCollection = new ArrayCollection(valuesArray);
					resultCollection.addAll(ac);
				}
				else
				{
					resultCollection.addAll(arrayColl);
				}
				if(listenerFunction != null)
					PrEventDispatcher.Dispatcher.dispatchEvent(new PrICntnrEvent(auditContext));
			}
			);
			var userObject:UserObject = ObjectUtil.copy(FlexGlobals.topLevelApplication.userObject) as UserObject;
			userObject.auditContext = auditContext;
			userObject.auditDescr = auditDescr;
			userObject.dataObject = sqlQuery;
			crudGateway.executeDirectSelect(userObject);
		}
		
		/**
		 * API to Get List Data Using SQL
		 *
		 **/
		public static function getDataUsingStmt(resultCollection:ArrayCollection, stmtName:String,parameter:String, auditContext:String, auditDescr:String, listenerFunction:Function):void
		{
			resultCollection.removeAll();
			var crudGateway:RemoteObject= RemoteGateway.getInstance().getCrudGateway();
			if(listenerFunction != null)
				PrEventDispatcher.registerForEvent(auditContext, listenerFunction);
			crudGateway.addEventListener(ResultEvent.RESULT, function (resultEvent:ResultEvent):void
			{
				var userObject:UserObject = resultEvent.result as UserObject;
				var arrayColl:ArrayCollection =  userObject.dataObject as ArrayCollection;
				resultCollection.addAll(arrayColl);
				if(listenerFunction != null)
					PrEventDispatcher.Dispatcher.dispatchEvent(new PrICntnrEvent(auditContext));
			}
			);
			var userObject:UserObject = ObjectUtil.copy(FlexGlobals.topLevelApplication.userObject) as UserObject;
			userObject.auditContext = auditContext;
			userObject.auditDescr = auditDescr;
			var dataObj:Object = new Object();
			dataObj.statement = stmtName;
			dataObj.parameter = parameter;
			userObject.dataObject = dataObj;
			crudGateway.getDataUsingStmt(userObject);
		}

		public static function getDateComponent(before:String, after:String):PrDateField
		{
			var xmlString :String = "<icomp><before>" + before + "</before><after>" + after + "</after></icomp>";
			var icomp:XML = new XML(xmlString);
			var prDateField:PrDateField = new PrDateField(icomp);
			return prDateField;
		}
		
		public static function getHashMap(object:Object):HashMap
		{
			var hash:HashMap = new HashMap;
			for(var key:String in object)
			{
				hash.put(key,object[key]);
			}
			return hash;
		}
			
	}
}