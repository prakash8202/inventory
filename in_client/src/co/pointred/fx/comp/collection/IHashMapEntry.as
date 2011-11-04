package co.pointred.fx.comp.collection
{
	public interface IHashMapEntry
	{

		/**
		 *
		 * Assigns a value to the <code>key</code> property of the 
		 * <code>IHashMapEntry</code> implementation.
		 *  
		 * @param value to assign to the <code>key</code> property
		 * 
		 */        
	
		function set key(value:*) : void;
		
		/**
		 *
		 * Retrieves the value of the <code>key</code> property of the 
		 * <code>IHashMapEntry</code> implementation.
		 *  
		 * @return value of the <code>key</code> property
		 * 
		 */            
		function get key() : *;
		
		/**
		 *
		 * Assignes a value to the <code>value</code> property of an
		 * <code>IHashMapEntry</code> implementation.
		 *  
		 * @param value to assign to the <code>value</code> property
		 * 
		 */        
		function set value(value:*) : void;
		
		/**
		 *
		 * Retrieves the value of the <code>value</code> property of an
		 * <code>IHashMapEntry</code> implementation. 
		 *  
		 * @return value of the <code>value</code> property
		 * 
		 */        
		function get value() : *;
	}

}