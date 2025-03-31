package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.BienesServiciosInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.PromedioInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.BienesServiciosInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.PromedioInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.controllers.CombosController;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TipoOrganizacion;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
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
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validaPago.bean.PagoConfiguracionesBean;


@ManagedBean
@ViewScoped
public class PagosTasaRegistroAmbientalV1Controller {

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
	
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	
	@EJB
	private TarifasFacade tarifasFacade;
	
	@EJB
    private AreaFacade areaFacade;
	
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;
	
	@EJB
	private SolicitudTramiteFacade solicitudTramiteFacade;
	
	@EJB
	private TarifasNUTFacade tarifasNUTFacade;
	
	@EJB
	private DocumentosCoaFacade documentosFacade;
	@EJB
    private OrganizacionFacade organizacionFacade;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	@EJB
	private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
	@EJB
	private PromedioInventarioForestalFacade promedioInventarioForestalFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private BienesServiciosInventarioForestalFacade bienesServiciosInvFacade;
	
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
	private List<InstitucionFinanciera> institucionesFinancierasCobertura;
	
	@Getter
	@Setter
	private List<TransaccionFinanciera> transaccionesFinancieras;

	@Getter
	@Setter
	private TransaccionFinanciera transaccionFinanciera;

	@Getter
	@Setter
	private TransaccionFinanciera transaccionFinancieraCobertura;
	
	@Getter
	@Setter
	private String numeroTransaccion, mensaje, mensajeCobertura;
	
	@Getter
    @Setter
	private List<DocumentosCOA> documentosNUT;
	
	@Getter
    @Setter
	private Tarifas tarifaSitema;
	
	@Getter
	@Setter
	public boolean generarNUT, cumpleValorTotal, pagoCoverturaVegetal, mostrarPnlCobertura, cumpleValorTotalProyecto, cumpleValorTotalCobertura;
	
	@Getter
	@Setter
	public double valorTotalAPagar, montoTotalCoberturaVegetal, montoTotalProyecto;

	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;

	private boolean esEnteAcreditado;
	
	@Getter
   	@Setter
   	public Boolean esEmpresaPublica;

