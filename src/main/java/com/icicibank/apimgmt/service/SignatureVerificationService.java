package com.icicibank.apimgmt.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.icicibank.apimgmt.model.DigestMethodAlgo;
import com.icicibank.apimgmt.model.SignatureMethodAlgo;

@Service
public interface SignatureVerificationService {

	//public String verifySignature(BufferedReader reader) throws SAXException, IOException,ParserConfigurationException,SignatureMethodAlgo,DigestMethodAlgo, MarshalException, XMLSignatureException,Exception;

	public String verifySignature(String reader) throws SAXException, IOException,ParserConfigurationException,SignatureMethodAlgo,DigestMethodAlgo, MarshalException, XMLSignatureException,Exception;
	
	public String doDigitalSignature(String reader)throws SAXException, IOException,ParserConfigurationException,SignatureMethodAlgo,DigestMethodAlgo, MarshalException, XMLSignatureException,Exception;
}
