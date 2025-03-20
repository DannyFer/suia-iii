package ec.gob.ambiente.prevencion.categoria2.bean;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.*;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import java.io.Serializable;
import java.util.*;

/**
 * @author Frank Torres
 */
@ManagedBean
@ViewScoped
public class DescripcionAreaImplantacionPmaBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7543750361248602739L;

    private static final Logger LOGGER = Logger
            .getLogger(DescripcionAreaImplantacionPmaBean.class);
    @Getter
    public final String seccionDescipcionArea = "7";


    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

    @EJB
    private CatalogoFisicoFacade catalogoFisicoFacade;
    @EJB
    private CatalogoSocialFacade catalogoSocialFacade;
    @EJB
    private CatalogoBioticoFacade catalogoBioticoFacade;
    @EJB
    private CategoriaIICatalogoFisicoFacade categoriaIICatalogoFisicoFacade;
    @EJB
    private CategoriaIICatalogoSocialFacade categoriaIICatalogoSocialFacade;
    @EJB
    private CategoriaIICatalogoBioticoFacade categoriaIICatalogoBioticoFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;


    @Getter
    @Setter
    private FichaAmbientalPma fichaAmbientalPma;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;

    @Getter
    @Setter
    private List<CatalogoGeneralFisico> clima;

    @Getter
    @Setter
    private List<CatalogoGeneralFisico> tipoSuelo;
    @Getter
    @Setter
    private CatalogoGeneralFisico[] tipoSueloSeleccionado;

    @Getter
    @Setter
    private List<CatalogoGeneralFisico> pendienteSuelo;
    @Getter
    @Setter
    private CatalogoGeneralFisico[] pendienteSueloSeleccionado;

    @Getter
    @Setter
    private List<CatalogoGeneralFisico> condicionesDrenaje;

    // social

    @Getter
    @Setter
    private List<CatalogoGeneralSocial> demografia;
    @Getter
    @Setter
    private CatalogoGeneralSocial[] demografiaSeleccionado;

    @Getter
    @Setter
    private List<CatalogoGeneralSocial> abastecimientoAguaPoblacion;
    @Getter
    @Setter
    private CatalogoGeneralSocial[] abastecimientoAguaPoblacionSeleccionado;

    @Getter
    @Setter
    private List<CatalogoGeneralSocial> evacuacionAguasServidasPonlacion;

    @Getter
    @Setter
    private CatalogoGeneralSocial[] evacuacionAguasServidasPonlacionSeleccionado;

    @Getter
    @Setter
    private List<CatalogoGeneralSocial> electrificacion;
    @Getter
    @Setter
    private CatalogoGeneralSocial[] electrificacionSeleccionado;

    @Getter
    @Setter
    private List<CatalogoGeneralSocial> vialidadAccesoPoblacion;

    @Getter
    @Setter
    private CatalogoGeneralSocial[] vialidadAccesoPoblacionSeleccionado;

    @Getter
    @Setter
    private List<CatalogoGeneralSocial> organizacionSocial;
    @Getter
    @Setter
    private CatalogoGeneralSocial[] organizacionSocialSeleccionado;

    // biotico
    @Getter
    @Setter
    private List<CatalogoGeneralBiotico> formacionVegetal;
    @Getter
    @Setter
    private CatalogoGeneralBiotico[] formacionVegetalSeleccionado;

    @Getter
    @Setter
    private List<CatalogoGeneralBiotico> habitat;
    @Getter
    @Setter
    private CatalogoGeneralBiotico[] habitatSeleccionado;

    @Getter
    @Setter
    private List<CatalogoGeneralBiotico> tiposBosques;
    @Getter
    @Setter
    private CatalogoGeneralBiotico tiposBosquesActivo;

    @Getter
    @Setter
    private List<CatalogoGeneralBiotico> gradoIntervencionCoberturaVegetal;
    @Getter
    @Setter
    private CatalogoGeneralBiotico gradoIntervencionCoberturaVegetalActivo;

    @Getter
    @Setter
    private List<CatalogoGeneralBiotico> aspectosEcologicos;
    @Getter
    @Setter
    private CatalogoGeneralBiotico[] aspectosEcologicosSeleccionado;

    @Getter
    @Setter
    private List<CatalogoGeneralBiotico> pisoZoogeografico;

    @Getter
    @Setter
    private List<CatalogoGeneralBiotico> gruposFaunistico;
    @Getter
    @Setter
    private CatalogoGeneralBiotico[] gruposFaunisticoSeleccionado;
    @Getter
    @Setter
    private CatalogoGeneralBiotico gruposFaunisticoNinguno;
    @Getter
    @Setter
    private List<CatalogoGeneralBiotico> sensibilidadPresentadaArea;
    @Getter
    @Setter
    private CatalogoGeneralBiotico[] sensibilidadPresentadaAreaSeleccionado;
    @Getter
    @Setter
    private List<CatalogoGeneralBiotico> datosEcologicosPresentes;
    @Getter
    @Setter
    private CatalogoGeneralBiotico[] datosEcologicosPresentesSeleccionado;

    // /----
    @Getter
    @Setter
    private CategoriaIICatalogoGeneralFisico tipoSueloOtros;
    @Getter
    @Setter
    private CategoriaIICatalogoGeneralSocial electrificacionOtros;
    @Getter
    @Setter
    private CategoriaIICatalogoGeneralSocial vialidadAccesoPoblacionOtros;

    // Seleccionados//
    @Getter
    @Setter
    private CatalogoGeneralFisico climaActivo;

    @Getter
    @Setter
    private CatalogoGeneralFisico condicionesDrenajeActivo;

    @Getter
    @Setter
    private CatalogoGeneralSocial demografiaActivo;

    @Getter
    @Setter
    private CatalogoGeneralBiotico pisoZoogeograficoActivo;

    // /
    @Getter
    @Setter
    private Boolean climaEditable;

    @Getter
    @Setter
    private Boolean sectorHidrocarburos;
    @Getter
    @Setter
    private Boolean sectorPesca;
    @Getter
    @Setter
    private Boolean sectorPescaMaricultura;
    @Getter
    @Setter
    private Boolean otrosSectores;
    @Getter
    @Setter
    private Boolean agroindustria;
    @Getter
    @Setter
    private Boolean mineriaLibreAprovechamiento;

    @Getter
    @Setter
    private Boolean electTelecomunicaciones;
    @Getter
    @Setter
    private Boolean saneamiento;
    @Getter
    @Setter
    private Boolean saneamientoUrbano;

    @Getter
    @Setter
    private Boolean urbano;

    @Getter
    @Setter
    private Boolean pecuario;

    @Getter
    @Setter
    private Boolean agricola;

    @Getter
    @Setter
    private Boolean gruposFaunisticoNingunoActivo = false;


    @Getter
    @Setter
    private Boolean validado = false;
    private Map<String, List<CatalogoGeneralFisico>> listaGeneralFisico;
    private Map<String, List<CatalogoGeneralSocial>> listaGeneralSocial;
    private Map<String, List<CatalogoGeneralBiotico>> listaGeneralBiotico;
    private Map<String, List<CategoriaIICatalogoGeneralFisico>> listaGeneralCatalogoFisico;
    private Map<String, List<CategoriaIICatalogoGeneralSocial>> listaGeneralCatalogoSocial;
    private Map<String, List<CategoriaIICatalogoGeneralBiotico>> listaGeneralCatalogoBiotico;
    
    /**
     * Cris F: variables historico
     */
    @Getter
    @Setter
    private List<CategoriaIICatalogoGeneralFisico> listaGeneralFisicoHistorico = new ArrayList<CategoriaIICatalogoGeneralFisico>();    
    
    @Getter
    @Setter
    private List<CategoriaIICatalogoGeneralFisico> listaClimaHistorico, listaTipoSueloHistorico, listaPendienteSueloHistorico, listaCondicionesDrenajeHistorico;
    
    @Getter
    @Setter
    private List<CategoriaIICatalogoGeneralSocial> listaGeneralSocialHistorico, listaDemografiaHistorico, listaAbastecimientoAguaHistorico, listaEvacuacionAguasHistorico,
    		listaElectrificacionHistorico, listaVialidadAccesoPoblacionHistorico, listaOrganizacionSocialHistorico;
        
    @Getter
    @Setter
    private List<CategoriaIICatalogoGeneralBiotico> listaGeneralBioticoHistorico, listaFormacionVegetalHistorico, listaHabitatHistorico, 
    listaTiposBosquesHistorico, listaGradoIntervencionHistorico, listaAspectosEcologicosHistorico,
    		listaPisoZoogeologicoHistorico, listaGrupoFaunisticoHistorico, listaSensibilidadPresentadaAreaHistorico, listaDatosEcologicosPresentesHistorico;
    
    private Map<String, List<CategoriaIICatalogoGeneralFisico>> listaGeneralCatalogoFisicoHistorico;
    private Map<String, List<CategoriaIICatalogoGeneralSocial>> listaGeneralCatalogoSocialHistorico;
    private Map<String, List<CategoriaIICatalogoGeneralBiotico>> listaGeneralCatalogoBioticoHistorico;
    
    @PostConstruct
    public void init() {
        inicializarFichaTecnica();
        inicializarDatosGeneralesFisico();
        inicializarDatosGeneralesSocial();
        inicializarDatosGeneralesBiotico();
        inicializarClima();
        tipoSuelo = inicializarFisico(TipoCatalogo.CODIGO_TIPO_SUELO);
        tipoSueloSeleccionado = inicializarFisicoSeleccionados(
                TipoCatalogo.CODIGO_TIPO_SUELO, tipoSuelo);

        tipoSueloOtros = inicializarFisicoOtros(tipoSuelo,
                TipoCatalogo.CODIGO_TIPO_SUELO);

        pendienteSuelo = inicializarFisico(TipoCatalogo.CODIGO_PENDIENTE_SUELO);

        pendienteSueloSeleccionado = inicializarFisicoSeleccionados(
                TipoCatalogo.CODIGO_PENDIENTE_SUELO, pendienteSuelo);

        condicionesDrenaje = inicializarFisico(TipoCatalogo.CODIGO_CONDICIONES_DRENAJE);
//        condicionesDrenajeActivo = inicializarFisicoSeleccionadosSimple(
//                TipoCatalogo.CODIGO_CONDICIONES_DRENAJE, condicionesDrenaje);

        // social
        demografia = inicializarSocial(TipoCatalogo.CODIGO_DEMOGRAFIA);
        demografiaActivo = inicializarSocialSeleccionadosSimple(
                TipoCatalogo.CODIGO_DEMOGRAFIA, demografia);
        demografiaSeleccionado = inicializarSocialSeleccionados(
                TipoCatalogo.CODIGO_DEMOGRAFIA, demografia);

        abastecimientoAguaPoblacion = inicializarSocial(TipoCatalogo.CODIGO_ABASTECIMIENTO_AGUA);
        abastecimientoAguaPoblacionSeleccionado = inicializarSocialSeleccionados(
                TipoCatalogo.CODIGO_ABASTECIMIENTO_AGUA,
                abastecimientoAguaPoblacion);

        evacuacionAguasServidasPonlacion = inicializarSocial(TipoCatalogo.CODIGO_EVACUACION_AGUAS_SERVIDAS_POBLACION);
        evacuacionAguasServidasPonlacionSeleccionado = inicializarSocialSeleccionados(
                TipoCatalogo.CODIGO_EVACUACION_AGUAS_SERVIDAS_POBLACION,
                evacuacionAguasServidasPonlacion);

        electrificacion = inicializarSocial(TipoCatalogo.CODIGO_ELECTRIFICACION);
        electrificacionSeleccionado = inicializarSocialSeleccionados(
                TipoCatalogo.CODIGO_ELECTRIFICACION, electrificacion);
        electrificacionOtros = inicializarSocialOtros(electrificacion,
                TipoCatalogo.CODIGO_ELECTRIFICACION);

        vialidadAccesoPoblacion = inicializarSocial(TipoCatalogo.CODIGO_VIALIDAD_ACCESO_POBLACION);
        vialidadAccesoPoblacionSeleccionado = inicializarSocialSeleccionados(
                TipoCatalogo.CODIGO_VIALIDAD_ACCESO_POBLACION,
                vialidadAccesoPoblacion);
        vialidadAccesoPoblacionOtros = inicializarSocialOtros(
                vialidadAccesoPoblacion,
                TipoCatalogo.CODIGO_VIALIDAD_ACCESO_POBLACION);

        organizacionSocial = inicializarSocial(TipoCatalogo.CODIGO_ORGANIZACION_SOCIAL);
        organizacionSocialSeleccionado = inicializarSocialSeleccionados(
                TipoCatalogo.CODIGO_ORGANIZACION_SOCIAL, organizacionSocial);

        // biotico
        formacionVegetal = inicializarBiotico(TipoCatalogo.CODIGO_FORMACION_VEGETAL);
        formacionVegetalSeleccionado = inicializarBioticoSeleccionados(
                TipoCatalogo.CODIGO_FORMACION_VEGETAL, formacionVegetal);

        habitat = inicializarBiotico(TipoCatalogo.CODIGO_HABITAT);
        habitatSeleccionado = inicializarBioticoSeleccionados(
                TipoCatalogo.CODIGO_HABITAT, habitat);

        tiposBosques = inicializarBiotico(TipoCatalogo.CODIGO_TIPOS_BOSQUES);
        tiposBosquesActivo = inicializarBioticoSeleccionadosSimple(
                TipoCatalogo.CODIGO_TIPOS_BOSQUES, tiposBosques);

        gradoIntervencionCoberturaVegetal = inicializarBiotico(TipoCatalogo.CODIGO_GRADO_INTERVENCION_COBERTURA_VEGETAL);

        gradoIntervencionCoberturaVegetalActivo = inicializarBioticoSeleccionadosSimple(
                TipoCatalogo.CODIGO_GRADO_INTERVENCION_COBERTURA_VEGETAL,
                gradoIntervencionCoberturaVegetal);

        aspectosEcologicos = inicializarBiotico(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS);
        aspectosEcologicosSeleccionado = inicializarBioticoSeleccionados(
                TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS, aspectosEcologicos);

        pisoZoogeografico = inicializarBiotico(TipoCatalogo.CODIGO_PISO_ZOOGEOLOGICO);
        pisoZoogeograficoActivo = inicializarBioticoSeleccionadosSimple(
                TipoCatalogo.CODIGO_PISO_ZOOGEOLOGICO, pisoZoogeografico);
        inicializarPisoZoogeografico();

        gruposFaunistico = inicializarBiotico(TipoCatalogo.CODIGO_GRUPO_FAUNISTICO);
        gruposFaunisticoSeleccionado = inicializarBioticoSeleccionados(
                TipoCatalogo.CODIGO_GRUPO_FAUNISTICO, gruposFaunistico);

        gruposFaunisticoNinguno = new CatalogoGeneralBiotico();
        if (!gruposFaunistico.isEmpty()) {
            gruposFaunisticoNinguno = gruposFaunistico.get(gruposFaunistico.size() - 1);

            if (Arrays.asList(gruposFaunisticoSeleccionado).contains(gruposFaunisticoNinguno)) {
                gruposFaunisticoNingunoActivo = true;
            }
        }


        sensibilidadPresentadaArea = inicializarBiotico(TipoCatalogo.CODIGO_SENSIBILIDAD_PRESENTADA_AREA);
        sensibilidadPresentadaAreaSeleccionado = inicializarBioticoSeleccionados(
                TipoCatalogo.CODIGO_SENSIBILIDAD_PRESENTADA_AREA,
                sensibilidadPresentadaArea);

        datosEcologicosPresentes = inicializarBiotico(TipoCatalogo.CODIGO_DATOS_ECOLOGICOS_PRESENTES);
        datosEcologicosPresentesSeleccionado = inicializarBioticoSeleccionados(
                TipoCatalogo.CODIGO_DATOS_ECOLOGICOS_PRESENTES,
                datosEcologicosPresentes);

        inicializarVariablesSectores();
        
        /**
         * Cris F: Mostrar historico
         */

        mostrarListasHistoricoFisico();
        mostrarHistoricoFisico();
        mostrarListasHistoricoSocial();
        mostrarHistoricoBiotico();
    }

    public void inicializarVariablesSectores() {
        sectorHidrocarburos = false;
        sectorPesca = false;
        sectorPescaMaricultura = false;
        otrosSectores = false;
        agroindustria = false;
        mineriaLibreAprovechamiento = false;
        electTelecomunicaciones = false;
        saneamiento = false;
        saneamientoUrbano = false;

        urbano = false;
        agricola = false;
        pecuario = false;
        mineriaLibreAprovechamiento = false;
        String sector = proyecto.getCatalogoCategoria().getTipoSubsector()
                .getCodigo();
        if (proyecto.getCatalogoCategoria().getCodigo()
                .equals(Constantes.SECTOR_HIDROCARBURO_CODIGO) || sector.equals("0034")) {
            //HIDROCARBUROS
            sectorHidrocarburos = true;
        }

        if (fichaAmbientalPma.getProyectoLicenciamientoAmbiental().getTipoPoblacion() != null
                && fichaAmbientalPma.getProyectoLicenciamientoAmbiental().getTipoPoblacion()
                .getNombre().equals("Urbana")) {
            urbano = true;
        }
        if (sectorHidrocarburos == true) {
            sectorHidrocarburos = false;
            if (urbano) {
                otrosSectores = true;
            }
        } else {


            if (sector != null) {

                if (sector.equals("0009") || sector.equals("0013")) {
                    // pesca --- 0009    camaronera
                    //UPDATE 'CAMARONERAS', secl_description = 'CAMARONERAS', = '0013'
                    sectorPesca = true;
                } else if (sector.equals("0002")) {// Agroindustria --- 0002
                    agroindustria = true;
                } else if (sector.equals("0011")) {
                    // Pesca maricultura --- 0011
                    sectorPescaMaricultura = true;
                } else if (sector.equals("0008")) {// Otros sectores ---- 0008
                    if (urbano) {
                        otrosSectores = true;
                    }

                } else if (sector.equals("0005")) { // mineriaLibreAprovechamiento
                    // --- // 0005
                    if (urbano) {
                        mineriaLibreAprovechamiento = true;
                    }

                } else if (sector.equals("0025") || sector.equals("0026") || sector.equals("0027") ||
                        sector.equals("0028") || sector.equals("0029") || sector.equals("0030") ||
                        sector.equals("0031") || sector.equals("0032") || sector.equals("0033")
                        || sector.equals("0006")) { // electTelecomunicaciones
                    // ///*UPDATE public.sectors_classification SET secl_code = '0025' WHERE secl_id = 1024;
                   /*
               '0025'  '0026'   '0027'    '0028'   '0029'   '0030'   '0031'   '0032'   '0033'
UPDATE public.sectors_classification SET secl_name = 'ELÉCTRICO TÉRMICA', secl_description = 'ELÉCTRICO TÉRMICA', secl_status = true, secl_code = '0025' WHERE secl_id = 1024;
UPDATE public.sectors_classification SET secl_name = 'ELÉCTRICO MAREOMOTRIZ', secl_description = 'ELÉCTRICO MAREOMOTRIZ', secl_status = true, secl_code = '0026' WHERE secl_id = 1025;
UPDATE public.sectors_classification SET secl_name = 'ELÉCTRICO HIDROELÉCTRICA', secl_description = 'ELÉCTRICO HIDROELÉCTRICA', secl_status = true, secl_code = '0027' WHERE secl_id = 1026;
UPDATE public.sectors_classification SET secl_name = 'ELÉCTRICO GEOTÉRMICA', secl_description = 'ELÉCTRICO GEOTÉRMICA', secl_status = true, secl_code = '0028' WHERE secl_id = 1027;
UPDATE public.sectors_classification SET secl_name = 'ELÉCTRICO EÓLICA', secl_description = 'ELÉCTRICO EÓLICA', secl_status = true, secl_code = '0029' WHERE secl_id = 1028;
UPDATE public.sectors_classification SET secl_name = 'ELÉCTRICO FOTOVOLTAICA', secl_description = 'ELÉCTRICO FOTOVOLTAICA', secl_status = true, secl_code = '0030' WHERE secl_id = 1029;
UPDATE public.sectors_classification SET secl_name = 'ELÉCTRICO REPOTENCIACIÓN DISTRIBUCIÓN', secl_description = 'ELÉCTRICO REPOTENCIACIÓN DISTRIBUCIÓN', secl_status = true, secl_code = '0031' WHERE secl_id = 1030;
UPDATE public.sectors_classification SET secl_name = 'ELÉCTRICO REPOTENCIACIÓN TRANSMISIÓN', secl_description = 'ELÉCTRICO REPOTENCIACIÓN TRANSMISIÓN', secl_status = true, secl_code = '0032' WHERE secl_id = 1031;
UPDATE public.sectors_classification SET secl_name = 'TELECOMUNICACIONES', secl_description = 'TELECOMUNICACIONES', secl_status = true, secl_code = '0033' WHERE secl_id = 1032;

                    */
                    // --- // 0006
                    if (urbano) {
                        electTelecomunicaciones = true;
                    }

                } else if (sector.equals("0007") || sector.equals("0015") || sector.equals("0016") || sector.equals("0017") || sector.equals("0018")) { // saneamiento -- 0007
//                   'RESIDUOS SÓLIDOS',                     '   0015'
//                   'PLANTA POTABILIZADORA DE AGUA',            '0016'
//                   'PLANTA DE TRATAMIENTO AGUAS RESIDUALES'    '0017'
//                    'CONSTRUCCIÓN DE ALCANTARILLADO',         '0018'

                    saneamiento = true;
                    if (urbano) {
                        saneamientoUrbano = true;
                    }

                } else if (sector.equals("0001")) { // agricola -- 0001
                    agricola = true;

                } else if (sector.equals("0003")) { // pecuario -- 0003
                    pecuario = true;

                }
            }
        }

    }

    public void inicializarFichaTecnica() {
        try {
            proyecto = proyectosBean.getProyecto();
            if (proyecto != null) {
                fichaAmbientalPma = fichaAmbientalPmaFacade
                        .getFichaAmbientalPorIdProyecto(proyecto.getId());


                if (fichaAmbientalPma == null || fichaAmbientalPma.getId() == null) {
                    LOGGER.error("Error al obtener los datos de la ficha.");
                    JsfUtil.redirectTo(JsfUtil.actionNavigateToBandeja());
                }

            } else {
                JsfUtil.redirectTo(JsfUtil.actionNavigateToBandeja());
            }

        } catch (Exception e) {

            JsfUtil.addMessageError("Error al obtener los datos del proyecto.");
            LOGGER.error("Error al obtener los datos del proyecto.", e);

        }

    }

    public void inicializarDatosGeneralesFisico() {
        List<String> codigosFisicos = new ArrayList<String>();
        listaGeneralFisico = new HashMap<String, List<CatalogoGeneralFisico>>();
        codigosFisicos.add(TipoCatalogo.CODIGO_CLIMA);
        codigosFisicos.add(TipoCatalogo.CODIGO_TIPO_SUELO);
        codigosFisicos.add(TipoCatalogo.CODIGO_PENDIENTE_SUELO);
//        codigosFisicos.add(TipoCatalogo.CODIGO_CONDICIONES_DRENAJE);
        List<CatalogoGeneralFisico> fisicos = catalogoFisicoFacade
                .obtenerListaFisicoTipo(codigosFisicos);
        for (CatalogoGeneralFisico catalogoGeneralFisico : fisicos) {
            List<CatalogoGeneralFisico> tmp = new ArrayList<CatalogoGeneralFisico>();
            String key = catalogoGeneralFisico.getTipoCatalogo().getCodigo();
            if (listaGeneralFisico.containsKey(key)) {
                tmp = listaGeneralFisico.get(key);
            }
            tmp.add(catalogoGeneralFisico);
            listaGeneralFisico.put(key, tmp);
        }
        // ----
        listaGeneralCatalogoFisico = new HashMap<String, List<CategoriaIICatalogoGeneralFisico>>();
        List<CategoriaIICatalogoGeneralFisico> catalogo = categoriaIICatalogoFisicoFacade
                .catalogoSeleccionadosCategoriaIITipo(proyecto.getId(),
                        codigosFisicos, seccionDescipcionArea);
        for (CategoriaIICatalogoGeneralFisico categoriaIICatalogoGeneralFisico : catalogo) {
            List<CategoriaIICatalogoGeneralFisico> temporal = new ArrayList<CategoriaIICatalogoGeneralFisico>();
            String key = categoriaIICatalogoGeneralFisico.getCatalogo()
                    .getTipoCatalogo().getCodigo();
            if (listaGeneralCatalogoFisico.containsKey(key)) {
                temporal = listaGeneralCatalogoFisico.get(key);
            }
            temporal.add(categoriaIICatalogoGeneralFisico);
            listaGeneralCatalogoFisico.put(key, temporal);
        }

    }

    public void inicializarDatosGeneralesSocial() {
        List<String> codigosSocials = new ArrayList<String>();
        listaGeneralSocial = new HashMap<String, List<CatalogoGeneralSocial>>();

        codigosSocials.add(TipoCatalogo.CODIGO_DEMOGRAFIA);
        codigosSocials.add(TipoCatalogo.CODIGO_ABASTECIMIENTO_AGUA);
        codigosSocials
                .add(TipoCatalogo.CODIGO_EVACUACION_AGUAS_SERVIDAS_POBLACION);
        codigosSocials.add(TipoCatalogo.CODIGO_ELECTRIFICACION);
        codigosSocials.add(TipoCatalogo.CODIGO_VIALIDAD_ACCESO_POBLACION);
        codigosSocials.add(TipoCatalogo.CODIGO_ORGANIZACION_SOCIAL);
        List<CatalogoGeneralSocial> Socials = catalogoSocialFacade
                .obtenerListaSocialTipo(codigosSocials);
        for (CatalogoGeneralSocial catalogoGeneralSocial : Socials) {
            List<CatalogoGeneralSocial> tmp = new ArrayList<CatalogoGeneralSocial>();
            String key = catalogoGeneralSocial.getTipoCatalogo().getCodigo();
            if (listaGeneralSocial.containsKey(key)) {
                tmp = listaGeneralSocial.get(key);
            }
            tmp.add(catalogoGeneralSocial);
            listaGeneralSocial.put(key, tmp);
        }

        // ----
        listaGeneralCatalogoSocial = new HashMap<String, List<CategoriaIICatalogoGeneralSocial>>();
        List<CategoriaIICatalogoGeneralSocial> catalogo = categoriaIICatalogoSocialFacade
                .catalogoSeleccionadosCategoriaIITipo(proyecto.getId(),
                        codigosSocials, seccionDescipcionArea);
        for (CategoriaIICatalogoGeneralSocial categoriaIICatalogoGeneralSocial : catalogo) {
            List<CategoriaIICatalogoGeneralSocial> temporal = new ArrayList<CategoriaIICatalogoGeneralSocial>();
            String key = categoriaIICatalogoGeneralSocial.getCatalogo()
                    .getTipoCatalogo().getCodigo();
            if (listaGeneralCatalogoSocial.containsKey(key)) {
                temporal = listaGeneralCatalogoSocial.get(key);
            }
            temporal.add(categoriaIICatalogoGeneralSocial);
            listaGeneralCatalogoSocial.put(key, temporal);
        }

    }

    public void inicializarDatosGeneralesBiotico() {
        List<String> codigosBioticos = new ArrayList<String>();
        listaGeneralBiotico = new HashMap<String, List<CatalogoGeneralBiotico>>();

        codigosBioticos.add(TipoCatalogo.CODIGO_DATOS_ECOLOGICOS_PRESENTES);
//        codigosBioticos.add(TipoCatalogo.CODIGO_SENSIBILIDAD_PRESENTADA_AREA);
        codigosBioticos.add(TipoCatalogo.CODIGO_GRUPO_FAUNISTICO);
        codigosBioticos.add(TipoCatalogo.CODIGO_PISO_ZOOGEOLOGICO);
//        codigosBioticos.add(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS);
//        codigosBioticos
//                .add(TipoCatalogo.CODIGO_GRADO_INTERVENCION_COBERTURA_VEGETAL);
//        codigosBioticos.add(TipoCatalogo.CODIGO_TIPOS_BOSQUES);
//        codigosBioticos.add(TipoCatalogo.CODIGO_HABITAT);
//        codigosBioticos.add(TipoCatalogo.CODIGO_FORMACION_VEGETAL);

        List<CatalogoGeneralBiotico> Bioticos = catalogoBioticoFacade
                .obtenerListaBioticoTipo(codigosBioticos);
        for (CatalogoGeneralBiotico catalogoGeneralBiotico : Bioticos) {
            List<CatalogoGeneralBiotico> tmp = new ArrayList<CatalogoGeneralBiotico>();
            String key = catalogoGeneralBiotico.getTipoCatalogo().getCodigo();
            if (listaGeneralBiotico.containsKey(key)) {
                tmp = listaGeneralBiotico.get(key);
            }
            tmp.add(catalogoGeneralBiotico);
            listaGeneralBiotico.put(key, tmp);
        }
        // ----
        listaGeneralCatalogoBiotico = new HashMap<String, List<CategoriaIICatalogoGeneralBiotico>>();
        List<CategoriaIICatalogoGeneralBiotico> catalogo = categoriaIICatalogoBioticoFacade
                .catalogoSeleccionadosCategoriaIITipo(proyecto.getId(),
                        codigosBioticos, seccionDescipcionArea);
        for (CategoriaIICatalogoGeneralBiotico categoriaIICatalogoGeneralBiotico : catalogo) {
            List<CategoriaIICatalogoGeneralBiotico> temporal = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
            String key = categoriaIICatalogoGeneralBiotico.getCatalogo()
                    .getTipoCatalogo().getCodigo();
            if (listaGeneralCatalogoBiotico.containsKey(key)) {
                temporal = listaGeneralCatalogoBiotico.get(key);
            }
            temporal.add(categoriaIICatalogoGeneralBiotico);
            listaGeneralCatalogoBiotico.put(key, temporal);
        }

    }

    public CategoriaIICatalogoGeneralFisico inicializarFisicoOtros(
            List<CatalogoGeneralFisico> catalogos, String codigo) {
        if (catalogos.size() > 0) {
            List<CategoriaIICatalogoGeneralFisico> temporal = new ArrayList<CategoriaIICatalogoGeneralFisico>();
            if (listaGeneralCatalogoFisico.containsKey(codigo)) {
                temporal = listaGeneralCatalogoFisico.get(codigo);
            }

            CategoriaIICatalogoGeneralFisico otros = new CategoriaIICatalogoGeneralFisico();
            otros.setCatalogo(catalogos.get(catalogos.size() - 1));
            otros.setFichaAmbientalPma(fichaAmbientalPma);
            System.out.println(otros.getCatalogo().getId());
            for (CategoriaIICatalogoGeneralFisico tmp : temporal) {
                if (catalogos.contains(tmp.getCatalogo())
                        && tmp.getCatalogo().equals(otros.getCatalogo())) {
                    otros.setValor(tmp.getValor());
                    return otros;
                }

            }
            return otros;
        }
        return null;
    }

    /**
     * Inicializar los catalogos seleccionados
     *
     * @param codigo
     * @param catalogos
     * @return
     */
    public CatalogoGeneralFisico inicializarFisicoSeleccionadosSimple(
            String codigo, List<CatalogoGeneralFisico> catalogos) {
        List<CategoriaIICatalogoGeneralFisico> temporal = new ArrayList<CategoriaIICatalogoGeneralFisico>();
        if (listaGeneralCatalogoFisico.containsKey(codigo)) {
            temporal = listaGeneralCatalogoFisico.get(codigo);
        }

        for (CategoriaIICatalogoGeneralFisico tmp : temporal) {
            if (catalogos.contains(tmp.getCatalogo())) {
                Integer pos = catalogos.lastIndexOf(tmp.getCatalogo());
                return catalogos.get(pos);

            }

        }
        return null;
    }

    public List<CatalogoGeneralFisico> inicializarFisico(String codigo) {

        if (listaGeneralFisico.containsKey(codigo)) {
            return listaGeneralFisico.get(codigo);
        }
        return new ArrayList<CatalogoGeneralFisico>();
    }

    public CatalogoGeneralFisico[] inicializarFisicoSeleccionados(
            String codigo, List<CatalogoGeneralFisico> catalogos) {
        List<CategoriaIICatalogoGeneralFisico> temporal = new ArrayList<CategoriaIICatalogoGeneralFisico>();
        if (listaGeneralCatalogoFisico.containsKey(codigo)) {
            temporal = listaGeneralCatalogoFisico.get(codigo);
        }
        int i = 0;
        CatalogoGeneralFisico[] seleccionados = new CatalogoGeneralFisico[temporal
                .size()];
        for (CategoriaIICatalogoGeneralFisico tmp : temporal) {
            Integer pos = catalogos.lastIndexOf(tmp.getCatalogo());
            seleccionados[i++] = catalogos.get(pos);
        }
        return seleccionados;
    }

    public void inicializarClima() {

        clima = inicializarFisico(TipoCatalogo.CODIGO_CLIMA);
        List<CategoriaIICatalogoGeneralFisico> climasProyecto = categoriaIICatalogoFisicoFacade
                .catalogoSeleccionadosCategoriaIITipo(proyecto.getId(),
                        TipoCatalogo.CODIGO_CLIMA, seccionDescipcionArea);

        if (!proyecto.isConcesionesMinerasMultiples()) {

            if (proyecto.getAltitud() > 500) {
                climaEditable = false;
                if (proyecto.getAltitud() > 2300) {
                    clima = clima.subList(3, 4);
                } else {
                    clima = clima.subList(2, 3);
                }
                climaActivo = clima.get(0);
            } else {
                climaEditable = true;
                clima = clima.subList(0, 2);
                for (CategoriaIICatalogoGeneralFisico climaTmp : climasProyecto) {
                    if (clima.contains(climaTmp.getCatalogo())) {
                        Integer pos = clima.lastIndexOf(climaTmp.getCatalogo());
                        climaActivo = clima.get(pos);
                    }

                }

            }
        } else {

            for (CategoriaIICatalogoGeneralFisico climaTmp : climasProyecto) {
                if (clima.contains(climaTmp.getCatalogo())) {
                    Integer pos = clima.lastIndexOf(climaTmp.getCatalogo());
                    climaActivo = clima.get(pos);
                }

            }
        }
        //
    }

    public void inicializarPisoZoogeografico() {

        if (!proyecto.isConcesionesMinerasMultiples()) {
            /*
             *
			 * Tropical noroccidental 0 – 800 msnm
			 * 
			 * Tropical suroccidental 800 - 1000 msnm Se cargaría en base al
			 * dato de altitud Tropical oriental bajo 800 msnm
			 * 
			 * Subtropical oriental 800 -1000 msnm y 1800 - 2000 msnm
			 * 
			 * Subtropical occidental 800 -1000 msnm y 1800 - 2000 msnm
			 * 
			 * templado 1800 - 3000 mnsm
			 * 
			 * Alto andino 2800 - 3000 msnm
			 * 
			 * Insular (Galápagos) Galápagos
			 */
            List<CatalogoGeneralBiotico> pisoZoogeograficoTmp = new ArrayList<CatalogoGeneralBiotico>();
            if (proyecto.getProyectoUbicacionesGeograficas().get(0)
                    .getUbicacionesGeografica().getUbicacionesGeografica()
                    .getUbicacionesGeografica().getCodificacionInec()
                    .equals("20")) {// galápagos
                pisoZoogeograficoTmp.add(pisoZoogeografico.get(8));
            } else {

                if (proyecto.getAltitud() <= 800) {

                    if (proyecto.getAltitud() == 0) {
                        pisoZoogeograficoTmp.add(pisoZoogeografico.get(7));
                    }
                    pisoZoogeograficoTmp.add(pisoZoogeografico.get(0));
                    pisoZoogeograficoTmp.add(pisoZoogeografico.get(2));
                } else if (proyecto.getAltitud() > 800
                        && proyecto.getAltitud() <= 2000) {
                    if (proyecto.getAltitud() <= 1000) {
                        pisoZoogeograficoTmp.add(pisoZoogeografico.get(1));
                    }
                    pisoZoogeograficoTmp.add(pisoZoogeografico.get(3));
                    pisoZoogeograficoTmp.add(pisoZoogeografico.get(4));

                } else if (proyecto.getAltitud() > 800
                        && proyecto.getAltitud() <= 1000) {
                    pisoZoogeograficoTmp.add(pisoZoogeografico.get(1));
                    pisoZoogeograficoTmp.add(pisoZoogeografico.get(3));
                    pisoZoogeograficoTmp.add(pisoZoogeografico.get(4));

                }
                if (proyecto.getAltitud() > 1800
                        && proyecto.getAltitud() <= 3000) {
                    pisoZoogeograficoTmp.add(pisoZoogeografico.get(5));

                }
                if (proyecto.getAltitud() > 2800
                        && proyecto.getAltitud() <= 3000) {
                    pisoZoogeograficoTmp.add(pisoZoogeografico.get(6));

                }
                if (proyecto.getAltitud() > 3000) {
                    pisoZoogeograficoTmp.add(pisoZoogeografico.get(9));

                }
            }
            pisoZoogeografico = pisoZoogeograficoTmp;

            if (pisoZoogeografico.size() == 1) {
                pisoZoogeograficoActivo = pisoZoogeografico.get(0);
            }
        }
        //
    }

    public CategoriaIICatalogoGeneralSocial inicializarSocialOtros(
            List<CatalogoGeneralSocial> catalogos, String codigo) {
        if (catalogos.size() > 0) {
            List<CategoriaIICatalogoGeneralSocial> temporal = new ArrayList<CategoriaIICatalogoGeneralSocial>();
            if (listaGeneralCatalogoSocial.containsKey(codigo)) {
                temporal = listaGeneralCatalogoSocial.get(codigo);
            }

            CategoriaIICatalogoGeneralSocial otros = new CategoriaIICatalogoGeneralSocial();
            otros.setCatalogo(catalogos.get(catalogos.size() - 1));
            otros.setFichaAmbientalPma(fichaAmbientalPma);

            for (CategoriaIICatalogoGeneralSocial tmp : temporal) {
                if (catalogos.contains(tmp.getCatalogo())
                        && tmp.getCatalogo().equals(otros.getCatalogo())) {
                    otros.setValor(tmp.getValor());
                    return otros;
                }

            }
            return otros;
        }
        return null;
    }

    /**
     * Inicializar los catalogos seleccionados
     *
     * @param codigo
     * @param catalogos
     * @return
     */
    public CatalogoGeneralSocial[] inicializarSocialSeleccionados(
            String codigo, List<CatalogoGeneralSocial> catalogos) {

        List<CategoriaIICatalogoGeneralSocial> temporal = new ArrayList<CategoriaIICatalogoGeneralSocial>();
        if (listaGeneralCatalogoSocial.containsKey(codigo)) {
            temporal = listaGeneralCatalogoSocial.get(codigo);
        }
        int i = 0;
        CatalogoGeneralSocial[] seleccionados = new CatalogoGeneralSocial[temporal
                .size()];
        for (CategoriaIICatalogoGeneralSocial tmp : temporal) {
            if (catalogos.contains(tmp.getCatalogo())) {
                Integer pos = catalogos.lastIndexOf(tmp.getCatalogo());
                seleccionados[i++] = catalogos.get(pos);

            }

        }
        return seleccionados;
    }

    public List<CatalogoGeneralSocial> inicializarSocial(String codigo) {
        if (listaGeneralSocial.containsKey(codigo)) {
            return listaGeneralSocial.get(codigo);
        }
        return new ArrayList<CatalogoGeneralSocial>();
    }

    /**
     * Inicializar los catalogos seleccionados
     *
     * @param codigo
     * @param catalogos
     * @return
     */
    public CatalogoGeneralSocial inicializarSocialSeleccionadosSimple(
            String codigo, List<CatalogoGeneralSocial> catalogos) {
        List<CategoriaIICatalogoGeneralSocial> temporal = new ArrayList<CategoriaIICatalogoGeneralSocial>();
        if (listaGeneralCatalogoSocial.containsKey(codigo)) {
            temporal = listaGeneralCatalogoSocial.get(codigo);
        }

        for (CategoriaIICatalogoGeneralSocial tmp : temporal) {
            if (catalogos.contains(tmp.getCatalogo())) {
                Integer pos = catalogos.lastIndexOf(tmp.getCatalogo());
                return catalogos.get(pos);

            }

        }
        return null;
    }

    // parte biotica

    /**
     * Inicializar los catalogos seleccionados
     *
     * @param codigo
     * @param catalogos
     * @return
     */
    public CatalogoGeneralBiotico[] inicializarBioticoSeleccionados(
            String codigo, List<CatalogoGeneralBiotico> catalogos) {

        List<CategoriaIICatalogoGeneralBiotico> temporal = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
        if (listaGeneralCatalogoBiotico.containsKey(codigo)) {
            temporal = listaGeneralCatalogoBiotico.get(codigo);
        }
        int i = 0;
        CatalogoGeneralBiotico[] seleccionados = new CatalogoGeneralBiotico[temporal
                .size()];
        for (CategoriaIICatalogoGeneralBiotico tmp : temporal) {
            Integer pos = catalogos.lastIndexOf(tmp.getCatalogo());
            seleccionados[i++] = catalogos.get(pos);

        }
        return seleccionados;
    }

    /**
     * Inicializar los catalogos seleccionados
     *
     * @param codigo
     * @param catalogos
     * @return
     */
    public CatalogoGeneralBiotico inicializarBioticoSeleccionadosSimple(
            String codigo, List<CatalogoGeneralBiotico> catalogos) {
        List<CategoriaIICatalogoGeneralBiotico> temporal = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
        if (listaGeneralCatalogoBiotico.containsKey(codigo)) {
            temporal = listaGeneralCatalogoBiotico.get(codigo);
        }

        for (CategoriaIICatalogoGeneralBiotico tmp : temporal) {
            if (catalogos.contains(tmp.getCatalogo())) {
                Integer pos = catalogos.lastIndexOf(tmp.getCatalogo());
                return catalogos.get(pos);

            }

        }
        return null;
    }

    public List<CatalogoGeneralBiotico> inicializarBiotico(String codigo) {
        if (listaGeneralBiotico.containsKey(codigo)) {
            return listaGeneralBiotico.get(codigo);
        }
        return new ArrayList<CatalogoGeneralBiotico>();
    }

    public void validarEnvio(Boolean b) {
        System.out.println(b);
        validado = b;
    }


    public Boolean selectSimple(List lista) {
        return lista.size() == 1;
    }

    public void validarGruposFaunisticoSeleccionado() {
        if (gruposFaunisticoNingunoActivo) {
            gruposFaunisticoNingunoActivo = false;
            if (gruposFaunisticoSeleccionado != null && gruposFaunisticoSeleccionado.length > 1) {
                List<CatalogoGeneralBiotico> tmp = Arrays.asList(gruposFaunisticoSeleccionado);
                gruposFaunisticoSeleccionado = new CatalogoGeneralBiotico[gruposFaunisticoSeleccionado.length - 1];
                int cont = 0;
                for (CatalogoGeneralBiotico cat : tmp) {
                    if(cat!=null && cat.getId()!=null) {
                        if (cat.getId() != gruposFaunisticoNinguno.getId()) {
                            gruposFaunisticoSeleccionado[cont++] = cat;
                        }
                    }
                }
            }
        } else if (Arrays.asList(gruposFaunisticoSeleccionado).contains(gruposFaunisticoNinguno)) {
            gruposFaunisticoSeleccionado = new CatalogoGeneralBiotico[]{gruposFaunisticoNinguno};
            gruposFaunisticoNingunoActivo = true;
        }
    }
    
    /**
     * Cris F: Historico
     */
    private List<String> listaCodigosFisico;
    private void mostrarListasHistoricoFisico(){
    	
    	listaCodigosFisico = new ArrayList<String>();
    	listaCodigosFisico.add(TipoCatalogo.CODIGO_CLIMA);
    	listaCodigosFisico.add(TipoCatalogo.CODIGO_TIPO_SUELO);
    	listaCodigosFisico.add(TipoCatalogo.CODIGO_PENDIENTE_SUELO);
    	listaCodigosFisico.add(TipoCatalogo.CODIGO_CONDICIONES_DRENAJE);    	
    	    	
    	listaGeneralFisicoHistorico = categoriaIICatalogoFisicoFacade.catalogoSeleccionadosCategoriaIITipoHistorico(
    			fichaAmbientalPma.getProyectoLicenciamientoAmbiental().getId(),
                        listaCodigosFisico, seccionDescipcionArea);
    	
    }

    
    private void mostrarHistoricoFisico(){
    	listaClimaHistorico = new ArrayList<CategoriaIICatalogoGeneralFisico>();
    	
    	listaGeneralCatalogoFisicoHistorico = new HashMap<String, List<CategoriaIICatalogoGeneralFisico>>();
        
        for (CategoriaIICatalogoGeneralFisico categoriaIICatalogoGeneralFisico : listaGeneralFisicoHistorico) {
            List<CategoriaIICatalogoGeneralFisico> temporal = new ArrayList<CategoriaIICatalogoGeneralFisico>();
            String key = categoriaIICatalogoGeneralFisico.getCatalogo()
                    .getTipoCatalogo().getCodigo();
            if (listaGeneralCatalogoFisicoHistorico.containsKey(key)) {
                temporal = listaGeneralCatalogoFisicoHistorico.get(key);
            }
            temporal.add(categoriaIICatalogoGeneralFisico);
            listaGeneralCatalogoFisicoHistorico.put(key, temporal);
        }
    	    	
    	if (listaGeneralCatalogoFisicoHistorico.containsKey(TipoCatalogo.CODIGO_CLIMA)) {
    		List<CategoriaIICatalogoGeneralFisico> listaClimaHistoricoAux = listaGeneralCatalogoFisicoHistorico.get(TipoCatalogo.CODIGO_CLIMA);;
    		
    		for(CategoriaIICatalogoGeneralFisico clima : listaClimaHistoricoAux){
    			if(clima.getIdRegistroOriginal() != null){
    				listaClimaHistorico.add(clima);
    			}
    		}           	
        }
    	
    	listaTipoSueloHistorico = new ArrayList<CategoriaIICatalogoGeneralFisico>();
    	
    	if (listaGeneralCatalogoFisicoHistorico.containsKey(TipoCatalogo.CODIGO_TIPO_SUELO)) {
    		listaTipoSueloHistorico = listaGeneralCatalogoFisicoHistorico.get(TipoCatalogo.CODIGO_TIPO_SUELO);
        }
    	
    	listaPendienteSueloHistorico = new ArrayList<CategoriaIICatalogoGeneralFisico>();
    	
    	if (listaGeneralCatalogoFisicoHistorico.containsKey(TipoCatalogo.CODIGO_PENDIENTE_SUELO)) {
    		listaPendienteSueloHistorico = listaGeneralCatalogoFisicoHistorico.get(TipoCatalogo.CODIGO_PENDIENTE_SUELO);
        }
    	
    	listaCondicionesDrenajeHistorico = new ArrayList<CategoriaIICatalogoGeneralFisico>();
    	
    	if (listaGeneralCatalogoFisicoHistorico.containsKey(TipoCatalogo.CODIGO_CONDICIONES_DRENAJE)) {
    		listaCondicionesDrenajeHistorico = listaGeneralCatalogoFisicoHistorico.get(TipoCatalogo.CODIGO_CONDICIONES_DRENAJE);
        }
    	
    }

    //Social
    private List<String> listaCodigosSocial;
    private void mostrarListasHistoricoSocial(){
    	
    	listaCodigosSocial = new ArrayList<String>();
    	listaCodigosSocial.add(TipoCatalogo.CODIGO_DEMOGRAFIA);
    	listaCodigosSocial.add(TipoCatalogo.CODIGO_ABASTECIMIENTO_AGUA);
    	listaCodigosSocial.add(TipoCatalogo.CODIGO_EVACUACION_AGUAS_SERVIDAS_POBLACION);
    	listaCodigosSocial.add(TipoCatalogo.CODIGO_ELECTRIFICACION);
    	listaCodigosSocial.add(TipoCatalogo.CODIGO_VIALIDAD_ACCESO_POBLACION);
    	listaCodigosSocial.add(TipoCatalogo.CODIGO_ORGANIZACION_SOCIAL);
    	    	
    	listaGeneralSocialHistorico = categoriaIICatalogoSocialFacade.catalogoSeleccionadosCategoriaIITipoHistorico(fichaAmbientalPma.getProyectoLicenciamientoAmbiental().getId(), listaCodigosSocial, seccionDescipcionArea);
    	
    	listaGeneralCatalogoSocialHistorico = new HashMap<String, List<CategoriaIICatalogoGeneralSocial>>();
    	
		for (CategoriaIICatalogoGeneralSocial categoriaIICatalogoGeneralSocial : listaGeneralSocialHistorico) {
			List<CategoriaIICatalogoGeneralSocial> temporal = new ArrayList<CategoriaIICatalogoGeneralSocial>();
			String key = categoriaIICatalogoGeneralSocial.getCatalogo().getTipoCatalogo().getCodigo();
			if (listaGeneralCatalogoSocialHistorico.containsKey(key)) {
				temporal = listaGeneralCatalogoSocialHistorico.get(key);
			}
			temporal.add(categoriaIICatalogoGeneralSocial);
			listaGeneralCatalogoSocialHistorico.put(key, temporal);
		}
    	 
    	 listaDemografiaHistorico = new ArrayList<CategoriaIICatalogoGeneralSocial>();
    	 if(listaGeneralCatalogoSocialHistorico.containsKey(TipoCatalogo.CODIGO_DEMOGRAFIA)){
    		 listaDemografiaHistorico = listaGeneralCatalogoSocialHistorico.get(TipoCatalogo.CODIGO_DEMOGRAFIA);
    	 }
    	 
    	 listaAbastecimientoAguaHistorico = new ArrayList<CategoriaIICatalogoGeneralSocial>();
    	 if(listaGeneralCatalogoSocialHistorico.containsKey(TipoCatalogo.CODIGO_ABASTECIMIENTO_AGUA)){
    		 listaAbastecimientoAguaHistorico = listaGeneralCatalogoSocialHistorico.get(TipoCatalogo.CODIGO_ABASTECIMIENTO_AGUA);
    	 }
    	 
    	 listaEvacuacionAguasHistorico = new ArrayList<CategoriaIICatalogoGeneralSocial>();
    	 if(listaGeneralCatalogoSocialHistorico.containsKey(TipoCatalogo.CODIGO_EVACUACION_AGUAS_SERVIDAS_POBLACION)){
    		 listaEvacuacionAguasHistorico = listaGeneralCatalogoSocialHistorico.get(TipoCatalogo.CODIGO_EVACUACION_AGUAS_SERVIDAS_POBLACION);
    	 }
    	 
    	 listaElectrificacionHistorico = new ArrayList<CategoriaIICatalogoGeneralSocial>();
    	 if(listaGeneralCatalogoSocialHistorico.containsKey(TipoCatalogo.CODIGO_ELECTRIFICACION)){
    		 listaElectrificacionHistorico = listaGeneralCatalogoSocialHistorico.get(TipoCatalogo.CODIGO_ELECTRIFICACION);
    	 }
    	 
    	 listaVialidadAccesoPoblacionHistorico = new ArrayList<CategoriaIICatalogoGeneralSocial>();
    	 if(listaGeneralCatalogoSocialHistorico.containsKey(TipoCatalogo.CODIGO_VIALIDAD_ACCESO_POBLACION)){
    		 listaVialidadAccesoPoblacionHistorico = listaGeneralCatalogoSocialHistorico.get(TipoCatalogo.CODIGO_VIALIDAD_ACCESO_POBLACION);
    	 }
    	 
    	 listaOrganizacionSocialHistorico = new ArrayList<CategoriaIICatalogoGeneralSocial>();
    	 if(listaGeneralCatalogoSocialHistorico.containsKey(TipoCatalogo.CODIGO_ORGANIZACION_SOCIAL)){
    		 listaOrganizacionSocialHistorico = listaGeneralCatalogoSocialHistorico.get(TipoCatalogo.CODIGO_ORGANIZACION_SOCIAL);
    	 }    	 
    }
    
    //Biotico
    private List<String> listaCodigosBiotico;
    private void mostrarHistoricoBiotico(){
    	listaCodigosBiotico = new ArrayList<String>();
    	
    	listaCodigosBiotico.add(TipoCatalogo.CODIGO_FORMACION_VEGETAL);
    	listaCodigosBiotico.add(TipoCatalogo.CODIGO_HABITAT);
    	listaCodigosBiotico.add(TipoCatalogo.CODIGO_TIPOS_BOSQUES);
    	listaCodigosBiotico.add(TipoCatalogo.CODIGO_GRADO_INTERVENCION_COBERTURA_VEGETAL);
    	listaCodigosBiotico.add(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS);
    	listaCodigosBiotico.add(TipoCatalogo.CODIGO_PISO_ZOOGEOLOGICO);
    	listaCodigosBiotico.add(TipoCatalogo.CODIGO_GRUPO_FAUNISTICO);
    	listaCodigosBiotico.add(TipoCatalogo.CODIGO_SENSIBILIDAD_PRESENTADA_AREA);
    	listaCodigosBiotico.add(TipoCatalogo.CODIGO_DATOS_ECOLOGICOS_PRESENTES);
    	
    	listaGeneralCatalogoBioticoHistorico = new HashMap<String, List<CategoriaIICatalogoGeneralBiotico>>();
    	
    	listaGeneralBioticoHistorico = categoriaIICatalogoBioticoFacade.catalogoSeleccionadosCategoriaIITipoHistorico(fichaAmbientalPma.getProyectoLicenciamientoAmbiental().getId(), listaCodigosBiotico, seccionDescipcionArea);
    	
    	for (CategoriaIICatalogoGeneralBiotico categoriaIICatalogoGeneralBiotico : listaGeneralBioticoHistorico) {
			List<CategoriaIICatalogoGeneralBiotico> temporal = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
			String key = categoriaIICatalogoGeneralBiotico.getCatalogo().getTipoCatalogo().getCodigo();
			if (listaGeneralCatalogoBioticoHistorico.containsKey(key)) {
				temporal = listaGeneralCatalogoBioticoHistorico.get(key);
			}
			temporal.add(categoriaIICatalogoGeneralBiotico);
			listaGeneralCatalogoBioticoHistorico.put(key, temporal);
		}
    	
    	listaFormacionVegetalHistorico = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
    	if(listaGeneralCatalogoBioticoHistorico.containsKey(TipoCatalogo.CODIGO_FORMACION_VEGETAL)){
    		listaFormacionVegetalHistorico = listaGeneralCatalogoBioticoHistorico.get(TipoCatalogo.CODIGO_FORMACION_VEGETAL);
    	}
    	
    	listaHabitatHistorico = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
    	if(listaGeneralCatalogoBioticoHistorico.containsKey(TipoCatalogo.CODIGO_HABITAT)){
    		listaHabitatHistorico = listaGeneralCatalogoBioticoHistorico.get(TipoCatalogo.CODIGO_HABITAT);
    	}
    	
    	listaTiposBosquesHistorico = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
    	if(listaGeneralCatalogoBioticoHistorico.containsKey(TipoCatalogo.CODIGO_TIPOS_BOSQUES)){
    		List<CategoriaIICatalogoGeneralBiotico> listaTiposBosquesHistoricoAux = listaGeneralCatalogoBioticoHistorico.get(TipoCatalogo.CODIGO_TIPOS_BOSQUES);
    		
    		for(CategoriaIICatalogoGeneralBiotico tipoBosque : listaTiposBosquesHistoricoAux){
    			if(tipoBosque.getIdRegistroOriginal() != null){
    				listaTiposBosquesHistorico.add(tipoBosque);
    			}
    		}
    	}
    	
    	listaGradoIntervencionHistorico = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
    	if(listaGeneralCatalogoBioticoHistorico.containsKey(TipoCatalogo.CODIGO_GRADO_INTERVENCION_COBERTURA_VEGETAL)){
    		List<CategoriaIICatalogoGeneralBiotico> listaGradoIntervencionHistoricoAux = listaGeneralCatalogoBioticoHistorico.get(TipoCatalogo.CODIGO_GRADO_INTERVENCION_COBERTURA_VEGETAL);
    		
    		for(CategoriaIICatalogoGeneralBiotico coverturaVegetal : listaGradoIntervencionHistoricoAux){
    			if(coverturaVegetal.getIdRegistroOriginal() != null){
    				listaGradoIntervencionHistorico.add(coverturaVegetal);
    			}
    		}
    	}
    	
    	listaAspectosEcologicosHistorico = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
    	if(listaGeneralCatalogoBioticoHistorico.containsKey(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS)){
    		listaAspectosEcologicosHistorico = listaGeneralCatalogoBioticoHistorico.get(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS);
    	}
    	
    	listaPisoZoogeologicoHistorico = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
    	if(listaGeneralCatalogoBioticoHistorico.containsKey(TipoCatalogo.CODIGO_PISO_ZOOGEOLOGICO)){
    		List<CategoriaIICatalogoGeneralBiotico> listaPisoZoogeologicoHistoricoAux = listaGeneralCatalogoBioticoHistorico.get(TipoCatalogo.CODIGO_PISO_ZOOGEOLOGICO);
    		
    		for(CategoriaIICatalogoGeneralBiotico piso : listaPisoZoogeologicoHistoricoAux){
    			if(piso.getIdRegistroOriginal() != null)
    				listaPisoZoogeologicoHistorico.add(piso);
    		}
    	}    	
    	
    	listaGrupoFaunisticoHistorico = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
    	if(listaGeneralCatalogoBioticoHistorico.containsKey(TipoCatalogo.CODIGO_GRUPO_FAUNISTICO)){
    		listaGrupoFaunisticoHistorico = listaGeneralCatalogoBioticoHistorico.get(TipoCatalogo.CODIGO_GRUPO_FAUNISTICO);
    	}
    	
    	listaSensibilidadPresentadaAreaHistorico = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
    	if(listaGeneralCatalogoBioticoHistorico.containsKey(TipoCatalogo.CODIGO_SENSIBILIDAD_PRESENTADA_AREA)){
    		listaSensibilidadPresentadaAreaHistorico = listaGeneralCatalogoBioticoHistorico.get(TipoCatalogo.CODIGO_SENSIBILIDAD_PRESENTADA_AREA);
    	}
    	
    	listaDatosEcologicosPresentesHistorico = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
    	if(listaGeneralCatalogoBioticoHistorico.containsKey(TipoCatalogo.CODIGO_DATOS_ECOLOGICOS_PRESENTES)){
    		listaDatosEcologicosPresentesHistorico = listaGeneralCatalogoBioticoHistorico.get(TipoCatalogo.CODIGO_DATOS_ECOLOGICOS_PRESENTES);
    	}
    	
    	
    }

}