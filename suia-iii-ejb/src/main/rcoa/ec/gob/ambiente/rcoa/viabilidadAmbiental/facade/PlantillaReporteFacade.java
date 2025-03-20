package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;

@Stateless
public class PlantillaReporteFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public PlantillaReporte getPlantillaReporte(TipoDocumentoSistema tipoDocumento) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("p_tipoDocumentoId", tipoDocumento.getIdTipoDocumento());
		return crudServiceBean.findByNamedQuerySingleResult(PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parameters);
	}
	
	public PlantillaReporte obtenerPlantillaReportePorCodigo(Integer tipoDocumentoId, String codigoProceso) {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("p_tipoDocumentoId", tipoDocumentoId);
			parametros.put("codProceso", codigoProceso);

			List<PlantillaReporte> lista = this.crudServiceBean.findByNamedQueryGeneric(
					PlantillaReporte.OBTENER_PLANTILLA_POR_TIPO_POR_CODIGO, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (PlantillaReporte) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
}
