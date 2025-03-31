package ec.gob.ambiente.suia.certificadointerseccion.service;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CertificadoInterseccion;
import ec.gob.ambiente.suia.domain.DetalleInterseccionProyecto;
import ec.gob.ambiente.suia.domain.InterseccionProyecto;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class CertificadoInterseccionService {

	@EJB
	private CrudServiceBean crudServiceBean;

	public boolean verificarCreacionMapa(String codigoProyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigoProyecto", codigoProyecto);
		List<Object> aa = crudServiceBean.findByNativeQuery(
				"SELECT count(proyecto) FROM mapaproyecto where proyecto=:codigoProyecto", parameters);

		int a = ((BigInteger) aa.get(0)).intValue();
		return (a > 0) ? true : false;
	}

	@SuppressWarnings("unchecked")
	public List<DetalleInterseccionProyecto> getDetallesInterseccion(Integer idInterseccionProyecto) {
		List<DetalleInterseccionProyecto> detalles = new ArrayList<DetalleInterseccionProyecto>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInterseccionProyecto", idInterseccionProyecto);
		detalles = (List<DetalleInterseccionProyecto>) crudServiceBean.findByNamedQuery(
				"DetalleInterseccionProyecto.findByInterseccionProject", parameters);
		return detalles;
	}

	@SuppressWarnings("unchecked")
	public boolean isProyectoIntersecaCapas(String codigoProyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigoProyecto", codigoProyecto);
		List<InterseccionProyecto> intersecciones = null;
		intersecciones = (List<InterseccionProyecto>) crudServiceBean.findByNamedQuery(
				"InterseccionProyecto.findByCodeProject", parameters);
		return (!intersecciones.isEmpty() && intersecciones.size() > 0) ? true : false;
	}

	@SuppressWarnings("unchecked")
	public List<InterseccionProyecto> getListaInterseccionProyectoIntersecaCapas(String codigoProyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigoProyecto", codigoProyecto);
		List<InterseccionProyecto> intersecciones = null;
		intersecciones = (List<InterseccionProyecto>) crudServiceBean.findByNamedQuery(
				"InterseccionProyecto.findByCodeProject", parameters);
		return intersecciones;
	}

	@SuppressWarnings("unchecked")
	public String getComentarioInterseccionAmortiguamiento(String codigoProyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigoProyecto", codigoProyecto);
		List<InterseccionProyecto> intersecciones = null;
		intersecciones = (List<InterseccionProyecto>) crudServiceBean.findByNamedQuery(
				"InterseccionProyecto.findByCodeProject", parameters);
		String pronunciamiento = "";
		for (InterseccionProyecto interseccionProyecto : intersecciones) {
			if (interseccionProyecto.getIntersectaConCapaAmortiguamiento()) {
				pronunciamiento = pronunciamiento + " " + interseccionProyecto.getCapa().getNombre() + ",";
			}
		}

		return pronunciamiento;
	}

	/**
	 * Método que retorna si interseco con SNAP, BP y PFE
	 * 
	 * @param codigoProyecto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getCapasInterseccion(String codigoProyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigoProyecto", codigoProyecto);
		List<InterseccionProyecto> intersecciones = null;
		String nombreCapas = "";
		intersecciones = (List<InterseccionProyecto>) crudServiceBean.findByNamedQuery(
				"InterseccionProyecto.findByCodeProject", parameters);

		nombreCapas = getValorInterseccion(intersecciones, Constantes.ID_CAPA_SNAP) + ","
				+ getValorInterseccion(intersecciones, Constantes.ID_CAPA_BP) + ","
				+ getValorInterseccion(intersecciones, Constantes.ID_CAPA_PFE)+ ","
				+ getValorInterseccion(intersecciones, Constantes.ID_CAPA_RAMSAR_PUNTO)+ ","
				+ getValorInterseccion(intersecciones, Constantes.ID_CAPA_RAMSAR_AREA);
				
		nombreCapas = nombreCapas + "," + getValorInterseccionCapasAmortiguamiento(intersecciones);
		return nombreCapas;
	}

	public boolean getValorInterseccionBoolean(List<InterseccionProyecto> intersecciones, Integer idCapa) {
		for (InterseccionProyecto interseccionProyecto : intersecciones) {
			if (interseccionProyecto.getCapa().getId() == idCapa) {
				return true;
			}
		}
		return false;
	}

	private String getValorInterseccion(List<InterseccionProyecto> intersecciones, Integer idCapa) {
		for (InterseccionProyecto interseccionProyecto : intersecciones) {
			if (interseccionProyecto.getCapa().getId() == idCapa) {
				return "SI";
			}
		}
		return "NO";
	}

	private String getValorInterseccionCapasAmortiguamiento(List<InterseccionProyecto> intersecciones) {
		for (InterseccionProyecto interseccionProyecto : intersecciones) {
			if (interseccionProyecto.getIntersectaConCapaAmortiguamiento()) {
				return "SI";
			}
		}
		return "NO";
	}

	public Boolean isGeneradoPDFCertificadoInterseccion(String codigoProyecto) throws ServiceException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigoProy", codigoProyecto);
		String query = "SELECT count(*) FROM mapaproyecto where proyecto=:codigoProy";
		List<Object> result = crudServiceBean.findByNativeQuery(query, parameters);
		boolean pdfGenerado = ((BigInteger) result.get(0)).intValue() == 0 ? false : true;
		File file = new File(Constantes.getPathPdfCertificadoInterseccion() + codigoProyecto + ".pdf");
		if (pdfGenerado && file.exists()) {
			return true;
		} else {
			throw new ServiceException("El Certificado de intersección aún no ha sido generado, intente nuevamente");
		}
	}

	public File getFilePDFMapaCertificadoInterseccion(String codigoProyecto) {
		return new File(Constantes.getPathPdfCertificadoInterseccion() + codigoProyecto + ".pdf");

	}

	@SuppressWarnings("unchecked")
	public CertificadoInterseccion recuperarCertificadoInterseccion(ProyectoLicenciamientoAmbiental proyecto) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("proyecto", proyecto);
		List<CertificadoInterseccion> lista = (List<CertificadoInterseccion>) crudServiceBean.findByNamedQuery(
				CertificadoInterseccion.GET_CERTIFICADO_INTERSECCION, parametros);
		if (!lista.isEmpty()) {
			return lista.get(0);
		} else {
			return null;
		}

	}
	
	@SuppressWarnings("unchecked")
	public CertificadoInterseccion getCertificadoInterseccion(Integer idProyecto) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idProyecto", idProyecto);
		List<CertificadoInterseccion> lista = (List<CertificadoInterseccion>) crudServiceBean.findByNamedQuery(
				CertificadoInterseccion.GET_CERTIFICADO_INTERSECCION_ID_PROYECTO, parametros);
		if (!lista.isEmpty()) {
			return lista.get(0);
		} else {
			return null;
		}

	}
	
	@SuppressWarnings("unchecked")
	public CertificadoInterseccion getCertificadoInterseccionActualizacion(Integer idProyecto, Integer nroActualizacion) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idProyecto", idProyecto);
		parametros.put("nroActualizacion", nroActualizacion);
		List<CertificadoInterseccion> lista = (List<CertificadoInterseccion>) crudServiceBean.findByNamedQuery(
				CertificadoInterseccion.GET_CERTIFICADO_INTERSECCION_ACTUALIZACION, parametros);
		if (!lista.isEmpty()) {
			return lista.get(0);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public CertificadoInterseccion getCertificadoInterseccionActualizacionSuiaVerde(String idProyecto, Integer nroActualizacion) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idProyecto", idProyecto);
		parametros.put("nroActualizacion", nroActualizacion);
		List<CertificadoInterseccion> lista = (List<CertificadoInterseccion>) crudServiceBean.findByNamedQuery(
				CertificadoInterseccion.GET_CI_ACTUALIZACION_SUIA_VERDE, parametros);
		if (!lista.isEmpty()) {
			return lista.get(0);
		} else {
			return null;
		}

	}
	
	@SuppressWarnings("unchecked")
	public CertificadoInterseccion getCertificadoInterseccionCodigo(String codigo) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("codigo", codigo);
		List<CertificadoInterseccion> lista = (List<CertificadoInterseccion>) crudServiceBean.findByNamedQuery(
				CertificadoInterseccion.GET_CERTIFICADO_INTERSECCION_CODIGO, parametros);
		if (!lista.isEmpty()) {
			return lista.get(0);
		} else {
			return null;
		}

	}
	
	@SuppressWarnings("unchecked")
	public boolean isProyectoIntersecaCapasActualizacion(String codigoProyecto, Integer nroActualizacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigoProyecto", codigoProyecto);
		parameters.put("nroActualizacion", nroActualizacion);
		List<InterseccionProyecto> intersecciones = null;
		intersecciones = (List<InterseccionProyecto>) crudServiceBean.findByNamedQuery(
				"InterseccionProyecto.findByCodeProjectUpdated", parameters);
		Boolean interseca = false;
		if(!intersecciones.isEmpty() && intersecciones.size() > 0) {
			for(InterseccionProyecto capaInterseccion : intersecciones){
				if(capaInterseccion.getCapaCoa() != null && (capaInterseccion.getCapaCoa().getId().equals(Constantes.ID_CAPA_SNAP) 
						|| capaInterseccion.getCapaCoa().getId().equals(Constantes.ID_CAPA_BP) 
						|| capaInterseccion.getCapaCoa().getId().equals(Constantes.ID_CAPA_PFE))) {
					interseca = true;
					break;
				}
			}
		}
		return interseca;
	}
	
	@SuppressWarnings("unchecked")
	public List<InterseccionProyecto> getListaInterseccionProyectoIntersecaCapasActualizacion(String codigoProyecto, Integer nroActualizacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigoProyecto", codigoProyecto);
		parameters.put("nroActualizacion", nroActualizacion);
		List<InterseccionProyecto> intersecciones = null;
		intersecciones = (List<InterseccionProyecto>) crudServiceBean.findByNamedQuery(
				"InterseccionProyecto.findByCodeProjectUpdated", parameters);
		
		List<InterseccionProyecto> interseccionesProy = new ArrayList<>();
		if(!intersecciones.isEmpty() && intersecciones.size() > 0) {
			for(InterseccionProyecto capaInterseccion : intersecciones){
				if(capaInterseccion.getCapaCoa() != null && (capaInterseccion.getCapaCoa().getId().equals(Constantes.ID_CAPA_SNAP) 
						|| capaInterseccion.getCapaCoa().getId().equals(Constantes.ID_CAPA_BP) 
						|| capaInterseccion.getCapaCoa().getId().equals(Constantes.ID_CAPA_PFE)))
					interseccionesProy.add(capaInterseccion);
			}
		}
		return interseccionesProy;
	}
}
