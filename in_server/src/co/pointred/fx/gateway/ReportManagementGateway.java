package co.pointred.fx.gateway;

import co.pointred.core.database.DbManager;
import co.pointred.fx.gateway.support.ReportManagementSupport;
import flex.messaging.io.amf.ASObject;

public class ReportManagementGateway 
{
	////////////////////// HOME SUMMARY /////////////////////////
	
	public void createProductSummaryView(ASObject asObject) 
	{
		try {
			String purchaseConditionStr = ReportManagementSupport.instance.getDateCondition(asObject,"purchase_date",false);
			String salesConditionStr = ReportManagementSupport.instance.getDateCondition(asObject,"sales_date",false);
			DbManager.instance.executeSqlQuery("CREATE OR REPLACE VIEW product_summary_view AS SELECT p.product_name,p.rate, CONVERT(COALESCE((SELECT SUM(pp.quantity) FROM purchase pp WHERE pp.product_id = p.product_pid"+purchaseConditionStr+"),'-'),CHAR(30)) as purchased, CONVERT(COALESCE((SELECT SUM(pp.quantity) * p.rate FROM purchase pp WHERE pp.product_id = p.product_pid"+purchaseConditionStr+"),'-'),CHAR(30)) as cost_price, CONVERT(COALESCE((SELECT SUM(s.quantity) FROM sales s WHERE s.product_id = p.product_pid"+salesConditionStr+"),'-'),CHAR(30)) as sold, CONVERT(COALESCE((SELECT SUM(s.quantity * s.price) FROM sales s WHERE s.product_id = p.product_pid"+salesConditionStr+"),'-'),CHAR(30)) as sold_price,CONVERT(COALESCE((SELECT (SUM(s.quantity * s.price) - SUM(s.quantity * p.rate)) FROM sales s WHERE s.product_id = p.product_pid"+salesConditionStr+"),'-'),CHAR(30)) as profit, p.available FROM product p ORDER BY profit DESC");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createExpenseSummaryView(ASObject asObject) 
	{
		try {
			String expenseConditionStr = ReportManagementSupport.instance.getDateCondition(asObject,"expense_date",false);
			DbManager.instance.executeSqlQuery("CREATE OR REPLACE VIEW expense_summary_view AS SELECT et.expense_type as expense, CONVERT(COALESCE((SELECT SUM(e.amount) FROM expense e WHERE e.expense_type_id = et.expense_type_pid"+expenseConditionStr+"),'-'),CHAR(30)) as amount FROM expensetype et ORDER BY amount DESC");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ASObject fetchOverallDetails(ASObject asObject) 
	{
		return ReportManagementSupport.instance.fetchOverallDetails(asObject);
	}
}
