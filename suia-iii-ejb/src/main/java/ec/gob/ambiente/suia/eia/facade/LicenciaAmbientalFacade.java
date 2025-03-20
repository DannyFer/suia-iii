/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.eia.facade;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.catalogocategorias.facade.CatalogoCategoriasFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.LicenciaAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.VehiculoDesechoSanitarioProcesoPma;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.iniciadorproceso.clases.ParticipacionSocialPIClass;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ResourcesUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author jgras
 * @version Revision: 1.0
 *          <p>
 *          [Autor: jgras, Fecha: 22/08/2015]
 *          </p>
 */
@Stateless
public class LicenciaAmbientalFacade {

    private static final Integer ID_DIRECCION_ADMINISTRATIVA = 506;
    private static final Logger LOG = Logger.getLogger(LicenciaAmbientalFacade.class);
    @EJB
    DocumentosFacade documentosFacade;
    @EJB
    MensajeNotificacionFacade variableFacade;
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private AreaFacade areaFacade;
    @EJB
    private CertificadoInterseccionFacade certificadoInterseccionService;
    @EJB
    private CatalogoCategoriasFacade catalogoCategoriasFacade;
    
    @EJB
    private AsignarTareaFacade asignarTareaFacade;

    public void iniciarProcesoLicenciaAmbiental(Usuario usuario,
                                                ProyectoLicenciamientoAmbiental proyecto) throws ServiceException {

        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        params.put(Constantes.ID_PROYECTO, proyecto.getId());
        params.put("codigoProyecto", proyecto.getCodigo());
        params.put("tramite", proyecto.getCodigo());
        params.put("codigoSector", proyecto.getTipoSector().getId().toString());
        params.put("codigoSectorHidrocarburo", "1");
        params.put("cantidadNotificaciones", 0);
        params.put("esActualizacionFinLic", true); //Para ir al proceso de Participación social

        //Usuarios
        String usrTecnicoPronunciamiento;
        String usrCoordinador; //Coordinador de la DNPCA/Calidad Ambiental
        String usrDirectorDPNCA = ""; //Director de la DNPCA
        String usrDirectorFinanciero = areaFacade.getDirectorPlantaCentralPorArea("role.pc.director.financiero").getNombre(); //Dirección Financiera
        String usrCoordinadorJuridico = "";
        String usrAsesorJuridico = "";
        String usrMaximaAutoridad;
        String usrDirectorForestal;

        String usrDirectorBiodiversidad = "";
        String usrSubsecretaria;
        Area areaProyecto = areaFacade.getArea(proyecto.getAreaResponsable().getId());
        String roleType = "role.area.tecnico";
        Usuario coordinador = null;
        List<Usuario> coordinadores=null;
        //Para Area Planta Central
        if (areaProyecto.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
           // coordinador = areaFacade.getCoordinadorPorSector(proyecto.getTipoSector());
        	String rolCoordinador = Constantes.getRoleAreaName("role.pc.coordinador.area." + proyecto.getTipoSector().getId().toString());
            coordinadores=asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolCoordinador,areaProyecto.getAreaName());
            coordinador=coordinadores.get(0);
            roleType = "role.pc.tecnico.area." + proyecto.getTipoSector().getId().toString();
            roleType = "role.pc.tecnico.area." + proyecto.getTipoSector().getId().toString();
            usrMaximaAutoridad = areaFacade.getMinistra().getNombre(); //Ministra
            usrDirectorForestal = areaFacade.getDirectorPlantaCentralPorArea("role.pc.director.forestal").getNombre(); //Director forestal
            usrSubsecretaria = areaFacade.getDirectorPlantaCentral("role.area.subsecretario.calidad.ambiental").getNombre(); //Subsecretaria
            usrCoordinador = coordinador.getNombre();

            usrDirectorBiodiversidad = areaFacade.getDirectorPlantaCentralPorArea("role.pc.director.biodiversidad").getNombre();
            usrCoordinadorJuridico = areaFacade.getUsuarioPorRolArea("role.pc.coordinador.Juridico", areaFacade.getAreaSiglas("CGAJ")).getNombre(); //Coordinador Jurídico
            usrAsesorJuridico = areaFacade.getUsuarioPlantaCentralPorArea("role.pc.asesor.juridico").getNombre(); //Asesor jurídico
            usrDirectorDPNCA = areaFacade.getDirectorPrevencionPlantaCentral().getNombre();


        }
        //Para otras Areas
        else {
            coordinador = areaFacade.getCoordinadorProvincial(areaProyecto);
            usrCoordinador = coordinador.getNombre();
            usrDirectorForestal = areaFacade.getUsuarioPorRolArea("role.director.forestal", areaProyecto).getNombre();
            /**
             * Nombre:SUIA
             * Descripción: Validación para que no pida el rol biodiversidad para entes acreditados
             * ParametrosIngreso:
             * PArametrosSalida:
             * Fecha:16/08/2015
             */

            if (!(areaProyecto.getTipoArea().getId().equals(3))) {
                usrDirectorBiodiversidad = areaFacade.getUsuarioPorRolArea("role.director.biodiversidad", areaProyecto).getNombre();
            }
            /**
             * FIN Validación para que no pida el rol biodiversidad para entes acreditados
             */
            //areaFacade.getCoordinadorProvincial(areaProyecto).getNombre(); //Coordinador Patrimonio Natural (por Director Forestal)
            Area areaUsuarios = areaProyecto;
            if(areaProyecto.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT)) 
            	areaUsuarios = areaProyecto.getArea(); //zonal
            
