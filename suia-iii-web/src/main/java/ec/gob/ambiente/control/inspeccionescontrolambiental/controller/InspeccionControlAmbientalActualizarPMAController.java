package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.comun.bean.IdentificarProyectoComunBean;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;



@ManagedBean
@SessionScoped
public class InspeccionControlAmbientalActualizarPMAController implements Serializable {

    private static final long serialVersionUID = 908900291326245412L;

//    private static final Logger LOG = Logger.getLogger(InspeccionControlAmbientalActualizarPMAController.class);

    @Getter
    private ProyectoCustom proyectoCustom;

    @EJB
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;


    public void seleccionarProyecto() {
        JsfUtil.getBean(IdentificarProyectoComunBean.class).initFunction(
                "/prevencion/actualizacionPma/pma/planManejoAmbiental", new CompleteOperation() {

                    @Override
                    public Object endOperation(Object object) {
                        proyectoCustom = (ProyectoCustom) object; //instanciar el proyectoCustom

                        ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(Integer.parseInt(proyectoCustom.getId()));

                        try {
                            EstudioImpactoAmbiental proyectoEIA = estudioImpactoAmbientalFacade.obtenerPorProyecto(proyecto);

                            if (proyectoEIA == null){
                                JsfUtil.addMessageError("No se ha realizado el estudio de impacto ambiental");
                                return false;
                            } else {
                                JsfUtil.cargarObjetoSession(ec.gob.ambiente.suia.utils.Constantes.SESSION_EIA_OBJECT,proyectoEIA);
                            }


                        } catch (Exception e){}



                        return null;
                    }
                });
    }

}

