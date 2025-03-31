/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.medioBiotico;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ComparacionPuntosMuestreoEIA;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.MedioBioticoFormulasEIA;
import ec.gob.ambiente.suia.domain.MedioBioticoJustificacionEIA;
import ec.gob.ambiente.suia.domain.PuntosMuestreoEIA;
import ec.gob.ambiente.suia.domain.RegistroEspeciesEIA;
import ec.gob.ambiente.suia.domain.TipoRegistroEspecie;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * @author Oscar Campana
 */
@LocalBean
@Stateless
public class PuntosMuestreoEiaFacade {

    @EJB
    private CrudServiceBean crudServiceBean;

    public void guardar(final List<PuntosMuestreoEIA> listaPuntosMuestreoEIA,
                        final List<PuntosMuestreoEIA> listaPuntosMuestreoEIAEliminados) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaPuntosMuestreoEIA);
            if (!listaPuntosMuestreoEIAEliminados.isEmpty()) {
                crudServiceBean.delete(listaPuntosMuestreoEIAEliminados);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }


    public void guardarEspecies(final List<RegistroEspeciesEIA> listaRegistroEspeciesEIA,
                                final List<RegistroEspeciesEIA> listaRegistroEspeciesEIAEliminados) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaRegistroEspeciesEIA);
            if (!listaRegistroEspeciesEIAEliminados.isEmpty()) {
                crudServiceBean.delete(listaRegistroEspeciesEIAEliminados);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void guardarComparaciones(final List<ComparacionPuntosMuestreoEIA> listaComparacionesPtosMuestreoEIA,
                                final List<ComparacionPuntosMuestreoEIA> listaComparacionesPtosMuestreoEIAEliminados) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaComparacionesPtosMuestreoEIA);
            if (!listaComparacionesPtosMuestreoEIAEliminados.isEmpty()) {
                crudServiceBean.delete(listaComparacionesPtosMuestreoEIAEliminados);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void guardarJustificaion(final MedioBioticoJustificacionEIA medioBioticoJustificacionEIA,
                                    final MedioBioticoJustificacionEIA medioBioticoJustificacionEIAEliminar) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(medioBioticoJustificacionEIA);
            if (medioBioticoJustificacionEIAEliminar != null) {
                crudServiceBean.delete(medioBioticoJustificacionEIAEliminar);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void guardarFormulas(final MedioBioticoFormulasEIA medioBioticoFormulasEIA,
                                final MedioBioticoFormulasEIA medioBioticoFormulasEIAEliminar) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(medioBioticoFormulasEIA);
            if (medioBioticoFormulasEIAEliminar != null) {
                crudServiceBean.delete(medioBioticoFormulasEIAEliminar);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recupera la lista de Puntos de Muestreo de acuerdo al Estudio
     *
     * @param estudioImpactoAmbiental El estudio de impacto ambiental
     * @return Lista de Puntos de muestreo
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    @SuppressWarnings("unchecked")
    public List<PuntosMuestreoEIA> listarPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental, String tipo)
            throws ServiceException {
        try {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idEia", estudioImpactoAmbiental.getId());
            params.put("tipo", tipo);
            List<PuntosMuestreoEIA> lista = (List<PuntosMuestreoEIA>) crudServiceBean.findByNamedQuery(
                    PuntosMuestreoEIA.LISTAR, params);


            for (PuntosMuestreoEIA puntosMuestreoEIA : lista) {
                List<RegistroEspeciesEIA> registrosEspecies = puntosMuestreoEIA.getEspecies();
                puntosMuestreoEIA.getMetodologiaPuntoMuestreo().getId();
                if (registrosEspecies != null) {
                    registrosEspecies.size();
                    for (RegistroEspeciesEIA r : puntosMuestreoEIA.getEspecies()) {
                        r.getPuntosMuestreo().getId();
                    }
                }

            }

            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recupera la lista de tipos de registros de especies para la sección dada(Mastofauna,Entmofauna,..., etc.)
     *
     * @param seccionMenu La sección del menú lateral (Mastofauna, Entomofauna,..., etc.)
     * @return Lista de los tipos de registros de especies
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    @SuppressWarnings("unchecked")
    public List<TipoRegistroEspecie> listarTiposRegistrosEspecies(String seccionMenu, boolean directo)
            throws ServiceException {
        try {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("seccionMenu", seccionMenu);
            params.put("directo", directo);
            List<TipoRegistroEspecie> lista = (List<TipoRegistroEspecie>) crudServiceBean.findByNamedQuery(
                    TipoRegistroEspecie.LISTAR, params);

            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recupera la lista de especies de acuerdo al Estudio
     *
     * @param estudioImpactoAmbiental El estudio de impacto ambiental
     * @return Lista de especies
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    @SuppressWarnings("unchecked")
    public List<RegistroEspeciesEIA> listarEspeciesPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental, String tipo)
            throws ServiceException {
        try {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idEia", estudioImpactoAmbiental.getId());
            params.put("tipo", tipo);
            List<RegistroEspeciesEIA> lista = (List<RegistroEspeciesEIA>) crudServiceBean.findByNamedQuery(
                    RegistroEspeciesEIA.LISTAR, params);


            for (RegistroEspeciesEIA registroEspeciesEIA : lista) {

                if (registroEspeciesEIA != null && registroEspeciesEIA.getPuntosMuestreo() != null) {
                    registroEspeciesEIA.getPuntosMuestreo().getId();
                }

            }

            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    /**
     * Recupera la lista de comparaciones de cuya muestraA coincide con el punto dado.
     *
     * @param muestraA Punto pivote(para buscar las comparaciones que la incluyan)
     * @return Lista de Comparaciones
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    @SuppressWarnings("unchecked")
    public List<ComparacionPuntosMuestreoEIA> listarComparacionesPtosPorMuestraA(PuntosMuestreoEIA muestraA)
            throws ServiceException {
        try {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idMuestraA", muestraA.getId());
            List<ComparacionPuntosMuestreoEIA> lista = (List<ComparacionPuntosMuestreoEIA>) crudServiceBean.findByNamedQuery(
                    ComparacionPuntosMuestreoEIA.LISTAR_POR_MUESTRA_A, params);

            for (ComparacionPuntosMuestreoEIA comp : lista) {

                if (comp != null && comp.getMuestraA() != null && comp.getMuestraB() != null) {
                    comp.getId();
                    comp.getMuestraA().getId();
                    comp.getMuestraB().getId();

                }

            }

            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    /**
     * Recupera la lista de justificaiones del medio biotico de acuerdo al Estudio
     *
     * @param estudioImpactoAmbiental El estudio de impacto ambiental
     * @return Lista de justificaciones
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    @SuppressWarnings("unchecked")
    public MedioBioticoJustificacionEIA listarJustificacionPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental, String tipo)
            throws ServiceException {
        try {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idEia", estudioImpactoAmbiental.getId());
            params.put("tipo", tipo);
            List<MedioBioticoJustificacionEIA> lista = (List<MedioBioticoJustificacionEIA>) crudServiceBean.findByNamedQuery(
                    MedioBioticoJustificacionEIA.LISTAR, params);

            if (lista != null && lista.size() > 0) {
                return lista.get(0);
            }
            return null;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recupera la lista de formulas del medio biotico de acuerdo al Estudio
     *
     * @param estudioImpactoAmbiental El estudio de impacto ambiental
     * @return Lista de justificaciones
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    @SuppressWarnings("unchecked")
    public MedioBioticoFormulasEIA listarFormulasPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental, String tipo)
            throws ServiceException {
        try {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idEia", estudioImpactoAmbiental.getId());
            params.put("tipo", tipo);
            List<MedioBioticoFormulasEIA> lista = (List<MedioBioticoFormulasEIA>) crudServiceBean.findByNamedQuery(
                    MedioBioticoFormulasEIA.LISTAR, params);

            if (lista != null && lista.size() > 0) {
                return lista.get(0);
            }
            return null;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }


}
