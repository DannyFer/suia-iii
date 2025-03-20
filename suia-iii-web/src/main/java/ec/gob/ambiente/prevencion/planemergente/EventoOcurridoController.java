package ec.gob.ambiente.prevencion.planemergente;

import java.util.ArrayList;
import java.util.List;

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

import ec.gob.ambiente.suia.comun.bean.CargarCoordenadasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.AccionEmergente;
import ec.gob.ambiente.suia.domain.EtapasProyecto;
import ec.gob.ambiente.suia.domain.EventoCoordenada;
import ec.gob.ambiente.suia.domain.EventoEtapaFase;
import ec.gob.ambiente.suia.domain.EventoOcurrido;
import ec.gob.ambiente.suia.domain.Fase;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.planemergente.service.AccionEmergenteService;
import ec.gob.ambiente.suia.prevencion.planemergente.service.EventoCoordenadaService;
import ec.gob.ambiente.suia.prevencion.planemergente.service.EventoOcurridoService;
import ec.gob.ambiente.suia.prevencion.planemergente.service.TipoSectorFaseService;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.ubicaciongeografica.service.UbicacionGeograficaServiceBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * Created by msit-erislan on 06/11/2015.
 */
@ManagedBean
@ViewScoped
public class EventoOcurridoController {

    @ManagedProperty(value = "#{planEmergenteController}")
    @Setter
    private PlanEmergenteController planEmergenteController;
    @ManagedProperty(value = "#{cargarCoordenadasBean}")
    @Setter
    @Getter
    private CargarCoordenadasBean cargarCoordenadasBean;

    @EJB
    private EventoOcurridoService eventoOcurridoService;
    @EJB
    private UbicacionGeograficaServiceBean ubicacionGeograficaServiceBean;
    @EJB
    private EventoCoordenadaService eventoCoordenadaService;
    @EJB
    private AccionEmergenteService accionEmergenteService;
    @EJB
    private TipoSectorFaseService tipoSectorFaseService;
    @EJB
    private CrudServiceBean crudServiceBean;

    @Getter
    private List<EventoOcurrido> eventosOcurridos, eventosBorrados;
    @Getter
    @Setter
    private EventoOcurrido eventoOcurrido;
    @Getter
    private List<UbicacionesGeografica> provincias, cantones, parroquias;
    @Getter
    @Setter
    private int provincia, canton, parroquia;
    private List<EventoCoordenada> coordenadasBorradas;
    @Getter
    @Setter
    private Integer[] eventoEtapaFase;
    @Getter
    private List<Fase> fases;
    private boolean nuevo;
    private List<EventoEtapaFase> eventoEtapaFasesBorradas;

    @PostConstruct
    public void init() {
        eventosBorrados = new ArrayList<>();
        coordenadasBorradas = new ArrayList<>();
        eventoEtapaFasesBorradas = new ArrayList<>();
        planEmergenteController.actualizar();
        eventosOcurridos = eventoOcurridoService.buscarPorPlanEmergente(planEmergenteController.getPlanEmergente());
        if(!eventosOcurridos.isEmpty()) {
            planEmergenteController.addSeccionCompletada(PlanEmergenteController.EVENTOOCURRIDO);
            planEmergenteController.addSeccionCompletada(PlanEmergenteController.LEVANTAMIENTO);
        }

        List<AccionEmergente> accionImpl = accionEmergenteService.buscarPorEventos(eventosOcurridos, AccionEmergente.YA_IMPLEMENTADA);
        boolean accionImplB = false;
        for (AccionEmergente accionEmergente : accionImpl) {
            accionImplB = true;
            if(accionEmergente.getMedidaCorrectivas().isEmpty()) {
                accionImplB = false;
                break;
            }
        }
        if(accionImplB) planEmergenteController.addSeccionCompletada(PlanEmergenteController.IMPLEMENTADAS);

        List<AccionEmergente> accionPorImpl = accionEmergenteService.buscarPorEventos(eventosOcurridos, AccionEmergente.POR_IMPLEMENTAR);
        boolean accionPorImplB = false;;
        for (AccionEmergente accionEmergente : accionPorImpl) {
            accionPorImplB = true;
            if(accionEmergente.getMedidaCorrectivas().isEmpty()) {
                accionPorImplB = false;
                 break;
            }
        }
        if(accionPorImplB) planEmergenteController.addSeccionCompletada(PlanEmergenteController.PORIMPLEMENTAR);

        nuevo = false;
        provincias = ubicacionGeograficaServiceBean.buscarUbicacionGeograficaPorPadre(new UbicacionesGeografica(1));
        provincia = provincias.isEmpty() ? 0 : provincias.get(0).getId();
        fases = new ArrayList<>();
    }

