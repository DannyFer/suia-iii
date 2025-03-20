/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AgregarDesechoPeligroso;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.DesechoPeligrosoTransporteBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.DesechoPeligrosoTransporteFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.RequisitosVehiculoFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ValidacionesPagesAprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.desechoPeligroso.facade.DesechoPeligrosoFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoEspecialRecoleccion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoPeligrosoTransporte;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoPeligrosoTransporteUbicacionGeografica;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.SerializationUtils;

/**
 * <b> Clase controlador para la transportacion de los desechos peligrosos. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 22/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class DesechoPeligrosoTransporteController implements Serializable {
	private static final Logger LOGGER = Logger.getLogger(DesechoPeligrosoTransporteController.class);

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@Setter
	@Getter
	@ManagedProperty(value = "#{desechoPeligrosoTransporteBean}")
	private DesechoPeligrosoTransporteBean desechoPeligrosoTransporteBean;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private DesechoPeligrosoTransporteFacade desechoPeligrosoTransportacionFacade;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private RequisitosVehiculoFacade requisitosVehiculoFacade;

	@EJB
	private DesechoPeligrosoFacade desechoPeligrosoFacade;

	@Setter
	@Getter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Setter
	@Getter
	@ManagedProperty(value = "#{agregarDesechoPeligroso}")
	private AgregarDesechoPeligroso agregarDesechoPeligroso;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@EJB
	private ValidacionesPagesAprobacionRequisitosTecnicosFacade validacionesPagesAprobacionRequisitosTecnicosFacade;
	
	@EJB
	ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	@Getter
	@Setter
	private boolean tipoEstudioExPost;

	@PostConstruct
	public void init() {
		try {
			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos();
			if (existeIngresoVehiculo()) {
				cargarDesechosPeligrososTransporte();
				cargarCaracterisiticas();
				agregarDesechoPeligroso.setMostrarDesechosB(true);
				desechoPeligrosoTransporteBean.setDesechoPeligrosoTransporteVer(new DesechoPeligrosoTransporte());
				aprobacionRequisitosTecnicosBean.verART(DesechoPeligrosoTransporte.class.getName());
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
		cargarRequisitoVehiculo();
		if (desechoPeligrosoTransporteBean.getRequisitosVehiculos() == null
				|| desechoPeligrosoTransporteBean.getRequisitosVehiculos().isEmpty()) {
			existe = false;
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
	 * <b> Metodo que inicializa las variables para crear un nuevo desechos para
	 * la transportacion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 */
	public void prepararNuevo() {
		if(!desechoPeligrosoTransporteBean.isEditar()){
		desechoPeligrosoTransporteBean.setDesechoPeligrosoTransporte(new DesechoPeligrosoTransporte());
		desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setDesechosUbicaciones(
				new ArrayList<DesechoPeligrosoTransporteUbicacionGeografica>());
		desechoPeligrosoTransporteBean.setOrigenes(new ArrayList<UbicacionesGeografica>());
		agregarDesechoPeligroso.setDesechoSeleccionado(new DesechoPeligroso());
		desechoPeligrosoTransporteBean.setProvinciaDestino(new UbicacionesGeografica());
	}
		else {
			desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setDesechoPeligroso(agregarDesechoPeligroso.getDesechoSeleccionado());
			desechoPeligrosoTransporteBean.setEditar(false);
		}

	}

	/**
	 * 
	 * <b> Metodo que obtiene los vehiculos segun el proyecto. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 */
	public void cargarRequisitoVehiculo() {
		try {
			desechoPeligrosoTransporteBean.setRequisitosVehiculos(requisitosVehiculoFacade
					.getListaRequisitosVehiculo(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
		} catch (ServiceException e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Error al cargar las cargar los vehiculos");
		}

	}

	/**
	 * 
	 * <b> Metodo para obtener las provincias y los cantones. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 */
	public void cargarCaracterisiticas() {
		if (desechoPeligrosoTransporteBean.getCantones() == null) {
			desechoPeligrosoTransporteBean.setCantones(new ArrayList<UbicacionesGeografica>());
		}
		if (desechoPeligrosoTransporteBean.getProvincias() == null) {
			desechoPeligrosoTransporteBean.setProvincias(new ArrayList<UbicacionesGeografica>());
		}
		desechoPeligrosoTransporteBean.setProvincias(ubicacionGeograficaFacade.getProvincias());

	}

	/**
	 * 
	 * <b> Metodo para obtener todos los desechos de la transportacion segun el
	 * proyecto. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 */
	/*public void cargarDesechosPeligrososTransporte() {
		try {
			desechoPeligrosoTransporteBean
					.setDesechoPeligrosoTransporteEliminar(new ArrayList<DesechoPeligrosoTransporte>());
			desechoPeligrosoTransporteBean
					.setDesechosubicacionesGeograficasEliminar(new ArrayList<DesechoPeligrosoTransporteUbicacionGeografica>());
			desechoPeligrosoTransporteBean
					.setDesechoPeligrosoTransporteModificados(new ArrayList<DesechoPeligrosoTransporte>());
			desechoPeligrosoTransporteBean
					.setDesechosEspecialesRecoleccionEliminar(new ArrayList<DesechoEspecialRecoleccion>());
			List<DesechoPeligrosoTransporte> suGeograficas = desechoPeligrosoTransportacionFacade
					.getListaListaDesechoPeligrosoTransporte(aprobacionRequisitosTecnicos.getId());
			desechoPeligrosoTransporteBean.setDesechosPeligrososTransportes(suGeograficas);
		} catch (Exception e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Error al cargar las desechos peligrosos");
		}

	}*/
	public void cargarDesechosPeligrososTransporte() {
		try {
			desechoPeligrosoTransporteBean
					.setDesechoPeligrosoTransporteEliminar(new ArrayList<DesechoPeligrosoTransporte>());
			desechoPeligrosoTransporteBean
					.setDesechosubicacionesGeograficasEliminar(new ArrayList<DesechoPeligrosoTransporteUbicacionGeografica>());
			desechoPeligrosoTransporteBean
					.setDesechosEspecialesRecoleccionEliminar(new ArrayList<DesechoEspecialRecoleccion>());
			List<DesechoPeligrosoTransporte> suGeograficas = desechoPeligrosoTransportacionFacade
					.getListaListaDesechoPeligrosoTransporte(aprobacionRequisitosTecnicos.getId());
			desechoPeligrosoTransporteBean.setDesechosPeligrososTransportes(suGeograficas);
		} catch (Exception e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Error al cargar las desechos peligrosos");
		}

	}

	/**
	 * 
	 * <b> Metodo para editar, ver origenes y los datos del vehiculo un desecho.
	 * </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 * @param desechoPeligrosoTransporte
	 *            : desecho seleccionado
	 */
	public void seleccionarDesechoTranporte(DesechoPeligrosoTransporte desechoPeligrosoTransporte) {
		desechoPeligrosoTransporteBean.setDesechoPeligrosoTransporte(desechoPeligrosoTransporte);
		if (!desechoPeligrosoTransporte.isOtroPais() && !desechoPeligrosoTransporte.isDestinoNivelNacional()) {

			desechoPeligrosoTransporteBean.setProvinciaDestino(desechoPeligrosoTransporte.getCantonDestino()
					.getUbicacionesGeografica());
			cargarCantonesDestino();
		}
		agregarDesechoPeligroso.setDesechoSeleccionado(desechoPeligrosoTransporte.getDesechoPeligroso());
		desechoPeligrosoTransporteBean.setEditar(true);
		desechoPeligrosoTransporteBean.setOrigenes(new ArrayList<UbicacionesGeografica>());
		for (DesechoPeligrosoTransporteUbicacionGeografica desechoUbicacion : desechoPeligrosoTransporte
				.getDesechosUbicaciones()) {
			desechoPeligrosoTransporteBean.getOrigenes().add(desechoUbicacion.getUbicacionGeografica());
		}
		desechoPeligrosoTransporteBean.setDesechoPeligrosoTransporteAux((DesechoPeligrosoTransporte) SerializationUtils
				.clone(desechoPeligrosoTransporte));

	}

	/**
	 * 
	 * <b> Metodo que carga los cantones de origen cuando se selecciona la
	 * provincia de origen . </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 */
	public void cargarCantones() {
		desechoPeligrosoTransporteBean.setCantones(new ArrayList<UbicacionesGeografica>());
		if (desechoPeligrosoTransporteBean.getProvinciaOrigenSeleccionada() != null)
			desechoPeligrosoTransporteBean.setCantones(ubicacionGeograficaFacade
					.getCantonesParroquia(desechoPeligrosoTransporteBean.getProvinciaOrigenSeleccionada()));
		else
			desechoPeligrosoTransporteBean.setCantones(new ArrayList<UbicacionesGeografica>());
	}

	/**
	 * 
	 * <b> Metodo que carga los cantones de destino cuando se selecciona la
	 * provincia de destino </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 */
	public void cargarCantonesDestino() {
		desechoPeligrosoTransporteBean.setCantonesDestino(new ArrayList<UbicacionesGeografica>());
		if (desechoPeligrosoTransporteBean.getProvinciaDestino() != null) {
			desechoPeligrosoTransporteBean.setCantonesDestino(ubicacionGeograficaFacade
					.getCantonesParroquia(desechoPeligrosoTransporteBean.getProvinciaDestino()));
		} else {
			desechoPeligrosoTransporteBean.setCantonesDestino(new ArrayList<UbicacionesGeografica>());
		}
	}

	/**
	 * 
	 * <b> Metodo que agrega una ubicacion a la lista para cada desecho. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 */
	public void agregarUbicacion() {
		boolean required = false;
		if (desechoPeligrosoTransporteBean.getProvinciaOrigenSeleccionada() == null) {
			JsfUtil.addMessageErrorForComponent("provincia_" + "panelUbicacion",
					JsfUtil.getMessageFromBundle(null, "javax.faces.component.UIInput.REQUIRED", "Provincia"));
			required = true;
		}
		if (desechoPeligrosoTransporteBean.getCantonOrigenSeleccionado() == null) {
			JsfUtil.addMessageErrorForComponent("canton_" + "panelUbicacion",
					JsfUtil.getMessageFromBundle(null, "javax.faces.component.UIInput.REQUIRED", "Cantón"));
			required = true;
		}
		if (required)
			return;

		UbicacionesGeografica ubicacionesGeografica = getUbicacionSeleccionada();

		if (!desechoPeligrosoTransporteBean.getOrigenes().contains(ubicacionesGeografica)) {
			desechoPeligrosoTransporteBean.getOrigenes().add(ubicacionesGeografica);
			desechoPeligrosoTransporteBean.setCantonOrigenSeleccionado(null);
			desechoPeligrosoTransporteBean.setProvinciaOrigenSeleccionada(null);
			DesechoPeligrosoTransporteUbicacionGeografica desechoUbicacion = new DesechoPeligrosoTransporteUbicacionGeografica();
			desechoUbicacion.setUbicacionGeografica(ubicacionesGeografica);
			desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().getDesechosUbicaciones()
					.add(desechoUbicacion);
			JsfUtil.addCallbackParam("addLocation");
		} else
			JsfUtil.addMessageError("Esta ubicación ya está seleccionada.");
	}

	/**
	 * 
	 * <b> Metodo que obtiene la ubicacion seleccionada. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 * @return UbicacionesGeografica: ubicacion selecccionada
	 */
	public UbicacionesGeografica getUbicacionSeleccionada() {
		if (desechoPeligrosoTransporteBean.getCantonOrigenSeleccionado() != null)
			return desechoPeligrosoTransporteBean.getCantonOrigenSeleccionado();
		return desechoPeligrosoTransporteBean.getProvinciaOrigenSeleccionada();
	}

	/**
	 * 
	 * <b> Metodo que remueve de la lista la ubicacion seleccion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 * @param desechoubicacionesGeografica
	 *            : desecho seleccionado
	 */
	public void quitarUbicacion(DesechoPeligrosoTransporteUbicacionGeografica desechoubicacionesGeografica) {
		desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().getDesechosUbicaciones()
				.remove(desechoubicacionesGeografica);
		desechoPeligrosoTransporteBean.getOrigenes().remove(desechoubicacionesGeografica.getUbicacionGeografica());
		desechoPeligrosoTransporteBean.getDesechosubicacionesGeograficasEliminar().add(desechoubicacionesGeografica);
	}

	/**
	 * 
	 * <b> Metodo que valida todos los campos del desecho para la
	 * transportacion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 * @return
	 */
	public boolean validateDesechosPeligrosos() {
		List<String> listaMensajes = new ArrayList<String>();
		if (agregarDesechoPeligroso.getDesechoSeleccionado().getClave() == null
				|| agregarDesechoPeligroso.getDesechoSeleccionado().getClave().isEmpty()) {
			listaMensajes.add("No se han definido el desecho peligroso.");
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
	 * <b> Metodo para agregar un desecho de la lista. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 */
	/*
	* public void agregarDesechoPeligrosoTrans() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (validateDesechosPeligrosos()) {
//desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte();
			if (desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().isOtroPais()) {
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setProvinciaDestino(null);
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setCantonDestino(null);
			} else {
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setPais(new String());
			}
			if (desechoPeligrosoTransporteBean.isEditar()) {

				if (desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().getDesechoPeligroso().getClave()
						.startsWith("Q.")
						&& !desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().getDesechoPeligroso()
								.equals(agregarDesechoPeligroso.getDesechoSeleccionado())) {

					desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setIdDesechoEspecial(
							desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().getDesechoPeligroso()
									.getId());
				}
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setDesechoPeligroso(
						agregarDesechoPeligroso.getDesechoSeleccionado());
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteModificados().add(
						desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte());
			} else {
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setDesechoPeligroso(
						agregarDesechoPeligroso.getDesechoSeleccionado());
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setAprobacionRequisitosTecnicos(
						aprobacionRequisitosTecnicos);
				desechoPeligrosoTransporteBean.getDesechosPeligrososTransportes().add(
						desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte());
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteModificados().add(
						desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte());

			}

			prepararNuevo();
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			context.addCallbackParam("desechoIn", true);
		} else {
			context.addCallbackParam("desechoIn", false);
		}

	}*/
	public void agregarDesechoPeligrosoTrans() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (validateDesechosPeligrosos()) {
//desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte();
			if (desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().isOtroPais()) {
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setProvinciaDestino(null);
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setCantonDestino(null);
			} else {
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setPais(new String());
			}

				if (desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().getDesechoPeligroso()!=null && desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().getDesechoPeligroso().getClave()
						.startsWith("Q.")
						&& !desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().getDesechoPeligroso()
						.equals(agregarDesechoPeligroso.getDesechoSeleccionado())) {

					desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setIdDesechoEspecial(
							desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().getDesechoPeligroso()
									.getId());
				}
			if (!desechoPeligrosoTransporteBean.isEditar()) {
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setDesechoPeligroso(
						agregarDesechoPeligroso.getDesechoSeleccionado());
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setAprobacionRequisitosTecnicos(
						aprobacionRequisitosTecnicos);
				desechoPeligrosoTransporteBean.getDesechosPeligrososTransportes().add(
						desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte());
			}

				/*desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteModificados().add(
						desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte());*/
//hereeeeee
			prepararNuevo();
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			context.addCallbackParam("desechoIn", true);
		} else {
			context.addCallbackParam("desechoIn", false);
		}

	}

	/*public void agregarRegistroEspecies() {



					if (!this.isEditingRegistro) {
						this.listaRegistroEspecies.add(this.registroEspeciesEIA);
						//Actualizamos el número de registros de especies del punto.
						actualizarCantRegEspecies(this.registroEspeciesEIA, true);
					}

					realizarCalculos();
					compararPuntos();
					if(!this.tipo.equalsIgnoreCase("Macroinvertebrados") &&
							!this.tipo.equalsIgnoreCase("Limnofauna"))
						this.calcularAbundanciaRelativa();
					JsfUtil.addCallbackParam("registroEspecies");

					RequestContext.getCurrentInstance().execute(
							"PF('dlg4').hide();");
				}



	}*/

	/**
	 * 
	 * <b> Metodo que elimina un desecho de la lista. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 */
	public void eliminarDesecho() {
		desechoPeligrosoTransporteBean.getDesechosPeligrososTransportes().remove(
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte());
		if (desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte() != null) {
			if (desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().getDesechosUbicaciones() != null
					&& !desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().getDesechosUbicaciones()
							.isEmpty()) {
				desechoPeligrosoTransporteBean.getDesechosubicacionesGeograficasEliminar().addAll(
						desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().getDesechosUbicaciones());
			}
			desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteEliminar().add(
					desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte());
		}
	}

	/**
	 * 
	 * <b> Metodo que guarda todos los registros de la lista de desechos. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 */
	/*public void guardar() {
		try {
			if (validarGuardar()) {

				desechoModificado();
				List<DesechoPeligrosoTransporte> listaDesechos = desechoPeligrosoTransporteBean
						.getDesechoPeligrosoTransporteModificados();
				desechoPeligrosoTransportacionFacade.guardarDesechoPeligrosoTransportacion(listaDesechos,
						desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteEliminar(),
						desechoPeligrosoTransporteBean.getDesechosubicacionesGeograficasEliminar(),
						desechoPeligrosoTransporteBean.getDesechosEspecialesRecoleccionEliminar());
				prepararNuevo();
				cargarDesechosPeligrososTransporte();
				validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos",
						"desechoPeligrosoTransporte", aprobacionRequisitosTecnicosBean
								.getAprobacionRequisitosTecnicos().getId().toString());
				JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
			}
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
			LOGGER.error(e, e);
		}
	}*/

	public void guardar() {
		try {
			if (validarGuardar()) {

				desechoModificado();
				/*List<DesechoPeligrosoTransporte> listaDesechos = desechoPeligrosoTransporteBean
						.getDesechoPeligrosoTransporteModificados();*/
				desechoPeligrosoTransportacionFacade.guardarDesechoPeligrosoTransportacion(this.desechoPeligrosoTransporteBean.getDesechosPeligrososTransportes(),
						desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteEliminar(),
						desechoPeligrosoTransporteBean.getDesechosubicacionesGeograficasEliminar(),
						desechoPeligrosoTransporteBean.getDesechosEspecialesRecoleccionEliminar());
				prepararNuevo();
				cargarDesechosPeligrososTransporte();
				validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos",
						"desechoPeligrosoTransporte", aprobacionRequisitosTecnicosBean
								.getAprobacionRequisitosTecnicos().getId().toString());
				JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
			}
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
			LOGGER.error(e, e);
		}
	}

	/**
	 * 
	 * <b> Metodo que valida que no se guarde vacia la lista . </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 * @return boolean: true si esta vacia
	 */
	public boolean validarGuardar() {
		List<String> listaMensajes = new ArrayList<String>();
		if (desechoPeligrosoTransporteBean.getDesechosPeligrososTransportes().isEmpty()) {
			listaMensajes.add("Debe ingresar al menos un Desecho.");
		}
		if (listaMensajes.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			return false;
		}
	}

	public void validarNivelNacional() {

		if (desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().isOrigenNivelNacional()
				&& !desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().getDesechosUbicaciones().isEmpty()) {
			desechoPeligrosoTransporteBean.setDesechosubicacionesGeograficasEliminar(desechoPeligrosoTransporteBean
					.getDesechoPeligrosoTransporte().getDesechosUbicaciones());
			desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setDesechosUbicaciones(
					new ArrayList<DesechoPeligrosoTransporteUbicacionGeografica>());
			desechoPeligrosoTransporteBean.setOrigenes(new ArrayList<UbicacionesGeografica>());

		}
	}

	public void validarNivelNacionalDestino() {

		if (desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().isDestinoNivelNacional()) {
			desechoPeligrosoTransporteBean.setProvinciaDestino(null);
			desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setCantonDestino(null);

		}
	}

	/**
	 * 
	 * <b> Metodo que verifica si un desecho fue o es desecho especial para
	 * eliminar. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/07/2015]
	 * </p>
	 * 
	 */
	/*
	public void desechoModificado() {
		try {
			if (!desechoPeligrosoTransporteBean.getDesechosPeligrososTransportes().isEmpty()) {
				for (DesechoPeligrosoTransporte desechoTransporte : desechoPeligrosoTransporteBean
						.getDesechosPeligrososTransportes()) {
					for (DesechoPeligrosoTransporte desechoTransporteModificado : desechoPeligrosoTransporteBean
							.getDesechoPeligrosoTransporteModificados()) {

						if (desechoTransporte.getId() == desechoTransporteModificado.getId()
								&& desechoTransporteModificado.getIdDesechoEspecial() != null
								&& desechoTransporteModificado.getIdDesechoEspecial() != 0) {
							DesechoEspecialRecoleccion desechoEspecialRecoleccion = desechoPeligrosoTransportacionFacade
									.obtenerDesechoEspecialRecoleccionPorProyecto(desechoTransporteModificado
											.getIdDesechoEspecial(), desechoTransporte
											.getAprobacionRequisitosTecnicos().getId());
							if (desechoEspecialRecoleccion == null) {
								JsfUtil.addMessageError("Error al eliminar desecho especial");
							} else {
								desechoPeligrosoTransporteBean.getDesechosEspecialesRecoleccionEliminar().add(
										desechoEspecialRecoleccion);
							}

						}
					}

				}
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al eliminar desecho especial");
			LOGGER.error(e, e);
		}

	}*/

	public void desechoModificado() {
		try {
			if (!desechoPeligrosoTransporteBean.getDesechosPeligrososTransportes().isEmpty()) {
				for (DesechoPeligrosoTransporte desechoTransporte : desechoPeligrosoTransporteBean
						.getDesechosPeligrososTransportes()) {

						if (desechoTransporte.getIdDesechoEspecial() != null
								&& desechoTransporte.getIdDesechoEspecial() != 0) {
							DesechoEspecialRecoleccion desechoEspecialRecoleccion = desechoPeligrosoTransportacionFacade
									.obtenerDesechoEspecialRecoleccionPorProyecto(desechoTransporte
											.getIdDesechoEspecial(), desechoTransporte
											.getAprobacionRequisitosTecnicos().getId());
							if (desechoEspecialRecoleccion == null) {
								JsfUtil.addMessageError("Error al eliminar desecho especial");
							} else {
								desechoPeligrosoTransporteBean.getDesechosEspecialesRecoleccionEliminar().add(
										desechoEspecialRecoleccion);
							}

						}

				}
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al eliminar desecho especial");
			LOGGER.error(e, e);
		}

	}

	public void seleccionarDesechoVer(DesechoPeligrosoTransporte desechoPeligrosoTransporte) {
		desechoPeligrosoTransporteBean.setDesechoPeligrosoTransporteVer(desechoPeligrosoTransporte);
	}

	public void validateUbicaciones(FacesContext context, UIComponent validate, Object value) {
		StringBuilder functionJs = new StringBuilder();
		List<FacesMessage> mensajes = new ArrayList<>();
		if (desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte() != null
				&& desechoPeligrosoTransporteBean.getOrigenes().isEmpty()
				&& !desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().isOrigenNivelNacional()) {
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

	public String recuparPaginaAnterior(boolean isModoVer) {
		String extencionModo = (isModoVer ? "Ver" : "") + ".jsf?faces-redirect=true";
		if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isTransporte()) {
			return "/control/aprobacionRequisitosTecnicos/sustanciasQuimicasPeligrosas" + extencionModo;
		} else {
			return "/control/aprobacionRequisitosTecnicos/requisitosConductor" + extencionModo;
		}
	}

	public boolean isVisibleOpcionNavegarSiguienteEnMenuModoVer(boolean isModoVer) {
		String page = recuperarPaginaSiguiente(isModoVer);
		return !(page.contains("envioAprobacionRequisitosVer"));
	}

	public String recuperarPaginaSiguiente(boolean isModoVer) {
		String extencionModo = (isModoVer ? "Ver" : "") + ".jsf?faces-redirect=true";
		try {
			if (validacionesPagesAprobacionRequisitosTecnicosFacade
					.isPageRecoleccionTransporteDesechosVisible(aprobacionRequisitosTecnicosBean
							.getAprobacionRequisitosTecnicos())) {
				return "/control/aprobacionRequisitosTecnicos/recoleccionYTransporteDesechos" + extencionModo;
			} else {
				if (!isModoVer) {
					return "/control/aprobacionRequisitosTecnicos/informativo.jsf?faces-redirect=true";
				} else {
					if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestionSoloConTransporte()) {
						return "/control/aprobacionRequisitosTecnicos/envioAprobacionRequisitosVer.jsf?faces-redirect=true";
					} else if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestion()) {
						return "/control/aprobacionRequisitosTecnicos/recepcionDesechosPeligrososVer.jsf?faces-redirect=true";
					} else {
						return "/control/aprobacionRequisitosTecnicos/envioAprobacionRequisitosVer.jsf?faces-redirect=true";
					}
				}
			}
		} catch (ServiceException e) {
			LOGGER.error(e, e);
			return "";
		}
	}

	public void cancelar() {

		if (desechoPeligrosoTransporteBean.isEditar()) {
			desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setDesechosUbicaciones(
					desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteAux().getDesechosUbicaciones());
			desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setRequisitosVehiculo(
					desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteAux().getRequisitosVehiculo());
			desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setOtroPais(
					desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteAux().isOtroPais());
			if (desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().isOtroPais())
				desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setPais(
						desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteAux().getPais());
		} else if (desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteAux() != null
				&& desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteAux().getCantonDestino() != null) {

			desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setProvinciaDestino(
					desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteAux().getProvinciaDestino());
			desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporte().setCantonDestino(
					desechoPeligrosoTransporteBean.getDesechoPeligrosoTransporteAux().getCantonDestino());
		}

		prepararNuevo();

	}
}
