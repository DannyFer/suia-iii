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

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AlmacenamientoTemporalBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AlmacenFacade;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.Almacen;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AlmacenRecepcion;
import ec.gob.ambiente.suia.dto.EntityRecepcionDesecho;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * <b> Clase controller para el almacenamiento temporal. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 09/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class AlmacenamientoTemporalControllers implements Serializable {

	private static final Logger LOG = Logger.getLogger(AlmacenamientoTemporalControllers.class);
	private static final long serialVersionUID = 7384572568264083243L;
	private static final String REQUERIDO_POST = "' es requerido.";
	private static final String REQUERIDO_PRE = "El campo '";
	private static final Double VALOR_CERO = 0.0;
	private static final String ESTADO_LIQUIDO = "Líquido";
	private static final String ESTADO_SEMISOLIDO = "Semisólido";
	public static final String[] TIPOS_ELIMINACION_OTROS = new String[] { "OMT1", "R2", "RE3", "RE4", "RM7", "RS5",
			"CP4", "TDB4", "DF3" };

	@Getter
	@Setter
	private AlmacenamientoTemporalBean almacenamientoTemporalBean;

	@EJB
	private AlmacenFacade almacenFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@PostConstruct
	public void init() {
		almacenamientoTemporalBean = new AlmacenamientoTemporalBean();
		almacenamientoTemporalBean.setAlmacenes(new ArrayList<Almacen>());
		almacenamientoTemporalBean.setAlmacenRecepcion(new AlmacenRecepcion());
		almacenamientoTemporalBean.setAlmacenRecepcionAux(new AlmacenRecepcion());
		try {
			almacenamientoTemporalBean.setAprobacionRequisitosTecnicos(aprobacionRequisitosTecnicosBean
					.getAprobacionRequisitosTecnicos());
			if (existeIngresoRecepcion()) {
				obtenerCaracteristicas();
				iniciarDatos();
				aprobacionRequisitosTecnicosBean.verART(Almacen.class.getName());
			} else {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('importanteWdgt').show();");
			}

		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Error al cargar la aprobacion");
		}

	}

	/**
	 * 
	 * <b> Metodo para verificar la informacion que se completo la recepcion de
	 * desechos. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 21/08/2015]
	 * </p>
	 * 
	 * @return boolean: true si no existe la informacion
	 */
	private boolean existeIngresoRecepcion() {
		boolean existe = true;
		obtenerListaDesechos();
		if (almacenamientoTemporalBean.getListaEntityRecepcionDesecho() == null
				|| almacenamientoTemporalBean.getListaEntityRecepcionDesecho().isEmpty()) {
			existe = false;
		}
		return existe;
	}

	private void iniciarDatos() {
		almacenamientoTemporalBean.setAlmacenRecepcionEliminar(new ArrayList<AlmacenRecepcion>());
		almacenamientoTemporalBean.setAlmacenesEliminar(new ArrayList<Almacen>());
		almacenamientoTemporalBean.setAlmacenesModificacion(new ArrayList<Almacen>());
		obtenerListaAlmacenes();
	}

	private void obtenerListaDesechos() {
		try {
			almacenamientoTemporalBean.setListaEntityRecepcionDesecho(almacenFacade
					.obtenerPorAprobacionRequisitosTecnicos(almacenamientoTemporalBean
							.getAprobacionRequisitosTecnicos().getId()));
		} catch (Exception e) {
			LOG.error("Error al obtener los desechos.", e);
			JsfUtil.addMessageError("No se puede obtener los desechos.");
		}
	}

	private void obtenerListaAlmacenes() {
		try {
			List<Almacen> listaAlmacen = almacenFacade.listarAlmacenesPorAprobacionRequistos(almacenamientoTemporalBean
					.getAprobacionRequisitosTecnicos().getId());
			if (listaAlmacen != null && !listaAlmacen.isEmpty()) {
				almacenamientoTemporalBean.setAlmacenes(listaAlmacen);
				int indice = 0;
				for (Almacen alm : almacenamientoTemporalBean.getAlmacenes()) {
					alm.setIndice(indice);
					cargarDetalleAlmacen(alm.getAlmacenRecepciones());
					indice++;
				}
			}
		} catch (Exception e) {
			LOG.error("Error al obtener los almacenes.", e);
			JsfUtil.addMessageError("No se puede obtener los almacenes.");
		}
	}

	private void cargarDetalleAlmacen(List<AlmacenRecepcion> listaAlmacenRecepcion) {
		int indice = 0;
		for (AlmacenRecepcion almRecp : listaAlmacenRecepcion) {
			almRecp.setIndice(indice);
			for (EntityRecepcionDesecho e : almacenamientoTemporalBean.getListaEntityRecepcionDesecho()) {
				if (almRecp.getIdRecepcionDesechoPeligroso().intValue() == e.getIdRecepcion()) {
					almRecp.setEntityRecepcionDesecho(e);
					break;
				}
			}
			indice++;
		}
	}

	private void ponerIndiceAlmacen() {
		int i = 0;
		for (Almacen a : almacenamientoTemporalBean.getAlmacenes()) {
			a.setIndice(i);
			i++;
		}
	}

	private void ponerIndiceDesecho(List<AlmacenRecepcion> listaAlmacenRecepcion) {
		int i = 0;
		for (AlmacenRecepcion ar : listaAlmacenRecepcion) {
			ar.setIndice(i);
			i++;
		}
	}

	public void adicionarAlmacen() {
		almacenamientoTemporalBean.setAlmacen(new Almacen());
		almacenamientoTemporalBean.getAlmacen().setAlmacenRecepciones(new ArrayList<AlmacenRecepcion>());
		almacenamientoTemporalBean.getAlmacen().setAprobacionRequisitosTecnicos(
				almacenamientoTemporalBean.getAprobacionRequisitosTecnicos());
		almacenamientoTemporalBean.setAlmacenAux(new Almacen());
		almacenamientoTemporalBean.setMostrarFosasRetencion(false);
	}

	public void guardarAlmacen() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (validarGuardarAlmacen()) {

			almacenamientoTemporalBean.getAlmacen().setCantidad(
					almacenamientoTemporalBean.getAlmacen().getAltura()
							* almacenamientoTemporalBean.getAlmacen().getAncho()
							* almacenamientoTemporalBean.getAlmacen().getLargo());
			if (almacenamientoTemporalBean.getAlmacen().isEditar()) {
				if (!almacenamientoTemporalBean.getAlmacen().equals(almacenamientoTemporalBean.getAlmacenAux())
						|| almacenamientoTemporalBean.getAlmacen().isModificado() != almacenamientoTemporalBean
								.getAlmacenAux().isModificado()
						|| almacenamientoTemporalBean.getAlmacenAux().getAlmacenRecepciones().size() < almacenamientoTemporalBean
								.getAlmacen().getAlmacenRecepciones().size()) {

					if (existeModificados()) {
						almacenamientoTemporalBean.getAlmacenesModificacion().set(
								almacenamientoTemporalBean.getIndiceModificadoExiste(),
								almacenamientoTemporalBean.getAlmacen());
					} else {
						almacenamientoTemporalBean.getAlmacenesModificacion().add(
								almacenamientoTemporalBean.getAlmacen());
					}
				}
			} else {
				almacenamientoTemporalBean.getAlmacenesModificacion().add(almacenamientoTemporalBean.getAlmacen());
				almacenamientoTemporalBean.getAlmacenes().add(almacenamientoTemporalBean.getAlmacen());
				ponerIndiceAlmacen();
			}
			context.addCallbackParam("almacenIn", true);
		} else {
			context.addCallbackParam("almacenIn", false);
		}
	}

	public boolean validarGuardarAlmacen() {
		List<String> listaMensajes = new ArrayList<String>();
		if (almacenamientoTemporalBean.getAlmacen().getIdentificacion() == null
				|| almacenamientoTemporalBean.getAlmacen().getIdentificacion().isEmpty()) {
			listaMensajes.add(REQUERIDO_PRE + "Identificación de Almacén" + REQUERIDO_POST);
		}
		if (almacenamientoTemporalBean.getAlmacen().getAlmacenRecepciones().isEmpty()) {
			listaMensajes.add("Debe adicionar desechos.");
		}
		if (almacenamientoTemporalBean.getAlmacen().getTipoLocal() == null) {
			listaMensajes.add(REQUERIDO_PRE + "Local" + REQUERIDO_POST);
		}
		if (almacenamientoTemporalBean.getAlmacen().getTipoMaterialConstruccion() == null) {
			listaMensajes.add(REQUERIDO_PRE + "Material" + REQUERIDO_POST);
		}
		if (almacenamientoTemporalBean.getAlmacen().getTipoVentilacion() == null) {
			listaMensajes.add(REQUERIDO_PRE + "Ventilación" + REQUERIDO_POST);
		}
		if (almacenamientoTemporalBean.getAlmacen().getTipoIluminacion() == null) {
			listaMensajes.add(REQUERIDO_PRE + "Iluminación" + REQUERIDO_POST);
		}
		if (almacenamientoTemporalBean.getAlmacen().getLargo().equals(VALOR_CERO)) {
			listaMensajes.add(REQUERIDO_PRE + "Largo" + REQUERIDO_POST);
		}
		if (almacenamientoTemporalBean.getAlmacen().getAncho().equals(VALOR_CERO)) {
			listaMensajes.add(REQUERIDO_PRE + "Ancho" + REQUERIDO_POST);
		}
		if (almacenamientoTemporalBean.getAlmacen().getAltura().equals(VALOR_CERO)) {
			listaMensajes.add(REQUERIDO_PRE + "Altura" + REQUERIDO_POST);
		}
		if (almacenamientoTemporalBean.isMostrarFosasRetencion()
				&& almacenamientoTemporalBean.getAlmacen().getCapacidadFosas().equals(VALOR_CERO)) {
			listaMensajes.add(REQUERIDO_PRE + "Capacidad de fosas" + REQUERIDO_POST);
		}
		if (almacenamientoTemporalBean.getAlmacen().getExtincionIncendio() == null
				|| almacenamientoTemporalBean.getAlmacen().getExtincionIncendio().isEmpty()) {
			listaMensajes
					.add(REQUERIDO_PRE + "Breve descripción de sistema de extinción de incendios" + REQUERIDO_POST);
		}
		if (buscarIdAlmacen()) {
			listaMensajes.add("La identificación del Almacén introducido ya existe.");
		}
		if (listaMensajes.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			return false;
		}
	}

	public boolean buscarIdAlmacen() {
		boolean existe = false;
		if (almacenamientoTemporalBean.getAlmacen().isEditar()) {
			if (!almacenamientoTemporalBean.getAlmacen().getIdentificacion()
					.equals(almacenamientoTemporalBean.getAlmacenAux().getIdentificacion())) {
				existe = buscarIdentificacionAlmacen();
			}
		} else {
			existe = buscarIdentificacionAlmacen();
		}
		return existe;
	}

	public boolean buscarIdentificacionAlmacen() {
		boolean existe = false;
		for (Almacen alm : almacenamientoTemporalBean.getAlmacenes()) {
			if (alm.getIdentificacion().equalsIgnoreCase(almacenamientoTemporalBean.getAlmacen().getIdentificacion())) {
				existe = true;
				break;
			}
		}
		return existe;
	}

	public void seleccionarAlmacen(Almacen almacen) {
		almacen.setEditar(true);
		almacenamientoTemporalBean.setAlmacen(almacen);
		almacenamientoTemporalBean.setAlmacenAux((Almacen) SerializationUtils.clone(almacen));
		validarFosasRetencion(almacenamientoTemporalBean.getAlmacen().getAlmacenRecepciones());
	}

	public void eliminarAlmacen() {
		almacenamientoTemporalBean.getAlmacenes().remove(almacenamientoTemporalBean.getAlmacen().getIndice());
		if (almacenamientoTemporalBean.getAlmacen().getId() != null) {
			if (almacenamientoTemporalBean.getAlmacen().getAlmacenRecepciones() != null
					&& !almacenamientoTemporalBean.getAlmacen().getAlmacenRecepciones().isEmpty()) {
				almacenamientoTemporalBean.getAlmacenRecepcionEliminar().addAll(
						almacenamientoTemporalBean.getAlmacen().getAlmacenRecepciones());
			}
			almacenamientoTemporalBean.getAlmacenesEliminar().add(almacenamientoTemporalBean.getAlmacen());
		}
		ponerIndiceAlmacen();
	}

	public void adicionarDesecho() {
		almacenamientoTemporalBean.setAlmacenRecepcion(new AlmacenRecepcion());
	}

	public void seleccionarAlmacenRecepcion(AlmacenRecepcion almacenRecepcion) {
		almacenRecepcion.setEditar(true);
		if (almacenRecepcion.getIdRecepcionDesechoPeligroso() != null) {
			entityRecepcion(almacenRecepcion);
		}

		almacenamientoTemporalBean.setAlmacenRecepcion(almacenRecepcion);
		almacenamientoTemporalBean.getAlmacenRecepcion().setEditar(true);
		almacenamientoTemporalBean
				.setAlmacenRecepcionAux((AlmacenRecepcion) SerializationUtils.clone(almacenRecepcion));
	}

	public void eliminarAlmacenRecepcion(AlmacenRecepcion almacenRecepcion) {
		almacenamientoTemporalBean.getAlmacen().getAlmacenRecepciones().remove(almacenRecepcion.getIndice());
		if (almacenRecepcion.getId() != null) {
			almacenamientoTemporalBean.getAlmacenRecepcionEliminar().add(almacenRecepcion);
		}
	}

	public void guardarDesecho() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (validarGuardarDesecho()) {
			if (almacenamientoTemporalBean.getAlmacenRecepcion().isEditar()) {
				almacenamientoTemporalBean.getAlmacen().setModificado(true);
				if (!almacenamientoTemporalBean.getAlmacenRecepcion().getTipoEnvase().isOtro()) {
					almacenamientoTemporalBean.getAlmacenRecepcion().setOtroTipoEnvase(new String());
				}
				if (almacenamientoTemporalBean.getAlmacen().isEditar()
						&& almacenamientoTemporalBean.getAlmacenAux().getAlmacenRecepciones().size() < almacenamientoTemporalBean
								.getAlmacen().getAlmacenRecepciones().size()) {
					almacenamientoTemporalBean.getAlmacen().getAlmacenRecepciones()
							.add(almacenamientoTemporalBean.getAlmacenRecepcion());
				}
			} else {
				almacenamientoTemporalBean.getAlmacen().getAlmacenRecepciones()
						.add(almacenamientoTemporalBean.getAlmacenRecepcion());
				ponerIndiceDesecho(almacenamientoTemporalBean.getAlmacen().getAlmacenRecepciones());
			}

			context.addCallbackParam("desechoIn", true);
		} else {
			context.addCallbackParam("desechoIn", false);
		}
		validarFosasRetencion(almacenamientoTemporalBean.getAlmacen().getAlmacenRecepciones());
		if (!almacenamientoTemporalBean.isMostrarFosasRetencion()) {
			almacenamientoTemporalBean.getAlmacen().setCapacidadFosas(null);
		}
	}

	public void validarFosasRetencion(List<AlmacenRecepcion> almacenRecepciones) {
		getAlmacenamientoTemporalBean().setMostrarFosasRetencion(false);
		for (AlmacenRecepcion almacenRecepcion : almacenRecepciones) {
			if (ESTADO_LIQUIDO.equalsIgnoreCase(almacenRecepcion.getEntityRecepcionDesecho().getTipoEstado())
					|| ESTADO_SEMISOLIDO.equalsIgnoreCase(almacenRecepcion.getEntityRecepcionDesecho().getTipoEstado())) {
				getAlmacenamientoTemporalBean().setMostrarFosasRetencion(true);
				break;
			}
		}
	}

	public boolean validarGuardarDesecho() {
		List<String> listaMensajes = new ArrayList<String>();
		if (almacenamientoTemporalBean.getAlmacenRecepcion().getEntityRecepcionDesecho() == null) {
			listaMensajes.add(REQUERIDO_PRE + "Desecho" + REQUERIDO_POST);
		}

		if (almacenamientoTemporalBean.getAlmacenRecepcion().getTipoEnvase() == null) {
			listaMensajes.add(REQUERIDO_PRE + "Tipo Envase" + REQUERIDO_POST);
		}
		if (almacenamientoTemporalBean.getAlmacenRecepcion().getTipoEnvase() != null
				&& almacenamientoTemporalBean.getAlmacenRecepcion().getTipoEnvase().isOtro()
				&& almacenamientoTemporalBean.getAlmacenRecepcion().getOtroTipoEnvase().isEmpty()) {
			listaMensajes.add(REQUERIDO_PRE + "Otro tipo de envase" + REQUERIDO_POST);
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
	 * <b> Metodo para llenar los combos para el almacenmiento. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 11/06/2015]
	 * </p>
	 * 
	 */
	private void obtenerCaracteristicas() {
		try {
			almacenamientoTemporalBean.setLocales(almacenFacade.getLocales());
			almacenamientoTemporalBean.setMateriales(almacenFacade.getMateriales());
			almacenamientoTemporalBean.setVentilaciones(almacenFacade.getVentilacion());
			almacenamientoTemporalBean.setIluminaciones(almacenFacade.getIluminacion());
			almacenamientoTemporalBean.setTipoEnvases(almacenFacade.getTipoEnvase());
		} catch (Exception e) {
			LOG.error("Error al obtener las caracteristicas.", e);
			JsfUtil.addMessageError("No se puede obtener los caracteristicas.");
		}
	}

	public void guardar() {
		try {
			if (validarGuardar()) {
				almacenFacade.guardarAlmacenes(almacenamientoTemporalBean.getAlmacenesModificacion(),
						almacenamientoTemporalBean.getAlmacenesEliminar(),
						almacenamientoTemporalBean.getAlmacenRecepcionEliminar());				
				init();
				validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos",
						"almacenamientoTemporal", almacenamientoTemporalBean.getAprobacionRequisitosTecnicos().getId()
								.toString());
				JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
				validarNumeroDesechosIngresados();

			}
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
			LOG.error(e, e);
		}
	}

	public boolean validarGuardar() {
		List<String> listaMensajes = new ArrayList<String>();
		if (almacenamientoTemporalBean.getAlmacenes().isEmpty()) {
			listaMensajes.add("Debe ingresar almenos un almacen.");
		}
		if (listaMensajes.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			return false;
		}
	}

	public void seleccionarAlmacenVer(Almacen almacen) {
		almacenamientoTemporalBean.setAlmacenVer(almacen);
	}

	/**
	 * 
	 * <b> Metodo que devuelve true si existe en la lista de los modificados.
	 * </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 14/07/2015]
	 * </p>
	 * 
	 * @return boolean: true si existe
	 */
	public boolean existeModificados() {
		int indice = 0;
		boolean existe = false;
		for (Almacen el : almacenamientoTemporalBean.getAlmacenesModificacion()) {

			if (el.getIndice() == almacenamientoTemporalBean.getAlmacen().getIndice()) {
				almacenamientoTemporalBean.setIndiceModificadoExiste(indice);
				existe = true;
				break;
			}
			indice++;
		}
		return existe;
	}

	/**
	 * 
	 * <b> Metodo que cierre el modal para la disposicion y vuelve al original.
	 * </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 14/07/2015]
	 * </p>
	 * 
	 */
	public void cerrarModal() {
		if (almacenamientoTemporalBean.getAlmacen().isEditar()) {
			almacenamientoTemporalBean.getAlmacenes().set(almacenamientoTemporalBean.getAlmacenAux().getIndice(),
					almacenamientoTemporalBean.getAlmacenAux());
			almacenamientoTemporalBean.setAlmacenesModificacion(new ArrayList<Almacen>());
		}

	}

	/**
	 * 
	 * <b> Metodo qe cierra el modal de los desechos y vuelve al original. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 14/07/2015]
	 * </p>
	 * 
	 */
	public void cerrarModalRecepciones() {
		if (!almacenamientoTemporalBean.getAlmacen().getAlmacenRecepciones().isEmpty()
				&& almacenamientoTemporalBean.getAlmacenRecepcion().isEditar()) {
			almacenamientoTemporalBean.getAlmacenRecepcionAux().setIdRecepcionDesechoPeligroso(
					almacenamientoTemporalBean.getAlmacenRecepcionAux().getEntityRecepcionDesecho().getIdRecepcion());
			almacenamientoTemporalBean.setAlmacenRecepcionAux(entityRecepcion(almacenamientoTemporalBean
					.getAlmacenRecepcionAux()));
			almacenamientoTemporalBean
					.getAlmacen()
					.getAlmacenRecepciones()
					.set(almacenamientoTemporalBean.getAlmacenRecepcionAux().getIndice(),
							almacenamientoTemporalBean.getAlmacenRecepcionAux());
		}

	}

	/**
	 * 
	 * <b> Metodo para obtener la entidad de la lista para la eliminacion de
	 * recepcion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 14/07/2015]
	 * </p>
	 * 
	 * @param almacenRecepcion
	 *            : almacen recepcion
	 * @return AlmacenRecepcion: almacen recepcion
	 */
	public AlmacenRecepcion entityRecepcion(AlmacenRecepcion almacenRecepcion) {
		for (EntityRecepcionDesecho e : almacenamientoTemporalBean.getListaEntityRecepcionDesecho()) {
			if (almacenRecepcion.getIdRecepcionDesechoPeligroso().intValue() == e.getIdRecepcion()) {
				almacenRecepcion.setEntityRecepcionDesecho(e);
				break;
			}
		}
		return almacenRecepcion;
	}

	private void validarNumeroDesechosIngresados() throws ServiceException {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean existe = almacenFacade.validarAlmacenRecepcionDesechos(aprobacionRequisitosTecnicosBean
				.getAprobacionRequisitosTecnicos().getId());
		if (existe) {
			context.addCallbackParam("numDesechosAlmacen", false);
		} else {
			context.addCallbackParam("numDesechosAlmacen", true);
		}

	}
	public boolean isVisibleOpcionNavegarSiguienteEnMenuModoVer(boolean isModoVer) {
		String page = recuperarPageSiguiente(isModoVer);
		return !(page.contains("envioAprobacionRequisitosVer"));
	}

	public String recuperarPageSiguiente(boolean isModoVer) {
		String extensionModo = isModoVer ? "Ver.jsf" : ".jsf";
		if ((aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestionSoloModalidadOtros() || aprobacionRequisitosTecnicosBean
				.getAprobacionRequisitosTecnicos().isGestionSoloConModalidadOtrosYTransporte())) {
			return "/control/aprobacionRequisitosTecnicos/envioAprobacionRequisitos" + extensionModo
					+ "?faces-redirect=true";

		}

		return "/control/aprobacionRequisitosTecnicos/gestion/eliminacionDisposicionFinal" + extensionModo
				+ "?faces-redirect=true";

	}
}
