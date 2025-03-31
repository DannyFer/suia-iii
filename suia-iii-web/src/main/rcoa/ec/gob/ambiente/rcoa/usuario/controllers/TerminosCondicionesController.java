package ec.gob.ambiente.rcoa.usuario.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.terminoscondiciones.facade.DocumentosTerminosCondicionesFacade;
import ec.gob.ambiente.rcoa.terminoscondiciones.model.DocumentoTerminosCondiciones;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class TerminosCondicionesController {

	private final Logger LOG = Logger.getLogger(TerminosCondicionesController.class);

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private DocumentosTerminosCondicionesFacade documentosFacade;
	@EJB
	private SecuenciasFacade secuenciasFacade;

	@Getter
	@Setter
	private byte[] archivoBytes;

	@Getter
	@Setter
	private boolean documentoDescargado = false, ambienteProduccion, documentoSubido = false;

	@Getter
	@Setter
	private String nombreFormulario;

	@Getter
	@Setter
	public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");

	@Getter
	@Setter
	private String codigo, codigoRSQ;

	private Usuario usuario;

	@Getter
	@Setter
	private boolean verFirmar;

	@Getter
	@Setter
	boolean token, editarToken;

	@Getter
	@Setter
	private DocumentoTerminosCondiciones documento, documentoFirmado;

	@Getter
	@Setter
	private Organizacion organizacion;

	@PostConstruct
	public void init() {
		try {
			usuario = JsfUtil.getLoggedUser();

			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			token = true;
			if (!ambienteProduccion) {
				verificaToken();
			}

			organizacion = organizacionFacade.buscarPorRuc(usuario.getNombre());
			documento = documentosFacade.obtenerDocumentoPorUsuario(
					TipoDocumentoSistema.TERMINOS_CONDICIONES_FIRMADO_USUARIO, usuario.getId());

			if (documento == null) {
				documento = new DocumentoTerminosCondiciones(TipoDocumentoSistema.TERMINOS_CONDICIONES_FIRMADO_USUARIO,
						usuario.getId());
				generadorDocumento();
				verFirmar = true;
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				String fechaDocumento = sdf.format(documento.getFechaCreacion());

				String fechaActual = sdf.format(new Date());

				boolean nuevoDocumento = false;
				if (!fechaActual.equals(fechaDocumento)) {
					nuevoDocumento = true;
				}

				if (!ambienteProduccion) {

					if (documento.getFechaModificacion() != null) {
						verFirmar = false;
					} else {
						verFirmar = true;
					}

				} else {
					if (documentosFacade.verificarFirmaVersion(documento.getIdAlfresco()))
						verFirmar = false;
					else {
						if (nuevoDocumento) {
							documento = new DocumentoTerminosCondiciones(
									TipoDocumentoSistema.TERMINOS_CONDICIONES_FIRMADO_USUARIO, usuario.getId());
							generadorDocumento();
						}
						verFirmar = true;
					}
				}
			}

		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void generadorDocumento() {
		try {
			String nombreOperador = organizacion != null ? organizacion.getNombre() : usuario.getPersona().getNombre();
			String representanteEmpresa = "";
			if (organizacion != null) {
				Organizacion organizacionRep = organizacionFacade.buscarPorRuc(organizacion.getPersona().getPin());

				if (organizacionRep != null) {
					representanteEmpresa = organizacionRep.getPersona().getNombre() + "<br />" + nombreOperador;
				} else {
					representanteEmpresa = organizacion.getPersona().getNombre() + "<br />" + nombreOperador;
				}
			} else {
				representanteEmpresa = usuario.getPersona().getNombre();
			}

			TerminosCondicionesHtml entity = new TerminosCondicionesHtml(representanteEmpresa);
			PlantillaReporte plantillaReporte = plantillaReporteFacade
					.getPlantillaReporte(TipoDocumentoSistema.TERMINOS_CONDICIONES_FIRMADO_USUARIO);

			String nombreReporte = "TerminosCondicionesFirmadoUsuario" + usuario.getNombre() + ".pdf";
			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
					true, entity);

			Path path = Paths.get(informePdf.getAbsolutePath());
			String reporteHtmlfinal = nombreReporte;
			archivoBytes = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(Files.readAllBytes(path));
			file.close();

			TipoDocumento tipoDoc = new TipoDocumento();
			tipoDoc.setId(TipoDocumentoSistema.TERMINOS_CONDICIONES_FIRMADO_USUARIO.getIdTipoDocumento());

			documento.setNombre(nombreReporte);
			documento.setExtesion(".pdf");
			documento.setMime("application/pdf");
			documento.setContenidoDocumento(archivoBytes);
			documento.setTipoDocumento(tipoDoc);
			documento.setUsuarioId(usuario.getId());

			documentosFacade.guardarDocumento(usuario.getNombre(), documento);
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}

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

	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	public String firmarDocumento() {
		try {

			String documentOffice = documentosFacade.direccionDescarga(documento.getIdAlfresco());
			String usuarioFirma = "";
			if (organizacion != null) {

				if (organizacion.getPersona().getPin().length() == 13) {

					Organizacion organizacionRep = organizacionFacade.buscarPorRuc(organizacion.getPersona().getPin());

					if (organizacionRep != null) {
						if (organizacionRep.getPersona().getPin().length() == 13) {
							String cadena = organizacionRep.getPersona().getPin();
							cadena = cadena.substring(0, cadena.length() - 3);
							usuarioFirma = cadena;
						} else {
							usuarioFirma = organizacionRep.getPersona().getPin();
						}
					} else {
						String cadena = organizacion.getPersona().getPin();
						cadena = cadena.substring(0, cadena.length() - 3);
						usuarioFirma = cadena;
					}
				} else {
					usuarioFirma = organizacion.getPersona().getPin();
				}
			} else {
				if (JsfUtil.getLoggedUser().getNombre().length() == 13) {
					String cadena = JsfUtil.getLoggedUser().getNombre();
					cadena = cadena.substring(0, cadena.length() - 3);
					usuarioFirma = cadena;
				} else {
					usuarioFirma = JsfUtil.getLoggedUser().getNombre();
				}
			}

			return DigitalSign.sign(documentOffice, usuarioFirma);

		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del certificado", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}

	public StreamedContent descargarDocumento() throws Exception {

		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),
					documento.getExtesion());
			content.setName(documento.getNombre());
		}

		documentoDescargado = true;
		return content;
	}

	public void enviar() {
		try {
			if (token) {
				String idAlfrescoInforme = documento.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
					JsfUtil.addMessageError("El documento no está firmado electrónicamente.");
					return;
				}

			} else {
				if (!documentoSubido) {
					JsfUtil.addMessageError("Debe adjuntar el documento firmado.");
					return;
				} else {
					documento = documentosFacade.guardarDocumento(usuario.getNombre(), documento);
				}
			}

			verFirmar = false;
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);

		} catch (Exception e) {
			LOG.error(e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}

	public String firmaElectronica() {
		try {
			String usuarioFirma = "";
			if (organizacion != null) {

				if (organizacion.getPersona().getPin().length() == 13) {

					Organizacion organizacionRep = organizacionFacade.buscarPorRuc(organizacion.getPersona().getPin());

					if (organizacionRep != null) {
						if (organizacionRep.getPersona().getPin().length() == 13) {
							String cadena = organizacionRep.getPersona().getPin();
							cadena = cadena.substring(0, cadena.length() - 3);
							usuarioFirma = cadena;
						} else {
							usuarioFirma = organizacionRep.getPersona().getPin();
						}
					} else {
						String cadena = organizacion.getPersona().getPin();
						cadena = cadena.substring(0, cadena.length() - 3);
						usuarioFirma = cadena;
					}
				} else {
					usuarioFirma = organizacion.getPersona().getPin();
				}
			} else {
				if (JsfUtil.getLoggedUser().getNombre().length() == 13) {
					String cadena = JsfUtil.getLoggedUser().getNombre();
					cadena = cadena.substring(0, cadena.length() - 3);
					usuarioFirma = cadena;
				} else {
					usuarioFirma = JsfUtil.getLoggedUser().getNombre();
				}
			}

			return DigitalSign.sign(documentosFacade.direccionDescarga(documento.getIdAlfresco()), usuarioFirma);
		} catch (Throwable e) {
			LOG.error(e);
			JsfUtil.addMessageError("Error al realizar la operación.");
			return "";
		}
	}

	boolean firmadoFisico;

	public void uploadFileFirmado(FileUploadEvent event) {
		try {
			documento = new DocumentoTerminosCondiciones();
			documento.cargarArchivo(event.getFile().getContents(), event.getFile().getFileName());
			documento.setUsuarioModificacion(usuario.getNombre());
			documento.setFechaModificacion(new Date());
			documento.setUsuarioId(usuario.getId());
			TipoDocumento tipoDoc = new TipoDocumento();
			tipoDoc.setId(TipoDocumentoSistema.TERMINOS_CONDICIONES_FIRMADO_USUARIO.getIdTipoDocumento());
			documento.setTipoDocumento(tipoDoc);
			firmadoFisico = true;
			documentoSubido = true;
			JsfUtil.addMessageInfo("El documento de términos y condiciones fue adjuntado con éxito");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al guardar el documento del de términos y condiciones.");
			e.printStackTrace();
		}
	}

	public StreamedContent descargarDocumentoOperador() throws Exception {

		documento = documentosFacade
				.obtenerDocumentoPorUsuario(TipoDocumentoSistema.TERMINOS_CONDICIONES_FIRMADO_USUARIO, usuario.getId());

		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),
					documento.getExtesion());
			content.setName(documento.getNombre());
		}

		documentoDescargado = true;
		return content;
	}

}
