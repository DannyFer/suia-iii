package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.DesechoGestorAlmacenamientoExportacion;
import ec.gob.ambiente.retce.model.DesechoGestorTransporte;
import ec.gob.ambiente.retce.model.ManifiestoUnicoTransferencia;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class DesechoGestorAlmacenamientoExportacionFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public DesechoGestorAlmacenamientoExportacion getById(Integer idDesechoPeligrosoGestorAlmacenamiento) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", idDesechoPeligrosoGestorAlmacenamiento);
		return (DesechoGestorAlmacenamientoExportacion) crudServiceBean.findByNamedQuery(DesechoGestorAlmacenamientoExportacion.FIND_BY_ID, parameters).get(0);
	}
	
	@SuppressWarnings("unchecked")
	public DesechoGestorAlmacenamientoExportacion getByDesechoGestorAlmacenamiento(Integer idDesechoGestorAlmacenamiento) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesechoGestorAlmacenamiento", idDesechoGestorAlmacenamiento);
		try {
			DesechoGestorAlmacenamientoExportacion lista = (DesechoGestorAlmacenamientoExportacion) crudServiceBean.findByNamedQuery(DesechoGestorAlmacenamientoExportacion.GET_BY_GESTOR_ALMACENAMIENTO,parameters).get(0);
			return lista;
		} catch (Exception e) {
			return null;
		}
	}
	
	public DesechoGestorAlmacenamientoExportacion guardar(DesechoGestorAlmacenamientoExportacion desechoGestorAlmacenamientoExportacion){
		desechoGestorAlmacenamientoExportacion = crudServiceBean.saveOrUpdate(desechoGestorAlmacenamientoExportacion);
		return desechoGestorAlmacenamientoExportacion;
	}

}
