package co.pointred.fx.gateway.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.pointred.core.constants.SharedConstants;
import co.pointred.core.database.DbManager;
import co.pointred.fx.dataobjects.UserObject;
import co.pointred.ibatis.pojo.Expense;
import co.pointred.ibatis.pojo.ExpenseType;
import flex.messaging.io.amf.ASObject;

public enum ExpenseManagementSupport
{
	instance;

	public UserObject createExpenseType(UserObject userObject)
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;


		ExpenseType expenseType = new ExpenseType();
		expenseType.setExpense_type(dataHash.get("expense_type"));
		String stmtName = dataHash.remove("IBATIS_STATEMENT");
		boolean create = false;
		try {
			ExpenseType expenseTypeInDB = (ExpenseType) DbManager.instance.selectOneAsObject("EXPENSE_TYPE.SELECT.BY_NAME", dataHash.get("expense_type"));
			
			//Save if group name doesnt exists already
			if(expenseTypeInDB != null)
			{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("ExpenseType already found.");
			}else{
				create = DbManager.instance.createObject( expenseType, stmtName);
				if (create == true)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("ExpenseType created successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in ExpenseType Creation");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in ExpenseType Creation : " + e.getMessage());
		}
		return userObject;
	}

	public UserObject updateExpenseType(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		String stmtName = dataHash.remove("IBATIS_STATEMENT");

		int updateCnt = -1;
		try {
			ExpenseType expenseType= (ExpenseType) DbManager.instance.selectOneAsObject("EXPENSE_TYPE.SELECT.BY_NAME", dataHash.get("expense_type"));
			
			//proceed to update if group name already exists
    		if(expenseType != null && expenseType.getExpense_type_pid() != Long.parseLong(dataHash.get("expense_type_pid")))
			{
    			userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("ExpenseType already found.");
			}else
			{
				expenseType= (ExpenseType) DbManager.instance.selectOneAsObject("EXPENSE_TYPE.SELECT.BY_ID", Long.parseLong(dataHash.get("expense_type_pid")));
				expenseType.setExpense_type(dataHash.get("expense_type"));
				updateCnt = DbManager.instance.updateObject(stmtName, expenseType);
				if (updateCnt > 0)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("ExpenseType updated successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in ExpenseType Updation");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in ExpenseType Updation : " + e.getMessage());
		} 
		return userObject;
	}

	public List<ExpenseType> fetchExpenseTypes() 
	{
		List<ExpenseType> expenseTypes = new ArrayList<ExpenseType>();
		
		try {
			List<Object> objList = DbManager.instance.selectAllAsObject("EXPENSE_TYPE.SELECT.ALL");
			
			for (Object object : objList) 
			{
				expenseTypes.add((ExpenseType) object);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return expenseTypes;
	}

	public UserObject createExpense(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		Expense expense = new Expense();
		boolean create = false;
		try {
			expense.setExpense_date(dateFormat.parse(dataHash.get("expense_date")));
			expense.setAmount(dataHash.get("amount"));
			expense.setExpense_type_id(Long.parseLong(dataHash.get("expense_type")));
			String stmtName = dataHash.remove("IBATIS_STATEMENT");
			
			create = DbManager.instance.createObject( expense, stmtName);
			
			if (create == true)
			{
		    	userObject.setStatus(SharedConstants.SUCCESS);
				userObject.setStatusMsg("Expense created successfully!!!");
			}else{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Error in Expense Creation");
			}

		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Expense Creation : " + e.getMessage());
		} finally
		{
		    
		}
		return userObject;
	}

	public UserObject updateExpense(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		String stmtName = dataHash.remove("IBATIS_STATEMENT");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		int updateCnt = -1;
		try {
			Expense expense= (Expense) DbManager.instance.selectOneAsObject("EXPENSE.SELECT.BY_ID", Long.parseLong(dataHash.get("expense_pid")));
			if(expense != null)
			{
				expense.setExpense_date(dateFormat.parse(dataHash.get("expense_date")));
				expense.setAmount(dataHash.get("amount"));
				expense.setExpense_type_id(Long.parseLong(dataHash.get("expense_type")));
				updateCnt = DbManager.instance.updateObject(stmtName, expense);
				
				if (updateCnt > 0)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("Expense updated successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in Expense Updation");
				}
			}else
			{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Error in Expense Updation. Data not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Expense Updation : " + e.getMessage());
		} finally {
		}
		return userObject;
	}

	public UserObject deleteExpense(UserObject userObject) 
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		
		int updateCnt = -1;
		try {
			updateCnt = DbManager.instance.deleteRow("EXPENSE.DELETE",Long.parseLong(dataHash.get("expense_pid")));
			
			if (updateCnt > 0)
			{
		    	userObject.setStatus(SharedConstants.SUCCESS);
				userObject.setStatusMsg("Expense deleted successfully!!!");
			}else{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Error in Expense Deletion");
			}
		}catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in Expense Deletion : " + e.getMessage());
		} finally {
		}
		
		return userObject;
	}

	public UserObject deleteExpenseType(UserObject userObject)
	{
		ASObject asObject = (ASObject) userObject.getDataObject();
		HashMap<String, String> dataHash = asObject;
		
		int updateCnt = -1;
		try {
			
			List<Object> objList = DbManager.instance.selectAllAsObject("EXPENSE.SELECT.BY_TYPE_ID", Long.parseLong(dataHash.get("expense_type_pid")));
			
			if(objList == null || objList.size() < 1)
			{
				updateCnt = DbManager.instance.deleteRow("EXPENSE_TYPE.DELETE",Long.parseLong(dataHash.get("expense_type_pid")));
				
				if (updateCnt > 0)
				{
			    	userObject.setStatus(SharedConstants.SUCCESS);
					userObject.setStatusMsg("ExpenseType deleted successfully!!!");
				}else{
					userObject.setStatus(SharedConstants.FAILURE);
					userObject.setStatusMsg("Error in ExpenseType Deletion");
				}
			}else
			{
				userObject.setStatus(SharedConstants.FAILURE);
				userObject.setStatusMsg("Delete expenses under this type to delete this Expense Type.");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			userObject.setStatus(SharedConstants.FAILURE);
			userObject.setStatusMsg("Error in ExpenseType Deletion : " + e.getMessage());
		} finally {
		}
		
		return userObject;
	}
}
