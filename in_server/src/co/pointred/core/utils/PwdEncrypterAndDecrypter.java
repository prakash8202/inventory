package co.pointred.core.utils;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import co.pointred.core.log.LoggerService;

public enum PwdEncrypterAndDecrypter
{
    instance;

    Cipher ecipher;
    Cipher dcipher;

    byte[] salt =
    { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };

    int iterationCount = 19;

    private PwdEncrypterAndDecrypter()
    {
	try
	{
	    KeySpec keySpec = new PBEKeySpec("PNEUMANO:SO]<>ULTRA#^MICROSCOPIC(!~SILICO".toCharArray(), salt, iterationCount);
	    SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
	    ecipher = Cipher.getInstance(key.getAlgorithm());
	    dcipher = Cipher.getInstance(key.getAlgorithm());

	    // Prepare the parameter to the ciphers
	    AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

	    // Create the ciphers
	    ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
	    dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
	} catch (Exception e)
	{
	    LoggerService.instance.error(e.getMessage(), PwdEncrypterAndDecrypter.class, e);
	}
    }

    public String encrypt(String password)
    {
	try
	{
	    byte[] utf8 = password.getBytes("UTF8");

	    // Encrypt
	    byte[] enc = ecipher.doFinal(utf8);

	    // Encode bytes to base64 to get a string
	    return new sun.misc.BASE64Encoder().encode(enc);
	} catch (Exception e)
	{
	    LoggerService.instance.error(e.getMessage(), PwdEncrypterAndDecrypter.class, e);
	}
	return null;
    }

    public String decrypt(String encypString)
    {
	try
	{
	    byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(encypString);

	    byte[] utf8 = dcipher.doFinal(dec);

	    return new String(utf8, "UTF8");
	} catch (Exception e)
	{
	    LoggerService.instance.error(e.getMessage(), PwdEncrypterAndDecrypter.class, e);
	}
	return null;
    }

}
