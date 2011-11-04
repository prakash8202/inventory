package co.pointred.fx.comp.singleton
{
	import co.pointred.fx.comp.collection.HashMap;

	public class UserPrivilege
	{
		private var _privilegeHash:HashMap = new HashMap();
		private static var instance:UserPrivilege;
		
		public static function getInstance():UserPrivilege
		{
			if(instance==null)
			{
				instance=new UserPrivilege(new PrivateClass());
			}
			
			return instance;
		}
		
		public function UserPrivilege(privateClass:PrivateClass)
		{
			
		}

		public function get privilegeHash():HashMap
		{
			return _privilegeHash;
		}

		public function set privilegeHash(value:HashMap):void
		{
			_privilegeHash = value;
		}
		
		public function getPrivilege(privId:String):String
		{
			var retVal:String = privilegeHash.getValue(privId);
			if(null == retVal || retVal == "" || retVal.length < 1)
			{
				retVal = "2";
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