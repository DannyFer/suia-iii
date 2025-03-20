package ec.gob.ambiente.rcoa.pagos;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.enums.CodigoTasa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.recaudaciones.controllers.GenerarNUTController;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.SolicitudTramiteFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.TarifasFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.TarifasNUTFacade;
import ec.gob.ambiente.suia.recaudaciones.model.Cuentas;
import ec.gob.ambiente.suia.recaudaciones.model.EstadosNut;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.recaudaciones.model.SolicitudUsuario;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;
import ec.gob.ambiente.suia.recaudaciones.model.TarifasNUT;
import ec.gob.ambiente.suia.utils.JsfUtil;


@ManagedBean
@ViewScoped
public class PagosTasaController {

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
	
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	
	@EJB
	private TarifasFacade tarifasFacade;
	
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;
	
	@EJB
	private SolicitudTramiteFacade solicitudTramiteFacade;
	
	@EJB
	private TarifasNUTFacade tarifasNUTFacade;
	
	@EJB
	private DocumentosCoaFacade documentosFacade;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
    
    @ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;

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
	private String numeroTransaccion;
	
	@Getter
    @Setter
	private List<DocumentosCOA> documentosNUT;
	
	@Getter
    @Setter
	private Tarifas tarifaUsoSitema;
	
	@Getter
	@Setter
	public Boolean generarNUT, cumpleValorTotal;
	
	@Getter
	@Setter
	public double valorTotalUsoSitema;
	
