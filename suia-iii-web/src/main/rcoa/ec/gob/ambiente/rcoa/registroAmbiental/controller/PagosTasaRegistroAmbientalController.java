package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.WordUtils;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.BienesServiciosInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.PromedioInventarioForestalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.BienesServiciosInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.PromedioInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.model.PagoKushkiJson;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.controllers.CombosController;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TipoOrganizacion;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.recaudaciones.bean.PagoRcoaBean;
import ec.gob.ambiente.suia.recaudaciones.controllers.GenerarNUTController;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.model.DocumentoNUT;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validaPago.bean.PagoConfiguracionesBean;

@ManagedBean
@ViewScoped
public class PagosTasaRegistroAmbientalController {

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
	
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;

	@EJB
    private AreaFacade areaFacade;
	
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;
	@EJB
    private OrganizacionFacade organizacionFacade;
	@EJB
	private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
	@EJB
	private PromedioInventarioForestalFacade promedioInventarioForestalFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
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
	@ManagedProperty(value = "#{pagoRcoaBean}")
	private PagoRcoaBean pagoRcoaBean;
	
	@Getter
	@Setter
	public boolean pagoCoverturaVegetal;
	
	@Getter
	@Setter
	public double valorTotalAPagar, montoTotalProyecto;

	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	@Getter
	@Setter
	private String mensajeAdd;
	/*Ubicacion Geografica del Proyecto*/
	@Getter
	@Setter
	List<UbicacionesGeografica> ubicacionesSeleccionadas;
	@Getter
	@Setter
	private String mensaje;

	private UbicacionesGeografica data_localizacion = new UbicacionesGeografica();
	
	@Getter
   	@Setter
   	public Boolean esEmpresaPublica;

