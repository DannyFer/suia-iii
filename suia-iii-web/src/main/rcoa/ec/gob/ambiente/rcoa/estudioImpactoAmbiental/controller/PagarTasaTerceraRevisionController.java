package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.enums.CodigoTasa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.recaudaciones.controllers.GenerarNUTController;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.TarifasFacade;
import ec.gob.ambiente.suia.recaudaciones.model.Cuentas;
import ec.gob.ambiente.suia.recaudaciones.model.EstadosNut;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.recaudaciones.model.SolicitudUsuario;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;
import ec.gob.ambiente.suia.recaudaciones.model.TarifasNUT;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;


@ManagedBean
@ViewScoped
public class PagarTasaTerceraRevisionController {
	
	private static final Logger LOG = Logger.getLogger(PagarTasaTerceraRevisionController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;
	@EJB
	private DocumentosImpactoEstudioFacade documentosImpactoEstudioFacade;
	@EJB	
	private TarifasFacade tarifasFacade;	
	@EJB
	private CrudServiceBean crudServiceBean;	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	
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
	
	private String tramite;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private InformacionProyectoEia informacionEstudio;
	
	@Getter
	@Setter
	private double valorAPagar;
	
	@Getter
	@Setter
	public Boolean generarNUT;
	
	@Getter
	@Setter
	public DocumentoEstudioImpacto documentoEiaNut;
	
	@Getter
    @Setter
	private List<DocumentoEstudioImpacto> documentosNUT;	
	
	@Getter
	@Setter
	private List<InstitucionFinanciera> institucionesFinancieras;
	
	@Getter
	@Setter
	private List<TransaccionFinanciera> transaccionesFinancieras;	
	
	@Getter
	@Setter
	private TransaccionFinanciera transaccionFinanciera;
	
	@Getter
	@Setter
	public Boolean cumpleMontoProyecto = false;
	
	@Getter
	@Setter
	private boolean eligioEnte;	
	
	@Getter
	@Setter
	private List<InstitucionFinanciera> entesAcreditados;
	
	public Boolean cumpleMonto = false;
	
	@Getter
	@Setter
	public double montoTotal;
	
	@Getter
	@Setter
	public double montoTotalProyecto;
		
	@Getter
	@Setter
	private Boolean persist = false;
	
	@Getter
	@Setter
	private double valorTotalAPagar;
	
	@Getter
	@Setter
	private boolean cumpleValorTotal; 
	
	
	@PostConstruct
	public void init(){
		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);		
			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			informacionEstudio = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);			
			
			if(informacionEstudio != null){
				valorAPagar = informacionEstudio.getValorAPagar();
				montoTotalProyecto = informacionEstudio.getValorContrato();
				valorTotalAPagar = valorAPagar;
			}
			
			generarNUT=true;
			
			transaccionFinanciera = new TransaccionFinanciera();
			transaccionesFinancieras = new ArrayList<>();
			
			if (proyectosBean.getProyectoRcoa().getAreaResponsable().getTipoEnteAcreditado() != null) {
				if (proyectosBean.getProyectoRcoa().getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
					institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC();
				} else {
					institucionesFinancieras = institucionFinancieraFacade.obtenerPorNombre(proyecto.getAreaResponsable().getAreaName());
					if(institucionesFinancieras==null)
						JsfUtil.addMessageError("No se encontró institución financiara para: "+ proyecto.getAreaResponsable().getAreaName());
				}
			} else {
				institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC();
			}
			
