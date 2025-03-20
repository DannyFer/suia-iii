package ec.gob.ambiente.prevencion.categoriaii.mineria.v2.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.ws.rs.GET;

import lombok.Getter;
import lombok.Setter;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.categoria2.v2.controller.FichaAmbientalGeneralFinalizarControllerV2;
import ec.gob.ambiente.prevencion.categoria2.bean.FichaAmbientalPmaBean;
import ec.gob.ambiente.prevencion.categoria2.controllers.FichaAmbientalPmaController;
import ec.gob.ambiente.prevencion.categoriaii.mineria.controller.ImpresionFichaMineriaController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.builders.TaskSummaryCustomBuilder;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.InventarioForestalPma;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.inventarioForestalPma.facade.InventarioForestalPmaFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.v2.CategoriaIIFacadeV2;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;

/**
 * @author frank torres
 */
@ManagedBean
@ViewScoped
public class FichaMineriaEnviarControllerV2 implements Serializable {

    private static final long serialVersionUID = 1657408543900049845L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(FichaMineriaEnviarControllerV2.class);
    private static final String MENSAJE_CATEGORIAII_PAGO = "mensaje.categoriaII.pago";
    private static final String MENSAJE_CATEGORIAII_SIN_PAGO = "mensaje.categoriaII.final";
    private static final String IMAGEN_VALIDAR_PAGO = "/resources/images/mensajes/validar_Pago_tasas.png";

    @Getter
    @Setter
    private FichaAmbientalMineria fichaAmbientalMineria;

    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @EJB
    private CategoriaIIFacadeV2 categoriaIIFacade;

    @EJB
    private InventarioForestalPmaFacade inventarioForestalPmaFacade;

    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    @EJB
    private ProcesoFacade procesoFacade;
    
    @EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{impresionFichaMineriaController}")
    private ImpresionFichaMineriaController impresionFichaMineriaController;

    @Getter
    @Setter
    @ManagedProperty(value = "#{fichaAmbientalPmaController}")
    private FichaAmbientalPmaController fichaAmbientalPmaController;

    @Getter
    @Setter
    private String mensaje;

    @Getter
    @Setter
    private byte[] archivo;

    @Getter
    @Setter
    private String pdf;
    @Getter
    @Setter
    private File archivoFinal;
    @Getter
    @Setter
    private Boolean descargarFicha = false;
    @Getter
    @Setter
    private Boolean descargarRegistro = false;

    @Getter
    @Setter
    private Boolean condiciones = false;

    @Getter
    @Setter
    private boolean mostrarMensaje = false;

    @Getter
    @Setter
    private StreamedContent ficha;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;

    private static final int TIPO_ESTUDO = 2;
    private Boolean completado;

    @Getter
    @Setter
    private String mensajeFinalizacion;

    @Getter
    @Setter
    private String pathImagenFinalizacion;

    @PostConstruct
    public void cargarDatos() {
        cargarDatosBandeja();
    }

