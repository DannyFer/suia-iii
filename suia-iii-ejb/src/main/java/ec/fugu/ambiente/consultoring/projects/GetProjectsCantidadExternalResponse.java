
package ec.fugu.ambiente.consultoring.projects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getProjectsCantidadExternalResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getProjectsCantidadExternalResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cantidadListProjects" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getProjectsCantidadExternalResponse", propOrder = {
    "cantidadListProjects"
})
public class GetProjectsCantidadExternalResponse {

    protected Long cantidadListProjects;

    /**
     * Gets the value of the cantidadListProjects property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCantidadListProjects() {
        return cantidadListProjects;
    }

    /**
     * Sets the value of the cantidadListProjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCantidadListProjects(Long value) {
        this.cantidadListProjects = value;
    }

}
