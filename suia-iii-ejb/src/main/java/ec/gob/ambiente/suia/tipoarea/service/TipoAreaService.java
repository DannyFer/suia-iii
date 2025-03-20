package ec.gob.ambiente.suia.tipoarea.service;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoArea;

/**
 * 
 * @author frank torres rodríguez
 */
@LocalBean
@Stateless
public class TipoAreaService {

	@EJB
	private CrudServiceBean crudServiceBean;

	public TipoArea getTipoAreaPorSiglas(String siglas) {
		return (TipoArea) crudServiceBean.getEntityManager()
				.createQuery(" FROM TipoArea t where t.siglas =:siglas ")
				.setParameter("siglas", siglas).getSingleResult();

	}

}
