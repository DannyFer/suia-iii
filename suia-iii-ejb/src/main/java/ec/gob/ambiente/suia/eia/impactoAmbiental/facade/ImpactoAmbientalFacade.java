/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.eia.impactoAmbiental.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.AspectoAmbiental;
import ec.gob.ambiente.suia.domain.Componente;
import ec.gob.ambiente.suia.domain.DetalleEvaluacionAspectoAmbiental;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.EvaluacionAspectoAmbiental;
import ec.gob.ambiente.suia.domain.IdentificacionEvaluacionImpactoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 26/06/2015]
 *          </p>
 */

@Stateless
public class ImpactoAmbientalFacade {

    @EJB
    private CrudServiceBean crudServiceBean;

    @EJB
    private DocumentosFacade documentosFacade;

    @SuppressWarnings("unchecked")
    public List<Componente> getComponentes() throws Exception {
        return (List<Componente>) crudServiceBean.findAll(Componente.class);
    }

    @SuppressWarnings("unchecked")
    public List<Componente> getComponentes(String nombre) throws Exception {
        Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
        parameters.put("nombre", nombre);
        List<Componente> result = (List<Componente>) crudServiceBean
                .findByNamedQuery(Componente.LISTAR, parameters);
        return result;
    }

	@SuppressWarnings("unchecked")
	public List<AspectoAmbiental> getAspectosAmbientalesPorComponente(
			Componente componente) throws Exception {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idComponente", componente.getId());
		List<AspectoAmbiental> result = (List<AspectoAmbiental>) crudServiceBean
				.findByNamedQuery(AspectoAmbiental.FIND_BY_COMPONENT,
						parameters);
		return result;
	}


	@SuppressWarnings("unchecked")
	public List<DetalleEvaluacionAspectoAmbiental> getDetallesEvaluacionAspectosAmbientalesPorEvaluacion(
			EvaluacionAspectoAmbiental evaluacionAspectoAmbiental)
			throws Exception {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idEvaluacion", evaluacionAspectoAmbiental.getId());
		List<DetalleEvaluacionAspectoAmbiental> result = (List<DetalleEvaluacionAspectoAmbiental>) crudServiceBean
				.findByNamedQuery(
						DetalleEvaluacionAspectoAmbiental.FIND_BY_ENVIRONMENTAL_ASPECT_EVALUATION,
						parameters);
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<EvaluacionAspectoAmbiental> getEvaluacionesAspectosAmbientalesPorImpactoAmbiental(
			IdentificacionEvaluacionImpactoAmbiental identificacionEvaluacionImpactoAmbiental)
			throws Exception {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idImpactoAmbiental",
				identificacionEvaluacionImpactoAmbiental.getId());
		List<EvaluacionAspectoAmbiental> result = (List<EvaluacionAspectoAmbiental>) crudServiceBean
				.findByNamedQuery(
						EvaluacionAspectoAmbiental.FIND_BY_IMPACTO_AMBIENTAL,
						parameters);
		return result;
	}

