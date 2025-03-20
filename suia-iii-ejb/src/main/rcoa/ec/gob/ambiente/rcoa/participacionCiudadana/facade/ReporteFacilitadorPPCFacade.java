package ec.gob.ambiente.rcoa.participacionCiudadana.facade;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.OficioResolucionAmbiental;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.DocumentosPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.ReporteFacilitadorPPC;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class ReporteFacilitadorPPCFacade {
	
	private static final Logger LOG = Logger.getLogger(ReporteFacilitadorPPCFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	public ReporteFacilitadorPPC guardar(ReporteFacilitadorPPC reporteFacilitadorPPC){
		return crudServiceBean.saveOrUpdate(reporteFacilitadorPPC);
	}

	@SuppressWarnings("unchecked")
	public List<ReporteFacilitadorPPC> getByIdProyectoFacilitador(Integer idProyectoFacilitador) {
		List<ReporteFacilitadorPPC> result = new ArrayList<ReporteFacilitadorPPC>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT rfppc FROM ReporteFacilitadorPPC rfppc WHERE rfppc.proyectoFacilitadorPPC.id=:idProyectoFacilitador");
		sql.setParameter("idProyectoFacilitador", idProyectoFacilitador);
		if (sql.getResultList().size() > 0)
			result = (List<ReporteFacilitadorPPC>) sql.getResultList();
		return result;
	}
	
	public ReporteFacilitadorPPC getByIdProyectoFacilitadorTipoReporte(Integer idProyectoFacilitador, Integer idCategoria) {
		ReporteFacilitadorPPC result = new ReporteFacilitadorPPC();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT rfppc FROM ReporteFacilitadorPPC rfppc "
				+ "WHERE rfppc.proyectoFacilitadorPPC.id = :idProyectoFacilitador and rfppc.tipoReporte.id = :idCategoria order by id desc");
		sql.setParameter("idProyectoFacilitador", idProyectoFacilitador);
		sql.setParameter("idCategoria", idCategoria);
		if (sql.getResultList().size() > 0){
			result =(ReporteFacilitadorPPC) sql.getResultList().get(0);
		}
		return result;
	}

}
