package connectorGenerator;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.exceptions.PreconditionException;
import javassist.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 * The class <code>ConnectorConfigurationParser</code>.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 *  This class is used to read connectors encoded as xml files
 * </p>
 *
 * <p><strong>Invariants</strong></p>
 *
 * <pre>
 * </pre>
 *
 * <p>Created on : 2025-10-04</p>
 *
 * @author    <a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author    <a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public class ConnectorConfigurationParser {

    protected static final String xml_extension = ".xml";

    /**
     *
     * Tries to create the connectorCanonicalName connector class from the xml file entitled filename
     * If the class already exists then we do nothing
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code connectorCanonicalName != null && !connectorCanonicalName.isEmpty()}
     *  pre {@code connectorImplementedInterface != null }
     *  pre {@code filename != null && !filename.isEmpty()}
     *  post {@code true} // no postcondition
     * </pre>
     * @param connectorCanonicalName name of the class we want to create
     * @param connectorImplementedInterface Interfaces implemented by the connector
     * @param filename name of the xml file
     * @throws NotFoundException The file does not exist
     * @throws CannotCompileException An error has occurred
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static void ClassFromXml(String connectorCanonicalName, Class<?> connectorImplementedInterface, String filename)
            throws NotFoundException, CannotCompileException, ParserConfigurationException, SAXException, IOException {

        assert filename != null && ! filename.isEmpty():
                new PreconditionException("filename == null || filename.isEmpty()");
        assert connectorCanonicalName != null && !connectorCanonicalName.isEmpty():
                new PreconditionException("connectorCanonicalName == null || connectorCanonicalName.isEmpty()");
        assert connectorImplementedInterface != null :
                new PreconditionException("connectorImplementedInterface == null");

        if (!filename.endsWith(xml_extension)) {
            throw new IllegalArgumentException("filename has no xml_extension");
        }

        ClassPool pool = ClassPool.getDefault();

        // If the class already exists we do nothing
        CtClass connectorCtClass = pool.getOrNull(connectorCanonicalName);

        if (connectorCtClass == null) {
            CtClass superClass = pool.get(AbstractConnector.class.getCanonicalName());
            connectorCtClass = pool.makeClass(connectorCanonicalName);

            connectorCtClass.setSuperclass(superClass);

            CtClass CtInterface = pool.get(connectorImplementedInterface.getCanonicalName());
            connectorCtClass.setInterfaces(new CtClass[]{CtInterface});

            CtInterface.detach();
            superClass.detach();

            // We parse and create the class
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            ConfigurationHandler handler = new ConfigurationHandler(connectorCtClass, connectorImplementedInterface);
            parser.parse(filename, handler);
            handler.getConnectorClass();
        }

    }

}

class ConfigurationHandler
extends DefaultHandler {

    protected static final char SPACE = ' ';
    protected static final char LPAR = '(';
    protected static final char RPAR = ')';
    protected static final char RBRA = '}';
    protected static final char LBRA = '{';
    protected static final char NEWLINE = '\n';

    protected static final String PARAMETER_COMMA = ", ";
    protected static final String THROWS = " throws";

    protected static final String EXCEPTION = "java.lang.Exception";

    protected static final String INT_TYPE = "int ";

    private CtClass connectorCtClass;

    /** Accumulator for the content of the class */
    StringBuilder builder;

    /** The previous marker is sometimes useful to determine what code to generate */
    String previousTag;
    /** The equipmentRef is an argument in the body marker*/
    String equipmentRef;
    String offeringCast;
    /** Used to know if we are parsing internal auxiliary methods */
    boolean inInternal;
    public ConfigurationHandler(CtClass new_class, Class<?> connectorImplementedInterface)
            throws NotFoundException, CannotCompileException {
        super();

        this.builder = new StringBuilder();
        this.previousTag = "";
        this.equipmentRef = null;
        this.offeringCast = "";
        this.inInternal = false;

        this.connectorCtClass = new_class;

    }

    /**
     *
     * Add the correct modifiers to the fields of the class
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code true} // no precondition
     *  post {@code true} // no postcondition
     * </pre>
     * @param field
     * @param modifiers
     */
    private void addModifiers(CtField field, String modifiers) {

        if (modifiers.contains("public")) {
            field.setModifiers(Modifier.PUBLIC);
        } else if (modifiers.contains("protected")) {
            field.setModifiers(Modifier.PROTECTED);
        } else if (modifiers.contains("private")) {
            field.setModifiers(Modifier.PRIVATE);
        }

        if (modifiers.contains("static")) {
            field.setModifiers(Modifier.STATIC);
        }

        if (modifiers.contains("final")) {
            field.setModifiers(Modifier.FINAL);
        }

    }

    /**
     *
     * Creates the variables
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code true} // no precondition
     *  post {@code true} // no postcondition
     * </pre>
     * @param attributes
     */
    private void addVar(Attributes attributes) {
        String modifiers = attributes.getValue("modifiers");
        String type = attributes.getValue("type");

        // We get the type of the field
        CtClass typeClass = null;
        try {
            typeClass = ClassPool.getDefault().get(type);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        String name = attributes.getValue("name");
        String initializer = attributes.getValue("static-init");

        try {
            CtField field = new CtField(typeClass, name, this.connectorCtClass);
            // adding the modifiers to the field
            addModifiers(field, modifiers);

            // if the field have a initial value in the xml file
            // we use it to initialise the field in class
            if (initializer != null) {
                this.connectorCtClass.addField(field, initializer);
            } else {
                this.connectorCtClass.addField(field);
            }

        } catch (CannotCompileException e) {
            e.printStackTrace();
        }

    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case "control-adapter":
                String offeredInterface = attributes.getValue("offered");
                // We save the offered interface so as to be able to cast the abstract connector in the class
                this.offeringCast = this.castingString(offeredInterface);
                break;
            case "required":
                break;
            case "instance-var": {
                this.addVar(attributes);
                break;
            }
            case "internal": {
                this.inInternal = true;

                String modifiers = attributes.getValue("modifiers");
                String type = attributes.getValue("type");
                String name = attributes.getValue("name");
                this.builder.append(modifiers);
                this.builder.append(SPACE);
                this.builder.append(type);
                this.builder.append(SPACE);
                this.builder.append(name);
                this.builder.append(LPAR);
                break;
            }
            case "parameter": {
                String type = attributes.getValue("type");
                if (type == null) {
                    // We consider the int type to be the default type
                    type = INT_TYPE;
                }
                String name = attributes.getValue("name");
                if (previousTag.equals("parameter")) {
                    this.builder.append(PARAMETER_COMMA);
                }
                this.builder.append(type);
                this.builder.append(SPACE);
                this.builder.append(name);
                break;
            }
            case "thrown": {
                if (previousTag.equals("thrown")){
                    this.builder.append(PARAMETER_COMMA);
                } else {
                    this.builder.append(RPAR);
                    this.builder.append(THROWS);
                    this.builder.append(SPACE);
                }
                break;
            }
            case "body": {
                this.equipmentRef = attributes.getValue("equipmentRef");

                if (previousTag.equals("parameter")) {
                    this.builder.append(RPAR);

                    if (! this.inInternal ) {
                        this.builder.append(THROWS);
                        this.builder.append(SPACE);
                        this.builder.append(EXCEPTION);
                    }
                }
                this.builder.append(LBRA);
                this.builder.append(NEWLINE);
                break;
            }
            case "maxMode": {
                this.builder.append("public int maxMode() throws java.lang.Exception\n");
                break;
            }
            case "upMode": {
                this.builder.append("public boolean upMode() throws java.lang.Exception\n");
                break;
            }
            case "downMode": {
                this.builder.append("public boolean downMode() throws java.lang.Exception\n");
                break;
            }
            case "currentMode": {
                this.builder.append("public int currentMode() throws java.lang.Exception\n");
                break;
            }
            case "suspended": {
                this.builder.append("public boolean suspended() throws java.lang.Exception\n");
                break;
            }
            case "suspend": {
                this.builder.append("public boolean suspend() throws java.lang.Exception\n");
                break;
            }
            case "resume": {
                this.builder.append("public boolean resume() throws java.lang.Exception\n");
                break;
            }
            case "emergency": {
                this.builder.append("public double emergency() throws java.lang.Exception\n");
                break;
            }
            case "setMode": {
                this.builder.append("public boolean setMode(");
                break;
            }
            case "getModeConsumption": {
                this.builder.append("public double getModeConsumption(");
                break;
            }
            default:

        }
        this.previousTag = qName;
    }

    public void endElement(String uri, String localName, String qName) {
        switch (qName) {
            case "internal": {
                this.inInternal = false;
                break;
            }
            case "body": {
                this.builder.append(NEWLINE);
                this.builder.append(RBRA);
                this.builder.append(NEWLINE);
                this.builder.append(NEWLINE);

                // creating a new method
                CtMethod newMethod;
                try {
                    newMethod = CtMethod.make(this.builder.toString(), this.connectorCtClass);
                    connectorCtClass.addMethod(newMethod);
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }

                // reset the string builder
                this.builder = new StringBuilder();
                this.equipmentRef = null;

                break;
            }
            default:
        }
    }

    public void characters(char[] ch, int start, int end) {
        String content_string = new String(ch, start, end);

        if (this.equipmentRef != null) {
            content_string = content_string.replaceAll(this.equipmentRef, this.offeringCast);
        }
        this.builder.append(content_string);
    }

    public Class<?> getConnectorClass() throws CannotCompileException, NotFoundException, IOException {
        this.connectorCtClass.writeFile();
        return this.connectorCtClass.toClass();
    }

    private String castingString(String offeredInterface) {
        return "((" + offeredInterface + ")this.offering)";
    }
}