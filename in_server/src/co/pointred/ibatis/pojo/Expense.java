package co.pointred.ibatis.pojo;

import java.util.Date;

public class Expense 
{
	private long expense_pid;
	private long expense_type_id;
	private Date expense_date;
	private String amount;
	
	public long getExpense_pid() {
		return expense_pid;
	}
	public void setExpense_pid(long expense_pid) {
		this.expense_pid = expense_pid;
	}
	public long getExpense_type_id() {
		return expense_type_id;
	}
	public void setExpense_type_id(long expense_type_id) {
		this.expense_type_id = expense_type_id;
	}
	public Date getExpense_date() {
		return expense_date;
	}
	public void setExpense_date(Date expense_date) {
		this.expense_date = expense_date;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
}
