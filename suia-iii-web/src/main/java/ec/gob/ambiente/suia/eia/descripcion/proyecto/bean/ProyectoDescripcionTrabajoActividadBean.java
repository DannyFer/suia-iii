/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.bean;

import ec.gob.ambiente.suia.domain.DetalleProyectoDescripcionTrabajoActividad;
import ec.gob.ambiente.suia.domain.ProyectoDescripcionTrabajoActividad;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public class ProyectoDescripcionTrabajoActividadBean {

    @Getter
    @Setter
    private List<ProyectoDescripcionTrabajoActividad> listaProyectoDescripcionTrabajoActividad;
    @Getter
    @Setter
    private List<DetalleProyectoDescripcionTrabajoActividad> listaDetalleProyectoDescripcionTrabajoActividad;
    @Getter
    @Setter
    private List<DetalleProyectoDescripcionTrabajoActividad> listaDetalleProyectoDescripcionTrabajoActividadEliminado;
    @Getter
    @Setter
    private ProyectoDescripcionTrabajoActividad proyectoDescripcionTrabajoActividad;
    @Getter
    @Setter
    private boolean apareceCronograma;

    public void iniciarDatos() {
        setListaProyectoDescripcionTrabajoActividad(null);
        setListaDetalleProyectoDescripcionTrabajoActividad(null);
        setProyectoDescripcionTrabajoActividad(null);
        setListaDetalleProyectoDescripcionTrabajoActividadEliminado(new ArrayList<DetalleProyectoDescripcionTrabajoActividad>());
        setApareceCronograma(false);
    }
}