            usrMaximaAutoridad = areaFacade.getDirectorProvincial(areaUsuarios).getNombre(); //Director Provincial (por ministra)
            usrDirectorDPNCA = usrMaximaAutoridad;
            usrSubsecretaria = usrMaximaAutoridad; //Director Provincial (por Subsecretaria)

            usrCoordinadorJuridico = areaFacade.getUsuarioPorRolArea("role.area.coordinador.Juridico", areaUsuarios).getNombre();
            usrAsesorJuridico = areaFacade.getUsuarioPorRolArea("role.area.asesor.juridico", areaUsuarios).getNombre(); //Asesor jurídico

            usrDirectorFinanciero = areaFacade.getUsuarioPorRolArea("role.pc.director.financiero", areaUsuarios).getNombre(); //Dirección Financiera


        }
        params.put("u_Promotor", proyecto.getUsuario().getNombre());
        params.put("u_Coordinador", usrCoordinador);
        params.put("u_DirectorDPNCA", usrDirectorDPNCA);
        params.put("u_DirectorForestal", usrDirectorForestal);
        params.put("u_DirectorFinanciero", usrDirectorFinanciero);
        params.put("u_Subsecretaria", usrSubsecretaria);
        params.put("u_CoordinadorJuridico", usrCoordinadorJuridico);
        params.put("u_AsesorJuridico", usrAsesorJuridico);
        params.put("u_MaximaAutoridad", usrMaximaAutoridad);

        //Intersecciones
        Boolean interseca = certificadoInterseccionService.getValueInterseccionProyectoIntersecaCapas(proyecto);
        Boolean intersecaSNAP_BP_PFE = false;
        Boolean intersecaZA = false;
        Boolean intersecaBP = false;
        Boolean intersecaSNAP = false;
        Boolean intersecaTagaeriTaromenane = false;
        List<String> areasPronunciamiento = new ArrayList<String>();
        List<String> areasPronunciamientoTDR = new ArrayList<String>();
        Boolean forestalActivo = false;
        String informeFB = "";
        if (interseca) {
            Map<String, Boolean> capasInterseca = certificadoInterseccionService.getCapasInterseccionBoolean(proyecto.getCodigo());
            intersecaSNAP_BP_PFE = capasInterseca.get(Constantes.CAPA_SNAP) || capasInterseca.get(Constantes.CAPA_PFE) || capasInterseca.get(Constantes.CAPA_BP);
            intersecaSNAP = capasInterseca.get(Constantes.CAPA_SNAP);
            intersecaBP = capasInterseca.get(Constantes.CAPA_BP);
            intersecaZA = capasInterseca.get(Constantes.CAPAS_AMORTIGUAMIENTO);

            String detalle = certificadoInterseccionService.getDetalleInterseccion(proyecto.getCodigo());

            if (detalle.toLowerCase().contains("taromenane") || detalle.toLowerCase().contains("Tagaeri")) {
                intersecaTagaeriTaromenane = true;
            }
            //Asignar técnico de pronunciamiento
            if (intersecaSNAP) {
                usrTecnicoPronunciamiento = areaFacade.getUsuarioPlantaCentralPorArea("role.pc.tecnico.Biodiversidad").getNombre(); //TÉCNICO BIODIVERSIDAD
                params.put("u_TecnicoPronunciamiento", usrTecnicoPronunciamiento);

                params.put("u_DirectorForestal", usrDirectorForestal);
                params.put("u_DirectorBiodiversidad", usrDirectorBiodiversidad);
                params.put("u_CoordinadorBiodiversidad", usrDirectorBiodiversidad);
                areasPronunciamiento.add("Biodiversidad");
                areasPronunciamientoTDR.add("Biodiversidad");
                informeFB = "Biodiversidad";
            }
            if (intersecaBP) {
                forestalActivo = true;
                usrTecnicoPronunciamiento = areaFacade.getUsuarioPlantaCentralPorArea("role.pc.tecnico.Forestal").getNombre(); //TÉCNICO FORESTAL
                params.put("u_TecnicoPronunciamiento", usrTecnicoPronunciamiento);

                params.put("u_CoordinadorForestal", usrDirectorForestal);
                areasPronunciamiento.add("Forestal");
                areasPronunciamientoTDR.add("Forestal");
                informeFB = informeFB.isEmpty() ? "Forestal" : informeFB + "/Forestal";
            }
        }

        if (!forestalActivo) {
            informeFB = informeFB.isEmpty() ? "Forestal" : informeFB + "/Forestal";
            params.put("u_CoordinadorForestal", usrDirectorForestal);
            areasPronunciamiento.add("Forestal");
            // informeFB ="Forestal";
        }
        params.put("interseca", interseca);
        params.put("intersecaSNAP_BP_PFE", intersecaSNAP_BP_PFE);
        params.put("intersecaZA", intersecaZA);
        params.put("intersecaSNAP", intersecaSNAP);
        params.put("intersecaBP", intersecaBP);
        String[] listaArea = new String[areasPronunciamiento.size()];
        String[] listaAreaTDR = new String[areasPronunciamientoTDR.size()];
        Integer contador = 0;
        for (String area : areasPronunciamiento) {
            listaArea[contador++] = area;
        }
        contador = 0;
        for (String area : areasPronunciamientoTDR) {
            listaAreaTDR[contador++] = area;
        }
        params.put("listaAreasEquipoForestal", listaArea);
        params.put("listaAreasEquipoForestalTDR", listaAreaTDR);
        //variables de mensajes del proceso
        params.put("bodyNotificacionForestal", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionForestalLA",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionNoFavorable", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionReqTecNoFavorable",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionPronunciamiento", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionPronunciamientoLA",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionAprobacionReq", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionAprobacionReq",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyRequerimientoViabilidadTecnica", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionReqTecFavorable",
                new Object[]{proyecto.getCodigo()}));

        params.put("bodyNotificacionPronunciamientoLAForestalBiodiversidad", variableFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionPronunciamientoLAForestalBiodiversidad",
                new Object[]{proyecto.getCodigo(), informeFB}));
