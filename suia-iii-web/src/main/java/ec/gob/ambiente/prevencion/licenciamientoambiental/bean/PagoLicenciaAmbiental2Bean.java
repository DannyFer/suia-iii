package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

import org.apache.log4j.Logger;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.comun.bean.PagoServiciosBean;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.InventarioForestalPma;
import ec.gob.ambiente.suia.domain.LicenciaAmbiental;
import ec.gob.ambiente.suia.domain.OficioAprobacionEia;
import ec.gob.ambiente.suia.domain.OficioSolicitarPronunciamiento;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.eia.inventarioforestal.facade.InventarioForestalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class PagoLicenciaAmbiental2Bean implements Serializable {

    private static final long serialVersionUID = -7403172866537439775L;
    private static final String MENSAJE_CATEGORIAII_PAGO = "mensaje.categoriaII.pago";
    private final Logger LOG = Logger.getLogger(PagoLicenciaAmbiental2Bean.class);

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
    /**
     * Valor minimo a pagar
     */
    @Getter
    @Setter
    private float valorCPMinimo = 500f;

    @Getter
    @Setter
    private boolean noPaga = false;

    @Getter
    @Setter
    private LicenciaAmbiental licenciaAmbiental;

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
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

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
    
    @Getter
    @Setter
    private String mensajePagoLicencia;

    @PostConstruct
    public void init() {
        try {        	
            estudioImpactoAmbiental =
                    estudioImpactoAmbientalFacade.obtenerPorProyecto(proyectosBean.getProyecto());
            if (proyectosBean.getProyecto().getCatalogoCategoria().getCategoria().getCodigo().equals("IV")) {
                valorCPMinimo = 1000f;
            }
            oficioAprobacionEia = informeOficioFacade
                    .obtenerOficioAprobacionEiaPorEstudio(
                            TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA,
                            estudioImpactoAmbiental.getId());
            valorPoyecto = 0;
            licenciaAmbiental = licenciaAmbientalFacade.obtenerLicenciaAmbientallPorProyectoId(proyectosBean.getProyecto().getId());
            ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade.cargarProyectoFullPorId(proyectosBean.getProyecto().getId());
            Usuario promotor = usuarioFacade.buscarUsuario(proyecto.getUsuario().getNombre());
            List<Organizacion> organizaciones = promotor.getPersona().getOrganizaciones();
            for (Organizacion organizacion : organizaciones) {
                if (organizacion.getRuc().equals(promotor.getNombre())) {
//                    if (organizacion.getTipoOrganizacion().getDescripcion().equals("EP")) {
//                        noPaga = true;
//                    } else if (organizacion.getTipoOrganizacion().getDescripcion().equals("EM") && organizacion.getParticipacionEstado().equals("Igual o más que el 70% de participación del Estado")) {
//                        noPaga = true;
//                    }
                	if (organizacion.getTipoOrganizacion().getDescripcion().equals("EP")||(organizacion.getTipoOrganizacion().getDescripcion().equals("EM") && organizacion.getParticipacionEstado().equals("Igual o más que el 70% de participación del Estado"))||organizacion.getTipoOrganizacion().getDescripcion().equals("GA")) {
                        noPaga = true;
                    } 
                }
            }
            if (licenciaAmbiental == null) {


                Map<String, Object> variables = procesoFacade
                        .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                                .getTarea().getProcessInstanceId());

                String tecnicoName = (String) variables.get("u_TecnicoResponsable");


                licenciaAmbiental = new LicenciaAmbiental();
                licenciaAmbiental.setProponente(proyecto.getUsuario());
                licenciaAmbiental.setCategoriaLicencia(proyecto.getCatalogoCategoria().getCategoria());
                licenciaAmbiental.setProyecto(proyecto);
                licenciaAmbiental.setTecnicoResponsable(usuarioFacade.buscarUsuario(tecnicoName));
                licenciaAmbiental.setCostoInversion(0d);
            }

            valorRemocion = inventarioForestalFacade.obtenerMontoTotalValoracion(estudioImpactoAmbiental);
       	    paramsValorAPagar.put("coberturaVegetal", valorRemocion.floatValue());
            valorPSC = oficioAprobacionEia.getValorTotal();

            calcularValorTotal();
        } catch (ServiceException e) {
            LOG.error(e);

            JsfUtil.addMessageError("Error al cargar los datos.");
        } catch (JbpmException e) {
            LOG.error(e);

            JsfUtil.addMessageError("Error al cargar los datos.");
        } catch (Exception e) {
            LOG.error(e);

            JsfUtil.addMessageError("Error al cargar los datos.");
        }
    }

    public void calcularValorTotal() {
        valorPoyecto = Float.valueOf(licenciaAmbiental.getCostoInversion().toString());
        if (!noPaga) {
            valorCP = valorPoyecto / 1000;
            if (valorCP < valorCPMinimo) {
                valorCP = valorCPMinimo;
            }
        } else {
            valorCP = 0;
        }
        valorTotal = valorCP + valorPSC + valorRemocion;
        NumberFormat formatter = new DecimalFormat("#.##");
        valorTotal = Double.parseDouble(formatter.format(valorTotal).replace(",", "."));
        
        if (proyectosBean.getProyecto().getAreaResponsable().getTipoArea().getId().equals(3)){
    		mensajePagoLicencia = (Constantes.getMessageResourceString("mensaje.categoriaII.pago.entes"));
    	}else{
    		mensajePagoLicencia = Constantes.getMessageResourceString(MENSAJE_CATEGORIAII_PAGO);
    	}
        String textoPago=" pago por concepto de Licencia Ambiental: "+ (valorCP + valorPSC) +" en  BanEcuador a nombre del Ministerio del Ambiente y Agua en la Cuenta corriente Nro. 3001174975 sublínea 370102. ";
        
        if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
    		textoPago=" pago por concepto de Licencia Ambiental: "+ (valorCP + valorPSC) +" USD  en la entidad financiera o lugar de recaudación correspondiente al "+proyectosBean.getProyecto().getAreaResponsable().getAreaName();
		}

        mensajePagoLicencia += textoPago;
        
        if(valorRemocion>0){
        	mensajePagoLicencia += " Y debe realizar el pago por concepto de remoción de cobertura vegetal nativa: "+valorRemocion+" USD en BanEcuador a nombre del Ministerio del Ambiente y Agua en la cuenta corriente Nro. 0010000777 sublínea 190499 y con los números de referencia debe completar la tarea \"Pago de licencia ambiental\"";
        }else {
        	mensajePagoLicencia += " Y con el número de referencia debe completar la tarea \"Pago de licencia ambiental\".";
		}
    }

    public void pagoLicencia() {
        calcularValorTotal();

   	    paramsValorAPagar.put("valorAPagar", (float) valorTotal);
        licenciaAmbientalFacade.archivarProcesoLicenciaAmbiental(licenciaAmbiental);
        JsfUtil.getBean(PagoServiciosBean.class).initFunctionWithProjectOrMotive(
                "/bandeja/bandejaTareas.jsf", new CompleteOperation() {

                    @Override
                    public Object endOperation(Object object) {
                        try {
                            //Se aprueba la tarea
                            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                            procesoFacade.aprobarTarea(loginBean.getUsuario(),bandejaTareasBean.getTarea().getTaskId(),
                                    bandejaTareasBean.getProcessId(), data);
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
        String url = "/prevencion/licenciamiento-ambiental/pagoLicenciaAmbiental.jsf";
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }

}
