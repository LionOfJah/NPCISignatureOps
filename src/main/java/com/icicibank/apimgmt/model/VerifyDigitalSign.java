package com.icicibank.apimgmt.model;

import java.security.PublicKey;
import java.util.logging.Logger;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
public class VerifyDigitalSign {

  @Autowired(required = false)	
  private PublicKey pubKey;
  private static final Logger LOGGER = Logger.getLogger(VerifyDigitalSign.class.getName());
  private static XMLSignatureFactory xmlSigfactory = XMLSignatureFactory.getInstance("DOM");
  
  public VerifyDigitalSign(PublicKey publicKey) {
    LOGGER.info("Signature Library Version : 1.0.0");
    this.pubKey = publicKey;
  }
  
  public boolean isXmlDigitalSignatureValid(Document document) throws SignatureMethodAlgo, DigestMethodAlgo, MarshalException, XMLSignatureException {
    boolean validFlag = false;
    if (document == null) {
      LOGGER.warning("document is null unable verify signature");
      throw new NullPointerException("document is null unable verify signature");
    } 
    document.getDocumentElement().normalize();
    
    
    NodeList signatureNode = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
    //NodeList signatureNode = document.getElementsByTagName("Signature");
    if (signatureNode.getLength() == 0) {
      LOGGER.warning("No Signature found in the document");
      throw new NullPointerException("No Signature found in the document");
    } 
    Node signatureMethodNode = document.getElementsByTagName("SignatureMethod").item(0);
    String signaturMethode = signatureMethodNode.getAttributes().getNamedItem("Algorithm").getNodeValue();
    if (!signaturMethode.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256")) {
      LOGGER.warning("Signature method is different in the documenet. Use http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
      throw new SignatureMethodAlgo("Signature method is different in the documenet. Use http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
    } 
    Node digestNode = document.getElementsByTagName("DigestMethod").item(0);
   
    String digestAlgo = digestNode.getAttributes().getNamedItem("Algorithm").getNodeValue();
    if (!digestAlgo.equals("http://www.w3.org/2001/04/xmlenc#sha256")) {
      LOGGER.warning("Digest method is different in the documenet. Use http://www.w3.org/2001/04/xmlenc#sha256");
      throw new DigestMethodAlgo("Digest method is different in the documenet. Use http://www.w3.org/2001/04/xmlenc#sha256");
    } 
    if (this.pubKey == null) {
      LOGGER.warning("PublicKey is null");
      throw new NullPointerException("PublicKey is null");
    } 
    LOGGER.info("public key "+this.pubKey);
    
    DOMValidateContext valContext = new DOMValidateContext(this.pubKey, signatureNode.item(0));
    XMLSignature signature = xmlSigfactory.unmarshalXMLSignature(valContext);


    validFlag = signature.validate(valContext);
   
    if (validFlag) {
      validFlag = true;
      LOGGER.info("Signature is successfully validated");
    } else {
      LOGGER.warning("Error while validating the signature");
    } 
    return validFlag;
  }

public VerifyDigitalSign() {
	super();
	
}
}
