package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.EquipoApoyoProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformeTecnicoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.EquipoApoyoProyecto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.SubActividades;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.service.AreaService;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.carga.facade.CargaLaboralFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class RevisarEIAController {
	private final Logger LOG = Logger.getLogger(RevisarEIAController.class);
	

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private DocumentosFacade documentoGuiaFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private EquipoApoyoProyectoEIACoaFacade equipoApoyoProyectoEIACoaFacade;

	@EJB
	private CargaLaboralFacade cargaLaboralFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private AsignarTareaFacade asignarTareaFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
    private AreaService areaService;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
    private AreaFacade areaFacade;
	
	@EJB
	private InformeTecnicoEsIAFacade informeTecnicoEsIAFacade;
	
	@EJB
	private DocumentosImpactoEstudioFacade documentosImpactoEstudioFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
    @Setter
	private InformacionProyectoEia informacionProyectoEia;
	
	@Getter
    @Setter
	private EquipoApoyoProyecto equipoApoyoProyecto;
	
	@Getter
	@Setter
	private List<InformeTecnicoEsIA> listaInformes, informesSeleccionados;

	@Getter
    @Setter
	private boolean intersecaSNAP, intersecaBP, inventarioForestal;

	@Getter
    @Setter
	private Boolean equipoMultidisciplinario, inspeccion, esPlantaCentral, esGad, agregarTecnicosEspecialistas;

	@Getter
    @Setter
	private String urlInformeTecnico;

	@Getter
    @Setter
	private Integer numTecnicosApoyo, numMaximoTecnicosApoyo, numeroRevision;

    @Getter
    @Setter
    private List<String> areas, areasApoyo;

    @Getter
    @Setter
    private String[] areasSeleccionadas, areasSeleccionadasApoyo;
    
    @Getter
    @Setter
    private List<String> tecnicosEspecialistas;
    
    @Getter
	@Setter
	private List<Usuario> listaUsuario;
	
    private Map<String, Object> variables;
	private String tramite;
	
	@Getter
	@Setter
	private String zonaCamaronera;
	
	@Getter
	@Setter
	private Boolean esActividadCamaronera = false;
	
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoDocCamaroneraPlaya, documentoDocCamaroneraAlta;

	
	@PostConstruct
	private void init(){
		try{
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			informacionProyectoEia = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyectoLicenciaCoa);
			if(informacionProyectoEia != null){
					equipoMultidisciplinario = informacionProyectoEia.getEquipoMultidisciplinario();
					inspeccion = informacionProyectoEia.getInspeccion();
			}else{
		
			}
			equipoApoyoProyecto = equipoApoyoProyectoEIACoaFacade.obtenerEquipoApoyoProyecto(informacionProyectoEia);
			
			intersecaSNAP = proyectoLicenciaCoa.getInterecaSnap();
			intersecaBP = false;
			if(proyectoLicenciaCoa.getInterecaBosqueProtector() || proyectoLicenciaCoa.getInterecaPatrimonioForestal())
				intersecaBP = true;
			inventarioForestal = proyectoLicenciaCoa.getRenocionCobertura();
			
			cargarCatalogos();
			
			String revision = (String) variables.get("numeroRevision");
			numeroRevision = (revision != null) ? Integer.parseInt(revision) : 0;
			if(numeroRevision > 1) 	
				cargarDatosSegundaRevision();
			
			if(loginBean.getUsuario().getId() == null){
				JsfUtil.redirectTo("/start.js");
				return;
			}
			
			validarActividadCamaronera();
			
			if(esActividadCamaronera){
				if(zonaCamaronera != null && zonaCamaronera.equals("MIXTA")){
					documentoDocCamaroneraAlta = documentosImpactoEstudioFacade.documentoXTablaIdXIdDoc(informacionProyectoEia.getId(), "RA_CONCESION_MINERA_ALTA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
					documentoDocCamaroneraPlaya = documentosImpactoEstudioFacade.documentoXTablaIdXIdDoc(informacionProyectoEia.getId(), "RA_CONCESION_MINERA_PLAYA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
										
				}else if(zonaCamaronera != null && zonaCamaronera.equals("ALTA")){
					documentoDocCamaroneraAlta = documentosImpactoEstudioFacade.documentoXTablaIdXIdDoc(informacionProyectoEia.getId(), "RA_CONCESION_MINERA_ALTA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
				}else if(zonaCamaronera != null){
					documentoDocCamaroneraPlaya = documentosImpactoEstudioFacade.documentoXTablaIdXIdDoc(informacionProyectoEia.getId(), "RA_CONCESION_MINERA_PLAYA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
				}
				if(zonaCamaronera == null){
					esActividadCamaronera = false;
				}
				if(documentoDocCamaroneraAlta == null && documentoDocCamaroneraPlaya == null){
					esActividadCamaronera = false;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/estudioImpactoAmbiental/revisarEsIAIngresado.jsf");
	}

	public void cargarCatalogos(){
        areas = new ArrayList<String>();
		areasApoyo = new ArrayList<String>();
		
		//verifico el tipo de area responsable
		esPlantaCentral = false;
		esGad = false;
		agregarTecnicosEspecialistas = false;
		if (proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
			esPlantaCentral = true;
			agregarTecnicosEspecialistas = true;
		} else if (!proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)
				&& !proyectoLicenciaCoa.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) { 
			esGad = true;
			
			Area areaT = proyectoLicenciaCoa.getAreaResponsable();
			if (areaT.getTipoEnteAcreditado().equals("GOBIERNO")) {
				agregarTecnicosEspecialistas = true;
			} else if (areaT.getTipoEnteAcreditado().equals("MUNICIPIO")) {
				if (areaT.getAreaName().contains("QUITO")
						|| areaT.getAreaName().contains("GUAYAQUIL")
						|| areaT.getAreaName().contains("CUENCA")) {
					agregarTecnicosEspecialistas = true;
				}
			}
		}
		
		if(agregarTecnicosEspecialistas) {
			areas.add("tecnicoSocial");
            areas.add("tecnicoCartografo");
            areas.add("tecnicoBiotico");
		}
		
		if(esGad) {
			if(!inventarioForestal)
	        	areasApoyo.add("tecnicoInventarioForestal");
		} else {
	        if(!intersecaSNAP)
	            areasApoyo.add("tecnicoBiodiversidad");
	        if(!intersecaBP)
	            areasApoyo.add("tecnicoForestal");
	        if(!inventarioForestal)
	        	areasApoyo.add("tecnicoForestalRemocion");
		}
        
      	areasSeleccionadas = new String[1];
      	areasSeleccionadasApoyo = new String[1];
		
		numMaximoTecnicosApoyo =0;
		if (!agregarTecnicosEspecialistas) {
			numTecnicosApoyo = 0;
			String rolTecnico = (esGad) ? Constantes.getRoleAreaName("role.esia.gad.tecnico.responsable") : Constantes.getRoleAreaName("role.esia.cz.tecnico.responsable");
			
			listaUsuario = cargaLaboralFacade.cargaLaboralPorUsuarioArea(rolTecnico, proyectoLicenciaCoa.getAreaResponsable().getAreaName());
		}
	}
	
	public void validateDatos(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(equipoMultidisciplinario != null && equipoMultidisciplinario) {
			if (!agregarTecnicosEspecialistas && numTecnicosApoyo == 0 && (areasSeleccionadasApoyo.length == 0 || areasSeleccionadasApoyo[0] == null))
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "Debe seleccionar al menos un técnico especialista o seleccionar técnicos de Patrominio Natural.", null));
			
			if (agregarTecnicosEspecialistas && areasSeleccionadas.length == 0 && (areasSeleccionadasApoyo.length == 0 || areasSeleccionadasApoyo[0] == null))
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "Debe seleccionar al menos un técnico especialista o seleccionar técnicos de Patrominio Natural.", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public boolean guardar() throws Exception {
		try {
			informacionProyectoEia.setEquipoMultidisciplinario(equipoMultidisciplinario);
			informacionProyectoEia.setInspeccion(inspeccion);
			
			equipoApoyoProyecto.setNumeroTecnicosApoyo(null);
			equipoApoyoProyecto.setTecnicoSocial(false);
			equipoApoyoProyecto.setTecnicoCartografo(false);
			equipoApoyoProyecto.setTecnicoBiotico(false);
			equipoApoyoProyecto.setTecnicoBiodiversidad(false);
			equipoApoyoProyecto.setTecnicoForestal(false);
			equipoApoyoProyecto.setTecnicoForestalRemocion(false);
			
			if (equipoMultidisciplinario) {
				if(areasSeleccionadasApoyo.length > 0 && areasSeleccionadasApoyo[0] != null) {
					for (int i = 0; i < areasSeleccionadasApoyo.length; i++) {
						String areaSelect = areasSeleccionadasApoyo[i];
						switch (areaSelect) {
						case "tecnicoBiodiversidad":
							equipoApoyoProyecto.setTecnicoBiodiversidad(true);
							break;
						case "tecnicoForestal":
							equipoApoyoProyecto.setTecnicoForestal(true);
							break;
						case "tecnicoForestalRemocion":
							equipoApoyoProyecto.setTecnicoForestalRemocion(true);
							break;
						case "tecnicoInventarioForestal":
							equipoApoyoProyecto.setTecnicoForestalRemocion(true);
							break;
						default:
							break;
						}
					}
				}
				
				if(agregarTecnicosEspecialistas) {
					for (int i = 0; i < areasSeleccionadas.length; i++) {
						String areaSelect = areasSeleccionadas[i];
						switch (areaSelect) {
						case "tecnicoSocial":
							equipoApoyoProyecto.setTecnicoSocial(true);
							break;
						case "tecnicoCartografo":
							equipoApoyoProyecto.setTecnicoCartografo(true);
							break;
						case "tecnicoBiotico":
							equipoApoyoProyecto.setTecnicoBiotico(true);
							break;
						default:
							break;
						}
					}
				} else
					equipoApoyoProyecto.setNumeroTecnicosApoyo(numTecnicosApoyo);
			} 

			equipoApoyoProyecto.setInformacionProyectoEia(informacionProyectoEia);
			equipoApoyoProyectoEIACoaFacade.guardar(equipoApoyoProyecto);
			
			informacionProyectoEIACoaFacade.guardar(informacionProyectoEia);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void finalizar() throws Exception{
		try {
			if(!guardar())
				return;
			
			Map<String, Object> parametros = new HashMap<>();

			parametros.put("requiereEquipoMultidiciplinario", informacionProyectoEia.getEquipoMultidisciplinario());
			parametros.put("requiereTecnicosApoyo", false);
			parametros.put("esPlantaCentral", esPlantaCentral);
			parametros.put("esGad", esGad);
			
			if(informacionProyectoEia.getEquipoMultidisciplinario()) {
				parametros.put("requierePronunciamientoSnap", false);
				if(equipoApoyoProyecto.isTecnicoBiodiversidad()) {
					parametros.put("requierePronunciamientoSnap", true);
					
					//buscar usuario
					Area areaConservacion = (esPlantaCentral) ? areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS) : proyectoLicenciaCoa.getAreaResponsable(); 
					String tipoRol = (esPlantaCentral) ? "role.esia.pc.tecnico.conservacion" : "role.esia.cz.tecnico.conservacion";
					String tecnicoConservacion = recuperarUsuario(areaConservacion, tipoRol);
					if(tecnicoConservacion == null)
						return;
					else
						parametros.put("tecnicoConservacion", tecnicoConservacion);
				}
				
				parametros.put("requierePronunciamientoForestal", false);
				parametros.put("requierePronunciamientoInventario", false);
				if(equipoApoyoProyecto.isTecnicoForestal() || equipoApoyoProyecto.isTecnicoForestalRemocion()) {
					String tecnicoBosques = (String)variables.get("tecnicoBosques");
					
					if(tecnicoBosques == null) {
						//buscar usuario
						Area areaBosques = (esPlantaCentral) ? areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES) : proyectoLicenciaCoa.getAreaResponsable();
						if(esGad) {
							UbicacionesGeografica ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa).getUbicacionesGeografica();
							areaBosques = areaService.getAreaCoordinacionZonal(ubicacionPrincipal.getUbicacionesGeografica());
						}
						
						String tipoRol = (esPlantaCentral) ? "role.esia.pc.tecnico.bosques" : "role.esia.cz.tecnico.bosques";
						tecnicoBosques = recuperarUsuario(areaBosques, tipoRol);
						if(tecnicoBosques == null)
							return;
					} 
					
					parametros.put("tecnicoBosques", tecnicoBosques);
					
					if(equipoApoyoProyecto.isTecnicoForestal()) {
						parametros.put("requierePronunciamientoForestal", true);
					}
					if(equipoApoyoProyecto.isTecnicoForestalRemocion()) {
						parametros.put("requierePronunciamientoInventario", true);
					}
				}
				
				if(!agregarTecnicosEspecialistas && equipoApoyoProyecto.getNumeroTecnicosApoyo() != null && equipoApoyoProyecto.getNumeroTecnicosApoyo() > 0) {										
					tecnicosEspecialistas = new ArrayList<>();
					for(Usuario user : listaUsuario) {
						if(user.getSelectable()) {
							tecnicosEspecialistas.add(user.getNombre());
						}
					}
					
					String[] listaTecnicosApoyo = new String[tecnicosEspecialistas.size()];
					Integer contador = 0;
					for (String tecnico : tecnicosEspecialistas) {
						listaTecnicosApoyo[contador++] = tecnico;
					}
					
					parametros.put("listaTecnicosApoyo", listaTecnicosApoyo);
					parametros.put("requiereTecnicosApoyo", true);
				} else if(agregarTecnicosEspecialistas && (equipoApoyoProyecto.isTecnicoSocial() || equipoApoyoProyecto.isTecnicoCartografo() || equipoApoyoProyecto.isTecnicoBiotico())) {
					ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
					CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();
					
					Integer idSector = actividadPrincipal.getTipoSector().getId();
					String[] listaTecnicosApoyo = recuperarTecnicosEspecialistasPlantaCentral(idSector);
					
					if (listaTecnicosApoyo == null || listaTecnicosApoyo.length == 0) 
						return;
					
					
					parametros.put("listaTecnicosApoyo", listaTecnicosApoyo);
					parametros.put("requiereTecnicosApoyo", true);
				} 
			}
			

			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);

			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public String recuperarUsuario(Area areaResponsable , String tipoRol) {

		String rolTecnico = Constantes.getRoleAreaName(tipoRol);
		
		List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaResponsable);
		if (listaUsuario == null || listaUsuario.size() == 0) {
			LOG.error("No se encontró usuario " + rolTecnico + " en " + areaResponsable.getAreaName());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
			return null;
		}
		
		Usuario usuarioTecnico = null;
		String proceso = Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL + "'' , ''" + Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2;
		List<Usuario> listaTecnicosResponsables = asignarTareaFacade
					.getCargaLaboralPorUsuariosProceso(listaUsuario, proceso);
		usuarioTecnico = listaTecnicosResponsables.get(0);
		
		return usuarioTecnico.getNombre();
	}
	
	public String[] recuperarTecnicosEspecialistasPlantaCentral(Integer idSector) {
		tecnicosEspecialistas = new ArrayList<>();
		
		if(equipoApoyoProyecto.isTecnicoSocial()) {
			String tipoRol = (esPlantaCentral) ? "role.esia.pc.tecnico.social.tipoSector." + idSector : "role.esia.gad.tecnico.social";
			String tecnico = recuperarUsuario(proyectoLicenciaCoa.getAreaResponsable(), tipoRol);
			if(tecnico == null)
				return null;
			else 
				tecnicosEspecialistas.add(tecnico);
		}
		
		if(equipoApoyoProyecto.isTecnicoCartografo()) {
			String tipoRol = (esPlantaCentral) ? "role.esia.pc.tecnico.cartografo.tipoSector." + idSector : "role.esia.gad.tecnico.cartografo";
			String tecnico = recuperarUsuario(proyectoLicenciaCoa.getAreaResponsable(), tipoRol);
			if(tecnico == null)
				return null;
			else 
				tecnicosEspecialistas.add(tecnico);
		}
		
		if(equipoApoyoProyecto.isTecnicoBiotico()) {
			String tipoRol = (esPlantaCentral) ? "role.esia.pc.tecnico.biotico.tipoSector." + idSector : "role.esia.gad.tecnico.biotico";
			String tecnico = recuperarUsuario(proyectoLicenciaCoa.getAreaResponsable(), tipoRol);
			if(tecnico == null)
				return null;
			else 
				tecnicosEspecialistas.add(tecnico);
		}
		
		String[] listaTecnicosApoyo = new String[tecnicosEspecialistas.size()];
		Integer contador = 0;
		for (String tecnico : tecnicosEspecialistas) {
			listaTecnicosApoyo[contador++] = tecnico;
		}
		
		return listaTecnicosApoyo;
	}
	
	public void cargarDatosSegundaRevision() {
		listaInformes = new ArrayList<>();
		
		Map<String, InformeTecnicoEsIA> informes = new HashMap<>();
		
		List<Integer> listaIdTiposInformes = new ArrayList<>();
		
		if(informacionProyectoEia.getEquipoMultidisciplinario()) {
			List<InformeTecnicoEsIA> informesEstudio = informeTecnicoEsIAFacade.obtenerInformesPorEstudio(informacionProyectoEia);
			if(informesEstudio != null) {
				for (InformeTecnicoEsIA informe : informesEstudio) {
					if(informe.getTipoInforme().equals(3) || informe.getTipoInforme().equals(4) || 
							informe.getTipoInforme().equals(5)) {
						if(!listaIdTiposInformes.contains(informe.getTipoInforme())) {
							informes.put(informe.getUsuarioCreacion(), informe);
							listaIdTiposInformes.add(informe.getTipoInforme());
						} 
					} else if(informe.getTipoInforme().equals(6)) {
						if(!informes.containsKey(informe.getUsuarioCreacion()))
							informes.put(informe.getUsuarioCreacion(), informe);
					}
				}
				
				if(informes.size() > 0)
					listaInformes.addAll(informes.values());
			}
		}
		
		informacionProyectoEia.setInspeccion(null);
		inspeccion = null;
	}
	
	public String getnombreTecnico(String nombre) {
		Usuario objUsuario = usuarioFacade.buscarUsuarioWithOutFilters(nombre);
		if (objUsuario != null && objUsuario.getPersona() != null) {
			return objUsuario.getPersona().getNombre();
		}
		return "";
	}
	
	public void enviarRevision() {
		try {
			Map<String, Object> parametros = new HashMap<>();
			
			informacionProyectoEia.setInspeccion(inspeccion);
			informacionProyectoEIACoaFacade.guardar(informacionProyectoEia);
			
			if(informacionProyectoEia.getEquipoMultidisciplinario()) {
				if(informesSeleccionados != null && informesSeleccionados.size() > 0) {
					String[] listaTecnicosApoyo = new String[informesSeleccionados.size()];
					Integer contador = 0;
					for (InformeTecnicoEsIA informe : informesSeleccionados) {
						String usuarioInforme = informe.getUsuarioCreacion();
						
						if(informe.getTipoInforme().equals(3) 
								|| informe.getTipoInforme().equals(4) || informe.getTipoInforme().equals(5)) {
							//se actualiza el técnico responsable, cuando el técnico se encuentra desactivado 
							//y el informe es social, biotico, cartografo porque se asignan de acuerdo con la carga laboral
							Usuario objUsuario = usuarioFacade.buscarUsuario(informe.getUsuarioCreacion());
							if (objUsuario == null || objUsuario.getPersona() == null) {
								ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
								CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();
								
								Integer idSector = actividadPrincipal.getTipoSector().getId();
								
								if(informe.getTipoInforme().equals(3)) {
									String tipoRol = (esPlantaCentral) ? "role.esia.pc.tecnico.social.tipoSector." + idSector : "role.esia.gad.tecnico.social";
									String tecnico = recuperarUsuario(proyectoLicenciaCoa.getAreaResponsable(), tipoRol);
									if(tecnico != null)
										usuarioInforme = tecnico;
								}else if(informe.getTipoInforme().equals(5)) {
									String tipoRol = (esPlantaCentral) ? "role.esia.pc.tecnico.cartografo.tipoSector." + idSector : "role.esia.gad.tecnico.cartografo";
									String tecnico = recuperarUsuario(proyectoLicenciaCoa.getAreaResponsable(), tipoRol);
									if(tecnico != null)
										usuarioInforme = tecnico;
								}else if(informe.getTipoInforme().equals(4)) {
									String tipoRol = (esPlantaCentral) ? "role.esia.pc.tecnico.biotico.tipoSector." + idSector : "role.esia.gad.tecnico.biotico";
									String tecnico = recuperarUsuario(proyectoLicenciaCoa.getAreaResponsable(), tipoRol);
									if(tecnico != null)
										usuarioInforme = tecnico;
								}
							}
						}
						
						listaTecnicosApoyo[contador++] = usuarioInforme;
					}
					
					parametros.put("listaTecnicosApoyo", listaTecnicosApoyo);
					parametros.put("requiereTecnicosApoyo", true);
				} else {
					parametros.put("requiereTecnicosApoyo", false);
				}
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateSelectedUser(Usuario usuario) {
		if (usuario.getSelectable()) {
			numTecnicosApoyo++;
		} else {
			numTecnicosApoyo--;
		}
	}
	
	public void validarActividadCamaronera(){
		try {
			
			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = new ArrayList<ProyectoLicenciaCuaCiuu>();
			listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyectoLicenciaCoa);
			
			for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
				
				if(Constantes.getActividadesCamaroneras().contains(actividad.getCatalogoCIUU().getCodigo())){
					SubActividades subActividad1=actividad.getSubActividad();
					if(subActividad1.getSubActividades().getRequiereValidacionCoordenadas().equals(1)){
						esActividadCamaronera = true;	
						setZonaCamaronera(proyectoLicenciaCoa.getZona_camaronera());
					}					
				}				
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public StreamedContent descargarDocConcesionAlta() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoDocCamaroneraAlta != null && documentoDocCamaroneraAlta.getAlfrescoId() != null) {
				documentoContent = documentosImpactoEstudioFacade.descargar(documentoDocCamaroneraAlta.getAlfrescoId());
			} else if (documentoDocCamaroneraAlta.getContenidoDocumento() != null) {
				documentoContent = documentoDocCamaroneraAlta.getContenidoDocumento();
			}
			
			if (documentoDocCamaroneraAlta != null && documentoDocCamaroneraAlta.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoDocCamaroneraAlta.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public StreamedContent descargarDocConcesionPlaya() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoDocCamaroneraPlaya != null && documentoDocCamaroneraPlaya.getAlfrescoId() != null) {
				documentoContent = documentosImpactoEstudioFacade.descargar(documentoDocCamaroneraPlaya.getAlfrescoId());
			} else if (documentoDocCamaroneraPlaya.getContenidoDocumento() != null) {
				documentoContent = documentoDocCamaroneraPlaya.getContenidoDocumento();
			}
			
			if (documentoDocCamaroneraPlaya != null && documentoDocCamaroneraPlaya.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoDocCamaroneraPlaya.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
}