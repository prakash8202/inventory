package co.pointred.fx.gateway.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.pointred.core.constants.SharedConstants;
import co.pointred.core.database.DbManager;
import co.pointred.fx.dataobjects.UserObject;
import co.pointred.ibatis.pojo.Product;
import co.pointred.ibatis.pojo.ProductType;
import flex.messaging.io.amf.ASObject;

public enum ProductManagementSupport
{
	instance;
	
	public UserObject createProductType(UserObject userObject)
    {
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;


		ProductType productType = new ProductType();
		productType.setProduct_type(dataHash.get("product_type"));
		String stmtName = dataHash.remove("IBATIS_STATEMENT");
		boolean create = false;
		try {
			ProductType productTypeInDB = (ProductType) DbManager.instance.selectOneAsObject("PRODUCT_TYPE.SELECT.BY_NAME", dataHash.get("product_type"));
			
			//Save if group name doesnt exists already
			if(productTypeInDB != null)
			{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("ProductType already found.");
			}else{
				create = DbManager.instance.createObject( productType, stmtName);
				if (create == true)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("ProductType created successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in ProductType Creation");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in ProductType Creation : " + e.getMessage());
		}
		return userObject;

    }

	public UserObject updateProductType(UserObject userObject)
    {
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		String stmtName = dataHash.remove("IBATIS_STATEMENT");

		int updateCnt = -1;
		try {
			ProductType productType= (ProductType) DbManager.instance.selectOneAsObject("PRODUCT_TYPE.SELECT.BY_NAME", dataHash.get("product_type"));
			
			//proceed to update if group name already exists
    		if(productType != null && productType.getProduct_type_pid() != Long.parseLong(dataHash.get("product_type_pid")))
			{
    			userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("ProductType already found.");
			}else
			{
				productType= (ProductType) DbManager.instance.selectOneAsObject("PRODUCT_TYPE.SELECT.BY_ID", Long.parseLong(dataHash.get("product_type_pid")));
				productType.setProduct_type(dataHash.get("product_type"));
				updateCnt = DbManager.instance.updateObject(stmtName, productType);
				if (updateCnt > 0)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("ProductType updated successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in ProductType Updation");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in ProductType Updation : " + e.getMessage());
		} 
		return userObject;

    }

	public List<ProductType> fetchProductTypes() 
	{
		List<ProductType> productTypes = new ArrayList<ProductType>();
		
		try {
			List<Object> objList = DbManager.instance.selectAllAsObject("PRODUCT_TYPE.SELECT.ALL");
			
			for (Object object : objList) 
			{
				productTypes.add((ProductType) object);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return productTypes;
	}

	public UserObject createProduct(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		Product product = new Product();
		String stmtName = dataHash.remove("IBATIS_STATEMENT");
		boolean create = false;
		try {
			product.setProduct_name(dataHash.get("product_name"));
			product.setRate(dataHash.get("rate"));
			product.setProduct_type_id(Long.parseLong(dataHash.get("product_type")));
			product.setAvailable(0);
			product.setStart_date(dateFormat.parse(dataHash.get("start_date")));
			
			Product productInDB = (Product) DbManager.instance.selectOneAsObject("PRODUCT.SELECT.BY_NAME", dataHash.get("product_name"));
			
			//Save if group name doesnt exists already
			if(productInDB != null)
			{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Product already found.");
			}else{
				create = DbManager.instance.createObject( product, stmtName);
				
				if (create == true)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("Product created successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Product Creation");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Product Creation : " + e.getMessage());
		} finally
		{
		    
		}
		return userObject;
	}

	public UserObject updateProduct(UserObject userObject)
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		String stmtName = dataHash.remove("IBATIS_STATEMENT");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		int updateCnt = -1;
		try {
			Product product= (Product) DbManager.instance.selectOneAsObject("PRODUCT.SELECT.BY_NAME", dataHash.get("product_name"));
			
			//proceed to update if group name already exists
    		if(product != null && product.getProduct_pid() != Long.parseLong(dataHash.get("product_pid")))
			{
    			userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Product already found.");
			}else
			{
				product= (Product) DbManager.instance.selectOneAsObject("PRODUCT.SELECT.BY_ID", Long.parseLong(dataHash.get("product_pid")));
				product.setProduct_name(dataHash.get("product_name"));
				product.setRate(dataHash.get("rate"));
				product.setProduct_type_id(Long.parseLong(dataHash.get("product_type")));
				product.setStart_date(dateFormat.parse(dataHash.get("start_date")));
				updateCnt = DbManager.instance.updateObject(stmtName, product);
				
				if (updateCnt > 0)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("Product updated successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Product Updation");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Product Updation : " + e.getMessage());
		} finally {
		}
		return userObject;

	}

	public UserObject deleteProductType(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		
		int updateCnt = -1;
		try {
			
			List<Object> objList = DbManager.instance.selectAllAsObject("PRODUCT.SELECT.BY_TYPE_ID", Long.parseLong(dataHash.get("product_type_pid")));
			
			if(objList == null || objList.size() < 1)
			{
				updateCnt = DbManager.instance.deleteRow("PRODUCT_TYPE.DELETE",Long.parseLong(dataHash.get("product_type_pid")));
				
				if (updateCnt > 0)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("ProductType deleted successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in ProductType Deletion");
				}
			}else
			{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Delete products under this type to delete this Product Type.");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in ProductType Deletion : " + e.getMessage());
		} finally {
		}
		
		return userObject;
	}

	public UserObject deleteProduct(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		
		int updateCnt = -1;
		try {
			
			List<Object> puchaseList = DbManager.instance.selectAllAsObject("PURCHASE.SELECT.BY_PRODUCT_ID", Long.parseLong(dataHash.get("product_pid")));
			List<Object> salesList = DbManager.instance.selectAllAsObject("SALES.SELECT.BY_PRODUCT_ID", Long.parseLong(dataHash.get("product_pid")));
			
			if(puchaseList == null || puchaseList.size() < 1 || salesList == null || salesList.size() < 1)
			{
				updateCnt = DbManager.instance.deleteRow("PRODUCT.DELETE",Long.parseLong(dataHash.get("product_pid")));
				
				if (updateCnt > 0)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("Product deleted successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Product Deletion");
				}
			}else
			{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Delete puchase and sales orders of this product to delete this Product.");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Product Deletion : " + e.getMessage());
		} finally {
		}
		
		return userObject;
	}

	public List<Product> fetchProductList() 
	{
		List<Product> productList = new ArrayList<Product>();
		
		try {
			List<Object> objList = DbManager.instance.selectAllAsObject("PRODUCT.SELECT.ALL");
			
			for (Object object : objList) 
			{
				productList.add((Product) object);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return productList;
	}

	public List<Product> fetchProductListForSales() 
	{
		List<Product> productList = new ArrayList<Product>();
		
		try {
			List<Object> objList = DbManager.instance.selectAllAsObject("PRODUCT.SELECT.AVAILABLE");
			
			for (Object object : objList) 
			{
				productList.add((Product) object);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return productList;
	}
}
