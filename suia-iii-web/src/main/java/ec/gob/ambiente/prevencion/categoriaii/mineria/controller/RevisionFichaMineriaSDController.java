package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.util.NotificacionInternaUtil;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoArea;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.tipoarea.facade.TipoAreaFacade;
import ec.gob.ambiente.suia.tramiteresolver.RegistroGeneradorTramiteResolver;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisionFichaMineriaSDController implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private ContactoFacade contactoFacade;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoRcoa;
	
	@Getter
	@Setter
	private PerforacionExplorativa perforacionExplorativa;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	//nuevo 
	private FichaAmbientalMineria fichaMineria;
	
	@EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private boolean estadoObservaciones=false;
	
	@Setter
    @Getter
	private Boolean esProyectoRcoa;
	
	@PostConstruct
	public void init(){
		esProyectoRcoa = false;
		perforacionExplorativa = new PerforacionExplorativa();		
		verProyecto(proyectosBean.getProyecto());
	}
	
	public void verProyecto(ProyectoLicenciamientoAmbiental proyectoL){
		try {
			if(proyectoL.getId()!=null)
				this.proyecto = proyectoL;
			else
			{
				esProyectoRcoa = true;
				this.proyectoRcoa = proyectosBean.getProyectoRcoa();
			}
				
			if(esProyectoRcoa)
				perforacionExplorativa =fichaAmbientalMineria020Facade.cargarPerforacionExplorativaRcoa(proyectoRcoa.getId());
			else
			{
				perforacionExplorativa =fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyecto);
				fichaMineria = fichaAmbientalMineriaFacade.obtenerPorProyecto(proyecto);
			}
			
			perforacionExplorativa.setApproveTechnical(null);
		} catch (Exception e) {
			e.printStackTrace();
		}				
	}
	
	public void guardar(){
		try {
			
			//falta hacer el método para poder suspender el trámite y activar el pago
			
			// verifico que se haya descargado todos los documentos
			if (JsfUtil.getBean(ComponenteSocialSDController.class).verificarDocumentosDownload()){
				fichaAmbientalMineria020Facade.guardarPerforacionExplorativa(perforacionExplorativa);
				
				if(!perforacionExplorativa.getApproveTechnical()){
					suspenderProcesoHabilitarPago();
					notificacion();
				}else{
					if(esProyectoRcoa) {
						proyectoRcoa.setProyectoFinalizado(true);
						proyectoLicenciaCoaFacade.guardar(proyectoRcoa);
					}
					
					iniciarRegistroGeneradorDesechos(proyecto!=null?proyecto.getCodigo():proyectoRcoa.getCodigoUnicoAmbiental());
					notificacionTecnicoFinanciero();
					notificacionAprobado();
				}
				JsfUtil.addMessageInfo("Guardado con éxito");
				JsfUtil.redirectTo("/prevencion/categoria2/v2/fichaMineria020/listaProyectosSD.jsf");
			}else{
				JsfUtil.addMessageError("Debes descargar los documentos para poder guardar.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al guardar");
		}
		
	}
	
	private void iniciarRegistroGeneradorDesechos(String codigo){
		try {
			
			variables = new HashMap<String, Object>();
			variables = registroGeneradorDesechosFacade.validacionRGD(codigo);
			
			if(variables == null){
				registroGeneradorDesechosFacade.iniciarEmisionProcesoRegistroGenerador(
						proyecto.getUsuario(), RegistroGeneradorTramiteResolver.class, proyecto);
				proyecto.setRgdEncurso(true);
				proyectoLicenciamientoAmbientalFacade.actualizarProyecto(proyecto);
			}		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void suspenderProcesoHabilitarPago(){
		try {
			String pagoRgd = "";
			List<TransaccionFinanciera> listTransaccionFinanciera= new ArrayList<TransaccionFinanciera>();
			if(esProyectoRcoa)
				listTransaccionFinanciera=transaccionFinancieraFacade.cargarTransacciones(proyecto.getId());
			else
				listTransaccionFinanciera=transaccionFinancieraFacade.cargarTransaccionesRcoa(proyectoRcoa.getId());
			
			pagoRgd=null;
			if(listTransaccionFinanciera.size()>0)
			pagoRgd=listTransaccionFinanciera.get(0).getNumeroTransaccion();			
			
			fichaAmbientalMineria020Facade.modificarPropietarioTareasRGD(proyecto!=null?proyecto.getCodigo():proyectoRcoa.getCodigoUnicoAmbiental(),JsfUtil.getLoggedUser(),true,pagoRgd,JsfUtil.getSenderIp());
			if(!esProyectoRcoa)
				fichaAmbientalMineria020Facade.setEstadoProyecto(proyecto.getCodigo(), false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void notificacion(){
		try{
			Usuario usuario = usuarioFacade.buscarUsuarioPorId(proyecto!=null?proyecto.getUsuario().getId():proyectoRcoa.getUsuario().getId());
			
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuario.getNombre());
			
			String nombreProponente = "";
			
			if(organizacion != null)
				nombreProponente = organizacion.getNombre();
			else
				nombreProponente = usuario.getPersona().getNombre();	
			
			List<Contacto> listaContactos = contactoFacade.buscarPorPersona(usuario.getPersona());
			String emailProponente = "";
			for(Contacto contacto : listaContactos){
				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
					emailProponente = contacto.getValor();
					break;
				}				
			}						
			
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacionSD("bodyNotificacionEliminacionProyectoSD");
			mensaje = mensaje.replace("nombre_proponente", nombreProponente);
			mensaje = mensaje.replace("codigo_proyecto", proyecto!=null?proyecto.getCodigo():proyectoRcoa.getCodigoUnicoAmbiental());
			mensaje = mensaje.replace("nombre_proyecto", proyecto!=null?proyecto.getNombre():proyectoRcoa.getNombreProyecto());
			mensaje = mensaje.replace("fecha_registro", JsfUtil.devuelveFechaEnLetrasSinHora(proyecto!=null?proyecto.getFechaRegistro():proyectoRcoa.getFechaGeneracionCua()));
			
	        String tramite = proyecto!=null?proyecto.getCodigo():proyectoRcoa.getCodigoUnicoAmbiental();
	        NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
			mail_a.sendEmailInformacionProponente(emailProponente, nombreProponente, mensaje, "Proyecto Archivado", tramite, usuario, loginBean.getUsuario());		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void notificacionAprobado(){
		try{
			Usuario usuario = usuarioFacade.buscarUsuarioPorId(proyecto!=null?proyecto.getUsuario().getId():proyectoRcoa.getUsuario().getId());
			
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuario.getNombre());
			
			String nombreProponente = "";
			
			if(organizacion != null)
				nombreProponente = organizacion.getNombre();
			else
				nombreProponente = usuario.getPersona().getNombre();	
			
			List<Contacto> listaContactos = contactoFacade.buscarPorPersona(usuario.getPersona());
			String emailProponente = "";
			for(Contacto contacto : listaContactos){
				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
					emailProponente = contacto.getValor();
					break;
				}				
			}						
			
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacionSD("bodyNotificacionAprovacionProyectoSD");
			mensaje = mensaje.replace("nombre_proponente", nombreProponente);
			mensaje = mensaje.replace("codigo_proyecto", proyecto!=null?proyecto.getCodigo():proyectoRcoa.getCodigoUnicoAmbiental());
			mensaje = mensaje.replace("nombre_proyecto", proyecto!=null?proyecto.getNombre():proyectoRcoa.getNombreProyecto());
			mensaje = mensaje.replace("fecha_registro", JsfUtil.devuelveFechaEnLetrasSinHora(proyecto!=null?proyecto.getFechaRegistro():proyectoRcoa.getFechaGeneracionCua()));
			

	        String tramite = proyecto!=null?proyecto.getCodigo():proyectoRcoa.getCodigoUnicoAmbiental();
	        NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
			mail_a.sendEmailInformacionProponente(emailProponente, nombreProponente, mensaje, "Proyecto Aprobado", tramite, usuario, loginBean.getUsuario());
			
			
			if(proyecto == null && proyectoRcoa != null) {
				//notificacion usuarios internos
				NotificacionInternaUtil.enviarNotificacionRegistro(proyectoRcoa, proyectoRcoa.getNumeroResolucion());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	private void notificacionTecnicoFinanciero(){
		try{
			
			Usuario usuario = usuarioFacade.buscarUsuarioPorId(proyecto!=null?proyecto.getUsuario().getId():proyectoRcoa.getUsuario().getId());
						
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuario.getNombre());
			
			String nombreProponente = "";
			
			if(organizacion != null)
				nombreProponente = organizacion.getNombre();
			else
				nombreProponente = usuario.getPersona().getNombre();	
									
//			List<Contacto> listaContactos = contactoFacade.buscarPorPersona(usuario.getPersona());
//			String emailProponente = "";
//			for(Contacto contacto : listaContactos){
//				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
//					emailProponente = contacto.getValor();
//					break;
//				}				
//			}							
			
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacionSD("bodyNotificacionTecnicoFinancieroSD");
			mensaje = mensaje.replace("nombre_proponente", nombreProponente);
			mensaje = mensaje.replace("codigo_proyecto", proyecto!=null?proyecto.getCodigo():proyectoRcoa.getCodigoUnicoAmbiental());
			mensaje = mensaje.replace("nombre_proyecto", proyecto!=null?proyecto.getNombre():proyectoRcoa.getNombreProyecto());
			if(fichaMineria!=null)
				mensaje = mensaje.replace("registro_ambiental", fichaMineria.getNumeroResolucion() != null ? fichaMineria.getNumeroResolucion() : ""); //registro ambiental
			else
				mensaje = mensaje.replace("registro_ambiental", proyectoRcoa.getNumeroResolucion() != null ? proyectoRcoa.getNumeroResolucion() : ""); //registro ambiental
			
			mensaje = mensaje.replace("fecha_registro", JsfUtil.devuelveFechaEnLetrasSinHora(proyecto!=null?proyecto.getFechaRegistro():proyectoRcoa.getFechaGeneracionCua()));  // recha Registro
			mensaje = mensaje.replace("total_pma", perforacionExplorativa.getTotalPma() != null ? perforacionExplorativa.getTotalPma().toString() : ""); 
	        
			List<Persona> tecnicosFinanciero = getPersonaRol("TÉCNICO FINANCIERO");
			
			
			for(Persona tecnico : tecnicosFinanciero){
				String emailTecnico = "";
				List<Contacto> listaContactosFinanciero = contactoFacade.buscarPorPersona(tecnico);			
				for(Contacto contacto : listaContactosFinanciero){
					if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
						emailTecnico = contacto.getValor();
						break;
					}				
				}

            	//busco el usuario del tecnico
            	Usuario usuarioTecnico = usuarioFacade.buscarUsuario(tecnico.getPin());
    	        String tramite = proyecto!=null?proyecto.getCodigo():proyectoRcoa.getCodigoUnicoAmbiental();
				NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
				mail_a.sendEmailInformacionProponente(emailTecnico, nombreProponente, mensaje, "Proceso Sondeo de pruebas o reconocimiento", tramite, usuarioTecnico, loginBean.getUsuario());	
			}
	        	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@EJB
	private TipoAreaFacade tipoAreaFacade;
	private List<Persona> getPersonaRol(String rol) throws ServiceException {
		TipoArea tipoArea = new TipoArea();
		tipoArea.setId(1);
		List<Persona> listaPersonas = new ArrayList<Persona>();
		
		List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolTipoArea(rol, tipoArea);
		if (!usuarios.isEmpty()) {
			for(Usuario usuario : usuarios){
				listaPersonas.add(usuario.getPersona());
			}	
			
			return listaPersonas;
		} else {
			throw new ServiceException("No existe el usuario con el rol " + rol);
		}

	}
	
	public void cancelar(){
		JsfUtil.redirectTo("/prevencion/categoria2/v2/fichaMineria020/listaProyectosSD.jsf");
	}
	
	public void verObservacion()
	{
		if(perforacionExplorativa.getApproveTechnical())
			estadoObservaciones=false;
		else
			estadoObservaciones=true;
			
	}
	

}
