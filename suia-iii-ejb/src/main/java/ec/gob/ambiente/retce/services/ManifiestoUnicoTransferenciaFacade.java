package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.ManifiestoUnicoTransferencia;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class ManifiestoUnicoTransferenciaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<ManifiestoUnicoTransferencia> getByManifiestoUnico(Integer idManifiestoUnico) throws ServiceException {
		List<ManifiestoUnicoTransferencia> lista = new ArrayList<ManifiestoUnicoTransferencia>(); 
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idManifiestoUnico", idManifiestoUnico);
		try {
			lista = (List<ManifiestoUnicoTransferencia>) crudServiceBean.findByNamedQuery(ManifiestoUnicoTransferencia.GET_BY_MANIFIESTO,parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public ManifiestoUnicoTransferencia getById(Integer idManifiestoUnicoTransferencia) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idManifiestoUnicoTransferencia", idManifiestoUnicoTransferencia);
		return (ManifiestoUnicoTransferencia) crudServiceBean.findByNamedQuery(ManifiestoUnicoTransferencia.FIND_BY_ID, parameters).get(0);
	}
	
	public ManifiestoUnicoTransferencia guardar(ManifiestoUnicoTransferencia manifiestoUnicoTransferencia){
		manifiestoUnicoTransferencia = crudServiceBean.saveOrUpdate(manifiestoUnicoTransferencia);
        return manifiestoUnicoTransferencia;
	}

}
