package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

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
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class InformacionProyectosSDController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	
//	@Getter
//	@Setter
//	private List<ProyectoLicenciamientoAmbiental> listaProyectos, listaProyectosSD;
	
	@Getter
	@Setter
	private List<PerforacionExplorativa> listaProyectos, listaProyectosSD;
	
	@Setter
	@Getter
	private List<ProyectoCustom> proyectos;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@PostConstruct
	private void init(){
		try{
			
//			listaProyectos = new ArrayList<ProyectoLicenciamientoAmbiental>();	
//			listaProyectosSD = new ArrayList<ProyectoLicenciamientoAmbiental>();
//			
//			listaProyectos = fichaAmbientalMineria020Facade.cargarProyectosPerforacionExplorativa();
//			
//			for(ProyectoLicenciamientoAmbiental proyecto : listaProyectos){
////				if(fichaAmbientalMineria020Facade.consultarPagoPorTramite(proyecto.getCodigo())){
//					if(comprobarPago(proyecto.getId())){
//						listaProyectosSD.add(proyecto);
//					}					
////				}
//			}	
			
			listaProyectos = new ArrayList<PerforacionExplorativa>();	
			listaProyectosSD = new ArrayList<PerforacionExplorativa>();
			
			listaProyectos = fichaAmbientalMineria020Facade.cargarProyectosPerforacionExplorativa2();
			
			for(PerforacionExplorativa proyecto : listaProyectos){
//				if(fichaAmbientalMineria020Facade.consultarPagoPorTramite(proyecto.getCodigo())){
				if(proyecto.getProyectoLicenciamientoAmbiental()!=null)
				{
					if(comprobarPago(proyecto.getProyectoLicenciamientoAmbiental().getId()))
						listaProyectosSD.add(proyecto);
				}
				else
				{
					if(comprobarPagoRcoa(proyecto.getProyectoLicenciaCoa().getId()))
						listaProyectosSD.add(proyecto);
					
				}
//				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	public String seleccionar(ProyectoLicenciamientoAmbiental proyecto) {
		try {
			proyectosBean.setProyectoSD(proyecto);
			
			Usuario usuario = usuarioFacade.buscarUsuarioPorId(proyecto.getUsuario().getId());
			
			proyectosBean.setProponente(usuario.getPersona().getNombre());
			return JsfUtil.actionNavigateTo("/prevencion/categoria2/v2/fichaMineria020/aprobarRegistroMineriaSD.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	public String seleccionar(PerforacionExplorativa proyecto) {
		try {
			if(proyecto.getProyectoLicenciamientoAmbiental()!=null) {
				proyectosBean.setProyecto(proyecto.getProyectoLicenciamientoAmbiental());
				proyectosBean.setProyectoRcoa(null);
			} else {
				proyectosBean.setProyectoRcoa(proyecto.getProyectoLicenciaCoa());
				proyectosBean.setProyecto(new ProyectoLicenciamientoAmbiental());
			}
			
			Usuario usuario = usuarioFacade.buscarUsuarioPorId(proyecto.getProyectoLicenciamientoAmbiental()!=null?proyecto.getProyectoLicenciamientoAmbiental().getUsuario().getId():proyecto.getProyectoLicenciaCoa().getUsuario().getId());
			
			proyectosBean.setProponente(usuario.getPersona().getNombre());
			return JsfUtil.actionNavigateTo("/prevencion/categoria2/v2/fichaMineria020/aprobarRegistroMineriaSD.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	private boolean comprobarPago(Integer id){
		try {
			List<TransaccionFinanciera> listTransaccionFinanciera= new ArrayList<TransaccionFinanciera>();
			listTransaccionFinanciera=transaccionFinancieraFacade.cargarTransacciones(id);
			
			if(listTransaccionFinanciera.size()>0)
				return true;
			else 
				return false;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean comprobarPagoRcoa(Integer id){
		try {
			List<TransaccionFinanciera> listTransaccionFinanciera= new ArrayList<TransaccionFinanciera>();
			listTransaccionFinanciera=transaccionFinancieraFacade.cargarTransaccionesRcoa(id);
			
			if(listTransaccionFinanciera.size()>0)
				return true;
			else 
				return false;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
