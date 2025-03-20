package ec.gob.ambiente.suia.comun.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.google.protobuf.ServiceException;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.categoria2.v2.controller.FichaAmbientalGeneralFinalizarControllerV2;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.GenerarNotificacionesAplicativoController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.builders.TaskSummaryCustomBuilder;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.comun.base.ComunBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.recaudaciones.controllers.GenerarNUTController;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.ProyectosConPagoSinNutFacade;
import ec.gob.ambiente.suia.recaudaciones.model.Cuentas;
import ec.gob.ambiente.suia.recaudaciones.model.EstadosNut;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.recaudaciones.model.ProyectosConPagoSinNut;
import ec.gob.ambiente.suia.recaudaciones.model.SolicitudUsuario;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;
import ec.gob.ambiente.suia.recaudaciones.model.TarifasNUT;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class PagoServiciosBean extends ComunBean {

	private static final long serialVersionUID = 4910545245153339126L;
	/**
     * Nombre:SUIA
     * ParametrosIngreso:
     * PArametrosSalida:
     * Fecha:17/09/2015
     **/
	
	private static final Logger LOG = Logger.getLogger(PagoServiciosBean.class);
	/**
	  * FIN 
	  */

	@Getter
	@Setter
	public Boolean cumpleMonto = false;

	@Getter
	@Setter
	public double montoTotal;
	
	@Getter
	@Setter
	public double montoTotalProyecto;
	
	@Getter
	@Setter
	public double montoTotalCoberturaVegetal;
	
	@Getter
	@Setter
	private String imagenAyudaPropia;
	
	@Getter
	@Setter
	private String mensajeFinalizar;

	@Getter
	@Setter
	private String textoAyudaPropia;

	@Getter
	@Setter
	private boolean mostrarAyuda;

	@Getter
	@Setter
	private List<InstitucionFinanciera> institucionesFinancieras;
	
	@Getter
	@Setter
	private List<InstitucionFinanciera> institucionesFinancierasCobertura;

	@Getter
	@Setter
	private TransaccionFinanciera transaccionFinanciera;
	
	@Getter
	@Setter
	private TransaccionFinanciera transaccionFinancieraCobertura;

	@Getter
	@Setter
	private List<TransaccionFinanciera> transaccionesFinancieras;

	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;

	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;

	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectosConPagoSinNutFacade proyectosConPagoSinNutFacade;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	 /**
     * Nombre:SUIA
     * Descripción: validar si se realiza el pago de entes
     * ParametrosIngreso:
     * PArametrosSalida:
     * Fecha:17/09/2015
     * */
	@Getter
	@Setter
	private boolean eligioEnte;
	
	@Getter
	@Setter
	private List<InstitucionFinanciera> entesAcreditados;

	private Boolean persist = false;
	
	
	@Getter
	public Boolean datoFormaspago=true;
	@Getter
	@Setter
	public Boolean formasPago = false;
	
	@Getter
	@Setter
	public Boolean cumpleMontoProyecto = false;
	
	@Getter
	@Setter
	public Boolean cumpleMontoCobertura = false;
	
	@Getter
	@Setter
	public Boolean panelCoberturaVegetal = false;
	
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	
	@Getter
	@Setter
	public Boolean generarNUT;

	/**
	  * FIN validar si se realiza el pago de entes
	  */

	@Getter
	@Setter
	public boolean dbhabilitarEfectivoCeluar=Boolean.parseBoolean(Constantes.getServiceEfectivoCelular());
	
	private String identificadorMotivo;
	
	@Getter
	private boolean pagoFacilitadores=false;
	
	@Getter
	private boolean pagoScoutDrilling =false;
	
	@EJB
    private DocumentosFacade documentosFacade;
	
	@Getter
    @Setter
    private Boolean condiciones = false;
	
	@Getter
    @Setter
	private List<Documento> documentosNUT;
	
	@Getter
	@Setter
	private List<String> listPathArchivos = new ArrayList<String>();
	
	@Getter
	@Setter
	private Usuario usuarioAutoridad;
	
	@EJB
	private CategoriaIIFacade categoriaIIFacade;
	
	@EJB
	private ContactoFacade contactoFacade;

	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;

	public void initData() {
		cumpleMontoProyecto=false;
		cumpleMontoCobertura=false;
		panelCoberturaVegetal=false;
//		generarNUT=true;
		try {
			// suia
			if ((proyectosBean.getProyecto()) != null && proyectosBean.getProyectoRcoa().getId() == null) {				
				List<Usuario> listaFacilitadoresAreas = new ArrayList<>();
				//validar si tiene facilitadores
				if (bandejaTareasBean.getTarea().getTaskName().contains("Validar pago de tasas de facilitador/es") && proyectosBean.getProyecto().getAreaResponsable().getTipoArea().getId()==3){
					listaFacilitadoresAreas= usuarioFacade.buscarUsuarioPorRolNombreArea("Facilitador",proyectosBean.getProyecto().getAreaResponsable().getAreaName());					
				}
				if (bandejaTareasBean.getTarea().getTaskName().contains("Validar pago de tasas de facilitador/es")){
					pagoFacilitadores = true;
				}
				
				// verifico si existe el registro scout drilling y es una actualizacion
				PerforacionExplorativa objProyectoPE = fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyectosBean.getProyecto());
				if(objProyectoPE != null && objProyectoPE.getCodeUpdate() != null){
					pagoScoutDrilling = true;
				}				
				if (proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.05") 
						|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.04.03")
						|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.05.03")
						|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.02.03")) {
					pagoScoutDrilling = true;
				}
				
				/**
				* Walter se realizo la separacion por entes acreditados y
				* planta central. nombre: descripción: parametrosIngreso:
				* parametrosSalida: fecha:
				*/
				if (bandejaTareasBean
						.getTarea()
						.getProcessName()
						.equals("Registro de generador de desechos especiales y peligrosos") || ((bandejaTareasBean.getTarea().getTaskName().contains("Validar pago de tasas de facilitador/es") && (listaFacilitadoresAreas.size()<=0)))) {
					institucionesFinancieras = institucionFinancieraFacade
							.obtenerListaInstitucionesFinancierasPC();
					pagoScoutDrilling = false;
					formasPago=true;
				} else {

					if (proyectosBean.getProyecto().getAreaResponsable().getTipoEnteAcreditado() != null) {	
						if (proyectosBean.getProyecto().getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")){
							institucionesFinancieras = institucionFinancieraFacade
									.obtenerListaInstitucionesFinancierasPC();
							formasPago=false;
						
						}else{
							institucionesFinancieras = institucionFinancieraFacade.obtenerPorNombre(proyectosBean.getProyecto().getAreaResponsable().getAreaName());
							formasPago=false;
							if(institucionesFinancieras==null)
								JsfUtil.addMessageError("No se encontró institución financiara para: "+proyectosBean.getProyecto().getAreaResponsable().getAreaName());
						}							
					} else {
						institucionesFinancieras = institucionFinancieraFacade
								.obtenerListaInstitucionesFinancierasPC();
						formasPago=false;
					}
	            }			
			}else {
				institucionesFinancieras = institucionFinancieraFacade
						.obtenerListaInstitucionesFinancierasPC();
				formasPago=true;
				
				if(proyectosBean.getProyectoRcoa() != null)
				{
					PerforacionExplorativa objProyectoPE = fichaAmbientalMineria020Facade.cargarPerforacionExplorativaRcoa(proyectosBean.getProyectoRcoa().getId());
					if(objProyectoPE != null && objProyectoPE.getId() != null){
						pagoScoutDrilling = true;
					}
				}
			}
			institucionesFinancierasCobertura= institucionFinancieraFacade
					.obtenerListaInstitucionesFinancierasPC();
//			institucionesFinancieras = institucionFinancieraFacade
//					.obtenerListaInstitucionesFinancieras(ug.getNombre(),ug.getCodificacionInec());
			/**
			* fin de la separacion por entes acreditados y planta central.
			*/
			transaccionFinanciera = new TransaccionFinanciera();
			transaccionFinancieraCobertura = new TransaccionFinanciera();
//            if(bandejaTareasBean.getTarea().getProcessName().equals("Registro de generador de desechos especiales y peligrosos"))
//            {
//            	institucionesFinancieras = institucionFinancieraFacade
//        				.obtenerListaInstitucionesFinancierasPC();
//            }else{
//    			institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancieras();	            	
//            }
//			transaccionFinanciera = new TransaccionFinanciera();
			if (this.identificadorMotivo == null){
				
				if(proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getId() != null){
					transaccionesFinancieras = transaccionFinancieraFacade
							.cargarTransacciones(proyectosBean.getProyecto()
									.getId(), bandejaTareasBean.getTarea()
									.getProcessInstanceId(), bandejaTareasBean
									.getTarea().getTaskId());
					
				}else if(proyectosBean.getProyectoRcoa() != null){
					transaccionesFinancieras = transaccionFinancieraFacade
							.cargarTransaccionesRcoa(proyectosBean.getProyectoRcoa()
									.getId(), bandejaTareasBean.getTarea()
									.getProcessInstanceId(), bandejaTareasBean
									.getTarea().getTaskId());
				}				
			}else
				transaccionesFinancieras = transaccionFinancieraFacade
						.cargarTransacciones(this.identificadorMotivo);
			
			if(transaccionesFinancieras.isEmpty())
				persist = true;
			//suia
			if(proyectosBean.getProyecto()!=null && proyectosBean.getProyecto().getCodigo() != null && proyectosBean.getProyecto().isAreaResponsableEnteAcreditado() && montoTotalCoberturaVegetal>0){
				panelCoberturaVegetal=true;
				//generarNUT=true;
			}
			
			if(bandejaTareasBean.getTarea().getProcessName().equals("CertificadoAmbiental")&& montoTotalProyecto==0){
				panelCoberturaVegetal=true;
			}
			
			if(montoTotalProyecto==0 && montoTotalCoberturaVegetal>0){
				panelCoberturaVegetal=true;
				//generarNUT=true;
			}
			
			if(transaccionesFinancieras.size()>0){
				cumpleMonto = cumpleMonto(1);	
			}else{
				cumpleMonto = cumpleMonto(0);
			}
			
			if(montoTotalProyecto==0){
				cumpleMontoProyecto=true;
			}
			
			if(bandejaTareasBean.getTarea().getProcessName().equals("Registro de generador de desechos especiales y peligrosos")){
				//generarNUT=true;
			}
			
			if(proyectosBean.getProyecto()!=null && proyectosBean.getProyecto().getCodigo() != null && !proyectosBean.getProyecto().isAreaResponsableEnteAcreditado() && montoTotalProyecto>0){
				// generarNUT=true; // ocultado el nut  para produccion
			}
			
			String codigoTramite="";
			if(proyectosBean.getProyecto()!=null && proyectosBean.getProyecto().getCodigo() != null){
				codigoTramite=proyectosBean.getProyecto().getCodigo();
			}else{
				codigoTramite=bandejaTareasBean.getTarea().getProcedure();
			}
			 
			documentosNUT = new ArrayList<>();
			List<NumeroUnicoTransaccional> listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoTramite);
			if(listNUTXTramite!=null && listNUTXTramite.size()>0){
				for (NumeroUnicoTransaccional nut : listNUTXTramite) {
					List<Documento> comprobantes = documentosFacade
							.documentoXTablaIdXIdDoc(nut.getSolicitudUsuario().getId(),
									"NUT RECAUDACIONES", TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
					
					if (comprobantes.size() > 0) {
						documentosNUT.addAll(comprobantes);
					} 
				}
			}
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al cargar los datos");
			LOG.error("Error en PagoServiciosBean", e);
		}
	}

	/**
	 * @param nextURL
	 *            Próxima URL a navegar después de ejecutar el pago.
	 * @param completeOperation
	 *            Lógica a ejecutar después de ejecutar el pago.
	 * @param montoTotal
	 *            Total del monto a pagar.
	 * @param identificadorMotivo
	 *            Código del motivo, si el pago es asociado al proyecto, debe ser <b>null</b>.
	 * @param imagenAyudaPropia
	 *            Imagen personalizada para mostrar en la ayuda 
	 * @param textoAyudaPropia
	 *            Texto personalizado para mostrar en la ayuda
	 * @param mostrarAyuda
	 *            Si deseo o no mostrar la ayuda                            
	 * @throws ServiceException
	 */
	public void initFunctionWithProjectOrMotive(String nextURL, CompleteOperation completeOperation, Map<String, Float> costoTotalProyecto,
			String identificadorMotivo, String imagenAyudaPropia, String textoAyudaPropia, boolean mostrarAyuda) {
		super.initFunction(nextURL, completeOperation);
		this.montoTotal = costoTotalProyecto.get("valorAPagar")==0?(costoTotalProyecto.get("coberturaVegetal")!=null?costoTotalProyecto.get("coberturaVegetal"):0):costoTotalProyecto.get("valorAPagar");
		this.montoTotalCoberturaVegetal=(costoTotalProyecto.get("coberturaVegetal")!=null?costoTotalProyecto.get("coberturaVegetal"):0);
		this.montoTotalProyecto=(costoTotalProyecto.get("valorAPagar")-(costoTotalProyecto.get("coberturaVegetal")!=null?costoTotalProyecto.get("coberturaVegetal"):0));
		this.montoTotalProyecto=this.montoTotalProyecto<=0.0 ? 0.0:this.montoTotalProyecto;
		
		
		
		this.identificadorMotivo = identificadorMotivo;
		this.imagenAyudaPropia = imagenAyudaPropia;
		this.textoAyudaPropia = textoAyudaPropia;
		this.mostrarAyuda = mostrarAyuda;
		this.datoFormaspago=true;
		initData();
	}

	@Override
	public String getFunctionURL() {
		return "/comun/pagoServicios.jsf";
	}

	@Override
	public void cleanData() {
		identificadorMotivo = null;
		transaccionFinanciera = new TransaccionFinanciera();
		transaccionFinancieraCobertura = new TransaccionFinanciera();
		transaccionesFinancieras = new ArrayList<TransaccionFinanciera>();
	}

	@Override
	public boolean executeBusinessLogic(Object object) {
		if(persist) {
		String motivo = this.identificadorMotivo == null ? 
				((proyectosBean.getProyecto() == null || proyectosBean.getProyecto().getCodigo() == null) ? proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() : proyectosBean.getProyecto().getCodigo())
			: this.identificadorMotivo;
						
		List<TransaccionFinanciera> transaccionesFinancierasProyecto= new ArrayList<TransaccionFinanciera>();
		List<TransaccionFinanciera> transaccionesFinancierasCobertura= new ArrayList<TransaccionFinanciera>();
		for (TransaccionFinanciera transaccionFinanciera : getTransaccionesFinancieras()) {
			if(transaccionFinanciera.getTipoPagoProyecto().contains("Proyecto")){
				transaccionesFinancierasProyecto.add(transaccionFinanciera);
			}else {
				transaccionesFinancierasCobertura.add(transaccionFinanciera);
			}
		}
		boolean pagoSatisfactorio=false;
		if(proyectosBean.getProyecto()!=null && proyectosBean.getProyecto().getCodigo() != null && !proyectosBean.getProyecto().isAreaResponsableEnteAcreditado() && transaccionesFinancierasProyecto.size()>0){
			if(montoTotalProyecto!=montoTotal){
				montoTotalProyecto=montoTotal;
			}
		}
						
		if(transaccionesFinancierasProyecto.size()>0){
			pagoSatisfactorio=transaccionFinancieraFacade.realizarPago(montoTotalProyecto, transaccionesFinancierasProyecto,motivo);
			for (TransaccionFinanciera transaccionFinanciera : transaccionesFinancierasProyecto) {
				try {
					NumeroUnicoTransaccional nut=new NumeroUnicoTransaccional();
					nut=numeroUnicoTransaccionalFacade.buscarNUTPorNumeroTramite(transaccionFinanciera.getNumeroTransaccion());
					if(nut!=null){
						nut.setNutUsado(true);
						crudServiceBean.saveOrUpdate(nut);
					}
				} catch (ec.gob.ambiente.suia.exceptions.ServiceException e) {
					e.printStackTrace();
				}
			}
		}
		if(transaccionesFinancierasCobertura.size()>0){
			pagoSatisfactorio=transaccionFinancieraFacade.realizarPago(montoTotalCoberturaVegetal, transaccionesFinancierasCobertura,motivo);
		}
		
		if(transaccionesFinancierasCobertura.size()>0){
			pagoSatisfactorio=transaccionFinancieraFacade.realizarPago(montoTotalCoberturaVegetal, transaccionesFinancierasCobertura,motivo);
			for (TransaccionFinanciera transaccionF : transaccionesFinancierasCobertura) {
				try {
					NumeroUnicoTransaccional nut=new NumeroUnicoTransaccional();
					nut=numeroUnicoTransaccionalFacade.buscarNUTPorNumeroTramite(transaccionF.getNumeroTransaccion());
					if(nut!=null){
						nut.setNutUsado(true);
						crudServiceBean.saveOrUpdate(nut);
					}
				} catch (ec.gob.ambiente.suia.exceptions.ServiceException e) {
					e.printStackTrace();
				}
			}
		}
			
		//boolean pagoSatisfactorio = transaccionFinancieraFacade.realizarPago(montoTotal, getTransaccionesFinancieras(),motivo);
		
		if (pagoSatisfactorio) {
            transaccionFinancieraFacade.guardarTransacciones(getTransaccionesFinancieras(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getTaskName(),
                    bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getProcessId(),
                    bandejaTareasBean.getTarea().getProcessName());
            
            if (pagoScoutDrilling) {
        		try{
        			if(proyectosBean.getProyectoRcoa() != null)
        			{
        				categoriaIIFacade.ingresarDocumentoCoa(documento.getContenidoDocumento(), proyectosBean.getProyectoRcoa(), 
        						bandejaTareasBean.getTarea().getProcessInstanceId(), 
        						TipoDocumentoSistema.TIPO_RLA_FACTURA_PERMISO_AMBIENTAL, documento.getNombre());
        			}
        			else
        			{
        				documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(),
        						Constantes.NOMBRE_PROCESO_CATEGORIA2, bandejaTareasBean.getProcessId(), 
        						documento, TipoDocumentoSistema.TIPO_RLA_FACTURA_PERMISO_AMBIENTAL,
        						null);
        			}
        		}catch(Exception ex){
    				JsfUtil.addMessageError("Error al adjuntar el documento.");
        		}
			}
            
			JsfUtil.addMessageInfo("Pago satisfactorio.");
			executeOperation(object);
			// envio notificacion al tecnico de mineria si es un proyecto de scout drilling
			if (pagoScoutDrilling) {
				GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil.getBean(GenerarNotificacionesAplicativoController.class);
				
				if(proyectosBean.getProyecto() != null)
					enviarNotificaciones.envioNotificacion(proyectosBean.getProyecto(), "TÉCNICO SONDEO MAE", proyectosBean.getProponente(), " ", false);
				else
					enviarNotificaciones.envioNotificacionRcoa(proyectosBean.getProyectoRcoa(), "TÉCNICO SONDEO MAE", proyectosBean.getProponente(), " ", false);
			}
			finalizarRegistroambiental();
		} else {
			JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
		}

		return pagoSatisfactorio; }
		else{
			JsfUtil.addMessageInfo("Pago satisfactorio.");
			executeOperation(object);
			finalizarRegistroambiental();
			return true;
		}
	}