    public void cambiarCantones(AjaxBehaviorEvent event) {
        cantones = ubicacionGeograficaServiceBean.buscarUbicacionGeograficaPorPadre(new UbicacionesGeografica(provincia));
        canton = cantones.isEmpty() ? 0 : cantones.get(0).getId();
        cambiarParroquias(event);
    }

    public void cambiarParroquias(AjaxBehaviorEvent event) {
        parroquias = ubicacionGeograficaServiceBean.buscarUbicacionGeograficaPorPadre(new UbicacionesGeografica(canton));
        parroquia = parroquias.isEmpty() ? 0 : parroquias.get(0).getId();
    }

    public void nuevo(ActionEvent event) {
        eventoOcurrido = new EventoOcurrido();
        eventoOcurrido.setCoordenadas(new ArrayList<EventoCoordenada>());
        eventoOcurrido.setEtapas(new ArrayList<EventoEtapaFase>());
        eventoOcurrido.setMeridiano("a");
        nuevo = true;
        eventoOcurrido.setPlanEmergente(planEmergenteController.getPlanEmergente());
        if(planEmergenteController.isRegularizacion()) {
            eventoOcurrido.setProyectoLicenciamientoAmbiental(planEmergenteController.getProyectoLicenciamientoAmbiental());
            if(planEmergenteController.isHidrocarburos()) {
                EventoEtapaFase eventoEtapaFase = new EventoEtapaFase();
                eventoEtapaFase.setEventoOcurrido(eventoOcurrido);
                eventoEtapaFase.setFase(eventoOcurrido.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getFase());
                eventoOcurrido.getEtapas().add(eventoEtapaFase);
            }
        } else {
            if(planEmergenteController.isHidrocarburos()) {
                EventoEtapaFase eventoEtapaFase = new EventoEtapaFase();
                eventoEtapaFase.setEventoOcurrido(eventoOcurrido);
                eventoEtapaFase.setFase(eventoOcurrido.getPlanEmergente().getCatalogoCategoria ().getFase());
                eventoOcurrido.getEtapas().add(eventoEtapaFase);
            }
        }
        cambiarCantones(null);
        RequestContext.getCurrentInstance().execute("PF('eventoOcurridoDialog').show();");
    }

    public void adicionar(ActionEvent event) {
        if(validar()) {
            if(nuevo) eventosOcurridos.add(eventoOcurrido);
            nuevo = false;
            try { eventoOcurrido.setParroquia(ubicacionGeograficaServiceBean.buscarPorId(parroquia));
            } catch (ServiceException e) { e.printStackTrace(); }
            RequestContext.getCurrentInstance().execute("PF('eventoOcurridoDialog').hide();");
        }
    }

    public void eliminarEvento(int pos) {
        eventosBorrados.add(eventosOcurridos.get(pos));
        eventosOcurridos.remove(pos);
    }