//-----
        params.put("bodyNotificacionRetrasoRevisarEIA", variableFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionRetrasoRevisarEIA",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionRetrasoInformeOficioEIA", variableFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionRetrasoInformeOficioEIA",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionRetrasoRevisarInformeOficioEIA", variableFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionRetrasoRevisarInformeOficioEIA",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionRetrasoCorregirInformeOficioEIA", variableFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionRetrasoCorregirInformeOficioEIA",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionRetrasoRevisarOficioAprovacionEIA", variableFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionRetrasoRevisarOficioAprovacionEIA",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionRetrasoRevisarLAEIA", variableFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionRetrasoRevisarLAEIA",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionRetrasoRevisarJuridicoLAEIA", variableFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionRetrasoRevisarJuridicoLAEIA",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionRetrasoRevisarCoordinadorJuridicoLAEIA", variableFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionRetrasoRevisarCoordinadorJuridicoLAEIA",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionDirectorLAEIA", variableFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionDirectorLAEIA",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionRetrasoRevisarAsesorJuridicoLAEIA", variableFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionRetrasoRevisarAsesorJuridicoLAEIA",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionCordinadorJuridicoRegistroLAEIA", variableFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionCordinadorJuridicoRegistroLAEIA",
                new Object[]{proyecto.getCodigo()}));


        //Requiere aprobacion: Si el proyecto Gestiona o Transporta desechos Peligrosos y/o Especiales
        Boolean requiereAproReq = proyecto.getGestionaDesechosPeligrosos() || proyecto.getTransporteSustanciasQuimicasPeligrosos();
        params.put("requiereAproRequisitosTecnicos", requiereAproReq);

        //Requiere viabilida técnica
        System.out.println("Requiere viabilidad: " + requiereViabilidadTecnica(proyecto.getCatalogoCategoria()).toString());
        params.put("requiereViabilidadTecnica", requiereViabilidadTecnica(proyecto.getCatalogoCategoria()));
        params.put("iniciarPSClass", ParticipacionSocialPIClass.class.getName());
        params.put("serviceConfigurarProcesoURL", Constantes.getStartProcessServiceURL()
        );
        //Boolean esEnte = areaProyecto.getTipoArea().getId() == 3;
        Boolean esEnte = false;
        params.put("esEnte", esEnte);

        params.put("requestEncodeRest", "application/x-www-form-urlencoded;charset=UTF-8");
