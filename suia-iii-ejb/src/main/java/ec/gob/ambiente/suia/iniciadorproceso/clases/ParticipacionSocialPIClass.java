package ec.gob.ambiente.suia.iniciadorproceso.clases;

import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.iniciadorproceso.base.ProcessInitiatorClass;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;

public class ParticipacionSocialPIClass extends ProcessInitiatorClass {

    @Override
    public void configurarVariables(Integer idProyectoPS, ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade, ParticipacionSocialFacade participacionSocialFacade) throws Exception {

       // if (!(variablesProceso.containsKey("requiereFacilitador") && variablesProceso.get("requiereFacilitador") != null)) {
        System.out.println("**********ENTRA AL MÉTODO configurarVariables**********");

            ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorId(idProyectoPS);

            System.out.println("**********configurarVariables --> participacionSocialFacade OK **********");
            variables = participacionSocialFacade.inicializarParticipacionSocial(proyecto);
            variables.put("completadoProcesoPS", false);
        /*}else{
            System.out.println("EL proceso "+idProyectoPS.toString()+" ya se encuentra inicializado.");
        }*/
    }

    /*@Override
    public void configurarVariables() throws Exception {

        for (String key : variablesProceso.keySet()){
            System.out.println(key+"---->"+variables.get(key));
        }
        Integer idProceso = Integer.parseInt((String) variablesProceso
                .get("idProyectoPS"));
      configurarVariables(idProceso, proyectoLicenciaAmbientalFacade);
    }*/
}