/***
 * funcion para finalizar el registro ambiental en la tarea de validar el pago para registros categoria II
 * fecha:  2018-10-17
 *  
 */
	public void finalizarRegistroambiental(){
		// solo si es categoria II y esta en la tarea de  "Validar Pago por servicios administrativos"
		FichaAmbientalGeneralFinalizarControllerV2 fichaAmbientalGeneralFinalizarControllerV2 = JsfUtil.getBean(FichaAmbientalGeneralFinalizarControllerV2.class);
		if(proyectosBean.getProyecto()!=null && proyectosBean.getProyecto().getCodigo() != null && proyectosBean.getProyecto().getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_II && bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("validar pago por servicios administrativos")){
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
		else
		{
			if(proyectosBean.getProyectoRcoa() != null)
			{

				try {
					PerforacionExplorativa objProyectoPE = fichaAmbientalMineria020Facade.cargarPerforacionExplorativaRcoa(proyectosBean.getProyectoRcoa().getId());
					if(objProyectoPE != null && objProyectoPE.getId() != null){
						// recupero la tarea actual que se genero 
						Long processInstanceId = bandejaTareasBean.getProcessId();
						TaskSummary tareaActual = procesoFacade.getCurrenTask(JsfUtil.getLoggedUser(), processInstanceId);
						// si la tarea actual es "Descargar documentos de Registro Ambiental " finalizo el proceso
						if(tareaActual.getName().toLowerCase().contains("descargar documentos de registro ambiental")){
							Map<String, Object> processVariables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), processInstanceId);
							ProcessInstanceLog processLog = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(), processInstanceId);
							bandejaTareasBean.setTarea(new TaskSummaryCustomBuilder().fromSuiaIII(processVariables,	processLog.getProcessName(), tareaActual).build());
//							fichaAmbientalGeneralFinalizarControllerV2.generarFichaRegistroAmbientalRCOA();
							fichaAmbientalGeneralFinalizarControllerV2.setDescargarFicha(true);
							fichaAmbientalGeneralFinalizarControllerV2.setDescargarRegistro(true);
							fichaAmbientalGeneralFinalizarControllerV2.redireccionarBandeja();
							if(fichaAmbientalGeneralFinalizarControllerV2.getDescargarRegistro()){
								fichaAmbientalGeneralFinalizarControllerV2.direccionar();
								fichaAmbientalGeneralFinalizarControllerV2.cambiarEstadoTarea();
							}
						}

					}
				} catch (ec.gob.ambiente.suia.exceptions.ServiceException | JbpmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}	

	public void guardarTransaccion(Integer pagoProyecto) {
		if(pagoProyecto==2){
			transaccionFinanciera = new TransaccionFinanciera();
			transaccionFinanciera=transaccionFinancieraCobertura;
		}else {
			transaccionFinancieraCobertura= new TransaccionFinanciera();
		}
		
		if (transaccionFinanciera.getInstitucionFinanciera() != null
				&& transaccionFinanciera.getNumeroTransaccion() != "") {
			try {
				//Mariela recaudaciones
				String codigoTramite = "";
				if (proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getCodigo() != null) {
					codigoTramite = proyectosBean.getProyecto().getCodigo();
				} else {
					codigoTramite = bandejaTareasBean.getTarea().getProcedure();
				}

				/*List<NumeroUnicoTransaccional> listaNutsTramite = numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoTramite);
				if (listaNutsTramite != null && listaNutsTramite.size() > 0) {
					// verificacion de NUT en estado pagado 
					NumeroUnicoTransaccional nut = listaNutsTramite.get(0);
					if (!nut.getEstadosNut().getId().equals(3)) {
						JsfUtil.addMessageWarning("El número NUT " + nut.getNutCodigo() + " aún no ha sido pagado");
						return;
					}

					//verificación de que el #comprobante ingresado corresponda con el numero de referencia de pago del NUT
					if (!nut.getBnfTramitNumber().equals(transaccionFinanciera.getNumeroTransaccion())) {
						JsfUtil.addMessageWarning("El número de comprobante " + transaccionFinanciera.getNumeroTransaccion() + " no se relaciona con el NUT generado");
						return;
					}
				}*/
				//fin recaudaciones
				//validaciones pagos liberados RGD
				//verifico si tiene un pago liberado por el numero de tramite
				boolean existePagoLiberado = false;
				List<ProyectosConPagoSinNut> ListProyectoNoNut = proyectosConPagoSinNutFacade.buscarPorProyectoPorUsuarioPorCodigoProyecto(JsfUtil.getLoggedUser(), codigoTramite);
				if(ListProyectoNoNut != null){
					for (ProyectosConPagoSinNut objProyectoNoNut : ListProyectoNoNut) {
						if(objProyectoNoNut.getNumeroTramite().equals(transaccionFinanciera.getNumeroTransaccion())){
							existePagoLiberado = true;
							break;
						}
					}
					if(!existePagoLiberado){
						JsfUtil.addMessageError("El número de transacción ingresado no corresponde al pago liberado del proyecto "+codigoTramite+".");
						return;
					}
				}
				// verifico si tiene un pago liberado por el numero de transaccion
				if(!existePagoLiberado){
					ListProyectoNoNut = proyectosConPagoSinNutFacade.buscarPorProyectoPorUsuarioPorNumero(transaccionFinanciera.getNumeroTransaccion());
					if(ListProyectoNoNut != null){
						//valido que sea del mismo operador
						for (ProyectosConPagoSinNut objProyectoNoNut : ListProyectoNoNut) {
							if(!objProyectoNoNut.getUsuario().equals(JsfUtil.getLoggedUser())){
								JsfUtil.addMessageError("El número de transacción ingresado no corresponde al operador del proyecto "+codigoTramite+".");
								return;
							}
						}
						for (ProyectosConPagoSinNut objProyectoNoNut : ListProyectoNoNut) {
							if(objProyectoNoNut.getTipoProceso().equals(1)){
								existePagoLiberado = true;
								break;
							}
						}
						if(!existePagoLiberado){
							JsfUtil.addMessageError("El número de transacción ingresado corresponde al pago liberado de otro proyecto "+".");
							return;
						}
					}
				}
				//fin validacion pagos liberados RGD
				if (existeTransaccion(transaccionFinanciera)) {
					JsfUtil.addMessageInfo("El número de comprobante: " + transaccionFinanciera.getNumeroTransaccion()
							+ " (" + transaccionFinanciera.getInstitucionFinanciera().getNombreInstitucion()
							+ ") ya fue registrado por usted. Ingrese otro distinto.");
					transaccionFinanciera = new TransaccionFinanciera();
					transaccionFinancieraCobertura = new TransaccionFinanciera();
					return;
				} else {
					double monto = transaccionFinancieraFacade.consultarSaldo(transaccionFinanciera
							.getInstitucionFinanciera().getCodigoInstitucion(), transaccionFinanciera
							.getNumeroTransaccion());

					if (monto == 0) {
						JsfUtil.addMessageError("El número de comprobante: "
								+ transaccionFinanciera.getNumeroTransaccion() + " ("
								+ transaccionFinanciera.getInstitucionFinanciera().getNombreInstitucion()
								+ ") no ha sido registrado.");
						transaccionFinanciera = new TransaccionFinanciera();
						transaccionFinancieraCobertura = new TransaccionFinanciera();
						return;
					} else {
						transaccionFinanciera.setMontoTransaccion(monto);
						transaccionFinanciera.setTipoPagoProyecto((pagoProyecto==1)?"Proyecto":"Cobertura");
						if (this.identificadorMotivo == null) {
							if(proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getCodigo() != null){
								transaccionFinanciera.setProyecto(proyectosBean.getProyecto());
							}else if(proyectosBean.getProyectoRcoa() != null){
								transaccionFinanciera.setProyectoRcoa(proyectosBean.getProyectoRcoa());
							}							
							transaccionFinanciera.setIdentificadorMotivo(null);
						} else {
							transaccionFinanciera.setProyecto(null);
							transaccionFinanciera.setIdentificadorMotivo(this.identificadorMotivo);
						}
						transaccionesFinancieras.add(transaccionFinanciera);
						cumpleMonto = cumpleMonto(pagoProyecto);
						transaccionFinanciera = new TransaccionFinanciera();
						transaccionFinancieraCobertura = new TransaccionFinanciera();
						return;
					}
				}
			} catch (Exception e) {
				JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
				return;
			}
		} else {
			JsfUtil.addMessageWarning("Debe ingresar datos correctos para la transacción");
		}
	}

	private boolean existeTransaccion(TransaccionFinanciera _transaccionFinanciera) {

		for (TransaccionFinanciera transaccion : transaccionesFinancieras) {
			if (transaccion.getNumeroTransaccion().trim().equals(_transaccionFinanciera.getNumeroTransaccion())
					&& transaccion.getInstitucionFinanciera().getCodigoInstitucion().trim()
							.equals(_transaccionFinanciera.getInstitucionFinanciera().getCodigoInstitucion())) {
				return true;
			}
		}
		return false;
	}

	private boolean cumpleMonto(Integer pagoProyecto) {
        double montoTotal = 0;
        double montoTotalCobertura = 0;
        for (TransaccionFinanciera transa : transaccionesFinancieras) {
//        	montoTotal += transa.getMontoTransaccion();
        	if(transa.getTipoPagoProyecto() == null)
				transa.setTipoPagoProyecto((panelCoberturaVegetal) ? "Cobertura" : "Proyecto"); // para solventar error de la siguiente linea cuando el campo es nulo
        	
        	if(transa.getTipoPagoProyecto().equals("Proyecto")){
            	montoTotal += transa.getMontoTransaccion();
            }else {
            	montoTotalCobertura += transa.getMontoTransaccion();
			}
        }
       
        DecimalFormat decimalValorTramite= new DecimalFormat(".##");
        String x="";
       
        x=decimalValorTramite.format(this.montoTotal).replace(",",".");
        
        if(proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getCodigo() != null){
        	if(pagoProyecto==1 && proyectosBean.getProyecto()!=null && proyectosBean.getProyecto().isAreaResponsableEnteAcreditado() && montoTotalCoberturaVegetal>0){
            	this.montoTotal=Double.valueOf(decimalValorTramite.format(this.montoTotalProyecto).replace(",","."));
            	 if (montoTotal >= this.montoTotal) {
            		 cumpleMontoProyecto=true;
                     return true;
                 }
            	 cumpleMontoProyecto=false;
            	 return false;
            }
            
            if(pagoProyecto==2 && proyectosBean.getProyecto()!=null && proyectosBean.getProyecto().isAreaResponsableEnteAcreditado() && montoTotalCoberturaVegetal>0){
            	this.montoTotal=Double.valueOf(decimalValorTramite.format(this.montoTotalCoberturaVegetal).replace(",","."));
            	 if (montoTotalCobertura >= this.montoTotal) {
            		 cumpleMontoCobertura=true;
                     return true;
                 }
            	 cumpleMontoCobertura=false;
            	 return false;
            }
            
            if(pagoProyecto==2 && proyectosBean.getProyecto()!=null && montoTotalCoberturaVegetal>0 && montoTotal==0){
            	this.montoTotal=Double.valueOf(decimalValorTramite.format(this.montoTotalCoberturaVegetal).replace(",","."));
            	 if (montoTotalCobertura >= this.montoTotal) {
            		 cumpleMontoCobertura=true;
                     return true;
                 }
            	 cumpleMontoCobertura=false;
            	 return false;
            }
        }                
        
        this.montoTotal=Double.valueOf(x);        
        if (montoTotal >= this.montoTotal) {
        	cumpleMontoProyecto=true;
            return true;
        }
        cumpleMontoProyecto=false;
        return false;
    }
	
	public double Monto() {
		double montoTotal = 0;
		for (TransaccionFinanciera transa : transaccionesFinancieras) {
			montoTotal += transa.getMontoTransaccion();
		}
		return montoTotal;
	}

	public void eliminarTransacion(TransaccionFinanciera transaccion) throws Exception {
		transaccionesFinancieras.remove(transaccion);
		if(transaccion.getTipoPagoProyecto().equals("Proyecto")){
			cumpleMonto = cumpleMonto(1);
		}else {
			cumpleMonto = cumpleMonto(2);
		}
	}

	public String completarTarea() {
		this.montoTotal=montoTotalProyecto+montoTotalCoberturaVegetal;
		
		DecimalFormat decimalValorTramite= new DecimalFormat(".##");
        String x="";
       
        x=decimalValorTramite.format(this.montoTotal).replace(",",".");
        
        this.montoTotal=Double.valueOf(x);
        
		
		if(montoTotalCoberturaVegetal>0 && proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
			List<TransaccionFinanciera>listTransCobertura= new ArrayList<TransaccionFinanciera>();
			for (TransaccionFinanciera transaccionFinanciera : getTransaccionesFinancieras()) {
				if(transaccionFinanciera.getTipoPagoProyecto().contains("Cobertura")){
					listTransCobertura.add(transaccionFinanciera);
				}
			}
			if(listTransCobertura.size()==0 || !cumpleMontoCobertura){
				JsfUtil.addMessageError("Debe registrar un pago para cobertura vegetal antes de continuar.");
				return null;
			}
		}
		
		if(montoTotalProyecto>0){
			List<TransaccionFinanciera>listTransProyecto= new ArrayList<TransaccionFinanciera>();
			for (TransaccionFinanciera transaccionFinanciera : getTransaccionesFinancieras()) {
				if(transaccionFinanciera.getTipoPagoProyecto().contains("Proyecto")){
					listTransProyecto.add(transaccionFinanciera);
				}
			}
			if(listTransProyecto.size()==0 || !cumpleMontoProyecto){
				JsfUtil.addMessageError("Debe registrar un pago para el proyecto antes de continuar.");
				return null;
			}
		}
		
		if(montoTotalProyecto==0.0 && montoTotalCoberturaVegetal>0){
			List<TransaccionFinanciera>listTransCobertura= new ArrayList<TransaccionFinanciera>();
			for (TransaccionFinanciera transaccionFinanciera : getTransaccionesFinancieras()) {
				if(transaccionFinanciera.getTipoPagoProyecto().contains("Cobertura")){
					listTransCobertura.add(transaccionFinanciera);
				}
			}
			if(listTransCobertura.size()==0 || !cumpleMontoCobertura){
				JsfUtil.addMessageError("Debe registrar un pago para cobertura vegetal antes de continuar.");
				return null;
			}
		}
		
		if (getTransaccionesFinancieras().size() == 0) {
			JsfUtil.addMessageError("Debe registrar un pago antes de continuar.");
			return null;
		}

		if (!getCumpleMonto()) {
			JsfUtil.addMessageError("El monto de la transacción utilizada es insuficiente para completar el pago de tasas.");
			return null;
		}
		
		//CrisF: validacion de factura de facilitadores
		if(pagoFacilitadores){
			if(documento == null){
				JsfUtil.addMessageError("Debe adjuntar la factura del pago de tasas de facilitadores.");
				return null;
			}
		}
		
		//validacion de factura de scout drilling
		if (pagoScoutDrilling) {
			if(documento == null){
				JsfUtil.addMessageError("Debe adjuntar la factura del pago de tasas.");
				return null;
			}
		}


		double montoTotal = Monto();
		if(proyectosBean.getProyecto()!=null && proyectosBean.getProyecto().getCodigo() != null && proyectosBean.getProyecto().getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_II && bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("validar pago por servicios administrativos")){
			if (pagoScoutDrilling) {
				setMensajeFinalizar("Alerta: Usted debe ingresar mediante oficio dirigido a la Dirección Financiera de esta Cartera de Estado, la documentación original de la Póliza o garantía bancaria de fiel cumplimiento, declaración juramentada de que el valor de la póliza o garantía corresponda al 100% del valor del Plan de Manejo Ambiental, la factura del pago de tasa ambiental por la obtención del Registro Ambiental y en el caso de que el proyecto lo requiera la factura de pago de tasa por inventario forestal. En caso de incumplimiento de lo antes expuesto se aplicará las sanciones establecidas en la normativa ambiental vigente.");
				
				if (montoTotal > this.montoTotal) {
					RequestContext.getCurrentInstance().execute("PF('dlg1V2').show();");
				} else if (montoTotal == this.montoTotal) {
					RequestContext.getCurrentInstance().execute("PF('send').disable();");
					RequestContext.getCurrentInstance().execute("PF('continuarDialog').show();");
				}
			}else{			
				if (montoTotal > this.montoTotal) {
					RequestContext.getCurrentInstance().execute("PF('dlg1').show();");
				} else if (montoTotal == this.montoTotal) {
					RequestContext.getCurrentInstance().execute("PF('send').disable();");
					enviarNotificacionFirma();
					return executeOperationAction(montoTotal);
				}
			}
		}else{
			if (montoTotal > this.montoTotal) {
				RequestContext.getCurrentInstance().execute("PF('dlg1').show();");
			} else if (montoTotal == this.montoTotal) {
				RequestContext.getCurrentInstance().execute("PF('send').disable();");
				return executeOperationAction(montoTotal);
			}
		}
		return null;
	}

	public void mostrarDialogo() {
		RequestContext.getCurrentInstance().execute("PF('continuarDialog').show();");
	}
	
	/**
     * Nombre:SUIA
     * Descripción: obtener entes acreditados para pagos
     * ParametrosIngreso:
     * PArametrosSalida:
     * Fecha:17/09/2015
     **/
	public void obtenerEntesAcreditados() {
		try {
			if (transaccionFinanciera.getInstitucionFinanciera() != null
					&& transaccionFinanciera.getInstitucionFinanciera()
							.getCodigoInstitucion().equals("2")) {
				eligioEnte = true;
				entesAcreditados = institucionFinancieraFacade
						.obtenerEntesAcreditados();
				RequestContext.getCurrentInstance().update(
						":#{p:component('entidadBancaria')}");
				if (entesAcreditados == null) {
					JsfUtil.addMessageError("No se obtuvieron entes acreditados disponibles, intente más tarde nuevamente");
				}
			} else {
				eligioEnte = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error("Error en PagoServiciosBean", e);
			JsfUtil.addMessageError("No se obtuvieron entes acreditados disponibles, intente más tarde nuevamente");
		}
	}
	/**
	  * FIN obtener entes acreditados para pagos
	  */
	@Override
	public String executeOperationAction() {
		RequestContext.getCurrentInstance().execute("PF('sendMore').disable();PF('sendCancel').disable();");
		
		if (proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getCodigo() != null
				&& proyectosBean.getProyecto().getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_II
				&& bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("validar pago por servicios administrativos")) {
			enviarNotificacionFirma();
		}	
		
		return super.executeOperationAction();
	}
	
	
	public void setDatoFormaspago(Boolean datoFormaspago) {
		this.datoFormaspago = datoFormaspago;

	}
	
	/**
     * CFlores: aumento para adjuntar la factura de los facilitadores
     */
    @Getter
    @Setter
    private Documento documento;
    public void uploadListenerDocumento(FileUploadEvent event) {
		documento = this.uploadListener(event, ParticipacionSocialAmbiental.class, "pdf");
	}
	
	private Documento uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = crearDocumento(contenidoDocumento, clazz, extension);
		documento.setNombre(event.getFile().getFileName());
		
		if(proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getId() != null)
			documento.setIdTable(proyectosBean.getProyecto().getId());
		else
			documento.setIdTable(proyectosBean.getProyectoRcoa().getId());
		return documento;
	}
	
	public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
		Documento documento = new Documento();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setExtesion("." + extension);

		documento.setMime(extension == "pdf" ? "application/pdf" : "application/vnd.ms-excel");
		return documento;
	}
	
	public void uploadListenerDocumentoSD(FileUploadEvent event) {
		documento = this.uploadListener(event, ProyectoLicenciamientoAmbiental.class, "pdf");
    }
	
	
	@Getter
	@Setter
	private List<NumeroUnicoTransaccional>listNUT= new ArrayList<NumeroUnicoTransaccional>();
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;
    	
	public void generarNUT() throws Exception{	
		
		List<NumeroUnicoTransaccional> listNUTXTramite=new ArrayList<NumeroUnicoTransaccional>();
		NumeroUnicoTransaccional numeroUnicoTransaccional;
		TarifasNUT tarifasNUT= new TarifasNUT();
		SolicitudUsuario solicitudUsuario= new SolicitudUsuario();
		solicitudUsuario.setUsuario(loginBean.getUsuario());
		
		DecimalFormat formato1 = new DecimalFormat("#.00");
		
		String codigoTramite="";
		if(proyectosBean.getProyecto()!=null){
			codigoTramite=proyectosBean.getProyecto().getCodigo();
		}else{
			codigoTramite=bandejaTareasBean.getTarea().getProcedure();
		}
		 
		listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoTramite);
		if(listNUTXTramite!=null && listNUTXTramite.size()>0){			
			for (NumeroUnicoTransaccional nut : listNUTXTramite) {
				if(nut.getEstadosNut().getId()==5){
					nut.setNutFechaActivacion(new Date());
					nut.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
					nut.setEstadosNut(new EstadosNut(2));
					crudServiceBean.saveOrUpdate(nut);
					JsfUtil.addMessageWarning("Su trámite está caducado, se activo nuevamente por 3 días, por favor revisar.");
				}
			}
			JsfUtil.addMessageWarning("Usted ya tiene generado un Número Único de Trámite, por favor descargue en documentos del trámite.");
			return;
		}
		
		solicitudUsuario.setSolicitudDescripcion("Pagos por el trámite: "+codigoTramite);
		solicitudUsuario.setSolicitudCodigo(JsfUtil.getBean(GenerarNUTController.class).generarCodigoSolicitud());
		
		crudServiceBean.saveOrUpdate(solicitudUsuario);
		Integer numeroDocumento=1;
		
		if(bandejaTareasBean.getTarea().getProcessName().equals("Registro de generador de desechos especiales y peligrosos") && montoTotalProyecto>0){
			numeroUnicoTransaccional = new NumeroUnicoTransaccional();
			numeroUnicoTransaccional.setNutCodigo(secuenciasFacade.getNextValueDedicateSequence("NUT_CODIGO", 10));
			numeroUnicoTransaccional.setNutFechaActivacion(new Date());
			numeroUnicoTransaccional.setEstadosNut(new EstadosNut(2));
			numeroUnicoTransaccional.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
			numeroUnicoTransaccional.setSolicitudUsuario(solicitudUsuario);
			numeroUnicoTransaccional.setCuentas(new Cuentas(1));
			numeroUnicoTransaccional.setNutValor(montoTotalProyecto);
			numeroUnicoTransaccional.setNutCodigoProyecto(codigoTramite);
			crudServiceBean.saveOrUpdate(numeroUnicoTransaccional);
			
			tarifasNUT.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
			tarifasNUT.setTarifas(new Tarifas(26));
			
			tarifasNUT.setCantidad(1);
			tarifasNUT.setValorUnitario(montoTotalProyecto);
			crudServiceBean.saveOrUpdate(tarifasNUT);
			JsfUtil.getBean(GenerarNUTController.class).setSolicitudUsuario(solicitudUsuario);
			JsfUtil.getBean(GenerarNUTController.class).generarDocumentoNutPago(numeroUnicoTransaccional, solicitudUsuario, numeroDocumento);
			numeroDocumento++;
		}
		
		if(bandejaTareasBean.getTarea().getProcessName().equals("Registro ambiental v2") && montoTotalProyecto>0 && !proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
			numeroUnicoTransaccional = new NumeroUnicoTransaccional();
			numeroUnicoTransaccional.setNutCodigo(secuenciasFacade.getNextValueDedicateSequence("NUT_CODIGO", 10));
			numeroUnicoTransaccional.setNutFechaActivacion(new Date());
			numeroUnicoTransaccional.setEstadosNut(new EstadosNut(2));
			numeroUnicoTransaccional.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
			numeroUnicoTransaccional.setSolicitudUsuario(solicitudUsuario);
			numeroUnicoTransaccional.setCuentas(new Cuentas(1));
			numeroUnicoTransaccional.setNutValor(montoTotalProyecto);
			numeroUnicoTransaccional.setNutCodigoProyecto(codigoTramite);
			crudServiceBean.saveOrUpdate(numeroUnicoTransaccional);
			
			tarifasNUT.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
			
			if(montoTotalProyecto==80.0){
				tarifasNUT.setTarifas(new Tarifas(30));
			}
			
			if(montoTotalProyecto==180.0){
				tarifasNUT.setTarifas(new Tarifas(25));
			}
			
			tarifasNUT.setCantidad(1);
			tarifasNUT.setValorUnitario(montoTotalProyecto);
			crudServiceBean.saveOrUpdate(tarifasNUT);
			JsfUtil.getBean(GenerarNUTController.class).setSolicitudUsuario(solicitudUsuario);
			JsfUtil.getBean(GenerarNUTController.class).generarDocumentoNutPago(numeroUnicoTransaccional, solicitudUsuario, numeroDocumento);
			numeroDocumento++;
		}
		
		if(bandejaTareasBean.getTarea().getProcessName().equals("Evaluacion Social") && montoTotalProyecto>0 && !proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
			numeroUnicoTransaccional = new NumeroUnicoTransaccional();
			numeroUnicoTransaccional.setNutCodigo(secuenciasFacade.getNextValueDedicateSequence("NUT_CODIGO", 10));
			numeroUnicoTransaccional.setNutFechaActivacion(new Date());
			numeroUnicoTransaccional.setEstadosNut(new EstadosNut(2));
			numeroUnicoTransaccional.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
			numeroUnicoTransaccional.setSolicitudUsuario(solicitudUsuario);
			numeroUnicoTransaccional.setCuentas(new Cuentas(1));
			numeroUnicoTransaccional.setNutValor(montoTotalProyecto);
			numeroUnicoTransaccional.setNutCodigoProyecto(codigoTramite);
			crudServiceBean.saveOrUpdate(numeroUnicoTransaccional);
			
			tarifasNUT.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
	
			
			Tarifas tarifas= new Tarifas();
			
			tarifas=crudServiceBean.find(Tarifas.class, 14);
			tarifasNUT.setTarifas(tarifas);			
			Double cantidad=montoTotalProyecto/tarifas.getTasasValor();
			tarifasNUT.setCantidad(cantidad.intValue());
			tarifasNUT.setValorUnitario(tarifas.getTasasValor());
			
			if(proyectosBean.getProyecto().getAreaResponsable().getId()==272){
				tarifas=crudServiceBean.find(Tarifas.class, 15);
				tarifasNUT.setTarifas(tarifas);
				cantidad=montoTotalProyecto/tarifas.getTasasValor();
				tarifasNUT.setCantidad(cantidad.intValue());
				tarifasNUT.setValorUnitario(tarifas.getTasasValor());
			}
			
			crudServiceBean.saveOrUpdate(tarifasNUT);
			JsfUtil.getBean(GenerarNUTController.class).setSolicitudUsuario(solicitudUsuario);
			JsfUtil.getBean(GenerarNUTController.class).generarDocumentoNutPago(numeroUnicoTransaccional, solicitudUsuario, numeroDocumento);
			numeroDocumento++;
		}
		
		if(bandejaTareasBean.getTarea().getProcessName().equals("Licencia Ambiental") && montoTotalProyecto>0 && !proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
			numeroUnicoTransaccional = new NumeroUnicoTransaccional();
			numeroUnicoTransaccional.setNutCodigo(secuenciasFacade.getNextValueDedicateSequence("NUT_CODIGO", 10));
			numeroUnicoTransaccional.setNutFechaActivacion(new Date());
			numeroUnicoTransaccional.setEstadosNut(new EstadosNut(2));
			numeroUnicoTransaccional.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
			numeroUnicoTransaccional.setSolicitudUsuario(solicitudUsuario);
			numeroUnicoTransaccional.setCuentas(new Cuentas(1));
			numeroUnicoTransaccional.setNutValor(montoTotalProyecto);
			numeroUnicoTransaccional.setNutCodigoProyecto(codigoTramite);
			crudServiceBean.saveOrUpdate(numeroUnicoTransaccional);
			
			tarifasNUT.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
			
			Tarifas tarifas= new Tarifas();
			tarifas=crudServiceBean.find(Tarifas.class, 74);
			tarifasNUT.setTarifas(tarifas);			
			tarifasNUT.setCantidad(1);
			tarifasNUT.setValorUnitario(tarifas.getTasasValor());
			crudServiceBean.saveOrUpdate(tarifasNUT);
			
			JsfUtil.getBean(GenerarNUTController.class).setSolicitudUsuario(solicitudUsuario);
			JsfUtil.getBean(GenerarNUTController.class).generarDocumentoNutPago(numeroUnicoTransaccional, solicitudUsuario, numeroDocumento);
			numeroDocumento++;
		}
		
		if(montoTotalCoberturaVegetal>0){
			numeroUnicoTransaccional = new NumeroUnicoTransaccional();
			numeroUnicoTransaccional.setNutCodigo(secuenciasFacade.getNextValueDedicateSequence("NUT_CODIGO", 10));
			numeroUnicoTransaccional.setNutFechaActivacion(new Date());
			numeroUnicoTransaccional.setEstadosNut(new EstadosNut(2));
			numeroUnicoTransaccional.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
			numeroUnicoTransaccional.setSolicitudUsuario(solicitudUsuario);
			numeroUnicoTransaccional.setCuentas(new Cuentas(4));
			numeroUnicoTransaccional.setNutValor(Double.valueOf(formato1.format(montoTotalCoberturaVegetal).replace(",", ".")));
			numeroUnicoTransaccional.setNutCodigoProyecto(codigoTramite);
			crudServiceBean.saveOrUpdate(numeroUnicoTransaccional);
			
			tarifasNUT.setNumeroUnicoTransaccional(numeroUnicoTransaccional);			
			Tarifas tarifas= new Tarifas();
			tarifas=crudServiceBean.find(Tarifas.class, 43);
			tarifasNUT.setTarifas(tarifas);
			tarifasNUT.setCantidad(Double.valueOf(montoTotalCoberturaVegetal/tarifas.getTasasValor()).intValue());
			
			tarifasNUT.setValorUnitario(tarifas.getTasasValor());
			crudServiceBean.saveOrUpdate(tarifasNUT);
			
			JsfUtil.getBean(GenerarNUTController.class).setSolicitudUsuario(solicitudUsuario);
			JsfUtil.getBean(GenerarNUTController.class).generarDocumentoNutPago(numeroUnicoTransaccional, solicitudUsuario, numeroDocumento);
		}
		List<String>listaArchivos= new ArrayList<String>();
		listaArchivos=JsfUtil.getBean(GenerarNUTController.class).getListPathArchivos();