	@PostConstruct
	public void initData() throws Exception {
		String url_redirec = "/pages/rcoa/registroAmbiental/pagoRegistroAmbientalCoa.jsf";
		pagoRcoaBean.setUrl_enlace(url_redirec);		
		proyecto = proyectosBean.getProyectoRcoa();
		pagoRcoaBean.setMontoTotalCoberturaVegetal(0);
		pagoRcoaBean.setEsEnteAcreditado(false);
		pagoRcoaBean.setMostrarPnlCobertura(false);
		pagoRcoaBean.setControl_salto(true);
		mensajeAdd =  pagoRcoaBean.getMensaje_Adicional();
		String tramite = proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental();
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		if(proyecto.getRenocionCobertura() && (proyecto.getCategorizacion()==1 || proyecto.getCategorizacion()==2)){
			pagoCoverturaVegetal = true;
			// para cobertura forestal
			InventarioForestalAmbiental inventario = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(proyecto.getId());				
			PromedioInventarioForestal suma = new PromedioInventarioForestal();
			BienesServiciosInventarioForestal bienes = new BienesServiciosInventarioForestal();
			if(inventario != null){
				
				bienes = bienesServiciosInvFacade.getByIdInventarioForestalRegistro(inventario.getId());
				
				if(bienes !=null && bienes.getPagoTotal() != null && bienes.getPagoTotal() >0){
					pagoRcoaBean.setMontoTotalCoberturaVegetal(bienes.getPagoTotal());
					pagoRcoaBean.setMontoTotalCoberturaVegetal(Double.valueOf(twoDForm.format(pagoRcoaBean.getMontoTotalCoberturaVegetal()).replace(",", ".")));					
				}else{
					suma = promedioInventarioForestalFacade.getByIdInventarioForestalRegistro(inventario.getId());
					if(suma != null && suma.getPagoDesbroceCobertura() != null && suma.getPagoDesbroceCobertura() > 0){
						pagoRcoaBean.setMontoTotalCoberturaVegetal(suma.getPagoDesbroceCobertura());
						pagoRcoaBean.setMontoTotalCoberturaVegetal(Double.valueOf(twoDForm.format(pagoRcoaBean.getMontoTotalCoberturaVegetal()).replace(",", ".")));
					}else{
						pagoCoverturaVegetal= false;
					}
				}
			}
		}else{
			pagoCoverturaVegetal= false;
		}

		GenerarNUTController nutController = JsfUtil.getBean(GenerarNUTController.class);
		List<DocumentoNUT> documentosNUTOtraTarea = new ArrayList<DocumentoNUT>();
		List<DocumentoNUT> documentosNUTAux = nutController.obtenerDocumentosNut(tramite);
		List<PagoKushkiJson> PagoKushkiJsonAux = nutController.listPagosKushki(tramite);
		if (documentosNUTAux == null)
			documentosNUTAux = new ArrayList<DocumentoNUT>();
		for (DocumentoNUT documentoNUT : documentosNUTAux) {
			if(!documentoNUT.getIdProceso().equals(bandejaTareasBean.getProcessId())){
				documentosNUTOtraTarea.add(documentoNUT);
			}
		}
		if (PagoKushkiJsonAux != null)
		{
			pagoRcoaBean.setPagoKushkiJson(PagoKushkiJsonAux);
		}
		if (pagoRcoaBean.getPagoKushkiJson() != null)
		{
			pagoRcoaBean.setValidaNutKushki(false);
			//pagoRcoaBean.setControl_salto(true);
		}		
		//elimino los documentos de otras tareas
		if(documentosNUTOtraTarea.size() > 0){
			documentosNUTAux.removeAll(documentosNUTOtraTarea);
		}
		pagoRcoaBean.setDocumentosNUT(documentosNUTAux);
		pagoRcoaBean.setTramite(tramite);
		pagoRcoaBean.setTipoProyecto("REGISTRO");
		pagoRcoaBean.setValidaNutKushki(true);
		cargarUbicacionProyecto();		
		pagoRcoaBean.setIdentificadorMotivo(null);
		pagoRcoaBean.setCategorizacion(proyecto.getCategorizacion());
		pagoRcoaBean.setDireccion(proyectosBean.getProyectoRcoa().getDireccionProyecto());
		pagoRcoaBean.setIdproy(proyectosBean.getProyectoRcoa().getId()); 
		pagoRcoaBean.setCiudad(data_localizacion.getUbicacionesGeografica().getNombre().toString());
		pagoRcoaBean.setProvincia(data_localizacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre().toString());
		pagoRcoaBean.setPais(data_localizacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre().toString());				
		// busco la institucion financiera si es un ente acreditado
		if (proyectosBean.getProyectoRcoa().getAreaResponsable().getTipoEnteAcreditado() != null) {
			if (proyectosBean.getProyectoRcoa().getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
				pagoRcoaBean.setInstitucionesFinancieras(institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC());
			}else{
				pagoRcoaBean.setEsEnteAcreditado(true);
				pagoRcoaBean.setEsPagoEnLinea(true);
				//pagoRcoaBean.setControl_salto(true);
				if (pagoRcoaBean.getPagoKushkiJson() != null)
				{
					pagoRcoaBean.setValidaNutKushki(false);
				}
				pagoRcoaBean.setInstitucionesFinancieras(institucionFinancieraFacade.obtenerPorNombre(proyecto.getAreaResponsable().getAreaName()));
				if(pagoRcoaBean.getInstitucionesFinancieras()==null)
					JsfUtil.addMessageError("No se encontró institución financiara para: "+proyecto.getAreaResponsable().getAreaName());
			}
		} else {
			
			pagoRcoaBean.setInstitucionesFinancieras(institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC());
		}
		if(pagoRcoaBean.getInstitucionesFinancieras() != null && pagoRcoaBean.getInstitucionesFinancieras().size() > 0)
			pagoRcoaBean.getTransaccionFinanciera().setInstitucionFinanciera(pagoRcoaBean.getInstitucionesFinancieras().get(0));
		// institucion bancaria para cobertura vegetal
		pagoRcoaBean.setInstitucionesFinancierasCobertura(institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC());
		if(pagoCoverturaVegetal && pagoRcoaBean.getInstitucionesFinancieras() != null && pagoRcoaBean.getInstitucionesFinancieras().size() > 0
								&& pagoRcoaBean.getInstitucionesFinancierasCobertura() != null && pagoRcoaBean.getInstitucionesFinancierasCobertura().size() > 0
								/*&& pagoRcoaBean.getInstitucionesFinancieras().get(0).getId() != pagoRcoaBean.getInstitucionesFinancierasCobertura().get(0).getId()*/){
			pagoRcoaBean.setMostrarPnlCobertura(true);
			pagoRcoaBean.getTransaccionFinancieraCobertura().setInstitucionFinanciera(pagoRcoaBean.getInstitucionesFinancierasCobertura().get(0));
		}

		Organizacion organizacion = new Organizacion();
		if (proyecto.getUsuario().getPersona().getOrganizaciones() != null && proyecto.getUsuario().getPersona().getOrganizaciones().size() > 0) {
			organizacion = organizacionFacade.buscarPorRuc(proyecto.getUsuario().getNombre());
		}
		montoTotalProyecto = getCostoRegistroAmbiental(organizacion);
		nutController.cargarTransacciones();
		valorTotalAPagar = montoTotalProyecto + pagoRcoaBean.getMontoTotalCoberturaVegetal();
		pagoRcoaBean.setValorAPagar(valorTotalAPagar);
		pagoRcoaBean.setMontoTotalProyecto(montoTotalProyecto);
		if(pagoRcoaBean.getTransaccionesFinancieras() != null && !pagoRcoaBean.getTransaccionesFinancieras().isEmpty())
			pagoRcoaBean.setCumpleMontoProyecto(nutController.cumpleMonto());
		
		if((pagoRcoaBean.getInstitucionesFinancieras() == null || pagoRcoaBean.getInstitucionesFinancieras().size() == 0) || (!pagoRcoaBean.isMostrarPnlCobertura() && pagoRcoaBean.isEsEnteAcreditado()))
			pagoRcoaBean.setGenerarNUT(false);

		if (pagoRcoaBean.isEsEnteAcreditado())
		{
			mensaje = "Para continuar con el proceso, "
					+ "usted debe realizar el pago de tasa en la entidad financiera o lugar de recaudación correspondiente al " + pagoRcoaBean.getInstitucionesFinancieras().get(0).getNombreInstitucion() + " y " 
					+ "con el número de referencia debe completar la tarea \"Realizar pago de tasa\".";
		}
		else
		{
			pagoRcoaBean.setControl_salto(false);
			String nombreMinisterio = WordUtils.capitalize(Constantes.NOMBRE_INSTITUCION.toLowerCase());
			if(esEmpresaPublica) {
				mensaje = "El pago correspondiente por el Registro Ambiental, se lo debe realizar por medio de la opción de Pago en Línea del " + nombreMinisterio;
				mensajeAdd =  "El pago correspondiente a la tasa por concepto de emisión de Inventario Forestal, se lo debe gestionar por medio de la opción de Pago en Línea del " + nombreMinisterio;
			} else {
				mensaje = "El pago correspondiente por el Registro Ambiental, se lo debe realizar por medio de la opción de Pago en Línea del Ministerio del Ambiente, Agua y Transición Ecológica.";
			}
		}
		
		valorTotalAPagar = montoTotalProyecto + pagoRcoaBean.getMontoTotalCoberturaVegetal() ;
		if (pagoRcoaBean.getEsPagoEnLinea() != null && pagoRcoaBean.getEsPagoEnLinea())
		{
			pagoRcoaBean.setValorAPagar(pagoRcoaBean.getMontoTotalCoberturaVegetal());		
		}
		else
		{
			pagoRcoaBean.setValorAPagar(valorTotalAPagar);
		}
		pagoRcoaBean.setMontoTotalProyecto(montoTotalProyecto);
		nutController.cerrarMensaje();
	}
	
