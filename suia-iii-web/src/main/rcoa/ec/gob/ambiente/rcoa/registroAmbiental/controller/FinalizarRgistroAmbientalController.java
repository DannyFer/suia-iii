package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.CriterioMagnitudFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.VariableCriterioFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.BienesServiciosInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.PromedioInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.BienesServiciosInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.PromedioInventarioForestal;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.CriterioMagnitud;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.model.VariableCriterio;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.ViabilidadTecnicaProyectoFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnicaProyecto;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validaPago.bean.PagoConfiguracionesBean;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;
import lombok.Getter;
import lombok.Setter;


@ManagedBean
@ViewScoped
public class FinalizarRgistroAmbientalController {
    
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentosFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
    private OrganizacionFacade organizacionFacade;
	@EJB
	private CriterioMagnitudFacade criterioMagnitudFacade;
	@EJB
	private VariableCriterioFacade variableCriterioFacade;
	@EJB
	private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
	@EJB
	private PromedioInventarioForestalFacade promedioInventarioForestalFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	@EJB
    private AreaFacade areaFacade;
	@EJB
	private ViabilidadTecnicaProyectoFacade viabilidadTecnicaProyectoFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private BienesServiciosInventarioForestalFacade bienesServiciosInvFacade;
    @EJB
    private ContactoFacade contactoFacade;
	@EJB
	private AsignarTareaFacade asignarTareaFacade;
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
   
	@Getter
    @Setter
    @ManagedProperty(value = "#{marcoLegalReferencialController}")
    private MarcoLegalReferencialController marcoLegalReferencialBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{menuRegistroAmbientalCoaController}")
    private MenuRegistroAmbientalCoaController menuCoaBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{registroAmbientalController}")
    private RegistroAmbientalController registroAmbientalController;
	
    @Getter
    @Setter
	@ManagedProperty(value = "#{datosOperadorRgdBean}")
    private DatosOperadorRgdBean datosOperadorRgdBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
	@Getter
    @Setter
    private String mensaje, pdf, mensajeConfirmacion;
	
    @Getter
    @Setter
    private Boolean aceptaCondiciones = false, existeError;
    
	@Getter
	@Setter
	private Area areaResponsable;
    
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private RegistroAmbientalRcoa registroAmbiental;

	private boolean tienePago;
	
	@Getter
	@Setter
	private Boolean esActividadRelleno = false, pmaViabilidad=false;
	
	@Getter
	@Setter
	private String mensajeBloqueoRegistroAmbiental;
	
    
	private static final String MENOS_PARTICIPACION = "EMPRESAS MIXTAS CUANDO SU CAPITAL SUSCRITO SEA MENOR A LAS DOS TERCERAS PARTES A ENTIDADES DE DERECHO PÚBLICO.";
	private static final String IGUAL_MAS_PARTICIPACION = "EMPRESAS MIXTAS CUANDO SU CAPITAL SUSCRITO PERTENEZCA POR "
			+ "LO MENOS A LAS DOS TERCERAS PARTES A ENTIDADES DE DERECHO PÚBLICO.";

