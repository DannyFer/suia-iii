package ec.gob.ambiente.suia.tipoforma.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoForma;

@Stateless
public class TipoFormaService {
	@EJB
	CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<TipoForma> listasTipoFormas() {
		return (List<TipoForma>) crudServiceBean.findAll(TipoForma.class);
	}
}
