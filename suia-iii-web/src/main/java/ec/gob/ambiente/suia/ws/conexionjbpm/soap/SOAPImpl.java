package ec.gob.ambiente.suia.ws.conexionjbpm.soap;

import java.util.Map;
import javax.ejb.EJB;
import javax.jws.WebService;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.ProcessBeanFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@WebService(endpointInterface = "ec.gob.ambiente.suia.ws.conexionjbpm.soap.SOAPI")
public class SOAPImpl implements SOAPI {

	@EJB(lookup = Constantes.JBPM_EJB_PROCESS_BEAN)
	private ProcessBeanFacade processBeanFacade;
	
	@Override
	public long invocarInstanciaJBPM(String processName, Map<String, Object> params, String deploymentId, String usuario,
		String password, String urlBusinessCentral, int timeout) {
		
		Long salida=0L;
		try {
			salida= processBeanFacade.startProcess(processName, params, deploymentId, usuario,
					password, urlBusinessCentral, timeout);

		}catch (Exception e) {
			e.printStackTrace();
			salida=-1L;
		}
		
		return salida;
	}
	
	@Override
	public void abortarInstanciaJBPM(Long processInstanceId, String deploymentId, String usuario, String password,
		String urlBusinessCentral, int timeout)  {
		
		try {
			processBeanFacade.abortProcess(processInstanceId, deploymentId, usuario, password, urlBusinessCentral, timeout);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}