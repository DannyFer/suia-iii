package ec.gob.ambiente.suia.certificadointerseccion.service;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import ec.gob.ambiente.suia.utils.BeanLocator;

@Path("/RestServices/certificadoInterseccionWs")
@Stateless
public class CertificadoInterseccionServiceWS {

	@GET
	@Path("/detalleInterseccion/{codigoProyecto}")
	@Produces("text/plain")
	public String getDetalleInterseccion(@PathParam("codigoProyecto") String codigoProyecto) {
		try {

			CertificadoInterseccionFacade ci = (CertificadoInterseccionFacade) BeanLocator
					.getInstance(CertificadoInterseccionFacade.class);
			return ci.getCapasInterseccion(codigoProyecto);

		} catch (Exception e) {
			e.getStackTrace();
		}
		return null;
	}

}
