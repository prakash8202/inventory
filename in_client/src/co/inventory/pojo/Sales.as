package co.inventory.pojo
{
	/**
	* Class is equivalent to Remote class co.pointred.ibatis.pojo.Sales
	* */

	[Bindable]
	[RemoteClass(alias="co.pointred.ibatis.pojo.Sales")]
	public class Sales
	{
		public var sales_pid:Number;
		public var product_id:Number;
		public var customer_id:Number;
		public var sales_date:Date;
		public var price:String;
		public var quantity:String;

		public function Sales()
		{
		}
	}
}
