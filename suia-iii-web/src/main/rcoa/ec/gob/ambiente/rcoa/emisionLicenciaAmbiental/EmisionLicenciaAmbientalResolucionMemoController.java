package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.controller.InventarioForestalInformeTecnicoPronunciamientoController;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class EmisionLicenciaAmbientalResolucionMemoController extends DocumentoReporteResolucionMemoController implements Serializable {

	private static final long serialVersionUID = 5382084905489409831L;

	private static final Logger LOG = Logger.getLogger(InventarioForestalInformeTecnicoPronunciamientoController.class);
	
	public EmisionLicenciaAmbientalResolucionMemoController() {	
		super();
	}

	/* BEANs */
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;

	/* EJBs */
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@Getter
	@Setter
	public String tramite, coordinadorSector;

	@Getter
	@Setter
	private Boolean existeObservaciones, existeObservacionesResolucion;
	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporteInforme;
	
	@PostConstruct
	public void init() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			coordinadorSector = (String) variables.get("coordinadorSector");
			idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
			
			existeObservaciones = false;
			if (variables.get("existeObservacion") != null) {
				existeObservaciones = Boolean.parseBoolean(variables.get("existeObservacion").toString());
			}
			
			existeObservacionesResolucion = false;
			if (variables.get("pronunciamientoConformidadLegal") != null) {
				existeObservacionesResolucion = !Boolean.parseBoolean(variables.get("pronunciamientoConformidadLegal").toString());
			}

			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			inicializarResolucionMemorando();
			visualizarResolucion(true);
			visualizarMemorando(true);
		} catch (Exception e) {
			JsfUtil.addMessageError("Error visualizar informe / oficio");
			e.printStackTrace();
		}
	}
	
	private boolean validarResolucion() {
		Boolean validarResolucion = true;
		List<String> msg= new ArrayList<String>();
		if (documentoResolucion.getConsiderando() == null) {
    		msg.add("Ingrese la Consideración");
		}
		if (documentoResolucion.getResuelve() == null) {
    		msg.add("Ingrese Resuelve");
		}
		if (msg.size() > 0) {
			JsfUtil.addMessageError(msg);
			validarResolucion = false;
		}
		return validarResolucion;
	}
	
	private boolean validarMemorando() {
		Boolean validarMemorando = true;
		List<String> msg= new ArrayList<String>();
		if (documentoMemorando.getAsuntoOficio() == null) {
    		msg.add("Ingrese el Asunto");
		}
		if (documentoMemorando.getPronunciamientoOficio() == null) {
    		msg.add("Ingrese el Pronunciamiento");
		}
		if (msg.size() > 0) {
			JsfUtil.addMessageError(msg);
			validarMemorando = false;
		}
		return validarMemorando;
	}
	
	private Usuario asignarCoordinadorSector() {
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
    	String rolPrefijo;
		String rolTecnico;
		rolPrefijo = "role.esia.cz.coordinador";
    	
    	String tipoArea=proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().toLowerCase();
    	
    	if(tipoArea.contains("pc")) {
			ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
			CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();
			Integer idSector = actividadPrincipal.getTipoSector().getId();
			rolPrefijo = "role.esia.pc.coordinador.tipoSector." + idSector;
		} else if(tipoArea.contains("ea")) {
			rolPrefijo = "role.esia.gad.coordinador";
		}
    	
    	rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
    	
    	List<Usuario> listaUsuariosCargaLaboral = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, areaTramite.getAreaName());			

		if (listaUsuariosCargaLaboral==null || listaUsuariosCargaLaboral.isEmpty()){
			LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
			return null;
		}
		
		Usuario nuevoResponsable = null;
		
		// recuperar tecnico de bpm y validar si el usuario existe en el listado anterior		
		Usuario usuarioTecnico = usuarioFacade.buscarUsuario(coordinadorSector);
		if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
			if (listaUsuariosCargaLaboral != null && listaUsuariosCargaLaboral.size() >= 0
					&& listaUsuariosCargaLaboral.contains(usuarioTecnico)) {
				nuevoResponsable = usuarioTecnico;
			}
		}
		
		// si no se encontró el usuario se realiza la busqueda de uno nuevo y se actualiza en el bpm
		if (nuevoResponsable == null) {
			nuevoResponsable = listaUsuariosCargaLaboral.get(0);
		}

		return nuevoResponsable;
    }

	public void enviar() {
		try {
			if (validarResolucion() && validarMemorando()) {
				try {
					
					oficioResolucionAmbientalFacade.guardar(documentoResolucion);
					oficioResolucionAmbientalFacade.guardar(documentoMemorando);
					
	    			Map<String, Object> params=new HashMap<>();

	    			Usuario tecnico = asignarCoordinadorSector();
	    			if(tecnico == null) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
    					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
    					return;
					} else if (coordinadorSector == null || !coordinadorSector.equals(tecnico.getNombre())) {
						params.put("coordinadorSector",tecnico.getNombre());
	    				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
					}
	    			
	    			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
	    			
	    			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	    	        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	    		} catch (JbpmException e) {
	    			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
	    			e.printStackTrace();
	    		}
			}
		} catch (Exception e) {							
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}

	public void guardarResolucion() {
		try {
			oficioResolucionAmbientalFacade.guardar(documentoResolucion);
			
			visualizarResolucion(true);
			
			JsfUtil.addMessageInfo("Datos Guardados Correctamente");
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void guardarMemo() {
		try {
			oficioResolucionAmbientalFacade.guardar(documentoMemorando);
			
			visualizarMemorando(true);
			
			JsfUtil.addMessageInfo("Datos Guardados Correctamente");
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public StreamedContent descargarPlantillaOficio() throws IOException {
		DefaultStreamedContent content = null;
		try {
			String nombreFormato = "Anexo01_Resolucion_Licencia_Ambiental.docx";
			String nombreDoc = "Anexo 01 - Resolución de Licencia Ambiental";
			
            byte[] documentoGuia = documentoResolucionAmbientalFacade.descargarDocumentoPorNombre(nombreFormato);
            if (documentoGuia != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documentoGuia));
				content.setName(nombreDoc + ".docx");
				return content;
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

}
