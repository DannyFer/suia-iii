package ec.gob.ambiente.prevencion.registroautorizacionesadministrativas.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.SolicitudAutorizacionesAdministrativas;
import ec.gob.ambiente.suia.domain.SolicitudAutorizacionesAdministrativasCoordenada;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.solicitudautorizacionesadministrativas.facade.SolicitudAutorizacionesAdministrativasFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class IngresarSolicitudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -781018151141924366L;

	@Setter
	@Getter
	private Usuario usuarioActivo;

	@Setter
	@Getter
	private String categoria;

	@Setter
	@Getter
	private String registro;

	@Setter
	@Getter
	private String numeroResolucion;

	@Setter
	@Getter
	private Date fechaEmision;

	@Setter
	@Getter
	private UploadedFile licencia;

	@Setter
	@Getter
	private List<Usuario> usuarios;

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoFacade;

	@Setter
	@Getter
	private ProyectoCustom proyectoActivo;

	@Setter
	@Getter
	private List<ProyectoCustom> proyectos;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@Setter
	@Getter
	private List<UbicacionesGeografica> provincias;
	@Setter
	@Getter
	private UbicacionesGeografica provinciaActiva;

	@Setter
	@Getter
	private List<UbicacionesGeografica> cantones;

	@Setter
	@Getter
	private UbicacionesGeografica[] cantonesActivos;

	@EJB
	private SolicitudAutorizacionesAdministrativasFacade solicitudAutorizacionesAdministrativasFacade;

	@Getter
	@Setter
	private SolicitudAutorizacionesAdministrativas solicitud;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@PostConstruct
	public void init() {
		usuarios = usuarioFacade.buscarUsuarioPorRol("admin");
		registro = "l";
		proyectos = proyectoFacade.listarProyectosLicenciamientoAmbientalFinalizados();
		proyectoActivo = new ProyectoCustom();
		if (!proyectos.isEmpty()) {
			proyectoActivo = proyectos.get(0);

		}

		provincias = ubicacionGeograficaFacade.getProvincias();
		cantones = new ArrayList<UbicacionesGeografica>();
		if (!provincias.isEmpty()) {
			provinciaActiva = provincias.get(0);
			actualizarProvinciaActiva();
		}

		solicitud = solicitudAutorizacionesAdministrativasFacade
				.buscarSolicitudPorIdProceso(bandejaTareasBean.getTarea()
						.getProcessInstanceId());
		if (solicitud != null) {
			//proyectoActivo = solicitud.getProyecto();

			usuarioActivo = solicitud.getPromotor();
			registro = solicitud.getRegistro();
			categoria = solicitud.getCategoria();
			numeroResolucion = solicitud.getNumeroResolucion();
			fechaEmision = solicitud.getFechaEmision();
			cantones = new ArrayList<UbicacionesGeografica>();
			List<SolicitudAutorizacionesAdministrativasCoordenada> listaCantones = solicitud
					.getCantones();
			cantonesActivos = new UbicacionesGeografica[listaCantones.size()];
			int i = 0;

			for (SolicitudAutorizacionesAdministrativasCoordenada solicitudAutorizacionesAdministrativasCoordenada : listaCantones) {

				cantones.add(solicitudAutorizacionesAdministrativasCoordenada
						.getUbicacionesGeografica());

				cantonesActivos[i] = solicitudAutorizacionesAdministrativasCoordenada
						.getUbicacionesGeografica();
				i++;

				provinciaActiva = solicitudAutorizacionesAdministrativasCoordenada
						.getUbicacionesGeografica().getUbicacionesGeografica();
			}

			actualizarProvinciaActiva();
		}

	}

	public void cambiarUsuarioActivo() {
	}

	public void actualizarProvinciaActiva() {
		if (provinciaActiva != null) {
			cantones = ubicacionGeograficaFacade
					.getUbicacionPorPadre(provinciaActiva);
		}

	}

	public void handleFileUpload(FileUploadEvent event) {
		JsfUtil.addMessageInfo(event.getFile().getFileName());
		// System.out.println("------------------------------------");
		this.licencia = event.getFile();

	}

}
