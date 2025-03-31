
package ec.gob.ambiente.suia.procedimientosadministrativosmaelogin.controller;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ec.gob.ambiente.suia.procedimientosadministrativosmaelogin.controller package. 
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

    private final static QName _ObtenerRUC_QNAME = new QName("http://Controller.procedimientosAdministrativosMAELogin.suia.ambiente.gob.ec/", "obtenerRUC");
    private final static QName _ObtenerRUCResponse_QNAME = new QName("http://Controller.procedimientosAdministrativosMAELogin.suia.ambiente.gob.ec/", "obtenerRUCResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ec.gob.ambiente.suia.procedimientosadministrativosmaelogin.controller
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ObtenerRUC }
     * 
     */
    public ObtenerRUC createObtenerRUC() {
        return new ObtenerRUC();
    }

    /**
     * Create an instance of {@link ObtenerRUCResponse }
     * 
     */
    public ObtenerRUCResponse createObtenerRUCResponse() {
        return new ObtenerRUCResponse();
    }

    /**
     * Create an instance of {@link ProcedimientoAdministrativoVO }
     * 
     */
    public ProcedimientoAdministrativoVO createProcedimientoAdministrativoVO() {
        return new ProcedimientoAdministrativoVO();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerRUC }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://Controller.procedimientosAdministrativosMAELogin.suia.ambiente.gob.ec/", name = "obtenerRUC")
    public JAXBElement<ObtenerRUC> createObtenerRUC(ObtenerRUC value) {
        return new JAXBElement<ObtenerRUC>(_ObtenerRUC_QNAME, ObtenerRUC.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerRUCResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://Controller.procedimientosAdministrativosMAELogin.suia.ambiente.gob.ec/", name = "obtenerRUCResponse")
    public JAXBElement<ObtenerRUCResponse> createObtenerRUCResponse(ObtenerRUCResponse value) {
        return new JAXBElement<ObtenerRUCResponse>(_ObtenerRUCResponse_QNAME, ObtenerRUCResponse.class, null, value);
    }

}
