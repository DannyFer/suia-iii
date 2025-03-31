 package ec.gob.ambiente.prevencion.categoria1.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfWriter;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.categoria2.bean.FichaAmbientalPmaBean;
import ec.gob.ambiente.prevencion.categoria2.bean.ImpresionFichaGeneralBean;
import ec.gob.ambiente.prevencion.categoria2.v2.controller.FichaAmbientalGeneralFinalizarControllerV2;
import ec.gob.ambiente.prevencion.certificado.ambiental.controllers.GenerarQRCertificadoAmbiental;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.GenerarNotificacionesAplicativoController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.dto.DocumentoTareaDTO;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.TareaFirmaMasivaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.preliminar.contoller.GeneracionDocumentosController;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.FlujosCategoriaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.autoridadambiental.service.AutoridadAmbientalFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.builders.TaskSummaryCustomBuilder;
import ec.gob.ambiente.suia.comun.classes.Selectable;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.control.documentos.service.DocumentosService;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.CertificadoRegistroAmbiental;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentoProyecto;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityCertificadoAmbientalAmb;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.Categoria1Facade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.PlantillaHeaderFooter;
import ec.gob.ambiente.suia.reportes.PlantillaHeaderFooterOficio;
import ec.gob.ambiente.suia.reportes.ReporteLicenciaAmbientalCategoriaII;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.reportes.facade.ReportesFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class FirmarResolucionAmbientalController {
	private static final long serialVersionUID = -509930215520733156L;
	private static final String[] LIBRE_APROVECHAMIENTO = new String[] { "21.02.06.02" };
	private static final String[] MINERIA_ARTESANAL = new String[] { "21.02.01.01" };
	private static final String[] MINERIA_EXPLORACION_INICIAL = new String[] { "21.02.02.01", "21.02.03.06" };
	private static final String[] MINERIA_PERFORACION_EXPLORATIVA = new String[] { "21.02.03.05", "21.02.04.03",
			"21.02.05.03", "21.02.02.03" };
	public static final String CategoriaIILicencia = "CategoriaIILicencia";

	private final Logger LOG = Logger.getLogger(FirmarResolucionAmbientalController.class);

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private Categoria1Facade categoria1Facade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private ReportesFacade reportesFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private AutoridadAmbientalFacade autoridadAmbientalFacade;

	@EJB
	private TareaFirmaMasivaFacade tareaFirmaMasivaFacade;

	@EJB
	private InformeProvincialGADFacade informeProvincialGADFacade;

	@EJB
	private DocumentosService documentosService;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Setter
	@ManagedProperty(value = "#{fichaAmbientalPmaBean}")
	private FichaAmbientalPmaBean fichaAmbientalPmaBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private List<Selectable<ProyectoLicenciamientoAmbiental>> listaProyectos, listaSelectProyectos;

	@Getter
	@Setter
	private boolean ambienteProduccion, mostrarFirma;

	@Getter
	@Setter
	public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");

	@Getter
	@Setter
	private String pathTotal;

	@Getter
	@Setter
	private Documento documentoCertificado, documentoManual, documento, documentoCargado, documentoInformacionManual;

	@Getter
	@Setter
	private boolean token, mostrarBotonFirma;

	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;

	@Getter
	@Setter
	private String tramite;
	
	@Getter
	@Setter
	private String nombreFichero, nombreReporte;

	@Getter
	@Setter
	private byte[] archivoInforme;

	@Getter
	@Setter
	private String idProceso, idTarea;

	@Getter
	@Setter
	private boolean documentoDescargado = false, documentoSubido = false, ingresoFirma = false;

	@Getter
	@Setter
	private List<String> notificaciones;
	@Getter
	@Setter
	private byte[] archivo;

	@Getter
	@Setter
	private File archivoFinal;

	@Getter
	@Setter
	private String pdf;

	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbiental;

	@Getter
	@Setter
	private Path path;

	@Getter
	@Setter
	private Long processId;

	@Getter
	@Setter
	private Integer docId;

	@Getter
	@Setter
	private boolean firmaSoloToken, informacionSubida;

	@Getter
	@Setter
	private Boolean mostrarFirmaMasiva;

	@Getter
	@Setter
	private String nombreDocumentoFirmado, urlAlfresco;

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;

	@EJB
	private CategoriaIIFacade categoriaIIFacade;

	@Getter
	@Setter
	List<TaskSummaryCustom> listaSelectTareas;

	@Getter
	@Setter
	private Boolean habilitarForm;

	@EJB
	private FlujosCategoriaFacade flujosCategoriaFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private DocumentosCoaFacade documentosCoaFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentosRegistroFacade;

	@Getter
	@Setter
	private List<Documento> listaDocumentos;

	@Getter
	@Setter
	private List<DocumentoTareaDTO> listaDocumentoTarea;

	@Getter
	@Setter
	private String idsDocumentosAlfresco;

	@Getter
	@Setter
	private ImpresionFichaGeneralBean impresionFichaGeneralBean;

	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	AlfrescoServiceBean alfrescoServiceBean;

	@PostConstruct
	public void init() {
		try {

			listaProyectos = new ArrayList<Selectable<ProyectoLicenciamientoAmbiental>>();
			listaSelectProyectos = new ArrayList<Selectable<ProyectoLicenciamientoAmbiental>>();
			notificaciones = new ArrayList<String>();
			listaDocumentos = new ArrayList<>();
			idsDocumentosAlfresco = "";
			habilitarForm = true;
			mostrarBotonFirma = true;

			listaDocumentoTarea = new ArrayList<>();

			if (JsfUtil.devolverObjetoSession("codigoProyecto") == null
					|| JsfUtil.devolverObjetoSession("codigoProyecto").equals("")) {

				if (loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getTipoArea().getSiglas().equals("PC")
						|| loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getTipoArea().getSiglas()
								.toUpperCase().equals("ZONALES")
						|| loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getTipoArea().getSiglas()
								.equals("OT")) {

					List<ProyectoLicenciamientoAmbiental> proyectos = proyectoFacade
							.obtenerRegistroAmbientales(loginBean.getUsuario().getListaAreaUsuario().get(0).getArea());
					listaProyectos = new ArrayList<>();
					for (ProyectoLicenciamientoAmbiental proyecto : proyectos) {
						listaProyectos.add(new Selectable<>(proyecto));
					}
				} else {
					List<ProyectoLicenciamientoAmbiental> proyectos = proyectoFacade
							.obtenerRegistroAmbientales(loginBean.getUsuario().getListaAreaUsuario().get(0).getArea());
					listaProyectos = new ArrayList<>();
					for (ProyectoLicenciamientoAmbiental proyecto : proyectos) {
						listaProyectos.add(new Selectable<>(proyecto));
					}
				}

				ingresoFirma = false;
			}

			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);

			token = true;

			if (!ambienteProduccion) {
				verificaToken();
				documentoDescargado = true;
			}
			urlAlfresco = "";

			if (ingresoFirma) {
				JsfUtil.cargarObjetoSession("codigoProyecto", "");

				proyecto = proyectoFacade.buscarProyectoPorCodigoCompleto(tramite);

				List<String[]> lista = proyectoFacade.obtenerProcesoId(proyecto.getCodigo());

				for (String[] codigoProyecto : lista) {

					idTarea = codigoProyecto[0];
					idProceso = codigoProyecto[2];
					break;
				}
			}

			if (!ingresoFirma) {

				proyecto = proyectoFacade.buscarProyectoPorCodigoCompleto(tramite);
				JsfUtil.addMessageInfo("Estos Documentos por firmar, corresponden al módulo anterior del SUIA");
			}

			mostrarFirmaMasiva = true;
			if (Usuario.isUserInRole(JsfUtil.getLoggedUser(), "sujeto de control"))
				mostrarFirmaMasiva = false;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * FIRMA ELECTRONICA
	 */
	public boolean verificaToken() {
		if (firmaSoloToken) {
			token = true;
			return token;
		}

		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void seleccionar(ProyectoLicenciamientoAmbiental proyecto) {
		try {

			JsfUtil.cargarObjetoSession("codigoProyecto", proyecto.getCodigo());

			JsfUtil.redirectTo("/prevencion/certificadoambiental/firmarCertificado.jsf");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String firmarDocumento() {
		try {
			if (!idsDocumentosAlfresco.equals("")) {
				return DigitalSign.sign(idsDocumentosAlfresco, loginBean.getUsuario().getNombre());

			}
		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}

		return "";
	}
//	public String firmarDocumento() {
//		try {
//			descargarInformacion();
//			//creaDocumento();
//
//			 
//				String documentOffice = documentosFacade.direccionDescarga(documentoCertificado);
//				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre()); 
//		 
//		} catch (Exception exception) {
//			LOG.error("Ocurrió un error durante la firma del certificado", exception);
//			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
//		}
//		return "";
//	}

	/**
	 * CREA CERTIFICADO
	 */
	private void creaDocumento() {
		try {

			if (documentoCertificado != null
					&& !documentoCertificado.getUsuarioCreacion().equals(loginBean.getUsuario().getNombre())) {
				documentoCertificado = null;
			}

			if (documentoCertificado == null || documentoCertificado.getId() == null) {

				if (proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("PC")
						|| proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("OT")
						|| proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("Zonales")
						|| proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("DP")) {

					List<Documento> documentos = new ArrayList<Documento>();// documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(),ProyectoLicenciamientoAmbiental.class.getSimpleName(),TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_MAE);
					if (documentos == null || documentos.size() == 0) {
						generarCertificado();

						if (notificaciones.isEmpty()) {
							documentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(),
									ProyectoLicenciamientoAmbiental.class.getSimpleName(),
									TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_MAE);
							documentosFacade.guardarDocumentoTareaProceso(documentos.get(0), Long.valueOf(idProceso),
									Long.valueOf(idTarea));
						} else {
							return;
						}

					}
					documentoCertificado = documentos.get(0);
				} else {
					List<Documento> documentos = null;
					if (documentos == null || documentos.size() == 0) {
						String errorCertificado = crearCertificadoRegistroAmbiental(proyecto,
								loginBean.getUsuario().getNombre(), false);
						if (errorCertificado != null) {
							JsfUtil.addMessageError(
									"Error al descargar el Certificado Ambiental. Comuníquese con mesa de ayuda");
							LOG.error(errorCertificado);
							return;
						}

						documentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(),
								ProyectoLicenciamientoAmbiental.class.getSimpleName(),
								TipoDocumentoSistema.TIPO_CERTIFICADO_CATEGORIA_UNO);
						documentosFacade.guardarDocumentoTareaProceso(documentos.get(0), Long.valueOf(idProceso),
								Long.valueOf(idTarea));

					}
					documentoCertificado = documentos.get(0);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generarCertificado() {
		try {

			String identificacionUsuario = proyecto.getUsuario().getNombre();
			String cargo = "";
			String direccion = "";
			String correo = "";
			String telefono = "";
			String celular = "";
			String nombreEmpresa = "";
			String representante = "";
//			personaJuridica = false;

			if (identificacionUsuario.length() == 10) {
				cargo = proyecto.getUsuario().getPersona().getTitulo();

				List<Contacto> contacto = contactoFacade.buscarPorPersona(proyecto.getUsuario().getPersona());

				for (Contacto con : contacto) {
					if (con.getFormasContacto().getId() == 2 && con.getEstado().equals(true)) {
						direccion = con.getValor();
					}
					if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
						correo = con.getValor();
					}
					if (con.getFormasContacto().getId() == 6 && con.getEstado().equals(true)) {
						telefono = con.getValor();
					}
					if (con.getFormasContacto().getId() == 4 && con.getEstado().equals(true)) {
						celular = con.getValor();
					}
				}

				nombreEmpresa = proyecto.getUsuario().getPersona().getNombre();
				representante = nombreEmpresa;

			} else {
				Organizacion organizacion = organizacionFacade.buscarPorRuc(identificacionUsuario);

				if (organizacion != null) {
					cargo = organizacion.getCargoRepresentante();

					List<Contacto> contacto = contactoFacade.buscarPorOrganizacion(organizacion);

					for (Contacto con : contacto) {
						if (con.getFormasContacto().getId() == 2 && con.getEstado().equals(true)) {
							direccion = con.getValor();
						}
						if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
							correo = con.getValor();
						}
						if (con.getFormasContacto().getId() == 6 && con.getEstado().equals(true)) {
							telefono = con.getValor();
						}
						if (con.getFormasContacto().getId() == 4 && con.getEstado().equals(true)) {
							celular = con.getValor();
						}
					}

					nombreEmpresa = organizacion.getNombre();
					representante = organizacion.getPersona().getNombre();
//					personaJuridica = true;
				} else {

					cargo = proyecto.getUsuario().getPersona().getTitulo();

					List<Contacto> contacto = contactoFacade.buscarPorPersona(proyecto.getUsuario().getPersona());

					for (Contacto con : contacto) {
						if (con.getFormasContacto().getId() == 2 && con.getEstado().equals(true)) {
							direccion = con.getValor();
						}
						if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
							correo = con.getValor();
						}
						if (con.getFormasContacto().getId() == 6 && con.getEstado().equals(true)) {
							telefono = con.getValor();
						}
						if (con.getFormasContacto().getId() == 4 && con.getEstado().equals(true)) {
							celular = con.getValor();
						}
					}

					nombreEmpresa = proyecto.getUsuario().getPersona().getNombre();
					representante = nombreEmpresa;
				}
			}

			EntityCertificadoAmbientalAmb entidad = new EntityCertificadoAmbientalAmb();

			entidad.setActividad(proyecto.getCatalogoCategoria().getDescripcion());
			entidad.setCedulausu(proyecto.getUsuario().getPersona().getPin());
			entidad.setCodigoproy(proyecto.getCodigo());
			entidad.setDireccion(proyecto.getDireccionProyecto());
			entidad.setDireccionpromotor(direccion);
			entidad.setEmailpromotor(correo);
			entidad.setFechaActual(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));

			String ubicacionCompleta = "";
			ubicacionCompleta = "<table>";

			ubicacionCompleta += "<tr><td>PROVINCIA</td><td>CANTÓN</td><td>PARROQUIA</td></tr>";
			for (ProyectoUbicacionGeografica ubicacionActual : proyecto.getProyectoUbicacionesGeograficas()) {

				ubicacionCompleta += "<tr><td>"
						+ ubicacionActual.getUbicacionesGeografica().getUbicacionesGeografica()
								.getUbicacionesGeografica().getNombre()
						+ "</td><td>"
						+ ubicacionActual.getUbicacionesGeografica().getUbicacionesGeografica().getNombre()
						+ "</td><td>" + ubicacionActual.getUbicacionesGeografica().getNombre() + "</td></tr>";
			}

			ubicacionCompleta += "</table>";

			entidad.setUbicacionProyecto(ubicacionCompleta);

			String rol = "AUTORIDAD AMBIENTAL";

			List<Usuario> usuarios = new ArrayList<Usuario>();
			if (proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("OT")) {
				usuarios = usuarioFacade.buscarUsuarioPorRolNombreArea(rol,
						proyecto.getAreaResponsable().getArea().getAreaName());
			} else {
				usuarios = usuarioFacade.buscarUsuarioPorRolNombreArea(rol,
						proyecto.getAreaResponsable().getAreaName());
			}

			if (usuarios != null && !usuarios.isEmpty()) {
				entidad.setNombreAutoridad(usuarios.get(0).getPersona().getNombre());
			} else {
				notificaciones.add("No existe usuario AUTORIDAD AMBIENTAL");
				return;
			}

			entidad.setNombreDireccionProvincial("DIRECCION ZONAL");

			entidad.setNombreProyecto(proyecto.getNombre());
			entidad.setNombreRepresentanteLegal(representante);
			entidad.setNumeroResolucion(
					secuenciasFacade.getSecuenciaResolucionAreaResponsableNuevoFormato(proyecto.getAreaResponsable()));
			entidad.setOperador(nombreEmpresa);
			entidad.setTelefonopromotor(telefono);

			List<String> lista = getCodigoQrUrl(proyecto.getCodigo(), GenerarQRCertificadoAmbiental.tipo_suia_iii);
			entidad.setCodigoQrFirma(lista.get(1));

			UbicacionesGeografica parroquia = proyectoFacade.getUbicacionProyectoPorIdProyecto(proyecto.getId());
			if (proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("OT")) {
				// incluir informacion de la sede de la zonal en el documento
				ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil
						.getBean(ProyectoSedeZonalUbicacionController.class);
				String sedeZonal = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarios.get(0),
						"PROYECTOSUIAIII", null, proyecto, null);
				entidad.setProvincia(sedeZonal);
			} else {
				entidad.setProvincia(parroquia.getUbicacionesGeografica().getNombre());
			}

			nombreFichero = "CertificadoAmbiental" + proyecto.getCodigo() + ".pdf";
			nombreReporte = "CertificadoAmbiental.pdf";

			PlantillaReporte plantillaReporte = plantillaReporteFacade
					.getPlantillaReporte(TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_MAE);

			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
					true, entidad);

			Path path = Paths.get(informePdf.getAbsolutePath());
			String reporteHtmlfinal = nombreReporte;
			archivoInforme = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(Files.readAllBytes(path));
			file.close();

			TipoDocumento tipoDoc = new TipoDocumento();

			tipoDoc.setId(TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_MAE.getIdTipoDocumento());

			Documento documento = new Documento();
			documento.setNombre(nombreReporte);
			documento.setExtesion(".pdf");
			documento.setTipoContenido("application/pdf");
			documento.setContenidoDocumento(archivoInforme);
			documento.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
			documento.setTipoDocumento(tipoDoc);
			documento.setIdTable(proyecto.getId());
			documento.setCodigoPublico(entidad.getNumeroResolucion());

			documento = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigo(), "Certificado Ambiental",
					Long.valueOf(idProceso), documento, TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_MAE, null);

			documento.setUsuarioCreacion(usuarios.get(0).getNombre());
			documentosFacade.actualizarDocumento(documento);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (documentoDescargado == true) {
			documentoManual = new Documento();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");
			documentoManual.setMime("application/pdf");
			documentoManual.setNombreTabla(ProyectoCertificadoAmbiental.class.getSimpleName());
			documentoManual.setIdTable(proyecto.getId());
			documentoSubido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else {
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}

	}

	public StreamedContent descargarDocumento() throws Exception {

		creaDocumento();

		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documentoCertificado != null) {
			if (documentoCertificado.getContenidoDocumento() == null) {
				documentoCertificado
						.setContenidoDocumento(documentosFacade.descargar(documentoCertificado.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(documentoCertificado.getContenidoDocumento()),
					documentoCertificado.getExtesion());
			content.setName(documentoCertificado.getNombre());
		}

		documentoDescargado = true;
		return content;
	}

	public void completarTarea() {
		try {

			if (token) {
				String idAlfrescoInforme = documentoCertificado.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				}
			} else {
				if (!documentoSubido) {
					JsfUtil.addMessageError("Debe adjuntar el certificado ambiental firmado.");
					return;
				} else {
					documentoCertificado = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigo(),
							"Certificado Ambiental", Long.valueOf(idProceso), documentoManual,
							TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_MAE, null);

				}
			}

			Map<String, Object> parametros = new HashMap<>();

			procesoFacade.modificarVariablesProceso(proyecto.getUsuario(), Long.parseLong(idProceso), parametros);

			procesoFacade.aprobarTarea(proyecto.getUsuario(), Long.parseLong(idTarea), Long.parseLong(idProceso), null);

			notificacion();

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/prevencion/certificadoambiental/listaCertificados.jsf");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void notificacion() {
		try {
			Object[] parametrosCorreoTecnicos = new Object[] { proyecto.getUsuario().getPersona().getNombre() };

			String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
					"bodyDocumentoCertificadoFirmadoParaOperador", parametrosCorreoTecnicos);

			List<Contacto> contactos = proyecto.getUsuario().getPersona().getContactos();
			String emailDestino = "";
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL) && contacto.getEstado()) {
					emailDestino = contacto.getValor();
					break;
				}
			}

			notificacion = notificacion.replace("nombre_operador", proyecto.getUsuario().getPersona().getNombre());

			NotificacionAutoridadesController email = new NotificacionAutoridadesController();
			email.sendEmailInformacionProponente(emailDestino, "", notificacion,
					"Notificación Certificado Ambiental Firmado", proyecto.getCodigo(), proyecto.getUsuario(),
					loginBean.getUsuario());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void validarTareaBpm() {

	}

	public String crearCertificadoRegistroAmbiental(ProyectoLicenciamientoAmbiental proyecto, String userName,
			boolean marcaAgua) {

		if (proyecto != null) {
			try {
				Usuario usuario = proyectoLicenciamientoAmbientalFacade.getRepresentanteProyecto(proyecto.getId());
				proyecto = proyectoLicenciamientoAmbientalFacade
						.buscarProyectosLicenciamientoAmbientalPorId(proyecto.getId());
				String direccionPlantillaJasperReport = "prevencion/recibirCertificadoRegistroAmbiental";

				List<String> imagenesReporte = new ArrayList<String>();
				List<String> subreportes = new ArrayList<String>();
				List<String> subreportesUrl = new ArrayList<String>();
				String cargo = "AUTORIDAD AMBIENTAL";
				byte[] firma_area = null;
				byte[] logo_area = null;
				byte[] pie_area = null;

				List<Usuario> usuario_area = null;
				String nombre_usuario = "";
				String cedula_usuario = "";

//				String firma_mae= "firma__"+ proyecto.getAreaResponsable().getAreaAbbreviation().replace("/", "_") + ".png";
				if (proyecto.getCatalogoCategoria().getTipoArea().getId() == 3
						&& proyecto.getAreaResponsable().getTipoArea().getId() == 3) {

					String logo = "logo__" + proyecto.getAreaResponsable().getAreaAbbreviation() + ".png";
					String pie = "pie__" + proyecto.getAreaResponsable().getAreaAbbreviation() + ".png";
					logo_area = documentosFacade.descargarDocumentoPorNombre(logo);
					pie_area = documentosFacade.descargarDocumentoPorNombre(pie);

//					firma_area = documentosFacade.descargarDocumentoPorNombre(firma_mae);
					usuario_area = usuarioFacade.buscarUsuarioPorRolNombreArea(cargo,
							proyecto.getAreaResponsable().getAreaName());
					if (usuario_area == null || usuario_area.size() == 0)
						return "Error al buscar usuario Autoridad en: " + proyecto.getAreaResponsable().getAreaName()
								+ ".";
					nombre_usuario = usuario_area.get(0).getPersona().getNombre() + "\n"
							+ proyecto.getAreaResponsable().getAreaName();
					cedula_usuario = usuario_area.get(0).getNombre();
				} else {
					imagenesReporte.add("logo_mae.png");
					imagenesReporte.add("fondo-documentos.png");

					direccionPlantillaJasperReport = "prevencion/recibirCertificadoRegistroAmbientalMae";
//					firma_area = documentosFacade.descargarDocumentoPorNombre(firma_mae);
					usuario_area = usuarioFacade.buscarUsuarioPorRolNombreArea(cargo,
							proyecto.getAreaResponsable().getAreaName());
					if (usuario_area == null || usuario_area.size() == 0)
						return "Error al buscar usuario Autoridad en: " + proyecto.getAreaResponsable().getAreaName()
								+ ".";
					nombre_usuario = usuario_area.get(0).getPersona().getNombre() + "\n" + cargo;
					cedula_usuario = usuario_area.get(0).getNombre();
				}

//				InputStream isFirmaSubsecretario = new ByteArrayInputStream(firma_area);
				InputStream islogo_area = null;
				InputStream ispie_area = null;
				if (proyecto.getCatalogoCategoria().getTipoArea().getId() == 3
						&& proyecto.getAreaResponsable().getTipoArea().getId() == 3) {
					islogo_area = new ByteArrayInputStream(logo_area);
					ispie_area = new ByteArrayInputStream(pie_area);
				}

				byte[] borrador;
				InputStream isborrador;
				borrador = documentosFacade
						.descargarDocumentoPorNombre("borrador" + (marcaAgua ? "" : "vacio") + ".png");
				isborrador = new ByteArrayInputStream(borrador);
				subreportes.add("subreporteUbicacionesGeograficas");
				subreportesUrl.add("prevencion/verRegistroProyecto_listaUbicaciones");
				if (usuario != null) {
					if (proyecto != null) {
						Map<String, Object> parametrosReporte = new ConcurrentHashMap<String, Object>();

						parametrosReporte.put("proyecto", proyecto);

						String direccionRepresentanteLegal = "";
						String telefonoRepresentanteLegal = "";
						String emailRepresentanteLegal = "";
						String cedulaRepresentanteLegal = "";
						String entidadResponsable = "";
						String localizacionEntidadResponsable = "";

						Area area = proyecto.getAreaResponsable();

						if (usuario.getPersona() != null) {
							List<Contacto> listContacto = new ArrayList<Contacto>();
							listContacto = contactoFacade.buscarUsuarioNativeQuery(userName);
							for (Contacto contacto : listContacto) {
								if (contacto.getFormasContacto().getId() == FormasContacto.DIRECCION)
									direccionRepresentanteLegal = contacto.getValor();
								if (contacto.getFormasContacto().getId() == FormasContacto.TELEFONO)
									telefonoRepresentanteLegal = contacto.getValor();
								if (contacto.getFormasContacto().getId() == FormasContacto.EMAIL)
									emailRepresentanteLegal = contacto.getValor();
							}

							String cedula = categoria1Facade.determinarCedulaRepresentanteLegal(usuario);
							if (cedula != null)
								cedulaRepresentanteLegal = cedula;
						}

						if (area != null)
							entidadResponsable = area.getAreaName();

						UbicacionesGeografica parroquia = proyectoLicenciamientoAmbientalFacade
								.getUbicacionProyectoPorIdProyecto(proyecto.getId());

						// incluir informacion de la sede de la zonal en el documento
						ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil
								.getBean(ProyectoSedeZonalUbicacionController.class);
						String sedeZonal = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(
								usuario_area.get(0), "PROYECTOSUIAIII", null, proyecto, null);
						localizacionEntidadResponsable = sedeZonal;
						// localizacionEntidadResponsable =
						// parroquia.getUbicacionesGeografica().getNombre();

//						if (area != null && area.getUbicacionesGeografica() != null && area.getUbicacionesGeografica().getUbicacionesGeografica() != null)
//							localizacionEntidadResponsable = area.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();

//						if (isFirmaSubsecretario!=null){
						if (imagenesReporte.size() <= 0) {
							if (islogo_area != null && ispie_area != null) {
								parametrosReporte.put("urlImagen-0", islogo_area);
								parametrosReporte.put("urlImagen-1", ispie_area);
							} else {
								return "Falta el logo de area.";
							}
						}

//						parametrosReporte.put("urlImagen-2", isFirmaSubsecretario);
						parametrosReporte.put("subsecretario", nombre_usuario);
						parametrosReporte.put("direccionRepresentanteLegal", direccionRepresentanteLegal);
						parametrosReporte.put("telefonoRepresentanteLegal", telefonoRepresentanteLegal);
						parametrosReporte.put("emailRepresentanteLegal", emailRepresentanteLegal);
						parametrosReporte.put("cedulaRepresentanteLegal", cedulaRepresentanteLegal);
						parametrosReporte.put("entidadResponsable", entidadResponsable);
						parametrosReporte.put("localizacionEntidadResponsable", localizacionEntidadResponsable);
						parametrosReporte.put("numeroResolucionCertificado",
								secuenciasFacade.getSecuenciaResolucionAreaResponsableNuevoFormato(area));
						parametrosReporte.put("urlImagen-3", isborrador);
						parametrosReporte.put("direccionMae", "");
						String codigoProyecto = proyecto.getCodigo();

						long idTarea = proyecto.getId();
						String nombreProcesoDirectorioGuardar = Constantes.CARPETA_CATEGORIA_UNO;
						String nombreProcesoConcatenarNombreFichero = "Categoría Uno";
						String nombreFichero = "Certificado.pdf";
						String mime = "application/pdf";
						String extension = ".pdf";
						Integer idTabla = proyecto.getId();
						String nombreTabla = ProyectoLicenciamientoAmbiental.class.getSimpleName();
						TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_CATEGORIA_UNO;

						List<String> lista = getCodigoQrUrl(codigoProyecto,
								GenerarQRCertificadoAmbiental.tipo_suia_iii);

						parametrosReporte.put("urlImagen-4", lista.get(1));

						Documento resultado = reportesFacade.generarReporteGuardarAlfresco(parametrosReporte,
								direccionPlantillaJasperReport, subreportes, subreportesUrl, imagenesReporte, idTarea,
								codigoProyecto, idTarea, nombreProcesoDirectorioGuardar,
								nombreProcesoConcatenarNombreFichero, nombreFichero, mime, extension, idTabla,
								nombreTabla, tipoDocumento);

						if (resultado != null) {

							List<CertificadoRegistroAmbiental> certificados = categoria1Facade
									.getCertificadoRegistroAmbientalPorIdProyecto(proyecto.getId());
							if (certificados != null) {
								for (int i = 0; i < certificados.size(); i++) {
									categoria1Facade.guardarRegistro(certificados.get(i));
								}
							}

							CertificadoRegistroAmbiental certificadoRegistroAmbiental = new CertificadoRegistroAmbiental();
							certificadoRegistroAmbiental.setDocumento(resultado);
							certificadoRegistroAmbiental.setProyecto(proyecto);

							categoria1Facade.guardarRegistro(certificadoRegistroAmbiental);

							DocumentoProyecto documentoProyecto = new DocumentoProyecto();
							documentoProyecto.setDocumento(resultado);
							documentoProyecto.setProyectoLicenciamientoAmbiental(proyecto);

							categoria1Facade.guardarRegistro(documentoProyecto);

							resultado.setUsuarioCreacion(cedula_usuario);
							documentosFacade.actualizarDocumento(resultado);
						} else {
							return "Error al Generar Documento.";
						}
//						} else {
//							return "Error de firma.";
//						}
					}
				} else {
					return "Error al buscar usuario representante.";
				}

				// Certificado generado correctamente (retorna error null)
				return null;
			} catch (Exception e) {
				LOG.error("Error al completar la tarea", e);
			}
		}
		return "Error al descargar el Certificado Ambiental. Comuníquese con mesa de ayuda";

	}

	public List<String> getCodigoQrUrl(String tramite, Integer tipo) {

		List<String> resultado = GenerarQRCertificadoAmbiental.getCodigoQrUrl(true, tramite, tipo);
		return resultado;
	}

	public Boolean habilitarFirmaMasiva(TaskSummaryCustom tarea) {
		Boolean resultado = false;
		if (tarea != null)
			resultado = tareaFirmaMasivaFacade.esTareaFirmaMasiva(tarea.getProcessId(), tarea.getTaskName());

		return !resultado;
	}

