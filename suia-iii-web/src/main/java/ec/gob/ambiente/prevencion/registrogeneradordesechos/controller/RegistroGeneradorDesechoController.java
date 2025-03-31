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

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.AlmacenamientoTemporalDesechosBean;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.DatosDesechosBean;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.EliminacionDesechosFueraInstalacionBean;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.EliminacionDesechosInstalacionBean;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.EmpresaPrestadoraServiciosBean;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.EmpresaPrestadoraServiciosBean1;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.EnvasadoEtiquetadoDesechosBean;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.IncompatibilidadesDesechosBean;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.PuntosRecuperacionBean;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.RealizaEliminacionDesechosInstalacionBean;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.RecoleccionTransporteDesechosBean;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.RegistroGeneradorDesechoBean;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.VerRegistroGeneradorDesechoBean;
import ec.gob.ambiente.suia.comun.bean.AdicionarDesechosPeligrososBean;
import ec.gob.ambiente.suia.comun.bean.WizardBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.AlmacenGeneradorDesechos;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FormaPuntoRecuperacion;
import ec.gob.ambiente.suia.domain.GeneradorDesechosDesechoPeligrosoDatosGenerales;
import ec.gob.ambiente.suia.domain.GeneradorDesechosDesechoPeligrosoEtiquetado;
import ec.gob.ambiente.suia.domain.GeneradorDesechosEliminador;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.GeneradorDesechosRecolector;
import ec.gob.ambiente.suia.domain.PuntoEliminacion;
import ec.gob.ambiente.suia.domain.PuntoRecuperacion;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
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
public class RegistroGeneradorDesechoController implements Serializable {

    private static final long serialVersionUID = 5407192014894898512L;

    private static final Logger LOG = Logger.getLogger(RegistroGeneradorDesechoController.class);

    @EJB
    private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

    @ManagedProperty(value = "#{registroGeneradorDesechoBean}")
    @Setter
    private RegistroGeneradorDesechoBean registroGeneradorDesechoBean;

	//cambios
	@ManagedProperty(value = "#{verRegistroGeneradorDesechoBean}")
	@Setter
	private VerRegistroGeneradorDesechoBean verRegistroGeneradorDesechoBean;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private CrudServiceBean crudServiceBean;


    @ManagedProperty(value = "#{wizardBean}")
    @Getter
    @Setter
    private WizardBean wizardBean;

    @EJB
    private ProcesoFacade procesoFacade;

    private boolean pasoTres;

    private boolean pasoNueve;//vear: 09/03/2016: correcto funcionamiento en paso 9 del boton siguiente

