package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.CatalogoCapaPermiso;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;


@Stateless
public class CatalogoCapaPermisoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<CatalogoCapaPermiso> listaCapasPorCiiu(Integer idCiiu) {

		List<CatalogoCapaPermiso> lista = new ArrayList<CatalogoCapaPermiso>();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idCiiu", idCiiu);
			
		lista = (ArrayList<CatalogoCapaPermiso>) crudServiceBean
				.findByNamedQuery(CatalogoCapaPermiso.GET_POR_CIIU, parameters);
			
		return lista;
	}

	@SuppressWarnings("unchecked")
	public CatalogoCapaPermiso getCapaPermisoPorProyectoCiiu(Integer idProyecto, Integer idCiiu) {

		List<CatalogoCapaPermiso> lista = new ArrayList<CatalogoCapaPermiso>();
		Query sql = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select c From CatalogoCapaPermiso c, InterseccionProyectoLicenciaAmbiental i "
					  + " where c.capa.id = i.capa.id and i.proyectoLicenciaCoa.id =:idProyecto and c.catalogoCIUU.id =:idCiiu "
					  + " and c.estado = true and i.estado = true order by c.jerarquiaCapa asc, c.id asc");

		sql.setParameter("idProyecto", idProyecto);
		sql.setParameter("idCiiu", idCiiu);
		
		lista = (List<CatalogoCapaPermiso>) sql.getResultList();

		if(lista != null && lista.size() > 0) {
			return lista.get(0);
		}
		
		return null;
	}

}