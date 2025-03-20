package ec.gob.ambiente.rcoa.certificado.interseccion.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.utils.Constantes;


@Stateless
public class CertificadoInterseccionCoaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	@EJB
	private AreaFacade areaFacade;
	
	public PlantillaReporte obtenerPlantillaReporte(Integer tipoDocumentoId) {
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("p_tipoDocumentoId", tipoDocumentoId);

			List<PlantillaReporte> lista = this.crudServiceBean.findByNamedQueryGeneric(
					PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (PlantillaReporte) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	private String generarCodigo(String siglasArea) {
		try {
			String nombreSecuencia = "MAAE-SUIA-RA-"+siglasArea+"-"+secuenciasFacade.getCurrentYear() + "-CI";
			if(siglasArea.equals(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL))
				nombreSecuencia = "MAAE-SUIA-RA-"+siglasArea+"-"+secuenciasFacade.getCurrentYear();
				
			String secuencia=Constantes.SIGLAS_INSTITUCION + "-SUIA-RA-"+siglasArea+"-"+secuenciasFacade.getCurrentYear();
			return secuencia+"-" + secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia,5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public CertificadoInterseccionOficioCoa guardar(CertificadoInterseccionOficioCoa certificadoInterseccionOficioCoa){
		if(certificadoInterseccionOficioCoa.getAreaUsuarioFirma() != null) {
			Area areaFirma = areaFacade.getArea(certificadoInterseccionOficioCoa.getAreaUsuarioFirma());
			String siglasArea = areaFirma.getAreaAbbreviation();
			if(areaFirma.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC))
				siglasArea = Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL;
			else if(areaFirma.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES))
				siglasArea = areaFirma.getAreaAbbreviation();
			else if(areaFirma.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT))
				siglasArea = areaFirma.getArea().getAreaAbbreviation();
			
			if(certificadoInterseccionOficioCoa.getCodigo()==null)
				certificadoInterseccionOficioCoa.setCodigo(generarCodigo(siglasArea));
			else if(!certificadoInterseccionOficioCoa.getCodigo().contains(siglasArea))
				certificadoInterseccionOficioCoa.setCodigo(generarCodigo(siglasArea));
		}
		
		return crudServiceBean.saveOrUpdate(certificadoInterseccionOficioCoa);
	}
	
	public CertificadoInterseccionOficioCoa obtenerPorCodigoProyecto(String codigoProyecto) {
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM CertificadoInterseccionOficioCoa o WHERE o.estado=true and o.proyectoLicenciaCoa.codigoUnicoAmbiental=:codigoProyecto ORDER BY 1 desc");
			query.setParameter("codigoProyecto", codigoProyecto);
			query.setMaxResults(1);
			return (CertificadoInterseccionOficioCoa)query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public CertificadoInterseccionOficioCoa obtenerPorCodigoProyectoTarea(String codigoProyecto, Long idTarea) {
		try {
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT o FROM CertificadoInterseccionOficioCoa o WHERE o.estado=true and o.proyectoLicenciaCoa.codigoUnicoAmbiental=:codigoProyecto and o.idTarea=:idTarea ORDER BY 1 desc");
			query.setParameter("codigoProyecto", codigoProyecto);
			query.setParameter("idTarea", idTarea);
			query.setMaxResults(1);
			return (CertificadoInterseccionOficioCoa) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public CertificadoInterseccionOficioCoa obtenerPorProyectoNroActualizacion(String codigoProyecto, Integer nroActualizacion) {
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM CertificadoInterseccionOficioCoa o "
					+ "WHERE o.estado=true and o.proyectoLicenciaCoa.codigoUnicoAmbiental=:codigoProyecto and o.nroActualizacion =:nroActualizacion ORDER BY 1 desc");
			query.setParameter("codigoProyecto", codigoProyecto);
			query.setParameter("nroActualizacion", nroActualizacion);
			query.setMaxResults(1);
			return (CertificadoInterseccionOficioCoa)query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public CertificadoInterseccionOficioCoa guardarActualizacion(CertificadoInterseccionOficioCoa certificadoInterseccionOficioCoa){
		if(certificadoInterseccionOficioCoa.getCodigo()==null)
			certificadoInterseccionOficioCoa.setCodigo(generarCodigoActualizacion(certificadoInterseccionOficioCoa.getAreaUsuarioFirma()));
		return crudServiceBean.saveOrUpdate(certificadoInterseccionOficioCoa);
	}
	
	private String generarCodigoActualizacion(Integer idArea) {
		try {			
			Area areaFirma = areaFacade.getArea(idArea);
			
			String siglasArea = areaFirma.getAreaAbbreviation();
			String nombreSecuencia = null;
			if(areaFirma.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				siglasArea = Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL;
				nombreSecuencia = "ACTUALIZACION_CERTIFICADO_INTERSECCION_" + secuenciasFacade.getCurrentYear();
			} else if(areaFirma.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES))
				siglasArea = areaFirma.getAreaAbbreviation();
			else if(areaFirma.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT))
				siglasArea = areaFirma.getArea().getAreaAbbreviation();
			
			if(nombreSecuencia == null)
				nombreSecuencia = "ACTUALIZACION_CERTIFICADO_INTERSECCION_" + siglasArea + "_" + secuenciasFacade.getCurrentYear();
			
			String secuencia=Constantes.SIGLAS_INSTITUCION + "-SUIA-RA-"+siglasArea+"-"+secuenciasFacade.getCurrentYear();
			return secuencia+"-" + secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 5) +"-A";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public List<CertificadoInterseccionOficioCoa> obtenerActualizadosPorCodigoProyecto(String codigoProyecto) {
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM CertificadoInterseccionOficioCoa o WHERE o.estado=true and o.proyectoLicenciaCoa.codigoUnicoAmbiental=:codigoProyecto and o.nroActualizacion > 0 ORDER BY 1 desc");
			query.setParameter("codigoProyecto", codigoProyecto);
			
			List<CertificadoInterseccionOficioCoa> listaCertificados = (List<CertificadoInterseccionOficioCoa>) query.getResultList();

			return listaCertificados;
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public void actualizarFechaCertificado(String fechaOficio, Integer idOficio){	
		try{

			String sql = " update coa_mae.certificate_intersection_coa set cein_trade_date = '" + fechaOficio+ "' "
					+ " where cein_id = " + idOficio + " ;";
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		}catch(Exception e){
			System.out.println("Error al actualizar la fecha del oficio");;
		}
	}

}
