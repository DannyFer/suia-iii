/**
 * Copyright (c) 2015 MAGMASOFT (Innovando tecnologia)
 * Todos los derechos reservados.
 * Este software es confidencial y debe usarlo de acorde con los términos de uso.
 */
package ec.gob.ambiente.suia.eia.mediofisico.bean;

import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.eia.mediofisico.facade.CalidadComponenteFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase que
 *
 * @author Juan Gabriel Guzmán.
 * @version 1.0
 */
@ManagedBean
@ViewScoped
public class CalidadComponenteBean {

    protected static final Integer ID_FACTOR_SUELO = 2;
    protected static final Integer ID_FACTOR_AIRE = 4;
    protected static final Integer ID_FACTOR_AGUA = 7;
    protected static final Integer ID_FACTOR_RUIDO = 10;
    protected static final Integer ID_FACTOR_RADIACION = 13;
    //protected static final Integer ID_FACTOR_PRESION_SONORA = 13;
    private static final Logger LOG = Logger.getLogger(CalidadComponenteBean.class);
    @Getter
    protected EstudioImpactoAmbiental estudio;
    @Setter
    protected List<Laboratorio> laboratorios;
    @Setter
    protected List<Muestra> muestras;
    @Setter
    protected List<Normativas> normativas;
    @Setter
    protected List<ResultadoAnalisis> resultados;
    @Setter
    protected List<ParametrosNormativas> parametros;
    @Setter
    protected List<CalidadParametro> calidades;
    @Getter
    @Setter
    protected List<Muestra> muestrasAdicionar;
    @Getter
    @Setter
    protected Muestra muestraActiva;
    @Setter
    @Getter
    protected List<ResultadoAnalisis> resultadosEliminados;
    @Getter
    private String labelCabeceraCalidadComponente;
    @Getter
    @Setter
    private Laboratorio laboratorio;
    @Getter
    @Setter
    private Muestra muestra;
    @Getter
    @Setter
    private Normativas normativa;
    @Getter
    @Setter
    private List<Laboratorio> coincidenciasLaboratorios;
    @Getter
    @Setter
    private TablasNormativas tabla;
    @Setter
    private List<TablasNormativas> tablas;
    @Getter
    @Setter
    private ResultadoAnalisis resultado;
    @Setter
    @Getter
    private FactorPma componente;
    @Setter
    private List<CalidadParametro> calidadesAlmacenadas;
    @Getter
    @Setter
    private CalidadParametro calidadComponente;
    @Setter
    private List<CuerpoHidrico> cuerposHidricos;
    @Setter
    @Getter
    private boolean mostrarColumnaValor = true;
    @Setter
    @Getter
    private boolean mostrarColumnaValorExcepcional = false;
    @Getter
    private String validateFieldText;
    @EJB
    private CalidadComponenteFacade calidadComponenteFacade;


    private void inicializarComponenteSuelo() {
        setComponente(calidadComponenteFacade.getComponente(ID_FACTOR_SUELO));
    }

    private void inicializarComponenteAire() {
        setComponente(calidadComponenteFacade.getComponente(ID_FACTOR_AIRE));
    }

    private void inicializarComponenteAgua() {
        setComponente(calidadComponenteFacade.getComponente(ID_FACTOR_AGUA));
    }

    private void inicializarComponenteRuido() {
        setComponente(calidadComponenteFacade.getComponente(ID_FACTOR_RUIDO));
    }

    private void inicializarComponenteRadiacion() {
        setComponente(calidadComponenteFacade.getComponente(ID_FACTOR_RADIACION));
    }

    private void inicializarComponentePresionSonora() {
        setComponente(calidadComponenteFacade.getComponente(ID_FACTOR_RUIDO));
    }


    @PostConstruct
    private void init() {
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = origRequest.getRequestURL().toString();
        if (url.contains("Suelo")) {
            inicializarComponenteSuelo();
            inicializarLabelComponenteSuelo();
        } else if (url.contains("Aire")) {
            inicializarComponenteAire();
            inicializarLabelComponenteAire();
        } else if (url.contains("Agua")) {
            inicializarComponenteAgua();
            inicializarLabelComponenteAgua();
        } else if (url.toLowerCase().contains("sonora") && !url.contains("PresionSonora")) {
            inicializarComponenteRuido();
            labelCabeceraCalidadComponente = "Nivel de presión sonora";
        } else if (url.toLowerCase().contains("radiacion")) {
            inicializarComponenteRadiacion();
            labelCabeceraCalidadComponente = "Radiaciones no ionizantes";
        } else if (url.contains("PresionSonora")) {
            inicializarComponentePresionSonora();
            labelCabeceraCalidadComponente = "Nivel de presión sonora";
        }
        inicializarVariables();
        if (url.contains("Agua")) {
            if (cuerposHidricos == null || cuerposHidricos.size() == 0) {
                RequestContext.getCurrentInstance().execute("PF('dlgInfo').show();");
            }
        }


    }

    private void inicializarLabelComponenteSuelo() {
        labelCabeceraCalidadComponente = "Características químicas del suelo";
    }

