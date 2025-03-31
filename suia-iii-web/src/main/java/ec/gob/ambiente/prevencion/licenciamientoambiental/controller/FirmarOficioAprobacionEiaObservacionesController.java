package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.licenciamientoambiental.informes.bean.OficioAprobacionEiaBean;
import ec.gob.ambiente.prevencion.tdr.controller.RevisarDocumentacionGeneralController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.VehiculoDesechoSanitarioProcesoPma;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class FirmarOficioAprobacionEiaObservacionesController implements Serializable {
    private static final Logger LOGGER = Logger
            .getLogger(RevisarDocumentacionGeneralController.class);
    private static final long serialVersionUID = -3526371393838393L;

    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;

    @EJB
    private DocumentosFacade documentosFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
    @Getter
    @Setter
    private String documentOffice = "";

    @Getter
    @Setter
    private Documento documento;
    @Getter
    @Setter
    private boolean token;
    
    @EJB
    private UsuarioFacade usuarioFacade;
    
    @Getter
    @Setter
    private boolean subido;
    @Getter
    @Setter
    private String area = "";

    private int docAprobObservacionesVersion;

    @PostConstruct
    public void init() {
        try {
            token = loginBean.getUsuario().getToken() != null && loginBean.getUsuario().getToken();

            List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
                    VehiculoDesechoSanitarioProcesoPma.class.getSimpleName(), TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA);
//            documentOffice = documentosFacade.direccionDescarga(VehiculoDesechoSanitarioProcesoPma.class.getSimpleName()
//                    , proyectosBean.getProyecto().getId(), TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA);
            
            if (documentos.size() > 0) {
                documento = documentos.get(0);
                String id = documento.getIdAlfresco();
                this.docAprobObservacionesVersion = Integer.parseInt(id.substring(id.length()-1));
            } else {
            	OficioAprobacionEiaBean oficioAprobacionEiaBean = (OficioAprobacionEiaBean) JsfUtil.getBean(OficioAprobacionEiaBean.class);
                oficioAprobacionEiaBean.cargarDatosGeneral();
                oficioAprobacionEiaBean.visualizarOficio("", token);
                oficioAprobacionEiaBean.subirDocuemntoAlfresco();
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);

            }
        } catch (CmisAlfrescoException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
        } catch (ServiceException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);

        }


    }

    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documento != null) {
            if (documento.getContenidoDocumento() == null) {
                documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
            }
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    documento.getContenidoDocumento()), "application/pdf");
            content.setName(documento.getNombre());

        }
        return content;

    }

    public void uploadListenerDocumentos(FileUploadEvent event) {
        byte[] contenidoDocumento = event.getFile().getContents();

        documento.setContenidoDocumento(contenidoDocumento);
        documento.setNombre(event.getFile().getFileName());
        subido = true;
    }

    public String firmarDocumento() {
        try {

            DigitalSign firmaE = new DigitalSign();
            documentOffice = documentosFacade.direccionDescarga(VehiculoDesechoSanitarioProcesoPma.class.getSimpleName()
                  , proyectosBean.getProyecto().getId(), TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA);
            return firmaE.sign(documentOffice, loginBean.getUsuario().getNombre()); // loginBean.getUsuario()
        } catch (Throwable e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
            return "";
        }
    }

    public boolean verificaToken (){
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	
	public void guardarToken(){
		Usuario usuario= JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken ();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public String completarTarea() {
        try {
            if (documento != null) {
                if (subido) {
//                    documentosFacade.replaceDocumentContent(documento);
                    licenciaAmbientalFacade.ingresarPronunciamiento(documento,
                            proyectosBean.getProyecto().getId(), proyectosBean.getProyecto().getCodigo(),
                            bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA);
                }
                String idAlfresco = documento.getIdAlfresco();

                idAlfresco = documento.getIdAlfresco().substring(0,idAlfresco.length()-1);
                idAlfresco= idAlfresco+""+(++this.docAprobObservacionesVersion);

                documento.setIdAlfresco(idAlfresco);
                if (!token || documentosFacade.verificarFirmaVersion(idAlfresco)) {
                    procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
                    this.documentosFacade.actualizarDocumento(documento);
                    JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                    procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
                    return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
                } else {
                    JsfUtil.addMessageError("El documento no está firmado.");
                    return "";
                }
            } else {
                JsfUtil.addMessageError("Error al realizar la operación.");
                return "";
            }
        } catch (JbpmException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        } catch (CmisAlfrescoException e) {

            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        } catch (Exception e) {

            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
        return "";
    }

    public void validarTareaBpm() {

        String url = "/prevencion/licenciamiento-ambiental/documentos/firmarOficioAprobacionEia.jsf";

//        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }

}
