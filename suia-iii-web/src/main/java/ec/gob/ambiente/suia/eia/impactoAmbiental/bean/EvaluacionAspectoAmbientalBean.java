/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.eia.impactoAmbiental.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.catalogocategorias.facade.CatalogoCategoriasFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.eia.descripcionProyecto.facade.ActividadLicenciamientoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 26/06/2015]
 *          </p>
 */

@ManagedBean
@ViewScoped
public class EvaluacionAspectoAmbientalBean implements Serializable {

	private static final long serialVersionUID = -7595545774016441720L;

    private static final Logger LOG = Logger.getLogger(EvaluacionAspectoAmbientalBean.class);

	@Setter
    private List<ActividadLicenciamiento> actividades;

    @Getter
    @Setter
    private CatalogoCategoriaFase catalogoCategoriaFase;


    @Setter
    private List<CatalogoCategoriaFase> catalogoCategoriaFases;

    @Getter
    @Setter
    EstudioImpactoAmbiental estudio;

    @Getter
    @Setter
    private Fase fase;

    @Getter
    @Setter
    private List<EtapasProyecto> etapasDeFase;

    @Getter
    @Setter
    private EtapasProyecto etapasProyectos;

    @Getter
    @Setter
    private ActividadesPorEtapa actividadPorEtapa;

    @Getter
    @Setter
    private List<ActividadesPorEtapa> actividadesPorEtapas;

    @EJB
    private ActividadLicenciamientoFacade actividadLicenciamientoFacade;
    @EJB
    private CatalogoCategoriasFacade catalogoCategoriasFacade;


    @PostConstruct()
    public void init() {
        try {
            estudio = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
            actividadLicenciamientoFacade.actividadesLicenciamiento(estudio);
			getCatalogoCategoriaFases().addAll(catalogoCategoriasFacade.buscarCatalogoCategoriasFase(estudio));
        } catch (Exception e) {
            LOG.error("No se pudo recuperar la lista de Actividades Licenciamiento", e);
        }
        fase = new Fase();
        etapasDeFase = new ArrayList<>();
        etapasProyectos = new EtapasProyecto();
        actividadPorEtapa = new ActividadesPorEtapa();
        actividadesPorEtapas = new ArrayList<>();
    }

//    @Getter
    @Setter
    private EvaluacionAspectoAmbiental evaluacionAspectoAmbiental;

    @Setter
    private List<EvaluacionAspectoAmbiental> evaluacionAspectoAmbientalLista;

    public EvaluacionAspectoAmbiental getEvaluacionAspectoAmbiental() {
        return evaluacionAspectoAmbiental == null ? evaluacionAspectoAmbiental = new EvaluacionAspectoAmbiental()
                : evaluacionAspectoAmbiental;
    }

    public List<EvaluacionAspectoAmbiental> getEvaluacionAspectoAmbientalLista() {
        return evaluacionAspectoAmbientalLista == null ? evaluacionAspectoAmbientalLista = new ArrayList<EvaluacionAspectoAmbiental>()
                : evaluacionAspectoAmbientalLista;
    }

    public List<ActividadLicenciamiento> getActividades() {
        return actividades == null ? actividades = new ArrayList<ActividadLicenciamiento>() : actividades;
    }

    public List<CatalogoCategoriaFase> getCatalogoCategoriaFases() {
        return catalogoCategoriaFases == null ? catalogoCategoriaFases = new ArrayList<CatalogoCategoriaFase>()
                : catalogoCategoriaFases;
    }
}
