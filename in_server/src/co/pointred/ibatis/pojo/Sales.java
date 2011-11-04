package co.pointred.ibatis.pojo;

import java.util.Date;

public class Sales 
{
	private long sales_pid;
	private long product_id;
	private long customer_id;
	private Date sales_date;
	private String price;
	private String quantity;
	public long getSales_pid() {
		return sales_pid;
	}
	public void setSales_pid(long sales_pid) {
		this.sales_pid = sales_pid;
	}
	public long getProduct_id() {
		return product_id;
	}
	public void setProduct_id(long product_id) {
		this.product_id = product_id;
	}
	public long getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(long customer_id) {
		this.customer_id = customer_id;
	}
	public Date getSales_date() {
		return sales_date;
	}
	public void setSales_date(Date sales_date) {
		this.sales_date = sales_date;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
}
