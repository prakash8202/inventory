package co.pointred.fx.gateway;

import java.util.List;

import co.pointred.fx.dataobjects.UserObject;
import co.pointred.fx.gateway.support.SalesManagementSupport;
import co.pointred.ibatis.pojo.Customer;

public class SalesManagementGateway 
{
	////////////////////// CUSTOMER /////////////////////////
	
	public UserObject createCustomer(UserObject userObject)
    {
		return SalesManagementSupport.instance.createCustomer(userObject);
    }

	public UserObject updateCustomer(UserObject userObject)
    {
		return SalesManagementSupport.instance.updateCustomer(userObject);
    }
	
	public List<Customer> fetchCustomerList()
    {
		return SalesManagementSupport.instance.fetchCustomerList();
    }
	
    public UserObject deleteCustomer(UserObject userObject)
    {
		return SalesManagementSupport.instance.deleteCustomer(userObject);
    }
    
    public Customer selectCustomer(String customerId)
    {
		return SalesManagementSupport.instance.selectCustomer(customerId);
    }
    
    ////////////////////// SALES /////////////////////////
    
    public UserObject createSales(UserObject userObject)
    {
		return SalesManagementSupport.instance.createSales(userObject);
    }

	public UserObject updateSales(UserObject userObject)
    {
		return SalesManagementSupport.instance.updateSales(userObject);
    }
	
    public UserObject deleteSales(UserObject userObject)
    {
		return SalesManagementSupport.instance.deleteSales(userObject);
    }
}
