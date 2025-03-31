
package ec.fugu.ambiente.consultoring.retasking;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ec.fugu.ambiente.consultoring.retasking package. 
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

    private final static QName _ReasignarProyestos_QNAME = new QName("http://retasking.consultoring.ambiente.fugu.ec/", "reasignarProyestos");
    private final static QName _TraerCargaTecnicos_QNAME = new QName("http://retasking.consultoring.ambiente.fugu.ec/", "traerCargaTecnicos");
    private final static QName _ListaTareasResponse_QNAME = new QName("http://retasking.consultoring.ambiente.fugu.ec/", "listaTareasResponse");
    private final static QName _ListaTareas_QNAME = new QName("http://retasking.consultoring.ambiente.fugu.ec/", "listaTareas");
    private final static QName _TraerCargaTecnicosResponse_QNAME = new QName("http://retasking.consultoring.ambiente.fugu.ec/", "traerCargaTecnicosResponse");
    private final static QName _ReasignarProyestosResponse_QNAME = new QName("http://retasking.consultoring.ambiente.fugu.ec/", "reasignarProyestosResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ec.fugu.ambiente.consultoring.retasking
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TraerCargaTecnicosResponse }
     * 
     */
    public TraerCargaTecnicosResponse createTraerCargaTecnicosResponse() {
        return new TraerCargaTecnicosResponse();
    }

    /**
     * Create an instance of {@link ReasignarProyestosResponse }
     * 
     */
    public ReasignarProyestosResponse createReasignarProyestosResponse() {
        return new ReasignarProyestosResponse();
    }

    /**
     * Create an instance of {@link ListaTareasResponse }
     * 
     */
    public ListaTareasResponse createListaTareasResponse() {
        return new ListaTareasResponse();
    }

    /**
     * Create an instance of {@link TraerCargaTecnicos }
     * 
     */
    public TraerCargaTecnicos createTraerCargaTecnicos() {
        return new TraerCargaTecnicos();
    }

    /**
     * Create an instance of {@link ReasignarProyestos }
     * 
     */
    public ReasignarProyestos createReasignarProyestos() {
        return new ReasignarProyestos();
    }

    /**
     * Create an instance of {@link ListaTareas }
     * 
     */
    public ListaTareas createListaTareas() {
        return new ListaTareas();
    }

    /**
     * Create an instance of {@link ProyectoLicenciaVo }
     * 
     */
    public ProyectoLicenciaVo createProyectoLicenciaVo() {
        return new ProyectoLicenciaVo();
    }

    /**
     * Create an instance of {@link UsuarioCarga }
     * 
     */
    public UsuarioCarga createUsuarioCarga() {
        return new UsuarioCarga();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReasignarProyestos }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://retasking.consultoring.ambiente.fugu.ec/", name = "reasignarProyestos")
    public JAXBElement<ReasignarProyestos> createReasignarProyestos(ReasignarProyestos value) {
        return new JAXBElement<ReasignarProyestos>(_ReasignarProyestos_QNAME, ReasignarProyestos.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraerCargaTecnicos }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://retasking.consultoring.ambiente.fugu.ec/", name = "traerCargaTecnicos")
    public JAXBElement<TraerCargaTecnicos> createTraerCargaTecnicos(TraerCargaTecnicos value) {
        return new JAXBElement<TraerCargaTecnicos>(_TraerCargaTecnicos_QNAME, TraerCargaTecnicos.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListaTareasResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://retasking.consultoring.ambiente.fugu.ec/", name = "listaTareasResponse")
    public JAXBElement<ListaTareasResponse> createListaTareasResponse(ListaTareasResponse value) {
        return new JAXBElement<ListaTareasResponse>(_ListaTareasResponse_QNAME, ListaTareasResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListaTareas }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://retasking.consultoring.ambiente.fugu.ec/", name = "listaTareas")
    public JAXBElement<ListaTareas> createListaTareas(ListaTareas value) {
        return new JAXBElement<ListaTareas>(_ListaTareas_QNAME, ListaTareas.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraerCargaTecnicosResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://retasking.consultoring.ambiente.fugu.ec/", name = "traerCargaTecnicosResponse")
    public JAXBElement<TraerCargaTecnicosResponse> createTraerCargaTecnicosResponse(TraerCargaTecnicosResponse value) {
        return new JAXBElement<TraerCargaTecnicosResponse>(_TraerCargaTecnicosResponse_QNAME, TraerCargaTecnicosResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReasignarProyestosResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://retasking.consultoring.ambiente.fugu.ec/", name = "reasignarProyestosResponse")
    public JAXBElement<ReasignarProyestosResponse> createReasignarProyestosResponse(ReasignarProyestosResponse value) {
        return new JAXBElement<ReasignarProyestosResponse>(_ReasignarProyestosResponse_QNAME, ReasignarProyestosResponse.class, null, value);
    }

}
