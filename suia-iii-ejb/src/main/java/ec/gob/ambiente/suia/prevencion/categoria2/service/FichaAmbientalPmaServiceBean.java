package ec.gob.ambiente.suia.prevencion.categoria2.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.dto.EntityFichaCompleta;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

@Stateless
public class FichaAmbientalPmaServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private EjecutarSentenciasNativas ejecutarSentenciasNativas;

	public FichaAmbientalPma guardarFichaAmbientalPma(FichaAmbientalPma fichaAmbientalPma) {
		return crudServiceBean.saveOrUpdate(fichaAmbientalPma);
	}

	@SuppressWarnings("unchecked")
	public FichaAmbientalPma getFichaAmbientalPorCodigoProyecto(String codigo) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("codigo", codigo);
		try {
			List<FichaAmbientalPma> result = (List<FichaAmbientalPma>) crudServiceBean.findByNamedQuery(
					FichaAmbientalPma.GET_FICHA_PMA_PROYECTO, parameters);

			FichaAmbientalPma fichaAmbientalPma = new FichaAmbientalPma();

			if (result != null && !result.isEmpty()) {
				fichaAmbientalPma = result.get(0);
			}
			return fichaAmbientalPma;
		} catch (Exception e) {
			return null;
		}
	}

	public FichaAmbientalPma getFichaAmbientalPorIdProyecto(Integer id) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("p_id", id);
		try {
			List<FichaAmbientalPma> result = crudServiceBean.findByNamedQueryGeneric(
					FichaAmbientalPma.GET_FICHA_PMA_PROYECTOID, parameters);

			FichaAmbientalPma fichaAmbientalPma = null;

			if (result != null && !result.isEmpty()) {
				fichaAmbientalPma = result.get(0);
			}
			return fichaAmbientalPma;
		} catch (Exception e) {
			return null;
		}
	}

	public List<EntityFichaCompleta> getFichasAmbientalPorProyecto() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT CAST(p.pren_id AS varchar(255)), a.area_name, p.pren_code, p.pren_resume, p.pren_name, c.cacs_description,s.secl_name, to_char(f.cafa_date_update, 'DD-MM-YYYY') ");
			sb.append(" FROM suia_iii.projects_environmental_licensing p, areas a, suia_iii.catii_fapma f, suia_iii.categories_catalog_system c, suia_iii.categories ct, sectors_classification s ");
			sb.append(
					" WHERE p.pren_id = f.pren_id AND p.area_id = a.area_id AND f.cafa_license_number IS NOT NULL AND f.cafa_status = true AND p.cacs_id = c.cacs_id AND c.cate_id=ct.cate_id AND ct.cate_id=2 and c.secl_id = s.secl_id and pren_status = 'TRUE'")
					.append(" ORDER BY p.pren_code DESC");
			return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityFichaCompleta.class, null);

		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Cris F;
	 * Obtener fichas historicas
	 */
	
	public List<FichaAmbientalPma> getFichaAmbientalPorIdProyectoHistorico(Integer id) {		
		try {			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT f FROM FichaAmbientalPma f WHERE "
					+ "f.proyectoLicenciamientoAmbiental.id = :id and f.idRegistroOriginal != null order by 1 desc");
			query.setParameter("id", id);
			
			List<FichaAmbientalPma> result = query.getResultList();

			if (result != null && !result.isEmpty()) {
				return result;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

}