package ec.gob.ambiente.suia.prevencion.categoria2.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.DetalleFichaPma;

/**
 * 
 * @author karla.carvajal
 * 
 */
@Stateless
public class DetalleFichaPmaServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<DetalleFichaPma> getDetallesFichaPmaPorIdFichaAnnType(
			Integer idFicha, Integer idCatalogo) {
		List<DetalleFichaPma> detallesFicha = new ArrayList<DetalleFichaPma>();
		detallesFicha = (List<DetalleFichaPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From DetalleFichaPma d where d.fichaAmbientalPma.id =:idFicha and d.estado = true and d.catalogoGeneral.tipoCatalogo.id = :idCatalogo and d.idRegistroOriginal = null")
				.setParameter("idFicha", idFicha)
				.setParameter("idCatalogo", idCatalogo).getResultList();

		return detallesFicha;
	}

	@SuppressWarnings("unchecked")
	public List<CatalogoGeneral> getCatalogoGeneralFichaPorIdFichaPorTipo(
			Integer idFicha, Integer tipo) {
		List<CatalogoGeneral> catalogos = new ArrayList<CatalogoGeneral>();
		catalogos = (List<CatalogoGeneral>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"select d.catalogoGeneral From DetalleFichaPma d where d.fichaAmbientalPma.id =:idFicha and d.catalogoGeneral.tipoCatalogo.id = :tipo and d.estado = true and d.idRegistroOriginal = null")
				.setParameter("idFicha", idFicha).setParameter("tipo", tipo)
				.getResultList();
		return catalogos;
	}

	public void eliminarDetalleFichaPma(
			List<DetalleFichaPma> listaDetallesFichaPma) {
		for (DetalleFichaPma detalleFichaPma : listaDetallesFichaPma) {
			crudServiceBean.delete(detalleFichaPma);
		}
	}

	public void guardarDetalleFichaPma(DetalleFichaPma detalleFichaPma) {
		if (detalleFichaPma.getId() == null) {
			crudServiceBean.saveOrUpdate(detalleFichaPma);
		}
	}

	@SuppressWarnings("unchecked")
	public List<DetalleFichaPma> getCatalogoGeneralFichaXIdFichaXTipoXCodigo(
			Integer idFicha, Integer tipo, String codigo) {
		List<DetalleFichaPma> detallesFicha = new ArrayList<DetalleFichaPma>();
		detallesFicha = (List<DetalleFichaPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From DetalleFichaPma d where d.fichaAmbientalPma.id =:idFicha and d.estado = true "
						+ "and d.catalogoGeneral.tipoCatalogo.id = :idCatalogo and d.catalogoGeneral.codigo=:p_codigo and d.idRegistroOriginal = null")
				.setParameter("idFicha", idFicha)
				.setParameter("idCatalogo", tipo)
				.setParameter("p_codigo", codigo).getResultList();

		return detallesFicha;
	}

	@SuppressWarnings("unchecked")
	public List<CatalogoGeneral> getCatalogoGeneralFichaPorIdFichaPorTipoPorCodigo(
			Integer idFicha, Integer tipo, String codigo) {
		List<CatalogoGeneral> catalogos = new ArrayList<CatalogoGeneral>();
		catalogos = (List<CatalogoGeneral>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"select d.catalogoGeneral From DetalleFichaPma d where d.fichaAmbientalPma.id =:idFicha "
						+ "and d.catalogoGeneral.tipoCatalogo.id = :tipo and d.estado = true "
						+ "and d.catalogoGeneral.codigo = :p_codigo and d.idRegistroOriginal = null")
				.setParameter("idFicha", idFicha).setParameter("tipo", tipo).setParameter("p_codigo", codigo)
				.getResultList();
		return catalogos;
	}
		
	
	/**
	 * Cris F 
	 * Consulta para historico de infraestructura	 
	 */
	
	@SuppressWarnings("unchecked")
	public List<DetalleFichaPma> getDetalleFichaPorIdFichaPorTipoHistorico(Integer idFicha, Integer tipo) {
		List<DetalleFichaPma> catalogos = new ArrayList<DetalleFichaPma>();
		catalogos = (List<DetalleFichaPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"select d From DetalleFichaPma d where d.fichaAmbientalPma.id =:idFicha and d.catalogoGeneral.tipoCatalogo.id = :tipo and d.estado = true and d.idRegistroOriginal != null order by 1 desc")
				.setParameter("idFicha", idFicha).setParameter("tipo", tipo)
				.getResultList();
		return catalogos;
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleFichaPma> getCatalogoGeneralFichaPorIdFichaPorTipoPorCodigoHistorico(
			Integer idFicha, Integer tipo, String codigo) {
		List<DetalleFichaPma> catalogos = new ArrayList<DetalleFichaPma>();
		catalogos = (List<DetalleFichaPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"select d From DetalleFichaPma d where d.fichaAmbientalPma.id =:idFicha "
						+ "and d.catalogoGeneral.tipoCatalogo.id = :tipo and d.estado = true "
						+ "and d.catalogoGeneral.codigo = :p_codigo and d.idRegistroOriginal != null order by 1")
				.setParameter("idFicha", idFicha).setParameter("tipo", tipo).setParameter("p_codigo", codigo).getResultList();
		return catalogos;
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleFichaPma> getDetalleFichaPorIdFichaPorTipoPorCodigo(
			Integer idFicha, Integer tipo, String codigo) {
		List<DetalleFichaPma> catalogos = new ArrayList<DetalleFichaPma>();
		catalogos = (List<DetalleFichaPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"select d From DetalleFichaPma d where d.fichaAmbientalPma.id = :idFicha "
						+ "and d.catalogoGeneral.tipoCatalogo.id = :tipo and d.estado = true "
						+ "and d.catalogoGeneral.codigo = :p_codigo and d.idRegistroOriginal = null")
				.setParameter("idFicha", idFicha).setParameter("tipo", tipo).setParameter("p_codigo", codigo).getResultList();
		return catalogos;
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleFichaPma> getCatalogoGeneralFichaPorIdFichaPorTipoGeneral(
			Integer idFicha, Integer tipo) {
		List<DetalleFichaPma> catalogos = new ArrayList<DetalleFichaPma>();
		catalogos = (List<DetalleFichaPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"select d  From DetalleFichaPma d where d.fichaAmbientalPma.id =:idFicha and d.catalogoGeneral.tipoCatalogo.id = :tipo and d.estado = true")
				.setParameter("idFicha", idFicha).setParameter("tipo", tipo)
				.getResultList();
		return catalogos;
	}
	
	
	/**
	 * Fin historico
	 */

}