package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.RolFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisarDocumentacionPagoLicenciaBean implements Serializable {


    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(RevisarDocumentacionPagoLicenciaBean.class);
    private static final long serialVersionUID = 8089350593176265029L;

    @EJB
    private ProcesoFacade procesoFacade;
    
    @EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;


    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    private Boolean esCorrecto = false;
    
    @EJB
	private UsuarioFacade usuarioFacade;
    
    @EJB
	private AreaFacade areaFacade;
    

    @EJB
	private RolFacade rolFacade;
    
    
    
    @PostConstruct
    public void init() throws ServiceException {
    	//JsfUtil.cargarObjetoSession(Constantes.SESSION_EIA_OBJECT, estudioImpactoAmbientalFacade.obtenerPorProyecto(proyectosBean.getProyecto())); //para buscar Costo de implementación del PMA
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/licenciamiento-ambiental/documentos/revisarDocumentacion.jsf");
    }

    public String realizarTarea() {
        try {
            if (validarObservaciones(esCorrecto)) {                          	
            	//Asigna un técnico predeterminado
            	Map<String, Object> variables=procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                        .getTarea().getProcessInstanceId());
    			
    			Object varTecnico = variables.get("u_CoordinadorJuridico");
    			 Map<String, Object> params = new ConcurrentHashMap<>();        
            	Usuario usuariovalidar= usuarioFacade.buscarUsuario(varTecnico.toString());
            	if (usuariovalidar!=null) {	
            		String tipoRol = "role.area.coordinador.Juridico";
            		Area areaProyecto = areaFacade.getArea(proyectosBean.getProyecto().getAreaResponsable().getId());
            		
            		if (areaProyecto.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
            			tipoRol = "role.pc.coordinador.Juridico";
            			areaProyecto = areaFacade.getAreaSiglas("CGAJ");
            		}
            		
            		List<Usuario> usuariorol=usuarioFacade.buscarUsuarioPorRolNombreAreaUnico(tipoRol, areaProyecto.getAreaName(), usuariovalidar.getId());
            		if (usuariorol.size()==0) {
            			Usuario usuarioTecniconuevo =areaFacade.getUsuarioPorRolArea(tipoRol,areaProyecto);  
            			 if(usuarioTecniconuevo!=null) {
        					 params.put("u_CoordinadorJuridico", usuarioTecniconuevo.getNombre());    					
        				}else {
        					LOG.error("Error al enviar los datos de validar pago de licencia. No existe el rol "+tipoRol +" en el area "+ areaProyecto.getAreaName());
        		            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        		            return "";
        				}
            		}            		
            	}				
            	//Set process variables
                params.put("pagoCorrecto",esCorrecto);             
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                        .getTarea().getProcessInstanceId(), params);

                taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), new ConcurrentHashMap<String, Object>(), loginBean.getPassword(),
                        Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
            } else {
                return "";
            }
        } catch (Exception e) {
            LOG.error("Error al enviar los datos de validar pago de licencia.", e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }
        return "";
    }

    public Boolean validarObservaciones(Boolean estado) {
        List<ObservacionesFormularios> observaciones = observacionesController.getObservacionesBB().getMapaSecciones().get("comprobante");

        if (estado) {
            for (ObservacionesFormularios observacion : observaciones) {
            	/* MarielaG
            	   Comparacion de usuario cambiar operador "=" por metodo equals **/
            	if (observacion.getUsuario().equals(loginBean
                        .getUsuario()) && !observacion.isObservacionCorregida()) {

                    JsfUtil.addMessageError("Existen observaciones sin corregir. Por favor rectifique los datos.");
                    return false;
                }
            }
        } else {
            int posicion = 0;
            int cantidad = observaciones.size();
            Boolean encontrado = false;
            while (!encontrado && posicion < cantidad) {
                if (observaciones.get(posicion).getUsuario().equals(loginBean.getUsuario())
                        && !observaciones.get(posicion).isObservacionCorregida()) {
                    encontrado = true;
                }
                posicion++;
            }
            if (!encontrado) {
                JsfUtil.addMessageError("No existen observaciones sin corregir. Por favor rectifique los datos.");
                return false;
            }
        }
        return true;
    }
}

