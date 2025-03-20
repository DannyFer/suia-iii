package ec.gob.ambiente.prevencion.categoriaii.mineria.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author lili
 *
 */
public class MuestreoInicialLineaBaseBean implements Serializable {

    private static final long serialVersionUID = -2229275902702309360L;
    @Getter
    @Setter
    private FichaAmbientalMineria fichaAmbientalMineria;
    @Getter
    @Setter
    private List<SelectItem> listaFormacionVegetal;
    @Getter
    @Setter
    private Integer idVegetal;
    @Getter
    @Setter
    private List<SelectItem> listaHabitat;
    @Getter
    @Setter
    private List<SelectItem> listaTipoBosque;
    @Getter
    @Setter
    private List<SelectItem> listaGradoIntervencion;
    @Getter
    @Setter
    private List<SelectItem> listaAreasIntervenidas;
    @Getter
    @Setter
    private List<SelectItem> listaDatosEcologicos;
    @Getter
    @Setter
    private List<SelectItem> listaUsoRecurso;
    @Getter
    @Setter
    private Integer idHabitat;
    @Getter
    @Setter
    private Integer idTipoBosque;
    @Getter
    @Setter
    private Integer idGradoIntervencion;
    @Getter
    @Setter
    private String[] opcionesAreasIntervenidas;
    @Getter
    @Setter
    private String[] opcionesDatosEcologicos;
    @Getter
    @Setter
    private String[] opcionesUsoRecurso;
    @Getter
    @Setter
    private List<SelectItem> listaPisoZoogeofrafico;
    @Getter
    @Setter
    private List<SelectItem> listaComponenteBiotico;
    @Getter
    @Setter
    private List<SelectItem> listaSensibilidad;
    @Getter
    @Setter
    private List<SelectItem> listaDatosEcologicosFauna;
    @Getter
    @Setter
    private List<SelectItem> listaUsoRecursoFauna;
    @Getter
    @Setter
    private Integer idPisoZoogeografico;
    @Getter
    @Setter
    private String[] opcionesComponenteBiotico;
    @Getter
    @Setter
    private String[] opcionesSensibilidad;
    @Getter
    @Setter
    private String[] opcionesDatosEcologicosFauna;
    @Getter
    @Setter
    private String[] opcionesUsoRecursoFauna;
    @Getter
    @Setter
    private List<SelectItem> listaNivelAreaInfluencia;
    @Getter
    @Setter
    private Integer idNivelAreaInfluencia;
    @Getter
    @Setter
    private List<SelectItem> listaTamanioPoblacion;
    @Getter
    @Setter
    private Integer idTamanioPoblacion;
    @Getter
    @Setter
    private List<SelectItem> listaComposicionEtnica;
    @Getter
    @Setter
    private String[] opcionesComposicionEtnica;
    @Getter
    @Setter
    private List<SelectItem> listaAbastecimientoAgua;
    @Getter
    @Setter
    private Integer idAbastecimientoAgua;
    @Getter
    @Setter
    private List<SelectItem> listaEvacuacionAguasServidas;
    @Getter
    @Setter
    private Integer idEvacuacionAguasServidas;
    @Getter
    @Setter
    private List<SelectItem> listaEvacuacionAguasLluvia;
    @Getter
    @Setter
    private Integer idEvacuacionAguasLluvia;
    @Getter
    @Setter
    private List<SelectItem> listaDesechosSolidos;
    @Getter
    @Setter
    private Integer idDesechosSolidos;
    @Getter
    @Setter
    private List<SelectItem> listaElectrificacion;
    @Getter
    @Setter
    private Integer idElectrificacion;
    @Getter
    @Setter
    private List<SelectItem> listaTransportePublico;
    @Getter
    @Setter
    private Integer idTransportePublico;
    @Getter
    @Setter
    private List<SelectItem> listaVialidad;
    @Getter
    @Setter
    private Integer idVialidad;
    @Getter
    @Setter
    private List<SelectItem> listaTelefonia;
    @Getter
    @Setter
    private Integer idTelefonia;
    @Getter
    @Setter
    private List<SelectItem> listaAprovechamientoTierra;
    @Getter
    @Setter
    private Integer idAprovechamientoTierra;
    @Getter
    @Setter
    private List<SelectItem> listaTenenciaTierra;
    @Getter
    @Setter
    private Integer idTenenciaTierra;
    @Getter
    @Setter
    private List<SelectItem> listaOrganizacionSocial;
    @Getter
    @Setter
    private Integer idOrganizacionSocial;
    @Getter
    @Setter
    private List<SelectItem> listaLengua;
    @Getter
    @Setter
    private String[] opcionesLengua;
    @Getter
    @Setter
    private List<SelectItem> listaReligion;
    @Getter
    @Setter
    private String[] opcionesReligion;
    @Getter
    @Setter
    private List<SelectItem> listaTradiciones;
    @Getter
    @Setter
    private String[] opcionesTradiciones;
    @Getter
    @Setter
    private List<SelectItem> listaPaisaje;
    @Getter
    @Setter
    private Integer idPaisaje;
    @Getter
    @Setter
    private List<SelectItem> listaPeligroDeslizamiento;
    @Getter
    @Setter
    private String idPeligroDeslizamiento;
    @Getter
    @Setter
    private List<SelectItem> listaPeligroInundaciones;
    @Getter
    @Setter
    private String idPeligroInundaciones;
    @Getter
    @Setter
    private List<SelectItem> listaPeligroTerremoto;
    @Getter
    @Setter
    private String idPeligroTerremoto;
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;
    
