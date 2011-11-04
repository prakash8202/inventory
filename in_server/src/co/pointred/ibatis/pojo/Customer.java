package co.pointred.ibatis.pojo;

public class Customer 
{
	private long customer_pid;
	private String customer_name;
	private String customer_addr;
	private String customer_number;
	
	public long getCustomer_pid() {
		return customer_pid;
	}
	public void setCustomer_pid(long customer_pid) {
		this.customer_pid = customer_pid;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getCustomer_addr() {
		return customer_addr;
	}
	public void setCustomer_addr(String customer_addr) {
		this.customer_addr = customer_addr;
	}
	public String getCustomer_number() {
		return customer_number;
	}
	public void setCustomer_number(String customer_number) {
		this.customer_number = customer_number;
	}
	
}