    public void guardar(EstudioImpactoAmbiental estudio, TaskSummaryCustom tarea,
                        IdentificacionEvaluacionImpactoAmbiental identificacionEvaluacionImpactoAmbiental,
                        List<EvaluacionAspectoAmbiental> evaluacionesSalvadas, List<EvaluacionAspectoAmbiental> evaluacionesNuevas) {
        try {
            Documento documento = identificacionEvaluacionImpactoAmbiental
                    .getDocumento();
            byte[] contenidoDocumento = identificacionEvaluacionImpactoAmbiental
                    .getDocumento().getContenidoDocumento();

            Documento tratamiento = identificacionEvaluacionImpactoAmbiental
                    .getTratamiento();
            if (tratamiento != null && tratamiento.getContenidoDocumento() != null) {
                byte[] contenidoTratamiento = identificacionEvaluacionImpactoAmbiental
                        .getTratamiento().getContenidoDocumento();
                DocumentosTareasProceso documentosTareasProceso1 = new DocumentosTareasProceso();
                documentosTareasProceso1.setIdTarea(tarea.getTaskId());
                documentosTareasProceso1.setProcessInstanceId(tarea
                        .getProcessInstanceId());
                tratamiento = documentosFacade.guardarDocumentoAlfresco(estudio.getProyectoLicenciamientoAmbiental()
                                .getCodigo(), tarea.getProcessName(),
                        tarea.getProcessInstanceId(), tratamiento,
                        TipoDocumentoSistema.DOCUMENTO_DEL_PROPONENTE_EIA, documentosTareasProceso1);
                tratamiento.setContenidoDocumento(contenidoTratamiento);
                identificacionEvaluacionImpactoAmbiental
                        .setTratamiento(tratamiento);
            }
            DocumentosTareasProceso documentosTareasProceso = new DocumentosTareasProceso();
            documentosTareasProceso.setIdTarea(tarea.getTaskId());
            documentosTareasProceso.setProcessInstanceId(tarea
                    .getProcessInstanceId());

            documento = documentosFacade.guardarDocumentoAlfresco(estudio.getProyectoLicenciamientoAmbiental()
                            .getCodigo(), tarea.getProcessName(),
                    tarea.getProcessInstanceId(), documento,
                    TipoDocumentoSistema.DOCUMENTO_DEL_PROPONENTE_EIA, documentosTareasProceso);


            documento.setContenidoDocumento(contenidoDocumento);

            identificacionEvaluacionImpactoAmbiental.setDocumento(documento);

            identificacionEvaluacionImpactoAmbiental
                    .setEstudioImpactoAmbiental(estudio);

            crudServiceBean
                    .saveOrUpdate(identificacionEvaluacionImpactoAmbiental);

            List<EvaluacionAspectoAmbiental> evaluacionesSalvadasCopia = new ArrayList<EvaluacionAspectoAmbiental>();
            List<DetalleEvaluacionAspectoAmbiental> detallesSalvados = new ArrayList<DetalleEvaluacionAspectoAmbiental>();

            if (evaluacionesSalvadas != null && !evaluacionesSalvadas.isEmpty()) {
                evaluacionesSalvadasCopia.addAll(evaluacionesSalvadas);
                for (EvaluacionAspectoAmbiental evaluacion : evaluacionesSalvadas) {
                    detallesSalvados.addAll(evaluacion
                            .getDetalleEvaluacionListaSalvadas());
                }
            }

            for (EvaluacionAspectoAmbiental evaluacion : evaluacionesNuevas) {
                evaluacion
                        .setIdentificacionEvaluacionImpactoAmbiental(identificacionEvaluacionImpactoAmbiental);

                List<DetalleEvaluacionAspectoAmbiental> detalles = new ArrayList<DetalleEvaluacionAspectoAmbiental>();
                detalles.addAll(evaluacion.getDetalleEvaluacionLista());

                crudServiceBean.saveOrUpdate(evaluacion);

                for (DetalleEvaluacionAspectoAmbiental detalle : detalles) {
                    detalle.setEvaluacionAspectoAmbiental(evaluacion);
                    crudServiceBean.saveOrUpdate(detalle);
                    detallesSalvados.remove(detalle);
                }
                evaluacion.setDetalleEvaluacionLista(detalles);
                evaluacionesSalvadasCopia.remove(evaluacion);
            }
            crudServiceBean.delete(evaluacionesSalvadasCopia);
            crudServiceBean.delete(detallesSalvados);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public IdentificacionEvaluacionImpactoAmbiental getIdentificacionEvaluacionImpactoAmbientale(
            EstudioImpactoAmbiental estudio) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("idEstudio", estudio.getId());

        IdentificacionEvaluacionImpactoAmbiental impacto = new IdentificacionEvaluacionImpactoAmbiental();
        List<IdentificacionEvaluacionImpactoAmbiental> impactos = (List<IdentificacionEvaluacionImpactoAmbiental>) crudServiceBean
                .findByNamedQuery(IdentificacionEvaluacionImpactoAmbiental.GET_BY_ESTUDIO, parameters);

        if (impactos != null && !impactos.isEmpty()) {
            if (impactos != null && !impactos.isEmpty())
                impacto = impactos.get(0);

            impacto.getEvaluacionAspectoAmbientals().size();
            for (EvaluacionAspectoAmbiental evaluacion : impacto.getEvaluacionAspectoAmbientals()) {
                evaluacion.getDetalleEvaluacionLista().size();
                evaluacion.setDetalleEvaluacionListaSalvadas(null);
                evaluacion.getDetalleEvaluacionListaSalvadas().addAll(evaluacion.getDetalleEvaluacionLista());
                for (DetalleEvaluacionAspectoAmbiental detalle : evaluacion.getDetalleEvaluacionLista()) {
                    detalle.getComponente().toString();
                    detalle.getAspectoAmbiental().toString();
                }
            }
        }
        return impacto;
    }

	@SuppressWarnings("unchecked")
	public List<IdentificacionEvaluacionImpactoAmbiental> getIdentificacionEvaluacionImpactoAmbientalPorEIA(
			EstudioImpactoAmbiental eia) throws Exception {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idEstudio", eia.getId());
		List<IdentificacionEvaluacionImpactoAmbiental> result = (List<IdentificacionEvaluacionImpactoAmbiental>) crudServiceBean
				.findByNamedQuery(
						IdentificacionEvaluacionImpactoAmbiental.GET_BY_ESTUDIO,
						parameters);
		return result;
	}

	/**
	 * Retorna Detalle Evaluacion Aspecto Ambiental por EIA
	 *
	 * @param eia
	 * @return Lista DetalleEvaluacionAspectoAmbiental
	 * @throws Exception
	 */
	public List<DetalleEvaluacionAspectoAmbiental> getDetalleEvaluacionAspectoAmbientalPorEIA(
			EstudioImpactoAmbiental eia) throws Exception {

		List<DetalleEvaluacionAspectoAmbiental> detalle = new ArrayList<DetalleEvaluacionAspectoAmbiental>();
		
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idEia", eia.getId());
		detalle = (List<DetalleEvaluacionAspectoAmbiental>) crudServiceBean.findByNamedQuery(
						DetalleEvaluacionAspectoAmbiental.FIND_BY_ENVIRONMENTAL_POR_EIA,parameters);
		
		if(detalle.size()>0){
			return detalle;
		}
		
//		for (IdentificacionEvaluacionImpactoAmbiental ieia : getIdentificacionEvaluacionImpactoAmbientalPorEIA(eia)) {
//			for (EvaluacionAspectoAmbiental eaa : ieia.getEvaluacionAspectoAmbientals()) {
//				for (DetalleEvaluacionAspectoAmbiental deaa : eaa.getDetalleEvaluacionLista()) {
//					detalle.add(deaa);
//					break;
//				}
//			}
//		}
		return detalle;
	}
	
	/**
	 * CF: Guardar Historico
	 */
	public void guardarHistorico(EstudioImpactoAmbiental estudio, TaskSummaryCustom tarea,
			IdentificacionEvaluacionImpactoAmbiental identificacionEvaluacionImpactoAmbiental,
			List<EvaluacionAspectoAmbiental> evaluacionesSalvadas, List<EvaluacionAspectoAmbiental> evaluacionesNuevas, 
			Integer numeroNotificacion, boolean guardarDocumento, boolean guardarTratamiento) {
		try {
			
			if(guardarDocumento){
				
				Documento documento = identificacionEvaluacionImpactoAmbiental.getDocumento();
				byte[] contenidoDocumento = identificacionEvaluacionImpactoAmbiental.getDocumento().getContenidoDocumento();
				
				DocumentosTareasProceso documentosTareasProceso = new DocumentosTareasProceso();
				documentosTareasProceso.setIdTarea(tarea.getTaskId());
				documentosTareasProceso.setProcessInstanceId(tarea.getProcessInstanceId());

				documento = documentosFacade.guardarDocumentoAlfresco(estudio.getProyectoLicenciamientoAmbiental().getCodigo(), tarea
						.getProcessName(), tarea.getProcessInstanceId(), documento,	TipoDocumentoSistema.DOCUMENTO_DEL_PROPONENTE_EIA,
						documentosTareasProceso);

				documento.setContenidoDocumento(contenidoDocumento);

				identificacionEvaluacionImpactoAmbiental.setDocumento(documento);				
			}
			
			if(guardarTratamiento){
				Documento tratamiento = identificacionEvaluacionImpactoAmbiental.getTratamiento();
				if (tratamiento != null) {				
					byte[] contenidoTratamiento = identificacionEvaluacionImpactoAmbiental.getTratamiento().getContenidoDocumento();
					DocumentosTareasProceso documentosTareasProceso1 = new DocumentosTareasProceso();
					documentosTareasProceso1.setIdTarea(tarea.getTaskId());
					documentosTareasProceso1.setProcessInstanceId(tarea.getProcessInstanceId());
					tratamiento = documentosFacade.guardarDocumentoAlfresco(estudio.getProyectoLicenciamientoAmbiental().getCodigo(),
							tarea.getProcessName(), tarea.getProcessInstanceId(),tratamiento,
							TipoDocumentoSistema.DOCUMENTO_DEL_PROPONENTE_EIA,documentosTareasProceso1);
					tratamiento.setContenidoDocumento(contenidoTratamiento);
					identificacionEvaluacionImpactoAmbiental.setTratamiento(tratamiento);
				}
			}			

			identificacionEvaluacionImpactoAmbiental.setEstudioImpactoAmbiental(estudio);
			
			IdentificacionEvaluacionImpactoAmbiental idenEvImAmbBdd = crudServiceBean.find(IdentificacionEvaluacionImpactoAmbiental.class, identificacionEvaluacionImpactoAmbiental.getId());
			
			if(idenEvImAmbBdd.getConclusiones().equals(identificacionEvaluacionImpactoAmbiental.getConclusiones()) && 
					idenEvImAmbBdd.getDocumento().equals(identificacionEvaluacionImpactoAmbiental.getDocumento()) && 
					((idenEvImAmbBdd.getTratamiento() == null && identificacionEvaluacionImpactoAmbiental.getTratamiento() == null) || 
					(idenEvImAmbBdd.getTratamiento() != null && identificacionEvaluacionImpactoAmbiental.getTratamiento() != null &&
					idenEvImAmbBdd.getTratamiento().equals(identificacionEvaluacionImpactoAmbiental.getTratamiento())))
					){
				//es igual y no existe modificación
			}else{				
				List<IdentificacionEvaluacionImpactoAmbiental> listaHistoricoEvaluacion = obtenerHistoricoIdentificacionE(identificacionEvaluacionImpactoAmbiental.getId());
				if(listaHistoricoEvaluacion == null || (listaHistoricoEvaluacion.size() < numeroNotificacion)){
					
					IdentificacionEvaluacionImpactoAmbiental identificacionHistorico = idenEvImAmbBdd.clone();
					identificacionHistorico.setEvaluacionAspectoAmbientals(null);
					identificacionHistorico.setIdHistorico(idenEvImAmbBdd.getId());
					identificacionHistorico.setNumeroNotificacion(numeroNotificacion);
					identificacionHistorico.setFechaCreacion(new Date());
					crudServiceBean.saveOrUpdate(identificacionHistorico);					
				}
			}	
			
			crudServiceBean.saveOrUpdate(identificacionEvaluacionImpactoAmbiental);
						
			List<EvaluacionAspectoAmbiental> evaluacionesSalvadasCopia = new ArrayList<EvaluacionAspectoAmbiental>();
			List<DetalleEvaluacionAspectoAmbiental> detallesSalvados = new ArrayList<DetalleEvaluacionAspectoAmbiental>();

			if (evaluacionesSalvadas != null && !evaluacionesSalvadas.isEmpty()) {
				//evaluacionesSalvadasCopia.addAll(evaluacionesSalvadas);
				for (EvaluacionAspectoAmbiental evaluacion : evaluacionesSalvadas) {
					if(evaluacion.getIdHistorico() == null){
						evaluacionesSalvadasCopia.add(evaluacion);
						
						for(DetalleEvaluacionAspectoAmbiental detalleGuardado : evaluacion.getDetalleEvaluacionListaSalvadas()){
							if(detalleGuardado.getIdHistorico() == null && detalleGuardado.getEstado()){
								detallesSalvados.add(detalleGuardado);
							}
						}	
					}
					//detallesSalvados.addAll(evaluacion.getDetalleEvaluacionListaSalvadas());
				}
			}

			
			if(evaluacionesNuevas != null && !evaluacionesNuevas.isEmpty()){
				for (EvaluacionAspectoAmbiental evaluacion : evaluacionesNuevas) {
					boolean evaluacionGuardada = false;
					
					EvaluacionAspectoAmbiental evaluacionBdd = new EvaluacionAspectoAmbiental();
					if(evaluacion.getId() != null){
						evaluacionBdd = crudServiceBean.find(EvaluacionAspectoAmbiental.class, evaluacion.getId());
						
						if(	((evaluacion.getActividadesPorEtapa() == null && evaluacionBdd.getActividadesPorEtapa() == null) || 
								(evaluacion.getActividadesPorEtapa() != null && evaluacionBdd.getActividadesPorEtapa() != null && 
								evaluacion.getActividadesPorEtapa().equals(evaluacionBdd.getActividadesPorEtapa()))) && 
							((evaluacion.getActividadLicenciamiento() == null && evaluacionBdd.getActividadLicenciamiento() == null) || 
								evaluacion.getActividadLicenciamiento() != null && evaluacionBdd.getActividadLicenciamiento() != null && 
								evaluacion.getActividadLicenciamiento().equals(evaluacionBdd.getActividadLicenciamiento())) && 
							((evaluacion.getEtapasProyecto() == null && evaluacionBdd.getEtapasProyecto() == null) || 
								evaluacion.getEtapasProyecto() != null && evaluacionBdd.getEtapasProyecto() != null && 
								evaluacion.getEtapasProyecto().equals(evaluacionBdd.getEtapasProyecto())) && 
							((evaluacion.getIdentificacionEvaluacionImpactoAmbiental() == null && evaluacionBdd.getIdentificacionEvaluacionImpactoAmbiental() == null) ||
								evaluacion.getIdentificacionEvaluacionImpactoAmbiental() != null && evaluacionBdd.getIdentificacionEvaluacionImpactoAmbiental() != null && 
								evaluacion.getIdentificacionEvaluacionImpactoAmbiental().equals(evaluacionBdd.getIdentificacionEvaluacionImpactoAmbiental()))
							){
							
							//son iguales
						}else{
							List<EvaluacionAspectoAmbiental> listaHistoricoEvaluacion = obtenerHistoricoEvaluacionAspectoAmbiental(evaluacionBdd.getId(), numeroNotificacion) ;
							
							if(evaluacion.getNumeroNotificacion() == null || (evaluacion.getNumeroNotificacion() != null && evaluacion.getNumeroNotificacion() < numeroNotificacion)){
								if(listaHistoricoEvaluacion == null){
									EvaluacionAspectoAmbiental evaluacionHistorico = evaluacionBdd.clone();
									evaluacionHistorico.setNumeroNotificacion(numeroNotificacion);
									evaluacionHistorico.setIdHistorico(evaluacionBdd.getId());
									evaluacionHistorico.setDetalleEvaluacionLista(null);
									evaluacionHistorico.setDetalleEvaluacionListaSalvadas(null);
									crudServiceBean.saveOrUpdate(evaluacionHistorico);
									evaluacionGuardada = true;
								}
							}					
						}				
					}else{
						evaluacion.setNumeroNotificacion(numeroNotificacion);
					}
					
					evaluacion.setIdentificacionEvaluacionImpactoAmbiental(identificacionEvaluacionImpactoAmbiental);

					List<DetalleEvaluacionAspectoAmbiental> detalles = new ArrayList<DetalleEvaluacionAspectoAmbiental>();
					detalles.addAll(evaluacion.getDetalleEvaluacionLista());

					crudServiceBean.saveOrUpdate(evaluacion);

					for (DetalleEvaluacionAspectoAmbiental detalle : detalles) {
						
						if(detalle.getId() != null){
							DetalleEvaluacionAspectoAmbiental detalleBdd = crudServiceBean.find(DetalleEvaluacionAspectoAmbiental.class, detalle.getId()) ;										
							
							if(((detalle.getAspectoAmbiental() == null && detalleBdd.getAspectoAmbiental() == null) ||
									detalle.getAspectoAmbiental() != null && detalleBdd.getAspectoAmbiental() != null && 
									detalle.getAspectoAmbiental().equals(detalleBdd.getAspectoAmbiental())) && 
								((detalle.getComponente() == null && detalleBdd.getComponente() == null) || 
									detalle.getComponente() != null && detalleBdd.getComponente() != null && 
									detalle.getComponente().equals(detalleBdd.getComponente())) && 
								((detalle.getEvaluacionAspectoAmbiental() == null && detalleBdd.getEvaluacionAspectoAmbiental() == null) || 
									detalle.getEvaluacionAspectoAmbiental() != null && detalleBdd.getEvaluacionAspectoAmbiental() != null && 
									detalle.getEvaluacionAspectoAmbiental().equals(detalleBdd.getEvaluacionAspectoAmbiental())) && 
								((detalle.getImpactosIdentificados() == null && detalleBdd.getImpactosIdentificados() == null) || 
									detalle.getImpactosIdentificados() != null && detalleBdd.getImpactosIdentificados() != null && 
									detalle.getImpactosIdentificados().equals(detalleBdd.getImpactosIdentificados())) && 
								((detalle.getResultados() == null && detalleBdd.getResultados() == null) || 
									detalle.getResultados() != null && detalleBdd.getResultados() != null && 
									detalle.getResultados().equals(detalleBdd.getResultados()))){
								//son iguales
							}else{
								List<DetalleEvaluacionAspectoAmbiental> listaHistoricoDetalle = obtenerHistoricoDetalleEvaluacion(detalleBdd.getId(), numeroNotificacion);
								
								if(detalle.getNumeroNotificacion() == null|| (detalle.getNumeroNotificacion() != null && detalle.getNumeroNotificacion() < numeroNotificacion)){
									if(listaHistoricoDetalle == null){
										DetalleEvaluacionAspectoAmbiental detalleHistorico = detalleBdd.clone();
										detalleHistorico.setNumeroNotificacion(numeroNotificacion);
										detalleHistorico.setIdHistorico(detalleBdd.getId());
										detalleHistorico.setFechaCreacion(new Date());
										crudServiceBean.saveOrUpdate(detalleHistorico);
										
										//si es un detalle modificado
										if(!evaluacionGuardada){
											if(evaluacionBdd.getId() != null){
												List<EvaluacionAspectoAmbiental> listaHistoricoEvaluacion = obtenerHistoricoEvaluacionAspectoAmbiental(evaluacionBdd.getId(), numeroNotificacion) ;
												
												if(evaluacion.getNumeroNotificacion() == null || (evaluacion.getNumeroNotificacion() != null && evaluacion.getNumeroNotificacion() < numeroNotificacion)){
													if(listaHistoricoEvaluacion == null){
														EvaluacionAspectoAmbiental evaluacionHistorico = evaluacionBdd.clone();
														evaluacionHistorico.setNumeroNotificacion(numeroNotificacion);
														evaluacionHistorico.setIdHistorico(evaluacionBdd.getId());
														evaluacionHistorico.setDetalleEvaluacionLista(null);
														evaluacionHistorico.setDetalleEvaluacionListaSalvadas(null);														
														crudServiceBean.saveOrUpdate(evaluacionHistorico);
														evaluacionGuardada = true;
													}
												}
											}			
										}
									}
								}
							}
							
						}else{
							detalle.setNumeroNotificacion(numeroNotificacion);				
							
							//si es un detalle nuevo
							if(!evaluacionGuardada){
								if(evaluacionBdd.getId() != null){
									List<EvaluacionAspectoAmbiental> listaHistoricoEvaluacion = obtenerHistoricoEvaluacionAspectoAmbiental(evaluacionBdd.getId(), numeroNotificacion) ;
									
									if(evaluacion.getNumeroNotificacion() == null || (evaluacion.getNumeroNotificacion() != null && evaluacion.getNumeroNotificacion() < numeroNotificacion)){
										if(listaHistoricoEvaluacion == null){
											EvaluacionAspectoAmbiental evaluacionHistorico = evaluacionBdd.clone();
											evaluacionHistorico.setNumeroNotificacion(numeroNotificacion);
											evaluacionHistorico.setIdHistorico(evaluacionBdd.getId());
											evaluacionHistorico.setDetalleEvaluacionLista(null);
											evaluacionHistorico.setDetalleEvaluacionListaSalvadas(null);
											crudServiceBean.saveOrUpdate(evaluacionHistorico);
											evaluacionGuardada = true;
										}
									}
								}			
							}
						}							
						
						detalle.setEvaluacionAspectoAmbiental(evaluacion);
						crudServiceBean.saveOrUpdate(detalle);
						detallesSalvados.remove(detalle);
					}
					evaluacion.setDetalleEvaluacionLista(detalles);
					evaluacionesSalvadasCopia.remove(evaluacion);
				}			
			}
			
			if(!evaluacionesSalvadasCopia.isEmpty()){
				
				for(EvaluacionAspectoAmbiental evaluacionEliminada : evaluacionesSalvadasCopia){
					
					if(evaluacionEliminada.getNumeroNotificacion() == null || (evaluacionEliminada.getNumeroNotificacion() != null && evaluacionEliminada.getNumeroNotificacion() < numeroNotificacion)){
						List<EvaluacionAspectoAmbiental> listaHistoricoEvaluacion = obtenerHistoricoEvaluacionAspectoAmbiental(evaluacionEliminada.getId(), numeroNotificacion) ;
						if(listaHistoricoEvaluacion == null){
							EvaluacionAspectoAmbiental evaluacionHistorico = evaluacionEliminada.clone();
							evaluacionHistorico.setNumeroNotificacion(numeroNotificacion);
							evaluacionHistorico.setIdHistorico(evaluacionEliminada.getId());
							evaluacionHistorico.setDetalleEvaluacionLista(null);
							evaluacionHistorico.setDetalleEvaluacionListaSalvadas(null);
							crudServiceBean.saveOrUpdate(evaluacionHistorico);							
						}	
					}
				}				
			}		
			crudServiceBean.delete(evaluacionesSalvadasCopia);
			
			if(!detallesSalvados.isEmpty()){
				for(DetalleEvaluacionAspectoAmbiental detalleEliminar : detallesSalvados){
					
					if(detalleEliminar.getNumeroNotificacion() == null|| (detalleEliminar.getNumeroNotificacion() != null && detalleEliminar.getNumeroNotificacion() < numeroNotificacion)){
						List<DetalleEvaluacionAspectoAmbiental> listaHistoricoDetalle = obtenerHistoricoDetalleEvaluacion(detalleEliminar.getId(), numeroNotificacion);
					
						if(listaHistoricoDetalle == null){
							DetalleEvaluacionAspectoAmbiental detalleHistorico = detalleEliminar.clone();
							detalleHistorico.setNumeroNotificacion(numeroNotificacion);
							detalleHistorico.setIdHistorico(detalleEliminar.getId());
							detalleHistorico.setFechaCreacion(new Date());
							crudServiceBean.saveOrUpdate(detalleHistorico);
							
							
								List<EvaluacionAspectoAmbiental> listaHistoricoEvaluacion = obtenerHistoricoEvaluacionAspectoAmbiental(detalleEliminar.getEvaluacionAspectoAmbiental().getId(), numeroNotificacion) ;
									
								if(detalleEliminar.getEvaluacionAspectoAmbiental().getNumeroNotificacion() == null || (detalleEliminar.getEvaluacionAspectoAmbiental().getNumeroNotificacion() != null && detalleEliminar.getEvaluacionAspectoAmbiental().getNumeroNotificacion() < numeroNotificacion)){
									if(listaHistoricoEvaluacion == null){
										EvaluacionAspectoAmbiental evaluacionHistorico = detalleEliminar.getEvaluacionAspectoAmbiental().clone();
										evaluacionHistorico.setNumeroNotificacion(numeroNotificacion);
										evaluacionHistorico.setIdHistorico(detalleEliminar.getEvaluacionAspectoAmbiental().getId());
										evaluacionHistorico.setDetalleEvaluacionLista(null);
										evaluacionHistorico.setDetalleEvaluacionListaSalvadas(null);
										crudServiceBean.saveOrUpdate(evaluacionHistorico);											
									}
								}
						}
					}
				}
			}
			crudServiceBean.delete(detallesSalvados);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<IdentificacionEvaluacionImpactoAmbiental> obtenerHistoricoIdentificacionE(Integer id) {
		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("Select i from IdentificacionEvaluacionImpactoAmbiental i where "
					+ "i.idHistorico = :id");
			query.setParameter("id", id);
			List<IdentificacionEvaluacionImpactoAmbiental> result = query.getResultList();
			if(result != null && !result.isEmpty()){
				return result;
			}else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<EvaluacionAspectoAmbiental> obtenerHistoricoEvaluacionAspectoAmbiental(Integer id, Integer numeroNotificacion) {
		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("Select e from EvaluacionAspectoAmbiental e where "
					+ "e.idHistorico = :id and e.numeroNotificacion = :numeroNotificacion");
			query.setParameter("id", id);
			query.setParameter("numeroNotificacion", numeroNotificacion);
			
			List<EvaluacionAspectoAmbiental> result = query.getResultList();
			if(result != null && !result.isEmpty()){
				return result;
			}else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleEvaluacionAspectoAmbiental> obtenerHistoricoDetalleEvaluacion(Integer id, Integer numeroNotificacion) {
		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("Select d from DetalleEvaluacionAspectoAmbiental d where "
					+ "d.idHistorico = :id and d.numeroNotificacion = :numeroNotificacion");
			query.setParameter("id", id);
			query.setParameter("numeroNotificacion", numeroNotificacion);
			
			List<DetalleEvaluacionAspectoAmbiental> result = query.getResultList();
			if(result != null && !result.isEmpty()){
				return result;
			}else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleEvaluacionAspectoAmbiental> obtenerDetalleEvaluacionPorEvaluacion(EvaluacionAspectoAmbiental evaluacion) {
		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("Select d from DetalleEvaluacionAspectoAmbiental d where "
					+ "d.evaluacionAspectoAmbiental = :evaluacion and d.idHistorico = null");
			query.setParameter("evaluacion", evaluacion);
			
			List<DetalleEvaluacionAspectoAmbiental> result = query.getResultList();
			if(result != null && !result.isEmpty()){
				return result;
			}else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * MarielaG recupera los registros historicos
	 * @param idHistorico
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<IdentificacionEvaluacionImpactoAmbiental> obtenerImpactoAmbientalHistorico(Integer idHistorico) {
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("Select i from IdentificacionEvaluacionImpactoAmbiental i where "
					+ "i.idHistorico = :idHistorico AND i.estado = true order by id desc");
			query.setParameter("idHistorico", idHistorico);
			
			List<IdentificacionEvaluacionImpactoAmbiental> result = query.getResultList();
			if(result != null && !result.isEmpty()){
				return result;
			}else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * MarielaG recupera detalle de evaluacion eliminada
	 * @param evaluacion
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DetalleEvaluacionAspectoAmbiental> obtenerDetallePorEvaluacionEliminada(Integer idEvaluacion) {
		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("Select d from DetalleEvaluacionAspectoAmbiental d where "
					+ "d.evaluacionAspectoAmbiental.id = :idEvaluacion");
			query.setParameter("idEvaluacion", idEvaluacion);
			
			List<DetalleEvaluacionAspectoAmbiental> result = query.getResultList();
			if(result != null && !result.isEmpty()){
				return result;
			}else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
