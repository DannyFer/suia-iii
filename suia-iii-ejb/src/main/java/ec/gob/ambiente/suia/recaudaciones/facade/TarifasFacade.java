package ec.gob.ambiente.suia.recaudaciones.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.recaudaciones.model.Cuentas;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;

@Stateless
public class TarifasFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardarTarifas(Tarifas tarifas) throws ServiceException {
		crudServiceBean.saveOrUpdate(tarifas);
	}
	
	@SuppressWarnings("unchecked")
	public List<Tarifas> listTarifasAll() throws ServiceException{
		List<Tarifas>listTarifas= new ArrayList<Tarifas>();
		listTarifas=(List<Tarifas>) crudServiceBean.findAll(Tarifas.class);
		if(listTarifas.size()>0)
			return listTarifas;
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Tarifas> buscarTarifasPorCuenta(Cuentas cuentas) throws ServiceException {
		List<Tarifas> tarifas = new ArrayList<Tarifas>();

		Query query = crudServiceBean.getEntityManager().createQuery(
							"FROM Tarifas c WHERE c.cuentas=:cuentas) ORDER BY c.id");
			query.setParameter("cuentas", cuentas);
			tarifas = query.getResultList();
			if(tarifas.size()>0)
				return tarifas;
			return null;
	}
	
	@SuppressWarnings("unchecked")
	public Tarifas buscarTarifasPorId(Integer id) throws ServiceException {
		List<Tarifas> tarifas = new ArrayList<Tarifas>();

		Query query = crudServiceBean.getEntityManager().createQuery(
							"FROM Tarifas c WHERE c.id=:id) ORDER BY c.id");
			query.setParameter("id", id);
			tarifas = query.getResultList();
			if(tarifas.size()>0)
				return tarifas.get(0);
			return null;
	}
	
	@SuppressWarnings("unchecked")
	public Tarifas buscarTarifasPorCodigo(String codigo) throws ServiceException {
		List<Tarifas> tarifas = new ArrayList<Tarifas>();

		Query query = crudServiceBean.getEntityManager().createQuery(
							"FROM Tarifas c WHERE c.tasasCodigo=:codigo ORDER BY c.id");
			query.setParameter("codigo", codigo);
			tarifas = query.getResultList();
			if(tarifas.size()>0)
				return tarifas.get(0);
			return null;
	}
}