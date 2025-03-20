/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoria2.v2.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;

import lombok.Getter;
import lombok.Setter;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.categoria2.v2.bean.InventarioForestalPmaBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.builders.TaskSummaryCustomBuilder;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.InventarioForestalPma;
import ec.gob.ambiente.suia.inventarioForestalPma.facade.InventarioForestalPmaFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.v2.CategoriaIIFacadeV2;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validaPago.PagoConfiguracionesUtil;

/**
 * @author frank torres
 */
@ManagedBean
@ViewScoped
public class FichaAmbientalGeneralControllerV2 implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3139973485508002420L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(FichaAmbientalGeneralControllerV2.class);

    private static final String MENSAJE_CATEGORIAII_PAGO = "mensaje.categoriaII.pago";
    private static final String MENSAJE_CATEGORIAII_SIN_PAGO = "mensaje.categoriaII.final";
    private static final String IMAGEN_VALIDAR_PAGO = "/resources/images/mensajes/validar_Pago_tasas.png";

    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{inventarioForestalPmaBean}")
    @Getter
    @Setter
    private InventarioForestalPmaBean inventarioForestalPmaBean;

    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalFacade;
    @EJB
    private InventarioForestalPmaFacade inventarioForestalPmaFacade;
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private CategoriaIIFacadeV2 categoriaIIFacade;

    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    private FichaAmbientalPma fichaAmbiental;

    @Getter
    @Setter
    private String mensaje;

    @Getter
    @Setter
    private byte[] archivo;

    @Getter
    @Setter
    private Boolean descargarFicha = false;
    @Getter
    @Setter
    private Boolean descargarRegistro = false;
    @Getter
    @Setter
    private Boolean completado = false;

    @Getter
    @Setter
    private boolean mostrarMensaje = false;

    @Getter
    @Setter
    private String mensajeFinalizacion;

    @Getter
    @Setter
    private String pathImagenFinalizacion;

    /**** modulo pago 2016-03-03 ****/
    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciaFacade;
    
    @Inject
    private PagoConfiguracionesUtil pagoConfiguracionesUtil;
    /**** modulo pago 2016-03-03 ****/
    
    
    @PostConstruct
    public void inicializarDatos() {completado = validarFichaCompletada();}

    public Boolean validarFichaCompletada() {
        CatalogoCategoriaSistema catalogo = proyectosBean.getProyecto()
                .getCatalogoCategoria();
        fichaAmbiental = fichaAmbientalPmaFacade
                .getFichaAmbientalPorIdProyecto(proyectosBean.getProyecto()
                        .getId());
        if (catalogo.getCodigo().equals(Constantes.SECTOR_HIDROCARBURO_CODIGO)) {
            fichaAmbiental.setValidarDescripcionAreaImplantacion(true);
        }
        try {
        	ppc();
        	if (getTipoRegistro().equals("sinPPC") ||  getTipoRegistro().equals("opcional")){
        	
            return fichaAmbiental.getValidarDatosGenerales() != null
                    && fichaAmbiental.getValidarMarcoLegalReferencial() != null
                    && fichaAmbiental
                    .getValidarDescripcionProyectoObraActividad() != null
                    && fichaAmbiental.getValidarDescripcionProceso() != null
                    && fichaAmbiental.getValidarDescripcionAreaImplantacion() != null
                    && fichaAmbiental
                    .getValidarPrincipalesImpactosAmbientales() != null
                    // && fichaAmbiental.getValidarPlanManejoAmbiental() != null
                    // && fichaAmbiental
                    // .getValidarCronogramaConstruccionOperacionProyecto() !=
                    // null
                    && fichaAmbiental
                    .getValidarCronogramaValoradoPlanManejoAmbiental() != null
                    && fichaAmbiental.getValidarDatosGenerales()
                    && fichaAmbiental.getValidarMarcoLegalReferencial()
                    && fichaAmbiental
                    .getValidarDescripcionProyectoObraActividad()
                    && fichaAmbiental.getValidarDescripcionProceso()
                    && fichaAmbiental.getValidarDescripcionAreaImplantacion()
                    && fichaAmbiental
                    .getValidarPrincipalesImpactosAmbientales()
                    // && fichaAmbiental.getValidarPlanManejoAmbiental()
                    // && fichaAmbiental
                    // .getValidarCronogramaConstruccionOperacionProyecto()
                    && fichaAmbiental
                    .getValidarCronogramaValoradoPlanManejoAmbiental()
                    &&fichaAmbiental.getValidarInventarioForestal()!=false;
        	}else{
        		return fichaAmbiental.getValidarDatosGenerales() != null
                        && fichaAmbiental.getValidarMarcoLegalReferencial() != null
                        && fichaAmbiental
                        .getValidarDescripcionProyectoObraActividad() != null
                        && fichaAmbiental.getValidarDescripcionProceso() != null
                        && fichaAmbiental.getValidarDescripcionAreaImplantacion() != null
                        && fichaAmbiental
                        .getValidarPrincipalesImpactosAmbientales() != null
                        // && fichaAmbiental.getValidarPlanManejoAmbiental() != null
                        // && fichaAmbiental
                        // .getValidarCronogramaConstruccionOperacionProyecto() !=
                        // null
                        && fichaAmbiental
                        .getValidarCronogramaValoradoPlanManejoAmbiental() != null
                        && fichaAmbiental.getValidarDatosGenerales()
                        && fichaAmbiental.getValidarMarcoLegalReferencial()
                        && fichaAmbiental
                        .getValidarDescripcionProyectoObraActividad()
                        && fichaAmbiental.getValidarDescripcionProceso()
                        && fichaAmbiental.getValidarDescripcionAreaImplantacion()
                        && fichaAmbiental
                        .getValidarPrincipalesImpactosAmbientales()
                        // && fichaAmbiental.getValidarPlanManejoAmbiental()
                        // && fichaAmbiental
                        // .getValidarCronogramaConstruccionOperacionProyecto()
                        && fichaAmbiental
                        .getValidarCronogramaValoradoPlanManejoAmbiental()
                        && fichaAmbiental.getValidarParticipacionCiudadana()
                        &&fichaAmbiental.getValidarInventarioForestal()!=false;
        	}
        } catch (Exception e) {
            return false;
        }

        // return false;
    }

    public void validarPagos(){
        try {
        	/**** modulo pago 2016-03-03 ****/
        	List<Object> arregloPago= new  ArrayList<Object>();
        	/**** modulo pago 2016-03-03 ****/
        	
            InventarioForestalPma inventario = inventarioForestalPmaFacade.obtenerInventarioForestalPmaPorFicha(fichaAmbiental.getId());
            if(inventario==null){
            	inventario=new InventarioForestalPma();
            }
            if (!categoriaIIFacade.getExentoPago(proyectosBean.getProyecto()) || inventario.getRemocionVegetal()==true) {
            	if (proyectosBean.getProyecto().getAreaResponsable().getTipoArea().getId().equals(3)){
            		mensajeFinalizacion = (Constantes.getMessageResourceString("mensaje.categoriaII.pago.entes"));
            	}else{
            		mensajeFinalizacion = Constantes.getMessageResourceString(MENSAJE_CATEGORIAII_PAGO);
            	}
                
                float total = 0f;
//                mensajeFinalizacion += "\nPago por concepto de:";
                if(!categoriaIIFacade.getExentoPago(proyectosBean.getProyecto())){
//                    mensajeFinalizacion += "\nRegistro Ambiental: " + Constantes.getPropertyAsString("costo.tramite.registro.ambiental") + " USD";
//                    total += Float.parseFloat(Constantes.getPropertyAsString("costo.tramite.registro.ambiental"));
                	
                	/**** modulo pago 2016-03-03 ****/
                	arregloPago = pagoConfiguracionesUtil.validaCostoRegistroAmbiental(proyectosBean.getProyecto(), bandejaTareasBean.getTarea().getProcessName().toString());
                	total = (float) arregloPago.get(0);
                	mensajeFinalizacion += arregloPago.get(1);
                	String textoPago=" en  BanEcuador a nombre del Ministerio del Ambiente y Agua en la Cuenta corriente Nro. 3001174975 sublínea 370102 y con el número de referencia debe completar la tarea \"Validar pago de servicios administrativos\".";
                	if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
                		textoPago=" en la entidad financiera o lugar de recaudación correspondiente al "+proyectosBean.getProyecto().getAreaResponsable().getAreaName()+". Y con los números de referencia debe completar la tarea \"Validar pago de servicios administrativos\".";
                		if(inventario.getRemocionVegetal()!=null && inventario.getRemocionVegetal()==true){
                		textoPago=" en la entidad financiera o lugar de recaudación correspondiente al "+proyectosBean.getProyecto().getAreaResponsable().getAreaName()+".";
                		}
                	}else if(inventario.getRemocionVegetal()!=null && inventario.getRemocionVegetal()==true) {
                		textoPago=" en  BanEcuador a nombre del Ministerio del Ambiente y Agua en la cuenta corriente Nro. 3001174975 sublínea 370102.";
					}else {
						textoPago=" en  BanEcuador a nombre del Ministerio del Ambiente y Agua en la Cuenta corriente Nro. 3001174975 sublínea 370102 y con el número de referencia debe completar la tarea \"Validar pago de servicios administrativos\".";
					}
                	
                    mensajeFinalizacion += "\nRegistro Ambiental: " + total + " USD"+textoPago;
                    /**** modulo pago 2016-03-03 ****/
                }
                if(inventario.getRemocionVegetal()!=null && inventario.getRemocionVegetal()==true){
                    float coberturaVegetal = inventario.getMaderaEnPie() * Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal"));
                    total += coberturaVegetal;
                    if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
                    	String mensajeActividadPagan="\n Y debe realizar el";
                    	boolean actividadesNoPagan=(proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("11.01.04")||proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.01.01")||proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.01.02"));
                    	mensajeFinalizacion += (actividadesNoPagan ? "" : mensajeActividadPagan) +" pago por concepto de remoción de cobertura vegetal nativa: " + String.valueOf(coberturaVegetal) + " USD en  BanEcuador a nombre del Ministerio del Ambiente y Agua en la cuenta corriente Nro. 0010000777 sublínea 190499 y con los números de referencia debe completar la tarea \"Validar pago de servicios administrativos\" ";
                    }else {
                    	mensajeFinalizacion += "\n Y por Remoción de cobertura vegetal nativa: " + String.valueOf(coberturaVegetal) + " USD. en  BanEcuador a nombre del Ministerio del Ambiente y Agua en la cuenta corriente Nro. 0010000777 sublínea 190499 y con los números de referencia debe completar la tarea \"Validar pago de servicios administrativos\" ";					}
                }
                pathImagenFinalizacion = IMAGEN_VALIDAR_PAGO;
            }
            else {
                mensajeFinalizacion = Constantes.getMessageResourceString(MENSAJE_CATEGORIAII_SIN_PAGO);
        		if(proyectosBean.getProyecto().getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_II && bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("completar registro ambiental")){
        			mostrarMensaje = true;
        		}
            }

            mensajeFinalizacion += " ¿Está seguro que desea finalizar su Registro Ambiental?";
            if (proyectosBean.getProyecto().getAreaResponsable().getTipoArea().getId().equals(3)){
            	mensajeFinalizacion+="\n"+Constantes.getMessageResourceString("mensaje.categoriaII.pago.entes_informacion");
            }
        }
        catch (Exception e){
            JsfUtil.addMessageError("Ocurrió un error al realizar la operación. Por favor intente más tarde.");
            LOG.error("Ocurrió un error al realizar la operación 'validarPagos'", e);
        }
    }

    /***** código para validar el pago (2016/01/21) *****/
    // método para calcular el pago - se personaliza el campo "mensajeFinalizacion" según el costo a pagar
    
    /***** fin código validar pago *****/    
    
    public String completarFicha() {
        try {
        	boolean validarPPC=false;
        	if (!ppc1() || opcional.equals("opcional")){
        		validarPPC=false;
        	}else{
        		validarPPC=true;
        	}
        	if (!validarPPC){
        		if (validarFichaCompletada()){
        			 try {
                         inicializarNotificacion();
                     } catch (JbpmException e) {
                         JsfUtil.addMessageError("Ocurrió un error al realizar la operación. Por favor intente más tarde.");
                         LOG.error("Ocurrió un error al realizar la operación 'inicializar Notificacion'", e);
                         return "";
                     }
                     if(fichaAmbiental.getNumeroOficio()==null || fichaAmbiental.getNumeroOficio().isEmpty() ) {
                         String numeroRes = fichaAmbientalMineriaFacade.generarNoResolucion(proyectosBean.getProyecto());
                         fichaAmbiental.setNumeroOficio(numeroRes);
                         this.fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbiental);
                     }
                     long idInstanciaProceso = bandejaTareasBean.getProcessId();
                     InventarioForestalPma inventario = inventarioForestalPmaFacade.obtenerInventarioForestalPmaPorFicha(fichaAmbiental.getId());
                     Map<String, Object> params = new HashMap<String, Object>();
                     params.put("requiereCoberturaVegetal", inventario.getRemocionVegetal());
                     params.put("metrosCobertura", inventario.getMaderaEnPie());
                     params.put("factorCobertura", Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal")));
                     procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), idInstanciaProceso, params);

                     taskBeanFacade.approveTask(loginBean.getNombreUsuario(),bandejaTareasBean.getTarea().getTaskId(),
                             bandejaTareasBean.getTarea().getProcessInstanceId(), null,
                             loginBean.getPassword(),
                             Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
             		finalizarRegistroambiental();
                     return JsfUtil.actionNavigateToBandeja();
                 } else {
                     JsfUtil.addMessageError("Debe completar los datos del Registro Ambiental.");
                 }
        	}else{
        		
            if (validarFichaCompletada() && fichaAmbiental.getValidarParticipacionCiudadana()) {
                try {
                    inicializarNotificacion();
                } catch (JbpmException e) {
                    JsfUtil.addMessageError("Ocurrió un error al realizar la operación. Por favor intente más tarde.");
                    LOG.error("Ocurrió un error al realizar la operación 'inicializar Notificacion'", e);
                    return "";
                }
                if(fichaAmbiental.getNumeroOficio()==null || fichaAmbiental.getNumeroOficio().isEmpty() ) {
                    String numeroRes = fichaAmbientalMineriaFacade.generarNoResolucion(proyectosBean.getProyecto());
                    fichaAmbiental.setNumeroOficio(numeroRes);
                    this.fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbiental);
                }
                long idInstanciaProceso = bandejaTareasBean.getProcessId();
                InventarioForestalPma inventario = inventarioForestalPmaFacade.obtenerInventarioForestalPmaPorFicha(fichaAmbiental.getId());
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("requiereCoberturaVegetal", inventario.getRemocionVegetal());
                params.put("metrosCobertura", inventario.getMaderaEnPie());
                params.put("factorCobertura", Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal")));
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), idInstanciaProceso, params);

                taskBeanFacade.approveTask(loginBean.getNombreUsuario(),bandejaTareasBean.getTarea().getTaskId(),
                        bandejaTareasBean.getTarea().getProcessInstanceId(), null,
                        loginBean.getPassword(),
                        Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
        		finalizarRegistroambiental();
                return JsfUtil.actionNavigateToBandeja();
            } else {
                JsfUtil.addMessageError("Debe completar los datos del Registro Ambiental.");
            }
        }
       
        
        } catch (Exception e) {
            LOG.error(e, e);
            JsfUtil.addMessageError("Ocurrió un error al realizar la operación. Por favor intente más tarde.");
        }
        return "";
    }

