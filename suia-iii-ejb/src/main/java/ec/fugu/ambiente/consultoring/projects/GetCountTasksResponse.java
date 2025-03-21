
package ec.fugu.ambiente.consultoring.projects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para getCountTasksResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="getCountTasksResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="countTasks" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCountTasksResponse", propOrder = {
    "countTasks"
})
public class GetCountTasksResponse {

    protected Long countTasks;

    /**
     * Obtiene el valor de la propiedad countTasks.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCountTasks() {
        return countTasks;
    }

    /**
     * Define el valor de la propiedad countTasks.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCountTasks(Long value) {
        this.countTasks = value;
    }

}