			if(institucionesFinancieras != null && institucionesFinancieras.size() > 0)
				transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));				
									
			transaccionesFinancieras = transaccionFinancieraFacade
					.cargarTransaccionesRcoa(proyectosBean.getProyectoRcoa()
							.getId(), bandejaTareasBean.getTarea()
							.getProcessInstanceId(), bandejaTareasBean
							.getTarea().getTaskId());
			
			
			documentosNUT = new ArrayList<>();
			List<NumeroUnicoTransaccional> listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(tramite);
			if(listNUTXTramite!=null && listNUTXTramite.size()>0){
				for (NumeroUnicoTransaccional nut : listNUTXTramite) {
					List<DocumentoEstudioImpacto> comprobantes = documentosImpactoEstudioFacade
							.documentoXTablaIdXIdDocLista(nut.getSolicitudUsuario().getId(),
									"NUT RECAUDACIONES", TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
					
					if (comprobantes.size() > 0) {
						documentosNUT.addAll(comprobantes);
					} 
				}
			}
			
			if(transaccionesFinancieras.isEmpty())
				persist = true;
			
			if(transaccionesFinancieras.size()>0){
				cumpleMonto = cumpleMonto();
			}
			
			transaccionesFinancieras = transaccionFinancieraFacade
					.cargarTransaccionesRcoa(
							proyecto.getId(),
							bandejaTareasBean.getTarea().getProcessInstanceId(),
							bandejaTareasBean.getTarea().getTaskId());
			
			if (transaccionesFinancieras != null && !transaccionesFinancieras.isEmpty())
				cumpleMonto();

			if(transaccionesFinancieras.isEmpty())
				persist = true;
			
			if(transaccionesFinancieras.size()>0){
				cumpleMonto = cumpleMonto();
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean cumpleMonto() {
        double montoTotal = 0;
        for (TransaccionFinanciera transa : transaccionesFinancieras) {
            montoTotal += transa.getMontoTransaccion();        	
        }
       
        DecimalFormat decimalValorTramite= new DecimalFormat(".##");
        String x="";
       
		x = decimalValorTramite.format(this.valorAPagar).replace(",", ".");

		this.valorAPagar = Double.valueOf(x);
		if (montoTotal >= this.valorAPagar) {
			cumpleValorTotal = true;
			return true;
		}
		cumpleValorTotal = false;
		return false;
    }
	
		
	public void generarNUT() throws Exception{	
		
		List<NumeroUnicoTransaccional> listNUTXTramite=new ArrayList<NumeroUnicoTransaccional>();
		NumeroUnicoTransaccional numeroUnicoTransaccional;
		TarifasNUT tarifasNUT= new TarifasNUT();
		SolicitudUsuario solicitudUsuario= new SolicitudUsuario();
		solicitudUsuario.setUsuario(loginBean.getUsuario());
		
		String codigoTramite="";
		
		codigoTramite=tramite;
		 
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
		
		Tarifas tarifa = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion());
		if(tarifa == null) {
			JsfUtil.addMessageError("Ocurrió un error al generar el NUT. Por favor comunicarse con mesa de ayuda.");
			return;
		}
		
		solicitudUsuario.setSolicitudDescripcion("Pagos por el trámite: "+codigoTramite);
		solicitudUsuario.setSolicitudCodigo(JsfUtil.getBean(GenerarNUTController.class).generarCodigoSolicitud());
		
		crudServiceBean.saveOrUpdate(solicitudUsuario);
		Integer numeroDocumento=1;
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
		tarifasNUT.setTarifas(tarifa);

		tarifasNUT.setCantidad(1);
		tarifasNUT.setValorUnitario(montoTotalProyecto);
		crudServiceBean.saveOrUpdate(tarifasNUT);
		JsfUtil.getBean(GenerarNUTController.class).setSolicitudUsuario(solicitudUsuario);
		byte[] contenidoDocumento = JsfUtil.getBean(GenerarNUTController.class).generarDocumentoNutRcoa(numeroUnicoTransaccional,solicitudUsuario, numeroDocumento);

		DocumentoEstudioImpacto documentoPago = new DocumentoEstudioImpacto();
		documentoPago.setContenidoDocumento(contenidoDocumento);
		documentoPago.setMime("application/pdf");
		documentoPago.setIdTable(solicitudUsuario.getId());
		documentoPago.setNombreTabla("NUT RECAUDACIONES");
		documentoPago.setNombre("ComprobantePago" + numeroDocumento + ".pdf");
		documentoPago.setExtesion(".pdf");
		documentoPago = documentosImpactoEstudioFacade.guardarDocumentoAlfrescoSinProyecto(numeroUnicoTransaccional.getNutCodigoProyecto()!=null ? numeroUnicoTransaccional.getNutCodigoProyecto() :solicitudUsuario.getSolicitudCodigo(), "RECAUDACIONES", null, documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
			
		numeroDocumento++;
		
		List<String>listaArchivos= new ArrayList<String>();
		listaArchivos=JsfUtil.getBean(GenerarNUTController.class).getListPathArchivos();
		
		JsfUtil.getBean(GenerarNUTController.class).enviarNotificacionPago(solicitudUsuario.getUsuario(),codigoTramite,listaArchivos);
				
		JsfUtil.addMessageInfo("Se ha enviado un correo electrónico con los comprobantes para realizar el pago.");
	}
	
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
	
	public StreamedContent descargar(DocumentoEstudioImpacto documento) {
		DefaultStreamedContent content = null;
		try {
			if (documento.getAlfrescoId() != null) {
				byte[] documentoContent = documentosImpactoEstudioFacade.descargar(documento.getAlfrescoId());

				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void guardarTransaccion() {
		
		if (transaccionFinanciera.getInstitucionFinanciera() != null && transaccionFinanciera.getNumeroTransaccion() != "") {
			try {
				
				String codigoTramite = "";
				if (proyectosBean.getProyectoRcoa() != null) {
					codigoTramite = proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental();
				} else {
					codigoTramite = bandejaTareasBean.getTarea().getProcedure();
				}

				List<NumeroUnicoTransaccional> listaNutsTramite = numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoTramite);
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
				}
				//fin recaudaciones
				
				if (existeTransaccion(transaccionFinanciera)) {
					JsfUtil.addMessageInfo("El número de comprobante: " + transaccionFinanciera.getNumeroTransaccion()
							+ " (" + transaccionFinanciera.getInstitucionFinanciera().getNombreInstitucion()
							+ ") ya fue registrado por usted. Ingrese otro distinto.");
					reiniciarTransaccion();
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
						reiniciarTransaccion();
						return;
					} else {
						transaccionFinanciera.setMontoTransaccion(monto);
						transaccionFinanciera.setTipoPagoProyecto("Proyecto");
						transaccionFinanciera.setProyectoRcoa(proyectosBean.getProyectoRcoa());
						transaccionFinanciera.setIdentificadorMotivo(null);
						
						transaccionesFinancieras.add(transaccionFinanciera);
						cumpleValorTotal = cumpleMonto();
						reiniciarTransaccion();

						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
				return;
			}
		} else {
			JsfUtil.addMessageWarning("Debe ingresar datos correctos para la transacción");
		}
	}
	
	private void reiniciarTransaccion() {
		transaccionFinanciera = new TransaccionFinanciera();
		if(institucionesFinancieras != null && institucionesFinancieras.size() > 0)
			transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
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
	
	public void eliminarTransacion(TransaccionFinanciera transaccion) throws Exception {
		transaccionesFinancieras.remove(transaccion);
		cumpleValorTotal = cumpleMonto();
	}
	
	public void completarTarea(){
		try {
			
			if (transaccionesFinancieras.size() == 0) {
				JsfUtil.addMessageError("Debe registrar un pago antes de continuar.");
				return;
			}

			if (!cumpleValorTotal) {
				JsfUtil.addMessageError("El monto de la transacción utilizada es insuficiente para completar el pago de tasas.");
				return;
			}
			
			if (realizarPago()) {
	            transaccionFinancieraFacade.guardarTransacciones(transaccionesFinancieras,
	                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getTaskName(),
	                    bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getProcessId(),
	                    bandejaTareasBean.getTarea().getProcessName());
	            
	            Map<String, Object> parametros = new HashMap<>();
				parametros.put("pagoTerceraRevision", true);
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
				
			} else {
				JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
			}
		} catch (Exception e) {					
			e.printStackTrace();
		}
	}
	
	private Boolean realizarPago() {
		Boolean pagoSatisfactorio = false;

		pagoSatisfactorio = transaccionFinancieraFacade.realizarPago(
				valorTotalAPagar, transaccionesFinancieras, proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());

		for (TransaccionFinanciera transaccionFinanciera : transaccionesFinancieras) {
			try {
				NumeroUnicoTransaccional nut = new NumeroUnicoTransaccional();
				nut = numeroUnicoTransaccionalFacade
						.buscarNUTPorNumeroTramite(transaccionFinanciera
								.getNumeroTransaccion());
				if (nut != null) {
					nut.setNutUsado(true);
					numeroUnicoTransaccionalFacade.guardarNUT(nut);
				}
			} catch (ec.gob.ambiente.suia.exceptions.ServiceException e) {
				e.printStackTrace();
			}
		}
		
		return pagoSatisfactorio;
	}

}
