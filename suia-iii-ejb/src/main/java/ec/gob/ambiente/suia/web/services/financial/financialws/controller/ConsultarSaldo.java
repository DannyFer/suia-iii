
package ec.gob.ambiente.suia.web.services.financial.financialws.controller;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for consultarSaldo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consultarSaldo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoEntidadBancaria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numeroTransaccion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultarSaldo", propOrder = {
    "codigoEntidadBancaria",
    "numeroTransaccion"
})
public class ConsultarSaldo {

    protected String codigoEntidadBancaria;
    protected String numeroTransaccion;

    /**
     * Gets the value of the codigoEntidadBancaria property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoEntidadBancaria() {
        return codigoEntidadBancaria;
    }

    /**
     * Sets the value of the codigoEntidadBancaria property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoEntidadBancaria(String value) {
        this.codigoEntidadBancaria = value;
    }

    /**
     * Gets the value of the numeroTransaccion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroTransaccion() {
        return numeroTransaccion;
    }

    /**
     * Sets the value of the numeroTransaccion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroTransaccion(String value) {
        this.numeroTransaccion = value;
    }

}
