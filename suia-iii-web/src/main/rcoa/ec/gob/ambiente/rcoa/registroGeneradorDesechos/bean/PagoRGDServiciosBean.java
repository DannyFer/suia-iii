package ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
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
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.CatalogoRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CatalogoRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.Usuario;
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
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@SessionScoped
public class PagoRGDServiciosBean implements Serializable {
	/**
	 * 
	 */
	
	private static final Logger LOG = Logger.getLogger(PagoRGDServiciosBean.class);
	
	private static final long serialVersionUID = -3349963054404332560L;
	
	@EJB
	private DocumentosRgdRcoaFacade documentosRgdRcofacade;	
	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;	
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;	
	@EJB
	private CrudServiceBean crudServiceBean;	
	@EJB
	private SecuenciasFacade secuenciasFacade;	
	@EJB
	private TarifasFacade tarifasFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private AreaFacade areaFacade;	
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private CatalogoRgdRcoaFacade catalogoRgdRcoaFacade;
	
	@Getter
	@Setter
	public double montoTotal;
	
	@Getter
	@Setter
	public double montoTotaladicional;
	
	@Getter
	@Setter
	public double montounico;
	
	@Getter
	@Setter
	public double montoAdicional;	
	
	@Getter
	@Setter
	public Integer adicional;

	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;	
	
	@Getter
    @Setter
	private List<DocumentosRgdRcoa> documentosRgdRcoasNUT;	
	
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

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;	
	
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
	public Boolean valorAdicional=false;	
	
	@Getter
	@Setter
	private String mensaje;
	
	@Getter
	private String tramite;

	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private String mensajeFinalizar;
	
	@Getter
	public Boolean datoFormaspago=true;
	
	@Getter
    @Setter
    private Boolean condiciones = false;
	
	@Getter
	@Setter
	private Boolean persist = false;
		
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private RegistroGeneradorDesechosRcoa registroGenerador;
	
	
	@PostConstruct
	public void init() throws JbpmException {
		
		variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
		tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);	
				
		proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite); 
		
		//Se debe realizar código para cuando sean varios proyectos
		Integer codigoProy=proyecto.getId();
		
		CatalogoRgdRcoa catalogoPago = catalogoRgdRcoaFacade.findByCodigo("rgd_valor_pago_registro");
		Double pago = Double.valueOf(catalogoPago.getDescripcion());
