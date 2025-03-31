/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import ec.gob.ambiente.suia.administracion.service.FormasContactoService;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * 
 * @author ishmael
 */
@Stateless
public class FormasContactoFacade {

	@EJB
	private FormasContactoService formasContactoService;

	public List<FormasContacto> buscarPorEstado(boolean estado)
			throws ServiceException {
		return formasContactoService.buscarPorEstado(estado);
	}

	public FormasContacto buscarPorNombre(String nombre) {
		return formasContactoService.buscarPorNombre(nombre);
	}
	public FormasContacto buscarPorId(Integer id) {
		return formasContactoService.buscarPorId(id);
	}
}
