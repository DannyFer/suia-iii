/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.tdr.facade;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.chemistry.opencmis.client.api.Document;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Consultor;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.prevencion.tdr.service.TdrService;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ResourcesUtil;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 30/01/2015]
 *          </p>
 */
@Stateless
public class TdrFacade {

	// private static final Logger LOG = Logger.getLogger(TdrFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	AlfrescoServiceBean alfrescoServiceBean;

	@EJB
	DocumentosFacade documentosFacade;
	
	@EJB
	TdrService tdrService;
	
	public List<Documento> recuperarDocumentosTdr(String nombreTabla, Integer id){
		return tdrService.recuperarDocumentosTdr(nombreTabla, id);
	}
	
	public List<Consultor> obtenerConsultores(){
		return tdrService.obtenerConsultores();
	}

	public void modificarRegistro(EntidadBase objeto) {
		crudServiceBean.saveOrUpdate(objeto);
	}

	public void saveOrUpdate(EntidadBase objeto) {
		crudServiceBean.saveOrUpdate(objeto);
	}

	public void delete(EntidadBase objeto) {
		crudServiceBean.delete(objeto);
	}

	public TdrEiaLicencia tdrEiaLicencia(int id) {
		return crudServiceBean.find(TdrEiaLicencia.class, id);
	}
	
	public TdrEiaLicencia guardarTdrEia(TdrEiaLicencia tdrEiaLicencia){
		return tdrService.guardarTdrEia(tdrEiaLicencia);
	}

	@SuppressWarnings("unchecked")
	public TdrEiaLicencia getTdrEiaLicenciaPorIdProyecto(Integer idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);

