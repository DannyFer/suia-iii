package ec.gob.ambiente.prevencion.registro.proyecto.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.categoria1.bean.RecibirCertificadoRegistroAmbientalBean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.ProcesoAdministrativoBean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.VerProyectoBean;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.base.RegistroProyectoController;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.MineroArtesanal;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.notificaciones.facade.NotificacionesFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.v2.CategoriaIIFacadeV2;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
public class VerProyectoController_antes_produccion implements Serializable {

	private static final long serialVersionUID = -1121710921505280710L;
	private static final Logger LOGGER = Logger.getLogger(VerProyectoController_antes_produccion.class);
	private static final String MENSAJE_REQUISITOS_PREVIOS_DESECHOS = "mensaje.requisitosprevios.genera.desechos";
	private static final String MENSAJE_REQUISITOS_PREVIOS_INTERSECA = "mensaje.requisitosprevios.interseca";
	private static final String MENSAJE_REQUISITOS_NO_FACTIBLE = "mensaje.requisitosprevios.no.factible";
	private static final String IMAGEN_REQUIERE_PREGUNTAS = "/resources/images/mensajes/ayuda_certificado_viabilidad_suia_iii.png";
	private static final String IMAGEN_GESTIONA_DESECHOS = "/resources/images/mensajes/ayuda_gestion_desechos_suia_iii.png";

	private static final String MENSAJE_CATEGORIAI = "mensaje.categoriaI";
	private static final String MENSAJE_CATEGORIAII = "mensaje.categoriaII";
	private static final String MENSAJE_CATEGORIAIII = "mensaje.categoriaIII";
	private static final String MENSAJE_CATEGORIAIV = "mensaje.categoriaIV";
	private static final String MENSAJE_ENTE = "mensaje.ente";
	private static final String MENSAJE_CATEGORIAII_EXENTO_PAGO = "mensaje.categoriaII.exento.pago";
	private static final String IMAGEN_LISTA_PROYECTOS = "/resources/images/mensajes/lista_proyecto.png";
	private static final String IMAGEN_BANDEJA_PROYECTOS = "/resources/images/mensajes/bandeja_tarea.png";
	private static final String IMAGEN_VALIDAR_PAGO = "/resources/images/mensajes/validar_Pago_tasas.png";
	private static final String IMAGEN_CATEGORIAIIIIV = "/resources/images/mensajes/CategoriaIIIIV.png";
	private static final String IMAGEN_LICENCIA_AMBIENTAL_NO_HIDROCARBUROS = "/resources/images/mensajes/ayuda_LicenciaAmbiental_suia_iii.png";

	@EJB
	private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;

	@EJB
	private CategoriaIIFacadeV2 categoriaIIFacade;

	@EJB
	private LicenciaAmbientalFacade licenciaAmbientalFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{verProyectoBean}")
	private VerProyectoBean verProyectoBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{generarCertificadoInterseccionController}")
	private GenerarCertificadoInterseccionController generarCertificadoInterseccionController;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private NotificacionesFacade notificacionesFacade;

	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionService;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private AreaFacade areaFacade;

	private boolean saltarMigracion;

	public void notificarProcesoAdministrativo() {
		try {
			int cantidadProcesosAdministrativos = JsfUtil.getBean(ProcesoAdministrativoBean.class)
					.getCantidadProcesosAdmUsuarioAutenticado();
			if (cantidadProcesosAdministrativos > 0 && getUsuariosNotificarProcesoAdministrativo() != null) {

				String[] parametrosAsunto = { String.valueOf(cantidadProcesosAdministrativos) };
				String asunto = DocumentoPDFPlantillaHtml.getPlantillaConParametros(
						"notificacion_proceso_administrativo_asunto", parametrosAsunto);

				String nombreProponente = JsfUtil.getBean(LoginBean.class).getUsuario().getPersona().getNombre();
				String cedulaORuc = JsfUtil.getBean(LoginBean.class).getUsuario().getPersona().getPin();
				String nombreProyecto = verProyectoBean.getProyecto().getNombre();
				String codigoProyecto = verProyectoBean.getProyecto().getCodigo();
				String actividadEconomica = verProyectoBean.getProyecto().getCatalogoCategoria().getDescripcion();

				String[] parametrosDescripcion = { nombreProponente, cedulaORuc, nombreProyecto, codigoProyecto,
						actividadEconomica };
				String descripcion = DocumentoPDFPlantillaHtml.getPlantillaConParametros(
						"notificacion_proceso_administrativo_descripcion", parametrosDescripcion);

				notificacionesFacade.adicionarNotificacion(codigoProyecto, asunto, descripcion,
						getUsuariosNotificarProcesoAdministrativo(), " ", 0L, null);
			}

		} catch (Exception exception) {
			LOGGER.error("Ocurrio un error en el proceso de notificacion", exception);
		}
	}

