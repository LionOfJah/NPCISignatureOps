package com.icicibank.apimgmt.service.impl;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.icicibank.apimgmt.model.DigestMethodAlgo;
import com.icicibank.apimgmt.model.ResponseModel;
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
	String publicKeyPath;
	
	@Value("${app.privateKey.path}")
	String privateKeyPath;
	@Autowired
	XmlDigitalSigner xmlDigitalSigner;
	
	@Override
	public String verifySignature(String input) throws SAXException, IOException, ParserConfigurationException, SignatureMethodAlgo, DigestMethodAlgo, MarshalException, XMLSignatureException,Exception {
		
		String response = "";
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
		
	
		//Document doc = docBuilder.parse(new InputSource(reader));
		InputSource sourceReader = new InputSource(new StringReader(input));
		Document doc = docBuilder.parse(sourceReader);
		
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
		PublicKey publicKey=getPublicKey(publicKeyPath); 
		verifySign= new VerifyDigitalSign(publicKey);
		
		boolean flag=verifySign.isXmlDigitalSignatureValid(doc);
		logger.info("flag value "+flag);
		
		if(flag) {
			//response="Signature is Valid";
			response=input.substring(0, input.indexOf("<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">"));
			String endTag=input.substring(input.indexOf("</Signature>"),input.length());
			endTag = endTag.substring(endTag.lastIndexOf("<"), endTag.lastIndexOf(">")+1);
			response = response.concat(endTag);
		}else {
			response="Signature is invalid";
		}
		return response;
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
	public String doDigitalSignature(String reader) throws SAXException, IOException, ParserConfigurationException,
			SignatureMethodAlgo, DigestMethodAlgo, MarshalException, XMLSignatureException, Exception {
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
		
		InputSource sourceReader = new InputSource(new StringReader(reader));
		Document doc = docBuilder.parse(sourceReader);
		
		PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry(privateKeyPath);
		
		XmlDigitalSigner xmlDigitalSigner = new XmlDigitalSigner(privateKeyEntry);
		
		String sign = xmlDigitalSigner.generateDigitalSignature(doc);
		
		logger.info("Singnature "+sign);
		
		return sign;
	}
	
	@Bean
	public PrivateKeyEntry getPrivateKeyEntry(@Value("${app.privateKey.path}")String privateKeyPath)	
	{
		FileInputStream keyStoreInputStream = null;
		PrivateKeyEntry privateKeyEntry = null;
		
		try 
		{
			KeyStore ks 			 = KeyStore.getInstance("PKCS12");
			String keyStoreFile 	 = privateKeyPath;
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
