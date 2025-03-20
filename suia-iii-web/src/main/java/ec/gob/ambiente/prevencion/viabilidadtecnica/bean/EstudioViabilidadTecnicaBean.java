package ec.gob.ambiente.prevencion.viabilidadtecnica.bean;

import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by M-SIT on 28/10/15.
 */
@Getter
@javax.faces.bean.ManagedBean(name = "evt")
@ViewScoped
public class EstudioViabilidadTecnicaBean implements Serializable {

    private static final long serialVersionUID = -6184431814037345854L;
    @EJB
    ValidacionSeccionesFacade validacionSeccionesFacade;
    @Getter
    @Setter
    private List<String> seccionesRequeridas;
    @Getter
    @Setter
    private List<String> secciones;
    @Getter
    @Setter
    private EstudioViabilidadTecnica estudio;
    @Getter
    @Setter
    private boolean completado;

    @Getter
    @Setter
    private String msg;


    @PostConstruct
    public void cargarDatos() {
        setEstudio(new EstudioViabilidadTecnica());
        if (this.secciones==null){
            secciones = new ArrayList<>();
        }
        //validarCompleado();
    }

    public void cargarDatosRequeridos() {

        seccionesRequeridas = new ArrayList<>();
        seccionesRequeridas.add("diagnosticoFactibilidadAnex");
        seccionesRequeridas.add("diagnosticoFactibilidadAnexo");
        seccionesRequeridas.add("infoGeneralZonaProyecto");
        seccionesRequeridas.add("evaluacionInfraestructura");
        seccionesRequeridas.add("alternativas");


    }

    public Boolean seccionCompletada(String seccion) {
        return secciones.contains(seccion);
    }


    public void validarCompleado() {
        secciones = validacionSeccionesFacade.listaSeccionesPorClase("EVT", estudio.getId().toString());
        completado = true;
        int i = seccionesRequeridas.size();
        while (completado && i > 0) {
            if (!secciones.contains(seccionesRequeridas.get(--i))) {
                completado = false;
            }
        }

    }
    /*public String verEIA() {
        return JsfUtil.getBean(EIAGeneralBean.class).actionVerEIA(estudio, false, true, false);

    }*/

}
