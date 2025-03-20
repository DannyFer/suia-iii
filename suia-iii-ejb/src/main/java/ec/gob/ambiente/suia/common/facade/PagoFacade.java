package ec.gob.ambiente.suia.common.facade;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.common.service.PagoService;
import ec.gob.ambiente.suia.domain.Pago;

@Stateless
public class PagoFacade implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3013846288778967659L;
	
	@EJB
	private PagoService pagoService;

	public void guardarActualizar(Pago pago){
		pagoService.guardarActualizarPago(pago);
	}
	
	public Pago consultar(Integer id){
		return pagoService.consultarPago(id);
	}
}
