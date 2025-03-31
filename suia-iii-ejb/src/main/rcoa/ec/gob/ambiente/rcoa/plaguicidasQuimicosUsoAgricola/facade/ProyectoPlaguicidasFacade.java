package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidas;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class ProyectoPlaguicidasFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private SecuenciasFacade secuenciasFacade;

	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();

	private String generarCodigo() {
		try {
			String nombreSecuencia = "MAATE-PQUA-SGA-"+secuenciasFacade.getCurrentYear();
			return Constantes.SIGLAS_INSTITUCION + "-PQUA-SGA-"+secuenciasFacade.getCurrentYear() 			
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia,4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ProyectoPlaguicidas guardar(ProyectoPlaguicidas proyecto){
		if(proyecto.getCodigoProyecto() == null) {
			proyecto.setCodigoProyecto(generarCodigo());
		}

		return crudServiceBean.saveOrUpdate(proyecto);
	}

	@SuppressWarnings("unchecked")
	public ProyectoPlaguicidas getPorCodigoProyecto(String codigoProyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigoProyecto", codigoProyecto);

		try {
			List<ProyectoPlaguicidas> lista = (List<ProyectoPlaguicidas>) crudServiceBean
					.findByNamedQuery(ProyectoPlaguicidas.GET_POR_CODIGO, parameters);

			if (lista == null || lista.isEmpty()) {
				return new ProyectoPlaguicidas();
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return new ProyectoPlaguicidas();
		}
	}

	@SuppressWarnings("unchecked")
	public ProyectoPlaguicidas getPorProducto(Integer idProducto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProducto", idProducto);

		try {
			List<ProyectoPlaguicidas> lista = (List<ProyectoPlaguicidas>) crudServiceBean
					.findByNamedQuery(ProyectoPlaguicidas.GET_POR_ID_PRODUCTO, parameters);

			if (lista == null || lista.isEmpty()) {
				return new ProyectoPlaguicidas();
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return new ProyectoPlaguicidas();
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProyectoPlaguicidas> getTramitesIngresados() {
		try {
			List<ProyectoPlaguicidas> lista = (List<ProyectoPlaguicidas>) crudServiceBean.findAll(ProyectoPlaguicidas.class);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Boolean validarInicioTramite(String codigoProyecto) {

		String sqltaskbpmhyd = "select * from dblink('" + dblinkBpmsSuiaiii + "',"
				+ "'select processinstanceid from variableinstancelog where value=''" + codigoProyecto+ "'' "
				+ " and variableid = ''tramite'' "
				+ " and processid = ''rcoa.ActualizacionEtiquetadoPlaguicidasUsoAgricola'' ') as (id integer)";
		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);

		List<Object> resultPro = new ArrayList<Object>();
		resultPro = queryProceso.getResultList();
		if (resultPro != null && resultPro.size() > 0) {
			return false;
		}

		return true;
	}

}
