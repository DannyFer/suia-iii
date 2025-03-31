package ec.gob.ambiente.prevencion.requisitosprevios.bean;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.util.Map;

@ManagedBean
@ViewScoped
public class AdjuntarCuestionarioBean implements Serializable {

    private static final long serialVersionUID = 2975389742323455827L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(AdjuntarCuestionarioBean.class);

    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

    @EJB
    private ProcesoFacade procesoFacade;

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
    private boolean requiereBP = false;

    @Getter
    @Setter
    private boolean requiereSNAP = false;

    private byte[] documentoBP;

    private String nombreDocumentoBP;

    private byte[] documentoSNAP;

    private String nombreDocumentoSNAP;

    @Getter
    @Setter
    private String header;

    @Getter
    @Setter
    private String nota;

    @Getter
    @Setter
    private String labelDocumentoBP;

    @Getter
    @Setter
    private String labelDocumentoSNAP;

    @Getter
    @Setter
    private String labelAdjuntoBP;

    @Getter
    @Setter
    private String labelAdjuntoSNAP;

    @Getter
    @Setter
    private boolean descargadoBP = false;

    @Getter
    @Setter
    private boolean descargadoSNAP = false;

    @Setter
    @Getter
    private File cuestionarioBP;

    @Setter
    @Getter
    private File cuestionarioSNAP;

    @Setter
    @Getter
    private ProyectoLicenciamientoAmbiental proyecto;

    @PostConstruct
    public void init() {
        try {
            Map<String, Object> variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());

            Integer idProyecto = Integer.parseInt((String) variables
                    .get(Constantes.ID_PROYECTO));
            proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorId(idProyecto);

            //Si es proyecto minero
            if(proyecto.getTipoSector().getId() == TipoSector.TIPO_SECTOR_MINERIA){
                nombreDocumentoBP = "TDRs_Mineria_BP.pdf";
                header = "TDRs para informe de factibilidad de actividades mineras";
                nota = "Nota: Una vez descargue y complete los TDRs, adjúntelo para continuar con el trámite.";
                labelDocumentoBP = "TDRs para informe *";
                labelAdjuntoBP = "Informe de factibilidad(.pdf) *";
                requiereBP = true;
                documentoBP = documentosFacade.descargarDocumentoPorNombre(nombreDocumentoBP);
            }
            else {
                header = "Cuestionario de Intersección del Proyecto";
                nota = "Nota: Una vez que descargue y responda las preguntas, adjunte el cuestionario con las respuestas correspondientes.";
                Boolean intersecaBP = Boolean.parseBoolean((String) variables.get("intersecaBP"));
                Boolean intersecaSNAP = Boolean.parseBoolean((String) variables.get("intersecaSNAP"));
                Boolean intersecaRAMSARPUNTO = Boolean.parseBoolean((String) variables.get("intersecaRAMSARPUNTO"));
                Boolean intersecaRAMSARAREA = Boolean.parseBoolean((String) variables.get("intersecaRAMSARAREA"));
                String CategoriaProyecto = proyecto.getCatalogoCategoria().getCategoria().getCodigo();
                if (intersecaBP) {
                    requiereBP = true;
                    nombreDocumentoBP = "pregunta_pronunciamiento_intersecaBP";
                    if(CategoriaProyecto.equals("III") || CategoriaProyecto.equals("IV")){
                        nombreDocumentoBP += "_cat_III_IV.pdf";
                    }
                    else if(CategoriaProyecto.equals("II") || CategoriaProyecto.equals("I")) {
                        nombreDocumentoBP += "_cat_"+CategoriaProyecto+".pdf";
                    }
                    documentoBP = documentosFacade.descargarDocumentoPorNombre(nombreDocumentoBP);
                    labelDocumentoBP = "Cuestionario de Intersección para Bosques Protectores *";
                    labelAdjuntoBP = "Cuestionario con respuestas para Bosques Protectores(.pdf) *";
                }
                if (intersecaSNAP || intersecaRAMSARPUNTO || intersecaRAMSARAREA) {
                	requiereSNAP = true;
					if (intersecaRAMSARAREA || intersecaRAMSARPUNTO) {
	                    nombreDocumentoSNAP = "pregunta_pronunciamiento";
						nombreDocumentoSNAP += "_RAMSAR.pdf";
					} else {
	                    nombreDocumentoSNAP = "pregunta_pronunciamiento_intersecaSNAP";
						if (CategoriaProyecto.equals("III")
								|| CategoriaProyecto.equals("IV")) {
							nombreDocumentoSNAP += "_cat_III_IV.pdf";
						} else if (CategoriaProyecto.equals("II") || CategoriaProyecto.equals("I")) {
							nombreDocumentoSNAP += "_cat_"+CategoriaProyecto+".pdf";
						}
					}
                    documentoSNAP = documentosFacade.descargarDocumentoPorNombre(nombreDocumentoSNAP);
                    labelDocumentoSNAP = "Cuestionario de Intersección para Sistema Nacional de Áreas Protegidas y/o RAMSAR *";
                    labelAdjuntoSNAP = "Cuestionario con respuestas para Sistema Nacional de Áreas Protegidas y/o RAMSAR (.pdf) *";
                }
            }
        } catch (JbpmException e) {
            JsfUtil.addMessageError("Error al obtener la intersección del proyecto. Intente más tarde.");
            LOG.error("Error al obtener la intersección del proyecto", e);
        } catch (CmisAlfrescoException e) {
            JsfUtil.addMessageError("Error al descargar el documento. Intente más tarde.");
            LOG.error("Error al descargar el documento. Intente más tarde.", e);
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
            LOG.error("Error al realizar la operación.", e);
        }
    }

    public StreamedContent getStreamBP() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documentoBP != null) {
            descargadoBP = true;
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    documentoBP), "application/pdf");
            content.setName(nombreDocumentoBP);
        }
        return content;
    }

    public StreamedContent getStreamSNAP() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documentoSNAP != null) {
            descargadoSNAP = true;
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    documentoSNAP), "application/pdf");
            content.setName(nombreDocumentoSNAP);
        }
        return content;
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/requisitosprevios/adjuntarCuestionario.jsf");
    }

    public void uploadListenerBP(FileUploadEvent event) {
        setCuestionarioBP(JsfUtil.upload(event));
    }

    public void uploadListenerSNAP(FileUploadEvent event) {
        setCuestionarioSNAP(JsfUtil.upload(event));
    }
}

