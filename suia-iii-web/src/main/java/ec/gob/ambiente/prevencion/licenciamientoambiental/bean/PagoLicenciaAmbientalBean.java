package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.comun.bean.PagoServiciosBean;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.LicenciaAmbiental;
import ec.gob.ambiente.suia.domain.OficioAprobacionEia;
import ec.gob.ambiente.suia.domain.OficioSolicitarPronunciamiento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.eia.inventarioforestal.facade.InventarioForestalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class PagoLicenciaAmbientalBean implements Serializable {

    private static final long serialVersionUID = -8900390283972456586L;

    private final Logger LOG = Logger.getLogger(PagoLicenciaAmbientalBean.class);

    @Getter
    @Setter
    private File oficioPdf;
    @Getter
    @Setter
    private byte[] archivoInforme;
    @Getter
    @Setter
    private String informePath;
    @Getter
    @Setter
    private OficioSolicitarPronunciamiento oficioSolicitarPronunciamiento;
    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    @Getter
    @Setter
    private OficioAprobacionEia oficioAprobacionEia;

    /**
     * Pago por Seguimiento y Control (PSC)
     */
    @Getter
    @Setter
    private Double valorRemocion;


    /**
     * Pago por Seguimiento y Control (PSC)
     */
    @Getter
    @Setter
    private Double valorPSC;


    /**
     * Costo proyecto
     */
    @Getter
    @Setter
    private float valorPoyecto;
    /**
     * Pago por costo proyecto
     */
    @Getter
    @Setter
    private float valorCP;

    /**
     * Valor Total a pagar
     */
    @Getter
    @Setter
    private double valorTotal;

    @EJB
    private InformeOficioFacade informeOficioFacade;

    @EJB
    private CertificadoInterseccionFacade certificadoInterseccionFacade;
    @EJB
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

    @EJB
    private AreaFacade areaFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
    private PersonaFacade personaFacade;

    @EJB
    private InventarioForestalFacade inventarioForestalFacade;

    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @ManagedProperty(value = "#{loginBean}")
    @Getter
    @Setter
    private LoginBean loginBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
    
    Map<String, Float> paramsValorAPagar = new ConcurrentHashMap<String, Float>();

    @PostConstruct
    public void init() {
        try {
            estudioImpactoAmbiental =
                    estudioImpactoAmbientalFacade.obtenerPorProyecto(proyectosBean.getProyecto());

            oficioAprobacionEia = informeOficioFacade
                    .obtenerOficioAprobacionEiaPorEstudio(
                            TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA,
                            estudioImpactoAmbiental.getId());

            LicenciaAmbiental licenciaAmbiental = licenciaAmbientalFacade.obtenerLicenciaAmbientallPorProyectoId(proyectosBean.getProyecto().getId());
            valorPoyecto = Float.valueOf(licenciaAmbiental.getCostoInversion().toString());

            valorRemocion = inventarioForestalFacade.obtenerMontoTotalValoracion(estudioImpactoAmbiental);
            paramsValorAPagar.put("coberturaVegetal", valorRemocion.floatValue());
            valorCP = valorPoyecto / 1000;
            valorPSC = oficioAprobacionEia.getValorTotal();
            //valorTotal = valorCP + valorPSC + valorRemocion;
            valorTotal = valorCP + valorPSC;
            NumberFormat formatter = new DecimalFormat("#.##");
            valorTotal = Double.parseDouble(formatter.format(valorTotal).replace(",", "."));
        } catch (ServiceException e) {
            LOG.error(e);
            JsfUtil.addMessageError("Error al cargar los datos.");
        }
    }

    public void pagoLicencia() {

   	    paramsValorAPagar.put("valorAPagar", (float) valorTotal);
   	    
        JsfUtil.getBean(PagoServiciosBean.class).initFunctionWithProjectOrMotive(
                "/bandeja/bandejaTareas.jsf", new CompleteOperation() {

                    @Override
                    public Object endOperation(Object object) {
                        try {
                            //Se aprueba la tarea
                            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                            taskBeanFacade.approveTask(loginBean.getNombreUsuario(), bandejaTareasBean.getTarea().getTaskId(),
                                    bandejaTareasBean.getTarea().getProcessInstanceId(), data, loginBean.getPassword(),
                                    Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
                            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                            procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
                        } catch (Exception e) {
                            LOG.error(e);
                            JsfUtil.addMessageError("Error al realizar la operación.");
                        }
                        return "";
                    }
                }, paramsValorAPagar, null,null,null,false);
    }

    public void validarTareaBpm() {
        String url = "/prevencion/licenciamiento-ambiental/pagoLicencia.jsf";
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }

}
