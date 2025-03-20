package ec.gob.ambiente.suia.eia.pma.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.AspectoAmbiental;
import ec.gob.ambiente.suia.domain.Componente;
import ec.gob.ambiente.suia.domain.DetalleEvaluacionAspectoAmbiental;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.Normativas;
import ec.gob.ambiente.suia.domain.PlanManejoAmbiental;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalEIA;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalEIADetalle;
import ec.gob.ambiente.suia.domain.enums.Periodicidad;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.ficha.bean.EstudioImpactoAmbientalBean;
import ec.gob.ambiente.suia.eia.impactoAmbiental.facade.ImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.pma.facade.PlanManejoAmbientalEIADetalleFacade;
import ec.gob.ambiente.suia.eia.pma.facade.PlanManejoAmbientalEIAFacade;
import ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade.PlanMonitoreoEiaFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

@ManagedBean
@ViewScoped
public class PlanManejoAmbientalEIAController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -971306035001252479L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(PlanManejoAmbientalEIAController.class);

	@EJB
	private ValidacionSeccionesFacade validacionesSeccionesFacade;

	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@EJB
	private PlanMonitoreoEiaFacade planMonitoreoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{estudioImpactoAmbientalBean}")
	private EstudioImpactoAmbientalBean eiaBean;

	@EJB
	private ImpactoAmbientalFacade impactoAmbientalFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	@Setter
	private List<Componente> componentes;

	@Getter
	@Setter
	private List<AspectoAmbiental> aspectoAmbientalLista;

	@Getter
	@Setter
	private String tipoTabla;

	/*************************/
	// ///////////

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	@Setter
	PlanManejoAmbientalEIA planManejoAmbientalEIA;

	@Getter
	@Setter
	PlanManejoAmbientalEIADetalle planManejoAmbientalEIADetalle, planManejoAmbientalEIADetalleOriginal;

	@Getter
	@Setter
	List<Normativas> listaNormativas;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIA> listaPlanManejoAmbientalEIA;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetallePrevencion, listaPmaPrevencionOriginal, listaPmaPrevencionEliminadosBdd;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetallleEliminados;
	
	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleContingencias, listaPmaContingenciasOriginal, listaPmaContingenciasEliminadosBdd;
	
	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleComunicacion, listaPmaComunicacionOriginal, listaPmaComunicacionEliminadosBdd;
	
	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleSeguridad, listaPmaSeguridadOriginal, listaPmaSeguridadEliminadosBdd;
	
	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleManejo, listaPmaManejoOriginal, listaPmaManejoEliminadosBdd;
	
	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleRelaciones, listaPmaRelacionesOriginal, listaPmaRelacionesEliminadosBdd;
	
	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleRehabilitacion, listaPmaRehabilitacionOriginal, listaPmaRehabilitacionEliminadosBdd;
	
	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleAbandono, listaPmaAbandonoOriginal, listaPmaAbandonoEliminadosBdd;
	
	@Getter
	@Setter
	List<PlanManejoAmbientalEIA> listaPlanManejoAmbientalEIAEliminados;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIAEIADetalle;

	@Getter
	@Setter
	List<DetalleEvaluacionAspectoAmbiental> listaDetalleEvaluacionAspectoAmbiental;

	@Getter
	@Setter
	DetalleEvaluacionAspectoAmbiental detalleEvaluacionAspectoAmbiental;

	@EJB
	private PlanManejoAmbientalEIAFacade planManejoAmbientalEIAFacade;

	@EJB
	private PlanManejoAmbientalEIADetalleFacade planManejoAmbientalEIADetalleFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;

	@Setter
	@Getter
	private Periodicidad[] periodicidad = Periodicidad.values();

	@Setter
	@Getter
	private Componente componente;

	@Setter
	@Getter
	private AspectoAmbiental aspectoAmbiental;

	@Setter
	@Getter
	private boolean isEditing;

	@Setter
	@Getter
	private boolean opcion2;
	
	@Getter
	private boolean esMineriaNoMetalicos,existeDocumentoAdjunto;
	
	@Getter
	@Setter
	private Documento documento, documentoHistorico, documentoOriginal;
	
	@Getter
	@Setter
	private List<Documento> listaDocumentoHistorico;
	
	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPmaHistorico;

	// //******************/////////////////////////
	

	private Map<String, Object> processVariables;
	private Integer numeroNotificaciones;
	private boolean existeObservaciones;
	private String promotor;

	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	//////////////////////////////////////////////////////////////////////////

	@PostConstruct
	private void postInit() throws Exception {
		
		/**
		 * Cristina Flores obtener variables
		 */
		processVariables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
		String numNotificaciones = (String) processVariables.get("cantidadNotificaciones");
		if(numNotificaciones != null){
			numeroNotificaciones = Integer.valueOf(numNotificaciones);
		}else{
			numeroNotificaciones = 0;
		}	
		promotor = (String) processVariables.get("u_Promotor");
		
		if(numeroNotificaciones > 0)
			existeObservaciones = true;
		//Fin CF	
		
		
		estudioImpactoAmbiental = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);

		listaPlanManejoAmbientalEIAEIADetalle = new ArrayList<PlanManejoAmbientalEIADetalle>();
		componentes = new ArrayList<Componente>();

		cargarDatos();
		
		esMineriaNoMetalicos=estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo().equals("21.02.03.02") && estudioImpactoAmbiental.getResumenEjecutivo()==null?true:false;
		
		if(esMineriaNoMetalicos)
		{
			cargarAdjuntosEIA(TipoDocumentoSistema.PLAN_MANEJO_AMBIENTAL,"Adjunto");
		}

	}

	private void cargarDatos() throws Exception {

		this.isEditing = false;
		this.opcion2 = false;
		estudioImpactoAmbiental = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
		this.planManejoAmbientalEIA = new PlanManejoAmbientalEIA();

		listaDetalleEvaluacionAspectoAmbiental = impactoAmbientalFacade.getDetalleEvaluacionAspectoAmbientalPorEIA(estudioImpactoAmbiental);

		listaPlanManejoAmbientalEIA = new ArrayList<PlanManejoAmbientalEIA>();
		listaPlanManejoAmbientalEIAEliminados = new ArrayList<PlanManejoAmbientalEIA>();

		listaPlanManejoAmbientalEIADetallePrevencion = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPlanManejoAmbientalEIADetallleEliminados = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPlanManejoAmbientalEIADetalleContingencias = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPlanManejoAmbientalEIADetalleComunicacion = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPlanManejoAmbientalEIADetalleSeguridad = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPlanManejoAmbientalEIADetalleManejo = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPlanManejoAmbientalEIADetalleRelaciones = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPlanManejoAmbientalEIADetalleRehabilitacion = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPlanManejoAmbientalEIADetalleAbandono = new ArrayList<PlanManejoAmbientalEIADetalle>();

		try {
			listaPlanManejoAmbientalEIA = planManejoAmbientalEIAFacade.listarPorEIA(estudioImpactoAmbiental);

			if (listaPlanManejoAmbientalEIA.size() > 0) {
				planManejoAmbientalEIA = listaPlanManejoAmbientalEIA.get(0);
//				listaPlanManejoAmbientalEIADetallePrevencion = planManejoAmbientalEIADetalleFacade.listarPorTipo(planManejoAmbientalEIA, "11");//
//				listaPlanManejoAmbientalEIADetalleContingencias = planManejoAmbientalEIADetalleFacade.listarPorTipo(planManejoAmbientalEIA, "12");
//				listaPlanManejoAmbientalEIADetalleComunicacion = planManejoAmbientalEIADetalleFacade.listarPorTipo(planManejoAmbientalEIA, "13");//
//				listaPlanManejoAmbientalEIADetalleSeguridad = planManejoAmbientalEIADetalleFacade.listarPorTipo(planManejoAmbientalEIA, "14");//
//				listaPlanManejoAmbientalEIADetalleManejo = planManejoAmbientalEIADetalleFacade.listarPorTipo(planManejoAmbientalEIA, "15");//
//				listaPlanManejoAmbientalEIADetalleRelaciones = planManejoAmbientalEIADetalleFacade.listarPorTipo(planManejoAmbientalEIA, "16");//
//				listaPlanManejoAmbientalEIADetalleRehabilitacion = planManejoAmbientalEIADetalleFacade.listarPorTipo(planManejoAmbientalEIA, "17");//   
//				listaPlanManejoAmbientalEIADetalleAbandono = planManejoAmbientalEIADetalleFacade.listarPorTipo(planManejoAmbientalEIA, "18");//
				
				//MarielaG
				//Cambio metodo para recuperar todos lo registros de base y evitar realizar doble consulta para recuperar los registros originales
				List<PlanManejoAmbientalEIADetalle>lista=planManejoAmbientalEIADetalleFacade.listarTodosPorTipoEia(planManejoAmbientalEIA);
				if(lista.size()>0){
					for (PlanManejoAmbientalEIADetalle planMAEDe : lista) {
						if(planMAEDe.getIdHistorico() == null){ //para ingresar en las listas solo los registros actuales
							if(planMAEDe.getTipoPlan().equals("11")){
								listaPlanManejoAmbientalEIADetallePrevencion.add(planMAEDe);
							}
							if(planMAEDe.getTipoPlan().equals("12")){
								listaPlanManejoAmbientalEIADetalleContingencias.add(planMAEDe);
							}
							if(planMAEDe.getTipoPlan().equals("13")){
								listaPlanManejoAmbientalEIADetalleComunicacion.add(planMAEDe);
							}
							if(planMAEDe.getTipoPlan().equals("14")){
								listaPlanManejoAmbientalEIADetalleSeguridad.add(planMAEDe);
							}
							if(planMAEDe.getTipoPlan().equals("15")){
								listaPlanManejoAmbientalEIADetalleManejo.add(planMAEDe);
							}
							if(planMAEDe.getTipoPlan().equals("16")){
								listaPlanManejoAmbientalEIADetalleRelaciones.add(planMAEDe);
							}
							if(planMAEDe.getTipoPlan().equals("17")){
								listaPlanManejoAmbientalEIADetalleRehabilitacion.add(planMAEDe);
							}
							if(planMAEDe.getTipoPlan().equals("18")){
								listaPlanManejoAmbientalEIADetalleAbandono.add(planMAEDe);
							}
						}
					}
					
					//MarielaG para buscar informacion original
					if (existeObservaciones) {
//						if (!promotor.equals(loginBean.getNombreUsuario())) {
							consultarRegistrosOriginales(lista);
//						}
					}
				}
				
			}
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	/**
	 * Guarda la lista de Análisis de Riesgo ingresadas
	 * 
	 * @throws Exception
	 */
	public void guardar(String tipo) throws Exception {

		try {
			if(!esMineriaNoMetalicos)
			{
				if (validarListaPlanManejoAmbientalEIAEIADetalle()) {
							
					eliminarDetalle();
					//MarielaG para validar antes de guardar si existe el plan de manejo ambiental
					if (planManejoAmbientalEIA.getId() == null) {
						List<PlanManejoAmbientalEIA> listaPlanManejoAmbientalEIA = planManejoAmbientalEIAFacade.listarPorEIA(estudioImpactoAmbiental);
						if (listaPlanManejoAmbientalEIA.size() > 0) {
							this.planManejoAmbientalEIA = this.listaPlanManejoAmbientalEIA.get(0);
						}
					}

					if (planManejoAmbientalEIA.getId() == null) {
						planManejoAmbientalEIA = new PlanManejoAmbientalEIA();
						planManejoAmbientalEIA.setEia(estudioImpactoAmbiental);
						planManejoAmbientalEIA.setTipo("EIA");
						planManejoAmbientalEIA.setPmaReferencia(null);
						planManejoAmbientalEIA.setFechaRegistro(new Date());
						planManejoAmbientalEIA.setPlanManejoAmbientalEIADetalle(generalistaPlanManejoAmbientalEIAEIADetalle(planManejoAmbientalEIA));
					} else {
						planManejoAmbientalEIA.setPlanManejoAmbientalEIADetalle(generalistaPlanManejoAmbientalEIAEIADetalle(planManejoAmbientalEIA));
					}

					//eliminarDetalle();
					planManejoAmbientalEIAFacade.guardar(this.planManejoAmbientalEIA, null);

					cargarDatos();
					actualizarEstadoValidacionSeccion();

					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
					
				}
			}else{
				if (documento != null && documento.getContenidoDocumento() != null) {
					documento.setIdTable(this.obtenerEIA().getId());
					documento.setDescripcion("Adjunto");
					documento.setEstado(true);
					Documento documentoGuardado = documentosFacade.guardarDocumentoAlfresco(this.obtenerEIA().getProyectoLicenciamientoAmbiental()
							.getCodigo(), Constantes.CARPETA_EIA, 0L, documento, TipoDocumentoSistema.PLAN_MANEJO_AMBIENTAL, null);
					
					if(existeObservaciones && documentoHistorico != null){
						documentoHistorico.setIdHistorico(documentoGuardado.getId());
						documentoHistorico.setNumeroNotificacion(numeroNotificaciones);
						documentosFacade.actualizarDocumento(documentoHistorico);
					}
					
					actualizarEstadoValidacionSeccion();
					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
					
					JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/pma/planManejoAmbiental.jsf?id=15");
				} else {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_NO_EXISTE_DOCUMENTO);
					return;
				}
			}
				
			} catch (ServiceException e) {
				LOG.error(e, e);
				JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
			} catch (RuntimeException e) {
				LOG.error(e, e);
				JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
			}
		

	}

	public boolean validarListaPlanManejoAmbientalEIAEIADetalle() {

		if (listaPlanManejoAmbientalEIADetallePrevencion.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;
		}
		if (listaPlanManejoAmbientalEIADetalleContingencias.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;
		}
		if (listaPlanManejoAmbientalEIADetalleComunicacion.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;

		}

		if (listaPlanManejoAmbientalEIADetalleSeguridad.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;
		}

		if (listaPlanManejoAmbientalEIADetalleManejo.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;
		}
		if (listaPlanManejoAmbientalEIADetalleRelaciones.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;

		}
		if (listaPlanManejoAmbientalEIADetalleRehabilitacion.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;
		}
		if (listaPlanManejoAmbientalEIADetalleAbandono.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;
		}

		return true;
	}

	
	/**
	 * Eliminar detalle
	 */
	List<PlanManejoAmbientalEIADetalle> listaDetalle;
	private void eliminarDetalle()
	{
		try {
			if(existeObservaciones){
				listaDetalle = new ArrayList<PlanManejoAmbientalEIADetalle>();
				for (PlanManejoAmbientalEIADetalle detalleEliminar : listaPlanManejoAmbientalEIADetallleEliminados) {
					
					List<PlanManejoAmbientalEIADetalle> listaHistorico = planManejoAmbientalEIADetalleFacade.obtenerHistoricoList(detalleEliminar.getId(), numeroNotificaciones);			

					if (listaHistorico == null && detalleEliminar.getId() != null) {
						PlanManejoAmbientalEIADetalle detalleHistorico = detalleEliminar.clone();
						detalleHistorico.setIdHistorico(detalleEliminar.getId());
						detalleHistorico.setFechaCreacion(new Date());
						//detalleHistorico.setPlanManejoAmbientalEIA(planManejoAmbientalEIA);
						detalleHistorico.setNumeroNotificacion(numeroNotificaciones);	
						
						listaDetalle.add(detalleHistorico);
						//planManejoAmbientalEIADetalleFacade.guardarEliminadosHistorico(detalleHistorico);
					}
				}
			}
			
			planManejoAmbientalEIADetalleFacade.eliminar(this.listaPlanManejoAmbientalEIADetallleEliminados);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Generar lista de Planes de manejo ambiental
	 * 
	 * @param pmaEIA
	 * @return
	 * @throws ServiceException 
	 */
	public List<PlanManejoAmbientalEIADetalle> generalistaPlanManejoAmbientalEIAEIADetalle(PlanManejoAmbientalEIA pmaEIA) throws ServiceException {

		List<PlanManejoAmbientalEIADetalle> detalleGeneral = new ArrayList<PlanManejoAmbientalEIADetalle>();
		List<PlanManejoAmbientalEIADetalle> detalleGeneralBdd = planManejoAmbientalEIADetalleFacade.listarPorTipoEia(planManejoAmbientalEIA);

		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetallePrevencion) {
			if(existeObservaciones){
				PlanManejoAmbientalEIADetalle detalleHistorico = obtenerHistoricoLista(detalle, detalleGeneralBdd);
				if(detalleHistorico != null){
					detalleHistorico.setPlanManejoAmbientalEIA(pmaEIA);
					detalleGeneral.add(detalleHistorico);
				}else{
					if(detalle.getId() == null)
						detalle.setNumeroNotificacion(numeroNotificaciones);
				}
			}
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleContingencias) {
			if(existeObservaciones){
				PlanManejoAmbientalEIADetalle detalleHistorico = obtenerHistoricoLista(detalle, detalleGeneralBdd);
				if(detalleHistorico != null){
					detalleHistorico.setPlanManejoAmbientalEIA(pmaEIA);
					detalleGeneral.add(detalleHistorico);
				}else{
					if(detalle.getId() == null)
						detalle.setNumeroNotificacion(numeroNotificaciones);
				}
			}
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleComunicacion) {
			if(existeObservaciones){
				PlanManejoAmbientalEIADetalle detalleHistorico = obtenerHistoricoLista(detalle, detalleGeneralBdd);
				if(detalleHistorico != null){
					detalleHistorico.setPlanManejoAmbientalEIA(pmaEIA);
					detalleGeneral.add(detalleHistorico);
				}else{
					if(detalle.getId() == null)
						detalle.setNumeroNotificacion(numeroNotificaciones);
				}
			}
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleSeguridad) {
			if(existeObservaciones){
				PlanManejoAmbientalEIADetalle detalleHistorico = obtenerHistoricoLista(detalle, detalleGeneralBdd);
				if(detalleHistorico != null){
					detalleHistorico.setPlanManejoAmbientalEIA(pmaEIA);
					detalleGeneral.add(detalleHistorico);
				}else{
					if(detalle.getId() == null)
						detalle.setNumeroNotificacion(numeroNotificaciones);
				}
			}
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleManejo) {
			if(existeObservaciones){
				PlanManejoAmbientalEIADetalle detalleHistorico = obtenerHistoricoLista(detalle, detalleGeneralBdd);
				if(detalleHistorico != null){
					detalleHistorico.setPlanManejoAmbientalEIA(pmaEIA);
					detalleGeneral.add(detalleHistorico);
				}else{
					if(detalle.getId() == null)
						detalle.setNumeroNotificacion(numeroNotificaciones);
				}
			}
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleRelaciones) {
			if(existeObservaciones){
				PlanManejoAmbientalEIADetalle detalleHistorico = obtenerHistoricoLista(detalle, detalleGeneralBdd);
				if(detalleHistorico != null){
					detalleHistorico.setPlanManejoAmbientalEIA(pmaEIA);
					detalleGeneral.add(detalleHistorico);
				}else{
					if(detalle.getId() == null)
						detalle.setNumeroNotificacion(numeroNotificaciones);
				}
			}
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleRehabilitacion) {
			if(existeObservaciones){
				PlanManejoAmbientalEIADetalle detalleHistorico = obtenerHistoricoLista(detalle, detalleGeneralBdd);
				if(detalleHistorico != null){
					detalleHistorico.setPlanManejoAmbientalEIA(pmaEIA);
					detalleGeneral.add(detalleHistorico);
				}else{
					if(detalle.getId() == null)
						detalle.setNumeroNotificacion(numeroNotificaciones);
				}
			}
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleAbandono) {
			if(existeObservaciones){
				PlanManejoAmbientalEIADetalle detalleHistorico = obtenerHistoricoLista(detalle, detalleGeneralBdd);
				if(detalleHistorico != null){
					detalleHistorico.setPlanManejoAmbientalEIA(pmaEIA);
					detalleGeneral.add(detalleHistorico);
				}else{
					if(detalle.getId() == null)
						detalle.setNumeroNotificacion(numeroNotificaciones);
				}
			}
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		
		if(listaDetalle != null && !listaDetalle.isEmpty()){
			for(PlanManejoAmbientalEIADetalle detalle : listaDetalle){
				detalle.setPlanManejoAmbientalEIA(pmaEIA);
				detalleGeneral.add(detalle);
			}
		}

		return detalleGeneral;
	}

	/**
	 * Asigna la sección válida
	 * 
	 * @throws ServiceException
	 * @throws CmisAlfrescoException
	 */
	private void actualizarEstadoValidacionSeccion() throws ServiceException, CmisAlfrescoException {
		validacionesSeccionesFacade.guardarValidacionSeccion("EIA", "planManejoAmbiental", estudioImpactoAmbiental
				.getId().toString());

	}

	/**
	 * Prepara la edición de un plan de manejo ambiental
	 * 
	 * @param analisisDeRiesgo
	 */
	public void editarPlanManejoAmbientalEIADetalle(String tipoTabla,
			PlanManejoAmbientalEIADetalle planManejoAmbientalEIADetalle) {

		if (tipoTabla.equals("11")) {
			this.opcion2 = false;
			this.tipoTabla = "11";
		}
		if (tipoTabla.equals("12")) {
			this.opcion2 = true;
			this.tipoTabla = "12";
		}
		if (tipoTabla.equals("13")) {
			this.opcion2 = false;
			this.tipoTabla = "13";
		}
		if (tipoTabla.equals("14")) {
			this.opcion2 = false;
			this.tipoTabla = "14";
		}
		if (tipoTabla.equals("15")) {
			this.opcion2 = false;
			this.tipoTabla = "15";
		}
		if (tipoTabla.equals("16")) {
			this.opcion2 = false;
			this.tipoTabla = "16";
		}
		if (tipoTabla.equals("17")) {
			this.opcion2 = false;
			this.tipoTabla = "17";
		}
		if (tipoTabla.equals("18")) {
			this.opcion2 = false;
			this.tipoTabla = "18";
		}

		this.planManejoAmbientalEIADetalle = planManejoAmbientalEIADetalle;
		this.isEditing = true;

	}

	/**
	 * Inicialización de variables del Plan Manejo Ambiental
	 */
	public void inicializarPlanManejoAmbiental(String tipo) {

		if (tipo.equals("11")) {
			opcion2 = false;
			this.tipoTabla = tipo;
		}
		if (tipo.equals("12")) {
			opcion2 = true;
			this.tipoTabla = tipo;
		}
		if (tipo.equals("13")) {
			opcion2 = false;
			this.tipoTabla = tipo;
		}
		if (tipo.equals("14")) {
			opcion2 = false;
			this.tipoTabla = tipo;
		}
		if (tipo.equals("15")) {
			opcion2 = false;
			this.tipoTabla = tipo;
		}
		if (tipo.equals("16")) {
			opcion2 = false;
			this.tipoTabla = tipo;
		}
		if (tipo.equals("17")) {
			opcion2 = false;
			this.tipoTabla = tipo;
		}
		if (tipo.equals("18")) {
			opcion2 = false;
			this.tipoTabla = tipo;
		}

		this.planManejoAmbientalEIADetalle = new PlanManejoAmbientalEIADetalle();
		//this.planManejoAmbientalEIADetalle.setMedidaPropuesta(null);
		this.planManejoAmbientalEIADetalle.setDetalleEvaluacionLista(null);

		this.isEditing = false;
		JsfUtil.addCallbackParam("planManejoAmbiental");
	}

	/**
	 * Agregar Plan Manejo Ambiental
	 */
	public void agregarPlanManejoAmbientalEIADetallePrevencion() {

		this.planManejoAmbientalEIADetalle.setTipoPlan(tipoTabla);

		if (tipoTabla.equals("11")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetallePrevencion.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("12")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleContingencias.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("13")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleComunicacion.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("14")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleSeguridad.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("15")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleManejo.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("16")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleRelaciones.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("17")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleRehabilitacion.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("18")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleAbandono.add(this.planManejoAmbientalEIADetalle);
			}
		}

		JsfUtil.addCallbackParam("planManejoAmbiental");
		RequestContext.getCurrentInstance().execute(
				"PF('dlg3').hide();");
	}

	/**
	 * Elimina un Plan Manejo Ambiental
	 * 
	 * @param planManejoAmbiental
	 */
	public void removerPlanManejoAmbiental(String tipoTabla, PlanManejoAmbientalEIADetalle planManejoAmbientalEIADetalle) {
		this.isEditing = false;

		if (tipoTabla.equals("11")) {
			this.listaPlanManejoAmbientalEIADetallePrevencion.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("12")) {
			this.listaPlanManejoAmbientalEIADetalleContingencias.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("13")) {
			this.listaPlanManejoAmbientalEIADetalleComunicacion.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("14")) {
			this.listaPlanManejoAmbientalEIADetalleSeguridad.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("15")) {
			this.listaPlanManejoAmbientalEIADetalleManejo.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("16")) {
			this.listaPlanManejoAmbientalEIADetalleRelaciones.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("17")) {
			this.listaPlanManejoAmbientalEIADetalleRehabilitacion.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("18")) {
			this.listaPlanManejoAmbientalEIADetalleAbandono.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
	}

	public void cancelar() {
		JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/pma/planManejoAmbiental.jsf");
	}
	
	public void descargarPlantilla() throws CmisAlfrescoException, IOException {

		JsfUtil.descargarMimeType(documentosFacade.descargarDocumentoPorNombre("Plantilla_Plan_Manejo_Ambiental_Mineria_No_Metalicos.xls"),
				"Plantilla_Plan_Manejo_Ambiental_Mineria_No_Metalicos.xls","xls", "application/vnd.ms-excel");

	}
	
	public void uploadListenerDocumentosEIA(FileUploadEvent event) {
		documento = this.uploadListener(event, EstudioImpactoAmbiental.class, "xls");
		this.existeDocumentoAdjunto = true;		
	}
	
	private Documento uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = crearDocumento(contenidoDocumento, clazz, extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}
	
	public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
		Documento documento = new Documento();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setIdTable(0);
		documento.setExtesion("." + extension);

		documento.setMime("application/vnd.ms-excel");
		return documento;
	}
	
	public StreamedContent getStreamContent() throws Exception {
		DefaultStreamedContent content = null;
		try {
			
			if (documento != null && documento.getNombre() != null
					&& documento.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documento.getContenidoDocumento()));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}
	
	private EstudioImpactoAmbiental obtenerEIA() {
		EstudioImpactoAmbiental es = (EstudioImpactoAmbiental) JsfUtil
				.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);

		return es;
	}
	
	private void cargarAdjuntosEIA(TipoDocumentoSistema tipoDocumento, String tipo) throws CmisAlfrescoException {

		List<Documento> documentosXEIA = documentosFacade.documentosTodosXTablaIdXIdDoc(this.obtenerEIA().getId(),
				"EstudioImpactoAmbiental", tipoDocumento);

		if (documentosXEIA.size() > 0) {

			if (tipo.equalsIgnoreCase("Adjunto")) {
				this.documento = documentosXEIA.get(0);
				this.descargarAlfresco(this.documento);
				this.existeDocumentoAdjunto = true;
				
				if(existeObservaciones){
					documentoHistorico = validarDocumentoHistorico(documento, documentosXEIA);
//					if(!promotor.equals(loginBean.getNombreUsuario())){
						consultarDocumentosOriginales(documentosXEIA.get(0).getId(), documentosXEIA);
//					}
				}

			} 
		}
	}
	
	public Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}
	
	/**
	 * CF: Historico
	 */
		
	private PlanManejoAmbientalEIADetalle obtenerHistoricoLista(PlanManejoAmbientalEIADetalle detalle, List<PlanManejoAmbientalEIADetalle> listaDetalleBdd){	
		
		try {
			
			PlanManejoAmbientalEIADetalle detalleHistorico = null;
			
			Comparator<PlanManejoAmbientalEIADetalle> c = new Comparator<PlanManejoAmbientalEIADetalle>() {
				
				@Override
				public int compare(PlanManejoAmbientalEIADetalle o1, PlanManejoAmbientalEIADetalle o2) {
					return o1.getId().compareTo(o2.getId());
				}
			}; 
			
			Collections.sort(listaDetalleBdd, c);
			
			if(detalle.getId() != null){
				int index = Collections.binarySearch(listaDetalleBdd, new PlanManejoAmbientalEIADetalle(detalle.getId()), c);
				
				if(index >= 0){
					PlanManejoAmbientalEIADetalle detalleBdd = listaDetalleBdd.get(index);
					
					if(((detalle.getComponenteAmbiental() == null && detalleBdd.getComponenteAmbiental() == null) || detalle.getComponenteAmbiental() != null && 
							detalleBdd.getComponenteAmbiental() != null && detalle.getComponenteAmbiental().equals(detalleBdd.getComponenteAmbiental())) && 
						((detalle.getFrecuencia() == null && detalleBdd.getFrecuencia() == null) || detalle.getFrecuencia() != null && 
							detalleBdd.getFrecuencia() != null && detalle.getFrecuencia().equals(detalleBdd.getFrecuencia())) && 
						((detalle.getIndicador() == null && detalleBdd.getIndicador() == null) || 
							detalle.getIndicador() != null && detalleBdd.getIndicador() != null && detalle.getIndicador().equals(detalleBdd.getIndicador())) && 
						((detalle.getMedidaPropuesta() == null && detalleBdd.getMedidaPropuesta() == null) || detalle.getMedidaPropuesta() != null && 
							detalleBdd.getMedidaPropuesta() != null && detalle.getMedidaPropuesta().equals(detalleBdd.getMedidaPropuesta())) && 
						((detalle.getMedioVerificacion() == null && detalleBdd.getMedioVerificacion() == null) || detalle.getMedioVerificacion() != null && 
							detalleBdd.getMedioVerificacion() != null && detalle.getMedioVerificacion().equals(detalleBdd.getMedioVerificacion())) && 
						((detalle.getPeriodo() == null && detalleBdd.getPeriodo() == null) || detalle.getPeriodo() != null && 
							detalleBdd.getPeriodo() != null && detalle.getPeriodo().equals(detalleBdd.getPeriodo())) && 
						((detalle.getResponsable() == null && detalleBdd.getResponsable() == null) || detalle.getResponsable() != null && 
							detalleBdd.getResponsable() != null && detalle.getResponsable().equals(detalleBdd.getResponsable())) && 
						((detalle.getRiesgo() == null && detalleBdd.getRiesgo() == null) || detalle.getRiesgo() != null && 
							detalleBdd.getRiesgo() != null && detalle.getRiesgo().equals(detalleBdd.getRiesgo())) && 
						((detalle.getTipoPlan() == null && detalleBdd.getTipoPlan() == null) || detalle.getTipoPlan() != null && 
							detalleBdd.getTipoPlan() != null && detalle.getTipoPlan().equals(detalleBdd.getTipoPlan()))){
						//son iguales
					}else{
						if(detalle.getNumeroNotificacion() == null || (detalle.getNumeroNotificacion() != null && detalle.getNumeroNotificacion() < numeroNotificaciones)){
							
							List<PlanManejoAmbientalEIADetalle> listaHistorico = planManejoAmbientalEIADetalleFacade.obtenerHistoricoList(detalle.getId(), numeroNotificaciones);
							if(listaHistorico == null){
								detalleHistorico = detalleBdd.clone();
								detalleHistorico.setIdHistorico(detalle.getId());
								detalleHistorico.setFechaCreacion(new Date());
								detalleHistorico.setNumeroNotificacion(numeroNotificaciones);									
							}
						}					
					}					
				}
			}			
			return detalleHistorico;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	/**
	 * Cris F: Documento Historico Proyecto Minero
	 * @param documentoIngresado
	 * @return
	 */
	private Documento validarDocumentoHistorico(Documento documentoIngresado, List<Documento> documentosXEIA){		
		try {
			List<Documento> documentosList = new ArrayList<>();
			for(Documento documento : documentosXEIA){
				if(documento.getIdHistorico() != null && 
						documento.getIdHistorico().equals(documentoIngresado.getId()) && 
						documento.getNumeroNotificacion().equals(numeroNotificaciones)){
					documentosList.add(documento);
				}
			}			
			
			if(documentosList != null && !documentosList.isEmpty()){		        
				return documentosList.get(0);
			}else{
				return documentoIngresado;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return documentoIngresado;
		}		
	}
	
	/**
	 * MarielaG
	 * Consultar los documentos originales
	 */
	private void consultarDocumentosOriginales(Integer idDocumento, List<Documento> documentosList){		
		try {
			listaDocumentoHistorico = new ArrayList<>();
			if (documentosList != null && !documentosList.isEmpty() && documentosList.size() > 1) {
				while (idDocumento > 0) {
					idDocumento = recuperarHistoricos(idDocumento, documentosList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Consultar los documentos historicos en bucle
	 */
	private int recuperarHistoricos(Integer idDocumento, List<Documento> documentosList) {
		try {
			int nextDocument = 0;
			for (Documento documento : documentosList) {
				if (documento.getIdHistorico() != null && documento.getIdHistorico().equals(idDocumento)) {
					nextDocument = documento.getId();
					listaDocumentoHistorico.add(0, documento);
				}
			}

			return nextDocument;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * MarielaG
	 * Recuperar los registros por tipo de plan
	 * @param lista
	 */
	private void consultarRegistrosOriginales(List<PlanManejoAmbientalEIADetalle> lista){
		List<PlanManejoAmbientalEIADetalle> listaPrevencion = new ArrayList<PlanManejoAmbientalEIADetalle>();
		List<PlanManejoAmbientalEIADetalle> listaContingencias = new ArrayList<PlanManejoAmbientalEIADetalle>();
		List<PlanManejoAmbientalEIADetalle> listaComunicacion = new ArrayList<PlanManejoAmbientalEIADetalle>();
		List<PlanManejoAmbientalEIADetalle> listaSeguridad = new ArrayList<PlanManejoAmbientalEIADetalle>();
		List<PlanManejoAmbientalEIADetalle> listaManejo = new ArrayList<PlanManejoAmbientalEIADetalle>();
		List<PlanManejoAmbientalEIADetalle> listaRelaciones = new ArrayList<PlanManejoAmbientalEIADetalle>();
		List<PlanManejoAmbientalEIADetalle> listaRehabilitacion = new ArrayList<PlanManejoAmbientalEIADetalle>();
		List<PlanManejoAmbientalEIADetalle> listaAbandono = new ArrayList<PlanManejoAmbientalEIADetalle>();
		
		listaPmaPrevencionOriginal = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPmaContingenciasOriginal = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPmaComunicacionOriginal = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPmaSeguridadOriginal = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPmaManejoOriginal = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPmaRelacionesOriginal = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPmaRehabilitacionOriginal = new ArrayList<PlanManejoAmbientalEIADetalle>();
		listaPmaAbandonoOriginal = new ArrayList<PlanManejoAmbientalEIADetalle>();
		
		for (PlanManejoAmbientalEIADetalle planMAEDe : lista) {
			if(planMAEDe.getTipoPlan().equals("11")){
				listaPrevencion.add(planMAEDe);
			}
			if(planMAEDe.getTipoPlan().equals("12")){
				listaContingencias.add(planMAEDe);
			}
			if(planMAEDe.getTipoPlan().equals("13")){
				listaComunicacion.add(planMAEDe);
			}
			if(planMAEDe.getTipoPlan().equals("14")){
				listaSeguridad.add(planMAEDe);
			}
			if(planMAEDe.getTipoPlan().equals("15")){
				listaManejo.add(planMAEDe);
			}
			if(planMAEDe.getTipoPlan().equals("16")){
				listaRelaciones.add(planMAEDe);
			}
			if(planMAEDe.getTipoPlan().equals("17")){
				listaRehabilitacion.add(planMAEDe);
			}
			if(planMAEDe.getTipoPlan().equals("18")){
				listaAbandono.add(planMAEDe);
			}
		}
		
		getOriginales(11, listaPrevencion, listaPlanManejoAmbientalEIADetallePrevencion);
		getOriginales(12, listaContingencias, listaPlanManejoAmbientalEIADetalleContingencias);
		getOriginales(13, listaComunicacion, listaPlanManejoAmbientalEIADetalleComunicacion);
		getOriginales(14, listaSeguridad, listaPlanManejoAmbientalEIADetalleSeguridad);
		getOriginales(15, listaManejo, listaPlanManejoAmbientalEIADetalleManejo);
		getOriginales(16, listaRelaciones, listaPlanManejoAmbientalEIADetalleRelaciones);
		getOriginales(17, listaRehabilitacion, listaPlanManejoAmbientalEIADetalleRehabilitacion);
		getOriginales(18, listaAbandono, listaPlanManejoAmbientalEIADetalleAbandono);
	}
	
	/**
	 * MarielaG
	 * Recuperar los registros originales y eliminados por tipo de plan
	 * @param tipo
	 * @param listaPlanManejoAmbientalEIADetalle
	 * @param listaPmaActuales
	 */
	private void getOriginales(Integer tipo, List<PlanManejoAmbientalEIADetalle>  listaPlanManejoAmbientalEIADetalle, List<PlanManejoAmbientalEIADetalle>  listaPmaActuales) {
		try {
			List<PlanManejoAmbientalEIADetalle> planesOriginales = new ArrayList<PlanManejoAmbientalEIADetalle>();
			List<PlanManejoAmbientalEIADetalle> planesEliminados = new ArrayList<>();
			int totalModificados = 0;

			for(PlanManejoAmbientalEIADetalle planBdd : listaPlanManejoAmbientalEIADetalle){
				//si es un registro que se ingreso originalmente
				//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
				if(planBdd.getNumeroNotificacion() == null ||
						!planBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
					boolean agregarItemLista = true;
	    			//buscar si tiene historial
					for (PlanManejoAmbientalEIADetalle planHistorico : listaPlanManejoAmbientalEIADetalle) {
						if (planHistorico.getIdHistorico() != null  
								&& planHistorico.getIdHistorico().equals(planBdd.getId())) {
							//si existe un registro historico, no se agrega a la lista en este paso
							agregarItemLista = false;
							planBdd.setRegistroModificado(true);
							break;
						}
					}
					if (agregarItemLista) {
						planesOriginales.add(planBdd);
					}
				} else {
					totalModificados++;
	    			//es una modificacion
	    			if(planBdd.getIdHistorico() == null && planBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
	    				//es un registro nuevo
	    				//no ingresa en el lista de originales
	    				planBdd.setNuevoEnModificacion(true);
	    			}else{
	    				planBdd.setRegistroModificado(true);
	    				if(!planesOriginales.contains(planBdd)){
	    					planesOriginales.add(planBdd);
	    				}
	    			}
	    		}
				
				//para consultar eliminados
				if (planBdd.getIdHistorico() != null
						&& planBdd.getNumeroNotificacion() != null) {
					boolean existePunto = false;
					for (PlanManejoAmbientalEIADetalle itemActual : listaPmaActuales) {
						if (itemActual.getId().equals(planBdd.getIdHistorico())) {
							existePunto = true;
							break;
						}
					}

					if (!existePunto) {
						planesEliminados.add(planBdd);
					}
				}
			}
			
			switch (tipo) {
			case 11:
				if (totalModificados > 0){
					listaPmaPrevencionOriginal = planesOriginales;
				}
				listaPmaPrevencionEliminadosBdd = planesEliminados;
				break;
			case 12:
				if (totalModificados > 0){
					listaPmaContingenciasOriginal = planesOriginales;
				}
				listaPmaContingenciasEliminadosBdd = planesEliminados;
				break;
			case 13:
				if (totalModificados > 0){
					listaPmaComunicacionOriginal = planesOriginales;
				}
				listaPmaComunicacionEliminadosBdd = planesEliminados;
				break;
			case 14:
				if (totalModificados > 0){
					listaPmaSeguridadOriginal = planesOriginales;
				}
				listaPmaSeguridadEliminadosBdd = planesEliminados;
				break;
			case 15:
				if (totalModificados > 0){
					listaPmaManejoOriginal = planesOriginales;
				}
				listaPmaManejoEliminadosBdd = planesEliminados;
				break;
			case 16:
				if (totalModificados > 0){
					listaPmaRelacionesOriginal = planesOriginales;
				}
				listaPmaRelacionesEliminadosBdd = planesEliminados;
				break;
			case 17:
				if (totalModificados > 0){
					listaPmaRehabilitacionOriginal = planesOriginales;
				}
				listaPmaRehabilitacionEliminadosBdd = planesEliminados;
				break;
			case 18:
				if (totalModificados > 0){
					listaPmaAbandonoOriginal = planesOriginales;
				}
				listaPmaAbandonoEliminadosBdd = planesEliminados;
				break;

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Para seleccionar el registro original
	 */
	public void mostrarRegistroOriginal(String tipoTabla, PlanManejoAmbientalEIADetalle planManejoAmbiental) {
		List<PlanManejoAmbientalEIADetalle> planesOriginales = new ArrayList<PlanManejoAmbientalEIADetalle>();
		
		this.opcion2 = false;
		
		if (tipoTabla.equals("11")) {
			planesOriginales = listaPmaPrevencionOriginal;
		}
		if (tipoTabla.equals("12")) {
			this.opcion2 = true;
			planesOriginales = listaPmaContingenciasOriginal;
		}
		if (tipoTabla.equals("13")) {
			planesOriginales = listaPmaComunicacionOriginal;
		}
		if (tipoTabla.equals("14")) {
			planesOriginales = listaPmaSeguridadOriginal;
		}
		if (tipoTabla.equals("15")) {
			planesOriginales = listaPmaManejoOriginal;
		}
		if (tipoTabla.equals("16")) {
			planesOriginales = listaPmaRelacionesOriginal;
		}
		if (tipoTabla.equals("17")) {
			planesOriginales = listaPmaRehabilitacionOriginal;
		}
		if (tipoTabla.equals("18")) {
			planesOriginales = listaPmaAbandonoOriginal;
		}

		this.planManejoAmbientalEIADetalle = planManejoAmbiental;
		
		listaPmaHistorico = new ArrayList<PlanManejoAmbientalEIADetalle>();
		
		for(PlanManejoAmbientalEIADetalle planOriginal : planesOriginales){
			if(planOriginal.getIdHistorico() != null && planManejoAmbiental.getId().equals(planOriginal.getIdHistorico())){
				listaPmaHistorico.add(planOriginal);
			}
		}
	}
	
	/**
	 * MarielaG
	 * Para seleccionar el registro original
	 */
	public void fillPlanesEliminados(String tipoTabla) {
		listaPmaHistorico = new ArrayList<PlanManejoAmbientalEIADetalle>();
		
		switch (tipoTabla) {
		case "11":
			listaPmaHistorico = listaPmaPrevencionEliminadosBdd;
			break;
		case "12":
			listaPmaHistorico = listaPmaContingenciasEliminadosBdd;
			break;
		case "13":
			listaPmaHistorico = listaPmaComunicacionEliminadosBdd;
			break;
		case "14":
			listaPmaHistorico = listaPmaSeguridadEliminadosBdd;
			break;
		case "15":
			listaPmaHistorico = listaPmaManejoEliminadosBdd;
			break;
		case "16":
			listaPmaHistorico = listaPmaRelacionesEliminadosBdd;
			break;
		case "17":
			listaPmaHistorico = listaPmaRehabilitacionEliminadosBdd;
			break;
		case "18":
			listaPmaHistorico = listaPmaAbandonoEliminadosBdd;
			break;

		default:
			break;
		}
	}
	
	/**
	 * MarielaG
	 * Para descargar documentos originales
	 */
	public StreamedContent getStreamContentOriginal(Documento documento) throws Exception {
        DefaultStreamedContent content = null;
        try {
        	this.documentoOriginal = this.descargarAlfresco(documento);
        	if (documentoOriginal != null && documentoOriginal.getNombre() != null
					&& documentoOriginal.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoOriginal.getContenidoDocumento()));
				content.setName(documentoOriginal.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
	
}
