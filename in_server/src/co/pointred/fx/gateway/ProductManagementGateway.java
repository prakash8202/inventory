package co.pointred.fx.gateway;

import java.util.List;

import co.pointred.fx.dataobjects.UserObject;
import co.pointred.fx.gateway.support.ProductManagementSupport;
import co.pointred.ibatis.pojo.Product;
import co.pointred.ibatis.pojo.ProductType;

public class ProductManagementGateway 
{
	////////////////////// PRODUCT TYPE /////////////////////////
	
	public UserObject createProductType(UserObject userObject)
    {
		return ProductManagementSupport.instance.createProductType(userObject);
    }

	public UserObject updateProductType(UserObject userObject)
    {
		return ProductManagementSupport.instance.updateProductType(userObject);
    }
	
	public List<ProductType> fetchProductTypes()
    {
		return ProductManagementSupport.instance.fetchProductTypes();
    }
	
    public UserObject deleteProductType(UserObject userObject)
    {
		return ProductManagementSupport.instance.deleteProductType(userObject);
    }
    
	//////////////////////PRODUCT /////////////////////////
	
	public UserObject createProduct(UserObject userObject)
    {
		return ProductManagementSupport.instance.createProduct(userObject);
    }

	public UserObject updateProduct(UserObject userObject)
    {
		return ProductManagementSupport.instance.updateProduct(userObject);
    }
	
	public UserObject deleteProduct(UserObject userObject)
    {
		return ProductManagementSupport.instance.deleteProduct(userObject);
    }
	public List<Product> fetchProductList()
    {
		return ProductManagementSupport.instance.fetchProductList();
    }
	
	public List<Product> fetchProductListForSales()
    {
		return ProductManagementSupport.instance.fetchProductListForSales();
    }
}
