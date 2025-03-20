/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.inventarioforestal.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.CatalogoLibroRojo;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EspeciesBajoCategoriaAmenaza;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.InventarioForestal;
import ec.gob.ambiente.suia.domain.InventarioForestalIndice;
import ec.gob.ambiente.suia.domain.InventarioForestalPuntos;
import ec.gob.ambiente.suia.domain.InventarioForestalVolumen;

/**
 *
 * @author martin
 */
public class InventarioForestalBean implements Serializable {

    private static final long serialVersionUID = -6121426502807786614L;

    @Getter
    @Setter
    private InventarioForestal inventarioForestal, inventarioForestalOriginal;

    @Getter
    @Setter
    private InventarioForestalPuntos inventarioForestalPunto, inventarioForestalPuntoOriginal;

    @Getter
    @Setter
    private InventarioForestalVolumen inventarioForestalVolumen;

    @Getter
    @Setter
    private InventarioForestalIndice inventarioForestalIndice;

    @Getter
    @Setter
    private EspeciesBajoCategoriaAmenaza especieCategoriaAmenaza;

    @Getter
    @Setter
    private List<CoordenadaGeneral> listaCoordenadas, listaCoordenadasOriginal, listaCoordenadasEliminadasBdd, listaCoordenadasHistorial;


    @Getter
    @Setter
    private List<InventarioForestalPuntos> listaInventarioForestalPuntos, listaInventarioForestalPuntosOriginales, listaInventarioPuntos, listaInventarioForestalPuntosEliminados;

    @Getter
    @Setter
    private List<InventarioForestalVolumen> listaInventarioForestalVolumen;

    @Getter
    @Setter
    private List<InventarioForestalIndice> listaInventarioForestalIndice;

    @Getter
    @Setter
    private List<EspeciesBajoCategoriaAmenaza> listaEspeciesBajoCategoriaAmenazas, listaEspeciesAmenazadas;

    @Getter
    @Setter
    private CoordenadaGeneral coordenadaGeneral;

    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    @SuppressWarnings("rawtypes")
	@Getter
    @Setter
    private List listaEntidadesRemover;

    @Getter
    @Setter
    private Documento documentoGeneral, documentoGeneralHistorico;

    @Getter
    @Setter
    private Documento adjuntosVolumen, adjuntoVolumenHistorico;

    @Getter
    @Setter
    private Documento adjuntosIndice, adjuntoIndiceHistorico;

    @Getter
    @Setter
    private List<CatalogoLibroRojo> listaCatalogoLibroRojo;
    
    @Getter
    @Setter
    private Integer documentoGeneralId;
    
    @Getter
    @Setter
    private Integer adjuntosVolumenId;
    
    @Getter
    @Setter
    private Integer adjuntosIndiceId;
    
    @Getter
    @Setter
    private Documento documento;

    @Getter
    @Setter
    private List<InventarioForestal> infoGeneralHistorial, resultadosHistorial, valoracionHistorial, justificacionHistorial;
    
    @Getter
    @Setter
    private List<EspeciesBajoCategoriaAmenaza> listaEspeciesCitesOriginal, listaEspeciesCitesEliminadasBdd, especiesHistorial;
    
    @Getter
    @Setter
    private List<InventarioForestalPuntos> listaInventarioPuntosHistorial;
}
