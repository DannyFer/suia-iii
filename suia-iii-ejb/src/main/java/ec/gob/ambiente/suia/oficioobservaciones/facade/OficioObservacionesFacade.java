package ec.gob.ambiente.suia.oficioobservaciones.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.OficioObservaciones;
import ec.gob.ambiente.suia.oficioobservaciones.service.OficioObservacionesBean;

/**
 * 
 * <b> Facade para almacenar oficios de observaciones. </b>
 * 
 * @author Frank Torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Frank Torres, Fecha: 27/01/2015]
 *          </p>
 */
@Stateless
public class OficioObservacionesFacade {

	@EJB
	private OficioObservacionesBean oficioObservacionesBean;

	public OficioObservaciones oficioObservacionesPorId(Integer id) {

		return oficioObservacionesBean.oficioObservacionesPorId(id);

	}

	public OficioObservaciones oficioObservacionesPorIdProceso(Long idProceso) {

		return oficioObservacionesBean
				.oficioObservacionesPorIdProceso(idProceso);
	}
}
