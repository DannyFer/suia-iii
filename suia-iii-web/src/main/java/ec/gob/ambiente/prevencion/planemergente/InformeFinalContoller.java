package ec.gob.ambiente.prevencion.planemergente;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.CargarCoordenadasBean;
import ec.gob.ambiente.suia.domain.AccionEmergente;
import ec.gob.ambiente.suia.domain.AccionEmergenteCoordenada;
import ec.gob.ambiente.suia.domain.EventoOcurrido;
import ec.gob.ambiente.suia.domain.IndicadorMedidaCorrectiva;
import ec.gob.ambiente.suia.domain.MedidaCorrectiva;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.planemergente.service.AccionEmergenteCoordenadaService;
import ec.gob.ambiente.suia.prevencion.planemergente.service.AccionEmergenteService;
import ec.gob.ambiente.suia.prevencion.planemergente.service.EventoOcurridoService;
import ec.gob.ambiente.suia.prevencion.planemergente.service.IndicadorMedidaCorrectivaService;
import ec.gob.ambiente.suia.prevencion.planemergente.service.MedidaCorrectivaService;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * Created by msit-erislan on 09/11/2015.
 */

@ManagedBean
@ViewScoped
public class InformeFinalContoller {

    @ManagedProperty(value = "#{planEmergenteController}")
    @Setter
    protected PlanEmergenteController planEmergenteController;
    @ManagedProperty(value = "#{cargarCoordenadasBean}")
    @Setter
    @Getter
    private CargarCoordenadasBean cargarCoordenadasBean;
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Setter
    private BandejaTareasBean bandejaTareasBean;
    @ManagedProperty(value = "#{loginBean}")
    @Setter
    private LoginBean loginBean;

    @EJB
    protected AccionEmergenteService accionEmergenteService;
    @EJB
    protected EventoOcurridoService eventoOcurridoService;
    @EJB
    protected MedidaCorrectivaService medidaCorrectivaService;
    @EJB
    protected IndicadorMedidaCorrectivaService indicadorMedidaCorrectivaService;
    @EJB
    protected AccionEmergenteCoordenadaService accionEmergenteCoordenadaService;
    @EJB
    private ProcesoFacade procesoFacade;

    @Getter
    protected List<EventoOcurrido> eventosOcurridos;
    @Getter
    protected List<AccionEmergente> accionesImformeFinal;
    @Getter
    protected AccionEmergente accion;
    @Getter
    protected MedidaCorrectiva medidaCorrectiva;
    @Getter
    protected IndicadorMedidaCorrectiva indicador;
    @Getter
    @Setter
    protected String medida, indi;
    @Getter
    protected List<MedidaCorrectiva> medidasCorrectivasBorrar;
    @Getter
    protected List<IndicadorMedidaCorrectiva> indicadoresBorrar;
    @Getter
    private String minFechaInicio, maxFechaInicio, minFechaFin;

    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private List<AccionEmergenteCoordenada> coordenadasBorradas;

    @PostConstruct
    public void init() {
        coordenadasBorradas = new ArrayList<>();
        planEmergenteController.actualizar();
        eventosOcurridos = eventoOcurridoService.buscarPorPlanEmergente(planEmergenteController.getPlanEmergente());
        accionesImformeFinal = accionEmergenteService.buscarPorEventos(eventosOcurridos, AccionEmergente.INFORME_FINAL);
        medidasCorrectivasBorrar = new ArrayList<>();
        indicadoresBorrar = new ArrayList<>();

        minFechaInicio = dateFormat.format(planEmergenteController.getPlanEmergente().getFechaCreacion());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(planEmergenteController.getPlanEmergente().getFechaCreacion());
        calendar.add(Calendar.DAY_OF_YEAR, 10);
        maxFechaInicio = dateFormat.format(calendar.getTime());
    }

