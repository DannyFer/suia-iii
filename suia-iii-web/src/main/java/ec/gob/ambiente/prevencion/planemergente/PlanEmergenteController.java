package ec.gob.ambiente.prevencion.planemergente;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.IdentificarProyectoComunBean;
import ec.gob.ambiente.suia.domain.AccionEmergente;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;
import ec.gob.ambiente.suia.domain.EventoOcurrido;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlanEmergente;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.planemergente.service.AccionEmergenteService;
import ec.gob.ambiente.suia.prevencion.planemergente.service.EventoOcurridoService;
import ec.gob.ambiente.suia.prevencion.planemergente.service.PlanEmergenteService;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

/**
 * Created by msit-erislan on 06/11/2015.
 */

@ManagedBean
@SessionScoped
public class PlanEmergenteController extends IdentificarProyectoComunBean {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6954761054810744202L;
    private static final Integer SECTOR_HIDROCARBUROS_ID = 1;
    private static final Integer SECTOR_MINERIA_ID = 2;

    public static final String EVENTOOCURRIDO = "eventoOcurrido";
    public static final String IMPLEMENTADAS = "accionesImplementadas";
    public static final String PORIMPLEMENTAR = "accionesPorImplementar";
    public static final String LEVANTAMIENTO = "levantamientoPreeliminar";


    @ManagedProperty(value = "#{loginBean}")
    @Getter
    @Setter
    private LoginBean loginBean;
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;

    @EJB
    private PlanEmergenteService planEmergenteService;
    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
    @EJB
    private OrganizacionFacade organizacionFacade;
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private EventoOcurridoService eventoOcurridoService;
    @EJB
    protected AccionEmergenteService accionEmergenteService;

    @Getter
    @Setter
    private PlanEmergente planEmergente;
    @Getter
    @Setter
    private boolean regularizacion, creado;
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;
    @Getter
    @Setter
    private String tipoRepresentante, proponente;
    @Getter
    private List<TipoSector> sectores;
    @Getter
    private List<CatalogoCategoriaSistema> categorias;

    private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
    private List<String> secciones;

    @PostConstruct
    public void init() {
        regularizacion = true;
        creado = false;
        tipoRepresentante = "PN";
        sectores = planEmergenteService.getSectores();
        secciones = new ArrayList<>();
    }

    public String iniciar() {
        regularizacion = false;
        planEmergente = new PlanEmergente();
        if(!sectores.isEmpty()) {
            categorias = planEmergenteService.getCategoriasPorSector(sectores.get(0));
            planEmergente.setTipoSector(sectores.get(0));
            if(!categorias.isEmpty()) {
                planEmergente.setCatalogoCategoria(categorias.get(0));
            }
        }
        planEmergente.setTipoRepresentante(tipoRepresentante);
        planEmergente = planEmergenteService.create(planEmergente);
        planEmergente.setTramite(Constantes.SIGLAS_INSTITUCION + "-SOL-PE-" + Calendar.getInstance().get(Calendar.YEAR) + "-" + planEmergente.getId());
        planEmergente = planEmergenteService.create(planEmergente);
        iniciarProceso();
        return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf#no-back-button");
    }

    public String seleccionarProyecto() {
        regularizacion = true;
        return JsfUtil.actionNavigateTo("/prevencion/planemergente/common/seleccionarProyecto.jsf");
    }

