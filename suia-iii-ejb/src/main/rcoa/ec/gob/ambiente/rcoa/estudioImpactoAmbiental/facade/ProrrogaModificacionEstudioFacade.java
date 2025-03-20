package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ProrrogaModificacionEstudio;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ProrrogaModificacionEstudioFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
		
	public ProrrogaModificacionEstudio guardar(ProrrogaModificacionEstudio aclaracion) {
		return crudServiceBean.saveOrUpdate(aclaracion);
	}
	
	@SuppressWarnings("unchecked")
	public ProrrogaModificacionEstudio getPorEstudio(Integer idEstudio)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", idEstudio);
		
		List<ProrrogaModificacionEstudio> lista = (List<ProrrogaModificacionEstudio>) crudServiceBean
					.findByNamedQuery(
							ProrrogaModificacionEstudio.GET_POR_ESTUDIO,
							parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}

	@SuppressWarnings("unchecked")
	public ProrrogaModificacionEstudio getPorEstudioRevision(Integer idEstudio, Integer nroRevision)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", idEstudio);
		parameters.put("nroRevision", nroRevision);
		
		List<ProrrogaModificacionEstudio> lista = (List<ProrrogaModificacionEstudio>) crudServiceBean
					.findByNamedQuery(
							ProrrogaModificacionEstudio.GET_POR_ESTUDIO_REVISION,
							parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}

}