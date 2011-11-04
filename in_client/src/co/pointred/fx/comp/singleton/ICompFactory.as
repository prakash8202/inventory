package co.pointred.fx.comp.singleton
{
	import co.pointred.fx.comp.core.PrButton;
	import co.pointred.fx.comp.core.PrCheckBoxGroup;
	import co.pointred.fx.comp.core.PrChkBox;
	import co.pointred.fx.comp.core.PrComboBox;
	import co.pointred.fx.comp.core.PrDateField;
	import co.pointred.fx.comp.core.PrDisplayList;
	import co.pointred.fx.comp.core.PrDualList;
	import co.pointred.fx.comp.core.PrLabelField;
	import co.pointred.fx.comp.core.PrTextArea;
	import co.pointred.fx.comp.core.dataGrid.PrDataGrid;
	import co.pointred.fx.comp.core.validation.ValidatorService;
	import co.pointred.fx.comp.utils.FrameworkConstants;
	import co.pointred.fx.dataobjects.UserObject;
	
	import mx.collections.ArrayCollection;
	import mx.collections.ArrayList;
	import mx.containers.FormItem;
	import mx.controls.dataGridClasses.DataGridColumn;
	import mx.core.FlexGlobals;
	import mx.managers.CursorManager;
	import mx.utils.ObjectUtil;
	import mx.validators.Validator;
	
	public class ICompFactory
	{
		import co.pointred.fx.comp.core.IComp;
		import co.pointred.fx.comp.core.PrTextField;
		import co.pointred.fx.comp.core.IComp;
		
		private static var instance:ICompFactory;
		
		public static function getInstance():ICompFactory
		{
			if(instance==null)
			{
				instance=new ICompFactory();
			}
			
			return instance;
		}
		
		public function ICompFactory()
		{
			
		}
		
		public function buildIComp(xmlComp:XML,form:Object, privFlag:String):IComp
		{
			var compType:String = xmlComp.@type;
			var iComp:IComp;
			
			if(compType=="textfield")
			{
				iComp=createTextField(xmlComp,form, privFlag);
			}
			else if(compType=="checkboxgroup")
			{
				iComp=createCheckBoxGroup(xmlComp,form, privFlag);
			}
			else if(compType=="combobox")
			{
				iComp=createComboBox(xmlComp,form,privFlag);
			}
			else if(compType=="chkbox")
			{
				iComp=createChkBox(xmlComp,form,privFlag);
			}
			else if(compType=="dataGrid")
			{
				iComp=createDataGrid(xmlComp,form,privFlag);
			}
			else if(compType == "datefield")
			{
				iComp=createDateField(xmlComp,form,privFlag);
			}
			else if(compType=="textarea")
			{
				iComp=createTextArea(xmlComp,form,privFlag);
			}
			else if(compType=="button")
			{
				iComp=createButtons(xmlComp,form,privFlag);
			}
			else if(compType == "displaylist")
			{
				iComp=createDisplayList(xmlComp,form,privFlag);
			}
			else if(compType=="duallist")
			{
				iComp=createDualList(xmlComp,form,privFlag);
			}
			else if(compType=="label")
			{
				iComp=createLabel(xmlComp,form,privFlag);
			}
			else if(compType=="nullcomp")
			{
				iComp=createNullComp(xmlComp,form);
			}
			
			return iComp;
		}
		
		//Creates TextField as FormItem Component
		private function createTextField(icomp:XML,form:Object, privFlag:String):IComp
		{
			var attrName:String = icomp.attrname;	
			var label:String=icomp.label;
			
			var retainoldvalue:String = icomp.retainoldvalue;
			var prTxtFld:PrTextField = new PrTextField();
			
			prTxtFld.setAttrName(attrName);
			
			if(retainoldvalue=="true")
			{
				prTxtFld.setRetainPreviousValue(true);	
			}
			
			// Validation Part
			var restrict:String = "";
			var range:String = "";
			var required:String = "false";
			var validator:String = "";
			var maxChars:Number = 0;
			
			restrict = icomp.restrict;
			range = icomp.range;
			required = icomp.required;
			validator = icomp.validator;
			maxChars = Number(icomp.maxChars);
			
			if(0 < maxChars)
			{
				prTxtFld.maxChars = maxChars;
			}
			
			
			if(null != restrict && "" != restrict && 0 < restrict.length)
			{
				prTxtFld.restrict = restrict;
			}
			
			if(null != required && "" != required && 0 < required.length)
			{
				prTxtFld.required = required;
			}
			else
			{
				required = "false";
				prTxtFld.required = "false";
			}
			
			if(null != range && "" != range && 0 < range.length)
			{
				prTxtFld.range = range;
			}
						
			if(null != validator && "" != validator && 0 < validator.length)
			{
				prTxtFld.iValidator = ValidatorService.getInstance().getValidator(validator);
				var validatorClass:Validator = prTxtFld.iValidator as Validator;
				validatorClass.source = prTxtFld;
				validatorClass.property = "text";
				
				
				if(null != required && "" != required && 0 < required.length)
				{
					validatorClass.required = ("true" == required)?true:false;
				}
			}
			
			var frmItem:FormItem = new FormItem();
			frmItem.label=label;
			frmItem.addElement(prTxtFld);
			frmItem.toolTip = icomp.tooltip;
			// apply privileges
			if(privFlag=='1')
			{
				prTxtFld.editable=false;
				prTxtFld.enabled=false;
				
				form.addElement(frmItem);
			}
			else if(privFlag=='0')
			{
				frmItem.visible=false;
			}
			else
			{
				form.addElement(frmItem);
			}
			return prTxtFld;
		}
		
		//Creates TextField as FormItem Component
		private function createLabel(icomp:XML,form:Object, privFlag:String):IComp
		{
			var attrName:String = icomp.attrname;	
			var label:String=icomp.label;
			var boldLabel:String = icomp.labelbold;
			
			var retainoldvalue:String = icomp.retainoldvalue;
			var prLblFld:PrLabelField = new PrLabelField();
			
			prLblFld.setAttrName(attrName);
			
			var frmItem:FormItem = new FormItem();
			frmItem.label=label;
			
			if(boldLabel == 'true')
			{
				frmItem.setStyle("fontWeight","bold");
				frmItem.setStyle("fontSize","14");
			}
			
			frmItem.addElement(prLblFld);
			frmItem.toolTip = icomp.tooltip;
			// apply privileges
			if(privFlag=='1')
			{
				form.addElement(frmItem);
			}
			else if(privFlag=='0')
			{
				frmItem.visible=false;
			}
			else
			{
				form.addElement(frmItem);
			}
			return prLblFld;
		}
		
		//Creates NullComp as FormItem Component
		private function createNullComp(icomp:XML,form:Object):IComp
		{
			var attrName:String = icomp.attrname;	
			var label:String=icomp.label;
			var retainoldvalue:String = icomp.retainoldvalue;
			var prTxtFld:PrTextField = new PrTextField();
			
			prTxtFld.setAttrName(attrName);
			prTxtFld.text = label;
			var frmItem:FormItem = new FormItem();
			frmItem.addElement(prTxtFld);
			frmItem.visible=false;
			return prTxtFld;
		}
		
		
		//Creates TextField as CheckBoxGroup Component
		private function createCheckBoxGroup(icomp:XML,form:Object, privFlag:String):IComp
		{
			var prCheckBoxGroup:PrCheckBoxGroup = new PrCheckBoxGroup(icomp);
			prCheckBoxGroup.setAttrName(icomp.attrname);
			form.addElement(prCheckBoxGroup);
			
			return prCheckBoxGroup;
		}
		private function createComboBox(icomp:XML,form:Object, privFlag:String):IComp
		{
			var attrName:String = icomp.attrname;	
			var label:String=icomp.label;
			var data:String = "";
			var width:String=icomp.width;
			var required:Boolean = icomp.required;
			if(width=="")
			{
				width="10";
			}
			
			var prCombo:PrComboBox = new PrComboBox();
			var dp:ArrayCollection = new ArrayCollection();
			dp.addItem("Select");
			for each(var dataValue:XML in icomp.dataprovider.data)
			{
				dp.addItem(dataValue);
			}
			prCombo.labelField = "label";
			
			prCombo.setAttrName(attrName);
			prCombo.setRequired(required);
			prCombo.setDataProvider(dp);
			prCombo.width=new Number(width);
			
			var frmItem:FormItem = new FormItem();
			
			frmItem.label=label;
			frmItem.addElement(prCombo);
			frmItem.toolTip = icomp.tooltip;
			// apply privileges
			if(privFlag=='1')
			{
				prCombo.enabled=false;
				form.addElement(frmItem);
			}
			else if(privFlag=='0')
			{
				frmItem.visible=false;
			}
			else
			{
				form.addElement(frmItem);
			}
			return prCombo;
		}
		
		private function createChkBox(icomp:XML,form:Object, privFlag:String):IComp
		{
			var attrName:String = icomp.attrname;	
			var label:String=icomp.label;
			var retainoldvalue:String = icomp.retainoldvalue;
			var prChkBox:PrChkBox = new PrChkBox();
			
			prChkBox.setAttrName(attrName);
			prChkBox.label=attrName;
			if(retainoldvalue=="true")
			{
				prChkBox.setRetainPreviousValue(true);	
			}
			
			var frmItem:FormItem = new FormItem();
			
			frmItem.label=label;
			frmItem.addElement(prChkBox);
			frmItem.toolTip = icomp.tooltip;
			// apply privileges
			if(privFlag=='1')
			{
				prChkBox.enabled=false;
				form.addElement(frmItem);
			}
			else if(privFlag=='0')
			{
				frmItem.visible=false;
			}
			else
			{
				form.addElement(frmItem);
			}
			return prChkBox;
		}
		
		private function createTextArea(icomp:XML,form:Object, privFlag:String):IComp
		{
			var attrName:String = icomp.attrname;	
			var label:String=icomp.label;
			var retainoldvalue:String = icomp.retainoldvalue;
			var prTextArea:PrTextArea = new PrTextArea();
			prTextArea.height = 80;
			
			prTextArea.setAttrName(attrName);
			if(retainoldvalue=="true")
			{
				prTextArea.setRetainPreviousValue(true);	
			}
			
			
			// Validation Part
			var restrict:String = "";
			var range:String = "";
			var required:String = "false";
			var validator:String = "";
			var maxChars:Number = 0;
			
			restrict = icomp.restrict;
			range = icomp.range;
			required = icomp.required;
			validator = icomp.validator;
			maxChars = Number(icomp.maxChars);
			
			if(0 < maxChars)
			{
				prTextArea.maxChars = maxChars;
			}
			
			
			if(null != restrict && "" != restrict && 0 < restrict.length)
			{
				prTextArea.restrict = restrict;
			}
			
			if(null != required && "" != required && 0 < required.length)
			{
				prTextArea.required = required;
			}
			else
			{
				required = "false";
				prTextArea.required = "false";
			}
			
			if(null != range && "" != range && 0 < range.length)
			{
				prTextArea.range = range;
			}
			
			if(null != validator && "" != validator && 0 < validator.length)
			{
				prTextArea.iValidator = ValidatorService.getInstance().getValidator(validator);
				var validatorClass:Validator = prTextArea.iValidator as Validator;
				validatorClass.source = prTextArea;
				validatorClass.property = "text";
				
				
				if(null != required && "" != required && 0 < required.length)
				{
					validatorClass.required = ("true" == required)?true:false;
				}
			}
			
			var frmItem:FormItem = new FormItem();
			frmItem.label=label;
			frmItem.addElement(prTextArea);
			frmItem.toolTip = icomp.tooltip;
			// apply privileges
			if(privFlag=='1')
			{
				prTextArea.enabled=false;
				form.addElement(frmItem);
			}
			else if(privFlag=='0')
			{
				frmItem.visible=false;
			}
			else
			{
				form.addElement(frmItem);
			}
			return prTextArea;
		}
		
		public function createDataGrid(icomp:XML,form:Object, privFlag:String):IComp
		{
			var prDataGrid:PrDataGrid = new PrDataGrid();
			prDataGrid.title = icomp.label;
			prDataGrid.percentHeight = 100;
			prDataGrid.percentWidth = 100;
			var dGColumnArr:Array = new Array();
			var colArr:ArrayList = new ArrayList();
			var sql:String=icomp.sql;
			prDataGrid.sql = sql;
			
			var _bucketSize:Number = Number(icomp.bucketsize);
			prDataGrid.bucketSize = _bucketSize;
			prDataGrid.navigationConfig.bucketSize = _bucketSize;
			
			prDataGrid.navigationConfig.action=FrameworkConstants.FIRST;
			prDataGrid.navigationConfig.audited = false;
			prDataGrid.navigationConfig.currentPage = 1;
			
			var dbType:String = icomp.dbType;
			var attrName:String=icomp.attrname;
			
			if(dbType != "1")
			{
				dbType = "0";
			}
			
			prDataGrid.dbType = dbType;

			var auditCtx:String = icomp.auditCtx;
			var auditDescr:String = icomp.auditDescr;
			
			prDataGrid.setAttrName(attrName);
			
			var controls:XMLList = icomp.controls; 
			var hideDataControls:Boolean = true;
			
			if(controls.children().length() > 0)
			{
				var hideTB:String = "";
				hideTB = icomp.controls.hidebar;
				
				var hideToolBar:Boolean = Boolean(icomp.controls.hidebar == "false");
				if(hideTB.length <= 0)
				{
					hideToolBar = false;
				}
				
				var hideNavigationControls:Boolean = Boolean(icomp.controls.navcontrol == "true");
				hideDataControls = Boolean(icomp.controls.datacontrol == "true");
				var hideFilterControls:Boolean = Boolean(icomp.controls.filtercontrol == "true");
				var hideRefreshButton:Boolean = Boolean(icomp.controls.refresh == "true");
				
				prDataGrid.configureToolBar( hideDataControls, hideFilterControls,hideNavigationControls ,hideRefreshButton ,hideToolBar);	
			}
			else
			{
				prDataGrid.configureToolBar();
			}
			
			
			for each(var xmlColumn:XML in icomp.column)
			{
				var colName:String=xmlColumn.colname;
				
				var dataField:String=xmlColumn.attrname;
				var comboObj:Object = {label:colName, attrName:dataField};
				var hide:String=xmlColumn.hide;
				var width:Number = 0;
				width = xmlColumn.colwidth;
				var dgCol:DataGridColumn = new DataGridColumn();
				if(width == 0)
				{
					width = 75;
				}
				dgCol.width = width;
				dgCol.headerText = colName;
				dgCol.wordWrap = true;
				dgCol.dataField=dataField;
				dGColumnArr.push(dgCol);
				
				if(hide=="true")
				{
					dgCol.visible=false;
				}
				else
				{
					colArr.addItem(comboObj);
				}
			}
			
			prDataGrid.setDataGridColumns(dGColumnArr);
			prDataGrid.setFilterFields(colArr);
			
			if(null != dbType && "" != dbType && 0 < dbType.length)
			{
				prDataGrid.navigationConfig.dbType = dbType;
			}
			prDataGrid.auditContext = auditCtx;
			prDataGrid.auditDescr = auditDescr;
			
			if(sql!="")
			{
				CursorManager.setBusyCursor();
				
				prDataGrid.navigationConfig.sqlString = sql;
				prDataGrid.navigationConfig.action=FrameworkConstants.FIRST;
				prDataGrid.navigationConfig.audited = false;
				prDataGrid.navigationConfig.currentPage = 1;
				
				if(null != dbType && "" != dbType && 0 < dbType.length)
				{
					prDataGrid.navigationConfig.dbType = dbType;
				}
				
				var userObject:UserObject = new UserObject;
				userObject.dataObject = prDataGrid.navigationConfig;
				userObject.auditContext = auditCtx;
				userObject.auditDescr = auditDescr;
				
				prDataGrid.getDataFromSql(userObject);
			}
			
			form.addElement(prDataGrid);
			if(false == hideDataControls)
			{
				prDataGrid.setPrivilegesForDataGroup(privFlag);
			}
			return prDataGrid;
		}
		
		//Creates DateField as FormItem Component
		private function createDateField(icomp:XML,form:Object, privFlag:String):IComp
		{
			var attrName:String = icomp.attrname;	
			var label:String=icomp.label;
			var retainoldvalue:String = icomp.retainoldvalue;
			var prDateFld:PrDateField = new PrDateField(icomp);
			
			prDateFld.setAttrName(attrName);
			if(retainoldvalue=="true")
			{
				prDateFld.setRetainPreviousValue(true);	
			}
			
			var frmItem:FormItem = new FormItem();
			frmItem.label=label;
			frmItem.addElement(prDateFld);
			frmItem.toolTip = icomp.tooltip;
			// apply privileges
			if(privFlag=='1')
			{
				prDateFld.editable=false;
				prDateFld.enabled=false;
				form.addElement(frmItem);
			}
			else if(privFlag=='0')
			{
				frmItem.visible=false;
			}
			else
			{
				form.addElement(frmItem);
			}
			return prDateFld;
		}
		
		private function createButtons(xmlBtn:XML,form:Object, privFlag:String):IComp
		{
			var attrName:String = xmlBtn.attrname;	
			var label:String=xmlBtn.label;
			
			var prBtn:PrButton = new PrButton();
			prBtn.setAttrName(attrName);
			prBtn.setAttrValue(label);
			
			if(privFlag=='1')
			{
				prBtn.enabled=false;
				form.addElement(prBtn);
			}
			else if(privFlag=='0')
			{
				prBtn.visible=false;
			}
			else
			{
				form.addElement(prBtn);
			}
			return prBtn;
		}
		
		private function createDisplayList(icomp:XML,form:Object, privFlag:String):IComp
		{
			var attrName:String = icomp.attrname;	
			var label:String=icomp.label;
			var data:String = "";
			var width:String=icomp.width;
			if(width=="")
			{
				width="100";
			}
			
			var prDisplayList:PrDisplayList = new PrDisplayList();
			prDisplayList.setAttrName(attrName);
			prDisplayList.dataProviderXml = new XML(icomp.dataprovider);
			prDisplayList.width=new Number(width);
			
			var frmItem:FormItem = new FormItem();
			
			frmItem.label=label;
			frmItem.addElement(prDisplayList);
			frmItem.toolTip = icomp.tooltip;
			// apply privileges
			if(privFlag=='0')
			{
				frmItem.visible=false;
			}
			else
			{
				form.addElement(frmItem);
			}
			return prDisplayList;
		}
		
		public function createDualList(icomp:XML,form:Object, privFlag:String):IComp
		{
			var attrName:String = icomp.attrname;	
			var label:String=icomp.label;
			var retainoldvalue:String = icomp.retainoldvalue;
			var handleAsCsvStr:String = icomp.csv;
			var handleAsCsv:Boolean = (handleAsCsvStr == "true")?true:false;
			var prDualList:PrDualList = new PrDualList();
			var frmItem:FormItem = new FormItem();
			frmItem.label=label;
			frmItem.addElement(prDualList);
			frmItem.toolTip = icomp.tooltip;
			prDualList.setAttrName(attrName);
			prDualList.dataXml = new XML(icomp.dataprovider);
			prDualList.handleAsCsv = handleAsCsv;
			// apply privileges
			if(privFlag=='1')
			{
				prDualList.enabled=false;
				form.addElement(frmItem);
			}
			else if(privFlag=='0')
			{
				frmItem.visible=false;
			}
			else
			{
				form.addElement(frmItem);
			}
			return prDualList;
		}
	}
}