    @PostConstruct
	private void init(){
		mensaje = getNotaResponsabilidad(loginBean.getUsuario());
		mensajeConfirmacion ="";
		registroAmbiental = marcoLegalReferencialBean.getRegistroAmbientalRcoa();
		datosOperadorRgdBean.buscarDatosOperador(registroAmbiental.getProyectoCoa().getUsuario());
		validarActividadRellenoSanitario();
		
		if(esActividadRelleno){
			if(registroAmbiental != null && registroAmbiental.getId() != null){
				aceptaCondiciones = registroAmbiental.isFinalizadoIngreso();
		    	// verificacion paso 2
		    	FaseRegistroAmbientalController fasesregistroambientalBean = JsfUtil.getBean(FaseRegistroAmbientalController.class);
			    if(!fasesregistroambientalBean.validateDataViabilidad()){
			    	menuCoaBean.setPageDescripcionViabilidad(true);
			    }else{
			    	PlanManejoAmbientalConstruccionCoaController planAmbientalConstruccionBean = JsfUtil.getBean(PlanManejoAmbientalConstruccionCoaController.class);
			    	PlanManejoAmbientalOperacionCoaController planAmbientalOperacionBean = JsfUtil.getBean(PlanManejoAmbientalOperacionCoaController.class);
			    	if(registroAmbientalController.isPmaConstruccion() && !planAmbientalConstruccionBean.validarDatosIngresadosViabilidad(false)){
				    	menuCoaBean.setPagePlanConstruccionViab(true);
			    	}
			    	if(registroAmbientalController.isPmaOperacion() && !planAmbientalOperacionBean.validarDatosIngresadosViabilidad(false)){
				    	menuCoaBean.setPagePlanOperacionViab(true);
			    	}
			    }
		    	
			    if(menuCoaBean.isPageDescripcion() || menuCoaBean.isPagePlanConstruccion() || menuCoaBean.isPagePlanOperacion()){
		            RequestContext context = RequestContext.getCurrentInstance();
		            context.execute("PF('dlgPasoFaltante').show();");
			    }
		    }else{
		    	menuCoaBean.setPageMarcoLegal(true);
		    	// verificacion paso 2
		    	FaseRegistroAmbientalController fasesregistroambientalBean = JsfUtil.getBean(FaseRegistroAmbientalController.class);
			    if(!fasesregistroambientalBean.validateDataViabilidad()){
			    	menuCoaBean.setPageDescripcionViabilidad(true);
			    }else{
			    	PlanManejoAmbientalConstruccionCoaController planAmbientalConstruccionBean = JsfUtil.getBean(PlanManejoAmbientalConstruccionCoaController.class);
			    	PlanManejoAmbientalOperacionCoaController planAmbientalOperacionBean = JsfUtil.getBean(PlanManejoAmbientalOperacionCoaController.class);
			    	if(registroAmbientalController.isPmaConstruccion() && !planAmbientalConstruccionBean.validarDatosIngresadosViabilidad(false)){
				    	menuCoaBean.setPagePlanConstruccionViab(true);
			    	}
			    	if(registroAmbientalController.isPmaOperacion() && !planAmbientalOperacionBean.validarDatosIngresadosViabilidad(false)){
				    	menuCoaBean.setPagePlanOperacionViab(true);
			    	}
			    }
	            RequestContext context = RequestContext.getCurrentInstance();
	            context.execute("PF('dlgPasoFaltante').show();");
		    }						
		}else{
			if(registroAmbiental != null && registroAmbiental.getId() != null){
				aceptaCondiciones = registroAmbiental.isFinalizadoIngreso();
		    	// verificacion paso 2
		    	FaseRegistroAmbientalController fasesregistroambientalBean = JsfUtil.getBean(FaseRegistroAmbientalController.class);
			    if(!fasesregistroambientalBean.validateData()){
			    	menuCoaBean.setPageDescripcion(true);
			    }else{
			    	PlanManejoAmbientalConstruccionCoaController planAmbientalConstruccionBean = JsfUtil.getBean(PlanManejoAmbientalConstruccionCoaController.class);
			    	PlanManejoAmbientalOperacionCoaController planAmbientalOperacionBean = JsfUtil.getBean(PlanManejoAmbientalOperacionCoaController.class);
			    	PlanManejoAmbientalCierreCoaController planAmbientalCierreBean = JsfUtil.getBean(PlanManejoAmbientalCierreCoaController.class);
			    	if(registroAmbientalController.isPmaConstruccion() && !planAmbientalConstruccionBean.validarDatosIngresados(false)){
				    	menuCoaBean.setPagePlanConstruccion(true);
			    	}
			    	if(registroAmbientalController.isPmaOperacion() && !planAmbientalOperacionBean.validarDatosIngresados(false)){
				    	menuCoaBean.setPagePlanOperacion(true);
			    	}
			    	if(registroAmbientalController.isPmaCierre() && !planAmbientalCierreBean.validarDatosIngresados(false)){
				    	menuCoaBean.setPagePlanCierre(true);
			    	}
			    }
		    	// verificacion paso 3
			    /*ParticipacionCiudadanaRcoaController ppcBean = JsfUtil.getBean(ParticipacionCiudadanaRcoaController.class);
			    if(!ppcBean.validarDatos()){
			    	menuCoaBean.setPagePPC(true);
			    }*/
			    if(menuCoaBean.isPageDescripcion() || menuCoaBean.isPagePlanConstruccion() || menuCoaBean.isPagePlanOperacion()  || menuCoaBean.isPagePlanCierre() /*|| menuCoaBean.isPagePPC()*/){
		            RequestContext context = RequestContext.getCurrentInstance();
		            context.execute("PF('dlgPasoFaltante').show();");
			    }
		    }else{
		    	menuCoaBean.setPageMarcoLegal(true);
		    	// verificacion paso 2
		    	FaseRegistroAmbientalController fasesregistroambientalBean = JsfUtil.getBean(FaseRegistroAmbientalController.class);
			    if(!fasesregistroambientalBean.validateData()){
			    	menuCoaBean.setPageDescripcion(true);
			    }else{
			    	PlanManejoAmbientalConstruccionCoaController planAmbientalConstruccionBean = JsfUtil.getBean(PlanManejoAmbientalConstruccionCoaController.class);
			    	PlanManejoAmbientalOperacionCoaController planAmbientalOperacionBean = JsfUtil.getBean(PlanManejoAmbientalOperacionCoaController.class);
			    	PlanManejoAmbientalCierreCoaController planAmbientalCierreBean = JsfUtil.getBean(PlanManejoAmbientalCierreCoaController.class);
			    	if(registroAmbientalController.isPmaConstruccion() && !planAmbientalConstruccionBean.validarDatosIngresados(false)){
				    	menuCoaBean.setPagePlanConstruccion(true);
			    	}
			    	if(registroAmbientalController.isPmaOperacion() && !planAmbientalOperacionBean.validarDatosIngresados(false)){
				    	menuCoaBean.setPagePlanOperacion(true);
			    	}
			    	if(registroAmbientalController.isPmaCierre() && !planAmbientalCierreBean.validarDatosIngresados(false)){
				    	menuCoaBean.setPagePlanCierre(true);
			    	}
			    }
		    	// verificacion paso 3
			    /*ParticipacionCiudadanaRcoaController ppcBean = JsfUtil.getBean(ParticipacionCiudadanaRcoaController.class);
			    if(!ppcBean.validarDatos()){
			    	menuCoaBean.setPagePPC(true);
			    }*/
	            RequestContext context = RequestContext.getCurrentInstance();
	            context.execute("PF('dlgPasoFaltante').show();");
		    }
		}
		
		
		if (registroAmbiental.getProyectoCoa() != null){
			proyecto = registroAmbiental.getProyectoCoa();
			tienePago=verificarPago();
		}
		areaResponsable = marcoLegalReferencialBean.getProyectoLicenciaCoa().getAreaResponsable();
	}
    
