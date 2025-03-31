package ec.gob.ambiente.prevencion.reinyecionoinyecciondeaguas.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;

import ec.gob.ambiente.suia.comun.bean.IdentificarProyectoComunBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;


@ManagedBean
@SessionScoped
public class ReinyeccionOinyeccionDeAguasDeFormacionController extends IdentificarProyectoComunBean {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4743963792419602481L;

//	private static final Logger LOG = Logger.getLogger(ReinyeccionOinyeccionDeAguasDeFormacionController.class);

    @Getter
    private ProyectoCustom proyectoCustom;

    @EJB
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

    @Getter
    private List<ProyectoCustom> ListaCustoms;

    @Setter
    @Getter
    private String formaciones;

    @Setter
    private boolean continuar;

    private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

    public String seleccionarProyecto() {
        return JsfUtil.actionNavigateTo("/prevencion/inyeccionoreinyeccion/common/seleccionarProyecto.jsf");
    }

    @PostConstruct
    public void init(){
       formaciones = "";
       ListaCustoms = proyectoLicenciamientoAmbientalFacade.listarProyectosLicenciamientoAmbientalHidrocarburos();
        /*for(ProyectoCustom proy : getProyectos()){
            proyectoLicenciamientoAmbiental = proyectoLicenciamientoAmbientalFacade.getProyectoPorId(new Integer(proy.getId()));
            if(proyectoLicenciamientoAmbiental.getTipoSector() != null && proyectoLicenciamientoAmbiental.getTipoSector().getId() != null) {
                if (proyectoLicenciamientoAmbiental.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS) &&
                        proyectoLicenciamientoAmbiental.getCatalogoCategoria().getFase().getId() == 3) {
                    ListaCustoms.add(proy);
                }
            }
        }*/
    }

    public String seleccionar(Object object) {
        proyectoLicenciamientoAmbiental = new ProyectoLicenciamientoAmbiental();
        proyectoLicenciamientoAmbiental = proyectoLicenciamientoAmbientalFacade.getProyectoPorId(new Integer(((ProyectoCustom)object).getId()));
         return JsfUtil.actionNavigateTo("/prevencion/inyeccionoreinyeccion/common/especificarsolicitud.jsf");
    }

    public String continuarFlujo(){
        return JsfUtil.actionNavigateTo("/prevencion/inyeccionoreinyeccion/common/especificarsolicitud.jsf");
    }

    public void validarFormaciones(){
        if(this.formaciones.equals("Si")){
            RequestContext.getCurrentInstance().execute("PF('modal').show();");
        }else{
           continuarFlujo();
        }
    }

}

