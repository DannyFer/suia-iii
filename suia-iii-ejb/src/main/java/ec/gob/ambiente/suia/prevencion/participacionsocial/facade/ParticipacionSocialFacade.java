/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ReglaFacilitadoresCatalogoCategoriaFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoFacade;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.ParticipacionSocialService;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ResourcesUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author frank torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: frank torres, Fecha: 11/03/2015]
 *          </p>
 */
@Stateless
public class ParticipacionSocialFacade {

    private static final String NOMBRE_PROCESO = "ParticipacionSocial";
    @EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    AlfrescoServiceBean alfrescoServiceBean;
    @EJB
    DocumentosFacade documentosFacade;
    @EJB
    AreaFacade areaFacade;
    @EJB
    UsuarioFacade usuarioFacade;
    @EJB
    ParticipacionSocialService participacionSocialService;
    @EJB
    MensajeNotificacionFacade mensajeNotificacionFacade;
    @EJB
    private ReglaFacilitadoresCatalogoCategoriaFacade reglaFacilitadoresCatalogoCategoriaFacade;

    @EJB
    private CrudServiceBean crudServiceBean;

    public ParticipacionSocialAmbiental guardar(ParticipacionSocialAmbiental participacionSocialAmbiental) {
        return participacionSocialService.guardar(participacionSocialAmbiental);
    }

    /**
     * Eliminar documento duplicado
     *
     * @param nombreTabla el nombre adicionar, puede ser ""
     * @param id
     * @param type
     */
    public void eliminarDocumento(String nombreTabla, Integer id, Integer type) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("nombreTabla", nombreTabla);
        parameters.put("idTabla", id);
        parameters.put("idTipo", type);

