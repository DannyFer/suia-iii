package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class InformacionProyectoEIACoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public InformacionProyectoEia guardar(InformacionProyectoEia informacionProyectoEIA) {        
    	return crudServiceBean.saveOrUpdate(informacionProyectoEIA);        
	}
	
	@SuppressWarnings("unchecked")
	public InformacionProyectoEia obtenerInformacionProyectoEIAPorProyecto(ProyectoLicenciaCoa proyecto )
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("proyectoId", proyecto.getId());
		
		List<InformacionProyectoEia> lista = (List<InformacionProyectoEia>) crudServiceBean
					.findByNamedQuery(
							InformacionProyectoEia.GET_INFORMACIONPROYECTOEIA_POR_PROYECTO,
							parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}

}