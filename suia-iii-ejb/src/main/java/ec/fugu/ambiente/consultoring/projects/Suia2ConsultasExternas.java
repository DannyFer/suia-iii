
package ec.fugu.ambiente.consultoring.projects;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "Suia2ConsultasExternas", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Suia2ConsultasExternas {


    /**
     * 
     * @param nombreProyecto
     * @param userName
     * @param codigoProyecto
     * @return
     *     returns java.lang.Long
     */
    @WebMethod
    @WebResult(name = "cantidadListProjects", targetNamespace = "")
    @RequestWrapper(localName = "getProjectsCantidadExternal", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.GetProjectsCantidadExternal")
    @ResponseWrapper(localName = "getProjectsCantidadExternalResponse", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.GetProjectsCantidadExternalResponse")
    public Long getProjectsCantidadExternal(
        @WebParam(name = "userName", targetNamespace = "")
        String userName,
        @WebParam(name = "codigoProyecto", targetNamespace = "")
        String codigoProyecto,
        @WebParam(name = "nombreProyecto", targetNamespace = "")
        String nombreProyecto,
	    @WebParam(name = "sector", targetNamespace = "")
	    String sector,
	    @WebParam(name = "permiso", targetNamespace = "")
	    String permiso);

    /**
     * 
     * @param limite
     * @param nombreProyecto
     * @param userName
     * @param user
     * @param codigoProyecto
     * @param desde
     * @param pass
     * @return
     *     returns java.util.List<ec.fugu.ambiente.consultoring.projects.ProyectoLicIntegacionVo>
     */
    @WebMethod
    @WebResult(name = "listProjects", targetNamespace = "")
    @RequestWrapper(localName = "getProjectsExternal", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.GetProjectsExternal")
    @ResponseWrapper(localName = "getProjectsExternalResponse", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.GetProjectsExternalResponse")
    public List<ProyectoLicIntegacionVo> getProjectsExternal(
        @WebParam(name = "user", targetNamespace = "")
        String user,
        @WebParam(name = "pass", targetNamespace = "")
        String pass,
        @WebParam(name = "userName", targetNamespace = "")
        String userName,
        @WebParam(name = "limite", targetNamespace = "")
        int limite,
        @WebParam(name = "desde", targetNamespace = "")
        int desde,
        @WebParam(name = "codigoProyecto", targetNamespace = "")
        String codigoProyecto,
        @WebParam(name = "nombreProyecto", targetNamespace = "")
        String nombreProyecto,
        @WebParam(name = "sector", targetNamespace = "")
	    String sector,
	    @WebParam(name = "permiso", targetNamespace = "")
	    String permiso);

    /**
     * 
     * @param assignee
     * @param user
     * @param pass
     * @return
     *     returns java.util.List<ec.fugu.ambiente.consultoring.projects.Task>
     */
    @WebMethod
    @WebResult(name = "listTasks", targetNamespace = "")
    @RequestWrapper(localName = "getTasksExternal", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.GetTasksExternal")
    @ResponseWrapper(localName = "getTasksExternalResponse", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.GetTasksExternalResponse")
    public List<Task> getTasksExternal(
        @WebParam(name = "user", targetNamespace = "")
        String user,
        @WebParam(name = "pass", targetNamespace = "")
        String pass,
        @WebParam(name = "assignee", targetNamespace = "")
        String assignee);

    /**
     * 
     * @param projectId
     * @param user
     * @param pass
     * @return
     *     returns java.lang.Boolean
     */
    @WebMethod
    @WebResult(name = "isHidrocarburos", targetNamespace = "")
    @RequestWrapper(localName = "isProjectHydrocarbonsExternal", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.IsProjectHydrocarbonsExternal")
    @ResponseWrapper(localName = "isProjectHydrocarbonsExternalResponse", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.IsProjectHydrocarbonsExternalResponse")
    public Boolean isProjectHydrocarbonsExternal(
        @WebParam(name = "user", targetNamespace = "")
        String user,
        @WebParam(name = "pass", targetNamespace = "")
        String pass,
        @WebParam(name = "projectId", targetNamespace = "")
        String projectId);

}
