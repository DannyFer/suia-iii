package ec.gob.ambiente.prevencion.viabilidadtecnica.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.comun.bean.CargarCoordenadasBean;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.CoordenadsProyectosNoRegulados;
import ec.gob.ambiente.suia.domain.FaseViabilidadTecnica;
import ec.gob.ambiente.suia.domain.Region;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.GestionIntegralFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

import org.primefaces.context.RequestContext;

@ViewScoped
@ManagedBean
public class DiagnosticoFactibilidadAnexBean implements Serializable {

	/**
	 * 
	 */   
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private List<CoordenadsProyectosNoRegulados> listaCoordenadasUTM;

	private static final Logger LOG = Logger.getLogger(DiagnosticoFactibilidadAnexBean.class);

	@Getter
	@Setter
	private List<FaseViabilidadTecnica> fasesVianilidadTecnica;

	@Getter
	@Setter
	private FaseViabilidadTecnica[] fasesSeleccionadas;

	@Getter
	@Setter
	private String descripcionProyecto;

	@Getter
	@Setter
	private Double urbana = 0.0;

	@Getter
	@Setter
	private Double rural = 0.0;

	@Getter
	@Setter
	private Double indiceCrecimiento = 0.0;

	@Getter
	@Setter
	private Double totalPoblacion;

	@Getter
	@Setter
	private String caracteristicasFisicas;

	@Getter
	@Setter
	private List<UbicacionesGeografica> listaProvincias;

	@Getter
	@Setter
	private List<UbicacionesGeografica> listaCantones;

	@Getter
	@Setter
	private Region regionSeleccionada;

	@Getter
	@Setter
	private UbicacionesGeografica provinciaSeleccionada;

	@Getter
	@Setter
	private UbicacionesGeografica cantonSeleccionada;

	@Getter
	@Setter
	private UbicacionesGeografica parroquiaSeleccionada;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private GestionIntegralFacade gestionIntegalFacade;

	@Getter
	@Setter
	private List<Region> listaRegiones;

	@Getter
	@Setter
	private List<UbicacionesGeografica> listaUbicacionesGeograficas;

	public void registrarUbicacion() {

		if (!existeUbicacion()){
			this.listaUbicacionesGeograficas.add(this.cantonSeleccionada);

			JsfUtil.addCallbackParam("agregarUbicacion");
			RequestContext.getCurrentInstance().execute(
					"PF('dlgUbicacion').hide();");
		}
		else{
			JsfUtil.addMessageError("Ya existe la ubicación seleccionada. Por favor, seleccione una ubicacion diferente.");
		}

	}


	private boolean existeUbicacion() {

		boolean exists = false;
		int size = this.listaUbicacionesGeograficas.size();
		int pos = 0;
		while (!exists && pos<size){

			if(this.listaUbicacionesGeograficas.get(pos).getNombre().equalsIgnoreCase(this.cantonSeleccionada.getNombre())){
				exists = true;
			}
			else{
				pos++;
			}

		}
		return exists;
	}
	public void cargarProvincias() {

		setListaProvincias(new ArrayList<UbicacionesGeografica>());

		setListaCantones(new ArrayList<UbicacionesGeografica>());
		if (getRegionSeleccionada() != null)
			setListaProvincias(gestionIntegalFacade.obtenerProvinciasPorRegion(getRegionSeleccionada().getId()));
		else
			setListaProvincias(new ArrayList<UbicacionesGeografica>());
	}

	public void cargarCantones() {
		setListaCantones(new ArrayList<UbicacionesGeografica>());
		if (getProvinciaSeleccionada() != null)
			setListaCantones(ubicacionGeograficaFacade.getCantonesParroquia(getProvinciaSeleccionada()));
		else
			setListaCantones(new ArrayList<UbicacionesGeografica>());
	}

	public void cargarValorCoordenadas() {
		listaCoordenadasUTM = new ArrayList<CoordenadsProyectosNoRegulados>();
		Iterator<CoordinatesWrapper> coords = JsfUtil.getBean(CargarCoordenadasBean.class).getCoordinatesWrappers()
				.iterator();
		while (coords.hasNext()) 
		{
			CoordinatesWrapper coordinatesWrapper = coords.next();
			coordinatesWrapper.getTipoForma();
			coordinatesWrapper.getCoordenadas();

			for (Coordenada cordenada : coordinatesWrapper.getCoordenadas()) {
				CoordenadsProyectosNoRegulados cpn = new CoordenadsProyectosNoRegulados();
				cpn.setDescripcion(cordenada.getDescripcion());
				cpn.setOrden(cordenada.getOrden());
				cpn.setValorX(Double.valueOf(cordenada.getX()));
				cpn.setValorY(Double.valueOf(cordenada.getY()));
				listaCoordenadasUTM.add(cpn);
			}
		}
	}

	public void eliminarUbicacion(UbicacionesGeografica ubicacion) {
		listaUbicacionesGeograficas.remove(ubicacion);

	}

	public void calcularTotalPoblacion() {
		totalPoblacion = urbana + rural + indiceCrecimiento;
	}

	public void inicializarUbicaciones(){
		this.regionSeleccionada = new Region();
		this.provinciaSeleccionada = new UbicacionesGeografica();
		this.cantonSeleccionada = new UbicacionesGeografica();
		this.parroquiaSeleccionada = new UbicacionesGeografica();
	}

}
