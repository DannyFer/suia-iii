
package ec.gob.ambiente.suia.web.services.financial.financialws.controller;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "PaymentServiceInternal", targetNamespace = "http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface PaymentServiceInternal {


    /**
     * 
     * @param listaTransacciones
     * @return
     *     returns ec.gob.ambiente.suia.web.services.financial.financialws.controller.RespuestaVO
     */
    @WebMethod
    @WebResult(name = "revertirResultado", targetNamespace = "")
    @RequestWrapper(localName = "revertirPago", targetNamespace = "http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", className = "ec.gob.ambiente.suia.web.services.financial.financialws.controller.RevertirPago")
    @ResponseWrapper(localName = "revertirPagoResponse", targetNamespace = "http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", className = "ec.gob.ambiente.suia.web.services.financial.financialws.controller.RevertirPagoResponse")
    public RespuestaVO revertirPago(
            @WebParam(name = "listaTransacciones", targetNamespace = "")
            List<PaymentInVO> listaTransacciones);

    /**
     * 
     * @param valorPagar
     * @param informacionPago
     * @param codigoProyecto
     * @return
     *     returns java.util.List<ec.gob.ambiente.suia.web.services.financial.financialws.controller.PaymentUpdateResponseVO>
     */
    @WebMethod
    @WebResult(name = "resultado", targetNamespace = "")
    @RequestWrapper(localName = "realizarPago", targetNamespace = "http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", className = "ec.gob.ambiente.suia.web.services.financial.financialws.controller.RealizarPago")
    @ResponseWrapper(localName = "realizarPagoResponse", targetNamespace = "http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", className = "ec.gob.ambiente.suia.web.services.financial.financialws.controller.RealizarPagoResponse")
    public List<PaymentUpdateResponseVO> realizarPago(
            @WebParam(name = "informacionPago", targetNamespace = "")
            List<PaymentInVO> informacionPago,
            @WebParam(name = "valorPagar", targetNamespace = "")
            Double valorPagar,
            @WebParam(name = "codigoProyecto", targetNamespace = "")
            String codigoProyecto);

    /**
     * 
     * @param numeroTransaccion
     * @param codigoEntidadBancaria
     * @return
     *     returns ec.gob.ambiente.suia.web.services.financial.financialws.controller.PaymentResponseVO
     */
    @WebMethod
    @WebResult(name = "resultado", targetNamespace = "")
    @RequestWrapper(localName = "consultarSaldo", targetNamespace = "http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", className = "ec.gob.ambiente.suia.web.services.financial.financialws.controller.ConsultarSaldo")
    @ResponseWrapper(localName = "consultarSaldoResponse", targetNamespace = "http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/", className = "ec.gob.ambiente.suia.web.services.financial.financialws.controller.ConsultarSaldoResponse")
    public PaymentResponseVO consultarSaldo(
            @WebParam(name = "codigoEntidadBancaria", targetNamespace = "")
            String codigoEntidadBancaria,
            @WebParam(name = "numeroTransaccion", targetNamespace = "")
            String numeroTransaccion);

}
