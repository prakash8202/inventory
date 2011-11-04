package co.pointred.fx.gateway.support;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import co.pointred.core.database.DbManager;
import flex.messaging.io.amf.ASObject;

public enum ReportManagementSupport 
{
	instance;

	@SuppressWarnings("unchecked")
	public ASObject fetchOverallDetails(ASObject asObj) 
	{
		ASObject asObject = new ASObject();
		
		try {
			String purchaseConditionStr = ReportManagementSupport.instance.getDateCondition(asObj,"purchase_date",true);
			String salesConditionStr = ReportManagementSupport.instance.getDateCondition(asObj,"sales_date",true);
			String expenseConditionStr = ReportManagementSupport.instance.getDateCondition(asObj,"expense_date",true);
			String profitConditionStr = ReportManagementSupport.instance.getDateCondition(asObj,"sales_date",false);
			
			Vector<Vector<String>> totalSales = DbManager.instance.executeDirectSelect("SELECT SUM(quantity * price) as total_sales FROM sales"+salesConditionStr);
			
			float total_sales = 0;
			if(((Vector<String>)totalSales.get(0)).get(0) != null && !((Vector<String>)totalSales.get(0)).get(0).toString().equals("-"))
				total_sales = Float.parseFloat(((Vector<String>)totalSales.get(0)).get(0).toString());
			
			Vector<Vector<String>> totalPurchase = DbManager.instance.executeDirectSelect("SELECT SUM(quantity * cost) as total_purchase FROM purchase" + purchaseConditionStr);
			float total_purchase = 0;
			if(((Vector<String>)totalPurchase.get(0)).get(0) != null && !((Vector<String>)totalPurchase.get(0)).get(0).toString().equals("-"))
				total_purchase = Float.parseFloat(((Vector<String>)totalPurchase.get(0)).get(0).toString());
			
			Vector<Vector<String>> totalExpense= DbManager.instance.executeDirectSelect("SELECT SUM(amount) as total_expense FROM expense"+ expenseConditionStr);
			float total_expense = 0;
			if(((Vector<String>)totalExpense.get(0)).get(0) != null && !((Vector<String>)totalExpense.get(0)).get(0).toString().equals("-"))
				total_expense = Float.parseFloat(((Vector<String>)totalExpense.get(0)).get(0).toString());
			
			Vector<Vector<String>> profitVec = DbManager.instance.executeDirectSelect("SELECT (SUM(s.quantity * s.price) - SUM(s.quantity * p.rate)) as total_sales FROM sales s, product p WHERE s.product_id = p.product_pid "+profitConditionStr);
			
			float profit = 0;
			if(((Vector<String>)profitVec.get(0)).get(0) != null && !((Vector<String>)profitVec.get(0)).get(0).toString().equals("-"))
				profit = Float.parseFloat(((Vector<String>)profitVec.get(0)).get(0).toString());
			
			Vector<Vector<String>> profitPurchase = DbManager.instance.executeDirectSelect("SELECT SUM(s.quantity * p.rate) as total_purchase FROM sales s, product p WHERE s.product_id = p.product_pid "+profitConditionStr);
			float profit_purchase = 0;
			if(((Vector<String>)profitPurchase.get(0)).get(0) != null && !((Vector<String>)profitPurchase.get(0)).get(0).toString().equals("-"))
				profit_purchase = Float.parseFloat(((Vector<String>)profitPurchase.get(0)).get(0).toString());
			
			float profit_loss = profit - total_expense;
			float percentage = (Math.abs(profit_loss) * 100)/(profit_purchase + total_expense);
			
			asObject.put("total_sales", ""+total_sales);
			asObject.put("total_purchase", ""+total_purchase);
			asObject.put("total_expense", ""+total_expense);
			asObject.put("profit_loss", ""+profit_loss);
			asObject.put("percentage", ""+Math.round(percentage * 100.0)/100.0 + " %");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return asObject;
	}

	public String getDateCondition(ASObject asObject, String dateVariable, boolean manual) 
	{
		String dateCondition = "";
		
		HashMap<String, Date> hash = asObject;
		
		Date fromDate = hash.get("fromDate");
		Date toDate = hash.get("toDate");
		
		if(fromDate != null || toDate != null)
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String from = "";
			String to = "";
			String timeStr = " 00:00:00";
			
			String condition = "AND";
			if(manual)
				condition = "WHERE";
			
			if(fromDate != null && toDate == null)
			{
				from = dateFormat.format(fromDate) + timeStr;
				dateCondition = dateCondition + " "+condition+" "+dateVariable+" >= '" + from + "'";
			}else if(fromDate == null && toDate != null)
			{
				to = dateFormat.format(toDate) + timeStr;
				dateCondition = dateCondition + " "+condition+" "+dateVariable+" <= '" + to + "'";
			}else if(fromDate != null && toDate != null)
			{
				if(fromDate.getTime() <= toDate.getTime())
				{
					from = dateFormat.format(fromDate) + timeStr;
					to = dateFormat.format(toDate) + timeStr;
					
					dateCondition = dateCondition + " "+condition+" "+dateVariable+" BETWEEN '" + from + "' AND '" + to + "'";
				}
			}
		}
		
		return dateCondition;
	}
}
