package co.pointred.core.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LoggerService
{
    instance;

    public void debug(String message, Class<?> className)
    {
	Logger logger = LoggerFactory.getLogger(className);
	logger.debug(message);
    }

    public void info(String message, Class<?> className)
    {
	Logger logger = LoggerFactory.getLogger(className);
	logger.info(message);
    }

    public void warning(String message, Class<?> className)
    {
	Logger logger = LoggerFactory.getLogger(className);
	logger.warn(message);
    }
    
    public void error(String message, Class<?> className)
    {
	Logger logger = LoggerFactory.getLogger(className);
	logger.error(message);
    }

    public void error(String message, Class<?> className, Throwable e)
    {
	Logger logger = LoggerFactory.getLogger(className);
	logger.error(message, e);
    }
}
