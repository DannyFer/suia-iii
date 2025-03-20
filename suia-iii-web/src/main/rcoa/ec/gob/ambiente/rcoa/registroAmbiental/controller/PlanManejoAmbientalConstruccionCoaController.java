package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.dto.EntidadPma;
import ec.gob.ambiente.rcoa.dto.EntidadPmaViabilidad;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.PlanManejoAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalConcesionesMinerasFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalSubPlanPmaCoaFacade;
import ec.gob.ambiente.rcoa.model.AspectoAmbientalPma;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.MedidaVerificacionPma;
import ec.gob.ambiente.rcoa.model.PlanManejoAmbientalPma;
import ec.gob.ambiente.rcoa.model.PmaAceptadoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalConcesionesMineras;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalSubPlanPma;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.AspectoViabilidadTecnicaFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.PmaViabilidadTecnicaFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.ViabilidadTecnicaProyectoFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.AspectoViabilidadTecnica;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.FaseViabilidadTecnicaRcoa;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.MedioFrecuenciaMedida;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.PmaViabilidadTecnica;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnicaProyecto;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;


@ManagedBean
@ViewScoped
public class PlanManejoAmbientalConstruccionCoaController {
	private final Logger LOG = Logger.getLogger(PlanManejoAmbientalConstruccionCoaController.class);

	@Getter
    @Setter
    @ManagedProperty(value = "#{marcoLegalReferencialController}")
    private MarcoLegalReferencialController marcoLegalReferencialBean;

	@Getter
    @Setter
    @ManagedProperty(value = "#{faseRegistroAmbientalController}")
    private FaseRegistroAmbientalController faseRegistroAmbientalController;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{menuRegistroAmbientalCoaController}")
    private MenuRegistroAmbientalCoaController menuCoaBean;

	@EJB
	private PlanManejoAmbientalCoaFacade planManejoAmbientalCoaFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private RegistroAmbientalSubPlanPmaCoaFacade registroAmbientalSubPlanPmaCoaFacade;
	
	@EJB
	private DocumentosRegistroAmbientalFacade documentosFacade;
	@EJB
	private DocumentosFacade documentosPlantillasFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
	private ViabilidadTecnicaProyectoFacade viabilidadTecnicaProyectoFacade;
	@EJB
	private PmaViabilidadTecnicaFacade pmaViabilidadTecnicaFacade;
	@EJB
	private AspectoViabilidadTecnicaFacade aspectoViabilidadTecnicaFacade;
	@EJB
	private ProyectoLicenciaAmbientalConcesionesMinerasFacade proyectoConcesionFacade;

	@Getter
    @Setter
	private boolean validarDatos;

	@Getter
    @Setter
	private Integer tipoPlan, index;

	@Getter
    @Setter
	private Double ponderacion;
	
	@Getter
    @Setter
	private EntidadPma entidadPlanPma;

    @Getter
    @Setter
    private List<EntidadPma> listaPlanPma, listaPlanPmaAdicionar;

    @Getter
    @Setter
    private List<RegistroAmbientalSubPlanPma> listaDocumentoPlanPma;

    @Getter
    @Setter
    private List<PmaAceptadoRegistroAmbiental> listaPlanPmaEliminar;
    
    @Getter
    @Setter
    private List<MedidaVerificacionPma> listaPlanPmaParaAdicionar;
    
    @Getter
    @Setter
    private List<AspectoAmbientalPma> listaPlanAspectoAmbiental;

    @Getter
    @Setter
    private List<ProyectoLicenciaCuaCiuu>  listaactividades;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;

    @Getter
    @Setter
    private PmaAceptadoRegistroAmbiental planPmaElimindo, planPmaNuevo;
	
	@Getter
	@Setter
	private RegistroAmbientalRcoa registroAmbiental;
	
	@Getter
	@Setter
	private DocumentoRegistroAmbiental documentoSubPlan;
	
	@Getter
	@Setter
	private List<DocumentoRegistroAmbiental> listaDocumentoSubPlan;
	
	@Getter
	@Setter
	private String sub_plan_cierre_abandono;
	
	@Getter
	@Setter
	private Double valorPlazo;
	
	@Getter
	@Setter
	private Date valorPlazoFecha;
	
	@Getter
	@Setter
	private String codigoRGD, tipoPlazo;

	@Getter
	@Setter
	private byte[] sub_plan_cierre_abandono_byte;
	
	//Relleno sanitario
	@Getter
	@Setter
	private Boolean esActividadRelleno;
	
	@Getter
	@Setter
	private List<EntidadPmaViabilidad> listaPlanPmaVia, listaPlanPmaViaAdicionar;
	
	@Getter
	@Setter
	private ViabilidadTecnicaProyecto viabilidadTecnica;
	
	@Getter
	@Setter
	private List<AspectoViabilidadTecnica> listaPlanAspectoViabilidad;
	
	@Getter
	@Setter
	private List<AspectoViabilidadTecnica> listaPlanAspectoVi;
	
	@Getter
    @Setter
    private List<MedioFrecuenciaMedida> listaPlanPmaViabilidadParaAdicionar;
	
	@Getter
	@Setter
	private PmaViabilidadTecnica planPmaViaEliminado, planPmaViaNuevo;
	
	@Getter
	@Setter
	private List<PmaViabilidadTecnica> listaPlanPmaViaEliminar;
	
	@Getter
	@Setter
	private FaseViabilidadTecnicaRcoa faseViabilidadTecnica;
	
	private boolean pmaViabilidad;
    
