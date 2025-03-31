
package ec.gob.ambiente.suia.web.services.financial.financialws.controller;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for revertirPago complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="revertirPago">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="listaTransacciones" type="{http://controller.financialWS.financial.services.web.suia.ambiente.gob.ec/}paymentInVO" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "revertirPago", propOrder = {
    "listaTransacciones"
})
public class RevertirPago {

    protected List<PaymentInVO> listaTransacciones;

    /**
     * Gets the value of the listaTransacciones property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listaTransacciones property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListaTransacciones().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentInVO }
     * 
     * 
     */
    public List<PaymentInVO> getListaTransacciones() {
        if (listaTransacciones == null) {
            listaTransacciones = new ArrayList<PaymentInVO>();
        }
        return this.listaTransacciones;
    }

}
