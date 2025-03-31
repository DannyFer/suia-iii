package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.LavadoContenedoresYTratamientoEfluentesBean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.VerProyectoBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.DesechoPeligrosoTransporteFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.RecoleccionTransporteDesechosFacade;
import ec.gob.ambiente.suia.desechoPeligroso.facade.DesechoPeligrosoFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoEspecialRecoleccion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.LavadoContenedoresYTratamientoEfluentes;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

@ManagedBean
@ViewScoped
public class LavadoContenedoresYTratamientoEfluentesController {

	@Setter
	@Getter
	@ManagedProperty(value = "#{lavadoContenedoresYTratamientoEfluentesBean}")
	private LavadoContenedoresYTratamientoEfluentesBean lavadoContenedoresYTratamientoEfluentesBean;

	@EJB
	private RecoleccionTransporteDesechosFacade recoleccionTransporteDesechosFacade;

	@EJB
	private DesechoPeligrosoTransporteFacade desechoPeligrosoTransportacionFacade;

	@EJB
	private DesechoPeligrosoFacade desechoPeligrosoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;


	@Getter
	@Setter
	@ManagedProperty(value = "#{verProyectoBean}")
	private VerProyectoBean verProyectoBean;

	private static final Logger LOGGER = Logger.getLogger(LavadoContenedoresYTratamientoEfluentesController.class);
	private static final String EX_POST = "Ex-post";

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;
	@Getter
	@Setter
	private boolean exPost;

	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;


