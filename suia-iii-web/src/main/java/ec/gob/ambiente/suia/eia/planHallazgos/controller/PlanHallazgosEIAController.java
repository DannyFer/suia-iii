package ec.gob.ambiente.suia.eia.planHallazgos.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.SelectEvent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.IdentificacionHallazgosEia;
import ec.gob.ambiente.suia.domain.PlanHallazgosEia;
import ec.gob.ambiente.suia.eia.identificacionHallazgos.facade.IdentificacionHallazgosEiaFacade;
import ec.gob.ambiente.suia.eia.planHallazgos.facade.PlanHallazgosEiaFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * @author Oscar Campana
 */
@ManagedBean
@ViewScoped
public class PlanHallazgosEIAController implements Serializable {

	private static final long serialVersionUID = 1572525482381028668L;
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(PlanHallazgosEIAController.class);

	@EJB
	private ValidacionSeccionesFacade validacionesSeccionesFacade;

	@Getter
	@Setter
	private Documento documentoGeneral;

	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@EJB
	private DocumentosFacade documentosFacade;
	

	/*************************/
	// ///////////

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	@Setter
	PlanHallazgosEia planHallazgosEia;

	@Getter
	@Setter
	List<PlanHallazgosEia> listaPlanHallazgos, listaPlanHallazgosOriginales, listaPlanHallazgosHistorico, listaPlanHallazgosEliminadosBdd;
	
	@Getter
	@Setter
	List<IdentificacionHallazgosEia> listaIdentificacionHallazgos;

	@Getter
	@Setter
	List<PlanHallazgosEia> listaPlanHallazgosEliminados;

	@EJB
	private PlanHallazgosEiaFacade planHallazgosEiaFacade;
	
	@EJB
	private IdentificacionHallazgosEiaFacade identificacionHallazgosEiaFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;


	@Setter
	@Getter
	private boolean isEditing;

	// //******************/////////////////////////
	
	private Map<String, Object> processVariables;
	private Integer numeroNotificaciones;
	private boolean existeObservaciones;
	private String promotor;
	
	@Setter
	@Getter
	private Integer totalPlanesModificados;
	
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

	@PostConstruct
	private void postInit() throws CmisAlfrescoException, JbpmException {
		
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
//		promotor = (String) processVariables.get("u_Promotor");
		
		if(numeroNotificaciones > 0)
			existeObservaciones = true;
		//Fin CF	
		
		estudioImpactoAmbiental = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
		cargarDatos();

	}

