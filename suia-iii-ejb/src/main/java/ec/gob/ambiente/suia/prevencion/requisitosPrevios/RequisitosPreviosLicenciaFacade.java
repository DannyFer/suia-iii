/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.requisitosPrevios;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.catalogocategorias.facade.CatalogoCategoriasFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.ViabilidadTecnicaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author jgras
 * @version Revision: 1.0
 *          <p>
 *          [Autor: jgras, Fecha: 09/07/2015]
 *          </p>
 */

@Stateless
public class RequisitosPreviosLicenciaFacade {

//    private static final Logger LOG = Logger.getLogger(RequisitosPreviosLicenciaFacade.class);

    @EJB
    private CertificadoInterseccionFacade certificadoInterseccionFacade;

    @EJB
    MensajeNotificacionFacade variableFacade;

    @EJB
    private CatalogoCategoriasFacade catalogoCategoriasFacade;

    @EJB
    private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

    @EJB
    private ViabilidadTecnicaFacade viabilidadTecnicaFacade;

    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    AreaFacade areaFacade;

    @EJB
    private DocumentosFacade documentosFacade;
    
    @EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;

    public Boolean proyectoNoFactible(ProyectoLicenciamientoAmbiental proyecto) {
        return intersecaSNAP(proyecto) && proyecto.getTipoSector().getId() == TipoSector.TIPO_SECTOR_MINERIA;
    }

    public Boolean requiereRequisitosPrevios(ProyectoLicenciamientoAmbiental proyecto) {
        return requiereRequisitosTecnicos(proyecto) || requiereViabilidadTecnica(proyecto) || intersecaAreasProtegidas(proyecto);
    }

    //*** tambien para algunas actividades. Requiere aprobacion de requisitos tecnicos si el proyecto Gestiona o Transporta desechos Peligrosos y/o Especiales
    public Boolean requiereRequisitosTecnicos(ProyectoLicenciamientoAmbiental proyecto) {
        return proyecto.getGestionaDesechosPeligrosos() || proyecto.getTransporteSustanciasQuimicasPeligrosos();
    }

    private boolean requiereViabilidadTecnica(ProyectoLicenciamientoAmbiental proyecto) {
        return proyecto.getCatalogoCategoria().getRequiereViabilidad();
    }

    public Boolean intersecaAreasProtegidas(ProyectoLicenciamientoAmbiental proyecto) {
        Map<String, Boolean> capasInterseca = certificadoInterseccionFacade.getCapasInterseccionBoolean(proyecto.getCodigo());
        return capasInterseca.get(Constantes.CAPA_SNAP) || capasInterseca.get(Constantes.CAPA_BP) || capasInterseca.get(Constantes.CAPA_RAMSAR_AREA) || capasInterseca.get(Constantes.CAPA_RAMSAR_PUNTO)|| capasInterseca.get(Constantes.CAPA_PFE);
    }

    private Boolean intersecaSNAP(ProyectoLicenciamientoAmbiental proyecto) {
        Map<String, Boolean> capasInterseca = certificadoInterseccionFacade.getCapasInterseccionBoolean(proyecto.getCodigo());
        return capasInterseca.get(Constantes.CAPA_SNAP) || capasInterseca.get(Constantes.CAPA_RAMSAR_AREA) || capasInterseca.get(Constantes.CAPA_RAMSAR_PUNTO) ;
    }

