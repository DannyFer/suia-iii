/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.eia.impactoAmbiental.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.DetalleEvaluacionAspectoAmbiental;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.EvaluacionAspectoAmbiental;
import ec.gob.ambiente.suia.domain.IdentificacionEvaluacionImpactoAmbiental;
import ec.gob.ambiente.suia.eia.impactoAmbiental.facade.ImpactoAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 26/06/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class IdentificacionEvaluacionImpactosBean implements Serializable {

	private static final long serialVersionUID = 6134327468384122515L;

	private static final Logger LOGGER = Logger.getLogger(IdentificacionEvaluacionImpactosBean.class);

	@Setter
	private IdentificacionEvaluacionImpactoAmbiental identificacionEvaluacionImpactoAmbiental;

	@Getter
	private List<EvaluacionAspectoAmbiental> evaluacionesSalvadas;

	@Getter
	private Documento indicaciones;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private ImpactoAmbientalFacade impactoAmbientalFacade;

	@Getter
	private EstudioImpactoAmbiental estudio;
	
	@Getter
	private Boolean esMineriaNoMetalicos;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
		
	private Map<String, Object> processVariables;
	
	@Getter
	private Integer numeroNotificaciones;
	@Getter
	private boolean existeObservaciones;
	
	@Getter
	private List<EvaluacionAspectoAmbiental> evaluacionesGuardadas, evaluacionesOriginales;
	
	@Getter
	private List<EvaluacionAspectoAmbiental> evaluacionesEliminadas, evaluacionesEliminadasEnBdd;
	
	@Getter
	@Setter
	private List<Documento> listaDocumentoGeneralHistorico, listaDocumentoResultadoHistorico, listaDocumentosHistorico;
	
	@Getter
	private EvaluacionAspectoAmbiental evaluacionSeleccionada;
	
	@Getter
	@Setter
	private List<DetalleEvaluacionAspectoAmbiental> listaDetallesEnBdd, listaDetalleOriginales, listaDetalleHistorico, detallesEliminadosBdd;
	
	@Getter
	@Setter
	private Integer totalDetallesModificados;
	

	public IdentificacionEvaluacionImpactoAmbiental getIdentificacionEvaluacionImpactoAmbiental() {
		return identificacionEvaluacionImpactoAmbiental == null ? identificacionEvaluacionImpactoAmbiental = new IdentificacionEvaluacionImpactoAmbiental()
				: identificacionEvaluacionImpactoAmbiental;
	}
	
	@Getter
	@Setter
	private List<IdentificacionEvaluacionImpactoAmbiental> identificacionEvaluacionImpactoAmbientalHistoricoList;

	@PostConstruct
	public void init() {
		try {
			
			/**
			 * Cris F obtener variables
			 */
			processVariables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
			String numNotificaciones = (String) processVariables.get("cantidadNotificaciones");
			numeroNotificaciones = Integer.valueOf(numNotificaciones);		
			String promotor = (String) processVariables.get("u_Promotor");
			
			if(numeroNotificaciones > 0){
				existeObservaciones = true;
			}
			//Fin CF	
						
			inicializarAyudas();
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	        estudio=(EstudioImpactoAmbiental) request.getSession().getAttribute("estudio");
			//estudio = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);

	        evaluacionesGuardadas = new ArrayList<EvaluacionAspectoAmbiental>();
			evaluacionesEliminadas = new ArrayList<EvaluacionAspectoAmbiental>();
			listaDetallesEnBdd = new ArrayList<DetalleEvaluacionAspectoAmbiental>();
			evaluacionesSalvadas = new ArrayList<EvaluacionAspectoAmbiental>();
			
	        identificacionEvaluacionImpactoAmbiental = impactoAmbientalFacade.getIdentificacionEvaluacionImpactoAmbientale(estudio);		
			if (identificacionEvaluacionImpactoAmbiental != null && identificacionEvaluacionImpactoAmbiental.getId() != null) {
				evaluacionesSalvadas = identificacionEvaluacionImpactoAmbiental.getEvaluacionAspectoAmbientals();
				for (EvaluacionAspectoAmbiental evaluacion : evaluacionesSalvadas) {
					if (evaluacion.getIdHistorico() == null
							&& evaluacion.getActividadLicenciamiento().getEstado()) {
						List<DetalleEvaluacionAspectoAmbiental> detallesEnBdd = evaluacion.getDetalleEvaluacionLista();
						listaDetallesEnBdd.addAll(detallesEnBdd);
						List<DetalleEvaluacionAspectoAmbiental> evaluacionDetalles = new ArrayList<DetalleEvaluacionAspectoAmbiental>();
						for (DetalleEvaluacionAspectoAmbiental detalle : detallesEnBdd) {
							if (detalle.getIdHistorico() == null) {
								evaluacionDetalles.add(detalle);
							}
						}

						evaluacion.setDetalleEvaluacionLista(evaluacionDetalles);

						evaluacionesGuardadas.add(evaluacion);
					}
				}
				if (identificacionEvaluacionImpactoAmbiental.getDocumento().getContenidoDocumento() == null) {
					Documento documento = JsfUtil.getBean(ImpactoAmbientalBaseBean.class).descargarDocumentoAlfresco(identificacionEvaluacionImpactoAmbiental.getDocumento());
					identificacionEvaluacionImpactoAmbiental.getDocumento().setContenidoDocumento(documento.getContenidoDocumento());
				}
					
				JsfUtil.getBean(EvaluacionAspectoAmbientalBean.class).setEvaluacionAspectoAmbientalLista(null);
				JsfUtil.getBean(EvaluacionAspectoAmbientalBean.class).getEvaluacionAspectoAmbientalLista().addAll(evaluacionesGuardadas);
				

//				JsfUtil.getBean(ImpactoAmbientalBaseBean.class).descargarDocumentoAlfresco(identificacionEvaluacionImpactoAmbiental.getDocumento());
//				JsfUtil.getBean(ImpactoAmbientalBaseBean.class).descargarDocumentoAlfresco(identificacionEvaluacionImpactoAmbiental.getTratamiento());
//
//				if (evaluacionesSalvadas != null && !evaluacionesSalvadas.isEmpty()) {
//					
//					for(EvaluacionAspectoAmbiental evaluacion : evaluacionesSalvadas){
//						if(evaluacion.getIdHistorico() == null){
//							List<DetalleEvaluacionAspectoAmbiental> detalles = impactoAmbientalFacade.obtenerDetalleEvaluacionPorEvaluacion(evaluacion);
//							evaluacion.setDetalleEvaluacionLista(detalles);
//							
//							if(evaluacion.getActividadLicenciamiento().getEstado()){
//								evaluacionesGuardadas.add(evaluacion);
//							}else{
//								evaluacionesEliminadas.add(evaluacion);
//							}
//						}
//					}
//					
//					JsfUtil.getBean(EvaluacionAspectoAmbientalBean.class).setEvaluacionAspectoAmbientalLista(null);
//					JsfUtil.getBean(EvaluacionAspectoAmbientalBean.class).getEvaluacionAspectoAmbientalLista().addAll(evaluacionesGuardadas);
//				}
				
				//MarielaG
				if(existeObservaciones){
					//consultar datos originales cuando el usuario es diferente al proponente
//					if(!promotor.equals(loginBean.getNombreUsuario())){
						consultarImpactoOriginal();
						consultarEvaluacionImpactosOriginal(evaluacionesSalvadas);
//					}
				}
			}
			 esMineriaNoMetalicos=estudio.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo().equals("21.02.03.02") && estudio.getResumenEjecutivo()==null?true:false;

		} catch (Exception exception) {
			LOGGER.error("Ocurrió un error en el alfresco al iniciar las ayudas del EIA- Impacto Ambiental.", exception);
		}
	}

	public void inicializarAyudas() throws Exception {
		indicaciones = new Documento();
		byte[] indicacionesContenido = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(
				Constantes.EIA_INDICACIONES_PARA_GENERAR_DOCUMENTO_ADJUNTO, null);
		if (indicacionesContenido != null) {
			indicaciones.setContenidoDocumento(indicacionesContenido);
			indicaciones.setNombre(Constantes.EIA_INDICACIONES_PARA_GENERAR_DOCUMENTO_ADJUNTO);
		}
	}
	
	/**
	 * MarielaG
	 * Consultar la informacion ingresada antes de las correcciones
	 */
	private void consultarImpactoOriginal() {
		try {
			listaDocumentoGeneralHistorico = new ArrayList<>();
			listaDocumentoResultadoHistorico = new ArrayList<>();
			identificacionEvaluacionImpactoAmbientalHistoricoList = new ArrayList<IdentificacionEvaluacionImpactoAmbiental>();
			
			List<IdentificacionEvaluacionImpactoAmbiental> lista = impactoAmbientalFacade.obtenerImpactoAmbientalHistorico(
					identificacionEvaluacionImpactoAmbiental.getId());
			if (lista != null && !lista.isEmpty()) {
				for(IdentificacionEvaluacionImpactoAmbiental impactoHistorial : lista){
					if(!impactoHistorial.getDocumento().getId().equals(identificacionEvaluacionImpactoAmbiental.getDocumento().getId())){
						impactoHistorial.getDocumento().setNumeroNotificacion(impactoHistorial.getNumeroNotificacion());
						if(!listaDocumentoGeneralHistorico.contains(impactoHistorial.getDocumento())){
							listaDocumentoGeneralHistorico.add(0, impactoHistorial.getDocumento());
						}
					}
					
					if(impactoHistorial.getTratamiento() != null && 
							!impactoHistorial.getTratamiento().getId().equals(identificacionEvaluacionImpactoAmbiental.getTratamiento().getId())){
						impactoHistorial.getTratamiento().setNumeroNotificacion(impactoHistorial.getNumeroNotificacion());
						if(!listaDocumentoResultadoHistorico.contains(impactoHistorial.getTratamiento())){
							listaDocumentoResultadoHistorico.add(0, impactoHistorial.getTratamiento());
						}
					}
					
					if(impactoHistorial.getConclusiones() != null && !impactoHistorial.getConclusiones().equals(identificacionEvaluacionImpactoAmbiental.getConclusiones())){
						identificacionEvaluacionImpactoAmbientalHistoricoList.add(impactoHistorial);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Getter
	@Setter
	private Integer totalModificados;
	
	private void consultarEvaluacionImpactosOriginalCarga(EvaluacionAspectoAmbiental listaEvaluacionesEnBdd) {
		try {
				List<DetalleEvaluacionAspectoAmbiental> detallesEliminados = new ArrayList<>();
				detallesEliminados = impactoAmbientalFacade.obtenerDetallePorEvaluacionEliminada(listaEvaluacionesEnBdd.getIdHistorico());				
				listaEvaluacionesEnBdd.setDetalleEvaluacionLista(detallesEliminados);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Consultar el listado de detalles ingresados antes de las correcciones
	 */
	private void consultarEvaluacionImpactosOriginal(List<EvaluacionAspectoAmbiental> listaEvaluacionesEnBdd) {
		try {
			List<EvaluacionAspectoAmbiental> evaluacionesOriginales = new ArrayList<EvaluacionAspectoAmbiental>();			
			evaluacionesEliminadasEnBdd = new ArrayList<>();
			int totalModificados = 0;
			
			for(EvaluacionAspectoAmbiental evaluacionBdd : listaEvaluacionesEnBdd){
				//si es un registro que se ingreso originalmente
				//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
				if(evaluacionBdd.getNumeroNotificacion() == null ||
						!evaluacionBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
					boolean agregarItemLista = true;
	    			//buscar si tiene historial
					for (EvaluacionAspectoAmbiental evaluacionHistorico : listaEvaluacionesEnBdd) {
						if (evaluacionHistorico.getIdHistorico() != null  
								&& evaluacionHistorico.getIdHistorico().equals(evaluacionBdd.getId())) {
							//si existe un registro historico, no se agrega a la lista en este paso
							agregarItemLista = false;
							evaluacionBdd.setRegistroModificado(true);
							break;
						}
					}
					if (agregarItemLista) {
						evaluacionesOriginales.add(evaluacionBdd);
					}
				} else {
					totalModificados++;
	    			//es una modificacion
	    			if(evaluacionBdd.getIdHistorico() == null && evaluacionBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
	    				//es un registro nuevo
	    				//no ingresa en el lista de originales
	    				evaluacionBdd.setNuevoEnModificacion(true);
	    			}else{
	    				evaluacionBdd.setRegistroModificado(true);
	    				if(!evaluacionesOriginales.contains(evaluacionBdd)){
	    					evaluacionesOriginales.add(evaluacionBdd);
	    				}
	    			}
	    		}
				
				//para consultar eliminados
				if (evaluacionBdd.getIdHistorico() != null
						&& evaluacionBdd.getNumeroNotificacion() != null
						&& evaluacionBdd.getNumeroNotificacion().equals(numeroNotificaciones)) {
					boolean existePunto = false;
					for (EvaluacionAspectoAmbiental itemActual : JsfUtil.getBean(EvaluacionAspectoAmbientalBean.class).getEvaluacionAspectoAmbientalLista()) {
						if (itemActual.getId().equals(evaluacionBdd.getIdHistorico())) {
							existePunto = true;
							break;
						}
					}

					if (!existePunto) {
						evaluacionesEliminadasEnBdd.add(evaluacionBdd);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Consultar el listado de detalles ingresados antes de las correcciones
	 */
	private void consultarDetalleImpactosOriginal() {
		try {
			List<DetalleEvaluacionAspectoAmbiental> detallesOriginales = new ArrayList<>();
			List<DetalleEvaluacionAspectoAmbiental> detallesEliminados = new ArrayList<>();
			totalDetallesModificados = 0;
			
			for(DetalleEvaluacionAspectoAmbiental detalleBdd : listaDetallesEnBdd){
				if(detalleBdd.getEvaluacionAspectoAmbiental().getId().equals(evaluacionSeleccionada.getId())){
					//si es un registro que se ingreso originalmente
					//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
					if(detalleBdd.getNumeroNotificacion() == null ||
							!detalleBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
						boolean agregarItemLista = true;
		    			//buscar si tiene historial
						for (DetalleEvaluacionAspectoAmbiental evaluacionHistorico : listaDetallesEnBdd) {
							if (evaluacionHistorico.getIdHistorico() != null  
									&& evaluacionHistorico.getIdHistorico().equals(detalleBdd.getId())) {
								//si existe un registro historico, no se agrega a la lista en este paso
								agregarItemLista = false;
								detalleBdd.setRegistroModificado(true);
								break;
							}
						}
						if (agregarItemLista) {
							detallesOriginales.add(detalleBdd);
						}
					} else {
						totalDetallesModificados++;
		    			//es una modificacion
		    			if(detalleBdd.getIdHistorico() == null && detalleBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
		    				//es un registro nuevo
		    				//no ingresa en el lista de originales
		    				detalleBdd.setNuevoEnModificacion(true);
		    			}else{
		    				detalleBdd.setRegistroModificado(true);
		    				if(!detallesOriginales.contains(detalleBdd)){
		    					detallesOriginales.add(detalleBdd);
		    				}
		    			}
		    		}
					
					//para consultar eliminados
					if (detalleBdd.getIdHistorico() != null
							&& detalleBdd.getNumeroNotificacion() != null
							//&& detalleBdd.getNumeroNotificacion().equals(numeroNotificaciones)
							) {
						boolean existePunto = false;
						for (DetalleEvaluacionAspectoAmbiental itemActual : evaluacionSeleccionada.getDetalleEvaluacionLista()) {
							if (itemActual.getId().equals(detalleBdd.getIdHistorico())) {
								existePunto = true;
								break;
							}
						}
	
						if (!existePunto) {
							detallesEliminados.add(detalleBdd);
						}
					}
				}
			}
			
			if (totalDetallesModificados > 0){
				this.listaDetalleOriginales = detallesOriginales;
			}
			
			detallesEliminadosBdd = detallesEliminados;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Visualizar informacion de una evaluacion
	 */
	public void mostrarDetalles(EvaluacionAspectoAmbiental evaluacion) {
		evaluacionSeleccionada = evaluacion;
		
		listaDetalleOriginales = new ArrayList<>();
		detallesEliminadosBdd = new ArrayList<>();
		consultarEvaluacionImpactosOriginalCarga(evaluacion);
		consultarDetalleImpactosOriginal();
	}
	
	/**
	 * MarielaG
	 * Visualizar informacion de un plan de monitoreo
	 */
	public void mostrarDetallesOriginales(DetalleEvaluacionAspectoAmbiental detalle) {
		listaDetalleHistorico = new ArrayList<>();

		for (DetalleEvaluacionAspectoAmbiental detalleOriginal : listaDetalleOriginales) {
			if (detalleOriginal.getIdHistorico() != null
					&& detalle.getId().equals(
							detalleOriginal.getIdHistorico())) {
				listaDetalleHistorico.add(detalleOriginal);
			}
		}
	}
	
	/**
	 * MarielaG para mostrar el historial de los detalles eliminados
	 */
	public void fillDetallesEliminados() {
		listaDetalleHistorico = new ArrayList<>();

		listaDetalleHistorico = detallesEliminadosBdd;
	}
	
	/**
	 * MarielaG para mostrar el historial de los documentos
	 */
	public void fillHistorialDocumentos(Integer tipoDocumento) { 
		listaDocumentosHistorico = new ArrayList<Documento>();
		
		switch (tipoDocumento) {
			case 0:
				listaDocumentosHistorico = listaDocumentoGeneralHistorico;
				break;
			case 1:
				listaDocumentosHistorico = listaDocumentoResultadoHistorico;
				break;
		}
	}
	
	/**
	 * MarielaG
	 * Para descargar documentos originales
	 */
	public StreamedContent descargarOriginal(Documento documento) throws Exception {
        DefaultStreamedContent content = null;
        try {
			Documento documentoOriginal = JsfUtil.getBean(ImpactoAmbientalBaseBean.class).descargarDocumentoAlfresco(documento);
            if (documentoOriginal != null && documentoOriginal.getNombre() != null && documentoOriginal.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documentoOriginal.getContenidoDocumento()));
                content.setName(documentoOriginal.getNombre());
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
}