		List<TdrEiaLicencia> result = (List<TdrEiaLicencia>) crudServiceBean
				.findByNamedQuery(TdrEiaLicencia.FIND_BY_PROJECT, parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	public TdrEiaLicencia getTdrEiaLicenciaPorIdProyectoFull(Integer idProyecto) {
		return tdrService.tdrEiaLicenciaPorIdProyectoFull(idProyecto);
	}

	public void ingresarInformeTdr(File file, Integer id, long idProceso,
			long idTarea) throws Exception {
		String folderName;
		folderName = UtilAlfresco.generarEstructuraCarpetas(
				Constantes.ALFRESCO_TDR_FOLDER_NAME, "InformeTecnnicoGeneral",
				id.toString());
		String folderId;

		folderId = alfrescoServiceBean.createFolderStructure(folderName,
				Constantes.getRootId());

		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

		byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

		Document documentCreate = alfrescoServiceBean.fileSaveStream(data,
				file.getName(), folderId, Constantes.ALFRESCO_TDR_FOLDER_NAME,
				folderName, id);

		Documento documentoAlfresco = crudServiceBean.saveOrUpdate(UtilAlfresco
				.crearDocumento(documentCreate.getName(),
						mimeTypesMap.getContentType(file),
						documentCreate.getId(), id,
						mimeTypesMap.getContentType(file), file.getName(),
						TdrEiaLicencia.class.getSimpleName(), TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();

		documentoTarea.setDocumento(documentoAlfresco);
		documentoTarea.setIdTarea(idTarea);
		documentoTarea.setProcessInstanceId(idProceso);
		crudServiceBean.saveOrUpdate(documentoTarea);
	}

	/*
	 * 
	 * public void ingresarInformeTdr(File file, Integer id, Integer idProceso,
	 * Integer idTarea) throws Exception { String folderName; folderName =
	 * UtilAlfresco.generarEstructuraCarpetas(
	 * Constantes.ALFRESCO_TDR_FOLDER_NAME, "InformeTecnnicoGeneral",
	 * id.toString()); String folderId;
	 * 
	 * folderId = alfrescoServiceBean.createFolderStructure(folderName,
	 * Constantes.ROOT_ID);
	 * 
	 * MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
	 * 
	 * byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
	 * 
	 * // Document documentCreate = alfrescoServiceBean.fileSaveStream(data, //
	 * file.getName(), folderId, Constantes.ALFRESCO_TDR_FOLDER_NAME, //
	 * folderName, id); Documento documento =
	 * UtilAlfresco.crearDocumento(file.getName(),
	 * mimeTypesMap.getContentType(file), null, id,
	 * mimeTypesMap.getContentType(file), file.getName(),
	 * TdrEiaLicencia.class.getSimpleName(), 2);
	 * 
	 * DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
	 * documentoTarea.setIdTarea(idTarea);
	 * documentoTarea.setProcessInstanceId(idProceso); //String
	 * codigoProyecto,String nombreProceso, Integer idProceso, Documento
	 * documento, // Integer tipoDocumento,DocumentosTareasProceso
	 * documentoTarea documentosFacade.guardarDocumentoAlfresco(
	 * Constantes.ALFRESCO_TDR_FOLDER_NAME, "InformeTecnnicoGeneral", idProceso,
	 * documento, mimeTypesMap.getContentType(file), documentoTarea);
	 * 
	 * }
	 */

	public void ingresarTdrAdjunto(File file, Integer id,
			String folderFileName, long idProceso, long idTarea)
			throws Exception {
		String folderName;
		folderName = UtilAlfresco.generarEstructuraCarpetas(
				Constantes.ALFRESCO_TDR_FOLDER_NAME, "Adjunto", id.toString());
		String folderId;

		folderId = alfrescoServiceBean.createFolderStructure(folderName,
				Constantes.getRootId());

		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

		byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

		Document documentCreate = alfrescoServiceBean.fileSaveStream(data,
				file.getName(), folderId, Constantes.ALFRESCO_TDR_FOLDER_NAME,
				folderName, id);

		if (documentCreate != null) {
			String nombreFichero = file.getName();
			if (!ResourcesUtil.getRoleAreaName(
					"file.name.informe.area." + folderFileName).isEmpty()) {
				nombreFichero = ResourcesUtil
						.getRoleAreaName("file.name.informe.area."
								+ folderFileName);
			}
			Documento documentoAlfresco = crudServiceBean
					.saveOrUpdate(UtilAlfresco.crearDocumento(
							documentCreate.getName(),
							mimeTypesMap.getContentType(file),
							documentCreate.getId(), id,
							mimeTypesMap.getContentType(file), nombreFichero,
							TdrEiaLicencia.class.getSimpleName()
									+ folderFileName, TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

			DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
			documentoTarea.setDocumento(documentoAlfresco);
			documentoTarea.setIdTarea(idTarea);
			documentoTarea.setProcessInstanceId(idProceso);
			crudServiceBean.saveOrUpdate(documentoTarea);
		} else {
			throw new Exception("Error al crear el documento en el alfreso.");
		}
	}

	/**
	 * Eliminar documento duplicado
	 * 
	 * @param className
	 *            el nombre adicionar, puede ser ""
	 * @param id
	 * @param type
	 */
	public void eliminarDocumentoTdr(String className, Integer id, Integer type) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nombreTabla", TdrEiaLicencia.class.getSimpleName()
				+ className);
		parameters.put("idTabla", id);
		parameters.put("idTipo", type);

		@SuppressWarnings("unchecked")
		List<Documento> documentos = (List<Documento>) crudServiceBean
				.findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA_TIPO,
						parameters);
		if (!documentos.isEmpty()) {
			for (Documento documento : documentos) {
				crudServiceBean.delete(documento);
			}
		}
	}

	public byte[] recuperarInformeTdr(Integer id) throws Exception {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nombreTabla", TdrEiaLicencia.class.getSimpleName());
		parameters.put("idTabla", id);
		parameters.put("idTipo", 2);
		@SuppressWarnings("unchecked")
		List<Documento> documentos = (List<Documento>) crudServiceBean
				.findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA_TIPO,
						parameters);
		if (!documentos.isEmpty()) {
			Documento doc = documentos.get(0);
			System.out.println("--------------------------------------------");
			System.out.println(doc.getIdAlfresco());
			byte[] archivo = alfrescoServiceBean.downloadDocumentById(doc
					.getIdAlfresco());
			return archivo;
			// FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
			// fos.write(archivo);
			// fos.close();
			//
			// return f;
			// floraGeneral.setFileAnexo(archivo);
			// floraGeneral.setContentType(documentos.get(0).getMime());
			// }

		}
		return null;
	}

	public void ingresarInformeTdrArea(File file, String codigoProyecto,
			Integer id, String area, long idProceso, long idTarea)
			throws Exception {
		String folderName;
		folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto,
				"InformeTecnnicoArea" + area, id.toString());
		String folderId;
		folderId = alfrescoServiceBean.createFolderStructure(folderName,
				Constantes.getRootId());

		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

		byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

		Document documentCreate = alfrescoServiceBean.fileSaveStream(data,
				file.getName(), folderId, Constantes.ALFRESCO_TDR_FOLDER_NAME,
				folderName, id);
		String nombreFichero = file.getName();

		if (!ResourcesUtil.getRoleAreaName("file.name.informe.area." + area)
				.isEmpty()) {
			nombreFichero = ResourcesUtil
					.getRoleAreaName("file.name.informe.area." + area);
		}
		Documento documentoAlfresco = crudServiceBean.saveOrUpdate(UtilAlfresco
				.crearDocumento(documentCreate.getName(),
						mimeTypesMap.getContentType(file),
						documentCreate.getId(), id,
						mimeTypesMap.getContentType(file), nombreFichero,
						TdrEiaLicencia.class.getSimpleName() + "-informe-area-"
								+ area, TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

		DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();

		documentoTarea.setDocumento(documentoAlfresco);
		documentoTarea.setIdTarea(idTarea);
		documentoTarea.setProcessInstanceId(idProceso);
		crudServiceBean.saveOrUpdate(documentoTarea);
	}

	public byte[] recuperarInformeTdrArea(Integer id, String area)
			throws Exception {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nombreTabla", TdrEiaLicencia.class.getSimpleName()
				+ "-informe-area-" + area);
		parameters.put("idTabla", id);
		parameters.put("idTipo", 2);
		@SuppressWarnings("unchecked")
		List<Documento> documentos = (List<Documento>) crudServiceBean
				.findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA_TIPO,
						parameters);
		if (!documentos.isEmpty()) {
			Documento doc = documentos.get(0);
			byte[] archivo = alfrescoServiceBean.downloadDocumentById(doc
					.getIdAlfresco());
			return archivo;

		}
		return null;
	}

