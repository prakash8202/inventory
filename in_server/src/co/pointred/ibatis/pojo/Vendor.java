package co.pointred.ibatis.pojo;

public class Vendor 
{
	private long vendor_pid;
	private String vendor_name;
	private String vendor_addr;
	private String vendor_number;
	public long getVendor_pid() {
		return vendor_pid;
	}
	public void setVendor_pid(long vendor_pid) {
		this.vendor_pid = vendor_pid;
	}
	public String getVendor_name() {
		return vendor_name;
	}
	public void setVendor_name(String vendor_name) {
		this.vendor_name = vendor_name;
	}
	public String getVendor_addr() {
		return vendor_addr;
	}
	public void setVendor_addr(String vendor_addr) {
		this.vendor_addr = vendor_addr;
	}
	public String getVendor_number() {
		return vendor_number;
	}
	public void setVendor_number(String vendor_number) {
		this.vendor_number = vendor_number;
	}
	
}
