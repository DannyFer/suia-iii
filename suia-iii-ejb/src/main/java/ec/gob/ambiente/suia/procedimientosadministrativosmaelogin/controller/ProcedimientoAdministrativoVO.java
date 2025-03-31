
package ec.gob.ambiente.suia.procedimientosadministrativosmaelogin.controller;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for procedimientoAdministrativoVO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="procedimientoAdministrativoVO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoRes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mensajeRes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paArchivado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paArgumento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paAsunto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paDireccion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paExtractoResolucion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paFechaApertura" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="paFechaAutosResolver" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="paFechaCierre" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="paFechaCitacion" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="paFechaDenuncia" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="paFechaEnvioCoactiva" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="paFechaEnvioExpediente" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="paFechaProvidencia" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="paFechaResolucion" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="paId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paMateria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paNumero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paObservacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paPresuntoInfractor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paRUC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paRecurso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paSecretarioAdHoc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "procedimientoAdministrativoVO", propOrder = {
    "codigoRes",
    "mensajeRes",
    "paArchivado",
    "paArgumento",
    "paAsunto",
    "paDireccion",
    "paExtractoResolucion",
    "paFechaApertura",
    "paFechaAutosResolver",
    "paFechaCierre",
    "paFechaCitacion",
    "paFechaDenuncia",
    "paFechaEnvioCoactiva",
    "paFechaEnvioExpediente",
    "paFechaProvidencia",
    "paFechaResolucion",
    "paId",
    "paMateria",
    "paNumero",
    "paObservacion",
    "paPresuntoInfractor",
    "paRUC",
    "paRecurso",
    "paSecretarioAdHoc"
})
public class ProcedimientoAdministrativoVO {

    protected String codigoRes;
    protected String mensajeRes;
    protected String paArchivado;
    protected String paArgumento;
    protected String paAsunto;
    protected String paDireccion;
    protected String paExtractoResolucion;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar paFechaApertura;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar paFechaAutosResolver;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar paFechaCierre;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar paFechaCitacion;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar paFechaDenuncia;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar paFechaEnvioCoactiva;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar paFechaEnvioExpediente;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar paFechaProvidencia;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar paFechaResolucion;
    protected String paId;
    protected String paMateria;
    protected String paNumero;
    protected String paObservacion;
    protected String paPresuntoInfractor;
    protected String paRUC;
    protected String paRecurso;
    protected String paSecretarioAdHoc;