    public void editarEvento(int pos) {
        eventoOcurrido = eventosOcurridos.get(pos);
        parroquia = eventoOcurrido.getParroquia() == null ? 0 : eventoOcurrido.getParroquia().getId();
        if(parroquia != 0) {
            try {
                canton = ubicacionGeograficaServiceBean.buscarPorId(parroquia).getUbicacionesGeografica().getId();
                provincia = ubicacionGeograficaServiceBean.buscarPorId(canton).getUbicacionesGeografica().getId();
                cantones = ubicacionGeograficaServiceBean.buscarUbicacionGeograficaPorPadre(new UbicacionesGeografica(provincia));
                parroquias = ubicacionGeograficaServiceBean.buscarUbicacionGeograficaPorPadre(new UbicacionesGeografica(canton));
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
        RequestContext.getCurrentInstance().execute("PF('eventoOcurridoDialog').show();");
    }

    public void handleFileUpload(final FileUploadEvent event) {
        cargarCoordenadasBean.handleFileUpload(event);
        List<CoordinatesWrapper> coordinatesWrappers = cargarCoordenadasBean.getCoordinatesWrappers();
        if(!coordinatesWrappers.isEmpty()) {
            coordenadasBorradas.addAll(eventoOcurrido.getCoordenadas());
            eventoOcurrido.setCoordenadas(new ArrayList<EventoCoordenada>());
            for (CoordinatesWrapper wrapper : coordinatesWrappers) {
                eventoOcurrido.getCoordenadas().add(new EventoCoordenada(wrapper, eventoOcurrido));
            }
            RequestContext.getCurrentInstance().execute("PF('adjuntarCoordenadas').hide();");
        }
    }

    public void guardar(ActionEvent event) {
        if(eventosOcurridos.size() != 0) {
            eventoCoordenadaService.borrarSiExiste(coordenadasBorradas);
            coordenadasBorradas.clear();

            eventoOcurridoService.borrarEtapasSiExisten(eventoEtapaFasesBorradas);
            eventoEtapaFasesBorradas.clear();

            eventoOcurridoService.borrarSiExiste(eventosBorrados);
            eventosBorrados.clear();

            for (EventoOcurrido eventoOcurrido1: eventosOcurridos) {
                List<EventoCoordenada> coordenadas = eventoOcurrido1.getCoordenadas();
                List<EventoEtapaFase> etapas = eventoOcurrido1.getEtapas();
                eventoOcurrido1 = eventoOcurridoService.crear(eventoOcurrido1);
                coordenadas = eventoCoordenadaService.crear(coordenadas);
                etapas = eventoOcurridoService.crearEtapas(etapas);
                eventoOcurrido1.setCoordenadas(coordenadas);
                eventoOcurrido1.setEtapas(etapas);
            }

            accionEmergenteService.crear(eventosOcurridos);
            if(planEmergenteController.isRegularizacion())
                planEmergenteController.createPlanConRegularizacion();
            planEmergenteController.addSeccionCompletada(PlanEmergenteController.EVENTOOCURRIDO);
            planEmergenteController.addSeccionCompletada(PlanEmergenteController.LEVANTAMIENTO);
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        } else {
            JsfUtil.addMessageError("Debe adicionar al menos un evento.");
        }
    }

    public void mostrarFases(ActionEvent event) {
        fases = new ArrayList<>();
        if(planEmergenteController.isRegularizacion()) {
            if(planEmergenteController.isHidrocarburos()) {
                fases.add(planEmergenteController.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getFase());
            } else {
                fases = tipoSectorFaseService.findFaseByTipoSector(planEmergenteController.getProyectoLicenciamientoAmbiental().getTipoSector());
            }
        }
        else {
            if(planEmergenteController.isHidrocarburos()) {
                fases.add(planEmergenteController.getPlanEmergente().getCatalogoCategoria().getFase());
            } else {
                fases = tipoSectorFaseService.findFaseByTipoSector(planEmergenteController.getPlanEmergente().getTipoSector());
            }
        }
        eventoEtapaFase = new Integer[eventoOcurrido.getEtapas().size()];
        for (int i = 0; i < eventoOcurrido.getEtapas().size(); i++) {
            EventoEtapaFase etapaFase = eventoOcurrido.getEtapas().get(i);
            if(etapaFase.getEtapa() != null) {
                eventoEtapaFase[i] = etapaFase.getEtapa().getId();
            } else {
                eventoEtapaFase[i] = etapaFase.getFase().getId();
            }
        }
        RequestContext.getCurrentInstance().execute("PF('seleccionarFase').show();");
    }

    public void adicionarFases(ActionEvent event) {
        eventoEtapaFasesBorradas.addAll(eventoOcurrido.getEtapas());
        eventoOcurrido.setEtapas(new ArrayList<EventoEtapaFase>());
        for (Integer id : eventoEtapaFase) {
            EventoEtapaFase eventoEtapaFase = new EventoEtapaFase();
            eventoEtapaFase.setEventoOcurrido(eventoOcurrido);
            if(planEmergenteController.isHidrocarburos() || planEmergenteController.isMineria()) {
                eventoEtapaFase.setFase((Fase) crudServiceBean.find(Fase.class, id));
            }
            else {
                eventoEtapaFase.setFase(fases.get(0));
                eventoEtapaFase.setEtapa((EtapasProyecto) crudServiceBean.find(EtapasProyecto.class, id));
            }
            eventoOcurrido.getEtapas().add(eventoEtapaFase);
        }
        RequestContext.getCurrentInstance().execute("PF('seleccionarFase').hide();");
    }

    private boolean validar() {
        boolean flat = true;
        if(eventoOcurrido.getCoordenadas().isEmpty()) {
            JsfUtil.addMessageError("Introduzca las coordenadas del evento.");
            flat = false;
        } if(eventoOcurrido.getEtapas().isEmpty()) {
            JsfUtil.addMessageError("Seleccione en que fase/etapa ocurrió el evento.");
            flat = false;
        } if((eventoOcurrido.getMinuto() > 59) || (eventoOcurrido.getHora() > 12) || (eventoOcurrido.getHora() == 0)) {
            JsfUtil.addMessageErrorForComponent("hora", "Introduzca una hora válida.");
            flat = false;
        }
        return flat;
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(planEmergenteController.getBandejaTareasBean().getTarea(), "/prevencion/planemergente/ingresarinformacion/eventoOcurrido.jsf");
    }

}
