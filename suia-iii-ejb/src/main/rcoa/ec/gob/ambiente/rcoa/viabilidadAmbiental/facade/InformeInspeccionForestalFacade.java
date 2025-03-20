package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionForestal;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformeInspeccionForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;

	public void guardar(InformeInspeccionForestal informe) {			
		crudServiceBean.saveOrUpdate(informe);
	}

	@SuppressWarnings("unchecked")
	public InformeInspeccionForestal getInformePorViabilidad(Integer idViabilidad) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idViabilidad", idViabilidad);

		try {
			List<InformeInspeccionForestal> lista = (List<InformeInspeccionForestal>) crudServiceBean
					.findByNamedQuery(InformeInspeccionForestal.GET_POR_VIABILIDAD, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public InformeInspeccionForestal guardar(InformeInspeccionForestal informe, Area area) {
		if(informe.getId() == null)
			informe.setNumeroInforme(generarCodigo(area));
			
		return crudServiceBean.saveOrUpdate(informe);
	}
	
	private String generarCodigo(Area area) {
		String anioActual=secuenciasFacade.getCurrentYear();
		String nombreSecuencia="INFORME_INSPECCION_FORESTAL"+anioActual+"_"+area.getAreaAbbreviation();
		
		try {
			return Constantes.SIGLAS_INSTITUCION + "-"
					+ area.getAreaAbbreviation()
					+ "-"
					+ anioActual
					+ "-IO-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 3);
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public InformeInspeccionForestal getInformePorViabilidadRevision(Integer idViabilidad, Integer numeroRevision) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idViabilidad", idViabilidad);
		parameters.put("numeroRevision", numeroRevision);

		try {
			List<InformeInspeccionForestal> lista = (List<InformeInspeccionForestal>) crudServiceBean
					.findByNamedQuery(InformeInspeccionForestal.GET_POR_VIABILIDAD_REVISION, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public InformeInspeccionForestal guardarInformeViabilidad(InformeInspeccionForestal informe, Area area) {
		if(informe.getNumeroInforme() == null)
			informe.setNumeroInforme(generarCodigoInformeViabilidad(area));
			
		return crudServiceBean.saveOrUpdate(informe);
	}

	private String generarCodigoInformeViabilidad(Area area) {
		String anioActual = secuenciasFacade.getCurrentYear();
		String areaSecuencia = area.getArea().getAreaAbbreviation();
		String nombreSecuencia = "INFORME_INSPECCION_FORESTAL" + anioActual + "_" + areaSecuencia;

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