    /**
     * Gets the value of the codigoRes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoRes() {
        return codigoRes;
    }

    /**
     * Sets the value of the codigoRes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoRes(String value) {
        this.codigoRes = value;
    }

    /**
     * Gets the value of the mensajeRes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMensajeRes() {
        return mensajeRes;
    }

    /**
     * Sets the value of the mensajeRes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMensajeRes(String value) {
        this.mensajeRes = value;
    }

    /**
     * Gets the value of the paArchivado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaArchivado() {
        return paArchivado;
    }

    /**
     * Sets the value of the paArchivado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaArchivado(String value) {
        this.paArchivado = value;
    }

    /**
     * Gets the value of the paArgumento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaArgumento() {
        return paArgumento;
    }

    /**
     * Sets the value of the paArgumento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaArgumento(String value) {
        this.paArgumento = value;
    }

    /**
     * Gets the value of the paAsunto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaAsunto() {
        return paAsunto;
    }

    /**
     * Sets the value of the paAsunto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaAsunto(String value) {
        this.paAsunto = value;
    }

    /**
     * Gets the value of the paDireccion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaDireccion() {
        return paDireccion;
    }

    /**
     * Sets the value of the paDireccion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaDireccion(String value) {
        this.paDireccion = value;
    }

    /**
     * Gets the value of the paExtractoResolucion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaExtractoResolucion() {
        return paExtractoResolucion;
    }

    /**
     * Sets the value of the paExtractoResolucion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaExtractoResolucion(String value) {
        this.paExtractoResolucion = value;
    }

    /**
     * Gets the value of the paFechaApertura property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaFechaApertura() {
        return paFechaApertura;
    }

    /**
     * Sets the value of the paFechaApertura property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaFechaApertura(XMLGregorianCalendar value) {
        this.paFechaApertura = value;
    }

    /**
     * Gets the value of the paFechaAutosResolver property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaFechaAutosResolver() {
        return paFechaAutosResolver;
    }

    /**
     * Sets the value of the paFechaAutosResolver property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaFechaAutosResolver(XMLGregorianCalendar value) {
        this.paFechaAutosResolver = value;
    }

    /**
     * Gets the value of the paFechaCierre property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaFechaCierre() {
        return paFechaCierre;
    }

    /**
     * Sets the value of the paFechaCierre property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaFechaCierre(XMLGregorianCalendar value) {
        this.paFechaCierre = value;
    }

    /**
     * Gets the value of the paFechaCitacion property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaFechaCitacion() {
        return paFechaCitacion;
    }

    /**
     * Sets the value of the paFechaCitacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaFechaCitacion(XMLGregorianCalendar value) {
        this.paFechaCitacion = value;
    }

    /**
     * Gets the value of the paFechaDenuncia property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaFechaDenuncia() {
        return paFechaDenuncia;
    }

    /**
     * Sets the value of the paFechaDenuncia property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaFechaDenuncia(XMLGregorianCalendar value) {
        this.paFechaDenuncia = value;
    }

    /**
     * Gets the value of the paFechaEnvioCoactiva property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaFechaEnvioCoactiva() {
        return paFechaEnvioCoactiva;
    }

    /**
     * Sets the value of the paFechaEnvioCoactiva property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaFechaEnvioCoactiva(XMLGregorianCalendar value) {
        this.paFechaEnvioCoactiva = value;
    }

    /**
     * Gets the value of the paFechaEnvioExpediente property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaFechaEnvioExpediente() {
        return paFechaEnvioExpediente;
    }

    /**
     * Sets the value of the paFechaEnvioExpediente property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaFechaEnvioExpediente(XMLGregorianCalendar value) {
        this.paFechaEnvioExpediente = value;
    }

    /**
     * Gets the value of the paFechaProvidencia property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaFechaProvidencia() {
        return paFechaProvidencia;
    }

    /**
     * Sets the value of the paFechaProvidencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaFechaProvidencia(XMLGregorianCalendar value) {
        this.paFechaProvidencia = value;
    }

    /**
     * Gets the value of the paFechaResolucion property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaFechaResolucion() {
        return paFechaResolucion;
    }

    /**
     * Sets the value of the paFechaResolucion property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaFechaResolucion(XMLGregorianCalendar value) {
        this.paFechaResolucion = value;
    }

    /**
     * Gets the value of the paId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaId() {
        return paId;
    }

    /**
     * Sets the value of the paId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaId(String value) {
        this.paId = value;
    }

    /**
     * Gets the value of the paMateria property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaMateria() {
        return paMateria;
    }

    /**
     * Sets the value of the paMateria property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaMateria(String value) {
        this.paMateria = value;
    }

    /**
     * Gets the value of the paNumero property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaNumero() {
        return paNumero;
    }

    /**
     * Sets the value of the paNumero property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaNumero(String value) {
        this.paNumero = value;
    }

    /**
     * Gets the value of the paObservacion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaObservacion() {
        return paObservacion;
    }

    /**
     * Sets the value of the paObservacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaObservacion(String value) {
        this.paObservacion = value;
    }

    /**
     * Gets the value of the paPresuntoInfractor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaPresuntoInfractor() {
        return paPresuntoInfractor;
    }

    /**
     * Sets the value of the paPresuntoInfractor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaPresuntoInfractor(String value) {
        this.paPresuntoInfractor = value;
    }

    /**
     * Gets the value of the paRUC property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaRUC() {
        return paRUC;
    }

    /**
     * Sets the value of the paRUC property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaRUC(String value) {
        this.paRUC = value;
    }

    /**
     * Gets the value of the paRecurso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaRecurso() {
        return paRecurso;
    }

    /**
     * Sets the value of the paRecurso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaRecurso(String value) {
        this.paRecurso = value;
    }

    /**
     * Gets the value of the paSecretarioAdHoc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaSecretarioAdHoc() {
        return paSecretarioAdHoc;
    }

    /**
     * Sets the value of the paSecretarioAdHoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaSecretarioAdHoc(String value) {
        this.paSecretarioAdHoc = value;
    }

}
