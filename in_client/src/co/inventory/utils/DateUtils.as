package co.inventory.utils
{
	public class DateUtils
	{
		public function DateUtils()
		{
		}
		
		public static function dateAdd(datepart:String = "", number:Number = 0, date:Date = null):Date {
			if (date == null) {
				/* Default to current date. */
				date = new Date();
			}
			
			var returnDate:Date = new Date(date.time);;
			
			switch (datepart.toLowerCase()) {
				case "fullyear":
				case "month":
				case "date":
				case "hours":
				case "minutes":
				case "seconds":
				case "milliseconds":
					returnDate[datepart] += number;
					break;
				default:
					/* Unknown date part, do nothing. */
					break;
			}
			return returnDate;
		}
	}
}