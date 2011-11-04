package co.pointred.fx.comp.core.dataGrid
{
	import co.pointred.fx.comp.core.IComp;
	import co.pointred.fx.comp.core.validation.IValidator;
	
	import mx.controls.dataGridClasses.DataGridColumn;
	
	public class PrDataGrid extends PrDataGridComponent implements IComp
	{
		
		private var attrName:String;
		
		
		public function PrDataGrid()
		{
			super();
		}
		
		public function getAttrName():String
		{
			return this.attrName;
		}
		
		public function getAttrValue():Object
		{
			return null;
		}
		
		public function setAttrName(attrName:String):void
		{
			this.attrName=attrName;	
		}
		
		public function setAttrValue(attrValue:Object):void
		{
			getDataGrid().dataProvider = attrValue;
		}
		
		public function getValidationError():Object
		{
			return null;
		}
		
		public function doValidate():Boolean
		{
			return false;
		}
		
		public function resetValidationFlag():void
		{
		}
		
		public function setRetainPreviousValue(retainPrevValue:Boolean):void
		{
		}
		
		public function isRetainPreviousValue():Boolean
		{
			return false;
		}
		
		public function getPreviousValue():Object
		{
			return null;
		}
		
		public function getValueForDb(objToInject:Object):Object
		{
			return getDataGrid().dataProvider;
		}
		
		public function resetAttrVal():void
		{
			getDataGrid().dataProvider = new Object();
		}
				
		public function getValidator():IValidator
		{
			return null;
		}
		
		public function getGridColumn(columnId:String):DataGridColumn
		{
			var columns:Array = super.getDataGrid().columns;
			for each(var column:DataGridColumn in columns)
			{
				if(column.dataField == columnId)
					return column;
			}
			return null;
		}
		
		override public function refreshGrid():void
		{
			super.refreshGrid();
		}
		
		public function configureToolBar(hideDataControls:Boolean = false, hideFilterControls:Boolean = false, hideNavigationControls:Boolean = false, hideRefreshButton:Boolean = false, hideToolBar:Boolean = true):void
		{
			super.hideDataControls = hideDataControls;
			super.hideFilterControls = hideFilterControls;
			super.hideNavigationControls = hideNavigationControls;
			super.hideRefreshButton = hideRefreshButton;
			super.hideToolBar = hideToolBar;
			
			super.buildControlBar();
		}

		/**
		 **** To Add a additional button to Datagrid Control Bar
		 * var addBtn:IconButton = new IconButton();
		 * addBtn.toolTip = "ADD";
		 * addBtn.operation = "ADD";
		 * addBtn.icon = AssetsLibrary.addIcon;
		 * addBtn.addEventListener(MouseEvent.CLICK, function (evt:MouseEvent):void
		 * {
		 * Alert.show("Additional Btn Clicked");
		 * });
		 * addBtn.setStyle("skinClass",Class(IconButtonSkin));
		 * currentAlarmGrid.addAdditionalButton(addBtn);
		 * 
		 **/
		override public function addAdditionalButton(btn:IconButton):void
		{
			super.addAdditionalButton(btn);
		}
		
		override public function getButton(operation:String):IconButton
		{
			return super.getButton(operation);
		}
	}
}