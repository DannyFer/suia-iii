package ec.gob.ambiente.prevencion.certificado.ambiental.controllers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.CertificadoAmbientalSumatoriaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.CertificadoAmbientalSumatoria;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.model.PagoKushkiJson;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.comun.bean.PagoServiciosBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.recaudaciones.bean.PagoRcoaBean;
import ec.gob.ambiente.suia.recaudaciones.controllers.GenerarNUTController;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.ProyectosConPagoSinNutFacade;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.recaudaciones.model.ProyectosConPagoSinNut;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;

@ManagedBean
@ViewScoped
public class PagoCertificadoController {
	
	private static final Logger LOG = Logger.getLogger(PagoCertificadoController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
	@EJB
	private CertificadoAmbientalSumatoriaFacade certificadoAmbientalSumatoriaFacade;
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;
	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ProyectosConPagoSinNutFacade proyectosConPagoSinNutFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
    	
	@Getter
	@Setter
	@ManagedProperty(value = "#{pagoRcoaBean}")
	private PagoRcoaBean pagoRcoaBean;
	
	float costoTotal = 100;
	
	@Getter
	@Setter
	private double valorAPagar;
	
	@Getter
	@Setter
	private boolean visualizarPopup = false;
	
	@Getter
	@Setter
	private String mensaje;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	private String tramite;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	public double montoTotal;
	
	private String identificadorMotivo;
	
	@Getter
	@Setter
	private Boolean persist = false;
	
	@Getter
	@Setter
	private Boolean condiciones = false;
	
	@Getter
	@Setter
	private String mensajeFinalizar;
	/*Ubicacion Geografica del Proyecto*/
	@Getter
	@Setter
	List<UbicacionesGeografica> ubicacionesSeleccionadas;

	private UbicacionesGeografica data_localizacion = new UbicacionesGeografica();	
	
	@PostConstruct
	public void init(){
		try {
			String url_redirec = "/pages/rcoa/certificadoAmbiental/realizarPagoTasaCA.jsf";
			pagoRcoaBean.setUrl_enlace(url_redirec);			
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			/*Método para obtener la ubicacion del proyecto*/
			cargarUbicacionProyecto(); 
			InventarioForestalAmbiental inventario = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(proyecto.getId());
			
			CertificadoAmbientalSumatoria suma = new CertificadoAmbientalSumatoria();
			suma = certificadoAmbientalSumatoriaFacade.getByIdInventarioForestalAmbiental(inventario.getId());
			
			valorAPagar = suma.getPagoDesbroceCobertura();
			this.montoTotal = valorAPagar;
			
			mensaje = 	"En cumplimiento al acuerdo ministerial 083-B publicado en el registro oficial edición especial Nro. 387 del 4 de noviembre del 2015. "+
		        	"Para continuar con el proceso de obtención del permiso ambiental, usted debe realizar el pago de Remoción de Cobertura Vegetal Nativa "+ 
		        	"por el valor de " + valorAPagar + " USD,  por medio de la opción de Pago en Línea del Ministerio del Ambiente, Agua y Transición Ecológica.";
			
			pagoRcoaBean.setValidaNutKushki(true);
			String codigoTramite=tramite;
			GenerarNUTController nutController = JsfUtil.getBean(GenerarNUTController.class);
			pagoRcoaBean.setDocumentosNUT(nutController.obtenerDocumentosNut(tramite));
			List<PagoKushkiJson> PagoKushkiJsonAux = nutController.listPagosKushki(tramite);
			if (PagoKushkiJsonAux != null)
			{
				pagoRcoaBean.setPagoKushkiJson(PagoKushkiJsonAux);
			} 
			if (pagoRcoaBean.getPagoKushkiJson() != null)
			{
				pagoRcoaBean.setValidaNutKushki(false);
			} 			
			pagoRcoaBean.setMontoTotalProyecto(montoTotal);
			pagoRcoaBean.setEsEnteAcreditado(false);
			pagoRcoaBean.setValorAPagar(valorAPagar);
			pagoRcoaBean.setTramite(tramite);
			pagoRcoaBean.setTipoProyecto("CERTIFICADO");
			pagoRcoaBean.setIdentificadorMotivo(identificadorMotivo);
			pagoRcoaBean.setCiudad(data_localizacion.getUbicacionesGeografica().getNombre().toString());
			pagoRcoaBean.setProvincia(data_localizacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre().toString());
			pagoRcoaBean.setPais(data_localizacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre().toString());
			pagoRcoaBean.setDireccion(proyecto.getDireccionProyecto());
			pagoRcoaBean.setIdproy(proyecto.getId());
			
			pagoRcoaBean.setInstitucionesFinancieras(institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC());
			
			
			if(codigoTramite==null && pagoRcoaBean.getMontoTotalProyecto() <= 0){
				pagoRcoaBean.setGenerarNUT(false);
			}
			
			if(pagoRcoaBean.getInstitucionesFinancieras() != null && !pagoRcoaBean.getInstitucionesFinancieras().isEmpty()){
				pagoRcoaBean.getTransaccionFinanciera().setInstitucionFinanciera(pagoRcoaBean.getInstitucionesFinancieras().get(0));
			}
			
			if (this.identificadorMotivo == null){
					pagoRcoaBean.setTransaccionesFinancieras(transaccionFinancieraFacade
							.cargarTransaccionesRcoa(proyectosBean.getProyectoRcoa()
									.getId(), bandejaTareasBean.getTarea()
									.getProcessInstanceId(), bandejaTareasBean
									.getTarea().getTaskId()));
			}else{
				pagoRcoaBean.setTransaccionesFinancieras(transaccionFinancieraFacade.cargarTransacciones(this.identificadorMotivo));
			}
			
			if(pagoRcoaBean.getTransaccionesFinancieras().isEmpty())
				persist = true;
			
			if(pagoRcoaBean.getTransaccionesFinancieras().size()>0){
				pagoRcoaBean.setCumpleMontoProyecto(nutController.cumpleMonto());
			}
			nutController.cerrarMensaje();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cargarUbicacionProyecto() {
		ubicacionesSeleccionadas = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyecto);
		data_localizacion = ubicacionesSeleccionadas.get(0);
	}
	
	public String completarTarea() {
		this.montoTotal=pagoRcoaBean.getMontoTotalProyecto();
		DecimalFormat decimalValorTramite= new DecimalFormat(".##");
        String x="";
        x=decimalValorTramite.format(this.montoTotal).replace(",",".");
        this.montoTotal=Double.valueOf(x);
		
		if(pagoRcoaBean.getMontoTotalProyecto()>0){
			List<TransaccionFinanciera>listTransProyecto= new ArrayList<TransaccionFinanciera>();
			for (TransaccionFinanciera transaccionFinanciera : pagoRcoaBean.getTransaccionesFinancieras()) {
				listTransProyecto.add(transaccionFinanciera);
			}
			if(listTransProyecto.size()==0 || !pagoRcoaBean.isCumpleMontoProyecto()){
				JsfUtil.addMessageError("Debe registrar un pago para el proyecto antes de continuar.");
				return null;
			}
		}
		if (pagoRcoaBean.getTransaccionesFinancieras().size() == 0) {
			JsfUtil.addMessageError("Debe registrar un pago antes de continuar.");
			return null;
		}

		if (!pagoRcoaBean.isCumpleMontoProyecto()) {
			JsfUtil.addMessageError("El monto de la transacción utilizada es insuficiente para completar el pago de tasas.");
			return null;
		}

		double montoTotal = monto();
		if (montoTotal > this.montoTotal) {
			RequestContext.getCurrentInstance().execute("PF('dlg1V2').show();");
		} else if (montoTotal == this.montoTotal) {
			executeBusinessLogic();
		}
		return null;
	}
	
	public double monto() {
		double montoTotal = 0;
		for (TransaccionFinanciera transa : pagoRcoaBean.getTransaccionesFinancieras()) {
			montoTotal += transa.getMontoTransaccion();
		}
		return montoTotal;
	}
	
	public void executeBusinessLogic() {
		if(persist) {
		String motivo = this.identificadorMotivo == null ?
				((proyectosBean.getProyecto() == null || proyectosBean.getProyecto().getCodigo() == null)  ? proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() : proyectosBean.getProyecto().getCodigo())
			: this.identificadorMotivo;
		List<TransaccionFinanciera> transaccionesFinancierasProyecto= new ArrayList<TransaccionFinanciera>();
		for (TransaccionFinanciera transaccionFinanciera : pagoRcoaBean.getTransaccionesFinancieras()) {
			transaccionesFinancierasProyecto.add(transaccionFinanciera);
		}
		boolean pagoSatisfactorio=false;
		if(proyectosBean.getProyecto()!=null && proyectosBean.getProyecto().getCodigo() != null && !proyectosBean.getProyecto().isAreaResponsableEnteAcreditado() && transaccionesFinancierasProyecto.size()>0){
			if(pagoRcoaBean.getMontoTotalProyecto()!=montoTotal){
				pagoRcoaBean.setMontoTotalProyecto(montoTotal);
			}
		}
		if(transaccionesFinancierasProyecto.size()>0){
			pagoSatisfactorio=transaccionFinancieraFacade.realizarPago(pagoRcoaBean.getMontoTotalProyecto(), transaccionesFinancierasProyecto,motivo);
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
		
		if (pagoSatisfactorio) {
            transaccionFinancieraFacade.guardarTransacciones(pagoRcoaBean.getTransaccionesFinancieras(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getTaskName(),
                    bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getProcessId(),
                    bandejaTareasBean.getTarea().getProcessName());
            
			JsfUtil.addMessageInfo("Pago satisfactorio.");
			if(finalizar())
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} else {
			JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
		}
		
		}else{
			JsfUtil.addMessageInfo("Pago satisfactorio.");
			if(finalizar())
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}
	}
	
	private boolean finalizar(){
		try {
			Map<String, Object> params=new HashMap<>();
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void validarTareaBpm() {
//        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/categoria2/v2/pagoServicios.jsf");
	}
	
	public void continuar(){
		try{
			ProyectosConPagoSinNut objProyectoNoNut = proyectosConPagoSinNutFacade.buscarNUTPorProyectoPorUsuario(bandejaTareasBean.getProcessId(), JsfUtil.getLoggedUser());
			if(objProyectoNoNut != null && objProyectoNoNut.getId() != null)
				continuar_();
			else
				JsfUtil.redirectTo("/pages/rcoa/certificadoAmbiental/realizarPagoTasaCA.jsf");
		}catch(Exception e){
			
		}
	}
	
	public void continuar_(){
		Map<String, Float> costoTotalProyecto= new ConcurrentHashMap<String, Float>();
		costoTotalProyecto.put("valorAPagar", (float) valorAPagar);
		
		 //Ejecutar componente de pago 
        JsfUtil.getBean(PagoServiciosBean.class).initFunctionWithProjectOrMotive("/bandeja/bandejaTareas.jsf", new CompleteOperation() {
            @Override
            public Object endOperation(Object object) {
                try {
                    taskBeanFacade.approveTask(loginBean.getNombreUsuario(),bandejaTareasBean.getTarea().getTaskId(),
                            bandejaTareasBean.getTarea().getProcessInstanceId(), null, loginBean.getPassword(),
                            Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
                } catch (Exception exp) {
                    JsfUtil.addMessageError("Ocurrió un error inesperado, por favor intente más adelante. Si el error persiste contacte a Mesa de ayuda.");
                    LOG.error("Error al aprobar la tarea de pago del proceso de Certificado Ambiental.", exp);
                }
                return null;
            }
        }, costoTotalProyecto, null,null,null,false);
	}
	
	public void cancelar(){
		
		JsfUtil.actionNavigateToBandeja();
	}

}
