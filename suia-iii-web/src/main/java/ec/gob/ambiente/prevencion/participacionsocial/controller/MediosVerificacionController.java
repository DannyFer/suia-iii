package ec.gob.ambiente.prevencion.participacionsocial.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.participacionsocial.bean.MediosVerificacionBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.RegistroMediosParticipacionSocial;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.MediosVerificacionFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@RequestScoped
public class MediosVerificacionController implements Serializable {


    private static final long serialVersionUID = -5000821815587464004L;


    private static final Logger LOGGER = Logger.getLogger(MediosVerificacionController.class);
    private static final String COMPLETADA = "Completada";


    @EJB
    private MediosVerificacionFacade mediosVerificacionFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;
    
    @EJB
    private AreaFacade areaFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{mediosVerificacionBean}")
    private MediosVerificacionBean mediosVerificacionBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;


    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;


    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    private String documentoActivo = "";

    @Getter
    @Setter
    private Map<String, Documento> documentos;

    @PostConstruct
    private void init() {
        this.documentos = mediosVerificacionBean.getDocumentos();
    }


    /*GUARDAR DOCUMENTO inicio*/
    public void uploadListenerDocumentos(FileUploadEvent event) {
        documentoActivo = event.getComponent().getAttributes().get("clave").toString();
        Documento documento = this.uploadListener(event, RegistroMediosParticipacionSocial.class, "rar");
        documentos.put(documentoActivo, documento);
        documentoActivo = "";
    }


