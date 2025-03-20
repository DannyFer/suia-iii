package ec.gob.ambiente.rcoa.pagos;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
import ec.gob.ambiente.rcoa.model.PagoKushkiJson;
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
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.CodigoTasa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.recaudaciones.bean.PagoRcoaBean;
import ec.gob.ambiente.suia.recaudaciones.controllers.GenerarNUTController;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.SolicitudTramiteFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.TarifasFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.TarifasNUTFacade;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;


@ManagedBean
@ViewScoped
public class PagosFacilitadoresBypassController {

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
	@ManagedProperty(value = "#{pagoRcoaBean}")
	private PagoRcoaBean pagoRcoaBean;
	@Getter
	@Setter
	private ProyectoFacilitadorPPC proyectoFacilitadorPPC;
	@Getter
	@Setter
	private Tarifas tarifaUsoSitema;
	@Getter
	@Setter
	private DocumentosPPC documentosPPC;

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
	public Double valorTotalFacilitador;
	
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
	@Getter
	@Setter
	private String mensajeAddFacilitador;
	
	/*Ubicacion Geografica del Proyecto*/
	@Getter
	@Setter
	List<UbicacionesGeografica> ubicacionesSeleccionadas;

	private UbicacionesGeografica data_localizacion = new UbicacionesGeografica();
	
