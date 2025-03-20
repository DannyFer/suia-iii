/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.prevencion.categoria2.facade;

import ec.gob.ambiente.suia.dto.EntityDetalleCronograma;
import ec.gob.ambiente.suia.dto.EntityDetalleCronogramaValorado;
import ec.gob.ambiente.suia.dto.EntityPlanManejoAmbiental;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author ishmael
 */
@Stateless
public class ImpresionFichaGeneralFacade {

    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;

    public List<EntityDetalleCronogramaValorado> obtenerDetalleCronogramaValoradoPOrFicha(Integer idFicha) throws ServiceException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT p.empt_type, s.cava_activity, s.cava_responsible, s.cava_start_date, s.cava_end_date, s.cava_budget,s.cava_justify, concat(s.cava_frequency, fu.freun_name) as frequency ,   case when s.cava_enter_information is true then 'SI'"
        		+ "  when s.cava_enter_information is FALSE then 'NO' when s.cava_enter_information is NULL then '' end as cava_enter_information,s.cava_observation");
        sb.append(" FROM suia_iii.catii_valued_schedule s");
        sb.append(" INNER JOIN suia_iii.environmental_management_plan_types p ON s.empt_id = p.empt_id");
        sb.append(" LEFT JOIN frecuency_units fu ON fu.freun_id = s.freun_id");
        sb.append(" WHERE s.cava_status = TRUE AND s.cava_original_record_id is null AND s.cafa_id =").append(idFicha).append(" ORDER BY p.empt_type");
        return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityDetalleCronogramaValorado.class, null);
    }
    
   public List<EntityDetalleCronograma> obtenerDetalleCronogramaPOrFicha(Integer idFicha) throws ServiceException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT f.phas_name, a.caac_description, a.caac_start_date, a.caac_end_date FROM suia_iii.catii_activities_schedule s, suia_iii.catii_activities a, suia_iii.sectors_classifications_phases c,");
        sb.append(" suia_iii.phases f WHERE f.phas_id = c.phas_id AND c.secp_id = s.secp_id AND s.caas_id = a.caas_id AND s.caas_status = TRUE AND a.caac_status = TRUE");
        sb.append(" AND s.cafa_id =").append(idFicha).append(" ORDER BY f.phas_name");
        return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityDetalleCronograma.class, null);
    }
    
    public List<EntityPlanManejoAmbiental> obtenerPLanManejoAmbientalPorFicha(Integer idFicha) throws ServiceException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT p.empt_type, prde_start_date, prde_end_date FROM suia_iii.plan_record_details pd, suia_iii.plan_sectors ps, suia_iii.environmental_management_plan_types p");
        sb.append(" WHERE pd.plse_id = ps.plse_id AND ps.empt_id = p.empt_id AND pd.prde_status = TRUE");
        sb.append(" AND pd.cafa_id =").append(idFicha).append(" ORDER BY p.empt_type");
        return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityPlanManejoAmbiental.class, null);
    }
    
    public List<EntityDetalleCronogramaValorado> obtenerDetalleCronogramaValoradoPOrFichaMineria(Integer idFicha) throws ServiceException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT p.empt_type, s.cava_activity, s.cava_responsible, s.cava_start_date, s.cava_end_date, s.cava_budget,s.cava_justify,  (s.cava_frequency || ' ' || fu.freun_name) as frequency , case when s.cava_enter_information is true then 'SI'"
        		+ "  when s.cava_enter_information is FALSE then 'NO' when s.cava_enter_information is NULL then '' end as cava_enter_information,s.cava_observation");
        sb.append(" FROM suia_iii.catii_valued_schedule s ");
        sb.append(" INNER JOIN suia_iii.environmental_management_plan_types p ON s.empt_id = p.empt_id");
        sb.append(" LEFT JOIN frecuency_units fu ON fu.freun_id = s.freun_id");
        sb.append(" WHERE s.cava_status = TRUE AND s.cava_original_record_id is null AND s.mien_id =").append(idFicha).append(" ORDER BY p.empt_type");
        return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityDetalleCronogramaValorado.class, null);
    }
    

}
