package co.pointred.ibatis.pojo;

public class ExpenseType 
{
	private long expense_type_pid;
	private String expense_type;
	public long getExpense_type_pid() {
		return expense_type_pid;
	}
	public void setExpense_type_pid(long expense_type_pid) {
		this.expense_type_pid = expense_type_pid;
	}
	public String getExpense_type() {
		return expense_type;
	}
	public void setExpense_type(String expense_type) {
		this.expense_type = expense_type;
	}
	
}