    private void validarActividadRellenoSanitario(){
		try {
			FaseRegistroAmbientalController fasesregistroambientalBean = JsfUtil.getBean(FaseRegistroAmbientalController.class);
			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = new ArrayList<ProyectoLicenciaCuaCiuu>();
			listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(fasesregistroambientalBean.getRegistroAmbiental().getProyectoCoa());
			
			for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
				if(actividad.getCatalogoCIUU().getCodigo().equals("E3821.01") || actividad.getCatalogoCIUU().getCodigo().equals("E3821.01.01")){
					List<ViabilidadTecnicaProyecto> lista = viabilidadTecnicaProyectoFacade.buscarPorProyecto(fasesregistroambientalBean.getRegistroAmbiental().getProyectoCoa().getId());
					if(lista!= null && !lista.isEmpty()){
						esActividadRelleno = true;
						pmaViabilidad = true;
					}
					break;
				}
			}
			// verifico si es actividad de botaderos y corresponde a una de las consideraciones siguientes ingreso el pma de la consideracion seleccionada 
			for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
				if(actividad.getPrimario() && actividad.getCatalogoCIUU().getCodigo().equals("E3821.01") || actividad.getCatalogoCIUU().getCodigo().equals("E3821.01.01")){
					if(actividad.getSubActividad().getNombre().equalsIgnoreCase("Cierre técnico de botaderos e implementación de celda emergente para la disposición final de desechos sólidos no peligrosos en el mismo predio")
							|| actividad.getSubActividad().getNombre().equalsIgnoreCase("Cierre técnico de botaderos de desechos sólidos")){
						pmaViabilidad = false;
					}	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
    /**
     * metodo para obtener el mensaje de Responsabilidad configurado en la plantilla
     * @param persona
     * @return
     */
    private String getNotaResponsabilidad( Usuario persona) {
        String[] parametros = {persona.getPersona().getNombre(), persona.getPersona().getPin()};
        return DocumentoPDFPlantillaHtml.getPlantillaConParametros("nota_responsabilidad_certificado_interseccion", parametros);
    }

    private boolean validardatos(){
    	boolean valido = true;
    	if(!registroAmbiental.isAceptacion()){
			JsfUtil.addMessageError("Debe aceptar el marco legal.");
    		return false;
    	}
    	// verificacion paso 2
    	
    	if(esActividadRelleno){
    		FaseRegistroAmbientalController fasesregistroambientalBean = JsfUtil.getBean(FaseRegistroAmbientalController.class);
        	valido = fasesregistroambientalBean.validateDataViabilidad();
    	}else{
    		FaseRegistroAmbientalController fasesregistroambientalBean = JsfUtil.getBean(FaseRegistroAmbientalController.class);
        	valido = fasesregistroambientalBean.validateData();
    	}    	
    	return valido;
    }

	public void completarTarea(){
		try {
			if(validardatos()){
				String nombreTarea= "Completar formulario del registro ambiental";
				Map<String, Object> parametros = new HashMap<>();
				parametros.put("tienePago", tienePago);
				//valido que sea la tarea de completar informacion para pasar de tarea
				if(!nombreTarea.equals(bandejaTareasBean.getTarea().getTaskName()) && !"Registrar información del proyecto en la ficha ambiental".equals(bandejaTareasBean.getTarea().getTaskName())){
					JsfUtil.addMessageError("Error al procesar la tarea.");
					return;
				}
				//guardo el registro generado
				existeError=false;
				cargarDatosRegistroAmbientalCoa("si");
	            if(existeError){
					JsfUtil.addMessageError("Error al generar el documento.");
					return;
	            }
				List<Usuario> listaUsuario = null;
				// inicializo la variable de la autoridad ambiental
				String rol="role.registro.ambiental.autoridad";
				Area area = proyecto.getAreaResponsable();
				String rolAutoridad = Constantes.getRoleAreaName(rol);
				
//				if(area.getTipoArea().getSiglas().equals("DP") || area.getTipoArea().getSiglas().equals("ZONALES")){
//					UbicacionesGeografica provincia = proyecto.getAreaResponsable().getUbicacionesGeografica();	
//					area = areaFacade.getAreaCoordinacionZonal(provincia);
//				}
				
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
				
				//bloquear registros ambientales para ejecución de PPC fisico. Ticket#10418702
				Boolean bloqueo = false, esRegistroConPPC=false;
				Usuario tecnico = new Usuario();
				// si es un registro ambiental com PPC busco el tecnico reponsable de mineria o hidrocaruros MAE
				if (bandejaTareasBean.getTarea().getProcessId().equals(Constantes.RCOA_REGISTRO_AMBIENTAL_PPC)) {
					ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
					CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();
					if (actividadPrincipal.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)) {
							rol="role.esia.pc.tecnico.responsable.tipoSector."+actividadPrincipal.getTipoSector().getId();
							String rolTecnico = Constantes.getRoleAreaName(rol);
							if(area.getTipoArea().getSiglas().equals("PC")){
								listaUsuario = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, area.getAreaName());
							}else if(area.getTipoArea().getSiglas().equals("OT")){
								listaUsuario = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, area.getAreaName());
							}else{
								listaUsuario = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, area.getAreaName());
							}							
					}else if(actividadPrincipal.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_MINERIA)){						
						if(area.getTipoArea().getSiglas().equals("PC")){
							rol="role.esia.pc.tecnico.responsable.tipoSector."+actividadPrincipal.getTipoSector().getId();
							String rolTecnico = Constantes.getRoleAreaName(rol);							
							listaUsuario = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, area.getAreaName());
						}else if(area.getTipoArea().getSiglas().equals("OT")){
							rol="role.ppc.cz.tecnico.mineria";
							String rolTecnico = Constantes.getRoleAreaName(rol);
							listaUsuario = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, area.getAreaName());
						}else{
							rol="role.ppc.gad.tecnico.mineria";
							String rolTecnico = Constantes.getRoleAreaName(rol);
							listaUsuario = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, area.getAreaName());
						}						
					}
										
					if(listaUsuario != null && listaUsuario.size() > 0){
						tecnico = listaUsuario.get(0);
						parametros.put("tecnico", listaUsuario.get(0).getNombre());
						esRegistroConPPC=true;
					}else{
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
						return;
					}
				}
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				registroAmbiental.setFinalizadoIngreso(true);
				registroAmbiental.setFechaFinalizacionIngreso(new Date());
				registroAmbientalCoaFacade.guardar(registroAmbiental);
				//envio notificacion al tecnico responsable si el proyecto es de mineria o hidrocarburos
				if(esRegistroConPPC){
					notificacionTecnico(tecnico);
				}				
				if(bloqueo) {
					mensajeBloqueoRegistroAmbiental = mensajeNotificacionFacade.recuperarValorMensajeNotificacionSD("mensajeBloqueoRegistroAmbiental");
					
					RequestContext.getCurrentInstance().update("frmEnviar:bloqueoRegistroAmbientalPpc");
					RequestContext.getCurrentInstance().execute("PF('bloqueoRegistroAmbientalPpc').show();");
				} else {
					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
					JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
				}
			}
		} catch (JbpmException e) {					
			e.printStackTrace();
		}catch (Exception e) {					
			e.printStackTrace();
		}
	}
	
	private void notificacionTecnico(Usuario tecnico){
		try {
			Object[] parametrosNotificacion = new Object[] {tecnico.getPersona().getNombre(), datosOperadorRgdBean.getDatosOperador().getRepresentanteLegal(), 
        			proyecto.getNombreProyecto(), proyecto.getCodigoUnicoAmbiental()};
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacion_R_A_PPC_revisionTecnico", parametrosNotificacion);
			Email.sendEmail(tecnico, "Revisar información Ficha Ambiental", mensaje, proyecto.getCodigoUnicoAmbiental(), loginBean.getUsuario());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean verificarPago(){
		// obtengo las actividades seleccionadas en informacion preliminar para verificar el pago
		boolean existePago=false, pagoCoberturaVegetal=false;
		
		String mensajeFinalizacion=(Constantes.getMessageResourceString("mensaje.categoriaII.pago.entes"));
		//verifico si el usuario esta relacionado a una empresa publica o gobierno autonomo no paga
		try{
			Organizacion organizacion = new Organizacion();
			if (proyecto.getUsuario().getPersona().getOrganizaciones() != null && proyecto.getUsuario().getPersona().getOrganizaciones().size() > 0) {
				organizacion = organizacionFacade.buscarPorRuc(proyecto.getUsuario().getNombre());
			}
			if(proyecto.getRenocionCobertura() && (proyecto.getCategorizacion()==1 || proyecto.getCategorizacion()==2))
				pagoCoberturaVegetal = true;
			else
				pagoCoberturaVegetal= false;
			float total =0;
				   // verifico el tipo de usuario para comprobar la catidad del pago
		    	List<Object> arregloPago = validaCostoRegistroAmbiental(organizacion);
		    	total = (float) arregloPago.get(0);
		    	mensajeFinalizacion += arregloPago.get(1);
				// verifico si la actividad tiene paga o no
				List<ProyectoLicenciaCuaCiuu>  listaactividades = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyecto);
				for (ProyectoLicenciaCuaCiuu objActividad : listaactividades) {
					if(objActividad.getPrimario()){
						if(objActividad.getCatalogoCIUU().getPagoRegistroAmbiental()){
							existePago=true;
							// verifico si el sector es mineria
							if(objActividad.getCatalogoCIUU().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_MINERIA)){
								// verifico si es mineria artesanal no paga tasa
								List<CriterioMagnitud> listaMagnitud = criterioMagnitudFacade.buscarCriterioMagnitudPorProyecto(proyecto);
								if(listaMagnitud != null && !listaMagnitud.isEmpty()){
									for (CriterioMagnitud criterioMagnitud : listaMagnitud) {
										VariableCriterio variableCriterio = new VariableCriterio();
										variableCriterio = variableCriterioFacade.buscarVariableCriterioPorId(criterioMagnitud.getVariableCriterio());
										if(variableCriterio.getNivelMagnitud().getId() == 3 && variableCriterio.getUnidad().equals("Tonelada/día")){
											if(criterioMagnitud.getValorMagnitud().getValor() <= 10){
												existePago=false;
											}
										}
									}
								}
							}else{
								existePago=true;
							}
						}
						break;
					}
				}

		      	String textoPago=" ";
		      	if(proyecto.getAreaResponsable().getTipoEnteAcreditado() != null){
					if(!proyecto.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
			      		textoPago=" en la entidad financiera o lugar de recaudación correspondiente al "+proyecto.getAreaResponsable().getAreaName()+". Y con los números de referencia debe completar la tarea \"Realizar pago de tasa\".";
					}
		      	}else if(pagoCoberturaVegetal) {
		      			textoPago=" se lo debe realizar por medio de la opción de Pago en Línea del "+Constantes.NOMBRE_INSTITUCION+".";
					}else {
						textoPago=" se lo debe realizar por medio de la opción de Pago en Línea del "+Constantes.NOMBRE_INSTITUCION+".";
					}
		      	
		          mensajeFinalizacion += "\nRegistro Ambiental: " + total + " USD"+textoPago;
		          /**** modulo pago 2016-03-03 ****/

			      if(pagoCoberturaVegetal){
			    	  double valorCobertura =0;
			    	// para covertura foresta
						InventarioForestalAmbiental inventario = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(proyecto.getId());				
						PromedioInventarioForestal suma = new PromedioInventarioForestal();
						BienesServiciosInventarioForestal bienes = new BienesServiciosInventarioForestal();
						if(inventario != null){
							bienes = bienesServiciosInvFacade.getByIdInventarioForestalRegistro(inventario.getId());
							
							if(bienes !=null && bienes.getPagoTotal() != null && bienes.getPagoTotal() >0){
								valorCobertura = bienes.getPagoTotal();													
							}else{
								suma = promedioInventarioForestalFacade.getByIdInventarioForestalRegistro(inventario.getId());
								if(suma != null && suma.getPagoDesbroceCobertura() != null){
									if(suma.getPagoDesbroceCobertura() > 0){
										valorCobertura = suma.getPagoDesbroceCobertura();
									}
								}
							}							
						}
						
			          double coberturaVegetal = valorCobertura;
			          total += coberturaVegetal;
			          if(valorCobertura > 0){
			              DecimalFormat decimalValorTramite= new DecimalFormat(".##");
			              String valorCoverturaFormato ="";
			             
			              valorCoverturaFormato =decimalValorTramite.format(valorCobertura).replace(",",".");
			              
				          if(proyecto.getAreaResponsable().getTipoEnteAcreditado() != null){
				          	mensajeFinalizacion += " y para el pago por concepto de remoción de cobertura vegetal nativa: " + valorCoverturaFormato + " USD  se lo debe realizar por medio de la opción de Pago en Línea del "+Constantes.NOMBRE_INSTITUCION+".";
				          }else {
				        	  mensajeFinalizacion += "\n Y por Remoción de cobertura vegetal nativa: " + valorCoverturaFormato + " USD.  se lo debe realizar por medio de la opción de Pago en Línea del "+Constantes.NOMBRE_INSTITUCION+".";
				          }
			          }else{
			        	  pagoCoberturaVegetal= false;
			          }
			      }

				mensajeConfirmacion ="Está seguro que desea enviar la información?";

		}catch(Exception e){
			
		}
		existePago = pagoCoberturaVegetal?pagoCoberturaVegetal:existePago;
		if(existePago){
			mensajeConfirmacion =mensajeFinalizacion;
		}
		return existePago;
	}

