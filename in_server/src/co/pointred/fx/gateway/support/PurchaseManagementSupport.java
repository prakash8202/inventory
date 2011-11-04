package co.pointred.fx.gateway.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.pointred.core.constants.SharedConstants;
import co.pointred.core.database.DbManager;
import co.pointred.fx.dataobjects.UserObject;
import co.pointred.ibatis.pojo.Product;
import co.pointred.ibatis.pojo.Purchase;
import co.pointred.ibatis.pojo.Vendor;
import flex.messaging.io.amf.ASObject;
public enum PurchaseManagementSupport 
{
	instance;

	public UserObject createVendor(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;


		Vendor vendor = new Vendor();
		vendor.setVendor_name(dataHash.get("vendor_name"));
		vendor.setVendor_addr(dataHash.get("vendor_addr"));
		vendor.setVendor_number(dataHash.get("vendor_number"));
		String stmtName = dataHash.remove("IBATIS_STATEMENT");
		boolean create = false;
		try {
			Vendor vendorInDB = (Vendor) DbManager.instance.selectOneAsObject("VENDOR.SELECT.BY_NAME", dataHash.get("vendor_name"));
			
			//Save if group name doesnt exists already
			if(vendorInDB != null)
			{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Vendor already found.");
			}else{
				create = DbManager.instance.createObject( vendor, stmtName);
				if (create == true)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("Vendor created successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Vendor Creation");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Vendor Creation : " + e.getMessage());
		}
		return userObject;
	}

	public UserObject updateVendor(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		String stmtName = dataHash.remove("IBATIS_STATEMENT");

		int updateCnt = -1;
		try {
			Vendor vendor= (Vendor) DbManager.instance.selectOneAsObject("VENDOR.SELECT.BY_NAME", dataHash.get("vendor_name"));
			
			//proceed to update if group name already exists
    		if(vendor != null && vendor.getVendor_pid() != Long.parseLong(dataHash.get("vendor_pid")))
			{
    			userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Vendor already found.");
			}else
			{
				vendor= (Vendor) DbManager.instance.selectOneAsObject("VENDOR.SELECT.BY_ID", Long.parseLong(dataHash.get("vendor_pid")));
				vendor.setVendor_name(dataHash.get("vendor_name"));
				vendor.setVendor_addr(dataHash.get("vendor_addr"));
				vendor.setVendor_number(dataHash.get("vendor_number"));
				updateCnt = DbManager.instance.updateObject(stmtName, vendor);
				if (updateCnt > 0)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("Vendor updated successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Vendor Updation");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Vendor Updation : " + e.getMessage());
		} 
		return userObject;

	}

	public List<Vendor> fetchVendorList() 
	{
		List<Vendor> vendorList = new ArrayList<Vendor>();
		
		try {
			List<Object> objList = DbManager.instance.selectAllAsObject("VENDOR.SELECT.ALL");
			
			for (Object object : objList) 
			{
				vendorList.add((Vendor) object);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return vendorList;
	}

	public UserObject deleteVendor(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		
		int updateCnt = -1;
		try {
			
			List<Object> objList = DbManager.instance.selectAllAsObject("PURCHASE.SELECT.BY_VENDOR_ID", Long.parseLong(dataHash.get("vendor_pid")));
			
			if(objList == null || objList.size() < 1)
			{
				updateCnt = DbManager.instance.deleteRow("VENDOR.DELETE",Long.parseLong(dataHash.get("vendor_pid")));
				
				if (updateCnt > 0)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("Vendor deleted successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Vendor Deletion");
				}
			}else
			{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Delete purchase orders under this Vendor to delete this Vendor.");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Vendor Deletion : " + e.getMessage());
		} finally {
		}
		
		return userObject;
	}

	public UserObject createPurchase(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		Purchase purchase = new Purchase();
		String stmtName = dataHash.remove("IBATIS_STATEMENT");
		boolean create = false;
		int updateCnt = -1;
		try {
			purchase.setProduct_id(Long.parseLong(dataHash.get("product_id")));
			purchase.setVendor_id(Long.parseLong(dataHash.get("vendor_id")));
			purchase.setPurchase_date(dateFormat.parse(dataHash.get("purchase_date")));
			purchase.setCost(dataHash.get("cost"));
			purchase.setQuantity(dataHash.get("quantity"));
			
			Product productInDB = (Product) DbManager.instance.selectOneAsObject("PRODUCT.SELECT.BY_ID", Long.parseLong(dataHash.get("product_id")));
			productInDB.setAvailable(productInDB.getAvailable() + Integer.parseInt(dataHash.get("quantity")));
			
			create = DbManager.instance.createObject( purchase, stmtName);
			if (create == true)
			{
				updateCnt = DbManager.instance.updateObject("PRODUCT.UPDATE",productInDB);
				
				if (updateCnt > 0)
				{
					userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("Purchase order added successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Purchase order Creation");
				}
		    	
			}else{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Error in Purchase order Creation");
			}

		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Purchase order Creation : " + e.getMessage());
		}
		return userObject;
	}

	public UserObject updatePurchase(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		String stmtName = dataHash.remove("IBATIS_STATEMENT");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		int oldQuantity = 0;
		int updateCnt = -1;
		try {
			Purchase purchase= (Purchase) DbManager.instance.selectOneAsObject("PURCHASE.SELECT.BY_ID", Long.parseLong(dataHash.get("purchase_pid")));
			oldQuantity = Integer.parseInt(purchase.getQuantity());
			purchase.setProduct_id(Long.parseLong(dataHash.get("product_id")));
			purchase.setVendor_id(Long.parseLong(dataHash.get("vendor_id")));
			purchase.setPurchase_date(dateFormat.parse(dataHash.get("purchase_date")));
			purchase.setCost(dataHash.get("cost"));
			purchase.setQuantity(dataHash.get("quantity"));
			
			HashMap<String, String> totalSold = DbManager.instance.selectOneAsHashMap("SALES.QUANTITY.BY_PRODUCT_ID", Long.parseLong(dataHash.get("product_id")));
			int sold = Integer.parseInt(totalSold.get("sold").split("\\.")[0]);
			
			HashMap<String, String> totalPurchased = DbManager.instance.selectOneAsHashMap("PURCHASE.QUANTITY.BY_PRODUCT_ID", Long.parseLong(dataHash.get("product_id")));
			int purchased = Integer.parseInt(totalPurchased.get("purchased").split("\\.")[0]);
			
			Product productInDB = (Product) DbManager.instance.selectOneAsObject("PRODUCT.SELECT.BY_ID", Long.parseLong(dataHash.get("product_id")));
			
			int difference = 0;
			int newValue = 0;
			int newAvailable = 0;
			
			if(oldQuantity < Integer.parseInt(dataHash.get("quantity")))
			{
				difference = (Integer.parseInt(dataHash.get("quantity")) - oldQuantity);
				newValue = purchased + difference;
				newAvailable = productInDB.getAvailable() + difference;
			}else if(oldQuantity > Integer.parseInt(dataHash.get("quantity")))
			{
				difference = oldQuantity - Integer.parseInt(dataHash.get("quantity"));
				newValue = purchased - difference;
				newAvailable = productInDB.getAvailable() - difference;
			}
			
			if(newValue >= sold)
			{
				updateCnt = DbManager.instance.updateObject(stmtName, purchase);
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
						userObject.setStatusMsg("Purchase order updated successfully!!!");
					}else{
						userObject.setStatus(SharedConstants.FAILURE);
						userObject.setStatusMsg("Error in Purchase order Updation");
					}
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Purchase order Updation");
				}
			}else{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Entered quantity is less than total quantity sold for " + productInDB.getProduct_name().toUpperCase());
			}
		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Purchase order Updation : " + e.getMessage());
		} 
		return userObject;

	}

	public UserObject deletePurchase(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		
		int updateCnt = -1;
		try {
			Purchase purchaseInDB = (Purchase) DbManager.instance.selectOneAsObject("PURCHASE.SELECT.BY_ID", Long.parseLong(dataHash.get("purchase_pid")));
			long product_id = purchaseInDB.getProduct_id();

			HashMap<String, String> totalSold = DbManager.instance.selectOneAsHashMap("SALES.QUANTITY.BY_PRODUCT_ID", product_id);
			int sold = Integer.parseInt(totalSold.get("sold").split("\\.")[0]);
			
			HashMap<String, String> totalPurchased = DbManager.instance.selectOneAsHashMap("PURCHASE.QUANTITY.BY_PRODUCT_ID", product_id);
			int purchased = Integer.parseInt(totalPurchased.get("purchased").split("\\.")[0]);
			
			Product productInDB = (Product) DbManager.instance.selectOneAsObject("PRODUCT.SELECT.BY_ID", product_id);
			
			if((purchased - Integer.parseInt(purchaseInDB.getQuantity())) >= sold)
			{
				updateCnt = DbManager.instance.deleteRow("PURCHASE.DELETE",Long.parseLong(dataHash.get("purchase_pid")));
				
				if (updateCnt > 0)
				{
					productInDB.setAvailable(productInDB.getAvailable() - Integer.parseInt(purchaseInDB.getQuantity()));
					updateCnt = DbManager.instance.updateObject("PRODUCT.UPDATE", productInDB);
					
					if (updateCnt > 0)
					{
				    	userObject.setStatus(SharedConstants.SUCCESS);
						userObject.setStatusMsg("Purchase Order deleted successfully!!!");
					}else{
						userObject.setStatus(SharedConstants.FAILURE);
						userObject.setStatusMsg("Error in Purchase Order Deletion");
					}
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Purchase Order Deletion");
				}
			}else{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Deleting this purchase data makes sold quantity for "+ productInDB.getProduct_name().toUpperCase() + " greater than purchase quantity.");
			}
		}catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Purchase Order Deletion : " + e.getMessage());
		} finally {
		}
		
		return userObject;
	}

	public Vendor selectVendor(String vendorId) 
	{
		Vendor vendor = null;
		try {
			vendor = (Vendor) DbManager.instance.selectOneAsObject("VENDOR.SELECT.BY_ID", Long.parseLong(vendorId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vendor;
	}

}
