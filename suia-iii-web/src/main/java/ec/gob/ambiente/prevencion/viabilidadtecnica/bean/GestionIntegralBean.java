package ec.gob.ambiente.prevencion.viabilidadtecnica.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.comun.bean.CargarCoordenadasBean;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.CoordenadsProyectosNoRegulados;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.domain.FaseViabilidadTecnica;
import ec.gob.ambiente.suia.domain.ProyectoAmbientalNoRegulado;
import ec.gob.ambiente.suia.domain.ProyectoNoRegularizadoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.Region;
import ec.gob.ambiente.suia.domain.ServiciosBasicosProyectosNoRegulados;
import ec.gob.ambiente.suia.domain.ServiciosPublicosProyectosNoRegulados;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;

import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.EstudioViabilidadTecnicaDiagnosticoFacade;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.EstudioViabilidadTecnicalFacade;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.FaseViabilidadTecnicaDiagnosticoFacade;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.FaseViabilidadTecnicaFacade;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.GestionIntegralFacade;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.ProyectoAmbientalNoReguladoFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;



@ManagedBean
@ViewScoped
public class GestionIntegralBean implements Serializable{
	
	private static final long serialVersionUID = -985232953639127645L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GestionIntegralBean.class);

	
	 @EJB
     private ProcesoFacade procesoFacade;
	 
	 @EJB
	 private GestionIntegralFacade gestionIntegralFacade;
	 
	 @EJB
	 private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	 
	 @EJB
	 private FaseViabilidadTecnicaFacade faseViabilidadTenicaFacade;
	 
	 @EJB
	 private EstudioViabilidadTecnicalFacade estudioViabilidadTecnicaFacade;
	 
	 @EJB
	 private EstudioViabilidadTecnicaDiagnosticoFacade estudioViabilidadTecnicaDiagnosticoFacade;
	 
	 @EJB
	 private FaseViabilidadTecnicaDiagnosticoFacade faseViabilidadTecnicaDiagnosticoFacade;
	 
	 @EJB
	 private ProyectoAmbientalNoReguladoFacade proyectoAmbientalNoReguladoFacade;
	 
	 @Setter
	 @Getter
	 private List<FaseViabilidadTecnica> listFaseViabilidadTecnica;
	 
	 @Getter
     @Setter
	 private List<Region> listaRegiones;
	 
	 
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
	 private UbicacionesGeografica cantonSeleccionado;
	
	 @Getter
	 @Setter
	 private List<ProyectoNoRegularizadoUbicacionGeografica> listaProyectoNoRegularizadoUbicacionGeografica;
	 
	 @Getter
	 @Setter
	 private List<ServiciosPublicosProyectosNoRegulados> listaServiciosPublicosNoRegulados;
	 
	 @Getter
	 @Setter
	 public ServiciosPublicosProyectosNoRegulados serviciosPublicosProyectosNoRegulados;
	 
	 @Getter
	 @Setter
	 public ServiciosBasicosProyectosNoRegulados serviciosBasicosProyectosNoRegulados;
	 
	 @Getter
	 @Setter
	 private List<FaseViabilidadTecnica> fasesSeleccionadas;
	 
	 @Getter
	 @Setter
	 private Double urbana = 0.0 ;
		
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
	 private String viasUrbanas="";
	 
	 
	 @Getter
     @Setter
	 private ProyectoAmbientalNoRegulado proyectoAmbientalNoRegulado;
	 
	 @Getter
     @Setter
	 private ProyectoNoRegularizadoUbicacionGeografica proyectoNoRegularizadoUbicacionGeografica;
	 
	 @Getter
     @Setter
	 private List<ProyectoNoRegularizadoUbicacionGeografica> listProyectoNoRegularizadoUbicacionGeografica;
	 
	 @Getter
	 @Setter
	 private List<CoordenadsProyectosNoRegulados> listaCoordenadasUTM;
	 
	 
	 @PostConstruct
	 public void init(){
		 LOG.info("Inicia Bean GESTION INTEGRAL");
		 serviciosPublicosProyectosNoRegulados = new ServiciosPublicosProyectosNoRegulados();
		 serviciosBasicosProyectosNoRegulados = new ServiciosBasicosProyectosNoRegulados();
		 listaProyectoNoRegularizadoUbicacionGeografica = new ArrayList<ProyectoNoRegularizadoUbicacionGeografica>();
		 regionSeleccionada = new Region();
		 provinciaSeleccionada = new UbicacionesGeografica();
		 cantonSeleccionado = new UbicacionesGeografica();
		 listaServiciosPublicosNoRegulados =new ArrayList<ServiciosPublicosProyectosNoRegulados>();
		 listaRegiones = ubicacionGeograficaFacade.listaRegionesEcuador();
		 listaProvincias = new ArrayList<>();
		 listaCantones = new ArrayList<>();
		 proyectoAmbientalNoRegulado = new ProyectoAmbientalNoRegulado();
		 listFaseViabilidadTecnica = new ArrayList<FaseViabilidadTecnica>();
		 fasesSeleccionadas = new ArrayList<FaseViabilidadTecnica>();
		 cargarFasesporGestionIntegral();
	 }
	 
	 public void cargarFasesporGestionIntegral(){
		 listFaseViabilidadTecnica = faseViabilidadTenicaFacade.obtenerLista("Gestión Integral");	 
	 }
	 
	 
	 
	 public void cargarProvincias(){

			setListaProvincias(new ArrayList<UbicacionesGeografica>());
			if (getRegionSeleccionada() != null)
			{	
				listaProvincias = gestionIntegralFacade.obtenerProvinciasPorRegion(getRegionSeleccionada().getId());
				
			}else
				listaProvincias = new ArrayList<UbicacionesGeografica>();
	 }
	 
	 public void cargarCantones() {
			setListaCantones(new ArrayList<UbicacionesGeografica>());
			if (getProvinciaSeleccionada() != null)
				setListaCantones(ubicacionGeograficaFacade.getCantonesParroquia(getProvinciaSeleccionada()));
			else
				setListaCantones(new ArrayList<UbicacionesGeografica>());
		}
			 
	 public void iniciarObjetosUbicacion(){
		 regionSeleccionada = new Region();
		 provinciaSeleccionada = new UbicacionesGeografica();
		 cantonSeleccionado = new UbicacionesGeografica();
	 }
	 
	 
	 public void cargarListaUbicacionesGeo(){
		
		if (proyectoAmbientalNoRegulado.getId()==null) {
			 
			 proyectoAmbientalNoRegulado = proyectoAmbientalNoReguladoFacade.ingresarProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado);
		}
		
		if (proyectoAmbientalNoRegulado.getId()!=null) {
		
			 ProyectoNoRegularizadoUbicacionGeografica pnrug = new ProyectoNoRegularizadoUbicacionGeografica(); 
			 pnrug.setUbicacionesGeografica(cantonSeleccionado);
			 pnrug.setProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado);
			 proyectoAmbientalNoReguladoFacade.ingresarProyectoNoRegularizadoUbicacionGeografica(pnrug);
			 this.listaProyectoNoRegularizadoUbicacionGeografica =  proyectoAmbientalNoReguladoFacade.listarUbicacionesGeograficasPorProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado.getId());
		}
		

	 }
	 
	 public void cargarListaServiciosPublicos(){
		
		 if (proyectoAmbientalNoRegulado.getId()==null) {
			  proyectoAmbientalNoRegulado = proyectoAmbientalNoReguladoFacade.ingresarProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado);
		 }
		 if (proyectoAmbientalNoRegulado.getId()!=null) {
			 this.serviciosPublicosProyectosNoRegulados.setProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado); 
			 serviciosPublicosProyectosNoRegulados = gestionIntegralFacade.ingresarServiciosPublicosProyectosNoRegulados(this.serviciosPublicosProyectosNoRegulados);
			 this.listaServiciosPublicosNoRegulados = gestionIntegralFacade.listarServiciosPublicosProyectosNoRegulados(proyectoAmbientalNoRegulado.getId());
		 }
		 
		 serviciosPublicosProyectosNoRegulados = new ServiciosPublicosProyectosNoRegulados();
	 }
	 	 
	 public void eliminarProyectoNoRegularizadoUbicacionGeografica(ProyectoNoRegularizadoUbicacionGeografica pnrug) 
	 {	
		 proyectoAmbientalNoReguladoFacade.eliminarProyectoAmbientalNoRegulado(pnrug);
		 this.listaProyectoNoRegularizadoUbicacionGeografica =  proyectoAmbientalNoReguladoFacade.listarUbicacionesGeograficasPorProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado.getId());
	 }
	 
	 public void eliminarServiciosProyectosNoRegularizado(ServiciosPublicosProyectosNoRegulados sppng){
		 gestionIntegralFacade.eliminarServiciosProyectoNoRegulado(sppng);
		 this.listaServiciosPublicosNoRegulados = gestionIntegralFacade.listarServiciosPublicosProyectosNoRegulados(proyectoAmbientalNoRegulado.getId());
	 }
	 
	 public void guardarDatos(){
				 
		proyectoAmbientalNoRegulado.setPoblacionRural(rural);
	    proyectoAmbientalNoRegulado.setPublacionUrbana(urbana);
	    proyectoAmbientalNoRegulado.setTasaCrecimiento(indiceCrecimiento);
		proyectoAmbientalNoRegulado = proyectoAmbientalNoReguladoFacade.ingresarProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado); 
		
	    serviciosBasicosProyectosNoRegulados.setProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado);
	    serviciosBasicosProyectosNoRegulados = gestionIntegralFacade.ingresarServiciosBasicosProyectosNoRegulados(serviciosBasicosProyectosNoRegulados);
		
	    
	    guardarFasesSeleccionadas();
		
	    cargarValorCoordenadas();
	 	gestionIntegralFacade.ingresarCoordenadasProyectosNoRegulados(listaCoordenadasUTM, proyectoAmbientalNoRegulado);
	    System.out.println("...Despues de ingresar...");
	 }
	 
	 
	 
	 public void guardarFasesSeleccionadas()
	 {

		 	if(fasesSeleccionadas.size() > 0){
		 		EstudioViabilidadTecnica estudioViabilidadTecnica = new EstudioViabilidadTecnica();
		 		estudioViabilidadTecnicaFacade.ingresarEstudioViabilidadTecnicaDiagnostico(estudioViabilidadTecnica);
			
		 		EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico = new EstudioViabilidadTecnicaDiagnostico();
		 		estudioViabilidadTecnicaDiagnostico.setViasUrbanas(this.viasUrbanas);
		 		estudioViabilidadTecnicaDiagnostico.setEstudioViabilidadTecnica(estudioViabilidadTecnica);
		 		estudioViabilidadTecnicaDiagnosticoFacade.ingresarEstudioViabilidadTecnicaDiagnostico(estudioViabilidadTecnicaDiagnostico);
			
			
		 		faseViabilidadTecnicaDiagnosticoFacade.ingresarFasesDiagnosticoViabilidad(fasesSeleccionadas, estudioViabilidadTecnicaDiagnostico);
		 	 }
			
	 }
		
	 
		public void calcularTotalPoblacion(){
			totalPoblacion = urbana + rural + indiceCrecimiento;
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
		
			
		
		
	 
	 
	 

}
