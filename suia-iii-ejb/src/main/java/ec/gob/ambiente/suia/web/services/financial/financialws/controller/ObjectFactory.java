
package ec.gob.ambiente.suia.web.services.financial.financialws.controller;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ec.gob.ambiente.suia.web.services.financial.financialws.controller package. 
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

    private final static QName _RealizarPagoResponse_QNAME = new QName("http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", "realizarPagoResponse");
    private final static QName _RevertirPagoResponse_QNAME = new QName("http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", "revertirPagoResponse");
    private final static QName _RealizarPago_QNAME = new QName("http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", "realizarPago");
    private final static QName _RevertirPago_QNAME = new QName("http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", "revertirPago");
    private final static QName _ConsultarSaldo_QNAME = new QName("http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", "consultarSaldo");
    private final static QName _ConsultarSaldoResponse_QNAME = new QName("http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", "consultarSaldoResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ec.gob.ambiente.suia.web.services.financial.financialws.controller
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RealizarPagoResponse }
     * 
     */
    public RealizarPagoResponse createRealizarPagoResponse() {
        return new RealizarPagoResponse();
    }

    /**
     * Create an instance of {@link RevertirPagoResponse }
     * 
     */
    public RevertirPagoResponse createRevertirPagoResponse() {
        return new RevertirPagoResponse();
    }

    /**
     * Create an instance of {@link RealizarPago }
     * 
     */
    public RealizarPago createRealizarPago() {
        return new RealizarPago();
    }

    /**
     * Create an instance of {@link RevertirPago }
     * 
     */
    public RevertirPago createRevertirPago() {
        return new RevertirPago();
    }

    /**
     * Create an instance of {@link ConsultarSaldo }
     * 
     */
    public ConsultarSaldo createConsultarSaldo() {
        return new ConsultarSaldo();
    }

    /**
     * Create an instance of {@link ConsultarSaldoResponse }
     * 
     */
    public ConsultarSaldoResponse createConsultarSaldoResponse() {
        return new ConsultarSaldoResponse();
    }

    /**
     * Create an instance of {@link PaymentResponseVO }
     * 
     */
    public PaymentResponseVO createPaymentResponseVO() {
        return new PaymentResponseVO();
    }

    /**
     * Create an instance of {@link PaymentUpdateResponseVO }
     * 
     */
    public PaymentUpdateResponseVO createPaymentUpdateResponseVO() {
        return new PaymentUpdateResponseVO();
    }

    /**
     * Create an instance of {@link PaymentInVO }
     * 
     */
    public PaymentInVO createPaymentInVO() {
        return new PaymentInVO();
    }

    /**
     * Create an instance of {@link RespuestaVO }
     * 
     */
    public RespuestaVO createRespuestaVO() {
        return new RespuestaVO();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RealizarPagoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", name = "realizarPagoResponse")
    public JAXBElement<RealizarPagoResponse> createRealizarPagoResponse(RealizarPagoResponse value) {
        return new JAXBElement<RealizarPagoResponse>(_RealizarPagoResponse_QNAME, RealizarPagoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RevertirPagoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", name = "revertirPagoResponse")
    public JAXBElement<RevertirPagoResponse> createRevertirPagoResponse(RevertirPagoResponse value) {
        return new JAXBElement<RevertirPagoResponse>(_RevertirPagoResponse_QNAME, RevertirPagoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RealizarPago }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", name = "realizarPago")
    public JAXBElement<RealizarPago> createRealizarPago(RealizarPago value) {
        return new JAXBElement<RealizarPago>(_RealizarPago_QNAME, RealizarPago.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RevertirPago }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", name = "revertirPago")
    public JAXBElement<RevertirPago> createRevertirPago(RevertirPago value) {
        return new JAXBElement<RevertirPago>(_RevertirPago_QNAME, RevertirPago.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarSaldo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", name = "consultarSaldo")
    public JAXBElement<ConsultarSaldo> createConsultarSaldo(ConsultarSaldo value) {
        return new JAXBElement<ConsultarSaldo>(_ConsultarSaldo_QNAME, ConsultarSaldo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarSaldoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", name = "consultarSaldoResponse")
    public JAXBElement<ConsultarSaldoResponse> createConsultarSaldoResponse(ConsultarSaldoResponse value) {
        return new JAXBElement<ConsultarSaldoResponse>(_ConsultarSaldoResponse_QNAME, ConsultarSaldoResponse.class, null, value);
    }

}