    public String aceptar() throws CmisAlfrescoException {
        try {
            guardarRegistro();

            registroGeneradorDesechosFacade.continuarProcesoRegistroGenerador(
                    registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(),
                    JsfUtil.getCurrentProcessInstanceId(), JsfUtil.getCurrentTask().getTaskId(),
                    JsfUtil.getLoggedUser());

            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            return JsfUtil.actionNavigateTo(JsfUtil.NAVIGATION_TO_BANDEJA);
        /*} catch (CmisAlfrescoException ae) {
            throw ae;*/
        } catch (ServiceException | JbpmException ex) {
            LOG.error("Error al continuar con el proceso de registro de generador.", ex);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA_1);
            registroGeneradorDesechoBean.setPermitirContinuar(false);
            return "";
        } catch (Exception e) {
            LOG.error("Error al guardar el registro de generador.", e);
            if(e.getMessage().contains("punto"))
            	JsfUtil.addMessageError(e.getMessage());
            else
            	JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            return "";
        }
    }

    public String continuarActualizacion() throws CmisAlfrescoException {
        try {
            guardarRegistro();

            registroGeneradorDesechosFacade.continuarProcesoRegistroGenerador(
                    registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(),
                    JsfUtil.getCurrentProcessInstanceId(), JsfUtil.getCurrentTask().getTaskId(),
                    JsfUtil.getLoggedUser());

            /*procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getTaskId(),
                    JsfUtil.getCurrentProcessInstanceId(), null);*/

            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            return JsfUtil.actionNavigateTo(JsfUtil.NAVIGATION_TO_BANDEJA);
        } catch (CmisAlfrescoException ae) {
            throw ae;
        } catch (ServiceException | JbpmException ex) {
            LOG.error("Error al continuar con el proceso de registro de generador.", ex);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
            registroGeneradorDesechoBean.setPermitirContinuar(false);
            return "";
        } catch (Exception e) {
            LOG.error("Error al guardar el registro de generador.", e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            return "";
        }
    }


    /**
     * autor: vear
     * fecha: 24/02/2016
     * objetivo: guardar el paso 3 del wizard , como objeto no como lista
     */
    public void guardarPaso3(PuntoRecuperacion puntoRecuperacion) throws CmisAlfrescoException {
        try {
            String currentStep = wizardBean.getCurrentStep();            
        	PuntoRecuperacion puntoRecuperacionClone = (PuntoRecuperacion)SerializationUtils.clone(puntoRecuperacion);            
            this.pasoTres = currentStep != null && currentStep.equals("paso3");
            if (currentStep.equals("paso3")) {
                guardarRegistroPaso3(puntoRecuperacion);
            }
            puntoRecuperacion.setFormasPuntoRecuperacion(puntoRecuperacionClone.getFormasPuntoRecuperacion());
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

        } catch (CmisAlfrescoException ae) {
            throw ae;
        } catch (ServiceException | JbpmException ex) {
            LOG.error("Error al continuar con el proceso de registro de generador.", ex);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
            registroGeneradorDesechoBean.setPermitirContinuar(false);
        } catch (Exception e) {
            LOG.error("Error al guardar el registro de generador.", e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }

    public void guardar() throws CmisAlfrescoException {
        try {

//			String currentStep = wizardBean.getCurrentStep();
//			this.pasoTres = currentStep != null && currentStep.equals("paso3");
            //vear
            //04/03/2016
            //para la persistencia por pasos en el wizard
            String currentStep = wizardBean.getCurrentStep();
            //this.pasoTres = currentStep != null && currentStep.equals("paso3");
            if (currentStep == null) {
                guardarRegistroPaso1();
                inicializarVariablesResponsabilidadExtendida();
            } else if (currentStep.equals("paso2")) {
                guardarRegistroPaso2();
            } else if (currentStep.equals("paso4")) {
                guardarRegistroPaso4();
            } else if (currentStep.equals("paso7")) {
                guardarRegistroPaso7();
            } else if (currentStep.equals("paso9")) {
                guardarRegistroPaso9();
                registroGeneradorDesechoBean.getDatos().put("paso9", true);
            } else if (currentStep.equals("paso13")) {
                guardarRegistroPaso13();
            }
            registroGeneradorDesechoBean.setGuardado(true);
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

        } catch (CmisAlfrescoException ae) {
            registroGeneradorDesechoBean.setGuardado(false);
            throw ae;
        } catch (ServiceException | JbpmException ex) {
            registroGeneradorDesechoBean.setGuardado(false);
            LOG.error("Error al continuar con el proceso de registro de generador.", ex);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
            registroGeneradorDesechoBean.setPermitirContinuar(false);
        } catch (Exception e) {
            registroGeneradorDesechoBean.setGuardado(false);
            LOG.error("Error al guardar el registro de generador.", e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }

    public void guardarPaso6(GeneradorDesechosDesechoPeligrosoEtiquetado desechoPeligrosoEtiquetado) throws CmisAlfrescoException {
        try {
            guardarRegistroPaso6(desechoPeligrosoEtiquetado);
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        } catch (CmisAlfrescoException ae) {
            throw ae;
        } catch (ServiceException | JbpmException ex) {
            LOG.error("Error al continuar con el proceso de registro de generador.", ex);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
            registroGeneradorDesechoBean.setPermitirContinuar(false);
        } catch (Exception e) {
            LOG.error("Error al guardar el registro de generador.", e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }

    /**
     * autor: vear
     * fecha: 25/02/2016
     * objetivo: persistir solo el paso 5 del wizard, como objeto no como lista
     *
     * @throws CmisAlfrescoException
     */
    public void guardarPaso5(GeneradorDesechosDesechoPeligrosoDatosGenerales desechoPeligrosoDatosGenerales) throws CmisAlfrescoException {
        try {

            guardarRegistroPaso5(desechoPeligrosoDatosGenerales);
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

        } catch (CmisAlfrescoException ae) {
            throw ae;
        } catch (ServiceException | JbpmException ex) {
            LOG.error("Error al continuar con el proceso de registro de generador.", ex);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
            registroGeneradorDesechoBean.setPermitirContinuar(false);
        } catch (Exception e) {
            LOG.error("Error al guardar el registro de generador.", e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }

    private void guardarRegistro() throws Exception, ServiceException, CmisAlfrescoException, JbpmException {
        registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().setEliminacionDentroEstablecimiento(
                JsfUtil.getBean(RealizaEliminacionDesechosInstalacionBean.class).isEliminaDesechosDentroInstalacion());

        registroGeneradorDesechosFacade.guardar(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(), JsfUtil
                        .getCurrentProcessInstanceId(), JsfUtil.getBean(PuntosRecuperacionBean.class).getPuntosRecuperacion(),
                JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).getDesechosSeleccionados(),
                JsfUtil.getBean(DatosDesechosBean.class).getDesechosPeligrososDatosGenerales(),
                JsfUtil.getBean(EnvasadoEtiquetadoDesechosBean.class).getDesechosPeligrososEtiquetados(), JsfUtil
                        .getBean(IncompatibilidadesDesechosBean.class).getIncompatibilidadesDesechos(), JsfUtil
                        .getBean(AlmacenamientoTemporalDesechosBean.class).getGeneradoresDesechosAlmacenes(), JsfUtil
                        .getBean(EliminacionDesechosInstalacionBean.class).getPuntosEliminacion(),
                JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).getGeneradoresDesechosRecolectores(), JsfUtil
                        .getBean(EliminacionDesechosFueraInstalacionBean.class).getGeneradoresDesechosEliminadores(), this.pasoTres, false);

       /* Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);
        
        verRegistroGeneradorDesechoBean.getRegistroGeneradorDesechosAsociado();
		//cambios
		String workspace="";
		if(verRegistroGeneradorDesechoBean.getDocumentoLicencia()!=null){
			try {
				workspace = documentosFacade.guardarDocumentoAlfrescoDesechosIVCategorias(verRegistroGeneradorDesechoBean.getRegistroGeneradorDesechosAsociado().getCodigoDesecho(), JsfUtil.getBean(BandejaTareasBean.class).getTarea().getProcessName().toString(), JsfUtil.getBean(BandejaTareasBean.class).getProcessId(), verRegistroGeneradorDesechoBean.getDocumentoLicencia());
				workspace = workspace.substring(0, workspace.lastIndexOf(";"));
				verRegistroGeneradorDesechoBean.getRegistroGeneradorDesechosAsociado().setIdAlfresco(workspace);
				crudServiceBean.saveOrUpdate(verRegistroGeneradorDesechoBean.getRegistroGeneradorDesechosAsociado());
				verRegistroGeneradorDesechoBean.setDocumentoLicencia(null);
				
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CmisAlfrescoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
   

    private void guardarRegistroPaso4() throws ServiceException, CmisAlfrescoException, JbpmException {

        registroGeneradorDesechosFacade.guardarPaso4(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(),
                JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).getDesechosSeleccionados(),
                JsfUtil.getBean(AlmacenamientoTemporalDesechosBean.class).getGeneradoresDesechosAlmacenes());

        /*Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);

        try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void guardarRegistroPaso7() throws ServiceException, CmisAlfrescoException, JbpmException {

        registroGeneradorDesechosFacade.guardarPaso7(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(), JsfUtil
                .getCurrentProcessInstanceId(), JsfUtil
                .getBean(IncompatibilidadesDesechosBean.class).getIncompatibilidadesDesechos());

        /*Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);

        try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void guardarRegistroPaso9() throws ServiceException, CmisAlfrescoException, JbpmException {
        registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().setEliminacionDentroEstablecimiento(
                JsfUtil.getBean(RealizaEliminacionDesechosInstalacionBean.class).isEliminaDesechosDentroInstalacion());

        registroGeneradorDesechosFacade.guardar(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos());
       /* Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);

        try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /*guardarRegistro8 Paso 8*/
    public void guardarpaso8(AlmacenGeneradorDesechos almacenGeneradorDesechos, List<AlmacenGeneradorDesechos> almacenesGeneradorDesechos) throws CmisAlfrescoException {
        try {
            guardarRegistro8(almacenGeneradorDesechos, almacenesGeneradorDesechos);
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        } catch (CmisAlfrescoException ae) {
            throw ae;
        } catch (ServiceException | JbpmException ex) {
            LOG.error("Error al continuar con el proceso de registro de generador.", ex);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
            registroGeneradorDesechoBean.setPermitirContinuar(false);
        } catch (Exception e) {
            LOG.error("Error al guardar el registro de generador.", e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }

    /*guardarRegistro10 Paso 10*/
    public void guardarpaso10(PuntoEliminacion puntoEliminacion) throws CmisAlfrescoException {
        try {

            guardarRegistro10(puntoEliminacion);
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        } catch (CmisAlfrescoException ae) {
            throw ae;
        } catch (ServiceException | JbpmException ex) {
            LOG.error("Error al continuar con el proceso de registro de generador.", ex);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
            registroGeneradorDesechoBean.setPermitirContinuar(false);
        } catch (Exception e) {
            LOG.error("Error al guardar el registro de generador.", e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }


    /*guardarRegistro11 Paso 11*/
    public void guardarpaso11(GeneradorDesechosRecolector generadorDesechosRecolector, boolean valor) throws CmisAlfrescoException {
        try {

            guardarRegistro11(generadorDesechosRecolector);
            if (valor)
                JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        } catch (CmisAlfrescoException ae) {
            throw ae;
        } catch (ServiceException | JbpmException ex) {
            LOG.error("Error al continuar con el proceso de registro de generador.", ex);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
        } catch (Exception e) {
            LOG.error("Error al guardar el registro de generador.", e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }

    /*guardarRegistro12 Paso 12*/
    public void guardarpaso12(GeneradorDesechosEliminador generadorDesechosEliminador) throws CmisAlfrescoException {
        try {

            guardarRegistro12(generadorDesechosEliminador);
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        } catch (CmisAlfrescoException ae) {
            throw ae;
        } catch (ServiceException | JbpmException ex) {
            LOG.error("Error al continuar con el proceso de registro de generador.", ex);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
            registroGeneradorDesechoBean.setPermitirContinuar(false);
        } catch (Exception e) {
            LOG.error("Error al guardar el registro de generador.", e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }


    /*guardar8 Paso 8*/
    private void guardarRegistro8(AlmacenGeneradorDesechos almacenGeneradorDesechos, List<AlmacenGeneradorDesechos> almacenesGeneradorDesechos) throws ServiceException, CmisAlfrescoException, JbpmException {

        registroGeneradorDesechosFacade.guardar8(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(), almacenGeneradorDesechos, almacenesGeneradorDesechos, JsfUtil.getCurrentProcessInstanceId());

       /* Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);

        try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


	/*guardar10 Paso 10*/

    private void guardarRegistro10(PuntoEliminacion puntoEliminacion) throws ServiceException, CmisAlfrescoException, JbpmException {


        registroGeneradorDesechosFacade.guardar10(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(), puntoEliminacion);

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);

        try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*guardar11 Paso 11*/
    private void guardarRegistro11(GeneradorDesechosRecolector generadorDesechosRecolector) throws ServiceException, CmisAlfrescoException, JbpmException {

        registroGeneradorDesechosFacade.guardar11(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(),
                generadorDesechosRecolector, JsfUtil.getCurrentProcessInstanceId());

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);

        try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*guardar12 Paso 12*/
    private void guardarRegistro12(GeneradorDesechosEliminador generadorDesechosEliminador) throws ServiceException, CmisAlfrescoException, JbpmException {
        registroGeneradorDesechosFacade.guardar12(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(), generadorDesechosEliminador, JsfUtil.getCurrentProcessInstanceId());

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);

        try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void guardarRegistroPaso1() throws ServiceException, CmisAlfrescoException, JbpmException {

        registroGeneradorDesechosFacade.guardarPaso1(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(), JsfUtil
                .getCurrentProcessInstanceId());

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);

       try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarRegistroPaso13() throws ServiceException, CmisAlfrescoException, JbpmException {

        registroGeneradorDesechosFacade.guardarPaso13(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(), JsfUtil
                .getCurrentProcessInstanceId());

       /* Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);

        try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * autor: vear
     * fecha: 26/02/2016
     * objetivo: dividir la accion de guardar  y que no persista los pasos que ya
     * persistimos
     * <p/>
     * este metodo esta modificado, el de arriba que esta comentado es el original
     *
     * @throws ServiceException
     * @throws CmisAlfrescoException
     * @throws JbpmException
     */
    private void guardarRegistroPaso2() throws ServiceException, CmisAlfrescoException, JbpmException {

        registroGeneradorDesechosFacade.guardarPaso2(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(), JsfUtil
                .getCurrentProcessInstanceId());

        /*Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);

       try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void guardarRegistroPaso6(GeneradorDesechosDesechoPeligrosoEtiquetado desechoPeligrosoEtiquetado) throws ServiceException, CmisAlfrescoException, JbpmException {
        registroGeneradorDesechosFacade.guardarPaso6(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(),
                JsfUtil.getCurrentProcessInstanceId(), desechoPeligrosoEtiquetado);

        /*Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);
*/
        try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarRegistroPaso5(GeneradorDesechosDesechoPeligrosoDatosGenerales desechoPeligrosoDatosGenerales) throws ServiceException, CmisAlfrescoException, JbpmException {

        registroGeneradorDesechosFacade.guardarPaso5(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(),
                desechoPeligrosoDatosGenerales);

        /*Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);

        try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * autor: vear
     * fecha: 23/02/2016
     * objetivo: guardar solo el paso 3 del wizard
     **/
    private void guardarRegistroPaso3(PuntoRecuperacion puntoRecuperacion) throws ServiceException, CmisAlfrescoException, JbpmException {

        registroGeneradorDesechosFacade.guardarPaso3(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos(), JsfUtil
                .getCurrentProcessInstanceId(), puntoRecuperacion, this.pasoTres);

        /*Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        JsfUtil.getCurrentTask().updateVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR_ACCION_GUARDAR,
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getId());

        procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
                variables);

        try {
            registroGeneradorDesechoBean.initGuardado();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * autor: vear
     * fecha: 01/03/2016
     * objetivo: haabilitar desabilitar el boton siguiente del wizard
     */
    public String btnSiguiente() {
        registroGeneradorDesechoBean.setGuardado(false);
        String currentStep = wizardBean.getCurrentStep();


        if (currentStep == null) {
            if (registroGeneradorDesechoBean.isActualizar()) {//vear: 01/04/16: se hace esta validacion, porque si es actualización no se presenta la responsabilidad extendida
                if (registroGeneradorDesechoBean.getDatos().get("paso3") != null) {
                    registroGeneradorDesechoBean.setGuardado(true);
                }
            } else {
                if (registroGeneradorDesechoBean.getDatos().get("paso2") != null) {
                    registroGeneradorDesechoBean.setGuardado(true);
                }
            }
            inicializarVariablesResponsabilidadExtendida();
        } else if (currentStep.equals("paso1") && registroGeneradorDesechoBean.getDatos().get("paso2") != null) {
            registroGeneradorDesechoBean.setGuardado(true);
            inicializarVariablesResponsabilidadExtendida();
        } else if (currentStep.equals("paso2") && registroGeneradorDesechoBean.getDatos().get("paso3") != null) {
            registroGeneradorDesechoBean.setGuardado(true);
        } else if (currentStep.equals("paso3") && registroGeneradorDesechoBean.getDatos().get("paso4") != null) {
            registroGeneradorDesechoBean.setGuardado(true);
        } else if (currentStep.equals("paso4") && registroGeneradorDesechoBean.getDatos().get("paso5") != null) {
            registroGeneradorDesechoBean.setGuardado(true);
        } else if (currentStep.equals("paso5") && registroGeneradorDesechoBean.getDatos().get("paso6") != null) {
            registroGeneradorDesechoBean.setGuardado(registroGeneradorDesechoBean.getDatos().get("paso6"));
        } else if (currentStep.equals("paso6") && registroGeneradorDesechoBean.getDatos().get("paso7") != null) {
            registroGeneradorDesechoBean.setGuardado(true);
            //registroGeneradorDesechoBean.initGuardado();
        } else if (currentStep.equals("paso7") && registroGeneradorDesechoBean.getDatos().get("paso8") != null) {
            registroGeneradorDesechoBean.setGuardado(true);
        } else if (currentStep.equals("paso8") && registroGeneradorDesechoBean.getDatos().get("paso9") != null) {
            registroGeneradorDesechoBean.setGuardado(true);
        } else if (currentStep.equals("paso9") && registroGeneradorDesechoBean.getDatos().get("paso10") != null) {
            registroGeneradorDesechoBean.setGuardado(true);
        } else if (currentStep.equals("paso10") && registroGeneradorDesechoBean.getDatos().get("paso11") != null) {
            registroGeneradorDesechoBean.setGuardado(true);
        } else if (currentStep.equals("paso11") && registroGeneradorDesechoBean.getDatos().get("paso12") != null) {
            registroGeneradorDesechoBean.setGuardado(true);
        } else if (currentStep.equals("paso12") && registroGeneradorDesechoBean.getDatos().get("paso13") != null) {
            registroGeneradorDesechoBean.setGuardado(true);
        } else if (currentStep.equals("paso13") && registroGeneradorDesechoBean.getDatos().get("paso13") != null && registroGeneradorDesechoBean.isActualizar()) {
            registroGeneradorDesechoBean.setGuardado(true);
        }

        return null;


    }


    /**
     * autor: vear
     * fecha: 01/03/2016
     * objetivo: haabilitar desabilitar el boton siguiente del wizard
     */

    public String btnAtras() {
        registroGeneradorDesechoBean.setGuardado(true);
        return null;
    }

    public void updatePoliticaSeleccionada() {
        if (registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getResponsabilidadExtendida() != null
                && !registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getResponsabilidadExtendida()) {
            registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().setPoliticaDesecho(null);
            registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().setPoliticaDesechoActividad(null);
            if (registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getProyecto() == null) {
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().setTipoSector(null);
                registroGeneradorDesechoBean.setIdDesecho(null);
            }
        } else if (registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getResponsabilidadExtendida() != null
                && registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getResponsabilidadExtendida()) {
            if (registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getProyecto() == null) {
                registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().setTipoSector(
                        registroGeneradorDesechoBean.getTipoSectorOtros());
            }
        }
    }

    public void updateActividadSeleccionada() {
        Integer idAnterior = registroGeneradorDesechoBean.getIdDesecho();
//        Integer idDesecho = registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getPoliticaDesechoActividad()
//                .getDesechoPeligroso().getId();
        Integer idDesecho = registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getPoliticaDesechoActividad().getId();        
        if (idAnterior == null || !idAnterior.equals(idDesecho)) {
            List<DesechoPeligroso> desechos = JsfUtil.getBean(AdicionarDesechosPeligrososBean.class)
                    .getDesechosSeleccionados();
            if (desechos != null) {
                List<DesechoPeligroso> desechosEliminar = new ArrayList<>(desechos);
                for (DesechoPeligroso desechoPeligroso : desechosEliminar) {
                    JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).eliminarDesecho(desechoPeligroso);
                }
            }
        }
        registroGeneradorDesechoBean.setIdDesecho(idDesecho);
    }

    /**
     * vear
     * 03/03/2016
     * para eliminar directamente de la bdd
     */
    public void eliminarPuntoRecuperacion(PuntoRecuperacion puntoRecuperacion) {
        registroGeneradorDesechosFacade.eliminarPuntoRecuperacion(puntoRecuperacion);
    }

    /**
     * autor: vear
     * fecha: 03/03/2016
     * objetivo: eliminar directamente de la bdd
     *
     * @param desechoPeligrosoDatosGenerales
     */
    public void eliminarDesechoPeligrosoDatosGenerales(GeneradorDesechosDesechoPeligrosoDatosGenerales desechoPeligrosoDatosGenerales) {
        registroGeneradorDesechosFacade.eliminarDesechoPeligrosoDatosGenerales(desechoPeligrosoDatosGenerales);
    }

    /**
     * autor: vear
     * fecha: 04/03/2016
     * objetivo: eliminar directamente de la bdd
     *
     * @param desechoEtiquetado
     */
    public void eliminarDesechoEtiquetado(GeneradorDesechosDesechoPeligrosoEtiquetado desechoEtiquetado) {
        registroGeneradorDesechosFacade.eliminarDesechoEtiquetado(desechoEtiquetado);
    }

    /*eliminar paso 8*/
    public void eliminarAlmacenGeneradorDesechos(AlmacenGeneradorDesechos almacen) {
        registroGeneradorDesechosFacade.eliminarAlmacenGeneradorDesechos(almacen);
    }

    /*eliminar paso 10*/
    public void eliminarPuntoEliminacion(PuntoEliminacion punto) {
        registroGeneradorDesechosFacade.eliminarPuntoEliminacion(punto);
    }

    /*eliminar paso 11*/
    public void eliminarGeneradorDesechosRecolector(GeneradorDesechosRecolector generadorDesechosRecolector) {
        registroGeneradorDesechosFacade.eliminarGeneradorDesechosRecolector(generadorDesechosRecolector);
    }

    /*eliminar paso 12*/
    public void eliminarGeneradorDesechosEliminador(GeneradorDesechosEliminador eliminador) {
        registroGeneradorDesechosFacade.eliminarGeneradorDesechosEliminador(eliminador);
    }

    public void inicializarVariablesResponsabilidadExtendida(){
        JsfUtil.getBean(AlmacenamientoTemporalDesechosBean.class).setEsResponsabilidadExtendida(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getResponsabilidadExtendida());
        JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).setEsResponsabilidadExtendida(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getResponsabilidadExtendida());
        JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).setEsResponsabilidadExtendida(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getResponsabilidadExtendida());
    }
}