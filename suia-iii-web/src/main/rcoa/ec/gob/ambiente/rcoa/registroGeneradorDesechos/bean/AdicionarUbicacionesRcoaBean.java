package ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.TipoUbicacion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AdicionarUbicacionesRcoaBean implements Serializable{

	private static final long serialVersionUID = -2280344496977812691L;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@Setter
	@Getter
	private List<UbicacionesGeografica> provincias;

	@Setter
	@Getter
	private List<UbicacionesGeografica> cantones;

	@Setter
	@Getter
	private List<UbicacionesGeografica> parroquias;
	
	@Setter
	@Getter
	private List<UbicacionesGeografica> listParroquias;
	
	@Setter
	@Getter
	private List<UbicacionesGeografica> listParroquiasGuardar;

	@Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionesSeleccionadas;

	@Setter
	@Getter
	private UbicacionesGeografica provincia;

	@Setter
	@Getter
	private UbicacionesGeografica canton;

	@Setter
	@Getter
	private UbicacionesGeografica parroquia;

	@Getter
	protected String tablaUbicaciones;

	@Getter
	protected String dialogWidgetVar;

	@Getter
	protected String panelUbicacion;

	@Getter
	protected boolean mostrarParroquias;
	
	@Setter
	@Getter
	private HashMap<String, UbicacionesGeografica> parroquiaSeleccionadas;	

	@PostConstruct
	public void init() {
		setProvincias(ubicacionGeograficaFacade.getProvincias());
		ubicacionesSeleccionadas = new ArrayList<UbicacionesGeografica>();
		listParroquiasGuardar= new ArrayList<UbicacionesGeografica>();
		parroquiaSeleccionadas= new HashMap<>();
		listParroquias= new ArrayList<UbicacionesGeografica>();

		tablaUbicaciones = "tbl_ubicaciones";
		dialogWidgetVar = "adicionarUbicaciones";
		panelUbicacion = "panelUbicacion";
		
		mostrarParroquias = true;
	}

	public void resetSelections() {
		provincia = null;
		canton = null;
		parroquia = null;
		ubicacionesSeleccionadas = new ArrayList<UbicacionesGeografica>();
	}

	public UbicacionesGeografica getUbicacionSeleccionada() {
		if (parroquia != null)
			return parroquia;
		if (canton != null)
			return canton;
		return provincia;
	}

	/**
	 * @param ubicacionesGeografica
	 * @param nivel
	 *            , 3 = Parroquia, 2 = Cantón
	 */
	
	public void setUbicacionSeleccionada(UbicacionesGeografica ubicacionesGeografica, int nivel) {
		if (nivel == 3) {
			parroquia = ubicacionesGeografica;
			canton = ubicacionesGeografica.getUbicacionesGeografica();
			provincia = ubicacionesGeografica.getUbicacionesGeografica().getUbicacionesGeografica();

			mostrarParroquias = true;
		} else if (nivel == 2) {
			canton = ubicacionesGeografica;
			provincia = ubicacionesGeografica.getUbicacionesGeografica();
			cargarCantones();
			cargarParroquias();//se aumento
			mostrarParroquias = false;
		}
	}

	public void cargarCantones() {
		setCantones(new ArrayList<UbicacionesGeografica>());
		setParroquias(new ArrayList<UbicacionesGeografica>());
		if (getProvincia() != null)
			setCantones(ubicacionGeograficaFacade.getCantonesParroquia(getProvincia()));
		else
			setCantones(new ArrayList<UbicacionesGeografica>());
	}

	public void cargarParroquias() {
		setParroquias(new ArrayList<UbicacionesGeografica>());
		if (getCanton() != null)
			setParroquias(ubicacionGeograficaFacade.getCantonesParroquia(getCanton()));
		else
			setParroquias(new ArrayList<UbicacionesGeografica>());
	}
	
	public List<UbicacionesGeografica> cargarParroquiasPorPadre(UbicacionesGeografica ubicacionesGeografica) {
		listParroquias = new ArrayList<UbicacionesGeografica>();
		if (ubicacionesGeografica != null){
			listParroquias=(ubicacionGeograficaFacade.getUbicacionPadre(ubicacionesGeografica));
		}
		return listParroquias;
	}
	
	public void adicionarParroquias(String index){
		if(!parroquiaSeleccionadas.containsValue(parroquia)){
		parroquiaSeleccionadas.put(index, parroquia);
		listParroquiasGuardar.clear();
		listParroquiasGuardar.addAll(parroquiaSeleccionadas.values());
		parroquia=null;
		}else {
			JsfUtil.addMessageError("Esta ubicación ya está seleccionada.");
		}
	}
	
	public void agregarUbicacion() {
		boolean required = false;
		if (provincia == null) {
			JsfUtil.addMessageErrorForComponent("provincia_" + panelUbicacion, JsfUtil.getMessageFromBundle(null,
					"javax.faces.component.UIInput.REQUIRED", "Provincia"));
			required = true;
		}
		if (canton == null) {
			JsfUtil.addMessageErrorForComponent("canton_" + panelUbicacion, JsfUtil.getMessageFromBundle(null,
					"javax.faces.component.UIInput.REQUIRED", "Cantón"));
			required = true;
		}
		if (isMostrarParroquias() && parroquia == null) {
			JsfUtil.addMessageErrorForComponent("parroquia_" + panelUbicacion, JsfUtil.getMessageFromBundle(null,
					"javax.faces.component.UIInput.REQUIRED", "Parroquia"));
			required = true;
		}
		if (required)
			return;

		UbicacionesGeografica ubicacionesGeografica = getUbicacionSeleccionada();
		if (getUbicacionesSeleccionadas()!=null) {
				getUbicacionesSeleccionadas().add(ubicacionesGeografica);
				JsfUtil.addCallbackParam("addLocation");
			}
		else if (!getUbicacionesSeleccionadas().contains(ubicacionesGeografica)) {
			getUbicacionesSeleccionadas().add(ubicacionesGeografica);
			setParroquia(null);
			setCanton(null);
			setProvincia(null);
			JsfUtil.addCallbackParam("addLocation");
		} 
		else
			JsfUtil.addMessageError("Esta ubicación ya está seleccionada.");
	}

	public void agregarUbicacionAndClear() {
		agregarUbicacion();
		resetSelections();
	}

	public void quitarUbicacion(UbicacionesGeografica ubicacionesGeografica) {
		getUbicacionesSeleccionadas().remove(ubicacionesGeografica);
	}

	public void cargarProvinciasSegunTipoUbicacion(Integer idUbicacion) {
		if (idUbicacion.equals(TipoUbicacion.AGUAS_TERRITORIALES)) {
			List<Integer> aguasTerritoriales = Arrays.asList(UbicacionesGeografica.AGUAS_TERRITORIALES);
			List<UbicacionesGeografica> validas = new ArrayList<UbicacionesGeografica>();
			for (int i = 0; i < getUbicacionesSeleccionadas().size(); i++) {
				if (aguasTerritoriales.contains(getUbicacionesSeleccionadas().get(i).getUbicacionesGeografica()
						.getUbicacionesGeografica().getId()))
					validas.add(getUbicacionesSeleccionadas().get(i));
			}
			setUbicacionesSeleccionadas(validas);
			setProvincias(ubicacionGeograficaFacade.getProvinciasAguasTerritoriales());
			setCantones(null);
			setParroquias(validas);
		} else {
			setProvincias(ubicacionGeograficaFacade.getProvincias());}

	}
	
	public List<UbicacionesGeografica> ubicacionConcesion(List<UbicacionesGeografica> ubi)
	{
		ubicacionesSeleccionadas=ubi;
		return ubi;
	}
}
