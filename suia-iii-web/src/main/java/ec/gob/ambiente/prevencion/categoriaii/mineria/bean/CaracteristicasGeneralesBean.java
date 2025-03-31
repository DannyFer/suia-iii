/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoriaii.mineria.bean;

import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.TipoMaterial;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.List;
import javax.faces.model.SelectItem;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public class CaracteristicasGeneralesBean {

    @Getter
    @Setter
    private List<TipoMaterial> listaTipoMaterial;
    @Getter
    @Setter
    private FichaAmbientalMineria fichaAmbientalMineria;
    @Getter
    @Setter
    private List<SelectItem> listaOpcionesTipoMaterial;
    @Getter
    @Setter
    private List<SelectItem> listaPredioActividadMinera;
    @Getter
    @Setter
    private List<SelectItem> listaEtapaActividadMinera;
    @Getter
    @Setter
    private String[] opcionesTipoMaterial;
    @Getter
    @Setter
    private String idPredio;
    @Getter
    @Setter
    private String idEtapa;
    @Getter
    @Setter
    private String otro;
    @Getter
    @Setter
    private String catalogoOtro;

    public void iniciarDatos() {
        setListaTipoMaterial(null);
        setFichaAmbientalMineria(null);
        setListaOpcionesTipoMaterial(null);
        setOpcionesTipoMaterial(null);
        setListaPredioActividadMinera(null);
        setListaEtapaActividadMinera(null);
        setIdEtapa(null);
        setIdPredio(null);
        setOtro(null);
        setCatalogoOtro(String.valueOf(CatalogoGeneralFisico.OTRO_PREDIO_ACTIVO));
    }

    public void validarFormulario() throws ServiceException {
        if (getCatalogoOtro().equals(getIdPredio()) && getOtro() == null) {
            throw new ServiceException("Debe ingresar el campo otro");
        }
    }

}