    public void mostrar(int pos) {
        accion = accionesImformeFinal.get(pos);
        medidaCorrectiva = !accion.getMedidaCorrectivas().isEmpty() ? accion.getMedidaCorrectivas().get(0) : new MedidaCorrectiva();
        RequestContext.getCurrentInstance().execute("PF('informeFinalEditarDialog').show();");
    }

    public void editar(ActionEvent event) {
        if(validar(accion))
            RequestContext.getCurrentInstance().execute("PF('informeFinalEditarDialog').hide();");
    }

    public void eliminar(int pos) {}

    public void guardar(ActionEvent event) {
        boolean flat = true;
        for (AccionEmergente accionEmergente: accionesImformeFinal) {
            flat &= validar(accionEmergente);
        }

        if(flat) {
            accionEmergenteCoordenadaService.borrarSiExiste(coordenadasBorradas);
            coordenadasBorradas.clear();
            medidaCorrectivaService.eliminar(medidasCorrectivasBorrar);
            indicadorMedidaCorrectivaService.eliminar(indicadoresBorrar);

            for (AccionEmergente accion: accionesImformeFinal) {
                accion.setEventoOcurrido(eventoOcurridoService.editar(accion.getEventoOcurrido()));

                List<MedidaCorrectiva> medidas = accion.getMedidaCorrectivas();
                accion.setMedidaCorrectivas(new ArrayList<MedidaCorrectiva>());
                accion = accionEmergenteService.editar(accion);

                for (MedidaCorrectiva correctiva : medidas) {
                    correctiva.setAccionEmergente(accion);
                    List<IndicadorMedidaCorrectiva> indicadores = correctiva.getIndicadores();
                    correctiva.setIndicadores(new ArrayList<IndicadorMedidaCorrectiva>());
                    correctiva = medidaCorrectivaService.guardar(correctiva);

                    for (IndicadorMedidaCorrectiva indicador : indicadores) {
                        indicador.setMedidaCorrectiva(correctiva);
                        indicador = indicadorMedidaCorrectivaService.guardar(indicador);
                        correctiva.getIndicadores().add(indicador);
                    }
                    accion.getMedidaCorrectivas().add(correctiva);
                }
            }
        }
        JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
    }

    public void adjuntarMedioVerificacion(ActionEvent event) {}

    private boolean validar(AccionEmergente accionEmergente) {
        boolean flat = true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(planEmergenteController.getPlanEmergente().getFechaCreacion());
        calendar.add(Calendar.DAY_OF_YEAR, 10);
        if(accionEmergente.getMedidaCorrectivas().isEmpty()) {
            JsfUtil.addMessageError("Debe adicionar al menos una medida correctiva para cada evento.");
            flat = false;
        }
        else {
            for (MedidaCorrectiva medida : accionEmergente.getMedidaCorrectivas()) {
                if (medida.getIndicadores().isEmpty()) {
                    JsfUtil.addMessageError("Debe adicionar al menos un indicador para cada medida correctiva.");
                    flat = false;
                }
            }
        }
        if(accionEmergente.getCoordenadas().isEmpty()) {
            JsfUtil.addMessageError("Debe adicionar las coordenadas para cada acción.");
            flat = false;
        }
        if(accionEmergente.getFechaInicio().before(planEmergenteController.getPlanEmergente().getFechaCreacion())) {
            JsfUtil.addMessageError("La fecha de inicio no debe ser menor que la fecha de creación del plan emergente.");
            flat = false;
        }
        if (accionEmergente.getFechaInicio().after(calendar.getTime())) {
            JsfUtil.addMessageError("La fecha de inicio no debe superar los 10 días después de la fecha de creación del plan emergente.");
            flat = false;
        }
        if (accionEmergente.getFechaFin().before(accion.getFechaInicio())) {
            JsfUtil.addMessageError("La fecha de fin no debe ser menor que la fecha de inicio.");
            flat = false;
        }
        return flat;
    }

