package co.pointred.fx.comp.singleton
{
	import co.pointred.fx.dataobjects.UserObject;
	import co.pointred.fx.comp.singleton.CrudCallBack;
	import co.pointred.fx.rpc.RemoteGateway;
	
	import mx.core.FlexGlobals;
	import mx.events.EventListenerRequest;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;

	public class CrudService
	{
		
		private static var instance:CrudService
		
		public static function getInstance():CrudService
		{
			if(instance==null)
			{
				instance=new CrudService();
			}
			
			return instance;
		}
		
		public function CrudService()
		{
			
		}

		////////////////////////// CREATE  ///////////////////////////
		
		/**
		 * A generic create API to create Objects in the Db through iBatis
		 * */
		public function createObject(crudCallBack:CrudCallBack, asObject:Object, stmtName:String):void
		{
			asObject["IBATIS_STATEMENT"]=stmtName;
			var crudGateway:RemoteObject = RemoteGateway.getInstance().getCrudGateway();
			crudGateway.addEventListener(ResultEvent.RESULT, function (resultEvent:ResultEvent):void
			{
				createSuccess(resultEvent,crudCallBack);
			}
			);
			
			var userObject:UserObject = FlexGlobals.topLevelApplication.userObject;
			userObject.dataObject = asObject;

			crudGateway.createObject(userObject);
		}
		
		private function createSuccess(resultEvent:ResultEvent, crudCallBack:CrudCallBack):void
		{
			crudCallBack.callBackCreate(resultEvent);
		}
		
		
		///////////////////// MODIFY /////////////////////////////
		
		/**
		 * A generic create API to Update Objects in the Db through iBatis
		 * */
		public function updateObject(crudCallBack:CrudCallBack, asObject:Object, stmtName:String):void
		{
			asObject["IBATIS_STATEMENT"]=stmtName;
			var crudGateway:RemoteObject = RemoteGateway.getInstance().getCrudGateway();
			crudGateway.addEventListener(ResultEvent.RESULT, function (resultEvent:ResultEvent):void
			{
				updateSuccess(resultEvent,crudCallBack);
			}
			);
			var userObject:UserObject = FlexGlobals.topLevelApplication.userObject;
			userObject.dataObject = asObject;

			crudGateway.updateObject(userObject);
		}
		
		private function updateSuccess(resultEvent:ResultEvent, crudCallBack:CrudCallBack):void
		{
			crudCallBack.callBackUpdate(resultEvent);
		}
		
		/////////////////////// SELECT //////////////////////////////////
		public function selectObject(crudCallBack:CrudCallBack,asObject:Object):void
		{
			var crudGateway:RemoteObject = RemoteGateway.getInstance().getCrudGateway();
			crudGateway.addEventListener(ResultEvent.RESULT,function(resultEvent:ResultEvent):void
			{
				selectSuccess(resultEvent,crudCallBack);	
			}
			);
			var userObject:UserObject = FlexGlobals.topLevelApplication.userObject;
			userObject.dataObject = asObject;
			crudGateway.selectObject(userObject);
		}
		
		private function selectSuccess(resultEvent:ResultEvent, crudCallBack:CrudCallBack):void
		{
			crudCallBack.callBackRead(resultEvent);
		}
		////////////////////// DELETE  /////////////////////////////////
		/**
		 * A generic Delete API to delete objects in the DB thru iBatis
		 * */
		 
		public function deleteObject(crudCallBack:CrudCallBack, asObject:Object, stmntName:String):void
		{
			asObject["IBATIS_STATEMENT"]=stmntName;
			var crudGateway:RemoteObject = RemoteGateway.getInstance().getCrudGateway();
			crudGateway.addEventListener(ResultEvent.RESULT,function(resultEvent:ResultEvent):void
			{
				deleteSuccess(resultEvent,crudCallBack);
			});
			
			var userObject:UserObject = FlexGlobals.topLevelApplication.userObject;
			userObject.dataObject = asObject;

			crudGateway.deleteObject(userObject);
		}
		
		private function deleteSuccess(resultEvent:ResultEvent, crudCallBack:CrudCallBack):void
		{
			crudCallBack.callBackDelete(resultEvent);
		}
	}
}