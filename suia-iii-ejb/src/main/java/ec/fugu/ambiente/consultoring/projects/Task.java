
package ec.fugu.ambiente.consultoring.projects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for task complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="task">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="create_" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dbid_" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="execution_id_" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nameFlow_" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name_" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name_show_" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="projectId_" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "task", propOrder = {
    "create",
    "dbid",
    "executionId",
    "nameFlow",
    "name",
    "nameShow",
    "projectId"
})
public class Task {

    @XmlElement(name = "create_")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar create;
    @XmlElement(name = "dbid_")
    protected String dbid;
    @XmlElement(name = "execution_id_")
    protected String executionId;
    @XmlElement(name = "nameFlow_")
    protected String nameFlow;
    @XmlElement(name = "name_")
    protected String name;
    @XmlElement(name = "name_show_")
    protected String nameShow;
    @XmlElement(name = "projectId_")
    protected String projectId;

    /**
     * Gets the value of the create property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreate() {
        return create;
    }

    /**
     * Sets the value of the create property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreate(XMLGregorianCalendar value) {
        this.create = value;
    }

    /**
     * Gets the value of the dbid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDbid() {
        return dbid;
    }

    /**
     * Sets the value of the dbid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDbid(String value) {
        this.dbid = value;
    }

    /**
     * Gets the value of the executionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExecutionId() {
        return executionId;
    }

    /**
     * Sets the value of the executionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExecutionId(String value) {
        this.executionId = value;
    }

    /**
     * Gets the value of the nameFlow property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameFlow() {
        return nameFlow;
    }

    /**
     * Sets the value of the nameFlow property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameFlow(String value) {
        this.nameFlow = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the nameShow property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameShow() {
        return nameShow;
    }

    /**
     * Sets the value of the nameShow property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameShow(String value) {
        this.nameShow = value;
    }

    /**
     * Gets the value of the projectId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Sets the value of the projectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjectId(String value) {
        this.projectId = value;
    }

}
