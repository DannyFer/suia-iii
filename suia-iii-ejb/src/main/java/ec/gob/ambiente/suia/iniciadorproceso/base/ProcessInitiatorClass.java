package ec.gob.ambiente.suia.iniciadorproceso.base;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;

public abstract class ProcessInitiatorClass {

    protected ProcesoFacade procesoFacade;

    @Getter
    @Setter
    protected Usuario usuario;

    @Getter
    @Setter
    protected Long processInstanceId;

    @Getter
    @Setter
    protected Map<String, Object> variables;

    @Getter
    @Setter
    protected Map<String, Object> variablesProceso;


    /**
     * configura las variables especificas del proceso, debe configurar <p>variables</p>
     *
     * @throws Exception
     * @param idProyectoPS
     * @param proyectoLicenciaAmbientalFacade
     * @param participacionSocialFacade
     */
    public abstract void configurarVariables(Integer idProyectoPS, ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade, ParticipacionSocialFacade participacionSocialFacade) throws Exception;

    /**
     * configura las variables especificas del proceso, debe configurar <p>variables</p>
     *
     * @throws Exception
     */
    //public abstract void configurarVariables() throws Exception;

    /**
     * Inicializa los datos del proceso
     *
     * @throws JbpmException
     */
    public void inicializar() throws JbpmException {
       procesoFacade = (ProcesoFacade) BeanLocator
                .getInstance(ProcesoFacade.class);
        //variablesProceso = procesoFacade.recuperarVariablesProceso(usuario, processInstanceId);
        variables = new HashMap<String, Object>();
        variablesProceso = new HashMap<String, Object>();


    }

    /**
     * Guarda las variables configuradas
     *
     * @throws JbpmException
     */
    public boolean guardarVariables(){
        try {
            procesoFacade.modificarVariablesProceso(usuario, processInstanceId, variables);
            return true;
        } catch (JbpmException e) {
            e.printStackTrace();
            return false;
        }

    }
}
