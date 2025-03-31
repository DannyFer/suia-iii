package ec.gob.ambiente.prevencion.categoriaii.mineria.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.ActividadMinera;
import ec.gob.ambiente.suia.domain.CatalogoActividadComercial;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaFase;
import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.CatalogoHerramientaArtesanal;
import ec.gob.ambiente.suia.domain.CatalogoInstalacion;
import ec.gob.ambiente.suia.domain.CatalogoTipoMaterial;
import ec.gob.ambiente.suia.domain.DescripcionActividadMineria;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.FichaMineriaInsumos;
import ec.gob.ambiente.suia.domain.HerramientaMinera;
import ec.gob.ambiente.suia.domain.Instalacion;
import ec.gob.ambiente.suia.domain.ProcesoMinero;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UnidadMedida;

/**
 *
 * @author christian
 */
public class DescripcionActividadMineraBean {

    @Getter
    @Setter
    private DescripcionActividadMineria descripcionActividadMineria;
    
    @Getter
    @Setter
    private FichaAmbientalMineria fichaAmbientalMineria;
    @Getter
    @Setter
    private String etiqueta;
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;
    @Getter
    @Setter
    private List<CatalogoCategoriaFase> catalogoCategoriaFases;
    @Getter
    @Setter
    private CatalogoCategoriaFase faseSeleccionada;
    @Getter
    @Setter
    private List<CatalogoActividadComercial> listaActividades;
    @Getter
    @Setter
    private ActividadMinera actividadMinera;
    @Getter
    @Setter
    private List<ActividadMinera> listaActividadesSeleccionadas;
    @Getter
    @Setter
    private List<ActividadMinera> listaActividadesEliminadas;
    @Getter
    @Setter
    private List<CatalogoTipoMaterial> catalogoTipoObtencion;
    @Getter
    @Setter
    private List<ProcesoMinero> catalogoProcesosMineros;
    @Getter
    @Setter
    private List<CatalogoHerramientaArtesanal> catalogoHerramientas;
    @Getter
    @Setter
    private HerramientaMinera herramientaMinera;
    @Getter
    @Setter
    private List<HerramientaMinera> listaHerramientas;
    @Getter
    @Setter
    private List<HerramientaMinera> listaHerramientasEliminadas;
    @Getter
    @Setter
    private List<CatalogoInstalacion> catalogoInstalaciones;
    @Getter
    @Setter
    private Instalacion instalacion;
    @Getter
    @Setter
    private List<Instalacion> listaInstalaciones;
    @Getter
    @Setter
    private List<Instalacion> listaInstalacionesEliminadas;
    @Getter
    @Setter
    private String idInsumos;
    @Getter
    @Setter
    private String idHijosInsumos;
    @Getter
    @Setter
    private List<SelectItem> listaInsumos;
    @Getter
    @Setter
    private List<SelectItem> listaHijosInsumos;
    @Getter
    @Setter
    private List<CatalogoGeneralFisico> listaCatalogoInsumos;
    @Getter
    @Setter
    private FichaMineriaInsumos fichaMineriaInsumos;
    @Getter
    @Setter
    private List<FichaMineriaInsumos> listaInsumosAgregados;
    @Getter
    @Setter
    private List<FichaMineriaInsumos> listaInsumosEliminados;
    @Getter
    @Setter
    private boolean apareceOtrosInsumos;
    @Getter
    @Setter
    private boolean apareceOtrosHijosInsumos;
    @Getter
    @Setter
    private String numeroColumnas;
    @Getter
    @Setter
    private int operacion;
    @Getter
    @Setter
    private List<UnidadMedida> catalogoUnidadMedida;

    public void iniciarDatos() {
        setDescripcionActividadMineria(null);
        setFichaAmbientalMineria(null);
        setEtiqueta(null);
        setProyecto(null);
        setCatalogoCategoriaFases(null);
        setFaseSeleccionada(null);
        setListaActividades(null);
        setListaActividadesSeleccionadas(new ArrayList<ActividadMinera>());
        setListaActividadesEliminadas(new ArrayList<ActividadMinera>());
        actividadMinera = new ActividadMinera();
        setCatalogoTipoObtencion(null);
        listaHerramientas = new ArrayList<HerramientaMinera>();
        setListaHerramientasEliminadas(new ArrayList<HerramientaMinera>());
        setCatalogoHerramientas(null);
        herramientaMinera = new HerramientaMinera();
        setCatalogoInstalaciones(null);
        listaInstalaciones = new ArrayList<Instalacion>();
        setListaInstalacionesEliminadas(new ArrayList<Instalacion>());
        instalacion = new Instalacion();
        setIdInsumos(null);
        setIdHijosInsumos(null);
        setListaInsumos(null);
        setListaHijosInsumos(null);
        setListaCatalogoInsumos(null);
        setFichaMineriaInsumos(new FichaMineriaInsumos());
        setListaInsumosAgregados(new ArrayList<FichaMineriaInsumos>());
        setListaInsumosEliminados(new ArrayList<FichaMineriaInsumos>());
        setApareceOtrosInsumos(false);
        setApareceOtrosHijosInsumos(false);
        setNumeroColumnas("4");
        setOperacion(0);
        setCatalogoUnidadMedida(null);
    }
}
