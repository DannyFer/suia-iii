
package ec.fugu.ambiente.consultoring.projects;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ec.fugu.ambiente.consultoring.projects package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetCountTasksResponse_QNAME = new QName("http://projects.consultoring.ambiente.fugu.ec/", "getCountTasksResponse");
    private final static QName _GetCountTasks_QNAME = new QName("http://projects.consultoring.ambiente.fugu.ec/", "getCountTasks");
    private final static QName _GetTasksResponse_QNAME = new QName("http://projects.consultoring.ambiente.fugu.ec/", "getTasksResponse");
    private final static QName _GetProjectsResponse_QNAME = new QName("http://projects.consultoring.ambiente.fugu.ec/", "getProjectsResponse");
    private final static QName _GetTasks_QNAME = new QName("http://projects.consultoring.ambiente.fugu.ec/", "getTasks");
    private final static QName _IsProjectHydrocarbons_QNAME = new QName("http://projects.consultoring.ambiente.fugu.ec/", "isProjectHydrocarbons");
    private final static QName _IsProjectHydrocarbonsResponse_QNAME = new QName("http://projects.consultoring.ambiente.fugu.ec/", "isProjectHydrocarbonsResponse");
    private final static QName _GetProjects_QNAME = new QName("http://projects.consultoring.ambiente.fugu.ec/", "getProjects");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ec.fugu.ambiente.consultoring.projects
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link IsProjectHydrocarbons }
     * 
     */
    public IsProjectHydrocarbons createIsProjectHydrocarbons() {
        return new IsProjectHydrocarbons();
    }

    /**
     * Create an instance of {@link IsProjectHydrocarbonsResponse }
     * 
     */
    public IsProjectHydrocarbonsResponse createIsProjectHydrocarbonsResponse() {
        return new IsProjectHydrocarbonsResponse();
    }

    /**
     * Create an instance of {@link GetProjects }
     * 
     */
    public GetProjects createGetProjects() {
        return new GetProjects();
    }

    /**
     * Create an instance of {@link GetTasksResponse }
     * 
     */
    public GetTasksResponse createGetTasksResponse() {
        return new GetTasksResponse();
    }

    /**
     * Create an instance of {@link GetProjectsResponse }
     * 
     */
    public GetProjectsResponse createGetProjectsResponse() {
        return new GetProjectsResponse();
    }

    /**
     * Create an instance of {@link GetTasks }
     * 
     */
    public GetTasks createGetTasks() {
        return new GetTasks();
    }

    /**
     * Create an instance of {@link GetCountTasksResponse }
     * 
     */
    public GetCountTasksResponse createGetCountTasksResponse() {
        return new GetCountTasksResponse();
    }

    /**
     * Create an instance of {@link GetCountTasks }
     * 
     */
    public GetCountTasks createGetCountTasks() {
        return new GetCountTasks();
    }

    /**
     * Create an instance of {@link Task }
     * 
     */
    public Task createTask() {
        return new Task();
    }

    /**
     * Create an instance of {@link ProyectoLicIntegacionVo }
     * 
     */
    public ProyectoLicIntegacionVo createProyectoLicIntegacionVo() {
        return new ProyectoLicIntegacionVo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCountTasksResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://projects.consultoring.ambiente.fugu.ec/", name = "getCountTasksResponse")
    public JAXBElement<GetCountTasksResponse> createGetCountTasksResponse(GetCountTasksResponse value) {
        return new JAXBElement<GetCountTasksResponse>(_GetCountTasksResponse_QNAME, GetCountTasksResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCountTasks }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://projects.consultoring.ambiente.fugu.ec/", name = "getCountTasks")
    public JAXBElement<GetCountTasks> createGetCountTasks(GetCountTasks value) {
        return new JAXBElement<GetCountTasks>(_GetCountTasks_QNAME, GetCountTasks.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTasksResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://projects.consultoring.ambiente.fugu.ec/", name = "getTasksResponse")
    public JAXBElement<GetTasksResponse> createGetTasksResponse(GetTasksResponse value) {
        return new JAXBElement<GetTasksResponse>(_GetTasksResponse_QNAME, GetTasksResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetProjectsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://projects.consultoring.ambiente.fugu.ec/", name = "getProjectsResponse")
    public JAXBElement<GetProjectsResponse> createGetProjectsResponse(GetProjectsResponse value) {
        return new JAXBElement<GetProjectsResponse>(_GetProjectsResponse_QNAME, GetProjectsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTasks }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://projects.consultoring.ambiente.fugu.ec/", name = "getTasks")
    public JAXBElement<GetTasks> createGetTasks(GetTasks value) {
        return new JAXBElement<GetTasks>(_GetTasks_QNAME, GetTasks.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsProjectHydrocarbons }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://projects.consultoring.ambiente.fugu.ec/", name = "isProjectHydrocarbons")
    public JAXBElement<IsProjectHydrocarbons> createIsProjectHydrocarbons(IsProjectHydrocarbons value) {
        return new JAXBElement<IsProjectHydrocarbons>(_IsProjectHydrocarbons_QNAME, IsProjectHydrocarbons.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsProjectHydrocarbonsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://projects.consultoring.ambiente.fugu.ec/", name = "isProjectHydrocarbonsResponse")
    public JAXBElement<IsProjectHydrocarbonsResponse> createIsProjectHydrocarbonsResponse(IsProjectHydrocarbonsResponse value) {
        return new JAXBElement<IsProjectHydrocarbonsResponse>(_IsProjectHydrocarbonsResponse_QNAME, IsProjectHydrocarbonsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetProjects }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://projects.consultoring.ambiente.fugu.ec/", name = "getProjects")
    public JAXBElement<GetProjects> createGetProjects(GetProjects value) {
        return new JAXBElement<GetProjects>(_GetProjects_QNAME, GetProjects.class, null, value);
    }

}
