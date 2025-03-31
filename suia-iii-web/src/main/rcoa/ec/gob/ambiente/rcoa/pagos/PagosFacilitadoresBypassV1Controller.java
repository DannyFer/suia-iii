package ec.gob.ambiente.rcoa.pagos;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformeTecnicoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.OficioPronunciamientoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.DocumentoPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.FacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.ProyectoFacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.DocumentosPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.FacilitadorPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.ProyectoFacilitadorPPC;
import ec.gob.ambiente.rcoa.participacionCiudadanaBypass.controllers.UtilPPCBypass;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.CodigoTasa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
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
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;


@ManagedBean
@ViewScoped
public class PagosFacilitadoresBypassV1Controller {

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
	private DocumentoPPCFacade documentosFacade;	
	@EJB
	private SecuenciasFacade secuenciasFacade;	
	@EJB
	private FacilitadorFacade facilitadorFacade;	
	@EJB
	private FacilitadorPPCFacade facilitadorPPCFacade;	
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ProyectoFacilitadorPPCFacade proyectoFacilitadorPPCFacade;
	@EJB
	private OficioPronunciamientoEsIAFacade oficioPronunciamientoEsIAFacade;
	@EJB
	private InformeTecnicoEsIAFacade informeTecnicoEsIAFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	@EJB
	private DocumentosImpactoEstudioFacade documentosEIAFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	public CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@Inject
	private UtilPPCBypass utilPPC;
	
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
	private ProyectoFacilitadorPPC proyectoFacilitadorPPC;
    @Getter
	@Setter
	private TransaccionFinanciera transaccionFinanciera;
    @Getter
    @Setter
	private Tarifas tarifaUsoSitema;
    @Getter
	@Setter
	private DocumentosPPC documentosPPC;
    

	@Getter
	@Setter
	private List<InstitucionFinanciera> institucionesFinancieras;	
	@Getter
	@Setter
	private List<TransaccionFinanciera> transaccionesFinancieras;
	@Getter
	@Setter
	private List<DocumentosPPC> documentosNUT;
	@Getter
	@Setter
	private List<Usuario> listaUsuariosFacilitadores;
	@Getter
	@Setter
	private List<String> facilitadoresTarifaEspecial;

	
	@Getter
	@Setter
	private String numeroTransaccion;
	@Getter
	@Setter
	public Boolean generarNUT, cumpleValorTotal;
	@Getter
	@Setter
	public double valorTotalFacilitador;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private Integer numeroFacilitadores;
	
	@Getter
	@Setter
	private Boolean facilitadorAdicional;
	
	private String codigoTramite;
	
	private Integer idProyecto;
	
	@Getter
	@Setter
	private String nombreArchivo;
	
