
package ec.gob.ambiente.hyd.service;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

import ec.gob.ambiente.suia.utils.Constantes;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "HidrocarburosWsService", targetNamespace = "http://service.hyd.ambiente.gob.ec/", wsdlLocation = "http://qa-suia.ambiente.gob.ec:8092/hidrocarburos/remote/hidrocarburosWs?wsdl")
public class HidrocarburosWsService
    extends Service
{

    private final static URL HIDROCARBUROSWSSERVICE_WSDL_LOCATION;
    private final static WebServiceException HIDROCARBUROSWSSERVICE_EXCEPTION;
    private final static QName HIDROCARBUROSWSSERVICE_QNAME = new QName("http://service.hyd.ambiente.gob.ec/", "HidrocarburosWsService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://qa-suia.ambiente.gob.ec:8092/hidrocarburos/remote/hidrocarburosWs?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        HIDROCARBUROSWSSERVICE_WSDL_LOCATION = url;
        HIDROCARBUROSWSSERVICE_EXCEPTION = e;
    }
    
    public static URL getServiceUrl() throws MalformedURLException {
		return new URL(Constantes.getAppIntegrationServicesTrayHydrocarbons());
	}

    public HidrocarburosWsService() throws MalformedURLException {
        super(getServiceUrl(), HIDROCARBUROSWSSERVICE_QNAME);
    }

    public HidrocarburosWsService(WebServiceFeature... features) {
        super(__getWsdlLocation(), HIDROCARBUROSWSSERVICE_QNAME, features);
    }

    public HidrocarburosWsService(URL wsdlLocation) {
        super(wsdlLocation, HIDROCARBUROSWSSERVICE_QNAME);
    }

    public HidrocarburosWsService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, HIDROCARBUROSWSSERVICE_QNAME, features);
    }

    public HidrocarburosWsService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public HidrocarburosWsService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns HidrocarburosWs
     */
    @WebEndpoint(name = "HidrocarburosWsPort")
    public HidrocarburosWs getHidrocarburosWsPort() {
        return super.getPort(new QName("http://service.hyd.ambiente.gob.ec/", "HidrocarburosWsPort"), HidrocarburosWs.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns HidrocarburosWs
     */
    @WebEndpoint(name = "HidrocarburosWsPort")
    public HidrocarburosWs getHidrocarburosWsPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://service.hyd.ambiente.gob.ec/", "HidrocarburosWsPort"), HidrocarburosWs.class, features);
    }

    private static URL __getWsdlLocation() {
        if (HIDROCARBUROSWSSERVICE_EXCEPTION!= null) {
            throw HIDROCARBUROSWSSERVICE_EXCEPTION;
        }
        return HIDROCARBUROSWSSERVICE_WSDL_LOCATION;
    }

}
