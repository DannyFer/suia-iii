/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.Almacen;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AlmacenRecepcion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.dto.EntityRecepcionDesecho;

public class AlmacenTemporalBean implements Serializable {

    private static final long serialVersionUID = -6121426502807786614L;

    @Getter
    @Setter
    private Almacen almacen;

    @Getter
    @Setter
    private Almacen almacenAux;

    @Getter
    @Setter
    private AlmacenRecepcion almacenRecepcion;

    @Getter
    @Setter
    private AlmacenRecepcion almacenRecepcionAux;

    @Getter
    @Setter
    private List<Almacen> listaAlmacenes;

    @Getter
    @Setter
    private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

    @Getter
    @Setter
    private List ListaEntidadesRemover;

    @Getter
    @Setter
    private List<EntityRecepcionDesecho> listaEntityRecepcionDesecho;

    @Getter
    @Setter
    private boolean mostrarFosasRetencion;



}
