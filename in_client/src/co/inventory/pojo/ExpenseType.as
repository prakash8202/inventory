package co.inventory.pojo
{
	/**
	* Class is equivalent to Remote class co.pointred.ibatis.pojo.ExpenseType
	* */

	[Bindable]
	[RemoteClass(alias="co.pointred.ibatis.pojo.ExpenseType")]
	public class ExpenseType
	{
		public var expense_type_pid:Number;
		public var expense_type:String;

		public function ExpenseType()
		{
		}
	}
}
