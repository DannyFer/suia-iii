package ec.gob.ambiente.suia.eia.analisisAlternativas.controller;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.ActividadImplantacion;
import ec.gob.ambiente.suia.domain.Alternativa;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.analisisAlternativas.bean.AnalisisAlternativasBean;
import ec.gob.ambiente.suia.eia.analisisAlternativas.facade.AnalisisAlternativasFacade;
import ec.gob.ambiente.suia.eia.resumenEjecutivo.bean.ResumenEjecutivoBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ManagedBean
@ViewScoped
public class AnalisisAlternativasController implements Serializable {

    private static final long serialVersionUID = -6629736496175936111L;

    private static final Logger LOG = Logger.getLogger(AnalisisAlternativasController.class);

    @Setter
    @ManagedProperty(value = "#{analisisAlternativasBean}")
    private AnalisisAlternativasBean analisisAlternativasBean;

    @EJB
    private AnalisisAlternativasFacade analisisAlternativasFacade;

    //PROYECTO HIDROCARBURO
    @EJB
    private DocumentosFacade documentosFacade;



    @EJB
    private ValidacionSeccionesFacade validacionSeccionesFacade;

    public boolean salvarDocumento() {
        try {

            if (analisisAlternativasBean.getDocumentoGeneral() != null && analisisAlternativasBean.getDocumentoGeneral().getContenidoDocumento() != null) {

                analisisAlternativasBean.getDocumentoGeneral().setIdTable(this.analisisAlternativasBean.getEstudio().getId());

                analisisAlternativasBean.getDocumentoGeneral().setDescripcion(TipoDocumentoSistema.ANALISIS_ALTERNATIVAS.toString());
                analisisAlternativasBean.getDocumentoGeneral().setEstado(true);
                Documento documentoA = documentosFacade.guardarDocumentoAlfresco(this.analisisAlternativasBean.getEstudio()
                                .getProyectoLicenciamientoAmbiental().getCodigo(), Constantes.CARPETA_EIA, 0L, analisisAlternativasBean.getDocumentoGeneral(),
                        TipoDocumentoSistema.ANALISIS_ALTERNATIVAS, null);
                
                
                if(analisisAlternativasBean.isExisteObservaciones() && analisisAlternativasBean.getDocumentoGeneralHistorico() != null){
                	analisisAlternativasBean.getDocumentoGeneralHistorico().setIdHistorico(documentoA.getId());
					analisisAlternativasBean.getDocumentoGeneralHistorico().setNumeroNotificacion(analisisAlternativasBean.getNumeroNotificaciones());
					documentosFacade.actualizarDocumento(analisisAlternativasBean.getDocumentoGeneralHistorico());
				}
                
                
            } else {
                JsfUtil.addMessageError(JsfUtil.MESSAGE_NO_EXISTE_DOCUMENTO);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;

    }

    public void guardar() {
        try {
            if (JsfUtil.getBean(ResumenEjecutivoBean.class).isProyectoHidrocarburos()) {
                if(salvarDocumento()){
                    analisisAlternativasFacade.guardar(analisisAlternativasBean.getEstudio(), analisisAlternativasBean.getActividadesImplantacion(),
                            analisisAlternativasBean.getAlternativas(), analisisAlternativasBean.getSaved(), analisisAlternativasBean.getDocumentoGeneral());
                }

            } else {
            	if(!analisisAlternativasBean.getEsMineriaNoMetalicos())
            	{
            		if(analisisAlternativasBean.isExisteObservaciones()){
            			analisisAlternativasFacade.guardarHistorico(analisisAlternativasBean.getEstudio(),
                                analisisAlternativasBean.getActividadesImplantacion(), analisisAlternativasBean.getAlternativas(),
                                analisisAlternativasBean.getSaved(),null, analisisAlternativasBean.getNumeroNotificaciones());
            			
            		}else{        	
            			analisisAlternativasFacade.guardar(analisisAlternativasBean.getEstudio(),
                            analisisAlternativasBean.getActividadesImplantacion(), analisisAlternativasBean.getAlternativas(),
                            analisisAlternativasBean.getSaved(),null);
            		}
            	}else
            	{
            		salvarDocumento();
            	}
            }

            validacionSeccionesFacade.guardarValidacionSeccion("EIA", "analisisAlternativas", analisisAlternativasBean
                    .getEstudio().getId().toString());

            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/analisisAlternativas/analisisAlternativas.jsf");
            
        } catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            LOG.error("Error guardando el estudio de impacto ambiental", e);
        }
    }

    public void cancelar() {
        JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/analisisAlternativas/analisisAlternativas.jsf");
    }

    public void adicionarActividadImplantacion() {
        if (analisisAlternativasBean.getIndiceModificacionActividad() == null)//es nuevo
        {
            analisisAlternativasBean.getActividadesImplantacion().add(analisisAlternativasBean.getActividadImplantacion());
            analisisAlternativasBean.setActividadImplantacion(null);

        }

        JsfUtil.addCallbackParam("addActividad");

    }


    public void validateActividades(FacesContext context, UIComponent validate, Object value) {
        String nombreActividad = (String) value;
        ActividadImplantacion actividad = new ActividadImplantacion();
        actividad.setNombre(nombreActividad);
        if (analisisAlternativasBean.getIndiceModificacionActividad() == null) { //es nuevo
            if (analisisAlternativasBean.getActividadesImplantacion().contains(actividad)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Actividad ya registrada", null));
            }
        } else { // es edición
            analisisAlternativasBean.getActividadesImplantacion().remove(analisisAlternativasBean.getIndiceModificacionActividad());
            if (analisisAlternativasBean.getActividadesImplantacion().contains(actividad)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Actividad ya registrada", null));
            } else {
                analisisAlternativasBean.getActividadImplantacion().setNombre(nombreActividad);
            }

        }
    }

    public void seleccionarActividadImplantacion(ActividadImplantacion actividad) {
        if (actividad != null) {
            Integer indiceModificacionActividad = analisisAlternativasBean.getActividadesImplantacion().indexOf(actividad);
            analisisAlternativasBean.setIndiceModificacionActividad(indiceModificacionActividad);
            analisisAlternativasBean.setActividadImplantacion(analisisAlternativasBean.getActividadesImplantacion().get(indiceModificacionActividad));
        } else {
            analisisAlternativasBean.setIndiceModificacionActividad(null);
            analisisAlternativasBean.setActividadImplantacion(actividad);

        }

    }

    public void resetAdicionarActividad() {
        seleccionarActividadImplantacion(null);

    }

    public void eliminarActividadImplantacion(ActividadImplantacion actividad) {
        analisisAlternativasBean.getActividadesImplantacion().remove(actividad);
        actualizarListaAlternativas(actividad);
    }

    private void actualizarListaAlternativas(ActividadImplantacion actividad) {
        Set<Integer> indicesAlternativasParaEliminar = new HashSet<Integer>();
        for (Alternativa alternativa : analisisAlternativasBean.getAlternativas()) {
            if (alternativa.getActividadImplantacion().equals(actividad)) {
                indicesAlternativasParaEliminar.add(analisisAlternativasBean.getAlternativas().indexOf(alternativa));
            }
        }
        Set<Alternativa> alternativasParaEliminar = new HashSet<Alternativa>();
        for (Integer indice : indicesAlternativasParaEliminar) {
            Alternativa alternativaAEliminar = analisisAlternativasBean.getAlternativas().get(indice);
            alternativasParaEliminar.add(alternativaAEliminar);
        }
        for (Alternativa alternativa : alternativasParaEliminar) {
            analisisAlternativasBean.getAlternativas().remove(alternativa);
        }
    }


    public void adicionarAlternativa() {
        if (!analisisAlternativasBean.getAlternativas().contains(analisisAlternativasBean.getAlternativa())) {
            if (analisisAlternativasBean.getAlternativasPorActividad(
                    analisisAlternativasBean.getAlternativa().getActividadImplantacion()).size() >= 3) {
                JsfUtil.addMessageError("La actividad seleccionada cuenta con el máximo número de alternativas (3).");
                return;
            }
            analisisAlternativasBean.getAlternativas().add(analisisAlternativasBean.getAlternativa());
        }
        analisisAlternativasBean.setAlternativa(null);
        JsfUtil.addCallbackParam("addAlternativa");
    }

    public void seleccionarAlternativa(Alternativa alternativa) {
        analisisAlternativasBean.setAlternativa(alternativa);
    }

    public void resetAdicionarAlternativa() {
        seleccionarAlternativa(null);
    }

    public void eliminarAlternativa(Alternativa alternativa) {
        analisisAlternativasBean.getAlternativas().remove(alternativa);


    }


// PROYECTO HIDROCARBUROS

    private Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
        byte[] documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null) {
            documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
        }
        if (documentoContenido != null) {
            documento.setContenidoDocumento(documentoContenido);
        }
        return documento;
    }


    public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz,
                                    String extension) {
        Documento documento = new Documento();
        documento.setContenidoDocumento(contenidoDocumento);
        documento.setNombreTabla(clazz.getSimpleName());
        documento.setIdTable(0);
        documento.setExtesion("." + extension);
        documento.setMime("application/pdf");
        return documento;
    }


    private Documento uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
        byte[] contenidoDocumento = event.getFile().getContents();
        Documento documento = crearDocumento(contenidoDocumento, clazz, extension);
        documento.setNombre(event.getFile().getFileName());
        return documento;
    }

    public void uploadListenerDocumentos(FileUploadEvent event) {
        analisisAlternativasBean.setDocumentoGeneral(uploadListener(event, EstudioImpactoAmbiental.class, "pdf"));
    }

    public StreamedContent getStreamContent() throws Exception {
        DefaultStreamedContent content = null;
        analisisAlternativasBean.setDocumentoGeneral(descargarAlfresco(analisisAlternativasBean.getDocumentoGeneral()));
        try {
            if (analisisAlternativasBean.getDocumentoGeneral() != null
                    && analisisAlternativasBean.getDocumentoGeneral().getNombre() != null
                    && analisisAlternativasBean.getDocumentoGeneral().getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(
                        new ByteArrayInputStream(
                                analisisAlternativasBean.getDocumentoGeneral().getContenidoDocumento()));
                content.setName(analisisAlternativasBean.getDocumentoGeneral().getNombre());

            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }

    /**
	 * MarielaG
	 * Descarga documento original Proyecto Minero
	 */
	public StreamedContent getStreamContentOriginal(Documento documento) throws Exception {
        DefaultStreamedContent content = null;
        try {
        	analisisAlternativasBean.setDocumentoGeneralOriginal(descargarAlfresco(documento));
        	if (analisisAlternativasBean.getDocumentoGeneralOriginal() != null
                    && analisisAlternativasBean.getDocumentoGeneralOriginal().getNombre() != null
                    && analisisAlternativasBean.getDocumentoGeneralOriginal().getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(
                        new ByteArrayInputStream(
                                analisisAlternativasBean.getDocumentoGeneralOriginal().getContenidoDocumento()));
                content.setName(analisisAlternativasBean.getDocumentoGeneralOriginal().getNombre());

            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

        } catch (Exception exception) {
            LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
}
