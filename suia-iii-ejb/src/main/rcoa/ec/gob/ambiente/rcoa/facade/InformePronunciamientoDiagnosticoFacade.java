package ec.gob.ambiente.rcoa.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.InformePronunciamientoDiagnostico;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformePronunciamientoDiagnosticoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private SecuenciasFacade secuenciasFacade;

	private String generarCodigo(Area area) {
		try {			
			String nombreSecuencia = "INFORME_DIAGNOSTICO_AMBIENTAL"
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

	public InformePronunciamientoDiagnostico guardar(InformePronunciamientoDiagnostico informe, Area area){
		if(informe.getCodigo()==null)
			informe.setCodigo(generarCodigo(area));		
		return crudServiceBean.saveOrUpdate(informe);
	}

	public InformePronunciamientoDiagnostico guardar(InformePronunciamientoDiagnostico informe){
		return crudServiceBean.saveOrUpdate(informe);
	}
	
	public InformePronunciamientoDiagnostico getPorProyecto(Integer idProyecto) {
		InformePronunciamientoDiagnostico result = new InformePronunciamientoDiagnostico();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT i FROM InformePronunciamientoDiagnostico i "
				+ "WHERE i.proyectoLicenciaCoa.id = :idProyecto order by id desc");
		sql.setParameter("idProyecto", idProyecto);
		if (sql.getResultList().size() > 0){
			result =(InformePronunciamientoDiagnostico) sql.getResultList().get(0);
		}
		return result;
	}

}
