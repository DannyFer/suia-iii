package ec.gob.ambiente.suia.control.documentos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentoProyecto;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class DocumentosService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@SuppressWarnings("unchecked")
	public List<Documento> getDocumentosPorFlowProcessInstanceId(Long processInstanceId) {
		List<Documento> documentos = crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT dpt.documento FROM DocumentosTareasProceso dpt WHERE dpt.processInstanceId =:paramIdProcessInstance and dpt.documento.estado=true and dpt.idTarea != dpt.processInstanceId")
				.setParameter("paramIdProcessInstance", processInstanceId).getResultList();
		//existen documentos de certificados ambientales que fueron guardados con el campo processInstanceId = id_proyecto y idTarea = id_proyecto en el metodo categoria1Facade.crearCertificadoRegistroAmbiental
		//se agrega la condicion dpt.idTarea != dpt.processInstanceId para que los documentos de certificados no se visualicen en procesos distintos
		// se verifico en base de datos que todos los documentos q cumplen la condicion dpt.idTarea != dpt.processInstanceId  son certificados ambientales
		return documentos;
	}

	public List<Documento> documentoXTablaId(Integer idTabla, String nombreTabla) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idTabla", idTabla);
		params.put("nombreTabla", nombreTabla);
		@SuppressWarnings("unchecked")
		List<Documento> listaDocumentos = (List<Documento>) crudServiceBean.findByNamedQuery(
				Documento.LISTAR_POR_ID_NOMBRE_TABLA, params);
		return listaDocumentos;
	}

	public List<Documento> documentoXTablaIdXIdDoc(Integer idTabla, String nombreTabla, TipoDocumentoSistema tipoDocumento) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idTabla", idTabla);
		params.put("nombreTabla", nombreTabla);
		params.put("idTipo", tipoDocumento.getIdTipoDocumento());
		List<Documento> listaDocumentos = (List<Documento>) crudServiceBean.findByNamedQuery(
				Documento.LISTAR_POR_ID_NOMBRE_TABLA_TIPO, params);
		return listaDocumentos;
	}

	public Documento buscarDocumentoPorId(Integer idDocumento) {
		return crudServiceBean.find(Documento.class, idDocumento);
	}
	public Documento buscarDocumentoPorIdAlfresco(String idAlfresco) {

		List<Documento> documentos = crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT d FROM Documento d WHERE d.idAlfresco =:idAlfresco")
				.setParameter("idAlfresco", idAlfresco).getResultList();

		return !documentos.isEmpty() ? documentos.get(documentos.size()-1) : null;

	}


	public Documento eliminarDocumento(Documento doc) {
		return crudServiceBean.delete(doc);
	}

	public void guardarDocumentoProyecto(DocumentoProyecto documentoProyecto) {
		crudServiceBean.saveOrUpdate(documentoProyecto);
	}

	@SuppressWarnings("unchecked")
	public List<Documento> getDocumentosGeneradosPorCertificadoInterseccion(Integer idProyecto) {
		// select * from suia_iii.projects_document pd, suia_iii.documents d where pd.docu_id=d.docu_id and
		// pd.pren_id=14287 and d.doty_id in (3,4)

		String query = "Select doc FROM DocumentoProyecto docProy, Documento doc, ProyectoLicenciamientoAmbiental proy where docProy.documento=doc and docProy.proyectoLicenciamientoAmbiental=proy and proy.id=:idProyecto and doc.tipoDocumento IN (3,4)";
		List<Documento> documentos = crudServiceBean.getEntityManager().createQuery(query)
				.setParameter("idProyecto", idProyecto).getResultList();
		return documentos;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoProyecto> getDocumentosPorIdProyecto(Integer idProyecto){
		List<DocumentoProyecto> documentosProyecto = new ArrayList<DocumentoProyecto>();
		documentosProyecto = (List<DocumentoProyecto>) crudServiceBean.getEntityManager()
				.createQuery("From DocumentoProyecto m where m.proyectoLicenciamientoAmbiental.id =:idProyecto and m.estado = true")
				.setParameter("idProyecto", idProyecto).getResultList();
		return documentosProyecto;
	}
	
	public Documento getDocumentoPorWorkSpace(String workSpace) {
		try {
			String query = "select d FROM Documento d where d.idAlfresco = :workSpace";
			Documento documento = (Documento) crudServiceBean.getEntityManager().createQuery(query)
					.setParameter("workSpace", workSpace).getSingleResult();
			return documento;
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<Documento> listarTodoDocumentosXTablaId(Integer idTabla, String nombreTabla) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idTabla", idTabla);
		params.put("nombreTabla", nombreTabla);
		@SuppressWarnings("unchecked")
		List<Documento> listaDocumentos = (List<Documento>) crudServiceBean.findByNamedQuery(
				Documento.LISTAR_TODO_POR_ID_NOMBRE_TABLA, params);
		return listaDocumentos;
	}
	
	@SuppressWarnings("unchecked")
	public List<Documento> getDocumentosPorIdHistorico(Integer idHistorico){
		List<Documento> documentos = new ArrayList<Documento>();
		documentos = (List<Documento>) crudServiceBean.getEntityManager()
				.createQuery("From Documento m where m.idHistorico =:idHistorico and m.estado = true order by 1 desc")
				.setParameter("idHistorico", idHistorico).getResultList();
		return documentos;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Documento> getDocumentosPorIdHistoricoNotificacion(Integer idHistorico, Integer numeroNotificacion){
		List<Documento> documentos = new ArrayList<Documento>();
		documentos = (List<Documento>) crudServiceBean.getEntityManager()
				.createQuery("From Documento m where m.idHistorico =:idHistorico and m.numeroNotificacion = :numeroNotificacion "
						+ "and m.estado = true order by 1 desc")
				.setParameter("idHistorico", idHistorico).setParameter("numeroNotificacion", numeroNotificacion).getResultList();
		return documentos;
	}
	
	public List<Documento> documentosTodosXTablaIdXIdDoc(Integer idTabla, String nombreTabla, TipoDocumentoSistema tipoDocumento) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idTabla", idTabla);
		params.put("nombreTabla", nombreTabla);
		params.put("idTipo", tipoDocumento.getIdTipoDocumento());
		List<Documento> listaDocumentos = (List<Documento>) crudServiceBean.findByNamedQuery(
				Documento.LISTAR_TODOS_POR_ID_NOMBRE_TABLA_TIPO, params);
		return listaDocumentos;
	}
}
