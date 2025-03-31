package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.DesechoAutogestion;
import ec.gob.ambiente.retce.model.DesechoExportacion;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.DisposicionFueraInstalacion;
import ec.gob.ambiente.retce.model.EliminacionFueraInstalacion;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.TransporteEmpresasGestoras;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.RegistroGeneradorDesechosAsociado;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class GeneradorDesechosPeligrososFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	@EJB
	private IdentificacionDesechosFacade identificacionDesechosFacade;
	
	@EJB
	private DesechoAutogestionFacade desechoAutogestionFacade;
	
	@EJB
	private TransporteFacade transporteFacade;
	
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	
	@EJB
	private ExportacionDesechosFacade exportacionDesechosFacade;
	
	@EJB
	private EliminacionFueraInstalacionFacade eliminacionFueraInstalacionFacade;
	
	@EJB
	private DisposicionFueraInstalacionFacade disposicionFueraInstalacionFacade;

	@SuppressWarnings("unchecked")
	public List<GeneradorDesechosPeligrosos> getByProyectoLicenciaAmbiental(String codProyecto) {
		List<GeneradorDesechosPeligrosos>  lista = new ArrayList<GeneradorDesechosPeligrosos>();
		try {
			lista = (ArrayList<GeneradorDesechosPeligrosos>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT g FROM GeneradorDesechosPeligrosos g WHERE g.proyecto.codigo = :codProyecto and g.estado = true ")
					.setParameter("codProyecto", codProyecto).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;	
	}
	
	@SuppressWarnings("unchecked")
	public GeneradorDesechosPeligrosos buscarRGDPorProyectoCoa(Integer codProyecto) {
		try {
			List<GeneradorDesechosPeligrosos> lista = (List<GeneradorDesechosPeligrosos>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT g FROM GeneradorDesechosPeligrosos g WHERE g.proyecto.id = :codProyecto and g.estado = true ")
					.setParameter("codProyecto", codProyecto).getResultList();
			if(lista != null && lista.size() > 0)
				return lista.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;	 
	}
	
	@SuppressWarnings("unchecked")
	public GeneradorDesechosPeligrosos buscarRGDFPorProyectoCoa(Integer codProyecto) {
		try {
			List<GeneradorDesechosPeligrosos> lista = (List<GeneradorDesechosPeligrosos>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT g FROM GeneradorDesechosPeligrosos g WHERE g.idPadreHistorial = :codProyecto and g.estado = true ")
					.setParameter("codProyecto", codProyecto).getResultList();
			if(lista != null && lista.size() > 0)
				return lista.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;	 
	}
	
	@SuppressWarnings("unchecked")
	public List<GeneradorDesechosPeligrosos> getByProyectoRcoa(String codigoProyecto) {
		List<GeneradorDesechosPeligrosos>  lista = new ArrayList<GeneradorDesechosPeligrosos>();
		try {
			lista = (ArrayList<GeneradorDesechosPeligrosos>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT g FROM GeneradorDesechosPeligrosos g, VinculoProyectoRgdSuia v, ProyectoLicenciaCoa p WHERE g.id = v.idRgdSuia and v.idProyectoRcoa = p.id and p.codigoUnicoAmbiental = :codProyecto and g.estado = true and p.estado = true ")
					.setParameter("codProyecto", codigoProyecto).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;	
	}
	
	@SuppressWarnings("unchecked")
	public GeneradorDesechosPeligrosos getGeneradorById(Integer idGenerador) {
		List<GeneradorDesechosPeligrosos>  lista = new ArrayList<GeneradorDesechosPeligrosos>();
		try {
			lista = (ArrayList<GeneradorDesechosPeligrosos>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT g FROM GeneradorDesechosPeligrosos g WHERE g.id = :idGenerador and g.estado = true ")
					.setParameter("idGenerador", idGenerador).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroGeneradorDesechosAsociado> getGeneradoresAsociadosLicenciaFisica(String resolucion) {
		List<RegistroGeneradorDesechosAsociado>  lista = new ArrayList<RegistroGeneradorDesechosAsociado>();
		try {
			lista = (ArrayList<RegistroGeneradorDesechosAsociado>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT g FROM RegistroGeneradorDesechosAsociado g WHERE g.codigoPermisoAmbiental = :resolucion and g.estado = true ")
					.setParameter("resolucion", resolucion).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;	
	}	
	
	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> getDesechosByGenerador(Integer idGenerador){
		List<DesechoPeligroso>  lista = new ArrayList<DesechoPeligroso>();
		try {
			lista = (ArrayList<DesechoPeligroso>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o.desechoPeligroso FROM GeneradorDesechosDesechoPeligroso o where o.estado = true and o.generadorDesechosPeligrosos.id = :idGenerador ")
					.setParameter("idGenerador", idGenerador).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;	
	}
	
	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> getDesechosByGeneradorRcoa(Integer idGenerador){
		List<DesechoPeligroso>  lista = new ArrayList<DesechoPeligroso>();
		try {
			lista = (ArrayList<DesechoPeligroso>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o.desechoPeligroso FROM DesechosRegistroGeneradorRcoa o where o.estado = true and o.generaDesecho = false and o.registroGeneradorDesechosRcoa.id = :idGenerador ")
					.setParameter("idGenerador", idGenerador).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;	
	}
	
	@SuppressWarnings("unchecked")
	public GeneradorDesechosPeligrososRetce getRgdRetce(Integer idRgd, Integer anio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgd", idRgd);
		parameters.put("anio", anio);
		try {
			List<GeneradorDesechosPeligrososRetce> lista = (List<GeneradorDesechosPeligrososRetce>) crudServiceBean.findByNamedQuery(GeneradorDesechosPeligrososRetce.GET_GENERADOR_POR_RGD_ANIO,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<GeneradorDesechosPeligrososRetce> getRgdRetceByInformacionProyecto(Integer idInformacionProyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyecto", idInformacionProyecto);

		try {
			List<GeneradorDesechosPeligrososRetce> lista = (List<GeneradorDesechosPeligrososRetce>) crudServiceBean.findByNamedQuery(GeneradorDesechosPeligrososRetce.GET_GENERADOR_POR_INFORMACION_PROYECTO,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public void guardarRgdRetce(GeneradorDesechosPeligrososRetce generadorRetce){
        crudServiceBean.saveOrUpdate(generadorRetce);
	}
	
	public String generarCodigoTramite() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-RETCE-GEN-"
					+ secuenciasFacade.getCurrentYear()
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence("MAAE-RETCE-GENERADOR", 4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public GeneradorDesechosPeligrososRetce getRgdRetcePorID(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);
		try {
			List<GeneradorDesechosPeligrososRetce> lista = (List<GeneradorDesechosPeligrososRetce>) crudServiceBean.findByNamedQuery(GeneradorDesechosPeligrososRetce.GET_GENERADOR_POR_ID,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public GeneradorDesechosPeligrososRetce getRgdRetcePorCodigo(String codigo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigo", codigo);
		try {
			List<GeneradorDesechosPeligrososRetce> lista = (List<GeneradorDesechosPeligrososRetce>) crudServiceBean.findByNamedQuery(GeneradorDesechosPeligrososRetce.GET_GENERADOR_POR_CODIGO,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public GeneradorDesechosPeligrososRetce getRgdRetceByCodigoRGD(String codigo) {
	    GeneradorDesechosPeligrososRetce resultado = null;
	    try {
	        List<GeneradorDesechosPeligrososRetce> lista = crudServiceBean
	                .getEntityManager()
	                .createQuery("SELECT g FROM GeneradorDesechosPeligrososRetce g WHERE g.estado = true AND g.codigoGeneradorDesechosPeligrosos = :codigo", GeneradorDesechosPeligrososRetce.class)
	                .setParameter("codigo", codigo)
	                .getResultList();

	        if (!lista.isEmpty()) {
	            resultado = lista.get(0);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return resultado;
	}
	
	public void eliminarGenerador(GeneradorDesechosPeligrososRetce generador) {
		identificacionDesechosFacade.eliminarIdentificacionDesechosPorRgdRetce(generador.getId());
		
		List<DesechoAutogestion> desechosAutogestion = desechoAutogestionFacade.getListaDesechosAutogestion(generador.getId());
		if(desechosAutogestion != null)
			desechoAutogestionFacade.eliminarDesechosAutogestion(desechosAutogestion);
		
		DetalleCatalogoGeneral tipoActividad = detalleCatalogoGeneralFacade.findByCodigo("tipoactividaddesecho.transporte");
		transporteFacade.eliminarTransporte(generador.getId(), tipoActividad.getId());
		
		List<DesechoExportacion> desechosExportacion = exportacionDesechosFacade.getDesechosExportacionPorRgdRetce(generador.getId());
		if(desechosExportacion != null)
			crudServiceBean.delete(desechosExportacion);
		
		tipoActividad = detalleCatalogoGeneralFacade.findByCodigo("tipoactividaddesecho.eliminacion");
		List<TransporteEmpresasGestoras> empresas = transporteFacade.getEmpresasGestorasPorRgdRetce(generador.getId(), tipoActividad.getId());
		if(empresas != null)
			transporteFacade.eliminarTransporteEmpresasGestoras(empresas);
		
		List<EliminacionFueraInstalacion> eliminacion = eliminacionFueraInstalacionFacade.getEliminacionPorRgdRetce(generador.getId());
		if(eliminacion != null)
			eliminacionFueraInstalacionFacade.eliminarEliminacionFueraInstalacion(eliminacion);
		
		tipoActividad = detalleCatalogoGeneralFacade.findByCodigo("tipoactividaddesecho.disposicion");
		List<TransporteEmpresasGestoras> empresasDisposicion = transporteFacade.getEmpresasGestorasPorRgdRetce(generador.getId(), tipoActividad.getId());
		if(empresasDisposicion != null)
			transporteFacade.eliminarTransporteEmpresasGestoras(empresasDisposicion);
		
		List<DisposicionFueraInstalacion> disposicion = disposicionFueraInstalacionFacade.getDisposicionPorRgdRetce(generador.getId());
		if(disposicion != null)
			disposicionFueraInstalacionFacade.eliminarListaDisposicion(disposicion);
		
		generador.setEstado(false);
		crudServiceBean.saveOrUpdate(generador);
	}
	
	@SuppressWarnings("unchecked")
	public GeneradorDesechosPeligrososRetce getRgdRetceHistorialPorID(Integer idRgdRetce, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);
		parameters.put("nroObservacion", nroObservacion);
		try {
			List<GeneradorDesechosPeligrososRetce> lista = (List<GeneradorDesechosPeligrososRetce>) crudServiceBean.findByNamedQuery(GeneradorDesechosPeligrososRetce.GET_HISTORIAL_GENERADOR,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<GeneradorDesechosPeligrososRetce> getRgdRetceHistorialPorID(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);
		try {
			List<GeneradorDesechosPeligrososRetce> lista = (List<GeneradorDesechosPeligrososRetce>) crudServiceBean.findByNamedQuery(GeneradorDesechosPeligrososRetce.GET_HISTORIAL_GENERADOR_POR_ID,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public GeneradorDesechosPeligrosos GeneradorActivosNoVinculado(String idDocumento)
	{
		try 
		{
			GeneradorDesechosPeligrosos obj = new GeneradorDesechosPeligrosos();
			StringBuilder query = new StringBuilder();
			query.append("select d.* ");
			query.append("from suia_iii.hazardous_wastes_generators d ");
			query.append("where hwge_status = true ");
			query.append("and hwge_code = '" + idDocumento + "' ");
			//query.append(" and hwge_finalized = TRUE ");
			query.append("and pren_id is NULL ");
			query.append("and hwge_physical = true ");
			query.append("and (hwge_linkage is null or hwge_linkage is false) ");
			query.append("group by 1, 2, 3  ");
			query.append("order by 1, 2 desc  ");
			@SuppressWarnings("unchecked")
			List<GeneradorDesechosPeligrosos> lista = (List<GeneradorDesechosPeligrosos>) crudServiceBean.findNativeQuery(query.toString(), GeneradorDesechosPeligrosos.class);
			if ((lista != null) && (lista.size() > 0)) {
				obj = lista.get(0);
			}
			else
			{
				obj = null;
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public GeneradorDesechosPeligrosos GeneradorActivosVinculado(Integer idDocumento)
	{
		try 
		{
			GeneradorDesechosPeligrosos obj = new GeneradorDesechosPeligrosos();
			StringBuilder query = new StringBuilder();
			query.append("select d.* ");
			query.append("from suia_iii.hazardous_wastes_generators d ");
			query.append("where hwge_status = true ");
			query.append("and hwge_id = '" + idDocumento + "' ");
			//query.append("and pren_id is NULL ");
			//query.append("and hwge_physical = true ");
			//query.append("and hwge_linkage is true ");
			@SuppressWarnings("unchecked")
			List<GeneradorDesechosPeligrosos> lista = (List<GeneradorDesechosPeligrosos>) crudServiceBean.findNativeQuery(query.toString(), GeneradorDesechosPeligrosos.class);
			if ((lista != null) && (lista.size() > 0)) {
				obj = lista.get(0);
			}
			else
			{
				obj = null;
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	public GeneradorDesechosPeligrosos getGeneradorDesechos(String idDocumento)
	{
		try 
		{
			GeneradorDesechosPeligrosos obj = new GeneradorDesechosPeligrosos();
			StringBuilder query = new StringBuilder();
			query.append("select d.* ");
			query.append("from suia_iii.hazardous_wastes_generators d ");
			query.append("where hwge_status = true ");
			query.append("and hwge_code = '" + idDocumento + "' ");
			@SuppressWarnings("unchecked")
			List<GeneradorDesechosPeligrosos> lista = (List<GeneradorDesechosPeligrosos>) crudServiceBean.findNativeQuery(query.toString(), GeneradorDesechosPeligrosos.class);
			if ((lista != null) && (lista.size() > 0)) {
				obj = lista.get(0);
			}
			else
			{
				obj = null;
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}		
}
