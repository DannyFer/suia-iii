package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.OficioPronunciamientoDiagnostico;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class OficioPronunciamientoDiagnosticoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private SecuenciasFacade secuenciasFacade;

	private String generarCodigo(Area area) {
		try {			
			String nombreSecuencia = "OFICIO_DIAGNOSTICO_AMBIENTAL"
					+ area.getAreaAbbreviation() + "_"
					+ secuenciasFacade.getCurrentYear();
			String secuencia = Constantes.SIGLAS_INSTITUCION + "-SUIA-" + area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
			if(area.getTipoArea().getId()==3)
				secuencia = "SUIA-" + area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
			
			return secuencia
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public OficioPronunciamientoDiagnostico guardar(OficioPronunciamientoDiagnostico oficio, Area area){
		if(oficio.getCodigo()==null)
			oficio.setCodigo(generarCodigo(area));		
		return crudServiceBean.saveOrUpdate(oficio);
	}

	public OficioPronunciamientoDiagnostico guardar(OficioPronunciamientoDiagnostico oficio){
		return crudServiceBean.saveOrUpdate(oficio);
	}

	public OficioPronunciamientoDiagnostico getPorProyecto(Integer idProyecto) {
		OficioPronunciamientoDiagnostico result = new OficioPronunciamientoDiagnostico();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT i FROM OficioPronunciamientoDiagnostico i "
				+ "WHERE i.proyectoLicenciaCoa.id = :idProyecto order by id desc");
		sql.setParameter("idProyecto", idProyecto);
		if (sql.getResultList().size() > 0){
			result =(OficioPronunciamientoDiagnostico) sql.getResultList().get(0);
		}
		return result;
	}

	public OficioPronunciamientoDiagnostico getPorProyectoTipo(Integer idProyecto, Integer tipo) {
		OficioPronunciamientoDiagnostico result = new OficioPronunciamientoDiagnostico();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT i FROM OficioPronunciamientoDiagnostico i "
				+ "WHERE i.proyectoLicenciaCoa.id = :idProyecto and i.tipoPronunciamiento = :tipo order by id desc");
		sql.setParameter("idProyecto", idProyecto);
		sql.setParameter("tipo", tipo);
		if (sql.getResultList().size() > 0){
			result =(OficioPronunciamientoDiagnostico) sql.getResultList().get(0);
		}
		return result;
	}

}
