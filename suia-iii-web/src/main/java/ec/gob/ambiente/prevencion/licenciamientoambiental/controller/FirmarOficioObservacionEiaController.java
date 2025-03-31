package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import ec.gob.ambiente.prevencion.categoria1.controllers.FirmarCertificadoAmbientalController;
import ec.gob.ambiente.prevencion.licenciamientoambiental.informes.bean.OficioObservacionEiaBean;
import ec.gob.ambiente.prevencion.tdr.controller.RevisarDocumentacionGeneralController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoActualizacionCertificadoInterseccionFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class FirmarOficioObservacionEiaController implements Serializable {
    private static final Logger LOGGER = Logger
            .getLogger(RevisarDocumentacionGeneralController.class);
    private static final long serialVersionUID = 6278899190628756179L;

    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;
    @EJB
    private DocumentosFacade documentosFacade;
    
    @EJB
	protected ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
    
    @EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
    
    @EJB
	private ProyectoActualizacionCertificadoInterseccionFacade proyectoActualizacionCIFacade;
    
    @EJB
	private OrganizacionFacade organizacionFacade;

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
    private boolean mostrarInforme;

    @Getter
    @Setter
    private boolean subido;
    @Getter
    @Setter
    private boolean token;
    
    @EJB
    private UsuarioFacade usuarioFacade;

    private  int docOficioObserVersion;
    
    @Getter
	@Setter
	private boolean ambienteProduccion;
    
    @Getter
    @Setter
    public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");

    @PostConstruct
    public void init() {
        try {
            token = loginBean.getUsuario().getToken() != null && loginBean.getUsuario().getToken();
//            OficioObservacionEiaBean oficioObservacionEiaBean = (OficioObservacionEiaBean) JsfUtil.getBean(OficioObservacionEiaBean.class);
//            oficioObservacionEiaBean.visualizarOficio(token);
//            oficioObservacionEiaBean.subirDocuemntoAlfresco();
            
            List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
                    LicenciaAmbientalFacade.class.getSimpleName(), TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_EIA);
            
            ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			token = true;
			if(!ambienteProduccion){
				verificaToken();
			}
            
            if (documentos.size() > 0) {
            	            	
                documento = documentos.get(0);
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				String fechaDocumento = sdf.format(documento.getFechaCreacion());

				String fechaActual = sdf.format(new Date());

				boolean nuevoDocumento = false;
				if (!fechaActual.equals(fechaDocumento)) {
					nuevoDocumento = true;
				}
				
				if(nuevoDocumento){
					JsfUtil.getBean(OficioObservacionEiaBean.class).visualizarOficio(false);
					JsfUtil.getBean(OficioObservacionEiaBean.class).subirDocuemntoAlfresco();		
					
		            List<Documento> documentosNuevo = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
		                    LicenciaAmbientalFacade.class.getSimpleName(), TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_EIA);
		            if(documentosNuevo.size() >0){
		            	documento = documentosNuevo.get(0);
		            }
				}
                
				documentOffice = documentosFacade.direccionDescarga(LicenciaAmbientalFacade.class.getSimpleName()
	                    , proyectosBean.getProyecto().getId(), TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_EIA);
				
                String id = documento.getIdAlfresco();
                this.docOficioObserVersion = Integer.parseInt(id.substring(id.length()-1));
            } else {            	            	
                JsfUtil.addMessageError("Error al realizar la operación.");
            }
        } catch (CmisAlfrescoException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al cargar los datos. Por favor intente más tarde.");
        } catch (ServiceException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al cargar los datos. Por favor intente más tarde.");
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al cargar los datos. Por favor intente más tarde.");
        } 
    }

    public String firmarDocumento() {
        try {

            DigitalSign firmaE = new DigitalSign();
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
                            bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_EIA);

                }

                String idAlfresco = this.documento.getIdAlfresco();

                idAlfresco = this.documento.getIdAlfresco().substring(0,idAlfresco.length()-1);
                idAlfresco= idAlfresco+""+(++this.docOficioObserVersion);

                this.documento.setIdAlfresco(idAlfresco);

                if (!token || documentosFacade.verificarFirmaVersion(idAlfresco)) {
                    procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);

                    this.documentosFacade.actualizarDocumento(this.documento);
                    
                    //MarielaGuano para actualizacion de certificado de interseccion
                    Integer requiereActualizacion = proyectosBean.getProyecto().getEstadoActualizacionCertInterseccion();
                    if(requiereActualizacion != null && requiereActualizacion.equals(1)) {//1 solicitada la actualizacion por el tecnico
	                    proyectosBean.getProyecto().setEstadoActualizacionCertInterseccion(2); //2 Habilitado para modificacion del operador
	                    proyectoActualizacionCIFacade.guardarEstadoActualizacionCertificado(proyectosBean.getProyecto(), null);
	                    
	                    Object[] parametrosCorreo = new Object[] {proyectosBean.getProyecto().getCodigo()};
		    			
		    			String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
		    							"bodyNotificacionActualizacionCertificadoInterseccion",
		    							parametrosCorreo);
		    			
		    			Organizacion orga = organizacionFacade.buscarPorRuc(proyectosBean.getProyecto().getUsuarioCreacion());
		    			if(orga != null)
		    				Email.sendEmail(orga, "Regularización Ambiental Nacional", notificacion, proyectosBean.getProyecto().getCodigo(), proyectosBean.getProyecto().getUsuario(), loginBean.getUsuario());
		    			else
		    				Email.sendEmail(proyectosBean.getProyecto().getUsuario(), "Regularización Ambiental Nacional", notificacion, proyectosBean.getProyecto().getCodigo(), loginBean.getUsuario());
                    }
	    			//fin actualizacion de certificado de interseccion                    

                    JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
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

        String url = "/prevencion/licenciamiento-ambiental/documentos/firmarOficioObservacionEia.jsf";
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
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


}
