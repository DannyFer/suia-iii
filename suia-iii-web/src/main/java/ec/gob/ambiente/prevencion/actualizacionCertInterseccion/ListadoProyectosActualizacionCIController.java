package ec.gob.ambiente.prevencion.actualizacionCertInterseccion;

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
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.AutorizacionesAdministrativasFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.PermisoAmbiental;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.integracion.bean.ContenidoExterno;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.PermisoAmbientalFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.Proyecto4CategoriasFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoActualizacionCertificadoInterseccionFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ListadoProyectosActualizacionCIController implements Serializable {

	private static final long serialVersionUID = 2198593440217678246L;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{verProyectoBean}")
    private VerProyectoBean verProyectoBean;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
	private Proyecto4CategoriasFacade proyecto4CategoriasFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private PermisoAmbientalFacade permisoAmbientalFacade;
	
	@EJB
	private AutorizacionesAdministrativasFacade autorizacionesAdministrativasFacade;
	
	@EJB
	private ProyectoActualizacionCertificadoInterseccionFacade proyectoActualizacionCIFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@Getter
	@Setter
	private List<ProyectoCustom> proyectos;
	
	@PostConstruct
    public void init() {
		
		proyectos = new ArrayList<>();
		
		proyectos =  proyectoActualizacionCIFacade.listarLicenciasEnTramite(JsfUtil.getLoggedUser().getListaAreaUsuario());
		
		List<ProyectoCustom> proyectosHidrocarburos = proyectoActualizacionCIFacade.listarLicenciasHidrocarburosEnTramite(JsfUtil.getLoggedUser().getListaAreaUsuario());
		proyectos.addAll(proyectosHidrocarburos);
		
		List<ProyectoCustom>  proyectos4Categorias = proyectoActualizacionCIFacade.listarLicencias4CategoriasEnTramite(JsfUtil.getLoggedUser().getListaAreaUsuario());
		proyectos.addAll(proyectos4Categorias);
		
		List<ProyectoCustom>  proyectosRcoa = proyectoLicenciaCoaFacade.listarLicenciasRcoaEnTramite(JsfUtil.getLoggedUser().getListaAreaUsuario());
		proyectos.addAll(proyectosRcoa);
	}
	
	public void solicitar(ProyectoCustom proyecto) {
		try {
			
			Object[] parametrosCorreo = new Object[] {proyecto.getCodigo()};
			
			Usuario responsableProyecto = null;
			
			String tipoProyecto = proyecto.getSourceType();
			switch (tipoProyecto) {
			case "source_type_external_suia":
				List<PermisoAmbiental> listaProyecto4Categorias = permisoAmbientalFacade
						.getProyectos4cat(proyecto.getCodigo(), null, null, null);

				if (listaProyecto4Categorias != null) {
					Usuario userResponsable = usuarioFacade.buscarUsuario(listaProyecto4Categorias.get(0).getCedulaProponente()); 
					responsableProyecto = userResponsable;
				}
				
				proyectoActualizacionCIFacade.guardarHistorialActualizacionCertificado(proyecto.getCodigo(), 2, null);
				proyectoActualizacionCIFacade.guardarSolicitudActualizacion(proyecto.getCodigo(), 2);  //2 aprobado para modificar por el operador
				
				break;
			case "source_type_internal":
				ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(proyecto.getCodigo());
				
				proyectoLicenciamientoAmbiental.setEstadoActualizacionCertInterseccion(2); //2 aprobado para modificar por el operador
				proyectoActualizacionCIFacade.guardarEstadoActualizacionCertificado(proyectoLicenciamientoAmbiental, null);
				
				responsableProyecto = proyectoLicenciamientoAmbiental.getUsuario();
				
				Boolean esHidrocarburos =JsfUtil.getBean(ContenidoExterno.class).esHidrocarburos(proyecto);
				if (esHidrocarburos) {
					proyectoActualizacionCIFacade.guardarSolicitudActualizacion(proyecto.getCodigo(), 2);  //2 aprobado para modificar por el operador
				}
				
				break;
			case "source_type_rcoa":
				ProyectoLicenciaCoa proyectoRcoa = proyectoLicenciaCoaFacade.buscarProyecto(proyecto.getCodigo());
				
				proyectoRcoa.setEstadoActualizacionCertInterseccion(2);
				proyectoLicenciaCoaFacade.guardar(proyectoRcoa);
				
				responsableProyecto = proyectoRcoa.getUsuario();
				
				proyectoActualizacionCIFacade.guardarHistorialActualizacionCertificado(proyecto.getCodigo(), 2, null);
				
				break;

			default:
				break;
			}
			
			JsfUtil.addMessageInfo("La solicitud de actualización se realizó con éxito");
			
			if(responsableProyecto != null ) {
				String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
								"bodyNotificacionActualizacionCertificadoInterseccion", parametrosCorreo);
				
				Organizacion orga = organizacionFacade.buscarPorRuc(responsableProyecto.getNombre());
    			if(orga != null)
    				Email.sendEmail(orga, "Regularización Ambiental Nacional", notificacion, proyecto.getCodigo(), responsableProyecto, JsfUtil.getLoggedUser());
    			else
    				Email.sendEmail(responsableProyecto, "Regularización Ambiental Nacional", notificacion, proyecto.getCodigo(), JsfUtil.getLoggedUser());
			}
			
			JsfUtil.redirectTo("/prevencion/actualizacionCertInterseccion/listadoProyectosActualizacion.jsf");
			
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}

}
