package com.icicibank.apimgmt.model;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Component;

@Component
public class CipherUtil {
  private static final Logger LOGGER = Logger.getLogger(CipherUtil.class.getName());
  
  private String CIPHER_ALGO = null;
  
  
  
  public CipherUtil() {
	super();
	// TODO Auto-generated constructor stub
}

public CipherUtil(String algorithm) {
    LOGGER.info("Signature Library Version : 1.0.0");
    this.CIPHER_ALGO = algorithm;
  }
  
  public String encrypt(String plainText, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    if (publicKey == null) {
      LOGGER.warning("Public key is null");
      throw new NullPointerException("Public key is null");
    } 
    if (plainText == null) {
      LOGGER.warning("Plain text is null");
      throw new NullPointerException("Plain text is null");
    } 
    Cipher cipher = Cipher.getInstance(this.CIPHER_ALGO);
    cipher.init(1, publicKey);
    byte[] cipherData = cipher.doFinal(plainText.getBytes());
    String encodedData = DatatypeConverter.printBase64Binary(cipherData);
    return encodedData;
  }
  
  public String decrypt(String ecryptedText, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
    if (privateKey == null) {
      LOGGER.warning("Private key is null");
      throw new NullPointerException("Private key is null");
    } 
    if (ecryptedText == null) {
      LOGGER.warning("Encrypted text is null");
      throw new NullPointerException("Encrypted text is null");
    } 
    Cipher cipher = Cipher.getInstance(this.CIPHER_ALGO);
    cipher.init(2, privateKey);
    byte[] bytes = DatatypeConverter.parseBase64Binary(ecryptedText);
    return new String(cipher.doFinal(bytes), "UTF-8");
  }
  
  
}
