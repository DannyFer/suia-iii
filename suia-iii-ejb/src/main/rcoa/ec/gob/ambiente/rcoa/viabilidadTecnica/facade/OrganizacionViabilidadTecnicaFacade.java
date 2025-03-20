package ec.gob.ambiente.rcoa.viabilidadTecnica.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.OrganizacionViabilidadTecnica;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Organizacion;

@Stateless
public class OrganizacionViabilidadTecnicaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	@SuppressWarnings("unchecked")
	public OrganizacionViabilidadTecnica getOrganizacionPorTipoProceso(Organizacion organizacion, String tipoProceso)
	{
		CatalogoGeneralCoa catalogo = catalogoCoaFacade.obtenerCatalogoPorCodigo(tipoProceso);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("organizacion", organizacion);
		parameters.put("tipoActividad", catalogo);
		
		List<OrganizacionViabilidadTecnica> lista = (List<OrganizacionViabilidadTecnica>) crudServiceBean
					.findByNamedQuery(OrganizacionViabilidadTecnica.GET_POR_ORGANIZACION_ACTIVIDAD, parameters);
		if(lista != null && lista.size() > 0 ){
			return lista.get(0);
		}
		
		return  null;
	}
	
}
