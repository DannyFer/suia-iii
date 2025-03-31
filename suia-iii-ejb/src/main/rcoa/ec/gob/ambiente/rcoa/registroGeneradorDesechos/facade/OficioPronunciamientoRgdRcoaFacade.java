package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.OficioPronunciamientoRgdRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class OficioPronunciamientoRgdRcoaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public OficioPronunciamientoRgdRcoa findById(Integer id) {
		try {
			return (OficioPronunciamientoRgdRcoa) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM OficioPronunciamientoRgdRcoa o WHERE o.id = :id").setParameter("id", id)
					.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<OficioPronunciamientoRgdRcoa> findByGeneradorDesechos(Integer id) {
		List<OficioPronunciamientoRgdRcoa> resultado = new ArrayList<OficioPronunciamientoRgdRcoa>();

		try {
			resultado = (List<OficioPronunciamientoRgdRcoa>) crudServiceBean.getEntityManager().createQuery(
					"SELECT o FROM OficioPronunciamientoRgdRcoa o where o.registroGeneradorDesechosRcoa.id = :id and o.estado = true order by id")
					.setParameter("id", id).getResultList();

			return resultado;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultado;
	}

	public void save(OficioPronunciamientoRgdRcoa obj, Usuario usuario) {
		if (obj.getId() == null) {
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
		} else {
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());
		}
		crudServiceBean.saveOrUpdate(obj);
	}

}
