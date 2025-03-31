package ec.gob.ambiente.suia.catalogos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;

@Stateless
public class CatalogoGeneralService {

    private static final Logger LOG = Logger
            .getLogger(CatalogoGeneralService.class);

    @EJB
    CrudServiceBean crudServiceBean;

    /**
     *
     * @param tipoId
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<CatalogoGeneral> obtenerCatalogoXTipo(int tipoId) throws Exception {
        List<CatalogoGeneral> catalogos = null;
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("p_tipoId", tipoId);
            catalogos = (List<CatalogoGeneral>) crudServiceBean
                    .findByNamedQuery("CatalogoGeneral.findByType",
                            parameters);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception();
        }

        return catalogos;
    }
    
    @SuppressWarnings("unchecked")
    public List<CatalogoGeneral> obtenerCatalogoXTipoOrdenado(int tipoId) throws Exception {
        List<CatalogoGeneral> catalogos = null;
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("p_tipoId", tipoId);
            catalogos = (List<CatalogoGeneral>) crudServiceBean
                    .findByNamedQuery("CatalogoGeneral.findByTypeOrderByDescription",
                            parameters);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception();
        }

        return catalogos;
    }

    @SuppressWarnings("unchecked")
    public List<CatalogoGeneral> obtenerCatalogoXPadre(int padreId) throws Exception {
        List<CatalogoGeneral> catalogos = null;
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("p_padreId", padreId);
            catalogos = (List<CatalogoGeneral>) crudServiceBean
                    .findByNamedQuery("CatalogoGeneral.findByParent",
                            parameters);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception();
        }

        return catalogos;
    }
    
    @SuppressWarnings("unchecked")
    public List<CatalogoGeneral> obtenerCatalogoXPadreOrdenado(int padreId) throws Exception {
        List<CatalogoGeneral> catalogos = null;
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("p_padreId", padreId);
            catalogos = (List<CatalogoGeneral>) crudServiceBean
                    .findByNamedQuery("CatalogoGeneral.findByParentOrderById",
                            parameters);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception();
        }

        return catalogos;
    }
    @SuppressWarnings("unchecked")
    public List<CatalogoGeneral> obtenerCatalogoXPadreOrdenadoDescripcion(int padreId) throws Exception {
        List<CatalogoGeneral> catalogos = null;
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("p_padreId", padreId);
            catalogos = (List<CatalogoGeneral>) crudServiceBean
                    .findByNamedQuery("CatalogoGeneral.findByParentOrderByDescripcion",
                            parameters);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception();
        }

        return catalogos;
    }
	
	@SuppressWarnings("unchecked")
	public List<CatalogoGeneral> obtenerCatalogoXTipoXCodigo(int tipoId, String codigo) throws Exception{
		List<CatalogoGeneral> catalogos = null;
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("p_tipoId", tipoId);
			parameters.put("p_codigo", codigo);
			catalogos = (List<CatalogoGeneral>) crudServiceBean
					.findByNamedQuery("CatalogoGeneral.findByTypeAndCode",
							parameters);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception();
		}
		
		return catalogos;
	}
	
	public CatalogoGeneral obtenerCatalogoXId(Integer id) throws Exception{
		CatalogoGeneral objeto = null;
		try {
			objeto = crudServiceBean.find(CatalogoGeneral.class, id);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception();
		}
		
		return objeto;
	}
	@SuppressWarnings("unchecked")
	public List<CatalogoGeneral> obtenerCatalogoXPadreXCodigo(int padreId, String codigo) throws Exception{
		List<CatalogoGeneral> catalogos = null;
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("p_padreId", padreId);
			parameters.put("p_codigo", codigo);
			catalogos = (List<CatalogoGeneral>) crudServiceBean
					.findByNamedQuery("CatalogoGeneral.findByParentAndCode",
							parameters);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception();
		}
		
		return catalogos;
	}
	/**
	 * Lista de catalogos por tipo de categoria que no hayan sido seleccionados y guardados en tdrEia
	 *
	 * @param idCaty
	 * @param idProyecto
	 * @param idTdrEia
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<CatalogoGeneral> obtenerCatalogoXTipoSeleccionado(int idCaty,
			int idProyecto) throws Exception {
		List<CatalogoGeneral> catalogos = (List<CatalogoGeneral>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT c FROM CatalogoGeneral c where c.estado = true "
						+ "and c.tipoCatalogo.id = :idCaty and c not in (SELECT "
						+ "p.catalogoGeneral FROM ProyectoGeneralCatalogo p WHERE "
						+ "p.proyectoLicenciamientoAmbiental.id = :idProyecto AND "
						+ "p.catalogoGeneral.tipoCatalogo.id = :idCaty) order by "
						+ "c.descripcion")
				.setParameter("idCaty", idCaty)
				.setParameter("idProyecto", idProyecto).getResultList();
		return catalogos;
	}
	
	/**
	 * Lista de catalogos Medio Fisico no seleccionados y guardados en tdrEia
	 *
	 * @param idCaty
	 * @param idProyecto
	 * @param idTdrEia
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<CatalogoGeneral> catalogoMedioFisicoXTipoSeleccionado(int idCaty,
			int idProyecto) throws Exception {
		List<CatalogoGeneral> catalogos = (List<CatalogoGeneral>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT c FROM CatalogoGeneral c where c.estado = true "
						+ "and c.tipoCatalogo.id = :idCaty and c not in (SELECT "
						+ "p.catalogoGeneral FROM MedioFisico p WHERE "
						+ "p.proyectoLicenciamientoAmbiental.id = :idProyecto AND "
						+ "p.catalogoGeneral.tipoCatalogo.id = :idCaty) order by "
						+ "c.descripcion")
				.setParameter("idCaty", idCaty)
				.setParameter("idProyecto", idProyecto).getResultList();
		return catalogos;
	}
	
	/**
	 * Lista de catalogos Medio Biotico no seleccionados y guardados en tdrEia
	 *
	 * @param idCaty
	 * @param idProyecto
	 * @param idTdrEia
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<CatalogoGeneral> catalogoMedioBioticoXTipoSeleccionado(int idCaty,
			int idProyecto) throws Exception {
		List<CatalogoGeneral> catalogos = (List<CatalogoGeneral>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT c FROM CatalogoGeneral c where c.estado = true "
						+ "and c.tipoCatalogo.id = :idCaty and c not in (SELECT "
						+ "p.catalogoGeneral FROM MedioBioticoTdr p WHERE "
						+ "p.proyectoLicenciamientoAmbiental.id = :idProyecto AND "
						+ "p.catalogoGeneral.tipoCatalogo.id = :idCaty) order by "
						+ "c.descripcion")
				.setParameter("idCaty", idCaty)
				.setParameter("idProyecto", idProyecto).getResultList();
		return catalogos;
	}
	
	/**
	 * Lista de catalogos Medio Social no seleccionados y guardados en tdrEia
	 *
	 * @param idCaty
	 * @param idProyecto
	 * @param idTdrEia
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<CatalogoGeneral> catalogoMedioSocialXTipoSeleccionado(int idCaty,
			int idProyecto) throws Exception {
		List<CatalogoGeneral> catalogos = (List<CatalogoGeneral>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT c FROM CatalogoGeneral c where c.estado = true "
						+ "and c.tipoCatalogo.id = :idCaty and c not in (SELECT "
						+ "p.catalogoGeneral FROM MedioSocial p WHERE "
						+ "p.proyectoLicenciamientoAmbiental.id = :idProyecto AND "
						+ "p.catalogoGeneral.tipoCatalogo.id = :idCaty) order by "
						+ "c.descripcion")
				.setParameter("idCaty", idCaty)
				.setParameter("idProyecto", idProyecto).getResultList();
		return catalogos;
	}
	
    public CatalogoGeneral obtenerXTipoXCodigo(int tipoId, String codigo) throws Exception {
        List<CatalogoGeneral> catalogos = null;
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("p_tipoId", tipoId);
            parameters.put("p_codigo", codigo);
            catalogos = crudServiceBean
                    .findByNamedQueryGeneric(CatalogoGeneral.LISTAR_POR_TIPOID_CODIGO,
                            parameters);
            if(catalogos != null && !catalogos.isEmpty()){
            	return catalogos.get(0);
            }else{
            	return null;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception();
        }
    }

}
