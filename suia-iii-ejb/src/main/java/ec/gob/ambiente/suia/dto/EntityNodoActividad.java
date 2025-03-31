/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.dto;

import java.io.Serializable;

import ec.gob.ambiente.suia.domain.ActividadMinera;
import ec.gob.ambiente.suia.domain.ActividadProcesoPma;
import ec.gob.ambiente.suia.domain.FactorPma;
import ec.gob.ambiente.suia.domain.ImpactoPma;
import ec.gob.ambiente.suia.domain.MatrizFactorImpacto;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ishmael
 */
public class EntityNodoActividad implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2571069444322197398L;
	@Getter
    @Setter
    private ActividadProcesoPma actividadProcesoPma;
    @Getter
    @Setter
    private FactorPma factor;
    @Getter
    @Setter
    private ImpactoPma impacto;
    @Getter
    @Setter
    private boolean nodoFinal;
    @Getter
    @Setter
    private ActividadMinera actividadMinera;
    @Getter
    @Setter
    private int indice;
    
    @Getter
    @Setter
    private boolean editar;
    
    @Getter
    @Setter
    private MatrizFactorImpacto factorImpacto;
    
    @Getter
    @Setter
    private boolean nuevoEnModificacion = false;
    
    @Getter
    @Setter
    private boolean historialModificaciones = false;

    public EntityNodoActividad() {
    }

    public EntityNodoActividad(ActividadProcesoPma actividadProcesoPma) {
        this.actividadProcesoPma = actividadProcesoPma;
    }

    public EntityNodoActividad(FactorPma factor, ImpactoPma impacto, boolean nodoFinal) {
        this.factor = factor;
        this.impacto = impacto;
        this.nodoFinal = nodoFinal;
    }

    public EntityNodoActividad(ActividadMinera actividadMinera, FactorPma factor, ImpactoPma impacto, boolean nodoFinal, int indice) {
        this.factor = factor;
        this.impacto = impacto;
        this.nodoFinal = nodoFinal;
        this.actividadMinera = actividadMinera;
        this.indice = indice;
    }

    //MarielaG cambio metodo para manejo historial
    public EntityNodoActividad(ActividadProcesoPma actividadProcesoPma, FactorPma factor, ImpactoPma impacto, int indice, 
    		boolean nodoFinal, MatrizFactorImpacto factorImpacto) {
        this.actividadProcesoPma = actividadProcesoPma;
        this.factor = factor;
        this.impacto = impacto;
        this.nodoFinal = nodoFinal;
        this.indice = indice;
        this.nuevoEnModificacion = factorImpacto.isNuevoEnModificacion();
        this.historialModificaciones = factorImpacto.isHistorialModificaciones();
        this.factorImpacto = factorImpacto;
    }

    //MarielaG cambio metodo para manejo historial
    public EntityNodoActividad(ActividadMinera actividadMinera, FactorPma factor, ImpactoPma impacto, boolean nodoFinal, int indice, MatrizFactorImpacto factorImpacto) {
        this.factor = factor;
        this.impacto = impacto;
        this.nodoFinal = nodoFinal;
        this.actividadMinera = actividadMinera;
        this.indice = indice;
        this.nuevoEnModificacion = factorImpacto.isNuevoEnModificacion();
        this.historialModificaciones = factorImpacto.isHistorialModificaciones();
        this.factorImpacto = factorImpacto;
    }
}