    @PostConstruct
	public void init(){
		tipoPlan = 1;
		ponderacion = 12.5;
		validarDatos = false;
		try{
			sub_plan_cierre_abandono = "Formato para Subplan Cierre y Abandono.pdf";
			sub_plan_cierre_abandono_byte =  documentosPlantillasFacade.descargarDocumentoPorNombre(sub_plan_cierre_abandono);
		}catch(Exception e){
            LOG.error("Error al recuperar archivo de fornato de subplan de cierre y abandono", e);
		}
		
		validarActividadRellenoSanitario();
		registroAmbiental = marcoLegalReferencialBean.getRegistroAmbientalRcoa();
		proyectoLicenciaCoa = marcoLegalReferencialBean.getProyectoLicenciaCoa();
		listaPlanPmaAdicionar = new ArrayList<EntidadPma>();
		listaPlanPmaEliminar = new ArrayList<PmaAceptadoRegistroAmbiental>();
		planPmaElimindo = new PmaAceptadoRegistroAmbiental();
		planPmaNuevo = new PmaAceptadoRegistroAmbiental();
		documentoSubPlan = new DocumentoRegistroAmbiental();
		listaPlanPmaViaEliminar = new ArrayList<PmaViabilidadTecnica>();
		listaPlanPmaViaAdicionar = new ArrayList<EntidadPmaViabilidad>();
		planPmaViaNuevo = new PmaViabilidadTecnica();
		planPmaViaEliminado = new PmaViabilidadTecnica();
	    faseRegistroAmbientalController.cargarFasesProyecto();	   
	    
	    if(esActividadRelleno){
	    	if(faseRegistroAmbientalController.validateDataViabilidad()){
	    		faseViabilidadTecnica = faseRegistroAmbientalController.obtenerFase();
	    		if(pmaViabilidad)
	    			cargarPmaPorActividadesViabilidad();
	    		else
	    			cargarPmaPoractividades();
	    	}else{
	    		if (registroAmbiental != null && registroAmbiental.getId() != null) {
					menuCoaBean.setPageMarcoLegal(false);
				}
				menuCoaBean.setPageDescripcion(true);
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('dlgPasoFaltante').show();");
	    	}  	    	
	    }else{
	    	 if(faseRegistroAmbientalController.validateData()){
	    		 cargarPmaPoractividades();
	    	 }else{
				if (registroAmbiental != null && registroAmbiental.getId() != null) {
					menuCoaBean.setPageMarcoLegal(false);
				}
				menuCoaBean.setPageDescripcion(true);
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('dlgPasoFaltante').show();");
	    	 }
	    }
	
	    // verifico si tiene rgd
	    if(proyectoLicenciaCoa.getGeneraDesechos()){	    	
	    	RegistroGeneradorDesechosRcoa registroGeneradorDesechos = registroGeneradorDesechosRcoaFacade.buscarRGDPorProyectoRcoa(proyectoLicenciaCoa.getId());
	    	if(registroGeneradorDesechos != null){
		    	codigoRGD = registroGeneradorDesechos.getCodigo();	    		    	
	    	}else{
	    		codigoRGD = "";
	    	}
	    }else{
	    	codigoRGD = "";
	    }
	    // verifico si realiza gestion propia
	    if(esActividadRelleno){
	    	
	    	
	    }else{
	    	if(listaPlanPma != null){
		    	for (EntidadPma entidadPlanInicial : listaPlanPma){
		    		if(entidadPlanInicial.getPlanId().equals(5)){
			    		for (PmaAceptadoRegistroAmbiental medidaPma : entidadPlanInicial.getMedidasProyecto()) {
			    			if(proyectoLicenciaCoa.getEsGestionPropia() != null && proyectoLicenciaCoa.getEsGestionPropia()
			    					&& medidaPma.getMedidaVerificacionPma() != null 
			    					&& medidaPma.getMedidaVerificacionPma().isRgdGestioPropia()){
			    				medidaPma.getMedidaVerificacionPma().setObligatorio(true);
			    			}
			    			if(medidaPma.getMedidaVerificacionPma() != null && medidaPma.getMedidaVerificacionPma().isMostrarRGD()){
			    				medidaPma.getMedidaVerificacionPma().setFrecuencia(codigoRGD);
			    				medidaPma.setFrecuencia(codigoRGD);
			    			}
			    		}
		    		}
			    }
	    	}
	    }
	    
	    
    	
	}
    
