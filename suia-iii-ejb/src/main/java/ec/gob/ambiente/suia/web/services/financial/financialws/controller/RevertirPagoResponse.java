
package ec.gob.ambiente.suia.web.services.financial.financialws.controller;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for revertirPagoResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="revertirPagoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="revertirResultado" type="{http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/}respuestaVO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "revertirPagoResponse", propOrder = {
    "revertirResultado"
})
public class RevertirPagoResponse {

    protected RespuestaVO revertirResultado;

    /**
     * Gets the value of the revertirResultado property.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaVO }
     *     
     */
    public RespuestaVO getRevertirResultado() {
        return revertirResultado;
    }

    /**
     * Sets the value of the revertirResultado property.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaVO }
     *     
     */
    public void setRevertirResultado(RespuestaVO value) {
        this.revertirResultado = value;
    }

}
