package ec.gob.ambiente.proyectos.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PermisoRegistroGeneradorDesechosFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ViewScoped
@ManagedBean
public class ArchivarProyectosController implements Serializable {

	private static final long serialVersionUID = 4689463316622237704L;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
	private PermisoRegistroGeneradorDesechosFacade permisoRegistroGeneradorDesechosFacade;
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
	@EJB
	private DocumentosCoaFacade documentosCoaFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
    private CrudServiceBean crudServiceBean;
	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa = new ProyectoLicenciaCoa();
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental = new ProyectoLicenciamientoAmbiental();
	@Getter
	@Setter
	private List<DocumentosCOA> listaDocumentosSolicitud = new ArrayList<DocumentosCOA>();
	@Getter
	@Setter
	private List<DocumentosCOA> listaDocumentosResolucion = new ArrayList<DocumentosCOA>();
	@Getter
	@Setter
	private DocumentosCOA documentoSolicitud;
	@Getter
	@Setter
	private DocumentosCOA documentoResolucion;
	@Getter
	@Setter
	private String codigoTramite;
	@Getter
	@Setter
	private String motivo, asociado, codigoRGD;
	@Getter
	@Setter
	private Boolean deshabilitado = true;
	@Getter
	@Setter
	private String motivoArchivacion;
	@Getter
	@Setter
	private Date fechaArchivacion;
	@Getter
	@Setter
	private Date fechaRegistro;
	@Getter
	@Setter
	private Integer idProyecto;
	@Getter
	@Setter
	private Boolean esFinalizado, mostrarPaneles;

	@PostConstruct
	public void init() {
		mostrarPaneles = false;
		esFinalizado = false;
	}
	
	public void validarCodigo() {
	    try {
	        motivoArchivacion = "";
	        mostrarPaneles = false;

	        ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(codigoTramite);
	        ProyectoLicenciaCoa proyectoRcoa = proyectoLicenciaCoaFacade.buscarProyecto(codigoTramite);

	        proyectoLicenciamientoAmbiental = (proyecto != null && proyecto.getId() != null) ? proyecto: null;
	        proyectoLicenciaCoa = (proyectoRcoa != null && proyectoRcoa.getId() != null) ? proyectoRcoa : null;

	        if (proyecto != null && proyecto.getId() != null) {
	            Integer idCOA = proyecto.getId();
	            GeneradorDesechosPeligrosos rgd = generadorDesechosPeligrososFacade.buscarRGDPorProyectoCoa(idCOA);
	            if (rgd != null && proyecto.getCatalogoCategoria().getCategoria().getId().equals(2) && proyecto.isCompletado()) {
	                JsfUtil.addMessageInfo("El Proyecto tiene asociado el Registro Generador de Residuos o Desechos Peligrosos y/o Especiales No. " + rgd.getCodigo());
	            }

	            deshabilitado = false;
	            motivoArchivacion = proyecto.getMotivoEliminar();
	            fechaArchivacion = proyecto.getFechaDesactivacion();
	            fechaRegistro = proyecto.getFechaModificacion();

	        } else if (proyectoRcoa != null && proyectoRcoa.getId() != null) {
	            if (!(proyectoRcoa.getCategoria().getId().equals(4) || proyectoRcoa.getCategoria().getId().equals(3) || proyectoRcoa.getCategoria().getId().equals(1))) {
	                Integer idRCOA = proyectoRcoa.getId();
	                String realizoPago = procesoFacade.getStatusPagoRcoa(codigoTramite);
	                RegistroGeneradorDesechosRcoa asociadoRGD = registroGeneradorDesechosRcoaFacade.buscarRGDPorProyectoRcoa(idRCOA);
	                if (asociadoRGD != null && proyectoRcoa.getCategoria().getId().equals(2) && !proyectoRcoa.getProyectoFinalizado()) {
	                    JsfUtil.addMessageInfo("El Proyecto tiene asociado el Registro Generador de Residuos o Desechos Peligrosos y/o Especiales No. " + asociadoRGD.getCodigo());
	                    if ("Completed".equals(realizoPago)&& !asociadoRGD.getFinalizado()){
			            	JsfUtil.addMessageError("El Registro Generador asociado tiene un pago vigente");
			            	return;
			            }
	                }

	                deshabilitado = false;
	                motivoArchivacion = proyectoRcoa.getRazonEliminacion();
	                fechaArchivacion = proyectoRcoa.getFechaDesactivacion();
	                fechaRegistro = proyectoRcoa.getFechaModificacion();
	                
	            } else {
	                JsfUtil.addMessageInfo("El Proyecto que se va archivar es " + codigoTramite);
	                deshabilitado = false;
	            }
	            
	        } else {
	            deshabilitado = true;
	            JsfUtil.addMessageError("El código de proyecto no existe o es un proyecto archivado o no cumple con las validaciones para el archivo");
	            return;
	        }

	        if ((proyecto != null && proyecto.getId() != null && proyecto.isCompletado()) || (proyectoRcoa != null && proyectoRcoa.getId() != null && proyectoRcoa.getProyectoFinalizado())) {
	            JsfUtil.addMessageInfo("El proyecto está FINALIZADO");
	            esFinalizado = true;
	        } else {
	            JsfUtil.addMessageInfo("El proyecto está EN CURSO");
	            esFinalizado = false;
	        }

	        if ((proyectoRcoa != null && proyectoRcoa.getId() != null && proyectoRcoa.getCategoria().getId().equals(1)) || (proyecto != null && proyecto.getId() != null && proyecto.getCatalogoCategoria().getCategoria().getId().equals(1))) {
	            esFinalizado = false;
	        }

	        this.mostrarPaneles = true;

	    } catch (Exception e) {
	        e.printStackTrace();
	        JsfUtil.addMessageError("Ocurrió un error al validar el código.");
	    }
	}
	