	public String[] getUsuariosNotificarProcesoAdministrativo() {
		if (Constantes.getUsuariosNotificarProcesoAdministrativo() != null) {
			return Constantes.getUsuariosNotificarProcesoAdministrativo().split(",");
		}
		return null;
	}

	private boolean isProyectoListoParaFinalizar() {
		return generarCertificadoInterseccionController
				.validarDocumentosCertificadosInterseccionProyectoParaFinalizar()
				&& verProyectoBean.getProyecto().getProyectoInterseccionExitosa();
	}

	private void iniciarProcesoRespectivos() throws ServiceException {
		// Para el sector de Hidrocarburos no inicia proceso de licenciamiento
		// en suiia azul
		if (TipoSector.TIPO_SECTOR_HIDROCARBUROS == verProyectoBean.getProyecto().getTipoSector().getId()
				&& (verProyectoBean.isCategoriaIII() || verProyectoBean.isCategoriaIV())) {
			verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_CATEGORIAIV));
			verProyectoBean.setPathImagen(IMAGEN_CATEGORIAIIIIV);
			return;
		}
		int ID_TIPO_AREA_ENTE_ACREDITADO = 3;
		boolean isAcreditado = verProyectoBean.getProyecto().getAreaResponsable().getTipoArea().getId()
				.equals(ID_TIPO_AREA_ENTE_ACREDITADO);
		Area enteAcreditado = areaFacade.getAreaEnteAcreditado(ID_TIPO_AREA_ENTE_ACREDITADO,
				loginBean.getNombreUsuario());

		if (enteAcreditado != null) {
			isAcreditado = true;
		}

		final Boolean isEstrategico = verProyectoBean.getProyecto().getCatalogoCategoria().getEstrategico();
		final Boolean interseca = certificadoInterseccionService.isProyectoIntersecaCapas(verProyectoBean.getProyecto()
				.getCodigo());
		if (!interseca && isAcreditado && !isEstrategico
				&& verProyectoBean.getProyecto().getAreaResponsable().getArea() == null && enteAcreditado != null) {
			verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_ENTE) + " "
					+ verProyectoBean.getProyecto().getAreaResponsable().getAreaName());
			verProyectoBean.setPathImagen(null);
			LOGGER.info("No inicia proceso porque es ente acreditado, solamente se creó el proyecto");
			return;
		}

		// Se verifica si el proyecto requiere de requisitos previos a Permiso
		// Ambiental
		if (requisitosPreviosFacade.requiereRequisitosPrevios(verProyectoBean.getProyecto())) {
			// Se verifica si el proyecto es minero e interseca con Áreas
			// Protegidas
			if (requisitosPreviosFacade.proyectoNoFactible(verProyectoBean.getProyecto())) {
				verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_REQUISITOS_NO_FACTIBLE));
				verProyectoBean.setPathImagen(null);
			}
			// Si interseca, se muestra el mensaje de interseccion
			else if (requisitosPreviosFacade.intersecaAreasProtegidas(verProyectoBean.getProyecto())) {
				verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_REQUISITOS_PREVIOS_INTERSECA));
				verProyectoBean.setPathImagen(IMAGEN_REQUIERE_PREGUNTAS);
			}
			// Si no interseca y requiere requisitos técnicos, se muestra el
			// mensaje de gestion de desechos
			else if (requisitosPreviosFacade.requiereRequisitosTecnicos(verProyectoBean.getProyecto())) {
				verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_REQUISITOS_PREVIOS_DESECHOS));
				verProyectoBean.setPathImagen(IMAGEN_GESTIONA_DESECHOS);
			}
			// Inicio de requisitos previos
			requisitosPreviosFacade.iniciarProcesoRequisitosPrevios(loginBean.getUsuario(),
					verProyectoBean.getProyecto(), verProyectoBean.isCategoriaIII() || verProyectoBean.isCategoriaIV());
		}
		// Si no se requieren requisitos previos, se inician los procesos de
		// registro o licencia
		else {
			if (verProyectoBean.isCategoriaIII() || verProyectoBean.isCategoriaIV()) {
				// Para el sector de Hidrocarburos no inicia proceso de
				// licenciamiento
				if (TipoSector.TIPO_SECTOR_HIDROCARBUROS == verProyectoBean.getProyecto().getTipoSector().getId()) {
					verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_CATEGORIAIV));
					verProyectoBean.setPathImagen(IMAGEN_CATEGORIAIIIIV);
					return;
				}
				// Inicio de Licenciamiento Ambiental
				verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_CATEGORIAIII));
				verProyectoBean.setPathImagen(IMAGEN_LICENCIA_AMBIENTAL_NO_HIDROCARBUROS);
				iniciarProcesoLicenciaAmbiental();
			} else if (verProyectoBean.isCategoriaII()) {
				verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_CATEGORIAII_EXENTO_PAGO));
				verProyectoBean.setPathImagen(IMAGEN_BANDEJA_PROYECTOS);
				iniciarProcesoCategoriaII();
			}
		}
	}

	private boolean finalizarRegistroProyectoSuperioACategoriaI() {
		try {
			if (isProyectoListoParaFinalizar() && !verProyectoBean.getProyecto().isFinalizado()) {
				iniciarProcesoRespectivos();
			}
			// if (true) {
			// iniciarProcesoRespectivos();
			// }
			else {
				if (verProyectoBean.getProyecto().getProyectoInterseccionExitosa() != null
						&& !verProyectoBean.getProyecto().getProyectoInterseccionExitosa()) {
					JsfUtil.addMessageError("La ejecución del proceso de intersección no ha sido ejecutada exitosamente, por lo tanto no puede finalizar el registro del proyecto.");
				} else {
					JsfUtil.addMessageError("El mapa u oficio del certificado de intersección aún no han sido generado, por lo tanto no puede finalizar el registro del proyecto.");
				}
				return false;
			}

			if (!verProyectoBean.isCategoriaII() && !saltarMigracion
					&& TipoSector.TIPO_SECTOR_HIDROCARBUROS == verProyectoBean.getProyecto().getTipoSector().getId()) {
				migrarProyecto();
			}
			return true;

		} catch (Exception e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error con el registro del proyecto, por favor contacte a Mesa de Ayuda.");
			return false;
		}

	}

	public String getUrlContinuar() {
		return JsfUtil.getStartPage();
	}

	public void finalizarRegistroProyecto() {
		try {
			boolean finalizar = false;
			if (verProyectoBean.isCategoriaI()) {
				verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_CATEGORIAI));
				verProyectoBean.setPathImagen(IMAGEN_LISTA_PROYECTOS);
				finalizar = true;
			} else {
				finalizar = finalizarRegistroProyectoSuperioACategoriaI();
			}
			if (finalizar) {
				finalizarProyecto();
				notificarProcesoAdministrativo();
				RequestContext.getCurrentInstance().execute("PF('continuarDialog').show();");
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}

	}

	public String eliminar() {
		verProyectoBean.getProyecto().setEstado(false);
		proyectoLicenciamientoAmbientalFacade.actualizarSimpleProyecto(verProyectoBean.getProyecto());
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		return JsfUtil.actionNavigateTo("/proyectos/listadoProyectos.jsf");
	}

	private void finalizarProyecto() {
		verProyectoBean.getProyecto().setFinalizado(true);
		proyectoLicenciamientoAmbientalFacade.actualizarSimpleProyecto(verProyectoBean.getProyecto());
	}

	public void migrarProyecto() throws Exception {
		proyectoLicenciamientoAmbientalFacade.ejecutarMigracionProyecto(verProyectoBean.getProyecto(),
				JsfUtil.getLoggedUser());

	}

	private void iniciarProcesoCategoriaII() throws ServiceException {
		categoriaIIFacade.inicarProcesoCategoriaII(loginBean.getUsuario(), verProyectoBean.getProyecto());
	}

	private void iniciarProcesoLicenciaAmbiental() throws ServiceException {
		licenciaAmbientalFacade.iniciarProcesoLicenciaAmbiental(loginBean.getUsuario(), verProyectoBean.getProyecto());
	}

	public void verProyecto(Integer id) throws CmisAlfrescoException {
		verProyecto(id, false);
	}

	public void verProyecto(Integer id, boolean mostrarAcciones) throws CmisAlfrescoException {
		try {
			verProyectoBean.setProyecto(proyectoLicenciamientoAmbientalFacade.cargarProyectoFullPorId(id));
		} catch (Exception e) {
			LOGGER.error("Error al cargar el proyecto.", e);
			JsfUtil.addMessageError("Ha ocurrido un error recuperando los datos del proyecto.");
			return;
		}
		verProyectoBean.setMostrarAcciones(mostrarAcciones);
		generarCertificadoInterseccionController.validarProcesoGeneracionCertificadoInterseccion(mostrarAcciones);
		if (verProyectoBean.isCategoriaI()) {
			verProyectoBean.setShowModalCertificadoIntercepcion(false);
			verProyectoBean.setShowModalAceptarResponsabilidad(true);
			JsfUtil.getBean(RecibirCertificadoRegistroAmbientalBean.class).iniciar(id);
		} else {
			generarCertificadoInterseccionController.iniciarVariablesGeneracionCI();
		}
		if (verProyectoBean.getProyecto().isMinerosArtesanales()) {
			for (int i = 0; i < verProyectoBean.getProyecto().getProyectoMinerosArtesanales().size(); i++) {
				descargarAlfresco(verProyectoBean.getProyecto().getProyectoMinerosArtesanales().get(i)
						.getMineroArtesanal().getContratoOperacion());
			}
		}
		if (verProyectoBean.isMineriaArtesanalOLibreAprovechamiento()
				&& verProyectoBean.getProyecto().isMineriaAreasConcesionadas()) {
			MineroArtesanal mineroArtesanal = verProyectoBean.getProyecto().getMineroArtesanal();
			if (mineroArtesanal != null) {
				descargarAlfresco(mineroArtesanal.getContratoOperacion());
				descargarAlfresco(mineroArtesanal.getRegistroMineroArtesanal());
			}
		}
		if (verProyectoBean.isMineriaArtesanalOLibreAprovechamiento()
				&& !verProyectoBean.getProyecto().isMineriaAreasConcesionadas()) {
			MineroArtesanal mineroArtesanal = verProyectoBean.getProyecto().getMineroArtesanal();
			if (mineroArtesanal != null)
				descargarAlfresco(mineroArtesanal.getRegistroMineroArtesanal());
		}
		try {
			verProyectoBean.setNotaResponsabilidadRegistro(getNotaResponsabilidadInformacionRegistroProyecto(loginBean
					.getUsuario().getPersona(), loginBean.getUsuario().getId()));
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al recuperar los datos de proponente.");
			LOGGER.error("Error al recuperar los datos de proponente.", e);
		}
	}

	public String modificar() {
		RegistroProyectoController.setProyectoModificar();
		String type = "Hidrocarburos";
		generarCertificadoInterseccionController.iniciarVariablesGeneracionCI();
		if (verProyectoBean.getProyecto().getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_MINERIA)
			type = "Mineria";
		else if (verProyectoBean.getProyecto().getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_OTROS)
			type = "OtrosSectores";
		else if (verProyectoBean.getProyecto().getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_ELECTRICO)
			type = "Electrico";
		else if (verProyectoBean.getProyecto().getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_SANEAMIENTO)
			type = "Saneamiento";
		else if (verProyectoBean.getProyecto().getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_TELECOMUNICACIONES)
			type = "Telecomunicaciones";
		try {
			generarCertificadoInterseccionController.eliminarPDFMapaCertificadoInterseccion();
		} catch (Exception e) {
			LOGGER.error("Error al eliminar el PDF del certificado de interseccion", e);
		}
		return JsfUtil.actionNavigateTo("/prevencion/registroProyecto/registro" + type);
	}

	private String getNotaResponsabilidadInformacionRegistroProyecto(Persona persona, Integer idUsuario)
			throws ServiceException {
		String[] parametros = { persona.getNombre(), proyectoLicenciamientoAmbientalFacade.getIdentificacion(idUsuario) };
		return DocumentoPDFPlantillaHtml.getPlantillaConParametros("nota_responsabilidad_certificado_interseccion",
				parametros);
	}

	public Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}

}
