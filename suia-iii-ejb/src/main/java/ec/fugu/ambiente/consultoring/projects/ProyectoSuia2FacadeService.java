
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
@WebService(name = "ProyectoSuia2FacadeService", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ProyectoSuia2FacadeService {


    /**
     * 
     * @param userName
     * @param user
     * @param pass
     * @return
     *     returns java.util.List<ec.fugu.ambiente.consultoring.projects.ProyectoLicIntegacionVo>
     */
    @WebMethod
    @WebResult(name = "listProjects", targetNamespace = "")
    @RequestWrapper(localName = "getProjects", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.GetProjects")
    @ResponseWrapper(localName = "getProjectsResponse", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.GetProjectsResponse")
    public List<ProyectoLicIntegacionVo> getProjects(
        @WebParam(name = "user", targetNamespace = "")
        String user,
        @WebParam(name = "pass", targetNamespace = "")
        String pass,
        @WebParam(name = "userName", targetNamespace = "")
        String userName);

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
    @RequestWrapper(localName = "getTasks", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.GetTasks")
    @ResponseWrapper(localName = "getTasksResponse", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.GetTasksResponse")
    public List<Task> getTasks(
        @WebParam(name = "user", targetNamespace = "")
        String user,
        @WebParam(name = "pass", targetNamespace = "")
        String pass,
        @WebParam(name = "assignee", targetNamespace = "")
        String assignee,
        @WebParam(name = "tramite", targetNamespace = "")
        String tramite,
        @WebParam(name = "flujo", targetNamespace = "")
        String flujo,
        @WebParam(name = "actividad", targetNamespace = "")
        String actividad,
        @WebParam(name = "limite", targetNamespace = "")
        int limite,
        @WebParam(name = "desde", targetNamespace = "")
        int desde);
    
    
    /**
     * 
     * @param assignee
     * @param user
     * @param pass
     * @param assignee
     * @param tramite
     * @param flujo
     * @param actividad
     * @return
     *     returns java.util.List<ec.fugu.ambiente.consultoring.projects.CountTask>
     */
    @WebMethod
    @WebResult(name = "countTasks", targetNamespace = "")
    @RequestWrapper(localName = "getCountTasks", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.GetCountTasks")
    @ResponseWrapper(localName = "getTasksResponse", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.GetCountTasksResponse")
    public Long getCountTasks(
        @WebParam(name = "user", targetNamespace = "")
        String user,
        @WebParam(name = "pass", targetNamespace = "")
        String pass,
        @WebParam(name = "assignee", targetNamespace = "")
        String assignee,
        @WebParam(name = "tramite", targetNamespace = "")
        String tramite,
        @WebParam(name = "flujo", targetNamespace = "")
        String flujo,
        @WebParam(name = "actividad", targetNamespace = "")
        String actividad);
    
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
    @RequestWrapper(localName = "isProjectHydrocarbons", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.IsProjectHydrocarbons")
    @ResponseWrapper(localName = "isProjectHydrocarbonsResponse", targetNamespace = "http://projects.consultoring.ambiente.fugu.ec/", className = "ec.fugu.ambiente.consultoring.projects.IsProjectHydrocarbonsResponse")
    public Boolean isProjectHydrocarbons(
        @WebParam(name = "user", targetNamespace = "")
        String user,
        @WebParam(name = "pass", targetNamespace = "")
        String pass,
        @WebParam(name = "projectId", targetNamespace = "")
        String projectId);

}