//        params.put("requestBody", configurarPeticion(ResourcesUtil.getRoleAreaName(roleType), coordinador.getArea().getAreaName()));
        params.put("requestBody", configurarPeticion(ResourcesUtil.getRoleAreaName(roleType), areaProyecto.getAreaName()));

        params.put("requiereOficioOG", intersecaTagaeriTaromenane);//si interseca con zonas de amortiguamiento
        params.put("requiereHidrPronForBio", intersecaBP || intersecaSNAP);// si interseca con el SNAP o BP

        params.put("urlAsignacionAutomatica", Constantes.getWorkloadServiceURL());
        
        try {
            long process= procesoFacade.iniciarProceso(usuario, Constantes.NOMBRE_PROCESO_LICENCIA_AMBIENTAL,
                    proyecto.getCodigo(), params);
            procesoFacade.envioSeguimientoLicenciaAmbiental(usuario, process);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void archivarProcesoLicenciaAmbiental(LicenciaAmbiental licencia) {
        licencia.setRealizoEncuesta(false);
        crudServiceBean.saveOrUpdate(licencia);
    }

    public Documento ingresarPronunciamiento(File file, Integer id, String codProyecto, long idProceso, long idTarea,
                                             TipoDocumentoSistema tipoDocumento, String nombreClase) throws Exception {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        Documento documento1 = new Documento();
        documento1.setIdTable(id);
        String ext = getExtension(file.getAbsolutePath());
        documento1.setNombre(file.getName());
        documento1.setExtesion(ext);
        documento1.setNombreTabla(nombreClase);
        documento1.setMime(mimeTypesMap.getContentType(file));
        documento1.setContenidoDocumento(data);
        DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
        documentoTarea.setIdTarea(idTarea);
        documentoTarea.setProcessInstanceId(idProceso);
        return documentosFacade.guardarDocumentoAlfresco(codProyecto,
                "LicenciaAmbiental", idProceso, documento1, tipoDocumento,
                documentoTarea);
    }


    public Documento ingresarPronunciamiento(File file, Integer id, String codProyecto, long idProceso, long idTarea,
                                             TipoDocumentoSistema tipoDocumento) throws Exception {
        return ingresarPronunciamiento(file, id, codProyecto, idProceso, idTarea, tipoDocumento, VehiculoDesechoSanitarioProcesoPma.class.getSimpleName());
    }

    public Documento ingresarPronunciamiento(Documento documento, Integer id, String codProyecto, long idProceso, long idTarea,
                                             TipoDocumentoSistema tipoDocumento) throws Exception {
        Documento documento1 = new Documento();
        documento1.setIdTable(id);
        String ext = getExtension(documento.getExtesion());
        documento1.setNombre(documento.getNombre());
        documento1.setExtesion(ext);
        documento1.setNombreTabla(documento.getNombreTabla());
        documento1.setMime(documento.getMime());
        documento1.setContenidoDocumento(documento.getContenidoDocumento());
        DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
        documentoTarea.setIdTarea(idTarea);
        documentoTarea.setProcessInstanceId(idProceso);
        return documentosFacade.guardarDocumentoAlfresco(codProyecto,
                "LicenciaAmbiental", idProceso, documento1, tipoDocumento,
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

    public LicenciaAmbiental obtenerLicenciaAmbientallPorProyectoId(Integer idProyecto) {
        try {
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("idProyecto", idProyecto);

            List<LicenciaAmbiental> lista = crudServiceBean
                    .findByNamedQueryGeneric(
                            LicenciaAmbiental.OBTENER_LICENCIA_POR_PROYECTO,
                            parametros);
            if (lista != null && !lista.isEmpty()) {
                return lista.get(0);
            }
        } catch (Exception e) {

            return null;
        }

        return null;
    }

    public Boolean requiereViabilidadTecnica(CatalogoCategoriaSistema actividad) {
        List<CatalogoCategoriaSistema> categoriasRequierenViabilidad = catalogoCategoriasFacade.buscarCatalogoCategoriasRequierenViabilidad();
        for (CatalogoCategoriaSistema catalogoCategoriaSistema : categoriasRequierenViabilidad) {
            if (catalogoCategoriaSistema.getCodigo().equals(actividad.getCodigo())) {
                return true;
            }
        }
        return false;
    }

    public Documento ingresarDocumentos(File file, Integer id, String codProyecto, long idProceso, long idTarea,
                                        TipoDocumentoSistema tipoDocumento, String nombreClase) throws Exception {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        Documento documento1 = new Documento();
        documento1.setIdTable(id);
        String ext = getExtension(file.getAbsolutePath());
        documento1.setNombre(file.getName());
        documento1.setExtesion(ext);
        documento1.setNombreTabla(nombreClase);
        documento1.setMime(mimeTypesMap.getContentType(file));
        documento1.setContenidoDocumento(data);
        DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
        documentoTarea.setIdTarea(idTarea);
        documentoTarea.setProcessInstanceId(idProceso);
        return documentosFacade.guardarDocumentoAlfresco(codProyecto,
                "LicenciaAmbiental", idProceso, documento1, tipoDocumento,
                documentoTarea);
    }

    public String configurarPeticion(String rol, String area) {
        return "rol=" + rol + "&area=" + area;
    }
}
