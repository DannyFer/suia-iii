package ec.gob.ambiente.rcoa.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.VinculoProyectoRgdSuia;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class VinculoProyectoRgdSuiaFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(VinculoProyectoRgdSuia asociado) {        
        	crudServiceBean.saveOrUpdate(asociado);        
    }

    public VinculoProyectoRgdSuia getProyectoVinculadoRgd (Integer idProyectoRcoa) {
    	VinculoProyectoRgdSuia obj = null;

		Query sql = crudServiceBean.getEntityManager().createQuery(
						"select v from VinculoProyectoRgdSuia v where v.idProyectoRcoa=:idProyectoRcoa and v.estado=true");
		sql.setParameter("idProyectoRcoa", idProyectoRcoa);
		
		if (sql.getResultList().size() > 0) {
			obj = (VinculoProyectoRgdSuia) sql.getSingleResult();
		}

		return obj;
	}
}
