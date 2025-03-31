/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.bean;

import ec.gob.ambiente.suia.domain.DeterminacionAreasInfluenciaProyecto;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public class DeterminacionAreasInfluenciaBean {

    @Getter
    @Setter
    private List<DeterminacionAreasInfluenciaProyecto> listaAreasInfluenciaProyectosDirecta;
    @Getter
    @Setter
    private List<DeterminacionAreasInfluenciaProyecto> listaAreasInfluenciaProyectosDirectaEliminar;
    @Getter
    @Setter
    private List<DeterminacionAreasInfluenciaProyecto> listaAreasInfluenciaProyectosInDirecta;
    @Getter
    @Setter
    private List<DeterminacionAreasInfluenciaProyecto> listaAreasInfluenciaProyectosInDirectaEliminar;
    @Getter
    @Setter
    private List<DeterminacionAreasInfluenciaProyecto> listaDistanciaElementosProyecto;
    @Getter
    @Setter
    private List<DeterminacionAreasInfluenciaProyecto> listaDistanciaElementosProyectoEliminar;
    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;
    @Getter
    @Setter
    private EntityAdjunto entityAdjunto;

    public void iniciarDatos() {
        setListaAreasInfluenciaProyectosDirecta(null);
        setListaAreasInfluenciaProyectosInDirecta(null);
        setListaDistanciaElementosProyecto(null);
        setEstudioImpactoAmbiental(null);
        setListaAreasInfluenciaProyectosDirectaEliminar(new ArrayList<DeterminacionAreasInfluenciaProyecto>());
        setListaAreasInfluenciaProyectosInDirectaEliminar(new ArrayList<DeterminacionAreasInfluenciaProyecto>());
        setListaDistanciaElementosProyectoEliminar(new ArrayList<DeterminacionAreasInfluenciaProyecto>());
        setEntityAdjunto(new EntityAdjunto());
    }
}
