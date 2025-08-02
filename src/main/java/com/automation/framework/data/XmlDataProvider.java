package com.automation.framework.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class XmlDataProvider {
    
    private static final Logger logger = LogManager.getLogger(XmlDataProvider.class);
    private DocumentBuilderFactory documentBuilderFactory;
    private XPathFactory xPathFactory;
    
    public XmlDataProvider() {
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.xPathFactory = XPathFactory.newInstance();
    }
    
    /**
     * Parse XML file and return Document
     */
    public Document parseXmlFile(String filePath) {
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(filePath));
            document.getDocumentElement().normalize();
            logger.info("XML file parsed successfully: " + filePath);
            return document;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Failed to parse XML file: " + filePath + " - " + e.getMessage());
            throw new RuntimeException("Cannot parse XML file: " + filePath, e);
        }
    }
    
    /**
     * Get single value using XPath expression
     */
    public String getXmlValue(String filePath, String xpathExpression) {
        try {
            Document document = parseXmlFile(filePath);
            XPath xpath = xPathFactory.newXPath();
            Node node = (Node) xpath.compile(xpathExpression).evaluate(document, XPathConstants.NODE);
            
            if (node != null) {
                return node.getTextContent().trim();
            } else {
                logger.warn("XPath expression returned no results: " + xpathExpression);
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to get XML value using XPath: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get multiple values using XPath expression
     */
    public List<String> getXmlValues(String filePath, String xpathExpression) {
        List<String> values = new ArrayList<>();
        try {
            Document document = parseXmlFile(filePath);
            XPath xpath = xPathFactory.newXPath();
            NodeList nodeList = (NodeList) xpath.compile(xpathExpression).evaluate(document, XPathConstants.NODESET);
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                values.add(node.getTextContent().trim());
            }
            
            logger.info("Retrieved " + values.size() + " values using XPath: " + xpathExpression);
        } catch (Exception e) {
            logger.error("Failed to get XML values using XPath: " + e.getMessage());
        }
        return values;
    }
    
    /**
     * Get XML node attributes as Map
     */
    public Map<String, String> getNodeAttributes(String filePath, String xpathExpression) {
        Map<String, String> attributes = new HashMap<>();
        try {
            Document document = parseXmlFile(filePath);
            XPath xpath = xPathFactory.newXPath();
            Node node = (Node) xpath.compile(xpathExpression).evaluate(document, XPathConstants.NODE);
            
            if (node != null && node.hasAttributes()) {
                NamedNodeMap attributeMap = node.getAttributes();
                for (int i = 0; i < attributeMap.getLength(); i++) {
                    Node attribute = attributeMap.item(i);
                    attributes.put(attribute.getNodeName(), attribute.getNodeValue());
                }
            }
            
            logger.info("Retrieved " + attributes.size() + " attributes for node: " + xpathExpression);
        } catch (Exception e) {
            logger.error("Failed to get node attributes: " + e.getMessage());
        }
        return attributes;
    }
    
    /**
     * Convert XML test data to Object[][] for TestNG DataProvider
     */
    public Object[][] getTestDataFromXml(String filePath, String testCaseXPath) {
        try {
            Document document = parseXmlFile(filePath);
            XPath xpath = xPathFactory.newXPath();
            NodeList testCases = (NodeList) xpath.compile(testCaseXPath).evaluate(document, XPathConstants.NODESET);
            
            Object[][] testData = new Object[testCases.getLength()][];
            
            for (int i = 0; i < testCases.getLength(); i++) {
                Node testCaseNode = testCases.item(i);
                Map<String, String> testCaseData = new HashMap<>();
                
                // Get all child elements as key-value pairs
                NodeList children = testCaseNode.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        testCaseData.put(child.getNodeName(), child.getTextContent().trim());
                    }
                }
                
                // Also get attributes
                if (testCaseNode.hasAttributes()) {
                    NamedNodeMap attributes = testCaseNode.getAttributes();
                    for (int k = 0; k < attributes.getLength(); k++) {
                        Node attribute = attributes.item(k);
                        testCaseData.put("@" + attribute.getNodeName(), attribute.getNodeValue());
                    }
                }
                
                testData[i] = new Object[]{testCaseData};
            }
            
            logger.info("Test data prepared from XML: " + testData.length + " test cases");
            return testData;
            
        } catch (Exception e) {
            logger.error("Failed to get test data from XML: " + e.getMessage());
            return new Object[0][0];
        }
    }
    
    /**
     * Get specific test case by index
     */
    public Map<String, String> getTestCase(String filePath, String testCaseXPath, int index) {
        Object[][] allTestData = getTestDataFromXml(filePath, testCaseXPath);
        
        if (index >= 0 && index < allTestData.length) {
            return (Map<String, String>) allTestData[index][0];
        } else {
            logger.error("Invalid test case index: " + index);
            return new HashMap<>();
        }
    }
    
    /**
     * Get test cases by attribute value
     */
    public List<Map<String, String>> getTestCasesByAttribute(String filePath, String testCaseXPath, 
                                                           String attributeName, String attributeValue) {
        List<Map<String, String>> filteredTestCases = new ArrayList<>();
        Object[][] allTestData = getTestDataFromXml(filePath, testCaseXPath);
        
        for (Object[] testCase : allTestData) {
            Map<String, String> testCaseMap = (Map<String, String>) testCase[0];
            String attrKey = "@" + attributeName;
            
            if (testCaseMap.containsKey(attrKey) && testCaseMap.get(attrKey).equals(attributeValue)) {
                filteredTestCases.add(testCaseMap);
            }
        }
        
        logger.info("Filtered test cases: " + filteredTestCases.size() + 
                   " matches for " + attributeName + "=" + attributeValue);
        return filteredTestCases;
    }
    
    /**
     * Update XML node value
     */
    public void updateXmlValue(String filePath, String xpathExpression, String newValue) {
        try {
            Document document = parseXmlFile(filePath);
            XPath xpath = xPathFactory.newXPath();
            Node node = (Node) xpath.compile(xpathExpression).evaluate(document, XPathConstants.NODE);
            
            if (node != null) {
                node.setTextContent(newValue);
                saveXmlDocument(document, filePath);
                logger.info("XML value updated: " + xpathExpression + " = " + newValue);
            } else {
                logger.warn("Node not found for XPath: " + xpathExpression);
            }
            
        } catch (Exception e) {
            logger.error("Failed to update XML value: " + e.getMessage());
        }
    }
    
    /**
     * Save XML document to file
     */
    private void saveXmlDocument(Document document, String filePath) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
            logger.info("XML document saved: " + filePath);
        } catch (Exception e) {
            logger.error("Failed to save XML document: " + e.getMessage());
        }
    }
    
    /**
     * Unmarshal XML to Java object using JAXB
     */
    public <T> T unmarshalXml(String filePath, Class<T> clazz) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            T result = (T) unmarshaller.unmarshal(new File(filePath));
            logger.info("XML unmarshalled to object: " + clazz.getSimpleName());
            return result;
        } catch (JAXBException e) {
            logger.error("Failed to unmarshal XML: " + e.getMessage());
            throw new RuntimeException("Cannot unmarshal XML to " + clazz.getSimpleName(), e);
        }
    }
    
    /**
     * Marshal Java object to XML using JAXB
     */
    public <T> void marshalToXml(T object, String filePath) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, new File(filePath));
            logger.info("Object marshalled to XML: " + filePath);
        } catch (JAXBException e) {
            logger.error("Failed to marshal object to XML: " + e.getMessage());
            throw new RuntimeException("Cannot marshal object to XML", e);
        }
    }
    
    /**
     * Validate XML structure by checking if required nodes exist
     */
    public boolean validateXmlStructure(String filePath, List<String> requiredXPaths) {
        try {
            Document document = parseXmlFile(filePath);
            XPath xpath = xPathFactory.newXPath();
            
            for (String xpathExpr : requiredXPaths) {
                Node node = (Node) xpath.compile(xpathExpr).evaluate(document, XPathConstants.NODE);
                if (node == null) {
                    logger.error("Required XML node not found: " + xpathExpr);
                    return false;
                }
            }
            
            logger.info("XML structure validation passed");
            return true;
        } catch (Exception e) {
            logger.error("XML validation failed: " + e.getMessage());
            return false;
        }
    }
}