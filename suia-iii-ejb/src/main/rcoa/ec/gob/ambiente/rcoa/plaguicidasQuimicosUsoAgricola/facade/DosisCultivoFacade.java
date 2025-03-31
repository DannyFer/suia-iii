package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DosisCultivo;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class DosisCultivoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public DosisCultivo guardar(DosisCultivo dosis) {
		return crudServiceBean.saveOrUpdate(dosis);
	}

}
