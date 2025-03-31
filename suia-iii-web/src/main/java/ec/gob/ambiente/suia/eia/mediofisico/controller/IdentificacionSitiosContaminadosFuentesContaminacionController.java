package ec.gob.ambiente.suia.eia.mediofisico.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.IdentificacionSitiosContaminadosFuentesContaminacion;
import ec.gob.ambiente.suia.eia.mediofisico.bean.MedioFisicoBean;
import ec.gob.ambiente.suia.eia.mediofisico.facade.IdentificacionSitiosContaminadosFuentesContaminacionFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

@ManagedBean
@ViewScoped
public class IdentificacionSitiosContaminadosFuentesContaminacionController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4310482971806811266L;

    @EJB
    private IdentificacionSitiosContaminadosFuentesContaminacionFacade identificacionSitiosContaminadosFuentesContaminacionFacade;

    @Getter
    @Setter
    private Integer eiaId;

    @Getter
    @Setter
    @ManagedProperty(value = "#{medioFisicoBean}")
    private MedioFisicoBean medioFisicoBean;

    @EJB
    private ValidacionSeccionesFacade validacionSecciones;

    @Getter
    @Setter
    private String justificacion;

//    @Getter
//    @Setter
//    private ValidacionSecciones validacionSeccio;

    @PostConstruct
    public void iniciar(){
        try{
            EstudioImpactoAmbiental eia = (EstudioImpactoAmbiental)JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
            setEiaId(eia.getId());
            List<IdentificacionSitiosContaminadosFuentesContaminacion> listaIdentificacionSitiosContaminadosFuentesContaminacion= this.identificacionSitiosContaminadosFuentesContaminacionFacade.identificacionSitiosContaminadosFuentesContaminacionsXEiaId(getEiaId());
            if(!listaIdentificacionSitiosContaminadosFuentesContaminacion.isEmpty()){
                medioFisicoBean.setListaIdentificacionSitiosContaminadosFuentesContaminacion(listaIdentificacionSitiosContaminadosFuentesContaminacion);

            }

            medioFisicoBean.setIdentificacionSCFCBorrados(new ArrayList<IdentificacionSitiosContaminadosFuentesContaminacion>());

        }catch(Exception e){
            e.getMessage();
        }
    }

    public void agregarIdentificacionSitiosContaminadosFuentesContaminacion() {
        if(!medioFisicoBean.getListaIdentificacionSitiosContaminadosFuentesContaminacion().contains(medioFisicoBean.getIdentificacionSitiosContaminadosFuentesContaminacion())) {
            medioFisicoBean.getListaIdentificacionSitiosContaminadosFuentesContaminacion().add(medioFisicoBean.getIdentificacionSitiosContaminadosFuentesContaminacion());
        }
        JsfUtil.addCallbackParam("addSitio");
    }
    public void seleccionarIdentificacionSitiosContaminadosFuentesContaminacion(IdentificacionSitiosContaminadosFuentesContaminacion identificacionSitiosContaminadosFuentesContaminacion) {
        identificacionSitiosContaminadosFuentesContaminacion.setSeleccionado(true);
        medioFisicoBean.setIdentificacionSitiosContaminadosFuentesContaminacion(identificacionSitiosContaminadosFuentesContaminacion);
    }

    public void eliminarIdentificacionSitiosContaminadosFuentesContaminacion(IdentificacionSitiosContaminadosFuentesContaminacion identificacionSitiosContaminadosFuentesContaminacion) {
        medioFisicoBean.getListaIdentificacionSitiosContaminadosFuentesContaminacion().remove(identificacionSitiosContaminadosFuentesContaminacion);
        if (identificacionSitiosContaminadosFuentesContaminacion.getId() != null) {
            identificacionSitiosContaminadosFuentesContaminacion.setEstado(false);
            medioFisicoBean.getIdentificacionSCFCBorrados().add(identificacionSitiosContaminadosFuentesContaminacion);
        }
    }



    public String cancelar() {
        medioFisicoBean.setListaIdentificacionSitiosContaminadosFuentesContaminacion(new ArrayList<IdentificacionSitiosContaminadosFuentesContaminacion>());
        return JsfUtil.actionNavigateTo("/pages/pages/eia/lineaBase/calidadAire.jsf");
    }

    public String  guardar(){
        try{
            if(this.medioFisicoBean.getEstudio().getTieneIdentificacionSitiosContaminados() == true){
                if (this.medioFisicoBean.getListaIdentificacionSitiosContaminadosFuentesContaminacion().isEmpty()){
                    JsfUtil.addMessageError("Usted debe tener al menos un registro");


                }
                else{
                    identificacionSitiosContaminadosFuentesContaminacionFacade.guardar(medioFisicoBean.getListaIdentificacionSitiosContaminadosFuentesContaminacion(), medioFisicoBean.getIdentificacionSCFCBorrados(),medioFisicoBean.getEstudio());
                    JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
                validacionSecciones.guardarValidacionSeccion("EIA",
                        "identificacionSitiosContaminadosFuentesContaminacion", medioFisicoBean
                                .getEstudio().getId()
                                .toString());


                    return JsfUtil.actionNavigateTo("/pages/eia/default.jsf");
                }


            }

            else{
                /*if (!medioFisicoBean.getJustificacionSitiosContaminados().equals("")){
                    identificacionSitiosContaminadosFuentesContaminacionFacade.actualizarEIA(medioFisicoBean.getEstudio(), medioFisicoBean.getJustificacionSitiosContaminados());

                    validacionSecciones.guardarValidacionSeccion("EIA",
                            "identificacionSitiosContaminadosFuentesContaminacion", medioFisicoBean
                                    .getEstudio().getId()
                                    .toString());
                    JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
                }*/
/*Alejandro*/
                identificacionSitiosContaminadosFuentesContaminacionFacade.actualizarEIA(medioFisicoBean.getEstudio(), medioFisicoBean.getJustificacionSitiosContaminados());

                validacionSecciones.guardarValidacionSeccion("EIA",
                        "identificacionSitiosContaminadosFuentesContaminacion", medioFisicoBean
                                .getEstudio().getId()
                                .toString());
                JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
            }
        }catch(Exception ex){
            ex.printStackTrace();
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO);
        }
        return null;
    }


}
