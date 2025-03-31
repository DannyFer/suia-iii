/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.prevencion.categoria2.facade.v2;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.autoridadambiental.service.AutoridadAmbientalFacade;
import ec.gob.ambiente.suia.catalogocategorias.facade.CatalogoCategoriasFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.persona.service.PersonaServiceBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import org.apache.log4j.Logger;


import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author jgras
 * @version Revision: 1.0
 *          <p>
 *          [Autor: jgras, Fecha: 03/02/2015]
 *          </p>
 */
@Stateless
public class CategoriaIIFacadeV2 {

    private static final Logger LOG = Logger.getLogger(CategoriaIIFacadeV2.class);

//    private static final String NADA = "";
    @EJB
    SecuenciasFacade secuenciasFacade;
    @EJB
    UsuarioFacade usuarioFacade;
    @EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    AlfrescoServiceBean alfrescoServiceBean;
    @EJB
    DocumentosFacade documentosFacade;
    @EJB
    PersonaServiceBean personaService;
    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private ContactoFacade contactoFacade;

    @EJB
    private AutoridadAmbientalFacade autoridadAmbientalFacade;

    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

    @EJB
    private CatalogoCategoriasFacade catalogoCategoriasFacade;

    @EJB
    private CertificadoInterseccionFacade certificadoInterseccionFacade;
    @EJB
    private OrganizacionFacade organizacionFacade;
    
    @EJB
    private AreaFacade areaFacade;

    public void modificarRegistro(EntidadBase objeto) {
        crudServiceBean.saveOrUpdate(objeto);
    }

    public void guardarRegistro(EntidadBase objeto) {
        crudServiceBean.saveOrUpdate(objeto);
    }

    public void delete(EntidadBase objeto) {
        crudServiceBean.delete(objeto);
    }

    public void ingresarDocumentoCategoriaII(File file, Integer id,
                                             String codProyecto, long idProceso, long idTarea,
                                             TipoDocumentoSistema tipoDocumento, String nombreDocumento) throws Exception {

        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        Documento documento1 = new Documento();
        documento1.setIdTable(id);
        documento1.setNombre(nombreDocumento + ".pdf");
        documento1.setExtesion(mimeTypesMap.getContentType(file));
        documento1.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
        documento1.setMime(mimeTypesMap.getContentType(file));
        documento1.setContenidoDocumento(data);
        DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
        documentoTarea.setIdTarea(idTarea);
        documentoTarea.setProcessInstanceId(idProceso);

        documentosFacade.guardarDocumentoAlfresco(codProyecto,
                "LicenciaAmbientalCategoriaII", idProceso, documento1, tipoDocumento,
                documentoTarea);
    }

    public void ingresarArchivoFotografico(File file, Integer id,
                                           String codProyecto, long idProceso, long idTarea,
                                           TipoDocumentoSistema tipoDocumento, String nombreDocumento) throws Exception {

        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        Documento documento1 = new Documento();
        documento1.setIdTable(id);
        documento1.setNombre(nombreDocumento + ".pdf");
        documento1.setExtesion(mimeTypesMap.getContentType(file));
        documento1.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
        documento1.setMime(mimeTypesMap.getContentType(file));
        documento1.setContenidoDocumento(data);
        DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
        documentoTarea.setIdTarea(idTarea);
        documentoTarea.setProcessInstanceId(idProceso);

        documentosFacade.guardarDocumentoAlfresco(codProyecto,
                "LicenciaAmbientalCategoriaII", idProceso, documento1, tipoDocumento,
                documentoTarea);
    }

