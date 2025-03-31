package ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PlanManejoAmbiental020;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalProyecto;

@LocalBean
@Stateless
public class PlanManejoAmbiental020Facade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(PlanManejoAmbientalProyecto plan){
		crudServiceBean.saveOrUpdate(plan);
	}
	
	public void guardarLista(List<PlanManejoAmbientalProyecto> plan){
		crudServiceBean.saveOrUpdate(plan);
	}
	
	public List<PlanManejoAmbiental020> buscarPMAPadres020() {
		try {
			//select * from suia_iii.scdr_environmental_management where  enma_description <>'' and enma_parent_id is null  order by 1
			List<PlanManejoAmbiental020> planManejoAmbiental020s = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT pl FROM PlanManejoAmbiental020 pl where pl.descripcion <>'' and pl.planManejoAmbiental020.id is null and pl.estado=true order by 1  ").getResultList();
			return planManejoAmbiental020s;
		} catch (Exception e) {
			return null;
			// TODO: handle exception
		}
	}
	
	public PlanManejoAmbiental020 buscarPMAPadres020PorNombre(String nombre) {
		try {
			//select * from suia_iii.scdr_environmental_management where  enma_description <>'' and enma_parent_id is null  order by 1
			List<PlanManejoAmbiental020> planManejoAmbiental020s = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT pl FROM PlanManejoAmbiental020 pl where pl.descripcion =:nombrePlan and pl.planManejoAmbiental020.id is null and pl.estado=true  ")
							.setParameter("nombrePlan", nombre)
							.getResultList();
			if(planManejoAmbiental020s.size() > 0){
				return planManejoAmbiental020s.get(0);
			}
			return null;
		} catch (Exception e) {
			return null;
			// TODO: handle exception
		}
	}
//	
	public List<PlanManejoAmbiental020> buscarPMA020(List<PlanManejoAmbiental020> planPadre020) {
		try {
			List<PlanManejoAmbiental020> planManejoAmbiental020s = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT pl FROM PlanManejoAmbiental020 pl where pl.planManejoAmbiental020 in( :plan ) and pl.estado=true order by 1")
					.setParameter("plan", planPadre020).getResultList();
			return planManejoAmbiental020s;
		} catch (Exception e) {
			return null;
			// TODO: handle exception
		}

	}
	
	public List<PlanManejoAmbiental020> buscarPMA020Hijos(Integer planManejoId) {
		try {
			List<PlanManejoAmbiental020> planManejoAmbiental020s = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT pl FROM PlanManejoAmbiental020 pl where pl.planManejoAmbiental020.id =:planId and pl.estado=true order by pl.orden")
					.setParameter("planId", planManejoId).getResultList();
			return planManejoAmbiental020s;
		} catch (Exception e) {
			return null;
			// TODO: handle exception
		}
	}
	

	public PlanManejoAmbientalProyecto buscarPlanProyectoPorId(Integer planManejoId, Integer scdrId) {
		try {
			List<PlanManejoAmbientalProyecto> planManejoAmbiental020s = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT pl FROM PlanManejoAmbientalProyecto pl where pl.planManejoAmbiental020.id =:planId and pl.perforacionExplorativa = :scdrId and pl.estado=true order by 1")
					.setParameter("planId", planManejoId)
					.setParameter("scdrId", scdrId).getResultList();
			if(planManejoAmbiental020s.size() > 0){
				return planManejoAmbiental020s.get(0);
			}
			return null;
		} catch (Exception e) {
			return null;
			// TODO: handle exception
		}
	}
	


	public List<PlanManejoAmbientalProyecto> buscarPlanProyectoPorIdOtros(Integer scdrId, Integer planPadreId) {
		try {
			List<PlanManejoAmbientalProyecto> planManejoAmbiental020s = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT pl FROM PlanManejoAmbientalProyecto pl where pl.planManejoAmbiental020 is null  and pl.perforacionExplorativa = :scdrId and pl.padreId = :planPadreId and pl.estado=true order by 1")
					.setParameter("planPadreId", planPadreId)
					.setParameter("scdrId", scdrId).getResultList();
			
			return planManejoAmbiental020s;
		} catch (Exception e) {
			return null;
			// TODO: handle exception
		}
	}
	

	public Map<String, List<PlanManejoAmbientalProyecto>> obtenerActividdaesProyecto(Integer proyectoId)  {
		String espacios="                  ";
		Map<String, List<PlanManejoAmbientalProyecto>> parameters = new HashMap<String, List<PlanManejoAmbientalProyecto>>();
		List<PlanManejoAmbiental020> listaPadre =  buscarPMAPadres020();
		List<PlanManejoAmbiental020> listaHijos = new ArrayList<PlanManejoAmbiental020>();
		List<PlanManejoAmbientalProyecto> listaPlanProyecto = new ArrayList<PlanManejoAmbientalProyecto>();
		for(PlanManejoAmbiental020 objLista : listaPadre){
			listaPlanProyecto = new ArrayList<PlanManejoAmbientalProyecto>();
//			listaHijos = objLista.getPlanManejoList(); /// uscarPMA020Hijos(objLista.getId());
			listaHijos = buscarPMA020Hijos(objLista.getId());
			for(PlanManejoAmbiental020 objListaHijo : listaHijos){
				PlanManejoAmbientalProyecto objPlan = new PlanManejoAmbientalProyecto();
				objPlan = buscarPlanProyectoPorId(objListaHijo.getId(), proyectoId);
				if(objPlan == null){
					objPlan = new PlanManejoAmbientalProyecto();
					objPlan.setPlanManejoAmbiental020(objListaHijo);
					objPlan.setEstado(true);
					objPlan.setPerforacionExplorativa(proyectoId);
					objPlan.setPadreId(objListaHijo.getPlanManejoAmbiental020().getId());
				}
				listaPlanProyecto.add(objPlan);
			}
			List<PlanManejoAmbientalProyecto> listaHijosOtros = buscarPlanProyectoPorIdOtros(proyectoId, objLista.getId());

			for(PlanManejoAmbientalProyecto objListaHijo : listaHijosOtros){
				listaPlanProyecto.add(objListaHijo);
			}
			espacios = espacios.substring(1, espacios.length());
			parameters.put(espacios+objLista.getDescripcion(), listaPlanProyecto);
		}
		Map<String, List<PlanManejoAmbientalProyecto>> parameters1 = new TreeMap<String, List<PlanManejoAmbientalProyecto>>(parameters);

		return parameters1;
	}
	
	
	
}