//		this.montoTotal = 180; // valor que se paga
		this.montoTotal = pago;
		this.montoAdicional=50;	
		this.montounico=90; //180
		
		List<RegistroGeneradorDesechosProyectosRcoa> registroGeneradorDesechosProyectosRcoas= registroGeneradorDesechosProyectosRcoaFacade.asociados(codigoProy);
		if (registroGeneradorDesechosProyectosRcoas.size()>1){
			mensaje = "El pago corresponde a la tasa por concepto de Registro Generador de Residuos o "
					+ " Desechos Peligrosos y/o Especiales, con un valor de 180 USD que incluye a una "
					+ "actividad; y adicionalmente, 50 USD por cada una de las actividades agrupadas "
					+ "bajo un mismo Registro Generador. Estos valores serán depositados en la cuenta "
					+ "corriente No. 3001480620 sublínea No. 190499 a nombre del MINISTERIO DEL AMBIENTE Y AGUA";			
			
			valorAdicional=true;
			adicional=registroGeneradorDesechosProyectosRcoas.size();
			montoTotaladicional=(montoAdicional*adicional);
			montoTotal=montounico+montoTotaladicional;
			montoTotalProyecto=montoTotal;
		}else{
			registroGenerador = new RegistroGeneradorDesechosRcoa();
			registroGenerador = registroGeneradorDesechosProyectosRcoas.get(0).getRegistroGeneradorDesechosRcoa();
			montoTotalProyecto=montoTotal;
			CatalogoRgdRcoa catalogoMensaje = catalogoRgdRcoaFacade.findByCodigo("rgd_mensaje_pago_registro");
			mensaje = catalogoMensaje.getDescripcion();
		}

		cumpleMontoProyecto=false;
		transaccionFinanciera = new TransaccionFinanciera();
		
		String codigoTramite=tramite;
		documentosRgdRcoasNUT = new ArrayList<>();
		List<NumeroUnicoTransaccional> listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoTramite);
		if(listNUTXTramite!=null && listNUTXTramite.size()>0){
			for (NumeroUnicoTransaccional nut : listNUTXTramite) {
				List<DocumentosRgdRcoa> comprobantes =  documentosRgdRcofacade
						.documentoXTablaIdXIdDoc(nut.getSolicitudUsuario().getId(), 
								"NUT RECAUDACIONES", TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
				
				if (comprobantes.size() > 0) {
					documentosRgdRcoasNUT.addAll(comprobantes);
				} 
			}
		}
		if(codigoTramite!=null && montoTotalProyecto>0){
			generarNUT=true;
		}
		
		institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC();
		
		if(institucionesFinancieras != null && !institucionesFinancieras.isEmpty()){
			transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
		}		
		
		if (this.identificadorMotivo == null){
						
				transaccionesFinancieras = transaccionFinancieraFacade
						.cargarTransaccionesRcoa(proyectosBean.getProyectoRcoa()
								.getId(), bandejaTareasBean.getTarea()
								.getProcessInstanceId(), bandejaTareasBean
								.getTarea().getTaskId());				
		}else{
			transaccionesFinancieras = transaccionFinancieraFacade
					.cargarTransacciones(this.identificadorMotivo);
		}
		
		if(transaccionesFinancieras.isEmpty())
			persist = true;
		
		if(transaccionesFinancieras.size()>0){
			cumpleMonto = cumpleMonto();
		}		
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

		DocumentosRgdRcoa documentoPago = new DocumentosRgdRcoa();
		documentoPago.setContenidoDocumento(contenidoDocumento);
		documentoPago.setMime("application/pdf");
		documentoPago.setIdTable(solicitudUsuario.getId());
		documentoPago.setNombreTabla("NUT RECAUDACIONES");
		documentoPago.setNombre("ComprobantePago" + numeroDocumento + ".pdf");
		documentoPago.setExtesion(".pdf");
		documentoPago = documentosRgdRcofacade.guardarDocumentoAlfrescoSinProyecto(
						numeroUnicoTransaccional.getNutCodigoProyecto() != null ? numeroUnicoTransaccional
								.getNutCodigoProyecto() : solicitudUsuario
								.getSolicitudCodigo(), "RECAUDACIONES", null, documentoPago,
						TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);

		numeroDocumento++;	
		List<String>listaArchivos= new ArrayList<String>();
		listaArchivos=JsfUtil.getBean(GenerarNUTController.class).getListPathArchivos();
		
		JsfUtil.getBean(GenerarNUTController.class).enviarNotificacionPago(solicitudUsuario.getUsuario(),codigoTramite,listaArchivos);
				
		JsfUtil.addMessageInfo("Se ha enviado un correo electrónico con los comprobantes para realizar el pago.");
	}

	public StreamedContent descargar(DocumentosRgdRcoa documento) {
		DefaultStreamedContent content = null;
		try {
			if (documento.getIdAlfresco() != null) {
				byte[] documentoContent = documentosRgdRcofacade.descargar(documento
						.getIdAlfresco());

				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent));
				content.setName(documento.getNombre());
			} else {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}	
	
	public void guardarTransaccion(Integer pagoProyecto) {		
		
		if (transaccionFinanciera.getInstitucionFinanciera() != null
				&& transaccionFinanciera.getNumeroTransaccion() != "") {
			try {
				String codigoTramite = "";
				if (proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getCodigo() != null) {
					codigoTramite = proyectosBean.getProyecto().getCodigo();
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
					transaccionFinanciera = new TransaccionFinanciera();
					if(institucionesFinancieras != null && !institucionesFinancieras.isEmpty()){
						transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
					}
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
						if(institucionesFinancieras != null && !institucionesFinancieras.isEmpty()){
							transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
						}
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
						cumpleMonto = cumpleMonto();
						transaccionFinanciera = new TransaccionFinanciera();
						if(institucionesFinancieras != null && !institucionesFinancieras.isEmpty()){
							transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
						}
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

	private boolean cumpleMonto() {
        double montoTotal = 0;
        for (TransaccionFinanciera transa : transaccionesFinancieras) {
           montoTotal += transa.getMontoTransaccion();
        }
       
        DecimalFormat decimalValorTramite= new DecimalFormat(".##");
        String x="";
       
        x=decimalValorTramite.format(this.montoTotal).replace(",",".");
        
        if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() != null){
        	
			this.montoTotal = Double.valueOf(decimalValorTramite.format(this.montoTotalProyecto).replace(",", "."));
			if (montoTotal >= this.montoTotal) {
				cumpleMontoProyecto = true;
				return true;
			}
			cumpleMontoProyecto = false;
			return false;                     
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
		cumpleMontoProyecto = cumpleMonto();		
	}	
	
	public String completarTarea() {
		this.montoTotal=montoTotalProyecto;
		
		DecimalFormat decimalValorTramite= new DecimalFormat(".##");
        String x="";
       
        x=decimalValorTramite.format(this.montoTotal).replace(",",".");
        
        this.montoTotal=Double.valueOf(x);		
		
		if(montoTotalProyecto>0){
			List<TransaccionFinanciera>listTransProyecto= new ArrayList<TransaccionFinanciera>();
			for (TransaccionFinanciera transaccionFinanciera : getTransaccionesFinancieras()) {
				listTransProyecto.add(transaccionFinanciera);
			}
			if(listTransProyecto.size()==0 || !cumpleMontoProyecto){
				JsfUtil.addMessageError("Debe registrar un pago para el proyecto antes de continuar.");
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

		double montoTotal = Monto();
		if (montoTotal > this.montoTotal) {
			RequestContext.getCurrentInstance().execute("PF('dlg1V2').show();");
		} else if (montoTotal == this.montoTotal) {
			executeBusinessLogic();
		}
		return null;
	}
	
	public void mostrarDialogo() {
		RequestContext.getCurrentInstance().execute("PF('continuarDialog').show();");
	}
	
	private boolean pagoRealizado(){
		List<TransaccionFinanciera> lista=transaccionFinancieraFacade.cargarTransacciones(bandejaTareasBean.getTarea().getProcessInstanceId(),bandejaTareasBean.getTarea().getTaskId());
		if(!lista.isEmpty()) {
			return true;}
		else { 
			return false;}
	}
	
	
	public void executeBusinessLogic() {
		if(persist) {
		String motivo = this.identificadorMotivo == null ? 
				((proyectosBean.getProyecto() == null || proyectosBean.getProyecto().getCodigo() == null)  ? proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() : proyectosBean.getProyecto().getCodigo())
			: this.identificadorMotivo;
						
		List<TransaccionFinanciera> transaccionesFinancierasProyecto= new ArrayList<TransaccionFinanciera>();
		for (TransaccionFinanciera transaccionFinanciera : getTransaccionesFinancieras()) {
			transaccionesFinancierasProyecto.add(transaccionFinanciera);
		}
		boolean pagoSatisfactorio=false;
		if(proyectosBean.getProyecto()!=null && proyectosBean.getProyecto().getCodigo() != null && !proyectosBean.getProyecto().isAreaResponsableEnteAcreditado() && transaccionesFinancierasProyecto.size()>0){
			if(montoTotalProyecto!=montoTotal){
					montoTotalProyecto=montoTotal;
			}
		}						
				
		if(transaccionesFinancierasProyecto.size()>0){
			pagoSatisfactorio=transaccionFinancieraFacade.realizarPago(montoTotalProyecto, transaccionesFinancierasProyecto,motivo);
		}
			
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
		
		if (pagoSatisfactorio) {
            transaccionFinancieraFacade.guardarTransacciones(getTransaccionesFinancieras(),
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
			String rolDirector = "";
			Usuario usuarioAutoridad;
			String autoridad = "";
			if (proyecto.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				rolDirector = Constantes.getRoleAreaName("role.pc.director.control.rgd");				
				
				List<Usuario> listaUsuarios = usuarioFacade.buscarUsuarioPorRolActivo(rolDirector);
								
				if(listaUsuarios != null && !listaUsuarios.isEmpty()){
					usuarioAutoridad = listaUsuarios.get(0);
					autoridad = usuarioAutoridad.getNombre();
				}else{
					JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
					return false;
				}				
			}else{
				try {
					rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");
					List<Usuario> listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(rolDirector, proyecto.getAreaInventarioForestal().getArea());
					
					if(listaUsuarios != null && !listaUsuarios.isEmpty()){
						usuarioAutoridad = listaUsuarios.get(0);
						autoridad = usuarioAutoridad.getNombre();
					}else{
						JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
						return false;
					}					
				} catch (Exception e) {
					JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
					return false;
				}
			}
							
			Map<String, Object> params=new HashMap<>();			
			params.put("director", autoridad);
					
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
			return true;            
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}


}
