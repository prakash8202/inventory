package co.inventory.pojo
{
	/**
	* Class is equivalent to Remote class co.pointred.ibatis.pojo.ProductType
	* */

	[Bindable]
	[RemoteClass(alias="co.pointred.ibatis.pojo.ProductType")]
	public class ProductType
	{
		public var product_type_pid:Number;
		public var product_type:String;

		public function ProductType()
		{
		}
	}
}
