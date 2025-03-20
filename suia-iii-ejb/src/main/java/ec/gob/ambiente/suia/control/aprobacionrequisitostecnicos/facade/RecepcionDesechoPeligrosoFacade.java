/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.ManifiestoUnico;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.RecepcionDesechoPeligrosoService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoEstadoFisico;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AlmacenRecepcion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionRecepcion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RecepcionDesechoPeligroso;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <b> Clase facade de la pagina recepción del desecho peligroso </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 09/06/2015 $]
 *          </p>
 */
@Stateless
public class RecepcionDesechoPeligrosoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private RecepcionDesechoPeligrosoService recepcionDesechoPeligrosoService;

	@EJB
	private CatalogoGeneralFacade catalogoGeneralFacade;

	@EJB
	private AlmacenFacade almacenFacade;

	@EJB
	private EliminacionDesechoFacade eliminacionDesechoFacade;

	/**
	 * 
	 * <b> Guarda una lista la lista de desechos peligroso receptados. </b>
	 * <p>
	 * [Author: vero, Date: 09/06/2015]
	 * </p>
	 * 
	 * @param requisitosVehiculo
	 * @return
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<RecepcionDesechoPeligroso> guardar(List<RecepcionDesechoPeligroso> listaRecepcionDesechoPeligroso) {
		return (List<RecepcionDesechoPeligroso>) crudServiceBean.saveOrUpdate(listaRecepcionDesechoPeligroso);
	}

	public void eliminar(List<RecepcionDesechoPeligroso> recepcionDesechoPeligroso,
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		try {
			for (RecepcionDesechoPeligroso recepcionDesechoPeligroso1 : recepcionDesechoPeligroso) {
				almacenFacade.eliminarAlmacenRecepcionPorIdRecepcion(recepcionDesechoPeligroso1.getId());
				eliminacionDesechoFacade.eliminarRecepcionPorIdRecepcion(recepcionDesechoPeligroso1.getId(),
						aprobacionRequisitosTecnicos);
			}
			crudServiceBean.delete(recepcionDesechoPeligroso);
		} catch (ServiceException ex) {
			Logger.getLogger(RecepcionDesechoPeligrosoFacade.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public boolean validarPaginasRecepcion(List<RecepcionDesechoPeligroso> recepcionDesechoPeligroso)
			throws ServiceException {
		boolean existe = false;
		for (RecepcionDesechoPeligroso recepcionDesechoPeligroso1 : recepcionDesechoPeligroso) {
			List<AlmacenRecepcion> almacenRecepcion = almacenFacade.listaAlmacenRecepcion(recepcionDesechoPeligroso1
					.getId());
			List<EliminacionRecepcion> eliminacionRecepcion = eliminacionDesechoFacade
					.listaEliminacionRecepcion(recepcionDesechoPeligroso1.getId());
			if (almacenRecepcion != null || !almacenRecepcion.isEmpty() || eliminacionRecepcion != null
					|| !eliminacionRecepcion.isEmpty()) {
				existe = true;
				break;
			}
		}
		return existe;

	}

	public List<RecepcionDesechoPeligroso> getListaRecepcionDesechoPeligroso(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		return recepcionDesechoPeligrosoService.getListaRecepcionDesechosPeligrosos(aprobacionRequisitosTecnicos);
	}

	@SuppressWarnings("unchecked")
	public List<TipoEstadoFisico> getEstadosFisicos() {
		return (List<TipoEstadoFisico>) crudServiceBean.findAll(TipoEstadoFisico.class);
	}

	@SuppressWarnings("unchecked")
	public RecepcionDesechoPeligroso getById(Integer idRecepcionDesechoPeligroso) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", idRecepcionDesechoPeligroso);
		return (RecepcionDesechoPeligroso) crudServiceBean.findByNamedQuery(RecepcionDesechoPeligroso.FIND_BY_ID, parameters).get(0);
	}
	
}