	@PostConstruct
	public void initData() throws Exception {
		proyecto = proyectosBean.getProyectoRcoa();
		cumpleValorTotal = false;
		mostrarPnlCobertura = false;
		mensajeCobertura = "Para el pago correspondiente por el valor de Cobertura Vegetal ingresar el número de referencia del depósito en la sección de Ingreso de transacciones.";
		esEnteAcreditado=false;
		montoTotalCoberturaVegetal = 0;
		if(proyecto.getRenocionCobertura() && (proyecto.getCategorizacion()==1 || proyecto.getCategorizacion()==2)){
			pagoCoverturaVegetal = true;
			// para covertura forestal
			InventarioForestalAmbiental inventario = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(proyecto.getId());				
			PromedioInventarioForestal suma = new PromedioInventarioForestal();
			BienesServiciosInventarioForestal bienes = new BienesServiciosInventarioForestal();
			if(inventario != null){				
				bienes = bienesServiciosInvFacade.getByIdInventarioForestalRegistro(inventario.getId());
				
				if(bienes !=null && bienes.getPagoTotal() != null && bienes.getPagoTotal() >0){
					montoTotalCoberturaVegetal = bienes.getPagoTotal();					
				}else{
					suma = promedioInventarioForestalFacade.getByIdInventarioForestalRegistro(inventario.getId());
					if(suma != null && suma.getPagoDesbroceCobertura() != null && suma.getPagoDesbroceCobertura() > 0){
						montoTotalCoberturaVegetal = suma.getPagoDesbroceCobertura();
					}else{
						pagoCoverturaVegetal= false;
					}
				}
			}
		}else{
			pagoCoverturaVegetal= false;
		}
		transaccionFinanciera = new TransaccionFinanciera();
		transaccionFinancieraCobertura = new TransaccionFinanciera();
		transaccionesFinancieras = new ArrayList<>();
		// busco la institucion financiera si es un enteacreditado
		if (proyectosBean.getProyectoRcoa().getAreaResponsable().getTipoEnteAcreditado() != null) {
			if (proyectosBean.getProyectoRcoa().getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
				institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC();
			}else{
				institucionesFinancieras = institucionFinancieraFacade.obtenerPorNombre(proyecto.getAreaResponsable().getAreaName());
				esEnteAcreditado=true;
				if(institucionesFinancieras==null)
					JsfUtil.addMessageError("No se encontró institución financiara para: "+proyecto.getAreaResponsable().getAreaName());
			}
		} else {
			institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC();
		}
		if(institucionesFinancieras != null && institucionesFinancieras.size() > 0)
			transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
		// institucion bancaria para cobertura vegetal
		institucionesFinancierasCobertura= institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC();
		if(pagoCoverturaVegetal && institucionesFinancieras != null && institucionesFinancieras.size() > 0
								&& institucionesFinancierasCobertura != null && institucionesFinancierasCobertura.size() > 0
								&& institucionesFinancieras.get(0).getId() != institucionesFinancierasCobertura.get(0).getId()){
			mostrarPnlCobertura = true;
			transaccionFinancieraCobertura.setInstitucionFinanciera(institucionesFinancierasCobertura.get(0));
		}
		transaccionesFinancieras = transaccionFinancieraFacade.cargarTransaccionesRcoa(proyecto.getId(),
						bandejaTareasBean.getTarea().getProcessInstanceId(),
						bandejaTareasBean.getTarea().getTaskId());
		if(transaccionesFinancieras != null && !transaccionesFinancieras.isEmpty())
			cumpleMonto();
		
		generarNUT = true;
		
		if(generarNUT) {

			Organizacion organizacion = new Organizacion();
			if (proyecto.getUsuario().getPersona().getOrganizaciones() != null && proyecto.getUsuario().getPersona().getOrganizaciones().size() > 0) {
				organizacion = organizacionFacade.buscarPorRuc(proyecto.getUsuario().getNombre());
			}
			montoTotalProyecto = getCostoRegistroAmbiental(organizacion);			
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
			montoTotalProyecto = 0;

		valorTotalAPagar = montoTotalProyecto + montoTotalCoberturaVegetal ;
		if(esEnteAcreditado)
			mensaje = "Para continuar con el proceso, "
				+ "usted debe realizar el pago de tasa en la entidad financiera o lugar de recaudación correspondiente al " + institucionesFinancieras.get(0).getNombreInstitucion() + " y " 
				+ "con el número de referencia debe completar la tarea \"Realizar pago de tasa\".";
		else
			mensaje = "El pago correspondiente por el Registro Ambiental, se lo debe realizar por medio de la opción de Pago en Línea del Ministerio del Ambiente, Agua y Transición Ecológica.";
		System.out.println("PagosTasaRegistroAmbientalV1Controller.initData()");
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
			numeroUnicoTransaccional.setNutValor(montoTotalProyecto);
			numeroUnicoTransaccional.setNutCodigoProyecto(codigoTramite);
			numeroUnicoTransaccionalFacade.guardarNUT(numeroUnicoTransaccional);
			
			TarifasNUT tarifasNUT= new TarifasNUT();
			tarifasNUT.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
			tarifasNUT.setTarifas(tarifaSitema);
			tarifasNUT.setCantidad(1);
			tarifasNUT.setValorUnitario(montoTotalProyecto);
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
			
			documentoPago = documentosFacade.guardarDocumentoAlfresco(
					codigoTramite,
					"RECAUDACIONES", bandejaTareasBean.getTarea().getProcessInstanceId(),
					documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
			
			documentosNUT = new ArrayList<>();
			documentosNUT.add(documentoPago);
			
			if(mostrarPnlCobertura && montoTotalCoberturaVegetal > 0){
				generarNutCoberturaVegetal();
			}
			List<String>listaArchivos= new ArrayList<String>();
			listaArchivos=JsfUtil.getBean(GenerarNUTController.class).getListPathArchivos();
			JsfUtil.getBean(GenerarNUTController.class).enviarNotificacionPago(solicitudUsuario.getUsuario(),codigoTramite,listaArchivos);
			JsfUtil.addMessageInfo("Se ha enviado un correo electrónico con los comprobantes para realizar el pago.");
			
		} catch (Exception e) {					
			e.printStackTrace();
		}
	}
	

	
	public void generarNutCoberturaVegetal() {
		try{
			String codigoTramite=proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental();
			
			List<NumeroUnicoTransaccional> listNUTXTramite=new ArrayList<NumeroUnicoTransaccional>();
			
			listNUTXTramite = numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoTramite);
			if (listNUTXTramite != null && listNUTXTramite.size() > 0) {
				for (NumeroUnicoTransaccional nut : listNUTXTramite) {
					// verifico si la tarifa corresponde a RGD
					for (TarifasNUT objTarifaNut : nut.getListTarifasNUT()) {
						if(objTarifaNut.getTarifas().getTasasCodigo().equals(CodigoTasa.REGISTRO_GENERADOR_DESECHOS)){
							if (nut.getEstadosNut().getId() == 5) {
								nut.setNutFechaActivacion(new Date());
								nut.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
								nut.setEstadosNut(new EstadosNut(2));
								numeroUnicoTransaccionalFacade.guardarNUT(nut);
								JsfUtil.addMessageWarning("Su trámite está caducado, se activo nuevamente por 3 días, por favor revisar.");
								return;
							}
							JsfUtil.addMessageWarning("Usted ya tiene generado un Número Único de Trámite, por favor descargue en documentos del trámite.");
							return;
						}
					}
				}
			}
			
			SolicitudUsuario solicitudUsuario= new SolicitudUsuario();
			solicitudUsuario.setUsuario(loginBean.getUsuario());
			solicitudUsuario.setSolicitudDescripcion("Pagos cobertura vegetal por el trámite: "+codigoTramite);
			solicitudUsuario.setSolicitudCodigo(JsfUtil.getBean(GenerarNUTController.class).generarCodigoSolicitud());
			solicitudTramiteFacade.guardarSolicitudUsuario(solicitudUsuario);
			
			Integer numeroDocumento=2;
			
			NumeroUnicoTransaccional numeroUnicoTransaccional;
			numeroUnicoTransaccional = new NumeroUnicoTransaccional();
			numeroUnicoTransaccional.setNutCodigo(secuenciasFacade.getNextValueDedicateSequence("NUT_CODIGO", 10));
			numeroUnicoTransaccional.setNutFechaActivacion(new Date());
			numeroUnicoTransaccional.setEstadosNut(new EstadosNut(2));
			numeroUnicoTransaccional.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
			numeroUnicoTransaccional.setSolicitudUsuario(solicitudUsuario);
			numeroUnicoTransaccional.setCuentas(new Cuentas(4));
			numeroUnicoTransaccional.setNutValor(montoTotalCoberturaVegetal);
			numeroUnicoTransaccional.setNutCodigoProyecto(codigoTramite);
			numeroUnicoTransaccionalFacade.guardarNUT(numeroUnicoTransaccional);
			
			TarifasNUT tarifasNUT= new TarifasNUT();
			tarifasNUT.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
			tarifasNUT.setTarifas(tarifaSitema);
			tarifasNUT.setCantidad(1);
			tarifasNUT.setValorUnitario(montoTotalCoberturaVegetal);
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
			
			documentoPago = documentosFacade.guardarDocumentoAlfresco(
					codigoTramite,
					"RECAUDACIONES", bandejaTareasBean.getTarea().getProcessInstanceId(),
					documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
			
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
		if(pagoProyecto==2){
			transaccionFinanciera = new TransaccionFinanciera();
			transaccionFinanciera=transaccionFinancieraCobertura;
		}else {
			transaccionFinancieraCobertura= new TransaccionFinanciera();
		}
		if (transaccionFinanciera.getInstitucionFinanciera() != null
				&& transaccionFinanciera.getNumeroTransaccion() != "") {
			try {
				
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
						transaccionFinanciera.setTipoPagoProyecto((pagoProyecto==1)?"Proyecto":"Cobertura");
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
		transaccionFinancieraCobertura= new TransaccionFinanciera();
		if(institucionesFinancierasCobertura != null && institucionesFinancierasCobertura.size() > 0)
			transaccionFinancieraCobertura.setInstitucionFinanciera(institucionesFinancierasCobertura.get(0));
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
        double montoTotal = 0, montoTotalPro = 0, montoTotalCob = 0;
        for (TransaccionFinanciera transa : transaccionesFinancieras) {
        	if(transa.getTipoPagoProyecto() == null || transa.getTipoPagoProyecto().equals("Proyecto")){
            	montoTotal += transa.getMontoTransaccion();
            	montoTotalPro += transa.getMontoTransaccion();
        	}else if(transa.getTipoPagoProyecto() == null || transa.getTipoPagoProyecto().equals("Cobertura")){
            	montoTotal += transa.getMontoTransaccion();
            	montoTotalCob += transa.getMontoTransaccion();
        	}
        }
       
        DecimalFormat decimalValorTramite= new DecimalFormat(".##");
        String x="";
       
		x = decimalValorTramite.format(this.valorTotalAPagar).replace(",", ".");
		this.valorTotalAPagar = Double.valueOf(x);
		cumpleValorTotalProyecto=false;
		cumpleValorTotalCobertura = false;
		/// si hay pago de cobertura y son cuentas diferentes valido el monto por separado de cada cuenta
		if(mostrarPnlCobertura){
			if (montoTotalPro >= this.montoTotalProyecto) {
				cumpleValorTotalProyecto=true;
			}
			if (montoTotalCob >= this.montoTotalCoberturaVegetal) {
				cumpleValorTotalCobertura = true;
			}
			if (montoTotalPro >= this.montoTotalProyecto && montoTotalCob >= this.montoTotalCoberturaVegetal) {
				return true;
			}
		}else if (montoTotal >= this.valorTotalAPagar) {
			cumpleValorTotalProyecto=true;
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
			 Map<String, Object> parametros = new HashMap<>();
	            List<Usuario> listaUsuario = null;	            	            
	            String rol="role.registro.ambiental.autoridad";
				Area area = proyecto.getAreaResponsable();
				String rolAutoridad = Constantes.getRoleAreaName(rol);
				
				if(area.getTipoArea().getSiglas().toUpperCase().equals("ZONALES")){
					UbicacionesGeografica provincia = proyecto.getAreaResponsable().getUbicacionesGeografica();	
					area = areaFacade.getAreaCoordinacionZonal(provincia);
				}
				
				if(area.getTipoArea().getSiglas().equals("PC")){
					listaUsuario = usuarioFacade.buscarUsuarioPorRol(Constantes.getRoleAreaName("role.area.subsecretario.calidad.ambiental"));
				}else if(area.getTipoArea().getSiglas().equals("OT")){
					listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, area.getArea());
				}else{
					listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, area);
				}
				if(listaUsuario != null && listaUsuario.size() > 0){
					parametros.put("autoridadAmbiental", listaUsuario.get(0).getNombre());
				}else{
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					return;
				}
								
			if (realizarPago()) {	            
	           			transaccionFinancieraFacade.guardarTransacciones(transaccionesFinancieras,
	                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getTaskName(),
	                    bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getProcessId(),
	                    bandejaTareasBean.getTarea().getProcessName());
	           			
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

		/// si hay pago de cobertura y son cuentas diferentes valido el monto por separado de cada cuenta
		if(mostrarPnlCobertura){
			for (TransaccionFinanciera transaccionFinanciera : transaccionesFinancieras) {
				Double montoAux =0.0;
				List<TransaccionFinanciera> listaTransacion = new ArrayList<TransaccionFinanciera>();
				if(transaccionFinanciera.getId() != null && transaccionFinanciera.getId() > 0)
					continue;
				listaTransacion.add(transaccionFinanciera);
				if(transaccionFinanciera.getTipoPagoProyecto().equals("Proyecto")){
					montoAux=montoTotalProyecto;
				}else if(transaccionFinanciera.getTipoPagoProyecto().equals("Cobertura")){
					montoAux=montoTotalCoberturaVegetal;
				}
				pagoSatisfactorio = transaccionFinancieraFacade.realizarPago(montoAux, listaTransacion, proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());
				// si hubo error
				if(!pagoSatisfactorio)
					return pagoSatisfactorio;
			}
		}else{
			pagoSatisfactorio = transaccionFinancieraFacade.realizarPago(
				valorTotalAPagar, transaccionesFinancieras, proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());
		}
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

	public Double validaCostoRegistroAmbiental(Organizacion organizacion) {
		try {
			//buscar tasa para pago x uso de sistema
			tarifaSitema = new Tarifas();
	    	if(organizacion != null && organizacion.getId() != null) {
				// pago de control y emisión ($180.00)
	    		if (organizacion.getTipoOrganizacion().getDescripcion().equals("EP") || organizacion.getTipoOrganizacion().getDescripcion().equals("GA")) {
	    			// no hay pago
	   			}else {
	   				tarifaSitema = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.REGISTRO_AMBIENTAL.getDescripcion());
	    		}
	    	} else {
					tarifaSitema = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.REGISTRO_AMBIENTAL.getDescripcion());
	    	}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(tarifaSitema == null){
			JsfUtil.addMessageError("Ocurrió un error al cargar la información. Por favor comunicarse con mesa de ayuda.");
			cumpleValorTotal = true;
			return Double.valueOf("0");
		}
		if(tarifaSitema != null && tarifaSitema.getId() != null){
			return Double.valueOf(BeanLocator.getInstance(PagoConfiguracionesBean.class).getPagoConfigValue("Valor por registro ambiental v2"));	//100.00
		}else 
			return 0.0;
	}

public Double getCostoRegistroAmbiental(Organizacion organizacion) {
	//buscar tasa para pago x uso de sistema
	tarifaSitema = new Tarifas();
	float valorAPagar = 0.00f;
	esEmpresaPublica = false;

	try {
    	Float costoEmisionRegistro = 0f;
    	Float costoControlSeguimiento = 0f;
    		costoEmisionRegistro = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
    				getPagoConfigValue("Valor por registro ambiental v2"));	//100.00
    		costoControlSeguimiento = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
    				getPagoConfigValue("Valor por seguimiento de registro ambiental v1"));	//80.00
   
	    	valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
	    	
	    	if(organizacion != null && organizacion.getId() != null) {
	    		if(organizacion.getTipoOrganizacion() != null 
	    				&& (organizacion.getTipoOrganizacion().getTipoEmpresa().equals(TipoOrganizacion.publica)
	        				|| (organizacion.getTipoOrganizacion().getTipoEmpresa().equals(TipoOrganizacion.mixta)
	        					&& organizacion.getParticipacionEstado().equalsIgnoreCase(CombosController.IGUAL_MAS_PARTICIPACION)))) {
	    			valorAPagar = costoControlSeguimiento;
        			esEmpresaPublica = true;
        		}
	    	}
	    	
	    	if(valorAPagar > 0) 
	    		tarifaSitema = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.REGISTRO_AMBIENTAL.getDescripcion());
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Double.valueOf(valorAPagar);
	}
}