	public byte[] recuperarInformeTdrPronunciamiento(Integer id)
			throws Exception {
		System.out.println("--sss-----");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nombreTabla", TdrEiaLicencia.class.getSimpleName()
				+ "PronunciamientoMJ");
		parameters.put("idTabla", id);
		parameters.put("idTipo", 1);
		System.out.println("---s----");
		@SuppressWarnings("unchecked")
		List<Documento> documentos = (List<Documento>) crudServiceBean
				.findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA_TIPO,
						parameters);
		if (!documentos.isEmpty()) {
			Documento doc = documentos.get(0);
			byte[] archivo = alfrescoServiceBean.downloadDocumentById(doc
					.getIdAlfresco());
			return archivo;

		}
		return null;
	}

	public String recuperarInformeTdrAreaDocumento(Integer id, String area)
			throws Exception {

		System.out.println(area);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nombreTabla", TdrEiaLicencia.class.getSimpleName()
				+ "-informe-area-" + area);
		parameters.put("idTabla", id);
		parameters.put("idTipo", 2);
		@SuppressWarnings("unchecked")
		List<Documento> documentos = (List<Documento>) crudServiceBean
				.findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA_TIPO,
						parameters);
		if (!documentos.isEmpty()) {
			Documento doc = documentos.get(0);
			return alfrescoServiceBean.downloadDocumentByObjectId(doc
					.getIdAlfresco());

		}
		return null;
	}
	
	public String obtenerSecuencial(){
		return crudServiceBean.getSecuenceNextValue(TdrEiaLicencia.SEQUENCE_CODE, "suia_iii").toString();
	}

}
