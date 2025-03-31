package ec.gob.ambiente.rcoa.preliminar.contoller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.VerProyectoBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.proyecto.controller.VerProyectoRcoaBean;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ListadoProyectosPendienteAsignacionController implements Serializable {

	private static final long serialVersionUID = 2198593440217678246L;
	
	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;

	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@Getter
	@Setter
	private List<ProyectoCustom> proyectos;
	
	@PostConstruct
    public void init() {
		
		proyectos = new ArrayList<>();
		
		List<ProyectoCustom>  proyectosRcoa = proyectoLicenciaCoaFacade.listarProyectosPorAsignarArea();
		proyectos.addAll(proyectosRcoa);
	}
	
	public void actualizar(ProyectoCustom proyecto) {
		try {
			
			ProyectoLicenciaCoa proyectoRcoa = proyectoLicenciaCoaFacade.buscarProyecto(proyecto.getCodigo());
			
			proyectosBean.seleccionar(proyectoRcoa,true);
			
			JsfUtil.getBean(VerProyectoRcoaBean.class).cargarDatos();
			
			JsfUtil.redirectTo("/pages/rcoa/preliminar/asignarAreaResponsable.jsf");
			
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}

}