    private void inicializarLabelComponenteAire() {
        labelCabeceraCalidadComponente = "Calidad del aire";
    }

    private void inicializarLabelComponenteAgua() {
        labelCabeceraCalidadComponente = "Calidad del agua";
    }


    private void inicializarVariables() {
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = origRequest.getRequestURL().toString();
        try {
            laboratorio = new Laboratorio();
            muestra = new Muestra();
            estudio = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
            normativas = calidadComponenteFacade.getNormativas(estudio, getComponente());
            calidades = calidadComponenteFacade.getCalidadComponente(estudio, getComponente());

            laboratorios = calidadComponenteFacade.getLaboratorios(calidades);
            muestras = calidadComponenteFacade.getMuestras(calidades);


            cuerposHidricos = calidadComponenteFacade.getCuerposHidricos(estudio);
            muestrasAdicionar = new ArrayList<>();
            if (!calidades.isEmpty()) {
                getCalidadesAlmacenadas().addAll(calidades);
            }
        } catch (ServiceException e) {
            LOG.error(e, e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
        }
    }

    public List<Laboratorio> getLaboratorios() {
        return laboratorios == null ? laboratorios = new ArrayList<Laboratorio>() : laboratorios;
    }

    public List<Muestra> getMuestras() {
        return muestras == null ? muestras = new ArrayList<Muestra>() : muestras;
    }

    public List<Normativas> getNormativas() {
        return normativas == null ? normativas = new ArrayList<Normativas>() : normativas;
    }

    public List<TablasNormativas> getTablas() {
        return tablas == null ? tablas = new ArrayList<TablasNormativas>() : tablas;
    }


    public List<ResultadoAnalisis> getResultados() {
        return resultados == null ? resultados = new ArrayList<ResultadoAnalisis>() : resultados;
    }

    public List<ParametrosNormativas> getParametros() {
        return parametros == null ? parametros = new ArrayList<ParametrosNormativas>() : parametros;
    }


    public List<CalidadParametro> getCalidades() {
        return calidades == null ? calidades = new ArrayList<CalidadParametro>() : calidades;
    }


    public List<CalidadParametro> getCalidadesAlmacenadas() {
        return calidadesAlmacenadas == null ? calidadesAlmacenadas = new ArrayList<CalidadParametro>() : calidadesAlmacenadas;
    }

    public List<CuerpoHidrico> getCuerposHidricos() {
        return cuerposHidricos == null ? cuerposHidricos = new ArrayList<CuerpoHidrico>() : cuerposHidricos;
    }

    /***
     * @param parametroExcepcional
     * @return
     */
    public void cambiarColumna() {
        boolean parametroExcepcional = calidadComponente.getParametroNormativas().isTipoExcepcional();
        if (!parametroExcepcional) {//Si se desea renderear la columna valor
            this.mostrarColumnaValor = true;
            this.mostrarColumnaValorExcepcional = false;
        } else {

            this.mostrarColumnaValor = false;
            this.mostrarColumnaValorExcepcional = true;
        }
    }


    public void setValidateFieldText(boolean tipoExcepcional) {
        if (tipoExcepcional) {
            this.validateFieldText = "Criterio";
        } else {
            this.validateFieldText = "Límite";
        }
    }

   /* public void eliminarResultado(ResultadoAnalisis resultado) {

        resultadosEliminados.add(resultado);
        calidadComponente.getResultadosAnalisis().remove(resultado);


    }*/

    public void limpiarResultadosEliminados() {
        this.resultadosEliminados = new ArrayList<ResultadoAnalisis>();
    }

    public void eliminarResultado(ResultadoAnalisis resultado) {

        this.resultadosEliminados.add(resultado);
        int i = 0;
        boolean found = false;

        while (!found && i < this.muestras.size()) {
            if (resultado.getMuestra().getId() != null) {
                if (this.muestras.get(i).getId().equals(resultado.getMuestra().getId())) {
                    found = true;
                    this.muestrasAdicionar.add(this.muestras.get(i));
                } else {
                    i++;
                }
            } else {
                found = true;
                if (!this.muestrasAdicionar.contains(resultado.getMuestra())) {
                    this.muestrasAdicionar.add(resultado.getMuestra());
                }
            }
        }

    }

    public void persistirEliminacionResultados() {

        if (!this.resultadosEliminados.isEmpty()) {
            int i = 0;
            for (ResultadoAnalisis res : this.resultadosEliminados) {
                if (calidadComponente.getResultadosAnalisis().contains(res) && calidadComponente.getResultadosAnalisis().get(i).getId() != null) {
                    calidadComponente.getResultadosAnalisis().get(i).setEstado(false);
                } else {
                    calidadComponente.getResultadosAnalisis().remove(res);
                }
                i++;
            }

            limpiarResultadosEliminados();
        }
    }

    public boolean mostrarResultado(ResultadoAnalisis resultado) {

        if (resultado != null && this.resultadosEliminados != null && this.resultadosEliminados.contains(resultado)) {
            return false;
        } else {
            return true;
        }

    }

    public Date getCurrentDate() {
        return new Date();
    }


}