	private void cargarDatos() throws CmisAlfrescoException {

		this.isEditing = false;
		planHallazgosEia = new PlanHallazgosEia();
		estudioImpactoAmbiental = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
		listaPlanHallazgos = new ArrayList<PlanHallazgosEia>();
		listaIdentificacionHallazgos = new ArrayList<IdentificacionHallazgosEia>();
		listaPlanHallazgosEliminados = new ArrayList<PlanHallazgosEia>();

		
		try {
			List<PlanHallazgosEia> listaPlanHallazgosEia = new ArrayList<PlanHallazgosEia>();
			listaPlanHallazgosEia = planHallazgosEiaFacade.listarTodosRegistrosPorEIA(estudioImpactoAmbiental);
			for (PlanHallazgosEia planHallazgo : listaPlanHallazgosEia) {
				if(planHallazgo.getIdHistorico() == null){
					this.listaPlanHallazgos.add(planHallazgo); 
				}
			}
			
			this.listaIdentificacionHallazgos = identificacionHallazgosEiaFacade.listarPorEIA(estudioImpactoAmbiental); 
				
			
			//MarielaG para buscar informacion original
			if (existeObservaciones) {
//				if (!promotor.equals(loginBean.getNombreUsuario())) {
					consultarPlanOriginal(listaPlanHallazgosEia);
//				}
			}

		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	/**
	 * Guarda la lista de Análisis de Riesgo ingresadas y el documento al Alfresco
	 * 
	 * @throws CmisAlfrescoException
	 */
	public void guardar() throws CmisAlfrescoException {
		try {
			
			if(existeObservaciones){
				planHallazgosEiaFacade.guardarHistorico(listaPlanHallazgos,listaPlanHallazgosEliminados, numeroNotificaciones);
			}else{
				planHallazgosEiaFacade.guardar(listaPlanHallazgos,listaPlanHallazgosEliminados);
			}
			//this.salvarDocumento();
			cargarDatos();
			if (this.listaPlanHallazgos.size() > 0) {
				this.actualizarEstadoValidacionSeccion();
			}
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
			JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/planHallazgos/planHallazgos.jsf?id=19");
			
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
		} catch (RuntimeException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
		}

	}

	/**
	 * Asigna la sección válida
	 * 
	 * @throws ServiceException
	 * @throws CmisAlfrescoException
	 */
	private void actualizarEstadoValidacionSeccion() throws ServiceException, CmisAlfrescoException {
		validacionesSeccionesFacade.guardarValidacionSeccion("EIA", "planHallazgos", estudioImpactoAmbiental
				.getId().toString());

	}

	/**
	 * Prepara la edición de un hallazgo
	 * 
	 * @param analisisDeRiesgo
	 */
	public void editarPlan(PlanHallazgosEia hallazgo) {

		this.planHallazgosEia = hallazgo;
		this.isEditing = true;
		
	}

	/**
	 * Inicialización de variables de la Identificacion de hallazgos
	 */
	public void inicializarPlan() {

		this.planHallazgosEia = new PlanHallazgosEia();

		this.planHallazgosEia.setEia(this.estudioImpactoAmbiental);
		this.planHallazgosEia.setHallazgos(new IdentificacionHallazgosEia());
		
		this.isEditing = false;
		JsfUtil.addCallbackParam("hallazgo");

	}

	/**
	 * Agregar hallazgo a la lista Hallazgos
	 */
	public void agregarPlan() {
		if (!this.isEditing) {
			this.listaPlanHallazgos.add(this.planHallazgosEia);
		}
		JsfUtil.addCallbackParam("hallazgo");
	}

	/**
	 * Elimina una Identificacion de hallazgo de la tabla
	 * 
	 * @param hallazgo
	 *            Identificacion de hallazgo a eliminar
	 */
	public void removerPlan(PlanHallazgosEia hallazgo) {
		this.isEditing = false;
		this.listaPlanHallazgos.remove(hallazgo);
		this.listaPlanHallazgosEliminados.add(hallazgo);

	}
	
	public void validarFechaIni(SelectEvent event)
	{
		this.planHallazgosEia.setFechaFin(null);
	}
	
	public void validarFechaFin(SelectEvent event)
	{
		this.planHallazgosEia.setFechaInicio(null);
	}
		
	/**
	 * MarielaG
	 * Consultar el listado de hallazgos ingresado antes de las correcciones
	 */
	private void consultarPlanOriginal(List<PlanHallazgosEia>  listaPlanHallazgosEia) {
		try {
			List<PlanHallazgosEia> planesOriginales = new ArrayList<PlanHallazgosEia>();
			List<PlanHallazgosEia> planesEliminados = new ArrayList<>();
			totalPlanesModificados = 0;
			
			for(PlanHallazgosEia planHallazgoBdd : listaPlanHallazgosEia){
				//si es un registro que se ingreso originalmente
				//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
				if(planHallazgoBdd.getNumeroNotificacion() == null ||
						!planHallazgoBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
					boolean agregarItemLista = true;
	    			//buscar si tiene historial
					for (PlanHallazgosEia planHistorico : listaPlanHallazgosEia) {
						if (planHistorico.getIdHistorico() != null  
								&& planHistorico.getIdHistorico().equals(planHallazgoBdd.getId())) {
							//si existe un registro historico, no se agrega a la lista en este paso
							agregarItemLista = false;
							planHallazgoBdd.setRegistroModificado(true);
							break;
						}
					}
					if (agregarItemLista) {
						planesOriginales.add(planHallazgoBdd);
					}
				} else {
					totalPlanesModificados++;
	    			//es una modificacion
	    			if(planHallazgoBdd.getIdHistorico() == null && planHallazgoBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
	    				//es un registro nuevo
	    				//no ingresa en el lista de originales
	    				planHallazgoBdd.setNuevoEnModificacion(true);
	    			}else{
	    				planHallazgoBdd.setRegistroModificado(true);
	    				if(!planesOriginales.contains(planHallazgoBdd)){
	    					planesOriginales.add(planHallazgoBdd);
	    				}
	    			}
	    		}
				
				//para consultar eliminados
				if (planHallazgoBdd.getIdHistorico() != null
						&& planHallazgoBdd.getNumeroNotificacion() != null) {
					boolean existePunto = false;
					for (PlanHallazgosEia itemActual : this.listaPlanHallazgos) {
						if (itemActual.getId().equals(planHallazgoBdd.getIdHistorico())) {
							existePunto = true;
							break;
						}
					}

					if (!existePunto) {
						planesEliminados.add(planHallazgoBdd);
					}
				}
			}
			
			if (totalPlanesModificados > 0){
				this.listaPlanHallazgosOriginales = planesOriginales;
			}
			
			listaPlanHallazgosEliminadosBdd = planesEliminados; 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Para seleccionar el hallazgo original mostrarPlanesOriginales
	 */
	/**
	 * MarielaG
	 * Para mostrar el historial de cambios del hallazgo
	 */
	public void mostrarPlanesOriginales(PlanHallazgosEia plan) {
		listaPlanHallazgosHistorico = new ArrayList<>();
		
		for(PlanHallazgosEia planOriginal : listaPlanHallazgosOriginales){
			if(planOriginal.getIdHistorico() != null && plan.getId().equals(planOriginal.getIdHistorico())){
				listaPlanHallazgosHistorico.add(planOriginal);
			}
		}
	}
	
	/**
	 * MarielaG 
	 * Para mostrar los planes eliminados
	 */
	public void fillPlanesEliminados() {
		listaPlanHallazgosHistorico = listaPlanHallazgosEliminadosBdd;
	}
}
