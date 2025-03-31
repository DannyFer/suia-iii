package ec.gob.ambiente.suia.recaudaciones.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.recaudaciones.model.Cuentas;

@Stateless
public class CuentaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public Cuentas buscarPorCodigo(String codigo) throws ServiceException {
		List<Cuentas> listaCuentas = new ArrayList<Cuentas>();

		Query query = crudServiceBean
				.getEntityManager()
				.createQuery("FROM Cuentas c WHERE c.codigoDesarrollo=:codigo ORDER BY c.id");
		
		query.setParameter("codigo", codigo);
		
		listaCuentas = query.getResultList();
		if (listaCuentas.size() > 0)
			return listaCuentas.get(0);
		return null;
	}
}