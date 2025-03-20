package ec.gob.ambiente.suia.catalogos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.CategoriaIICatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.TipoCatalogo;

@Stateless
public class ClimaService {

	Integer idCategoria = TipoCatalogo.CLIMA;
	String codigoCategoria = TipoCatalogo.CODIGO_CLIMA;

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<CategoriaIICatalogoGeneralFisico> climasSeleccionadosCategoriaII(
			Integer idProyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);
		parameters.put("catyCode", codigoCategoria);
		return (List<CategoriaIICatalogoGeneralFisico>) crudServiceBean
				.findByNamedQuery(
						CategoriaIICatalogoGeneralFisico.FIND_BY_PROJECT_CATYCODE,
						parameters);
	}

	@SuppressWarnings("unchecked")
	public List<CatalogoGeneralFisico> obtenerListaClima() {

		return crudServiceBean
				.getEntityManager()
				.createQuery(
						"From CatalogoGeneralFisico c where c.tipoCatalogo.codigo = :catyCode order by c.orden")
				.setParameter("catyCode", codigoCategoria).getResultList();

	}

	// public void adicionarMedioSocial(MedioSocial MedioSocial) throws
	// Exception {
	// crudServiceBean.saveOrUpdate(MedioSocial);
	// }
	//
	// public void eliminarMedioSocial(MedioSocial MedioSocial) throws Exception
	// {
	// crudServiceBean.delete(MedioSocial);
	// }

}
