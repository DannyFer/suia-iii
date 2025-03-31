package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AgregarSustanciasQuimicasBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.SustanciaQuimicaPeligrosaBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.RequisitosVehiculoFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.SustanciasQuimicaPeligrosaTransporteFacade;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.SustanciaQuimicaPeligrosaTransporte;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.SustanciaQuimicaPeligrosaTransporteUbicacionGeografica;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.SerializationUtils;
import org.primefaces.event.FileUploadEvent;

/**
 * 
 * <b> Clase controlador para las sustancias quimicas peligrosas. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 22/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class SustanciaQuimicaPeligrosaController implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(SustanciaQuimicaPeligrosaController.class);

	public static final String HIDROCARBURO = "esHidrocarburo";

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	@Setter
	@Getter
	@ManagedProperty(value = "#{sustanciaQuimicaPeligrosaBean}")
	private SustanciaQuimicaPeligrosaBean sustanciaQuimicaPeligrosaBean;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private RequisitosVehiculoFacade requisitosVehiculoFacade;

	@EJB
	private SustanciasQuimicaPeligrosaTransporteFacade sustanciasQuimicaPeligrosaTransporteFacade;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@Setter
	@Getter
	@ManagedProperty(value = "#{agregarSustanciasQuimicasBean}")
	private AgregarSustanciasQuimicasBean agregarSustanciasQuimicasBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@Setter
	@Getter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	public boolean tipoEstudioExPost, formVisualizar;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	@PostConstruct
	public void init() {
		try {
			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos();
			
			HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	        String url = origRequest.getRequestURL().toString();
	        if (url.contains("Ver")) {
	        	formVisualizar = true;
	        }
	        
			if (existeIngresoVehiculo()) {
				Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
						bandejaTareasBean.getTarea().getProcessInstanceId());

				if (variables.containsKey(HIDROCARBURO)) {
					sustanciaQuimicaPeligrosaBean.setMostrarManual(Boolean.parseBoolean(variables.get(HIDROCARBURO)
							.toString()));
				}
				cargarSustanciasQuimicas();
				cargarCaracterisiticas();
				sustanciaQuimicaPeligrosaBean
						.setSustanciaQuimicaPeligrosaTransporteVer(new SustanciaQuimicaPeligrosaTransporte());
				aprobacionRequisitosTecnicosBean.verART(SustanciaQuimicaPeligrosaTransporte.class.getName());
			} else {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('importanteWdgt').show();");
			}
		} catch (Exception e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Error al cargar la aprobacion");
		}

	}

	/**
	 * 
	 * <b> Metodo para verificar si se completo los vehiculos. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 19/08/2015]
	 * </p>
	 * 
	 * @return boolean: true si existe registros
	 */
	private boolean existeIngresoVehiculo() {
		boolean existe = true;
		
		if(!formVisualizar) {
			cargarRequisitoVehiculo();
			if (sustanciaQuimicaPeligrosaBean.getRequisitosVehiculos() == null
					|| sustanciaQuimicaPeligrosaBean.getRequisitosVehiculos().isEmpty()) {
				existe = false;
			}
		} else {
			try {
				Integer totalRequisitosVehiculo = requisitosVehiculoFacade
				.getNumeroRequisitosVehiculo(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getId());
				
				if(totalRequisitosVehiculo == 0) {
					existe = false;
				}
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		
		if (aprobacionRequisitosTecnicos.isProyectoExAnte()) {
			existe = true;
			tipoEstudioExPost = false;
		}else{
			tipoEstudioExPost = true;
			/**
			 * 2019-01-18
			 * validacion para que funcione com exAnte cuando es exPost y es alguna de las actividades indicadas
			 */
			ProyectoLicenciamientoAmbiental proyecto = this.proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(this.aprobacionRequisitosTecnicos.getProyecto());
			if (proyecto != null){
				switch (proyecto.getCatalogoCategoria().getDescripcion()) {
					case "RECOLECCIÓN Y TRANSPORTE DE DESECHOS PELIGROSOS":
					case "TRANSPORTE DE DESECHOS PELIGROSOS":
					case "TRANSPORTE Y DISTRIBUCIÓN DE SUSTANCIAS QUÍMICAS PELIGROSAS":
						existe = true;
						tipoEstudioExPost = false;
						break;
					default:
						break;
				}
			}
			// fin validacion
		}
		return existe;
	}

	/**
	 * 
	 * <b> Metodo para cargar los vehiculos. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 */
	public void cargarRequisitoVehiculo() {
		try {
			sustanciaQuimicaPeligrosaBean.setRequisitosVehiculos(requisitosVehiculoFacade
					.getListaRequisitosVehiculo(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
		} catch (ServiceException e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Error al cargar las cargarRequisitoVehiculo");
		}

	}

	/**
	 * 
	 * <b> Metodo que obtiene todas las sustancias quimicas. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 */
	public void cargarSustanciasQuimicas() {
		try {

			sustanciaQuimicaPeligrosaBean.setSustanciaQuimicaTransporteEliminar(new ArrayList<SustanciaQuimicaPeligrosaTransporte>());
			sustanciaQuimicaPeligrosaBean.setSustanciasUbicacionesEliminar(new ArrayList<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica>());
			sustanciaQuimicaPeligrosaBean.setSustanciasQuimicasPeligrosasTransportesModificados(new ArrayList<SustanciaQuimicaPeligrosaTransporte>());
			sustanciaQuimicaPeligrosaBean.setSustanciaQuimicaPeligrosaTransporte(new SustanciaQuimicaPeligrosaTransporte());
			
			sustanciaQuimicaPeligrosaBean.setSustanciaQuimicaPeligrosaTransporteLazy(new LazySustanciasDataModel(aprobacionRequisitosTecnicos.getId()));
			
		} catch (Exception e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Error al cargar las sustancias quimicas");
		}

	}

	/**
	 * 
	 * <b> Metodo que obtiene los detalles de cada sustancia, todas las
	 * ubicaciones de destino. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 * @param listaSustanciasUbicaciones
	 *            : Lista de ubicaciones
	 */
	private void cargarDetalleSustancias(
			List<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica> listaSustanciasUbicaciones) {
		int indice = 0;
		for (SustanciaQuimicaPeligrosaTransporteUbicacionGeografica susUbi : listaSustanciasUbicaciones) {
			susUbi.setIndice(indice);
			indice++;
		}
	}

	/**
	 * 
	 * <b> Metodo que obtiene los cantones y las provincias. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 */
	public void cargarCaracterisiticas() {
		if (sustanciaQuimicaPeligrosaBean.getCantonesOrigen() == null) {
			sustanciaQuimicaPeligrosaBean.setCantonesOrigen(new ArrayList<UbicacionesGeografica>());
		}
		if (sustanciaQuimicaPeligrosaBean.getProvincias() == null) {
			sustanciaQuimicaPeligrosaBean.setProvincias(new ArrayList<UbicacionesGeografica>());
		}
		for (UbicacionesGeografica ug : ubicacionGeograficaFacade.getProvincias()) {
			for (UbicacionesGeografica ug2 : ubicacionGeograficaFacade.getUbicacionPorPadre(ug)) {
				sustanciaQuimicaPeligrosaBean.getCantonesOrigen().add(ug2);
			}
		}
		sustanciaQuimicaPeligrosaBean.setProvincias(ubicacionGeograficaFacade.getProvincias());

	}

	/**
	 * 
	 * <b> Metodo que inicializa las varieble para un nuevo registro. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 */
	public void prepararNuevo() {

		sustanciaQuimicaPeligrosaBean.setEditar(false);
		sustanciaQuimicaPeligrosaBean.setSustanciaQuimicaPeligrosaTransporte(new SustanciaQuimicaPeligrosaTransporte());
		sustanciaQuimicaPeligrosaBean
				.setSustanciaQuimicaPeligrosaTransporteAux(new SustanciaQuimicaPeligrosaTransporte());
		agregarSustanciasQuimicasBean.setSustanciaSeleccionada(new SustanciaQuimicaPeligrosa());
		sustanciaQuimicaPeligrosaBean.setDestinos(new ArrayList<UbicacionesGeografica>());
		sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().setSustanciasUbicacion(
				new ArrayList<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica>());
	}

	/**
	 * 
	 * <b> Metodo que agrega las ubicaciones a cada sustancia en la lista. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 */
	public void agregarUbicacion() {
		boolean required = false;
		if (sustanciaQuimicaPeligrosaBean.getProvinciaDestinoSeleccionada() == null) {
			JsfUtil.addMessageErrorForComponent("provincia_" + "panelUbicacion",
					JsfUtil.getMessageFromBundle(null, "javax.faces.component.UIInput.REQUIRED", "Provincia"));
			required = true;
		}
		if (sustanciaQuimicaPeligrosaBean.getCantonDestinoSeleccionado() == null) {
			JsfUtil.addMessageErrorForComponent("canton_" + "panelUbicacion",
					JsfUtil.getMessageFromBundle(null, "javax.faces.component.UIInput.REQUIRED", "Cantón"));
			required = true;
		}
		if (required) {
			return;
		}

		UbicacionesGeografica ubicacionesGeografica = getUbicacionSeleccionada();
		if (!sustanciaQuimicaPeligrosaBean.getDestinos().contains(ubicacionesGeografica)) {
			sustanciaQuimicaPeligrosaBean.getDestinos().add(ubicacionesGeografica);
			sustanciaQuimicaPeligrosaBean.setCantonDestinoSeleccionado(null);
			sustanciaQuimicaPeligrosaBean.setProvinciaDestinoSeleccionada(null);
			SustanciaQuimicaPeligrosaTransporteUbicacionGeografica sustanciaUbicacion = new SustanciaQuimicaPeligrosaTransporteUbicacionGeografica();
			sustanciaUbicacion.setUbicacionesGeografica(ubicacionesGeografica);
			sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().getSustanciasUbicacion()
					.add(sustanciaUbicacion);
			JsfUtil.addCallbackParam("addLocation");
		} else {
			JsfUtil.addMessageError("Esta ubicación ya está seleccionada.");
		}
	}

	/**
	 * 
	 * <b> Metodo que obtiene la ubicacion seleccionada. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 * @return
	 */
	public UbicacionesGeografica getUbicacionSeleccionada() {
		if (sustanciaQuimicaPeligrosaBean.getCantonDestinoSeleccionado() != null) {
			return sustanciaQuimicaPeligrosaBean.getCantonDestinoSeleccionado();
		}
		return sustanciaQuimicaPeligrosaBean.getProvinciaDestinoSeleccionada();
	}

	/**
	 * 
	 * <b> Metodo que remueve la ubicacion seleccionada de la lista. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 * @param ubicacionesGeografica
	 */
	public void quitarUbicacion(SustanciaQuimicaPeligrosaTransporteUbicacionGeografica ubicacionesGeografica) {
		sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().getSustanciasUbicacion()
				.remove(ubicacionesGeografica);
		sustanciaQuimicaPeligrosaBean.getDestinos().remove(ubicacionesGeografica);
		sustanciaQuimicaPeligrosaBean.getSustanciasUbicacionesEliminar().add(ubicacionesGeografica);
	}

	/**
	 * 
	 * <b> Metodo que valida todos los campos para cada sustancia. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 * @return boolean: true si todo esta bien
	 */
	public boolean validateSustanciasQuimicas() {
		List<String> listaMensajes = new ArrayList<String>();
		if (agregarSustanciasQuimicasBean.getSustanciaSeleccionada().getDescripcion() == null
				|| agregarSustanciasQuimicasBean.getSustanciaSeleccionada().getDescripcion().isEmpty()) {
			listaMensajes.add("No se han definido el sustancia quimica peligrosa.");
		}
		if (listaMensajes.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			return false;
		}
	}

	public void validateUbicaciones(FacesContext context, UIComponent validate, Object value) {
		StringBuilder functionJs = new StringBuilder();
		List<FacesMessage> mensajes = new ArrayList<>();
		if (sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte() != null
				&& !sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().isOtroPais()
				&& sustanciaQuimicaPeligrosaBean.getDestinos().isEmpty()
				&& !sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().isDestinoNivelNacional()) {
			functionJs.append("highlightComponent('form:pnlAgregarDesecho');");
			FacesMessage mensajeValidacion = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"No se han definido ubicaciones geográficas.", null);
			mensajes.add(mensajeValidacion);
			RequestContext.getCurrentInstance().execute(functionJs.toString());
			throw new ValidatorException(mensajes);

		} else {
			functionJs.append("removeHighLightComponent('form:pnlAgregarDesecho');");
			RequestContext.getCurrentInstance().execute(functionJs.toString());
		}

	}

	public void validarNivelNacional() {

		if (sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().isDestinoNivelNacional()
				&& !sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().getSustanciasUbicacion()
						.isEmpty()) {
			sustanciaQuimicaPeligrosaBean.setSustanciasUbicacionesEliminar(sustanciaQuimicaPeligrosaBean
					.getSustanciaQuimicaPeligrosaTransporte().getSustanciasUbicacion());
			sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().setSustanciasUbicacion(
					new ArrayList<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica>());
			sustanciaQuimicaPeligrosaBean.setDestinos(new ArrayList<UbicacionesGeografica>());

		}
	}

	/**
	 * 
	 * <b> Metodo que agrega a la lista de sustancia. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 */
	public void agregarSustanciaQuimicaPeligrosaTrans() {

		RequestContext context = RequestContext.getCurrentInstance();
		if (validateSustanciasQuimicas()) {

			if (sustanciaQuimicaPeligrosaBean.isEditar()) {
				if (sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().isOtroPais()) {
					sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().setProvinciaOrigen(null);
				} else {
					sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().setPais(new String());
				}

			} else {
				sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().setSustanciaQuimicaPeligrosa(
						agregarSustanciasQuimicasBean.getSustanciaSeleccionada());

				sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().setAprobacionRequisitosTecnicos(
						aprobacionRequisitosTecnicos);
				if (sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().isOtroPais()) {
					sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().setProvinciaOrigen(null);
				} else {
					sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().setPais(new String());
				}
			}
			
			List<SustanciaQuimicaPeligrosaTransporte> listaPeligrosoTransportes = new ArrayList<>();
			listaPeligrosoTransportes.add(sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte());
			
			List<SustanciaQuimicaPeligrosaTransporte> listaPeligrosoTransportesEliminar = new ArrayList<>();
			List<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica> listaSustanciaUbicacionGeograficaEliminar = new ArrayList<>();
			
			try {
				sustanciasQuimicaPeligrosaTransporteFacade.guardarSustanciaQuimicaPeligrosaTransportacion(
						listaPeligrosoTransportes,
						listaPeligrosoTransportesEliminar,
						listaSustanciaUbicacionGeograficaEliminar,
						bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId());
			} catch (ServiceException | CmisAlfrescoException e) {
				e.printStackTrace();
			}
			
			prepararNuevo();
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			context.addCallbackParam("sustanciaIn", true);
		} else {
			context.addCallbackParam("sustanciaIn", false);
		}

	}

	/**
	 * 
	 * <b> Metodo que valida que no se guarde la lista vacia. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 * @return
	 */
	public boolean validarGuardar() {
		List<String> listaMensajes = new ArrayList<String>();
		if (sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporteLazy().getRowCount() == 0) {
			listaMensajes.add("Debe ingresar al menos una sustancia.");
		}
		if (listaMensajes.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			return false;
		}
	}

	/**
	 * 
	 * <b> Metodo que selecciona una sustancia de la lista para la edicion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 * @param sustanciaQuimicaPeligrosaTransporte
	 *            : sustancia seleccionada
	 */
	public void seleccionarSustanciaQuimica(SustanciaQuimicaPeligrosaTransporte sustanciaQuimicaPeligrosaTransporte) {
		sustanciaQuimicaPeligrosaBean.setSustanciaQuimicaPeligrosaTransporte(sustanciaQuimicaPeligrosaTransporte);
		agregarSustanciasQuimicasBean.setSustanciaSeleccionada(sustanciaQuimicaPeligrosaTransporte
				.getSustanciaQuimicaPeligrosa());
		sustanciaQuimicaPeligrosaBean.setEditar(true);
		sustanciaQuimicaPeligrosaBean.setDestinos(new ArrayList<UbicacionesGeografica>());
		for (SustanciaQuimicaPeligrosaTransporteUbicacionGeografica sustanciaUbicacion : sustanciaQuimicaPeligrosaTransporte
				.getSustanciasUbicacion()) {
			sustanciaQuimicaPeligrosaBean.getDestinos().add(sustanciaUbicacion.getUbicacionesGeografica());
		}
		sustanciaQuimicaPeligrosaBean
				.setSustanciaQuimicaPeligrosaTransporteAux((SustanciaQuimicaPeligrosaTransporte) SerializationUtils
						.clone(sustanciaQuimicaPeligrosaTransporte));
	}

	/**
	 * 
	 * <b> Metodo que carga los cantones segun la provincia seleccionada. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 */
	public void cargarCantones() {
		sustanciaQuimicaPeligrosaBean.setCantones(new ArrayList<UbicacionesGeografica>());
		if (sustanciaQuimicaPeligrosaBean.getProvinciaDestinoSeleccionada() != null) {
			sustanciaQuimicaPeligrosaBean.setCantones(ubicacionGeograficaFacade
					.getCantonesParroquia(sustanciaQuimicaPeligrosaBean.getProvinciaDestinoSeleccionada()));
		} else {
			sustanciaQuimicaPeligrosaBean.setCantones(new ArrayList<UbicacionesGeografica>());
		}
	}

	/**
	 * 
	 * <b> Metodo que elimina un sustancia de la lista. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 */
	public void eliminarSustancia() {
		try {
			if (sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte() != null) {
				if (sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().getSustanciasUbicacion() != null
						&& !sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().getSustanciasUbicacion()
								.isEmpty()) {
					sustanciaQuimicaPeligrosaBean.getSustanciasUbicacionesEliminar()
							.addAll(sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte()
									.getSustanciasUbicacion());
				}
				sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaTransporteEliminar().add(
						sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte());
			}
			
			sustanciasQuimicaPeligrosaTransporteFacade.eliminarSustanciaQuimicaPeligrosaTransportacion(
							sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaTransporteEliminar(),
							sustanciaQuimicaPeligrosaBean.getSustanciasUbicacionesEliminar());
		
			if (sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporteLazy().getRowCount() == 1) {
				//si solo existe un registro despues de eliminar la tabla va a quedar vacia
				//Por eso se elimina la validacion de seccion completa
				validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos",
						"sustanciasQuimicasPeligrosas", aprobacionRequisitosTecnicosBean
								.getAprobacionRequisitosTecnicos().getId().toString(), false);
			}
		} catch (Exception e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Error al eliminar");
		}
	}

	/**
	 * 
	 * <b> Metodo que guarda la lista de las sustancias agregadas a la lit. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 */
	public void guardar() {

		try {
			if (validarGuardar()) {
				sustanciasQuimicaPeligrosaTransporteFacade.guardarSustanciaQuimicaPeligrosaTransportacion(
						sustanciaQuimicaPeligrosaBean.getSustanciasQuimicasPeligrosasTransportesModificados(),
						sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaTransporteEliminar(),
						sustanciaQuimicaPeligrosaBean.getSustanciasUbicacionesEliminar(),
						bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId());
				prepararNuevo();
				cargarSustanciasQuimicas();
				validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos",
						"sustanciasQuimicasPeligrosas", aprobacionRequisitosTecnicosBean
								.getAprobacionRequisitosTecnicos().getId().toString());
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}

		} catch (Exception e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Error al guardar");
		}

	}

	public void seleccionarSustanciaQuimicaVer(SustanciaQuimicaPeligrosaTransporte peligrosaTransporte) {
		sustanciaQuimicaPeligrosaBean.setSustanciaQuimicaPeligrosaTransporteVer(peligrosaTransporte);
	}

	public boolean isVisibleOpcionNavegarSiguienteEnMenuModoVer(boolean isModoVer) {
		String page = recuparPaginaSiguiente(isModoVer);
		return !(page.contains("envioAprobacionRequisitosVer"));
	}

	public String recuparPaginaSiguiente(boolean isModoVer) {
		String extencionModo = (isModoVer ? "Ver" : "") + ".jsf?faces-redirect=true";
		if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestionConTransporte()) {
			return "/control/aprobacionRequisitosTecnicos/desechoPeligrosoTransporte" + extencionModo;
		} else {
			if (isModoVer) {
				if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestion()) {
					return "/control/aprobacionRequisitosTecnicos/recepcionDesechosPeligrosos" + extencionModo;
				} else {
					return "/control/aprobacionRequisitosTecnicos/envioAprobacionRequisitos" + extencionModo;
				}
			} else {
				return "/control/aprobacionRequisitosTecnicos/informativo.jsf?faces-redirect=true";
			}
		}

	}

	public void cancelar() {
		if (sustanciaQuimicaPeligrosaBean.isEditar()) {
			sustanciaQuimicaPeligrosaBean.setSustanciaQuimicaPeligrosaTransporte(null);
			sustanciaQuimicaPeligrosaBean.setSustanciaQuimicaPeligrosaTransporteAux(null);

		} else if (sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporteAux() != null
				&& sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporteAux().getProvinciaOrigen() != null) {

		}

	}

	/**
	 * 
	 * <b> Metodo para descargar los anexos. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/10/2015]
	 * </p>
	 * 
	 * @throws IOException
	 *             : Excepcion
	 */
	public void descargar() throws IOException {
		try {

			UtilDocumento.descargarZipRar(sustanciasQuimicaPeligrosaTransporteFacade
					.descargarFile(sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte()
							.getDocumentoManualOperaciones()), sustanciaQuimicaPeligrosaBean
					.getSustanciaQuimicaPeligrosaTransporte().getDocumentoManualOperaciones().getNombre());

		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}

	}

	/**
	 * 
	 * <b> Metodo que sube un archivo. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/07/2015]
	 * </p>
	 * 
	 * @param eventcancelar
	 *            : evento
	 */
	public void handleFileUpload(FileUploadEvent event) {
		sustanciaQuimicaPeligrosaBean.setFile(event.getFile());
		sustanciaQuimicaPeligrosaBean.getSustanciaQuimicaPeligrosaTransporte().setDocumentoManualOperaciones(
				UtilDocumento.generateDocumentZipFromUpload(sustanciaQuimicaPeligrosaBean.getFile().getContents(),
						sustanciaQuimicaPeligrosaBean.getFile().getFileName()));
	}
}
