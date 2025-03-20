/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.*;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.comun.bean.AdicionarDesechosPeligrososBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.EliminacionDesechoFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.entidadResponsable.facade.EntidadResponsableFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.tramiteresolver.RegistroGeneradorTramiteResolver;
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
public class RegistroGeneradorDesechoActualizarController implements Serializable {

    private static final long serialVersionUID = 6792843606389699865L;

    private static final Logger LOG = Logger.getLogger(RegistroGeneradorDesechoActualizarController.class);

    @EJB
    private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @Getter
    @Setter
    private String codigo;

    @Getter
    private boolean permitirNotificar;

    @EJB
    ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;
    @EJB
	private AreaFacade areaFacade;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

    @Getter
    @Setter
    private GeneradorDesechosPeligrosos generadorDesechosPeligrosos;

    @EJB
    EliminacionDesechoFacade eliminacionDesechoFacade;

    @Getter
    @Setter
    private Map<String, Boolean> datos;

    @ManagedProperty(value = "#{registroGeneradorDesechoBean}")
    @Getter
    @Setter
    RegistroGeneradorDesechoBean registroGeneradorDesechoBean;

    @EJB
    CrudServiceBean crudServiceBean;
    
    @EJB
	private EntidadResponsableFacade entidadResponsableFacade;

    @PostConstruct
    private void init() {
        permitirNotificar = false;
        JsfUtil.getBean(VerRegistroGeneradorDesechoBean.class).setGeneradorDesechosPeligrosos(null);
    }

