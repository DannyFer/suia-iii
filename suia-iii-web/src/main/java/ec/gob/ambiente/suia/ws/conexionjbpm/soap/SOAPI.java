/**
 * SOAPI.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ec.gob.ambiente.suia.ws.conexionjbpm.soap;

import java.util.Map;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface SOAPI {
     
	@WebMethod
	public long invocarInstanciaJBPM(String processName, Map<String, Object> params, String deploymentId, String usuario,
		String password, String urlBusinessCentral, int timeout);
 
	 
	@WebMethod
	public void abortarInstanciaJBPM(Long processId, String deploymentId, String usuario, String password, 
		String urlBusinessCentral, int timeout);
}