public List<Object> validaCostoRegistroAmbiental(Organizacion organizacion) {
	String mensajeFinalizacion = "";
	List<Object>objLic=new ArrayList<Object>();
	
	float valorAPagar = 0.00f;
	try {
    	Float costoEmisionRegistro = 0f;
    	Float costoControlSeguimiento = 0f;
    		costoEmisionRegistro = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
    				getPagoConfigValue("Valor por registro ambiental v2"));	//100.00
    		costoControlSeguimiento = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
    				getPagoConfigValue("Valor por seguimiento de registro ambiental v1"));	//80.00
   
    	if(organizacion != null && organizacion.getId() != null) {
    			if (organizacion.getTipoOrganizacion().getDescripcion().equals("EP") // 7 = Empresas Públicas
    					|| organizacion.getTipoOrganizacion().getDescripcion().equals("OG")   // 2 = OG
    					|| organizacion.getTipoOrganizacion().getDescripcion().equals("GA")) {	// 5 = Gobiernos Autónomos
    				valorAPagar = costoControlSeguimiento;
    				mensajeFinalizacion += "";
    			}else if(organizacion.getTipoOrganizacion().getDescripcion().equals("ONG")    // 1 = ONG
    					|| organizacion.getTipoOrganizacion().getDescripcion().equals("A")    // 3 = Asociaciones
    					|| organizacion.getTipoOrganizacion().getDescripcion().equals("C")  // 4 = Comunidades
    					|| organizacion.getTipoOrganizacion().getDescripcion().equals("EA") ){   // 6 = Empresas Privadas
    				valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
    			}else if(organizacion.getTipoOrganizacion().getDescripcion().equals("EM") ){     // 8 = Empresas Mixtas
    				if(organizacion.getParticipacionEstado() == null 
    						|| organizacion.getParticipacionEstado().equals(MENOS_PARTICIPACION)) {
    					valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
    				} else if(organizacion.getParticipacionEstado().equals(IGUAL_MAS_PARTICIPACION)) {
    					valorAPagar = costoControlSeguimiento;
    				}
    			}else {
    				valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
    			}
    	} // PAGOS PARA PERSONAS NATURALES, EL PAGO ES LA SUMA TOTAL
    	else {
    		valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
    	}
		mensajeFinalizacion += "pago por concepto de ";
	} catch (Exception e) {
		e.printStackTrace();
	}
	objLic.add(0, valorAPagar);
	objLic.add(1, mensajeFinalizacion);
	return objLic;
}

	public void iniciar(){
		ProcesoRegistroAmbientalRcoaController procesoRetceController =JsfUtil.getBean(ProcesoRegistroAmbientalRcoaController.class);
		if(procesoRetceController.iniciarProceso(registroAmbiental)){
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		}else{
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
			return;
		}
	}

	public void cargarDatosRegistroAmbientalCoa(String finalizar ) throws Exception{
		boolean marcaAgua = finalizar.equals("si")?false:true;
		//ImpresionFichaGeneralController impresionFichaGeneralController = JsfUtil.getBean(ImpresionFichaGeneralController.class);
		GenerarDocumentosRgistroAmbientalCoaController generarDocumentosRgistroAmbientalCoa = JsfUtil.getBean(GenerarDocumentosRgistroAmbientalCoaController.class);
		File archivoTmp = generarDocumentosRgistroAmbientalCoa.imprimirFichaPdf(registroAmbiental, esActividadRelleno, pmaViabilidad);
		existeError = generarDocumentosRgistroAmbientalCoa.isExisteError();
		if(existeError){
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
			RequestContext.getCurrentInstance().execute("PF('dlgRegistro').hide();");
			return;
		}
		//File archivoTmp = impresionFichaGeneralController.imprimirFichaPdf();
		//File archivoArticulosTmp = generarDocumentosRgistroAmbientalCoa.exportarMarcoLegalCoaPdf(marcoLegalReferencialBean.getCatalogoGenerals(), areaResponsable);
		List<File> listaFiles = new ArrayList<File>();
		listaFiles.add(archivoTmp);
		//listaFiles.add(archivoArticulosTmp);
		File archivoconcatenadoTmp;
		if(marcaAgua)
		{
			//File archivoUnirTmp = UtilFichaMineria.unirPdf(listaFiles, "Ficha_Ambiental");                
			//archivoconcatenadoTmp= JsfUtil.fileMarcaAguaRCoa(archivoUnirTmp, "--BORRADOR--",BaseColor.GRAY);
			archivoconcatenadoTmp= JsfUtil.fileMarcaAguaRCoaHorizontal(archivoTmp, "--BORRADOR--",BaseColor.GRAY);
		}else{
			//archivoconcatenadoTmp= UtilFichaMineria.unirPdf(listaFiles, "Ficha_Ambiental");
			archivoconcatenadoTmp = archivoTmp;
		}
		//En cumplimiento al acuerdo ministerial 083-B publicado en el registro oficial edición especial Nro. 387 
		Path path = Paths.get(archivoconcatenadoTmp.getAbsolutePath());
		byte[] archivo = Files.readAllBytes(path);
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(archivoconcatenadoTmp.getName()));
		DocumentoRegistroAmbiental documentoInformacion = new DocumentoRegistroAmbiental();
		documentoInformacion.setContenidoDocumento(archivo);
		if(documentoInformacion.getContenidoDocumento()!=null && finalizar.equals("si")){
			documentosFacade.ingresarDocumento(documentoInformacion, marcoLegalReferencialBean.getProyectoLicenciaCoa().getId(), marcoLegalReferencialBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), TipoDocumentoSistema.REGISTRO_AMBIENTAL_RCOA, archivoconcatenadoTmp.getName(), ProyectoLicenciaCoa.class.getSimpleName(), marcoLegalReferencialBean.getIdProceso());
        }
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(archivo);
		file.close();
		setPdf(JsfUtil.devolverContexto("/reportesHtml/" + archivoconcatenadoTmp.getName()));
	}

	
	public StreamedContent descargarFicha(){

		return null;
	}
}
