package co.pointred.fx.comp.tab.components
{
	import spark.components.NavigatorContent;
	
	public class PrTab extends NavigatorContent
	{
		private var _tabContent:IPrTab;
		
		public function PrTab()
		{
			super();
			this.setStyle("backgroundColor","#E7E7E5");
		}

		public function get tabContent():IPrTab
		{
			return _tabContent;
		}

		public function set tabContent(value:IPrTab):void
		{
			_tabContent = value;
		}

	}
}