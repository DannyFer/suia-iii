package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.ExpedienteBPCFacade;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.ExpedienteBPC;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.DocumentoResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.ResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.DocumentoResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ResolucionAmbiental;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.PagoKushkiJson;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.ProyectoFacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.ProyectoFacilitadorPPC;
import ec.gob.ambiente.retce.model.ManifiestoUnico;
import ec.gob.ambiente.suia.administracion.controllers.CombosController;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TipoOrganizacion;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.CodigoTasa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.recaudaciones.bean.PagoRcoaBean;
import ec.gob.ambiente.suia.recaudaciones.controllers.GenerarNUTController;
import ec.gob.ambiente.suia.recaudaciones.facade.CuentaFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.TarifasFacade;
import ec.gob.ambiente.suia.recaudaciones.model.Cuentas;
import ec.gob.ambiente.suia.recaudaciones.model.DocumentoNUT;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class EmisionLicenciaAmbientalController {
	
	private static final Logger LOG = Logger.getLogger(EmisionLicenciaAmbientalController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	private Map<String, Object> variables;
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;
    
	@Getter
	@Setter
	@ManagedProperty(value = "#{pagoRcoaBean}")
	private PagoRcoaBean pagoRcoaBean;
    
	/*EJBs*/
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
    
    
    // FACADES GENERALES
    @EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
    @EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
    @EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
    @EJB
	private TarifasFacade tarifasFacade;
    @EJB
	private SecuenciasFacade secuenciasFacade;
    @EJB
	private ResolucionAmbientalFacade resolucionAmbientalFacade;
    @EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
    @EJB
	private DocumentoResolucionAmbientalFacade  documentoResolucionAmbientalFacade;
    @EJB
    private ProyectoFacilitadorPPCFacade proyectoFacilitadorPPCFacade;
    @EJB
    private CuentaFacade cuentaFacade;
	@EJB
	private DocumentosCoaFacade documentosFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;
	@EJB
	private ExpedienteBPCFacade expedienteBPCFacade;
	    
    @Getter
	@Setter
	private List<TransaccionFinanciera> transaccionesFinancierasEliminar = new ArrayList<TransaccionFinanciera>();
    
    @Getter
	@Setter
	private List<PagoKushkiJson> PagoKushkiJsonControl = new ArrayList<PagoKushkiJson>();
        
    /*Object*/
    @Setter
    @Getter
    private ProyectoLicenciaCoa proyectoLicenciaCoa = new ProyectoLicenciaCoa();
    @Setter
    @Getter
    private ResolucionAmbiental resolucionAmbiental = new ResolucionAmbiental();
    @Setter
    @Getter
    private InformacionProyectoEia informacionProyectoEia = new InformacionProyectoEia();
    @Setter
    @Getter
    private InstitucionFinanciera institucionFinanciera = new InstitucionFinanciera();
    @Setter
    @Getter
    private TransaccionFinanciera transaccionFinancieraSelected = new TransaccionFinanciera();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental facturaPermisoAmbiental = new DocumentoResolucionAmbiental();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental pagoEmisionPermiso = new DocumentoResolucionAmbiental();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental polizaCostoImplementacion = new DocumentoResolucionAmbiental();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental justificacionCostoMedidas = new DocumentoResolucionAmbiental();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental cronogramaValorado = new DocumentoResolucionAmbiental();
    @Setter
    @Getter
    private Organizacion organizacion;
    @Setter
    @Getter
    private ExpedienteBPC expedienteBPC = new ExpedienteBPC();
    
    
	/*Variables*/
	@Getter
	private Integer idRegistroPreliminar;
	@Getter
	@Setter
	public Boolean cumpleMonto = false, detalleRevisionEmision, calcularValorPagar = false, datosGuardados, persist = false;
	@Getter
	@Setter
	private String tramite, tecnicoFinanciero,mensajeInventario,mensajeRegistro;
	@Getter
	@Setter
	private Double montoTotalLicencia, montoTotalInventario, montoTotalRgd, valorPorInventarioForestal;
	@Getter
   	@Setter
   	public Boolean calcularUnoPorMil, tienePagoPorRgd, PagoOnline, esEmpresaPublica, esOrganizacion, tieneBypassPPC = false, habilitaPagoIF = false;
	@Getter
	@Setter
	private String mensaje;
	@Getter
	@Setter
	private boolean cumpleMontoRevalidacion;
	
	/*Ubicacion Geografica del Proyecto*/
	@Getter
	@Setter
	List<UbicacionesGeografica> ubicacionesSeleccionadas;

	private UbicacionesGeografica data_localizacion = new UbicacionesGeografica();
	
    @PostConstruct
	public void init() {
    	try {
    		PagoKushkiJsonControl = new ArrayList<PagoKushkiJson>();
    		PagoOnline = false;
    		String url_redirec = "/pages/rcoa/emisionLicenciaAmbiental/operadorCostoProyecto.jsf";
    		pagoRcoaBean.setUrl_enlace(url_redirec);
    		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
    		tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
    		tecnicoFinanciero = (String) variables.get("tecnicoFinanciero");
    		idRegistroPreliminar = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
    		proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idRegistroPreliminar);
    		expedienteBPC = expedienteBPCFacade.getByProyectoLicenciaCoa(idRegistroPreliminar);
    		tieneBypassPPC = (expedienteBPC.getId() != null) ? true : false;
    		
			Boolean esCorreccion = variables.containsKey("existeObservacion") ? (Boolean.valueOf((String) variables.get("existeObservacion"))) : false;
			
			organizacion = organizacionFacade.buscarPorRuc(proyectoLicenciaCoa.getUsuario().getNombre());
    		
    		datosIniciales();

    		GenerarNUTController nutController = JsfUtil.getBean(GenerarNUTController.class);
    		List<DocumentoNUT> documentosNUTOtraTarea = new ArrayList<DocumentoNUT>();
    		List<DocumentoNUT> documentosNUTAux = nutController.obtenerDocumentosNut(tramite);
    		List<PagoKushkiJson> PagoKushkiJsonAux = nutController.listPagosKushki(tramite);
    		for (DocumentoNUT documentoNUT : documentosNUTAux) {
    			if(!documentoNUT.getIdProceso().equals(bandejaTareasBean.getProcessId())){
    				documentosNUTOtraTarea.add(documentoNUT);
    			}
    		}
    		//elimino los documentos de otras tareas
    		if(documentosNUTOtraTarea.size() > 0){
    			documentosNUTAux.removeAll(documentosNUTOtraTarea);
    		}
    		if(documentosNUTAux.size() == 0){
    			pagoRcoaBean.setTipoPago(null); //para ocultar botón de generar nut
    		}
    		pagoRcoaBean.setDocumentosNUT(documentosNUTAux);
    		pagoRcoaBean.setGenerarNUT(true);
    		pagoRcoaBean.setValidaNutKushki(true);
    		pagoRcoaBean.setCumpleMontoProyecto(false);
    		pagoRcoaBean.setCalcularValorPagar(false);
    		pagoRcoaBean.setControl_salto(false);
    		pagoRcoaBean.setMostrarPnlCobertura(tienePagoPorRgd || resolucionAmbiental.getPagoInventarioForestal() > 0);
    		pagoRcoaBean.setTramite(tramite);
    		if (PagoKushkiJsonAux != null)
    		{
    			pagoRcoaBean.setPagoKushkiJson(PagoKushkiJsonAux);
    			PagoKushkiJsonControl = PagoKushkiJsonAux;
    		}    		
    		if (pagoRcoaBean.getPagoKushkiJson() != null)
			{
    			PagoKushkiJson PagoKushkiJsonAux1 = new PagoKushkiJson();
				pagoRcoaBean.setValidaNutKushki(false);
				PagoKushkiJsonAux1 = PagoKushkiJsonAux.get(0);
				resolucionAmbiental.setCostoProyecto(Double.valueOf(PagoKushkiJsonAux1.getPajsSku()));
				resolucionAmbiental.setMontoTotalProyecto(Double.valueOf(PagoKushkiJsonAux1.getPajsPrice()));
				PagoOnline = true;
				pagoRcoaBean.setControl_salto(true);
			}    		
		    cargarUbicacionProyecto();
    		pagoRcoaBean.setTipoProyecto("LICENCIA");
			pagoRcoaBean.setDireccion(proyectoLicenciaCoa.getDireccionProyecto());
			pagoRcoaBean.setIdproy(proyectoLicenciaCoa.getId());  
			pagoRcoaBean.setCiudad(data_localizacion.getUbicacionesGeografica().getNombre().toString());
			pagoRcoaBean.setProvincia(data_localizacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre().toString());
			pagoRcoaBean.setPais(data_localizacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre().toString());					
    		pagoRcoaBean.setIdentificadorMotivo(null);
    		pagoRcoaBean.setTransaccionFinanciera(new TransaccionFinanciera());
    		pagoRcoaBean.setTransaccionFinancieraCobertura(new TransaccionFinanciera());
    		pagoRcoaBean.setTransaccionesFinancieras(new ArrayList<TransaccionFinanciera>());
    		pagoRcoaBean.setCategorizacion(proyectoLicenciaCoa.getCategorizacion());
    		mensajeInventario =  pagoRcoaBean.getMensaje_Costo_Inventario();
    		mensajeRegistro = pagoRcoaBean.getMensaje_Costo_Registro();
    		///////////////////////
    		pagoRcoaBean.setEsPagoEnLinea(false);
    		if (proyectoLicenciaCoa.getAreaResponsable().getTipoEnteAcreditado() != null) {
				if (proyectoLicenciaCoa.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
				} else {
					pagoRcoaBean.setEsPagoEnLinea(true);
				}
			}
    		/////////////////////////////////////////
    		if(resolucionAmbiental.getCostoProyecto() > 0) {
    			calculaCostoTotal();
    		}
    		if(esEmpresaPublica) {
				calculaCostoTotal();
			}
			if (resolucionAmbiental.getId() != null) {
		    		pagoRcoaBean.setCalcularValorPagar(true);
			}
    		pagoRcoaBean.setEsEnteAcreditado(false);
    		if (proyectoLicenciaCoa.getAreaResponsable().getTipoEnteAcreditado() != null) {
				if (proyectoLicenciaCoa.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
					pagoRcoaBean.setInstitucionesFinancieras(institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC());
					mensaje = "El pago correspondiente por Licencia Ambiental, se lo debe realizar por medio de la opción de Pago en Línea del Ministerio del Ambiente, Agua y Transición Ecológica.";
				} else {
					pagoRcoaBean.setEsEnteAcreditado(true);
					pagoRcoaBean.setInstitucionesFinancieras(institucionFinancieraFacade.obtenerPorNombre(proyectoLicenciaCoa.getAreaResponsable().getAreaName()));
					mensaje = "En cumplimiento al acuerdo ministerial 083-B publicado en el registro oficial edición especial Nro. 387 del 4 de Noviembre del 2015. Para continuar con el proceso de obtención del permiso ambiental, "
							+ "usted debe realizar el pago por concepto de Licencia Ambiental en la entidad financiera o lugar de recaudación correspondiente al " + pagoRcoaBean.getInstitucionesFinancieras().get(0).getNombreInstitucion() + " y " 
							+ "con el número de referencia debe completar en la sección Ingreso de Transacciones.";
					
					if (pagoRcoaBean.getInstitucionesFinancieras() == null)
						JsfUtil.addMessageError("No se encontró institución financiara para: "
								+ proyectoLicenciaCoa.getAreaResponsable().getAreaName());
				}
			} else {
				mensaje = "El pago correspondiente por Licencia Ambiental debe ser depositado en entidades bancarias autorizadas a nombre del Ministerio del Ambiente Agua y Transición Ecológica o a través de la opción de Pago en Línea.";
				pagoRcoaBean.setInstitucionesFinancieras(institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC());
			}
    		
    		pagoRcoaBean.setInstitucionesFinancierasCobertura(institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC());
    		pagoRcoaBean.getTransaccionFinancieraCobertura().setInstitucionFinanciera(pagoRcoaBean.getInstitucionesFinancierasCobertura().get(0));

			nutController.cargarTransacciones();
    		if(esCorreccion)
    			pagoRcoaBean.setTransaccionesFinancieras(transaccionFinancieraFacade.cargarTransaccionesRcoaPorProceso(proyectoLicenciaCoa.getId(), bandejaTareasBean.getTarea().getProcessInstanceId()));
			
			if (pagoRcoaBean.getTransaccionesFinancieras().size() > 0 && resolucionAmbiental.getCostoProyecto() > 0)
				cumpleMonto = cumpleMonto();
			
			if (pagoRcoaBean.getTransaccionesFinancieras().isEmpty())
				persist = true;
			
			if(resolucionAmbiental.getCostoProyecto() > 0)
				calcularValorPagar = true;
    		
    		datosGuardados = false;
    		
    		if((pagoRcoaBean.getInstitucionesFinancieras() == null || pagoRcoaBean.getInstitucionesFinancieras().size() == 0) || (pagoRcoaBean.isEsEnteAcreditado() && informacionProyectoEia.getValorPorInventarioForestal() == null && !tienePagoPorRgd))
    			pagoRcoaBean.setGenerarNUT(false);
    		nutController.cerrarMensaje();
    		
    		if (tieneBypassPPC) {
    			resolucionAmbiental.setPagoTotalInspeccion((expedienteBPC.getPagoControlSeguimiento() == null) ? 0
    					: expedienteBPC.getPagoControlSeguimiento());
    		} else {
    			ProyectoFacilitadorPPC proyectoFacilitadorPPC = proyectoFacilitadorPPCFacade.getByIdProyecto(proyectoLicenciaCoa.getId());
    			if((proyectoFacilitadorPPC != null) && (proyectoFacilitadorPPC.getId() != null)) {
    				resolucionAmbiental.setPagoTotalInspeccion(proyectoFacilitadorPPC.getPagoInspeccion());
    			}
    		}		
    		if((resolucionAmbiental.getPagoTotalInspeccion() != null) && (resolucionAmbiental.getPagoTotalInspeccion() > 0.0) && (tienePagoPorRgd.equals(true) || valorPorInventarioForestal != 0))
    		{
    			habilitaPagoIF = true;
    			pagoRcoaBean.setGenerarNUT(true);
    		}
    		else
    		{
    			habilitaPagoIF = false;
    			pagoRcoaBean.setGenerarNUT(false);
    		}    		
    		
    	} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del Emisión.");
		}
	}
    
	public void cargarUbicacionProyecto() {
		ubicacionesSeleccionadas = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoLicenciaCoa);
		data_localizacion = ubicacionesSeleccionadas.get(0);
	}
	
    public void datosIniciales() {
    	try {
    		informacionProyectoEia = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyectoLicenciaCoa);
    		resolucionAmbiental = resolucionAmbientalFacade.getByIdRegistroPreliminar(idRegistroPreliminar);
			Tarifas tarifa = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion());
			Double tasaPagoRGDP = tarifa.getTasasValor();
			
			if (resolucionAmbiental.getId() == null) {
				resolucionAmbiental.setCostoProyecto(0.00);
				resolucionAmbiental.setProyectoLicenciaCoa(proyectoLicenciaCoa);
			}
			
			valorPorInventarioForestal = 0.00;
			Double valorPorRGDP = 0.00;
			tienePagoPorRgd = false;
			if (informacionProyectoEia != null) {
				valorPorInventarioForestal = (informacionProyectoEia.getValorPorInventarioForestal() != null) ? informacionProyectoEia.getValorPorInventarioForestal() : 0.00;
				if(proyectoLicenciaCoa.getGeneraDesechos()
						&& (proyectoLicenciaCoa.getCodigoRgdAsociado() == null 
						|| proyectoLicenciaCoa.getCodigoRgdAsociado().isEmpty())) {
					valorPorRGDP = (proyectoLicenciaCoa.getGeneraDesechos()) ? tasaPagoRGDP : 0.00;
					tienePagoPorRgd = true;
				}
			}
			resolucionAmbiental.setPagoInventarioForestal(valorPorInventarioForestal);
			resolucionAmbiental.setPagoRGDP(valorPorRGDP);
			
			if (tieneBypassPPC) {
				resolucionAmbiental.setPagoTotalInspeccion((expedienteBPC.getPagoControlSeguimiento() == null) ? 0
						: expedienteBPC.getPagoControlSeguimiento());
			} else {
				ProyectoFacilitadorPPC proyectoFacilitadorPPC = proyectoFacilitadorPPCFacade.getByIdProyecto(proyectoLicenciaCoa.getId());
				if((proyectoFacilitadorPPC != null) && (proyectoFacilitadorPPC.getId() != null)) {
					resolucionAmbiental.setPagoTotalInspeccion(proyectoFacilitadorPPC.getPagoInspeccion());
				}
			}			
			
			// 3 - 500     4 - 1000 ---categoria licencia de bajo 3 y alto 4 impacto ambiental
			Tarifas tarifaCostoProyecto = new Tarifas();
			if (proyectoLicenciaCoa.getCategorizacion()==3) {
				tarifaCostoProyecto = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.EMISION_LICENCIA_PAGO_MINIMO_BAJO_IMPACTO.getDescripcion());
			} else {
				tarifaCostoProyecto = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.EMISION_LICENCIA_PAGO_MINIMO_ALTO_IMPACTO.getDescripcion());
			}
			
    		Double costoMinimo = tarifaCostoProyecto.getTasasValor();
    		Boolean excepcionPago = Constantes.getPropertyAsBoolean("rcoa.existe.acuerdo.excepcionPagoResolucion");
    		calcularUnoPorMil = true;
    		esEmpresaPublica = false;
    		esOrganizacion = false;
    		if(organizacion != null && organizacion.getId() != null) {
    			esOrganizacion = true;
    		}
    		//no se realiza calculo del 1x1000 para empresas publicas y mixtas si existe acuerdo para excepcion 
        	if(excepcionPago && organizacion != null && organizacion.getId() != null) {
        		if(organizacion.getTipoOrganizacion() != null 
	    				&& (organizacion.getTipoOrganizacion().getTipoEmpresa().equals(TipoOrganizacion.publica)
	        				|| (organizacion.getTipoOrganizacion().getTipoEmpresa().equals(TipoOrganizacion.mixta)
	        					&& organizacion.getParticipacionEstado().equalsIgnoreCase(CombosController.IGUAL_MAS_PARTICIPACION)))) {
        			costoMinimo = 0.00;
        			calcularUnoPorMil = false;
        			esEmpresaPublica = true;
        		}
        	}
    		
    		resolucionAmbiental.setPagoMinimoProyecto(costoMinimo);
    		resolucionAmbiental.setSubeFacturaPermiso(false);
    		resolucionAmbiental.setSubeDocumentoPago(false);
    		resolucionAmbiental.setSubeCostoImplementacion(false);
    		resolucionAmbiental.setSubeJustificacionCosto(false);
    		resolucionAmbiental.setSubeCronogramaPMA(false);
    		
			cargarDocumentos();
			pagoRcoaBean.setCumpleMontoProyecto(false);
			pagoRcoaBean.setCumpleMontoCobertura(false);
			pagoRcoaBean.setCumpleMontoRGD(false);
			
		} catch (ServiceException e1) {
			LOG.error(e1, e1);
	    	JsfUtil.addMessageError("Ocurrió un error al consultar los datos.");
	    } catch (CmisAlfrescoException e) {
	    	LOG.error(e, e);
	    	JsfUtil.addMessageError("Ocurrió un error al recuperar los documentos.");
	    }
    	
    }
    
    public void calculaCostoTotal() throws ServiceException {
    	Double montoTotalProyecto = 0.00;
    	Double pagoMinimoProyecto = 0.00;
    	Double costoProyecto = resolucionAmbiental.getCostoProyecto();
    	Double valorInspeccion = (resolucionAmbiental.getPagoTotalInspeccion() == null) ? 0 : resolucionAmbiental.getPagoTotalInspeccion();
    	
    	BigDecimal costoPorMil = BigDecimal.valueOf(costoProyecto / 1000).setScale(2, RoundingMode.HALF_UP);
    	pagoMinimoProyecto = costoPorMil.doubleValue();
    
		if (calcularUnoPorMil) {
			Tarifas tarifaCostoProyecto = new Tarifas();
			if (proyectoLicenciaCoa.getCategorizacion()==3) {
				tarifaCostoProyecto = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.EMISION_LICENCIA_PAGO_MINIMO_BAJO_IMPACTO.getDescripcion());
			} else {
				tarifaCostoProyecto = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.EMISION_LICENCIA_PAGO_MINIMO_ALTO_IMPACTO.getDescripcion());
			}
    		Double costoMinimo = tarifaCostoProyecto.getTasasValor();
		if(proyectoLicenciaCoa.getCategorizacion() == 3) {
	    		if (pagoMinimoProyecto < costoMinimo) {
	        		pagoMinimoProyecto = costoMinimo;
	    		}
	    	} else {
	    		if (pagoMinimoProyecto < costoMinimo) {
	        		pagoMinimoProyecto = costoMinimo;
	    		}
	    	}
    	} else {
    		pagoMinimoProyecto = 0.00;
    	}
    	
    	resolucionAmbiental.setPagoMinimoProyecto(pagoMinimoProyecto);
    	montoTotalProyecto = valorInspeccion + resolucionAmbiental.getPagoMinimoProyecto();
    	resolucionAmbiental.setMontoTotalProyecto(montoTotalProyecto);
    	pagoRcoaBean.setMontoTotalProyecto(montoTotalProyecto);
    	montoTotalLicencia = pagoRcoaBean.getMontoTotalProyecto();
	    	///////
			pagoRcoaBean.setValproy("0");
			pagoRcoaBean.setVallicamb("0");
			pagoRcoaBean.setValproy(costoProyecto.toString());
			pagoRcoaBean.setVallicamb(montoTotalProyecto.toString());
    	pagoRcoaBean.setMontoTotalCoberturaVegetal(resolucionAmbiental.getPagoInventarioForestal());
    	montoTotalInventario = pagoRcoaBean.getMontoTotalCoberturaVegetal();
    	
    	pagoRcoaBean.setMontoTotalRGD(resolucionAmbiental.getPagoRGDP());
    	montoTotalRgd = pagoRcoaBean.getMontoTotalRGD();
    	
    	calcularValorPagar = true;
    	
    	for (TransaccionFinanciera transa : pagoRcoaBean.getTransaccionesFinancieras()) {
			switch (transa.getTipoPago()) {
			case 1:
				transaccionesFinancierasEliminar.add(transa);
				break;
			default:
				break;
			}
		}
	if(transaccionesFinancierasEliminar.size() > 0) {
		List<TransaccionFinanciera> eliminarAux = new ArrayList<TransaccionFinanciera>();
		for (TransaccionFinanciera transaccion : transaccionesFinancierasEliminar) {
			pagoRcoaBean.getTransaccionesFinancieras().remove(transaccion);
			if(transaccion.getId() != null) {
				eliminarAux.add(transaccion);
				transaccionFinancieraFacade.revertirPago(tramite,
					transaccion.getNumeroTransaccion(), JsfUtil.getSenderIp(),
					transaccion.getInstitucionFinanciera().getCodigoInstitucion(),
					transaccion.getMontoPago());
			}
		}
    		if(eliminarAux.size() > 0)
    			transaccionFinancieraFacade.eliminarTransacciones(eliminarAux);
    		
    		transaccionesFinancierasEliminar = new ArrayList<>();
    	}
    	
    	cumpleMonto = cumpleMonto();
    	datosGuardados = false; // para bloquear el boton enviar
	pagoRcoaBean.setCalcularValorPagar(false);
        	if (!pagoRcoaBean.getEsPagoEnLinea())
        	{
        		//pagoRcoaBean.setValorAPagar(costoProyecto + pagoRcoaBean.getMontoTotalCoberturaVegetal()+pagoRcoaBean.getMontoTotalProyecto()+pagoRcoaBean.getMontoTotalRGD());
        		pagoRcoaBean.setValorAPagar(pagoRcoaBean.getMontoTotalCoberturaVegetal()+pagoRcoaBean.getMontoTotalProyecto()+pagoRcoaBean.getMontoTotalRGD());
        	}else
        	{
        		pagoRcoaBean.setValorAPagar(pagoRcoaBean.getMontoTotalCoberturaVegetal()+pagoRcoaBean.getMontoTotalRGD());
        	}

    }
    
    public void editarValorProyecto() {
    	calcularValorPagar = false;
		pagoRcoaBean.setCalcularValorPagar(false);
    }
    
    public void agregarComprobante(Integer tipoPago) {
    	try {
    		transaccionFinancieraSelected.setInstitucionFinanciera(institucionFinanciera);
    		transaccionFinancieraSelected.setTipoPago(tipoPago);
    		
    		if (existeTransaccion(transaccionFinancieraSelected)) {
				JsfUtil.addMessageInfo("El número de comprobante: "
						+ transaccionFinancieraSelected.getNumeroTransaccion()
						+ " ("+ transaccionFinancieraSelected.getInstitucionFinanciera().getNombreInstitucion()
						+ ") ya fue registrado por usted. Ingrese otro distinto.");
				transaccionFinancieraSelected = new TransaccionFinanciera();
				return;
			} else {
				double monto = transaccionFinancieraFacade.consultarSaldo(transaccionFinancieraSelected.getInstitucionFinanciera().getCodigoInstitucion(), transaccionFinancieraSelected.getNumeroTransaccion());
				if (monto == 0) {
					JsfUtil.addMessageError("El número de comprobante: "+transaccionFinancieraSelected.getNumeroTransaccion()+" ("+ transaccionFinancieraSelected.getInstitucionFinanciera().getNombreInstitucion()+ ") no ha sido registrado.");
					transaccionFinancieraSelected = new TransaccionFinanciera();
					return;
				} else {
					Boolean validarCuenta = true;
					Double montoPago = 0.00;
					String tipoCuenta = "";
					String nroCuentaTransaccion = transaccionFinancieraFacade.getCuentaTransaccion(transaccionFinancieraSelected.getInstitucionFinanciera().getCodigoInstitucion(), transaccionFinancieraSelected.getNumeroTransaccion());
					switch (tipoPago) {
					case 1:
						montoPago = montoTotalLicencia;
						tipoCuenta = "code_sgca";
						validarCuenta = (proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_EA)) ? false : true;
						break;
					case 2:
						montoPago = montoTotalInventario;
						tipoCuenta = "code_sf";
						break;
					case 3:
						montoPago = montoTotalRgd;
						tipoCuenta = "code_sgca";
						break;
					default:
						break;
					}
					
					Cuentas cuentaPago = cuentaFacade.buscarPorCodigo(tipoCuenta);
					if(validarCuenta && cuentaPago.getCuentaNumero() != null && !cuentaPago.getCuentaNumero().equals(nroCuentaTransaccion)) {
						JsfUtil.addMessageInfo("El número de comprobante: "
								+ transaccionFinancieraSelected.getNumeroTransaccion()
								+ " no corresponde a la cuenta requerida " + cuentaPago.getCuentaNumero());
						transaccionFinancieraSelected = new TransaccionFinanciera();
						return;
					} else {
					
						Double montoUtilizadoTransaccion = getValorUtilizadoPorTransaccion(transaccionFinancieraSelected);
						monto = monto - montoUtilizadoTransaccion;
						
						if (monto <= 0) {
							JsfUtil.addMessageError("El número de comprobante: "+transaccionFinancieraSelected.getNumeroTransaccion()+" ("+ transaccionFinancieraSelected.getInstitucionFinanciera().getNombreInstitucion()+ ") no tiene saldo disponible.");
							transaccionFinancieraSelected = new TransaccionFinanciera();
							return;
						} else {
							Double montoPagado = getValorPagadoPorTipoPago(transaccionFinancieraSelected);
							Double resto = montoPago - montoPagado; // resto el valor total del pago - el valor ya cancelado 
							BigDecimal montoPagoAux = BigDecimal.valueOf(resto).setScale(2, RoundingMode.HALF_UP); 
							montoPago = montoPagoAux.doubleValue();
							
							if(montoPago > monto)
								montoPago = monto;
							
							transaccionFinancieraSelected.setMontoTransaccion(monto);
							transaccionFinancieraSelected.setIdentificadorMotivo(null);
							transaccionFinancieraSelected.setProyectoRcoa(proyectoLicenciaCoa);
							transaccionFinancieraSelected.setMontoPago(montoPago);
							
							pagoRcoaBean.getTransaccionesFinancieras().add(transaccionFinancieraSelected);
							cumpleMonto = cumpleMonto();
							transaccionFinancieraSelected = new TransaccionFinanciera();
							
							persist = true;
						}
					}
				}
			}
    	} catch (Exception e) {
			JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
			return;
		}
    }
    
    public void eliminarComprobante(TransaccionFinanciera item) {
    	pagoRcoaBean.getTransaccionesFinancieras().remove(item);
    	cumpleMonto = cumpleMonto();
		persist = true;
		
		if(item.getId() != null) {
			transaccionesFinancierasEliminar.add(item);
			transaccionFinancieraFacade.eliminarTransacciones(transaccionesFinancierasEliminar);
			
			for (TransaccionFinanciera transaccion : transaccionesFinancierasEliminar) {
				transaccionFinancieraFacade.revertirPago(tramite,
						transaccion.getNumeroTransaccion(), JsfUtil.getSenderIp(), 
						transaccion.getInstitucionFinanciera().getCodigoInstitucion(),
						transaccion.getMontoPago());
			}
			
			transaccionesFinancierasEliminar = new ArrayList<>();
		}
    }
    
    private boolean existeTransaccion(TransaccionFinanciera _transaccionFinanciera) {
		for (TransaccionFinanciera transaccion : pagoRcoaBean.getTransaccionesFinancieras()) {
			if (transaccion.getTipoPago().equals(_transaccionFinanciera.getTipoPago())
					&& transaccion.getNumeroTransaccion().trim().equals(_transaccionFinanciera.getNumeroTransaccion())
					&& transaccion.getInstitucionFinanciera().getCodigoInstitucion().trim()
							.equals(_transaccionFinanciera.getInstitucionFinanciera().getCodigoInstitucion())) {
				return true;
			}
		}
		return false;
	}
    
    private Double getValorUtilizadoPorTransaccion(TransaccionFinanciera _transaccionFinanciera) {
    	Double montoPagado = 0.00;
		for (TransaccionFinanciera transaccion : pagoRcoaBean.getTransaccionesFinancieras()) {
			if (transaccion.getNumeroTransaccion().trim().equals(_transaccionFinanciera.getNumeroTransaccion())
					&& transaccion.getInstitucionFinanciera().getCodigoInstitucion().trim()
							.equals(_transaccionFinanciera.getInstitucionFinanciera().getCodigoInstitucion())) {
				montoPagado += transaccion.getMontoPago();
			}
		}
		return montoPagado;
	}
    
    private Double getValorPagadoPorTipoPago(TransaccionFinanciera _transaccionFinanciera) {
    	Double montoPagado = 0.00;
		for (TransaccionFinanciera transaccion : pagoRcoaBean.getTransaccionesFinancieras()) {
			if (transaccion.getTipoPago().equals(_transaccionFinanciera.getTipoPago())
					&& transaccion.getInstitucionFinanciera().getCodigoInstitucion().trim()
							.equals(_transaccionFinanciera.getInstitucionFinanciera().getCodigoInstitucion())) {
				montoPagado += transaccion.getMontoPago();
			}
		}
		return montoPagado;
	}
    
    private boolean cumpleMonto() {
		double montoTotalLicencia = 0;
		double montoTotalInventario = 0;
		double montoTotalRgd = 0;
		for (TransaccionFinanciera transa : pagoRcoaBean.getTransaccionesFinancieras()) {
			if(transa.getTipoPago()==null) {// Verificar xq retorna null
				transa.setTipoPago(1);
			}
			switch (transa.getTipoPago()) {
			case 1:
				montoTotalLicencia += transa.getMontoTransaccion();
				break;
			case 2:
				montoTotalInventario += transa.getMontoTransaccion();
				break;
			case 3:
				montoTotalRgd += transa.getMontoTransaccion();
				break;
			default:
				break;
			}
		}
		

		DecimalFormat decimalValorTramite = new DecimalFormat(".##");
		
		this.montoTotalLicencia = Double.valueOf(decimalValorTramite.format(pagoRcoaBean.getMontoTotalProyecto()).replace(",", "."));
		if (montoTotalLicencia >= this.montoTotalLicencia) {
			pagoRcoaBean.setCumpleMontoProyecto(true);
			pagoRcoaBean.setCumpleValorRegistro(true);
		} else {
			pagoRcoaBean.setCumpleMontoProyecto(false);
			pagoRcoaBean.setCumpleValorRegistro(false);
		}
		
//		if (PagoOnline)
//		{
//			return true;
//		}
//		else
//		{
			if(informacionProyectoEia.getValorPorInventarioForestal() != null) {
				this.montoTotalInventario = Double.valueOf(decimalValorTramite.format(pagoRcoaBean.getMontoTotalCoberturaVegetal()).replace(",", "."));
				if (montoTotalInventario >= this.montoTotalInventario) {
					pagoRcoaBean.setCumpleMontoCobertura(true);
				} else {
					pagoRcoaBean.setCumpleMontoCobertura(false);
				}
			} else
				pagoRcoaBean.setCumpleMontoCobertura(true);
			
			if(tienePagoPorRgd) {
				this.montoTotalRgd = Double.valueOf(decimalValorTramite.format(pagoRcoaBean.getMontoTotalRGD()).replace(",", "."));
				if (montoTotalRgd >= this.montoTotalRgd) {
					pagoRcoaBean.setCumpleMontoRGD(true);
				} else {
					pagoRcoaBean.setCumpleMontoRGD(false);
				}
			} else
				pagoRcoaBean.setCumpleMontoRGD(true);
			
			return pagoRcoaBean.isCumpleMontoProyecto() && pagoRcoaBean.isCumpleMontoCobertura() && pagoRcoaBean.isCumpleMontoRGD();
		//}

	}

    public void asignarFacturaPermisoAmbiental(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	facturaPermisoAmbiental = new DocumentoResolucionAmbiental();
    	facturaPermisoAmbiental.setNombre(file.getFile().getFileName());
    	facturaPermisoAmbiental.setMime(file.getFile().getContentType());
    	facturaPermisoAmbiental.setContenidoDocumento(file.getFile().getContents());
    	facturaPermisoAmbiental.setExtension(extension);
    	facturaPermisoAmbiental.setNombreTabla(ManifiestoUnico.class.getSimpleName());
    	resolucionAmbiental.setSubeFacturaPermiso(true);
    }
    public void asignarPolizaCostoImplementacion(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	polizaCostoImplementacion = new DocumentoResolucionAmbiental();
    	polizaCostoImplementacion.setNombre(file.getFile().getFileName());
    	polizaCostoImplementacion.setMime(file.getFile().getContentType());
    	polizaCostoImplementacion.setContenidoDocumento(file.getFile().getContents());
    	polizaCostoImplementacion.setExtension(extension);
    	polizaCostoImplementacion.setNombreTabla(ManifiestoUnico.class.getSimpleName());
    	resolucionAmbiental.setSubeCostoImplementacion(true);
    }
    public void asignarCronogramaValorado(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	cronogramaValorado = new DocumentoResolucionAmbiental();
    	cronogramaValorado.setNombre(file.getFile().getFileName());
    	cronogramaValorado.setMime(file.getFile().getContentType());
    	cronogramaValorado.setContenidoDocumento(file.getFile().getContents());
    	cronogramaValorado.setExtension(extension);
    	cronogramaValorado.setNombreTabla(ManifiestoUnico.class.getSimpleName());
    	resolucionAmbiental.setSubeCronogramaPMA(true);
    }
    public void asignarPagoEmisionPermiso(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	pagoEmisionPermiso = new DocumentoResolucionAmbiental();
    	pagoEmisionPermiso.setNombre(file.getFile().getFileName());
    	pagoEmisionPermiso.setMime(file.getFile().getContentType());
    	pagoEmisionPermiso.setContenidoDocumento(file.getFile().getContents());
    	pagoEmisionPermiso.setExtension(extension);
    	pagoEmisionPermiso.setNombreTabla(ManifiestoUnico.class.getSimpleName());
    	resolucionAmbiental.setSubeDocumentoPago(true);
    }
    public void asignarJustificacionCostoMedidas(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	justificacionCostoMedidas = new DocumentoResolucionAmbiental();
    	justificacionCostoMedidas.setNombre(file.getFile().getFileName());
    	justificacionCostoMedidas.setMime(file.getFile().getContentType());
    	justificacionCostoMedidas.setContenidoDocumento(file.getFile().getContents());
    	justificacionCostoMedidas.setExtension(extension);
    	justificacionCostoMedidas.setNombreTabla(ManifiestoUnico.class.getSimpleName());
    	resolucionAmbiental.setSubeJustificacionCosto(true);
    }
    
    public void cargarDocumentos() throws CmisAlfrescoException{
    	// 1. Factura por permiso ambiental
    	facturaPermisoAmbiental = documentoResolucionAmbientalFacade.getDocumentoByResolucionAmbiental(resolucionAmbiental.getId(), TipoDocumentoSistema.EMISION_LICENCIA_FACTURA_PERMISO_AMBIENTAL);
		if (facturaPermisoAmbiental.getId() == null) {
			facturaPermisoAmbiental = new DocumentoResolucionAmbiental();
		} else {
			resolucionAmbiental.setSubeFacturaPermiso(true);
		}

		// 2. Protocolarización del pago por permiso ambiental 
		pagoEmisionPermiso = documentoResolucionAmbientalFacade.getDocumentoByResolucionAmbiental(resolucionAmbiental.getId(), TipoDocumentoSistema.EMISION_LICENCIA_PAGO_PERMISO_AMBIENTAL);
		if(pagoEmisionPermiso.getId() == null){
			pagoEmisionPermiso = new DocumentoResolucionAmbiental();
		} else {
			resolucionAmbiental.setSubeDocumentoPago(true);
		}

		// 3. Póliza o garantía bancaria por el 100% del costo de implementación del PMA
		polizaCostoImplementacion = documentoResolucionAmbientalFacade.getDocumentoByResolucionAmbiental(resolucionAmbiental.getId(), TipoDocumentoSistema.EMISION_LICENCIA_POLIZA_PMA);
		if(polizaCostoImplementacion.getId() == null){
			polizaCostoImplementacion = new DocumentoResolucionAmbiental();
		} else {
			resolucionAmbiental.setSubeCostoImplementacion(true);
		}
		
		// 4. Justificación del costo dentro de las medidas incluidas del PMA
		justificacionCostoMedidas = documentoResolucionAmbientalFacade.getDocumentoByResolucionAmbiental(resolucionAmbiental.getId(), TipoDocumentoSistema.EMISION_LICENCIA_MEDIDAS_PMA);
		if(justificacionCostoMedidas.getId() == null){
			justificacionCostoMedidas = new DocumentoResolucionAmbiental();
		} else {
			resolucionAmbiental.setSubeJustificacionCosto(true);
		}

		// 5. Cronograma valorado del PMA
		cronogramaValorado = documentoResolucionAmbientalFacade.getDocumentoByResolucionAmbiental(resolucionAmbiental.getId(), TipoDocumentoSistema.EMISION_LICENCIA_CRONOGRAMA_PMA);
		if(cronogramaValorado.getId() == null){
			cronogramaValorado = new DocumentoResolucionAmbiental();
		} else {
			resolucionAmbiental.setSubeCronogramaPMA(true);
		}
	}
    
    public Boolean validarDatos() {
    	boolean validar = true;
    	cumpleMonto = cumpleMonto();
    	if (((PagoKushkiJsonControl != null) && (PagoKushkiJsonControl.size() > 0)) && (!pagoRcoaBean.getEsPagoEnLinea()))
    	{
    		cumpleMonto = true;
    	}
    	
    	
    	if (!esEmpresaPublica && resolucionAmbiental.getCostoProyecto() <= 0) {
    		JsfUtil.addMessageError("El campo 'Valor del proyecto' es requerido");
    		validar = false;
		}
    	
    	if (!pagoRcoaBean.getControl_salto()) {
	    	if (pagoRcoaBean.getTransaccionesFinancieras() == null || pagoRcoaBean.getTransaccionesFinancieras().size() == 0) {
	    		JsfUtil.addMessageError("Debe ingresar el número de comprobante de pago");
	    		validar = false;
    		}
    	}
    	
    	if (pagoRcoaBean.getTransaccionesFinancieras() != null && pagoRcoaBean.getTransaccionesFinancieras().size() > 0 && !cumpleMonto) {
    		JsfUtil.addMessageError("Los comprobantes ingresados no cumplen el valor a pagar");
    		validar = false;
		}
    	
    	if (!esEmpresaPublica) {
    		if (!resolucionAmbiental.getSubeFacturaPermiso()) {
        		JsfUtil.addMessageError("1. Factura por permiso ambiental");
        		validar = false;
    		}
        	if (!resolucionAmbiental.getSubeDocumentoPago()) {
        		JsfUtil.addMessageError("2. Protocolización del pago por emisión de Autorización Administrativa Ambiental");
        		validar = false;
        	}
        	if (!resolucionAmbiental.getSubeCostoImplementacion()) {
        		JsfUtil.addMessageError("3. Póliza o garantía bancaria por el 100% del costo de implementación del PMA");
        		validar = false;
    		}
        	if (!resolucionAmbiental.getSubeJustificacionCosto()) {
        		JsfUtil.addMessageError("4. Justificación del costo de las medidas incluidas dentro del PMA");
        		validar = false;
        	}
        	if (!resolucionAmbiental.getSubeCronogramaPMA()) {
        		JsfUtil.addMessageError("5. Cronograma valorado del PMA");
        		validar = false;
    		}
        	if (resolucionAmbiental.getNumeroPoliza() == null) {
        		JsfUtil.addMessageError("El campo 'Número de póliza' es requerido");
        		validar = false;
    		}
        	if (resolucionAmbiental.getCostoImplementacion() == null) {
        		JsfUtil.addMessageError("El campo 'Costo de implementación del PMA' es requerido");
        		validar = false;
    		}
        	if (resolucionAmbiental.getFechaInicioVigenciaPoliza() == null) {
        		JsfUtil.addMessageError("El campo 'Vigencia de póliza Desde' es requerido");
        		validar = false;
    		}
        	if (resolucionAmbiental.getFechaFinVigenciaPoliza() == null) {
        		JsfUtil.addMessageError("El campo 'Vigencia de póliza Hasta' es requerido");
        		validar = false;
    		}
    	}
    	
    	if (!calcularValorPagar) {
    		JsfUtil.addMessageError("Realizar cálculo del valor total");
    		validar = false;
		}

    	
    	return validar;
    }
       
    public void guardarDatos() throws Exception {
    	resolucionAmbiental = resolucionAmbientalFacade.guardar(resolucionAmbiental);
		
		if (facturaPermisoAmbiental.getId() == null && facturaPermisoAmbiental.getContenidoDocumento() != null) {
			facturaPermisoAmbiental.setResolucionAmbiental(resolucionAmbiental);
			facturaPermisoAmbiental.setIdTabla(resolucionAmbiental.getId());
			facturaPermisoAmbiental.setNombreTabla(ResolucionAmbiental.class.getSimpleName());
			facturaPermisoAmbiental.setDescripcionTabla("Factura del Permiso Ambiental");
			facturaPermisoAmbiental.setIdProceso(bandejaTareasBean.getProcessId());
			facturaPermisoAmbiental = documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "EMISION_LICENCIA", bandejaTareasBean.getProcessId(), facturaPermisoAmbiental, TipoDocumentoSistema.EMISION_LICENCIA_FACTURA_PERMISO_AMBIENTAL);
		}
		
		if (polizaCostoImplementacion.getId() == null && polizaCostoImplementacion.getContenidoDocumento() != null) {
			polizaCostoImplementacion.setResolucionAmbiental(resolucionAmbiental);
			polizaCostoImplementacion.setIdTabla(resolucionAmbiental.getId());
			polizaCostoImplementacion.setNombreTabla(ResolucionAmbiental.class.getSimpleName());
			polizaCostoImplementacion.setDescripcionTabla("Poliza costo de implementacion PMA");
			polizaCostoImplementacion.setIdProceso(bandejaTareasBean.getProcessId());
			polizaCostoImplementacion = documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "EMISION_LICENCIA", bandejaTareasBean.getProcessId(), polizaCostoImplementacion, TipoDocumentoSistema.EMISION_LICENCIA_POLIZA_PMA);
		}
		
		if (cronogramaValorado.getId() == null && cronogramaValorado.getContenidoDocumento() != null) {
			cronogramaValorado.setResolucionAmbiental(resolucionAmbiental);
			cronogramaValorado.setIdTabla(resolucionAmbiental.getId());
			cronogramaValorado.setNombreTabla(ResolucionAmbiental.class.getSimpleName());
			cronogramaValorado.setDescripcionTabla("Cronograma valorado del PMA");
			cronogramaValorado.setIdProceso(bandejaTareasBean.getProcessId());
			cronogramaValorado = documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "EMISION_LICENCIA", bandejaTareasBean.getProcessId(), cronogramaValorado, TipoDocumentoSistema.EMISION_LICENCIA_CRONOGRAMA_PMA);
		}
		
		if (pagoEmisionPermiso.getId() == null && pagoEmisionPermiso.getContenidoDocumento() != null) {
			pagoEmisionPermiso.setResolucionAmbiental(resolucionAmbiental);
			pagoEmisionPermiso.setIdTabla(resolucionAmbiental.getId());
			pagoEmisionPermiso.setNombreTabla(ResolucionAmbiental.class.getSimpleName());
			pagoEmisionPermiso.setDescripcionTabla("Pago por emision del permiso ambiental");
			pagoEmisionPermiso.setIdProceso(bandejaTareasBean.getProcessId());
			pagoEmisionPermiso = documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "EMISION_LICENCIA", bandejaTareasBean.getProcessId(), pagoEmisionPermiso, TipoDocumentoSistema.EMISION_LICENCIA_PAGO_PERMISO_AMBIENTAL);
		}
		
		if (justificacionCostoMedidas.getId() == null && justificacionCostoMedidas.getContenidoDocumento() != null) {
			justificacionCostoMedidas.setResolucionAmbiental(resolucionAmbiental);
			justificacionCostoMedidas.setIdTabla(resolucionAmbiental.getId());
			justificacionCostoMedidas.setNombreTabla(ResolucionAmbiental.class.getSimpleName());
			justificacionCostoMedidas.setDescripcionTabla("Justificacion del costo PMA");
			justificacionCostoMedidas.setIdProceso(bandejaTareasBean.getProcessId());
			justificacionCostoMedidas = documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "EMISION_LICENCIA", bandejaTareasBean.getProcessId(), justificacionCostoMedidas, TipoDocumentoSistema.EMISION_LICENCIA_MEDIDAS_PMA);
		}
		

		Boolean pagoSatisfactorio = false;
		persist = true;
		if (persist) {
			if (pagoRcoaBean.getTransaccionesFinancieras().size() > 0) {
				
				List<TransaccionFinanciera> transaccionesFinancierasGuardar = new ArrayList<TransaccionFinanciera>();
				
				for (TransaccionFinanciera tf : pagoRcoaBean.getTransaccionesFinancieras()) {
					if(tf.getId() == null)
						transaccionesFinancierasGuardar.add(tf);
				}
				
				if (transaccionesFinancierasGuardar.size() > 0) {
				
					String motivo = proyectoLicenciaCoa.getCodigoUnicoAmbiental();
					
					List<TransaccionFinanciera> transaccionesLicencia = new ArrayList<>();
					List<TransaccionFinanciera> transaccionesInventario = new ArrayList<>();
					List<TransaccionFinanciera> transaccionesRgd = new ArrayList<>();
					
					Double totalLicencia = 0.0, totalInventario =0.0, totalRgd = 0.0;
					
					for (TransaccionFinanciera tf : transaccionesFinancierasGuardar) {
						switch (tf.getTipoPago()) {
						case 1:
							transaccionesLicencia.add(tf);
							totalLicencia += tf.getMontoPago();
							break;
						case 2:
							transaccionesInventario.add(tf);
							totalInventario += tf.getMontoPago();
							break;
						case 3:
							transaccionesRgd.add(tf);
							totalRgd += tf.getMontoPago();
							break;
						default:
							break;
						}
					}
					
					if(transaccionesLicencia.size() >0) {
						pagoSatisfactorio = transaccionFinancieraFacade
								.realizarPago(totalLicencia,
										transaccionesLicencia, motivo);
					}
					
					if(transaccionesInventario.size() >0) {
						pagoSatisfactorio = transaccionFinancieraFacade
								.realizarPago(totalInventario,
										transaccionesInventario, motivo);
					}
					
					if(transaccionesRgd.size() >0) {
						pagoSatisfactorio = transaccionFinancieraFacade
								.realizarPago(totalRgd,
										transaccionesRgd, motivo);
					}
					
					if (pagoSatisfactorio) {
						transaccionFinancieraFacade.guardarTransacciones(
								transaccionesFinancierasGuardar, bandejaTareasBean.getTarea().getTaskId(), 
								bandejaTareasBean.getTarea().getTaskName(), 
								bandejaTareasBean.getTarea().getProcessInstanceId(),
								bandejaTareasBean.getTarea().getProcessId(),
								bandejaTareasBean.getTarea().getProcessName());
					}
				}
			}
		}
    }
        
    private boolean cumpleMontoRevalidacion() {
    	double montoTotalLicencia = 0;
		double montoTotalInventario = 0;
		double montoTotalRgd = 0;
		for (TransaccionFinanciera transa : pagoRcoaBean.getTransaccionesFinancieras()) {
			if(transa.getTipoPago()==null) {
				transa.setTipoPago(1);
			}
			switch (transa.getTipoPago()) {
			case 1:
				montoTotalLicencia += transa.getMontoTransaccion();
				break;
			case 2:
				montoTotalInventario += transa.getMontoTransaccion();
				break;
			case 3:
				montoTotalRgd += transa.getMontoTransaccion();
				break;
			default:
				break;
			}
		}

        return (montoTotalLicencia >= pagoRcoaBean.getMontoTotalProyecto()) &&
               (informacionProyectoEia.getValorPorInventarioForestal() == null || 
                montoTotalInventario >= pagoRcoaBean.getMontoTotalCoberturaVegetal()) &&
               (!tienePagoPorRgd || montoTotalRgd >= pagoRcoaBean.getMontoTotalRGD());
    }
    
    public void guardar() {
    	try {
    		guardarDatos();
    		
    		cumpleMontoRevalidacion = cumpleMontoRevalidacion();
    		
    		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
			datosGuardados = true;
			
    		if (habilitaPagoIF || cumpleMontoRevalidacion)
    		{
    			datosGuardados = true;	
    		}
    		else
    		{
    			datosGuardados = false;	
    		}
    		
	    	pagoRcoaBean.setCalcularValorPagar(true);
		} catch (Exception e) {
			datosGuardados = false;
			JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
			LOG.error(e, e);
		}
    }
    
    private Usuario asignarTecnicoFinanciero() {
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
    	if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT)){
    		areaTramite = areaTramite.getArea();
    	} else if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)){
    		areaTramite = areaFacade.getAreaSiglas(Constantes.SIGLAS_DIRECCION_FINANCIERA);
    	}
    	
    	String rolPrefijo;
		String rolTecnico;
		rolPrefijo = "role.resolucion.tecnico.financiero";
    	rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
    	List<Usuario> listaUsuariosCargaLaboral = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, areaTramite.getAreaName());

        if (listaUsuariosCargaLaboral == null || listaUsuariosCargaLaboral.isEmpty()){
            LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
            return null;
        } 

        Usuario tecnicoResponsable = null;
        
        // recuperar tecnico de bpm y validar si el usuario existe en el listado anterior       
        Usuario usuarioTecnico = usuarioFacade.buscarUsuario(tecnicoFinanciero);
        if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
            if (listaUsuariosCargaLaboral != null && listaUsuariosCargaLaboral.size() >= 0
                    && listaUsuariosCargaLaboral.contains(usuarioTecnico)) {
                tecnicoResponsable = usuarioTecnico;
            }
        }
        
        // si no se encontró el usuario se realiza la busqueda de uno nuevo y se actualiza en el bpm
        if (tecnicoResponsable == null) {
            tecnicoResponsable = listaUsuariosCargaLaboral.get(0);
        }

        return tecnicoResponsable;
    }
    
    public void enviar() {
    	datosGuardados = validarDatos();
    	if (datosGuardados) {
    		try {
    			guardarDatos();
    			
    			Map<String, Object> params=new HashMap<>();
                Usuario tecnico = asignarTecnicoFinanciero();

                if (tecnico == null){
                    JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
                    JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
                    return;
                } else if(tecnicoFinanciero == null || !tecnicoFinanciero.equals(tecnico.getNombre())) {
                    params.put("tecnicoFinanciero", tecnico.getNombre());
                    procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
                }
    			
    			
    			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
    			
    			enviarNotificacion(tecnico);
    			
    			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
    	        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
    		} catch (Exception e) {
    			datosGuardados = false;
    			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
    			e.printStackTrace();
    		}
		} 
    }
    
    public void validateValorProyecto(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if (!calcularValorPagar) {
    		errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe realizar el cálculo del valor total", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
    
    public void abrirFinalizar() {
    	RequestContext.getCurrentInstance().execute("PF('dlgConfirmar').show();");
    }
    
    public String getConceptoPago(Integer tipoPago) {
    	String tipoConceptoPago = "";
	
		switch (tipoPago) {
		case 1:
			tipoConceptoPago = "Valor Total por Licencia Ambiental";
			break;
		case 2:
			tipoConceptoPago = "Pago por Inventario Forestal";
			break;
		case 3:
			tipoConceptoPago = "Pago por RGD";
			break;
		default:
			break;
		}
		if(pagoRcoaBean.isEsEnteAcreditado() && informacionProyectoEia.getValorPorInventarioForestal() != null && proyectoLicenciaCoa.getGeneraDesechos())
			tipoConceptoPago = "Pago por Inventario Forestal y RGD";
		return tipoConceptoPago;
    }
    
    public void enviarNotificacion(Usuario tecnicoFinanciero) throws ServiceException {
    	String nombreTecnico = tecnicoFinanciero.getPersona().getNombre();
		String nombreOperador = "";
		Usuario usuarioOperador = proyectoLicenciaCoa.getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		Object[] parametrosCorreoNuevo = new Object[] {nombreTecnico, nombreOperador,  proyectoLicenciaCoa.getCodigoUnicoAmbiental(),  
				proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getAreaResponsable().getAreaName()};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionRegistroValoresResolucionAmbiental");
		String notificacionNuevo = String.format(mensajeNotificacion.getValor(), parametrosCorreoNuevo);
		
		Email.sendEmail(tecnicoFinanciero, mensajeNotificacion.getAsunto(), notificacionNuevo, tramite, loginBean.getUsuario());
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
}