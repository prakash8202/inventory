package co.inventory.pojo
{
	/**
	* Class is equivalent to Remote class co.pointred.ibatis.pojo.Expense
	* */

	[Bindable]
	[RemoteClass(alias="co.pointred.ibatis.pojo.Expense")]
	public class Expense
	{
		public var expense_pid:Number;
		public var expense_type_id:Number;
		public var expense_date:Date;
		public var amount:String;

		public function Expense()
		{
		}
	}
}
