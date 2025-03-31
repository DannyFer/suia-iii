
package ec.fugu.ambiente.consultoring.retasking;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reasignarProyestos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reasignarProyestos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="proyectoSelecionados" type="{http://retasking.consultoring.ambiente.fugu.ec/}proyectoLicenciaVo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="usuarioAnterior" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="usuarioReasignar" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reasignarProyestos", propOrder = {
    "proyectoSelecionados",
    "usuarioAnterior",
    "usuarioReasignar"
})
public class ReasignarProyestos {

    protected List<ProyectoLicenciaVo> proyectoSelecionados;
    protected String usuarioAnterior;
    protected String usuarioReasignar;

    /**
     * Gets the value of the proyectoSelecionados property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the proyectoSelecionados property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProyectoSelecionados().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProyectoLicenciaVo }
     * 
     * 
     */
    public List<ProyectoLicenciaVo> getProyectoSelecionados() {
        if (proyectoSelecionados == null) {
            proyectoSelecionados = new ArrayList<ProyectoLicenciaVo>();
        }
        return this.proyectoSelecionados;
    }

    /**
     * Gets the value of the usuarioAnterior property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsuarioAnterior() {
        return usuarioAnterior;
    }

    /**
     * Sets the value of the usuarioAnterior property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsuarioAnterior(String value) {
        this.usuarioAnterior = value;
    }

    /**
     * Gets the value of the usuarioReasignar property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsuarioReasignar() {
        return usuarioReasignar;
    }

    /**
     * Sets the value of the usuarioReasignar property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsuarioReasignar(String value) {
        this.usuarioReasignar = value;
    }

}
