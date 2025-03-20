/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.bean;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.LazyDataModel;

import ec.gob.ambiente.suia.dto.EntityUsuario;

/**
 *
 * @author christian
 */
public class CambiaUsuarioBean {

    @Getter
    @Setter
    private LazyDataModel<EntityUsuario> listaUsuariosFilterLazy;



}
