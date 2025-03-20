package ec.gob.ambiente.suia.prevencion.categoria2.facade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ActividadEspecial;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaFase;
import ec.gob.ambiente.suia.domain.DetalleFichaPlan;
import ec.gob.ambiente.suia.domain.FasesFichaAmbiental;

@Stateless
public class FaseFichaAmbientalFacade implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6281008886797733223L;

	@EJB
	private CrudServiceBean crudServiceBean;

	public List<CatalogoCategoriaFase> obtenerCatalogoCategoriaFasesPorSubsectorCodigo(
			String codigo) throws Exception {

		try {
			Map<String, Object> parametro = new HashMap<String, Object>();
			parametro.put("p_codigo", codigo);

			return crudServiceBean.findByNamedQueryGeneric(
					CatalogoCategoriaFase.LISTAR_POR_SUBSECTOR_CODIGO,
					parametro);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
public List<CatalogoCategoriaFase> obtenerCatalogoCategoriaFasesLA() throws Exception {

		try {
			Map<String, Object> parametro = new HashMap<String, Object>();

			return crudServiceBean.findByNamedQueryGeneric(
					CatalogoCategoriaFase.LISTAR_POR_SUBSECTOR_LA,
					parametro);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	public void eliminarFasesExistentes(Integer fichaId) throws Exception {
		try {
			Map<String, Object> parametro = new HashMap<String, Object>();
			parametro.put("p_fichaId", fichaId);

			List<FasesFichaAmbiental> fases = crudServiceBean
					.findByNamedQueryGeneric(
							FasesFichaAmbiental.LISTAR_POR_FICHA_ID, parametro);

			if (fases != null) {
				crudServiceBean.delete(fases);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public void guardarFasesFicha(List<FasesFichaAmbiental> fases)
			throws Exception {
		try {
			crudServiceBean.saveOrUpdate(fases);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public void guardarFasesFicha(FasesFichaAmbiental fase) throws Exception {
		try {
			crudServiceBean.saveOrUpdate(fase);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public List<CatalogoCategoriaFase> obtenerCatalogoCategoriaFasesPorFicha(Integer fichaId) throws Exception {

		try {
			Map<String, Object> parametro = new HashMap<String, Object>();
			parametro.put("p_fichaId", fichaId);

			List<FasesFichaAmbiental> fases = crudServiceBean.findByNamedQueryGeneric(
							FasesFichaAmbiental.LISTAR_POR_FICHA_ID, parametro);

			List<CatalogoCategoriaFase> catalogoCategoriaFases = new ArrayList<CatalogoCategoriaFase>();
			if (fases != null) {
				for (FasesFichaAmbiental fasesFichaAmbiental : fases) {					
					catalogoCategoriaFases.add(fasesFichaAmbiental.getCatalogoCategoriaFase());
					fasesFichaAmbiental.getCatalogoCategoriaFase().getTipoSubsector().getId();
				}
			}
			return catalogoCategoriaFases;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public void guardarDetalleFichaPlan(DetalleFichaPlan fichaPlan)
			throws Exception {
		try {
			crudServiceBean.saveOrUpdate(fichaPlan);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@SuppressWarnings("unchecked")
	public Integer getActividadEspecial(Integer subSectorId) {
		List<ActividadEspecial> catalogo = (List<ActividadEspecial>) crudServiceBean
				.getEntityManager()
				.createQuery("From ActividadEspecial a where a.tipoSubsector.id =:subSectorId and a.estado = true")
				.setParameter("subSectorId", subSectorId).getResultList();

		if(catalogo.isEmpty())
			return -1;
		else
			return catalogo.get(0).getId();
	}
	
	
	/**
	 * Cris F: metodo para buscar fases
	 */
	public List<FasesFichaAmbiental> buscarFasesExistentes(Integer fichaId) throws Exception {
		try {
			Map<String, Object> parametro = new HashMap<String, Object>();
			parametro.put("p_fichaId", fichaId);

			List<FasesFichaAmbiental> fases = crudServiceBean
					.findByNamedQueryGeneric(
							FasesFichaAmbiental.LISTAR_POR_FICHA_ID, parametro);
			
			if(fases != null && !fases.isEmpty())
				return fases;
			else
				return null;
			
		} catch (Exception e) {
			throw new Exception(e);			
		}
	}
	
	/**
	 * Cris F: Metodo para eliminar un elemento
	 */
	public void eliminarFases(List<FasesFichaAmbiental> fases) throws Exception {
		try {			
			if (fases != null) {
				crudServiceBean.delete(fases);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	/**
	 * Cris F: Metodo para obtener las fases que se guardaron como historico
	 * @param fichaId
	 * @return
	 * @throws Exception
	 */
	public List<FasesFichaAmbiental> obtenerFasesHistoricoPorFicha(Integer fichaId) throws Exception {

		try {				
			 Query query = crudServiceBean.getEntityManager().createQuery("SELECT c FROM FasesFichaAmbiental c where "
					+ "c.estado = true and c.fichaAmbientalPma.id = :p_fichaId and (c.idRegistroOriginal != null or c.fechaHistorico != null) "
					+ "order by c.id asc");			 
			
			query.setParameter("p_fichaId", fichaId);

			List<FasesFichaAmbiental> fases = query.getResultList();
			
			return fases;
			
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
