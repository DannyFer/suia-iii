/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.eia.analisisAlternativas.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 26/06/2015]
 *          </p>
 */

@Stateless
public class AnalisisAlternativasFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	

	public void guardar(EstudioImpactoAmbiental estudio, List<ActividadImplantacion> actividades,
			List<Alternativa> alternativas, List<ActividadImplantacion> saved, Documento documento) {

		List<ActividadImplantacion> actividadesNoVerificadas = new ArrayList<ActividadImplantacion>();
		List<Alternativa> alternativasNoVerificadas = new ArrayList<Alternativa>();
		if (saved != null && !saved.isEmpty()) {
			actividadesNoVerificadas.addAll(saved);
			for (ActividadImplantacion actividadImplantacion : saved) {
				alternativasNoVerificadas.addAll(actividadImplantacion.getAlternativas());
			}
		}
		//HIDROCARBURO
		if(documento!=null){
			crudServiceBean.saveOrUpdate(documento);

		}

		for (ActividadImplantacion actividadImplantacion : actividades) {
			actividadImplantacion.setEstudioImpactoAmbiental(estudio);
			actividadImplantacion.setFechaCreacion(new Date());
			crudServiceBean.saveOrUpdate(actividadImplantacion);
			actividadesNoVerificadas.remove(actividadImplantacion);
		}

		for (Alternativa alternativa : alternativas) {
			alternativa.setMejorOpcion(false);
			alternativa.setFechaCreacion(new Date());
			for (ActividadImplantacion actividadImplantacion : actividades) {
				if (alternativa.equals(actividadImplantacion.getAlternativaMejorOpcion()))
					alternativa.setMejorOpcion(true);
			}
			crudServiceBean.saveOrUpdate(alternativa);
			alternativasNoVerificadas.remove(alternativa);
		}


		crudServiceBean.delete(actividadesNoVerificadas);
		crudServiceBean.delete(alternativasNoVerificadas);
	}



	@SuppressWarnings("unchecked")
	public List<ActividadImplantacion> getActividadesImplantacion(EstudioImpactoAmbiental estudio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", estudio.getId());
		List<ActividadImplantacion> actividades = (List<ActividadImplantacion>) crudServiceBean.findByNamedQuery(
				ActividadImplantacion.GET_BY_ESTUDIO, parameters);
		for (ActividadImplantacion actividadImplantacion : actividades) {
			actividadImplantacion.getAlternativas().size();
			List<Alternativa> alternativasHistorico = new ArrayList<Alternativa>();
			for (Alternativa alternativa : actividadImplantacion.getAlternativas()) {
				if(alternativa.getIdHistorico() == null){
					if (alternativa.isMejorOpcion())
						actividadImplantacion.setAlternativaMejorOpcion(alternativa);
					
					alternativasHistorico.add(alternativa);
				}
			}
			actividadImplantacion.setAlternativas(alternativasHistorico);
		}
		return actividades;
	}

	@SuppressWarnings("unchecked")
	public List<TipoCriterioTecnico> getTiposCriterioTecnico() {
		return (List<TipoCriterioTecnico>) crudServiceBean.findAll(TipoCriterioTecnico.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoSistemaSocioeconomico> getTiposSistemaSocioeconomico() {
		return (List<TipoSistemaSocioeconomico>) crudServiceBean.findAll(TipoSistemaSocioeconomico.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoSistemaEcologico> getTiposSistemaEcologico() {
		return (List<TipoSistemaEcologico>) crudServiceBean.findAll(TipoSistemaEcologico.class);
	}
	
	/**
	 * Guardar Historico
	 */
	public void guardarHistorico(EstudioImpactoAmbiental estudio, List<ActividadImplantacion> actividades,
			List<Alternativa> alternativas, List<ActividadImplantacion> saved, Documento documento, Integer numeroNotificaciones) {
				
		try {
			/**
			 * Las actividades del saved son las actividades que vienen de la base como están en relación con las actividades de implantación
			 * estas se guardan en otra lista que también viene de lo almacenado en bdd.
			 */
			
			saved = getActividadesImplantacion(estudio);
			
			List<ActividadImplantacion> actividadesNoVerificadas = new ArrayList<ActividadImplantacion>();
			List<Alternativa> alternativasNoVerificadas = new ArrayList<Alternativa>();
			if (saved != null && !saved.isEmpty()) {
				actividadesNoVerificadas.addAll(saved);
				for (ActividadImplantacion actividadImplantacion : saved) {
					alternativasNoVerificadas.addAll(actividadImplantacion.getAlternativas());
				}
			}
			//HIDROCARBURO
			if(documento!=null){
				crudServiceBean.saveOrUpdate(documento);
			}

			for (ActividadImplantacion actividadImplantacion : actividades) {
				
				ActividadImplantacion actividadHistorico = obtenerHistorico(actividadImplantacion, saved, numeroNotificaciones);
				if(actividadHistorico != null){
					actividadHistorico.setAlternativas(null);
					actividadHistorico.setFechaCreacion(new Date());
					crudServiceBean.saveOrUpdate(actividadHistorico);
				}				
				//CF: Guardado de Historico
				if(actividadImplantacion.getId() == null){					
					actividadImplantacion.setNumeroNotificacion(numeroNotificaciones);
				}
				
				actividadImplantacion.setEstudioImpactoAmbiental(estudio);
				actividadImplantacion.setFechaCreacion(new Date());
				crudServiceBean.saveOrUpdate(actividadImplantacion);
				actividadesNoVerificadas.remove(actividadImplantacion);
			}

			for (Alternativa alternativa : alternativas) {
				
				alternativa.setMejorOpcion(false);
				for (ActividadImplantacion actividadImplantacion : actividades) {
					if (alternativa.equals(actividadImplantacion.getAlternativaMejorOpcion()))
						alternativa.setMejorOpcion(true);
				}
				
				Alternativa alternativaHistorico = obtenerAlternativasHistorico(alternativa, alternativasNoVerificadas, numeroNotificaciones);
				
				if(alternativaHistorico != null){
					crudServiceBean.saveOrUpdate(alternativaHistorico);
				}								
				
				if(alternativa.getId() == null){
					alternativa.setNumeroNotificacion(numeroNotificaciones);
				}
				
				alternativa.setFechaCreacion(new Date());
				crudServiceBean.saveOrUpdate(alternativa);
				alternativasNoVerificadas.remove(alternativa);
			}
			
			for(ActividadImplantacion actividad: actividadesNoVerificadas){
				if(actividad.getNumeroNotificacion() != null && actividad.getNumeroNotificacion() <= numeroNotificaciones){
					crudServiceBean.delete(actividad);
				}else{
					ActividadImplantacion actividadHistorico = new ActividadImplantacion();
					List<ActividadImplantacion> listaHistorico = getActividadesImplantacionHistorico(actividad.getId(), numeroNotificaciones);
					if(listaHistorico == null){
						actividadHistorico = actividad.clone();
						actividadHistorico.setIdHistorico(actividad.getId());
						actividadHistorico.setNumeroNotificacion(numeroNotificaciones);	
						actividadHistorico.setAlternativas(null);
						crudServiceBean.saveOrUpdate(actividadHistorico);
					}
					crudServiceBean.delete(actividad);
				}						
			}
			
			for(Alternativa alternativa : alternativasNoVerificadas){
				
				if(alternativa.getNumeroNotificacion() != null && alternativa.getNumeroNotificacion() <= numeroNotificaciones){
					crudServiceBean.delete(alternativa);
				}else{
					Alternativa alternativaHistorico = new Alternativa();
					List<Alternativa> listaHistorico = getAlternativaHistorico(alternativa.getId(), numeroNotificaciones);
					
					if(listaHistorico == null){
						alternativaHistorico = alternativa.clone();
						alternativaHistorico.setIdHistorico(alternativa.getId());
						alternativaHistorico.setNumeroNotificacion(numeroNotificaciones);
						alternativaHistorico.setFechaCreacion(new Date());
						crudServiceBean.saveOrUpdate(alternativaHistorico);
					}
					crudServiceBean.delete(alternativa);
				}			
			}
			
			//crudServiceBean.delete(actividadesNoVerificadas);
			crudServiceBean.delete(alternativasNoVerificadas);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private ActividadImplantacion obtenerHistorico(ActividadImplantacion actividad, List<ActividadImplantacion> listaGuardadas, Integer numeroNotificacion){
		
		try {
			ActividadImplantacion actividadHistorico = null;
			
			Comparator<ActividadImplantacion> a = new Comparator<ActividadImplantacion>() {
				
				@Override
				public int compare(ActividadImplantacion a1, ActividadImplantacion a2) {					
					return a1.getId().compareTo(a2.getId());
				}
			};
			
			Collections.sort(listaGuardadas, a);
			
			if(actividad.getId() != null){
				int index = Collections.binarySearch(listaGuardadas, new ActividadImplantacion(actividad.getId()), a);
				
				if(index >= 0){
					ActividadImplantacion actividadEncontrada = listaGuardadas.get(index);
					
					if(!actividadEncontrada.getNombre().equals(actividad.getNombre())){
						
						if(actividad.getNumeroNotificacion() != null && actividad.getNumeroNotificacion() == numeroNotificacion){
							actividadHistorico = null;
						}else{
							List<ActividadImplantacion> listaActividadesHistorico = getActividadesImplantacionHistorico(actividadEncontrada.getId(), numeroNotificacion);
							
							if(listaActividadesHistorico == null){
								actividadHistorico = actividadEncontrada.clone();
								actividadHistorico.setIdHistorico(actividadEncontrada.getId());
								actividadHistorico.setNumeroNotificacion(numeroNotificacion);							
							}						
						}
					}
				}
			}			
			
			if(actividadHistorico == null)
				return null;
			else
				return actividadHistorico;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Alternativa obtenerAlternativasHistorico(Alternativa alternativa, List<Alternativa> listaAlternativasGuardadas, Integer numeroNotificacion){
		
		try {
			
			Alternativa alternativaHistorico = null;			
			
			Comparator<Alternativa> a = new Comparator<Alternativa>() {
				
				@Override
				public int compare(Alternativa a1, Alternativa a2) {					
					return a1.getId().compareTo(a2.getId());
				}
			};
			
			Collections.sort(listaAlternativasGuardadas, a);
			
			if(alternativa.getId() != null){
				int index = Collections.binarySearch(listaAlternativasGuardadas, new Alternativa(alternativa.getId()), a);
				
				if(index >= 0){
					
					Alternativa alternativaEncontrada = listaAlternativasGuardadas.get(index);
					
					if(alternativa.getActividadImplantacion().getId().equals(alternativaEncontrada.getActividadImplantacion().getId()) && 
							alternativa.getCaracteristica().equals(alternativaEncontrada.getCaracteristica()) && 
							alternativa.getNombre().equals(alternativaEncontrada.getNombre()) && 
							alternativa.getTipoCriterioTecnico().equals(alternativaEncontrada.getTipoCriterioTecnico()) && 
							alternativa.getTipoSistemaEcologico().equals(alternativaEncontrada.getTipoSistemaEcologico()) && 
							alternativa.getTipoSistemaSocioeconomico().equals(alternativaEncontrada.getTipoSistemaSocioeconomico())){
						
						//son iguales
						alternativaHistorico = null;
					}else{
						if(alternativa.getNumeroNotificacion() != null && alternativa.getNumeroNotificacion() == numeroNotificacion){
							alternativaHistorico = null;
						}else{
							List<Alternativa> listaAlternativa = getAlternativaHistorico(alternativaEncontrada.getId(), numeroNotificacion);
							
							if(listaAlternativa == null){
								alternativaHistorico = alternativaEncontrada.clone();
								alternativaHistorico.setIdHistorico(alternativaEncontrada.getId());
								alternativaHistorico.setNumeroNotificacion(numeroNotificacion);						
							}						
						}
					}
					
					if(alternativaHistorico == null){
						if(alternativa.isMejorOpcion() == alternativaEncontrada.isMejorOpcion()){
							//son iguales
							alternativaHistorico = null;
						}else{
							boolean guardarHistorico = true;
							if(alternativaEncontrada.isMejorOpcion())
								guardarHistorico = true;
							else
								guardarHistorico = false;
							
							
							if(guardarHistorico){
								if(alternativa.getNumeroNotificacion() != null && alternativa.getNumeroNotificacion() == numeroNotificacion){
									alternativaHistorico = null;
								}else{
									List<Alternativa> listaAlternativa = getAlternativaHistorico(alternativaEncontrada.getId(), numeroNotificacion);
									
									if(listaAlternativa == null){
										alternativaHistorico = alternativaEncontrada.clone();
										alternativaHistorico.setIdHistorico(alternativaEncontrada.getId());
										alternativaHistorico.setNumeroNotificacion(numeroNotificacion);						
									}						
								}
							}
						}
					}					
				}
			}			
			
			if(alternativaHistorico == null)
				return null;
			else
				return alternativaHistorico;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ActividadImplantacion> getActividadesImplantacionHistorico(Integer idHistorico, Integer numeroNotificacion) {
		
		try{
			List<ActividadImplantacion> actividades = crudServiceBean.getEntityManager().
					createQuery("Select a FROM ActividadImplantacion a WHERE  a.idHistorico= :idHistorico AND a.numeroNotificacion = :notificacion order by 1 desc")
					.setParameter("idHistorico", idHistorico).setParameter("notificacion", numeroNotificacion).getResultList();
			
			if(actividades != null && !actividades.isEmpty())
				return actividades;
			else
				return null;
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Alternativa> getAlternativaHistorico(Integer idHistorico, Integer numeroNotificacion) {
		
		try{
			List<Alternativa> alternativas = crudServiceBean.getEntityManager().
					createQuery("Select a FROM Alternativa a WHERE  a.idHistorico= :idHistorico AND a.numeroNotificacion = :notificacion order by 1 desc")
					.setParameter("idHistorico", idHistorico).setParameter("notificacion", numeroNotificacion).getResultList();
			
			if(alternativas != null && !alternativas.isEmpty())
				return alternativas;
			else
				return null;
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * MarielaG
	 * Consultar actividades implantacion originales
	 */
	@SuppressWarnings("unchecked")
	public List<ActividadImplantacion> getActividadesImplantacionEstudio(Integer idEstudio) {
		
		try{
			List<ActividadImplantacion> actividades = crudServiceBean.getEntityManager().
					createQuery("Select a FROM ActividadImplantacion a WHERE a.estudioImpactoAmbiental.id = :estudio ")
					.setParameter("estudio", idEstudio).getResultList();
			
			if(actividades != null && !actividades.isEmpty())
				return actividades;
			else
				return null;
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * MarielaG
	 * Consultar alternativas originales
	 */
	@SuppressWarnings("unchecked")
	public List<Alternativa> getAlternativasEstudio(Integer idEstudio) {
		
		try{
			List<Alternativa> actividades = crudServiceBean.getEntityManager().
					createQuery("Select a FROM Alternativa a "
							+ "WHERE a.actividadImplantacion.estudioImpactoAmbiental.id = :estudio "
							+ "order by a.actividadImplantacion.id asc, id asc")
					.setParameter("estudio", idEstudio).getResultList();
			
			if(actividades != null && !actividades.isEmpty())
				return actividades;
			else
				return null;
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ActividadImplantacion> getActividadesImplantacionEnBdd(EstudioImpactoAmbiental estudio) {
		List<ActividadImplantacion> actividades = crudServiceBean.getEntityManager().
				createQuery("Select a FROM ActividadImplantacion a WHERE a.estudioImpactoAmbiental.id = :estudio ")
				.setParameter("estudio", estudio.getId()).getResultList();
		
		for (ActividadImplantacion actividadImplantacion : actividades) {
			actividadImplantacion.getAlternativas().size();
			List<Alternativa> alternativasHistorico = new ArrayList<Alternativa>();
			for (Alternativa alternativa : actividadImplantacion.getAlternativas()) {
				if(alternativa.getIdHistorico() == null){
					if (alternativa.isMejorOpcion())
						actividadImplantacion.setAlternativaMejorOpcion(alternativa);
					
					alternativasHistorico.add(alternativa);
				}
			}
			actividadImplantacion.setAlternativas(alternativasHistorico);
		}
		return actividades;
	}
}