/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.bean;

import ec.gob.ambiente.suia.domain.AnalisisRiesgoEia;
import ec.gob.ambiente.suia.domain.Riesgo;
import ec.gob.ambiente.suia.domain.SubTipoRiesgo;
import ec.gob.ambiente.suia.domain.enums.TipoRiesgo;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import lombok.Getter;
import lombok.Setter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class AnalisisRiesgoEIABean {

    @Getter
    @Setter
    private List<AnalisisRiesgoEia> listaAnalisisRiesgoEia, listaAnalisisRiesgoEiaOriginales, listaAnalisisRiesgoEiaHistorico, listaAnalisisRiesgoEliminadosBdd;
    @Getter
    @Setter
    private List<AnalisisRiesgoEia> listaAnalisisRiesgoEiaEliminados;
    @Getter
    @Setter
    private EntityAdjunto entityAdjunto;

    @Getter
    @Setter
    private AnalisisRiesgoEia analisisActivo;

    @Getter
    @Setter
    private TipoRiesgo tipoRiesgo;

    @Getter
    @Setter
    private SubTipoRiesgo subtipo;



    @Setter
    @Getter
    private TipoRiesgo[] tiposRiesgo = TipoRiesgo.values();

    @Setter
    @Getter
    private List<SubTipoRiesgo> subTiposRiesgo;

    @Setter
    @Getter
    private List<Riesgo> riesgos;


    @Setter
    @Getter
    private boolean renderSubtipo=true;

    @Setter
    @Getter
    private boolean editing=false;

    @Setter
    @Getter
    private boolean habilitarOtroRiesgo=false;


    public void iniciarDatos() {
        setListaAnalisisRiesgoEia(new ArrayList<AnalisisRiesgoEia>());
        setListaAnalisisRiesgoEiaEliminados(new ArrayList<AnalisisRiesgoEia>());

    }



}