	@PostConstruct
	public void initData() throws Exception {	
		String url_redirec = "/pages/rcoa/participacionCiudadanaBypass/realizarPagoFacilitadores.jsf";
		pagoRcoaBean.setUrl_enlace(url_redirec);		
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
		numeroFacilitadores = Integer.valueOf((String) variables.get("numeroFacilitadores"));
		idProyecto = Integer.valueOf((String) variables.get("idProyecto"));
		codigoTramite=proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental();
		
		facilitadorAdicional= false;
		pagoRcoaBean.setFacilitadores(false);
		pagoRcoaBean.setValidaNutKushki(true);
		GenerarNUTController nutController = JsfUtil.getBean(GenerarNUTController.class);
		pagoRcoaBean.setDocumentosNUT(nutController.obtenerDocumentosNut(codigoTramite));
		List<PagoKushkiJson> PagoKushkiJsonAux = nutController.listPagosKushki(codigoTramite);
		if (PagoKushkiJsonAux != null)
		{
			pagoRcoaBean.setPagoKushkiJson(PagoKushkiJsonAux);
		} 
		if (pagoRcoaBean.getPagoKushkiJson() != null)
		{
			pagoRcoaBean.setValidaNutKushki(false);
		}  		
		pagoRcoaBean.setGenerarNUT(true);
		pagoRcoaBean.setCumpleMontoProyecto(false);
		pagoRcoaBean.setTramite(codigoTramite);
		mensajeAddFacilitador = pagoRcoaBean.getMensaje_Facilitadores();
		cargarUbicacionProyecto();
		pagoRcoaBean.setTipoProyecto("PARTICIPACION");
		pagoRcoaBean.setDireccion(proyectosBean.getProyectoRcoa().getDireccionProyecto());
		pagoRcoaBean.setIdproy(proyectosBean.getProyectoRcoa().getId()); 	
		pagoRcoaBean.setCiudad(data_localizacion.getUbicacionesGeografica().getNombre().toString());
		pagoRcoaBean.setProvincia(data_localizacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre().toString());
		pagoRcoaBean.setPais(data_localizacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre().toString());				
		pagoRcoaBean.setIdentificadorMotivo(null);
		pagoRcoaBean.setTransaccionFinanciera(new TransaccionFinanciera());
		pagoRcoaBean.setTransaccionFinancieraCobertura(new TransaccionFinanciera());
		pagoRcoaBean.setTransaccionesFinancieras(new ArrayList<TransaccionFinanciera>());
		
		pagoRcoaBean.setInstitucionesFinancieras(institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC());
		if(pagoRcoaBean.getInstitucionesFinancieras() != null && pagoRcoaBean.getInstitucionesFinancieras().size() > 0)
			pagoRcoaBean.getTransaccionFinanciera().setInstitucionFinanciera(pagoRcoaBean.getInstitucionesFinancieras().get(0));
		
		nutController.cargarTransacciones();
		proyectoFacilitadorPPC = proyectoFacilitadorPPCFacade.getByIdProyecto(idProyecto);
		
		valorTotalFacilitador = 0.0;
		Boolean buscarFacilitadores = false;
		if(proyectoFacilitadorPPC.getId() != null) {
			//si ya existe ya existen los facilitadores
			listaUsuariosFacilitadores = facilitadorPPCFacade.usuariosFacilitadoresAsignadosPendientesAceptacion(proyectosBean.getProyectoRcoa());
			
			valorTotalFacilitador = calcularValorPago();
			
			if (Double.compare(valorTotalFacilitador, proyectoFacilitadorPPC.getValorTotal()) != 0) {
				proyectoFacilitadorPPC.setValorTotal(valorTotalFacilitador);
				proyectoFacilitadorPPC = proyectoFacilitadorPPCFacade.guardar(proyectoFacilitadorPPC);
			}
		} else 
			buscarFacilitadores = true;
		
		if(buscarFacilitadores) {
			listaUsuariosFacilitadores = utilPPC.getUsuariosfacilitadoresBypass(numeroFacilitadores,proyectosBean.getProyectoRcoa());
			if(listaUsuariosFacilitadores.size()==0) {
				JsfUtil.addMessageError("Error al completar la tarea. No existe la cantidad de facilitadores solicitados disponibles.");
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
				pagoRcoaBean.setGenerarNUT(false);
	            return;
			}
			
			valorTotalFacilitador = calcularValorPago();
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
		pagoRcoaBean.setValorAPagar(valorTotalFacilitador);
		pagoRcoaBean.setMontoTotalProyecto(valorTotalFacilitador);

		if(pagoRcoaBean.getTransaccionesFinancieras() != null && !pagoRcoaBean.getTransaccionesFinancieras().isEmpty())
			pagoRcoaBean.setCumpleMontoProyecto(nutController.cumpleMonto());
		
		documentosPPC = documentosFacade.getByDocumentoTipoDocumento(proyectosBean.getProyectoRcoa().getId(), TipoDocumentoSistema.FACTURA_PAGO_FACILITADORES, "FACTURA PAGOS");
		if(documentosPPC != null && documentosPPC.getId() != null)
			nombreArchivo = documentosPPC.getNombreDocumento();
		nutController.cerrarMensaje();
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/participacionCiudadanaBypass/realizarPagoFacilitadores.jsf");
	}
	
	public void cargarUbicacionProyecto() {
		ubicacionesSeleccionadas = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectosBean.getProyectoRcoa());
		data_localizacion = ubicacionesSeleccionadas.get(0);
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
			
			if (pagoRcoaBean.getTransaccionesFinancieras().size() == 0) {
				JsfUtil.addMessageError("Debe registrar un pago antes de continuar.");
				return;
			}

			if (!pagoRcoaBean.isCumpleMontoProyecto()) {
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
	            transaccionFinancieraFacade.guardarTransacciones(pagoRcoaBean.getTransaccionesFinancieras(),
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
				valorTotalFacilitador, pagoRcoaBean.getTransaccionesFinancieras(), proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());

		for (TransaccionFinanciera transaccionFinanciera : pagoRcoaBean.getTransaccionesFinancieras()) {
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
	
	private Double calcularValorPago() throws Exception {
		BigDecimal valorTotalFacilitadores = new BigDecimal(0);
		
		facilitadoresTarifaEspecial = new ArrayList<String>();
		
		String codeGalapagos = Constantes.CODIGO_INEC_GALAPAGOS;
		
		CatalogoGeneralCoa valorIvaS = catalogoCoaFacade.obtenerCatalogoPorCodigo("valor.impuesto.iva");
		double valorIva = Double.parseDouble(valorIvaS.getValor()); 
		valorIva = 1 + (valorIva /100);
		
		BigDecimal valorIvaFinal = BigDecimal.valueOf(valorIva);
		
		if(proyectosBean.getProyectoRcoa().getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
			//Si el tramite se lleva en el PNG
			Tarifas tarifaFacilitadorGalapagos = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.FACILIATDOR_GALAPAGOS.getDescripcion()); 
			Tarifas tarifaFacilitadorEC = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.FACILIATDOR.getDescripcion());
			BigDecimal valorTotal = new BigDecimal(0);
			
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
					pagoRcoaBean.setCumpleMontoProyecto(true);
					return 0.0;
				}
				
				BigDecimal valorUnitario = valorIvaFinal.multiply(new BigDecimal(tarifaUsoSitema.getTasasValor()));
						
				valorTotal.add(valorUnitario);
			}
			
			valorTotalFacilitadores = valorTotal;
		} else {
			//el valor es 1500 para ecuador continental
			tarifaUsoSitema = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.FACILIATDOR.getDescripcion());
			
			if(tarifaUsoSitema == null){
				JsfUtil.addMessageError("Ocurrió un error al cargar la información. Por favor comunicarse con mesa de ayuda.");
				pagoRcoaBean.setCumpleMontoProyecto(true);
				return 0.0;
			}
			
			BigDecimal valorUnitario = valorIvaFinal.multiply(new BigDecimal(tarifaUsoSitema.getTasasValor()));
			
			valorTotalFacilitadores = valorUnitario.multiply(new BigDecimal(numeroFacilitadores));
		}
		
		return valorTotalFacilitadores.doubleValue();
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
