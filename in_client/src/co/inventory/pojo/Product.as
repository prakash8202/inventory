package co.inventory.pojo
{
	/**
	* Class is equivalent to Remote class co.pointred.ibatis.pojo.Product
	* */

	[Bindable]
	[RemoteClass(alias="co.pointred.ibatis.pojo.Product")]
	public class Product
	{
		public var product_pid:Number;
		public var product_type_id:Number;
		public var product_name:String;
		public var rate:String;
		public var available:Number;
		public var start_date:Date;
		
		public function Product()
		{
		}
	}
}
