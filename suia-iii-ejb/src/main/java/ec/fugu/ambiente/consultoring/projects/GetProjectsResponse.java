
package ec.fugu.ambiente.consultoring.projects;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para getProjectsResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="getProjectsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="listProjects" type="{http://projects.consultoring.ambiente.fugu.ec/}proyectoLicIntegacionVo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getProjectsResponse", propOrder = {
    "listProjects"
})
public class GetProjectsResponse {

    protected List<ProyectoLicIntegacionVo> listProjects;

    /**
     * Gets the value of the listProjects property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listProjects property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListProjects().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProyectoLicIntegacionVo }
     * 
     * 
     */
    public List<ProyectoLicIntegacionVo> getListProjects() {
        if (listProjects == null) {
            listProjects = new ArrayList<ProyectoLicIntegacionVo>();
        }
        return this.listProjects;
    }

}
