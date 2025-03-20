package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/RestServices/participacionSocial")
@Singleton
public class ParticipacionSocialServiceWS {

    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;
    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

    @GET
    @Path("/publicarEstudio/{idProyecto}")
    @Produces("text/plain")
    public void publicarEstudio(
            @PathParam("idProyecto") Integer idProyecto) {
        proyectoLicenciamientoAmbientalFacade = (ProyectoLicenciamientoAmbientalFacade)BeanLocator.getInstance(ProyectoLicenciamientoAmbientalFacade.class);
        ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental =
                proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(idProyecto);
        participacionSocialFacade = (ParticipacionSocialFacade)BeanLocator.getInstance(ParticipacionSocialFacade.class);

        ParticipacionSocialAmbiental participacionSocialAmbiental = participacionSocialFacade.buscarCrearParticipacionSocialAmbiental(proyectoLicenciamientoAmbiental);
        participacionSocialAmbiental.setPublicacion(true);
        participacionSocialFacade.guardar(participacionSocialAmbiental);


    }
    @GET
    @Path("/terminarPublicarEstudio/{idProyecto}")
    @Produces("text/plain")
    public void terminarPublicarEstudio(
            @PathParam("idProyecto") Integer idProyecto) {
        System.out.println("------terminar publicación-----------" + idProyecto + "-----------------");
        proyectoLicenciamientoAmbientalFacade = (ProyectoLicenciamientoAmbientalFacade)BeanLocator.getInstance(ProyectoLicenciamientoAmbientalFacade.class);
        ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental =
                proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(idProyecto);
        participacionSocialFacade = (ParticipacionSocialFacade)BeanLocator.getInstance(ParticipacionSocialFacade.class);

        ParticipacionSocialAmbiental participacionSocialAmbiental = participacionSocialFacade.buscarCrearParticipacionSocialAmbiental(proyectoLicenciamientoAmbiental);
        participacionSocialAmbiental.setPublicacion(false);
        participacionSocialFacade.guardar(participacionSocialAmbiental);


    }
}
