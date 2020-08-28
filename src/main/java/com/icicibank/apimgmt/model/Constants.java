package com.icicibank.apimgmt.model;

import org.springframework.stereotype.Component;

@Component
public class Constants {
	
	
  public Constants() {
		super();
		// TODO Auto-generated constructor stub
	}

protected static final String LIB_VERSION = "1.0.0";
  
  protected static final String SIGNATURE_METHOD_ALGO = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
  
  protected static final String DIGEST_ALGO = "http://www.w3.org/2001/04/xmlenc#sha256";
  
  protected static final String JAVA_CIPHER_ALGO = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
  
  protected static final String IBM_CIPHER_ALGO = "RSA/ECB/OAEPWithSHA256AndMGF1Padding";
}
