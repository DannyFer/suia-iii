package ec.gob.ambiente.suia.eia.ficha.facade;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.Consultor;
import ec.gob.ambiente.suia.eia.ficha.service.ConsultorCalificadoService;
import ec.gob.ambiente.suia.exceptions.ServiceException;
/**
 * 
 * @author lili
 *
 */

@Stateless
public class ConsultorCalificadoFacade implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1707890166490552505L;

	@EJB
	private ConsultorCalificadoService consultorCalificadoService;
	
	public List<Consultor> consultoresCalificados() throws ServiceException{
		return consultorCalificadoService.consultoresCalificados();
	}
	
	public List<Consultor> consultoresCalificadosVigentes() throws ServiceException{
		return consultorCalificadoService.consultoresCalificadosVigentes();
	}
	
	public List<Consultor> consultoresUnicoCalificados(Integer id) throws ServiceException{
		return consultorCalificadoService.consultorUnicoCalificado(id);
	}
	
	public Consultor buscarConsultor(Integer id) throws ServiceException{
		return consultorCalificadoService.buscarConsultor(id);
	}
	
	public Consultor buscarConsultorPorRuc(String ruc) throws ServiceException{
		return consultorCalificadoService.buscarConsultorPorRuc(ruc);
	}
}
