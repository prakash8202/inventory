package co.pointred.ibatis.pojo;

import java.util.Date;

public class Purchase 
{
	private long purchase_pid;
	private long product_id;
	private long vendor_id;
	private Date purchase_date;
	private String cost;
	private String quantity;
	public long getPurchase_pid() {
		return purchase_pid;
	}
	public void setPurchase_pid(long purchase_pid) {
		this.purchase_pid = purchase_pid;
	}
	public long getProduct_id() {
		return product_id;
	}
	public void setProduct_id(long product_id) {
		this.product_id = product_id;
	}
	public long getVendor_id() {
		return vendor_id;
	}
	public void setVendor_id(long vendor_id) {
		this.vendor_id = vendor_id;
	}
	public Date getPurchase_date() {
		return purchase_date;
	}
	public void setPurchase_date(Date purchase_date) {
		this.purchase_date = purchase_date;
	}
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
}
