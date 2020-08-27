package com.icicibank.apimgmt.model;

import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@Component
public class XmlDigitalSigner {
  private static KeyStore.PrivateKeyEntry keyEntry;
  private static XMLSignatureFactory xmlSigFactory;
  private static KeyInfo keyInfo;
  private static PrivateKey priKey;
  private static Transformer trans;
  private static final Logger LOGGER = Logger.getLogger(XmlDigitalSigner.class.getName());
  
	
	 public XmlDigitalSigner(KeyStore.PrivateKeyEntry privateKeyEntry) throws
	 NoSuchAlgorithmException, InvalidAlgorithmParameterException,
	 TransformerConfigurationException {
	  LOGGER.info("Signature Library Version : 1.0.0");
	  keyEntry = privateKeyEntry;
	  LOGGER.info("Initializing xml digital signer config"); 
	  if (keyEntry == null)
	 throw new
	 RuntimeException("Key could not be read for digital signature. Please check value of signature alias and signature password"); 
	 keyEntry = privateKeyEntry;
	 LOGGER.info("Private Key Loaded Successfully"); xmlSigFactory =
	 XMLSignatureFactory.getInstance("DOM"); 
	 X509Certificate x509Cert =(X509Certificate)keyEntry.getCertificate();
	 priKey = keyEntry.getPrivateKey();
	 keyInfo = getKeyInfo(x509Cert, xmlSigFactory);
	  TransformerFactory tf = TransformerFactory.newInstance();
	  trans =tf.newTransformer(); 
	  }
  
  public String generateDigitalSignature(Document document) throws SAXException, IOException, ParserConfigurationException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, MarshalException, XMLSignatureException, TransformerException {
    if (document == null) {
      LOGGER.warning("document is null unable verify signature");
      throw new NullPointerException("document is null unable verify signature");
    } 
    Document signDoc = signXml(document);
    StringWriter stringWriter = new StringWriter();
    trans.transform(new DOMSource(signDoc), new StreamResult(stringWriter));
    return stringWriter.getBuffer().toString();
  }
  
  private Document signXml(Document document) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, MarshalException, XMLSignatureException {
    Reference ref = xmlSigFactory.newReference("", xmlSigFactory.newDigestMethod("http://www.w3.org/2001/04/xmlenc#sha256", null), 
        Collections.singletonList(xmlSigFactory
          .newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature", (TransformParameterSpec)null)), null, null);
    LOGGER.info("creating signed info object");
    SignedInfo signedInfo = xmlSigFactory.newSignedInfo(xmlSigFactory
        .newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (C14NMethodParameterSpec)null), xmlSigFactory
        
        .newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null), 
        Collections.singletonList(ref));
    DOMSignContext dsc = new DOMSignContext(priKey, document.getDocumentElement());
    XMLSignature signature = xmlSigFactory.newXMLSignature(signedInfo, keyInfo);
    
    LOGGER.info("priKey "+priKey.getFormat()+" prikey Algorithm "+priKey.getAlgorithm());
    LOGGER.info("Using http://www.w3.org/2001/04/xmlenc#sha256 Digest algorithm");
    LOGGER.info("Using http://www.w3.org/2001/04/xmldsig-more#rsa-sha256 Signature Method algorithm");
    LOGGER.info("Signing xml");
    signature.sign(dsc);
    LOGGER.info("Signing Completed Successfully. Returning signed Xml");
    return document;
  }
   
  private static KeyInfo getKeyInfo(X509Certificate cert, XMLSignatureFactory fac) {
    KeyInfoFactory kif = fac.getKeyInfoFactory();
    List x509Content = new ArrayList();
    x509Content.add(cert.getSubjectX500Principal().getName());
    x509Content.add(cert);
    X509Data xd = kif.newX509Data(x509Content);
    return kif.newKeyInfo(Collections.singletonList(xd));
  }
  
}