	@PostConstruct
	public void initData() throws Exception {
		
		cumpleValorTotal = false;

		transaccionFinanciera = new TransaccionFinanciera();
		transaccionesFinancieras = new ArrayList<>();
		institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC();
		if(institucionesFinancieras != null && institucionesFinancieras.size() > 0)
			transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
		
		transaccionesFinancieras = transaccionFinancieraFacade.cargarTransaccionesRcoa(proyectosBean.getProyectoRcoa().getId(),
						bandejaTareasBean.getTarea().getProcessInstanceId(),
						bandejaTareasBean.getTarea().getTaskId());
		if(transaccionesFinancieras != null && !transaccionesFinancieras.isEmpty())
			cumpleMonto();
		
		generarNUT = true;
		
		if(generarNUT) {
			//buscar tasa para pago x uso de sistema
			tarifaUsoSitema = new Tarifas();
			tarifaUsoSitema = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.USO_SISTEMA.getDescripcion());
			if(tarifaUsoSitema == null){
				JsfUtil.addMessageError("Ocurrió un error al cargar la información. Por favor comunicarse con mesa de ayuda.");
				cumpleValorTotal = true;
				return;
			}
			valorTotalUsoSitema = tarifaUsoSitema.getTasasValor();
			
			//buscar si hay nuts activos para el tramite y buscar los documentos
			documentosNUT = new ArrayList<>();
			List<NumeroUnicoTransaccional> listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());
			if(listNUTXTramite!=null && listNUTXTramite.size()>0){
				for (NumeroUnicoTransaccional nut : listNUTXTramite) {
					List<DocumentosCOA> comprobantes = documentosFacade
							.documentoXTablaIdXIdDoc(nut.getSolicitudUsuario().getId(),
									TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS, "NUT RECAUDACIONES");
					
					if (comprobantes.size() > 0) {
						documentosNUT.addAll(comprobantes);
					} 
				}
			}
		} else 
			valorTotalUsoSitema = 50;

		System.out.println("PagosTasaController.initData()");
	}
	
	public void generarNut() {
		try{
			String codigoTramite=proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental();
			
			List<NumeroUnicoTransaccional> listNUTXTramite=new ArrayList<NumeroUnicoTransaccional>();
			
			listNUTXTramite = numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoTramite);
			if (listNUTXTramite != null && listNUTXTramite.size() > 0) {
				for (NumeroUnicoTransaccional nut : listNUTXTramite) {
					if (nut.getEstadosNut().getId() == 5) {
						nut.setNutFechaActivacion(new Date());
						nut.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
						nut.setEstadosNut(new EstadosNut(2));
						numeroUnicoTransaccionalFacade.guardarNUT(nut);
						
						JsfUtil.addMessageWarning("Su trámite está caducado, se activo nuevamente por 3 días, por favor revisar.");
					}
				}
				JsfUtil.addMessageWarning("Usted ya tiene generado un Número Único de Trámite, por favor descargue en documentos del trámite.");
				return;
			}
			
			SolicitudUsuario solicitudUsuario= new SolicitudUsuario();
			solicitudUsuario.setUsuario(loginBean.getUsuario());
			solicitudUsuario.setSolicitudDescripcion("Pagos por el trámite: "+codigoTramite);
			solicitudUsuario.setSolicitudCodigo(JsfUtil.getBean(GenerarNUTController.class).generarCodigoSolicitud());
			solicitudTramiteFacade.guardarSolicitudUsuario(solicitudUsuario);
			
			Integer numeroDocumento=1;
			
			NumeroUnicoTransaccional numeroUnicoTransaccional;
			numeroUnicoTransaccional = new NumeroUnicoTransaccional();
			numeroUnicoTransaccional.setNutCodigo(secuenciasFacade.getNextValueDedicateSequence("NUT_CODIGO", 10));
			numeroUnicoTransaccional.setNutFechaActivacion(new Date());
			numeroUnicoTransaccional.setEstadosNut(new EstadosNut(2));
			numeroUnicoTransaccional.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
			numeroUnicoTransaccional.setSolicitudUsuario(solicitudUsuario);
			numeroUnicoTransaccional.setCuentas(new Cuentas(1));
			numeroUnicoTransaccional.setNutValor(tarifaUsoSitema.getTasasValor());
			numeroUnicoTransaccional.setNutCodigoProyecto(codigoTramite);
			numeroUnicoTransaccionalFacade.guardarNUT(numeroUnicoTransaccional);
			
			TarifasNUT tarifasNUT= new TarifasNUT();
			tarifasNUT.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
			tarifasNUT.setTarifas(tarifaUsoSitema);
			tarifasNUT.setCantidad(1);
			tarifasNUT.setValorUnitario(tarifaUsoSitema.getTasasValor());
			tarifasNUTFacade.guardarTarifasNUT(tarifasNUT);
			
			JsfUtil.getBean(GenerarNUTController.class).setSolicitudUsuario(solicitudUsuario);
			byte[] contenidoDocumento = JsfUtil.getBean(GenerarNUTController.class).generarDocumentoNutRcoa(numeroUnicoTransaccional, solicitudUsuario, numeroDocumento);
			
			DocumentosCOA documentoPago = new DocumentosCOA(); 
			documentoPago.setContenidoDocumento(contenidoDocumento);
			documentoPago.setNombreDocumento("ComprobantePago"+ numeroDocumento +".pdf");
			documentoPago.setExtencionDocumento(".pdf");
			documentoPago.setTipo("application/pdf");
			documentoPago.setNombreTabla("NUT RECAUDACIONES");
			documentoPago.setIdTabla(solicitudUsuario.getId());
			documentoPago.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
			
			documentoPago = documentosFacade.guardarDocumentoAlfresco(
					codigoTramite,
					"RECAUDACIONES", bandejaTareasBean.getTarea().getProcessInstanceId(),
					documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
			
			documentosNUT = new ArrayList<>();
			documentosNUT.add(documentoPago);
			
			List<String>listaArchivos= new ArrayList<String>();
			listaArchivos=JsfUtil.getBean(GenerarNUTController.class).getListPathArchivos();
			JsfUtil.getBean(GenerarNUTController.class).enviarNotificacionPago(solicitudUsuario.getUsuario(),codigoTramite,listaArchivos);
			JsfUtil.addMessageInfo("Se ha enviado un correo electrónico con los comprobantes para realizar el pago.");
			
		} catch (Exception e) {					
			e.printStackTrace();
		}
	}
	
	public StreamedContent descargar(DocumentosCOA documento) {
		DefaultStreamedContent content = null;
		try {
			if (documento.getIdAlfresco() != null) {
				byte[] documentoContent = documentosFacade.descargar(documento.getIdAlfresco());

				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent));
				content.setName(documento.getNombreDocumento());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void guardarTransaccion(Integer pagoProyecto) {
		
		if (transaccionFinanciera.getInstitucionFinanciera() != null
				&& transaccionFinanciera.getNumeroTransaccion() != "") {
			try {
				//Mariela recaudaciones
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

	private boolean cumpleMonto() {
        double montoTotal = 0;
        for (TransaccionFinanciera transa : transaccionesFinancieras) {
        	if(transa.getTipoPagoProyecto() == null || transa.getTipoPagoProyecto().equals("Proyecto")){
            	montoTotal += transa.getMontoTransaccion();
        	}
        }
       
        DecimalFormat decimalValorTramite= new DecimalFormat(".##");
        String x="";
       
		x = decimalValorTramite.format(this.valorTotalUsoSitema).replace(",", ".");

		this.valorTotalUsoSitema = Double.valueOf(x);
		if (montoTotal >= this.valorTotalUsoSitema) {
			cumpleValorTotal = true;
			return true;
		}
		cumpleValorTotal = false;
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
				valorTotalUsoSitema, transaccionesFinancieras, proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());

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
