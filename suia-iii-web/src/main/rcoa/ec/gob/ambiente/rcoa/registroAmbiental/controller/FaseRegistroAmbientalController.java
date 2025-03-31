package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.FasesCoaFacade;
import ec.gob.ambiente.rcoa.facade.FasesRegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.PlanManejoAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.FasesCoa;
import ec.gob.ambiente.rcoa.model.FasesRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.model.SubActividades;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.FaseViabilidadTecnicaRcoaFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.PmaViabilidadTecnicaFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.ViabilidadTecnicaProyectoFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.FaseViabilidadTecnicaRcoa;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnicaProyecto;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class FaseRegistroAmbientalController {

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

	@Getter
    @Setter
    @ManagedProperty(value = "#{marcoLegalReferencialController}")
    private MarcoLegalReferencialController marcoLegalReferencialBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{menuRegistroAmbientalCoaController}")
    private MenuRegistroAmbientalCoaController menuCoaBean;
	
	@EJB
	private FasesCoaFacade fasesCoaFacade;
	
	@EJB
	private FasesRegistroAmbientalCoaFacade fasesRegistroAmbientalCoaFacade;

	@EJB
	private PlanManejoAmbientalCoaFacade planManejoAmbientalCoaFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;	
	
	@EJB
	private FaseViabilidadTecnicaRcoaFacade faseViabilidadTecnicaRcoaFacade;
	
	@EJB
	private ViabilidadTecnicaProyectoFacade viabilidadTecnicaProyectoFacade;
	
	@EJB
	private PmaViabilidadTecnicaFacade pmaViabilidadTecnicaFacade;
	
	@EJB
	private DocumentosRegistroAmbientalFacade documentosFacade;
	
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalFacade;
	
	@EJB
    private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;

	@Getter
    @Setter
	private FasesRegistroAmbiental registroAmbientalFases;
	
	@Getter
	@Setter
	private RegistroAmbientalRcoa registroAmbiental;

	@Getter
    @Setter
	private FasesCoa faseSeleccionada;

	@Getter
    @Setter
	private boolean validarDatos, existeConstruccion;

	@Getter
    @Setter
	private Integer faseConstruccionId=1;
	
	@Getter
    @Setter
    private List<FasesCoa> listaFases;
	
	@Getter
    @Setter
    private List<FasesRegistroAmbiental> listaFasesRegistroAmbiental, listaFasesRegistroAmbientalEliminar;
	
	boolean esConstruccion = false, esOperaciones=false, esCierre=false, existePmaCierre;
	
	/**variables relleno sanitario*/
	@Getter
	@Setter
	private Boolean esActividadRelleno = false, pmaViabilidad;
	
	@Getter
	@Setter
	private List<FaseViabilidadTecnicaRcoa> listaFasesRegistroAmbientalViabilidad, listaFasesRegistroAmbientalViabilidadEliminar;
	
	@Getter
	@Setter
	private Boolean manejaDesechos;
	
	@Getter
    @Setter
	private FaseViabilidadTecnicaRcoa registroAmbientalFasesViabilidad;
	
	@Getter
	@Setter
	private ViabilidadTecnicaProyecto viabilidadTecnica;
	
	@Getter
	@Setter
	private String mensaje;
	
	@Getter
	@Setter
	private DocumentoRegistroAmbiental documentoDocCamaroneraPlaya, documentoDocCamaroneraAlta;
	
	@Getter
	@Setter
	private Boolean esActividadCamaronera = false;
	
	@Getter
	@Setter
	private String zonaCamaronera;
	
	@Getter
    @Setter
    private ProyectoLicenciaCoa proyectoRcoa;
	
	@PostConstruct
	private void init(){
		if(loginBean.getUsuario().getId() == null){
			JsfUtil.redirectTo("/start.js");
			return;
		}
        FacesContext ctx = FacesContext.getCurrentInstance();
        String urls = ctx.getViewRoot().getViewId();
		listaFases = fasesCoaFacade.listarFases();
		validarDatos = false;
		existeConstruccion=false;
		registroAmbiental = marcoLegalReferencialBean.getRegistroAmbientalRcoa();
		registroAmbientalFases = new FasesRegistroAmbiental();
		faseSeleccionada = new FasesCoa();
		listaFasesRegistroAmbientalEliminar = new ArrayList<FasesRegistroAmbiental>();
		listaFasesRegistroAmbientalViabilidadEliminar = new ArrayList<>();
		listaFasesRegistroAmbientalViabilidad = new ArrayList<FaseViabilidadTecnicaRcoa>();
		registroAmbientalFasesViabilidad = new FaseViabilidadTecnicaRcoa();
		validarActividadRellenoSanitario();
		validarActividadCamaronera();

		// si tiene pma de cierre aumento esa fase
		existePmaCierre=validarActividadConPmaCierre();
		if(existePmaCierre){
			FasesCoa faseCierre = new FasesCoa();
			faseCierre.setId(3);
			faseCierre.setDescripcion("Cierre y Abandono");
			faseCierre.setCodigo("CA");
			listaFases.add(faseCierre);
			esCierre=false;
		}
		if(registroAmbiental != null && registroAmbiental.getId() != null){
			if(esActividadRelleno){
				manejaDesechos = viabilidadTecnica.getManejaDesechosSanitarios();
				cargarFasesViabilidadProyecto();
			}else
				cargarFasesProyecto();
	    }else{
	    	if(urls.contains("descripcionProyecto")){
		    	menuCoaBean.setPageMarcoLegal(true);
		    	menuCoaBean.setPageDescripcion(false);
	            RequestContext context = RequestContext.getCurrentInstance();
	            context.execute("PF('dlgPasoFaltante').show();");
	    	}
	    }
		try {
			if(esActividadCamaronera){
				if(zonaCamaronera.equals("MIXTA")){
					documentoDocCamaroneraAlta = documentosFacade.documentoXTablaIdXIdDoc(registroAmbiental.getId(), "RA_CONCESION_MINERA_ALTA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
					documentoDocCamaroneraPlaya = documentosFacade.documentoXTablaIdXIdDoc(registroAmbiental.getId(), "RA_CONCESION_MINERA_PLAYA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
										
				}else if(zonaCamaronera.equals("ALTA")){
					documentoDocCamaroneraAlta = documentosFacade.documentoXTablaIdXIdDoc(registroAmbiental.getId(), "RA_CONCESION_MINERA_ALTA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
				}else{
					documentoDocCamaroneraPlaya = documentosFacade.documentoXTablaIdXIdDoc(registroAmbiental.getId(), "RA_CONCESION_MINERA_PLAYA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void cargarFasesProyecto(){
		listaFasesRegistroAmbiental = fasesRegistroAmbientalCoaFacade.obtenerfasesPorRegistroAmbiental(registroAmbiental);
		// verifico si escogio la fase de Construcción, Rehabilitación y/o Ampliación 
		for (FasesRegistroAmbiental objFases : listaFasesRegistroAmbiental) {
			objFases.setNuevoRegistro(true);
			if(objFases.getFasesCoa().getId().toString().equals("1")){
				existeConstruccion = true;
			}
		}
	}
	
	public void cargarFasesViabilidadProyecto(){
		listaFasesRegistroAmbientalViabilidad = faseViabilidadTecnicaRcoaFacade.obtenerfasesPorRegistroAmbiental(registroAmbiental);
		// verifico si escogio la fase de Construcción, Rehabilitación y/o Ampliación 
		for (FaseViabilidadTecnicaRcoa objFases : listaFasesRegistroAmbientalViabilidad) {
			objFases.setNuevoRegistro(true);
			if(objFases.getFasesCoa().getId().toString().equals("1")){
				existeConstruccion = true;
			}
		}
	}
	
	
	private void validarActividadRellenoSanitario(){
		try {
			pmaViabilidad = false;
			ProyectoLicenciaCoa proyecto = new ProyectoLicenciaCoa();
			if(registroAmbiental.getProyectoCoa() == null){
				proyecto = marcoLegalReferencialBean.getProyectoLicenciaCoa();
			}else{
				proyecto = registroAmbiental.getProyectoCoa();
			}
			
			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = new ArrayList<ProyectoLicenciaCuaCiuu>();
			listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyecto);
			
			for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
				if(actividad.getCatalogoCIUU().getCodigo().equals("E3821.01") || actividad.getCatalogoCIUU().getCodigo().equals("E3821.01.01")){
					List<ViabilidadTecnicaProyecto> lista = viabilidadTecnicaProyectoFacade.buscarPorProyecto(proyecto.getId());
					
					if(lista!= null && !lista.isEmpty()){
						viabilidadTecnica = lista.get(0);
						esActividadRelleno = true;
						pmaViabilidad = true;
					}					
					break;
				}
			}
			for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
				if(actividad.getSubActividad() != null && (actividad.getSubActividad().getNombre().equalsIgnoreCase("Cierre técnico de botaderos e implementación de celda emergente para la disposición final de desechos sólidos no peligrosos en el mismo predio")
						|| actividad.getSubActividad().getNombre().equalsIgnoreCase("Cierre técnico de botaderos de desechos sólidos"))){
					pmaViabilidad = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public boolean validateData() {
		boolean valido = true;
		esConstruccion = false;
		esOperaciones = false;
		esCierre = false;
		proyectoRcoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(registroAmbiental.getProyectoCoa().getId());
		if(listaFasesRegistroAmbiental.size() == 0){
			JsfUtil.addMessageError("Debe ingresar la(s) etapa(s) del proyecto para registro ambiental.");
			valido = false;
		}
		if(registroAmbientalFases != null && registroAmbientalFases.getId() != null ){
			JsfUtil.addMessageError("Existen datos pendientes de adicionar.");
			valido = false;
		}
		// verifico si escogio la fase de Construcción, Rehabilitación y/o Ampliación 
		for (FasesRegistroAmbiental objFases : listaFasesRegistroAmbiental) {
			if(objFases.getFasesCoa().getId().toString().equals("1")){
				esConstruccion = true;
			}else if(objFases.getFasesCoa().getId().toString().equals("2")){
				esOperaciones = true;
			} if(objFases.getFasesCoa().getId().toString().equals("3")){
				esCierre = true;
			}
		}
		Integer totalFases = (proyectoRcoa.getTipoProyecto() == 1) ? (existePmaCierre ? 3 : 2) : (proyectoRcoa.getTipoProyecto() == 2 ? (existePmaCierre ? 2 : 1) : 0);
		if (valido && listaFasesRegistroAmbiental.size() < totalFases && proyectoRcoa.getTipoProyecto() == 1) {
            if (esConstruccion && !esOperaciones) {
                JsfUtil.addMessageError("Debe ingresar la etapa de 'Operación y Mantenimiento'");
                valido = false;
            } else if (!esConstruccion && esOperaciones) {
                JsfUtil.addMessageError("Debe ingresar la etapa de 'Construcción, Rehabilitación y/o Ampliación'");
                valido = false;
            } else if (esCierre && !existePmaCierre) {
                JsfUtil.addMessageError("Debe ingresar la etapa de Cierre y Abandono.");
                valido = false;
            }
        } else if (valido && listaFasesRegistroAmbiental.size() < totalFases && proyectoRcoa.getTipoProyecto() == 2) {
            if (esConstruccion || !esOperaciones) {
                JsfUtil.addMessageError("Debe ingresar solo la etapa de Operación y Mantenimiento.");
                valido = false;
            } else if (esCierre && !existePmaCierre) {
                JsfUtil.addMessageError("Debe ingresar la etapa de Cierre y Abandono.");
                valido = false;
            }
        }
		if(valido && !esConstruccion && !esOperaciones && esCierre){
			JsfUtil.addMessageError("Debe completar el ingreso de la información de las etapas.");
			valido = false;
		}
		
		if(esActividadCamaronera){
			if(zonaCamaronera.equals("MIXTA")){
				if(registroAmbiental.getConcesionCamaroneraAlta()== null || registroAmbiental.getConcesionCamaroneraAlta().isEmpty()){
					JsfUtil.addMessageError("Debe ingresar el número del acuerdo de la concesión camaronera de Tierras Privadas o Zonas Altas");
					valido = false;
				}
				
				if(documentoDocCamaroneraAlta == null || documentoDocCamaroneraAlta.getContenidoDocumento() == null){
					JsfUtil.addMessageError("Adjunte el documento de Autorización Administrativa o Título de Concesión camaronera Tierras Privadas o Zonas Altas");
					valido = false;
				}
				
				if(registroAmbiental.getConcesionCamaroneraPlaya()== null || registroAmbiental.getConcesionCamaroneraPlaya().isEmpty()){
					JsfUtil.addMessageError("Debe ingresar el número del acuerdo de al concesión camaronera de Zona de Playa y Bahía");
					valido = false;
				}
				
				if(documentoDocCamaroneraPlaya == null || documentoDocCamaroneraPlaya.getContenidoDocumento() == null){
					JsfUtil.addMessageError("Adjunte el documento de Autorización Administrativa o Título de Concesión camaronera Zona de Playa y Bahía");
					valido = false;
				}
				
				
			}else if(zonaCamaronera.equals("ALTA")){
				if(registroAmbiental.getConcesionCamaroneraAlta()== null || registroAmbiental.getConcesionCamaroneraAlta().isEmpty()){
					JsfUtil.addMessageError("Debe ingresar el número del acuerdo de al concesión camaronera");
					valido = false;
				}
				
				if(documentoDocCamaroneraAlta == null || documentoDocCamaroneraAlta.getContenidoDocumento() == null){
					JsfUtil.addMessageError("Adjunte el documento de Autorización Administrativa o Título de Concesión camaronera");
					valido = false;
				}
			}else{
				if(registroAmbiental.getConcesionCamaroneraPlaya()== null || registroAmbiental.getConcesionCamaroneraPlaya().isEmpty()){
					JsfUtil.addMessageError("Debe ingresar el número del acuerdo de al concesión camaronera");
					valido = false;
				}
				
				if(documentoDocCamaroneraPlaya == null || documentoDocCamaroneraPlaya.getContenidoDocumento() == null){
					JsfUtil.addMessageError("Adjunte el documento de Autorización Administrativa o Título de Concesión camaronera");
					valido = false;
				}
			}
		}
		
		return valido;
	}
	
	public void inicializarFechas(){
		if(!registroAmbientalFases.getFasesCoa().getId().toString().equals("1")){
			registroAmbientalFases.setFechaInicio(null);
			registroAmbientalFases.setFechaFin(null);
		}
	}
	
	public void inicializarFechasViabilidad(){
		if(!registroAmbientalFasesViabilidad.getFasesCoa().getId().toString().equals("1")){
			registroAmbientalFasesViabilidad.setFechaInicio(null);
			registroAmbientalFasesViabilidad.setFechaFin(null);
		}
	}
	
	public void agregarFase(){
		boolean existe = false;
		if(registroAmbientalFases != null && registroAmbientalFases.getFasesCoa() != null ){
			for (FasesRegistroAmbiental objRegistro : listaFasesRegistroAmbiental) {
				if(registroAmbientalFases.getFasesCoa().getId().equals(objRegistro.getFasesCoa().getId())){
					if(registroAmbientalFases.isNuevoRegistro()){
						listaFasesRegistroAmbiental.remove(objRegistro);
					}else{
						existe = true;
					}
					break;
				}
			}
			if(!existe){
				registroAmbientalFases.setNuevoRegistro(true);
				listaFasesRegistroAmbiental.add(registroAmbientalFases);
				// verificvo si es construccion
				if(registroAmbientalFases.getFasesCoa().getId().toString().equals("1")){
					existeConstruccion = true;
				}
				registroAmbientalFases = new FasesRegistroAmbiental();
			}else{
				JsfUtil.addMessageError("La etapa "+registroAmbientalFases.getFasesCoa().getDescripcion()+" ya se encuentra registrada");
			}
		}
	}
	
	public void agregarFaseViabilidad(){
		boolean existe = false;
		if(registroAmbientalFasesViabilidad != null && registroAmbientalFasesViabilidad.getFasesCoa() != null ){
			for (FaseViabilidadTecnicaRcoa objRegistro : listaFasesRegistroAmbientalViabilidad) {
				if(registroAmbientalFasesViabilidad.getFasesCoa().getId().equals(objRegistro.getFasesCoa().getId())){
					if(registroAmbientalFasesViabilidad.isNuevoRegistro()){
						listaFasesRegistroAmbientalViabilidad.remove(objRegistro);
					}else{
						existe = true;
					}
					break;
				}
			}
			if(!existe){
				registroAmbientalFasesViabilidad.setNuevoRegistro(true);
				listaFasesRegistroAmbientalViabilidad.add(registroAmbientalFasesViabilidad);
				// verificvo si es construccion
				if(registroAmbientalFasesViabilidad.getFasesCoa().getId().toString().equals("1")){
					existeConstruccion = true;
				}
				registroAmbientalFasesViabilidad = new FaseViabilidadTecnicaRcoa();
			}else{
				JsfUtil.addMessageError("La etapa "+registroAmbientalFasesViabilidad.getFasesCoa().getDescripcion()+" ya se encuentra registrada");
			}
		}
	}

	public void eliminarFase(FasesRegistroAmbiental objRegistroFase){
		if(objRegistroFase != null){
			if(!listaFasesRegistroAmbientalEliminar.contains(objRegistroFase)){
				listaFasesRegistroAmbientalEliminar.add(objRegistroFase);
			}
		}
		listaFasesRegistroAmbiental.remove(objRegistroFase);
		// verificvo si es construccion
		if(objRegistroFase.getFasesCoa().getId().toString().equals("1")){
			existeConstruccion = false;
		}
	}
	
	public void eliminarFaseViabilidad(FaseViabilidadTecnicaRcoa objRegistroFase){
		if(objRegistroFase != null){
			if(!listaFasesRegistroAmbientalViabilidadEliminar.contains(objRegistroFase)){
				listaFasesRegistroAmbientalViabilidadEliminar.add(objRegistroFase);
			}
		}
		listaFasesRegistroAmbientalViabilidad.remove(objRegistroFase);
		// verificvo si es construccion
		if(objRegistroFase.getFasesCoa().getId().toString().equals("1")){
			existeConstruccion = false;
		}
	}
	
	public void editarFase(FasesRegistroAmbiental objRegistroFase){
		registroAmbientalFases = objRegistroFase;
		/*if(objRegistroFase != null){
			listaFasesRegistroAmbiental.remove(objRegistroFase);
		}*/
	}
	
	public void editarFaseViabilidad(FaseViabilidadTecnicaRcoa objRegistroFase){
		registroAmbientalFasesViabilidad = objRegistroFase;		
	}

	public boolean guardar(){
		try{
			validarDatos = false;
			if(validateData()){
				
				registroAmbientalFacade.guardar(registroAmbiental);
				RegistroAmbientalController finalizarRegistroAmbientalBean = JsfUtil.getBean(RegistroAmbientalController.class);
				//guardo las fases del registro 
				for (FasesRegistroAmbiental objRegistroFases : listaFasesRegistroAmbiental) {
					objRegistroFases.setEstado(true);
					if(objRegistroFases.getRegistroAmbientalId() == null ){
						objRegistroFases.setRegistroAmbientalId(marcoLegalReferencialBean.getRegistroAmbientalRcoa().getId());	
					}
					fasesRegistroAmbientalCoaFacade.guardar(objRegistroFases);
				}
				//guardo las fases eliminadas del registro 
				for (FasesRegistroAmbiental objRegistroFases : listaFasesRegistroAmbientalEliminar) {
					if(objRegistroFases.getId() != null){
						objRegistroFases.setEstado(false);
						objRegistroFases.setRegistroAmbientalId(marcoLegalReferencialBean.getRegistroAmbientalRcoa().getId());
						fasesRegistroAmbientalCoaFacade.guardar(objRegistroFases);					
					}
				}
				// desabilito el pma de las fases eliminadas
				planManejoAmbientalCoaFacade.deshabilitarPlanesPMAFasesEliminadass(marcoLegalReferencialBean.getRegistroAmbientalRcoa().getId());
				//valido si tiene pma de cierre
	    			//esCierre = validarActividadConPmaCierre();
				// si no tiene pma construccion deshabilita el menu
				if(esConstruccion && validarActividadSinPmaConstruccion()){
					esConstruccion=false;
				}
				
				if(esActividadCamaronera){
					guardarDocCamaroneras();
				}
				
				finalizarRegistroAmbientalBean.setPmaConstruccion(esConstruccion);
				finalizarRegistroAmbientalBean.setPmaOperacion(esOperaciones);
				finalizarRegistroAmbientalBean.setPmaCierre(esCierre);
				validarDatos = true;
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}
			return validarDatos;
		}catch(Exception e){
			e.printStackTrace();
			JsfUtil.addMessageError("No se puede actualizar la Ficha.");
			return false;
		}
	}
	
	public boolean guardarViabilidad(){
		try{
			validarDatos = false;
			if(validateDataViabilidad()){
				RegistroAmbientalController finalizarRegistroAmbientalBean = JsfUtil.getBean(RegistroAmbientalController.class);
				
				viabilidadTecnica.setManejaDesechosSanitarios(manejaDesechos);
				viabilidadTecnicaProyectoFacade.guardar(viabilidadTecnica, JsfUtil.getLoggedUser());
				//guardo las fases del registro 
				for (FaseViabilidadTecnicaRcoa objRegistroFases : listaFasesRegistroAmbientalViabilidad) {
					objRegistroFases.setEstado(true);
					objRegistroFases.setViabilidadTecnicaProyecto(viabilidadTecnica);
					faseViabilidadTecnicaRcoaFacade.guardar(objRegistroFases, JsfUtil.getLoggedUser());					
				}
				//guardo las fases eliminadas del registro 
				for (FaseViabilidadTecnicaRcoa objRegistroFases : listaFasesRegistroAmbientalViabilidadEliminar) {
					if(objRegistroFases.getId() != null){
						objRegistroFases.setEstado(false);
						faseViabilidadTecnicaRcoaFacade.guardar(objRegistroFases, JsfUtil.getLoggedUser());			
						
						pmaViabilidadTecnicaFacade.deshabilitarPlanesPMAFasesEliminadas(objRegistroFases.getId());
					}
				}
				// desabilito el pma de las fases eliminadas
				
				finalizarRegistroAmbientalBean.setPmaConstruccion(esConstruccion);
				finalizarRegistroAmbientalBean.setPmaOperacion(esOperaciones);
				validarDatos = true;
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}
			return validarDatos;
		}catch(Exception e){
			JsfUtil.addMessageError("No se puede actualizar la Ficha.");
			return false;
		}
	}
	
	public boolean validateDataViabilidad() {
		boolean valido = true;
		esConstruccion = false;
		esOperaciones = false;
		esCierre = false;
		
		if(manejaDesechos == null){
			JsfUtil.addMessageError("Debe seleccionar si su proyecto, obra o actividad cuenta con el manejo de desechos sanitarios");
			valido = false;
		}
		
		if(listaFasesRegistroAmbientalViabilidad.size() == 0){
			JsfUtil.addMessageError("Debe ingresar al menos una etapa del proyecto para registro ambiental.");
			valido = false;
		}
		if(registroAmbientalFasesViabilidad != null && registroAmbientalFasesViabilidad.getId() != null ){
			JsfUtil.addMessageError("Existen datos pendientes de adicionar.");
			valido = false;
		}
		// verifico si escogio la fase de Construcción, Rehabilitación y/o Ampliación 
		for (FaseViabilidadTecnicaRcoa objFases : listaFasesRegistroAmbientalViabilidad) {
			if(objFases.getFasesCoa().getId().toString().equals("1")){
				esConstruccion = true;
			}else if(objFases.getFasesCoa().getId().toString().equals("2")){
				esOperaciones = true;
			} if(objFases.getFasesCoa().getId().toString().equals("3")){
				esCierre = true;
			}
		}
		Integer totalFases = existePmaCierre ? 3 : 2;
		if(valido && esConstruccion && listaFasesRegistroAmbientalViabilidad.size() != totalFases){
			JsfUtil.addMessageError("Debe ingresar todas las etapas del proyecto.");
			valido = false;
		}
		if(valido && esConstruccion && !esCierre && existePmaCierre){
			JsfUtil.addMessageError("Debe ingresar la etapa de Cierre y Abandono.");
			valido = false;
		}
		if(valido && !esConstruccion && !esOperaciones && esCierre){
			JsfUtil.addMessageError("Debe completar el ingreso de la información de las etapas del proyecto.");
			valido = false;
		}
		
		return valido;
	}
	
	public FaseViabilidadTecnicaRcoa obtenerFase(){
		List<FaseViabilidadTecnicaRcoa> listaFasesViabilidad = faseViabilidadTecnicaRcoaFacade.obtenerfasesPorRegistroAmbiental(registroAmbiental);
		// verifico si escogio la fase de Construcción, Rehabilitación y/o Ampliación 
		for (FaseViabilidadTecnicaRcoa objFases : listaFasesViabilidad) {
			if(objFases.getFasesCoa().getId().toString().equals("1")){
				return objFases;
			}
		}
		return null;
	}
	
	public FaseViabilidadTecnicaRcoa obtenerFaseOperacion(){
		List<FaseViabilidadTecnicaRcoa> listaFasesViabilidad = faseViabilidadTecnicaRcoaFacade.obtenerfasesPorRegistroAmbiental(registroAmbiental);
		// verifico si escogio la fase de Construcción, Rehabilitación y/o Ampliación 
		for (FaseViabilidadTecnicaRcoa objFases : listaFasesViabilidad) {
			if(objFases.getFasesCoa().getId().toString().equals("2")){
				return objFases;
			}
		}
		return null;
	}
	
	public boolean validarActividadConPmaCierre(){
		boolean tienePma=false;
		try {
			ProyectoLicenciaCoa proyecto = new ProyectoLicenciaCoa();
			if(registroAmbiental.getProyectoCoa() == null){
				proyecto = marcoLegalReferencialBean.getProyectoLicenciaCoa();
			}else{
				proyecto = registroAmbiental.getProyectoCoa();
			}
			//busco la actividad principal del proyecto
			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyecto);
			if(listaActividadesCiuu != null && listaActividadesCiuu.size() > 0){
				for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
					if(actividad.getPrimario()){
						//verifico si es actividad que tiene plan de cierre 
						if(actividad.getCatalogoCIUU().getCodigo().equals("F4290.12") || actividad.getCatalogoCIUU().getCodigo().equals("F4290.12.01") // (Dragado de vías de navegación.)
								|| actividad.getCatalogoCIUU().getCodigo().equals("A0130.00.04") // Cultivo de flores en viveros.
								){
							tienePma = true;
						}
						//verifico si es actividad que tiene plan de cierre  (Operación de sistemas de transmisión y distribución de energía eléctrica (que constan de postes, medidores y cableado), que transportan la energía eléctrica recibida desde las instalaciones de generación o transmisión hacia el consumidor final.)
						if(actividad.getCatalogoCIUU().getCodigo().equals("D3510.02") || actividad.getCatalogoCIUU().getCodigo().equals("D3510.02.01")){
							// verifico si tiene la consideracio para la cual hay pma de cierre
							if(actividad.getSubActividad().getNombre().equals(Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION_MAYOR))
								tienePma = true;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tienePma;
	}
	
	public boolean validarActividadSinPmaConstruccion(){
		boolean noTienePma=false;
		try {
			ProyectoLicenciaCoa proyecto = new ProyectoLicenciaCoa();
			if(registroAmbiental.getProyectoCoa() == null){
				proyecto = marcoLegalReferencialBean.getProyectoLicenciaCoa();
			}else{
				proyecto = registroAmbiental.getProyectoCoa();
			}
			//busco la actividad principal del proyecto
			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyecto);
			if(listaActividadesCiuu != null && listaActividadesCiuu.size() > 0){
				for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
					if(actividad.getPrimario()){
						//verifico si es actividad que NO tiene plan de construnccion
						if(actividad.getCatalogoCIUU().getCodigo().equals("F4290.12") || actividad.getCatalogoCIUU().getCodigo().equals("F4290.12.01")){
							noTienePma = true;
						}	
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return noTienePma;
	}
	
	public void validarActividadCamaronera(){
		try {
			
			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = new ArrayList<ProyectoLicenciaCuaCiuu>();
			listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(registroAmbiental.getProyectoCoa());
			
			for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
				
				if(Constantes.getActividadesCamaroneras().contains(actividad.getCatalogoCIUU().getCodigo())){
					SubActividades subActividad1=actividad.getSubActividad();
					if(subActividad1.getSubActividades().getRequiereValidacionCoordenadas().equals(1)){
						if(registroAmbiental.getProyectoCoa().getConcesionCamaronera() == null){
							esActividadCamaronera = true;	
							setZonaCamaronera(registroAmbiental.getProyectoCoa().getZona_camaronera());
						}						
					}					
				}				
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void uploadListenerDocCamaronera(FileUploadEvent event) {		
		documentoDocCamaroneraAlta = new DocumentoRegistroAmbiental();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoDocCamaroneraAlta.setContenidoDocumento(contenidoDocumento);
		documentoDocCamaroneraAlta.setNombre(event.getFile().getFileName());
		documentoDocCamaroneraAlta.setExtesion(".pdf");
		documentoDocCamaroneraAlta.setMime("application/pdf");
		documentoDocCamaroneraAlta.setNombreTabla("RA_CONCESION_MINERA_ALTA");
	}
	
	public void uploadListenerDocCamaroneraPlaya(FileUploadEvent event) {		
		documentoDocCamaroneraPlaya = new DocumentoRegistroAmbiental();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoDocCamaroneraPlaya.setContenidoDocumento(contenidoDocumento);
		documentoDocCamaroneraPlaya.setNombre(event.getFile().getFileName());
		documentoDocCamaroneraPlaya.setExtesion(".pdf");
		documentoDocCamaroneraPlaya.setMime("application/pdf");
		documentoDocCamaroneraPlaya.setNombreTabla("RA_CONCESION_MINERA_PLAYA");
	}
	
	public void guardarDocCamaroneras()
	{		
		if (documentoDocCamaroneraAlta != null && documentoDocCamaroneraAlta.getContenidoDocumento() != null && documentoDocCamaroneraAlta.getId() == null) {
			documentoDocCamaroneraAlta.setIdTable(registroAmbiental.getId());
			byte[] contenidoDocumentoAlta = documentoDocCamaroneraAlta.getContenidoDocumento();
			try {
				documentoDocCamaroneraAlta = documentosFacade.guardarDocumentoAlfrescoCA(registroAmbiental.getProyectoCoa().getCodigoUnicoAmbiental(), "REGISTRO_AMBIENTAL",  
						documentoDocCamaroneraAlta, TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
				documentoDocCamaroneraAlta.setContenidoDocumento(contenidoDocumentoAlta);
			} catch (ServiceException | CmisAlfrescoException e) {
				e.printStackTrace();
			}
		}
		
		if (documentoDocCamaroneraPlaya != null && documentoDocCamaroneraPlaya.getContenidoDocumento() != null && documentoDocCamaroneraPlaya.getId()== null) {
			documentoDocCamaroneraPlaya.setIdTable(registroAmbiental.getId());
			byte[] contenidoDocumentoPlaya = documentoDocCamaroneraPlaya.getContenidoDocumento();
			try {
				documentoDocCamaroneraPlaya = documentosFacade.guardarDocumentoAlfrescoCA(registroAmbiental.getProyectoCoa().getCodigoUnicoAmbiental(), "REGISTRO_AMBIENTAL",  
						documentoDocCamaroneraPlaya, TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
				documentoDocCamaroneraPlaya.setContenidoDocumento(contenidoDocumentoPlaya);
			} catch (ServiceException | CmisAlfrescoException e) {
				e.printStackTrace();
			}
		}
	}
}