    public void iniciarProcesoRequisitosPrevios(Usuario usuario, ProyectoLicenciamientoAmbiental proyecto,
                                                Boolean esParaLicenciamiento) throws ServiceException {
        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        boolean requiereRequisitosTecnicos = requiereRequisitosTecnicos(proyecto);
        boolean requiereViabilidadTecnica = requiereViabilidadTecnica(proyecto);

        params.put(Constantes.ID_PROYECTO, proyecto.getId());
        params.put("codigoProyecto", proyecto.getCodigo());
        if (proyecto.getCatalogoCategoria().getCategoria().toString().equals("Categoría I")){
        	params.put("esCertificadoAmbiental", true);
        }else{
        	params.put("esLicenciaAmbiental", esParaLicenciamiento);
        }
        params.put("reqAprobacionRequisitosTec",requiereRequisitosTecnicos);

        //Intersecciones
        Map<String, Boolean> capasInterseca = certificadoInterseccionFacade.getCapasInterseccionBoolean(proyecto.getCodigo());
        Boolean intersecaSNAP = capasInterseca.get(Constantes.CAPA_SNAP);
        Boolean intersecaBP = capasInterseca.get(Constantes.CAPA_BP);
        Boolean intersecaRAMSARAREA = capasInterseca.get(Constantes.CAPA_RAMSAR_AREA);
        Boolean intersecaRAMSARPUNTO = capasInterseca.get(Constantes.CAPA_RAMSAR_PUNTO);
        Boolean intersecaPFE = capasInterseca.get(Constantes.CAPA_PFE);
        if (intersecaRAMSARAREA || intersecaRAMSARPUNTO ){
        	params.put("intersecaSNAP", true);
        }else{
        	params.put("intersecaSNAP", intersecaSNAP);
        }
        
       
        if(intersecaBP || intersecaPFE){
        params.put("intersecaBP", true);
        }else {
        	 params.put("intersecaBP", intersecaBP);
        	 params.put("intersecaPFE", intersecaPFE);
		}
        params.put("intersecaRAMSARAREA", intersecaRAMSARAREA);
        params.put("intersecaRAMSARPUNTO", intersecaRAMSARPUNTO);

        //Es proyecto minero
        Boolean esProyectoMinero = TipoSector.TIPO_SECTOR_MINERIA == proyecto.getTipoSector().getId();
        params.put("esProyectoMinero", esProyectoMinero);

        //Usuarios
        params.put("u_Promotor", proyecto.getUsuario().getNombre());

        if (intersecaSNAP || intersecaBP || intersecaRAMSARAREA || intersecaRAMSARPUNTO || intersecaPFE) {
            if (intersecaSNAP || intersecaRAMSARAREA || intersecaRAMSARPUNTO) {
                Area areaBiodiversidad;
                if (proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("PC")) {
                    areaBiodiversidad = areaFacade.getAreaSiglas("DNB");
                } else {
                    areaBiodiversidad = proyecto.getAreaResponsable(); //Ver como buscar el área de biodiversidad para las provincias
                }
                String usrResponsablePronunciamientoBiodiversidad = areaFacade.getUsuarioPorRolArea("role.responsable.pronunciamiento.biodiversidad", areaBiodiversidad).getNombre();
                params.put("u_ResponsablePronunciamientoBiodiversidad", usrResponsablePronunciamientoBiodiversidad);
                String userResponsable=usrResponsablePronunciamientoBiodiversidad;
                if(usuarioFacade.buscarJefeInmediatoSuperior(usrResponsablePronunciamientoBiodiversidad) !=null){
                    userResponsable =  usuarioFacade.buscarJefeInmediatoSuperior(usrResponsablePronunciamientoBiodiversidad).getNombre();
                }
                params.put("u_SupervisorBiodiversidad", userResponsable);
            }
            if (intersecaBP || intersecaPFE) {
                Area areaForestal;
                if (proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("PC")) {
                    areaForestal = areaFacade.getAreaSiglas("DNF");
                } else {
                    areaForestal = proyecto.getAreaResponsable(); //Ver como buscar el área de forestal para las provincias
                }
                String usrResponsablePronunciamientoForestal = areaFacade.getUsuarioPorRolArea("role.responsable.pronunciamiento.Forestal", areaForestal).getNombre();
                params.put("u_ResponsablePronunciamientoForestal", usrResponsablePronunciamientoForestal);
                 String userResponsable=usrResponsablePronunciamientoForestal;
                if(usuarioFacade.buscarJefeInmediatoSuperior(usrResponsablePronunciamientoForestal)!=null){
                    userResponsable=usuarioFacade.buscarJefeInmediatoSuperior(usrResponsablePronunciamientoForestal).getNombre();
                }
                params.put("u_SupervisorForestal", userResponsable);
            }
        }

        //Mensajes del proceso
        params.put("bodyNotificacionNoFavorable", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionReqTecNoFavorable",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionReqTecFavorable", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionReqTecFavorable",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionAprobacionReq", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionAprobacionReq",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyCancelacionSNAPMinero", variableFacade.recuperarValorMensajeNotificacion("bodyCancelacionSNAPMinero",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionAtrazoInformeInspecion", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionAtrazoInformeInspecion",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionInterseccionZP", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionInterseccionZP",
                new Object[]{proyecto.getCodigo()}));

        try {
            //Si requiere Requisitos Técnicos se optienen las variables
            if (requiereRequisitosTecnicos) {
                Map<String, Object> variablesRequisitosTecnicos = aprobacionRequisitosTecnicosFacade.obtenerVariablesAprobacionRequisitosTecnicosRequisitosPrevios(
                        usuario, proyecto.getCodigo(), proyecto.getTipoEstudio().getId(), proyecto.getPrimeraProvincia().getCodificacionInec(),
                        !esParaLicenciamiento, "ec.gob.ambiente.suia.tramiteresolver.AprobacionRequisitosTecnicosTramiteResolver");
                params.putAll(variablesRequisitosTecnicos);
            }

            //Si requiere Viabilidad Técnica se optienen las variables
            if (requiereViabilidadTecnica) {
                Map<String, Object> variablesViabilidadTecnica = viabilidadTecnicaFacade.obtenerVariablesViabilidadTenicaRequisitosPrevios(usuario, proyecto);
                params.putAll(variablesViabilidadTecnica);
            }

            //Inicio del proceso de Requisitos Previos
            try {
            long process=procesoFacade.iniciarProceso(usuario, Constantes.NOMBRE_PROCESO_REQUISITOS_PREVIOS, proyecto.getCodigo(), params);
            	procesoFacade.envioSeguimientoLicenciaAmbiental(usuario, process);	
			} catch (Exception e) {
				// TODO: handle exception
			}
            
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public Documento adjuntarDocumentoRequisitosPrevios(File file, Integer id, String codProyecto, long idProceso, long idTarea,
                                                        TipoDocumentoSistema tipoDocumento) throws Exception {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        Documento documento1 = new Documento();
        documento1.setIdTable(id);
        String ext = getExtension(file.getAbsolutePath());
        documento1.setNombre(file.getName());
        documento1.setExtesion(ext);
        documento1.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
        documento1.setMime(mimeTypesMap.getContentType(file));
        documento1.setContenidoDocumento(data);
        DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
        documentoTarea.setIdTarea(idTarea);
        documentoTarea.setProcessInstanceId(idProceso);
        return documentosFacade.guardarDocumentoAlfresco(codProyecto,
                Constantes.CARPETA_REQUISITOS_PREVIOS, idProceso, documento1, tipoDocumento,
                documentoTarea);
    }

    private String getExtension(String fullPath) {
        String extension = "";
        int i = fullPath.lastIndexOf('.');
        if (i > 0) {
            extension = fullPath.substring(i + 1);
        }
        return extension;
    }

    public File descargarDocumentoRequisitosPrevios(Integer idProyecto, TipoDocumentoSistema idTipoDocumento) throws CmisAlfrescoException, IOException {
        List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(idProyecto, ProyectoLicenciamientoAmbiental.class.getSimpleName(), idTipoDocumento);
        if (documentos.isEmpty()) {
            return null;
        } else {
            Documento descripcionProcesoDoc = documentos.get(0);
            byte[] content = documentosFacade.descargar(descripcionProcesoDoc.getIdAlfresco());
            File tempFile = File.createTempFile(descripcionProcesoDoc.getNombre(), "." + descripcionProcesoDoc.getExtesion());
            FileOutputStream fos = new FileOutputStream(tempFile.getAbsolutePath());
            fos.write(content);
            fos.close();
            return tempFile;
        }
    }

    public void adjuntarCertificadoViabilidad(File file, ProyectoLicenciamientoAmbiental proyecto, long idProceso, long idTarea)
            throws ServiceException, CmisAlfrescoException {
        Documento documento = new Documento();
        byte[] data;
        try {
            data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            documento.setContenidoDocumento(data);
            documento.setDescripcion("Certificado viabilidad");
            documento.setTipoContenido("application/pdf");
            documento.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
            documento.setIdTable(proyecto.getId());
            documento.setNombre("Certificado_Viabilidad.pdf");
            DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
            documentoTarea.setIdTarea(idTarea);
            documentoTarea.setProcessInstanceId(idProceso);
            documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigo(),
                    Constantes.CARPETA_REQUISITOS_PREVIOS, 0L, documento,
                    TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD, documentoTarea);
        } catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    public byte[] getAlfrescoMapa(ProyectoLicenciamientoAmbiental proyecto) throws ServiceException, CmisAlfrescoException {
        return certificadoInterseccionFacade.getAlfrescoMapaCertificadoInterseccion(proyecto);
    }

    public byte[] getAlfrescoOficio(ProyectoLicenciamientoAmbiental proyecto) throws ServiceException, CmisAlfrescoException {
        return certificadoInterseccionFacade.getAlfrescoOficioCertificadoInterseccion(proyecto);
    }

    public byte[] descargarCoordenadas(ProyectoLicenciamientoAmbiental proyecto) throws ServiceException, CmisAlfrescoException {
        return certificadoInterseccionFacade.descargarCoordenadasCertificadoInterseccion(proyecto);
    }
    
    public String direccionDescarga(Documento documento) throws CmisAlfrescoException {
		String workspace = (documento.getIdAlfresco().split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace);
	}

}