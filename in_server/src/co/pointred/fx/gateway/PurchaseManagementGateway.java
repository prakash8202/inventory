package co.pointred.fx.gateway;

import java.util.List;

import co.pointred.fx.dataobjects.UserObject;
import co.pointred.fx.gateway.support.PurchaseManagementSupport;
import co.pointred.fx.gateway.support.SalesManagementSupport;
import co.pointred.ibatis.pojo.Customer;
import co.pointred.ibatis.pojo.Vendor;

public class PurchaseManagementGateway 
{
	////////////////////// VENDOR /////////////////////////
	
	public UserObject createVendor(UserObject userObject)
    {
		return PurchaseManagementSupport.instance.createVendor(userObject);
    }

	public UserObject updateVendor(UserObject userObject)
    {
		return PurchaseManagementSupport.instance.updateVendor(userObject);
    }
	
	public List<Vendor> fetchVendorList()
    {
		return PurchaseManagementSupport.instance.fetchVendorList();
    }
	
    public UserObject deleteVendor(UserObject userObject)
    {
		return PurchaseManagementSupport.instance.deleteVendor(userObject);
    }
    
    public Vendor selectVendor(String vendorId)
    {
		return PurchaseManagementSupport.instance.selectVendor(vendorId);
    }
    
    ////////////////////// PURCHASE /////////////////////////
	
	public UserObject createPurchase(UserObject userObject)
    {
		return PurchaseManagementSupport.instance.createPurchase(userObject);
    }

	public UserObject updatePurchase(UserObject userObject)
    {
		return PurchaseManagementSupport.instance.updatePurchase(userObject);
    }
	
    public UserObject deletePurchase(UserObject userObject)
    {
		return PurchaseManagementSupport.instance.deletePurchase(userObject);
    }
}
