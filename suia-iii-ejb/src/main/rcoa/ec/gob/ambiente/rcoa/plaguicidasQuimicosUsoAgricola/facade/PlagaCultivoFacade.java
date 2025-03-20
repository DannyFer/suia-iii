package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.PlagaCultivo;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class PlagaCultivoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public PlagaCultivo guardar(PlagaCultivo plaga) {
		return crudServiceBean.saveOrUpdate(plaga);
	}

}