	@PostConstruct
	private void init() {

		obtenerRegistrosLavado();


		aprobacionRequisitosTecnicosBean.verART(LavadoContenedoresYTratamientoEfluentes.class.getName());
		lavadoContenedoresYTratamientoEfluentesBean.setLavadoContenedoresYTratamientoEfluentes(new LavadoContenedoresYTratamientoEfluentes());
		lavadoContenedoresYTratamientoEfluentesBean.setListaLavadosContenedoresModificados(new ArrayList<LavadoContenedoresYTratamientoEfluentes>());
		lavadoContenedoresYTratamientoEfluentesBean.setListaLavadosContenedoresEliminados(new ArrayList<LavadoContenedoresYTratamientoEfluentes>());
		lavadoContenedoresYTratamientoEfluentesBean.setDesechosEspeciales(new ArrayList<DesechoEspecialRecoleccion>());
		lavadoContenedoresYTratamientoEfluentesBean.setListaFrecuenciasLavado(recoleccionTransporteDesechosFacade.getPeriodicidad());

		obtenerDesechosPrevios();
		try {
			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosFacade
                    .recuperarCrearAprobacionRequisitosTecnicos(JsfUtil.getCurrentProcessInstanceId(),
							JsfUtil.getLoggedUser());

			if(this.aprobacionRequisitosTecnicos.getVoluntario()){
				exPost =false;
				lavadoContenedoresYTratamientoEfluentesBean.setMostrarFotografia(false);
			}
			else
			if(proyectosBean.getProyecto()!=null && EX_POST.equalsIgnoreCase(proyectosBean.getProyecto().getTipoEstudio().getNombre())){
				exPost =true;
				lavadoContenedoresYTratamientoEfluentesBean.setMostrarFotografia(true);

			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}




		/*if (verProyectoBean.getProyecto().getTipoEstudio() == null || proyectosBean.getProyecto().getTipoEstudio() == null) {
			lavadoContenedoresYTratamientoEfluentesBean.setMostrarFotografia(false);
		} else if (EX_POST.equalsIgnoreCase(proyectosBean.getProyecto().getTipoEstudio().getNombre())) {
			lavadoContenedoresYTratamientoEfluentesBean.setMostrarFotografia(true);

		} else {
			lavadoContenedoresYTratamientoEfluentesBean.setMostrarFotografia(false);
		}*/

		//lavadoContenedoresYTratamientoEfluentesBean.setMostrarFotografia(true);

	}

	/**
	 * 
	 * <b> Metodo que obtiene todos los registros de lavado. </b>
	 * <p>
	 * [Author: Vero, Date: 09/07/2015]
	 * </p>
	 * 
	 */
	private void obtenerRegistrosLavado() {
		lavadoContenedoresYTratamientoEfluentesBean.setListaLavadosContenedores(recoleccionTransporteDesechosFacade.getListaLavadoContenedor(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
		if (lavadoContenedoresYTratamientoEfluentesBean.getListaLavadosContenedores() == null) {
			lavadoContenedoresYTratamientoEfluentesBean.setListaLavadosContenedores(new ArrayList<LavadoContenedoresYTratamientoEfluentes>());
		}
	}

	/**
	 * 
	 * <b> Metodo que obtiene los desechos especiales en la categoria Q. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/07/2015]
	 * </p>
	 * 
	 */
	public void obtenerDesechosPrevios() {

		try {
			List<DesechoEspecialRecoleccion> desechosRecoleccion = desechoPeligrosoTransportacionFacade.listaDesechoEspecialRecoleccionPorProyecto(aprobacionRequisitosTecnicosBean
					.getAprobacionRequisitosTecnicos().getId());
			List<DesechoEspecialRecoleccion> desechosAux = new ArrayList<DesechoEspecialRecoleccion>();
			if (desechosRecoleccion != null || !desechosRecoleccion.isEmpty()) {
				for (DesechoEspecialRecoleccion desechosEspecialRecoleccion : desechosRecoleccion) {

					DesechoPeligroso desechoPeligroso = desechoPeligrosoTransportacionFacade.buscarDesechoPeligrososPorId(desechosEspecialRecoleccion.getIdDesecho());
					desechosEspecialRecoleccion.setDesechoPeligroso(desechoPeligroso);

				}
				desechosAux=desechosRecoleccion;

					//desechosAux.add(desechosRecoleccion.get(desechosRecoleccion.size()-1));

				lavadoContenedoresYTratamientoEfluentesBean.setDesechosEspeciales(desechosAux);

			}

		} catch (Exception e) {
			LOGGER.info(e, e);
			JsfUtil.addMessageError("No se obtiene los desechos");
		}

	}

	/**
	 * 
	 * <b> Metodo que agregar el lavado a la lista. </b>
	 * <p>
	 * [Author: Vero, Date: 09/07/2015]
	 * </p>
	 * 
	 */
	public void agregarLavadoContenedor() {

		if (!lavadoContenedoresYTratamientoEfluentesBean.isEditar()) {
			lavadoContenedoresYTratamientoEfluentesBean.getListaLavadosContenedores().add(
					lavadoContenedoresYTratamientoEfluentesBean.getLavadoContenedoresYTratamientoEfluentes());
			lavadoContenedoresYTratamientoEfluentesBean.getListaLavadosContenedoresModificados().add(
					lavadoContenedoresYTratamientoEfluentesBean.getLavadoContenedoresYTratamientoEfluentes());
		}
		lavadoContenedoresYTratamientoEfluentesBean
				.setLavadoContenedoresYTratamientoEfluentes(new LavadoContenedoresYTratamientoEfluentes());
		lavadoContenedoresYTratamientoEfluentesBean.setEditar(false);
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

	}

	/**
	 * 
	 * <b> Metodo que elimina un lavado de la lista. </b>
	 * <p>
	 * [Author: Vero, Date: 09/07/2015]
	 * </p>
	 * 
	 */
	public void remover() {
		lavadoContenedoresYTratamientoEfluentesBean.getListaLavadosContenedores().remove(
				lavadoContenedoresYTratamientoEfluentesBean.getLavadoContenedoresYTratamientoEfluentes());
		lavadoContenedoresYTratamientoEfluentesBean.getListaLavadosContenedoresEliminados().add(
				lavadoContenedoresYTratamientoEfluentesBean.getLavadoContenedoresYTratamientoEfluentes());
	}

	/**
	 * 
	 * <b> Metodo que selecciona un lavado de la lista. </b>
	 * <p>
	 * [Author: Vero, Date: 09/07/2015]
	 * </p>
	 * 
	 * @param lavadoContenedoresYTratamientoEfluentes
	 */
	public void seleccionarLavadoContenedor(
			LavadoContenedoresYTratamientoEfluentes lavadoContenedoresYTratamientoEfluentes) {
		lavadoContenedoresYTratamientoEfluentesBean
				.setLavadoContenedoresYTratamientoEfluentes(lavadoContenedoresYTratamientoEfluentes);
		lavadoContenedoresYTratamientoEfluentesBean.setEditar(true);

	}

	/**
	 * 
	 * <b> Metodo que selecciona un desecho de la lista. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/07/2015]
	 * </p>
	 * 
	 * @param especialRecoleccion
	 */
	public void seleccionarDesechoEspecial(DesechoEspecialRecoleccion especialRecoleccion) {
		lavadoContenedoresYTratamientoEfluentesBean.setDesechoEspecial(especialRecoleccion);
	}

	/**
	 * 
	 * <b> Metodo que instancia un nuevo lavado. </b>
	 * <p>
	 * [Author: Vero, Date: 09/07/2015]
	 * </p>
	 * 
	 */
	public void prepararNuevo() {
		lavadoContenedoresYTratamientoEfluentesBean.setEditar(false);
		lavadoContenedoresYTratamientoEfluentesBean
				.setLavadoContenedoresYTratamientoEfluentes(new LavadoContenedoresYTratamientoEfluentes());
		lavadoContenedoresYTratamientoEfluentesBean.getLavadoContenedoresYTratamientoEfluentes()
				.setAprobacionRequisitosTecnicos(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());

	}

	/**
	 * 
	 * <b> Metodo que guarda la lista de los lavados con la de los desechos
	 * especiales. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/07/2015]
	 * </p>
	 * 
	 */
	public void guardarPagina() {
		try {

			desechoPeligrosoTransportacionFacade.guardarListaRecoleccionDesechoEspecial(
					lavadoContenedoresYTratamientoEfluentesBean.getDesechosEspeciales(),
					bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isProyectoExPost());
			recoleccionTransporteDesechosFacade
					.guardarListaLavadoContenedor(lavadoContenedoresYTratamientoEfluentesBean
							.getListaLavadosContenedoresModificados());
			if (!lavadoContenedoresYTratamientoEfluentesBean.getListaLavadosContenedoresEliminados().isEmpty()) {
				recoleccionTransporteDesechosFacade
						.eliminarListaLavadoContenedor(lavadoContenedoresYTratamientoEfluentesBean
								.getListaLavadosContenedoresEliminados());
			}
			validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos",
					"recoleccionYTransporteDesechos", aprobacionRequisitosTecnicosBean
							.getAprobacionRequisitosTecnicos().getId().toString());
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

		} catch (Exception e) {
			LOGGER.error("error al guardar el desecho especial ", e);
			JsfUtil.addMessageError("Ocurrió un error al guardar las caracteristicas del desecho especial. Por favor comunicarse con mesa de ayuda.");
		}

	}

	/**
	 * 
	 * <b> Metodo que sube un archivo. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/07/2015]
	 * </p>
	 * 
	 * @param event
	 *            : evento
	 */
	public void handleFileUpload(FileUploadEvent event) {
		Map<String, Object> att = event.getComponent().getAttributes();
		int indice = new Integer(att.get("indice").toString());
		lavadoContenedoresYTratamientoEfluentesBean.setFile(event.getFile());
		switch (indice) {
		case 0:
			lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial().setDocumentoMecanica(
					UtilDocumento.generateDocumentZipFromUpload(lavadoContenedoresYTratamientoEfluentesBean.getFile()
							.getContents(), lavadoContenedoresYTratamientoEfluentesBean.getFile().getFileName()));
			break;
		case 1:
			lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial().setDocumentoEnfriamiento(
					UtilDocumento.generateDocumentZipFromUpload(lavadoContenedoresYTratamientoEfluentesBean.getFile()
							.getContents(), lavadoContenedoresYTratamientoEfluentesBean.getFile().getFileName()));
			break;
		case 2:
			lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial().setDocumentoCaptacion(
					UtilDocumento.generateDocumentZipFromUpload(lavadoContenedoresYTratamientoEfluentesBean.getFile()
							.getContents(), lavadoContenedoresYTratamientoEfluentesBean.getFile().getFileName()));
			break;
		default:
			LOGGER.error("Indice de archivo adjunto no esperado");
			JsfUtil.addMessageError("Ocurrió un error al tratar de subir el archivo, por favor comunicarse con mesa de ayuda.");
			break;
		}

	}

	/**
	 *
	 * <b> Metodo que agregar las caracteristicas al desecho especial. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/07/2015]
	 * </p>
	 *
	 */
	public void agregarCaracteristica() {

		if (validarCaracteristicas()) {
			lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial().setAprobacionRequisitosTecnicos(
					aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			lavadoContenedoresYTratamientoEfluentesBean.setDesechoEspecial(null);
			RequestContext.getCurrentInstance().execute("PF('agregarCaracteristicasImagenes').hide();");
		}
	}



	/**
	 *
	 * <b> Metodo que valida las caracteristicas del desecho especial. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/07/2015]
	 * </p>
	 *
	 * @return boolean: true si no hay errores.
	 */
	public boolean validarCaracteristicas() {

		List<String> listaMensajes = new ArrayList<String>();


			if (lavadoContenedoresYTratamientoEfluentesBean.isMostrarFotografia()
					&& lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial().getDocumentoMecanica() == null) {
				listaMensajes.add("No se añade la imagen de mecánica de carga y descarga.");
			}

			if (lavadoContenedoresYTratamientoEfluentesBean.isMostrarFotografia()
					&& lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial().getDocumentoEnfriamiento() == null) {
				listaMensajes.add("No se añade la imagen de sistema de enfriamiento.");
			}

			if (lavadoContenedoresYTratamientoEfluentesBean.isMostrarFotografia()
					&& lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial().getDocumentoCaptacion() == null) {
				listaMensajes.add("No se añade la imagen de sistema de captación de lixiviados y de caja hermética.");
			}
		//}


		if (lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial().getDescripcionMecanica() == null
				|| lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial().getDescripcionMecanica().isEmpty()) {
			listaMensajes.add("No se han definido la descripción de mecánica de carga y descarga.");
		}

		if (lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial().getDescripcionEnfriamiento() == null
				|| lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial().getDescripcionEnfriamiento()
						.isEmpty()) {
			listaMensajes.add("No se han definido la descripción de sistema de enfriamiento.");
		}

		if (lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial().getDescripcionCaptacion() == null
				|| lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial().getDescripcionCaptacion().isEmpty()) {
			listaMensajes.add("No se han definido la descripción de captación de lixiviados y de caja hermética.");
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
	 * <b> Metodo para descargar los anexos. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/07/2015]
	 * </p>
	 * 
	 * @param indice
	 *            : indice del documento
	 * @throws IOException
	 *             : Excepcion
	 */
	public void descargar(int indice) throws IOException {
		try {
			switch (indice) {
			case 0:
				UtilDocumento.descargarZipRar(recoleccionTransporteDesechosFacade
						.descargarFile(lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial()
								.getDocumentoMecanica()), lavadoContenedoresYTratamientoEfluentesBean
						.getDesechoEspecial().getDocumentoMecanica().getNombre());
				break;
			case 1:
				UtilDocumento.descargarZipRar(recoleccionTransporteDesechosFacade
						.descargarFile(lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial()
								.getDocumentoEnfriamiento()), lavadoContenedoresYTratamientoEfluentesBean
						.getDesechoEspecial().getDocumentoEnfriamiento().getNombre());
				break;
			case 2:
				UtilDocumento.descargarZipRar(recoleccionTransporteDesechosFacade
						.descargarFile(lavadoContenedoresYTratamientoEfluentesBean.getDesechoEspecial()
								.getDocumentoCaptacion()), lavadoContenedoresYTratamientoEfluentesBean
						.getDesechoEspecial().getDocumentoCaptacion().getNombre());
				break;
			default:
				LOGGER.error("Indice de archivo adjunto no esperado para descargar el adjunto");
				JsfUtil.addMessageError("Ocurrió un error al tratar de descargar el archivo, por favor comunicarse con mesa de ayuda.");
				break;
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}

	}

	public String recuparPaginaSiguienteModoVer() {

		if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestion()) {
			if (!aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestionSoloConTransporte()) {
				return "/control/aprobacionRequisitosTecnicos/recepcionDesechosPeligrososVer.jsf?faces-redirect=true";
			} else {
				return "/control/aprobacionRequisitosTecnicos/envioAprobacionRequisitosVer.jsf?faces-redirect=true";
			}
		} else {
			return "/control/aprobacionRequisitosTecnicos/envioAprobacionRequisitosVer.jsf?faces-redirect=true";
		}
	}
	
	public boolean isVisibleOpcionNavegarSiguienteEnMenuModoVer() {
		String page = recuparPaginaSiguienteModoVer();
		return !(page.contains("envioAprobacionRequisitosVer"));
	}

	public void validateAnexosDesechos(FacesContext context, UIComponent validate, Object value) {
		StringBuilder functionJs = new StringBuilder();
		List<FacesMessage> mensajes = new ArrayList<>();

		boolean isCompleto = true;

		for (DesechoEspecialRecoleccion desechoEspecial : lavadoContenedoresYTratamientoEfluentesBean
				.getDesechosEspeciales()) {
			if (desechoEspecial.getDescripcionMecanica() == null || desechoEspecial.getDescripcionMecanica().isEmpty()) {
				isCompleto = false;
				break;
			}
		}
		if (isCompleto) {
			functionJs.append("removeHighLightComponent('form:anexosDesechos');");

		} else {
			functionJs.append("highlightComponent('form:anexosDesechos');");
			FacesMessage mensajeValidacionDocumento = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe completar la sección Anexos desechos'", null);
			mensajes.add(mensajeValidacionDocumento);
		}
		RequestContext.getCurrentInstance().execute(functionJs.toString());
		if (!mensajes.isEmpty())
			throw new ValidatorException(mensajes);

	}

	public void validarLavadoContenedores(FacesContext context, UIComponent validate, Object value) {
		StringBuilder functionJs = new StringBuilder();
		List<FacesMessage> mensajes = new ArrayList<>();
		boolean isCompleto = true;

		if (lavadoContenedoresYTratamientoEfluentesBean.getListaLavadosContenedores().isEmpty()) {
			isCompleto = false;
		}
		if (isCompleto) {
			functionJs.append("removeHighLightComponent('form:lavadoContenedores');");

		} else {
			functionJs.append("highlightComponent('form:lavadoContenedores');");
			FacesMessage mensajeValidacionDocumento = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe ingresar al menos un registro en la sección Lavado de contenedores y tratamiento de efluentes'", null);
			mensajes.add(mensajeValidacionDocumento);
		}
		RequestContext.getCurrentInstance().execute(functionJs.toString());
		if (!mensajes.isEmpty())
			throw new ValidatorException(mensajes);

	}

	public void cargarRecoleccionTransporteDesechos() {


	}
}
