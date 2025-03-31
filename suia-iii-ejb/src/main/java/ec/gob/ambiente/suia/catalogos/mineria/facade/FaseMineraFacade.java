package ec.gob.ambiente.suia.catalogos.mineria.facade;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.catalogos.mineria.service.FaseMineraService;
import ec.gob.ambiente.suia.domain.FaseMinera;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class FaseMineraFacade implements Serializable{
	
	private static final long serialVersionUID = -4741848248071271982L;
	
	@EJB
	private FaseMineraService faseMineraService;
	
	
	public List<FaseMinera> listarTodasFasesMineras() throws ServiceException {
		return faseMineraService.listarTodasFasesMineras();
	}
	
}
