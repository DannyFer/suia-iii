package ec.gob.ambiente.suia.solicitudautorizacionesadministrativas.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.SolicitudAutorizacionesAdministrativas;
import ec.gob.ambiente.suia.solicitudautorizacionesadministrativas.service.SolicitudAutorizacionesAdministrativasBean;

/**
 * 
 * <b> Facade para solicitudes administrativas. </b>
 * 
 * @author frank torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: frank torres, Fecha: 20/12/2014]
 *          </p>
 */
@Stateless
public class SolicitudAutorizacionesAdministrativasFacade {

	@EJB
	private SolicitudAutorizacionesAdministrativasBean solicitudAutorizacionesAdministrativasBean;

	/**
	 * Obtener solicitud por Id
	 * 
	 * @param idSolicitud
	 * @return
	 */
	public SolicitudAutorizacionesAdministrativas buscarSolicitudPorId(
			Integer idSolicitud) {
		return solicitudAutorizacionesAdministrativasBean
				.buscarSolicitudPorId(idSolicitud);
	}

	public SolicitudAutorizacionesAdministrativas buscarSolicitudPorIdProceso(
			long idProceso) {
		try {
			SolicitudAutorizacionesAdministrativas solicitud = solicitudAutorizacionesAdministrativasBean
					.buscarSolicitudPorIdProceso(idProceso);

			solicitud.getCantones().size();
			return solicitud;
		} catch (Exception e) {
			return null;
		}

	}
}
