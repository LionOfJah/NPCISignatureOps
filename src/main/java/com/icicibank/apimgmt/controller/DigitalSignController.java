package com.icicibank.apimgmt.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icicibank.apimgmt.model.ResponseModel;
import com.icicibank.apimgmt.service.SignatureVerificationService;


@RestController
@RequestMapping(value = "/api/v0/")
public class DigitalSignController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	ResponseModel responseModel;
	
	@Autowired
	SignatureVerificationService verServ;
	
	@PostMapping(value ="/verifySignature",consumes = "application/xml",produces = "application/xml")
	public ResponseEntity<String> verifySignature(InputStream input){
		
		BufferedReader buffreader = new BufferedReader(new InputStreamReader(input));
		
		try {
			verServ.verifySignature(input);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(input.toString());
		return null;
	}
	
	@PostMapping(value ="/doSignature",consumes = "application/xml",produces = "application/xml")
	public ResponseEntity<String> doDigitalSignature(InputStream input) {
		String signedXml=null;
		try {
			signedXml=verServ.doDigitalSignature(input);
			logger.info("Signed Xml "+signedXml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.ok().body(signedXml);
		// TODO Auto-generated method stub

	}

}