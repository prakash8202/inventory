package co.inventory.pojo
{
	/**
	* Class is equivalent to Remote class co.pointred.ibatis.pojo.Purchase
	* */

	[Bindable]
	[RemoteClass(alias="co.pointred.ibatis.pojo.Purchase")]
	public class Purchase
	{
		public var purchase_pid:Number;
		public var product_id:Number;
		public var vendor_id:Number;
		public var purchase_date:Date;
		public var cost:String;
		public var quantity:String;

		public function Purchase()
		{
		}
	}
}