    //MarielaG para historial
    @Getter
    @Setter
    private FichaAmbientalMineria fichaAmbientalMineriaOriginal;
    @Getter
    @Setter
    private List<FichaAmbientalMineria> listaHistorialFormacionVegetal, listaHistorialFlora, listaHistorialFaunaSilvestre, 
    listaHistorialDemografia, listaHistorialInfraestructuraSocial, listaHistorialSocioEconomicas, listaHistorialAspectosCulturales, 
    listaHistorialMedioPercentual, listaHistorialRiesgosNaturales, listaHistorialFicha;
    
    @Getter
    @Setter
    private String nombreHistorial;
    
    @Getter
    @Setter
    private Integer opcionHistorial, tamanioModal;
    
    @Getter
    @Setter
    private Boolean otrosUsosFlora, otrosDesechoSolido, otrosTransportePublico, otrosVialidad, otrosAprovechamientoTierra, otrosLengua, otrosReligion, otrosTradiciones, otrosPaisaje, otrosComposicionEtnica, otrosOrganizacionSocial;
    //MarielaG fin historial
    
    public void inicializar() {
        setFichaAmbientalMineria(null);
        setIdVegetal(null);
        setIdHabitat(null);
        setIdTipoBosque(null);
        setIdGradoIntervencion(null);
        setIdPisoZoogeografico(null);
        setIdNivelAreaInfluencia(null);
        setIdTamanioPoblacion(null);
        setIdAbastecimientoAgua(null);
        setIdEvacuacionAguasServidas(null);
        setIdEvacuacionAguasServidas(null);
        setIdDesechosSolidos(null);
        setIdElectrificacion(null);
        setIdTransportePublico(null);
        setIdVialidad(null);
        setIdTelefonia(null);
        setIdAprovechamientoTierra(null);
        setIdTenenciaTierra(null);
        setIdOrganizacionSocial(null);
        setIdPaisaje(null);
        setIdPeligroDeslizamiento(null);
        setIdPeligroInundaciones(null);
        setIdPeligroTerremoto(null);

        setListaFormacionVegetal(new ArrayList());
        setListaHabitat(new ArrayList());
        setListaTipoBosque(new ArrayList());
        setListaGradoIntervencion(new ArrayList());
        setListaAreasIntervenidas(new ArrayList());
        setListaDatosEcologicos(new ArrayList());
        setListaUsoRecurso(new ArrayList());

        setListaPisoZoogeofrafico(new ArrayList());
        setListaComponenteBiotico(new ArrayList());
        setListaSensibilidad(new ArrayList());
        setListaDatosEcologicosFauna(new ArrayList());
        setListaUsoRecursoFauna(new ArrayList());

        setListaNivelAreaInfluencia(new ArrayList());
        setListaTamanioPoblacion(new ArrayList());
        setListaComposicionEtnica(new ArrayList());

        setListaAbastecimientoAgua(new ArrayList());
        setListaEvacuacionAguasServidas(new ArrayList());
        setListaEvacuacionAguasLluvia(new ArrayList());
        setListaDesechosSolidos(new ArrayList());
        setListaElectrificacion(new ArrayList());
        setListaTransportePublico(new ArrayList());
        setListaVialidad(new ArrayList());
        setListaTelefonia(new ArrayList());

        setListaAprovechamientoTierra(new ArrayList());
        setListaTenenciaTierra(new ArrayList());
        setListaOrganizacionSocial(new ArrayList());

        setListaLengua(new ArrayList());
        setListaReligion(new ArrayList());
        setListaTradiciones(new ArrayList());

        setListaPaisaje(new ArrayList());
        setListaPeligroDeslizamiento(new ArrayList());
        setListaPeligroInundaciones(new ArrayList());
        setListaPeligroTerremoto(new ArrayList());

        setOpcionesAreasIntervenidas(null);
        setOpcionesDatosEcologicos(null);
        setOpcionesUsoRecurso(null);
        setOpcionesComponenteBiotico(null);
        setOpcionesSensibilidad(null);
        setOpcionesDatosEcologicosFauna(null);
        setOpcionesUsoRecursoFauna(null);

        setOpcionesComposicionEtnica(null);
        setOpcionesLengua(null);
        setOpcionesReligion(null);
        setOpcionesTradiciones(null);

        ////MarielaG para historial
        setListaHistorialFormacionVegetal(new ArrayList());
        setListaHistorialFlora(new ArrayList());
        setListaHistorialFaunaSilvestre(new ArrayList());
        setListaHistorialDemografia(new ArrayList());
        setListaHistorialInfraestructuraSocial(new ArrayList());
        setListaHistorialSocioEconomicas(new ArrayList());
        setListaHistorialAspectosCulturales(new ArrayList());
        setListaHistorialMedioPercentual(new ArrayList());
        setListaHistorialRiesgosNaturales(new ArrayList());
        
    }
}