//		JsfUtil.getBean(GenerarNUTController.class).enviarNotificacionPago(solicitudUsuario.getUsuario(),codigoTramite,listaArchivos);
		
		JsfUtil.addMessageInfo("Se ha enviado un correo electrónico con los comprobantes para realizar el pago.");
	}
	
	public StreamedContent descargar(Documento documento) {
		DefaultStreamedContent content = null;
		try {
			if (documento.getIdAlfresco() != null) {
				byte[] documentoContent = documentosFacade.descargar(documento.getIdAlfresco());
				
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	private void enviarNotificacionFirma() {
		try {
			
			Area area = proyectosBean.getProyecto().getAreaResponsable();
			
			List<Usuario> listaUsuarios = new ArrayList<>();
			if(area.getTipoArea().getId().equals(3)){
				//entes
				listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName("role.reg.gad.autoridad"), area);
				
			}else if(area.getTipoArea().getId().equals(5)){
				//direccion zonal
				listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName("role.reg.cz.autoridad"), area);
				
			}else if(area.getTipoArea().getId().equals(6)){
				//oficina técnica
				listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName("role.reg.cz.autoridad"), area.getArea());
				
			}else if(area.getTipoArea().getId().equals(2)){
				//galapagos
				listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName("role.reg.galapagos.autoridad"), area);
				
			}else{
				//planta central
				listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName("role.reg.pc.subsecretario"), area);				
			}
			
			for(Usuario usuario : listaUsuarios){
				String emailDestino = "";

				List<Contacto> contacto = contactoFacade.buscarPorPersona(usuario.getPersona());
				for (Contacto con : contacto) {
					if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
						emailDestino = con.getValor();
						break;
					}
				}

				Object[] parametrosCorreo = new Object[] { usuario.getPersona().getNombre(), proyectosBean.getProyecto().getNombre(), bandejaTareasBean.getTarea().getProcedure() };

				String notificacion = "";
				notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionFirmaRegistroAutoridad", parametrosCorreo);
				
				NotificacionAutoridadesController email = new NotificacionAutoridadesController();
				email.sendEmailInformacionProponente(emailDestino, "", notificacion,"Firma de Registro Ambiental", proyectosBean.getProyecto().getCodigo(), usuario, JsfUtil.getLoggedUser());
								
			}
			
			/**
			 * NOTIFICACIÓN PARA EL OPERADOR
			 */
			String emailDestino = "";
			
			Organizacion org = organizacionFacade.buscarPorRuc(JsfUtil.getLoggedUser().getNombre());
			
			if(org != null && org.getId() != null){
				
				List<Contacto> contacto = contactoFacade.buscarPorOrganizacion(org);
				
				for (Contacto con : contacto) {
					if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
						emailDestino = con.getValor();
						break;
					}
				}				
				
			}else{
				List<Contacto> contacto = contactoFacade.buscarPorPersona(JsfUtil.getLoggedUser().getPersona());
				
				for (Contacto con : contacto) {
					if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
						emailDestino = con.getValor();
						break;
					}
				}
			}
			
			Object[] parametrosCorreo = new Object[] { JsfUtil.getLoggedUser().getPersona().getNombre(), proyectosBean.getProyecto().getNombre(), bandejaTareasBean.getTarea().getProcedure() };

			String notificacion = "";
			notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionRegistroAmbientalOperador", parametrosCorreo);
			
			NotificacionAutoridadesController email = new NotificacionAutoridadesController();
			email.sendEmailInformacionProponente(emailDestino, "", notificacion,"Avance de Proyecto", proyectosBean.getProyecto().getCodigo(), JsfUtil.getLoggedUser(), JsfUtil.getLoggedUser());				
			
			proyectosBean.getProyecto().setParaFirmaAutoridad(true);
			proyectoLicenciamientoAmbientalFacade.guardar(proyectosBean.getProyecto());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
