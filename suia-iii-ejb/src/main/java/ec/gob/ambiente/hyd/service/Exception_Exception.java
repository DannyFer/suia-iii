
package ec.gob.ambiente.hyd.service;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "Exception", targetNamespace = "http://service.hyd.ambiente.gob.ec/")
public class Exception_Exception
    extends java.lang.Exception
{

    /**
	 *
	 */
	private static final long serialVersionUID = 265390826342802391L;
	/**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private ec.gob.ambiente.hyd.service.Exception faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public Exception_Exception(String message, ec.gob.ambiente.hyd.service.Exception faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public Exception_Exception(String message, ec.gob.ambiente.hyd.service.Exception faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: ec.gob.ambiente.hyd.service.Exception
     */
    public ec.gob.ambiente.hyd.service.Exception getFaultInfo() {
        return faultInfo;
    }

}