    private void cargarDatosBandeja() {
        proyecto = proyectosBean.getProyecto();
        try {
            fichaAmbientalMineria = fichaAmbientalMineriaFacade
                    .obtenerPorProyecto(proyecto);
//            if (fichaAmbientalMineria.getValidarParticipacionCiudadana()==null){
//            	fichaAmbientalMineria.setValidarParticipacionCiudadana(false);
//            }
            mensaje = getNotaResponsabilidadInformacionRegistroProyecto(loginBean
                    .getUsuario());
            ficha = getStream();
            ppc();
            completado = validarFichaMineraCompletaInicial();
            if (validarFichaMineraCompleta()) {
                File archivoTmp = JsfUtil.fileMarcaAgua(impresionFichaMineriaController.cargarDatosPdfFile(),"BORRADOR",BaseColor.GRAY);
                Path path = Paths.get(archivoTmp.getAbsolutePath());
                FileOutputStream file;

                archivo = Files.readAllBytes(path);
                archivoFinal = new File(
                        JsfUtil.devolverPathReportesHtml(archivoTmp.getName()));
                file = new FileOutputStream(archivoFinal);
                file.write(archivo);
                file.close();
                setPdf(JsfUtil.devolverContexto("/reportesHtml/"
                        + archivoTmp.getName()));
            }


        } catch (ServiceException e) {
            LOG.error(e, e);
        } catch (IOException e) {
            LOG.error(e, e);
        } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public Boolean validarFichaMineraCompleta() {
        return completado;
    }

    public Boolean validarFichaMineraCompletaInicial() {
        try {
        	if (getTipoRegistro().equals("sinPPC") ||  getTipoRegistro().equals("opcional")){
            return fichaAmbientalMineria.getValidarInformacionGeneral()
                    && fichaAmbientalMineria.getValidarCaracteristicasGenerales()
                    && fichaAmbientalMineria.getValidarDescripcionActividad()
                    && fichaAmbientalMineria.getValidarCaracteristicasAreaInfluencia()
                    && fichaAmbientalMineria.getValidarMuestreoInicialLineaBase()
                    && fichaAmbientalMineria.getValidarMatrizIdentificacionImpactosAmbientales()
                    && fichaAmbientalMineria.getValidarPlanManejoAmbiental()
//                    && fichaAmbientalMineria.getValidarParticipacionCiudadana()
                    && fichaAmbientalMineria.getValidarInventarioForestal();
            }else{
            	return fichaAmbientalMineria.getValidarInformacionGeneral()
                        && fichaAmbientalMineria.getValidarCaracteristicasGenerales()
                        && fichaAmbientalMineria.getValidarDescripcionActividad()
                        && fichaAmbientalMineria.getValidarCaracteristicasAreaInfluencia()
                        && fichaAmbientalMineria.getValidarMuestreoInicialLineaBase()
                        && fichaAmbientalMineria.getValidarMatrizIdentificacionImpactosAmbientales()
                        && fichaAmbientalMineria.getValidarPlanManejoAmbiental()
                        && fichaAmbientalMineria.getValidarParticipacionCiudadana()
                        && fichaAmbientalMineria.getValidarInventarioForestal();
            }
        	
        } catch (RuntimeException e) {
            //LOG.error(e, e);
            return false;
        }
    }

    public void validarPagos(){
        try {
            InventarioForestalPma inventario = inventarioForestalPmaFacade.obtenerInventarioForestalPmaMineriaPorFicha(fichaAmbientalMineria.getId());

            if (!categoriaIIFacade.getExentoPago(proyectosBean.getProyecto()) || (inventario!=null && inventario.getRemocionVegetal()==true)) {            	
				if (proyectosBean.getProyecto().getAreaResponsable().getTipoArea().getId().equals(3)) {
					mensajeFinalizacion = (Constantes.getMessageResourceString("mensaje.categoriaII.pago.entes")+ proyectosBean.getProyecto().getAreaResponsable().getAreaName() + ".");
				} else {
					mensajeFinalizacion = Constantes.getMessageResourceString(MENSAJE_CATEGORIAII_PAGO);
				}
                float total = 0f;
                mensajeFinalizacion += "\nPago por concepto de:";
                if(!categoriaIIFacade.getExentoPago(proyectosBean.getProyecto())){
                    mensajeFinalizacion += "\nRegistro Ambiental: " + Constantes.getPropertyAsString("costo.tramite.registro.ambiental") + " USD";
                    total += Float.parseFloat(Constantes.getPropertyAsString("costo.tramite.registro.ambiental"));
                }
                if(inventario.getRemocionVegetal()==true){
                    float coberturaVegetal = inventario.getMaderaEnPie() * Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal"));
                    mensajeFinalizacion += "\nRemoción de cobertura vegetal nativa: " + String.valueOf(coberturaVegetal) + " USD";
                    total += coberturaVegetal;
                }
                mensajeFinalizacion += "\nTotal a pagar: " + String.valueOf(total) + " USD";
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

    public String enviarFicha() {
        try {
            if (validarFichaMineraCompleta()) {
                long idTask = bandejaTareasBean.getTarea().getTaskId();
                long idProcessInstance = bandejaTareasBean.getTarea().getProcessInstanceId();
                inicializarNotificacion();

                /*File fichaAmbiental = impresionFichaMineriaController.cargarDatosPdfFile();
                categoriaIIFacade.ingresarDocumentoCategoriaII(fichaAmbiental, proyecto.getId(), proyecto.getCodigo(),
                        idProcessInstance, idTask, TipoDocumentoSistema.TIPO_DOCUMENTO_FICHA_MINERIA, "Ficha Mineria");*/

              // LAST MOD 26/01/2016
                String numeroRes = fichaAmbientalMineriaFacade.generarNoResolucion(proyecto);
                fichaAmbientalMineria.setNumeroResolucion(numeroRes);
                this.fichaAmbientalMineriaFacade.guardarFicha(fichaAmbientalMineria);
                InventarioForestalPma inventario = inventarioForestalPmaFacade.obtenerInventarioForestalPmaMineriaPorFicha(fichaAmbientalMineria.getId());
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("requiereCoberturaVegetal", inventario.getRemocionVegetal());
                params.put("metrosCobertura", inventario.getMaderaEnPie());
                params.put("factorCobertura", Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal")));
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario() ,idProcessInstance, params);

                taskBeanFacade.approveTask(loginBean.getNombreUsuario(),bandejaTareasBean.getTarea().getTaskId(),
                        bandejaTareasBean.getTarea().getProcessInstanceId(), null,
                        loginBean.getPassword(),
                        Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

                archivoFinal.delete();

                boolean saltarRGD = false;
    			if (proyecto.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)
    					&& (proyecto.getCatalogoCategoria().getCategoria().getId()
    							.equals(Categoria.CATEGORIA_III) || proyecto.getCatalogoCategoria()
    							.getCategoria().getId().equals(Categoria.CATEGORIA_IV))) {
    				saltarRGD = true;
    			}

    			/*if (!saltarRGD && proyecto.getGeneraDesechos()) {
    				registroGeneradorDesechosFacade.iniciarEmisionProcesoRegistroGenerador(
    						JsfUtil.getLoggedUser(), RegistroGeneradorTramiteResolver.class, proyecto);
    			}*/
    			finalizarRegistroambiental();
                return JsfUtil.actionNavigateToBandeja();
            } else {
                JsfUtil.addMessageError("Debe completar los datos del Registro Ambiental Minería Artesanal.");
            }
        } catch (Exception e) {
            LOG.error(e, e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
        return "";
    }


/***
 * funcion para finalizar el registro ambiental en la tarea de completar informacion para registros categoria II
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
					//cambio para refrescar los datos
	                FichaAmbientalPmaBean fichaAmbientalPmaBean = JsfUtil.getBean(FichaAmbientalPmaBean.class);
	                fichaAmbientalPmaBean.init();
					// mando a generar la resolucion
					fichaAmbientalGeneralFinalizarControllerV2.generarFichaRegistroAmbiental();
					fichaAmbientalGeneralFinalizarControllerV2.setDescargarFicha(true);
					fichaAmbientalGeneralFinalizarControllerV2.setDescargarRegistro(true);
					// mando a generar el registro ambiental
					fichaAmbientalGeneralFinalizarControllerV2.redireccionarBandeja();
					// finalizo la tarea si se genero la resolucion satisfactoriamente
					if(fichaAmbientalGeneralFinalizarControllerV2.getDescargarRegistro()){
						fichaAmbientalGeneralFinalizarControllerV2.direccionar();
						fichaAmbientalGeneralFinalizarControllerV2.cambiarEstadoTarea();
					}
					RequestContext.getCurrentInstance().execute("PF('continuarDialog').show();");
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

    private String getNotaResponsabilidadInformacionRegistroProyecto(
            Usuario persona) {
        String[] parametros = {persona.getPersona().getNombre(), persona.getPin()};
        return DocumentoPDFPlantillaHtml.getPlantillaConParametros(
                "nota_responsabilidad_certificado_interseccion", parametros);
    }

    public StreamedContent getStream() {
        DefaultStreamedContent content = new DefaultStreamedContent();

        if (archivo != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    archivo), "application/pdf");
            content.setName("Registro_Ambiental_Minería_Artesanal.pdf");
        }
        return content;

    }

    public void descargarFichaTecnico() {
        descargarFicha = true;
        String[] tokens = archivoFinal.getName().split("\\.(?=[^\\.]+$)");
        UtilFichaMineria.descargar(archivoFinal.getAbsolutePath(), tokens[0]);
    }

    public void inicializarNotificacion() throws JbpmException {
        mensaje = "Estimado usuario, en cumplimiento de la normativa ambiental actual, usted debe realizar el Registro de Generador de Desechos Peligrosos y/o Especiales en un plazo de sesenta (60) días.";
        if (generaDesechosEspeciales()) {
            mensaje = "Estimado usuario, en cumplimiento de la normativa ambiental actual, usted debe realizar el Registro de Generador de Desechos Peligrosos y/o Especiales en un plazo de quince (15) días. ";
        }

        Map<String, Object> params = new ConcurrentHashMap<String, Object>();

        params.put("mensajePago", mensaje);
        params.put("mensajeAutoridad", mensaje);
        params.put("u_Autoridad",
                Constantes.getUsuariosNotificarRegistroGeneradoresDesechos());

        procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                        .getProcessInstanceId(), params);

    }

    public boolean generaDesechosEspeciales() {
        return proyecto.getTipoEstudio().getId() == TIPO_ESTUDO
                && proyecto.getGeneraDesechos() != null
                && proyecto.getGeneraDesechos();
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
			return true;
		}
		
				
		if (fechaproyecto.after(fechabloqueoSIN)){
			return false;
		}		
		
		return bloquear;
		
	}
    
    
}