    private Documento uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
        byte[] contenidoDocumento = event.getFile().getContents();
        Documento documento = crearDocumento(contenidoDocumento, clazz, extension);
        documento.setNombre(event.getFile().getFileName());
        return documento;
    }

    /**
     * Crea el documento
     *
     * @param contenidoDocumento arreglo de bytes
     * @param clazz              Clase a la cual se va a ligar al documento
     * @param extension          extension del archivo
     * @return Objeto de tipo Documento
     */
    public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
        Documento documento = new Documento();
        documento.setContenidoDocumento(contenidoDocumento);
        documento.setNombreTabla(clazz.getSimpleName());
        documento.setIdTable(proyectosBean.getProyecto().getId());
        documento.setExtesion("." + extension);

        documento.setMime("application/octet-stream");
        return documento;
    }

    public StreamedContent getStreamContent(String documentoActivo) throws Exception {
        DefaultStreamedContent content = null;
        Documento documento = this.descargarAlfresco(documentos.get(documentoActivo));
        try {
            if (documento != null && documento.getNombre() != null
                    && documento.getContenidoDocumentoFile() != null) {
                content = new DefaultStreamedContent(new FileInputStream(documento.getContenidoDocumentoFile()));
                content.setName(documento.getNombre());
            } else {
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
            }
        } catch (Exception exception) {
            LOGGER.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }

    public Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
        File documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null)
			try {
				documentoContenido = documentosFacade.descargarFile(documento.getIdAlfresco());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        if (documentoContenido != null)
            documento.setContenidoDocumentoFile(documentoContenido);
        return documento;
    }
    /*GUARDAR DOCUMENTO fin de bloque*/

    public void guardarMedioVerificacion() {
        try {
            mediosVerificacionBean.getRegistroMediosParticipacionSocial().setParticipacionSocialAmbiental(mediosVerificacionBean.getParticipacionSocialAmbiental());
            mediosVerificacionBean.getRegistroMediosParticipacionSocial().setCatalogoMedio(mediosVerificacionBean.getCatalogoMedio());
            mediosVerificacionFacade.guardarMedioVerificacion(mediosVerificacionBean.getRegistroMediosParticipacionSocial());

            // JsfUtil.addMessageInfo("Medio de Verificación guardado correctamente");
            limpiarMedioVerificacion();

        } catch (Exception e) {
            JsfUtil.addMessageError("No se pudo agregar el Medio de Verificación");
        }

    }

    public void limpiarMedioVerificacion() {
        try {

            mediosVerificacionBean.setRegistroMediosParticipacionSocial(new RegistroMediosParticipacionSocial());
            mediosVerificacionBean.getRegistroMediosParticipacionSocial().setCatalogoMedio(mediosVerificacionBean.getCatalogoMedio());
            mediosVerificacionBean.loadDataTable();
            mediosVerificacionBean.setCatalogoMedio(null);

        } catch (Exception e) {
            JsfUtil.addMessageError("No se pudo crear el Medio de Verificación");
        }

    }

    public void editarMedioVerificacion(RegistroMediosParticipacionSocial recordEdit) {
        try {
            mediosVerificacionBean.setRegistroMediosParticipacionSocial(recordEdit);
            mediosVerificacionBean.setCatalogoMedio(recordEdit.getCatalogoMedio());
            mediosVerificacionBean.getRegistroMediosParticipacionSocial().setCatalogoMedio(mediosVerificacionBean.getCatalogoMedio());
        } catch (Exception e) {
            JsfUtil.addMessageError("No se pudo editar el Medio de Verificación");
        }

    }

    public void eliminarMedioVerificacion(RegistroMediosParticipacionSocial recordDelete) {
        try {
            mediosVerificacionFacade.eliminarMedioVerificacion(recordDelete);
            mediosVerificacionBean.loadDataTable();
            JsfUtil.addMessageInfo("Medio de Verificación eliminado correctamente");
        } catch (Exception e) {
            JsfUtil.addMessageError("No se pudo Eliminar el Medio de Verificación");
        }

    }

    public void guardar() {
        try {
            if (mediosVerificacionBean.getRegistrosMediosParticipacionSocial().size() > 0 && documentos.size() > 0) {


                //Guardar el archivo adjunto
                participacionSocialFacade.ingresarInformes(mediosVerificacionBean.getDocumentos(), bandejaTareasBean.getProcessId(),
                        bandejaTareasBean.getTarea().getTaskId(), proyectosBean.getProyecto().getCodigo());


                List<Documento> documentosGuardados = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
                        (RegistroMediosParticipacionSocial.class.getSimpleName()), TipoDocumentoSistema.PS_RESPALDO_MEDIOS_VERIFICACION);

                if (documentosGuardados.size() > 0) ;
                Documento doc = documentosGuardados.get(0);

                //Guardar el id del archivo
                for (RegistroMediosParticipacionSocial record : mediosVerificacionBean.getRegistrosMediosParticipacionSocial()) {
                    record.setDocumento(doc);
                    mediosVerificacionFacade.guardarMedioVerificacion(record);

                }
                JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
            }
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);

        }
    }

    public String finalizar() {
        try {
            if (mediosVerificacionBean.isRevisar()) {

                if (observacionesController.validarObservaciones(loginBean.getUsuario(), mediosVerificacionBean.getInformacionCompleta(), "mediosVerificacion")) {

                    Map<String, Object> params = new HashMap<>();
                    params.put("informacionCompleta", mediosVerificacionBean.getInformacionCompleta());

                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId(), params);
                } else {
                    return "";
                }
            }


            if (mediosVerificacionBean.getRegistrosMediosParticipacionSocial().size() > 0 && documentos.size() > 0) {
                //Guardar el archivo adjunto
                participacionSocialFacade.ingresarInformes(mediosVerificacionBean.getDocumentos(), bandejaTareasBean.getProcessId(),
                        bandejaTareasBean.getTarea().getTaskId(), proyectosBean.getProyecto().getCodigo());


                List<Documento> documentosGuardados = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
                        (RegistroMediosParticipacionSocial.class.getSimpleName()), TipoDocumentoSistema.PS_RESPALDO_MEDIOS_VERIFICACION);

                if (documentosGuardados.size() > 0) ;
                Documento doc = documentosGuardados.get(0);

                //Guardar el id del archivo
                for (RegistroMediosParticipacionSocial record : mediosVerificacionBean.getRegistrosMediosParticipacionSocial()) {
                    record.setDocumento(doc);
                    mediosVerificacionFacade.guardarMedioVerificacion(record);

                }
                
                //Cris F: cambio para que tome el usuario coodinador social mineria
                Map<String, Object> params = new HashMap<>();
                Area areaProyecto = mediosVerificacionBean.getProyectosBean().getProyecto().getAreaResponsable();
                areaProyecto = areaFacade.getAreaFull(areaProyecto.getId());
                
                if (areaProyecto == null || areaProyecto.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
                	
                	Usuario coordinador = new Usuario();
                	Usuario tecnico = new Usuario();
                	if(mediosVerificacionBean.getProyectosBean().getProyecto().getCatalogoCategoria().getCatalogoCategoriaPublico().getTipoSector().getId() ==2){
                		coordinador = areaFacade.getUsuarioPorRolArea("role.pc.coordinador.Social.mineria", areaProyecto);    
                		tecnico = areaFacade.getUsuarioPorRolArea("role.pc.tecnico.Social.mineria", areaProyecto);
                	}else{        	
                		coordinador = areaFacade.getUsuarioPorRolArea("role.pc.coordinador.Social", areaProyecto);   
                		tecnico = areaFacade.getUsuarioPorRolArea("role.pc.tecnico.Social", areaProyecto);
                	}
                	params.put("u_Coordinador", coordinador.getNombre());
                	params.put("u_Tecnico", tecnico.getNombre());
                	
                }else{
                	Usuario coordinadorArea = new Usuario();
                	Usuario tecnicoArea = new Usuario();
                    
//                    if(mediosVerificacionBean.getProyectosBean().getProyecto().getCatalogoCategoria().getCatalogoCategoriaPublico().getTipoSector().getId() ==2){
//                    	coordinadorArea = areaFacade.getUsuarioPorRolArea("role.pc.coordinador.Social.mineria", areaProyecto); 
//                    }else{
                    	coordinadorArea = areaFacade.getCoordinadorPorArea(areaProyecto);
                    	tecnicoArea = areaFacade.getTecnicoPorArea(areaProyecto);
                    	
//                    }            
                    
                    params.put("u_Coordinador", coordinadorArea.getNombre());
                    params.put("u_Tecnico", tecnicoArea.getNombre());
                }                

                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                        .getTarea().getProcessInstanceId(), params);
                //fin del cambio

                //Finalizar la tarea
                Map<String, Object> data = new HashMap<>();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
                JsfUtil.addMessageInfo("Tarea completada correctamente");
                procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
                return "/bandeja/bandejaTareas.jsf";
            } else {
                JsfUtil.addMessageInfo("No se han ingresado medios de verificación o el archivo de respaldo es nulo");
                return "";

            }

        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
            return "";
        }
    }

    public void validarTareaBpm() {

        String url = "/prevencion/participacionsocial/mediosVerificacion.jsf";
        if (!mediosVerificacionBean.getTipo().isEmpty()) {
            url += "?tipo=" + mediosVerificacionBean.getTipo();
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }
}