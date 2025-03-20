package ec.gob.ambiente.suia.certificadointerseccion.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.autoridadambiental.service.AutoridadAmbientalFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class CertificadoViabilidadAmbientalInterseccionProyectoFacade {

    @EJB
    CertificadoInterseccionFacade certificadoInterseccionFacade;

    @EJB
    ProcesoFacade procesoFacade;

    @EJB
    ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

    @EJB
    DocumentosFacade documentosFacade;

    @EJB
    CategoriaIIFacade categoriaIIFacade;

    @EJB
    AreaFacade areaFacade;

    @EJB
    AutoridadAmbientalFacade autoridadAmbientalService;

    @EJB
    MensajeNotificacionFacade variableFacade;

    public Boolean requiereCertificadoViabilidad(ProyectoLicenciamientoAmbiental proyecto) {
        return certificadoInterseccionFacade.getValueInterseccionProyectoIntersecaCapas(proyecto);
    }

    public Boolean requierePreguntas(ProyectoLicenciamientoAmbiental proyecto){
        Map<String, Boolean> capasInterseca = certificadoInterseccionFacade.getCapasInterseccionBoolean(proyecto.getCodigo());
        return capasInterseca.get(Constantes.CAPA_SNAP) || capasInterseca.get(Constantes.CAPA_BP) || capasInterseca.get(Constantes.CAPA_RAMSAR_PUNTO) || capasInterseca.get(Constantes.CAPA_RAMSAR_AREA);
    }

    public void inicarProceso(ProyectoLicenciamientoAmbiental proyecto, Usuario usuario) throws ServiceException {
        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put(Constantes.ID_PROYECTO, proyecto.getId());
        parametros.put("proponente", usuario.getNombre());
        parametros.put("categoria", proyecto.getCatalogoCategoria().getCategoria().getId());

        //Cuando un proyecto NO es estratégico e intersecta con Bosque Protector o Zona Protegida
        // -> debe ir un responsable de la Dirección de Patrimonio de la provincia respectiva.

        //Cuando un proyecto es estratégico se debe ver dos responsables:
        //intersecta con Bosque Protector
        // -> debe ir un responsable de la Dirección Nacional Forestal (técnico forestal).
        //intersecta con Zona Protegida
        // -> debe ir un responsable de la Dirección Nacional Biodiversidad (técnico de biodiversidad).

        Map<String, Boolean> capasInterseca = certificadoInterseccionFacade.getCapasInterseccionBoolean(proyecto.getCodigo());
        Boolean intersecaSNAP = capasInterseca.get(Constantes.CAPA_SNAP);
        Boolean intersecaBP = capasInterseca.get(Constantes.CAPA_BP);
        Boolean intersecaRAMSARPUNTO = capasInterseca.get(Constantes.CAPA_RAMSAR_PUNTO);
        Boolean intersecaRAMSARAREA = capasInterseca.get(Constantes.CAPA_RAMSAR_AREA);

        //Requiere preguntas al proponente si interseca SNAP o BP
        parametros.put("intersecaSNAP", intersecaSNAP || intersecaBP || intersecaRAMSARPUNTO || intersecaRAMSARAREA);

        String usrTecnicoPronunciamiento;

        //En caso de que interseque SNAP o BP
        if(intersecaSNAP || intersecaBP || intersecaRAMSARPUNTO || intersecaRAMSARAREA ){
            //Para otros sectores
            if(proyecto.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_OTROS)){
                //////////////////////////////////
                parametros.put("u_TecnicoPronunciamiento", usuario.getNombre());
                /////////////////////////////////
            }
            //Para sectores estratégicos
            else {
            	if (intersecaSNAP || intersecaRAMSARPUNTO || intersecaRAMSARAREA) {
            		usrTecnicoPronunciamiento = areaFacade.getUsuarioPorRolArea("role.pc.tecnico.Biodiversidad", proyecto.getAreaResponsable()).getNombre(); //TÉCNICO BIODIVERSIDAD
                    parametros.put("u_TecnicoPronunciamiento", usrTecnicoPronunciamiento);
                } else if (intersecaBP) {
                    usrTecnicoPronunciamiento = areaFacade.getUsuarioPorRolArea("role.pc.tecnico.Forestal", proyecto.getAreaResponsable()).getNombre(); //TÉCNICO FORESTAL
                    parametros.put("u_TecnicoPronunciamiento", usrTecnicoPronunciamiento);
                }
            }
        }

        /*try {
            Usuario director = areaFacade.geSubsecretariaPatrimonioNaturalPorArea(proyecto.getAreaResponsable());
            if (director != null) {
                parametros.put("subSecretariaPN", director.getNombre());
            } else {
                director = areaFacade.geSubsecretariaPatrimonioNatural();
                if (director != null) {
                    parametros.put("subSecretariaPN", director.getNombre());
                }
            }
        } catch (Exception e) {

        }

        if (!parametros.containsKey("subSecretariaPN")) {
            parametros.put("subSecretariaPN",
                    autoridadAmbientalService.getUsuarioSubsecretariaPatrimonioNatural()
                            .getNombre());
        }*/

        //Notificaciones
        parametros.put("bodyNotificacionProponenteInicial", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionProponenteInicial",
                new Object[]{proyecto.getCodigo()}));

        parametros.put("bodyNotificacionSecretariaPNInicial", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionSecretariaPNInicial",
                new Object[]{proyecto.getCodigo()}));

        parametros.put("bodyNotificacionSecretariaPNPronunciamiento", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionSecretariaPNPronunciamiento",
                new Object[]{proyecto.getCodigo()}));

        parametros.put("bodyNotificacionProponenteDenegado", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionProponenteDenegado",
                new Object[]{proyecto.getCodigo()}));

        parametros.put("bodyNotificacionProponenteContinuar", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionProponenteContinuar",
                new Object[]{proyecto.getCodigo()}));

        parametros
                .putAll(categoriaIIFacade.getParametrosIniciarProcesoCategoriaII(usuario.getNombre(), proyecto, true));

        try {
            procesoFacade.iniciarProceso(usuario, Constantes.NOMBRE_PROCESO_CERTIFICAO_VIABILIDAD, proyecto.getCodigo(), parametros);
        } catch (JbpmException e) {
            throw new ServiceException(e.getCause());
        }

    }

    public Documento adjuntarPreguntas(File file, Integer id, String codProyecto, long idProceso, long idTarea,
                                       TipoDocumentoSistema tipoDocumento) throws Exception {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        Documento documento1 = new Documento();
        documento1.setIdTable(id);
        String ext = getExtension(file.getAbsolutePath());
        documento1.setNombre(file.getName());
        documento1.setExtesion(ext);
        documento1.setNombreTabla(EstudioImpactoAmbiental.class.getSimpleName());
        documento1.setMime(mimeTypesMap.getContentType(file));
        documento1.setContenidoDocumento(data);
        DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
        documentoTarea.setIdTarea(idTarea);
        documentoTarea.setProcessInstanceId(idProceso);
        return documentosFacade.guardarDocumentoAlfresco(codProyecto,
                Constantes.CARPETA_CARTIFICADO_INTERSECCION_ALFRESCO, idProceso, documento1, tipoDocumento,
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

    public File descargarRespuestas(Integer idProyecto, TipoDocumentoSistema tipoDocumento) throws CmisAlfrescoException, IOException {
        List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(idProyecto, EstudioImpactoAmbiental.class.getSimpleName(), tipoDocumento);
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

    public ProyectoLicenciamientoAmbiental getVariableProyecto(long idProceso, Usuario usuario) throws JbpmException {
        Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(usuario, idProceso);
        return proyectoLicenciaAmbientalFacade.getProyectoPorCodigo((String) variables.get("idProyecto"));

    }

    public void completarTarea(Long processInstanceID, Long idTarea, Usuario usuario, boolean isPronunciamientoFavorable)
            throws JbpmException {
        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("pronunciamiento_", isPronunciamientoFavorable);
        procesoFacade.aprobarTarea(usuario, idTarea, processInstanceID, parametros);
    }

    public void completarTareaYAdjuntarFile(Long processId, File file, ProyectoLicenciamientoAmbiental proyecto, long idProceso,
                                            long idTarea, Usuario usuario, boolean isPronunciamientoFavorable) throws ServiceException, CmisAlfrescoException {
        subirFileAlfresco(file, proyecto, idProceso, idTarea);
        try {
            completarTarea(processId, idTarea, usuario, isPronunciamientoFavorable);
        } catch (JbpmException e) {
            throw new ServiceException(e.getCause());
        }
    }

    private void subirFileAlfresco(File file, ProyectoLicenciamientoAmbiental proyecto, long idProceso, long idTarea)
            throws ServiceException, CmisAlfrescoException {

        Documento documento = new Documento();

        byte[] data;
        try {
            data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

            documento.setContenidoDocumento(data);
            documento.setDescripcion("Certificado viabilidad");
            documento.setTipoContenido("application/pdf");

            // No se que hace esto
            documento.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
            documento.setIdTable(proyecto.getId());

            documento.setNombre("Certificado_Viabilidad.pdf");
            DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
            documentoTarea.setIdTarea(idTarea);

            documentoTarea.setProcessInstanceId(idProceso);
            documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigo(),
                    Constantes.CARPETA_CARTIFICADO_INTERSECCION_ALFRESCO, 0L, documento,
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
    
    public byte[] descargarCertifiadoViabilidad(ProyectoLicenciamientoAmbiental proyecto) throws ServiceException, CmisAlfrescoException{
    	return certificadoInterseccionFacade.getAlfrescoOficioCertificadoViabilidad(proyecto);
    }

}
