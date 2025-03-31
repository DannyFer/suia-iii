package ec.gob.ambiente.suia.certificado.ambiental;

import javax.ejb.Singleton;
import javax.faces.bean.ManagedProperty;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.prevencion.certificado.ambiental.controllers.CertificadoAmbientalController;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.utils.BeanLocator;

@Path("/RestServices/generarCertificadoWs")
@Singleton
public class GenerarCertificadoAmbientalWs {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(GenerarCertificadoAmbientalWs.class);	
	
	
	private ProyectoLicenciaCoa proyecto;	
	
	private Long idProceso;
	
	@GET
    @Path("/generarCertificado/{idProyecto}-{idProceso}")
    @Produces("text/plain")
    public void iniciarProceso(@PathParam("idProyecto") Integer idProyecto, @PathParam("idProceso") Long idProceso){
		
		try {
			ProyectoLicenciaCoaFacade proyectoLicenciCoaFacade = (ProyectoLicenciaCoaFacade) BeanLocator.getInstance(ProyectoLicenciaCoaFacade.class);
			
			proyecto = proyectoLicenciCoaFacade.buscarProyectoPorId(idProyecto);
			
			this.idProceso = idProceso;
			
			crearDocumento();
		} catch (Exception e) {
			LOG.error(e, e);
		}	
	}
		
	
	private void crearDocumento(){
		try {			
			
			CertificadoAmbientalController certificadoAmbientalController = (CertificadoAmbientalController) BeanLocator.getInstance(CertificadoAmbientalController.class);
			
			certificadoAmbientalController.enviarFicha(proyecto.getId(), idProceso);
			
					
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

}