	public void cargarUbicacionProyecto() {
		ubicacionesSeleccionadas = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectosBean.getProyectoRcoa());
		data_localizacion = ubicacionesSeleccionadas.get(0);
	}
	
	public void completarTarea(){
		try {
			
			if (pagoRcoaBean.getTransaccionesFinancieras().size() == 0) {
				JsfUtil.addMessageError("Debe registrar un pago antes de continuar.");
				return;
			}

			if (!pagoRcoaBean.isCumpleMontoProyecto()) {
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
			transaccionFinancieraFacade.guardarTransacciones(pagoRcoaBean.getTransaccionesFinancieras(),
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
		if(pagoRcoaBean.isMostrarPnlCobertura()){
			List<TransaccionFinanciera> transaccionesFinancierasRegistradas = new ArrayList<TransaccionFinanciera>();
			for (TransaccionFinanciera transa : pagoRcoaBean.getTransaccionesFinancieras()) {
				Double montoAux =0.0;
				List<TransaccionFinanciera> listaTransaccion = new ArrayList<TransaccionFinanciera>();
				listaTransaccion.add(transa);
				if(transa.getTipoPago() == null || transa.getTipoPago() == 1){
					montoAux=montoTotalProyecto;
				}else if(transa.getTipoPago() == null || transa.getTipoPago() == 2){
					montoAux=pagoRcoaBean.getMontoTotalCoberturaVegetal();
				}
				if(montoAux > 0){
					if(transa.getId() != null && transa.getId() > 0){
						pagoSatisfactorio= true;
						// almaceno las transacciones ya registradas para no duplicar
						transaccionesFinancierasRegistradas.add(transa);
					}else{
						pagoSatisfactorio = transaccionFinancieraFacade.realizarPago(montoAux, listaTransaccion, proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());
					}
				}
				// si hubo error
				if(!pagoSatisfactorio)
					return pagoSatisfactorio;
			}
			// elimino las trasnaccione ya registradas para no duplicar
			if(transaccionesFinancierasRegistradas.size() > 0){
				pagoRcoaBean.getTransaccionesFinancieras().removeAll(transaccionesFinancierasRegistradas);
			}
		}else{
			pagoSatisfactorio = transaccionFinancieraFacade.realizarPago(
				valorTotalAPagar, pagoRcoaBean.getTransaccionesFinancieras(), proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());
		}
		/*for (TransaccionFinanciera transaccionFinanciera : pagoRcoaBean.getTransaccionesFinancieras()) {
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
		}*/
		
		return pagoSatisfactorio;
	}

	public Double getCostoRegistroAmbiental(Organizacion organizacion) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Double.valueOf(valorAPagar);
	}
}
