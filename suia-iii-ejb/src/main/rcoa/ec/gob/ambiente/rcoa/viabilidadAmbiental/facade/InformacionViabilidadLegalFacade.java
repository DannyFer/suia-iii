package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformacionViabilidadLegal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class InformacionViabilidadLegalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public InformacionViabilidadLegal getInformacionViabilidadLegalPorId(Integer idViabilidad) {
		Query sql = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select i from InformacionViabilidadLegal i where i.viabilidadCoa.id=:idViabilidad and i.estado=true order by i.id desc");
		sql.setParameter("idViabilidad", idViabilidad);
		if (sql.getResultList().size() > 0) {
			List<InformacionViabilidadLegal> conflictosLegales = (List<InformacionViabilidadLegal>) sql.getResultList();
			return conflictosLegales.get(0);
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<InformacionViabilidadLegal> getListaConflictosPorIdViabilidad(Integer idViabilidad) {
		Query sql = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select i from InformacionViabilidadLegal i where i.viabilidadCoa.id=:idViabilidad and i.estado=true order by i.id desc");
		sql.setParameter("idViabilidad", idViabilidad);
		if (sql.getResultList().size() > 0) {
			return (List<InformacionViabilidadLegal>) sql.getResultList();
		}

		return null;
	}

	public InformacionViabilidadLegal guardar(InformacionViabilidadLegal informacionViabilidadLegal) {
		return crudServiceBean.saveOrUpdate(informacionViabilidadLegal);
	}
}