/***
 * funcion para finalizar el registro ambiental en la tarea de validar el pago para registros categoria II
 * fecha:  2018-10-17
 *  
 */
	public void finalizarRegistroambiental(){
		// solo si es categoria II y esta en la tarea de  "Validar Pago por servicios administrativos"
		if(proyectosBean.getProyecto().getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_II && bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("completar registro ambiental")){
			FichaAmbientalGeneralFinalizarControllerV2 fichaAmbientalGeneralFinalizarControllerV2 = JsfUtil
					.getBean(FichaAmbientalGeneralFinalizarControllerV2.class);
			try{
				// recupero la tarea actual que se genero 
				Long processInstanceId = bandejaTareasBean.getProcessId();
				TaskSummary tareaActual = procesoFacade.getCurrenTask(JsfUtil.getLoggedUser(), processInstanceId);
				// si la tarea actual es "Descargar documentos de Registro Ambiental " finalizo el proceso
				if(tareaActual.getName().toLowerCase().contains("descargar documentos de registro ambiental")){
					Map<String, Object> processVariables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), processInstanceId);
					ProcessInstanceLog processLog = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(), processInstanceId);
					bandejaTareasBean.setTarea(new TaskSummaryCustomBuilder().fromSuiaIII(processVariables,	processLog.getProcessName(), tareaActual).build()); 
					// mando a generar el registro ambiental
					fichaAmbientalGeneralFinalizarControllerV2.generarFichaRegistroAmbiental();
					fichaAmbientalGeneralFinalizarControllerV2.setDescargarFicha(true);
					fichaAmbientalGeneralFinalizarControllerV2.setDescargarRegistro(true);
					// mando a generar la resolucion
					fichaAmbientalGeneralFinalizarControllerV2.redireccionarBandeja();
					// finalizo la tarea si se genero la resolucion satisfactoriamente
					if(fichaAmbientalGeneralFinalizarControllerV2.getDescargarRegistro()){
						fichaAmbientalGeneralFinalizarControllerV2.direccionar();
						fichaAmbientalGeneralFinalizarControllerV2.cambiarEstadoTarea();
					}
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			}
		}
	}
	
    public void irABandeja(){
        System.out.println("entra al enviar");
        JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
    }

    @SuppressWarnings("unused")
	public void inicializarNotificacion() throws JbpmException {
        String mensaje = "Estimado usuario, en cumplimiento de la normativa ambiental actual, usted debe realizar el Registro de Generador de Desechos Peligrosos y/o Especiales en un plazo de sesenta (60) días.";

        String mensajeAutoridades = "El sistema de Regularización y Control Ambiental notifica que se ha emitido el Registro Ambiental al proyecto "
                + proyectosBean.getProyecto().getCodigo()
                + ". En cumplimiento de las normativas legales vigentes, el proponente debe proceder a realizar el Registro de Generador de Desechos Peligrosos y/o Especiales en un plazo de sesenta (60) días.";
        if (generaDesechosEspeciales()) {
            mensaje = "Estimado usuario, en cumplimiento de la normativa ambiental actual, usted debe realizar el Registro de Generador de Desechos Peligrosos y/o Especiales en un plazo de quince (15) días. ";

            mensajeAutoridades = "El sistema de Regularización y Control Ambiental notifica que se ha emitido el Registro Ambiental al proyecto "
                    + proyectosBean.getProyecto().getCodigo()
                    + ". En cumplimiento de las normativas legales vigentes, el proponente debe proceder a realizar el Registro de Generador de Desechos Peligrosos y/o Especiales en un plazo de quince (15) días. ";

        }

        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        params.put("mensajePago", mensaje);
        params.put("mensajeAutoridad", mensaje);
        params.put("u_Autoridad",
                Constantes.getUsuariosNotificarRegistroGeneradoresDesechos());
        procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);

    }

    public boolean generaDesechosEspeciales() {
        return proyectosBean.getProyecto().getTipoEstudio().getId() == 2
                && proyectosBean.getProyecto().getGeneraDesechos() != null
                && proyectosBean.getProyecto().getGeneraDesechos() == true;
    }

    @Getter
    @Setter
    private String tipoRegistro="sinPPC";
    public boolean ppc() throws ParseException {
		Date fechaproyecto=null;
		Date fechabloqueo=null;
		Date fechabloqueoObligatorioInicio=null;
		Date fechabloqueoObligatorioFin=null;
		Date fechabloqueoOpcionalInicio=null;
		Date fechabloqueoOpcionalFin=null;
		
		Date fechabloqueoSIN=null;
		boolean bloquear=false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		fechabloqueo = sdf.parse(Constantes.getFechaProyectosSinPccAntes());
		fechabloqueoObligatorioInicio = sdf.parse(Constantes.getFechaProyectosObligatorioPccInicio());
		fechabloqueoObligatorioFin = sdf.parse(Constantes.getFechaProyectosObligatorioPccFin());		
		fechabloqueoSIN = sdf.parse(Constantes.getFechaProyectosSinPpcAdelante());
		fechabloqueoOpcionalInicio = sdf.parse(Constantes.getFechaProyectosOpcionalPccInicio());
		fechabloqueoOpcionalFin = sdf.parse(Constantes.getFechaProyectosOpcionalPccFin());	
		
		fechaproyecto=sdf.parse(proyectosBean.getProyecto().getFechaRegistro().toString());
		if (fechaproyecto.before(fechabloqueo)){
			setTipoRegistro("sinPPC");
			return true;
		}		
		if (fechaproyecto.after(fechabloqueoObligatorioInicio) && fechaproyecto.before(fechabloqueoObligatorioFin)){
			setTipoRegistro("obligatorio");
			return true;
		}
		
		if (fechaproyecto.after(fechabloqueoObligatorioInicio) && fechaproyecto.before(fechabloqueoObligatorioFin)){
			setTipoRegistro("obligatorio");
			return true;
		}
		
		if (fechaproyecto.after(fechabloqueoOpcionalInicio) && fechaproyecto.before(fechabloqueoOpcionalFin)){
			setTipoRegistro("opcional");
			return true;
		}
		
				
		if (fechaproyecto.after(fechabloqueoSIN)){
			setTipoRegistro("sinPPC");
			return true;
		}		
		
		return bloquear;
		
	}
    @Getter
    @Setter
    private String opcional="";
    public boolean ppc1() throws ParseException {
		Date fechaproyecto=null;
		Date fechabloqueo=null;
		Date fechabloqueoObligatorioInicio=null;
		Date fechabloqueoObligatorioFin=null;
		Date fechabloqueoOpcionalInicio=null;
		Date fechabloqueoOpcionalFin=null;
		
		Date fechabloqueoSIN=null;
		boolean bloquear=false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		fechabloqueo = sdf.parse(Constantes.getFechaProyectosSinPccAntes());
		fechabloqueoObligatorioInicio = sdf.parse(Constantes.getFechaProyectosObligatorioPccInicio());
		fechabloqueoObligatorioFin = sdf.parse(Constantes.getFechaProyectosObligatorioPccFin());		
		fechabloqueoSIN = sdf.parse(Constantes.getFechaProyectosSinPpcAdelante());
		fechabloqueoOpcionalInicio = sdf.parse(Constantes.getFechaProyectosOpcionalPccInicio());
		fechabloqueoOpcionalFin = sdf.parse(Constantes.getFechaProyectosOpcionalPccFin());	
		
		fechaproyecto=sdf.parse(proyectosBean.getProyecto().getFechaRegistro().toString());
		if (fechaproyecto.before(fechabloqueo)){
			return false;
		}		
		if (fechaproyecto.after(fechabloqueoObligatorioInicio) && fechaproyecto.before(fechabloqueoObligatorioFin)){
			return true;
		}
		
		if (fechaproyecto.after(fechabloqueoObligatorioInicio) && fechaproyecto.before(fechabloqueoObligatorioFin)){
			return true;
		}
		
		if (fechaproyecto.after(fechabloqueoOpcionalInicio) && fechaproyecto.before(fechabloqueoOpcionalFin)){
			setOpcional("opcional");
			return true;
		}
		
				
		if (fechaproyecto.after(fechabloqueoSIN)){
			return false;
		}		
		
		return bloquear;
		
	}
    
    
    
}
