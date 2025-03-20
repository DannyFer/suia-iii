package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DocumentoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.EfectoOrganismosAcuaticos;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;

@Stateless
public class EfectoOrganismosAcuaticosFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private DocumentoPquaFacade documentosFacade;

	public EfectoOrganismosAcuaticos guardar(EfectoOrganismosAcuaticos plaga) {
		return crudServiceBean.saveOrUpdate(plaga);
	}

	@SuppressWarnings("unchecked")
	public List<EfectoOrganismosAcuaticos> getPorProyectoTipo(Integer idProyecto, Integer tipoOrganismo){
		List<EfectoOrganismosAcuaticos> lista = new ArrayList<EfectoOrganismosAcuaticos>();
		try {

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("idProyecto", idProyecto);
			parameters.put("idTipoOrganismo", tipoOrganismo);
			
			lista = (ArrayList<EfectoOrganismosAcuaticos>) crudServiceBean
					.findByNamedQuery(EfectoOrganismosAcuaticos.GET_POR_PROYECTO_TIPO, parameters);
			
			if(lista != null && lista.size() > 0) {
				for(EfectoOrganismosAcuaticos item : lista) {
					List<DocumentoPqua> documentos = documentosFacade.documentoPorTablaIdPorIdDoc(item.getId(), 
						TipoDocumentoSistema.PQUA_RESPALDO_EFECTOS_ACUATICOS, 
						"RespaldoEfectosOrganismosAcuaticos");
					if(documentos != null && documentos.size() > 0) {
						item.setRespaldo(documentos.get(0));
					}
				}
			}
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<EfectoOrganismosAcuaticos> getPorProyecto(Integer idProyecto){
		List<EfectoOrganismosAcuaticos> lista = new ArrayList<EfectoOrganismosAcuaticos>();
		try {

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("idProyecto", idProyecto);
			
			lista = (ArrayList<EfectoOrganismosAcuaticos>) crudServiceBean
					.findByNamedQuery(EfectoOrganismosAcuaticos.GET_POR_PROYECTO, parameters);
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public EfectoOrganismosAcuaticos getItemMayorCategoria(Integer idProyecto){
		List<EfectoOrganismosAcuaticos> lista = new ArrayList<EfectoOrganismosAcuaticos>();
		try {

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("idProyecto", idProyecto);
			
			lista = (ArrayList<EfectoOrganismosAcuaticos>) crudServiceBean
					.findByNamedQuery(EfectoOrganismosAcuaticos.GET_POR_PROYECTO_ORDER_CATEGORIA, parameters);
			
			return lista.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
