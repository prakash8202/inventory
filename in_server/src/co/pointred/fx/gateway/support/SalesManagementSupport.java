package co.pointred.fx.gateway.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.pointred.core.constants.SharedConstants;
import co.pointred.core.database.DbManager;
import co.pointred.fx.dataobjects.UserObject;
import co.pointred.ibatis.pojo.Customer;
import co.pointred.ibatis.pojo.Product;
import co.pointred.ibatis.pojo.Sales;
import flex.messaging.io.amf.ASObject;

public enum SalesManagementSupport 
{
	instance;

	public UserObject createCustomer(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;


		Customer customer = new Customer();
		customer.setCustomer_name(dataHash.get("customer_name"));
		customer.setCustomer_addr(dataHash.get("customer_addr"));
		customer.setCustomer_number(dataHash.get("customer_number"));
		String stmtName = dataHash.remove("IBATIS_STATEMENT");
		boolean create = false;
		try {
			Customer customerInDB = (Customer) DbManager.instance.selectOneAsObject("CUSTOMER.SELECT.BY_NAME", dataHash.get("customer_name"));
			
			//Save if group name doesnt exists already
			if(customerInDB != null)
			{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Customer already found.");
			}else{
				create = DbManager.instance.createObject( customer, stmtName);
				if (create == true)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("Customer created successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Customer Creation");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Customer Creation : " + e.getMessage());
		}
		return userObject;
	}

	public UserObject updateCustomer(UserObject userObject)
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		String stmtName = dataHash.remove("IBATIS_STATEMENT");

		int updateCnt = -1;
		try {
			Customer customer= (Customer) DbManager.instance.selectOneAsObject("CUSTOMER.SELECT.BY_NAME", dataHash.get("customer_name"));
			
			//proceed to update if group name already exists
    		if(customer != null && customer.getCustomer_pid() != Long.parseLong(dataHash.get("customer_pid")))
			{
    			userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Customer already found.");
			}else
			{
				customer= (Customer) DbManager.instance.selectOneAsObject("CUSTOMER.SELECT.BY_ID", Long.parseLong(dataHash.get("customer_pid")));
				customer.setCustomer_name(dataHash.get("customer_name"));
				customer.setCustomer_addr(dataHash.get("customer_addr"));
				customer.setCustomer_number(dataHash.get("customer_number"));
				updateCnt = DbManager.instance.updateObject(stmtName, customer);
				if (updateCnt > 0)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("Customer updated successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Customer Updation");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Customer Updation : " + e.getMessage());
		} 
		return userObject;

	}

	public List<Customer> fetchCustomerList() 
	{
		List<Customer> customerList = new ArrayList<Customer>();
		
		try {
			List<Object> objList = DbManager.instance.selectAllAsObject("CUSTOMER.SELECT.ALL");
			
			for (Object object : objList) 
			{
				customerList.add((Customer) object);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return customerList;
	}

	public UserObject deleteCustomer(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		
		int updateCnt = -1;
		try {
			
			List<Object> objList = DbManager.instance.selectAllAsObject("SALES.SELECT.BY_CUSTOMER_ID", Long.parseLong(dataHash.get("customer_pid")));
			
			if(objList == null || objList.size() < 1)
			{
				updateCnt = DbManager.instance.deleteRow("CUSTOMER.DELETE",Long.parseLong(dataHash.get("customer_pid")));
				
				if (updateCnt > 0)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("Customer deleted successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Customer Deletion");
				}
			}else
			{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Delete sales orders under this Customer to delete this Customer.");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Customer Deletion : " + e.getMessage());
		} finally {
		}
		
		return userObject;
	}

	public UserObject createSales(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		Sales sales = new Sales();
		String stmtName = dataHash.remove("IBATIS_STATEMENT");
		boolean create = false;
		int updateCnt = -1;
		try {
			sales.setProduct_id(Long.parseLong(dataHash.get("product_id")));
			sales.setCustomer_id(Long.parseLong(dataHash.get("customer_id")));
			sales.setSales_date(dateFormat.parse(dataHash.get("sales_date")));
			sales.setPrice(dataHash.get("price"));
			sales.setQuantity(dataHash.get("quantity"));
			
			Product productInDB = (Product) DbManager.instance.selectOneAsObject("PRODUCT.SELECT.BY_ID", Long.parseLong(dataHash.get("product_id")));
			productInDB.setAvailable(productInDB.getAvailable() - Integer.parseInt(dataHash.get("quantity")));
			
			create = DbManager.instance.createObject( sales, stmtName);
			if (create == true)
			{
				updateCnt = DbManager.instance.updateObject("PRODUCT.UPDATE",productInDB);
				
				if (updateCnt > 0)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("Sales order added successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Sales order Creation");
				}
			}else{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Error in Sales order Creation");
			}

		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Sales order Creation : " + e.getMessage());
		}
		return userObject;
	}

	public UserObject updateSales(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		String stmtName = dataHash.remove("IBATIS_STATEMENT");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		int oldQuantity = 0;
		
		int updateCnt = -1;
		try {
			Sales sales = (Sales) DbManager.instance.selectOneAsObject("SALES.SELECT.BY_ID", Long.parseLong(dataHash.get("sales_pid")));
			oldQuantity = Integer.parseInt(sales.getQuantity());
			sales.setProduct_id(Long.parseLong(dataHash.get("product_id")));
			sales.setCustomer_id(Long.parseLong(dataHash.get("customer_id")));
			sales.setSales_date(dateFormat.parse(dataHash.get("sales_date")));
			sales.setPrice(dataHash.get("price"));
			sales.setQuantity(dataHash.get("quantity"));
			
			HashMap<String, String> totalSold = DbManager.instance.selectOneAsHashMap("SALES.QUANTITY.BY_PRODUCT_ID", Long.parseLong(dataHash.get("product_id")));
			int sold = Integer.parseInt(totalSold.get("sold").split("\\.")[0]);
			
			Product productInDB = (Product) DbManager.instance.selectOneAsObject("PRODUCT.SELECT.BY_ID", Long.parseLong(dataHash.get("product_id")));
			
			int difference = 0;
			int newValue = 0;
			int newAvailable = 0;
			
			if(oldQuantity < Integer.parseInt(dataHash.get("quantity")))
			{
				difference = (Integer.parseInt(dataHash.get("quantity")) - oldQuantity);
				newValue = sold + difference;
				newAvailable = productInDB.getAvailable() - difference;
			}else if(oldQuantity > Integer.parseInt(dataHash.get("quantity")))
			{
				difference = oldQuantity - Integer.parseInt(dataHash.get("quantity"));
				newValue = sold - difference;
				newAvailable = productInDB.getAvailable() + difference;
			}
			
			if(newValue <= (productInDB.getAvailable() + sold))
			{
				updateCnt = DbManager.instance.updateObject(stmtName, sales);
				if (updateCnt > 0)
				{
					if(difference > 0)
					{
						productInDB.setAvailable(newAvailable);
						updateCnt = DbManager.instance.updateObject("PRODUCT.UPDATE", productInDB);
					}
					
					if (updateCnt > 0)
					{
				    	userObject.setStatus(SharedConstants.SUCCESS);
						userObject.setStatusMsg("Sales order updated successfully!!!");
					}else{
						userObject.setStatus(SharedConstants.FAILURE);
						userObject.setStatusMsg("Error in Sales order Updation");
					}
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Sales order Updation");
				}
			}else{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Entered quantity exceeds total availability of " + productInDB.getProduct_name().toUpperCase());
			}
		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Sales order Updation : " + e.getMessage());
		} 
		return userObject;
	}

	public UserObject deleteSales(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		
		int updateCnt = -1;
		try {
			Sales salesInDB = (Sales) DbManager.instance.selectOneAsObject("SALES.SELECT.BY_ID", Long.parseLong(dataHash.get("sales_pid")));
			long product_id = salesInDB.getProduct_id();
			updateCnt = DbManager.instance.deleteRow("SALES.DELETE",Long.parseLong(dataHash.get("sales_pid")));
			
			if (updateCnt > 0)
			{
				Product productInDB = (Product) DbManager.instance.selectOneAsObject("PRODUCT.SELECT.BY_ID", product_id);
				productInDB.setAvailable(productInDB.getAvailable() + Integer.parseInt(salesInDB.getQuantity()));
				updateCnt = DbManager.instance.updateObject("PRODUCT.UPDATE", productInDB);
				
				if (updateCnt > 0)
				{
					userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("Sales Order deleted successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Sales Order Deletion");
				}
			}else{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Error in Sales Order Deletion");
			}
		}catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Sales Order Deletion : " + e.getMessage());
		} finally {
		}
		
		return userObject;
	}

	public Customer selectCustomer(String customerId) 
	{
		Customer customer = null;
		try {
			customer = (Customer) DbManager.instance.selectOneAsObject("CUSTOMER.SELECT.BY_ID", Long.parseLong(customerId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return customer;
	}
}
