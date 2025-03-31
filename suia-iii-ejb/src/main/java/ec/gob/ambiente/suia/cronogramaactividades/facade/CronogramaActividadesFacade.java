package ec.gob.ambiente.suia.cronogramaactividades.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.cronogramaactividades.service.CronogramaActividadesServiceBean;
import ec.gob.ambiente.suia.domain.CronogramaActividades;

@Stateless
public class CronogramaActividadesFacade {

	@EJB
	private CronogramaActividadesServiceBean serviceBean;

	public List<CronogramaActividades> listarCronogramaActividades() {
		return serviceBean.listarCronogramaActividades();
	}

	public CronogramaActividades buscarCronogramaActividadesPorId(
			Long idCronogramaActividades) {
		return serviceBean
				.buscarCronogramaActividadesPorId(idCronogramaActividades);
	}

	public List<CronogramaActividades> buscarCronogramaActividadesPorIdProceso(
			long idProceso) {
		return serviceBean.buscarCronogramaActividadesPorIdProceso(idProceso);
	}
}