    public String seleccionar(Object object) {
        proyectoLicenciamientoAmbiental = proyectoLicenciamientoAmbientalFacade.getProyectoPorId(new Integer(((ProyectoCustom)object).getId()));
        categorias = planEmergenteService.getCategoriasPorSector(proyectoLicenciamientoAmbiental.getTipoSector());
        proponente = proyectoLicenciamientoAmbiental.getUsuario().getPersona().getNombre();
        try {
            Organizacion organizacion = organizacionFacade.buscarPorPersona(proyectoLicenciamientoAmbiental.getUsuario().getPersona(), proyectoLicenciamientoAmbiental.getUsuario().getNombre());
            if (organizacion != null) {
                proponente = organizacion.getNombre();
                tipoRepresentante = "PN";
            } else {
                tipoRepresentante = "PJ";
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        planEmergente = new PlanEmergente();
        planEmergente.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
        planEmergente.setTipoRepresentante(tipoRepresentante);
        planEmergente.setCiRepresentante(proyectoLicenciamientoAmbiental.getUsuario().getPersona().getPin());
        planEmergente.setNombreRepresentante(proponente);
        planEmergente = planEmergenteService.create(planEmergente);
        planEmergente.setTramite(Constantes.SIGLAS_INSTITUCION + "-SOL-PE-" + Calendar.getInstance().get(Calendar.YEAR) + "-" + planEmergente.getId());
        planEmergente = planEmergenteService.create(planEmergente);
        creado = true;
        iniciarProceso();
        return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf#no-back-button");
    }

    public boolean actualizar() {
        try {
            Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());
            Integer idPlanEmergente = Integer.parseInt((String) variables.get("planEmergente"));
            planEmergente = planEmergenteService.getById(idPlanEmergente);
            regularizacion = planEmergente.getProyectoLicenciamientoAmbiental() != null;
            tipoRepresentante = planEmergente.getTipoRepresentante();
            if(regularizacion) {
                proyectoLicenciamientoAmbiental = planEmergente.getProyectoLicenciamientoAmbiental();
                creado = true;
                categorias = planEmergenteService.getCategoriasPorSector(proyectoLicenciamientoAmbiental.getTipoSector());
            } else {
                creado = planEmergente.getCiRepresentante() != null && !planEmergente.getCiRepresentante().equals("");
                categorias = planEmergenteService.getCategoriasPorSector(planEmergente.getTipoSector());
            }
            return true;
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al realizar la operación. Inténtelo más tarde.");
            return false;
        }
    }

    public void cambiarSector(AjaxBehaviorEvent event) {
        categorias = planEmergenteService.getCategoriasPorSectorId(planEmergente.getTipoSector().getId());
        if(!categorias.isEmpty()) {
            planEmergente.setCatalogoCategoria(categorias.get(0));
        }
    }

    public void createPlan(ActionEvent event) {
        planEmergente.setTipoRepresentante(tipoRepresentante);
        if(!isRegularizacion() && validarCedula()) {
            planEmergente = planEmergenteService.create(planEmergente);
            creado = true;
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        }
        if(isRegularizacion()) {
            planEmergente = planEmergenteService.create(planEmergente);
                JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        }
    }

    public void createPlanConRegularizacion() {
        planEmergente.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
        planEmergente = planEmergenteService.create(planEmergente);
    }

    private boolean elProyectoEsDe(int sector) {
        if(isRegularizacion())
            return proyectoLicenciamientoAmbiental.getTipoSector().getId() == sector;
        else
            return planEmergente.getTipoSector().getId() == sector;
    }

    public boolean isMineria() {
        return elProyectoEsDe(SECTOR_MINERIA_ID);
    }

    public boolean isHidrocarburos() {
        return elProyectoEsDe(SECTOR_HIDROCARBUROS_ID);
    }

    public boolean isOtros() {
        return !elProyectoEsDe(SECTOR_MINERIA_ID) && !elProyectoEsDe(SECTOR_HIDROCARBUROS_ID);
    }

    public boolean validarCedula() {
        switch (planEmergente.getTipoRepresentante()) {
            case "PN":
                Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, planEmergente.getCiRepresentante());
                if(cedula == null || cedula.getCedula() == null) {
                    JsfUtil.addMessageErrorForComponent("ci", "Introduzca una cédula válida.");
                    return false;
                }
                break;
            case "PJ":
                ContribuyenteCompleto contribuyente = consultaRucCedula.obtenerPorRucSRI(Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, planEmergente.getCiRepresentante());
                if(contribuyente == null || contribuyente.getNumeroRuc() == null) {
                    JsfUtil.addMessageErrorForComponent("ci", "Introduzca un RUC válido.");
                    return false;
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void addSeccionCompletada(String seccion) {
        if(!seccionCompletada(seccion))
            secciones.add(seccion);
    }

    public Boolean seccionCompletada(String seccion) {
        return secciones.contains(seccion);
    }

//    private List<String> getSeccionesDelProceso() {
//        List<String> list = new ArrayList<>();
//        list.add(EVENTOOCURRIDO);
//        list.add(IMPLEMENTADAS);
//        list.add(PORIMPLEMENTAR);
//        list.add(LEVANTAMIENTO);
//        return list;
//    }

//    private List<String> getSeccionesRequeridas() {
//        List<String> list = new ArrayList<>();
//        list.add(EVENTOOCURRIDO);
//        list.add(IMPLEMENTADAS);
//        list.add(PORIMPLEMENTAR);
//        list.add(LEVANTAMIENTO);
//        return list;
//    }

    public String irAEventosOcurridos() {
        return JsfUtil.actionNavigateTo("/prevencion/planemergente/ingresarinformacion/eventoOcurrido.jsf");
    }

    private void iniciarProceso() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("proponente", loginBean.getUsuario().getNombre());
        parametros.put("planEmergente", planEmergente.getId());
        try {

            procesoFacade.iniciarProceso(loginBean.getUsuario(), Constantes.NOMBRE_PROCESO_PLAN_EMERGENTE, planEmergente.getTramite(), parametros);
            JsfUtil.addMessageInfo("Se ha iniciado el tramite: " + planEmergente.getTramite() + ", por favor inicie su primera actividad.");
        } catch (JbpmException e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
            e.printStackTrace();
        }
    }

    public void enviarTarea(ActionEvent event) {
        planEmergente = planEmergenteService.create(planEmergente);
        List<EventoOcurrido> eventosOcurridos = eventoOcurridoService.buscarPorPlanEmergente(planEmergente);

        if(eventosOcurridos.isEmpty()) {
            RequestContext.getCurrentInstance().execute("PF('adicionarEventos').show();");
            return;
        }

        List<AccionEmergente> acciones = accionEmergenteService.buscarPorEventos(eventosOcurridos, AccionEmergente.YA_IMPLEMENTADA);
        for (AccionEmergente accion : acciones) {
            if(accion.getMedidaCorrectivas().isEmpty()) {
                RequestContext.getCurrentInstance().execute("PF('completarAccionesImpl').show();");
                return;
            }
        }

        acciones = accionEmergenteService.buscarPorEventos(eventosOcurridos, AccionEmergente.POR_IMPLEMENTAR);
        for (AccionEmergente accion : acciones) {
            if(accion.getMedidaCorrectivas().isEmpty()) {
                RequestContext.getCurrentInstance().execute("PF('completarAccionesPorImpl').show();");
                return;
            }
        }

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("accionesEnMasDeDiez", planEmergente.isHastaDiezDiasParaImplementar());
            data.put("to", loginBean.getUsuario().getNombre());
            data.put("from", loginBean.getUsuario().getNombre());
            String body = "Se le informa que se ingresó un Plan Emergente por el sujeto de control: ";
            body += loginBean.getUsuario().getNombre();
            body += ", perteneciente al proyecto: ";
            if(isRegularizacion())
                body += proyectoLicenciamientoAmbiental.getNombre();
            else
                body += planEmergente.getNombre();
            body += ", en el cual menciona que ocurrieron los siguiente eventos:\n";
            for (EventoOcurrido eventosOcurrido : eventosOcurridos) {
                body += eventosOcurrido.getDescripcion()+"\n";
            }
            data.put("body", new String(body.getBytes(Charset.forName("UTF-8"))));
            data.put("notificarIniciarAprobacionBody", "Debe iniciar el proceso Aprobacion y actualizacion del Plan de accion");
            data.put("notificarIniciarAprobacionTo", loginBean.getUsuario().getNombre());
            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), data);
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);

            List<AccionEmergente> accionesImple = accionEmergenteService.buscarPorEventos(eventosOcurridos, AccionEmergente.YA_IMPLEMENTADA);
            List<AccionEmergente> accionesPorImple = accionEmergenteService.buscarPorEventos(eventosOcurridos, AccionEmergente.POR_IMPLEMENTAR);
            for (AccionEmergente accionEmergente : accionesImple) {
                accionEmergenteService.createAccionInformeFinal(accionEmergente);
            }
            for (AccionEmergente accionEmergente : accionesPorImple) {
                accionEmergenteService.createAccionInformeFinal(accionEmergente);
            }

            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
            JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
        } catch (JbpmException e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
            e.printStackTrace();
        }

    }
}
