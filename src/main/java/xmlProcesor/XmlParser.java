package xmlProcesor;

import org.xml.sax.SAXException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class XmlParser {

    private static final String INIT_ERROR = "Could not initialize XML file";
    private static final String PROCESS_ERROR = "Error while processing input line";
    private static final String FINALIZE_ERROR = "Could not finalize XML Parser";

    private StreamResult outputFileXML;
    private TransformerHandler transformerFactoryHandler;

    public void init(String fileName) throws XmlParserException {
        try {
            outputFileXML = createXmlFile(fileName);
            transformerFactoryHandler = prepareXmlFile(outputFileXML);
        } catch (Exception e) {
            throw new XmlParserException(INIT_ERROR, e);
        }
    }

    private StreamResult createXmlFile(String fileName) throws IOException {
        return new StreamResult(new FileWriter(fileName + ".xml"));
    }

    public void processLine(String line) throws XmlParserException {
        try {
            processSentenceToXml(line);
        } catch (SAXException e) {
            throw new XmlParserException(PROCESS_ERROR, e);
        }
    }

    public void finish() throws XmlParserException {
        try {
            closeXmlFile();
        } catch (SAXException e) {
            throw new XmlParserException(FINALIZE_ERROR, e);
        }
    }

    private TransformerHandler prepareXmlFile(StreamResult xmlFile) throws
            TransformerConfigurationException, SAXException {
        SAXTransformerFactory transformerFactory = createTransformerFactoryInstance();
        TransformerHandler transformerFactoryHandler = createTransformerFactoryHandler(transformerFactory);
        Transformer transformer = transformerFactoryHandler.getTransformer();

        setHeaderProperties(transformer);
        startDocument(transformerFactoryHandler, xmlFile);

        return transformerFactoryHandler;
    }

    private SAXTransformerFactory createTransformerFactoryInstance() {
        return (SAXTransformerFactory) SAXTransformerFactory.newInstance();
    }

    private TransformerHandler createTransformerFactoryHandler(SAXTransformerFactory transformerFactory) throws TransformerConfigurationException {
        return transformerFactory.newTransformerHandler();
    }

    private void setHeaderProperties(Transformer transformer) {
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }

    private void startDocument(TransformerHandler transformerFactoryHandler, StreamResult xmlFile) throws SAXException {
        transformerFactoryHandler.setResult(xmlFile);
        transformerFactoryHandler.startDocument();
        startDocumentTag("text", transformerFactoryHandler);
    }

    private void startDocumentTag(String tag, TransformerHandler transformerFactoryHandler) throws SAXException {
        transformerFactoryHandler.startElement("", "", tag, null);
    }

    public TransformerHandler processSentenceToXml(String line) throws SAXException {
        startDocumentTag("sentence", transformerFactoryHandler);
        processSentence(line, transformerFactoryHandler);
        endDocumentTag("sentence", transformerFactoryHandler);
        return transformerFactoryHandler;
    }

    private void processSentence(String line, TransformerHandler transformerFactoryHandler) {
        Arrays.stream(line.split("[\\s,.]+"))
                .sorted((s, t1) -> s.compareToIgnoreCase(t1))
                .forEach(word -> {
                    try {
                        setWordTagsAndWord(transformerFactoryHandler, word);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void setWordTagsAndWord(TransformerHandler transformerFactoryHandler, String word) throws SAXException {
        startDocumentTag("word", transformerFactoryHandler);
        transformerFactoryHandler.characters(word.toCharArray(), 0, word.length());
        endDocumentTag("word", transformerFactoryHandler);
    }

    private void endDocumentTag(String tag, TransformerHandler transformerFactoryHandler) throws SAXException {
        transformerFactoryHandler.endElement("", "", tag);
    }

    public void closeXmlFile() throws SAXException {
        endDocumentTag("text", transformerFactoryHandler);
        transformerFactoryHandler.endDocument();
    }
}
