package ec.gob.ambiente.suia.tipocatalogo.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.tipocatalogo.service.TipoCatalogoService;

@Stateless
public class TipoCatalogoFacade {
	@EJB
	private TipoCatalogoService tipoCatalogoService;
	
	public List<TipoCatalogo> obtenerTipoCatalogoXCodigo(String codigo) throws Exception{
		return tipoCatalogoService.obtenerTipoCatalogoXCodigo(codigo);
	}

}
