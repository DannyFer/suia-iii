/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.fauna.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.chemistry.opencmis.client.api.Document;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.DetalleFauna;
import ec.gob.ambiente.suia.domain.DetalleFaunaEspecies;
import ec.gob.ambiente.suia.domain.DetalleFaunaSumaEspecies;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.Fauna;
import ec.gob.ambiente.suia.domain.PuntosMuestreo;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.EiaOpcionesFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class FaunaFacade {

    @EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    AlfrescoServiceBean alfrescoServiceBean;
    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EiaOpcionesFacade eiaOpcionesFacade;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(FaunaFacade.class);

    /**
     *
     * @param fauna
     * @param listaPuntosMuestreo
     * @param listaDetalleFauna
     * @param listaPuntosMuestreoEditar
     * @param listaDetalleFaunaEditar
     * @param eiaOpciones
     * @throws ServiceException
     */
    public void guardarFauna(final Fauna fauna, final List<PuntosMuestreo> listaPuntosMuestreo,
            final List<DetalleFauna> listaDetalleFauna, final List<PuntosMuestreo> listaPuntosMuestreoEditar,
            final List<DetalleFauna> listaDetalleFaunaEditar, final EiaOpciones eiaOpciones) throws ServiceException {
        try {
            eiaOpcionesFacade.guardar(fauna.getEstudioImpactoAmbiental(), eiaOpciones);
            EntityAdjunto adjunto = fauna.getAdjunto();
            Fauna faunaAux = crudServiceBean.saveOrUpdate(fauna);
            guardarAdjunto(adjunto.getMimeType(), adjunto.getArchivo(), faunaAux.getId(), adjunto.getNombre(), Fauna.class.getSimpleName());
            for (PuntosMuestreo p : listaPuntosMuestreo) {
                p.setFauna(faunaAux);
                List<DetalleFauna> listaDetalleFaunaAux = listarPorPunto(p, listaDetalleFauna);
                PuntosMuestreo pmPersist = crudServiceBean.saveOrUpdate(p);
                guardarDetalleFauna(pmPersist, listaDetalleFaunaAux);
                p.getCoordenada().setIdTable(pmPersist.getId());
                crudServiceBean.saveOrUpdate(p.getCoordenada());
                if (p.getCoordenada1() != null && p.getCoordenada1().getX() != null) {
                    p.getCoordenada1().setIdTable(pmPersist.getId());
                    crudServiceBean.saveOrUpdate(p.getCoordenada1());
                }

            }
            crudServiceBean.saveOrUpdate(listaDetalleFaunaEditar);
            crudServiceBean.saveOrUpdate(listaPuntosMuestreoEditar);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        } catch (Exception ex) {
            LOG.error(ex);
        }
    }

    /**
     *
     * @param fauna
     * @param listaPuntosMuestreo
     * @param listaDetalleFauna
     * @param detalleFaunaSumaEspecies
     * @param listaPuntosMuestreoEditar
     * @param listaDetalleFaunaEditar
     * @param eiaOpciones
     * @throws ServiceException
     */
    public void guardarFauna(final Fauna fauna, final List<PuntosMuestreo> listaPuntosMuestreo,
            final List<DetalleFauna> listaDetalleFauna, DetalleFaunaSumaEspecies detalleFaunaSumaEspecies,
            final List<PuntosMuestreo> listaPuntosMuestreoEditar,
            final List<DetalleFauna> listaDetalleFaunaEditar, final EiaOpciones eiaOpciones) throws ServiceException {
        try {
            eiaOpcionesFacade.guardar(fauna.getEstudioImpactoAmbiental(), eiaOpciones);
            EntityAdjunto adjunto = fauna.getAdjunto();
            Fauna faunaAux = crudServiceBean.saveOrUpdate(fauna);
            guardarAdjunto(adjunto.getMimeType(), adjunto.getArchivo(), faunaAux.getId(), adjunto.getNombre(), Fauna.class.getSimpleName());
            detalleFaunaSumaEspecies.setFaunId(faunaAux);
            crudServiceBean.saveOrUpdate(detalleFaunaSumaEspecies);
            for (PuntosMuestreo p : listaPuntosMuestreo) {
                p.setFauna(faunaAux);
                List<DetalleFauna> listaDetalleFaunaAux = listarPorPunto(p, listaDetalleFauna);
                PuntosMuestreo pmPersist = crudServiceBean.saveOrUpdate(p);
                guardarDetalleFauna(pmPersist, listaDetalleFaunaAux);
                p.getCoordenada().setIdTable(pmPersist.getId());
                crudServiceBean.saveOrUpdate(p.getCoordenada());
                if (p.getCoordenada1() != null && p.getCoordenada1().getX() != null) {
                    p.getCoordenada1().setIdTable(pmPersist.getId());
                    crudServiceBean.saveOrUpdate(p.getCoordenada1());
                }

            }
            crudServiceBean.saveOrUpdate(listaDetalleFaunaEditar);
            crudServiceBean.saveOrUpdate(listaPuntosMuestreoEditar);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        } catch (Exception ex) {
            LOG.error(ex);
        }
    }

    private void guardarDetalleFauna(PuntosMuestreo pm, List<DetalleFauna> listaDetalle) throws ServiceException {
        try {
            for (DetalleFauna detalleFauna : listaDetalle) {
                EntityAdjunto adjunto = detalleFauna.getAdjunto();
                detalleFauna.setPuntosMuestreo(pm);
                DetalleFauna detalleFaunaPersist = crudServiceBean.saveOrUpdate(detalleFauna);
                if (adjunto != null && adjunto.getArchivo() != null) {
                    guardarAdjunto(adjunto.getMimeType(), adjunto.getArchivo(), detalleFaunaPersist.getId(),
                            adjunto.getNombre(), DetalleFauna.class.getSimpleName());
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }

    }

    private void guardarAdjunto(String contentType, byte[] fileAnexo,
            Integer idTabla, String nombreAnexo, String nombreClase) throws Exception {
        if (fileAnexo == null) {
            return;
        }
        String folderName = UtilAlfresco.generarEstructuraCarpetas(
                EstudioImpactoAmbiental.PROJECT_CODE,
                EstudioImpactoAmbiental.PROCESS_NAME,
                EstudioImpactoAmbiental.PROCESS_ID
                .toString());
        String folderId = alfrescoServiceBean.createFolderStructure(
                folderName, Constantes.getRootId());
        Document documentCreate = alfrescoServiceBean.fileSaveStream(
                fileAnexo, nombreAnexo, folderId, "EIA",
                folderName, idTabla);

        Documento documento = crudServiceBean.saveOrUpdate(UtilAlfresco.crearDocumento(
                documentCreate.getName(), contentType,
                documentCreate.getId(), idTabla, contentType,
                nombreAnexo,
                nombreClase,
                TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

        crudServiceBean.saveOrUpdate(UtilAlfresco
                .crearDocumentoTareaProceso(documento,
                        123, 148L));
    }

    /**
     *
     * @param nombreClase
     * @param idTabla
     * @return
     * @throws CmisAlfrescoException 
     */
    public EntityAdjunto recuperarAdjunto(String nombreClase, Integer idTabla) throws CmisAlfrescoException {
        EntityAdjunto efa = new EntityAdjunto();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("nombreTabla", nombreClase);
        parameters.put("idTabla", idTabla);

        List<Documento> documentos = (List<Documento>) crudServiceBean
                .findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA,
                        parameters);
        if (!documentos.isEmpty()) {
            Documento doc = documentos.get(0);
            byte[] archivo = alfrescoServiceBean
                    .downloadDocumentById(doc.getIdAlfresco());
            efa.setArchivo(archivo);
            efa.setNombre(doc.getNombre());
            efa.setMimeType(doc.getMime());
        }
        return efa;
    }

    private List<DetalleFauna> listarPorPunto(final PuntosMuestreo puntosMuestreo, final List<DetalleFauna> listaDetalleFauna) {
        List<DetalleFauna> lista = new ArrayList<DetalleFauna>();
        for (DetalleFauna de : listaDetalleFauna) {
            if (puntosMuestreo.getCodigo().equals(de.getIdPuntoMuestreo())) {
                lista.add(de);
            }
        }
        return lista;
    }

    /**
     *
     * @param pinUsuario
     * @param eia
     * @param grupoTaxonomico
     * @return
     * @throws ServiceException
     */
    public Fauna obtenerPorIdPorPinUsuario(final String pinUsuario,
            EstudioImpactoAmbiental eia, CatalogoGeneral grupoTaxonomico) throws ServiceException {
        try {
            Fauna fauna = recuperarPorEIAGrupoTaxonomico(eia, grupoTaxonomico);
            if (fauna == null) {
                return null;
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idFauna", fauna.getId());
            List<PuntosMuestreo> listaPuntos = (List<PuntosMuestreo>) crudServiceBean.findByNamedQuery(PuntosMuestreo.LISTAR_POR_FAUNA, params);
            List<PuntosMuestreo> listaPuntosAux = new ArrayList<PuntosMuestreo>();
            List<Integer> listaPuntosIds = crudServiceBean.findByNamedQueryGeneric(PuntosMuestreo.LISTAR_POR_FAUNA_IDS, params);
            params = new HashMap<String, Object>();
            params.put("listaPuntosMuestro", listaPuntosIds);
            List<DetalleFauna> listaDetalleFauna = (List<DetalleFauna>) crudServiceBean.findByNamedQuery(DetalleFauna.LISTAR_POR_PUNTOS_MUESTRO_IDS, params);
            params = new HashMap<String, Object>();
            params.put("nombreTabla", PuntosMuestreo.class.getSimpleName());
            params.put("pinUsuario", "%;" + pinUsuario);
            List<CoordenadaGeneral> listaCoordenadaGeneral = (List<CoordenadaGeneral>) crudServiceBean.findByNamedQuery(CoordenadaGeneral.LISTAR_POR_NOMBRE_TABLA_PIN_USUARIO, params);
            for (PuntosMuestreo p : listaPuntos) {
                for (CoordenadaGeneral c : listaCoordenadaGeneral) {
                    if (p.getId().equals(c.getIdTable())) {
                        p = obtenerPorCoordenada(p, c);
                    }
                }
                p.setDetailFaunaCollection(listarPorPuntosDetalleFauna(p, listaDetalleFauna));
                listaPuntosAux.add(p);
            }
            fauna.setPuntosMuestreoCollection(listaPuntosAux);
            fauna.setDetalleFaunaSumaEspeciesCollection(listarPorFauna(fauna.getId()));
            return fauna;
        } catch (RuntimeException e) {
            LOG.error(e);
            return null;
        }

    }

    private PuntosMuestreo obtenerPorCoordenada(PuntosMuestreo p, final CoordenadaGeneral c) {
        if (("0").equals(c.getDescripcion().split(";")[0])) {
            p.setCoordenada(c);
        } else {
            if (("1").equals(c.getDescripcion().split(";")[0])) {
                p.setCoordenada1(c);
            }
        }
        return p;
    }

    private List<DetalleFauna> listarPorPuntosDetalleFauna(final PuntosMuestreo p, final List<DetalleFauna> listaDetalle) {
        List<DetalleFauna> listaDetalleAux = new ArrayList<DetalleFauna>();
        for (DetalleFauna d : listaDetalle) {
            if (p.getId().equals(d.getIdPuntosMuestreo())) {
                d.setPuntosMuestreo(p);
                d.setIdPuntoMuestreo(p.getCodigo());
                listaDetalleAux.add(d);
            }
        }
        return listaDetalleAux;
    }

    private List<DetalleFaunaSumaEspecies> listarPorFauna(final Integer idFauna) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("idFauna", idFauna);
        List<DetalleFaunaSumaEspecies> listaSumaDetalle = (List<DetalleFaunaSumaEspecies>) crudServiceBean.findByNamedQuery(DetalleFaunaSumaEspecies.OBTENER_POR_FAUNA, params);
        if (listaSumaDetalle != null && !listaSumaDetalle.isEmpty()) {
            for (DetalleFaunaSumaEspecies d : listaSumaDetalle) {
                params = new HashMap<String, Object>();
                params.put("idDetalleFaunaEspecies", d.getId());
                d.setDetailFaunaSpeciesCollection((List<DetalleFaunaEspecies>) crudServiceBean.findByNamedQuery(DetalleFaunaEspecies.LISTAR_POR_FAUNA_SUMA, params));
            }
        }
        return listaSumaDetalle;
    }

    /**
     *
     * @param estudioImpactoAmbiental
     * @param grupoTaxonomico
     * @return
     */
    public Fauna recuperarPorEIAGrupoTaxonomico(EstudioImpactoAmbiental estudioImpactoAmbiental, CatalogoGeneral grupoTaxonomico) {
        List<Fauna> result;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("estudioImpactoAmbiental", estudioImpactoAmbiental);
        params.put("catalogoGruposTaxonomicos", grupoTaxonomico);
        result = (List<Fauna>) crudServiceBean.findByNamedQuery(Fauna.BUSCAR_POR_EIA_GRUPO_TAXONOMICO, params);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }
}