	public void uploadSolicitud(FileUploadEvent event) {
		documentoSolicitud = new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoSolicitud.setContenidoDocumento(contenidoDocumento);
		documentoSolicitud.setNombreDocumento(event.getFile().getFileName());
		documentoSolicitud.setExtencionDocumento(".pdf");
		documentoSolicitud.setTipo("application/pdf");
		documentoSolicitud.setIdTabla(idProyecto);

		listaDocumentosSolicitud.add(documentoSolicitud);
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}

	public void uploadResolucion(FileUploadEvent event) {
		documentoResolucion = new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoResolucion.setContenidoDocumento(contenidoDocumento);
		documentoResolucion.setNombreDocumento(event.getFile().getFileName());
		documentoResolucion.setExtencionDocumento(".pdf");
		documentoResolucion.setTipo("application/pdf");
		documentoResolucion.setIdTabla(idProyecto);
		
		listaDocumentosResolucion.add(documentoResolucion);
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void archivar() {
	    try {
	        String codigoProyecto = null;
	        String	nombreTabla = null;
	        Integer idProyecto = null;

	        if (proyectoLicenciaCoa != null) {
	            codigoProyecto = proyectoLicenciaCoa.getCodigoUnicoAmbiental();
	            idProyecto = proyectoLicenciaCoa.getId();
	            nombreTabla = ProyectoLicenciaCoa.class.getSimpleName();
	        } else if (proyectoLicenciamientoAmbiental != null) {
	            codigoProyecto = proyectoLicenciamientoAmbiental.getCodigo();
	            idProyecto = proyectoLicenciamientoAmbiental.getId();
	            nombreTabla = ProyectoLicenciamientoAmbiental.class.getSimpleName();
	        }

	        if (listaDocumentosSolicitud.isEmpty()) {
	            JsfUtil.addMessageError("Debe adjuntar la Solicitud del pedido del Proponente.");
	            return;
	        }

	        if (this.esFinalizado && listaDocumentosResolucion.isEmpty()) {
	            JsfUtil.addMessageError("Debe adjuntar la Resolución de extinción del proyecto.");
	            return;
	        }

	        for (DocumentosCOA documento : listaDocumentosSolicitud) {
	            if (documento.getId() == null) {
	                documento.setIdTabla(idProyecto);
	                documento.setNombreTabla(nombreTabla);
	                documento = documentosCoaFacade.guardarDocumentoAlfresco(codigoProyecto,Constantes.CARPETA_ARCHIVO_PROYECTO, 0L, documento, TipoDocumentoSistema.ARCHIVAR_PROYECTOS_MESADEAYUDA);
	            }
	        }

	        for (DocumentosCOA documento : listaDocumentosResolucion) {
	            if (documento.getId() == null) {
	                documento.setIdTabla(idProyecto);
	                documento.setNombreTabla(nombreTabla);
	                documento = documentosCoaFacade.guardarDocumentoAlfresco(codigoProyecto,Constantes.CARPETA_ARCHIVO_PROYECTO, 0L, documento, TipoDocumentoSistema.ARCHIVAR_PROYECTOS_MESADEAYUDA);
	            }
	        }
	        
	        if (proyectoLicenciaCoa != null) {
	            String resultadoRcoa = proyectoLicenciaCoaFacade.archivarTramiteRcoa(codigoTramite, motivo);
	            if ("1".equals(resultadoRcoa)) {
	                JsfUtil.addMessageInfo("Se ha archivado el proyecto: " + codigoTramite);
	            } else {
	                JsfUtil.addMessageError("No se ha encontrado un proyecto válido con ese código.");
	            }

	            Integer idRCOA = proyectoLicenciaCoa.getId();
	            RegistroGeneradorDesechosRcoa asociadoRGD = registroGeneradorDesechosRcoaFacade.buscarRGDFPorProyectoRcoa(idRCOA);
	            if (asociadoRGD != null && proyectoLicenciaCoa.getCategoria().getId().equals(2) && !proyectoLicenciaCoa.getProyectoFinalizado()) {
	                JsfUtil.addMessageInfo("Se archivó o liberó el Registro Generador de Residuos o Desechos Peligrosos y/o Especiales No. " + asociadoRGD.getCodigo());
	            }

	        } else {
	            ProyectoLicenciamientoAmbiental proyectoBuscadocoa = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(codigoTramite);
	            if (proyectoBuscadocoa != null && proyectoBuscadocoa.getId() != null) {
	                String resultadoCoa = proyectoLicenciaCoaFacade.archivarTramiteCoa(codigoTramite, motivo);
	                if ("1".equals(resultadoCoa)) {
	                    JsfUtil.addMessageInfo("Se ha archivado el proyecto: " + codigoTramite);
	                } else {
	                    JsfUtil.addMessageError("No se ha encontrado un proyecto válido con ese código.");
	                }
	            } else {
	                JsfUtil.addMessageError("No se han encontrado proyectos válidos con ese código.");
	            }

	            Integer idCOA = proyectoBuscadocoa.getId();
	            GeneradorDesechosPeligrosos RGD = generadorDesechosPeligrososFacade.buscarRGDFPorProyectoCoa(idCOA);
	            if (RGD != null && proyectoBuscadocoa.getCatalogoCategoria().getCategoria().getId().equals(2) && !proyectoBuscadocoa.isFinalizado()) {
	                JsfUtil.addMessageInfo("Se archivó o liberó el Registro Generador de Residuos o Desechos Peligrosos y/o Especiales No. " + RGD.getCodigo());
	            }

	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
	    }

	    notificacionOperador();
	    deshabilitado = true;
	    codigoTramite = "";
	    motivo = "";
	    asociado = "";
	    listaDocumentosSolicitud.clear();
	    listaDocumentosResolucion.clear();
	    documentoSolicitud = new DocumentosCOA();
	    documentoResolucion = new DocumentosCOA();
	    mostrarPaneles = false;
	}
	
	public void cancelar() {
		deshabilitado = true;
		motivo = "";
		codigoTramite = "";
		mostrarPaneles = false;
		esFinalizado = false;
		documentoResolucion = new DocumentosCOA();
		documentoSolicitud = new DocumentosCOA();
	}
	
	public void notificacionOperador() {
		try {
			String nombreOperador = "";
			String proyectoCodigo = "";
			Usuario usuarioProyecto = new Usuario();
			if (proyectoLicenciaCoa != null) {
				usuarioProyecto = proyectoLicenciaCoa.getUsuario();
				proyectoCodigo = proyectoLicenciaCoa.getCodigoUnicoAmbiental();
			} else if (proyectoLicenciamientoAmbiental != null) {
				usuarioProyecto = proyectoLicenciamientoAmbiental.getUsuario();
				proyectoCodigo = proyectoLicenciamientoAmbiental.getCodigo();
			}

			if (usuarioProyecto.getId() != null) {
				if (usuarioProyecto.getPersona().getOrganizaciones().isEmpty()) {
					nombreOperador = usuarioProyecto.getPersona().getNombre();
				} else {
					Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioProyecto.getNombre());
					usuarioProyecto.getPersona().setContactos(organizacion.getContactos());
					nombreOperador = organizacion.getNombre();
				}

				MensajeNotificacion mensajeNotificacion = new MensajeNotificacion();

				mensajeNotificacion = mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionArchivoProyectoMesa");

				Object[] parametrosCorreoNuevo = new Object[] { nombreOperador, proyectoCodigo, motivo };

				String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreoNuevo);

				Email.sendEmail(usuarioProyecto, mensajeNotificacion.getAsunto(), notificacion, proyectoCodigo,	JsfUtil.getLoggedUser());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}