//	public void cargarDatosBandeja(ProyectoLicenciamientoAmbiental proyectoSeleccionado) {
//
//FichaAmbientalPmaController fichaAmbientalPmaController = JsfUtil.getBean(FichaAmbientalPmaController.class);
//File archivoTemporal = fichaAmbientalPmaController.generarLicenciaAmbiental(proyectoSeleccionado,2);
//
//		 
//	}

	public void cargarDatosBandeja(ProyectoLicenciamientoAmbiental proyectoSeleccionado) {
		// solo si es categoria II y esta en la tarea de "Validar Pago por servicios
		// administrativos"
		proyectosBean.setProyecto(proyectoSeleccionado);
		proyecto = proyectoSeleccionado;
		docId = documentosFacade.findDocuIdByDocuTableId(proyectoSeleccionado.getId());

		processId = documentosFacade.findProcessInstanceIdByDocuId(docId);

		FichaAmbientalGeneralFinalizarControllerV2 fichaAmbientalGeneralFinalizarControllerV2 = JsfUtil
				.getBean(FichaAmbientalGeneralFinalizarControllerV2.class);
		if (proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getCodigo() != null && proyectosBean
				.getProyecto().getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_II) {

			try {
				File archivoTemporal = generarLicenciaAmbiental(proyectoSeleccionado, 2);
				// guardarDocumentoOficio();

			} catch (Exception e) {
				LOG.error(e.getMessage());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			}
		} else {
			if (proyectosBean.getProyectoRcoa() != null) {

				try {
					PerforacionExplorativa objProyectoPE = fichaAmbientalMineria020Facade
							.cargarPerforacionExplorativaRcoa(proyectosBean.getProyectoRcoa().getId());
					if (objProyectoPE != null && objProyectoPE.getId() != null) {
						// recupero la tarea actual que se genero
						Long processInstanceId = bandejaTareasBean.getProcessId();
						TaskSummary tareaActual = procesoFacade.getCurrenTask(JsfUtil.getLoggedUser(),
								processInstanceId);
						// si la tarea actual es "Descargar documentos de Registro Ambiental " finalizo
						// el proceso
						if (tareaActual.getName().toLowerCase()
								.contains("descargar documentos de registro ambiental")) {
							Map<String, Object> processVariables = procesoFacade
									.recuperarVariablesProceso(JsfUtil.getLoggedUser(), processInstanceId);
							ProcessInstanceLog processLog = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(),
									processInstanceId);
							bandejaTareasBean.setTarea(new TaskSummaryCustomBuilder()
									.fromSuiaIII(processVariables, processLog.getProcessName(), tareaActual).build());
//							fichaAmbientalGeneralFinalizarControllerV2.generarFichaRegistroAmbientalRCOA();
							fichaAmbientalGeneralFinalizarControllerV2.setDescargarFicha(true);
							fichaAmbientalGeneralFinalizarControllerV2.setDescargarRegistro(true);
							fichaAmbientalGeneralFinalizarControllerV2.redireccionarBandeja();
							if (fichaAmbientalGeneralFinalizarControllerV2.getDescargarRegistro()) {
								fichaAmbientalGeneralFinalizarControllerV2.direccionar();
								fichaAmbientalGeneralFinalizarControllerV2.cambiarEstadoTarea();
							}
						}

					}
				} catch (ec.gob.ambiente.suia.exceptions.ServiceException | JbpmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public File generarLicenciaAmbiental(ProyectoLicenciamientoAmbiental proyectoActivo, Integer marcaAgua) {
		File archivoTemporal = null;
		File archivoLicenciaTemporal = null;
		File archivoAnexoTemporal = null;
		List<File> listaFiles = new ArrayList<File>();

		Boolean mineria = isMineriaArtesanal(proyectoActivo);
		Boolean libreAprovechamiento = isLibreAprovechamiento(proyectoActivo);
		Boolean exploracionInicial = isExploracionInicial(proyectoActivo);
		Boolean perforacionExplorativa = isPerforacionExplorativa(proyectoActivo);
		Boolean comercializacionHidrocarburos = proyectoActivo.getCatalogoCategoria().getCodigo().equals("21.01.07.03"); // false;
		Persona persona = null;
		/**
		 * Nombre:SUIA Descripción: Validación para obtener el cargo de la persona que
		 * firmará el documentos registro ambiental. ParametrosIngreso:
		 * PArametrosSalida: Fecha:16/08/2015
		 */

		String cargo = null;
		if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {
			cargo = "AUTORIDAD AMBIENTAL PROVINCIAL";

		} else {
			cargo = "SUBSECRETARIO DE CALIDAD AMBIENTAL";
		}
		/**
		 * FIN Validación para obtener el cargo de la persona que firmará el documentos
		 * registro ambiental.
		 */

		byte[] firma = null;

		Usuario usuarioSecretario = null;
		String reporteFinal = null;
		//
		try {

			/**
			 * Nombre:SUIA Descripción: Para obterner el anexo de las coordenadas de acuerdo
			 * al área. ParametrosIngreso: PArametrosSalida: Fecha:16/08/2015
			 */
			if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {
				cargo = proyectoActivo.getAreaResponsable().getAreaName();
				if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado().equals("MUNICIPIO")) {
					reporteFinal = "anexo_coordenadas_licencia_ambiental_categoria_II_Completo_entes_municipio";
				} else {
					reporteFinal = "anexo_coordenadas_licencia_ambiental_categoria_II_Completo_entes";
				}
			} else {
				usuarioSecretario = usuarioFacade.buscarUsuarioPorRol(cargo).get(0);
				reporteFinal = "anexo_coordenadas_licencia_ambiental_categoria_II_Completo";
			}
			/**
			 * FIN Para obterner el anexo de las coordenadas de acuerdo al área
			 */

			Usuario usuarioSecreUsuario = categoriaIIFacade
					.getPersonaSubsecretarioCalidadAmbiental(proyectoActivo.getAreaResponsable());
			if (usuarioSecretario != null) {
				persona = usuarioSecretario.getPersona();
				if (Usuario.isUserInRole(usuarioSecretario, AutoridadAmbientalFacade.GAD_MUNICIPAL)) {
					cargo = AutoridadAmbientalFacade.GAD_MUNICIPAL;
				} else {
					cargo = AutoridadAmbientalFacade.DIRECTOR_PROVINCIAL;
				}
			}
			
			if(proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(5) || 
					proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(6)){
				cargo = "DIRECCIÓN ZONAL";
			}
			
		} catch (Exception e) {
		}
		if (persona == null) {
			try {
				if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {
					persona = categoriaIIFacade
							.getPersonaAutoridadAmbientalEnte(proyectoActivo.getAreaResponsable().getAreaName());
				} else {
					persona = categoriaIIFacade.getPersonaSubsecretarioCalidadAmbiental();
				}
			} catch (Exception e) {
				LOG.error("Error al intentar recuperar la Persona Subsecretario de CalidadAmbiental.", e);
				try {
					GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil
							.getBean(GenerarNotificacionesAplicativoController.class);
					enviarNotificaciones.enviarNotificacionError(proyectoActivo, "responsable");
				} catch (Exception ex) {
					LOG.error("Error al intentar enviar notificacion cuando no existir usuario responsable.", ex);
				}
				return null;
			}
		} else {
			try {

				firma = categoriaIIFacade.getFirmaSubsecretarioCalidadAmbiental(proyectoActivo.getAreaResponsable());
			} catch (Exception ex) {
				LOG.error("Error al recuperar la firma del responsable del área.", ex);
				JsfUtil.addMessageError("Error al recuperar la firma del responsable del área.");
				try {
					GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil
							.getBean(GenerarNotificacionesAplicativoController.class);
					enviarNotificaciones.enviarNotificacionError(proyectoActivo, "firma");
				} catch (Exception e) {
					LOG.error("Error al intentar recuperar la firma del responsable del area.", e);
				}
				return null;
			}
		}
		// si no existe responsable envio notificacion
		if (persona == null) {
			try {
				GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil
						.getBean(GenerarNotificacionesAplicativoController.class);
				enviarNotificaciones.enviarNotificacionError(proyectoActivo, "responsable");
				// return null;
			} catch (Exception e) {
				LOG.error("Error al intentar enviar notificacion cuando no existir usuario responsable.", e);
			}
		}
		try {// licencia_ambiental_categoria_ii cargarDatosLicenciaAmbiental

			if (firma == null) {
				try {
					if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {
						String d = "firma__" + proyectoActivo.getAreaResponsable().getAreaAbbreviation() + ".png";
						firma = categoriaIIFacade.getFirmaAutoridadAmbientalEntes(d);
					} else {
						firma = categoriaIIFacade.getFirmaSubsecretarioCalidadAmbiental();
					}
				} catch (Exception ex) {
					GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil
							.getBean(GenerarNotificacionesAplicativoController.class);
					enviarNotificaciones.enviarNotificacionError(proyectoActivo, "firma");
					LOG.error("Error al cargar la firma", ex);
				}
			}
			// si no existe firma envio notificacion
			if (firma == null) {
				GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil
						.getBean(GenerarNotificacionesAplicativoController.class);
				enviarNotificaciones.enviarNotificacionError(proyectoActivo, "firma");
				// return null;
			}

			/**
			 * CAMBIO DE LOGO
			 */

			Area areaResponsable = proyectoActivo.getAreaResponsable();
			String nombre_pie = null;
			String nombre_logo = null;
			URL pie = null;
			URL logo = null;
			byte[] pie_datos = null;
			pie_datos = categoriaIIFacade.getLogo(proyectoActivo.getAreaResponsable());
			UtilDocumento utilDocumento = new UtilDocumento();

			nombre_pie = "pie__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";
			nombre_logo = "logo__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";
			pie = utilDocumento.getRecursoImage("ente/" + pie);
			System.out.println("VALOR DE LOGO =======>" + logo);

			if (pie == null) {
				try {
					// pie_datos = documentosFacade.descargarDocumentoPorNombre(nombre_pie);
					// File fi = new File(JsfUtil.devolverPathImagenMae());
					// byte[] fileContent = Files.readAllBytes(fi.toPath());
					File archivo = new File(JsfUtil.devolverPathImagenEnte(nombre_pie));
					// File archivo = new File(JsfUtil.devolverPathImagenMae());
					FileOutputStream file = new FileOutputStream(archivo);
					// file.write(pie_datos);
					// file.write(fileContent);
					file.close();
				} catch (Exception e) {
					LOG.error("Error al obtener la imagen del pie para el área " + areaResponsable.getAreaAbbreviation()
							+ " en /Documentos Fijos/DatosEnte/" + nombre_pie);
					File fi = new File(JsfUtil.devolverPathImagenMae());
					byte[] fileContent = Files.readAllBytes(fi.toPath());
					File archivo = new File(JsfUtil.devolverPathImagenMae());
					FileOutputStream file = new FileOutputStream(archivo);
					file.write(fileContent);
					file.close();
				}
			}

			if (mineria) {

				PlantillaReporte plantillaReporte = informeProvincialGADFacade.obtenerPlantillaReporte(
						TipoDocumentoSistema.RESOLUCION_REGISTRO_AMBIENTAL_020.getIdTipoDocumento());

				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII.crearLicenciaAmbientalCategoriaIIA("LicenciaAmbientalCategoríaII",categoriaIIFacade.cargarDatosLicenciaAmbientalMineroLibreAprovechamiento(fichaAmbientalPmaBean.getFichaAmbientalMineria().getNumeroResolucion(), proyectoActivo,mineria, JsfUtil.getLoggedUser().getNombre()),
						"licencia_ambiental_categoria_ii_mineria", "Licencia Ambiental Categoría II", persona, firma,
						pie_datos, cargo, proyectoActivo.getAreaResponsable(), marcaAgua, false);
			} else if (libreAprovechamiento) {
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII.crearLicenciaAmbientalCategoriaIIA(
						"LicenciaAmbientalCategoríaII",
						categoriaIIFacade.cargarDatosLicenciaAmbientalMineroLibreAprovechamiento(
								fichaAmbientalPmaBean.getFicha().getNumeroOficio(), proyectoActivo, mineria,
								JsfUtil.getLoggedUser().getNombre()),
						"licencia_ambiental_categoria_ii_mineria_la", "Licencia Ambiental Categoría II", persona, firma,
						pie_datos, cargo, proyectoActivo.getAreaResponsable(), marcaAgua, false);
			} else if (exploracionInicial) {
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII.crearLicenciaAmbientalCategoriaII(
						"LicenciaAmbientalCategoríaII",
						categoriaIIFacade.cargarDatosLicenciaAmbientalExploracionInicial(proyectoActivo,
								JsfUtil.getLoggedUser().getNombre()),
						"licencia_ambiental_categoria_ii_exploracion_inicial", "Licencia Ambiental Categoría II",
						persona, firma, cargo, proyectoActivo.getAreaResponsable(), marcaAgua, false);
			} else if (perforacionExplorativa) {
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII.crearLicenciaAmbientalCategoriaII(
						"LicenciaAmbientalCategoríaII",
						categoriaIIFacade.cargarDatosLicenciaAmbientalPerforacionExplorativa(proyectoActivo,
								JsfUtil.getLoggedUser().getNombre()),
						"licencia_ambiental_categoria_ii_exploracion_inicial", "Licencia Ambiental Categoría II",
						persona, firma, cargo, proyectoActivo.getAreaResponsable(), marcaAgua, true);
			} else if (comercializacionHidrocarburos) {
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII.crearLicenciaAmbientalCategoriaIIA(
						"LicenciaAmbientalCategoríaII",
						categoriaIIFacade.cargarDatosLicenciaAmbientalCompleto(proyectoActivo,
								JsfUtil.getLoggedUser().getNombre()),
						"licencia_ambiental_categoria_ii_comercializacion_hidrocarburos",
						"Licencia Ambiental Categoría II", persona, firma,pie_datos, cargo, proyectoActivo.getAreaResponsable(),
						marcaAgua, false);
			} else {
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII.crearLicenciaAmbientalCategoriaIIA(
						"LicenciaAmbientalCategoríaII",
						categoriaIIFacade.cargarDatosLicenciaAmbientalCompleto(proyectoActivo,
								JsfUtil.getLoggedUser().getNombre()),
						"licencia_ambiental_categoria_ii_n", "Licencia Ambiental Categoría II", persona, firma,
						pie_datos, cargo, proyectoActivo.getAreaResponsable(), marcaAgua, false);
			}
			// cargarDatosAnexosLicenciaAmbiental
			// anexo_coordenadas_licencia_ambiental_categoria_IIMINERIA_PERFORACION_EXPLORATIVA
			archivoAnexoTemporal = ReporteLicenciaAmbientalCategoriaII
					.crearAnexoCoordenadasLicenciaAmbientalCategoriaII("Anexo coordenadas",
							categoriaIIFacade.cargarDatosAnexosCompletoLicenciaAmbiental(proyectoActivo), reporteFinal,
							"Anexo coordenadas", proyectoActivo.getAreaResponsable(), marcaAgua);

			listaFiles.add(archivoLicenciaTemporal);
			listaFiles.add(archivoAnexoTemporal);
			archivoTemporal = UtilFichaMineria.unirPdf(listaFiles, "Ficha_Ambiental");

			Path path = Paths.get(archivoTemporal.getAbsolutePath());
			byte[] archivo = Files.readAllBytes(path);
			String reporteHtmlfinal = archivoTemporal.getName().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));

			// archivoFinalMemory = archivoFinal;
			FileOutputStream file = new FileOutputStream(archivoFinal);
			Document document = new Document(PageSize.A4, 36, 36, 54, 54);
			PdfWriter pdfWriter = PdfWriter.getInstance(document, file);
			pdfWriter.setBoxSize("art", new Rectangle(36, 54, 559, 788));
			boolean isInforme = true;
			pdfWriter.setPageEvent(
					(PdfPageEvent) (isInforme ? new PlantillaHeaderFooter() : new PlantillaHeaderFooterOficio()));
			file.write(archivo);
			file.close();
			nombreReporte = archivoTemporal.getName();
			pathTotal = archivoFinal.getAbsolutePath();
			pathTotal = (JsfUtil.devolverContexto("/reportesHtml/" + nombreReporte));

			Documento documentoOficio = new Documento();

			documentoOficio.setNombre("Resolución del Registro Ambiental.pdf");
			documentoOficio.setContenidoDocumento(archivo);
			documentoOficio.setNombreTabla("CategoriaIILicencia");
			documentoOficio.setIdTable(proyectoActivo.getId());
			documentoOficio.setDescripcion("Resolucion de Registro Ambiental");
			documentoOficio.setMime("application/pdf");
			documentoOficio.setExtesion(".pdf");

			documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(proyectoActivo.getCodigo(),
					"catIIOficioObservaciones", processId, documentoOficio,
					TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL, null);
			DocumentoProyecto documentoProyecto = new DocumentoProyecto();
			documentoProyecto.setDocumento(documento);
			documentoProyecto.setProyectoLicenciamientoAmbiental(proyectoActivo);
			documentosService.guardarDocumentoProyecto(documentoProyecto);

			String idAlfrescoWks = (documento.getIdAlfresco().split(";"))[0];
			String documentUrl = getUrlAlfresco(documento.getIdAlfresco());

			idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;

			listaDocumentos.add(documento);

			return archivoTemporal;

		} catch (Exception e) {
			LOG.error("Error al intentar generar el archivo del Registro Ambiental.", e);
			// JsfUtil.addMessageError("Error al intentar generar el archivo de licencia
			// Ambiental Categoría II.");
			return null;
		}

	}

	protected boolean isCatalogoCategoriaCodigoInicia(ProyectoLicenciamientoAmbiental proyecto, String[] values) {
		try {
			String code = proyecto.getCatalogoCategoria().getCodigo();
			for (String string : values) {
				if (code.startsWith(string))
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isMineriaArtesanal(ProyectoLicenciamientoAmbiental proyecto) {
		boolean result = isCatalogoCategoriaCodigoInicia(proyecto, MINERIA_ARTESANAL);
		return result;
	}

	private boolean isLibreAprovechamiento(ProyectoLicenciamientoAmbiental proyecto) {
		boolean result = isCatalogoCategoriaCodigoInicia(proyecto, LIBRE_APROVECHAMIENTO);
		return result;
	}

	private boolean isExploracionInicial(ProyectoLicenciamientoAmbiental proyecto) {
		boolean result = isCatalogoCategoriaCodigoInicia(proyecto, MINERIA_EXPLORACION_INICIAL);
		return result;
	}

	private boolean isPerforacionExplorativa(ProyectoLicenciamientoAmbiental proyecto) {
		boolean result = isCatalogoCategoriaCodigoInicia(proyecto, MINERIA_PERFORACION_EXPLORATIVA);
		return result;
	}

	public Usuario getPersonaSubsecretarioCalidadAmbiental(Area areaResponsable) throws ServiceException {
		return autoridadAmbientalFacade.getSubsecretarioCalidadAmbiental(areaResponsable);
	}

	public StreamedContent descargarInformacion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent;
			Documento auxdoc = new Documento();
			Boolean generar = false;

			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;

			// adjuntarPronunciamientoCertificadoViabilidadBean
			// .setAdjuntoInformeInspeccion(requisitosPreviosFacade.descargarDocumentoRequisitosPrevios(adjuntarPronunciamientoCertificadoViabilidadBean.getProyecto().getId(),
			// TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL));
//			auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(
//					adjuntarPronunciamientoCertificadoViabilidadBean.getProyecto().getId(),
//					ProyectoLicenciamientoAmbiental.class.getSimpleName(),
//					TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD);

			tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;

			auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(proyectosBean.getProyecto().getId(),
					CategoriaIILicencia, tipoDocumento);

			documentoContent = documentosFacade.descargar(auxdoc.getIdAlfresco());
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(auxdoc.getNombre());
				documentoDescargado = true;
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		}

		catch (Exception e) {
			JsfUtil.addMessageError(
					"Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	/**
	 * PARA FIRMA CON TOKEN
	 * 
	 * @param event
	 */
	@SuppressWarnings("static-access")
	public String firmarOficio() {

		try {
			System.out.println("firmarOficio proyecto=====>   " + proyectosBean.getProyecto().getId());
			// mostrarBotonFirma=false;
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;
			tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;

			documentoCargado = documentosFacade.documentoXTablaIdXIdDocUnicos(proyectosBean.getProyecto().getId(),
					CategoriaIILicencia, tipoDocumento);

			if (documentoCargado.getIdAlfresco() != null) {
				String documento = documentosFacade.direccionDescarga(documentoCargado);
				DigitalSign firmaE = new DigitalSign();
				documentoDescargado = true;
				int cont = 0;
				cont++;
				if (listaSelectProyectos.size() == cont) {
					for (int i = listaProyectos.size() - 1; i >= 0; i--) {
						if (listaSelectProyectos.contains(listaProyectos.get(i))) {
							listaProyectos.remove(i);
						}
					}
				}
				return firmaE.sign(documento, loginBean.getUsuario().getNombre());

			} else
				return "";
		} catch (Throwable e) {
		//	JsfUtil.addMessageError("Error al realizar la operación.");
			return "";
		}
	}

	public void uploadListenerInformacionFirmada(FileUploadEvent event) throws ServiceException, CmisAlfrescoException {
		System.out.println("Valor de documentoDescargado===========>" + documentoDescargado);

		if (documentoDescargado) {
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;

			// }
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoInformacionManual = new Documento();
			documentoInformacionManual.setId(null);
			documentoInformacionManual.setContenidoDocumento(contenidoDocumento);
			documentoInformacionManual.setNombre(event.getFile().getFileName());
			documentoInformacionManual.setExtesion(".pdf");
			documentoInformacionManual.setMime("application/pdf");
			documentoInformacionManual.setNombre("Resolución del Registro Ambiental.pdf");

			documentoInformacionManual.setIdTable(proyectosBean.getProyecto().getId());
			documentoInformacionManual.setNombreTabla(CategoriaIILicencia);
			documentoInformacionManual.setTipoDocumento(auxTipoDocumento);
			String proyectoIdStr = String.valueOf(proyectosBean.getProyecto().getId());
			documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(proyectoIdStr, "catIIOficioObservaciones",
					processId, documentoInformacionManual,
					TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL, null);

			informacionSubida = true;
			// adjuntarPronunciamientoCertificadoViabilidadBean.setHabilitarBotonFinalizar(true);

			nombreDocumentoFirmado = event.getFile().getFileName();
		} else {
			JsfUtil.addMessageError("No ha descargado el documento para la firmas");
		}
	}

	public void enviarParaFirma() {
		try {
			bandejaTareasBean.setProcessId(0L);
			bandejaTareasBean.setListaProyectos(listaSelectProyectos);

			System.out.println("Extension de la lista" + listaSelectProyectos.size());
			int valor = 0;
			for (Selectable<ProyectoLicenciamientoAmbiental> selectable : bandejaTareasBean.getListaProyectos()) {
				// Flujo flujo =
				// flujosCategoriaFacade.getFlujoPorIdProceso(tarea.getProcessId());

				ProyectoLicenciamientoAmbiental proyectoActivo = selectable.getValue();
				generarLicenciaAmbiental(proyectoActivo, 2);
				valor++;
				System.out.println("valor===>" + valor);
				proyectosBean.setProyecto(proyectoActivo);

				System.out.println("LUEGO DE FIRMAR====>" + valor);
				firmarOficio();
			}
			mostrarBotonFirma = false;
			// JsfUtil.redirectTo("/pages/rcoa/firmaMasiva/firmaElectronicaMasivaRegistrosAntiguos.jsf");
		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
		}
	}

	public void limpiarFirma() {
		listaSelectTareas = null;

		RequestContext.getCurrentInstance().update("tabview");
	}

	public String getUrlAlfresco(String documento) {
		String urlAlfresco = null;
		try {
			urlAlfresco = documentosCoaFacade.direccionDescarga(documento);
			String tiketStr = "alf_ticket=";
			if (!urlAlfresco.endsWith(tiketStr)) {
				int pos = urlAlfresco.lastIndexOf(tiketStr) + tiketStr.length();
				urlAlfresco = urlAlfresco.substring(0, pos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return urlAlfresco;
	}

	public void generarResolucionRegistroAmbiental(TaskSummaryCustom tarea) {
		try {
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),
					tarea.getProcessInstanceId());
			String tramite = (String) variables.get("tramite");

			ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);

			Boolean generarNuevo = false;
			DocumentoRegistroAmbiental documento = documentosRegistroFacade.documentoXTablaIdXIdDoc(proyecto.getId(),
					ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RESOLUCION_AMBIENTAL_RCOA);
			if (documento != null && !documento.getUsuarioCreacion().equals(loginBean.getNombreUsuario()))
				generarNuevo = true;
			else if (documento == null)
				generarNuevo = true;

			if (generarNuevo) {
				GeneracionDocumentosController oficioController = (GeneracionDocumentosController) BeanLocator
						.getInstance(GeneracionDocumentosController.class);

				documento = oficioController.generarResolucionRegistroAmbiental(proyecto);
			}

			if (documento != null) {
				String idAlfrescoWks = (documento.getAlfrescoId().split(";"))[0];
				String documentUrl = getUrlAlfresco(documento.getAlfrescoId());

				idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;

				DocumentoTareaDTO docTarea = new DocumentoTareaDTO();
				docTarea.setIdAlfresco(idAlfrescoWks);
				docTarea.setNombreDocumento(documento.getNombre());
				docTarea.setTarea(tarea);
				docTarea.setDocumentoRegistroAmbiental(documento);
				docTarea.setNumeroTramite(tramite);

				listaDocumentoTarea.add(docTarea);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public StreamedContent descargarDocumento(DocumentoTareaDTO itemFirma) {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			String nombreDocumento = "";

			documentoContent = documentosFacade.descargar(itemFirma.getIdAlfresco());
			nombreDocumento = itemFirma.getNombreDocumento();

			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(nombreDocumento);
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(
					"Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
}
