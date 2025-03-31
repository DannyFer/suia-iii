package ec.gob.ambiente.prevencion.planemergente;

import ec.gob.ambiente.suia.domain.AccionEmergente;
import ec.gob.ambiente.suia.domain.EventoOcurrido;
import ec.gob.ambiente.suia.domain.IndicadorMedidaCorrectiva;
import ec.gob.ambiente.suia.domain.MedidaCorrectiva;
import ec.gob.ambiente.suia.prevencion.planemergente.service.AccionEmergenteService;
import ec.gob.ambiente.suia.prevencion.planemergente.service.EventoOcurridoService;
import ec.gob.ambiente.suia.prevencion.planemergente.service.IndicadorMedidaCorrectivaService;
import ec.gob.ambiente.suia.prevencion.planemergente.service.MedidaCorrectivaService;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by msit-erislan on 09/11/2015.
 */

@ManagedBean
@ViewScoped
public class AccionEmergentePorImplementarController {

    @ManagedProperty(value = "#{planEmergenteController}")
    @Setter
    protected PlanEmergenteController planEmergenteController;

    @EJB
    protected AccionEmergenteService accionEmergenteService;
    @EJB
    protected EventoOcurridoService eventoOcurridoService;
    @EJB
    protected MedidaCorrectivaService medidaCorrectivaService;
    @EJB
    protected IndicadorMedidaCorrectivaService indicadorMedidaCorrectivaService;

    @Getter
    protected List<EventoOcurrido> eventosOcurridos;
    @Getter
    protected List<AccionEmergente> acciones;
    @Getter
    protected AccionEmergente accionEmergente;
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

    @PostConstruct
    public void init() {
        eventosOcurridos = eventoOcurridoService.buscarPorPlanEmergente(planEmergenteController.getPlanEmergente());
        acciones = accionEmergenteService.buscarPorEventos(eventosOcurridos, AccionEmergente.POR_IMPLEMENTAR);
        medidasCorrectivasBorrar = new ArrayList<>();
        indicadoresBorrar = new ArrayList<>();

        minFechaInicio = dateFormat.format(planEmergenteController.getPlanEmergente().getFechaCreacion());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(planEmergenteController.getPlanEmergente().getFechaCreacion());
        calendar.add(Calendar.DAY_OF_YEAR, 10);
        maxFechaInicio = dateFormat.format(calendar.getTime());

        if(acciones.isEmpty())
            RequestContext.getCurrentInstance().execute("PF('aviso').show();");
    }

    public void mostrar(int pos) {
        accionEmergente = acciones.get(pos);
        medidaCorrectiva = !accionEmergente.getMedidaCorrectivas().isEmpty() ? accionEmergente.getMedidaCorrectivas().get(0) : new MedidaCorrectiva();
        RequestContext.getCurrentInstance().execute("PF('accionEmergentePorImplementarDialog').show();");
    }

    public void editar(ActionEvent event) {
        if(validar(accionEmergente))
            RequestContext.getCurrentInstance().execute("PF('accionEmergentePorImplementarDialog').hide();");
    }

    private boolean validar(AccionEmergente accion) {
        boolean flat = true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(planEmergenteController.getPlanEmergente().getFechaCreacion());
        calendar.add(Calendar.DAY_OF_YEAR, 10);
        if(accion.getMedidaCorrectivas().isEmpty()) {
            JsfUtil.addMessageError("Debe adicionar al menos una medida correctiva para cada evento.");
            flat = false;
        }
        else {
            for (MedidaCorrectiva medida : accion.getMedidaCorrectivas()) {
                if (medida.getIndicadores().isEmpty()) {
                    JsfUtil.addMessageError("Debe adicionar al menos un indicador para cada medida correctiva.");
                    flat = false;
                }
            }
        }
        if(accion.getFechaInicio().before(planEmergenteController.getPlanEmergente().getFechaCreacion())) {
            JsfUtil.addMessageError("La fecha de inicio no debe ser menor que la fecha de creación del plan emergente.");
            flat = false;
        }
        if (accion.getFechaInicio().after(calendar.getTime())) {
            JsfUtil.addMessageError("La fecha de inicio no debe superar los 10 días después de la fecha de creación del plan emergente.");
            flat = false;
        }
        if (accion.getFechaFin().before(accion.getFechaInicio())) {
            JsfUtil.addMessageError("La fecha de fin no debe ser menor que la fecha de inicio.");
            flat = false;
        }
        return flat;
    }

    public void guardar(ActionEvent event) {
        boolean flat = true;
        for (AccionEmergente accion: acciones) {
            flat &= validar(accion);
        }

        if(acciones.isEmpty()) {
            JsfUtil.addMessageError("Debe adicionar al menos un evento.");
            flat = false;
        }

        if(flat) {
            medidaCorrectivaService.eliminar(medidasCorrectivasBorrar);
            indicadorMedidaCorrectivaService.eliminar(indicadoresBorrar);

            for (AccionEmergente accion: acciones) {
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
            planEmergenteController.addSeccionCompletada(PlanEmergenteController.PORIMPLEMENTAR);
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        }
    }

    public void adicionarMedida(ActionEvent event) {
        if(!medida.equals("")) {
            medidaCorrectiva = new MedidaCorrectiva();
            medidaCorrectiva.setAccionEmergente(accionEmergente);
            medidaCorrectiva.setDescripcion(medida);
            medidaCorrectiva.setIndicadores(new ArrayList<IndicadorMedidaCorrectiva>());
            accionEmergente.getMedidaCorrectivas().add(medidaCorrectiva);
            medida = "";
        }
    }

    public void mostrarMedida(int pos) {
        medidaCorrectiva = accionEmergente.getMedidaCorrectivas().get(pos);
        RequestContext.getCurrentInstance().execute("PF('accionEmergentePorImplementarMedidaDialog').show();");
    }

    public void editarMedida(ActionEvent event) {
        RequestContext.getCurrentInstance().execute("PF('accionEmergentePorImplementarMedidaDialog').hide();");
    }

    public void eliminarMedida(int pos) {
        medidasCorrectivasBorrar.add(accionEmergente.getMedidaCorrectivas().get(pos));
        accionEmergente.getMedidaCorrectivas().remove(pos);
    }

    public void cambiarMedida(int pos) {
        medidaCorrectiva = accionEmergente.getMedidaCorrectivas().get(pos);
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
        RequestContext.getCurrentInstance().execute("PF('accionEmergentePorImplementarIndicadorDialog').show();");
    }

    public void editarIndicador(ActionEvent event) {
        RequestContext.getCurrentInstance().execute("PF('accionEmergentePorImplementarIndicadorDialog').hide();");
    }

    public void eliminarIndicador(int pos) {
        indicadoresBorrar.add(medidaCorrectiva.getIndicadores().get(pos));
        medidaCorrectiva.getIndicadores().remove(pos);
    }

    public void cambiarUnidadMedida(AjaxBehaviorEvent event) {
        if ((accionEmergente.getUnidadAreaAfectada() != null && accionEmergente.getUnidadAreaAfectada().equals("No cuantificable"))
                || (accionEmergente.getUnidadAreaRecuperada() != null && accionEmergente.getUnidadAreaRecuperada().equals("No cuantificable"))) {
            accionEmergente.setJustificacion("");
        }
    }

    public void adjuntarMedioVerificacion(ActionEvent event) {

    }

    public void onDateSelect(SelectEvent event) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(accionEmergente.getFechaInicio());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        minFechaFin = dateFormat.format(calendar.getTime());
    }
}
