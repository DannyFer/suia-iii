package ec.gob.ambiente.suia.solicitudautorizacionesadministrativas.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.SolicitudAutorizacionesAdministrativas;

@Stateless
public class SolicitudAutorizacionesAdministrativasBean {

	@EJB
	private CrudServiceBean crudServiceCatalogoCategoriasBean;

	public SolicitudAutorizacionesAdministrativas buscarSolicitudPorId(
			long idSolicitud) {
		SolicitudAutorizacionesAdministrativas solicitud = (SolicitudAutorizacionesAdministrativas) crudServiceCatalogoCategoriasBean
				.getEntityManager()
				.createQuery(
						"From SolicitudAutorizacionesAdministrativas s where s.id =:Identificador")
				.setParameter("Identificador", idSolicitud).getSingleResult();
		return solicitud;
	}

	public SolicitudAutorizacionesAdministrativas buscarSolicitudPorIdProceso(
			long idProceso) {
		SolicitudAutorizacionesAdministrativas solicitud = (SolicitudAutorizacionesAdministrativas) crudServiceCatalogoCategoriasBean
				.getEntityManager()
				.createQuery(
						"From SolicitudAutorizacionesAdministrativas s where s.proceso =:Identificador")
				.setParameter("Identificador", idProceso).getSingleResult();
		return solicitud;
	}

}
