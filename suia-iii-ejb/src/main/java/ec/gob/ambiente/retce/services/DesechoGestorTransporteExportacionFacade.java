package ec.gob.ambiente.retce.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.DesechoGestorAlmacenamientoExportacion;
import ec.gob.ambiente.retce.model.DesechoGestorTransporteExportacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class DesechoGestorTransporteExportacionFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public DesechoGestorTransporteExportacion getById(Integer idDesechoPeligrosoGestorAlmacenamiento) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesechoPeligrosoGestorAlmacenamiento", idDesechoPeligrosoGestorAlmacenamiento);
		return (DesechoGestorTransporteExportacion) crudServiceBean.findByNamedQuery(DesechoGestorTransporteExportacion.FIND_BY_ID, parameters).get(0);
	}
	
	@SuppressWarnings("unchecked")
	public DesechoGestorTransporteExportacion getByDesechoGestorTransporte(Integer idDesechoGestorTransporte) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesechoGestorTransporte", idDesechoGestorTransporte);
		try {
			DesechoGestorTransporteExportacion lista = (DesechoGestorTransporteExportacion) crudServiceBean.findByNamedQuery(DesechoGestorTransporteExportacion.GET_BY_DESECHO_GESTOR_TRANSPORTE,parameters).get(0);
			return lista;
		} catch (Exception e) {
			return null;
		}	
	}
	
	public DesechoGestorTransporteExportacion guardar(DesechoGestorTransporteExportacion desechoGestorExportacion){
		desechoGestorExportacion = crudServiceBean.saveOrUpdate(desechoGestorExportacion);
		return desechoGestorExportacion;
	}

}
