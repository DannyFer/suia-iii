
package ec.gob.ambiente.suia.web.services.financial.financialws.controller;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for realizarPago complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="realizarPago">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="informacionPago" type="{http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/}paymentInVO" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="valorPagar" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="codigoProyecto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "realizarPago", propOrder = {
    "informacionPago",
    "valorPagar",
    "codigoProyecto"
})
public class RealizarPago {

    protected List<PaymentInVO> informacionPago;
    protected Double valorPagar;
    protected String codigoProyecto;

    /**
     * Gets the value of the informacionPago property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the informacionPago property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInformacionPago().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentInVO }
     * 
     * 
     */
    public List<PaymentInVO> getInformacionPago() {
        if (informacionPago == null) {
            informacionPago = new ArrayList<PaymentInVO>();
        }
        return this.informacionPago;
    }

    /**
     * Gets the value of the valorPagar property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getValorPagar() {
        return valorPagar;
    }

    /**
     * Sets the value of the valorPagar property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setValorPagar(Double value) {
        this.valorPagar = value;
    }

    /**
     * Gets the value of the codigoProyecto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoProyecto() {
        return codigoProyecto;
    }

    /**
     * Sets the value of the codigoProyecto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoProyecto(String value) {
        this.codigoProyecto = value;
    }

}
