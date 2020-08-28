package com.icicibank.apimgmt.model;

import org.springframework.stereotype.Component;


@Component
public class SignatureMethodAlgo extends Exception {
  public SignatureMethodAlgo() {
		super();
		
	}

private static final long serialVersionUID = 1L;
  
  public SignatureMethodAlgo(String message) {
    super(message);
  }
}
