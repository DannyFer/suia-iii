/**
 * Copyright (c) 2015 MAGMASOFT (Innovando tecnologia)
 * Todos los derechos reservados.
 * Este software es confidencial y debe usarlo de acorde con los términos de uso.
 */
package ec.gob.ambiente.suia.eia.mediofisico.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.eia.mediofisico.service.CalidadParametroService;
import ec.gob.ambiente.suia.eia.mediofisico.service.LaboratorioService;
import ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade.NormativasFacade;
import ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade.ParametrosNormativaFacade;
import ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade.TablasNormativasFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase que
 * @author Juan Gabriel Guzmán.
 * @version 1.0
 */
@Stateless
public class CalidadComponenteFacade {

    private static final Logger LOG = Logger.getLogger(CalidadComponenteFacade.class);

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private NormativasFacade normativasFacade;
    @EJB
    private TablasNormativasFacade tablasNormativasFacade;
    @EJB
    private ParametrosNormativaFacade parametrosNormativaFacade;
    @EJB
    private CalidadParametroService calidadParametroService;
    @EJB
    private LaboratorioService laboratorioService;

    @EJB
    private CuerpoHidricoFacade cuerpoHidricoFacade;

    public  List<CuerpoHidrico> getCuerposHidricos(EstudioImpactoAmbiental estudio) throws ServiceException{
        return cuerpoHidricoFacade.cuerpoHidricoXEiaId(estudio);
    }

    public List<Laboratorio> getLaboratorios() {
        return (List<Laboratorio>) crudServiceBean.findAll(Laboratorio.class);
    }

    public List<TablasNormativas> getTablasNormativa(Normativas normativa) throws ServiceException {
        return tablasNormativasFacade.listarPorNormativa(normativa);
    }

    public List<ParametrosNormativas> getParametros(TablasNormativas tabla) throws ServiceException {
        List<ParametrosNormativas> normativas = parametrosNormativaFacade.listarPorTablaFull(tabla.getId());
        return normativas;

    }

    public List<Laboratorio> getLaboratorios(String query) {
        try {
            return laboratorioService.getLaboratorios(query);
        } catch (ServiceException e) {
            LOG.error(e, e);
            return new ArrayList<Laboratorio>();
        }
    }

    public List<TablasNormativas> getTablas(Normativas normativa) throws ServiceException {
        return tablasNormativasFacade.listarPorNormativa(normativa);
    }

    public List<Laboratorio> getLaboratorios(List<CalidadParametro> calidadSuelos) {
        List<Laboratorio> laboratorios = new ArrayList<Laboratorio>();
        for (CalidadParametro calidad : calidadSuelos) {
            if (!laboratorios.contains(calidad.getLaboratorio())) {
                laboratorios.add(calidad.getLaboratorio());
            }
        }
        return laboratorios;
    }



    public List<Muestra> getMuestras(List<CalidadParametro> calidadSuelos) {
        List<Muestra> muestras = new ArrayList<Muestra>();
        for (CalidadParametro calidad : calidadSuelos) {
            for (ResultadoAnalisis resultado : calidad.getResultadosAnalisis()) {
                if (!muestras.contains(resultado.getMuestra())) {
                    muestras.add(resultado.getMuestra());
                }
            }
        }
        return muestras;
    }

    public List<Muestra> getMuestras(CuerpoHidrico cuerpoHidrico) {

        return crudServiceBean
                .getEntityManager()
                .createQuery(
                        " SELECT m FROM Muestra m"
                                + " where m.cuerpoHidrico.id=:idCuerpo and m.estado=true")
                .setParameter("idCuerpo", cuerpoHidrico.getId()).getResultList();
    }

    public List<CalidadParametro> getCalidadComponente(EstudioImpactoAmbiental estudio, FactorPma componente) throws ServiceException {
        List<CalidadParametro> calidadComponentes = calidadParametroService.getCalidadParametro(estudio, componente);
        return calidadComponentes;
    }


    public List<Normativas> getNormativas(EstudioImpactoAmbiental estudio, FactorPma componente) throws ServiceException {
        return normativasFacade.listarNormativasXComponente(componente.getNombre(), estudio.getProyectoLicenciamientoAmbiental().getTipoSector().getId(), estudio.getProyectoLicenciamientoAmbiental().getTipoEstudio().getId());
    }

