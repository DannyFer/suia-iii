package ec.gob.ambiente.suia.eia.areaInfluencia.controller;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.eia.areaInfluencia.AreaInfluenciaFacade;
import ec.gob.ambiente.suia.eia.areaInfluencia.bean.AreaInfluenciaBean;
import ec.gob.ambiente.suia.eia.resumenEjecutivo.bean.ResumenEjecutivoBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by juangabriel on 15/07/15.
 */
@ManagedBean
@ViewScoped
public class AreaInfluenciaController implements Serializable {



    /**
	 * 
	 */
	private static final long serialVersionUID = 2371091150641209960L;

    private static final Logger LOG = Logger.getLogger(AreaInfluenciaController.class);

    @Setter
    @ManagedProperty(value = "#{areaInfluenciaBean}")
    private AreaInfluenciaBean areaInfluenciaBean;

    @EJB
    private AreaInfluenciaFacade analisisAlternativasFacade;

    @EJB
    private DocumentosFacade documentosFacade;

    public void guardar() throws Exception {
        try {
            if(areaInfluenciaBean.getDocumentoGeneral().getContenidoDocumento()==null)
            {
                this.getStreamContent();
            }
            //if(JsfUtil.getBean(ResumenEjecutivoBean.class).isProyectoHidrocarburos()) {
            if(areaInfluenciaBean.isProyectoHidrocarburos()){
                areaInfluenciaBean.setListaAreaInfluenciaBorrar(
                        analisisAlternativasFacade.borrarListaAreaInfluencia(areaInfluenciaBean.getListaAreaInfluenciaBorrar()));
                List<AreaInfluencia> list = new ArrayList<>();
                list.addAll(areaInfluenciaBean.getListaAreaInfluenciaDirecta());
                list.addAll(areaInfluenciaBean.getListaAreaInfluenciaIndirecta());
                analisisAlternativasFacade.guardar(list,areaInfluenciaBean.getEstudio(), null, areaInfluenciaBean.getInfraestructurasAfectadasAlmacenadas(),
                        areaInfluenciaBean.getInfraestructurasAfectadas(),areaInfluenciaBean.getDocumentoGeneral(),
                        areaInfluenciaBean.getDistanciaElementoSensiblesEliminados());
            }
            else{
            	if(areaInfluenciaBean.isExisteObservaciones()){
            		analisisAlternativasFacade.guardarHistorico(null,areaInfluenciaBean.getEstudio(), areaInfluenciaBean.getAreaInfluencia(),
            				areaInfluenciaBean.getInfraestructurasAfectadas(),
                            areaInfluenciaBean.getDocumentoGeneral(), areaInfluenciaBean.getDistanciaElementoSensiblesEliminados(),
                            areaInfluenciaBean.getNumeroNotificaciones(), areaInfluenciaBean.getDocumentoHistorico());
            		areaInfluenciaBean.inicializarDocumento();
            		
            	}else{  
                    analisisAlternativasFacade.guardar(null,areaInfluenciaBean.getEstudio(), areaInfluenciaBean.getAreaInfluencia(),
                            areaInfluenciaBean.getInfraestructurasAfectadasAlmacenadas(), areaInfluenciaBean.getInfraestructurasAfectadas(),
                            areaInfluenciaBean.getDocumentoGeneral(), areaInfluenciaBean.getDistanciaElementoSensiblesEliminados()); 
                    
                    /*
                     * Cris F: Aumento de código para obtener los detalles
                     */
                    areaInfluenciaBean.setInfraestructurasAfectadas(new ArrayList<InfraestructuraAfectada>());
                    List<InfraestructuraAfectada> infraestructuraAfectadaEnBdd = analisisAlternativasFacade.getInfraestructuraEnBdd(areaInfluenciaBean.getEstudio());
            		List<InfraestructuraAfectada> infraestructuraDistanciaEnBdd = new ArrayList<>();
            		if (infraestructuraAfectadaEnBdd != null && !infraestructuraAfectadaEnBdd.isEmpty()) {
            			for (InfraestructuraAfectada infraestructura : infraestructuraAfectadaEnBdd) {
            				InfraestructuraAfectada infraestructuraCopia = infraestructura.clone();
            				infraestructuraCopia.setId(infraestructura.getId());
            				infraestructuraDistanciaEnBdd.add(infraestructuraCopia);
            				
            				if (infraestructura.getIdHistorico() == null) {
            					List<DistanciaElementoSensible> elementos = new ArrayList<DistanciaElementoSensible>();
            		        	for(DistanciaElementoSensible distancia : infraestructura.getDistanciaElementoSensibles()){
            		        		if(distancia.getIdHistorico() == null){
            		        			elementos.add(distancia);
            		        		}        		
            		        	}
            		        	infraestructura.setDistanciaElementoSensibles(elementos);
            		        	areaInfluenciaBean.getInfraestructurasAfectadas().add(infraestructura);            					
            				}
            			}
            		}                    
                    //Fin aumento                    
            	}
            }

            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            
            JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/areaInfluencia/areaInfluencia.jsf");

        } catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            LOG.error("Error guardando el estudio de impacto ambiental", e);
        }
    }

    public void cancelar() {
        JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/areaInfluencia/areaInfluencia.jsf");
    }


    public void resetInfraestructura() {
        seleccionarInfraestructura(null);
    }

    public void seleccionarInfraestructura(InfraestructuraAfectada infraestructuraAfectada) {
        areaInfluenciaBean.setInfraestructuraAfectada(infraestructuraAfectada);
    }



    public void adicionarInfraestructura() {
        if (!areaInfluenciaBean.getInfraestructurasAfectadas().contains(areaInfluenciaBean.getInfraestructuraAfectada()))
            areaInfluenciaBean.getInfraestructurasAfectadas().add(areaInfluenciaBean.getInfraestructuraAfectada());
        areaInfluenciaBean.setInfraestructuraAfectada(new InfraestructuraAfectada());
        JsfUtil.addCallbackParam("addInfraestructura");
    }



    public void eliminarInfraestructura(InfraestructuraAfectada infraestructuraAfectada) {
        areaInfluenciaBean.getInfraestructurasAfectadas().remove(infraestructuraAfectada);
    }



    public void resetDistancia() {
        seleccionarDistancia(null);
    }


    public void seleccionarDistancia(DistanciaElementoSensible distanciaElementoSensible) {
        areaInfluenciaBean.setDistanciaElementoSensible(distanciaElementoSensible);

    }

    public void adicionarDistancia() {
        if (areaInfluenciaBean.getInfraestructuraAfectada().getDistanciaElementoSensibles() == null) {
            areaInfluenciaBean.getInfraestructuraAfectada().setDistanciaElementoSensibles(new ArrayList<DistanciaElementoSensible>());
        }
        if (!areaInfluenciaBean.getInfraestructuraAfectada().getDistanciaElementoSensibles().contains(areaInfluenciaBean.getDistanciaElementoSensible())) {
            areaInfluenciaBean.getInfraestructuraAfectada().getDistanciaElementoSensibles().add(areaInfluenciaBean.getDistanciaElementoSensible());
        }
        areaInfluenciaBean.setDistanciaElementoSensible(new DistanciaElementoSensible());
        JsfUtil.addCallbackParam("addElemento");
    }

    public void eliminarDistancia(DistanciaElementoSensible distanciaElementoSensible) {
        areaInfluenciaBean.getInfraestructuraAfectada().getDistanciaElementoSensibles().remove(distanciaElementoSensible);
        if(distanciaElementoSensible.getId() != null)
        	areaInfluenciaBean.getDistanciaElementoSensiblesEliminados().add(distanciaElementoSensible);
    }


    public StreamedContent getStreamContent() throws Exception {
        DefaultStreamedContent content = null;
        areaInfluenciaBean.setDocumentoGeneral(descargarAlfresco(areaInfluenciaBean.getDocumentoGeneral()));
        try {
            if (areaInfluenciaBean.getDocumentoGeneral() != null
                    && areaInfluenciaBean.getDocumentoGeneral().getNombre() != null
                    && areaInfluenciaBean.getDocumentoGeneral().getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(
                        new ByteArrayInputStream(
                                areaInfluenciaBean.getDocumentoGeneral().getContenidoDocumento()));
                content.setName(areaInfluenciaBean.getDocumentoGeneral().getNombre());

            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }


    /**
     * Descarga documento desde el Alfresco
     *
     * @param documento
     * @return
     * @throws CmisAlfrescoException
     */
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

    public void uploadListenerDocumentos(FileUploadEvent event) {
        areaInfluenciaBean.setDocumentoGeneral(uploadListener(event, EstudioImpactoAmbiental.class, "zip"));
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
    public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz,
                                    String extension) {
        Documento documento = new Documento();
        documento.setContenidoDocumento(contenidoDocumento);
        documento.setNombreTabla(clazz.getSimpleName());
        documento.setIdTable(0);
        documento.setExtesion("." + extension);
        documento.setMime(extension == "zip" ? "application/zip": "application/vnd.ms-excel");
        return documento;
    }




    public void validateAdjunto(FacesContext context, UIComponent validate, Object value) {
        StringBuilder  functionJs = new StringBuilder();
        List<FacesMessage> mensajes = new ArrayList<>();
        if(areaInfluenciaBean.getDocumentoGeneral().getNombre()==null){//NO VALIDO
            FacesMessage mensajeValidacionDocumento = new FacesMessage(FacesMessage.SEVERITY_ERROR,"El campo 'Documento del estudio' es requerido.",null);
            mensajes.add(mensajeValidacionDocumento);
            functionJs.append("highlightComponent('form:panelGridAdjuntos');");
        }
        else{
            functionJs.append("removeHighLightComponent('form:panelGridAdjuntos');");
        }
        RequestContext.getCurrentInstance().execute(functionJs.toString());
        if (!mensajes.isEmpty())
            throw new ValidatorException(mensajes);
    }


    /**
     * Controla el render del input que permite el ingreso de otro Elemento
     */
    public void controlarOtroElemento() {
        if (areaInfluenciaBean.getDistanciaElementoSensible().getElementoSensible().getNombre().equals("Otros")) {
            areaInfluenciaBean.setHabilitarOtroElemento(true);
        } else {
            areaInfluenciaBean.setHabilitarOtroElemento(false);
        }
    }

    public void seleccionarAreaInfluencia(AreaInfluencia areaInfluencia) {
        areaInfluenciaBean.setAreaInfluencia(areaInfluencia);
        areaInfluenciaBean.setNuevo(false);
    }

    public void eliminarAreaInfluencia(AreaInfluencia areaInfluencia, boolean directa) {
        if (directa)
            areaInfluenciaBean.getListaAreaInfluenciaDirecta().remove(areaInfluencia);
        else
            areaInfluenciaBean.getListaAreaInfluenciaIndirecta().remove(areaInfluencia);
        
        if(areaInfluencia.getId() != null)
        	areaInfluenciaBean.getListaAreaInfluenciaBorrar().add(areaInfluencia);
    }
}
