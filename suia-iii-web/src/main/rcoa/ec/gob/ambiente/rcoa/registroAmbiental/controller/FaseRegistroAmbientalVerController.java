package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.FasesCoa;
import ec.gob.ambiente.rcoa.model.FasesRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalSubPlanPma;
import ec.gob.ambiente.rcoa.model.SubActividades;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.FaseViabilidadTecnicaRcoaFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.PmaViabilidadTecnicaFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.ViabilidadTecnicaProyectoFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.FaseViabilidadTecnicaRcoa;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnicaProyecto;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.FasesCoaFacade;
import ec.gob.ambiente.rcoa.facade.FasesRegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.PlanManejoAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;


@ManagedBean
@ViewScoped
public class FaseRegistroAmbientalVerController {

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

	@Getter
    @Setter
    @ManagedProperty(value = "#{marcoLegalReferencialController}")
    private MarcoLegalReferencialController marcoLegalReferencialBean;
	
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
	private Boolean manejaDesechos;
	
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
				cargarFasesProyecto();
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
		
	public boolean validateData() {
		boolean valido = true;
		esConstruccion = false;
		esOperaciones = false;
		esCierre = false;
		if(listaFasesRegistroAmbiental.size() == 0){
			JsfUtil.addMessageError("Debe ingresar al menos una etapa del proyecto para registro ambiental.");
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
		Integer totalFases = existePmaCierre ? 3 : 2;
		if(valido && esConstruccion && listaFasesRegistroAmbiental.size() != totalFases){
			JsfUtil.addMessageError("Debe ingresar todas las etapas del proyecto.");
			valido = false;
		}
		if(valido && esConstruccion && !esCierre && existePmaCierre){
			JsfUtil.addMessageError("Debe ingresar la etapa de Cierre y Abandono.");
			valido = false;
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
}