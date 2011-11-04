package co.inventory.pojo
{
	/**
	* Class is equivalent to Remote class co.pointred.ibatis.pojo.Vendor
	* */

	[Bindable]
	[RemoteClass(alias="co.pointred.ibatis.pojo.Vendor")]
	public class Vendor
	{
		public var vendor_pid:Number;
		public var vendor_name:String;
		public var vendor_addr:String;
		public var vendor_number:String;

		public function Vendor()
		{
		}
	}
}
