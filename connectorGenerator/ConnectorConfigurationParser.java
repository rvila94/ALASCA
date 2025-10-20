package connectorGenerator;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import javassist.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

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

    public static void ClassFromXml(String connectorCanonicalName, Class<?> connectorImplementedInterface, String filename)
            throws NotFoundException, CannotCompileException, ParserConfigurationException, SAXException, IOException {

        if (!filename.endsWith(xml_extension)) {
            throw new IllegalArgumentException("filename has no xml_extension");
        }

        ClassPool pool = ClassPool.getDefault();

        CtClass connectorCtClass = pool.getOrNull(connectorCanonicalName);
        if (connectorCtClass == null) {
            CtClass superClass = pool.get(AbstractConnector.class.getCanonicalName());
            connectorCtClass = pool.makeClass(connectorCanonicalName);

            connectorCtClass.setSuperclass(superClass);

            CtClass CtInterface = pool.get(connectorImplementedInterface.getCanonicalName());
            connectorCtClass.setInterfaces(new CtClass[]{CtInterface});

            CtInterface.detach();
            superClass.detach();

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

    StringBuilder builder;

    String previousTag;
    String equipmentRef;
    String offeringCast;
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

    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case "control-adapter":
                String offeredInterface = attributes.getValue("offered");
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