/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoria2.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Actividad;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaFase;
import ec.gob.ambiente.suia.domain.CronogramaActividadesPma;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;

/**
 *
 * @author ishmael
 */
public class CronogramaConstruccionBean implements Serializable {

    private static final long serialVersionUID = -8989540168872570090L;

    @Getter
    @Setter
    private CronogramaActividadesPma cronogramaActividadesPma;

    @Getter
    @Setter
    private List<CatalogoCategoriaFase> listaTipoActividad;
    
    @Getter
    @Setter
    private CatalogoCategoriaFase catalogoGeneralSeleccionado;
    
    @Getter
    @Setter
    private List<Actividad> listaActividadTabla;
    
    @Getter
    @Setter
    private List<Actividad> listaActividadEliminar;

    @Getter
    @Setter
    private long zoomMax;
    
    @Getter
    @Setter
    private long zoomMin;

    @Getter
    @Setter
    private Date start;

    @Getter
    @Setter
    private Date end;
    
    @Getter
    @Setter
    private FichaAmbientalPma ficha;
    
    @Getter
    @Setter
    private Actividad actividad;
    
    @Getter
    @Setter
    private boolean  habilitar;
    
    @Getter
    @Setter
    private String mensaje;


}
