package ec.gob.ambiente.suia.control.documentos.facade;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.model.DocumentoCertificadoAmbiental;
import ec.gob.ambiente.suia.comentarios.facade.ComentarioTareaFacade;
import ec.gob.ambiente.suia.control.documentos.service.DocumentosService;
import ec.gob.ambiente.suia.control.documentos.service.InformeTecnicoService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.crud.service.Generico;
import ec.gob.ambiente.suia.domain.ComentarioTarea;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentoProyecto;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.InformeTecnico;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.DocumentoReemplazable;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentosFacade extends Generico<Documento> {

    private static final Logger LOG = Logger.getLogger(DocumentosFacade.class);
    @EJB
    private InformeTecnicoService informeTecnicoService;
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private ComentarioTareaFacade comentarioTareaFacade;
    @EJB
    private DocumentosService documentosService;
    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
    @EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    private AlfrescoServiceBean alfrescoServiceBean;
    @EJB
    private CrudServiceBean crudServiceBean;
    
    //Cris Flores
    @EJB
    private UsuarioFacade usuarioFacade;

    public DocumentosFacade() {
        super(Documento.class);
    }

    public void guardarInformeTecnico(InformeTecnico informeTecnico) throws JbpmException {
        informeTecnicoService.guardarInformeTecnico(informeTecnico);
    }

    public void completarTareaEnviarInformeTecnico(InformeTecnico informeTecnico, Map<String, Object> parametros,
                                                   Long idTarea, Long idProceso, Usuario usuario) throws JbpmException {
        informeTecnicoService.guardarInformeTecnico(informeTecnico);
        // todo guR INFOME
        parametros.put("idInforme_", informeTecnico.getId());
        // parametros.put("idoFICIO_", ID)
        procesoFacade.aprobarTarea(usuario, idTarea, idProceso, parametros);
    }

    public void completarTarea(Map<String, Object> parametros, Long idTarea, Long idProceso, Usuario usuario)
            throws JbpmException {
        procesoFacade.aprobarTarea(usuario, idTarea, idProceso, parametros);
    }

    public InformeTecnico getInformeTecnico(Long idTarea, Usuario usuario) throws JbpmException {
        Map<String, Object> variables = procesoFacade.recuperarVariablesTarea(usuario, idTarea);
        Integer idInforme = (Integer) variables.get("_idInforme");
        InformeTecnico informe;
        if (idInforme == null)
            informe = new InformeTecnico();
        else
            informe = informeTecnicoService.getInformeTecnico(idInforme);
        return informe;
    }

    public void completarRevisionInformeTecnico(ComentarioTarea comentarioTarea, Map<String, Object> parametros,
                                                Long idInstanciaProceso, Integer idDocumento, Long idTarea, Usuario usuario) throws JbpmException {
        comentarioTarea.setIdUsuario(usuario.getNombre());
        comentarioTarea.setIdInstanciaProceso(idInstanciaProceso);
        comentarioTarea.setIdDocumento(idDocumento);
        comentarioTarea.setTipoDocumento(InformeTecnico.class.toString());
        comentarioTarea.setHora(new Date());
        if (comentarioTarea.isRequiereCorrecciones())
            comentarioTareaFacade.guaradar(comentarioTarea);

        parametros.put("tieneCorrecciones_", comentarioTarea.isRequiereCorrecciones());
        procesoFacade.aprobarTarea(usuario, idTarea, idInstanciaProceso, parametros);
    }

    private List<Documento> recuperarDocumentosPorFlujo(Long processInstanceId) {
        return documentosService.getDocumentosPorFlowProcessInstanceId(processInstanceId);
    }
    
    public List<Documento> recuperarDocumentosPorIdProceso(Long processInstanceId) {
        List<Documento> documentos = recuperarDocumentosPorFlujo(processInstanceId);
        return documentos;
    }
    
    public List<Documento> documentoXTablaId(Integer idTabla, String nombreTabla) {
        return documentosService.documentoXTablaId(idTabla, nombreTabla);
    }

    public List<Documento> documentoXTablaIdXIdDoc(Integer idTabla, String nombreTabla,
                                                   TipoDocumentoSistema tipoDocumento) {
        return documentosService.documentoXTablaIdXIdDoc(idTabla, nombreTabla, tipoDocumento);
    }
    
    public Documento documentoXTablaIdXIdDocUnico(Integer idTabla, String nombreTabla,TipoDocumentoSistema tipoDocumento) {
    	List<Documento> lista= documentosService.documentoXTablaIdXIdDoc(idTabla, nombreTabla, tipoDocumento);
    	if(lista == null || lista.isEmpty() || lista.get(0).getIdAlfresco() == null || lista.get(0).getIdAlfresco().isEmpty())
    		return null;
    	return lista.get(0);
    }
    
    public Documento documentoXTablaIdXIdDocUnicos(Integer idTabla, String nombreTabla,TipoDocumentoSistema tipoDocumento) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idTabla", idTabla);
		params.put("nombreTabla", nombreTabla);
		params.put("idTipo", tipoDocumento.getIdTipoDocumento());
		List<Documento> listaDocumentos = (List<Documento>) crudServiceBean.findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA_TIPO, params);
    	if(listaDocumentos == null || listaDocumentos.isEmpty() || listaDocumentos.get(0).getIdAlfresco() == null || listaDocumentos.get(0).getIdAlfresco().isEmpty())
    		return null;
    	return listaDocumentos.get(0);
    }

    public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {    	
    	Documento documentoBuscado = obtenerDocumentoPorWorkspace(idDocAlfresco);
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
//        byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
    	//Cris F: cambio para que se obtenga los documentos de los dos repositorios de alfresco
    	byte[] archivo = alfrescoServiceBean.downloadDocumentByIdLectura(idDocAlfresco, documentoBuscado.getFechaCreacion());
        return archivo;
    }
    
    public byte[] descargarAnterior(String idDocAlfresco) throws CmisAlfrescoException {  
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
        byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
        return archivo;
    }
    
    //Cris F: cambio para que se obtenga los documentos de los dos repositorios de alfresco
    public byte[] descargar(String idDocAlfresco, Date fecha) throws CmisAlfrescoException {    	
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];    	
    	byte[] archivo = alfrescoServiceBean.downloadDocumentByIdLectura(idDocAlfresco, fecha);
        return archivo;
    }
    
    public File descargarFile(String idDocAlfresco) throws CmisAlfrescoException, MalformedURLException, IOException {
    	//Cris F: descargar información de dos servidores
    	Documento documentoBuscado = obtenerDocumentoPorWorkspace(idDocAlfresco);
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	return alfrescoServiceBean.downloadDocumentByIdFile(idDocAlfresco, documentoBuscado.getFechaCreacion());
//        return alfrescoServiceBean.downloadDocumentByIdFile(idDocAlfresco);        
    }    

    public List<Documento> recuperarDocumentosConArchivosPorFlujo(Long processInstanceId) {
        List<Documento> documentos = recuperarDocumentosPorFlujo(processInstanceId);
        Map<TipoDocumento, Documento> documentosFiltrados = new HashMap<TipoDocumento, Documento>();
        for (Documento doc : documentos) {
            if (!documentosFiltrados.containsKey(doc.getTipoDocumento()))
                documentosFiltrados.put(doc.getTipoDocumento(), doc);
            else {
                Integer maxId = documentosFiltrados.get(doc.getTipoDocumento()).getId();
                if (doc.getId() > maxId) {
                    documentosFiltrados.put(doc.getTipoDocumento(), doc);
                }
            }
        }

        List<Documento> documentosResult = new ArrayList<Documento>();
        for (TipoDocumento tipoDocumento : documentosFiltrados.keySet()) {
            Documento doc = documentosFiltrados.get(tipoDocumento);
            if (doc != null && doc.getIdAlfresco() != null && !doc.getIdAlfresco().isEmpty()) {
                try {
                    byte[] archivoObtenido = descargar(doc.getIdAlfresco(), doc.getFechaCreacion());
//                	byte[] archivoObtenido = descargar(doc.getIdAlfresco());
                    doc.setContenidoDocumento(archivoObtenido);
                } catch (CmisAlfrescoException e) {
                    LOG.error("Error al descargar el documento del alfresco", e);
                }
                documentosResult.add(doc);
            }
        }

        return documentosResult;
    }

    public List<Documento> recuperarDocumentosConArchivosPorFlujoTodasVersionesNombresUnicos(Long processInstanceId) {
        List<Documento> documentos = recuperarDocumentosPorFlujo(processInstanceId);
        Map<String, Documento> documentosFiltrados = new HashMap<String, Documento>();
        for (Documento doc : documentos) {
        	if (!documentosFiltrados.containsKey(doc.getNombre()))
                documentosFiltrados.put(doc.getNombre(), doc);
            else {
                Integer maxId = documentosFiltrados.get(doc.getNombre()).getId();
                if (doc.getId() > maxId) {
                    documentosFiltrados.put(doc.getNombre(), doc);
                }
            }          
        }

        List<Documento> documentosResult = new ArrayList<Documento>();
        for (String nombreDocumento : documentosFiltrados.keySet()) {
            Documento doc = documentosFiltrados.get(nombreDocumento);
            if (doc != null && doc.getIdAlfresco() != null && !doc.getIdAlfresco().isEmpty()) {
                try {
                    byte[] archivoObtenido = descargar(doc.getIdAlfresco(), doc.getFechaCreacion());
//                	byte[] archivoObtenido = descargar(doc.getIdAlfresco());
                    doc.setContenidoDocumento(archivoObtenido);
                    documentosResult.add(doc);
                } catch (CmisAlfrescoException e) {
                	try {
                		byte[] archivoObtenido = descargarAnterior(doc.getIdAlfresco());
                		 doc.setContenidoDocumento(archivoObtenido);
                         documentosResult.add(doc);
					} catch (Exception e2) {
						LOG.error("Error al descargar el documento del alfresco", e);
					}                	
//                    LOG.error("Error al descargar el documento del alfresco", e);
                }

            }
        }

        Collections.sort(documentosResult, new Comparator<Documento>() {

            @Override
            public int compare(Documento o1, Documento o2) {
                if (o1.getFechaCreacion().before(o2.getFechaCreacion()))
                    return 1;
                if (o1.getFechaCreacion().after(o2.getFechaCreacion()))
                    return -1;
                return 0;
            }
        });

        return documentosResult;
    }

    public Documento buscarDocumentoPorId(Integer idDocumento) {
        return documentosService.buscarDocumentoPorId(idDocumento);
    }

    /**
     * <b>Método para guardar documento en alfreso. </b>
     *
     * @param codigoProyecto
     * @param nombreProceso
     * @param idProceso
     * @param documento
     * @param tipoDocumento
     * @param documentoTarea
     * @return
     * @throws Exception
     * @throws ServiceException
     * @throws CmisAlfrescoException
     * @author pganan
     * @version Revision: 1.0
     * <p>
     * [Autor: pganan, Fecha: 25/02/2015]
     * </p>
     */
    public Documento guardarDocumentoAlfresco(String codigoProyecto, String nombreProceso, Long idProceso,
                                              Documento documento, TipoDocumentoSistema tipoDocumento, DocumentosTareasProceso documentoTarea)
            throws ServiceException, CmisAlfrescoException {
        nombreProceso = nombreProceso.replace(".", "_");
        String idProces = null;
        if (idProceso != null) {
            idProces = idProceso.toString();
        }

        String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreProceso, idProces);
        String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootId());
        Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),
        		Calendar.getInstance().getTimeInMillis()+"-"+documento.getNombre().replace("/", "-"), folderId, nombreProceso, folderName, documento.getIdTable());

        Documento documentoAl = UtilAlfresco.crearDocumento(
                documento.getDescripcion() == null ? documentCreate.getName() : documento.getDescripcion(),
                documento.getExtesion(), documentCreate.getId(), documento.getIdTable(), documento.getMime(),
                documento.getNombre(), documento.getNombreTabla(), tipoDocumento);
        documentoAl.setCodigoPublico(documento.getCodigoPublico());
        Documento documentoAlfresco = crudServiceBean.saveOrUpdate(documentoAl);

        if (documentoTarea == null) {
            documentoTarea = UtilAlfresco.crearDocumentoTareaProceso(documento, 0, idProceso == null ? 0 : idProceso);
        }

        documentoTarea.setDocumento(documentoAlfresco);
        crudServiceBean.saveOrUpdate(documentoTarea);
        DocumentoProyecto documentoProyecto = new DocumentoProyecto();
        documentoProyecto.setDocumento(documentoAlfresco);
        documentoProyecto.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbientalFacade
                .getProyectoPorCodigo(codigoProyecto));
        documentosService.guardarDocumentoProyecto(documentoProyecto);
        return documentoAlfresco;

    }

    public Documento actualizarDocumento(Documento doc){
      return crudServiceBean.saveOrUpdate(doc);
    }

    /**
     * <b> Método que guarda los documentos en el alfresco de procesos que nos
     * estan asociados a un proyecto. Este método permite crear una estructura
     * de documentos en base al parametro nombreProceso ejemplo:
     * procesoUno/moduloA </b>
     * <p>
     * [Author: vero, Date: 18/08/2015]
     * </p>
     *
     * @param codigoProyecto
     * @param nombreProceso
     * @param idProceso
     * @param documento
     * @param tipoDocumento
     * @param documentoTarea
     * @return
     * @throws ServiceException
     * @throws CmisAlfrescoException
     */
    public Documento guardarDocumentoAlfrescoSinProyecto(String codigoProyecto, String nombreProceso, Long idProceso,
                                                         Documento documento, TipoDocumentoSistema tipoDocumento, DocumentosTareasProceso documentoTarea)
            throws ServiceException, CmisAlfrescoException {
        nombreProceso = nombreProceso.replace(".", "_");
        String idProces = null;
        if (idProceso != null) {
            idProces = idProceso.toString();
        }

        String modulo = null;
        if (nombreProceso.contains("/")) {
            String[] estructuraCarpetas = nombreProceso.split("/");
            modulo = estructuraCarpetas[estructuraCarpetas.length - 1];
        } else {
            modulo = nombreProceso;
        }
        
        String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreProceso, idProces);
        String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootRcoaId());
        Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),
        		Calendar.getInstance().getTimeInMillis()+"-"+documento.getNombre().replace("/", "-"), folderId, modulo, folderName);

        Documento documentoAlfresco = crudServiceBean.saveOrUpdate(UtilAlfresco.crearDocumento(
                documentCreate.getName(), documento.getExtesion(), documentCreate.getId(), documento.getIdTable(),
                documento.getMime(), documento.getNombre(), documento.getNombreTabla(), tipoDocumento));

        if (documentoTarea == null) {
            documentoTarea = UtilAlfresco.crearDocumentoTareaProceso(documento, 0, idProceso == null ? 0 : idProceso);
        }

        documentoTarea.setDocumento(documentoAlfresco);
        crudServiceBean.saveOrUpdate(documentoTarea);
        return documentoAlfresco;

    }
    
    public Documento guardarDocumentoAlfrescoSinProyecto(String codigoProyecto, String nombreProceso, Long idProceso,
            Documento documento, TipoDocumentoSistema tipoDocumento)
		throws ServiceException, CmisAlfrescoException {
    	nombreProceso = nombreProceso.replace(".", "_");
        String idProces = null;
        if (idProceso != null) {
            idProces = idProceso.toString();
        }
		
        String modulo = null;
        if (nombreProceso.contains("/")) {
            String[] estructuraCarpetas = nombreProceso.split("/");
            modulo = estructuraCarpetas[estructuraCarpetas.length - 1];
        } else {
            modulo = nombreProceso;
        }
		
        String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreProceso, idProces);
        String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootRcoaId());
        Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),
        		Calendar.getInstance().getTimeInMillis()+"-"+documento.getNombre().replace("/", "-"), folderId, modulo, folderName);

        Documento documentoAlfresco = crudServiceBean.saveOrUpdate(UtilAlfresco.crearDocumento(
                documentCreate.getName(), documento.getExtesion(), documentCreate.getId(), documento.getIdTable(),
                documento.getMime(), documento.getNombre(), documento.getNombreTabla(), tipoDocumento));
		
		return documentoAlfresco;
	}
    

