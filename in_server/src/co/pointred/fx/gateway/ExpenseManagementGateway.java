package co.pointred.fx.gateway;

import java.util.List;

import co.pointred.fx.dataobjects.UserObject;
import co.pointred.fx.gateway.support.ExpenseManagementSupport;
import co.pointred.ibatis.pojo.ExpenseType;

public class ExpenseManagementGateway 
{
	////////////////////// EXPENSE TYPE /////////////////////////
	
	public UserObject createExpenseType(UserObject userObject)
    {
		return ExpenseManagementSupport.instance.createExpenseType(userObject);
    }

	public UserObject updateExpenseType(UserObject userObject)
    {
		return ExpenseManagementSupport.instance.updateExpenseType(userObject);
    }
	
	public List<ExpenseType> fetchExpenseTypes()
    {
		return ExpenseManagementSupport.instance.fetchExpenseTypes();
    }
	
	public UserObject deleteExpenseType(UserObject userObject)
    {
		return ExpenseManagementSupport.instance.deleteExpenseType(userObject);
    }
	
	//////////////////////EXPENSE /////////////////////////
	
	public UserObject createExpense(UserObject userObject)
    {
		return ExpenseManagementSupport.instance.createExpense(userObject);
    }

	public UserObject updateExpense(UserObject userObject)
    {
		return ExpenseManagementSupport.instance.updateExpense(userObject);
    }
	
	public UserObject deleteExpense(UserObject userObject)
    {
		return ExpenseManagementSupport.instance.deleteExpense(userObject);
    }
}
