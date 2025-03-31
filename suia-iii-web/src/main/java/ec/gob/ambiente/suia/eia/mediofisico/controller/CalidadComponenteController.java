/**
 * Copyright (c) 2015 MAGMASOFT (Innovando tecnologia)
 * Todos los derechos reservados.
 * Este software es confidencial y debe usarlo de acorde con los términos de uso.
 */
package ec.gob.ambiente.suia.eia.mediofisico.controller;

import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.eia.mediofisico.bean.CalidadComponenteBean;
import ec.gob.ambiente.suia.eia.mediofisico.facade.CalidadComponenteFacade;
import ec.gob.ambiente.suia.eia.resumenEjecutivo.bean.ResumenEjecutivoBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que
 *
 * @author Juan Gabriel Guzmán.
 * @version 1.0
 */
@ViewScoped
@ManagedBean
public class CalidadComponenteController implements Serializable {


    protected static final String URL_PAGINA_CALIDAD_SUELO = "/prevencion/licenciamiento-ambiental/eia/medioFisico/calidadSuelo.jsf";
    protected static final String URL_PAGINA_CALIDAD_AGUA = "/prevencion/licenciamiento-ambiental/eia/medioFisico/calidadAgua.jsf";
    protected static final String URL_PAGINA_CALIDAD_AIRE = "/prevencion/licenciamiento-ambiental/eia/medioFisico/calidadAire.jsf";
    protected static final String URL_PAGINA_CALIDAD_SONORO = "/prevencion/licenciamiento-ambiental/eia/medioFisico/nivelPresionSonora.jsf";
    protected static final String URL_PAGINA_CALIDAD_RADIACION = "/prevencion/licenciamiento-ambiental/eia/medioFisico/radiacionNoIonizantes.jsf";
    private static final Logger LOG = Logger.getLogger(CalidadComponenteController.class);
    @EJB
    private CalidadComponenteFacade calidadComponenteFacade;

    @Setter
    @ManagedProperty(value = "#{calidadComponenteBean}")
    private CalidadComponenteBean calidadComponenteBean;

    @EJB
    private ValidacionSeccionesFacade validacionSeccionesFacade;

    @Getter
    @Setter
    private String linkPrev;

    @Getter
    @Setter
    private String linkNext;
    @Getter
    @Setter
    private boolean presionSonora;

    private CalidadParametro calidadSeleccionada = new CalidadParametro();
    private boolean editMode = false;

    @PostConstruct
    public void init() {
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = origRequest.getRequestURL().toString();

        if (url.contains("Suelo")) {
            linkPrev = "/prevencion/licenciamiento-ambiental/eia/medioFisico/calidadAire.jsf?id=12";
           // linkNext = "/prevencion/licenciamiento-ambiental/eia/adjuntos/adjuntos.jsf?id=13";
            linkNext = "/prevencion/licenciamiento-ambiental/eia/medioFisico/nivelPresionSonora.jsf?id=12";
        } else if (url.contains("Aire")) {
            linkPrev = "/prevencion/licenciamiento-ambiental/eia/medioFisico/calidadAgua.jsf?id=12";
            linkNext = "/prevencion/licenciamiento-ambiental/eia/medioFisico/calidadSuelo.jsf?id=12";
        } else if (url.contains("Agua")) {
            linkPrev = "/prevencion/licenciamiento-ambiental/eia/medioFisico/fisicoMecanicaSuelo.jsf?id=12";
            linkNext = "/prevencion/licenciamiento-ambiental/eia/medioFisico/calidadAire.jsf?id=12";
        } else if (url.toLowerCase().contains("sonora")) {
            linkPrev = "/prevencion/licenciamiento-ambiental/eia/medioFisico/calidadSuelo.jsf?id=12";
               if  (JsfUtil.getBean(ResumenEjecutivoBean.class).isProyectoHidrocarburos())
                {
                   linkNext = "/prevencion/licenciamiento-ambiental/eia/medioFisico/identificacionSitiosContaminadosFuentesContaminacion.jsf?id=12";
                }
               else {
                   linkNext = "/prevencion/licenciamiento-ambiental/eia/medioFisico/radiacionNoIonizantes.jsf?id=12";
                    }

        }else if (url.toLowerCase().contains("radiacion")) {
            linkPrev = "/prevencion/licenciamiento-ambiental/eia/medioFisico/nivelPresionSonora.jsf?id=12";
            linkNext = "/prevencion/licenciamiento-ambiental/eia/medioFisico/identificacionSitiosContaminadosFuentesContaminacion.jsf?id=12";
        }
        if (url.contains("Sonora")) {
            presionSonora=true;
        }

    }


