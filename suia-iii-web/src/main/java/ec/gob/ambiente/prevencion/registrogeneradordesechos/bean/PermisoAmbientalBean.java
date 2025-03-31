package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Setter;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FaseGestionDesecho;
import ec.gob.ambiente.suia.domain.PrestadorServiciosDesechoPeligroso;
import ec.gob.ambiente.suia.domain.PuntoEliminacionPrestadorServicioDesechoPeligroso;
import ec.gob.ambiente.suia.domain.TipoEliminacionDesecho;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class PermisoAmbientalBean implements Serializable {

	private static final long serialVersionUID = 4181490917175819346L;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@EJB
	private UsuarioFacade usuarioFacade;

	@Setter
	private List<PrestadorServiciosDesechoPeligroso> prestadoresServiciosDesechosPeligrosos;

	@Setter
	private List<PuntoEliminacionPrestadorServicioDesechoPeligroso> puntoEliminacionPrestadorServicioLista;

	@Setter
	private String filter;

	public String getFilter() {
		return filter == null ? "" : filter;
	}

	public void reset() {
		filter = null;
		init();
	}

	public void resetSelection() {
		setFilter(null);
		setPrestadoresServiciosDesechosPeligrosos(null);
		setPuntoEliminacionPrestadorServicioLista(null);
	}

	public void init() {

		List<Integer> idFasesGestion = new ArrayList<Integer>();
		idFasesGestion.add(FaseGestionDesecho.FASE_DISPOSICION_FINAL);
		idFasesGestion.add(FaseGestionDesecho.FASE_ELIMINACION);

		String ruc = JsfUtil.getBean(LoginBean.class).getUsuario().getNombre();
		DesechoPeligroso desecho = JsfUtil.getBean(EliminacionDesechosInstalacionBean.class).getPuntoEliminacion()
				.getDesechoPeligroso();

		List<Integer> idCantonesPuntosRecuperacion = JsfUtil.getBean(PuntosRecuperacionBean.class)
				.getIdCantonesPuntosRecuperacion();

		TipoEliminacionDesecho tipoEliminacion = JsfUtil.getBean(TipoEliminacionDesechoBean.class)
				.getTipoEliminacionDesechoSeleccionada();

		List<PrestadorServiciosDesechoPeligroso> copiaSeleccionados = copiarSeleccionados();

		if (desecho != null && idCantonesPuntosRecuperacion != null && ruc != null
				&& !idCantonesPuntosRecuperacion.isEmpty()) {
			List<PrestadorServiciosDesechoPeligroso> prestadores = registroGeneradorDesechosFacade
					.getPrestadoresServiciosDesechosPorFaseDesechoRucTipoUbicacion(idFasesGestion, desecho.getId(),
							ruc, idCantonesPuntosRecuperacion,tipoEliminacion, getFilter());
			if (prestadores != null)
				setPrestadoresServiciosDesechosPeligrosos(prestadores);
		}

		if (!copiaSeleccionados.isEmpty())
			marcarComoSeleccionados(copiaSeleccionados);

	}

	public List<PrestadorServiciosDesechoPeligroso> getPrestadoresServiciosDesechosPeligrosos() {
		return prestadoresServiciosDesechosPeligrosos == null ? prestadoresServiciosDesechosPeligrosos = new ArrayList<PrestadorServiciosDesechoPeligroso>()
				: prestadoresServiciosDesechosPeligrosos;
	}

	public List<PrestadorServiciosDesechoPeligroso> getPrestadoresServiciosSeleccionados() {
		List<PrestadorServiciosDesechoPeligroso> result = new ArrayList<PrestadorServiciosDesechoPeligroso>();
		if (prestadoresServiciosDesechosPeligrosos != null) {
			for (PrestadorServiciosDesechoPeligroso prestadorServicio : prestadoresServiciosDesechosPeligrosos) {
				if (prestadorServicio.isSeleccionado())
					result.add(prestadorServicio);
			}
		}
		return result;
	}

	public void setPrestadoresServiciosSeleccionados(List<PrestadorServiciosDesechoPeligroso> prestadoresSeleccionados) {
		init();
		for (PrestadorServiciosDesechoPeligroso prestadorServicio : getPrestadoresServiciosDesechosPeligrosos()) {
			if (prestadoresSeleccionados.contains(prestadorServicio))
				prestadorServicio.setSeleccionado(true);
		}
	}

	public List<PrestadorServiciosDesechoPeligroso> copiarSeleccionados() {
		List<PrestadorServiciosDesechoPeligroso> copiaSeleccionados = new ArrayList<PrestadorServiciosDesechoPeligroso>();
		if (!getPrestadoresServiciosSeleccionados().isEmpty()
				&& getPrestadoresServiciosSeleccionados()
						.get(0)
						.getDesechoPeligroso()
						.equals(JsfUtil.getBean(EliminacionDesechosInstalacionBean.class).getPuntoEliminacion()
								.getDesechoPeligroso())) {
			copiaSeleccionados.addAll(getPrestadoresServiciosSeleccionados());
		}
		return copiaSeleccionados;
	}

	public void marcarComoSeleccionados(List<PrestadorServiciosDesechoPeligroso> seleccionados) {
		if (prestadoresServiciosDesechosPeligrosos != null) {
			for (PrestadorServiciosDesechoPeligroso prestadorServiciosDesechoPeligroso : prestadoresServiciosDesechosPeligrosos) {
				if (seleccionados.contains(prestadorServiciosDesechoPeligroso))
					prestadorServiciosDesechoPeligroso.setSeleccionado(true);
			}
		}
	}

	public void eliminarPrestadorSeleccionado(PrestadorServiciosDesechoPeligroso prestadorServicio) {
		eliminarPuntoEliminacionPrestadorServicio(prestadorServicio);
		prestadorServicio.setSeleccionado(false);
		JsfUtil.getBean(TipoEliminacionDesechoBean.class).setTipoEliminacionDesechoSeleccionada(null);
	}

	public void seleccionarPrestadorServicio(PrestadorServiciosDesechoPeligroso prestadorServicioSeleccionado) {
		if (prestadoresServiciosDesechosPeligrosos != null) {
			for (PrestadorServiciosDesechoPeligroso prestadorServicio : prestadoresServiciosDesechosPeligrosos) {
				if (prestadorServicio.equals(prestadorServicioSeleccionado))
					prestadorServicio.setSeleccionado(true);
			}
		}
	}

	public List<PuntoEliminacionPrestadorServicioDesechoPeligroso> getPuntoEliminacionPrestadorServicioLista() {
		return puntoEliminacionPrestadorServicioLista == null ? puntoEliminacionPrestadorServicioLista = new ArrayList<PuntoEliminacionPrestadorServicioDesechoPeligroso>()
				: puntoEliminacionPrestadorServicioLista;
	}

	public void updateDatosAsociadosPermiso() {

		if (JsfUtil.getBean(TipoEliminacionDesechoBean.class).getTipoEliminacionDesechoSeleccionada()==null && getPrestadoresServiciosSeleccionados().size() > 0) {
			JsfUtil.getBean(TipoEliminacionDesechoBean.class).setTipoEliminacionDesechoSeleccionada(
					getPrestadoresServiciosSeleccionados().get(0).getTipoEliminacionDesecho());
		}

		PuntoEliminacionPrestadorServicioDesechoPeligroso auxiliar = new PuntoEliminacionPrestadorServicioDesechoPeligroso();
		for (PuntoEliminacionPrestadorServicioDesechoPeligroso puntoEliminacionPrestadorServicioDesechoPeligroso : getPuntoEliminacionPrestadorServicioLista()) {
			if (!getPrestadoresServiciosSeleccionados().contains(
					puntoEliminacionPrestadorServicioDesechoPeligroso.getPrestadorServiciosDesechoPeligroso())) {
				auxiliar = puntoEliminacionPrestadorServicioDesechoPeligroso;
				break;
			}
		}
		getPuntoEliminacionPrestadorServicioLista().remove(auxiliar);

		boolean encontrado;
		for (PrestadorServiciosDesechoPeligroso prestadorServicio : getPrestadoresServiciosSeleccionados()) {
			encontrado = false;
			for (PuntoEliminacionPrestadorServicioDesechoPeligroso punto : getPuntoEliminacionPrestadorServicioLista()) {
				if (prestadorServicio.equals(punto.getPrestadorServiciosDesechoPeligroso())) {
					encontrado = true;
					break;
				}
			}
			if (!encontrado) {
				PuntoEliminacionPrestadorServicioDesechoPeligroso punto = new PuntoEliminacionPrestadorServicioDesechoPeligroso();
				punto.setPrestadorServiciosDesechoPeligroso(prestadorServicio);
				getPuntoEliminacionPrestadorServicioLista().add(punto);
			}
		}
	}

	public void eliminarPuntoEliminacionPrestadorServicio(PrestadorServiciosDesechoPeligroso prestadorServicio) {
		PuntoEliminacionPrestadorServicioDesechoPeligroso puntoAux = new PuntoEliminacionPrestadorServicioDesechoPeligroso();
		for (PuntoEliminacionPrestadorServicioDesechoPeligroso punto : getPuntoEliminacionPrestadorServicioLista()) {
			if (punto.getPrestadorServiciosDesechoPeligroso().equals(prestadorServicio)) {
				puntoAux = punto;
				break;
			}
		}
		getPuntoEliminacionPrestadorServicioLista().remove(puntoAux);
	}
}
