package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.dto.EntityFichaCompletaRgd;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

@Stateless
public class ProyectosLicenciamientoCoaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
   	private EjecutarSentenciasNativas ejecutarSentenciasNativas;
	
	public ProyectoLicenciaCoa findById(Integer id){
		try {
			ProyectoLicenciaCoa proyecto = (ProyectoLicenciaCoa) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM ProyectoLicenciaCoa o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			
			return proyecto;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	 public List<EntityFichaCompletaRgd> getFinalizadosPorProponente(String cedulaProponente) {
	   		try {
	   			Map<String, Object> parametros = new HashMap<String, Object>();
	   			String like = "";
	   			like = " LOWER(u.user_name) like LOWER(:cedulaProponente)";
	   			parametros.put("cedulaProponente", "%" + cedulaProponente + "%");
	   			
	   			StringBuilder sb = new StringBuilder();
	   			sb.append("select CAST(p.prco_id AS varchar(255)), area_abbreviation, prco_cua, prco_name, CAST(prco_project_completion_date AS varchar(255)), "
	   					+ "sety_name, CAST(1 as varchar) AS sistema, c.cate_public_name "
	   					+ "from coa_mae.project_licencing_coa p "
	   					+ "join areas a on a.area_id = p.area_id  "
						+ "join users u on u.user_id = p.user_id "
						+ "join coa_mae.project_licencing_coa_ciuu pr on pr.prco_id = p.prco_id "
						+ "join coa_mae.catalog_ciuu cc on cc.caci_id = pr.caci_id "
						+ "join suia_iii.sector_types s on s.sety_id = cc.sety_id "
						+ "join suia_iii.categories c on c.cate_id = p.prco_categorizacion "
	   					+ "where prco_project_finished = true and p.prco_categorizacion not in (1)  and prli_primary = true and "+like)					
	   					.append(" ORDER BY prco_cua DESC ");
	   			return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityFichaCompletaRgd.class, parametros);

	   		} catch (Exception e) {
	   			e.printStackTrace();
	   			return null;
	   		}
	   	}  
	
	

}
