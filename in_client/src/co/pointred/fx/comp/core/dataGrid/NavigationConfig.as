package co.pointred.fx.comp.core.dataGrid
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="co.pointred.fx.dataobjects.NavigationConfig")]
	public class NavigationConfig
	{
		private var _dataList:ArrayCollection;
		private var _mongoDataList:ArrayCollection;
		private var _action:String;
		private var _prevAction:String;
		private var _sqlString:String;
		private var _prevSqlString:String;
		private var _numberOfRecords:Number;
		private var _startIndex:Number;
		private var _endIndex:Number;
		// DB Type  0 --> MySQL[Default]  / 1  -->  MongoDB
		private var _dbType:String = "0";
		
		private var _totalPages:Number;
		private var _currentPage:Number;
		private var _audited:Boolean;
		private var _bucketSize:Number;

		public function NavigationConfig()
		{
		}

		public function get dataList():ArrayCollection
		{
			return _dataList;
		}

		public function set dataList(value:ArrayCollection):void
		{
			_dataList=value;
		}
		
		public function get mongoDataList():ArrayCollection
		{
			return _mongoDataList;
		}
		
		public function set mongoDataList(value:ArrayCollection):void
		{
			_mongoDataList=value;
		}

		public function get action():String
		{
			return _action;
		}

		public function set action(value:String):void
		{
			_action=value;
		}

		public function get prevAction():String
		{
			return _prevAction;
		}

		public function set prevAction(value:String):void
		{
			_prevAction=value;
		}

		public function get sqlString():String
		{
			return _sqlString;
		}

		public function set sqlString(value:String):void
		{
			_sqlString=value;
		}

		public function get prevSqlString():String
		{
			return _prevSqlString;
		}

		public function set prevSqlString(value:String):void
		{
			_prevSqlString=value;
		}

		public function get numberOfRecords():Number
		{
			return _numberOfRecords;
		}

		public function set numberOfRecords(value:Number):void
		{
			_numberOfRecords=value;
		}

		public function get startIndex():Number
		{
			return _startIndex;
		}

		public function set startIndex(value:Number):void
		{
			_startIndex = value;
		}

		public function get endIndex():Number
		{
			return _endIndex;
		}

		public function set endIndex(value:Number):void
		{
			_endIndex = value;
		}

		public function get totalPages():Number
		{
			return _totalPages;
		}

		public function set totalPages(value:Number):void
		{
			_totalPages = value;
		}

		public function get currentPage():Number
		{
			return _currentPage;
		}

		public function set currentPage(value:Number):void
		{
			_currentPage = value;
		}

		public function get audited():Boolean
		{
			return _audited;
		}

		public function set audited(value:Boolean):void
		{
			_audited = value;
		}

		public function get dbType():String
		{
			return _dbType;
		}

		public function set dbType(value:String):void
		{
			_dbType = value;
		}

		public function get bucketSize():Number
		{
			return _bucketSize;
		}

		public function set bucketSize(value:Number):void
		{
			_bucketSize = value;
		}
	}
}