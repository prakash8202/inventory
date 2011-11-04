package co.pointred.fx.comp.core.dataGrid
{
	import co.pointred.fx.comp.container.PrPanel;
	import co.pointred.fx.comp.container.skins.PrHeaderlessPanel;
	import co.pointred.fx.dataobjects.UserObject;
	import co.pointred.fx.rpc.RemoteGateway;
	
	import mx.collections.ArrayCollection;
	import mx.collections.ArrayList;
	import mx.controls.DataGrid;
	import mx.controls.dataGridClasses.DataGridColumn;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;
	
	import spark.components.ComboBox;
	import spark.components.HGroup;
	import spark.components.TextInput;
	
	public class PrDataGridComponent extends PrPanel
	{
		private var dataGrid:DataGrid;
		public var dataControls:HGroup;
		private var filterCombo:ComboBox;
		private var searchText:TextInput;
		private var isFiltered:Boolean = false;
		public var dbType:String = "0";
		
		private var _auditContext:String;
		private var _auditDescr:String;
		
		private var _sql:String;
		
		private var _bucketSize:Number;
		
		private var controllers:Array = new Array();
		
		private var _additionalButtonsContainer:HGroup = new HGroup();
		
		[Bindable]
		private var _navigationConfig:NavigationConfig = new NavigationConfig();
		
		[Bindable]
		public var hideNavigationControls:Boolean = true;
		
		[Bindable]
		public var hideDataControls:Boolean = true;
		
		[Bindable]
		public var hideFilterControls:Boolean = true;
		
		[Bindable]
		public var hideRefreshButton:Boolean = true;
		
		[Bindable]
		public var hideToolBar:Boolean = true;
		
		include "ControlBarBuilder.as";
		include "GridControlEventHandlers.as";
		
		public function PrDataGridComponent()
		{
			super();
			super.percentWidth=100;
			this.setStyle("skinClass", Class(PrHeaderlessPanel));
//			buildControlBar();
		}
		
		/**
		 * sets and Array of DataGridColumn as the column list to the grid
		 **/
		public function setDataGridColumns(colArray:Array):void
		{
			dataGrid.columns = colArray;
		}
		
		public function setFilterFields(colArray:ArrayList):void{
			filterCombo.dataProvider = colArray;
		}
		
		public function getDataGrid():DataGrid
		{
			return this.dataGrid;
		}
		
		public function getDataControls():HGroup{
			return this.dataControls;
		}
		
		public function getDataFromSql(userObject:UserObject):void
		{
			var crudGateway:RemoteObject = RemoteGateway.getInstance().getCrudGateway();
			crudGateway.addEventListener(ResultEvent.RESULT, function (resultEvent:ResultEvent):void
			{
				if("0" == dbType)
				{
					getMySqlDataSuccess(resultEvent);	
				}else
				{
					getMongoDataSuccess(resultEvent);
				}
			}
			);
			crudGateway.getPaginatedData(userObject);
//			crudGateway.executeDirectSelect(userObject);
		}
		
		private function getMySqlDataSuccess(resultEvent:ResultEvent):void
		{
			var navConfigData:Object = resultEvent.result;
			this.navigationConfig = navConfigData as NavigationConfig;
			
			var ac:ArrayCollection = navConfigData.dataList as ArrayCollection;
			if(null != ac)
			{
				var jj:int = ac.length;
				
				if(jj > 0)
				{
					var colArr:Array= getDataGrid().columns;
					var kk:int =colArr.length;
					
					var dp:ArrayCollection = new ArrayCollection();
					
					for(var ii:int=0;ii<jj;ii++)
					{
						var obj:Object=new Object;
						var dataRow:ArrayCollection = ac.getItemAt(ii) as ArrayCollection;
						for(var aa:int=0;aa<dataRow.length;aa++)
						{	
							var dataGridCol:DataGridColumn = colArr[aa] as DataGridColumn;
							var key:String=dataGridCol.dataField;
							obj[key]=dataRow.getItemAt(aa);					
						}
						dp.addItem(obj);
					}
					getDataGrid().dataProvider=dp;	
				}
				else
				{
					if(navigationConfig.currentPage == 1)
					{
						getDataGrid().dataProvider = dp;	
					}
//					PrAlert.info("No More Records");
				}
			}
			cursorManager.removeAllCursors();
		}
		
		private function getMongoDataSuccess(resultEvent:ResultEvent):void
		{
			var navConfigData:Object = resultEvent.result;
			this.navigationConfig = navConfigData as NavigationConfig;
			
			var ac:ArrayCollection = navConfigData.mongoDataList as ArrayCollection;
			
			if(null != ac)
			{
				if(ac.length > 0)
				{
					getDataGrid().dataProvider=ac;
				}else
				{
//					PrAlert.info("No More Records");
				}
			}
			
			cursorManager.removeAllCursors();
		}

		public function get navigationConfig():NavigationConfig
		{
			return _navigationConfig;
		}

		public function set navigationConfig(value:NavigationConfig):void
		{
			_navigationConfig = value;
		}

		public function get sql():String
		{
			return _sql;
		}

		public function set sql(value:String):void
		{
			_sql = value;
		}
		
		public function refreshGrid():void
		{
			getPagedData(navigationConfig);
		}
		
		public function get bucketSize():Number
		{
			this._bucketSize = this.navigationConfig.bucketSize;
			return this._bucketSize;
		}
		
		public function set bucketSize(value:Number):void
		{
			this._bucketSize = value;
			this.navigationConfig.bucketSize = this._bucketSize;
		}
		
		public function get auditDescr():String
		{
			return _auditDescr;
		}
		
		public function set auditDescr(value:String):void
		{
			_auditDescr = value;
		}
		
		public function get auditContext():String
		{
			return _auditContext;
		}
		
		public function set auditContext(value:String):void
		{
			_auditContext = value;
		}
		
		public function setPrivilegesForDataGroup(privId:String):void
		{
			if(privId=='1')
			{
				dataControls.enabled = false;				
			}
			else if(privId=='0')
			{
				dataControls.enabled = false;	
				dataControls.visible = false;
			}
		}
	}
}