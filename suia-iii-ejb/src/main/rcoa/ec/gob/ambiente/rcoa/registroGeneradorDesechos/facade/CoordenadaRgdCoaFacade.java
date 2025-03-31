package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CoordenadaRgdCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CoordenadaRgdCoaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public CoordenadaRgdCoa findById(Integer id) {
		try {
			return (CoordenadaRgdCoa) crudServiceBean.getEntityManager()
					.createQuery("SELECT c FROM CoordenadaRgdCoa c where c.id = :id").setParameter("id", id)
					.getSingleResult();

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void save(CoordenadaRgdCoa obj, Usuario usuario) {
		if (obj.getId() == null) {
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
		} else {
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());
		}
		crudServiceBean.saveOrUpdate(obj);
	}

	@SuppressWarnings("unchecked")
	public List<CoordenadaRgdCoa> buscarPorForma(Integer id) {
		List<CoordenadaRgdCoa> lista = new ArrayList<CoordenadaRgdCoa>();

		try {
			lista = (List<CoordenadaRgdCoa>) crudServiceBean.getEntityManager().createQuery(
					"SELECT c FROM CoordenadaRgdCoa c where c.formasPuntoRecuperacionRgdRcoa.id = :id and c.estado = true order by c.orden ASC")
					.setParameter("id", id).getResultList();
			return lista;

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}

}
