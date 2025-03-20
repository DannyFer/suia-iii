package ec.gob.ambiente.suia.eia.mediofisico.controller;

import ec.gob.ambiente.suia.coordenadageneral.facade.CoordenadaGeneralFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.eia.mediofisico.bean.MedioFisicoBean;
import ec.gob.ambiente.suia.eia.mediofisico.facade.FisicoMecanicaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ViewScoped
@ManagedBean
public class FisicoMecanicaController implements Serializable {
    private static final long serialVersionUID = -1636755958728186244L;
    @Getter
    @Setter
    @ManagedProperty(value = "#{medioFisicoBean}")
    private MedioFisicoBean medioFisicoBean;
    @EJB
    private FisicoMecanicaFacade fisicoMecanicaFacade;
    @EJB
    private CoordenadaGeneralFacade coordenadaGeneralFacade;
    @Getter
    @Setter
    private Integer eiaId;

    @EJB
    private ValidacionSeccionesFacade validacionSeccionesFacade;

    @PostConstruct
    public void iniciar() {
        try {
            EstudioImpactoAmbiental eia = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
            setEiaId(eia.getId());

            recuperarFisicoMecanicas();

            medioFisicoBean.setFisicoMecanicaBorradas(new ArrayList<FisicoMecanicaSuelo>());

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void recuperarFisicoMecanicas() {
        List<FisicoMecanicaSuelo> fisicas = this.fisicoMecanicaFacade.fisoMecanicaXEiaId(getEiaId());
        if (!fisicas.isEmpty()) {
            medioFisicoBean.setListaFisicoMecanica(fisicas);
            for (FisicoMecanicaSuelo fisicoMecanicaSuelo : fisicas) {
                CoordenadaGeneral coordenadaGeneral = new CoordenadaGeneral();
                coordenadaGeneral = coordenadaGeneralFacade.coordenadasGeneralXTablaId(fisicoMecanicaSuelo.getId(), FisicoMecanicaSuelo.class.getSimpleName()).get(0);
                fisicoMecanicaSuelo.setCoordenadaGeneral(coordenadaGeneral);
            }
        }
    }

    public void agregarAListaFisicoMecanica() {
        try {
            if (!medioFisicoBean.getFisicoMecanica().isEditar())
                medioFisicoBean.getListaFisicoMecanica().add(medioFisicoBean.getFisicoMecanica());
            RequestContext.getCurrentInstance().addCallbackParam(
                    "fisicoMecanicaIn", true);
        } catch (Exception e) {
            RequestContext.getCurrentInstance().addCallbackParam(
                    "fisicoMecanicaIn", false);
        }
    }

    public void agregarFisicoMecananica() {
        FisicoMecanicaSuelo fisicoMecanica = new FisicoMecanicaSuelo();
        fisicoMecanica.setEditar(false);
        fisicoMecanica.setEiaId(getEiaId());
        medioFisicoBean.setFisicoMecanica(fisicoMecanica);
        medioFisicoBean.getFisicoMecanica().setCoordenadaGeneral(new CoordenadaGeneral());
    }

    public void seleccionarFisicoMecanica(FisicoMecanicaSuelo fisicoMecanica) {
        fisicoMecanica.setEditar(true);
        medioFisicoBean.setFisicoMecanica(fisicoMecanica);
    }

    public void eliminarFisicoMecanica(FisicoMecanicaSuelo fisicoMecanica) {
        medioFisicoBean.getListaFisicoMecanica().remove(fisicoMecanica);
        if (fisicoMecanica.getId() != null) {
            fisicoMecanica.setEstado(false);
            medioFisicoBean.getFisicoMecanicaBorradas().add(fisicoMecanica);
        }
    }

    public void limpiar() {
        medioFisicoBean.setListaFisicoMecanica(new ArrayList<FisicoMecanicaSuelo>());
    }

    public String cancelar() {
        medioFisicoBean.setListaCuerpoHidricos(new ArrayList<CuerpoHidrico>());
        return JsfUtil.actionNavigateTo("/pages/eia/lineaBase/fisicoMecanica.jsf");
    }

    public String guardar() {
        try {
            if (!medioFisicoBean.getListaFisicoMecanica().isEmpty()) {
                EstudioImpactoAmbiental es = new EstudioImpactoAmbiental(getEiaId());
                Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
                fisicoMecanicaFacade.guardarFisicoMecanica(medioFisicoBean.getListaFisicoMecanica(), medioFisicoBean.getFisicoMecanicaBorradas());
                JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);

                recuperarFisicoMecanicas();

                validacionSeccionesFacade.guardarValidacionSeccion("EIA",
                        "fisicoMecanicasSuelos", es.getId().toString());

            } else {
                JsfUtil.addMessageError("Debe ingresar registros.");
            }
        } catch (Exception ex) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO);
        }
        return null;
    }
}
