package co.pointred.ibatis.pojo;

import java.util.Date;

public class Product
{
	private long product_pid;
	private long product_type_id;
	private String product_name;
	private String rate;
	private int available;
	private Date start_date;
	
	public long getProduct_pid() {
		return product_pid;
	}
	public void setProduct_pid(long product_pid) {
		this.product_pid = product_pid;
	}
	public long getProduct_type_id() {
		return product_type_id;
	}
	public void setProduct_type_id(long product_type_id) {
		this.product_type_id = product_type_id;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public int getAvailable() {
		return available;
	}
	public void setAvailable(int available) {
		this.available = available;
	}
	public Date getStart_date() {
		return start_date;
	}
	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}
	
}
