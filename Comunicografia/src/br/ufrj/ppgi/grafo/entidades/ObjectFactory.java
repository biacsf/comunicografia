//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.03.26 at 03:52:01 PM BRT 
//


package br.ufrj.ppgi.grafo.entidades;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;



/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Discussao_QNAME = new QName("", "discussao");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Mensagem }
     * 
     */
    public Mensagem createMensagem() {
        return new Mensagem();
    }

    /**
     * Create an instance of {@link Discussao }
     * 
     */
    public Discussao createDiscussao() {
        return new Discussao();
    }
    

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Discussao }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "discussao")
    public JAXBElement<Discussao> createDiscussao(Discussao value) {
        return new JAXBElement<Discussao>(_Discussao_QNAME, Discussao.class, null, value);
    }

}
