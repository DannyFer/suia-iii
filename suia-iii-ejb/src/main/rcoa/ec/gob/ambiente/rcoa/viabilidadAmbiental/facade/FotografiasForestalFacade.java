package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.FotografiaInformeForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class FotografiasForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	public void guardarFotografia(FotografiaInformeForestal foto) {			
		crudServiceBean.saveOrUpdate(foto);
	}

	@SuppressWarnings("unchecked")
	public List<FotografiaInformeForestal> getListaFotografiasPorInformeTipo(Integer idInforme, Integer tipo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInforme", idInforme);
		parameters.put("tipo", tipo);

		try {
			List<FotografiaInformeForestal> lista = (List<FotografiaInformeForestal>) crudServiceBean
					.findByNamedQuery(FotografiaInformeForestal.GET_POR_INFORME_TIPO, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				
				for (FotografiaInformeForestal foto : lista) {
					byte[] documentoContent = null;
					documentoContent = documentosFacade.descargar(foto.getDocImagen().getIdAlfresco(), foto.getDocImagen().getFechaCreacion());
					
					foto.getDocImagen().setContenidoDocumento(documentoContent);
					foto.setNombre(foto.getDocImagen().getNombre());
				}
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
}