    public void adicionarMedida(ActionEvent event) {
        if(!medida.equals("")) {
            medidaCorrectiva = new MedidaCorrectiva();
            medidaCorrectiva.setAccionEmergente(accion);
            medidaCorrectiva.setDescripcion(medida);
            medidaCorrectiva.setIndicadores(new ArrayList<IndicadorMedidaCorrectiva>());
            accion.getMedidaCorrectivas().add(medidaCorrectiva);
            medida = "";
        }
    }

    public void mostrarMedida(int pos) {
        medidaCorrectiva = accion.getMedidaCorrectivas().get(pos);
        RequestContext.getCurrentInstance().execute("PF('informeFinalEditarMedidaDialog').show();");
    }

    public void eliminarMedida(int pos) {
        medidasCorrectivasBorrar.add(accion.getMedidaCorrectivas().get(pos));
        accion.getMedidaCorrectivas().remove(pos);
    }

    public void editarMedida(ActionEvent event) {
        RequestContext.getCurrentInstance().execute("PF('informeFinalEditarMedidaDialog').hide();");
    }

    public void adicionarIndicador(ActionEvent event) {
        if(!indi.equals("")) {
            indicador = new IndicadorMedidaCorrectiva();
            indicador.setMedidaCorrectiva(medidaCorrectiva);
            indicador.setDescripcion(indi);
            medidaCorrectiva.getIndicadores().add(indicador);
            indi = "";
        }
    }

    public void mostrarIndicador(int pos) {
        indicador = medidaCorrectiva.getIndicadores().get(pos);
        RequestContext.getCurrentInstance().execute("PF('informeFinalEditarIndicadorDialog').show();");
    }

    public void eliminarIndicador(int pos) {
        indicadoresBorrar.add(medidaCorrectiva.getIndicadores().get(pos));
        medidaCorrectiva.getIndicadores().remove(pos);
    }

    public void editarIndicador(ActionEvent event) {
        RequestContext.getCurrentInstance().execute("PF('informeFinalEditarIndicadorDialog').hide();");
    }

    public void cambiarUnidadMedida(AjaxBehaviorEvent event) {
        if ((accion.getUnidadAreaAfectada() != null && accion.getUnidadAreaAfectada().equals("No cuantificable"))
                || (accion.getUnidadAreaRecuperada() != null && accion.getUnidadAreaRecuperada().equals("No cuantificable"))) {
            accion.setJustificacion("");
        }
    }

    public void handleFileUpload(final FileUploadEvent event) {
        cargarCoordenadasBean.handleFileUpload(event);
        List<CoordinatesWrapper> coordinatesWrappers = cargarCoordenadasBean.getCoordinatesWrappers();
        if(!coordinatesWrappers.isEmpty()) {
            coordenadasBorradas.addAll(accion.getCoordenadas());
            accion.setCoordenadas(new ArrayList<AccionEmergenteCoordenada>());
            for (CoordinatesWrapper wrapper : coordinatesWrappers) {
                accion.getCoordenadas().add(new AccionEmergenteCoordenada(wrapper, accion));
            }
            RequestContext.getCurrentInstance().execute("PF('adjuntarCoordenadas').hide();");
        }
    }

    public void enviarTarea(ActionEvent event) {

        try {
            Map<String, Object> data = new HashMap<>();
            String body = "Se le informa que se inició un Proceso Administrativo por el incumplimiento de tiempo en normativa, correspondiente al sujeto de control ";
            body += loginBean.getUsuario().getNombre();
            body += ", perteneciente al proyecto: ";
            if(planEmergenteController.isRegularizacion())
                body += planEmergenteController.getProyectoLicenciamientoAmbiental().getNombre();
            else
                body += planEmergenteController.getPlanEmergente().getNombre();
            data.put("body", new String(body.getBytes(Charset.forName("UTF-8"))));
            data.put("to", loginBean.getUsuario().getNombre());
            data.put("coordinadorUser", loginBean.getUsuario().getNombre());
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
            JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
        } catch (JbpmException e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
            e.printStackTrace();
        }
    }

}
