package ec.gob.ambiente.rcoa.sustancias.quimicas.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosSustanciasQuimicasRcoaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
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
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class PagoSustanciasQuimicasServiciosBean implements Serializable {
	/**
	 * 
	 */
	
	private static final Logger LOG = Logger.getLogger(PagoSustanciasQuimicasServiciosBean.class);
	
	private static final long serialVersionUID = -3349963054404332560L;
	@Getter
	@Setter
	public double montoTotal;	
	
	@Getter
	@Setter
	public Integer adicional;

	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@EJB
	private DocumentosSustanciasQuimicasRcoaFacade documentosSustanciasQuimicasRcoaFacade;
	
	@Getter
    @Setter
	private List<DocumentosSustanciasQuimicasRcoa> documentosSustanciascoasNUT;
	
	
	@Getter
	@Setter
	private List<InstitucionFinanciera> institucionesFinancieras;
	

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
	private TarifasFacade tarifasFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;
	
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	@Getter
	@Setter
	public double montoTotalProyecto;
	
	@Getter
	@Setter
	public Boolean cumpleMontoProyecto = false;
	
	@Getter
	@Setter
	private boolean eligioEnte;
	
	@Getter
	@Setter
	private List<InstitucionFinanciera> entesAcreditados;
	
	@Getter
	@Setter
	public Boolean cumpleMonto = false;
	
	private String identificadorMotivo;
	
	@Getter
	@Setter
	public Boolean cumpleMontoCobertura = false;
	
	@Getter
	@Setter
	public double montoTotalCoberturaVegetal;
	
	@Getter
	@Setter
	public Boolean generarNUT;	
	
	@Getter
	@Setter
	private String mensaje;
	
	
	@PostConstruct
	public void init() {
		
		//Integer codigoProy=3;
		this.montoTotal = 180;
		
			mensaje = "El pago correspondiente a la tasa por concepto de Registro deSustancias Químicas, "
					+ " con  un  valor  de  180  USD,  los  cuales  deben  serdepositados  en  BAN Ecuador,"
					+ "  a  la  cuenta  corriente  No.3001174975  sublínea  No.370102  "
					+ " a  nombre  del MINISTERIO DEL AMBIENTE";					
			montoTotalProyecto=montoTotal;
		
		// TODO Auto-generated method stub
		cumpleMontoProyecto=false;
		transaccionFinanciera = new TransaccionFinanciera();
		transaccionFinancieraCobertura = new TransaccionFinanciera();
		String codigoTramite="SQ-MAAE-2020-0006";
		documentosSustanciascoasNUT = new ArrayList<>();
		List<NumeroUnicoTransaccional> listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoTramite);
		if(listNUTXTramite!=null && listNUTXTramite.size()>0){
			for (NumeroUnicoTransaccional nut : listNUTXTramite) {
				List<DocumentosSustanciasQuimicasRcoa> comprobantes =  documentosSustanciasQuimicasRcoaFacade
						.documentoXTablaIdXIdDoc(nut.getSolicitudUsuario().getId(), 
								"NUT RECAUDACIONES", TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
				
				if (comprobantes.size() > 0) {
					documentosSustanciascoasNUT.addAll(comprobantes);
				} 
			}
		}
		if(codigoTramite!=null && montoTotalProyecto>0){
			generarNUT=true;
		}
		
		institucionesFinancieras = institucionFinancieraFacade
				.obtenerListaInstitucionesFinancierasPC();
		// para cuando se vincule con el proceso
//		if (this.identificadorMotivo == null){
//						
//				transaccionesFinancieras = transaccionFinancieraFacade
//						.cargarTransaccionesRcoa(proyectosBean.getProyectoRcoa()
//								.getId(), bandejaTareasBean.getTarea()
//								.getProcessInstanceId(), bandejaTareasBean
//								.getTarea().getTaskId());			
//		}else{
//			transaccionesFinancieras = transaccionFinancieraFacade
//					.cargarTransacciones(this.identificadorMotivo);
//		}
		
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
	
public void generarNUT() throws Exception{	
		
		List<NumeroUnicoTransaccional> listNUTXTramite=new ArrayList<NumeroUnicoTransaccional>();
		NumeroUnicoTransaccional numeroUnicoTransaccional;
		TarifasNUT tarifasNUT= new TarifasNUT();
		SolicitudUsuario solicitudUsuario= new SolicitudUsuario();
		solicitudUsuario.setUsuario(loginBean.getUsuario());		
		DecimalFormat formato1 = new DecimalFormat("#.00");		
		String codigoTramite="";		
		codigoTramite="SQ-MAAE-2020-0006";		 
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
		
		Tarifas tarifa = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.REGISTRO_SUSTANCIAS_QUIMICAS.getDescripcion());
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
			byte[] contenidoDocumento = JsfUtil.getBean(GenerarNUTController.class).generarDocumentoNutRcoa(numeroUnicoTransaccional, solicitudUsuario, numeroDocumento);
			
			DocumentosSustanciasQuimicasRcoa documentoPago = new DocumentosSustanciasQuimicasRcoa();
			documentoPago.setContenidoDocumento(contenidoDocumento);
    		documentoPago.setMime("application/pdf");
    		documentoPago.setIdTable(solicitudUsuario.getId());
			documentoPago.setNombreTabla("NUT RECAUDACIONES");
			documentoPago.setNombre("ComprobantePago"+ numeroDocumento +".pdf");
			documentoPago.setExtesion(".pdf");
			documentosSustanciasQuimicasRcoaFacade.guardarDocumentoAlfrescoSinProyecto(numeroUnicoTransaccional.getNutCodigoProyecto()!=null ? numeroUnicoTransaccional.getNutCodigoProyecto() :solicitudUsuario.getSolicitudCodigo(), "RECAUDACIONES", null, documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS,null);
			
			numeroDocumento++;		
		List<String>listaArchivos= new ArrayList<String>();
		listaArchivos=JsfUtil.getBean(GenerarNUTController.class).getListPathArchivos();
		JsfUtil.getBean(GenerarNUTController.class).enviarNotificacionPago(solicitudUsuario.getUsuario(), codigoTramite, listaArchivos);
		
		JsfUtil.addMessageInfo("Se ha enviado un correo electrónico con los comprobantes para realizar el pago.");
	}

	public StreamedContent descargar(DocumentosSustanciasQuimicasRcoa documento) {
		DefaultStreamedContent content = null;
		try {
			if (documento.getIdAlfresco() != null) {
				byte[] documentoContent = documentosSustanciasQuimicasRcoaFacade.descargar(documento
						.getIdAlfresco());

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
				String codigoTramite = "";
//				if (proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getCodigo() != null) {
//					codigoTramite = proyectosBean.getProyecto().getCodigo();
//				} else {
//					codigoTramite = bandejaTareasBean.getTarea().getProcedure();
//				}
				codigoTramite="SQ-MAAE-2020-0006";

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


}
