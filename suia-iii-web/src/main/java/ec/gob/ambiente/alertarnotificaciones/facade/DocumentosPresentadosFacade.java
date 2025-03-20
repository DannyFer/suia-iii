package ec.gob.ambiente.alertarnotificaciones.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DocumentosPresentados;
import ec.gob.ambiente.suia.dto.EntityDocumentosPresentados;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

@Stateless
public class DocumentosPresentadosFacade {

//	private static final Logger LOG = Logger
//			.getLogger(DocumentosPresentadosFacade.class);
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private EjecutarSentenciasNativas ejecutarSentenciasNativas;
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();

	public List<EntityDocumentosPresentados> listaDocumentosPresentadosEntity(Integer area) throws ServiceException {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ((select pren_code, pren_name, dosu_id,cado_description, dosu_present,dosu_date_duration "
				+ " from suia_iii.projects_environmental_licensing p inner join alert_notification.documents_submitted a on a.pren_id = p.pren_id"
				+ " inner join alert_notification.catalog_document c on c.cado_id = a.cado_id and p.area_id="+area
				+ " ) union all"
				+ " (select id, nombre, dohi_id, cado_description, dohi_present,dohi_date_duration from alert_notification.documents_submitted_hidrocarbons a"
				+ " inner join alert_notification.catalog_document c on c.cado_id  = a.cado_id "
				+ " inner join ( select id, nombre from( select  id, nombre"
//				+ " from dblink('dbname= jbpmdb port=5432 host=172.16.0.233 user=postgres password=postgres',"
				+ " from dblink('"+dblinkSuiaVerde+"',"
				+ " 'select id, nombre from proyectolicenciaambiental order by 1' ) as t1"
				+ " (id text, nombre text ) ) y ) u on u.id = a.dohi_project_code and a.area_id="+area+" )) p "
				+ " group by pren_code, pren_name,dosu_id ,cado_description, dosu_present, dosu_date_duration order by 1");
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityDocumentosPresentados.class, null);
	}
	
	public DocumentosPresentados datosDocumentosPresentados(Integer id) {
		DocumentosPresentados documentosPresentados= new DocumentosPresentados();
		try {			
			documentosPresentados=(DocumentosPresentados) crudServiceBean.getEntityManager().createQuery(" FROM DocumentosPresentados d where d.id=:id").setParameter("id", id).getSingleResult();	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("errrol--------------"+e);
		}
		return documentosPresentados;
	}

	public void guadarDocumentosPresentados(
			DocumentosPresentados documentosPresentados) {
		crudServiceBean.saveOrUpdateAlert(documentosPresentados);
	}
}
