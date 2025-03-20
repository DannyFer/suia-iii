package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.NivelMagnitud;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class NivelMagnitudFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<NivelMagnitud> listarNivel() {
		List<NivelMagnitud> lista = new ArrayList<NivelMagnitud>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select n from NivelMagnitud n where n.estado=true");
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
    }	

}
