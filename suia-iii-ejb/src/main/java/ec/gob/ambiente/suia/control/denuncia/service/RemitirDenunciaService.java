package ec.gob.ambiente.suia.control.denuncia.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.RemitirDenuncia;

@Stateless
public class RemitirDenunciaService {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(RemitirDenuncia remitirDenuncia) {
		if (remitirDenuncia.getId() == null)
			crudServiceBean.saveOrUpdate(remitirDenuncia);
	}

}
