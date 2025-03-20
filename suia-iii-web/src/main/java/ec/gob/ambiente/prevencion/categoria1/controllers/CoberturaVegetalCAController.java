package ec.gob.ambiente.prevencion.categoria1.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.InventarioForestalPma;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.inventarioForestalPma.facade.InventarioForestalPmaFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class CoberturaVegetalCAController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1683511338517439792L;
    private static final String MENSAJE_CATEGORIAII_PAGO = "mensaje.categoriaII.pago";
	private static final Logger LOG = Logger.getLogger(CoberturaVegetalCAController.class);

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private InventarioForestalPmaFacade inventarioForestalPmaFacade;
	
    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private InventarioForestalPma inventarioForestalPma;

	@Setter
	@Getter
	private String nombreFileInventarioForestal;

    @Getter
    @Setter
    private Boolean descargado = false;

	@Setter
	@Getter
	private UploadedFile file;
	
	@Getter
	@Setter
	private String mensajePagoCoberturaVegetal;
	
	@Getter
	@Setter
	private boolean pagoCobertura;

	@PostConstruct
	public void init() {
		try {
			mensajePagoCoberturaVegetal = Constantes.getMessageResourceString(MENSAJE_CATEGORIAII_PAGO);
			inventarioForestalPma = inventarioForestalPmaFacade.obtenerInventarioForestalPmaPorProyecto(proyectosBean.getProyecto().getId());
			if (inventarioForestalPma == null) {
				inventarioForestalPma = new InventarioForestalPma();
				inventarioForestalPma.setProyectoLicenciamientoAmbiental(proyectosBean.getProyecto());
				inventarioForestalPma.setRemocionVegetal(true);
				pagoCobertura=true;
			} else {
                if (inventarioForestalPma.getInventarioForestal() != null) {
                    nombreFileInventarioForestal = inventarioForestalPma.getInventarioForestal().getNombre();

                    float coberturaVegetal = inventarioForestalPma.getMaderaEnPie() * Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal"));
                    mensajePagoCoberturaVegetal += " pago por concepto de remoción de cobertura vegetal nativa: " + String.valueOf(coberturaVegetal) + " USD en BanEcuador a nombre del Ministerio del Ambiente y Agua en la cuenta corriente Nro. 0010000777 sublínea 190499 y con el número de referencia debe completar la tarea \"Validar Pago\" ";
                    pagoCobertura=false;
                }
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos inventario forestal.");
		}
	}	

	public void fileUploadListenerInventarioForestal(FileUploadEvent event) {
		file = event.getFile();
		inventarioForestalPma.setInventarioForestal(UtilDocumento
				.generateDocumentXLSFromUpload(file.getContents(),
						file.getFileName()));
	}

	public StreamedContent descargar() {
		try {
            byte[] inventarioForestalFile = inventarioForestalPmaFacade
					.descargarFile(inventarioForestalPma.getInventarioForestal());
            if (inventarioForestalFile != null) {
                InputStream is = new ByteArrayInputStream(inventarioForestalFile);
                return new DefaultStreamedContent(is, "application/xls", inventarioForestalPma.getInventarioForestal().getNombre());
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
	}

	public StreamedContent descargarPlantillaInventarioForestal() {
        try {
            byte[] plantillaInventarioForestal = inventarioForestalPmaFacade
                    .getPlantillaInventarioForestal(Constantes.PLANTILLA_INVENTARIO_FORESTAL);
            if (plantillaInventarioForestal != null) {
                InputStream is = new ByteArrayInputStream(plantillaInventarioForestal);
                descargado = true;
                return new DefaultStreamedContent(is, "application/xls", "PlantillaInventarioForestal.xls");
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
        } catch (Exception e) {
            LOG.error("Error en optener la plantilla de inventario forestal.", e);
            JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
        }
	}

	public void guardar() {
		
		try {            
                if(getDescargado()) {
                    if (inventarioForestalPma.getInventarioForestal() != null) {
                        Boolean guardarArchivo = (file != null &&
                                !nombreFileInventarioForestal.equals(inventarioForestalPma.getInventarioForestal().getNombre()));

						//TODO Verificar el tipo de documento
                        inventarioForestalPmaFacade.guardar(inventarioForestalPma, TipoDocumentoSistema.INVENTARIO_FORESTAL_GEN ,guardarArchivo);
                        nombreFileInventarioForestal = inventarioForestalPma.getInventarioForestal().getNombre();

                        float coberturaVegetal = inventarioForestalPma.getMaderaEnPie() * Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal"));
                        mensajePagoCoberturaVegetal += " pago por concepto de remoción de cobertura vegetal nativa: " + String.valueOf(coberturaVegetal) + " USD en BanEcuador a nombre del Ministerio del Ambiente y Agua en la cuenta corriente Nro. 0010000777 sublínea 190499 y con el número de referencia debe completar la tarea \"Validar Pago\" ";
                        pagoCobertura=false;
                        
                    } else {
                        JsfUtil.addMessageError("Debe subir el archivo de inventario forestal");
                        return;
                    }
                }
                else {
                    JsfUtil.addMessageError("Debe descargar la plantilla del inventario forestal.");
                    return;
                }

		} catch (ServiceException | CmisAlfrescoException e) {
			LOG.error("error al guardar ", e);
			JsfUtil.addMessageError("Ocurrió un error al guardar Inventario Forestal");
		}
	}
	
	public void completarTarea(){
		
        if(getDescargado()) {
            if (inventarioForestalPma.getInventarioForestal() != null) {
            	Map<String, Object> data = new HashMap<String, Object>();
            	try {
					taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
					        bandejaTareasBean.getTarea()
					        .getTaskId(), bandejaTareasBean.getTarea()
					        .getProcessInstanceId(), data, loginBean
					        .getPassword(), Constantes
					        .getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
				} catch (JbpmException e) {
					JsfUtil.addMessageError("Error al aprobar la tarea.");
		            LOG.error(e);
				}
            } else {
                JsfUtil.addMessageError("Debe subir el archivo de inventario forestal");
                return;
            }
        }
        else {
            JsfUtil.addMessageError("Debe descargar la plantilla del inventario forestal.");
            return;
        }         
        
        JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	}
}