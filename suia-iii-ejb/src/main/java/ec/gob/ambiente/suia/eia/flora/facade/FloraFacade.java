/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.flora.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.FloraGeneral;
import ec.gob.ambiente.suia.domain.PuntosMuestreoFlora;
import ec.gob.ambiente.suia.eia.flora.service.FloraService;

/**
 *
 * @author 
 */


@Stateless
public class FloraFacade {

	@EJB
	FloraService floraService;
	
	public Integer guardarFlora(FloraGeneral flora, List<List<Object>> eliminados) throws Exception{
		try {
			return floraService.guardarFlora(flora, eliminados);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public FloraGeneral obtenerFlora(Integer id) throws Exception{
		try {
			return floraService.obtenerFlora(id);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public List<PuntosMuestreoFlora> obtenerPuntosMuestreo(Integer floraId) throws Exception{
		try {
			return floraService.obtenerPuntosMuestreo(floraId);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

}
