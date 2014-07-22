package gov.nysenate.openleg.util;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Component
public class XmlHelper
{
    private final DocumentBuilder dBuilder;
    private final XPath xpath;

    public XmlHelper() throws ParserConfigurationException {
        dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        xpath = XPathFactory.newInstance().newXPath();
    }

    public Document parse(File file) throws SAXException, IOException {
        return dBuilder.parse(file);
    }

    public Document parse(String xmlString) throws IOException, SAXException {
        InputSource is = new InputSource(new ByteArrayInputStream(xmlString.getBytes("utf-8")));
        return dBuilder.parse(is);
    }

    public Boolean getBoolean(String path, Node node) throws XPathExpressionException {
        return (Boolean)xpath.evaluate(path, node, XPathConstants.BOOLEAN);
    }

    public String getString(String path, Node node) throws XPathExpressionException {
        return (String)xpath.evaluate(path, node, XPathConstants.STRING);
    }

    public Double getDouble(String path, Node node) throws XPathExpressionException {
        return (Double)xpath.evaluate(path, node, XPathConstants.NUMBER);
    }

    public Integer getInteger(String path, Node node) throws XPathExpressionException {
        return ((Double)xpath.evaluate(path, node, XPathConstants.NUMBER)).intValue();
    }

    public Node getNode(String path, Node node) throws XPathExpressionException {
        return (Node)xpath.evaluate(path, node, XPathConstants.NODE);
    }

    public NodeList getNodeList(String path, Node node) throws XPathExpressionException {
        return (NodeList)xpath.evaluate(path, node, XPathConstants.NODESET);
    }
}
