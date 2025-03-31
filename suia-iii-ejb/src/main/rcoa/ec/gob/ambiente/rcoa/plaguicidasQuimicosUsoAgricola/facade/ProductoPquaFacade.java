package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DosisCultivo;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.OficioPronunciamientoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.PlagaCultivo;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProductoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidas;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidasDetalle;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.RegistroProducto;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ProductoPquaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
    private ProyectoPlaguicidasFacade proyectoPlaguicidasFacade;
	@EJB
    private ProyectoPlaguicidasDetalleFacade proyectoPlaguicidasDetalleFacade;
    @EJB
    private PlagaCultivoFacade plagaCultivoFacade;
	@EJB
    private DosisCultivoFacade dosisCultivoFacade;


	public ProductoPqua guardar(ProductoPqua producto){
		return crudServiceBean.saveOrUpdate(producto);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductoPqua> listaProductosPorRegistroProducto(RegistroProducto registro){
		List<ProductoPqua> lista = new ArrayList<>();
		try {

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("registro", registro);
			
			lista = (ArrayList<ProductoPqua>) crudServiceBean
					.findByNamedQuery(ProductoPqua.GET_POR_REGISTRO_PRODUCTO, parameters);
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;		
	}
	

	@SuppressWarnings("unchecked")
	public List<ProductoPqua> listaProductosPorUsuarioFecha(String usuario, Date mesIngreso){
		List<ProductoPqua> lista = new ArrayList<>();
		try {

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("usuario", usuario);
			parameters.put("mesIngreso", mesIngreso);
			
			lista = (ArrayList<ProductoPqua>) crudServiceBean
					.findByNamedQuery(ProductoPqua.GET_PENDIENTES_POR_USUARIO_MES, parameters);
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;		
	}
	

	@SuppressWarnings("unchecked")
	public List<ProductoPqua> getProductosFisicos() {
		Map<String, Object> parameters = new HashMap<String, Object>();

		List<ProductoPqua> lista = (List<ProductoPqua>) crudServiceBean
				.findByNamedQuery(ProductoPqua.GET_REGISTROS_FISICOS, parameters);

		if (lista == null || lista.isEmpty()) {
			return new ArrayList<>();
		} else {
			return lista;
		}
	}
	
	public List<ProductoPqua> getProductoPquaAprobados() {
		CriteriaBuilder cb = crudServiceBean.getEntityManager().getCriteriaBuilder();

		Metamodel m = crudServiceBean.getEntityManager().getMetamodel();
		EntityType<OficioPronunciamientoPqua> consultaProyecto = m.entity(OficioPronunciamientoPqua.class);
		CriteriaQuery<ProductoPqua> cq = cb.createQuery(ProductoPqua.class);

		Root<OficioPronunciamientoPqua> oficio = cq.from(consultaProyecto);
		Join<OficioPronunciamientoPqua, ProyectoPlaguicidas> proyecto = oficio.join("proyectoPlaguicidas", JoinType.INNER);
		Join<ProyectoPlaguicidas, ProductoPqua> producto = proyecto.join("productoPqua", JoinType.INNER);
		
		Predicate predicado = cb.conjunction();
		predicado = cb.and(predicado, cb.equal(proyecto.get("estado"), true));
		predicado = cb.and(predicado, cb.equal(oficio.get("estado"), true));
		predicado = cb.and(predicado, cb.equal(producto.get("estado"), true));
		predicado = cb.and(predicado, cb.equal(oficio.get("esAprobacion"), true));
		predicado = cb.and(predicado, cb.isNotNull(oficio.get("fechaFirma")));
		
		cq.select(producto);
		cq.where(predicado);
		
		TypedQuery<ProductoPqua> query = crudServiceBean.getEntityManager().createQuery(cq);
		
		return query.getResultList();
	}

	public void eliminarProducto(ProductoPqua producto){
		producto.setEstado(false);
		guardar(producto);

		List<ProyectoPlaguicidasDetalle> listaDetalleProyectoPlaguicidas = new ArrayList<>();

		if(producto.getAprobacionFisica()) {
			listaDetalleProyectoPlaguicidas = proyectoPlaguicidasDetalleFacade.getDetallePorProducto(producto.getId());
		} else {
			ProyectoPlaguicidas proyectoPlaguicidas = proyectoPlaguicidasFacade.getPorProducto(producto.getId());
			listaDetalleProyectoPlaguicidas = proyectoPlaguicidasDetalleFacade.getDetallePorProyecto(proyectoPlaguicidas.getId());
		}

		for (ProyectoPlaguicidasDetalle detalle : listaDetalleProyectoPlaguicidas) {
			detalle.setEstado(false);
			proyectoPlaguicidasDetalleFacade.guardar(detalle);

			for (PlagaCultivo plagaCultivo : detalle.getListaPlagaCultivo()) {
				plagaCultivo.setEstado(false);
					
				plagaCultivoFacade.guardar(plagaCultivo);
			}
				
			for (DosisCultivo dosisCultivo : detalle.getListaDosisCultivo()) {
				dosisCultivo.setEstado(false);
					
				dosisCultivoFacade.guardar(dosisCultivo);
			}
		}
	}
	
}
