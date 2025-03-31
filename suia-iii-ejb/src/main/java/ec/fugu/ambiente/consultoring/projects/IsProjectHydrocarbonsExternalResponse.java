
package ec.fugu.ambiente.consultoring.projects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for isProjectHydrocarbonsExternalResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="isProjectHydrocarbonsExternalResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="isHidrocarburos" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "isProjectHydrocarbonsExternalResponse", propOrder = {
    "isHidrocarburos"
})
public class IsProjectHydrocarbonsExternalResponse {

    protected Boolean isHidrocarburos;

    /**
     * Gets the value of the isHidrocarburos property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsHidrocarburos() {
        return isHidrocarburos;
    }

    /**
     * Sets the value of the isHidrocarburos property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsHidrocarburos(Boolean value) {
        this.isHidrocarburos = value;
    }

}