//    private Documento buscarDocumentoPorIdAlfresco(String worksapce) {
//
//        return this.documentosService.buscarDocumentoPorIdAlfresco(worksapce);
//    }

    @SuppressWarnings("unchecked")
    public byte[] descargarDocumentoAlfrescoQueryDocumentos(String nombreTabla, Integer idTabla,
                                                            TipoDocumentoSistema tipoDocumento) throws CmisAlfrescoException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("nombreTabla", nombreTabla);
        parameters.put("idTabla", idTabla);
        parameters.put("idTipo", tipoDocumento.getIdTipoDocumento());

        List<Documento> documentos = (List<Documento>) crudServiceBean.findByNamedQuery(
                Documento.LISTAR_POR_ID_NOMBRE_TABLA_TIPO, parameters);
        if (!documentos.isEmpty()) {
            Documento doc = documentos.get(0);
            byte[] archivo = descargar(doc.getIdAlfresco(), doc.getFechaCreacion());
//            byte[] archivo = descargar(doc.getIdAlfresco());
            return archivo;
        }
        return null;
    }

    public byte[] descargarDocumentoPorNombre(String nombreDocumento) throws CmisAlfrescoException {
    	try {
    		return alfrescoServiceBean.downloadDocumentByName(nombreDocumento);
		} catch (Exception e) {
		}
        return null;    	
    }

    public byte[] descargarDocumentoPorNombreYDirectorioEspecifico(String nombreDocumento, String directorio)
            throws Exception {
        if (nombreDocumento == null || directorio == null)
            return null;
        return alfrescoServiceBean.downloadDocumentByNameAndFolder(nombreDocumento, directorio, false);
    }

    public byte[] descargarDocumentoPorNombreYDirectorioBase(String nombreDocumento, String directorioBase)
            throws CmisAlfrescoException {
        if (nombreDocumento == null)
            return null;
        if (directorioBase == null)
            directorioBase = Constantes.getRootStaticDocumentsId();
        return alfrescoServiceBean.downloadDocumentByNameAndFolder(nombreDocumento, directorioBase, true);
    }

    public byte[] getBytesFromFile(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        long length = file.length();
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;

        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    public boolean isGeneradoCorrectamenteDocumentosCertificadoInterseccion(Integer idProyecto) {
        if (documentosService.getDocumentosGeneradosPorCertificadoInterseccion(idProyecto).isEmpty()
                || documentosService.getDocumentosGeneradosPorCertificadoInterseccion(idProyecto).size() < 2) {
            return false;
        } else
            return true;
    }

    public List<Documento> getDocumentosCertificadoInterseccion(Integer idProyecto) {
        return documentosService.getDocumentosGeneradosPorCertificadoInterseccion(idProyecto);
    }

    public List<DocumentoProyecto> getDocumentosPorIdProyecto(Integer idProyecto) {
        return documentosService.getDocumentosPorIdProyecto(idProyecto);
    }

    public List<DocumentoReemplazable> getDocumentsPorCarpeta(String carpeta) throws CmisAlfrescoException {
        List<DocumentoReemplazable> documentos = new ArrayList<DocumentoReemplazable>();
        List<String[]> result = alfrescoServiceBean.getFolderContent(carpeta);
        for (String[] strings : result) {
            Documento documento = documentosService.getDocumentoPorWorkSpace(strings[0]);
            documentos.add(new DocumentoReemplazable(documento, strings[0], strings[2]));
        }
        return documentos;
    }

    public void replaceDocumentContent(DocumentoReemplazable documentoReemplazable, byte[] content)
            throws CmisAlfrescoException {
        alfrescoServiceBean.replaceDocumentContent(documentoReemplazable.getWorkSpaceId(), content,
                documentoReemplazable.getExtension());
    }

    public void replaceDocumentContent(Documento documento)
            throws CmisAlfrescoException {
        alfrescoServiceBean.replaceDocumentContent(documento.getIdAlfresco(), documento.getContenidoDocumento(),
                documento.getExtesion());
    }

    /**
     * Elimina logicamente un documento
     *
     * @param doc Documento
     */
    public void eliminarDocumento(Documento doc) {
        documentosService.eliminarDocumento(doc);
    }

    public String direccionDescarga(String nombreTabla, Integer idTabla, TipoDocumentoSistema tipoDocumento)
            throws CmisAlfrescoException, ServiceException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("nombreTabla", nombreTabla);
        parameters.put("idTabla", idTabla);
        parameters.put("idTipo", tipoDocumento.getIdTipoDocumento());

        @SuppressWarnings("unchecked")
        List<Documento> documentos = (List<Documento>) crudServiceBean.findByNamedQuery(
                Documento.LISTAR_POR_ID_NOMBRE_TABLA_TIPO, parameters);
        if (!documentos.isEmpty()) {
            Documento doc = documentos.get(0);
            String workspace=(doc.getIdAlfresco().split(";"))[0];
            //Cris F: aumento de fecha para buscar en todos los servidores
            return alfrescoServiceBean.downloadDocumentByObjectId(workspace, doc.getFechaCreacion());
        }
        throw new ServiceException("No se encontró el documento solicitado.");
    }

    //aqui ver si se puede cambiar para la firma para todos los sistemas
    public String direccionDescarga(Documento documento) throws CmisAlfrescoException {
    	String workspace=(documento.getIdAlfresco().split(";"))[0];
        return alfrescoServiceBean.downloadDocumentByObjectId(workspace, documento.getFechaCreacion());
    }
    

    /**
     * <b>Método que retorna un documento agregando los atributos de clase,
     * tarea y procesos, para aquellos procesos que no tienen asociado un
     * proyecto. </b>
     * <p/>
     * <p>
     * [Author: vero, Date: 18/08/2015]
     * </p>
     *
     * @param documento
     * @param proyecto
     * @param idProceso
     * @param idTarea
     * @param nombreClase
     * @param nombreCarpetaAlfresco
     * @param tipoDocumentoSistema
     * @return
     * @throws ServiceException
     * @throws CmisAlfrescoException
     */
    public Documento subirFileAlfrescoSinProyectoAsociado(Documento documento, String proyecto, long idProceso,
                                                          long idTarea, String nombreClase, String nombreCarpetaAlfresco, TipoDocumentoSistema tipoDocumentoSistema)
            throws ServiceException, CmisAlfrescoException {
        if (documento.getIdAlfresco() == null) {
            DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
            documentoTarea.setIdTarea(idTarea);
            documento.setNombreTabla(nombreClase);
            documentoTarea.setProcessInstanceId(idProceso);
            return guardarDocumentoAlfrescoSinProyecto(proyecto, nombreCarpetaAlfresco, 0L, documento,
                    tipoDocumentoSistema, documentoTarea);
        } else {
            return documento;
        }
    }

    /****
     * @autor : Denis Linares
     * Verifica si el documento firmado existe en el alfresco.
     * @param idAlfresco : Identificador del documento firmado en el alfresco.
     * @return
     */
    public boolean verificarFirmaAlfresco(String idDocAlfresco) {
        File file = null;
        try {
        	String url = idDocAlfresco;
        	url=(url.split(";"))[0];
        	file = alfrescoServiceBean.downloadDocumentByIdFile(url);
        	if(verificarFirma(file.getAbsolutePath()))
        		return true;

        	return false;
        } catch (CmisAlfrescoException e) {
            LOG.error(e);
            return false;
        } catch (IOException e) {
            LOG.error(e);
            return false;
        }
    }

    /**
     * Documento almacenado en el alfresco
     *
     * @param documento
     * @return
     */
    /*public boolean verificarFirma(Documento documento) {
        return verificarFirmaAlfresco(documento.getIdAlfresco());

    }*/

    /****
     * @autor : Denis Linares
     * Verifica si el documento firmado existe en el alfresco.
     * @param idAlfresco : Identificador del documento firmado en el alfresco.
     * @return
     */
    public boolean verificarFirmaVersion(String idAlfresco) {
        return verificarFirmaAlfresco(idAlfresco);

    }

    /**
     * Id del documento en el alfresco
     *
     * @param idDocumento
     * @return
     */
    public boolean verificarFirma(Integer idDocumento) {
        Documento documento = buscarDocumentoPorId(idDocumento);
        return verificarFirmaAlfresco(documento.getIdAlfresco());

    }

    /**
     * Url del fichero que se va a verificar la firma.
     *
     * @param fileUrl
     * @return
     */
    public boolean verificarFirma(String fileUrl) {
        try {

            PdfReader reader = new PdfReader(fileUrl);
            AcroFields af = reader.getAcroFields();
            ArrayList names = af.getSignatureNames();
            return names.size() > 0;
//			Random rnd = new Random();
//			KeyStore kall = PdfPKCS7.loadCacertsKeyStore();
            /*
            for (int k = 0; k < names.size(); ++k) {
				String name = (String)names.get(k);;
				int random = rnd.nextInt();
				FileOutputStream out = new FileOutputStream("revision_" + random + "_" + af.getRevision(name) + ".pdf");

				byte bb[] = new byte[8192];
				InputStream ip = af.extractRevision(name);
				int n = 0;
				while ((n = ip.read(bb)) > 0)
					out.write(bb, 0, n);
				out.close();
				ip.close();

				PdfPKCS7 pk = af.verifySignature(name);
				Calendar cal = pk.getSignDate();
				Certificate pkc[] = pk.getCertificates();
				Object fails[] = PdfPKCS7.verifyCertificates(pkc, kall, null, cal);
				if (fails == null) {
					System.out.print(pk.getSignName());
				}
				else {
					System.out.print("Firma no válida");
				}
				File f = new File("revision_" + random + "_" + af.getRevision(name) + ".pdf");
				f.delete();
			}*/
        } catch (Exception e) {
            LOG.error(e);
            return false;
        }
    }
    
	public String guardarDocumentoAlfrescoDesechosIVCategorias(String codigoProyecto,String nombreProceso, Long idProceso, Documento documento) throws ServiceException,CmisAlfrescoException {
		
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		String workspace="";
		if (idProceso != null) {
			idProces = idProceso.toString();
		}

		String modulo = null;
		if (nombreProceso.contains("/")) {
			String[] estructuraCarpetas = nombreProceso.split("/");
			modulo = estructuraCarpetas[estructuraCarpetas.length - 1];
		} else {
			modulo = nombreProceso;
		}
		String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName,Constantes.getRootId());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(
				documento.getContenidoDocumento(), documento.getNombre(),
				folderId, modulo, folderName);
		workspace=documentCreate.getId();
		return workspace;

	}
	
	public String guardarDocumentoAlfrescoFirmas(String codigoProyecto,String nombreProceso, Long idProceso, Documento documento) throws ServiceException,CmisAlfrescoException {
		
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		String workspace="";
		if (idProceso != null) {
			idProces = idProceso.toString();
		}

		String modulo = null;
		if (nombreProceso.contains("/")) {
			String[] estructuraCarpetas = nombreProceso.split("/");
			modulo = estructuraCarpetas[estructuraCarpetas.length - 1];
		} else {
			modulo = nombreProceso;
		}
		String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName,Constantes.getRootId());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(
				documento.getContenidoDocumento(), documento.getNombre(),
				folderId, modulo, folderName);
		workspace=documentCreate.getId();
		return workspace;

	}

	@SuppressWarnings("unchecked")
	public List<Documento> recuperarDocumentosPorTipo(Integer id,
			String nombre, List<Integer> listaTipos) throws Exception {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nombreTabla", nombre);
		parameters.put("idTabla", id);
		parameters.put("idTipos", listaTipos);
		List<Documento> listaDocumentos = (List<Documento>) crudServiceBean
				.findByNamedQuery(
						Documento.LISTAR_POR_ID_NOMBRE_TABLA_LISTA_TIPO,
						parameters);

		return listaDocumentos;

	}
	
	//Cris F: aumento de método para mostrar todos los documentos de evaluacion social
	public List<Documento> recuperarDocumentosConArchivosPorFlujoTodasVersiones(Long processInstanceId) {
        List<Documento> documentos = recuperarDocumentosPorFlujo(processInstanceId);
        Map<String, Documento> documentosFiltrados = new HashMap<String, Documento>();
        int i = 1;
        for (Documento doc : documentos) {
        	if (!documentosFiltrados.containsKey(doc.getNombre()))
                documentosFiltrados.put(doc.getNombre(), doc);
            else { 
                Integer maxId = documentosFiltrados.get(doc.getNombre()).getId();
                if (doc.getId() > maxId) {
                    documentosFiltrados.put(doc.getNombre() + "_" + i, doc);
                    i++;
                }
            }        		            
        }

        List<Documento> documentosResult = new ArrayList<Documento>();
        for (String nombreDocumento : documentosFiltrados.keySet()) {
            Documento doc = documentosFiltrados.get(nombreDocumento);
            if (doc != null && doc.getIdAlfresco() != null && !doc.getIdAlfresco().isEmpty()) {
                try {
                    byte[] archivoObtenido = descargar(doc.getIdAlfresco(), doc.getFechaCreacion());
//                	byte[] archivoObtenido = descargar(doc.getIdAlfresco());
                    doc.setContenidoDocumento(archivoObtenido);
                    documentosResult.add(doc);
                } catch (CmisAlfrescoException e) {
                    LOG.error("Error al descargar el documento del alfresco", e);
                }

            }
        }

        Collections.sort(documentosResult, new Comparator<Documento>() {

            @Override
            public int compare(Documento o1, Documento o2) {
                if (o1.getFechaCreacion().before(o2.getFechaCreacion()))
                    return 1;
                if (o1.getFechaCreacion().after(o2.getFechaCreacion()))
                    return -1;
                return 0;
            }
        });

        return documentosResult;
    }

	public DocumentosTareasProceso guardarDocumentoTareaProceso(Documento documento,Long processInstanceId,Long idTarea) {

		DocumentosTareasProceso tareasProceso = new DocumentosTareasProceso();
		tareasProceso.setProcessInstanceId(processInstanceId);
		tareasProceso.setIdTarea(idTarea!=null?idTarea:0);		
		tareasProceso.setDocumento(documento);
		crudServiceBean.saveOrUpdate(tareasProceso);

		return tareasProceso;
	}
	
	private Documento obtenerDocumentoPorWorkspace(String workspace){
		try {			
			Documento documento = documentosService.getDocumentoPorWorkSpace(workspace);
			return documento;			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String direccionDescarga(String idDoc, Date fechaDoc) throws CmisAlfrescoException {
        return alfrescoServiceBean.getWorkspaceByObjectId(idDoc, fechaDoc);
    }
	
	public List<Documento> getDocumentosPorIdHistoricoNotificacion(Integer idHistorico, Integer numeroNotificacion) {
		return documentosService.getDocumentosPorIdHistoricoNotificacion(idHistorico, numeroNotificacion);
	}

	public List<Documento> documentosTodosXTablaIdXIdDoc(Integer idTabla, String nombreTabla,
            TipoDocumentoSistema tipoDocumento) {
		return documentosService.documentosTodosXTablaIdXIdDoc(idTabla, nombreTabla, tipoDocumento);
	}
	
	public List<Documento> getDocumentosPorIdHistorico(Integer idHistorico) {
        return documentosService.getDocumentosPorIdHistorico(idHistorico);
    }
	public List<Documento> listarTodoDocumentosXTablaId(Integer idTabla, String nombreTabla) {
		return documentosService.listarTodoDocumentosXTablaId(idTabla, nombreTabla);
	}

	public String descargaHidrocarburos(String nombreDoc) throws CmisAlfrescoException {
        return alfrescoServiceBean.downloadDocument(nombreDoc);
    }
	
	public String getWorkspaceByDocumentName(String nombreDoc) throws CmisAlfrescoException {
        return alfrescoServiceBean.getWorkspaceByDocumentName(nombreDoc);
    }
	
	@SuppressWarnings("unchecked")
	public List<Documento> recuperarHistorialDocumentosPorTipo(Integer id,
			String nombre, List<Integer> listaTipos) throws Exception {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nombreTabla", nombre);
		parameters.put("idTabla", id);
		parameters.put("idTipos", listaTipos);
		List<Documento> listaDocumentos = (List<Documento>) crudServiceBean
				.findByNamedQuery(
						Documento.LISTAR_HISTORIAL_POR_ID_NOMBRE_TABLA_LISTA_TIPO,
						parameters);

		return listaDocumentos;

	}	
	
	 public File descargarFileAnterior(String idDocAlfresco) throws CmisAlfrescoException, MalformedURLException, IOException {
		idDocAlfresco = (idDocAlfresco.split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByIdFile(idDocAlfresco);
	}
	 
	public byte[] descargarContenido(String idDocAlfresco) throws CmisAlfrescoException {
		idDocAlfresco = (idDocAlfresco.split(";"))[0];
		byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
		return archivo;
	}
	
	
	public List<Documento> recuperarDocumentosConArchivosPorFlujoTodasVersionesTipo(Long processInstanceId, int tipo) {
        List<Documento> documentos = recuperarDocumentosPorFlujo(processInstanceId);
        
        List<Documento> documentosFiltrados = new ArrayList<Documento>();
        
        for (Documento doc : documentos) {
        	if(doc.getTipoDocumento().getId().equals(tipo)){
        		documentosFiltrados.add(doc);
        	}
        }

        List<Documento> documentosResult = new ArrayList<Documento>();
        for (Documento doc : documentosFiltrados) {
            if (doc != null && doc.getIdAlfresco() != null && !doc.getIdAlfresco().isEmpty()) {
                try {
                    byte[] archivoObtenido = descargar(doc.getIdAlfresco(), doc.getFechaCreacion());
                    doc.setContenidoDocumento(archivoObtenido);
                    documentosResult.add(doc);
                } catch (CmisAlfrescoException e) {
                	try {
                		byte[] archivoObtenido = descargarAnterior(doc.getIdAlfresco());
                		 doc.setContenidoDocumento(archivoObtenido);
                         documentosResult.add(doc);
					} catch (Exception e2) {
						LOG.error("Error al descargar el documento del alfresco", e);
					}
                }

            }
        }       
        return documentosResult;
    }
	
	public Integer findDocuIdByDocuTableId(Integer docuTableId) {
	    String jpql = "SELECT d.id FROM Documento d WHERE d.idTable = :docuTableId ORDER BY d.id DESC";
	    Query query = crudServiceBean.getEntityManager().createQuery(jpql);
	    query.setParameter("docuTableId", docuTableId);
	    try {
	        return (Integer) query.getResultList().get(0); // Obtener el primer resultado de la lista
	    } catch (IndexOutOfBoundsException e) {
	        return null; // Devolver null si no hay resultados
	    }
	}
	
	public Long findProcessInstanceIdByDocuId(Integer docuId) {
	    String jpql = "SELECT t.processInstanceId FROM DocumentosTareasProceso t WHERE t.documento.id = :docuId";
	    Query query = crudServiceBean.getEntityManager().createQuery(jpql);
	    query.setParameter("docuId", docuId);
	    try {
	        return (Long) query.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}	
	
	public DocumentosTareasProceso guardarDocumentoProceso(
			DocumentosTareasProceso doc) {
		return crudServiceBean.saveOrUpdate(doc);
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentosTareasProceso> obtenerDocumentosProcesos(Integer idDoc){
		List<DocumentosTareasProceso> lista = new ArrayList<DocumentosTareasProceso>();
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT d from DocumentosTareasProceso d where d.documento.id = :idDoc");
			sql.setParameter("idDoc", idDoc);
			
			lista = (List<DocumentosTareasProceso>)sql.getResultList();
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
}