    public void guardar(List<CalidadParametro> calidadComponenteAlmacenadas, List<CalidadParametro> calidadParametros, EstudioImpactoAmbiental estudio, FactorPma componente) {
        try {

            //contruccion de variables temporales
            List<CalidadParametro> calidadParametrosNoVerificadas = new ArrayList<CalidadParametro>();
            List<ResultadoAnalisis> resultadosNoVerificados = new ArrayList<ResultadoAnalisis>();
            Set<Muestra> muestrasNoVerificadas = new HashSet<Muestra>();
            List<ResultadoAnalisis> resultados = new ArrayList<ResultadoAnalisis>();
            Set<Muestra> muestras = new HashSet<Muestra>();

            //Asignación de variables almacenadas
            calidadParametrosNoVerificadas.addAll(calidadComponenteAlmacenadas);
            for (CalidadParametro calidadNoVerificada : calidadParametrosNoVerificadas) {
                resultadosNoVerificados.addAll(calidadNoVerificada.getResultadosAnalisis());
                for (ResultadoAnalisis resultadoNoVerificado : calidadNoVerificada.getResultadosAnalisis()) {
                    muestrasNoVerificadas.add(resultadoNoVerificado.getMuestra());

                }
            }
            //Asignación de variables a guardar
            if (calidadParametros != null || calidadParametros.isEmpty()) {
                guardarCalidadParametro(calidadParametros, estudio, componente, calidadParametrosNoVerificadas, resultados, muestras);
                guardarMuestras(muestrasNoVerificadas, muestras);
                guardarResultados(resultadosNoVerificados, resultados);
                eliminarNoVerificadas(calidadParametrosNoVerificadas, resultadosNoVerificados, muestrasNoVerificadas);
                recargarObjetosHijos(calidadParametros);
            }
        } catch (RuntimeException e) {
            LOG.error(e, e);
        }
    }

    private void guardarCalidadParametro(List<CalidadParametro> calidadParametros, EstudioImpactoAmbiental estudio, FactorPma factor, List<CalidadParametro> calidadParametrosNoVerificadas, List<ResultadoAnalisis> resultados, Set<Muestra> muestras) {
        for (CalidadParametro calidad : calidadParametros) {
            calidad.setEstudioImpactoAmbiental(estudio);
            calidad.setComponente(factor);

            Laboratorio lab = new Laboratorio();
            lab = calidad.getLaboratorio();
            crudServiceBean.saveOrUpdate(lab);
            calidad.setLaboratorio(lab);

            if (calidad.getResultadosAnalisis() != null) {
                for (ResultadoAnalisis resultado : calidad.getResultadosAnalisis()) {
                    resultado.setCalidadParametro(calidad);
                    muestras.add(resultado.getMuestra());
                }
                resultados.addAll(calidad.getResultadosAnalisis());
            }
            crudServiceBean.saveOrUpdate(calidad);
            calidadParametrosNoVerificadas.remove(calidad);
        }
    }

    private void guardarMuestras(Set<Muestra> muestrasNoVerificadas, Set<Muestra> muestras) {
        for (Muestra muestra : muestras) {
            muestra.getCoordenadaGeneral().setNombreTabla(Muestra.class.getName());
            muestra.getCoordenadaGeneral().setIdTable(muestra.getId());
            muestra.getCoordenadaGeneral().setIdTable(0);
            crudServiceBean.saveOrUpdate(muestra);
            muestra.getCoordenadaGeneral().setIdTable(muestra.getId());
            crudServiceBean.saveOrUpdate(muestra.getCoordenadaGeneral());
            muestrasNoVerificadas.remove(muestra);
        }
    }

    private void guardarResultados(List<ResultadoAnalisis> resultadosNoVerificados, List<ResultadoAnalisis> resultados) {
        for (ResultadoAnalisis resultadoAnalisis : resultados) {
            crudServiceBean.saveOrUpdate(resultadoAnalisis);
            resultadosNoVerificados.remove(resultadoAnalisis);
        }
    }

    private void recargarObjetosHijos(List<CalidadParametro> calidadParametros) {
        for (CalidadParametro calidad : calidadParametros) {
            calidad.getParametroNormativas().getTablaNormativas().getNormativa().toString();
            if (calidad.getResultadosAnalisis() != null) {
                calidad.getResultadosAnalisis().size();

            }
        }
    }

    private void eliminarNoVerificadas(List<CalidadParametro> calidadParametrosNoVerificadas, List<ResultadoAnalisis> resultadosNoVerificados, Set<Muestra> muestrasNoVerificadas) {
        crudServiceBean.delete(resultadosNoVerificados);
        crudServiceBean.delete(calidadParametrosNoVerificadas);
        crudServiceBean.delete(new ArrayList<EntidadBase>(muestrasNoVerificadas));
    }

    public FactorPma getComponente(Integer id){
        return crudServiceBean.find(FactorPma.class,id);
    }
}