    public void guardar() {

        try {
            boolean muestraAdicionada = true;
            StringBuilder sb = null;
            int conrador = 0;
          //if( !JsfUtil.getBean(ResumenEjecutivoBean.class).isProyectoHidrocarburos()){

               sb = new StringBuilder();
               String separador = "";

               for (Muestra muestra : calidadComponenteBean.getMuestras()) {
                   int cont = 0;
                   boolean encontrado = false;
                   while (!encontrado && calidadComponenteBean.getCalidades().size() > cont) {
                       int i = 0;
                       while (!encontrado && calidadComponenteBean.getCalidades().get(cont).getResultadosAnalisis().size() > i) {
                           encontrado = calidadComponenteBean.getCalidades().get(cont).getResultadosAnalisis().get(i).getMuestra().equals(muestra);
                           i++;
                       }
                       cont++;
                   }
                   if (!encontrado) {
                       sb.append(separador + muestra.getCodigo());
                       separador = ", ";
                       muestraAdicionada = false;
                       conrador++;
                   }
               }

          // }

            if (muestraAdicionada) {
                calidadComponenteFacade.guardar(calidadComponenteBean.getCalidadesAlmacenadas(), calidadComponenteBean.getCalidades(), calidadComponenteBean.getEstudio(), calidadComponenteBean.getComponente());
                JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

                HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                String url = origRequest.getRequestURL().toString();

                String seccion = "";
                if (url.contains("Suelo")) {
                    seccion = "calidadSuelo";
                }
                if (url.contains("Aire")) {
                    seccion = "calidadAire";
                }
                if (url.contains("Agua")) {
                    seccion = "calidadAgua";
                }

                if (url.toLowerCase().contains("sonora")&& !url.contains("PresionSonora")) {
                    seccion = "nivelPresionSonora";
                }
                if (url.toLowerCase().contains("radiacion")) {
                    seccion = "radiacionNoIonizantes";
                }
                if (url.contains("PresionSonora")) {
                    seccion = "nivelPresionSonora";
                }
                validacionSeccionesFacade.guardarValidacionSeccion("EIA",
                        seccion, calidadComponenteBean.getEstudio().getId()
                                .toString());
            } else {
                if (conrador > 1) {
                    JsfUtil.addMessageError("Las muestras (" + sb.toString() + ") no han sido incluida en ningún resultado del punto " + calidadComponenteBean.getLabelCabeceraCalidadComponente() + ".");
                } else {
                    JsfUtil.addMessageError("La muestra '" + sb.toString() + "' no ha sido incluida en ningún resultado del punto" + calidadComponenteBean.getLabelCabeceraCalidadComponente() + ".");
                }
            }

        } catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            LOG.error(e, e);
        }

    }

    public void cancelar() {
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = origRequest.getRequestURL().toString();
        if (url.contains("Suelo")) {
            JsfUtil.redirectTo(URL_PAGINA_CALIDAD_SUELO);
        } else if (url.contains("Aire")) {
            JsfUtil.redirectTo(URL_PAGINA_CALIDAD_AIRE);
        } else if (url.contains("Agua")) {
            JsfUtil.redirectTo(URL_PAGINA_CALIDAD_AGUA);
        } else if (url.toLowerCase().contains("sonora")) {
            JsfUtil.redirectTo(URL_PAGINA_CALIDAD_SONORO);
        }else if (url.toLowerCase().contains("radiacion")) {
            JsfUtil.redirectTo(URL_PAGINA_CALIDAD_RADIACION);
        }
    }

    public void resetMuestra() {
        resetCalidad();
        calidadComponenteBean.setCalidadComponente(new CalidadParametro());
        calidadComponenteBean.setMuestra(new Muestra());
        CoordenadaGeneral coordenadaGeneral = new CoordenadaGeneral();
        calidadComponenteBean.getMuestra().setCoordenadaGeneral(coordenadaGeneral);
    }

    public void seleccionarMuestra(Muestra muestra) {
        calidadComponenteBean.setMuestra(muestra);
    }

    public void agregarMuestra() {
        if (!calidadComponenteBean.getMuestras().contains(calidadComponenteBean.getMuestra())) {
            calidadComponenteBean.getMuestras().add(calidadComponenteBean.getMuestra());
            ResultadoAnalisis resultado = new ResultadoAnalisis();
            resultado.setMuestra(calidadComponenteBean.getMuestra());
            calidadComponenteBean.getResultados().add(resultado);
            if(this.presionSonora){
                calidadComponenteBean.getCalidadComponente().setResultadosAnalisis(calidadComponenteBean.getResultados());
                agregarCalidad();
            }
        }
        calidadComponenteBean.setMuestra(new Muestra());
        JsfUtil.addCallbackParam("addMuestra");
    }


    public void eliminarMuestra(Muestra muestra) {

        int cont = 0;
        boolean encontrado = false;
        while (!encontrado && calidadComponenteBean.getCalidades().size() > cont) {
            int i = 0;
            while (!encontrado && calidadComponenteBean.getCalidades().get(cont).getResultadosAnalisis().size() > i) {
                encontrado = calidadComponenteBean.getCalidades().get(cont).getResultadosAnalisis().get(i).getMuestra().equals(muestra);
                i++;
            }
            cont++;
        }
        if (presionSonora || !encontrado) {
            calidadComponenteBean.getMuestras().remove(muestra);
        } else {

            JsfUtil.addMessageError("El muestra que desea eliminar está en uso en " + calidadComponenteBean.getLabelCabeceraCalidadComponente() + ".");
        }
    }


    public List<Laboratorio> buscarCoincidenciasLaboratorio(String query) {
        calidadComponenteBean.setCoincidenciasLaboratorios(calidadComponenteFacade.getLaboratorios(query));
        return calidadComponenteBean.getCoincidenciasLaboratorios();
    }

    public void agregarLaboratorio() {
        if (!calidadComponenteBean.getLaboratorios().contains(calidadComponenteBean.getLaboratorio())) {
            calidadComponenteBean.getLaboratorios().add(calidadComponenteBean.getLaboratorio());
        }
        calidadComponenteBean.setLaboratorio(new Laboratorio());
        JsfUtil.addCallbackParam("addLaboratorio");
    }

    public void resetLaboratorio() {
        seleccionarLaboratorio(new Laboratorio());
    }

    public void seleccionarLaboratorio(Laboratorio laboratorio) {
        calidadComponenteBean.setLaboratorio(laboratorio);
    }

    public void resetCalidad() {
        calidadComponenteBean.setNormativa(new Normativas());
        calidadComponenteBean.setCalidadComponente(new CalidadParametro());
        calidadComponenteBean.getCalidadComponente().setResultadosAnalisis(new ArrayList<ResultadoAnalisis>());
        editMode = false;
        calidadComponenteBean.setTabla(null);
        calidadComponenteBean.setResultados(new ArrayList<ResultadoAnalisis>());
        calidadComponenteBean.limpiarResultadosEliminados();
        for (Muestra muestra : calidadComponenteBean.getMuestras()) {
            ResultadoAnalisis resultado = new ResultadoAnalisis();
            resultado.setMuestra(muestra);
            calidadComponenteBean.getResultados().add(resultado);
        }
        actualizarResultadosAnalisis();
    }

    public void seleccionarResultado(ResultadoAnalisis resultado) {
        calidadComponenteBean.setResultado(resultado);
    }

    public void cargarTablas() {
        try {
            calidadComponenteBean.setTablas(calidadComponenteFacade.getTablasNormativa(calidadComponenteBean.getNormativa()));
            //  calidadComponenteBean.getCalidadComponente().setParametroNormativas(null);
        } catch (ServiceException e) {
            LOG.error("No se pudo recuperar la lista de tablas asociadas a la normativa " + calidadComponenteBean.getNormativa().getDescripcion(), e);
        }
    }

    public void cargarParametros() {
        try {
            calidadComponenteBean.setParametros(calidadComponenteFacade.getParametros(calidadComponenteBean.getTabla()));
        } catch (ServiceException e) {
            LOG.error("No se pudo recuperar la lista de parámetros asociada a la tabla " + calidadComponenteBean.getTabla());
        }
    }

    public void agregarCalidad() {


        //Se ejecutan las eliminaciones de resultados en memoria.
        calidadComponenteBean.persistirEliminacionResultados();
        if (calidadComponenteBean.getCalidadComponente().getResultadosAnalisis().size() == 0) {

            eliminarCalidad(calidadSeleccionada);
        }

        if (!calidadComponenteBean.getCalidades().contains(calidadComponenteBean.getCalidadComponente())) {
            // calidadComponenteBean.getCalidadComponente().setResultadosAnalisis(calidadComponenteBean.getResultados());
            if (this.calidadComponenteBean.isMostrarColumnaValorExcepcional()) {
                int i = 0;
                for (ResultadoAnalisis res : calidadComponenteBean.getCalidadComponente().getResultadosAnalisis()) {
                    res.setValor(new Double(-1));
                    calidadComponenteBean.getCalidadComponente().getResultadosAnalisis().set(i, res);
                    i++;
                }
            }
            calidadComponenteBean.getCalidades().add(calidadComponenteBean.getCalidadComponente());


        }
        //  calidadComponenteBean.setCalidadComponente(new CalidadParametro());
        JsfUtil.addCallbackParam("addCalidad");


    }

    public void seleccionarCalidad(CalidadParametro calidad) {
        /*
                            <h:outputText value="#{calidad.parametroNormativas.tablaNormativas.normativa.descripcion}"/>

                            <h:outputText value="#{calidad.parametroNormativas.tablaNormativas}"/>

                            <h:outputText value="#{calidad.parametroNormativas}"/>
        * */
        calidadComponenteBean.setCalidadComponente(calidad);
        removeResultsWithoutValue();
        //calidad.parametroNormativas.tablaNormativas.normativa
        calidadComponenteBean.setNormativa(calidad.getParametroNormativas().getTablaNormativas().getNormativa());
        cargarTablas();
        calidadComponenteBean.setTabla(calidad.getParametroNormativas().getTablaNormativas());
        calidadComponenteBean.getCalidadComponente().setParametroNormativas(calidad.getParametroNormativas());
        // calidadComponenteBean.setTabla(calidad.getParametroNormativas().getTablaNormativas());
        cargarParametros();
        calidadComponenteBean.setResultados(calidad.getResultadosAnalisis());

        calidadComponenteBean.setValidateFieldText(calidad.getParametroNormativas().isTipoExcepcional());
        editMode = true;
        this.calidadSeleccionada = calidad;
        calidadComponenteBean.limpiarResultadosEliminados();
        actualizarResultadosAnalisis();
    }


    public void eliminarCalidad(CalidadParametro calidadParametro) {
        calidadComponenteBean.getCalidades().remove(calidadParametro);
    }

    public void validateFormulario(FacesContext context, UIComponent validate, Object value) {
    }

    public void eliminarLaboratorio(Laboratorio laboratorio) {
        int cont = 0;
        boolean encontrado = false;
        while (!encontrado && calidadComponenteBean.getCalidades().size() > cont) {
            encontrado = calidadComponenteBean.getCalidades().get(cont).getLaboratorio().equals(laboratorio);
            cont++;
        }
        if (presionSonora ||!encontrado) {
            calidadComponenteBean.getLaboratorios().remove(laboratorio);
        } else {

            JsfUtil.addMessageError("El laboratorio que desea eliminar está en uso en " + calidadComponenteBean.getLabelCabeceraCalidadComponente() + ".");
        }

    }

    public void cerrarCalidadComponente() {
        try {

            JsfUtil.addCallbackParam("addCalidad");

        } catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            LOG.error(e, e);
        }
    }

    public void linkNextVal() {
        JsfUtil.redirectTo(this.linkNext);
    }

    public void linkPrevVal() {
        JsfUtil.redirectTo(this.linkPrev);
    }

    public void adicionarResultadosAnalisis() {
        if (calidadComponenteBean.getMuestraActiva() != null) {
            ResultadoAnalisis resultado = new ResultadoAnalisis();
            resultado.setMuestra(calidadComponenteBean.getMuestraActiva());
            calidadComponenteBean.getCalidadComponente().getResultadosAnalisis().add(resultado);
            calidadComponenteBean.setMuestraActiva(null);
            actualizarResultadosAnalisis();

        } else {
            JsfUtil.addMessageError("Debe seleccionar una muestra.");
        }
    }

    public void removeResultsWithoutValue() {
        ResultadoAnalisis res;
        for (int i = 0; i < calidadComponenteBean.getCalidadComponente().getResultadosAnalisis().size(); i++) {
            res = calidadComponenteBean.getCalidadComponente().getResultadosAnalisis().get(i);
            Double value = res.getValor();
            if (value == null) {
                calidadComponenteBean.getCalidadComponente().getResultadosAnalisis().remove(i);
            }
        }
    }


    public void actualizarResultadosAnalisis() {
        calidadComponenteBean.setMuestrasAdicionar(new ArrayList<Muestra>());
        List<Muestra> listaMuestra = new ArrayList<>();
        if (calidadComponenteBean.getCalidadComponente().getResultadosAnalisis() != null) {
            for (ResultadoAnalisis r : calidadComponenteBean.getCalidadComponente().getResultadosAnalisis()) {
                listaMuestra.add(r.getMuestra());
            }
        }
        for (Muestra muestra : calidadComponenteBean.getMuestras()) {
            if (!listaMuestra.contains(muestra)) {
                calidadComponenteBean.getMuestrasAdicionar().add(muestra);
            }
        }

    }


}
