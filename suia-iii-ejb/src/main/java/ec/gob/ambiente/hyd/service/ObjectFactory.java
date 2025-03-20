
package ec.gob.ambiente.hyd.service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ec.gob.ambiente.hyd.service package. 
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

    private final static QName _GetNamePorcessResponse_QNAME = new QName("http://service.hyd.ambiente.gob.ec/", "getNamePorcessResponse");
    private final static QName _LogoutResponse_QNAME = new QName("http://service.hyd.ambiente.gob.ec/", "logoutResponse");
    private final static QName _GetTaskListResponse_QNAME = new QName("http://service.hyd.ambiente.gob.ec/", "getTaskListResponse");
    private final static QName _Logout_QNAME = new QName("http://service.hyd.ambiente.gob.ec/", "logout");
    private final static QName _Exception_QNAME = new QName("http://service.hyd.ambiente.gob.ec/", "Exception");
    private final static QName _GetNamePorcess_QNAME = new QName("http://service.hyd.ambiente.gob.ec/", "getNamePorcess");
    private final static QName _GetTaskList_QNAME = new QName("http://service.hyd.ambiente.gob.ec/", "getTaskList");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ec.gob.ambiente.hyd.service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link Logout }
     * 
     */
    public Logout createLogout() {
        return new Logout();
    }

    /**
     * Create an instance of {@link GetTaskList }
     * 
     */
    public GetTaskList createGetTaskList() {
        return new GetTaskList();
    }

    /**
     * Create an instance of {@link GetNamePorcess }
     * 
     */
    public GetNamePorcess createGetNamePorcess() {
        return new GetNamePorcess();
    }

    /**
     * Create an instance of {@link GetTaskListResponse }
     * 
     */
    public GetTaskListResponse createGetTaskListResponse() {
        return new GetTaskListResponse();
    }

    /**
     * Create an instance of {@link LogoutResponse }
     * 
     */
    public LogoutResponse createLogoutResponse() {
        return new LogoutResponse();
    }

    /**
     * Create an instance of {@link GetNamePorcessResponse }
     * 
     */
    public GetNamePorcessResponse createGetNamePorcessResponse() {
        return new GetNamePorcessResponse();
    }

    /**
     * Create an instance of {@link TaskSummaryDto }
     * 
     */
    public TaskSummaryDto createTaskSummaryDto() {
        return new TaskSummaryDto();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNamePorcessResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hyd.ambiente.gob.ec/", name = "getNamePorcessResponse")
    public JAXBElement<GetNamePorcessResponse> createGetNamePorcessResponse(GetNamePorcessResponse value) {
        return new JAXBElement<GetNamePorcessResponse>(_GetNamePorcessResponse_QNAME, GetNamePorcessResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LogoutResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hyd.ambiente.gob.ec/", name = "logoutResponse")
    public JAXBElement<LogoutResponse> createLogoutResponse(LogoutResponse value) {
        return new JAXBElement<LogoutResponse>(_LogoutResponse_QNAME, LogoutResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTaskListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hyd.ambiente.gob.ec/", name = "getTaskListResponse")
    public JAXBElement<GetTaskListResponse> createGetTaskListResponse(GetTaskListResponse value) {
        return new JAXBElement<GetTaskListResponse>(_GetTaskListResponse_QNAME, GetTaskListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Logout }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hyd.ambiente.gob.ec/", name = "logout")
    public JAXBElement<Logout> createLogout(Logout value) {
        return new JAXBElement<Logout>(_Logout_QNAME, Logout.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hyd.ambiente.gob.ec/", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<Exception>(_Exception_QNAME, Exception.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNamePorcess }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hyd.ambiente.gob.ec/", name = "getNamePorcess")
    public JAXBElement<GetNamePorcess> createGetNamePorcess(GetNamePorcess value) {
        return new JAXBElement<GetNamePorcess>(_GetNamePorcess_QNAME, GetNamePorcess.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTaskList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hyd.ambiente.gob.ec/", name = "getTaskList")
    public JAXBElement<GetTaskList> createGetTaskList(GetTaskList value) {
        return new JAXBElement<GetTaskList>(_GetTaskList_QNAME, GetTaskList.class, null, value);
    }

}
