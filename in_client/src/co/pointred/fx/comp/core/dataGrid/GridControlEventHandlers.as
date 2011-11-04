import co.pointred.fx.comp.core.PrAlert;
import co.pointred.fx.comp.core.dataGrid.NavigationConfig;
import co.pointred.fx.comp.event.PrDatagridEvent;
import co.pointred.fx.comp.utils.FrameworkConstants;
import co.pointred.fx.comp.utils.StringUtils;
import co.pointred.fx.comp.utils.print.PrPrintManager;
import co.pointred.fx.dataobjects.UserObject;

import flash.events.MouseEvent;

import mx.containers.TitleWindow;
import mx.core.FlexGlobals;
import mx.events.CloseEvent;
import mx.managers.PopUpManager;
import mx.utils.ObjectUtil;

import spark.components.ComboBox;

private function validateClick(event:MouseEvent):void
{
	var clickedBtn:IconButton=event.target as IconButton;
	var operation:String=clickedBtn.operation;
//	Alert.show(clickedBtn.operation);
	switch (operation)
	{
		case "ADD":
			dispatchEvent(new PrDatagridEvent(PrDatagridEvent.ADD_CLICKED, "ADD", false, false));
			break;
		case "MODIFY":
			if (dataGrid.selectedItems.length == 1)
			{
				dispatchEvent(new PrDatagridEvent(PrDatagridEvent.EDIT_CLICKED, dataGrid.selectedItem, false, false));
			}
			else if(dataGrid.selectedItems.length == 0)
			{
				PrAlert.show("Select atleast One Row for Modification",PrAlert.INFO_MESSAGE);
			}
			else			
			{
				PrAlert.show("Only One Row Is Allowed To Perform Modification At A Time",PrAlert.INFO_MESSAGE);
			}
			break;
		case "DELETE":
			if (dataGrid.selectedItems.length > 0)
			{
				PrAlert.confirm("Are you sure you want to delete?", "Confirm Delete Operation",this, function onClose(event:CloseEvent):void
				{
//					var titleWindow:TitleWindow = event.target.parent.parent as TitleWindow;
//					PopUpManager.removePopUp(titleWindow);
//					var button:mx.controls.Button=event.target as mx.controls.Button;
//					if (button.label == PrAlert.OK)
					if(event.detail == PrAlert.CONFIRM_YES)
					{
						dispatchEvent(new PrDatagridEvent(PrDatagridEvent.DELETE_CLICKED, dataGrid.selectedItem, false, false));
					}
					else
					{
						//do nothing
					}
				});
			}
			else
			{
				PrAlert.show("Select Atleast One Row For Deletion",PrAlert.INFO_MESSAGE);
			}
			break;
		case "FIRST":
			navigationConfig.action=FrameworkConstants.FIRST;
			if(navigationConfig.currentPage > 1)
			{
				navigationConfig.currentPage = 1;
				getPagedData(navigationConfig);
			}
			else
			{
				PrAlert.show("First Page",PrAlert.INFO_MESSAGE);
			}
			break;
		case "PREV":
			navigationConfig.action=FrameworkConstants.PREVIOUS;
			if(navigationConfig.currentPage > 1)
			{
				navigationConfig.currentPage--;
				getPagedData(navigationConfig);
			}
			else
			{
				PrAlert.show("First Page",PrAlert.INFO_MESSAGE);
			}
			break;
		case "NEXT":
			navigationConfig.action=FrameworkConstants.NEXT;
			if(navigationConfig.currentPage < navigationConfig.totalPages)
			{
				navigationConfig.currentPage++;
				getPagedData(navigationConfig);
			}
			else
			{
				PrAlert.show("Last Page",PrAlert.INFO_MESSAGE);
			}
			break;
		case "LAST":
			navigationConfig.action=FrameworkConstants.LAST;
			if(navigationConfig.currentPage < navigationConfig.totalPages)
			{
				navigationConfig.currentPage = navigationConfig.totalPages;
				getPagedData(navigationConfig);	
			}
			else
			{
				PrAlert.show("Last Page",PrAlert.INFO_MESSAGE);
			}
			break;
		case "REFRESH":
			getPagedData(navigationConfig);
			break;
		case "FILTER":
			navigationConfig.action=FrameworkConstants.FIRST;
			navigationConfig.currentPage = 1;
			
			var filteredSql:String=sql;
			var filter:ComboBox=filterCombo;
			var selectItemObj:Object = filterCombo.selectedItem;
			var filterCtx:String = "";
			if(null != selectItemObj)
			filterCtx = filterCombo.selectedItem.attrName;
			var filterTxt:String=searchText.text;

			if (filterCtx.length > 0 && filterTxt.length > 0)
			{
				if("0" == dbType)
				{
					var filterString:String=filterCtx + " LIKE '" + filterTxt + "%'";
					if (true == StringUtils.contains(filteredSql, "WHERE"))
					{
						filteredSql=filteredSql + " AND " + filterString;
					}
					else
					{
						filteredSql=filteredSql + " WHERE " + filterString;
					}
				}else
				{
					var mongoFilterString:String=filterCtx + ":" + filterTxt;
					filteredSql=filteredSql + ";" + mongoFilterString;
				}
			}
			navigationConfig.sqlString=filteredSql;
			getPagedData(navigationConfig);
			break;
		case "RESET_FILTER":
			navigationConfig.sqlString=sql;
			navigationConfig.action=FrameworkConstants.REFRESH;
			navigationConfig.currentPage = 1;
			
			filterCombo.selectedIndex=-1;
			searchText.text="";
			getPagedData(navigationConfig);
			break;
		case "PRINT":
//			var printJob:FlexPrintJob = new FlexPrintJob();
//			if(true == printJob.start())
//			{
//				var printGrid:PrintDataGrid = new PrintDataGrid();
//				printGrid.dataProvider = getDataGrid().dataProvider;
//				printGrid.columns = getDataGrid().columns;
////				FlexGlobals.topLevelApplication.addChild(printGrid);
//				printJob.addObject(printGrid);
//				while(true == printGrid.validNextPage)
//				{
//					printGrid.nextPage();
//					printJob.addObject(printGrid);	
//				}
//				printJob.send();
//				FlexGlobals.topLevelApplication.removeChild(printGrid);
//			}
			PrPrintManager.getInstance().printPage(getDataGrid());
			break;
	}
}

private function itemClick(event:ListEvent):void
{
	if (dataGrid.selectedItems.length == 1)
	{
		dispatchEvent(new PrDatagridEvent(PrDatagridEvent.GRID_ITEM_CLICKED, dataGrid.selectedItem, false, false));
	}
//	else
//	{
//		Alert.show("Only One Row Is Allowed To Perform Modification At A Time 2");
//	}	
}

/**
 * API to update userobject and initiate get data
 **/
private function getPagedData(navigationConfig:NavigationConfig):void
{
	var t1:String = this._auditContext;
	var t2:String = this._auditDescr;
	if(null != navigationConfig.dataList)
	{
		navigationConfig.dataList.removeAll();
	}
	var userObject:UserObject= new UserObject;
	userObject.dataObject=navigationConfig;
	userObject.auditContext = this.auditContext;
	userObject.auditDescr = this.auditDescr;
	getDataFromSql(userObject);
}