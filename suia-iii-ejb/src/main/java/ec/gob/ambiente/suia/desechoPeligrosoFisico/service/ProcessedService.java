package ec.gob.ambiente.suia.desechoPeligrosoFisico.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.desechoPeligrosoFisico.dao.ProcessedFacade;
import ec.gob.ambiente.suia.domain.desechosfisicos.Processed;

/**
 * 
 * @author karla.carvajal
 *
 */
@Stateless
public class ProcessedService {

	@EJB
	private ProcessedFacade processedFacade;
	

	public List<Processed> getProcessedByCompanyTypeInstallation(int type,  String search)
	{
		return processedFacade.getProcessedByCompanyTypeInstallation(type, search);
	}
	
}