package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.AclaracionProrrogaEsIA;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class AclaracionProrrogaEsIAFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
		
	public AclaracionProrrogaEsIA guardar(AclaracionProrrogaEsIA aclaracion) {
		return crudServiceBean.saveOrUpdate(aclaracion);
	}
	
	@SuppressWarnings("unchecked")
	public AclaracionProrrogaEsIA getPorEstudio(Integer idEstudio)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", idEstudio);
		
		List<AclaracionProrrogaEsIA> lista = (List<AclaracionProrrogaEsIA>) crudServiceBean
					.findByNamedQuery(
							AclaracionProrrogaEsIA.GET_POR_ESTUDIO,
							parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}

}