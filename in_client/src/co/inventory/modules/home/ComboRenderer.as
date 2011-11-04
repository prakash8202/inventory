package co.inventory.modules.home
{
	import co.pointred.fx.comp.collection.HashMap;
	
	import mx.collections.ArrayCollection;
	import mx.containers.HBox;
	import mx.controls.Label;
	import mx.controls.Spacer;
	import mx.core.IFactory;
	import mx.core.mx_internal;
	
	import spark.components.DropDownList;
	import spark.events.IndexChangeEvent;
	
	use namespace mx_internal;
	
	/**
	 * @author Prakash R
	 **/
	public class ComboRenderer extends HBox implements IFactory
	{
		private var list:DropDownList;
		private var colorHash:HashMap = new HashMap();
		private var colorBox:HBox;
		
		public function ComboRenderer()
		{
			super();
			colorHash.put(0,"#981701");
			colorHash.put(1,"#007837");
			
		}
		
		override public function set data(value:Object) : void
		{
			if(value == null){
				// clear all the controls
				return;
			}
			super.data = value;
			this.removeAllElements();
			
			var color:int = 1;
			
			if(data['profit'].toString().indexOf('-') > -1)
			{
				color = 0;
			}
			
			var lbl:Label = new Label();
			lbl.text = data['profit'];
			this.addElement(lbl);
			
			var spacer:Spacer = new Spacer();
			spacer.percentWidth = 100;
			this.addElement(spacer);
			
			colorBox = new HBox();
			var dummyLbl:Label = new Label();
			colorBox.addElement(dummyLbl);
			colorBox.setStyle("backgroundColor",colorHash.getValue(color));
			colorBox.setStyle("float","right");
			this.addElement(colorBox);
		}
		
		public function newInstance():*
		{
			return new ComboRenderer();
		}
	}
}