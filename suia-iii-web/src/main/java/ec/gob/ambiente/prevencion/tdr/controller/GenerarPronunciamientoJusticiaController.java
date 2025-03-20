package ec.gob.ambiente.prevencion.tdr.controller;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.tdr.bean.GenerarPronunciamientoJusticiaBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class GenerarPronunciamientoJusticiaController {

	private static final Logger LOGGER = Logger
			.getLogger(GenerarPronunciamientoJusticiaController.class);

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{generarPronunciamientoJusticiaBean}")
	private GenerarPronunciamientoJusticiaBean generarPronunciamientoJusticiaBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private SecuenciasFacade secuenciasFacade;

	@EJB
	private AreaFacade areaFacade;

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoFacade;

	@Setter
	@Getter
	private ProyectoLicenciamientoAmbiental proyectoActivo;

	// Genera el documento de Oficio del Pronunciamiento para el ministerio de
	// justicia.
	public void generarDocumento() {

		// Buscando el proyecto activo
		@SuppressWarnings("unused")
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		// inicialización de las variables para cargar pantalla
		try {
			// seleccionar el proyecto del proceso activo
			Map<String, Object> variables = procesoFacade
					.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
							.getProcessInstanceId());
			Integer idProyecto = Integer.parseInt((String) variables
					.get(Constantes.ID_PROYECTO));

			if (idProyecto != null) {
                try {
                    proyectoActivo = proyectoFacade
                            .cargarProyectoFullPorId(idProyecto);
                }
                catch (Exception e){
                    LOGGER.error("Error cargando el proyecto", e);
                    JsfUtil.addMessageError("Error cargando los datos del proyecto.");
                }
				/*
				 * 
				 * Parametros: 0 Secuencial del documento que se genera 1:
				 * Ciudad, 2 Fecha 3, Tratamiento – Título Académico 4, Nombres
				 * Apellidos 5, Cargo 6, Institución/Empresa 7, Lugar de Entrega
				 * 8, Nombre del proyecto 9, Nombre de la provincia 10, Nombre
				 * del Proponente 11, Numero de documento del proyecto. 12,
				 * Lugar donde interseca el proyecto 13, Dia, de emision del
				 * certificado 14, Mes, de emision del certificado 15, Ano, de
				 * emision del certificado
				 */

				String[] parametrosO = new String[19];
				Integer i = 0;

				if (generarPronunciamientoJusticiaBean.getCodigoOficio()
						.isEmpty()) {
					try {
						generarPronunciamientoJusticiaBean
								.setCodigoOficio(Long.toString(secuenciasFacade
										.getNextValueDedicateSequence("tdr_eia_pronunciamiento_mj")));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				parametrosO[i++] = generarPronunciamientoJusticiaBean
						.getCodigoOficio();

				parametrosO[i++] = "Quito";

 

				parametrosO[i++] = DocumentoPDFPlantillaHtml.fechaActual();
				parametrosO[i++] = generarPronunciamientoJusticiaBean
						.getAsunto();
				Usuario usuario = proyectoActivo.getUsuario();
			 
				try {
					usuario = usuarioFacade.buscarUsuarioPorIdFull(usuario
							.getId());
				} catch (ServiceException e) {
				}
				if (usuario.getPersona().getTipoTratos() != null) {
					parametrosO[i++] = usuario.getPersona().getTipoTratos()
							.getNombre();
				} else {
					parametrosO[i++] = "Sr.|Sra.|Sta.";
				}
				// "Sr.|Sra.|Sta.";

				if (proyectoActivo != null) {
					parametrosO[i++] = proyectoActivo.getUsuario().getPersona()
							.getNombre();
					parametrosO[i++] = proyectoActivo.getUsuario().getPersona()
							.getTitulo();
					parametrosO[i++] = "Organizacion"; // proyectoActivo.getUsuario().getPersona()
					// .getOrganizaciones().get(0).getNombre();
					parametrosO[i++] = "Lugar de Entrega.";
					parametrosO[i++] = proyectoActivo.getNombre();

					if (proyectoActivo.getProyectoUbicacionesGeograficas().get(
							0) != null) {
						// "UBICACION";
						parametrosO[i++] = proyectoActivo
								.getProyectoUbicacionesGeograficas().get(0)
								.getUbicacionesGeografica().getNombre();
					} else {
						i++;
					}
					parametrosO[i++] = proyectoActivo.getUsuario().getPersona()
							.getNombre();
					parametrosO[i++] = proyectoActivo.getCodigo();
					parametrosO[i++] = "Interseccion del Proceso";

					// dia , mes , año de emision del certificado, por ahora
					// esta
					// los datos del proyecto
					Calendar calendario = new GregorianCalendar();
					calendario.setTime(proyectoActivo.getFechaRegistro());

					parametrosO[i++] = Integer.toString(calendario
							.get(Calendar.DAY_OF_MONTH));
					parametrosO[i++] = Integer.toString(calendario
							.get(Calendar.MONTH));
					parametrosO[i++] = Integer.toString(calendario
							.get(Calendar.YEAR));
					Usuario director = new Usuario(); // areaFacade.getDirectorPlantaCentral();
					// try {
					// director = usuarioFacade
					// .buscarUsuarioPorIdFull(director.getId());
					// } catch (ServiceException e) {
					// // 
					// e.printStackTrace();
					// }
					parametrosO[i++] = director.getPersona().getNombre() != null ? director
							.getPersona().getNombre() : "";
					parametrosO[i++] = director.getPersona().getPosicion() != null ? director
							.getPersona().getPosicion() : "";// "Especialista de SUIA";
					generarPronunciamientoJusticiaBean.setCompletado(true);
					DocumentoPDFPlantillaHtml.descargarArchivo("oficio",
							parametrosO, "oficio_ministerio_justicia", false,
							"Oficio");
				}
			}
		} catch (JbpmException e) {

			JsfUtil.addMessageError("Error al realizar la operación.");
		}

	}

	public String enviarDatos() {
		if (generarPronunciamientoJusticiaBean.getCompletado()) {
			Map<String, Object> params = new ConcurrentHashMap<String, Object>();

			try {
				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
						.getTarea().getProcessInstanceId(), params);
				Map<String, Object> data = new ConcurrentHashMap<String, Object>();
				taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
						bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data,
						loginBean.getPassword(),
						Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

				JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
				return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				LOGGER.error(e);
				JsfUtil.addMessageError("Error al realizar la operación.");
			}
			return "";
		} else {

			JsfUtil.addMessageError("Debe descargar el pronunciamiento antes de completar la tarea.");
			return "";
		}
	}
}
