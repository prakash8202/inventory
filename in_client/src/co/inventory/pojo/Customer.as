package co.inventory.pojo
{
	/**
	* Class is equivalent to Remote class co.pointred.ibatis.pojo.Customer
	* */

	[Bindable]
	[RemoteClass(alias="co.pointred.ibatis.pojo.Customer")]
	public class Customer
	{
		public var customer_pid:Number;
		public var customer_name:String;
		public var customer_addr:String;
		public var customer_number:String;

		public function Customer()
		{
		}
	}
}