    private void validarActividadRellenoSanitario(){
		try {
			esActividadRelleno = false;
			pmaViabilidad = false;
			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = new ArrayList<ProyectoLicenciaCuaCiuu>();
			listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(faseRegistroAmbientalController.getRegistroAmbiental().getProyectoCoa());
			
			for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
				if(actividad.getCatalogoCIUU().getCodigo().equals("E3821.01") || actividad.getCatalogoCIUU().getCodigo().equals("E3821.01.01")){
					List<ViabilidadTecnicaProyecto> lista = viabilidadTecnicaProyectoFacade.buscarPorProyecto(faseRegistroAmbientalController.getRegistroAmbiental().getProyectoCoa().getId());
					
					if(lista!= null && !lista.isEmpty()){
						viabilidadTecnica = lista.get(0);
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
    
    public boolean validarConcesionEnMaate(ProyectoLicenciaCuaCiuu actividad){
    	try {			
    		List<ProyectoLicenciaAmbientalConcesionesMineras> listaC = proyectoConcesionFacade.cargarConcesiones(proyectoLicenciaCoa);
    		
    		boolean tieneContratoMae = false;
    		
    		if(listaC != null & !listaC.isEmpty()){
    			for(ProyectoLicenciaAmbientalConcesionesMineras con : listaC){
    				if(con.getProyectoLicenciaCuaCiuu().getId().equals(actividad.getId()) && con.getTieneContrato() != null && con.getTieneContrato()){
    					tieneContratoMae = true;
    					break;
    				}
    			}
    		}
    		return tieneContratoMae;
    		
		} catch (Exception e) {
			e.printStackTrace();
		}    	
    	return false;
    }

	public void cargarPmaPoractividades(){
		// obtengo el pma registrado en el proyecto
		// obtengo las actividades seleccionadas en informacion preliminar
		listaactividades = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(registroAmbiental.getProyectoCoa());
		// obtengo los codigos de las actividades principar y complementarias
		String actividadPrincipal="", actividadSecundaria = "";
		Integer idConsideracion=0;
		// cargo el plan de la actividad principal
		for (ProyectoLicenciaCuaCiuu objActividad : listaactividades) {
			if(objActividad.getPrimario() == null || objActividad.getPrimario()){													
				
				actividadPrincipal = objActividad.getCatalogoCIUU().getCodigo();	
				
				if(objActividad.getCatalogoCIUU().getTipoSector().getNombre().toUpperCase().equals("MINERÍA")){
					/**
					 * Solo se valida para los que tienen subactividad ya que las actividades mineras no tomadas en cuenta no tiene subactividad.
					 */
					if(objActividad.getSubActividad() != null && objActividad.getValorOpcion() != null && objActividad.getValorOpcion()){
						if(validarConcesionEnMaate(objActividad)){
							idConsideracion = objActividad.getSubActividad().getId();
						}					
					}			
				}else{
					if(objActividad.getCatalogoCIUU().getConPma() && objActividad.getSubActividad() != null){
						idConsideracion = objActividad.getSubActividad().getId();
					}
				}								
								
					listaPlanPma = planManejoAmbientalCoaFacade.obtenerPmaActividad(actividadPrincipal, tipoPlan, true, registroAmbiental.getId(), proyectoLicenciaCoa, idConsideracion);
					// obtengo los documentos por cada subplan
					listaDocumentoPlanPma = registroAmbientalSubPlanPmaCoaFacade.obtenerSubPlanPorProyecto(registroAmbiental);
					try{
						index = 0;
						for (EntidadPma entidadPlanInicial : listaPlanPma){
							boolean existeArchivo = false;
							for (RegistroAmbientalSubPlanPma registroAmbientalSubPlanPma : listaDocumentoPlanPma) {
								if(entidadPlanInicial.getPlanId() != null && entidadPlanInicial.getPlanId().equals(registroAmbientalSubPlanPma.getPlanManejoAmbientalPma().getId())){
									listaDocumentoSubPlan = documentosFacade.documentoXTablaIdXIdDocLista(registroAmbientalSubPlanPma.getId(), RegistroAmbientalSubPlanPma.class.getSimpleName(), TipoDocumentoSistema.RCOA_SUBPLAN_MANEJO_AMBIENTAL);	
									if(listaDocumentoSubPlan != null && listaDocumentoSubPlan.size() > 0){
										entidadPlanInicial.setListaDocumentoAdjunto(listaDocumentoSubPlan);
										existeArchivo = true;
										break;
									}
								}
							}
							if(!existeArchivo){
								entidadPlanInicial.setListaDocumentoAdjunto(new ArrayList<DocumentoRegistroAmbiental>());
							}
							listaPlanAspectoAmbiental = new ArrayList<AspectoAmbientalPma>();
							listaPlanAspectoAmbiental = planManejoAmbientalCoaFacade.obtenerAspectoAmbientalPorPlan(entidadPlanInicial.getPlanId());
							entidadPlanInicial.setListaAspectoAmbiental(listaPlanAspectoAmbiental);
							//obtengo los pma nuevos ingresados por el usuario
							List<PmaAceptadoRegistroAmbiental> listaAspectosNuevos = planManejoAmbientalCoaFacade.buscarMedidaaAmbientalesIngresadasPorRegistroAmbiental(entidadPlanInicial.getPlanId(), registroAmbiental.getId());
							if(listaAspectosNuevos != null && listaAspectosNuevos.size() > 0){
								entidadPlanInicial.getMedidasProyecto().addAll(listaAspectosNuevos);
							}
						}
					}catch(CmisAlfrescoException e){
						
					}
			break;
			}
		}

		// insero los listas de los planes que no tienen item seleccionado para mostrar el acordion
		for (EntidadPma entidadPlanAux : listaPlanPmaAdicionar){
			boolean existe = false;
			for (EntidadPma entidadPlanInicial : listaPlanPma){
				if(entidadPlanAux.getPlanNombre().equals(entidadPlanInicial.getPlanNombre())){
					existe = true;
					break;
				}
			}
			if(!existe){
				EntidadPma entidad = new EntidadPma();
				entidad.setPlanNombre(entidadPlanAux.getPlanNombre());
				entidad.setMedidas(new ArrayList<MedidaVerificacionPma>());
				entidad.setMedidasProyecto(new ArrayList<PmaAceptadoRegistroAmbiental>());
				listaPlanPma.add(entidad);
			}
		}
		validarDatos = validarDatosIngresados(false);
	}
	
	public void cargarPmaPorActividadesViabilidad(){
		try {
			listaPlanPmaVia = pmaViabilidadTecnicaFacade.obtenerPma(viabilidadTecnica.getManejaDesechosSanitarios(), tipoPlan, viabilidadTecnica.getId(), faseViabilidadTecnica);			
			listaDocumentoPlanPma = registroAmbientalSubPlanPmaCoaFacade.obtenerSubPlanPorProyecto(registroAmbiental);
			index = 0;
			
			for(EntidadPmaViabilidad entidadPlanInicial : listaPlanPmaVia){
				
				boolean existeArchivo = false;
				for (RegistroAmbientalSubPlanPma registroAmbientalSubPlanPma : listaDocumentoPlanPma) {
					if(entidadPlanInicial.getPlanId() != null && entidadPlanInicial.getPlanId().equals(registroAmbientalSubPlanPma.getPlanManejoAmbientalPma().getId())){
						listaDocumentoSubPlan = documentosFacade.documentoXTablaIdXIdDocLista(registroAmbientalSubPlanPma.getId(), RegistroAmbientalSubPlanPma.class.getSimpleName(), TipoDocumentoSistema.RCOA_SUBPLAN_MANEJO_AMBIENTAL);	
						if(listaDocumentoSubPlan != null && listaDocumentoSubPlan.size() > 0){
							entidadPlanInicial.setListaDocumentoAdjunto(listaDocumentoSubPlan);
							existeArchivo = true;
							break;
						}
					}
				}
				if(!existeArchivo){
					entidadPlanInicial.setListaDocumentoAdjunto(new ArrayList<DocumentoRegistroAmbiental>());
				}				
								
				listaPlanAspectoVi = new ArrayList<>();
				listaPlanAspectoVi = aspectoViabilidadTecnicaFacade.obtenerAspectoAmbientalPorPlan(entidadPlanInicial.getPlanId());
				entidadPlanInicial.setListaAspectoAmbiental(listaPlanAspectoVi);
				
				//busqueda de los planes ingresados por el usuario
				List<PmaViabilidadTecnica> listaAspectosNuevos = pmaViabilidadTecnicaFacade.buscarMedidasAmbientalesIngresadasPorViabilidad(entidadPlanInicial.getPlanId(), viabilidadTecnica.getId());
						
				if(listaAspectosNuevos != null && listaAspectosNuevos.size() > 0){
					entidadPlanInicial.getMedidasProyecto().addAll(listaAspectosNuevos);
				}
			}			
			
			for(EntidadPmaViabilidad entidadPlanAux : listaPlanPmaViaAdicionar){
				boolean existe = false;
				for(EntidadPmaViabilidad entidadPlanInicial : listaPlanPmaVia){
					if(entidadPlanAux.getPlanNombre().equals(entidadPlanInicial.getPlanNombre())){
						existe = true;
						break;
					}
				}
				if(!existe){
					EntidadPmaViabilidad entidad = new EntidadPmaViabilidad();
					entidad.setPlanNombre(entidadPlanAux.getPlanNombre());
					entidad.setMedidas(new ArrayList<MedioFrecuenciaMedida>());
					entidad.setMedidasProyecto(new ArrayList<PmaViabilidadTecnica>());
					listaPlanPmaVia.add(entidad);
				}
			}
			
			for (EntidadPma entidadPlanAux : listaPlanPmaAdicionar){
				boolean existe = false;
				for (EntidadPma entidadPlanInicial : listaPlanPma){
					if(entidadPlanAux.getPlanNombre().equals(entidadPlanInicial.getPlanNombre())){
						existe = true;
						break;
					}
				}
				if(!existe){
					EntidadPma entidad = new EntidadPma();
					entidad.setPlanNombre(entidadPlanAux.getPlanNombre());
					entidad.setMedidas(new ArrayList<MedidaVerificacionPma>());
					entidad.setMedidasProyecto(new ArrayList<PmaAceptadoRegistroAmbiental>());
					listaPlanPma.add(entidad);
				}
			}
			validarDatos = validarDatosIngresadosViabilidad(false);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void mostrarListaPlanAdicional(String planNombre){
		listaPlanPmaParaAdicionar = new ArrayList<MedidaVerificacionPma>();
		for (EntidadPma entidadPlan : listaPlanPmaAdicionar){
			if(entidadPlan.getPlanNombre().equals(planNombre)){
				listaPlanPmaParaAdicionar = entidadPlan.getMedidas();
				break;
			}
		}
	}
	
	public void mostrarListaPlanViabilidadAdicional(String planNombre){
		listaPlanPmaViabilidadParaAdicionar = new ArrayList<>();
		for (EntidadPmaViabilidad entidadPlan : listaPlanPmaViaAdicionar){
			if(entidadPlan.getPlanNombre().equals(planNombre)){
				listaPlanPmaViabilidadParaAdicionar = entidadPlan.getMedidas();
				break;
			}
		}
	}

	public void agregarPlan(MedidaVerificacionPma objMedida){
		boolean existe = false;
		for (EntidadPma entidadPlan : listaPlanPma){
			if(entidadPlan.getPlanNombre().toString().equals(objMedida.getAspectoAmbientalPma().getPlanManejoAmbientalPma().getDescripcion())){
				for (PmaAceptadoRegistroAmbiental medidaPma : entidadPlan.getMedidasProyecto()) {
					if(medidaPma.getMedidaVerificacionPma().getId().equals(objMedida.getId())){
						existe = true;
						break;
					}
				}
				if(!existe){
					PmaAceptadoRegistroAmbiental medidaPma = new PmaAceptadoRegistroAmbiental();
					medidaPma.setMedidaVerificacionPma(objMedida);
					entidadPlan.getMedidasProyecto().add(medidaPma);
				}else{
					JsfUtil.addMessageError("El subplan ya se encuentra agregado.");
				}
			}
		}
	}
	
	public void agregarPlanViabilidad(MedioFrecuenciaMedida objMedida){
		boolean existe = false;
		for (EntidadPmaViabilidad entidadPlan : listaPlanPmaVia){
			if(entidadPlan.getPlanNombre().toString().equals(objMedida.getAspectoViabilidad().getPlanManejoAmbientalPma().getDescripcion())){
				for (PmaViabilidadTecnica medidaPma : entidadPlan.getMedidasProyecto()) {
					if(medidaPma.getMedioFrecuenciaMedida().getId().equals(objMedida.getId())){
						existe = true;
						break;
					}
				}
				if(!existe){
					PmaViabilidadTecnica medidaPma = new PmaViabilidadTecnica();
					medidaPma.setMedioFrecuenciaMedida(objMedida);
					entidadPlan.getMedidasProyecto().add(medidaPma);
				}else{
					JsfUtil.addMessageError("El subplan ya se encuentra agregado.");
				}
			}
		}
	}

	public boolean agregarNuevoPMA(){
		if(valorPlazoFecha == null && tipoPlazo.equals("fecha")){
			JsfUtil.addMessageError("El campo plazo es requerido.");
			return false;
		}
		
		planPmaNuevo.setMedida(planPmaNuevo.getMedida().toString().replaceAll("\\p{C}", " "));
		
		planPmaNuevo.setAceptado(true);
		planPmaNuevo.setEstado(true);
		if(tipoPlazo.equals("fecha")){
			planPmaNuevo.setPlazoFecha(valorPlazoFecha);
		}
		planPmaNuevo.setRegistroAmbientalId(registroAmbiental.getId());
		planManejoAmbientalCoaFacade.guardar(planPmaNuevo);
		index = -1;
		for (EntidadPma entidadPlan : listaPlanPma){
			index += 1;
			if(planPmaNuevo.getAspectoAmbientalPma().getPlanManejoAmbientalPma().getId().equals(entidadPlan.getPlanId())){
				for (PmaAceptadoRegistroAmbiental medida: entidadPlan.getMedidasProyecto()){
					if( planPmaNuevo.getId() != null && planPmaNuevo.getId().equals(medida.getId()) && planPmaNuevo.getPlazo() != null){
						medida.setPlazo(planPmaNuevo.getPlazo());
						break;
					}
				}
			}
			if(entidadPlan.getPlanId().equals(planPmaNuevo.getSubPlanId())){
				entidadPlan.getMedidasProyecto().add(planPmaNuevo);
				break;
			}
		}
		valorPlazo = null;
		valorPlazoFecha = null;
		tipoPlazo= "";
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		return true;
	}
	
	public boolean agregarNuevoPMAViabilidad(){
		if(valorPlazoFecha == null && tipoPlazo.equals("fecha")){
			JsfUtil.addMessageError("El campo plazo es requerido.");
			return false;
		}
		planPmaViaNuevo.setMedidaPropuesta(planPmaViaNuevo.getMedidaPropuesta().toString().replaceAll("\\p{C}", " "));
				
		planPmaViaNuevo.setAceptado(true);
		planPmaViaNuevo.setEstado(true);
		if(tipoPlazo.equals("fecha")){
			planPmaViaNuevo.setPlazoFecha(valorPlazoFecha);
		}
		planPmaViaNuevo.setFaseViabilidadTecnica(faseViabilidadTecnica);
		pmaViabilidadTecnicaFacade.guardar(planPmaViaNuevo, JsfUtil.getLoggedUser());
		
		index = -1;
		for (EntidadPmaViabilidad entidadPlan : listaPlanPmaVia){
			index += 1;
			if(planPmaViaNuevo.getAspectoViabilidadTecnica().getPlanManejoAmbientalPma().getId().equals(entidadPlan.getPlanId())){
				for (PmaViabilidadTecnica medida: entidadPlan.getMedidasProyecto()){
					if( planPmaViaNuevo.getId() != null && planPmaViaNuevo.getId().equals(medida.getId()) && planPmaViaNuevo.getPlazo() != null){
						medida.setPlazo(planPmaViaNuevo.getPlazo());
						break;
					}
				}
			}
			if(entidadPlan.getPlanId().equals(planPmaViaNuevo.getSubPlanId())){
				entidadPlan.getMedidasProyecto().add(planPmaViaNuevo);
				break;
			}
		}
		valorPlazo = null;
		valorPlazoFecha = null;
		tipoPlazo= "";
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		return true;
	}

	public void mostrarAdicionarPlanAdicional(EntidadPma objetoEntidad){
		planPmaNuevo = new PmaAceptadoRegistroAmbiental();
		planPmaNuevo.setSubPlanId(objetoEntidad.getPlanId());
		listaPlanAspectoAmbiental = objetoEntidad.getListaAspectoAmbiental();
	}
	
	public void mostrarAdicionarPlanViabilidadAdicional(EntidadPmaViabilidad objetoEntidad){
		planPmaViaNuevo = new PmaViabilidadTecnica();
		planPmaViaNuevo.setSubPlanId(objetoEntidad.getPlanId());
		listaPlanAspectoVi = objetoEntidad.getListaAspectoAmbiental();		
	}
	
	public void eliminarPMA(PmaAceptadoRegistroAmbiental objMedida){
		index = -1;
		for (EntidadPma entidadPlan : listaPlanPma){
			index += 1;
			if(entidadPlan.getPlanNombre().toString().equals(objMedida.getMedidaVerificacionPma().getAspectoAmbientalPma().getPlanManejoAmbientalPma().getDescripcion())){
				for (PmaAceptadoRegistroAmbiental medida: entidadPlan.getMedidasProyecto()){
					if(medida.getMedidaVerificacionPma().getId().equals(objMedida.getMedidaVerificacionPma().getId())){
						entidadPlan.getMedidasProyecto().remove(medida);
						medida.setAceptado(false);
						medida.setRegistroAmbientalId(registroAmbiental.getId());
						planManejoAmbientalCoaFacade.guardar(medida);
						listaPlanPmaEliminar.add(medida);
						break;
					}
				}
				break;
			}
		}
	}
	
	public void eliminarPMAViabilidad(PmaViabilidadTecnica objMedida){
		index = -1;
		for (EntidadPmaViabilidad entidadPlan : listaPlanPmaVia){
			index += 1;
			if(entidadPlan.getPlanNombre().toString().equals(objMedida.getMedioFrecuenciaMedida().getAspectoViabilidad().getPlanManejoAmbientalPma().getDescripcion())){
				for (PmaViabilidadTecnica medida: entidadPlan.getMedidasProyecto()){
					if(medida.getMedioFrecuenciaMedida().getId().equals(objMedida.getMedioFrecuenciaMedida().getId())){
						entidadPlan.getMedidasProyecto().remove(medida);
						medida.setAceptado(false);
						pmaViabilidadTecnicaFacade.guardar(medida, JsfUtil.getLoggedUser());
						listaPlanPmaViaEliminar.add(medida);
						break;
					}
				}
				break;
			}
		}
	}

	public void eliminarPlanPMA(PmaAceptadoRegistroAmbiental objMedida){
		planPmaElimindo = new PmaAceptadoRegistroAmbiental();
		for (EntidadPma entidadPlan : listaPlanPma){
			if(entidadPlan.getPlanNombre().toString().equals(objMedida.getMedidaVerificacionPma().getAspectoAmbientalPma().getPlanManejoAmbientalPma().getDescripcion())){
				for (PmaAceptadoRegistroAmbiental medida: entidadPlan.getMedidasProyecto()){
					if(medida.getMedidaVerificacionPma().getId().equals(objMedida.getMedidaVerificacionPma().getId())){
						planPmaElimindo = medida;
						break;
					}
				}
			}
		}
	}
	
	//Elimina un registro de la tabla de los subplanes
	public void eliminarPlanPMAViabilidad(PmaViabilidadTecnica objMedida){
		
		planPmaViaEliminado = new PmaViabilidadTecnica();
		for (EntidadPmaViabilidad entidadPlan : listaPlanPmaVia){
			if(entidadPlan.getPlanNombre().toString().equals(objMedida.getMedioFrecuenciaMedida().getAspectoViabilidad().getPlanManejoAmbientalPma().getDescripcion())){
				for (PmaViabilidadTecnica medida: entidadPlan.getMedidasProyecto()){
					if(medida.getMedioFrecuenciaMedida().getId().equals(objMedida.getMedioFrecuenciaMedida().getId())){
						planPmaViaEliminado = medida;
						break;
					}
				}
			}
		}
	}

	public void eliminarPlanPMANuevo(PmaAceptadoRegistroAmbiental objMedida){
		index = -1;
		for (EntidadPma entidadPlan : listaPlanPma){
			index += 1;
			if(entidadPlan.getPlanNombre().toString().equals(objMedida.getAspectoAmbientalPma().getPlanManejoAmbientalPma().getDescripcion())){
				for (int i = 0; i < entidadPlan.getMedidasProyecto().size(); i++) {
					if(entidadPlan.getMedidasProyecto().get(i).getId().equals(objMedida.getId())){
						entidadPlan.getMedidasProyecto().get(i).setOrden(90);
					}else{
						entidadPlan.getMedidasProyecto().get(i).setOrden(2);
					}
				}
				Collections.sort(entidadPlan.getMedidasProyecto());
				
				for (PmaAceptadoRegistroAmbiental medida: entidadPlan.getMedidasProyecto()){
					if(medida.getAspectoAmbientalPma() != null && medida.getId().equals(objMedida.getId()) && medida.getAspectoAmbientalPma().getId().equals(objMedida.getAspectoAmbientalPma().getId())){

						listaPlanPmaEliminar.add(medida);
						// guardo los planes eliminados
						String listaIdEliminados = "0";
						for (PmaAceptadoRegistroAmbiental objMedidas : listaPlanPmaEliminar){
							if(objMedidas.getAspectoAmbientalPma() != null){
								listaIdEliminados += ", "+objMedidas.getId();
							}
						}
						planManejoAmbientalCoaFacade.eliminarPlanesAgregadosNuevos(listaIdEliminados, registroAmbiental.getId());
						break;
					}
				}
				entidadPlan.getMedidasProyecto().removeAll(listaPlanPmaEliminar);
				break;
			}
		}
	}
	
	public void eliminarPlanPMANuevoViabilidad(PmaViabilidadTecnica objMedida){
		try {
			index = -1;
			for (EntidadPmaViabilidad entidadPlan : listaPlanPmaVia){
				index += 1;
				if(entidadPlan.getPlanNombre().toString().equals(objMedida.getAspectoViabilidadTecnica().getPlanManejoAmbientalPma().getDescripcion())){
					for (int i = 0; i < entidadPlan.getMedidasProyecto().size(); i++) {
						if(entidadPlan.getMedidasProyecto().get(i).getId() != null && entidadPlan.getMedidasProyecto().get(i).getId().equals(objMedida.getId())){
							entidadPlan.getMedidasProyecto().get(i).setOrden(90);
						}else{
							entidadPlan.getMedidasProyecto().get(i).setOrden(2);
						}
					}
					Collections.sort(entidadPlan.getMedidasProyecto());
					
					for (PmaViabilidadTecnica medida: entidadPlan.getMedidasProyecto()){
						if(medida.getAspectoViabilidadTecnica() != null && medida.getId().equals(objMedida.getId()) && 
								medida.getAspectoViabilidadTecnica().getId().equals(objMedida.getAspectoViabilidadTecnica().getId())){

							listaPlanPmaViaEliminar.add(medida);
							// guardo los planes eliminados
							String listaIdEliminados = "";
							for (PmaViabilidadTecnica objMedidas : listaPlanPmaViaEliminar){
								if(objMedidas.getAspectoViabilidadTecnica() != null){
									if(listaIdEliminados.equals("")){
										listaIdEliminados =objMedidas.getId().toString();
									}else{
										listaIdEliminados += ", "+objMedidas.getId();
									}									
								}
							}
							pmaViabilidadTecnicaFacade.eliminarPlanesAgregadosNuevos(listaIdEliminados);
							break;
						}
					}
					entidadPlan.getMedidasProyecto().removeAll(listaPlanPmaViaEliminar);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void editarPlanPMANuevo(PmaAceptadoRegistroAmbiental pma, List<AspectoAmbientalPma> listaAspectoAmbiental){
		planPmaNuevo = pma;
		if(pma.getPlazo() != null)
			valorPlazo = pma.getPlazo();
		listaPlanAspectoAmbiental = listaAspectoAmbiental;
	}
	
	public void editarPlanPMAViabilidadNuevo(PmaViabilidadTecnica pma, List<AspectoViabilidadTecnica> listaAspectoAmbiental){
		planPmaViaNuevo = pma;
		listaPlanAspectoVi = listaAspectoAmbiental;
	}

	public boolean validarDatos(boolean mostrarMensaje){
		boolean validar = true;
		List<EntidadPma> listaPlanPmaAux = listaPlanPma;
		if(listaPlanPmaAux != null && listaPlanPmaAux.size() > 0){
			for (EntidadPma entidadPma : listaPlanPmaAux) {
				if(entidadPma.getMedidasProyecto() == null || entidadPma.getMedidasProyecto().size() == 0){
					if(mostrarMensaje)
						JsfUtil.addMessageError("Debe ingresar el "+entidadPma.getPlanNombre());
					validar = false;
				}
			}
		}
		return validar;
	}

	public boolean validarDatosIngresados(boolean mostrarMensaje){
		boolean validar = true;
		List<EntidadPma> listaPlanPmaAux = listaPlanPma;
		if(listaPlanPmaAux != null && listaPlanPmaAux.size() > 0){
			for (EntidadPma entidadPma : listaPlanPmaAux) {
				if(entidadPma.getMedidasProyecto() == null || entidadPma.getMedidasProyecto().size() == 0){
					if(mostrarMensaje)
						JsfUtil.addMessageError("Debe ingresar el "+entidadPma.getPlanNombre());
					validar = false;
				}else{
					for (PmaAceptadoRegistroAmbiental objPlan : entidadPma.getMedidasProyecto()) {
						if(objPlan.getId() == null ){
							validar = false;
							break;
						}else if(objPlan.getPlazoFecha() == null){
							if(objPlan.getMedidaVerificacionPma() != null && objPlan.getMedidaVerificacionPma().isAplicaPlazo()){
								validar = false;
								break;
							}
						}
					}
					if(!validar){
						break;
					}
				}
			}
		}
		return validar;
	}
	
	public boolean validarDatosIngresadosViabilidad(boolean mostrarMensaje){
		boolean validar = true;
		List<EntidadPmaViabilidad> listaPlanPmaAux = listaPlanPmaVia;
		if(listaPlanPmaAux != null && listaPlanPmaAux.size() > 0){
			for (EntidadPmaViabilidad entidadPma : listaPlanPmaAux) {				
				if(entidadPma.getMedidasProyecto() == null || entidadPma.getMedidasProyecto().size() == 0){
					if(mostrarMensaje)
						JsfUtil.addMessageError("Debe ingresar el "+entidadPma.getPlanNombre());
					validar = false;
				}else{
					for (PmaViabilidadTecnica objPlan : entidadPma.getMedidasProyecto()) {
						if(objPlan.getId() == null ){
							validar = false;
							break;
						}else if(objPlan.getPlazoFecha() == null){
							if(objPlan.getMedioFrecuenciaMedida() != null && objPlan.getMedioFrecuenciaMedida().getPlazo()){
								validar = false;
								break;
							}
						}
					}
					if(!validar){
						break;
					}
				}
			}
		}
		return validar;
	}

	public boolean guardarPma(EntidadPma objEntidadPlan){
		try{
			validarDatos = false;
			if(listaPlanPma == null || listaPlanPma.size() == 0){
				JsfUtil.addMessageError("Debe ingresar el plan de manejo ambiental.");
				return false;
			}
			// valido los datos a gardar
			boolean existeError=false;
			for (PmaAceptadoRegistroAmbiental objMedida: objEntidadPlan.getMedidasProyecto()){
				if(objMedida.getPlazoFecha() == null){
					existeError = true;
				}
			}
			// guardo los planes eliminados
			String listaIdEliminados = "0";
			for (PmaAceptadoRegistroAmbiental objMedidas : listaPlanPmaEliminar){
				if(objMedidas.getAspectoAmbientalPma() != null){
					listaIdEliminados += ", "+objMedidas.getAspectoAmbientalPma().getId();
				}
			}
			// inicializo el index
			index = -1;
			for (EntidadPma entidadPlan : listaPlanPma){
				index += 1;
				if(entidadPlan.getPlanId().equals(objEntidadPlan.getPlanId())){
					break;
				}
			}

			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("acdPlan");
			planManejoAmbientalCoaFacade.eliminarPlanesAgregadosNuevos(listaIdEliminados, registroAmbiental.getId());
			// guardo los planes agregados
			for (PmaAceptadoRegistroAmbiental objMedida: objEntidadPlan.getMedidasProyecto()){
				objMedida.setRegistroAmbientalId(registroAmbiental.getId());
	
				if(codigoRGD != "" && objMedida.getMedidaVerificacionPma() != null && objMedida.getMedidaVerificacionPma().getFrecuencia().equals(codigoRGD)){
					objMedida.setFrecuencia(codigoRGD);
				}
				objMedida.setPonderacion(ponderacion / objEntidadPlan.getMedidasProyecto().size());
				objMedida.setEstado(true);
				objMedida.setAceptado(true);
				planManejoAmbientalCoaFacade.guardar(objMedida);
			}
			// para guardar documentos
			
				for (EntidadPma entidadPlanInicial : listaPlanPma){
					if(entidadPlanInicial.getPlanId() != null && entidadPlanInicial.getPlanId().equals(objEntidadPlan.getPlanId())){
						boolean existeRegistroSubPlan = false;
						for (RegistroAmbientalSubPlanPma registroAmbientalSubPlanPma : listaDocumentoPlanPma) {
							if(objEntidadPlan.getPlanId() != null && objEntidadPlan.getPlanId().equals(registroAmbientalSubPlanPma.getPlanManejoAmbientalPma().getId())){
								if(registroAmbientalSubPlanPma.getId() == null){
									registroAmbientalSubPlanPmaCoaFacade.guardar(registroAmbientalSubPlanPma);
								}
								// deshabilito documentos ingresados anteriormenet

								for (DocumentoRegistroAmbiental objDocumentoPma : objEntidadPlan.getListaDocumentoAdjunto()) {
									if (objDocumentoPma != null && objDocumentoPma.getId() == null && objDocumentoPma.getContenidoDocumento() != null ){
										documentoSubPlan = documentosFacade.ingresarDocumento(objDocumentoPma, registroAmbientalSubPlanPma.getId(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RCOA_SUBPLAN_MANEJO_AMBIENTAL, objDocumentoPma.getNombre(), RegistroAmbientalSubPlanPma.class.getSimpleName(), marcoLegalReferencialBean.getIdProceso());
										existeRegistroSubPlan = true;
									}
								}
								break;
							}
						}
						if(!existeRegistroSubPlan){
							RegistroAmbientalSubPlanPma subPlan = new RegistroAmbientalSubPlanPma();
							PlanManejoAmbientalPma objPlanManejoAmbiental = planManejoAmbientalCoaFacade.obtenerPlanPorId(objEntidadPlan.getPlanId());
							subPlan.setEstado(true);
							subPlan.setPlanManejoAmbientalPma(objPlanManejoAmbiental);
							subPlan.setRegistroAmbientalId(registroAmbiental.getId());
							registroAmbientalSubPlanPmaCoaFacade.guardar(subPlan);
							for (DocumentoRegistroAmbiental objDocumentoPma : objEntidadPlan.getListaDocumentoAdjunto()) {
								if (objDocumentoPma != null && objDocumentoPma.getId() == null && objDocumentoPma.getContenidoDocumento() != null ){
									documentoSubPlan = documentosFacade.ingresarDocumento(objDocumentoPma, subPlan.getId(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RCOA_SUBPLAN_MANEJO_AMBIENTAL, "documento "+objEntidadPlan.getPlanNombre()+".pdf", RegistroAmbientalSubPlanPma.class.getSimpleName(), marcoLegalReferencialBean.getIdProceso());
									existeRegistroSubPlan = true;
								}
							}
						}
					}
				}
			validarDatos = validarDatosIngresados(false);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	
			if(existeError){
				return false;
			}
			return true;
		}catch(Exception e){
			JsfUtil.addMessageError("No se puede actualizar la Ficha.");
			return false;
		}
	}
	
	public boolean guardarPmaViabilidad(EntidadPmaViabilidad objEntidadPlan){
		try{
			validarDatos = false;
			if(listaPlanPmaVia == null || listaPlanPmaVia.size() == 0){
				JsfUtil.addMessageError("Debe ingresar el plan de manejo ambiental.");
				return false;
			}
			// valido los datos a gardar
			boolean existeError=false;
			for (PmaViabilidadTecnica objMedida: objEntidadPlan.getMedidasProyecto()){
				if(objMedida.getPlazo() == null){
					existeError = true;
				}
			}
			// guardo los planes eliminados
			String listaIdEliminados = "0";
			for (PmaViabilidadTecnica objMedidas : listaPlanPmaViaEliminar){
				if(objMedidas.getAspectoViabilidadTecnica() != null && objMedidas.getId() != null){
					listaIdEliminados += ", "+objMedidas.getId();
				}
			}
			// inicializo el index
			index = -1;
			for (EntidadPmaViabilidad entidadPlan : listaPlanPmaVia){
				index += 1;
				if(entidadPlan.getPlanId().equals(objEntidadPlan.getPlanId())){
					break;
				}
			}
			
			pmaViabilidadTecnicaFacade.eliminarPlanesAgregadosNuevos(listaIdEliminados);
			// guardo los planes agregados
			for (PmaViabilidadTecnica objMedida: objEntidadPlan.getMedidasProyecto()){
				
	
				if(codigoRGD != "" && objMedida.getMedioFrecuenciaMedida() != null && 
						objMedida.getMedioFrecuenciaMedida().getFrecuencia().equals(codigoRGD)){
					objMedida.setFrecuencia(codigoRGD);
				}
				objMedida.setPonderacion(ponderacion / objEntidadPlan.getMedidasProyecto().size());
				objMedida.setEstado(true);
				objMedida.setAceptado(true);
				
				if(objMedida.getPlazoFecha() != null){
					Date fecha = objMedida.getPlazoFecha();
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String fechaString = sdf.format(fecha);
					objMedida.setPlazo(fechaString);
				}
				pmaViabilidadTecnicaFacade.guardar(objMedida, JsfUtil.getLoggedUser());
			}
			// para guardar documentos
			
			for (EntidadPmaViabilidad entidadPlanInicial : listaPlanPmaVia){
				if(entidadPlanInicial.getPlanId() != null && entidadPlanInicial.getPlanId().equals(objEntidadPlan.getPlanId())){
					boolean existeRegistroSubPlan = false;
					for (RegistroAmbientalSubPlanPma registroAmbientalSubPlanPma : listaDocumentoPlanPma) {
						if(objEntidadPlan.getPlanId() != null && objEntidadPlan.getPlanId().equals(registroAmbientalSubPlanPma.getPlanManejoAmbientalPma().getId())){
							if(registroAmbientalSubPlanPma.getId() == null){
								registroAmbientalSubPlanPmaCoaFacade.guardar(registroAmbientalSubPlanPma);
							}
							// deshabilito documentos ingresados anteriormenet

							for (DocumentoRegistroAmbiental objDocumentoPma : objEntidadPlan.getListaDocumentoAdjunto()) {
								if (objDocumentoPma != null && objDocumentoPma.getId() == null && objDocumentoPma.getContenidoDocumento() != null ){
									documentoSubPlan = documentosFacade.ingresarDocumento(objDocumentoPma, registroAmbientalSubPlanPma.getId(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RCOA_SUBPLAN_MANEJO_AMBIENTAL, objDocumentoPma.getNombre(), RegistroAmbientalSubPlanPma.class.getSimpleName(), marcoLegalReferencialBean.getIdProceso());
									existeRegistroSubPlan = true;
								}
							}
							break;
						}
					}
					if(!existeRegistroSubPlan){
						RegistroAmbientalSubPlanPma subPlan = new RegistroAmbientalSubPlanPma();
						PlanManejoAmbientalPma objPlanManejoAmbiental = planManejoAmbientalCoaFacade.obtenerPlanPorId(objEntidadPlan.getPlanId());
						subPlan.setEstado(true);
						subPlan.setPlanManejoAmbientalPma(objPlanManejoAmbiental);
						subPlan.setRegistroAmbientalId(registroAmbiental.getId());
						registroAmbientalSubPlanPmaCoaFacade.guardar(subPlan);
						for (DocumentoRegistroAmbiental objDocumentoPma : objEntidadPlan.getListaDocumentoAdjunto()) {
							if (objDocumentoPma != null && objDocumentoPma.getId() == null && objDocumentoPma.getContenidoDocumento() != null ){
								documentoSubPlan = documentosFacade.ingresarDocumento(objDocumentoPma, subPlan.getId(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RCOA_SUBPLAN_MANEJO_AMBIENTAL, "documento "+objEntidadPlan.getPlanNombre()+".pdf", RegistroAmbientalSubPlanPma.class.getSimpleName(), marcoLegalReferencialBean.getIdProceso());
								existeRegistroSubPlan = true;
							}
						}
					}
				}
			}
			validarDatos = validarDatosIngresadosViabilidad(false);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	
			if(existeError){
				return false;
			}
			return true;
		}catch(Exception e){
			JsfUtil.addMessageError("No se puede actualizar la Ficha.");
			return false;
		}
	}
	
	public boolean guardar(){
		try{
			validarDatos = false;
			if(listaPlanPma == null || listaPlanPma.size() == 0){
				JsfUtil.addMessageError("Debe ingresar el plan de manejo ambiental.");
				return false;
			}
			// guardo los planes eliminados
			String listaIdEliminados = "0";
			for (PmaAceptadoRegistroAmbiental objMedidas : listaPlanPmaEliminar) {
				listaIdEliminados += ", "+objMedidas.getMedidaVerificacionPma().getId();
			}
			planManejoAmbientalCoaFacade.eliminarPlanesAgregados(listaIdEliminados, registroAmbiental.getId());
			// guardo los planes agregados
			for (EntidadPma entidadPlan : listaPlanPma){
					for (PmaAceptadoRegistroAmbiental objMedida: entidadPlan.getMedidasProyecto()){
						objMedida.setRegistroAmbientalId(registroAmbiental.getId());
						objMedida.setPlazo(objMedida.getPlazo());
						objMedida.setPonderacion(ponderacion / entidadPlan.getMedidasProyecto().size());
						objMedida.setEstado(true);
						objMedida.setAceptado(true);
						planManejoAmbientalCoaFacade.guardar(objMedida);
					}
			}
			validarDatos = validarDatos(true);
			if (validarDatos){
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}
			return true;
		}catch(Exception e){
			JsfUtil.addMessageError("No se puede actualizar la Ficha.");
			return false;
		}
	}

	public void eliminarDocumento(String nomnrePlan, DocumentoRegistroAmbiental adjunto){
		for (EntidadPma entidadPlan : listaPlanPma){
			if(entidadPlan.getPlanNombre().toString().equals(nomnrePlan)){
				if(adjunto.getId() != null){
					adjunto.setEstado(false);
					documentosFacade.guardar(adjunto);
				}
				entidadPlan.getListaDocumentoAdjunto().remove(adjunto);
				break;
			}
		}
	}
	
	public void eliminarDocumentoViabilidad(String nomnrePlan, DocumentoRegistroAmbiental adjunto){
		for (EntidadPmaViabilidad entidadPlan : listaPlanPmaVia){
			if(entidadPlan.getPlanNombre().toString().equals(nomnrePlan)){
				if(adjunto.getId() != null){
					adjunto.setEstado(false);
					documentosFacade.guardar(adjunto);
				}
				entidadPlan.getListaDocumentoAdjunto().remove(adjunto);
				break;
			}
		}
	}
	
	public void uploadFileDocumento(FileUploadEvent event ) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoSubPlan = new DocumentoRegistroAmbiental();
		String nomnrePlan = event.getComponent().getAttributes().get("nombrePlan").toString();
		documentoSubPlan.setId(null);
		documentoSubPlan.setContenidoDocumento(contenidoDocumento);
		documentoSubPlan.setNombre(event.getFile().getFileName());
		documentoSubPlan.setMime("application/pdf");
		documentoSubPlan.setExtesion(".pdf");
		for (EntidadPma entidadPlan : listaPlanPma){
			if(entidadPlan.getPlanNombre().toString().equals(nomnrePlan)){
				entidadPlan.getListaDocumentoAdjunto().add(documentoSubPlan);
				break;
			}
		}
		documentoSubPlan = new DocumentoRegistroAmbiental();
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void uploadFileDocumentoViabilidad(FileUploadEvent event ) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoSubPlan = new DocumentoRegistroAmbiental();
		String nomnrePlan = event.getComponent().getAttributes().get("nombrePlan").toString();
		documentoSubPlan.setId(null);
		documentoSubPlan.setContenidoDocumento(contenidoDocumento);
		documentoSubPlan.setNombre(event.getFile().getFileName());
		documentoSubPlan.setMime("application/pdf");
		documentoSubPlan.setExtesion(".pdf");
		for (EntidadPmaViabilidad entidadPlan : listaPlanPmaVia){
			if(entidadPlan.getPlanNombre().toString().equals(nomnrePlan)){
				entidadPlan.getListaDocumentoAdjunto().add(documentoSubPlan);
				break;
			}
		}
		documentoSubPlan = new DocumentoRegistroAmbiental();
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}

	
	public StreamedContent getPlantillaComponente() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (sub_plan_cierre_abandono_byte != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(sub_plan_cierre_abandono_byte));
					content.setName(sub_plan_cierre_abandono);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
}
