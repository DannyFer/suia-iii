package ec.gob.ambiente.prevencion.categoria2.controllers;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.catalogos.facade.*;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.dto.EntityFichaAmbientalDescripcionArea;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import lombok.Getter;
import lombok.Setter;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Frank Torres
 */
@ManagedBean
@ViewScoped
public class DescripcionAreaImplantacionPmaImprimirController implements
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 523482163417492243L;
    @Getter
    public final String seccionDescipcionArea = "7";
    @EJB
    private CategoriaIICatalogoFisicoFacade categoriaIICatalogoFisicoFacade;
    @EJB
    private CategoriaIICatalogoSocialFacade categoriaIICatalogoSocialFacade;
    @EJB
    private CategoriaIICatalogoBioticoFacade categoriaIICatalogoBioticoFacade;
    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
    @EJB
    private CatalogoFisicoFacade catalogoFisicoFacade;
    @EJB
    private CatalogoSocialFacade catalogoSocialFacade;
    @EJB
    private CatalogoBioticoFacade catalogoBioticoFacade;
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;
    @Getter
    @Setter
    private Boolean climaEditable;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

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
    private FichaAmbientalPma fichaAmbientalPma;
    private Map<String, List<CategoriaIICatalogoGeneralFisico>> listaGeneralCatalogoFisico;
    private Map<String, List<CategoriaIICatalogoGeneralSocial>> listaGeneralCatalogoSocial;
    private Map<String, List<CategoriaIICatalogoGeneralBiotico>> listaGeneralCatalogoBiotico;

    public String processAction(FichaAmbientalPma fichaAmbientalPma) {
        // inicializar datos
        proyecto = proyectosBean.getProyecto();
        inicializarVariablesSectores();
        if (sectorHidrocarburos) {
            return UtilFichaMineria.generarHtml(
                    mostrarNoAplica("7. DESCRIPCIÓN DEL ÁREA DE IMPLANTACIÓN"),
                    proyecto);
        } else {
            //
            this.fichaAmbientalPma = fichaAmbientalPma;
            // inicializar datos

            String html = UtilFichaMineria
                    .extraeHtml(JsfUtil
                            .devolverPathReportesHtml("subreportes/descripcionArea.html"));

            return UtilFichaMineria.generarHtml(html,
                    inicializarObjetoDescripcionArea());
        }
    }

    public EntityFichaAmbientalDescripcionArea inicializarObjetoDescripcionArea() {
        EntityFichaAmbientalDescripcionArea descripcion = new EntityFichaAmbientalDescripcionArea();
        inicializarDatosGeneralesFisico();
        inicializarDatosGeneralesSocial();
        inicializarDatosGeneralesBiotico();
        if (!sectorPesca) {
            descripcion.setClima(mostrarDatosColumnas("Clima:",
                    inicializarFisicoSeleccionados(TipoCatalogo.CODIGO_CLIMA)));
            descripcion
                    .setTiposSuelo(mostrarDatosColumnas(
                            "Tipo de suelo:",
                            inicializarFisicoSeleccionados(TipoCatalogo.CODIGO_TIPO_SUELO)));

            descripcion
                    .setPendienteSuelo(mostrarDatosColumnas(
                            "Pendiente del suelo:",
                            inicializarFisicoSeleccionados(TipoCatalogo.CODIGO_PENDIENTE_SUELO)));


//			descripcion
//					.setCondicionesDrenaje(mostrarDatosColumnas(
//							"Condiciones de drenaje:",
//							inicializarFisicoSeleccionados(TipoCatalogo.CODIGO_CONDICIONES_DRENAJE)));
        }
        // ----
        descripcion
                .setDeografia(mostrarDatosColumnas(
                        "Demografía (Población más cercana):",
                        inicializarSocialSeleccionados(TipoCatalogo.CODIGO_DEMOGRAFIA)));
        descripcion
                .setAbastecimientoAguaPoblacion(mostrarDatosColumnas(
                        "Abstecimiento de agua población:",
                        inicializarSocialSeleccionados(TipoCatalogo.CODIGO_ABASTECIMIENTO_AGUA)));
        descripcion
                .setEvacuacionAguasServidasPoblacion(mostrarDatosColumnas(
                        "Evacuación de aguas servidas población:",
                        inicializarSocialSeleccionados(TipoCatalogo.CODIGO_EVACUACION_AGUAS_SERVIDAS_POBLACION)));
        descripcion
                .setElectrificacion(mostrarDatosColumnas(
                        "Electrificación:",
                        inicializarSocialSeleccionados(TipoCatalogo.CODIGO_ELECTRIFICACION)));
        descripcion
                .setVialidadAccesoPoblacion(mostrarDatosColumnas(
                        "Vialidad y acceso a la población:",
                        inicializarSocialSeleccionados(TipoCatalogo.CODIGO_VIALIDAD_ACCESO_POBLACION)));
        descripcion
                .setOrganizacionSocial(mostrarDatosColumnas(
                        "Organización social:",
                        inicializarSocialSeleccionados(TipoCatalogo.CODIGO_ORGANIZACION_SOCIAL)));

        // /------------
        if (!electTelecomunicaciones && !saneamientoUrbano && !agroindustria
                && !otrosSectores) {

//            descripcion
//                    .setComponenteFlora(mostrarSubtitulo("Componente Flora:"));

//            if (!mineriaLibreAprovechamiento) {
//                descripcion
//                        .setFormacion(mostrarDatosColumnas(
//                                "Formación vegetal:",
//                                inicializarBioticoSeleccionados(TipoCatalogo.CODIGO_FORMACION_VEGETAL)));
//            }

//            descripcion
//                    .setHabitat(mostrarDatosColumnas(
//                            "Tipo de cobertura vegetal:",
//                            inicializarBioticoSeleccionados(TipoCatalogo.CODIGO_HABITAT)));
//            descripcion
//                    .setTipoBosque(mostrarDatosColumnas(
//                            "Tipo de Bosque:",
//                            inicializarBioticoSeleccionados(TipoCatalogo.CODIGO_TIPOS_BOSQUES)));

//            descripcion
//                    .setGradoIntervencionCobertura(mostrarDatosColumnas(
//                            "Grado de intervención de la cobertura vegetal:",
//                            inicializarBioticoSeleccionados(TipoCatalogo.CODIGO_GRADO_INTERVENCION_COBERTURA_VEGETAL)));

//            descripcion
//                    .setAspectosEcologicos(mostrarDatosColumnas(
//                            "Aspectos ecológicos:",
//                            inicializarBioticoSeleccionados(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS)));
        }

        if (descripcion.getFormacion() != null
                || descripcion.getHabitat() != null
                || descripcion.getTipoBosque() != null
                || descripcion.getTipoBosque() != null
                || descripcion.getGradoIntervencionCobertura() != null
                || descripcion.getAspectosEcologicos() != null) {
            descripcion.setTablaComponenteFloraInicio(inicioTabla());
            descripcion.setTablaComponenteFloraFin(finTabla());
        }

        descripcion.setComponenteFauna(mostrarSubtitulo("Componente Fauna:"));
        descripcion.setTablaComponenteFaunaInicio(inicioTabla());
        descripcion.setTablaComponenteFaunaFin(finTabla());
        descripcion
                .setPisoZoogeografico(mostrarDatosColumnas(
                        "Piso Zoogeográfico donde se encuentra el proyecto:",
                        inicializarBioticoSeleccionados(TipoCatalogo.CODIGO_PISO_ZOOGEOLOGICO)));

        descripcion
                .setGruposFaunisticos(mostrarDatosColumnas(
                        "Grupos faunísticos que se encontraron en el área del Proyecto:",
                        inicializarBioticoSeleccionados(TipoCatalogo.CODIGO_GRUPO_FAUNISTICO)));

//        if (!otrosSectores && !electTelecomunicaciones && !saneamiento && !agricola && !pecuario && !agroindustria) {
//            descripcion
//                    .setSensibilidadPresentada(mostrarDatosColumnas(
//                            "Sensibilidad presentada en el área del Proyecto (deshabilita para Saneamiento, Agricultura):",
//                            inicializarBioticoSeleccionados(TipoCatalogo.CODIGO_SENSIBILIDAD_PRESENTADA_AREA)));
//        }

        if (false && !urbano && !otrosSectores && !electTelecomunicaciones && !saneamientoUrbano) {
            descripcion
                    .setDatosEcologicos(mostrarDatosColumnas(
                            "Datos Ecológicos presentes en el área del Proyecto:",
                            inicializarBioticoSeleccionados(TipoCatalogo.CODIGO_DATOS_ECOLOGICOS_PRESENTES)));
        }
        if (descripcion.getSensibilidadPresentada() != null
                || descripcion.getDatosEcologicos() != null) {
            descripcion.setTablaComponenteExtrasInicio(inicioTabla());
            descripcion.setTablaComponenteExtrasFin(finTabla());
        }
        /*
         * <tr> <td style="width: 50%"><span
		 * style="font-size: small">Clima:</span> </td> <td
		 * style="width: 50%"><span style="font-size: small">$F{clima}</span>
		 * </td> </tr>
		 */
        return descripcion;
    }

    public String mostrarDatosColumnas(String descripcion, String valor) {
        // String header =
        // "<table align=\"left\" border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"width: 100%\"> <tbody>";
        // String footer = "</tbody></table>";
        return "<tr><td style=\"width: 50%\">"
                + "<span style=\"font-size: small\">" + descripcion
                + "</span> </td><td style=\"width: 50%\">"
                + "<span style=\"font-size: small\">" + valor
                + "</span></td></tr>";
    }

    public String mostrarSubtitulo(String texto) {

        return "<p>&nbsp;</p> <div style=\"text-align: left;\"> "
                + "<span style=\"font-weight: bold;\">" + texto
                + "<br /> </span> </div> <br />";
    }

    public String mostrarNoAplica(String texto) {
        return "<!DOCTYPE html> <html><head><meta charset=\"UTF-8\" /><body> <div style=\"text-align: left;\">"
                + "<span style=\"font-weight: bold;\">"
                + texto
                + "<br /> </span>"
                + "</div> <br /><p>&nbsp;</p>  No Aplica <p>&nbsp;</p>  </body> </html>";

    }

    public String inicioTabla() {
        return "<table align=\"left\" border=\"0\" "
                + "cellpadding=\"1\" cellspacing=\"1\" "
                + "style=\"width: 100%\"> <tbody> ";

    }

    public String finTabla() {
        return "</tbody> </table>";
    }

    public void inicializarDatosGeneralesFisico() {
        List<String> codigosFisicos = new ArrayList<String>();
        codigosFisicos.add(TipoCatalogo.CODIGO_CLIMA);
        codigosFisicos.add(TipoCatalogo.CODIGO_TIPO_SUELO);
        codigosFisicos.add(TipoCatalogo.CODIGO_PENDIENTE_SUELO);
        codigosFisicos.add(TipoCatalogo.CODIGO_CONDICIONES_DRENAJE);
        listaGeneralCatalogoFisico = new HashMap<String, List<CategoriaIICatalogoGeneralFisico>>();
        List<CategoriaIICatalogoGeneralFisico> catalogo = categoriaIICatalogoFisicoFacade
                .catalogoSeleccionadosCategoriaIITipo(proyectosBean
                                .getProyecto().getId(), codigosFisicos,
                        seccionDescipcionArea);
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
        codigosSocials.add(TipoCatalogo.CODIGO_DEMOGRAFIA);
        codigosSocials.add(TipoCatalogo.CODIGO_ABASTECIMIENTO_AGUA);
        codigosSocials
                .add(TipoCatalogo.CODIGO_EVACUACION_AGUAS_SERVIDAS_POBLACION);
        codigosSocials.add(TipoCatalogo.CODIGO_ELECTRIFICACION);
        codigosSocials.add(TipoCatalogo.CODIGO_VIALIDAD_ACCESO_POBLACION);
        codigosSocials.add(TipoCatalogo.CODIGO_ORGANIZACION_SOCIAL);
        // ----
        listaGeneralCatalogoSocial = new HashMap<String, List<CategoriaIICatalogoGeneralSocial>>();
        List<CategoriaIICatalogoGeneralSocial> catalogo = categoriaIICatalogoSocialFacade
                .catalogoSeleccionadosCategoriaIITipo(proyectosBean
                                .getProyecto().getId(), codigosSocials,
                        seccionDescipcionArea);
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
        codigosBioticos.add(TipoCatalogo.CODIGO_DATOS_ECOLOGICOS_PRESENTES);
        codigosBioticos.add(TipoCatalogo.CODIGO_SENSIBILIDAD_PRESENTADA_AREA);
        codigosBioticos.add(TipoCatalogo.CODIGO_GRUPO_FAUNISTICO);
        codigosBioticos.add(TipoCatalogo.CODIGO_PISO_ZOOGEOLOGICO);
        codigosBioticos.add(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS);
        codigosBioticos
                .add(TipoCatalogo.CODIGO_GRADO_INTERVENCION_COBERTURA_VEGETAL);
        codigosBioticos.add(TipoCatalogo.CODIGO_TIPOS_BOSQUES);
        codigosBioticos.add(TipoCatalogo.CODIGO_HABITAT);
        codigosBioticos.add(TipoCatalogo.CODIGO_FORMACION_VEGETAL);
        listaGeneralCatalogoBiotico = new HashMap<String, List<CategoriaIICatalogoGeneralBiotico>>();
        List<CategoriaIICatalogoGeneralBiotico> catalogo = categoriaIICatalogoBioticoFacade
                .catalogoSeleccionadosCategoriaIITipo(proyectosBean
                                .getProyecto().getId(), codigosBioticos,
                        seccionDescipcionArea);
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

    public String inicializarFisicoSeleccionados(String codigo) {

        List<CategoriaIICatalogoGeneralFisico> temporal = listaGeneralCatalogoFisico
                .get(codigo);
        String resultado = "";
        if (temporal != null) {
            for (CategoriaIICatalogoGeneralFisico tmp : temporal) {
                resultado += tmp.getCatalogo().toString();
                if (tmp.getValor() != null && !tmp.getValor().isEmpty()) {
                    resultado += " (" + tmp.getValor() + ")";
                }
                resultado += "<br/>";
            }
        }
        return resultado;
    }

    public String inicializarSocialSeleccionados(String codigo) {

        // List<CategoriaIICatalogoGeneralSocial> temporal =
        // categoriaIICatalogoSocialFacade
        // .catalogoSeleccionadosCategoriaIITipo(proyecto.getId(), codigo,
        // seccionDescipcionArea);
        List<CategoriaIICatalogoGeneralSocial> temporal = listaGeneralCatalogoSocial
                .get(codigo);
        String resultado = "";
        if (temporal != null) {
            for (CategoriaIICatalogoGeneralSocial tmp : temporal) {
                resultado += tmp.getCatalogo().toString();
                if (tmp.getValor() != null && !tmp.getValor().isEmpty()) {
                    resultado += " (" + tmp.getValor() + ")";
                }
                resultado += "<br/>";
            }
        }
        return resultado;
    }

    public String inicializarBioticoSeleccionados(String codigo) {

        // List<CategoriaIICatalogoGeneralBiotico> temporal =
        // categoriaIICatalogoBioticoFacade
        // .catalogoSeleccionadosCategoriaIITipo(proyecto.getId(), codigo,
        // seccionDescipcionArea);

        List<CategoriaIICatalogoGeneralBiotico> temporal = listaGeneralCatalogoBiotico
                .get(codigo);
        String resultado = "";
        if (temporal != null) {
            for (CategoriaIICatalogoGeneralBiotico tmp : temporal) {
                resultado += tmp.getCatalogo().toString();
                if (tmp.getValor() != null && !tmp.getValor().isEmpty()) {
                    resultado += " (" + tmp.getValor() + ")";
                }
                resultado += "<br/>";
            }
        }
        return resultado;
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

        fichaAmbientalPma = fichaAmbientalPmaFacade
                .getFichaAmbientalPorIdProyecto(proyectosBean.getProyecto()
                        .getId());
        mineriaLibreAprovechamiento = false;
        String sector = proyectosBean.getProyecto().getCatalogoCategoria()
                .getTipoSubsector().getCodigo();
        if (fichaAmbientalPma.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo()
                .equals(Constantes.SECTOR_HIDROCARBURO_CODIGO) || sector.equals("0034")) {
            //HIDROCARBUROS
            sectorHidrocarburos = true;
        }

        if (fichaAmbientalPma.getProyectoLicenciamientoAmbiental().getTipoPoblacion() != null && fichaAmbientalPma.getProyectoLicenciamientoAmbiental().getTipoPoblacion()
                .getNombre() != null
                && fichaAmbientalPma.getProyectoLicenciamientoAmbiental().getTipoPoblacion()
                .getNombre().equals("Urbana")) {
            urbano = true;
        }
        if (sectorHidrocarburos == true) {
            sectorHidrocarburos = false;
            if (urbano) {
                otrosSectores = true;
            }
        } else if (sector != null) {


            if (sector.equals("0009")) {// pesca --- 0009
                sectorPesca = true;
            }
            if (sector.equals("0002")) {// Agroindustria --- 0002
                agroindustria = true;
            }
            if (sector.equals("0011")) {// Pesca maricultura --- 0011
                sectorPescaMaricultura = true;
            }
            if (sector.equals("0008")) {// Otros sectores ---- 0008
                if (urbano) {
                    otrosSectores = true;
                }

            }
            if (sector.equals("0005")) { // mineriaLibreAprovechamiento ---
                // 0005
                if (urbano) {
                    mineriaLibreAprovechamiento = true;
                }

            }
            if (sector.equals("0006")) { // electTelecomunicaciones ---
                // 0006
                if (urbano) {
                    electTelecomunicaciones = true;
                }

            }
            if (sector.equals("0007")) { // saneamiento -- 0007
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