package co.pointred.fx.comp.core
{
	import co.pointred.fx.comp.core.dataGrid.IconButton;
	import co.pointred.fx.comp.core.dataGrid.IconButtonSkin;
	import co.pointred.fx.comp.core.validation.IValidator;
	import co.pointred.fx.comp.event.PrEventDispatcher;
	import co.pointred.fx.comp.event.PrICntnrEvent;
	import co.pointred.fx.comp.utils.AssetsLibrary;
	import co.pointred.fx.comp.utils.DataProviderService;
	
	import flash.events.MouseEvent;
	
	import mx.collections.ArrayCollection;
	import mx.containers.ApplicationControlBar;
	import mx.controls.Spacer;
	import mx.events.FlexEvent;
	
	import spark.components.HGroup;
	import spark.components.List;
	import spark.components.VGroup;
	
	public class PrDualList extends HGroup implements IComp
	{
		private var attrName:String;
		private var _viewList:List = new List();
		private var _dataXml:XML;
		private var _viewData:String;
		private var srcList:List = new List();
		private var auditCtx:String;
		private var auditDescr:String;
		private var listenerFunction:Function = null;
		
		private var _masterSql:String;
		private var _selectedSql:String;
		private var _handleAsCsv:Boolean;
		
		private var validationErrMsg:String="OK";
		
		private const MOVE_ALL:String = "MOVE_ALL";
		private const MOVE:String = "MOVE";
		private const REMOVE_ALL:String = "REMOVE_ALL";
		private const REMOVE:String = "REMOVE";
		private const RESET:String = "RESET";
		
		
		
		[Bindable]
		public var srcListDP:ArrayCollection = new ArrayCollection();
		[Bindable]
		public var viewListDP:ArrayCollection = new ArrayCollection();
		
		public function PrDualList()
		{
			super();
			addEventListener(FlexEvent.CREATION_COMPLETE, initUI);
		}
		
		private function initUI(evt:FlexEvent):void
		{
			var listCntrBar:ApplicationControlBar = new ApplicationControlBar();
			var srcListCntnr:VGroup = new VGroup();
			
			this.gap = 0;
			
			this.addElement(_viewList);
			this.addElement(listCntrBar);
			this.addElement(srcListCntnr);
			
			_viewList.dragEnabled = true;
			_viewList.dropEnabled = true;
			_viewList.dragMoveEnabled = true;
			_viewList.allowMultipleSelection = true;
			_viewList.dataProvider = viewListDP;
			
			_viewList.percentWidth = 100;
			_viewList.height = 150;
			_viewList.labelFunction = mapLabels;
			
			srcListCntnr.addElement(srcList);
			
			srcListCntnr.gap = 0;
			srcListCntnr.percentHeight = 100;
			
			srcList.dragEnabled = true;
			srcList.dropEnabled = true;
			srcList.dragMoveEnabled = true;
			srcList.allowMultipleSelection = true;
			srcList.height = 150;
			srcList.dataProvider = srcListDP;
			srcList.labelFunction = mapLabels;
			
			listCntrBar.direction = "VERTICAL";
			applyStyles(listCntrBar);
			listCntrBar.percentHeight = 100;
			var hrule:Spacer = new Spacer();
			hrule.percentHeight = 100;
			var hrule1:Spacer = new Spacer();
			hrule1.percentHeight = 100;
			
			var moveAllBtn:IconButton = new IconButton();
			var moveBtn:IconButton = new IconButton();
			var removeAllBtn:IconButton = new IconButton();
			var removeBtn:IconButton = new IconButton();
			var resetBtn:IconButton = new IconButton();
			
			var vg:VGroup = new VGroup();
			vg.gap = 0;
			
			listCntrBar.addElement(hrule);
			vg.addElement(moveAllBtn);
			vg.addElement(moveBtn);
			vg.addElement(removeBtn);
			vg.addElement(removeAllBtn);
			listCntrBar.addElement(vg);
			listCntrBar.addElement(hrule1);
			listCntrBar.addElement(resetBtn);
			
			moveAllBtn.icon = AssetsLibrary.firstIcon;
			moveAllBtn.toolTip = "Move All";
			moveAllBtn.setStyle("skinClass",Class(IconButtonSkin));
			moveBtn.icon = AssetsLibrary.prevIcon;
			moveBtn.toolTip = "Move";
			moveBtn.setStyle("skinClass",Class(IconButtonSkin));
			removeAllBtn.icon = AssetsLibrary.lastIcon;
			removeAllBtn.toolTip = "Remove All";
			removeAllBtn.setStyle("skinClass",Class(IconButtonSkin));
			removeBtn.icon = AssetsLibrary.nextIcon;
			removeBtn.toolTip = "Remove";
			removeBtn.setStyle("skinClass",Class(IconButtonSkin));
			resetBtn.icon = AssetsLibrary.resetIcon;
			resetBtn.toolTip = "Reset";
			resetBtn.setStyle("skinClass",Class(IconButtonSkin));
			
			moveAllBtn.name = MOVE_ALL;
			moveBtn.name = MOVE;
			removeAllBtn.name = REMOVE_ALL;
			removeBtn.name = REMOVE;
			resetBtn.name = RESET;
			
			moveAllBtn.addEventListener(MouseEvent.CLICK, btnClickHandler);
			moveBtn.addEventListener(MouseEvent.CLICK, btnClickHandler);
			removeAllBtn.addEventListener(MouseEvent.CLICK, btnClickHandler);
			removeBtn.addEventListener(MouseEvent.CLICK, btnClickHandler);
			resetBtn.addEventListener(MouseEvent.CLICK, btnClickHandler);
		}
		
		private function btnClickHandler(evt:MouseEvent):void
		{
			var btn:IconButton = evt.target as IconButton;
			var name:String = btn.name;
			var srcListAc:ArrayCollection = srcList.dataProvider as ArrayCollection;
			var viewListAc:ArrayCollection = _viewList.dataProvider as ArrayCollection;
			switch(name)
			{
				case MOVE_ALL:
					if(srcListAc.length > 0)
					{
						viewListAc.addAll(srcListAc);
						srcListAc.removeAll();
					}
					break;
				case MOVE:
					if(srcList.selectedIndex != -1)
					{
						var selectedItems:Object = srcList.selectedIndices as Object;
						for each(var index:int in selectedItems)
						{
							viewListAc.addItem(srcListAc.getItemAt(index));
							srcListAc.removeItemAt(index);
						}
					}
					break;
				case REMOVE_ALL:
					if(viewListAc.length > 0)
					{
						srcListAc.addAll(viewListAc);
						viewListAc.removeAll();
					}
					break;
				case REMOVE:
					if(_viewList.selectedIndex != -1)
					{
						var viewListSelectedItems:Object = _viewList.selectedIndices as Object;
						for each(var idx:int in viewListSelectedItems)
						{
							srcListAc.addItem(viewListAc.getItemAt(idx));
							viewListAc.removeItemAt(idx);
						}
					}
					break;
				case RESET:
					srcListAc.removeAll();
					viewListAc.removeAll();
					populateLists(this.selectedSql,this.auditCtx,this.auditDescr,this.masterSql,this.listenerFunction);
					break;
			}
		}
		
		private function mapLabels(item:Object):String
		{
			var xmlD:XML = dataXml;
			var returnValue:String = item.toString();
			if(null != dataXml && 0 < dataXml.length())
			{
				returnValue = "" + dataXml.data.(value == returnValue).label;
				returnValue = (0 < returnValue.length)?returnValue:item.toString();
			}
			return returnValue;
		}
		
		private function mapValueToLabel(data:ArrayCollection):ArrayCollection
		{
			var labelData:ArrayCollection = new ArrayCollection();
//			for each(var str:String in viewListDP)
//			{
//				var xmlD:XML = dataXml;
//				var returnValue:String = str;
//				if(null != dataXml && 0 < dataXml.length())
//				{
//					returnValue = "" + dataXml.data.(label == returnValue).value;
//					returnValue = (0 < returnValue.length)?returnValue:item.toString();
//				}
//				
//			}
			
//			return returnValue;
			return labelData;
		}
		
		private function applyStyles(cntrlBar:ApplicationControlBar):void
		{
			cntrlBar.setStyle("dropShadowEnabled", false);
			cntrlBar.setStyle("backgroundColor", 0xefefef);
			cntrlBar.setStyle("cornerRadius", 0);
			cntrlBar.setStyle("paddingLeft", 0);
			cntrlBar.setStyle("paddingRight", 0);
			cntrlBar.setStyle("verticalAlign", "top");
			cntrlBar.setStyle("horizontalAlign", "right");
		}
		
		public function populateLists(selectedSql:String,auditCtx:String, auditDescr:String, masterSql:String = "", listenerFunction:Function = null):void
		{
			this.selectedSql = selectedSql;
			this.masterSql = masterSql;
			if(listenerFunction != null)
			{
				this.listenerFunction = listenerFunction;
				PrEventDispatcher.registerForEvent(auditCtx, listenerFunction);
			}
			var tmpViewList:ArrayCollection = new ArrayCollection();
			DataProviderService.getDataForList(tmpViewList,selectedSql,handleAsCsv,auditCtx + " - Selected List", auditDescr,function ():void
			{
				viewListDP.removeAll();
				for each(var val:String in tmpViewList)
				{
					viewListDP.addItem(val);
				}
				var tmpList:ArrayCollection = new ArrayCollection();
				if(0 < masterSql.length )
				{
					var sd:ArrayCollection = new ArrayCollection();
					DataProviderService.getDataForList(sd, masterSql,false,auditCtx + " - Master List", auditDescr,function ():void
					{
						for each(var str:String in sd)
						{
							var isSelected:Boolean = false;
							for each(var str1:String in viewListDP)
							{
								if(str == str1)
								{
									isSelected = true;
								}
								else
								{
									isSelected = (!isSelected)?false:true; 
								}
							}
							if(!isSelected)
								tmpList.addItem(str);
						}
						srcListDP.addAll(tmpList);
						if(listenerFunction != null)
							PrEventDispatcher.Dispatcher.dispatchEvent(new PrICntnrEvent(auditCtx));
					});
				}
				else
				{
					if(null != dataXml && 0 < dataXml.length())
					{
						var dp:ArrayCollection = new ArrayCollection();
						for each(var dataValue:XML in dataXml.data)
						{
							dp.addItem(dataValue.value);
						}
						for each(var str:String in dp)
						{
							var isSelected:Boolean = false;
							for each(var str1:String in viewListDP)
							{
								if(str == str1)
								{
									isSelected = true;
								}
								else
								{
									isSelected = (!isSelected)?false:true; 
								}
							}
							if(!isSelected)
								tmpList.addItem(str);
						}
						srcListDP.addAll(tmpList);
					}
					if(listenerFunction != null)
						PrEventDispatcher.Dispatcher.dispatchEvent(new PrICntnrEvent(auditCtx));
				}
			});
		}
		
		private function filterMasterAc(item:Object):Boolean
		{
			return !viewListDP.contains(item);
		}
		
		public function get dataXml():XML
		{
			return _dataXml;
		}
		
		public function set dataXml(value:XML):void
		{
			_dataXml = value;
		}
		
		public function getAttrName():String
		{
			return this.attrName;
		}
		
		public function getAttrValue():Object
		{
			var returnObj:Object = this.viewListDP;
			return returnObj;
		}
		
		public function setAttrName(attrName:String):void
		{
			this.attrName = attrName;
		}
		
		public function setAttrValue(attrValue:Object):void
		{
			// Not Used Instead use public function populateLists(selectedSql:String,auditCtx:String, auditDescr:String, masterSql:String = "", listenerFunction:Function = null):void
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
			var obj:Object = getAttrValue();
			var attrName:String=getAttrName();
			objToInject[attrName]=obj;
			return objToInject;
		}
		
		public function resetAttrVal():void
		{
			this.viewListDP.removeAll();
		}
		
		public function getValidator():IValidator
		{
			return null;
		}
					
		public function doValidate():Boolean
		{
			return true;
		}
		
		public function getValidationError():Object
		{
			return null;
		}

		public function get masterSql():String
		{
			return _masterSql;
		}

		public function set masterSql(value:String):void
		{
			_masterSql = value;
		}

		public function get selectedSql():String
		{
			return _selectedSql;
		}

		public function set selectedSql(value:String):void
		{
			_selectedSql = value;
		}

		public function get handleAsCsv():Boolean
		{
			return _handleAsCsv;
		}

		public function set handleAsCsv(value:Boolean):void
		{
			_handleAsCsv = value;
		}


	}
}