/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.mediofisico.bean;

import ec.gob.ambiente.suia.domain.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author martin
 */
public class ClimaBean implements Serializable {

    private static final long serialVersionUID = -6121426502807786614L;

    @Getter
    @Setter
    private Clima clima;

    @Getter
    @Setter
    private List<Clima> listaClimas;

    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    @Getter
    @Setter
    private List ListaEntidadesRemover;

}
