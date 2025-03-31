package ec.gob.ambiente.suia.reportes.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;

@Stateless
public class ReportTemplatesFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardar(PlantillaReporte plantilla) {
		crudServiceBean.saveOrUpdate(plantilla);
	}
	
	public void eliminar(PlantillaReporte plantilla) {
		crudServiceBean.delete(plantilla);
	}

	@SuppressWarnings("unchecked")
	public List<PlantillaReporte> getPlantillaReportes() {
		return (List<PlantillaReporte>) crudServiceBean.findAll(PlantillaReporte.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoDocumento> getTiposDocumento() {
		return (List<TipoDocumento>) crudServiceBean.findAll(TipoDocumento.class);
	}
}
