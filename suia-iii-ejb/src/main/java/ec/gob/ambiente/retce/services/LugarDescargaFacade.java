package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.LugarDescarga;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class LugarDescargaFacade {
	@EJB
	private CrudServiceBean crudServiceBean;
	/**
	 * listar los lugares de descarga
	 * @param parent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LugarDescarga> findAll(){	
		List<LugarDescarga> lugarDescargaList=new ArrayList<LugarDescarga>();
		try{
			lugarDescargaList = (ArrayList<LugarDescarga>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM LugarDescarga o where o.estado=true order by o.orden")
					.getResultList();
			return lugarDescargaList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lugarDescargaList;
	}

}
