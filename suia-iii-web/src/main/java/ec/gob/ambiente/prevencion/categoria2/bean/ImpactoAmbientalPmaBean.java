package ec.gob.ambiente.prevencion.categoria2.bean;

import ec.gob.ambiente.suia.domain.ActividadProcesoPma;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.FactorPma;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.MatrizFactorImpacto;
import ec.gob.ambiente.suia.domain.MatrizFactorImpactoOtros;
import ec.gob.ambiente.suia.dto.EntityNodoActividad;

import javax.faces.model.SelectItem;

import org.primefaces.model.TreeNode;

/**
 *
 * @author ishmael
 *
 */
public class ImpactoAmbientalPmaBean implements Serializable {
    
    private static final long serialVersionUID = -7060890768576906853L;

    @Getter
    @Setter
    private List<FactorPma> listaFactor;

    @Getter
    @Setter
    private List<FactorPma> listaFactorOtros;

    @Getter
    @Setter
    private List<SelectItem> listaImpacto;

    @Getter
    @Setter
    private String idImpacto;

    @Getter
    @Setter
    private FichaAmbientalPma ficha;

    @Getter
    @Setter
    private TreeNode root;
    
    @Getter
    @Setter
    private List<EntityNodoActividad> listaActividad;
    
    @Getter
    @Setter
    private EntityNodoActividad entityNodoActividad;
    
    @Getter
    @Setter
    List<ActividadProcesoPma> listaActividades;
    
    @Getter
    @Setter
    private List<MatrizFactorImpactoOtros> listaActividadOtros;
    
    @Getter
    @Setter
    List<ActividadProcesoPma> listaActividadesOtros;
    
    @Getter
    @Setter
    private MatrizFactorImpactoOtros actividadOtros;
    
    @Getter
    @Setter
    private List<MatrizFactorImpacto> listaImpactosOriginales, listaImpactosEliminados, listaImpactosHistoricos, historialImpactosSeleccionados;
    
    @Getter
    @Setter
    private List<MatrizFactorImpactoOtros> listaOtrosImpactosOriginales, listaOtrosImpactosEliminados, listaOtrosImpactosHistoricos, historialOtrosImpactosSeleccionados;
}
