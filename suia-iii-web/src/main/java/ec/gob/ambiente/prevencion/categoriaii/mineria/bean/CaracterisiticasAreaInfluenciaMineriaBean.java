/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoriaii.mineria.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public class CaracterisiticasAreaInfluenciaMineriaBean {

    @Getter
    @Setter
    private List<UbicacionesGeografica> listaUbicacionGeograficaProyecto;

    @Getter
    @Setter
    private List<SelectItem> listaRegiones;

    @Getter
    @Setter
    private List<SelectItem> listaClimaCombo;

    @Getter
    @Setter
    private List<CatalogoGeneralFisico> listaAltitud;

    @Getter
    @Setter
    private List<CatalogoGeneralFisico> listaClima;

    @Getter
    @Setter
    private List<SelectItem> listaOcupacionAreaInfluenciaCombo;

    @Getter
    @Setter
    private List<SelectItem> listaPendienteSueloCombo;

    @Getter
    @Setter
    private List<SelectItem> listaTipoSueloCombo;

    @Getter
    @Setter
    private List<SelectItem> listaCalidadSueloCombo;

    @Getter
    @Setter
    private List<SelectItem> listaPermiabilidadSueloCombo;

    @Getter
    @Setter
    private List<SelectItem> listaCondicionesDrenajeCombo;

    @Getter
    @Setter
    private List<SelectItem> listaRecursosHidricosCombo;

    @Getter
    @Setter
    private List<SelectItem> listaNivelFreaticoCombo;

    @Getter
    @Setter
    private List<SelectItem> listaPrecipitacionesCombo;

    @Getter
    @Setter
    private List<SelectItem> listaCaracteristicasAguaCombo;

    @Getter
    @Setter
    private List<SelectItem> listaCaracteristicasAireCombo;

    @Getter
    @Setter
    private List<SelectItem> listaRecirculacionAireCombo;

    @Getter
    @Setter
    private List<SelectItem> listaRuidoCombo;

    @Getter
    @Setter
    private String[] opcionesRegionesProyecto;

    @Getter
    @Setter
    private String[] opcionesOcupacionAreaInfluencia;

    @Getter
    @Setter
    private String[] opcionesPendienteSuelo;

    @Getter
    @Setter
    private String[] opcionesTipoSuelo;

    @Getter
    @Setter
    private String[] opcionesCalidadSuelo;

    @Getter
    @Setter
    private String[] opcionesRecursosHidricos;

    @Getter
    @Setter
    private String idPermeabilidadSuelo;

    @Getter
    @Setter
    private String idCondicionesDrenaje;

    @Getter
    @Setter
    private FichaAmbientalMineria fichaAmbientalMineria;

    @Getter
    @Setter
    private CatalogoGeneralFisico catalogoAltura;

    @Getter
    @Setter
    private CatalogoGeneralFisico catalogoClima;

    @Getter
    @Setter
    private boolean apareceComboClima;

    @Getter
    @Setter
    private String idClima;

    @Getter
    @Setter
    private String idNivelFreatico;

    @Getter
    @Setter
    private String idPrecipitaciones;
    @Getter
    @Setter
    private String idCaracteristicasAgua;

    @Getter
    @Setter
    private String idCaracteristicasAire;

    @Getter
    @Setter
    private String idRecirculacionAire;

    @Getter
    @Setter
    private String idRuido;

    private static final int INICIO = 0;
    private static final int FIN = 500;
    public static final SelectItem SELECCIONE = new SelectItem(null,
            "Seleccione");

    public void iniciarDatos() {
        setListaUbicacionGeograficaProyecto(null);
        setListaRegiones(null);
        setListaAltitud(null);
        setCatalogoAltura(null);
        setListaClima(null);
        setListaClimaCombo(null);
        setCatalogoClima(null);
        setIdClima(null);
        setListaOcupacionAreaInfluenciaCombo(null);
        setOpcionesOcupacionAreaInfluencia(null);
        setListaPendienteSueloCombo(null);
        setOpcionesPendienteSuelo(null);
        setListaTipoSueloCombo(null);
        setOpcionesTipoSuelo(null);
        setListaCalidadSueloCombo(null);
        setOpcionesCalidadSuelo(null);
        setListaPermiabilidadSueloCombo(null);
        setIdPermeabilidadSuelo(null);
        setListaCondicionesDrenajeCombo(null);
        setIdCondicionesDrenaje(null);
        setListaRecursosHidricosCombo(null);
        setOpcionesRecursosHidricos(null);
        setListaNivelFreaticoCombo(null);
        setIdNivelFreatico(null);
        setListaPrecipitacionesCombo(null);
        setIdPrecipitaciones(null);
        setListaCaracteristicasAguaCombo(null);
        setIdCaracteristicasAgua(null);
        setListaCaracteristicasAireCombo(null);
        setIdCaracteristicasAire(null);
        setListaRecirculacionAireCombo(null);
        setIdRecirculacionAire(null);
        setListaRuidoCombo(null);
        setIdRuido(null);
    }

    public void procesarRegiones() {
        setOpcionesRegionesProyecto(devuelveRegiones());
    }

    private String[] devuelveRegiones() {
        List<Integer> regiones = new ArrayList<Integer>();
        for (UbicacionesGeografica u : listaUbicacionGeograficaProyecto) {
            regiones.add(u.getUbicacionesGeografica()
                    .getUbicacionesGeografica().getIdRegion());
        }
        Set<Integer> regionesNoRepetidos = new HashSet<Integer>(regiones);
        String[] arreglo = new String[regionesNoRepetidos.size()];
        int i = 0;
        for (Integer ids : regionesNoRepetidos) {
            arreglo[i] = ids.toString();
            i++;
        }
        return arreglo;
    }

    public boolean procesarClima() {
        setListaClimaCombo(new ArrayList<SelectItem>());
        int altura = getFichaAmbientalMineria()
                .getProyectoLicenciamientoAmbiental().getAltitud();
        if (altura >= INICIO && altura <= FIN) {
            getListaClimaCombo().add(SELECCIONE);
            for (CatalogoGeneralFisico c : getListaClima()) {
                if (altura >= c.getValorDesde().intValue()
                        && altura <= c.getValorHasta().intValue()) {
                    getListaClimaCombo().add(
                            new SelectItem(c.getId(), c.getDescripcion()));
                }
            }
            return true;
        } else {
            return false;
        }
    }

}
