package ec.gob.ambiente.suia.prevencion.participacionsocial.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.dto.EntityProyectoParticipacionSocial;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

@Stateless
public class PublicarEstudioParticipacionSocialService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private EjecutarSentenciasNativas ejecutarSentenciasNativas;
	
	@SuppressWarnings("unchecked")
	public List<ParticipacionSocialAmbiental> getProyectosParticipacionSocial() {

		Map<String, Object> params = new HashMap<String, Object>();

			List<ParticipacionSocialAmbiental> result = (List<ParticipacionSocialAmbiental>)crudServiceBean.findByNamedQuery(ParticipacionSocialAmbiental.GET_ALL, params);

			for (ParticipacionSocialAmbiental part: result){
				part.getProyectoLicenciamientoAmbiental().getId();
			}
			/*List<ParticipacionSocialAmbiental> projects = new ArrayList<ParticipacionSocialAmbiental>();
			for (Object object : result) {
				projects.add((ParticipacionSocialAmbiental) object);
			}*/

			//return projects;
		return result;

	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public List<ProyectoCustom> getProyectosPublicarEstudio(/*Usuario usuario*/) {
	

		Map<String, Object> params = new HashMap<String, Object>();
		//params.put("userId", usuario.getId());

		String queryString0 = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE p.user_id = :userId AND p.pren_status = true AND p.pren_project_finalized = true AND p.area_id = a.area_id AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id ORDER BY p.pren_code";

		String queryString="SELECT  p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name"
				+ " FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a,"
				+ " suia_iii.environmental_social_participation pps"
				+ " WHERE  p.pren_status = true AND p.pren_project_finalized = true AND p.area_id = a.area_id AND"
				+ " cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id AND pps.pren_id=p.pren_id"
				+ " ORDER BY p.pren_code";




		List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
		for (Object object : result) {
			projects.add(new ProyectoCustom((Object[]) object));
		}

		return projects;
		
	}

	@SuppressWarnings("unchecked")
	public List<EntityProyectoParticipacionSocial> getProyectosPPSocial(){
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT CAST(e.ensp_id AS varchar(255)), p.pren_id, p.pren_code, p.pren_name, s.sety_name, to_char(p.pren_register_date, 'DD-MM-YYYY') ");
			sb.append(" FROM suia_iii.environmental_social_participation e, suia_iii.projects_environmental_licensing p, suia_iii.sector_types s");
			sb.append(
					" WHERE e.pren_id = p.pren_id and p.sety_id = s.sety_id and e.ensp_status = 'TRUE' and e.ensp_published='TRUE'  and p.pren_status = 'TRUE' ")
					.append(" ORDER BY e.ensp_id DESC ");
			return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityProyectoParticipacionSocial.class, null);

		} catch (Exception e) {
			return null;
		}
	}
	
	
	

}