	@PostConstruct
	public void initData() throws Exception {		
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
		numeroFacilitadores = Integer.valueOf((String) variables.get("numeroFacilitadores"));
		idProyecto = Integer.valueOf((String) variables.get("idProyecto"));
		codigoTramite=proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental();
		
		facilitadorAdicional= false;
		
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
		
		proyectoFacilitadorPPC = proyectoFacilitadorPPCFacade.getByIdProyecto(idProyecto);
		
		valorTotalFacilitador = 0;
		Boolean buscarFacilitadores = false;
		if(proyectoFacilitadorPPC.getId() != null) {
			//si ya existe ya existen los facilitadores
			listaUsuariosFacilitadores = facilitadorPPCFacade.usuariosFacilitadoresAsignadosPendientesAceptacion(proyectosBean.getProyectoRcoa());
			
			valorTotalFacilitador = proyectoFacilitadorPPC.getValorTotal();
		} else 
			buscarFacilitadores = true;
		
		if(buscarFacilitadores) {
			listaUsuariosFacilitadores = utilPPC.getUsuariosfacilitadoresBypass(numeroFacilitadores,proyectosBean.getProyectoRcoa());
			if(listaUsuariosFacilitadores.size()==0) {
				JsfUtil.addMessageError("Error al completar la tarea. No existe la cantidad de facilitadores solicitados disponibles.");
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
	            return;
			}
			
			calcularValorPago();
		}
		
		generarNUT = false;
		
		if(generarNUT) {			
			//buscar si hay nuts activos para el tramite y buscar los documentos
			documentosNUT = new ArrayList<>();
			List<NumeroUnicoTransaccional> listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());
			if(listNUTXTramite!=null && listNUTXTramite.size()>0){
				for (NumeroUnicoTransaccional nut : listNUTXTramite) {
					List<DocumentosPPC> comprobantes = documentosFacade
							.documentoXTablaIdXIdDoc(nut.getSolicitudUsuario().getId(),TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS, "NUT RECAUDACIONES");
					
					if (comprobantes.size() > 0) {
						documentosNUT.addAll(comprobantes);
					} 
				}
			}
		}
			
		
		if(proyectoFacilitadorPPC.getId()==null)
		{
			proyectoFacilitadorPPC = new ProyectoFacilitadorPPC();
			proyectoFacilitadorPPC.setEstado(true);
			proyectoFacilitadorPPC.setFacilitadoresAdicionales(facilitadorAdicional);
			proyectoFacilitadorPPC.setNumeroFacilitadores(numeroFacilitadores);
			proyectoFacilitadorPPC.setProyectoLicenciaCoa(proyectosBean.getProyectoRcoa());
			proyectoFacilitadorPPC.setValorTotal(valorTotalFacilitador);
			proyectoFacilitadorPPC=proyectoFacilitadorPPCFacade.guardar(proyectoFacilitadorPPC);
			
			guardarFacilitadores();
		}
	
		
		documentosPPC = documentosFacade.getByDocumentoTipoDocumento(proyectosBean.getProyectoRcoa().getId(), TipoDocumentoSistema.FACTURA_PAGO_FACILITADORES, "FACTURA PAGOS");
		if(documentosPPC != null && documentosPPC.getId() != null)
			nombreArchivo = documentosPPC.getNombreDocumento();
		
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/participacionCiudadanaBypass/realizarPagoFacilitadores.jsf");
	}
	
	public void generarNut() {
		try{
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
			
			DocumentosPPC documentoPago = new DocumentosPPC(); 
			documentoPago.setContenidoDocumento(contenidoDocumento);
			documentoPago.setNombreDocumento("ComprobantePago"+ numeroDocumento +".pdf");
			documentoPago.setExtencionDocumento(".pdf");
			documentoPago.setTipo("application/pdf");
			documentoPago.setNombreTabla("NUT RECAUDACIONES");
			documentoPago.setIdTabla(solicitudUsuario.getId());
			documentoPago.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
			
			documentoPago = documentosFacade.guardarDocumentoAlfresco(codigoTramite,"PARTICIPACION CIUDADANA", bandejaTareasBean.getTarea().getProcessInstanceId(),documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
			
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
       
		x = decimalValorTramite.format(this.valorTotalFacilitador).replace(",", ".");

		this.valorTotalFacilitador = Double.valueOf(x);
		if (montoTotal >= this.valorTotalFacilitador) {
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
	
	public void uploadDocumentoPago(FileUploadEvent event)
	{
		documentosPPC = new DocumentosPPC();
		byte[] contenido=event.getFile().getContents();
		documentosPPC.setContenidoDocumento(contenido);
		documentosPPC.setNombreDocumento(event.getFile().getFileName());
		documentosPPC.setExtencionDocumento(".pdf");		
		documentosPPC.setTipo("application/pdf");
		documentosPPC.setNombreTabla("FACTURA PAGOS");
		documentosPPC.setDescripcion("FACTURA PAGOS");
		documentosPPC.setIdTabla(proyectosBean.getProyectoRcoa().getId());
		documentosPPC.setProyectoFacilitadorPPC(proyectoFacilitadorPPC);
		documentosPPC.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
		nombreArchivo=event.getFile().getFileName();
	}
	
	public boolean guardarDocumentoPago()
	{
		boolean estado = false;		
		try {
			if (documentosPPC.getId() == null
					&& documentosPPC.getContenidoDocumento() != null) 
				documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental(), "PARTICIPACION CIUDADANA", bandejaTareasBean.getTarea().getProcessInstanceId(), documentosPPC, TipoDocumentoSistema.FACTURA_PAGO_FACILITADORES);
			estado=true;
		} catch (ServiceException | CmisAlfrescoException e) {				
			estado=false;
		}
		return estado;
	}
	
	public void completarTarea(){
		try {
			
			if(listaUsuariosFacilitadores.size()==0) {
				JsfUtil.addMessageError("No existe la cantidad de facilitadores solicitados disponibles.");
	            return;
			}
			
			if (transaccionesFinancieras.size() == 0) {
				JsfUtil.addMessageError("Debe registrar un pago antes de continuar.");
				return;
			}

			if (!cumpleValorTotal) {
				JsfUtil.addMessageError("El monto de la transacción utilizada es insuficiente para completar el pago de tasas.");
				return;
			}
			
			if(documentosPPC==null || documentosPPC.getContenidoDocumento()==null)
			{
				JsfUtil.addMessageError("Adjuntar factura de pago.");
				return;
			}
			
			if(!guardarDocumentoPago())
			{
				JsfUtil.addMessageError("Error al guardar la factura de pago.");
				return;
			}
			
			//validar la asignacion de los facilitadores
			String[] facilitadoresLista = new String[numeroFacilitadores];
			Integer cont = 0;		
			for (Usuario usuario : listaUsuariosFacilitadores) {
				if(cont<numeroFacilitadores)
					facilitadoresLista[cont++] = usuario.getNombre();
			}
			
			Map<String, Object> params = new HashMap<String, Object>();

            params.put("listaFacilitadores", facilitadoresLista);
			
			if (realizarPago()) {
	            transaccionFinancieraFacade.guardarTransacciones(transaccionesFinancieras,
	                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getTaskName(),
	                    bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getProcessId(),
	                    bandejaTareasBean.getTarea().getProcessName());
	            
	            List<FacilitadorPPC> facilitadores = facilitadorPPCFacade.facilitadoresAsignadosPendientesAceptacion(proyectosBean.getProyectoRcoa());
	            for (FacilitadorPPC facilitadorPPC : facilitadores) {
	            	facilitadorPPC.setFechaRegistroPago(new Date());
					facilitadorPPCFacade.guardar(facilitadorPPC);
				}
				
	            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
	            procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
	            
	            for (Usuario usuario : listaUsuariosFacilitadores) {
	            	notificaciones(usuario);
	            }
	            
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
				valorTotalFacilitador, transaccionesFinancieras, proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());

		for (TransaccionFinanciera transaccionFinanciera : transaccionesFinancieras) {
			try {
				NumeroUnicoTransaccional nut = new NumeroUnicoTransaccional();
				nut = numeroUnicoTransaccionalFacade.buscarNUTPorNumeroTramite(transaccionFinanciera.getNumeroTransaccion());
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
	
	private void calcularValorPago() throws Exception {
		facilitadoresTarifaEspecial = new ArrayList<String>();
		
		String codeGalapagos = Constantes.CODIGO_INEC_GALAPAGOS;
		
		CatalogoGeneralCoa valorIvaS = catalogoCoaFacade.obtenerCatalogoPorCodigo("valor.impuesto.iva");
		double valorIva = Double.parseDouble(valorIvaS.getValor()); 
		valorIva = 1 + (valorIva /100);
		
		if(proyectosBean.getProyectoRcoa().getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
			//Si el tramite se lleva en el PNG
			Tarifas tarifaFacilitadorGalapagos = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.FACILIATDOR_GALAPAGOS.getDescripcion()); 
			Tarifas tarifaFacilitadorEC = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.FACILIATDOR.getDescripcion());
			Double valorTotal = 0.0;
			
			for (Usuario user : listaUsuariosFacilitadores) {
				Usuario ufacilitador = usuarioFacade.buscarUsuarioPorId(user.getId());
				String codeProvinciaFacilitador = ufacilitador.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec();
				if(!codeProvinciaFacilitador.equals(codeGalapagos)) {
					tarifaUsoSitema = tarifaFacilitadorGalapagos; //si el facilitador NO es de galapagos el valor es 1900
					facilitadoresTarifaEspecial.add(user.getNombre());
				} else 
					tarifaUsoSitema = tarifaFacilitadorEC; //si el facilitador ES de galapagos el valor es 1500
				
				if(tarifaUsoSitema == null){
					JsfUtil.addMessageError("Ocurrió un error al cargar la información. Por favor comunicarse con mesa de ayuda.");
					cumpleValorTotal = true;
					return;
				}
				
				valorTotal += (tarifaUsoSitema.getTasasValor() * valorIva);
			}
			
			valorTotalFacilitador = valorTotal;
		} else {
			//el valor es 1500 para ecuador continental
			tarifaUsoSitema = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.FACILIATDOR.getDescripcion());
			
			if(tarifaUsoSitema == null){
				JsfUtil.addMessageError("Ocurrió un error al cargar la información. Por favor comunicarse con mesa de ayuda.");
				cumpleValorTotal = true;
				return;
			}
			valorTotalFacilitador = (tarifaUsoSitema.getTasasValor() * valorIva) * numeroFacilitadores;
		}
	}
	
	public void guardarFacilitadores() {		
		for (Usuario usuario : listaUsuariosFacilitadores) {
			Boolean tarifaEspecial = false;
			if(facilitadoresTarifaEspecial.contains(usuario.getNombre()))
				tarifaEspecial = true;
			
			FacilitadorPPC facilitador = new FacilitadorPPC();
			facilitador.setEstado(true);
			facilitador.setEsAdicional(facilitadorAdicional);
			facilitador.setProyectoLicenciaCoa(proyectosBean.getProyectoRcoa());
			facilitador.setUsuario(usuario);
			facilitador.setTarifaEspecial(tarifaEspecial);
			facilitadorPPCFacade.guardar(facilitador);
		}
		
	}
	
	public StreamedContent descargarOficoEia() throws IOException {
		DefaultStreamedContent content = null;
		try {
			InformacionProyectoEia esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyectoFacilitadorPPC.getProyectoLicenciaCoa());
			InformeTecnicoEsIA informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(esiaProyecto, InformeTecnicoEsIA.consolidado);
			OficioPronunciamientoEsIA oficioPronunciamiento = oficioPronunciamientoEsIAFacade.getPorEstudioInforme(esiaProyecto.getId(), informeTecnico.getId());
			
			DocumentoEstudioImpacto documentoEia = null;
			List<DocumentoEstudioImpacto> listaDocumentos = documentosEIAFacade.documentoXTablaIdXIdDocLista(oficioPronunciamiento.getId(), OficioPronunciamientoEsIA.class.getSimpleName(), TipoDocumentoSistema.EIA_OFICIO_APROBACION_CONSOLIDADO);		
			if (listaDocumentos.size() > 0) {
				documentoEia = listaDocumentos.get(0);
			}
			
			byte[] documentoContent = null;
				
			if (documentoEia != null && documentoEia.getAlfrescoId() != null) {
				documentoContent = documentosFacade.descargar(documentoEia.getAlfrescoId());
			}
			
			if (documentoEia != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoEia.getNombre());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	private void notificaciones(Usuario usuarioFacilitador) {
		try {
			Object[] parametrosCorreoTecnicos = new Object[] {usuarioFacilitador.getPersona().getTipoTratos().getNombre(), 
						usuarioFacilitador.getPersona().getNombre(), proyectoFacilitadorPPC.getProyectoLicenciaCoa().getNombreProyecto(), 
						proyectoFacilitadorPPC.getProyectoLicenciaCoa().getCodigoUnicoAmbiental() };
				
				String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
								"bodyNotificacionDesignacionFacilitadorByPass",
								parametrosCorreoTecnicos);
				
				Email.sendEmail(usuarioFacilitador, "Regularización Ambiental Nacional", notificacion, codigoTramite, loginBean.getUsuario());
				
		} catch (Exception e) {					
			e.printStackTrace();
		}
	}
}
