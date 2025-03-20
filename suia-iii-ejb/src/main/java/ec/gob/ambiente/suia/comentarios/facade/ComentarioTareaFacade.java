package ec.gob.ambiente.suia.comentarios.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ComentarioTarea;

@Stateless
public class ComentarioTareaFacade {

	@EJB
	CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<ComentarioTarea> getComentarioTarea(
			ComentarioTarea comentarioTarea) {
		String sql = "SELECT c from ComentarioTarea c where c.idDocumento=:idDocumento and c.tipoDocumento=:tipoDocumento ORDER BY c.fecha";
		List<ComentarioTarea> resultList;
		if (comentarioTarea.getIdDocumento() != null
				&& comentarioTarea.getIdDocumento() != 0)
			resultList = crudServiceBean
					.getEntityManager()
					.createQuery(sql)
					.setParameter("idDocumento",
							comentarioTarea.getIdDocumento())
					.setParameter("tipoDocumento",
							comentarioTarea.getTipoDocumento()).getResultList();
		else
			resultList = new ArrayList<ComentarioTarea>();
		return resultList;

	}

	public void guaradar(ComentarioTarea comentarioTarea) {
		if (comentarioTarea.getId() == null) {
			crudServiceBean.saveOrUpdate(comentarioTarea);
			comentarioTarea.setFecha(new Date());
		} else
			crudServiceBean.saveOrUpdate(comentarioTarea);
	}
}
