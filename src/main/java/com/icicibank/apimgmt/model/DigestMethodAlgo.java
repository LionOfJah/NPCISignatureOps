package com.icicibank.apimgmt.model;

import org.springframework.stereotype.Component;

@Component
public class DigestMethodAlgo extends Exception {
	
	
  public DigestMethodAlgo() {
		super();
		
	}

private static final long serialVersionUID = 1L;
  
  public DigestMethodAlgo(String message) {
    super(message);
  }
}
