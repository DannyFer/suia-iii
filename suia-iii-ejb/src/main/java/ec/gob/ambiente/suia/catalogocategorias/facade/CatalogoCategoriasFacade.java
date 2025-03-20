package ec.gob.ambiente.suia.catalogocategorias.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.catalogocategorias.service.CatalogoCategoriasServiceBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;

@Stateless
public class CatalogoCategoriasFacade {

	@EJB
	private CatalogoCategoriasServiceBean serviceBean;

	@EJB
	private CrudServiceBean crudServiceBean;

	public List<CatalogoCategoriaSistema> listarCatalogoCategorias() {
		return serviceBean.listarCatalogoCategorias();
	}

	public CatalogoCategoriaSistema buscarCatalogoCategoriasPorId(Integer id) {
		return serviceBean.buscarCatalogoCategoriasPorId(id);
	}

	public List<CatalogoCategoriaSistema> buscarCatalogoCategoriasPadres(
			String filtro, TipoSector tipoSector) {
		return serviceBean.buscarCatalogoCategoriasPadres(filtro, tipoSector);
	}

	public List<CatalogoCategoriaSistema> buscarCatalogoCategoriasPadres(
			String filtro) {
		return serviceBean.buscarCatalogoCategoriasPadres(filtro);
	}

	public List<CatalogoCategoriaSistema> buscarCatalogoCategoriasPorPadres(
			CatalogoCategoriaSistema padre) {
		return serviceBean.buscarCatalogoCategoriasPorPadre(padre);
	}

	public List<CatalogoCategoriaSistema> listarCatalogoCategoriasPorCategoriaPublica(
			CatalogoCategoriaPublico catalogoCategoriaPublico) {
		return serviceBean
				.listarCatalogoCategoriasPorCategoriaPublica(catalogoCategoriaPublico);
	}

	public List<CatalogoCategoriaPublico> listarCatalogoCategoriasPublicas() {
		return serviceBean.listarCatalogoCategoriasPublicas();
	}

	public CatalogoCategoriaPublico buscarCatalogoCategoriaPublicaPorId(
			Integer id) {
		return serviceBean.buscarCatalogoCategoriaPublicaPorId(id);
	}

	public CatalogoCategoriaSistema buscarCatalogoCategoriaSistemaPorId(
			Integer idCatalogoCategoriaSistema) {
		return serviceBean
				.buscarCatalogoCategoriaSistemaPorId(idCatalogoCategoriaSistema);
	}

	public List<CatalogoCategoriaPublico> buscarCatalogoCategoriasPublicasPadres(
			String filtro, TipoSector tipoSector) {
		return serviceBean.buscarCatalogoCategoriasPublicasPadres(filtro,
				tipoSector);
	}

	public List<CatalogoCategoriaPublico> buscarCatalogoCategoriasPublicasPadres(
			String filtro) {
		return serviceBean.buscarCatalogoCategoriasPublicasPadres(filtro);
	}

	public List<CatalogoCategoriaPublico> buscarCatalogoCategoriasPublicasPorPadres(
			CatalogoCategoriaPublico padre) {
		return serviceBean.buscarCatalogoCategoriasPublicasPorPadre(padre);
	}

	public TipoSector getTipoSector(Integer id) {
		return crudServiceBean.find(TipoSector.class, id);
	}

	public List<CatalogoCategoriaAcuerdo> buscarCatalogoCategoriaAcuerdoPorNombreOrganizacion(
			String nombreOrganizacion) {
		return serviceBean
				.buscarCatalogoCategoriaAcuerdoPorNombreOrganizacion(nombreOrganizacion);
	}

	public List<CatalogoCategoriaAcuerdo> buscarCatalogoCategoriaAcuerdoPorRucOrganizacion(
			String rucOrganizacion) {
		return serviceBean
				.buscarCatalogoCategoriaAcuerdoPorRucOrganizacion(rucOrganizacion);
	}

	public CatalogoCategoriaAcuerdo buscarCatalogoCategoriaAcuerdoPorId(
			Integer idCatalogoCategoriaAcuerdo) {
		return serviceBean
				.buscarCatalogoCategoriaAcuerdoPorId(idCatalogoCategoriaAcuerdo);
	}
	
	public List<CatalogoCategoriaSistema> buscarCatalogoCategoriasRequierenViabilidad() {
		return serviceBean.buscarCatalogoCategoriasRequierenViabilidad();
	}



	public List<CatalogoCategoriaFase> buscarCatalogoCategoriasFase(EstudioImpactoAmbiental estudio) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("paramEstudio",estudio);
		return (List<CatalogoCategoriaFase>) crudServiceBean.findByNamedQuery(CatalogoCategoriaFase.LISTAR_POR_ESTUDIO, parametros);
	}

	public List<String> obtenerIdActividadesPorManejoDesechoRequerido(String tipoManejo) {

		if (tipoManejo.compareTo("TRANSPORTE_SUSTANCIAS_QUIMICAS")==0){

			return this.serviceBean.getIdActividadesTipoManejoNativeQuery("cacs_transpor_hazardous_chemicals");

		}else if (tipoManejo.compareTo("GESTION_DESECHOS_PELIGROSOS")==0){

			return this.serviceBean.getIdActividadesTipoManejoNativeQuery("cacs_manages_hazardous_waste");

		}else if (tipoManejo.compareTo("GENERA_DESECHOS")==0){

			return this.serviceBean.getIdActividadesTipoManejoNativeQuery("cacs_waste_generated");

		}else if (tipoManejo.compareTo("UTILIZA_SUSTANCIAS_QUIMICAS")==0){

			return this.serviceBean.getIdActividadesTipoManejoNativeQuery("cacs_uses_chemical_substances");
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<CatalogoCategoriaSistema> listarCatalogoCategoriasPorSector(TipoSector tipoSector) {
		
		List<CatalogoCategoriaSistema> categorias =new ArrayList<CatalogoCategoriaSistema>();
		try {
			categorias = (List<CatalogoCategoriaSistema>) crudServiceBean
					.getEntityManager()
					.createQuery(
							"From CatalogoCategoriaSistema o where o.estado=true and o.categoriaSistema != null and o.catalogoCategoriaPublico.tipoSector.id=:idSector order by o.descripcion")
					.setParameter("idSector", tipoSector.getId()).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return categorias;
	}
}
