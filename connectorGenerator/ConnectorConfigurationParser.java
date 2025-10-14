package connectorGenerator;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import javassist.*;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * The class <code>connectorGenerator.ConnectorConfigurationParser</code>.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 *
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

    public static Class<?> parse_xml(String connectorCanoncicalName, Class<?> connectorImplementedInterface, String filename)
            throws Exception {

        if (! filename.endsWith(xml_extension)) {
            throw new IllegalArgumentException("filename has no xml_extension");
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        ConfigurationHandler handler = new ConfigurationHandler(connectorCanoncicalName, connectorImplementedInterface);
        parser.parse(filename, handler);
        return handler.getConnectorClass();
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
    protected static final String THROWS = "THROWS";

    protected static final String EXCEPTION = "EXCEPTION";

    protected static final String INT_TYPE = "int ";

    private CtClass connectInterface;

    private CtClass connectorCtClass;

    private Hashtable<String, Method> table;

    StringBuilder builder;

    String previousTag;
    String equipmentRef;
    String offeredInterface;
    boolean inInternal;
    public ConfigurationHandler(String connectorCanonicalName, Class<?> connectorImplementedInterface)
            throws NotFoundException, CannotCompileException {
        super();

        ClassPool pool = ClassPool.getDefault();
        connectInterface = pool.get(connectorImplementedInterface.getCanonicalName());
        CtClass superClass = pool.get(AbstractConnector.class.getCanonicalName());

        connectorCtClass = pool.makeClass(connectorCanonicalName);
        connectorCtClass.setSuperclass(superClass);

        this.builder = new StringBuilder();
        this.previousTag = "";
        this.equipmentRef = null;
        this.offeredInterface = "";
        this.inInternal = false;

        CtClass CtInterface = pool.get(connectorImplementedInterface.getCanonicalName());
        this.connectorCtClass.setInterfaces(new CtClass[]{CtInterface});

        CtInterface.detach();
        superClass.detach();
    }

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
    private void addVar(Attributes attributes) {
        String modifiers = attributes.getValue("modifiers");
        String type = attributes.getValue("type");
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
            addModifiers(field, modifiers);

            if (initializer != null) {
                this.connectorCtClass.addField(field, initializer);
            } else {
                this.connectorCtClass.addField(field);
            }

        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        typeClass.detach();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case "identification":
                this.offeredInterface = attributes.getValue("offered");
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
                if (type.isEmpty()) {
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
                if (previousTag.equals("parameter")) {
                    this.builder.append(RPAR);
                    this.builder.append(THROWS);
                    this.builder.append(SPACE);
                } else {
                    // If the previous element is not a parameter element
                    // then the previous element was also a thrown element
                    this.builder.append(PARAMETER_COMMA);
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
                this.builder.append("public int MaxMode() throws Exception\n");
                break;
            }
            case "upMode": {
                this.builder.append("public boolean upMode() throws Exception\n");
                break;
            }
            case "downMode": {
                this.builder.append("public boolean downMode() throws Exception\n");
                break;
            }
            case "currentMode": {
                this.builder.append("public int currentMode() throws Exception\n");
                break;
            }
            case "suspended": {
                this.builder.append("public boolean suspended() throws Exception\n");
                break;
            }
            case "suspend": {
                this.builder.append("public boolean suspend() throws Exception\n");
                break;
            }
            case "resume": {
                this.builder.append("public boolean resume() throws Exception\n");
                break;
            }
            case "emergency": {
                this.builder.append("public double emergency() throws Exception\n");
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
                System.out.println(qName);
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
            content_string.replaceAll(this.equipmentRef, this.offeredInterface);
        }
        this.builder.append(content_string);
    }

    public Class<?> getConnectorClass() throws CannotCompileException {
        return this.connectorCtClass.toClass();
    }
}