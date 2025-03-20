package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeTecnicoForestal;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformeRevisionForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;

	public void guardar(InformeTecnicoForestal informe) {
		crudServiceBean.saveOrUpdate(informe);
	}

	@SuppressWarnings("unchecked")
	public InformeTecnicoForestal getInformePorViabilidad(Integer idViabilidad) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idViabilidad", idViabilidad);

		try {
			List<InformeTecnicoForestal> lista = (List<InformeTecnicoForestal>) crudServiceBean
					.findByNamedQuery(InformeTecnicoForestal.GET_POR_VIABILIDAD, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public InformeTecnicoForestal guardar(InformeTecnicoForestal informe, Area area) {
		if(informe.getNumeroInforme() == null)
			informe.setNumeroInforme(generarCodigo(area));
			
		return crudServiceBean.saveOrUpdate(informe);
	}
	
	private String generarCodigo(Area area) {
		String anioActual=secuenciasFacade.getCurrentYear();
		String nombreSecuencia="INFORME_REVISION_FORESTAL_"+anioActual+"_"+area.getAreaAbbreviation();
		
		try {
			return Constantes.SIGLAS_INSTITUCION + "-"
					+ area.getAreaAbbreviation()
					+ "-"
					+ anioActual
					+ "-"
					+ "IO"
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public InformeTecnicoForestal getInformePorViabilidad(Integer idViabilidad, Integer tipo, Integer numeroRevision) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idViabilidad", idViabilidad);
		parameters.put("tipoInforme", tipo);
		parameters.put("numeroRevision", numeroRevision);

		try {
			List<InformeTecnicoForestal> lista = (List<InformeTecnicoForestal>) crudServiceBean
					.findByNamedQuery(InformeTecnicoForestal.GET_POR_VIABILIDAD_TIPO_INFORME_REVISION, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public InformeTecnicoForestal getInformePorViabilidad(Integer idViabilidad, Integer tipo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idViabilidad", idViabilidad);
		parameters.put("tipoInforme", tipo);

		try {
			List<InformeTecnicoForestal> lista = (List<InformeTecnicoForestal>) crudServiceBean
					.findByNamedQuery(InformeTecnicoForestal.GET_POR_VIABILIDAD_TIPO_INFORME, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public InformeTecnicoForestal guardarInformeApoyo(InformeTecnicoForestal informe, Area area) {
		if(informe.getNumeroInforme() == null)
			informe.setNumeroInforme(generarCodigoInformeApoyo(area));
			
		return crudServiceBean.saveOrUpdate(informe);
	}

	private String generarCodigoInformeApoyo(Area area) {
		String anioActual = secuenciasFacade.getCurrentYear();
		String areaSecuencia = area.getArea().getAreaAbbreviation();
		String nombreSecuencia = "INFORME_REVISION_FORESTAL_" + anioActual + "_" + areaSecuencia;

		String[] areaTramite = area.getAreaAbbreviation().split("-");
		
		try {
			return Constantes.SIGLAS_INSTITUCION + "-"
					+ Constantes.SIGLAS_SUIA + "-"
					+ area.getArea().getAreaAbbreviation() + "-" 
					+ areaTramite[0] + "-" 
					+ anioActual + "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
