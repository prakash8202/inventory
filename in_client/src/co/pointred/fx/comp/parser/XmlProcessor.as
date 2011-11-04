import co.pointred.fx.comp.collection.HashMap;
import co.pointred.fx.comp.container.PrUiContainer;
import co.pointred.fx.comp.core.PrAlert;
import co.pointred.fx.comp.core.fieldset.PrFieldSet;

import mx.containers.Form;
import mx.containers.TabNavigator;
import mx.core.IVisualElement;

import spark.components.HGroup;
import spark.components.NavigatorContent;
import spark.components.SkinnableContainer;

private function processXML(myXML:XML, iCntnr:ICntnr):Boolean 
{
	
	// iterate thru iCntnr
	for each (var pruicntnr:XML in myXML.pruicntnr)
	{
		var pruicntnrId:String=pruicntnr.@id;	
		var layout:String=pruicntnr.@layout;
		
		// Get the pruiContainer [mainContainer]
		var mainContainer:Object = getFormLayout(layout, mainContainer);
		
		var hashOfTabs:HashMap;
		var hashOfColumns:HashMap;
		var hashOfForm2:HashMap;

		// Main Container 
		var form:Object;
		
		// Sub Container [used when a component is a fieldset]
		var innerForm:Object;
		
		var tabNavigator:TabNavigator;
		var tabName:String = "";
		var multiColumnForm:HGroup;
		
		// Construct a new PrUiContainer with id = pruicntnrId [Step 1]
		var prUiCntnr:PrUiContainer = new PrUiContainer(pruicntnrId);
		
		// Add the new PrUiContainer to the main container [Step 1]
		iCntnr.addPrUiContainer(prUiCntnr);
		
		var isTab:String = pruicntnr.@istab;
		var isMultiColumn:String = pruicntnr.@ismulticolumn;
		
		if("true" == isTab)
		{
			// If the PrUiContainer is a Tabbed Container
			// Initialize tab related components
			hashOfTabs = new HashMap();
			tabNavigator = new TabNavigator();
			
			// To store multiple multicolumnforms for different tabs
			hashOfForm2 = new HashMap();
		}
		else
		{
			// the hash will be initialized but will have only one multicolumnform with key as 'DEFAULT'
			hashOfForm2 = new HashMap();
			multiColumnForm = new HGroup();
			tabName = "DEFAULT"; 
			hashOfForm2.put(tabName, multiColumnForm);
		}
		
		if("true" == isMultiColumn)
		{
			// If the PrUiContainer has multicolumnforms then initialize the hashOfColumns
			hashOfColumns = new HashMap();
		}
		
		// iterate thru components
		for each (var components:XML in pruicntnr.components)
		{
			var fieldSet:PrFieldSet;
			var componentId:String = components.@id; 
			
			if(null == componentId || 0 == componentId.length)
			{
				var str:String = components.toString();
				str = str.substr(0, str.indexOf('>') + 1);
				PrAlert.show("Please Add a component level id :  @ " + str, PrAlert.WARNING_MESSAGE);
				return false;
			}
			
			// Get the components block layout
			layout = components.@layout;
			var colGroup:String;
			
			if(layout.length == 0)
			{
				// If no components block layout is specified then use its parent's [PrUiContainer] layout
				layout = pruicntnr.@layout;
			}
			
			// Get the Container for specified layout
			form = getFormLayout(layout, form);
			
			
			if("true" == isMultiColumn)
			{
				// get the colGroup name
				colGroup = components.@colgroup;
				
				if(colGroup == '' || colGroup.length == 0)
				{
					colGroup = "DEFAULT";
				}
				if("true" == isTab)
				{
					// get the tabname
					tabName = components.@tabgroup;
					
					if(tabName == '' || tabName.length == 0)
					{
						tabName = "DEFAULT";
					}
					
					if(hashOfForm2.containsKey(tabName) == true)
					{
						// If the multicolumn form for the tab is already created get it
						multiColumnForm = hashOfForm2.getValue(tabName);
					}
					else
					{
						// If the multicolumn form for the tab is not created before create one and add it to hashOfForm2
						multiColumnForm = new HGroup();
						hashOfForm2.put(tabName, multiColumnForm);
//						form.addElement(multiColumnForm);
					}
				}
				else
				{
					// If the PrUiContainer is not a Tabbed Container then get the  'DEFAULT' multicolumn form
					multiColumnForm = hashOfForm2.getValue("DEFAULT");
				}
				
				if(multiColumnForm.parent == null)
				{
					form.addElement(multiColumnForm);
				}
			}
			
			if(components.@type == 'fieldset')
			{
				// If the components block is a fieldset change the container to fieldset
				fieldSet = new PrFieldSet();
//				form = new PrFieldSet();
				
//				var fs:PrFieldSet = form as PrFieldSet;
				fieldSet.title = components.@label;
			
//				form = fs;
				
				// Get the inner container of fieldset based on the layout specified
				innerForm = getFormLayout(layout, innerForm);
				iCntnr.addComponent(componentId, fieldSet);
			}
			else
			{
				innerForm = getFormLayout(layout, innerForm);
				iCntnr.addComponent(componentId, innerForm as IVisualElement);
			}
			
			// Iterate through icomp in the components block
			for each(var xmlComp:XML in components.icomp)
			{
				var compType:String=xmlComp.@type;
				var privId:String=xmlComp.privilegeId;
				var iComp:IComp;
				if(privId!="")
				{
					if(userPriv.privilegeHash.containsKey(privId))
					{
						var privFlag:String = userPriv.privilegeHash.getValue(privId);
						iComp=constructUi(xmlComp, innerForm,privFlag);
					}
					else
					{
						iComp=constructUi(xmlComp, innerForm,'0');
					}
				}
				else
				{
					iComp=constructUi(xmlComp, innerForm,"-1");
				}
				
				if(iComp!=null)
				{
					iCntnr.addICompToComponent(componentId, iComp);
					iCntnr.addIComp(pruicntnrId,iComp);
				}
			}
			
			if(isMultiColumn == 'true')
			{
				if(hashOfColumns.containsKey(tabName + ":" + colGroup) == true)
				{
					var column:Form = hashOfColumns.getValue(tabName + ":" + colGroup) as Form;
					if(components.@type == 'fieldset')
					{
						fieldSet.addElement(innerForm as IVisualElement);
						column.addElement(fieldSet  as IVisualElement);
					}
					else
					{
						column.addElement(innerForm  as IVisualElement);
					}					
				}
				else
				{
					var newColumn:Form = new Form();
					newColumn.id = colGroup;
					
					if(components.@type == 'fieldset')
					{
						var nIVE:IVisualElement = innerForm  as IVisualElement; 
						fieldSet.addElement(nIVE);
						newColumn.addElement(fieldSet);
						multiColumnForm.addElement(newColumn);
//						form.addElement(multiColumnForm);
					}
					else
					{
						var iVE:IVisualElement = innerForm  as IVisualElement; 
						newColumn.addElement(iVE);
						multiColumnForm.addElement(newColumn);
//						form.addElement(multiColumnForm);
//						form = multiColumnForm;
					}	
					hashOfColumns.put(tabName + ":" + colGroup, newColumn);
					
				}
			}
			else
			{
				if(components.@type == 'fieldset')
				{
					fieldSet.addElement(innerForm as IVisualElement);
					form.addElement(fieldSet);
				}
				else
				{
					form.addElement(innerForm);
//					form = innerForm;
				}	
			}
			
			if("true" == isTab)
			{
				performTabbing(components,hashOfTabs,form,pruicntnr,tabNavigator);
				iCntnr.setHashOfTabs(hashOfTabs);
			}
			else
			{
				mainContainer.addElement(form);
//				iCntnr.addContainer(mainContainer);
			}
		}
		
		if("true" == isTab)
		{
			tabNavigator.setStyle("focusRoundedCorners", "tl tr");
			mainContainer.addElement(tabNavigator);
		}
		iCntnr.addContainer(mainContainer);
	}
	return true;
}

private function performTabbing(components:XML, hashOfTabs:HashMap, form:Object, pruicntnr:XML, tabNavigator:TabNavigator):void
{
	var tab:NavigatorContent;
	var tabName:String = ""; 
	tabName = components.@tabgroup;
	if(tabName.length <= 0)
	{
		tabName = "DEFAULT";
	}
	var iForm:IVisualElement;
	var sC:SkinnableContainer;
	if(hashOfTabs.containsKey(tabName) == true)
	{
		tab = hashOfTabs.getValue(tabName) as NavigatorContent;
		iForm = form as IVisualElement; 
		sC = new SkinnableContainer();
		sC.addElement(iForm);
		tab.addElement(sC);
	}
	else
	{
		tab = new NavigatorContent();
		tab.layout = getLayout(pruicntnr.@layout);
		tab.label = tabName;
		hashOfTabs.put(tabName, tab);
		iForm = form as IVisualElement; 
		sC = new SkinnableContainer();
		sC.addElement(iForm);
		tab.addElement(sC);
		tabNavigator.addElement(tab);
	}
}