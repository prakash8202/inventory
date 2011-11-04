package co.pointred.fx.dataobjects
{
	import co.pointred.fx.comp.collection.HashMap;
	
	import mx.collections.ArrayCollection;
	import mx.collections.ArrayList;

	[Bindable]
	[RemoteClass(alias="co.pointred.fx.dataobjects.UserObject")]
	public class UserObject
	{
		public function UserObject()
		{
		}
		
		private var _userName:String;
		private var _password:String;
		private var _userId:String;
		private var _sessionId:String;
		
		private var _dataObject:Object;
		
		private var _privilegeList:ArrayCollection = new ArrayCollection();
		
		private var _statusMsg:String;
		private var _status:String;
		
		private var _loginAttempts:Number;
		
		private var _auditContext:String;
		private var _auditDescr:String;
		
		public function get userName():String
		{
			return this._userName;
		}
		
		public function set userName(userName:String):void
		{
			this._userName = userName;
		}
		
		public function get password():String
		{
			return this._password;
		}
		
		public function set password(password:String):void
		{
			this._password = password;
		}
		
		public function get userId():String
		{
			return _userId;
		}
		
		public function set userId(value:String):void
		{
			_userId = value;
		}

		public function get sessionId():String
		{
			return this._sessionId;
		}
		
		public function set sessionId(sessionId:String):void
		{
			this._sessionId = sessionId;
		}
		
		public function get dataObject():Object
		{
			return this._dataObject;
		}
		
		public function set dataObject(dataObject:Object):void
		{
			this._dataObject = dataObject;
		}

		public function get statusMsg():String
		{
			return _statusMsg;
		}
		
		public function set statusMsg(value:String):void
		{
			_statusMsg = value;
		}

		public function get status():String
		{
			return _status;
		}

		public function set status(value:String):void
		{
			_status = value;
		}
		
		public function get privilegeList():ArrayCollection
		{
			return _privilegeList;
		}

		public function set privilegeList(value:ArrayCollection):void
		{
			_privilegeList = value;
		}
		

		public function get auditContext():String
		{
			return _auditContext;
		}
		
		public function set auditContext(value:String):void
		{
			_auditContext = value;
		}
		
		public function get auditDescr():String
		{
			return _auditDescr;
		}
		
		public function set auditDescr(value:String):void
		{
			_auditDescr = value;
		}
	}
}