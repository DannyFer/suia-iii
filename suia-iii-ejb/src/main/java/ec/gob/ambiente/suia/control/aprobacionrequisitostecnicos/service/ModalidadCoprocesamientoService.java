/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ActividadProtocoloPrueba;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamiento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamientoDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamientoDesechoProcesar;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamientoFormulacion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ProgramaCalendarizadoCoprocesamiento;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * <b> Clase que contiene los servicios para modalidad coprocesamiento. </b>
 *
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 * <p>
 * [$Author: Jonathan Guerrero $, $Date: 22/06/2015 $]
 * </p>
 */
@Stateless
public class ModalidadCoprocesamientoService {

    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public ModalidadCoprocesamiento getModalidadCoprocesamiento(
            AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
        List<ModalidadCoprocesamiento> lista = null;
        ModalidadCoprocesamiento modalidadCoprocesamiento;

        try {
            lista = crudServiceBean
                    .getEntityManager()
                    .createQuery(
                            "From ModalidadCoprocesamiento m where  m.aprobacionRequisitosTecnicos.id=:aprobacion AND m.estado = true")
                    .setParameter("aprobacion", aprobacionRequisitosTecnicos.getId()).getResultList();
            if (lista == null || lista.isEmpty()) {
                return null;
            } else {
                DesechoPeligroso currentDesecho;
                Map<String, Object> parametros;


                modalidadCoprocesamiento = lista.get(0);
                modalidadCoprocesamiento.getId();
                modalidadCoprocesamiento.getModalidadCoprocesamientoDesechos().size();
                for (ModalidadCoprocesamientoDesecho desecho : modalidadCoprocesamiento.getModalidadCoprocesamientoDesechos()) {

                    desecho.getId();
                    desecho.getDesecho().getId();
                    desecho.getDesecho().getFuenteDesechoPeligroso().getId();
                    /*parametros = new HashMap<String, Object>();
                    parametros.put("idDesecho",desecho.getDesecho().getId());
                    currentDesecho = ((ArrayList<DesechoPeligroso>)crudServiceBean.findByNamedQuery(
                            DesechoPeligroso.FIND_BY_ID, parametros)).get(0);

                    currentDesecho.getId();
                   // desecho.setDesecho(currentDesecho);*/

                }


                modalidadCoprocesamiento.getModalidadDesechoProcesados().size();
                for (ModalidadCoprocesamientoDesechoProcesar procesar : lista.get(0).getModalidadDesechoProcesados()) {
                    procesar.getId();
                    procesar.getDesecho().getId();
                    procesar.getDesecho().getFuenteDesechoPeligroso();
                    //currentDesecho = buscarDesechoPeligrososPorId(procesar.getIdDesecho());

                    /*parametros = new HashMap<String, Object>();
                    parametros.put("idDesecho",procesar.getDesecho().getId());
                    currentDesecho  = ((ArrayList<DesechoPeligroso>)crudServiceBean.findByNamedQuery(
                            DesechoPeligroso.FIND_BY_ID, parametros)).get(0);


                    currentDesecho.getId();
                    //procesar.setDesecho(currentDesecho);*/
                }

               // lista.get(0).setModalidadCoprocesamientoFormulaciones(getModalidadCoprocesamientoFormulaciones(lista.get(0).getId()));

                return modalidadCoprocesamiento;
            }
        } catch (Exception e) {
            throw new ServiceException("Ocurrió un problema al recuperar los datos");
        }
    }

    public List<ModalidadCoprocesamientoFormulacion> getModalidadCoprocesamientoFormulaciones(final Integer idModalidad) {
        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("idModalidad", idModalidad);
        List<ModalidadCoprocesamientoFormulacion> formulacion = (List<ModalidadCoprocesamientoFormulacion>) crudServiceBean.findByNamedQuery(
                ModalidadCoprocesamientoFormulacion.LISTAR_POR_ID, parametros);
        return formulacion;
    }

    public DesechoPeligroso buscarDesechoPeligrososPorId(Integer id) {
        return crudServiceBean.find(DesechoPeligroso.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<ActividadProtocoloPrueba> getActividadesPorTipoModalidad(String tipoModalidad) throws ServiceException {
        List<ActividadProtocoloPrueba> lista = null;
        try {
            lista = crudServiceBean.getEntityManager()
                    .createQuery("From ActividadProtocoloPrueba m where m.tipo=:modalidad")
                    .setParameter("modalidad", tipoModalidad).getResultList();

            return lista;

        } catch (Exception e) {
            throw new ServiceException("Ocurrió un problema al recuperar los datos");
        }

    }

    @SuppressWarnings("unchecked")
    public List<ProgramaCalendarizadoCoprocesamiento> getCalendarioActividadesPorModalidad(
            ModalidadCoprocesamiento modalidad) throws ServiceException {
        List<ProgramaCalendarizadoCoprocesamiento> lista = null;
        try {
            lista = crudServiceBean
                    .getEntityManager()
                    .createQuery(
                            "From ProgramaCalendarizadoCoprocesamiento m WHERE m.modalidadCoprocesamiento=:modalidad")
                    .setParameter("modalidad", modalidad).getResultList();

            return lista;

        } catch (Exception e) {
            throw new ServiceException("Ocurrió un problema al recuperar los datos");
        }

    }
}
