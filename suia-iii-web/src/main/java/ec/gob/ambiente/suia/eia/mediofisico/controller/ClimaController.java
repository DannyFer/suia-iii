/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.mediofisico.controller;

import ec.gob.ambiente.suia.domain.Clima;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.eia.mediofisico.bean.ClimaBean;
import ec.gob.ambiente.suia.eia.mediofisico.facade.ClimaMedioFisicoFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author martin
 */
@ManagedBean
@ViewScoped
public class ClimaController implements Serializable {

    private static final long serialVersionUID = 4128346690041487900L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ClimaController.class);

    @EJB
    private ClimaMedioFisicoFacade climaMedioFisicoFacade;
    @Getter
    @Setter
    private ClimaBean climaBean;
    @EJB
    private ValidacionSeccionesFacade validacionSeccionesFacade;

    /**
     *
     */
    @PostConstruct
    public void inicio() {
        climaBean = new ClimaBean();
        EstudioImpactoAmbiental es = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
        climaBean.setEstudioImpactoAmbiental(es);

        climaBean.setClima(new Clima());
        climaBean.setListaClimas(new ArrayList<Clima>());
        climaBean.setListaEntidadesRemover(new ArrayList());

        recuperarClima();

    }

    private void recuperarClima() {
        try {
            climaBean.setListaClimas(climaMedioFisicoFacade.climaXEiaId(climaBean.getEstudioImpactoAmbiental()));
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    /**
     *
     */
    public void agregarClima() {
        climaBean.setClima(new Clima());
        climaBean.getClima().setEditar(false);
        climaBean.getClima().setEstudioImpactoAmbiental(climaBean.getEstudioImpactoAmbiental());
    }

    /**
     *
     */
    public void seleccionarClima(Clima clima) {
        clima.setEditar(true);
        climaBean.setClima(clima);
    }

    /**
     *
     *
     */
    public void removerClima(Clima clima) {
        try {
            climaBean.getListaClimas().remove(clima);
            if (clima.getId() != null) {
                clima.setEstado(false);
                climaBean.getListaEntidadesRemover().add(clima);
            }
            //HEREEEE
        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }

    /**
     *
     */
    public void guardarClima() {
        try {
            if (!climaBean.getClima().isEditar()) {
                climaBean.getListaClimas().add(climaBean.getClima());
            }

            JsfUtil.addCallbackParam("addClima");

        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }

    /**
     *
     */
    public void cerrarClima() {
        try {

            JsfUtil.addCallbackParam("addClima");
            climaBean.setListaClimas(new ArrayList<Clima>());

        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }

    private void reasignarIndiceClima() {
        int i = 0;
        for (Clima in : climaBean.getListaClimas()) {
            in.setIndice(i);
            i++;
        }
    }


    /**
     *
     */
    public void guardar() {
        try {
            //climaBean.getClima().setEstudioImpactoAmbiental(climaBean.getEstudioImpactoAmbiental());
            climaMedioFisicoFacade.guardar(climaBean.getListaClimas(), climaBean.getListaEntidadesRemover());
            climaBean.setListaEntidadesRemover(new ArrayList());
            recuperarClima();

            validacionSeccionesFacade.guardarValidacionSeccion("EIA",
                    "clima", climaBean
                            .getEstudioImpactoAmbiental().getId()
                            .toString());
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        } catch (ServiceException e) {
            JsfUtil.addMessageError(e.getMessage());
            e.printStackTrace();
            LOG.error(e, e);
        }
    }

    public void cerrar() {
        try {

            JsfUtil.addCallbackParam("addClima");

        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }


    /***
     * Método que permite validar los rangos del clima. El valor "999999" se utiliza
     * para fijar el máximo posible para el campo, ya que en la página @climaModal@ se especificó
     * un máximo de 6 caracteres para cada componente. Si aumenta o disminuye esta restricción se debe modificar
     * el valor "999999" indicando un "9" por cada caracter admitido.
     *
     * @param type
     * @return
     */
    public double validarRango(String type) {
        Double result;
        //Rango de precipitación
        if (type.equals("precipitacion")) {
            result = this.climaBean.getClima().getPrecipitacionMaxima();
            if (result == null)
                result = Double.valueOf(999999);
        } else {
            if (type.equals("temperatura")) {
                result = this.climaBean.getClima().getTemperaturaMaxima();
                if (result == null)
                    result = Double.valueOf(999999);
            } else {
                if (type.equals("humedad")) {
                    result = this.climaBean.getClima().getHumedadMaxima();
                    if (result == null)
                        result = Double.valueOf(999999);
                } else {
                    if (type.equals("velocidad")) {
                        result = this.climaBean.getClima().getVelocidadVientoMaxima();
                        if (result == null)
                            result = Double.valueOf(999999);
                    } else {//Evapotranspiracion
                        result = this.climaBean.getClima().getEvapotranspiracionMaxima();
                        if (result == null)
                            result = Double.valueOf(999999);
                    }
                }
            }
        }
        return result;
    }

    public void validatePromedio(FacesContext context, UIComponent validate, Object value) {
        String componentId = validate.getId();
        Double max, min;
        String type = componentId.substring(0, componentId.length() - 8);
        double prom_value = (Double) value;

        if (type.equals("precipitacion")) {
                max = this.climaBean.getClima().getPrecipitacionMaxima();
                min = this.climaBean.getClima().getPrecipitacionMinima();
                if (max != null && min != null) {
                    if (min.doubleValue() == max.doubleValue()){
                        if (prom_value != min.doubleValue())
                            throw new ValidatorException(
                                    new FacesMessage(
                                            FacesMessage.SEVERITY_ERROR,
                                            "El valor del campo 'Promedio' debe ser igual a los valor de los campos 'Mínimo' y 'Máximo'.",
                                            null));
                    }
                    else
                        if (!(min.doubleValue() < prom_value && prom_value < max.doubleValue()))
                                throw new ValidatorException(
                                        new FacesMessage(
                                                FacesMessage.SEVERITY_ERROR,
                                                "El valor del campo 'Promedio' debe ser mayor que el valor del campo 'Mínimo' y menor que el valor del campo 'Máximo'.",
                                                null));
                }
        }
        else {
                if (type.equals("temperatura")) {
                    max = this.climaBean.getClima().getTemperaturaMaxima();
                    min = this.climaBean.getClima().getTemperaturaMinima();
                    if (max != null && min != null) {
                        if (min.doubleValue() == max.doubleValue()){
                            if (prom_value != min.doubleValue())
                                throw new ValidatorException(
                                        new FacesMessage(
                                                FacesMessage.SEVERITY_ERROR,
                                                "El valor del campo 'Promedio' debe ser igual a los valor de los campos 'Mínimo' y 'Máximo'.",
                                                null));
                        }
                        else
                        if (!(min.doubleValue() < prom_value && prom_value < max.doubleValue()))
                            throw new ValidatorException(
                                    new FacesMessage(
                                            FacesMessage.SEVERITY_ERROR,
                                            "El valor del campo 'Promedio' debe ser mayor que el valor del campo 'Mínimo' y menor que el valor del campo 'Máximo'.",
                                            null));
                    }
                } else {
                    if (type.equals("humedad")) {
                        max = this.climaBean.getClima().getHumedadMaxima();
                        min = this.climaBean.getClima().getHumedadMinima();
                        if (max != null && min != null) {
                            if (min.doubleValue() == max.doubleValue()){
                                if (prom_value != min.doubleValue())
                                    throw new ValidatorException(
                                            new FacesMessage(
                                                    FacesMessage.SEVERITY_ERROR,
                                                    "El valor del campo 'Promedio' debe ser igual a los valor de los campos 'Mínimo' y 'Máximo'.",
                                                    null));
                            }
                            else
                            if (!(min.doubleValue() < prom_value && prom_value < max.doubleValue()))
                                throw new ValidatorException(
                                        new FacesMessage(
                                                FacesMessage.SEVERITY_ERROR,
                                                "El valor del campo 'Promedio' debe ser mayor que el valor del campo 'Mínimo' y menor que el valor del campo 'Máximo'.",
                                                null));
                        }
                    } else {
                        if (type.equals("velocidadViento")) {
                            max = this.climaBean.getClima().getVelocidadVientoMaxima();
                            min = this.climaBean.getClima().getVelocidadVientoMinima();
                            if (max != null && min != null) {
                                if (min.doubleValue() == max.doubleValue()){
                                    if (prom_value != min.doubleValue())
                                        throw new ValidatorException(
                                                new FacesMessage(
                                                        FacesMessage.SEVERITY_ERROR,
                                                        "El valor del campo 'Promedio' debe ser igual a los valor de los campos 'Mínimo' y 'Máximo'.",
                                                        null));
                                }
                                else
                                if (!(min.doubleValue() < prom_value && prom_value < max.doubleValue()))
                                    throw new ValidatorException(
                                            new FacesMessage(
                                                    FacesMessage.SEVERITY_ERROR,
                                                    "El valor del campo 'Promedio' debe ser mayor que el valor del campo 'Mínimo' y menor que el valor del campo 'Máximo'.",
                                                    null));
                            }
                        } else {//Evapotranspiracion
                            max = this.climaBean.getClima().getEvapotranspiracionMaxima();
                            min = this.climaBean.getClima().getEvapotranspiracionMinima();
                            if (max != null && min != null) {
                                if (min.doubleValue() == max.doubleValue()){
                                    if (prom_value != min.doubleValue())
                                        throw new ValidatorException(
                                                new FacesMessage(
                                                        FacesMessage.SEVERITY_ERROR,
                                                        "El valor del campo 'Promedio' debe ser igual a los valor de los campos 'Mínimo' y 'Máximo'.",
                                                        null));
                                }
                                else
                                if (!(min.doubleValue() < prom_value && prom_value < max.doubleValue()))
                                    throw new ValidatorException(
                                            new FacesMessage(
                                                    FacesMessage.SEVERITY_ERROR,
                                                    "El valor del campo 'Promedio' debe ser mayor que el valor del campo 'Mínimo' y menor que el valor del campo 'Máximo'.",
                                                    null));
                            }
                        }
                    }
                }
            }
    }
}