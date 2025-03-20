package ec.gob.ambiente.proyectos.controllers;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ViewScoped
@ManagedBean
public class ReactivarProyectosController implements Serializable{

	private static final long serialVersionUID = 4689463316622237704L;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@Getter
	@Setter
	private String codigoTramite;
	
	@Getter
	@Setter
	private String motivo;
	
	@Getter
	@Setter
	private Boolean deshabilitado = true;
	
	@Getter
	@Setter
	private String motivoDesactivacion;
	
	@Getter
	@Setter
	private String fechaDesactivacion;
	
	@PostConstruct
	public void init(){
		
	}
	
	public void validarCodigo(){
		try{
			motivoDesactivacion = "";
			fechaDesactivacion = "";
			ProyectoCustom proyecto = new ProyectoCustom();			
			
			proyecto = proyectoLicenciamientoFacade.buscarProyectoPorCodigoTipo(codigoTramite);
			
			if(proyecto != null && proyecto.getId() != null){				
				deshabilitado = false;		
				motivoDesactivacion = proyecto.getMotivoEliminar();
				fechaDesactivacion = proyecto.getFechaArchivo();
			}else{
				
				ProyectoCustom proyectoRcoa = new ProyectoCustom();
				proyectoRcoa = proyectoLicenciamientoFacade.buscaProyectoPorCodigoTipoRcoa(codigoTramite);
				
				if(proyectoRcoa != null && proyectoRcoa.getId() != null){					
					deshabilitado = false;
					motivoDesactivacion = proyectoRcoa.getMotivoEliminar();
					fechaDesactivacion = proyectoRcoa.getRegistro();					
				}else{
					JsfUtil.addMessageError("El código de proyecto no existe, no es un licencia ambiental o es un proyecto activo.");
				}				
			}			
		}catch(Exception e){
			e.printStackTrace();			
		}
	}
	
	public void reactivar(){
		try {
			ProyectoCustom proyecto = new ProyectoCustom();
			String mensaje = "";
			
			proyecto = proyectoLicenciamientoFacade.buscarProyectoPorCodigoTipo(codigoTramite);
			
			if(proyecto != null && proyecto.getId() != null){			
				
				String motivoString = proyecto.getMotivoEliminar() + " -Activacion- " + motivo;
								
				proyectoLicenciaCoaFacade.modificarTareaBpm(codigoTramite, motivo);
				
				proyectoLicenciamientoFacade.actualizarEstadoProyectoSuia(Integer.valueOf(proyecto.getId()), motivoString);
				
				JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
								
			}else{
				
				ProyectoCustom proyectoRcoa = new ProyectoCustom();
				proyectoRcoa = proyectoLicenciamientoFacade.buscaProyectoPorCodigoTipoRcoa(codigoTramite);
				
				if(proyectoRcoa != null && proyectoRcoa.getId() != null){
					String motivoString = proyectoRcoa.getMotivoEliminar() + " -Activacion- " + motivo;
										
					mensaje = proyectoLicenciaCoaFacade.modificarTareaBpmRcoa(codigoTramite, motivo);
					
					if(mensaje.equals("")){
						proyectoLicenciamientoFacade.actualizarEstadoProyectoRcoa(Integer.valueOf(proyectoRcoa.getId()), motivoString);
						
						JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
					}else{
						JsfUtil.addMessageError(mensaje);
					}
					
				}else{
					JsfUtil.addMessageError("El código de proyecto no existe o no es un licencia ambiental.");
				}				
			}
			
			deshabilitado = true;
			motivo = "";
			motivoDesactivacion = "";
			fechaDesactivacion = "";
			codigoTramite = "";
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO);
		}
	}
	
	public void cancelar(){
		deshabilitado = true;
		motivo = "";
		codigoTramite = "";
	}
	

}
