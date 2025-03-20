package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@ManagedBean
@ViewScoped
public class DescargarDocumentoLABean implements Serializable {

    private static final long serialVersionUID = 2975076042323455827L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(DescargarDocumentoLABean.class);


    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

    @EJB
    private ProcesoFacade procesoFacade;

//    @EJB
//    private CertificadoInterseccionFacade certificadoInterseccionFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;


    private byte[] documento;

    private String nombreDocumento;

    @Getter
    @Setter
    private boolean descargado = false;
    
    @Getter
    @Setter
    private boolean verDiag=true;	
    

    @PostConstruct
    public void init() {


        Map<String, Object> variables = null;
        try {
            getNombreArchivoBuenasPracticas();
            generarDocumentoDescarga();
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

    private void generarDocumentoDescarga() throws CmisAlfrescoException {
        if (nombreDocumento.isEmpty()) {
            nombreDocumento = "tdr_doc_";
            String subsector = proyecto.getCatalogoCategoria().getCatalogoCategoriaPublico().getTipoSector().getNombre();
            nombreDocumento += subsector + ".pdf";
        }
        documento = documentosFacade.descargarDocumentoPorNombre(nombreDocumento);
    }

    private void getNombreArchivoBuenasPracticas() throws JbpmException {
        Map<String, Object> variables;
        variables = procesoFacade.recuperarVariablesProceso(loginBean
                .getUsuario(), bandejaTareasBean.getTarea()
                .getProcessInstanceId());
        Integer idProyecto = Integer.parseInt((String) variables.get(Constantes.ID_PROYECTO));
        proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorId(idProyecto);
        nombreDocumento= proyecto.getCatalogoCategoria().getGuiaBuenasPracticas() != null ?
                proyecto.getCatalogoCategoria().getGuiaBuenasPracticas() : "";
        if (!nombreDocumento.isEmpty() && !nombreDocumento.toLowerCase().endsWith(".pdf")) {
            nombreDocumento = nombreDocumento + ".pdf";
        }

    }

    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documento != null) {
            descargado = true;
            content = new DefaultStreamedContent(new ByteArrayInputStream(documento), "application/vnd.ms-excel");
            content.setName(nombreDocumento);

        }
        return content;

    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/licenciamiento-ambiental/documentos.jsf");
    }

    public boolean isVerDiag() throws ParseException {
    	verDiag=true;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechabloqueo = sdf.parse(Constantes.getFechaBloqueoTdrMineria());
        Date fechaproyecto=sdf.parse(proyecto.getFechaRegistro().toString());
        boolean bloquear=false;        
        if (fechaproyecto.before(fechabloqueo)){
        	bloquear=true;
        }        
        if ((proyecto.getCatalogoCategoria().getCodigo().equals("21.02.03.01") 
    			|| proyecto.getCatalogoCategoria().getCodigo().equals("21.02.03.02")
    			||  proyecto.getCatalogoCategoria().getCodigo().equals("21.02.03.03")
    			|| proyecto.getCatalogoCategoria().getCodigo().equals("21.02.03.04")    			
    			|| proyecto.getCatalogoCategoria().getCodigo().equals("21.02.04.01")
    			|| proyecto.getCatalogoCategoria().getCodigo().equals("21.02.04.02")    			
    			|| proyecto.getCatalogoCategoria().getCodigo().equals("21.02.05.01")
    			|| proyecto.getCatalogoCategoria().getCodigo().equals("21.02.05.02")    			
    			|| proyecto.getCatalogoCategoria().getCodigo().equals("21.02.08.01")    			
    			|| proyecto.getCatalogoCategoria().getCodigo().equals("21.02.07.01")
    			|| proyecto.getCatalogoCategoria().getCodigo().equals("21.02.07.02")
    			|| proyecto.getCatalogoCategoria().getCodigo().equals("21.02.06.01")
    			|| proyecto.getCatalogoCategoria().getCodigo().equals("21.02.06.03")
    			) && proyecto.getIdEstadoAprobacionTdr()==null && bloquear){
		if(verDiag){
			verDiag=false;
			return true;
		}
    	}else{
    		verDiag=false;
    	}
		return verDiag;
	}
    

}

