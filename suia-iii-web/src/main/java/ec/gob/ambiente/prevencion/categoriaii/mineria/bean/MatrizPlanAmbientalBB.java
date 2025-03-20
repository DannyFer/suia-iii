/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoriaii.mineria.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.TreeNode;

import ec.gob.ambiente.suia.domain.ActividadMinera;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.MatrizFactorImpacto;
import ec.gob.ambiente.suia.dto.EntityNodoActividad;

/**
 *
 * @author christian
 */
public class MatrizPlanAmbientalBB {

    @Getter
    @Setter
    private FichaAmbientalMineria fichaAmbientalMineria;
    @Getter
    @Setter
    private List<SelectItem> listaFactores;
    @Getter
    @Setter
    private List<SelectItem> listaImpactos;
    @Getter
    @Setter
    private List<ActividadMinera> listaActividadMineria;
    @Getter
    @Setter
    private String idFactor;
    @Getter
    @Setter
    private String idImpacto;
    @Getter
    @Setter
    private EntityNodoActividad entityNodoActividad;
    @Getter
    @Setter
    private TreeNode root;
    
    @Getter
    @Setter
    private List<MatrizFactorImpacto> listaImpactosOriginales, listaImpactosEliminados, listaImpactosHistoricos, historialImpactosSeleccionados;
    

    public void iniciarDatos() {
        setFichaAmbientalMineria(null);
        setListaFactores(null);
        setListaActividadMineria(null);
        setListaImpactos(new ArrayList<SelectItem>());
        getListaImpactos().add(new SelectItem(null, "Seleccione"));
        setIdFactor(null);
        setIdImpacto(null);
        setEntityNodoActividad(null);
        setRoot(null);
    }
}
