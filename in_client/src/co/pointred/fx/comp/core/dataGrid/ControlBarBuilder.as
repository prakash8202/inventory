import co.pointred.fx.comp.core.dataGrid.IconButton;
import co.pointred.fx.comp.utils.AssetsLibrary;

import mx.controls.Spacer;
import mx.events.ListEvent;

import spark.layouts.HorizontalLayout;

public function buildControlBar():void
{
	dataControls = new HGroup()
	dataControls.percentHeight = 100;
	dataControls.percentWidth = 100;
	dataGrid = new DataGrid();
	dataGrid.horizontalScrollPolicy = "true";
	dataGrid.percentHeight =100;
	dataGrid.percentWidth = 100;
	dataGrid.dragEnabled = false;
	dataGrid.sortableColumns = true;
	dataGrid.includeInLayout = true;
	dataGrid.variableRowHeight = true;
	dataGrid.addEventListener(ListEvent.ITEM_CLICK,itemClick)
	this.addElement(dataGrid);
	
	var addBtn:IconButton = new IconButton();
	addBtn.toolTip = "ADD";
	addBtn.operation = "ADD";
	addBtn.icon = AssetsLibrary.addIcon;
	addBtn.addEventListener(MouseEvent.CLICK, validateClick);
	addBtn.setStyle("skinClass",Class(IconButtonSkin));
	
	var modifyBtn:IconButton = new IconButton();
	modifyBtn.toolTip = "MODIFY";
	modifyBtn.operation = "MODIFY";
	modifyBtn.icon = AssetsLibrary.editIcon;
	modifyBtn.addEventListener(MouseEvent.CLICK, validateClick);
	modifyBtn.setStyle("skinClass",Class(IconButtonSkin));
	
	var deleteBtn:IconButton = new IconButton();
	deleteBtn.toolTip = "DELETE";
	deleteBtn.operation = "DELETE";
	deleteBtn.icon = AssetsLibrary.deleteIcon;
	deleteBtn.addEventListener(MouseEvent.CLICK, validateClick);
	deleteBtn.setStyle("skinClass",Class(IconButtonSkin));
	
	dataControls.gap = 1;
	dataControls.addElementAt(addBtn,0);
	dataControls.addElementAt(modifyBtn,1);
	dataControls.addElementAt(deleteBtn,2);
	
	var navControls:HGroup = new HGroup();
	
	var moveFirstButton:IconButton = new IconButton();
	moveFirstButton.icon = AssetsLibrary.firstIcon;
	moveFirstButton.toolTip = "FIRST";
	moveFirstButton.operation = "FIRST";
	moveFirstButton.addEventListener(MouseEvent.CLICK, validateClick);
	moveFirstButton.setStyle("skinClass",Class(IconButtonSkin));
	
	var movePreviousButton:IconButton = new IconButton();
	movePreviousButton.icon = AssetsLibrary.prevIcon;
	movePreviousButton.toolTip = "PREV";
	movePreviousButton.operation = "PREV";
	movePreviousButton.addEventListener(MouseEvent.CLICK, validateClick);
	movePreviousButton.setStyle("skinClass",Class(IconButtonSkin));
	
	var moveNextButton:IconButton = new IconButton();
	moveNextButton.icon = AssetsLibrary.nextIcon;
	moveNextButton.toolTip = "NEXT";
	moveNextButton.operation = "NEXT";
	moveNextButton.addEventListener(MouseEvent.CLICK, validateClick);
	moveNextButton.setStyle("skinClass",Class(IconButtonSkin));
	
	var moveLastButton:IconButton = new IconButton();
	moveLastButton.icon = AssetsLibrary.lastIcon;
	moveLastButton.toolTip = "LAST";
	moveLastButton.operation = "LAST";
	moveLastButton.addEventListener(MouseEvent.CLICK, validateClick);
	moveLastButton.setStyle("skinClass",Class(IconButtonSkin));
	
	var refreshButton:IconButton = new IconButton();
	refreshButton.icon = AssetsLibrary.refreshIcon;
	refreshButton.toolTip = "REFRESH";
	refreshButton.operation = "REFRESH";
	refreshButton.addEventListener(MouseEvent.CLICK, validateClick);
	refreshButton.setStyle("skinClass",Class(IconButtonSkin));
	
	var printButton:IconButton = new IconButton();
	printButton.icon = AssetsLibrary.printIcon;
	printButton.toolTip = "PRINT";
	printButton.operation = "PRINT";
	printButton.addEventListener(MouseEvent.CLICK, validateClick);
	printButton.setStyle("skinClass",Class(IconButtonSkin));
	
	navControls.gap = 1;
	navControls.addElement(moveFirstButton);
	navControls.addElement(movePreviousButton);
	navControls.addElement(moveNextButton);
	navControls.addElement(moveLastButton);
	
	var filterControls:HGroup = new HGroup();
	
	filterCombo = new ComboBox();
	filterCombo.labelField = "label";
	searchText = new TextInput();
	var filterBtn:IconButton = new IconButton();
	filterBtn.icon = AssetsLibrary.filterIcon;
	filterBtn.operation = "FILTER";
	filterBtn.addEventListener(MouseEvent.CLICK, validateClick);
	filterBtn.setStyle("skinClass",Class(IconButtonSkin));
	
	var resetFilterBtn:IconButton = new IconButton();
	resetFilterBtn.icon = AssetsLibrary.removeFilterIcon;
	resetFilterBtn.operation = "RESET_FILTER";
	resetFilterBtn.addEventListener(MouseEvent.CLICK, validateClick);
	resetFilterBtn.setStyle("skinClass",Class(IconButtonSkin));
	
	filterControls.addElement(filterCombo);
	filterControls.addElement(searchText);
	filterControls.addElement(filterBtn);
	filterControls.addElement(resetFilterBtn);
	
	var navLayout:HorizontalLayout = new HorizontalLayout();
	navLayout.gap = 5;
	navLayout.columnWidth = 10;
	navLayout.paddingLeft = 5;
	navLayout.paddingRight = 10;
	navLayout.horizontalAlign = "right";
	
	_additionalButtonsContainer.gap = 1;
	controllers.push(_additionalButtonsContainer);
	
	if(hideDataControls == false)
	{
		controllers.push(dataControls);	
	}
	
	if(hideRefreshButton == false)
	{
		controllers.push(refreshButton);
	}
	controllers.push(printButton);
	
	if(hideFilterControls == false)
	{
		controllers.push(filterControls);
	}
	
	if(hideNavigationControls == false)
	{
		controllers.push(navControls);
	}
	
	this.controlBarContent = controllers;
	this.controlBarLayout = navLayout;
	this.controlBarVisible = hideToolBar;
//	this.setStyle("skinClass",Class(PrDataGridSkin));
}

public function addAdditionalButton(btn:IconButton):void
{
	this._additionalButtonsContainer.addElement(btn);
	this.invalidateDisplayList();
}

public function getButton(operation:String):IconButton
{
	var iconBtn:IconButton;
	if(operation == "ADD")
		iconBtn =  dataControls.getElementAt(0) as IconButton;
	else if(operation == "MODIFY")
		iconBtn =  dataControls.getElementAt(1) as IconButton;
	else if(operation == "DELETE")
		iconBtn =  dataControls.getElementAt(2) as IconButton;
	return iconBtn;
}