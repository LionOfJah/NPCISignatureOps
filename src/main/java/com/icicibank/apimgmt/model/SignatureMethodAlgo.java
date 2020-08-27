package com.icicibank.apimgmt.model;

import org.springframework.stereotype.Component;


public class SignatureMethodAlgo extends Exception {
  private static final long serialVersionUID = 1L;
  
  public SignatureMethodAlgo(String message) {
    super(message);
  }
}