    public byte[] recuperarDocumentoCategoriaII(Integer id,
                                                TipoDocumentoSistema tipoDocumento) throws Exception {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters
                .put("nombreTabla", CategoriaIILicencia.class.getSimpleName());
        parameters.put("idTabla", id);
        parameters.put("idTipo", tipoDocumento.getIdTipoDocumento());

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

    @SuppressWarnings("unchecked")
    public CategoriaIILicencia getCategoriaIILicenciaPorIdProyecto(
            Integer idProyecto) {
        Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
        parameters.put("idProyecto", idProyecto);

        List<CategoriaIILicencia> result = (List<CategoriaIILicencia>) crudServiceBean
                .findByNamedQuery(CategoriaIILicencia.FIND_BY_PROJECT,
                        parameters);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    public void inicarProcesoCategoriaII(Usuario usuario,
                                         ProyectoLicenciamientoAmbiental proyecto) throws ServiceException {
        Map<String, Object> params = getParametrosIniciarProcesoCategoriaII(usuario.getNombre(),
                proyecto);
        params.put(Constantes.ID_PROYECTO, proyecto.getId());       
        Area areaProyecto = areaFacade.getArea(proyecto.getAreaResponsable().getId());
        String usrMaximaAutoridad=null;
        try {
        	if (areaProyecto.getTipoArea().getId()==3){
   			 usrMaximaAutoridad = areaFacade.getDirectorProvincial(areaProyecto).getNombre();  //Director Provincial (por ministra)
	   			if (usrMaximaAutoridad!=null){
	   	        	procesoFacade.iniciarProceso(usuario, Constantes.NOMBRE_PROCESO_CATEGORIA2V2,
	   	                    proyecto.getCodigo(), params);
	   			}
       		}else{
            	procesoFacade.iniciarProceso(usuario, Constantes.NOMBRE_PROCESO_CATEGORIA2V2,
                        proyecto.getCodigo(), params);
       		}
        } catch (JbpmException e) {
            throw new ServiceException(e);
        }
    }

    public Map<String, Object> getParametrosIniciarProcesoCategoriaII(String nombreUsuario,
                                                                      ProyectoLicenciamientoAmbiental proyecto) {
        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        params.put("nombreFormularioPMA", getPageRegistroAmbientalPorSector(proyecto));
        params.put("u_Promotor", nombreUsuario);
        params.put("codigoProyecto", proyecto.getCodigo());
        params.put("exentoPago", getExentoPago(proyecto));
        params.put("factorCovertura", Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal")));
        params.put("costoTramite", Float.parseFloat(Constantes.getPropertyAsString("costo.tramite.registro.ambiental")));
        return params;
    }

    public Boolean getExentoPago(ProyectoLicenciamientoAmbiental proyecto) {
        try {
            crudServiceBean
                    .getEntityManager()
                    .createQuery(
                            "From CatalogoCategoriaSistema c where c.id =:catCatId and c.estado = true and c.pagoServiciosAdministrativos = true")
                    .setParameter("catCatId", proyecto.getCatalogoCategoria().getId())
                    .getSingleResult();
        } catch (NoResultException nre) {
            return true;// Si es una actividad que no paga
        }
        // Si es categoría III o IV
        String categoria = proyecto.getCatalogoCategoria().getCategoria()
                .getCodigo();
        if (categoria.equals("III") || categoria.equals("IV")) {
            // Si el usuario pertenece a una organizacion
            try {
                Usuario user = usuarioFacade.buscarUsuarioPorId(proyecto
                        .getUsuario().getId());
                if (user.getPersona().getOrganizaciones().size() > 0) {
                    String tipoOrganizacion = proyecto.getUsuario().getPersona()
                            .getOrganizaciones().get(0).getTipoOrganizacion()
                            .getDescripcion();

                    // Si es Empresa Pública o Mixta
                    if (tipoOrganizacion.equals("EP")
                            || tipoOrganizacion.equals("EM")) {
                        return true; // No paga
                    } else {
                        // Verifica acuerdo MAE

                        List<CatalogoCategoriaAcuerdo> acuerdosOrganizacion = catalogoCategoriasFacade
                                .buscarCatalogoCategoriaAcuerdoPorRucOrganizacion(proyecto
                                        .getUsuario().getPersona()
                                        .getOrganizaciones().get(0).getRuc());

                        if (acuerdosOrganizacion != null
                                && acuerdosOrganizacion.size() > 0) {
                            return true; // No paga
                        }
                        return false;
                    }
                }
            } catch (Exception e) {
                LOG.error("Error al obtener la organización e iniciar el proceso",
                        e);
            }
        }

        // Para el resto se debe pagar
        return false;
    }

    private String getPageRegistroAmbientalPorSector(
            ProyectoLicenciamientoAmbiental proyecto) {
    	if (proyecto.getCatalogoCategoria().getCodigo().equals("21.02.03.05") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.04.03")|| 
    			proyecto.getCatalogoCategoria().getCodigo().equals("21.02.05.03") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.02.03")) {
    		return "/prevencion/categoria2/v2/fichaMineria020/default.jsf";
    	}
        if (proyecto.getCatalogoCategoria().getCodigo().equals("21.02.01.01") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.01.02")) {
            return "/prevencion/categoria2/v2/fichaMineria/default.jsf";
        }
        return "/prevencion/categoria2/v2/fichaAmbiental/default.jsf";
    }

}