    public void validar() {
        try {
            permitirNotificar = true;

            GeneradorDesechosPeligrosos ultimoRGD = registroGeneradorDesechosFacade.buscarGeneradorPorCodigo(codigo, JsfUtil.getLoggedUser()
                    .getNombre());
            if(ultimoRGD != null){//Si existe un RGD previo con el código ingresado
                if (!ultimoRGD.isFinalizado()){
                    JsfUtil.addMessageInfo("Estimado usuario, su proceso de 'Registro de Generador de Desechos Especiales y/o Peligros' está en curso, por lo tanto no puede iniciar una actualización.");
                    JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
                }else{
                    JsfUtil.getBean(VerRegistroGeneradorDesechoBean.class).setGeneradorDesechosPeligrosos(ultimoRGD );

                    if (JsfUtil.getBean(VerRegistroGeneradorDesechoBean.class).getGeneradorDesechosPeligrosos() == null)
                        JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_NO_RESULTADOS);
                    else{
                    	String codSolicitud = JsfUtil.getBean(VerRegistroGeneradorDesechoBean.class).getGeneradorDesechosPeligrosos().getSolicitud();
                        //generadorDesechosPeligrosos = this.registroGeneradorDesechosFacade.get(codSolicitud,JsfUtil.getLoggedUser().getArea().getId());
//                        generadorDesechosPeligrosos = this.registroGeneradorDesechosFacade.get(codSolicitud);
                        generadorDesechosPeligrosos = this.registroGeneradorDesechosFacade.cargarGeneradorFullPorId(ultimoRGD.getId(), true);
                        this.registroGeneradorDesechoBean.setGeneradorDesechosPeligrosos(generadorDesechosPeligrosos);
                        this.registroGeneradorDesechoBean.cargarRegistroGuardadoActualizacion();
                    }
                }
            }


        } catch (Exception e) {
            LOG.error("Error al cargar el registro de generador.", e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }

    public String iniciarActualizacion() {

        try {

            boolean proyectoAsociado = this.generadorDesechosPeligrosos.getProyecto() != null && this.generadorDesechosPeligrosos.getProyecto().getId() != null;
            boolean tieneRegistroEnCurso = false;
            if (proyectoAsociado) {

                tieneRegistroEnCurso = this.eliminacionDesechoFacade.proyectoTieneGeneradorEnCurso(this.generadorDesechosPeligrosos.getProyecto().getId());

            } else {

                tieneRegistroEnCurso = this.eliminacionDesechoFacade.proyectoTieneGeneradorEnCurso(JsfUtil.getBean(VerRegistroGeneradorDesechoBean.class).getGeneradorDesechosPeligrosos()
                        .getSolicitud());
            }

            if (!tieneRegistroEnCurso) {

                if (registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos() != null && registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId() != null) {

                    GeneradorDesechosPeligrosos nuevoGenerador;

//                    boolean registroFisico = registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getUsuarioCreacion() == null ||
//                            registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getUsuario() == null;
                    
                    boolean registroFisico = registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().isFisico() == true;
                    
                    boolean bloquearGuardarNuevo=generadorDesechosPeligrosos.getSolicitud().compareTo(generadorDesechosPeligrosos.getCodigo())==0 && 
                    		(!generadorDesechosPeligrosos.getSolicitud().startsWith("MAE-SOL-RGD") 
                    				|| !generadorDesechosPeligrosos.getSolicitud().startsWith("MAAE-SOL-RGD")
                                    || !generadorDesechosPeligrosos.getSolicitud().startsWith("MAATE-SOL-RGD"))
                    				&& (generadorDesechosPeligrosos.getGeneradorDesechosDesechoPeligrosos() != null 
                    				&& generadorDesechosPeligrosos.getGeneradorDesechosDesechoPeligrosos().isEmpty()) ;
                   
                    if (!registroFisico){

                        this.generadorDesechosPeligrosos = registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos();

                        List<DesechoPeligroso> listaSeleccionados = new ArrayList<DesechoPeligroso>();
                        listaSeleccionados.addAll(JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).getDesechosSeleccionados());

                        List<GeneradorDesechosDesechoPeligrosoDatosGenerales> listaDatosGenerales = new ArrayList<GeneradorDesechosDesechoPeligrosoDatosGenerales>();
                        listaDatosGenerales.addAll(JsfUtil.getBean(DatosDesechosBean.class).getDesechosPeligrososDatosGenerales());

                        List<GeneradorDesechosDesechoPeligrosoEtiquetado> listaPeligrososEtiquetados = new ArrayList<GeneradorDesechosDesechoPeligrosoEtiquetado>();
                        listaPeligrososEtiquetados.addAll(JsfUtil.getBean(EnvasadoEtiquetadoDesechosBean.class).getDesechosPeligrososEtiquetados());

                        Map<DesechoPeligroso, List<IncompatibilidadDesechoPeligroso>> mapaIncompatibilidadesDesechos = new HashMap<DesechoPeligroso, List<IncompatibilidadDesechoPeligroso>>();

                        List<PuntoRecuperacion> puntosRecuperacion = JsfUtil.getBean(PuntosRecuperacionBean.class).getPuntosRecuperacion();
                        List<GeneradorDesechosDesechoPeligroso>  desechosDesechoPeligrosos = generadorDesechosPeligrosos.getGeneradorDesechosDesechoPeligrosos();
                        
                    	//si el rgd anterior está asignado a una DP se busca la oficina tecnica correspondiente 
                        Area areaResponsable = generadorDesechosPeligrosos.getAreaResponsable();
                    	if(!generadorDesechosPeligrosos.getResponsabilidadExtendida() && 
                    			(generadorDesechosPeligrosos.getAreaResponsable() == null 
                    			|| (generadorDesechosPeligrosos.getAreaResponsable().getTipoArea().getSiglas().equals("DP") && 
                    			!generadorDesechosPeligrosos.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)))) {
                    		areaResponsable = entidadResponsableFacade.buscarAreaDireccionZonalPorUbicacion(puntosRecuperacion
            						.get(0).getUbicacionesGeografica().getUbicacionesGeografica());
                    	}
                    	//valida que existan los usuarios requeridos para iniciar el proceso antes de guardar la información 
                    	if(!validarUsuarios(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(), areaResponsable)) {
                    		JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
                            return "";
                    	}

                        nuevoGenerador = new GeneradorDesechosPeligrosos();
                        nuevoGenerador.setEstado(true);
                        nuevoGenerador.setFinalizado(false);
                        nuevoGenerador.setAreaResponsable(areaResponsable);
                        nuevoGenerador.setResponsabilidadExtendida(generadorDesechosPeligrosos.getResponsabilidadExtendida());
                        nuevoGenerador.setEliminacionDentroEstablecimiento(generadorDesechosPeligrosos.isEliminacionDentroEstablecimiento());
                        nuevoGenerador.setCodigo(generadorDesechosPeligrosos.getCodigo());
                        nuevoGenerador.setSolicitud(generadorDesechosPeligrosos.getSolicitud());
                        nuevoGenerador.setRespuestasAclaratorias(generadorDesechosPeligrosos.getRespuestasAclaratorias());
                        nuevoGenerador.setProyecto(generadorDesechosPeligrosos.getProyecto());
                        nuevoGenerador.setTipoSector(generadorDesechosPeligrosos.getTipoSector());
                        nuevoGenerador.setPoliticaDesecho(generadorDesechosPeligrosos.getPoliticaDesecho());
                        nuevoGenerador.setPoliticaDesechoActividad(generadorDesechosPeligrosos.getPoliticaDesechoActividad());
                        nuevoGenerador.setUsuario(generadorDesechosPeligrosos.getUsuario());
                        nuevoGenerador.setAreaResponsable(areaResponsable);
                        nuevoGenerador.setJustificacionProponente(generadorDesechosPeligrosos.getJustificacionProponente());
                        nuevoGenerador.setDocumentoBorrador(generadorDesechosPeligrosos.getDocumentoBorrador());
                        nuevoGenerador.setDocumentoJustificacionProponente(generadorDesechosPeligrosos.getDocumentoJustificacionProponente());

                        nuevoGenerador.setGeneradorDesechosDesechoPeligrosos(null);
                        
                        if(!bloquearGuardarNuevo)
                        	this.registroGeneradorDesechosFacade.guardar(nuevoGenerador);

                        for (GeneradorDesechosDesechoPeligroso  generadorDesechosDesechoPeligroso : desechosDesechoPeligrosos){

                            List<IncompatibilidadGeneradorDesechosDesecho> incompatibilidades = new ArrayList<IncompatibilidadGeneradorDesechosDesecho>();
                            incompatibilidades.addAll(generadorDesechosDesechoPeligroso.getIncompatibilidades());

                            List<AlmacenGeneradorDesechoPeligroso> almacenes = new ArrayList<AlmacenGeneradorDesechoPeligroso>();
                            almacenes.addAll(generadorDesechosDesechoPeligroso.getAlmacenGeneradorDesechoPeligrosos());

                            List<GeneradorDesechosEliminador> eliminadores = new ArrayList<GeneradorDesechosEliminador>();
                            eliminadores.addAll(generadorDesechosDesechoPeligroso.getGeneradoresDesechosEliminadores());

                            List<PuntoEliminacion> puntosEliminacion = new ArrayList<PuntoEliminacion>();
                            puntosEliminacion.addAll(generadorDesechosDesechoPeligroso.getPuntosEliminacion());

                            List<GeneradorDesechosRecolector> recolectores = new ArrayList<GeneradorDesechosRecolector>();
                            recolectores.addAll(generadorDesechosDesechoPeligroso.getGeneradoresDesechosRecolectores());

                            GeneradorDesechosDesechoPeligrosoDatosGenerales datosGenerales = generadorDesechosDesechoPeligroso.getGeneradorDesechosDesechoPeligrosoDatosGenerales();
                            datosGenerales.setId(null);
                            datosGenerales.setGeneradorDesechosDesechoPeligroso(null);
                            crudServiceBean.saveOrUpdate(datosGenerales);

                            generadorDesechosDesechoPeligroso.setId(null);
                            generadorDesechosDesechoPeligroso.setGeneradorDesechosPeligrosos(nuevoGenerador);
                            generadorDesechosDesechoPeligroso.setGeneradorDesechosDesechoPeligrosoDatosGenerales(datosGenerales);
                            this.crudServiceBean.saveOrUpdate(generadorDesechosDesechoPeligroso);

                            datosGenerales.setGeneradorDesechosDesechoPeligroso(generadorDesechosDesechoPeligroso);
                            crudServiceBean.saveOrUpdate(datosGenerales);

                            if (!incompatibilidades.isEmpty()){
                                for (IncompatibilidadGeneradorDesechosDesecho incompatibilidad: incompatibilidades){
                                    incompatibilidad.setId(null);
                                    incompatibilidad.setGeneradorDesechosDesechoPeligroso(generadorDesechosDesechoPeligroso);
                                }
                                crudServiceBean.saveOrUpdate(incompatibilidades);
                            }

                            if (!almacenes.isEmpty()){
                                for (AlmacenGeneradorDesechoPeligroso almacen: almacenes){
                                    almacen.setId(null);
                                    almacen.setGeneradorDesechosDesechoPeligroso(generadorDesechosDesechoPeligroso);
                                }
                                crudServiceBean.saveOrUpdate(almacenes);
                            }

                            if (!eliminadores.isEmpty()){
                                for (GeneradorDesechosEliminador eliminador: eliminadores){
                                    eliminador.setId(null);
                                    eliminador.setGeneradorDesechosDesechoPeligroso(generadorDesechosDesechoPeligroso);
                                }
                                crudServiceBean.saveOrUpdate(eliminadores);
                            }

                            if (!puntosEliminacion.isEmpty()){
                                for (PuntoEliminacion pto: puntosEliminacion){
                                    pto.setId(null);
                                    pto.setGeneradorDesechosDesechoPeligroso(generadorDesechosDesechoPeligroso);
                                }
                                crudServiceBean.saveOrUpdate(puntosEliminacion);
                            }

                            if (!recolectores.isEmpty()){
                                for (GeneradorDesechosRecolector recolector: recolectores){
                                    recolector.setId(null);
                                    recolector.setGeneradorDesechosDesechoPeligroso(generadorDesechosDesechoPeligroso);
                                }
                                crudServiceBean.saveOrUpdate(recolectores);
                            }

                        }

                        nuevoGenerador.setGeneradorDesechosDesechoPeligrosos(desechosDesechoPeligrosos);

                        if(!bloquearGuardarNuevo)
                        	registroGeneradorDesechosFacade.guardar(nuevoGenerador, JsfUtil
                                        .getCurrentProcessInstanceId(), puntosRecuperacion,
                                listaSeleccionados, listaDatosGenerales, listaPeligrososEtiquetados,
                                mapaIncompatibilidadesDesechos, null, null,
                                null, null, false, true);

                    }else{
                        nuevoGenerador = generadorDesechosPeligrosos;
                    }

                    registroGeneradorDesechosFacade.iniciarProcesoActualizacionRegistroGenerador(!bloquearGuardarNuevo?nuevoGenerador:generadorDesechosPeligrosos,
                            JsfUtil.getLoggedUser(), RegistroGeneradorTramiteResolver.class,bloquearGuardarNuevo);

                    JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
                    return JsfUtil.actionNavigateTo(JsfUtil.NAVIGATION_TO_BANDEJA);
                }else{
                    LOG.error("Generador nulo o sin id.");
                    JsfUtil.addMessageError("Estimado usuario, ocurrió un error al cargar el registro de generador. Por favor, contacte con mesa de ayuda");
                    return "";
                }
            } else {
                JsfUtil.addMessageError("Estimado usuario, ya se encuentra un proceso de actualización para este registro de generador.");
                return "";
            }

        } catch (ServiceException ex) {//vear: 28/03/16: se divide la excepción para que muestre el mensaje de error
            if (ex.getMessage().contains("No se encontró un usuario ")) {
                LOG.error("Error al continuar con el proceso de actualizacion de registro de generador.", ex);
                JsfUtil.addMessageError(ex.getMessage());
                return "";
            } else {
                LOG.error("Error al continuar con el proceso de actualizacion de registro de generador.", ex);
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
                return "";
            }
        } catch (JbpmException ex) {
            LOG.error("Error al continuar con el proceso de actualizacion de registro de generador.", ex);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
            return "";
        } catch (Exception e) {
            LOG.error("Error al guardar el registro de generador.", e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            return "";
        }
    }
    
    public Boolean validarUsuarios(GeneradorDesechosPeligrosos generador, Area areaResponsable) {
    	try {
	    	Usuario director = null;
			Usuario coordinador = null;
	
			if (generador.getResponsabilidadExtendida() != null && generador.getResponsabilidadExtendida()) {
				director = areaFacade.getUsuarioPorRolArea("role.gerente", areaResponsable);
				coordinador = areaFacade.getUsuarioPorRolArea("role.pc.coordinador", areaResponsable);
			} else {
				if(areaResponsable.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
					director = areaFacade.getDirectorProvincial(areaResponsable);
            	} else {
            		director = areaFacade.getDirectorProvincial(areaResponsable.getArea());
            	}
				coordinador = areaFacade.getCoordinadorProvincialRegistro(areaResponsable);
			}
			
			if(director != null && coordinador != null)
				return true;
			else 
				return false;
    	} catch (Exception e) {
            LOG.error("Error al guardar el registro de generador.", e);
            return false;
        }
    }
}
