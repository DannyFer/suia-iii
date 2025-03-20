package ec.gob.ambiente.suia.common.service;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Pago;

@Stateless
public class PagoService implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -772645761079438681L;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	/**
	 * Metodo para guardar un pago
	 * @param pago
	 */
	public void guardarActualizarPago(Pago pago){
		crudServiceBean.saveOrUpdate(pago);
	}
	
	/**
	 * Metodo para consultar pago
	 * @param id
	 * @return
	 */
	public Pago consultarPago(Integer id){
		return crudServiceBean.find(Pago.class, id);
	}

}
