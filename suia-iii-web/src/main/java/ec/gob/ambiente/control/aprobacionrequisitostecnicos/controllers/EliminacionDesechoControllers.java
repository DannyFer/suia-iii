/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AgregarDesechoPeligroso;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.EliminacionDesechosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.TipoEliminacionDesechoModalBean;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.EliminacionDesechoFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionRecepcion;
import ec.gob.ambiente.suia.dto.EntityRecepcionDesecho;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * <b> Clase controller para la eliminacion de desecho. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 11/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class EliminacionDesechoControllers {

	private static final Logger LOG = Logger.getLogger(EliminacionDesechoControllers.class);
	private static final String REQUERIDO_POST = "' es requerido.";
	private static final String REQUERIDO_PRE = "El campo '";
	private static final Double VALOR_CERO = 0.0;
	private static final String CODIGO_ES_04 = "ES-04";
	private static final String CODIGO_ES_06 = "ES-06";
	private static final String MOSTRAR_DIALOG = "@(body)";
	public static final String[] TIPOS_ELIMINACION_OTROS = new String[] { "OR1","OMT1", "R2", "RE3", "RE4", "RM7", "RS5",
			"CP4", "TDB4", "DF3" };

	/**
     *
     */
//	private static final long serialVersionUID = 1L;

	@Setter
	@Getter
	@ManagedProperty(value = "#{eliminacionDesechosBean}")
	private EliminacionDesechosBean eliminacionDesechosBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{tipoEliminacionDesechoModalBean}")
	private TipoEliminacionDesechoModalBean tipoEliminacionDesechoModalBean;

	@EJB
	private EliminacionDesechoFacade eliminacionDesechoFacade;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@Setter
	@Getter
	@ManagedProperty(value = "#{agregarDesechoPeligroso}")
	private AgregarDesechoPeligroso agregarDesechoPeligroso;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@Setter
	@Getter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Setter
	@Getter
	private Map<EntityRecepcionDesecho,EliminacionDesecho> paresExistentesMap;//Para validar los pares EntityRecepcionDesecho - EliminacionDesecho existentes. Estos no
																				//Deben repetirse.

	@PostConstruct
	public void init() {
		eliminacionDesechosBean.setEliminacionesRecepciones(new ArrayList<EliminacionRecepcion>());
		eliminacionDesechosBean.setEliminacionDesechos(new ArrayList<EliminacionDesecho>());
		eliminacionDesechosBean.setListaEntityRecepcionDesecho(new ArrayList<EntityRecepcionDesecho>());
		eliminacionDesechosBean.setEliminacionDesechosVer(new ArrayList<EliminacionDesecho>());
		eliminacionDesechosBean.setEliminacionDesechoAux(new EliminacionDesecho());

		this.paresExistentesMap = new HashMap<EntityRecepcionDesecho, EliminacionDesecho>();

		try {
			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos();
			if (existeIngresoRecepcion()) {
				validacionesTipoEliminacionDesechos();
				iniciarDatos();
				cargarCaracterisiticas();
				aprobacionRequisitosTecnicosBean.verART(EliminacionRecepcion.class.getName());
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
		if (eliminacionDesechosBean.getListaEntityRecepcionDesecho() == null
				|| eliminacionDesechosBean.getListaEntityRecepcionDesecho().isEmpty()) {
			existe = false;
		}
		return existe;
	}

	/**
	 * 
	 * <b> Metodo para la validacion del tipo de eliminacion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 14/07/2015]
	 * </p>
	 * 
	 */
	public void validacionesTipoEliminacionDesechos() {
		tipoEliminacionDesechoModalBean.setCompleteOperationOnAdd(new CompleteOperation() {

			@Override
			public Object endOperation(Object object) {
				boolean resultado = tipoEliminacionDesechoModalBean.isClaveIgual(TIPOS_ELIMINACION_OTROS);
				if (resultado) {
					tipoEliminacionDesechoModalBean.setOtroSeleccionado(true);
				}
				return null;
			}
		});
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
		if (eliminacionDesechosBean.getCantones() == null) {
			eliminacionDesechosBean.setCantones(new ArrayList<UbicacionesGeografica>());
		}
		if (eliminacionDesechosBean.getProvincias() == null) {
			eliminacionDesechosBean.setProvincias(new ArrayList<UbicacionesGeografica>());
		}
		eliminacionDesechosBean.setProvincias(ubicacionGeograficaFacade.getProvincias());

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
		eliminacionDesechosBean.setCantones(new ArrayList<UbicacionesGeografica>());
		if (eliminacionDesechosBean.getProvinciaDestino() == null) {
			eliminacionDesechosBean.setCantones(new ArrayList<UbicacionesGeografica>());
		} else {
			eliminacionDesechosBean.setCantones(ubicacionGeograficaFacade.getCantonesParroquia(eliminacionDesechosBean
					.getProvinciaDestino()));
		}
	}

	/**
	 * 
	 * <b> Metodo que inicializa toda las variables para las eliminaciones. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 29/06/2015]
	 * </p>
	 * 
	 */
	private void iniciarDatos() {
		eliminacionDesechosBean.setEliminacionRecepcionesEliminar(new ArrayList<EliminacionRecepcion>());
		eliminacionDesechosBean.setEliminacionDesechos(new ArrayList<EliminacionDesecho>());
		eliminacionDesechosBean.setEliminacionesDesechosEliminar(new ArrayList<EliminacionDesecho>());
		tipoEliminacionDesechoModalBean.setTipoEliminacionDesechoSeleccionada(null);
		eliminacionDesechosBean.setEliminacionesRecepcionesModificacion(new ArrayList<EliminacionRecepcion>());
		obtenerListaEliminaciones();
	}

	/**
	 * 
	 * <b> Metodo que obtiene la lista de elimaciones. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 */
	private void obtenerListaEliminaciones() {
		try {
			List<EliminacionRecepcion> listaEliminacionRecepcion = eliminacionDesechoFacade
					.listarEliminacionPorAprobacionRequistos(aprobacionRequisitosTecnicos.getId());
			if (listaEliminacionRecepcion != null && !listaEliminacionRecepcion.isEmpty()) {
				eliminacionDesechosBean.setEliminacionesRecepciones(listaEliminacionRecepcion);
				int indice = 0;
				for (EliminacionRecepcion eli : eliminacionDesechosBean.getEliminacionesRecepciones()) {
					eli.setIndice(indice);
					entityRecepcion(eli);
					cargarDetalleEliminacion(eli.getEliminacionDesechos());
					indice++;
				}
			}
		} catch (Exception e) {
			LOG.error("Error al obtener los desechos.", e);
			JsfUtil.addMessageError("No se puede obtener los almacenes.");
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
	 * @param eliminacionRecepcion
	 *            : Eliminacion recepcion
	 */
	public void entityRecepcion(EliminacionRecepcion eliminacionRecepcion) {
		for (EntityRecepcionDesecho e : eliminacionDesechosBean.getListaEntityRecepcionDesecho()) {
			if (eliminacionRecepcion.getIdRecepcionDesechoPeligroso().intValue() == e.getIdRecepcion()) {
				eliminacionRecepcion.setEntityRecepcionDesecho(e);
				break;
			}
		}
	}

	/**
	 * 
	 * <b> Metodo que carga los desechos de cada una de las eliminacion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 * @param listaEliminacionDesecho
	 *            : Lista de eliminacion
	 * @throws ServiceException
	 *             : Excepcion
	 */
	private void cargarDetalleEliminacion(List<EliminacionDesecho> listaEliminacionDesecho) throws ServiceException {
		int indice = 0;
		for (EliminacionDesecho eliDes : listaEliminacionDesecho) {
			eliDes.setIndice(indice);
			if (eliDes.getDesecho() != null) {
				eliDes.setDesecho(eliminacionDesechoFacade.obtenerDesecho(eliDes.getIdDesecho()));
			}
			indice++;
		}
	}

	/**
	 * 
	 * <b> Metodo que obtiene las lista de los desechos. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 */
	private void obtenerListaDesechos() {
		try {
			List<EntityRecepcionDesecho> enti = eliminacionDesechoFacade
					.obtenerPorAprobacionRequisitosTecnicos(getAprobacionRequisitosTecnicos().getId());
			eliminacionDesechosBean.setListaEntityRecepcionDesecho(enti);
		} catch (Exception e) {
			LOG.error("Error al obtener los desechos.", e);
			JsfUtil.addMessageError("No se puede obtener los almacenes.");
		}
	}

	/**
	 * 
	 * <b> Metodo para adicionar una disposicion o eliminacion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 */
	public void adicionarDisposicion() {
		eliminacionDesechosBean.setEliminacionRecepcion(new EliminacionRecepcion());
		eliminacionDesechosBean.getEliminacionRecepcion().setEliminacionDesechos(new ArrayList<EliminacionDesecho>());
		eliminacionDesechosBean.getEliminacionRecepcion().setAprobacionRequisitosTecnicos(aprobacionRequisitosTecnicos);
		eliminacionDesechosBean.setProvinciaDestino(new UbicacionesGeografica());
		eliminacionDesechosBean.setEliminacionRecepcionAux(new EliminacionRecepcion());
	}

	/**
	 * 
	 * <b> Metodo que guarda la disposicion o eliminacion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 */
	public void guardarDisposicion() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (validarGuardarDisposicion()) {

			if (eliminacionDesechosBean.getEliminacionRecepcion().isEditar()) {
				if (!eliminacionDesechosBean.getEliminacionRecepcion().equals(
						eliminacionDesechosBean.getEliminacionRecepcionAux())) {
					if (existeModificados()) {
						eliminacionDesechosBean.getEliminacionesRecepcionesModificacion().set(
								eliminacionDesechosBean.getIndiceModificadoExiste(),
								eliminacionDesechosBean.getEliminacionRecepcion());
					} else {
						eliminacionDesechosBean.getEliminacionesRecepcionesModificacion().add(
								eliminacionDesechosBean.getEliminacionRecepcion());
					}
				}
			} else {
				eliminacionDesechosBean.getEliminacionRecepcion().setIndice(
						eliminacionDesechosBean.getEliminacionesRecepciones().size());
				eliminacionDesechosBean.getEliminacionesRecepciones().add(
						eliminacionDesechosBean.getEliminacionRecepcion());
				eliminacionDesechosBean.getEliminacionesRecepcionesModificacion().add(
						eliminacionDesechosBean.getEliminacionRecepcion());
			}
			context.addCallbackParam("disposicionIn", true);

		} else {
			context.addCallbackParam("disposicionIn", false);
		}
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
		for (EliminacionRecepcion el : eliminacionDesechosBean.getEliminacionesRecepcionesModificacion()) {

			if (el.getIndice() == eliminacionDesechosBean.getEliminacionRecepcion().getIndice()) {
				eliminacionDesechosBean.setIndiceModificadoExiste(indice);
				existe = true;
				break;
			}
			indice++;
		}
		return existe;
	}

	/**
	 * 
	 * <b> Metodo para agregar un desecho de la lista. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 */
	public void adicionarDesechoPeligroso() {//Hereeeee
		RequestContext context = RequestContext.getCurrentInstance();
		if (validateDesechosPeligrosos()) {
			if(!existePar()|| eliminacionDesechosBean.getEliminacionDesecho().isEditar()){
				if (eliminacionDesechosBean.getEliminacionDesecho().isEsDesechoPeligro()) {
					eliminacionDesechosBean.getEliminacionDesecho().setDesecho(
							agregarDesechoPeligroso.getDesechoSeleccionado());
					eliminacionDesechosBean.getEliminacionDesecho().setCodigoGenerador(
							agregarDesechoPeligroso.getCodigoGenerador());
				}
				if (eliminacionDesechosBean.getEliminacionDesecho().isEditar()) {
					eliminacionDesechosBean
							.getEliminacionRecepcion()
							.getEliminacionDesechos()
							.set(eliminacionDesechosBean.getEliminacionDesecho().getIndice(),
									eliminacionDesechosBean.getEliminacionDesecho());
				} else {

					eliminacionDesechosBean.getEliminacionDesecho().setTipoEliminacionDesecho(
							tipoEliminacionDesechoModalBean.getTipoEliminacionDesechoSeleccionada());
					eliminacionDesechosBean.getEliminacionDesecho().setIndice(
							eliminacionDesechosBean.getEliminacionRecepcion().getEliminacionDesechos().size());
					eliminacionDesechosBean.getEliminacionRecepcion().getEliminacionDesechos()
							.add(eliminacionDesechosBean.getEliminacionDesecho());

				}
				adicionarDesecho();
				tipoEliminacionDesechoModalBean.setTipoEliminacionDesechoSeleccionada(null);
				//JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				context.addCallbackParam("desechoIn", true);
			}
			else{
				JsfUtil.addMessageError("Debe tener en cuenta que un mismo desecho no puede estar asociado a  un " +
						"mismo tipo de operación de la modalidad más de una vez.");
			}
		} else {
			context.addCallbackParam("desechoIn", false);
		}

	}

	private boolean existePar(){

		boolean exists = false;
		int pos = 0;
		int i;

		EntityRecepcionDesecho currentEntity;
		List<EliminacionDesecho> currentEliminationList;

		while(!exists && pos < eliminacionDesechosBean.getEliminacionesRecepciones().size()){

			currentEntity = eliminacionDesechosBean.getEliminacionesRecepciones().get(pos).getEntityRecepcionDesecho();

			if(currentEntity!=null && currentEntity.equals(eliminacionDesechosBean.getEliminacionRecepcion().getEntityRecepcionDesecho())){

				currentEliminationList = eliminacionDesechosBean.getEliminacionesRecepciones().get(pos).getEliminacionDesechos();

				i = 0;
				while(!exists && i < currentEliminationList.size()){
					if(currentEliminationList.get(i).getTipoEliminacionDesecho().equals(
							tipoEliminacionDesechoModalBean.getTipoEliminacionDesechoSeleccionada()))
						exists = true;
					else
						i++;
				}

			}
				pos++;
		}

		return exists;
	}

	/**
	 * 
	 * <b> Metodo que valida todos los campos de los desechos. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 * @return boolean: true si no hay errores
	 */
	public boolean validateDesechosPeligrosos() {
		List<String> listaMensajes = new ArrayList<String>();
		boolean validar;
		if (tipoEliminacionDesechoModalBean.getTipoEliminacionDesechoSeleccionada() == null) {
			listaMensajes.add(REQUERIDO_PRE + "Tipo de eliminación o disposición final" + REQUERIDO_POST);

		}
		if (eliminacionDesechosBean.getEliminacionDesecho().isEsDesechoPeligro()
				&& (agregarDesechoPeligroso.getDesechoSeleccionado().getClave() == null || agregarDesechoPeligroso
						.getDesechoSeleccionado().getClave().isEmpty())) {
			listaMensajes.add("Seleccionar el desecho peligroso");

		}
		if (eliminacionDesechosBean.getEliminacionDesecho().getCantidad() == null
				|| eliminacionDesechosBean.getEliminacionDesecho().getCantidad().equals(VALOR_CERO)) {
			listaMensajes.add(REQUERIDO_PRE + "Cantidad toneladas" + REQUERIDO_POST);
		}

		if (eliminacionDesechosBean.getEliminacionDesecho().isDesechoEspecial()
				&& (eliminacionDesechosBean.getEliminacionDesecho().getCantidadEspecial() == null || eliminacionDesechosBean
						.getEliminacionDesecho().getCantidadEspecial().equals(VALOR_CERO))) {
			listaMensajes.add(REQUERIDO_PRE + "Cantidad unidades" + REQUERIDO_POST);

		}

		if (tipoEliminacionDesechoModalBean.isOtroSeleccionado()
				&& (eliminacionDesechosBean.getEliminacionDesecho().getOtroDesecho() == null || eliminacionDesechosBean
						.getEliminacionDesecho().getOtroDesecho().isEmpty())) {
			listaMensajes.add(REQUERIDO_PRE + "Descripción de Otros métodos " + REQUERIDO_POST);

		}

		/*if (!eliminacionDesechosBean.getEliminacionDesecho().isEsDesechoPeligro()
				&& (eliminacionDesechosBean.getEliminacionDesecho().getNombreDesecho() == null || eliminacionDesechosBean
						.getEliminacionDesecho().getNombreDesecho().isEmpty())) {
			listaMensajes.add(REQUERIDO_PRE + "nombre de desecho no peligroso" + REQUERIDO_POST);
		}*/

		/*if (eliminacionDesechosBean.getProvinciaDestino() == null) {
			listaMensajes.add(REQUERIDO_PRE + "Provincia" + REQUERIDO_POST);
		}*/

		/*if (eliminacionDesechosBean.getEliminacionDesecho().getCantonDestino() == null) {
			listaMensajes.add(REQUERIDO_PRE + "Canton" + REQUERIDO_POST);
		}*/

		if (listaMensajes.isEmpty()) {
			validar = true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			validar = false;
		}
		return validar;
	}

	/**
	 * 
	 * <b> Metodo para guardar la disposicion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 * @return boolean: true si no ha errores
	 */
	public boolean validarGuardarDisposicion() {
		List<String> listaMensajes = new ArrayList<String>();
		boolean validar;
		if (eliminacionDesechosBean.getEliminacionRecepcion().getEntityRecepcionDesecho() == null) {
			listaMensajes.add(REQUERIDO_PRE + "Desecho" + REQUERIDO_POST);
		}
		if (eliminacionDesechosBean.getEliminacionRecepcion().getEliminacionDesechos().isEmpty()) {
			listaMensajes.add("Debe adicionar desechos.");
		}
		if (listaMensajes.isEmpty()) {
			validar = true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			validar = false;
		}
		return validar;
	}

	/**
	 * 
	 * <b> Metodo que valida si un desecho es especial. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 */
	public void validarDesechoEspecial() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (!eliminacionDesechosBean.getEliminacionRecepcion().getEliminacionDesechos().isEmpty()
				&& ("ES-04".equals(eliminacionDesechosBean.getEliminacionRecepcion().getEntityRecepcionDesecho()
						.getCodigo()) || "ES-06".equals(eliminacionDesechosBean.getEliminacionRecepcion()
						.getEntityRecepcionDesecho().getCodigo()))) {
			context.addCallbackParam("eliminarRegistro", true);
		} else {
			context.addCallbackParam("eliminarRegistro", false);
		}
	}

	/**
	 * 
	 * <b> Metodo que inicializa la lista de los desechos. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 */
	public void eliminarDesechos() {
		eliminacionDesechosBean.setEliminacionesDesechosEliminar(eliminacionDesechosBean.getEliminacionRecepcion()
				.getEliminacionDesechos());
		eliminacionDesechosBean.getEliminacionRecepcion().setEliminacionDesechos(new ArrayList<EliminacionDesecho>());
	}

	/**
	 * 
	 * <b> Metodo para editar una disposicion o eliminacion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 * @param eliminacionRecepcion
	 *            : eliminacion o disposicion final
	 */
	public void seleccionarDisposicion(EliminacionRecepcion eliminacionRecepcion) {
		eliminacionRecepcion.setEditar(true);
		eliminacionDesechosBean.setEliminacionRecepcion(eliminacionRecepcion);
		eliminacionDesechosBean.setEliminacionRecepcionAux((EliminacionRecepcion) SerializationUtils
				.clone(eliminacionRecepcion));
	}

	/**
	 * 
	 * <b> Metodo que elimina una disposicion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 */
	public void eliminarDisposicion() {
		eliminacionDesechosBean.getEliminacionesRecepciones().remove(eliminacionDesechosBean.getEliminacionRecepcion());
		if (eliminacionDesechosBean.getEliminacionRecepcion().getId() != null) {
			if (eliminacionDesechosBean.getEliminacionRecepcion().getEliminacionDesechos() != null
					&& !eliminacionDesechosBean.getEliminacionRecepcion().getEliminacionDesechos().isEmpty()) {
				eliminacionDesechosBean.getEliminacionesDesechosEliminar().addAll(
						eliminacionDesechosBean.getEliminacionRecepcion().getEliminacionDesechos());
			}
			eliminacionDesechosBean.getEliminacionRecepcionesEliminar().add(
					eliminacionDesechosBean.getEliminacionRecepcion());
		}
	}

	/**
	 * 
	 * <b> Metodo que adiciona un desecho a la lista de la disposicion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 */
	public void adicionarDesecho() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (eliminacionDesechosBean.getEliminacionRecepcion().getEntityRecepcionDesecho() == null
				|| eliminacionDesechosBean.getEliminacionRecepcion().getEntityRecepcionDesecho().getCodigo() == null
				|| eliminacionDesechosBean.getEliminacionRecepcion().getEntityRecepcionDesecho().getCodigo().isEmpty()) {
			context.addCallbackParam("adicionar", false);
			JsfUtil.addMessageError("Debe seleccionar el desecho para continuar");
		} else {
			eliminacionDesechosBean.setEliminacionDesecho(new EliminacionDesecho());
			agregarDesechoPeligroso.setDesechoSeleccionado(new DesechoPeligroso());
			tipoEliminacionDesechoModalBean.resetSelection();
			eliminacionDesechosBean.setProvinciaDestino(new UbicacionesGeografica());
			eliminacionDesechosBean.setCantones(new ArrayList<UbicacionesGeografica>());
			if (CODIGO_ES_04.equals(eliminacionDesechosBean.getEliminacionRecepcion().getEntityRecepcionDesecho()
					.getCodigo())
					|| CODIGO_ES_06.equals(eliminacionDesechosBean.getEliminacionRecepcion()
							.getEntityRecepcionDesecho().getCodigo())) {
				eliminacionDesechosBean.getEliminacionDesecho().setDesechoEspecial(true);
			}
			JsfUtil.getBean(AgregarDesechoPeligroso.class).setRevisar(true);
			context.addCallbackParam("adicionar", true);
		}
	}

	/**
	 * 
	 * <b> Metodo que selecciona un desecho. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 * @param eliminacionDesecho
	 */
	public void seleccionarEliminacionDesecho(EliminacionDesecho eliminacionDesecho) {
		eliminacionDesechosBean.setEliminacionDesechoAux((EliminacionDesecho) SerializationUtils
				.clone(eliminacionDesecho));
		eliminacionDesecho.setEditar(true);
		eliminacionDesechosBean.setEliminacionDesecho(eliminacionDesecho);
		tipoEliminacionDesechoModalBean.setTipoEliminacionDesechoSeleccionada(eliminacionDesecho
				.getTipoEliminacionDesecho());
		boolean resultado = tipoEliminacionDesechoModalBean.isClaveIgual(TIPOS_ELIMINACION_OTROS);
		if (resultado) {
			tipoEliminacionDesechoModalBean.setOtroSeleccionado(true);
		}
		if (eliminacionDesechosBean.getEliminacionDesecho().isEsDesechoPeligro()) {
			agregarDesechoPeligroso.setDesechoSeleccionado(eliminacionDesecho.getDesecho());
		}
		UbicacionesGeografica dest = null;
		if(eliminacionDesecho.getCantonDestino()!=null)
			dest = eliminacionDesecho.getCantonDestino().getUbicacionesGeografica();

			eliminacionDesechosBean.setProvinciaDestino(dest);

		cargarCantones();
	}

	/**
	 * 
	 * <b> Metodo que elimina un desecho y lo coloca en la lista para eliminar.
	 * </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 * @param eliminacionDesecho
	 *            : eliminacion del desecho
	 */
	public void eliminarDesecho(EliminacionDesecho eliminacionDesecho) {
		eliminacionDesechosBean.getEliminacionRecepcion().getEliminacionDesechos()
				.remove(eliminacionDesecho.getIndice());
		if (eliminacionDesecho.getId() != null) {
			eliminacionDesechosBean.getEliminacionesDesechosEliminar().add(eliminacionDesecho);
		}
	}

	/**
	 * 
	 * <b> Metodo que guarda la lista de las disposiciones. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 */
	public void guardar() {
		try {
			if (validarGuardar()) {
				eliminacionDesechoFacade.guardarDisposicion(
						eliminacionDesechosBean.getEliminacionesRecepcionesModificacion(),
						eliminacionDesechosBean.getEliminacionRecepcionesEliminar(),
						eliminacionDesechosBean.getEliminacionesDesechosEliminar(),
						aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
				iniciarDatos();
				validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos",
						"eliminacionDisposicionFinal", aprobacionRequisitosTecnicosBean
								.getAprobacionRequisitosTecnicos().getId().toString());
				JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
				validarNumeroDesechosIngresados();
			}
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
			LOG.error(e, e);
		}
	}

	/**
	 * 
	 * <b> Metodo que valida la lista de las disposiciones. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 * @return boolean: true si no hay errores
	 */
	public boolean validarGuardar() {
		List<String> listaMensajes = new ArrayList<String>();
		boolean validar;
		if (eliminacionDesechosBean.getEliminacionesRecepciones().isEmpty()) {
			listaMensajes.add("Debe ingresar almenos una eliminación o disposición final.");
		}
		if (listaMensajes.isEmpty()) {
			validar = true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			validar = false;
		}
		return validar;
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
		if (eliminacionDesechosBean.getEliminacionRecepcion().isEditar()) {
			eliminacionDesechosBean.getEliminacionRecepcionAux().setIdRecepcionDesechoPeligroso(
					eliminacionDesechosBean.getEliminacionRecepcionAux().getEntityRecepcionDesecho().getIdRecepcion());
			entityRecepcion(eliminacionDesechosBean.getEliminacionRecepcionAux());
			eliminacionDesechosBean.getEliminacionesRecepciones().set(
					eliminacionDesechosBean.getEliminacionRecepcionAux().getIndice(),
					eliminacionDesechosBean.getEliminacionRecepcionAux());
			eliminacionDesechosBean.setEliminacionesRecepcionesModificacion(new ArrayList<EliminacionRecepcion>());
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
	public void cerrarModalDesechos() {
		if (!eliminacionDesechosBean.getEliminacionRecepcion().getEliminacionDesechos().isEmpty()
				&& eliminacionDesechosBean.getEliminacionDesechoAux().isEditar()) {
			eliminacionDesechosBean
					.getEliminacionRecepcion()
					.getEliminacionDesechos()
					.set(eliminacionDesechosBean.getEliminacionDesechoAux().getIndice(),
							eliminacionDesechosBean.getEliminacionDesechoAux());
		}

	}

	public void verEliminacionRecepcion(EliminacionRecepcion eliminacionRecepcion) {
		eliminacionDesechosBean.setEliminacionDesechosVer(eliminacionRecepcion.getEliminacionDesechos());
	}

	public void limpiarDatos() {
		if (eliminacionDesechosBean.getEliminacionDesecho().isEsDesechoPeligro()) {
			eliminacionDesechosBean.getEliminacionDesecho().setNombreDesecho(new String());
		} else {
			agregarDesechoPeligroso.setDesechoSeleccionado(new DesechoPeligroso());
			eliminacionDesechosBean.getEliminacionDesecho().setDesecho(null);
		}
	}

	private void validarNumeroDesechosIngresados() throws ServiceException {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean existe = eliminacionDesechoFacade.validarEliminacionRecepcionDesechos(aprobacionRequisitosTecnicosBean
				.getAprobacionRequisitosTecnicos().getId());
		if (existe) {
			context.addCallbackParam("numDesechosEliminacion", false);
		} else {
			context.addCallbackParam("numDesechosEliminacion", true);
		}

	}

}
