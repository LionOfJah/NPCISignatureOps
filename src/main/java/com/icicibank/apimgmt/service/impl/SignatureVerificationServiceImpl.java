package com.icicibank.apimgmt.service.impl;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.icicibank.apimgmt.model.DigestMethodAlgo;
import com.icicibank.apimgmt.model.SignatureMethodAlgo;
import com.icicibank.apimgmt.model.VerifyDigitalSign;
import com.icicibank.apimgmt.model.XmlDigitalSigner;
import com.icicibank.apimgmt.service.SignatureVerificationService;

@Service
public class SignatureVerificationServiceImpl implements SignatureVerificationService {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	VerifyDigitalSign verifySign;
	
	@Autowired(required = false)
	PublicKey publickey;
	
	@Value("${app.publickey.path}")
	String filePath;
	
	@Autowired
	XmlDigitalSigner xmlDigitalSigner;
	
	@Override
	public String verifySignature(InputStream input) throws SAXException, IOException, ParserConfigurationException, SignatureMethodAlgo, DigestMethodAlgo, MarshalException, XMLSignatureException,Exception {
		
		
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
		
	
		//Document doc = docBuilder.parse(new InputSource(reader));
		
		Document doc = docBuilder.parse(input);
		
		docBuilder.setErrorHandler(new ErrorHandler() {
		    @Override
		    public void warning(SAXParseException exception) throws SAXException {
		        logger.warn(exception.getMessage());
		    }

		    @Override
		    public void fatalError(SAXParseException exception) throws SAXException {
		        logger.error("Fatal error ", exception);
		    }

		    @Override
		    public void error(SAXParseException exception) throws SAXException {
		        logger.error("Exception ", exception);
		    }
		});
		//reader.lines().forEach(i->System.out.println(i.toString()));
		PublicKey publicKey=getPublicKey(filePath); 
		verifySign= new VerifyDigitalSign(publicKey);
		
		boolean flag=verifySign.isXmlDigitalSignatureValid(doc);
		logger.info("flag value "+flag);
		return null;
	}
	
	@Bean
	public PublicKey getPublicKey(@Value("${app.publickey.path}")String file) throws Exception
	{
	
	    CertificateFactory certFact 	= CertificateFactory.getInstance("x.509");
		FileInputStream fi 			= new FileInputStream(file);
		
		X509Certificate certificate = (X509Certificate) certFact.generateCertificate(fi);
		PublicKey publicKey 					= certificate.getPublicKey();
		
		return publicKey;
	}

	@Override
	public String doDigitalSignature(InputStream reader) throws SAXException, IOException, ParserConfigurationException,
			SignatureMethodAlgo, DigestMethodAlgo, MarshalException, XMLSignatureException, Exception {
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
		
		Document doc = docBuilder.parse(reader);
		
		PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry();
		
		XmlDigitalSigner xmlDigitalSigner = new XmlDigitalSigner(privateKeyEntry);
		
		String sign = xmlDigitalSigner.generateDigitalSignature(doc);
		
		return sign;
	}
	
	@Bean
	public PrivateKeyEntry getPrivateKeyEntry()	
	{
		FileInputStream keyStoreInputStream = null;
		PrivateKeyEntry privateKeyEntry = null;
		
		try 
		{
			KeyStore ks 			 = KeyStore.getInstance("PKCS12");
			String keyStoreFile 	 = "C:\\Users\\jitendra_rawat\\Downloads\\Icici_proj_docs\\IciciCertificate\\PublicPrivate\\ICIC0TREA00_15-04-2020T182123.pfx";
			char[] ketStrorePwdArray = "pfxfile123".toCharArray();
			keyStoreInputStream 	 = new FileInputStream(keyStoreFile);
			ks.load(keyStoreInputStream, ketStrorePwdArray);

			String alias 	= null;
			alias 		 	= (String) ks.aliases().nextElement();

			privateKeyEntry = (PrivateKeyEntry) ks.getEntry(alias, 
								new KeyStore.PasswordProtection(ketStrorePwdArray));
		} 
		catch (Exception e) 
		{
			logger.error("Exception Caught While get Private Key Entry");
			logger.error( "Exception", e );
		} 
		finally 
		{
			if (keyStoreInputStream != null) 
			{
				try 
				{
					keyStoreInputStream.close();
				} 
				catch (Exception e) 
				{
					logger.error("Exception in keyStoreInputStream");
					logger.error( "Exception", e );
				}
			}
		}
		
		//return privateKeyEntry.getPrivateKey();
		return privateKeyEntry;
	}

}
