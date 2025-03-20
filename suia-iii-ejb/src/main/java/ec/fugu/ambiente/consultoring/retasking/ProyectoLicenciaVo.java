
package ec.fugu.ambiente.consultoring.retasking;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for proyectoLicenciaVo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="proyectoLicenciaVo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="actividadNombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="actividadNombreMostrar" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="error" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaRegistro" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="flujo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="flujoMostrar" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="usuarioLdap" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "proyectoLicenciaVo", propOrder = {
    "actividadNombre",
    "actividadNombreMostrar",
    "error",
    "fechaRegistro",
    "flujo",
    "flujoMostrar",
    "id",
    "nombre",
    "usuarioLdap"
})
public class ProyectoLicenciaVo {

    protected String actividadNombre;
    protected String actividadNombreMostrar;
    protected String error;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaRegistro;
    protected String flujo;
    protected String flujoMostrar;
    protected String id;
    protected String nombre;
    protected String usuarioLdap;

    /**
     * Gets the value of the actividadNombre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActividadNombre() {
        return actividadNombre;
    }

    /**
     * Sets the value of the actividadNombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActividadNombre(String value) {
        this.actividadNombre = value;
    }

    /**
     * Gets the value of the actividadNombreMostrar property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActividadNombreMostrar() {
        return actividadNombreMostrar;
    }

    /**
     * Sets the value of the actividadNombreMostrar property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActividadNombreMostrar(String value) {
        this.actividadNombreMostrar = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setError(String value) {
        this.error = value;
    }

    /**
     * Gets the value of the fechaRegistro property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Sets the value of the fechaRegistro property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaRegistro(XMLGregorianCalendar value) {
        this.fechaRegistro = value;
    }

    /**
     * Gets the value of the flujo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlujo() {
        return flujo;
    }

    /**
     * Sets the value of the flujo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlujo(String value) {
        this.flujo = value;
    }

    /**
     * Gets the value of the flujoMostrar property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlujoMostrar() {
        return flujoMostrar;
    }

    /**
     * Sets the value of the flujoMostrar property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlujoMostrar(String value) {
        this.flujoMostrar = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the nombre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Sets the value of the nombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombre(String value) {
        this.nombre = value;
    }

    /**
     * Gets the value of the usuarioLdap property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsuarioLdap() {
        return usuarioLdap;
    }

    /**
     * Sets the value of the usuarioLdap property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsuarioLdap(String value) {
        this.usuarioLdap = value;
    }

}
