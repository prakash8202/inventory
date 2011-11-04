package co.pointred.fx.comp.core.validation
{
	public interface IValidator
	{
		function doValidate():Boolean;
		function getValidationError():Object;
	}
}