        @SuppressWarnings("unchecked")
        List<Documento> documentos = (List<Documento>) crudServiceBean
                .findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA_TIPO,
                        parameters);
        if (!documentos.isEmpty()) {
            for (Documento documento : documentos) {
                documento.setEstado(false);
                crudServiceBean.saveOrUpdate(documento);
            }
        }
    }

    public byte[] recuperarInforme(Integer id) throws Exception {

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("nombreTabla", ParticipacionSocialFacade.NOMBRE_PROCESO);
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

    public byte[] recuperarInforme(Integer id, String nombre) throws Exception {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("nombreTabla", ParticipacionSocialFacade.NOMBRE_PROCESO
                + nombre);
        parameters.put("idTabla", id);
        parameters.put("idTipo", 1);
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

    public Documento recuperarInformeDocumento(Integer id, String nombre)
            throws Exception {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("nombreTabla", ParticipacionSocialFacade.NOMBRE_PROCESO
                + nombre);
        parameters.put("idTabla", id);
        parameters.put("idTipo", 1);
        @SuppressWarnings("unchecked")
        List<Documento> documentos = (List<Documento>) crudServiceBean
                .findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA_TIPO,
                        parameters);
        if (!documentos.isEmpty()) {
            return documentos.get(0);

        }
        return null;
    }

    public String obtenerSecuencial() {
        return crudServiceBean.getSecuenceNextValue(
                ParticipacionSocialFacade.NOMBRE_PROCESO, "suia_iii")
                .toString();
    }


    public Map<String, Object> inicializarParticipacionSocial(ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental) throws ServiceException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(Constantes.VARIABLE_PROCESO_TRAMITE, proyectoLicenciamientoAmbiental.getCodigo());
        data.put("esEstrategico", proyectoLicenciamientoAmbiental.getCatalogoCategoria().getEstrategico());
        data.put("requiereFacilitador", reglaFacilitadoresCatalogoCategoriaFacade.listarAreasPadre(proyectoLicenciamientoAmbiental.getTipoEstudio().getId(),
                proyectoLicenciamientoAmbiental.getCatalogoCategoria().getId()));
        data.put("u_Proponente", proyectoLicenciamientoAmbiental.getUsuario().getNombre());
        data.put("serviceURL", Constantes.getFacilitatorServiceURL());
        data.put("serviceConfigurarProcesoURL", Constantes.getStartProcessServiceURL()
        );
        data.put("configurar", false);
        data.put("urlAsignacionAutomatica", Constantes.getWorkloadServiceURL());
        data.put("inclusionAmbiental", proyectoLicenciamientoAmbiental.getTipoEstudio().getId() == 4);
        data.put(Constantes.ID_PROYECTO, proyectoLicenciamientoAmbiental.getId());

        //Configurar area:"
        Area areaProyecto = proyectoLicenciamientoAmbiental.getAreaResponsable();
        areaProyecto = areaFacade.getAreaFull(areaProyecto.getId());


        ///
        if (areaProyecto == null
                || areaProyecto.getTipoArea().getSiglas()
                .equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
        	Usuario coordinador = new Usuario();
        	//Cris F: cambio para que cuando sea un proyecto de mineria tome el coordinador social de minería
        	if(proyectoLicenciamientoAmbiental.getCatalogoCategoria().getCatalogoCategoriaPublico().getTipoSector().getId() ==2){
        		coordinador = areaFacade.getUsuarioPorRolArea("role.pc.coordinador.Social.mineria", areaProyecto);    		
        	}else{        	
        		coordinador = areaFacade.getUsuarioPorRolArea("role.pc.coordinador.Social", areaProyecto);        		
        	}
        	//fin
        	data.put("u_Coordinador", coordinador.getNombre());
           
            Usuario director = areaFacade.getDirectorPlantaCentral();

            data.put("u_Director", director.getNombre());
            data.put("tipoAreaProyecto", Constantes.SIGLAS_TIPO_AREA_PC);
            data.put("rolTecnico",ResourcesUtil.getRoleAreaName(proyectoLicenciamientoAmbiental.getCatalogoCategoria().getCatalogoCategoriaPublico().getTipoSector().getId() ==2?"role.pc.tecnico.Social.mineria":"role.pc.tecnico.Social"));

//            data.put("requestBody", configurarPeticion(ResourcesUtil.getRoleAreaName("role.pc.tecnico.Social"), coordinador.getArea().getAreaName()));
            data.put("requestBody", configurarPeticion(ResourcesUtil.getRoleAreaName("role.pc.tecnico.Social"), areaProyecto.getAreaName()));
            String equipoMultidisciplinarioSolicitaApoyo = "";
            List<String> areas = new ArrayList<String>();
            areas.add("Social");
            areas.add("Cartografo");
            areas.add("Electrico");
            areas.add("Mineria");
            areas.add("OtrosSectores");
            String separador = "";
            for (String area : areas) {
                equipoMultidisciplinarioSolicitaApoyo += separador + "role.pc.coordinador." + area + "--"
                        + areaProyecto.getId();
                separador = ";;";
                //"Coordinador Muni--256;;as,nbksdbflk--1212;;"
            }
            //"role.pc.coordinador." + areaActual

            data.put("equipoMultidisciplinarioSolicitaApoyo", equipoMultidisciplinarioSolicitaApoyo);

        } else {
 
        	Usuario coordinadorArea = areaFacade.getCoordinadorPorArea(areaProyecto); 

            data.put("u_Coordinador", coordinadorArea.getNombre());
            Usuario directorArea = areaFacade.getDirectorProvincial(areaProyecto);
            data.put("u_Director", directorArea.getNombre());

            data.put("tipoAreaProyecto", areaProyecto.getTipoArea().getSiglas());
            data.put("areaProyectoId", areaProyecto.getId());
//            data.put("rolTecnico", ResourcesUtil.getRoleAreaName("role.area.tecnico"));
//            data.put("areaTecnico", coordinadorArea.getArea().getAreaName());

//            data.put("requestBody", configurarPeticion(ResourcesUtil.getRoleAreaName("role.pc.tecnico.Social"), coordinadorArea.getArea().getAreaName()));
            data.put("requestBody", configurarPeticion(ResourcesUtil.getRoleAreaName("role.pc.tecnico.Social"), areaProyecto.getAreaName()));
            String equipoMultidisciplinarioSolicitaApoyo = "";
            equipoMultidisciplinarioSolicitaApoyo = "role.pc.coordinador.General" + "--"
                    + areaProyecto.getId();
            data.put("equipoMultidisciplinarioSolicitaApoyo", equipoMultidisciplinarioSolicitaApoyo);
        }

        //equipo multidisciplinario
        data.put("equipoMultidisciplinarioDatosAdicionales", configurarEquipoM());

        data.put("publicarPSRequestURL", Constantes.getPublishSPServiceURL() + proyectoLicenciamientoAmbiental.getId());

        data.put("despublicarPSRequestURL", Constantes.getUnPublishSPServiceURL() + proyectoLicenciamientoAmbiental.getId());
        //mensajes
        data.put("bodyNotificacionFacilitadoresAsignadosPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionFacilitadoresAsignadosPS", new Object[]{proyectoLicenciamientoAmbiental.getCodigo(), ""}));
        data.put("asuntoNotificacionFacilitadoresAsignadosPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                "asuntoNotificacionFacilitadoresAsignadosPS", new Object[]{}));

        //Atraso en la tarea de firmar el oficio de participación social del proyecto %s.
        data.put("bodyNotificacionFirmaOficioAtrasoPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionFirmaOficioAtrasoPS", new Object[]{proyectoLicenciamientoAmbiental.getCodigo()}));


        data.put("asuntoNotificacionPromotorIngresoInformeRIPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                "asuntoNotificacionPromotorIngresoInformeRIPS", new Object[]{proyectoLicenciamientoAmbiental.getCodigo()}));

        data.put("bodyNotificacionPromotorIngresoInformeRIPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionPromotorIngresoInformeRIPS", new Object[]{proyectoLicenciamientoAmbiental.getCodigo()}));


        data.put("asuntoNotificarAprobacionPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                "asuntoNotificarAprobacionPS", new Object[]{proyectoLicenciamientoAmbiental.getCodigo()}));

        data.put("bodyNotificarAprobacionPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                "bodyNotificarAprobacionPS", new Object[]{proyectoLicenciamientoAmbiental.getCodigo()}));


        data.put("asuntoNotificarCalificacionEvaluacionFacilitadoresPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                "asuntoNotificarCalificacionEvaluacionFacilitadoresPS", new Object[]{proyectoLicenciamientoAmbiental.getCodigo()}));

        data.put("bodyNotificarCalificacionEvaluacionFacilitadoresPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                "bodyNotificarCalificacionEvaluacionFacilitadoresPS", new Object[]{proyectoLicenciamientoAmbiental.getCodigo()}));

        //---
        data.put("asuntoNotificacionConvocatoriaPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                "asuntoNotificacionConvocatoriaPS", new Object[]{}));
        Usuario proponente = usuarioFacade.buscarUsuarioPorIdFull(proyectoLicenciamientoAmbiental.getUsuario().getId());

        String urlDownload = "";
        if (proyectoLicenciamientoAmbiental.getCatalogoCategoria().getEstrategico()) {
            urlDownload = Constantes.getUrlAdjuntosPromotorEstrategicoES();
        } else {
            urlDownload = Constantes.getUrlAdjuntosPromotorNoEstrategicoES();
        }
        data.put("bodyNotificacionConvocatoriaPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionConvocatoriaPS", new Object[]{proponente.getPersona().getNombre(), urlDownload}));

        data.put("asuntoNotificacionAsignacionFacilitadoresPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                "asuntoNotificacionAsignacionFacilitadoresPS", new Object[]{}));
        data.put("bodyNotificacionAsignacionFacilitadoresPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                "bodyNotificacionAsignacionFacilitadoresPS", new Object[]{proyectoLicenciamientoAmbiental.getCodigo()}));

        return data;
    }

    public String configurarPeticion(String rol, String area) {
        return "rol=" + rol + "&area=" + area;
    }

    public String configurarEquipoM() {
        return "/prevencion/participacionsocial/vistaPrevia.xhtml---Datos del EIA";
    }

    public void ingresarInformes(Map<String, Documento> documentos, long idProceso, long idTarea, String codProyecto) throws Exception {
        for (String clave : documentos.keySet()) {
            Documento documento = documentos.get(clave);
            if (documento.getId() == null) {
                DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
                documentoTarea.setIdTarea(idTarea);
                documentoTarea.setProcessInstanceId(idProceso);
                eliminarDocumento(documento.getNombreTabla(), documento.getIdTable(), obtenerTipoPorClave(clave).getIdTipoDocumento());

                documentosFacade.guardarDocumentoAlfresco(codProyecto,
                        Constantes.NOMBRE_PROCESO_PARTICIPACION_SOCIAL, idProceso, documento, obtenerTipoPorClave(clave),
                        documentoTarea);
            }
        }

    }


    private TipoDocumentoSistema obtenerTipoPorClave(String clave) {

        if (clave.equals("visitaPrevia")) {
            return TipoDocumentoSistema.PS_INFORME_DE_VISITA_PREVIA;
        }
        if (clave.equals("invitacionesPersonales")) {
            return TipoDocumentoSistema.PS_INVITACIONES_PERSONALES;
        }
        if (clave.equals("convocatoriaPublica")) {
            return TipoDocumentoSistema.PS_CONVOCATORIA_PÚBLICA;
        }
        if (clave.equals("actaAperturaCierre")) {
            return TipoDocumentoSistema.PS_ACTA_APERTURA_Y_CIERRE_CIP;
        }
        if (clave.equals("actaAsamblea")) {
            return TipoDocumentoSistema.PS_ACTA_DE_LA_ASAMBLEA;
        }
        if (clave.equals("registroCip")) {
            return TipoDocumentoSistema.PS_REGISTRO_CIP;
        }
        if (clave.equals("registroAsistencia")) {
            return TipoDocumentoSistema.PS_REGISTRO_DE_ASISTENCIA;
        }

        if (clave.equals("informeSistematizacionPPS")) {
            return TipoDocumentoSistema.PS_INFORME_SISTEMATIZACION;
        }

        if (clave.equals("respaldoMediosVerificacionPPS")) {
            return TipoDocumentoSistema.PS_RESPALDO_MEDIOS_VERIFICACION;
        }

        if (clave.equals("documentacionComplementariaPPS")) {
            return TipoDocumentoSistema.PS_DOCUMENTACION_COMPLEMENTARIA;
        }

        if (clave.equals("respaldoInformeReunionPPS")) {
            return TipoDocumentoSistema.PS_INFORME_REUNION_INFORMATIVA;
        }

        if (clave.equals("correccionDocumentacionPPS")) {
            return TipoDocumentoSistema.PS_CORRECCION_DOCUMENTACION;
        }

        if (clave.equals("oficio")) {
            return TipoDocumentoSistema.PS_OFICIO_FINAL;
        }

        if (clave.equals("informeTecnico")) {
            return TipoDocumentoSistema.PS_INFORME_TECNICO_PPS;
        }


        //------------


        if (clave.equals("compMecanismos")) {
            return TipoDocumentoSistema.PS_MECANISMO_PPS;
        }

        if (clave.equals("compInformeFinal")) {
            return TipoDocumentoSistema.PS_INFORME_SISTEMATIZACION;
        }

        if (clave.equals("compOtro")) {
            return TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS;
        }
        
        if(clave.equals("documentoJustificacion")){
        	return TipoDocumentoSistema.JUSTIFICACION_RECHAZO_PPS;
        }
        
        if(clave.equals("informeTecnicoVisitaPreviaPPS")){
        	return TipoDocumentoSistema.INFORME_TECNICO_VISITA_PREVIA_PPS;
        }
        
        return TipoDocumentoSistema.PS_INFORME_DE_VISITA_PREVIA;
    }

    private String obtenerClavePorTipo(TipoDocumentoSistema tipo) {

        switch (tipo) {
            case PS_INFORME_DE_VISITA_PREVIA:
                return "visitaPrevia";
            case PS_INVITACIONES_PERSONALES:
                return "invitacionesPersonales";
            case PS_CONVOCATORIA_PÚBLICA:
                return "convocatoriaPublica";
            case PS_ACTA_APERTURA_Y_CIERRE_CIP:
                return "actaAperturaCierre";
            case PS_ACTA_DE_LA_ASAMBLEA:
                return "actaAsamblea";
            case PS_REGISTRO_CIP:
                return "registroCip";
            case PS_REGISTRO_DE_ASISTENCIA:
                return "registroAsistencia";
            case PS_RESPALDO_MEDIOS_VERIFICACION:
                return "respaldoMediosVerificacionPPS";
            case PS_INFORME_REUNION_INFORMATIVA:
                return "respaldoInformeReunionPPS";
            case PS_CORRECCION_DOCUMENTACION:
                return "correccionDocumentacionPPS";
            case PS_DOCUMENTACION_COMPLEMENTARIA:
                return "documentacionComplementariaPPS";
            case PS_OFICIO_FINAL:
                return "oficio";
            case PS_INFORME_TECNICO_PPS:
                return "informeTecnico";


            case PS_MECANISMO_PPS:
                return "compMecanismos";

            case PS_INFORME_SISTEMATIZACION:
                return "compInformeFinal";



            case TIPO_DOCUMENTO_ANEXOS:
                return "compOtro";
		default:
			break;


        }

        /*




        if (clave.equals("compOtro")) {
            return TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS;
        }

        * */
        return "";
    }

    @SuppressWarnings("unchecked")
    public Map<String, Documento> recuperarDocumentosTipo(Integer id, String nombre, List<String> claves)
            throws Exception {
        List<Integer> listaTipos = new ArrayList<Integer>();
        for (String clave : claves) {
            listaTipos.add(obtenerTipoPorClave(clave).getIdTipoDocumento());
        }
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("nombreTabla", nombre);
        parameters.put("idTabla", id);
        parameters.put("idTipos", listaTipos);
        List<Documento> documentos = (List<Documento>) crudServiceBean
                .findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA_LISTA_TIPO,
                        parameters);
        Map<String, Documento> resultado = new HashMap<String, Documento>();
        for (Documento documento : documentos) {
            resultado.put(obtenerClavePorTipo(documento.getTipoDocumento().getTipoDocumentoSistema()), documento);
        }
        return resultado;

    }

    @SuppressWarnings("unchecked")
    public Map<Integer, Documento> recuperarDocumentosTipoIds(List<Integer> ids, String nombre, String clave)
            throws Exception {
        List<Integer> listaTipos = new ArrayList<Integer>();
        listaTipos.add(obtenerTipoPorClave(clave).getIdTipoDocumento());

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("nombreTabla", nombre);
        parameters.put("idTabla", ids);
        parameters.put("idTipos", listaTipos);
        List<Documento> documentos = new ArrayList<Documento>();
        if(!ids.isEmpty()) {
            documentos = (List<Documento>) crudServiceBean
                    .findByNamedQuery(Documento.LISTAR_POR_IDS_NOMBRE_TABLA_LISTA_TIPO,
                            parameters);
        }
        Map<Integer, Documento> resultado = new HashMap<Integer, Documento>();
        for (Documento documento : documentos) {
            resultado.put(documento.getIdTable(), documento);
        }
        return resultado;

    }

    public ParticipacionSocialAmbiental buscarCrearParticipacionSocialAmbiental(ProyectoLicenciamientoAmbiental proyecto) {
        return participacionSocialService.buscarCrearParticipacionSocialAmbiental(proyecto);
    }


    public String listadoFacilitadores(ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental) throws ServiceException {
        FacilitadorProyectoFacade facilitadorProyectoFacade = (FacilitadorProyectoFacade) BeanLocator.getInstance(FacilitadorProyectoFacade.class);

        StringBuilder stringBuilder = new StringBuilder();
        List<FacilitadorProyecto> facilitadorProyectoList =
                facilitadorProyectoFacade.listarFacilitadoresProyecto(
                        proyectoLicenciamientoAmbiental);
        for (FacilitadorProyecto facilitadorProyecto : facilitadorProyectoList) {
            Usuario facilitadorU = usuarioFacade.buscarUsuarioPorIdFull(facilitadorProyecto.getUsuario().getId());
            
            if(facilitadorU != null){
            	 stringBuilder.append("<br />  <b  style='display: inline-block;'>Datos del facilitador:</b>");

                 stringBuilder.append("<br /><b  style='display: inline-block;'> Nombre(s) y Apellidos:</b> ");

                 stringBuilder.append(facilitadorU.getPersona().getNombre());
                 String telefonos = "";
                 String correos = "";
                 for (Contacto contacto : facilitadorU.getPersona().getContactos()) {
                     if (contacto.getFormasContacto().getId() == 5) {
                         String separador = correos.isEmpty() ? "" : ", ";
                         String mail = "<a href='mailto:" + contacto.getValor().trim() + "?Subject=' target='_top'>" + contacto.getValor().trim() + "</a>";
                         correos += separador + mail;
                     }

                     if (contacto.getFormasContacto().getId() == 6) {
                         String separador = telefonos.isEmpty() ? "" : ", ";
                         telefonos += separador + contacto.getValor();
                     }


                 }

                 stringBuilder.append("<br /> <b style='display: inline-block;'>Teléfono:</b> ");
                 stringBuilder.append(telefonos);
                 stringBuilder.append("<br /> <b  style='display: inline-block;'>Correo electrónico:</b> ");
                 stringBuilder.append(correos);
            }           
        }
        return stringBuilder.toString();
    }


    public ParticipacionSocialAmbiental getProyectoParticipacionSocialByProject(ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental) {
        return participacionSocialService.getProyectoParticipacionSocialByProjectId(proyectoLicenciamientoAmbiental);
    }


    public PlantillaReporte obtenerPlantillaReporte(Integer tipoDocumentoId) {
        return participacionSocialService.obtenerPlantillaReporte(tipoDocumentoId);

    }
//
//    public InformeTecnicoEia obtenerInformeTecnicoEiaPorEstudio(
//            TipoDocumentoSistema tipoDocumento, Integer estudioImpactoAmbientalId) {
//        return participacionSocialService.obtenerInformeTecnicoEiaPorEstudio(tipoDocumento,estudioImpactoAmbientalId);
//    }


}
