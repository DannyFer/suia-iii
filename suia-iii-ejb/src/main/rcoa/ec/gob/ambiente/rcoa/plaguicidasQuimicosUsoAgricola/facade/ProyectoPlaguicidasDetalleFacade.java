package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DosisCultivo;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.PlagaCultivo;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidasDetalle;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ProyectoPlaguicidasDetalleFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public ProyectoPlaguicidasDetalle guardar(ProyectoPlaguicidasDetalle detalle) {
		return crudServiceBean.saveOrUpdate(detalle);
	}

	public void eliminar(ProyectoPlaguicidasDetalle detalle) {
		for (PlagaCultivo plagaCultivo : detalle.getListaPlagaCultivo()) {
			if(plagaCultivo.getId() != null) {
				plagaCultivo.setEstado(false);
				crudServiceBean.saveOrUpdate(plagaCultivo);
			}
		}

		for (DosisCultivo dosisCultivo : detalle.getListaDosisCultivo()) {
			if(dosisCultivo.getId() != null) {
				dosisCultivo.setEstado(false);
				crudServiceBean.saveOrUpdate(dosisCultivo);
			}
		}

		crudServiceBean.saveOrUpdate(detalle);
	}

	@SuppressWarnings("unchecked")
	public List<ProyectoPlaguicidasDetalle> getDetallePorProyecto(Integer idProyecto){
		List<ProyectoPlaguicidasDetalle> lista = new ArrayList<ProyectoPlaguicidasDetalle>();
		try {

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("idProyecto", idProyecto);
			
			lista = (ArrayList<ProyectoPlaguicidasDetalle>) crudServiceBean
					.findByNamedQuery(ProyectoPlaguicidasDetalle.GET_POR_ID_PROYECTO, parameters);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<ProyectoPlaguicidasDetalle> getDetallePorProducto(Integer idProducto){
		List<ProyectoPlaguicidasDetalle> lista = new ArrayList<ProyectoPlaguicidasDetalle>();
		try {

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("idProducto", idProducto);
			
			lista = (ArrayList<ProyectoPlaguicidasDetalle>) crudServiceBean
					.findByNamedQuery(ProyectoPlaguicidasDetalle.GET_POR_ID_PRODUCTO, parameters);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

}
