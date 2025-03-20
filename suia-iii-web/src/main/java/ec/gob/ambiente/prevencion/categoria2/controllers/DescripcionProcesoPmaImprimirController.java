package ec.gob.ambiente.prevencion.categoria2.controllers;

import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.dto.*;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FaseFichaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import lombok.Getter;
import lombok.Setter;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Frank Torres
 */
@ManagedBean
@ViewScoped
public class DescripcionProcesoPmaImprimirController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -860395737813220191L;

    @Getter
    @Setter
    private Boolean aplicaHerramientas = false;

    @Getter
    @Setter
    private Boolean aplicaInsumos = false;

    /*@Getter
    @Setter
    private Boolean aplicaOrigenRecursoAgua = false;*/

    @Getter
    @Setter
    private List<CatalogoCategoriaFase> categoriaFaseList;

    @EJB
    private FaseFichaAmbientalFacade faseFichaAmbientalFacade;

    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;


    private FichaAmbientalPma ficha;
    private ProyectoLicenciamientoAmbiental proyecto;

    public String processAction(ProyectoLicenciamientoAmbiental proyecto, FichaAmbientalPma ficha) {
        this.proyecto = proyecto;
        this.ficha = ficha;
        inicializarVariablesSectores();
        String html = UtilFichaMineria
                .extraeHtml(JsfUtil
                        .devolverPathReportesHtml("subreportes/descripcionProceso.html"));
        return UtilFichaMineria.generarHtml(html,
                inicializarObjetoDescripcionProceso());
    }

    public EntityFichaAmbientalDescripcionProceso inicializarObjetoDescripcionProceso() {
        EntityFichaAmbientalDescripcionProceso descripcion = new EntityFichaAmbientalDescripcionProceso();

        descripcion.setActividades(mostrarSubtitulo("Actividades del proceso")
                + mostrarActividadesProceso());

        if (aplicaHerramientas) {
            String herramientas = mostrarHerramientas();
            if(!herramientas.isEmpty()) {
                descripcion
                        .setHerramientas(mostrarSubtitulo("Equipos y herramientas")
                                + herramientas);
            }
        }
        /*if (false && aplicaOrigenRecursoAgua) {
            descripcion
                    .setRecursoAgua(mostrarSubtitulo("Origen del recurso agua")
                            + mostrarOrigenRecursoAgua());
        }*/
        if (aplicaInsumos) {
            String insumos = mostrarInsumos();
            if (!insumos.isEmpty()) {
                descripcion.setInsumos(mostrarSubtitulo("Materiales e insumos")
                        + insumos);
            }
        }

        descripcion.setTecnicasProceso(mostrarTecnicasProceso());

        descripcion.setInstalacionesProceso(mostrarInstalacionesProceso());
        descripcion.setPlaguicidasProceso(mostrarUsoPlaguicidas());

        descripcion.setFertiliantesProceso(mostrarFertilizante());
        descripcion.setDisposicionFinal(mostrarDisposicionFinal());

        descripcion.setDesechosSanitarios(mostrarTransporteDesechosSanitarios());

        descripcion.setTransporteDesechos(mostrarTransporteDesechos());

        // ***---
        return descripcion;
    }

    public String mostrarSubtitulo(String texto) {

        return "<p>&nbsp;</p> <div style=\"text-align: left;\"> "
                + "<span style=\"font-weight: bold;\">" + texto
                + " </span> </div> ";
    }

    public String mostrarTexto(String texto) {

        return "<p>&nbsp;</p> <div style=\"text-align: left;\"> "
                + "<span >" + texto + " </span> </div> ";
    }

    private String mostrarTablaSimple(List<String> descripcion) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>&nbsp;</p><table align=\"left\" border=\"0\" cellpadding=\"2\" cellspacing=\"1\" style=\"width:100%\">");
        sb.append("<tbody>");
        for (String elemento : descripcion) {


            sb.append("<tr><td style=\"width: ")
                    .append(100)
                    .append("%; text-align: justify\"> <span style=\"font-size: small;background-color: inherit;\">");
            sb.append(elemento);
            sb.append("<br/></span></td>");
            sb.append("</tr>");
        }

        sb.append("</tbody></table><p>&nbsp;</p>");
        return sb.toString();
    }

    // -----

    /**
     * primero
     */
    private String mostrarActividadesProceso() { // / primero
        List<ActividadProcesoPma> actividadesProceso = new ArrayList<ActividadProcesoPma>();

        List<EntityActividadProceso> actividades = new ArrayList<EntityActividadProceso>();
        if (this.ficha.getId() != null) {//actividadProceso.actividadComercial.categoriaFase.fase.nombre
//            actividadesProceso = fichaAmbientalPmaFacade
//                    .getActividadesProcesosFichaPorIdFicha(this.ficha.getId());
            actividadesProceso = fichaAmbientalPmaFacade.getActividadesProcesosFichaPorIdFicha(this.ficha.getId());
            for (ActividadProcesoPma actividadProcesoPma : actividadesProceso) {
                EntityActividadProceso actividad = new EntityActividadProceso();

                if (actividadProcesoPma.getActividadComercial().getCategoriaFase() != null) {
                    actividad.setFase(actividadProcesoPma.getActividadComercial().getCategoriaFase().getFase().getNombre());
                } else {
                    actividad.setFase(actividadProcesoPma.getDescripcionFaseOtros());
                }
                if (actividadProcesoPma.getDescripcionOtros() != null && !actividadProcesoPma.getDescripcionOtros().isEmpty()) {
                    actividad.setNombre(actividadProcesoPma.getDescripcionOtros());
                } else {
                    actividad.setNombre(actividadProcesoPma.getActividadComercial()
                            .getNombreActividad());
                }
                SimpleDateFormat fechaFormateada = new SimpleDateFormat(
                        "dd/MM/yyyy");
                actividad.setFechaDesde(fechaFormateada
                        .format(actividadProcesoPma.getFechaInicio()));
                actividad.setFechaHasta(fechaFormateada
                        .format(actividadProcesoPma.getFechaFin()));
                actividad.setDescripcion(actividadProcesoPma.getDescripcion());
                actividades.add(actividad);

            }
        }
        String[] columnas = {"Fase", "Actividad", "Fecha desde", "Fecha hasta",
                "Descripción"};
        String[] orden = {"Fase", "Nombre", "FechaDesde", "FechaHasta", "Descripcion"};

        return UtilFichaMineria.devolverDetalle(null, columnas, actividades,
                orden, null);

        // return result;

    }

    /**
     * segundo
     */
    private String mostrarHerramientas() {
        List<HerramientaProcesoPma> herramientasProceso = new ArrayList<HerramientaProcesoPma>();

        List<EntityHerramientaProceso> herramientas = new ArrayList<EntityHerramientaProceso>();
        if (this.ficha.getId() != null) {
            herramientasProceso = fichaAmbientalPmaFacade
                    .getHerramientasProcesosFichaPorIdFicha(this.ficha.getId());
            if (herramientasProceso.size() > 0) {
                for (HerramientaProcesoPma herramientaProcesoPma : herramientasProceso) {
                    EntityHerramientaProceso herramineta = new EntityHerramientaProceso();
                    if (herramientaProcesoPma.getDescripcionOtras() != null && !herramientaProcesoPma.getDescripcionOtras().isEmpty()) {
                        herramineta.setNombre(herramientaProcesoPma.getDescripcionOtras());
                    } else {
                        herramineta.setNombre(herramientaProcesoPma.getHerramienta()
                                .getNombreHerramienta());
                    }
                    herramineta.setCantidad(herramientaProcesoPma
                            .getCantidadHerramientas().toString());
                    herramientas.add(herramineta);

                }
            } else {
                return "";
            }

        }
        String[] columnas = {"Equipo o Herramienta	", "Cantidad (Unidades)"};

        String[] orden = {"Nombre", "Cantidad"};
        return UtilFichaMineria.devolverDetalle(null, columnas, herramientas,
                orden, null);

        // return result;

    }

    /*private EntityActividadProceso mostrarOrigenRecursoAgua(String label,
                                                            Integer volumen) {
        EntityActividadProceso entidad = new EntityActividadProceso();
        entidad.setNombre(label);
        if (volumen > 0) {
            entidad.setDuracion(volumen.toString());
            String medida = "(m3)";
            entidad.setDescripcion(medida);
        }
        return entidad;
    }*/

    /**
     * tercero
     */
    /*private String mostrarOrigenRecursoAgua() {
        List<EntityActividadProceso> origenes = new ArrayList<EntityActividadProceso>();
        if (this.ficha.getId() != null) {
            OrigenRecursoAguaProcesoPma origenAgua = fichaAmbientalPmaFacade
                    .getOrigenRecursoAguaProcesosFichaPorIdFicha(this.ficha.getId());

            if (origenAgua != null) {

                if (origenAgua.getVolumenRios() > 0) {
                    origenes.add(mostrarOrigenRecursoAgua(
                            "Ríos, Esteros, Vertientes",
                            origenAgua.getVolumenRios()));

                }
                if (origenAgua.getVolumenLagos() > 0) {
                    origenes.add(mostrarOrigenRecursoAgua("Lagos",
                            origenAgua.getVolumenLagos()));
                }

                if (origenAgua.getVolumenLagunas() > 0) {
                    origenes.add(mostrarOrigenRecursoAgua("Lagunas",
                            origenAgua.getVolumenLagunas()));
                }

                if (origenAgua.getVolumenPozos() > 0) {
                    origenes.add(mostrarOrigenRecursoAgua("Pozos",
                            origenAgua.getVolumenPozos()));
                }

                if (origenAgua.getVolumenAguaEnvasada() > 0) {
                    origenes.add(mostrarOrigenRecursoAgua("Agua envasada",
                            origenAgua.getVolumenAguaEnvasada()));
                }

                if (origenAgua.getVolumenAguaLluvia() > 0) {
                    origenes.add(mostrarOrigenRecursoAgua("Agua de lluvia",
                            origenAgua.getVolumenAguaLluvia()));
                }

                if (origenAgua.getVolumenAguaMar() > 0) {
                    origenes.add(mostrarOrigenRecursoAgua("Agua de mar",
                            origenAgua.getVolumenAguaMar()));
                }

                if (origenAgua.getVolumenAguaSubterranea() > 0) {
                    origenes.add(mostrarOrigenRecursoAgua("Agua subterránea",
                            origenAgua.getVolumenAguaSubterranea()));
                }
            }
        }

        String[] columnas = {"Origen del recurso agua	", "Volumen",
                "Unidad de medida"};

        String[] orden = {"Nombre", "Duracion", "Descripcion"};
        return UtilFichaMineria.devolverDetalle(null, columnas, origenes,
                orden, null);
    }*/

    /**
     * cuarto
     */
    private String mostrarInsumos() {
        List<InsumoProcesoPma> insumosProcesos = new ArrayList<InsumoProcesoPma>();
        List<EntityHerramientaProceso> insumos = new ArrayList<EntityHerramientaProceso>();

        if (this.ficha.getId() != null) {
            insumosProcesos = fichaAmbientalPmaFacade
                    .getInsumosProcesosFichaPorIdFicha(this.ficha.getId());
            if (insumosProcesos.size() > 0) {
                for (InsumoProcesoPma insumoProcesoPma : insumosProcesos) {
                    EntityHerramientaProceso insumo = new EntityHerramientaProceso();
                    if (insumoProcesoPma.getDescripcionOtros() != null && !insumoProcesoPma.getDescripcionOtros().isEmpty()) {
                        insumo.setNombre(insumoProcesoPma.getDescripcionOtros());
                    } else {
                        insumo.setNombre(insumoProcesoPma.getCatalogoInsumo()
                                .getNombreInsumo());
                    }
                    String unidad = insumoProcesoPma.getUnidadMedida() != null ? insumoProcesoPma.getUnidadMedida().getSiglas() : "-";
                    insumo.setCantidad("<span style=\"text-align:left\">" +
                            insumoProcesoPma.getCantidad().toString() + " (" + unidad + ")</span>");
                    insumos.add(insumo);

                }
            } else {
                return "";
            }
        }

        String[] columnas = {"Materiales e insumos",
                "Cantidad"};
        String[] orden = {"Nombre", "Cantidad"};
        return UtilFichaMineria.devolverDetalle(null, columnas, insumos, orden,
                null);
    }


    /**
     * tecnicasProceso
     */
    private String mostrarTecnicasProceso() {

        List<TecnicaProcesoPma> tecnicasProceso = fichaAmbientalPmaFacade.getTecnicasProcesosPorIdFicha(this.ficha.getId());
        if (tecnicasProceso != null && !tecnicasProceso.isEmpty()) {
            List<String> elementos = new ArrayList<String>(tecnicasProceso.size());
            for (TecnicaProcesoPma tecnicaProceso : tecnicasProceso) {
                elementos.add(tecnicaProceso.getCatalogoTecnica().getNombre());

            }

            return mostrarSubtitulo("Técnicas del proceso") + mostrarTablaSimple(elementos);
        }

        return "";

    }

    /**
     * Instalaciones Proceso
     */
    private String mostrarInstalacionesProceso() {
        List<Instalacion> instalacionesProceso = fichaAmbientalPmaFacade.getInstalacionesProcesosFichaPorIdFicha(this.ficha.getId());
        if (instalacionesProceso != null && !instalacionesProceso.isEmpty()) {
            List<String> elementos = new ArrayList<String>(instalacionesProceso.size());
            for (Instalacion instalacion : instalacionesProceso) {
                elementos.add(instalacion.getCatalogoInstalacion().getNombre());

            }

            return mostrarSubtitulo("Instalaciones del proceso") + mostrarTablaSimple(elementos);
        }

        return "";

    }


    /**
     * Plaguicida Proceso
     */
    private String mostrarUsoPlaguicidas() {

        List<PlaguicidaProcesoPma> plaguicidasProceso = fichaAmbientalPmaFacade.getPlagicidasProcesosFichaPorIdFicha(this.ficha.getId());

        if (plaguicidasProceso != null && !plaguicidasProceso.isEmpty()) {

            List<EntityPlaguicidasImprimirReporte> lista = new ArrayList<EntityPlaguicidasImprimirReporte>(plaguicidasProceso.size());

            for (PlaguicidaProcesoPma plaguicida : plaguicidasProceso) {
                EntityPlaguicidasImprimirReporte elemento = new EntityPlaguicidasImprimirReporte();
                elemento.setNombre(plaguicida.getNombreComercial());
                elemento.setCategoria(plaguicida.getCategoriaToxicologica().getDescripcion());
                elemento.setDosis(Double.toString(plaguicida.getDosisAplicacion()));
                elemento.setFrecuencia(plaguicida.getFrecuenciaAplicacion().toString());

                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                elemento.setFecha(df.format(plaguicida.getFechaCaducidad()));
                elemento.setPresentacion(plaguicida.getPresentacion());

                lista.add(elemento);
            }
            String[] columnas = {"Nombre comercial del producto", "Categoría toxicológica",
                    "Dosis de aplicación (kg/ha)", "Frecuencia de aplicación (día)", "Fecha de caducidad", "Presentación"};

            String[] orden = {"Nombre", "Categoria", "Dosis", "Frecuencia", "Fecha", "Presentacion"};
            ;

            return mostrarSubtitulo("Uso de plaguicidas") + UtilFichaMineria.devolverDetalle(null, columnas, lista,
                    orden, null);
        }

        return "";

    }


    /**
     * Instalaciones Proceso
     */
    private String mostrarFertilizante() {
        try {
            List<FertilizanteProcesoPma> fertilizantesProceso = fichaAmbientalPmaFacade.getFertilizantesProcesoPorIdFicha(this.ficha.getId());

            if (fertilizantesProceso.isEmpty()) {
                return "";
            }
            List<EntityFertilizanteImprimirReporte> lista = new ArrayList<EntityFertilizanteImprimirReporte>(fertilizantesProceso.size());
            for (FertilizanteProcesoPma fertilizanteProceso : fertilizantesProceso) {
                EntityFertilizanteImprimirReporte imprimir = new EntityFertilizanteImprimirReporte();
                imprimir.setNombre(fertilizanteProceso.getNombreComercial());
                String organico = fertilizanteProceso.getEsOrganico() ? "Organicos" : "Quimicos";
                imprimir.setOrganico(organico);
                imprimir.setDosis(Double.toString(fertilizanteProceso.getDosisAplicacion()));
                lista.add(imprimir);
            }

            String[] columnas = {"Uso de fertilizante", "Nombre comercial del producto",
                    "Dosis de aplicación (kg/ha)"};
            String[] orden = {"Nombre", "Organico", "Dosis"};
            ;

            return mostrarSubtitulo("Uso de fertilizantes ") + UtilFichaMineria.devolverDetalle(null, columnas, lista,
                    orden, null);


        } catch (Exception e) {
        }


        return "";

    }

    /**
     * Disposicion Final
     */
    private String mostrarDisposicionFinal() {
        String codigoSubsector = proyecto.getCatalogoCategoria().getTipoSubsector().getCodigo();

        if (codigoSubsector.equals("0007")) {
            if (proyecto.getResiduosSolidos() != null && proyecto.getResiduosSolidos() == true) {
                DesechoSanitarioProcesoPma desechoSanitarioProceso = fichaAmbientalPmaFacade.getDesechoSanitarioProcesoPorIdFicha(this.ficha.getId());
                if (desechoSanitarioProceso != null) {
                    String resultado = "";
                    resultado += mostrarSubtitulo("Disposición final");
                    resultado += mostrarTexto("¿Incluye manejo de desechos sanitarios?: Si");
                    resultado += mostrarTexto("Capacidad total de almacenamiento de desechos (ton): " + desechoSanitarioProceso.getCapacidadAlmacenamiento().toString());
                    resultado += mostrarTexto("Métodos de reducción de la peligrosidad de los Desechos que se confinan: " + desechoSanitarioProceso.getMetodoReduccion());
                    resultado += mostrarTexto("Operaciones previas al confinamiento de los desechos: " + desechoSanitarioProceso.getOperacionesPrevias());

                    return resultado;
                } else {
                    return mostrarTexto("¿Incluye manejo de desechos sanitarios?: No");
                }

            }
        }
        return "";

    }

    /**
     * Transporte de desechos sanitarios (Descripción vehículos)
     */
    private String mostrarTransporteDesechosSanitarios() {

        try {
            List<VehiculoDesechoSanitarioProcesoPma> vehiculosDesechoSanitarioProceso = fichaAmbientalPmaFacade
                    .getVehiculosProcesosPorIdFicha(this.ficha.getId());
            if (vehiculosDesechoSanitarioProceso != null && !vehiculosDesechoSanitarioProceso.isEmpty()) {
                List<EntityVehiculosDesechoSanitarioImprimirReporte> lista = new ArrayList<EntityVehiculosDesechoSanitarioImprimirReporte>(vehiculosDesechoSanitarioProceso.size());

                for (VehiculoDesechoSanitarioProcesoPma vehiculo : vehiculosDesechoSanitarioProceso) {
                    EntityVehiculosDesechoSanitarioImprimirReporte elemento = new EntityVehiculosDesechoSanitarioImprimirReporte();
                    elemento.setPlaca(vehiculo.getNumeroPlaca());
                    elemento.setMotor(vehiculo.getNumeroMotor());
                    elemento.setModelo(vehiculo.getModelo());
                    elemento.setTipo(vehiculo.getTipo());
                    elemento.setCilindraje(Double.toString(vehiculo.getCilindraje()));
                    elemento.setPesoBruto(vehiculo.getPbv().toString());
                    elemento.setPesoVeicular(vehiculo.getPv().toString());
                    elemento.setPesoTonelaje(vehiculo.getTonelaje().toString());
                    lista.add(elemento);
                }


                String[] columnas = {"Placa", "No. Motor",
                        "Modelo", "Tipo", "Cilindraje", "Peso bruto vehicular (PBV)", "Peso vehicular (PV)", "Tonelaje"};

                String[] orden = {"Placa", "Motor", "Modelo", "Tipo", "Cilindraje", "PesoBruto", "PesoVeicular", "PesoTonelaje"};
                ;

                return mostrarSubtitulo("Transporte de desechos sanitarios (Descripción vehículos)") + UtilFichaMineria.devolverDetalle(null, columnas, lista,
                        orden, null);

            }
        } catch (Exception e) {
        }


        return "";

    }

    /**
     * Transporte de desechos
     */
    private String mostrarTransporteDesechos() {

        try {
            List<DesechoPeligrosoProcesoPma> desechosPeligrosoProceso = fichaAmbientalPmaFacade.getDesechosPeligrososProcesosPorIdFicha(this.ficha.getId(), true);
            boolean expost = true;
            if (desechosPeligrosoProceso == null) {
                expost = false;
                desechosPeligrosoProceso = fichaAmbientalPmaFacade.getDesechosPeligrososProcesosPorIdFicha(this.ficha.getId(), false);
            }
            if (desechosPeligrosoProceso != null && !desechosPeligrosoProceso.isEmpty()) {
                List<EntityVehiculosDesechoImprimirReporte> lista = new ArrayList<EntityVehiculosDesechoImprimirReporte>(desechosPeligrosoProceso.size());

                for (DesechoPeligrosoProcesoPma desecho : desechosPeligrosoProceso) {
                    EntityVehiculosDesechoImprimirReporte elemento = new EntityVehiculosDesechoImprimirReporte();
                    elemento.setDesecho(desecho.getDesechoPeligroso().getDescripcion());
                    elemento.setCapacidad(desecho.getCapacidadRecoleccion().toString());
                    if (expost) {
                        elemento.setTipo(desecho.getVehiculo().getModelo());
                    } else {
                        elemento.setTipo(desecho.getVehiculoDescripcionExantes());
                    }
                    elemento.setTipoEmbalaje(desecho.getTipoEnvalaje());
                    lista.add(elemento);
                }


                String[] columnas = {"Desecho Sanitario ", "Capacidad de recolección y transporte",
                        "Tipo de vehículo", "Tipo de Embalaje/Envase"};

                String[] orden = {"Desecho", "Capacidad", "Tipo", "TipoEmbalaje"};
                ;

                return mostrarSubtitulo("Desechos peligrosos") + UtilFichaMineria.devolverDetalle(null, columnas, lista,
                        orden, null);

            }
        } catch (Exception e) {
        }


        return "";

    }

    public void inicializarVariablesSectores() {

        try {
            categoriaFaseList = faseFichaAmbientalFacade
                    .obtenerCatalogoCategoriaFasesPorFicha(this.ficha.getId());
            if (categoriaFaseList.get(0).getTipoSubsector().getCodigo() != "0007") {
                aplicaHerramientas = true;
                //aplicaOrigenRecursoAgua = true;
                aplicaInsumos = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}