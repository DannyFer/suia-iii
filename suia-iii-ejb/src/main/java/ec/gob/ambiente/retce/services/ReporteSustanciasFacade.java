package ec.gob.ambiente.retce.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.ReporteSustancias;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class ReporteSustanciasFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	public String generarCodigoTramite() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-RETCE-SR-"
					+ secuenciasFacade.getCurrentYear()
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence("MAAE-RETCE-SUSTANCIAS", 4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public List<ReporteSustancias> getByInformacionProyecto(Integer idInformacionProyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyecto", idInformacionProyecto);
		try {
			List<ReporteSustancias> lista = (List<ReporteSustancias>) crudServiceBean.findByNamedQuery(ReporteSustancias.GET_POR_INFORMACION_PROYECTO,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public ReporteSustancias getByInformacionProyectoAnio(Integer idInformacionProyecto, Integer anio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyecto", idInformacionProyecto);
		parameters.put("anio", anio);
		try {
			List<ReporteSustancias> lista = (List<ReporteSustancias>) crudServiceBean.findByNamedQuery(ReporteSustancias.GET_POR_ANIO_INFORMACION_PROYECTO,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public ReporteSustancias getById(Integer idReporte) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idConsumo", idReporte);
		try {
			ReporteSustancias consumo = (ReporteSustancias) crudServiceBean.find(ReporteSustancias.class, idReporte);
			return consumo;
		} catch (Exception e) {
			return null;
		}
	}
	
	public void guardarReporte(ReporteSustancias reporte) {
		crudServiceBean.saveOrUpdate(reporte);
	}
	
	public void eliminarConsumo(ReporteSustancias reporte) {
		//borrar relacionados
		
		if(reporte.getListaSustanciasRetce() != null)
			eliminarSustancia(reporte.getListaSustanciasRetce());
		
		crudServiceBean.saveOrUpdate(reporte);
	}
	
	public void eliminarSustancia(List<SubstanciasRetce> listaSustancia) {
		for (SubstanciasRetce sustancia : listaSustancia) {
			if(sustancia.getDatosLaboratorio() != null){
				sustancia.getDatosLaboratorio().setEstado(false);
				crudServiceBean.saveOrUpdate(sustancia.getDatosLaboratorio());
			}
			
			if(sustancia.getDocumento() != null) {
				sustancia.getDocumento().setEstado(false);
				crudServiceBean.saveOrUpdate(sustancia.getDocumento());
			}
			
			sustancia.setEstado(false);
			crudServiceBean.saveOrUpdate(sustancia);
		}
	}

}
