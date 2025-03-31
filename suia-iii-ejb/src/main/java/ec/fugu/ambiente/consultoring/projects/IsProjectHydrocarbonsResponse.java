
package ec.fugu.ambiente.consultoring.projects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para isProjectHydrocarbonsResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="isProjectHydrocarbonsResponse">
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
@XmlType(name = "isProjectHydrocarbonsResponse", propOrder = {
    "isHidrocarburos"
})
public class IsProjectHydrocarbonsResponse {

    protected Boolean isHidrocarburos;

    /**
     * Obtiene el valor de la propiedad isHidrocarburos.
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
     * Define el valor de la propiedad isHidrocarburos.
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
