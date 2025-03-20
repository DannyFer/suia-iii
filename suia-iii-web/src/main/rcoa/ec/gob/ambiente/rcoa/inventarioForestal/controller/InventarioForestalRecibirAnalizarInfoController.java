
package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.DocumentoInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ReporteInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ReporteInventarioForestal;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;
import lombok.Getter;
import lombok.Setter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author Luis Lema
 * @version Revision: 1.0
 */
@ViewScoped
@ManagedBean

public class InventarioForestalRecibirAnalizarInfoController implements Serializable{

    private static final long serialVersionUID = 165685472149658047L;
    private final Logger LOG = Logger.getLogger(InventarioForestalRecibirAnalizarInfoController.class);
    
    @EJB
	private ProcesoFacade procesoFacade;
	private Map<String, Object> variables;
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    
    @EJB
    private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
    
    @EJB
    private ProyectoLicenciaCoaFacade ProyectoLicenciaCoaFacade;
    
    @EJB
    private ReporteInventarioForestalFacade ReporteInventarioForestalFacade;
    
    @EJB
    private DocumentoInventarioForestalFacade documentoInventarioForestalFacade;
    @EJB
    private AsignarTareaFacade asignarTareaFacade;
    @EJB
    private UsuarioFacade usuarioFacade;
    
    @Getter
    @Setter
    private InventarioForestalAmbiental inventarioForestalAmbiental;
    
    @Getter
    @Setter
    private ProyectoLicenciaCoa proyectoLicenciaCoa;
    
    @Getter
    @Setter
    private ReporteInventarioForestal reporteInventarioForestal;
    
    @Getter
    @Setter
    private DocumentoInventarioForestal documentoInventarioForestal;
    
    
    @Getter
    @Setter
    private Boolean requiereInspeccion;
    @Getter
    @Setter
    private Integer idProyecto;
    @Getter
    @Setter
    private String tramite, coordinadorForestal;
    
    private Usuario usuario;

    @PostConstruct
    public void init() {
    	try {
    		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
    		tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
    		coordinadorForestal = (String) variables.get("coordinadorForestal");
    		idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
    		proyectoLicenciaCoa = ProyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
    		inventarioForestalAmbiental = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(proyectoLicenciaCoa.getId());
    		requiereInspeccion = inventarioForestalAmbiental.getInspeccionTecnica();
    	} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del Inventario Forestal.");
		}
    }
    
    private Usuario asignarCoordinadorForestal() {
    	Area areaTramite = proyectoLicenciaCoa.getAreaInventarioForestal();
    	String rolPrefijo;
		String rolCoordinador;
    	if (areaTramite.getAreaAbbreviation() == "DRA") {
    		rolPrefijo = "role.inventario.pc.coordinador";
		} else {
			rolPrefijo = "role.inventario.cz.coordinador";
		}
    	rolCoordinador = Constantes.getRoleAreaName(rolPrefijo);
    	List<Usuario> listaTecnicosResponsables = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolCoordinador, areaTramite.getAreaName());			

		if (listaTecnicosResponsables==null || listaTecnicosResponsables.isEmpty()){
			LOG.error("No se encontro usuario " + rolCoordinador + " en " + areaTramite.getAreaName());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}
		Usuario tecnicoResponsable = listaTecnicosResponsables.get(0);
		return tecnicoResponsable;
    }

    public void enviar() {
    	inventarioForestalAmbiental.setInspeccionTecnica(requiereInspeccion);
    	inventarioForestalAmbientalFacade.guardar(inventarioForestalAmbiental);
        Map<String, Object> params=new HashMap<>();
        params.put("requiereInspeccion",requiereInspeccion);
        if (coordinadorForestal == null) {
        	Usuario coordinador = asignarCoordinadorForestal();
        	params.put("coordinadorForestal",coordinador.getNombre());
        } else {
			Usuario tecnicoBPM = usuarioFacade.buscarUsuario(coordinadorForestal);
			if (tecnicoBPM.getEstado() == false) {
				Usuario tecnico = asignarCoordinadorForestal();
				params.put("tecnicoForestal",tecnico.getNombre());
			}
		}
		try {
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
    }

}
