/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.domain.*;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.AdicionarDesechosPeligrososBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 11/06/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class RegistroGeneradorDesechoBean implements Serializable {

    private static final long serialVersionUID = -1736929462060748008L;

    private static final Logger LOGGER = Logger.getLogger(RegistroGeneradorDesechoBean.class);

    public static final String[] TIPOS_ELIMINACION_OTROS = new String[]{"OMT1"};

    public static final String[] TIPOS_ELIMINACION_OTROS1 = new String[]{"OMT1"};

    @EJB
    private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoFacade;

    @EJB
    private OrganizacionFacade organizacionFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;

    @Setter
    private GeneradorDesechosPeligrosos generadorDesechosPeligrosos;
    
    @Setter
    private GeneradorDesechosPeligrosos documentosCargados;

    @Getter
    private List<PoliticaDesecho> politicasDesechos;

    @Getter
    @Setter
    private List<PoliticaDesechoActividad> politicasDesechosActividades;

    @Getter
    private List<TipoSector> tiposSectores;

    @Getter
    private Organizacion organizacion;

    @Getter
    private UbicacionesGeografica ubicacionesGeografica;

    @Getter
    private String direccion;

    @Getter
    private String telefono;

    @Getter
    @Setter
    private boolean permitirContinuar;

    @Getter
    @Setter
    private boolean aceptarCondiciones;

    @Getter
    @Setter
    private Integer idDesecho;

    @Getter
    @Setter
    private boolean actualizar;

    @Getter
    @Setter
    private Map<String, Boolean> datos;//vear: 02/03/2016

    @Setter
    @Getter
    private boolean guardado;//vear: 04/03/2016: para la navegacion del wizard

    @Setter
    @Getter
    private boolean pasoNueve;//vear: 04/03/2016: para la navegacion del wizard

    @EJB
    ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;


    @PostConstruct
    public void init() throws Exception {

        guardado = false;//vear: 04/03/2016: para la navegacion en el wizard
        permitirContinuar = true;

        politicasDesechos = registroGeneradorDesechosFacade.getPoliticasDesechos();
        tiposSectores = registroGeneradorDesechosFacade.getTiposSectores();

        validacionesDesechos();
        validacionesTipoEliminacionDesechos();
        validacionesTipoEliminacionDesechos1();

        cargarDatosUsuario(generadorDesechosPeligrosos == null ? JsfUtil.getLoggedUser() : generadorDesechosPeligrosos.getUsuario());

        String accion = JsfUtil.getCurrentTask() != null ? (String)JsfUtil.getCurrentTask().getVariable("accion") : null;

        this.actualizar = accion == null||(accion != null && accion.equalsIgnoreCase("actualizacion.jsf"));

        if (actualizar){
            initActualizar();
        }
        else
         if (initGuardado())
            return;

        if (!actualizar) {
            try {
                Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),
                        JsfUtil.getBean(BandejaTareasBean.class).getProcessId());
                if (variables.containsKey(Constantes.ID_PROYECTO)) {
                    getGeneradorDesechosPeligrosos().setProyecto(proyectoFacade.getProyectoPorId(Integer.parseInt(variables.get(Constantes.ID_PROYECTO).toString())));
                }
                String numeroSolicitud = variables.get(GeneradorDesechosPeligrosos.VARIABLE_NUMERO_SOLICITUD)
                        .toString();
                getGeneradorDesechosPeligrosos().setSolicitud(numeroSolicitud);
            } catch (Exception e) {
                permitirContinuar = false;
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
                LOGGER.error("Error recuperando las variables del proceso de Registro de generador", e);
            }
        }
    }

    private void initActualizar() {
        try {
            Object idGeneradorValue = JsfUtil.getCurrentTask() != null ? JsfUtil.getCurrentTask().getVariable(
                    GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR) : null;
            if (idGeneradorValue != null && !idGeneradorValue.toString().isEmpty()) {
                cargarRegistroGuardado(Integer.parseInt(idGeneradorValue.toString()));
           }
        } catch (Exception e) {
            LOGGER.error("Error cargando los datos del registro de generador.", e);
        }
    }

    public boolean initGuardado() {
        try {
            Object idGeneradorValue = JsfUtil.getCurrentTask() != null ? JsfUtil.getCurrentTask().getVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR) : null;
            if (idGeneradorValue != null && !idGeneradorValue.toString().isEmpty()) {
                cargarRegistroGuardado(Integer.parseInt(idGeneradorValue.toString()));
                if (generadorDesechosPeligrosos.isPersisted())
                    return true;
            }
        } catch (Exception e) {
            LOGGER.error("Error cargando los datos del registro de generador.", e);
        }
        return false;
    }

    private void cargarRegistroGuardado(Integer id) throws Exception {
    	generadorDesechosPeligrosos = registroGeneradorDesechosFacade.cargarGeneradorFullPorId(id, true);

        datos = new HashMap<String, Boolean>();//vear: 03/03/2016: para la navegacion del wizard


        if (generadorDesechosPeligrosos.getResponsabilidadExtendida() != null
                && generadorDesechosPeligrosos.getResponsabilidadExtendida()) {
            updatePoliticasActividades();

            if (generadorDesechosPeligrosos.getPoliticaDesechoActividad() != null
                    && generadorDesechosPeligrosos.getPoliticaDesechoActividad().getDesechoPeligroso() != null)
                //idDesecho = generadorDesechosPeligrosos.getPoliticaDesechoActividad().getDesechoPeligroso().getId();
            	idDesecho = generadorDesechosPeligrosos.getPoliticaDesechoActividad().getId();

        }

        JsfUtil.getBean(PuntosRecuperacionBean.class).setPuntosRecuperacion(
                generadorDesechosPeligrosos.getPuntosRecuperacion());

        JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setDesechosSeleccionados(
                generadorDesechosPeligrosos.getDesechosPeligrosos());


        JsfUtil.getBean(DatosDesechosBean.class).setDesechosPeligrososDatosGenerales(
                generadorDesechosPeligrosos.getDatosGenerales());

        JsfUtil.getBean(EnvasadoEtiquetadoDesechosBean.class).setDesechosPeligrososEtiquetados(
                generadorDesechosPeligrosos.getEnvasadosEtiquetados());

        JsfUtil.getBean(IncompatibilidadesDesechosBean.class).setIncompatibilidadesDesechos(
                generadorDesechosPeligrosos.getIncompatibilidades());


        JsfUtil.getBean(AlmacenamientoTemporalDesechosBean.class).setGeneradoresDesechosAlmacenes(
                generadorDesechosPeligrosos.getAlmacenes());
        JsfUtil.getBean(AdicionarDesechosAlmacenBean.class).init();
        JsfUtil.getBean(RealizaEliminacionDesechosInstalacionBean.class).setEliminaDesechosDentroInstalacion(
                generadorDesechosPeligrosos.isEliminacionDentroEstablecimiento());
        if (generadorDesechosPeligrosos.isEliminacionDentroEstablecimiento())
            JsfUtil.getBean(EliminacionDesechosInstalacionBean.class).setPuntosEliminacion(
                    generadorDesechosPeligrosos.getPuntosEliminacion());
        JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).setGeneradoresDesechosRecolectores(
                generadorDesechosPeligrosos.getGeneradoresDesechosRecolectores());
        JsfUtil.getBean(EliminacionDesechosFueraInstalacionBean.class).setGeneradoresDesechosEliminadores(
                generadorDesechosPeligrosos.getGeneradoresDesechosEliminadores());
        cargarDatos();
    }

    public void cargarRegistroGuardadoActualizacion() throws Exception {

        datos = new HashMap<String, Boolean>();//vear: 03/03/2016: para la navegacion del wizard


        if (generadorDesechosPeligrosos.getResponsabilidadExtendida() != null
                && generadorDesechosPeligrosos.getResponsabilidadExtendida()) {
            updatePoliticasActividades();

            if (generadorDesechosPeligrosos.getPoliticaDesechoActividad() != null
                    && generadorDesechosPeligrosos.getPoliticaDesechoActividad().getDesechoPeligroso() != null)
                //idDesecho = generadorDesechosPeligrosos.getPoliticaDesechoActividad().getDesechoPeligroso().getId();
            	idDesecho = generadorDesechosPeligrosos.getPoliticaDesechoActividad().getId();
        }

        JsfUtil.getBean(PuntosRecuperacionBean.class).setPuntosRecuperacion(
                generadorDesechosPeligrosos.getPuntosRecuperacion());

        JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setDesechosSeleccionados(
                generadorDesechosPeligrosos.getDesechosPeligrosos());


        JsfUtil.getBean(DatosDesechosBean.class).setDesechosPeligrososDatosGenerales(
                generadorDesechosPeligrosos.getDatosGenerales());

        JsfUtil.getBean(EnvasadoEtiquetadoDesechosBean.class).setDesechosPeligrososEtiquetados(
                generadorDesechosPeligrosos.getEnvasadosEtiquetados());

        JsfUtil.getBean(IncompatibilidadesDesechosBean.class).setIncompatibilidadesDesechos(
                generadorDesechosPeligrosos.getIncompatibilidades());


        JsfUtil.getBean(AlmacenamientoTemporalDesechosBean.class).setGeneradoresDesechosAlmacenes(
                generadorDesechosPeligrosos.getAlmacenes());
        JsfUtil.getBean(AdicionarDesechosAlmacenBean.class).init();
        JsfUtil.getBean(RealizaEliminacionDesechosInstalacionBean.class).setEliminaDesechosDentroInstalacion(
                generadorDesechosPeligrosos.isEliminacionDentroEstablecimiento());
        if (generadorDesechosPeligrosos.isEliminacionDentroEstablecimiento())
            JsfUtil.getBean(EliminacionDesechosInstalacionBean.class).setPuntosEliminacion(
                    generadorDesechosPeligrosos.getPuntosEliminacion());
        JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).setGeneradoresDesechosRecolectores(
                generadorDesechosPeligrosos.getGeneradoresDesechosRecolectores());
        JsfUtil.getBean(EliminacionDesechosFueraInstalacionBean.class).setGeneradoresDesechosEliminadores(
                generadorDesechosPeligrosos.getGeneradoresDesechosEliminadores());
    }

    /**
     * vear
     * 02/03/2016
     * para navegar por el wizard
     */
    private void cargarDatos() {

        if (generadorDesechosPeligrosos.getResponsabilidadExtendida() != null) {
            datos.put("paso1", true);
            guardado = true;
        }

        if (generadorDesechosPeligrosos.getTipoSector() != null) {
            datos.put("paso2", true);
        }
        if (JsfUtil.getBean(PuntosRecuperacionBean.class).getPuntosRecuperacion().size() > 0) {
            datos.put("paso3", true);//
        }

        if (JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).getDesechosSeleccionados().size() > 0) {
            datos.put("paso4", true);//
        }

        if (JsfUtil.getBean(DatosDesechosBean.class).getDesechosPeligrososDatosGenerales().size() > 0) {
            datos.put("paso5", true);//
        }

        if (JsfUtil.getBean(EnvasadoEtiquetadoDesechosBean.class).getDesechosPeligrososEtiquetados().size() > 0) {
        	Boolean habilitarPaso6 = true;
        	for (GeneradorDesechosDesechoPeligrosoEtiquetado desechoEtiquetado : JsfUtil.getBean(EnvasadoEtiquetadoDesechosBean.class).getDesechosPeligrososEtiquetados()) {
				if(desechoEtiquetado.getModeloEtiqueta() == null) {
					habilitarPaso6 = false;
					break;
				}
			}
            datos.put("paso6", habilitarPaso6);//
        }

        if (JsfUtil.getBean(IncompatibilidadesDesechosBean.class).getIncompatibilidadesDesechos().size() > 0) {
            DesechoPeligroso desechoPeligroso = JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).getDesechosSeleccionados().get(0);
            if (JsfUtil.getBean(IncompatibilidadesDesechosBean.class).getIncompatibilidadesDesechos().get(desechoPeligroso).size() > 0) {
                datos.put("paso7", true);//
            }
        }


        if (JsfUtil.getBean(AlmacenamientoTemporalDesechosBean.class).getGeneradoresDesechosAlmacenes().size() > 0) {
            datos.put("paso8", true);//
            generadorDesechosPeligrosos.getAlmacenes();
        }

        //vear: no se pone el paso 9 para que exija grabar si cambia a si
