package ec.gob.ambiente.prevencion.viabilidadtecnica.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.prevencion.viabilidadtecnica.bean.DiagnosticoFactibilidadAnexBean;
import ec.gob.ambiente.suia.domain.CoordenadsProyectosNoRegulados;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.domain.FaseViabilidadTecnica;
import ec.gob.ambiente.suia.domain.ProyectoAmbientalNoRegulado;
import ec.gob.ambiente.suia.domain.Region;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.DiagnosticoFiabilidadTecnicaFacade;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.EstudioViabilidadTecnicaDiagnosticoFacade;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.EstudioViabilidadTecnicalFacade;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.FaseViabilidadTecnicaDiagnosticoFacade;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.FaseViabilidadTecnicaFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ViewScoped
@ManagedBean
public class DiagnosticoFactibilidadAnexController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	private static final Logger LOG = Logger.getLogger(DiagnosticoFactibilidadAnexController.class);

	@EJB
	private FaseViabilidadTecnicaFacade faseViabilidadTecnicaFacade;

	@EJB
	private FaseViabilidadTecnicaDiagnosticoFacade faseViabilidadTecnicaDiagnosticoFacade;

	@EJB
	private EstudioViabilidadTecnicalFacade estudioViabilidadTecnicaFacade;

	@EJB
	private EstudioViabilidadTecnicaDiagnosticoFacade estudioViabilidadTecnicaDiagnosticoFacade;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	@EJB
	private DiagnosticoFiabilidadTecnicaFacade diagnosticoFiablidadFacade;

	@Getter
	@Setter
	private boolean fases;

	@Setter
	@Getter
	@ManagedProperty(value = "#{diagnosticoFactibilidadAnexBean}")
	private DiagnosticoFactibilidadAnexBean diagnosticoFactibilidadAnexBean;

	@PostConstruct
	public void init() {
		this.fases = false;
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		if (params.containsKey("fases")) {
			String value =params.get("fases");
			if(value.equals("1")){
				this.fases = true;
			}
		}

		if(fases)
			diagnosticoFactibilidadAnexBean.setFasesVianilidadTecnica(faseViabilidadTecnicaFacade
					.obtenerLista(FaseViabilidadTecnica.DIAGNOSTICO_FACTIBILIDAD));
		else{
			//System.out.println("Lista regiones cargadas");
			this.diagnosticoFactibilidadAnexBean.setListaRegiones(this.ubicacionGeograficaFacade.listaRegionesEcuador());
			this.diagnosticoFactibilidadAnexBean.setCantonSeleccionada(new UbicacionesGeografica());
			this.diagnosticoFactibilidadAnexBean.setProvinciaSeleccionada(new UbicacionesGeografica());
			this.diagnosticoFactibilidadAnexBean.setRegionSeleccionada(new Region());
			this.diagnosticoFactibilidadAnexBean.setListaUbicacionesGeograficas(new ArrayList<UbicacionesGeografica>());
			this.diagnosticoFactibilidadAnexBean.setListaProvincias(new ArrayList<UbicacionesGeografica>());
			this.diagnosticoFactibilidadAnexBean.setListaCantones(new ArrayList<UbicacionesGeografica>());
		}

	}

	public void calcularTotalPoblacion() {
		diagnosticoFactibilidadAnexBean.calcularTotalPoblacion();
		enviarFlujo();
	}

	public void enviarFlujo(){
		
//		ProyectoNoRegularizadoUbicacionGeografica proyectoNoRegularizadoUbicacionGeografica = new ProyectoNoRegularizadoUbicacionGeografica();
		ProyectoAmbientalNoRegulado proyectoAmbientalNoRegulado = new ProyectoAmbientalNoRegulado();
		
		proyectoAmbientalNoRegulado.setDescripcion(diagnosticoFactibilidadAnexBean.getDescripcionProyecto());
		proyectoAmbientalNoRegulado.setPoblacionRural(diagnosticoFactibilidadAnexBean.getRural());
		proyectoAmbientalNoRegulado.setPublacionUrbana(diagnosticoFactibilidadAnexBean.getUrbana());
		proyectoAmbientalNoRegulado.setTasaCrecimiento(diagnosticoFactibilidadAnexBean.getIndiceCrecimiento());
		diagnosticoFactibilidadAnexBean.cargarValorCoordenadas();
		List<CoordenadsProyectosNoRegulados> listaCoordenadasUTM = diagnosticoFactibilidadAnexBean.getListaCoordenadasUTM();
		List<UbicacionesGeografica> listaUbicaciones = diagnosticoFactibilidadAnexBean.getListaCantones();
		
		//Persistir objeto proyectoAmbientalNoRegulado
		diagnosticoFiablidadFacade.ingresarProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado, listaUbicaciones, listaCoordenadasUTM);
		
		
		
//		for (CoordenadsProyectosNoRegulados coor : diagnosticoFactibilidadAnexBean.getListaCoordenadasUTM()) {
//			coor.setProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado);
//			//Persistir objeto coor
//		};
		
		EstudioViabilidadTecnica estudioViabilidadTecnica = new EstudioViabilidadTecnica();
		estudioViabilidadTecnica.setProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado);
		estudioViabilidadTecnicaFacade.ingresarEstudioViabilidadTecnicaDiagnostico(estudioViabilidadTecnica);
		EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico = new EstudioViabilidadTecnicaDiagnostico();
		estudioViabilidadTecnicaDiagnostico.setEstudioViabilidadTecnica(estudioViabilidadTecnica);
		estudioViabilidadTecnicaDiagnosticoFacade.ingresarEstudioViabilidadTecnicaDiagnostico(estudioViabilidadTecnicaDiagnostico);
		
		
		//faseViabilidadTecnicaDiagnosticoFacade.ingresarFasesDiagnosticoViabiliad(obtenerFasesSeleccionadas(), estudioViabilidadTecnicaDiagnostico);
		
		faseViabilidadTecnicaDiagnosticoFacade.ingresarFasesDiagnosticoViabilidad(obtenerFasesSeleccionadas(), estudioViabilidadTecnicaDiagnostico);
		
		
	}

	public List<FaseViabilidadTecnica> obtenerFasesSeleccionadas() {
		diagnosticoFactibilidadAnexBean.getFasesSeleccionadas();
		List<FaseViabilidadTecnica> resultado = new ArrayList<FaseViabilidadTecnica>();
		for (int i = 0; i < diagnosticoFactibilidadAnexBean.getFasesSeleccionadas().length; i++) {
			resultado.add(diagnosticoFactibilidadAnexBean.getFasesSeleccionadas()[i]);
		}
		return resultado;
	}

	/**
	 * Inicializacion de variables y objetos
	 */
	public void inicializarUbicaciones() {
		this.diagnosticoFactibilidadAnexBean.inicializarUbicaciones();

		JsfUtil.addCallbackParam("agregarUbicacion");

	}

}
