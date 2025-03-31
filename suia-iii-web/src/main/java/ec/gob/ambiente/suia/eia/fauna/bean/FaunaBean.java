/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.fauna.bean;

import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.DetalleFauna;
import ec.gob.ambiente.suia.domain.DetalleFaunaEspecies;
import ec.gob.ambiente.suia.domain.DetalleFaunaSumaEspecies;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.Fauna;
import ec.gob.ambiente.suia.domain.PuntosMuestreo;
import java.util.List;
import javax.faces.model.SelectItem;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public class FaunaBean {

    @Getter
    @Setter
    private List<CatalogoGeneral> listaGruposTaxonomicos;
    @Getter
    @Setter
    private List<SelectItem> listaTipoMuestreo;
    @Getter
    @Setter
    private String idTipoMuestreo;
    @Getter
    @Setter
    private CatalogoGeneral grupoTaxonomicoSeleccionado;
    @Getter
    @Setter
    private Integer indiceTab;
    @Getter
    @Setter
    private String esfuerzoMuestreo;
    @Getter
    @Setter
    private List<SelectItem> listaCodigoPunto;
    @Getter
    @Setter
    private List<SelectItem> listaCodigoPunto1;
    @Getter
    @Setter
    private List<PuntosMuestreo> listaPuntosMuestreo;
    @Getter
    @Setter
    private List<PuntosMuestreo> listaPuntosMuestreo1;
    @Getter
    @Setter
    private List<PuntosMuestreo> listaPuntosMuestreoEditar;
    @Getter
    @Setter
    private List<PuntosMuestreo> listaPuntosMuestreo1Editar;
    @Getter
    @Setter
    private DetalleFauna detalleFauna;
    @Getter
    @Setter
    private DetalleFauna detalleFauna1;
    @Getter
    @Setter
    private List<DetalleFauna> listaDetalleFauna;
    @Getter
    @Setter
    private List<DetalleFauna> listaDetalleFauna1;
    @Getter
    @Setter
    private List<DetalleFauna> listaDetalleFaunaEditar;
    @Getter
    @Setter
    private List<DetalleFauna> listaDetalleFauna1Editar;
    @Getter
    @Setter
    private Fauna fauna;
    @Getter
    @Setter
    private PuntosMuestreo puntosMuestreo;
    @Getter
    @Setter
    private PuntosMuestreo puntosMuestreo1;
    @Getter
    @Setter
    private DetalleFaunaSumaEspecies detalleFaunaSumaEspecies;
    @Getter
    @Setter
    private List<DetalleFaunaEspecies> listaDetalleFaunaEspecies;
    @Getter
    @Setter
    private Integer idMastofauna;
    @Getter
    @Setter
    private Integer idOrnitofauna;
    @Getter
    @Setter
    private Integer idHerpetofauna;
    @Getter
    @Setter
    private Integer idEntomofauna;
    @Getter
    @Setter
    private Integer idIctiofauna;
    @Getter
    @Setter
    private Integer idMacroinvertebradosAcuaticos;
    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    public void iniciarDatos() {
        setListaGruposTaxonomicos(null);
        setListaTipoMuestreo(null);
        setIdTipoMuestreo(null);
        setGrupoTaxonomicoSeleccionado(null);
        setIndiceTab(null);
        setListaCodigoPunto(null);
        setDetalleFauna(new DetalleFauna());
        setListaDetalleFauna(null);
        setDetalleFaunaSumaEspecies(null);
        setListaDetalleFaunaEspecies(null);
        setEstudioImpactoAmbiental(null);
        setIdMastofauna(CatalogoGeneral.MASTOFAUNA);
        setIdOrnitofauna(CatalogoGeneral.ORNITOFAUNA);
        setIdHerpetofauna(CatalogoGeneral.HERPETOFAUNA);
        setIdEntomofauna(CatalogoGeneral.ENTOMOFAUNA);
        setIdIctiofauna(CatalogoGeneral.ICTIOFAUNA);
        setIdMacroinvertebradosAcuaticos(CatalogoGeneral.MACROINVERTEBRADOS_ACUATICOS);
    }
}