//        if (!JsfUtil.getBean(RealizaEliminacionDesechosInstalacionBean.class).isEliminaDesechosDentroInstalacion()) {
//            datos.put("paso9", true);//
//        }

        if (!generadorDesechosPeligrosos.getGeneradorDesechosDesechoPeligrosos().isEmpty() && generadorDesechosPeligrosos.getGeneradorDesechosDesechoPeligrosos().get(0).getGeneradoresDesechosRecolectores().size() > 0) {
            datos.put("paso10", true);//
        }
        if (JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).getGeneradoresDesechosRecolectores().size() > 0) {
            datos.put("paso11", true);//
        }
        if (JsfUtil.getBean(EliminacionDesechosFueraInstalacionBean.class).getGeneradoresDesechosEliminadores().size() > 0) {
            datos.put("paso12", true);//
        }
        if (generadorDesechosPeligrosos.getDocumentoJustificacionProponente() != null) {
            datos.put("paso13", true);//
        }
    }


    public void updatePoliticasActividades() {
        politicasDesechosActividades = registroGeneradorDesechosFacade
                .getPoliticasDesechoActividad(generadorDesechosPeligrosos.getPoliticaDesecho());
    }

    public GeneradorDesechosPeligrosos getGeneradorDesechosPeligrosos() {
        return generadorDesechosPeligrosos == null ? generadorDesechosPeligrosos = new GeneradorDesechosPeligrosos()
                : generadorDesechosPeligrosos;
    }

    public void cargarDatosUsuario(Usuario usuario) {
        try {
            getGeneradorDesechosPeligrosos().setUsuario(usuario);
            organizacion = organizacionFacade.buscarPorPersona(getGeneradorDesechosPeligrosos().getUsuario().getPersona(),
                    JsfUtil.getLoggedUser().getNombre());
            if (organizacion != null) {
                ubicacionesGeografica = ubicacionGeograficaFacade.buscarPorId(organizacion.getIdUbicacionGeografica());
                try {
                    direccion = organizacion.obtenerContactoPorId(2).getValor();
                } catch (Exception e) {
                }
                try {
                    telefono = organizacion.obtenerContactoPorId(6).getValor();
                } catch (Exception e) {
                }
            } else {
                ubicacionesGeografica = ubicacionGeograficaFacade.buscarPorId(JsfUtil.getLoggedUser().getPersona()
                        .getIdUbicacionGeografica());
                try {
                    direccion = getGeneradorDesechosPeligrosos().getUsuario().getPersona().obtenerContactoPorId(2)
                            .getValor();
                } catch (Exception e) {
                }
                try {
                    telefono = getGeneradorDesechosPeligrosos().getUsuario().getPersona().obtenerContactoPorId(6)
                            .getValor();
                } catch (Exception e) {
                }
            }
        } catch (ServiceException exception) {
        }
    }

    public void validacionesDesechos() {
        JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setCompleteOperationOnDelete(new CompleteOperation() {

            @Override
            public Object endOperation(Object object) {
                DesechoPeligroso desechoPeligroso = (DesechoPeligroso) object;
                JsfUtil.getBean(DatosDesechosBean.class).validacionesDesechos(desechoPeligroso);
                JsfUtil.getBean(EnvasadoEtiquetadoDesechosBean.class).validacionesDesechos(desechoPeligroso);
                JsfUtil.getBean(AlmacenamientoTemporalDesechosBean.class).validacionesDesechos(desechoPeligroso);
                JsfUtil.getBean(IncompatibilidadesDesechosBean.class).validacionesDesechos(desechoPeligroso);
                JsfUtil.getBean(EliminacionDesechosInstalacionBean.class).validacionesDesechos(desechoPeligroso);
                JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).validacionesDesechos(desechoPeligroso);
                JsfUtil.getBean(EliminacionDesechosFueraInstalacionBean.class).validacionesDesechos(desechoPeligroso);

                JsfUtil.getBean(JustificacionProponenteBean.class).setSeEliminoPuntoGeneracionODesecho(true);
                return null;
            }
        });
    }

    public void validacionesTipoEliminacionDesechos() {
        JsfUtil.getBean(TipoEliminacionDesechoBean.class).setCompleteOperationOnAdd(new CompleteOperation() {

            @Override
            public Object endOperation(Object object) {
                boolean resultado = JsfUtil.getBean(TipoEliminacionDesechoBean.class).isClaveIgual(
                        TIPOS_ELIMINACION_OTROS);
                if (resultado)
                    JsfUtil.getBean(TipoEliminacionDesechoBean.class).setOtroSeleccionado(true);

				/*
                 * JsfUtil.getBean(PermisoAmbientalBean.class).resetSelection();
				 * RequestContext
				 * .getCurrentInstance().update("form:permisosAmbientalesContainer"
				 * ); RequestContext.getCurrentInstance().update(
				 * "form:permisosCantidadesContainer");
				 */

                return null;
            }
        });
    }

    public void validacionesTipoEliminacionDesechos1() {
        JsfUtil.getBean(TipoEliminacionDesechoBean1.class).setCompleteOperationOnAdd(new CompleteOperation() {

            @Override
            public Object endOperation(Object object) {
                RequestContext.getCurrentInstance().update("form:identificarEmpresasContainer1");
                if (JsfUtil.getBean(EliminacionDesechosFueraInstalacionBean.class).getGeneradorDesechosEliminador()
                        .getDesechoPeligroso() != null
                        && JsfUtil.getBean(TipoEliminacionDesechoBean1.class).getTipoEliminacionDesechoSeleccionada() != null)
                    JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).initSedes();

                boolean resultado1 = JsfUtil.getBean(TipoEliminacionDesechoBean1.class).isClaveIgual(
                        TIPOS_ELIMINACION_OTROS1);
                if (resultado1) {
                    JsfUtil.getBean(TipoEliminacionDesechoBean1.class).setOtroSeleccionado(true);
                    JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).setSeleccionMultiple(true);
                } else
                    JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).setSeleccionMultiple(false);
                RequestContext.getCurrentInstance().update(
                        "form:" + JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).getTbl_empresas());
                RequestContext.getCurrentInstance().update(
                        "form:" + JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).getBtnSeleccionar());

                return null;
            }
        });
    }

    public TipoSector getTipoSectorOtros() {
        for (TipoSector tipoSector : getTiposSectores()) {
            if (tipoSector.getId().equals(TipoSector.TIPO_SECTOR_OTROS))
                return tipoSector;
        }
        return null;
    }

    public String getClassName() {
        return GeneradorDesechosPeligrosos.class.getSimpleName();
    }

}
