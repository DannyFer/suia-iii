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
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoBioticoFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneralBiotico;
import ec.gob.ambiente.suia.domain.ComponenteBioticoSD;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.ComponenteBioticoSDFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ComponenteBioticoSDController implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private CatalogoBioticoFacade catalogoBioticoFacade;
	
	@EJB
	private ComponenteBioticoSDFacade componenteBioticoFacade;
	
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineraFacade;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;
	
	@Getter
	@Setter
	private List<CatalogoGeneralBiotico> coberturaVegetalList, pisosZooGeograficosList, componenteBioticoNativoList, componenteBioticoIntroducidoList,
											aspectosEcologicosList, areasSensiblesList, coberturaVegetalPadreList, coberturaVegetalEliminadosList;
	
	private Map<String, List<CatalogoGeneralBiotico>> listaGeneralBiotico;
	
	private List<ComponenteBioticoSD> listaBioticoCoberturaVegetal, listaBioticoPisosZooGeograficos, listaBioticoNativo, listaBioticoIntroducido, 
										listaBioticoAspectoEcologico, listaBioticoAreasSensibles;
	
	@Getter
	@Setter
	private Integer perforacionExplorativa;
	
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	
	@Getter
	@Setter
	private String mensajeBiotico = "Caracterización inicial de las condiciones presentes en el área de estudio. Caracterización inicial de las condiciones de la biota (Flora y Fauna) "
			+ "con especial referencia a hábitats y especies de alto valor ecológico y listadas en las listas Rojas de la IUCN, listas rojas nacionales, apéndices CITES";
	
	@PostConstruct
	public void init(){		
		try{
			
			//perforacionExplorativa = fichaAmbientalMineraFacade.cargarPerforacionExplorativa(proyectosBean.getProyecto());			
			
			inicializarDatosGeneralesBiotico();
			coberturaVegetalList = inicializarBiotico(TipoCatalogo.CODIGO_COBERTURA_VEGETAL_SD);
			pisosZooGeograficosList = inicializarBiotico(TipoCatalogo.CODIGO_PISOS_ZOOGEOGRAFICOS_SD);
			componenteBioticoNativoList = inicializarBiotico(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_NATIVO_SD);
			componenteBioticoIntroducidoList = inicializarBiotico(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_INTRODUCIDO_SD);
			aspectosEcologicosList = inicializarBiotico(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS_SD);
			areasSensiblesList = inicializarBiotico(TipoCatalogo.CODIGO_AREAS_SENSIBLES_SD);
			
			coberturaVegetalEliminadosList = new ArrayList<CatalogoGeneralBiotico>();
			
			inicializarCobertura();
			
			listaBioticoCoberturaVegetal = new ArrayList<ComponenteBioticoSD>();
			listaBioticoPisosZooGeograficos = new ArrayList<ComponenteBioticoSD>(); 
			listaBioticoNativo = new ArrayList<ComponenteBioticoSD>();
			listaBioticoIntroducido = new ArrayList<ComponenteBioticoSD>();
			listaBioticoAspectoEcologico = new ArrayList<ComponenteBioticoSD>();
			listaBioticoAreasSensibles = new ArrayList<ComponenteBioticoSD>();
			
			
			loadData();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void loadData(){
		try {
			
			//buscar con el proyecto
			//List<ComponenteBioticoSD> listaBiotico = componenteBioticoFacade.buscarComponenteBioticoPorPerforacionExplorativa(perforacionExplorativa.getId());
			if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getId() != null) {
				perforacionExplorativa = fichaAmbientalMineria020Facade.cargarPerforacionExplorativaRcoa(proyectosBean.getProyectoRcoa().getId()).getId();
			} else {
				perforacionExplorativa=fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyectosBean.getProyecto()).getId();
			}
			List<ComponenteBioticoSD> listaBiotico = componenteBioticoFacade.buscarComponenteBioticoPorPerforacionExplorativa(perforacionExplorativa);
			
			if(listaBiotico!=null)
			{
				for(ComponenteBioticoSD biotico : listaBiotico){
					for(CatalogoGeneralBiotico cobertura : coberturaVegetalList){
						if(cobertura.getCatalogoGeneralBioticoList() != null){
							for(CatalogoGeneralBiotico coberturaHijo : cobertura.getCatalogoGeneralBioticoList()){
								if(biotico.getCatalogoGeneralBiotico().getId().equals(coberturaHijo.getId())){
									coberturaHijo.setSeleccionado(true);
									listaBioticoCoberturaVegetal.add(biotico);
								}
							}
						}										
					}

					for(CatalogoGeneralBiotico pisos : pisosZooGeograficosList){
						if(biotico.getCatalogoGeneralBiotico().getId().equals(pisos.getId())){
							pisos.setSeleccionado(true);
							listaBioticoPisosZooGeograficos.add(biotico);
						}
					}

					for(CatalogoGeneralBiotico nativo : componenteBioticoNativoList){
						if(biotico.getCatalogoGeneralBiotico().getId().equals(nativo.getId())){
							nativo.setSeleccionado(true);
							listaBioticoNativo.add(biotico);
						}
					}

					for(CatalogoGeneralBiotico introducido : componenteBioticoIntroducidoList){
						if(biotico.getCatalogoGeneralBiotico().getId().equals(introducido.getId())){
							introducido.setSeleccionado(true);
							listaBioticoIntroducido.add(biotico);
						}
					}

					for(CatalogoGeneralBiotico ecologico : aspectosEcologicosList){
						if(biotico.getCatalogoGeneralBiotico().getId().equals(ecologico.getId())){
							ecologico.setSeleccionado(true);
							listaBioticoAspectoEcologico.add(biotico);
						}
					}

					for(CatalogoGeneralBiotico area : areasSensiblesList){
						if(biotico.getCatalogoGeneralBiotico().getId().equals(area.getId())){
							area.setSeleccionado(true);
							listaBioticoAreasSensibles.add(biotico);
						}
					}				
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void inicializarDatosGeneralesBiotico() {
        List<String> codigosBioticos = new ArrayList<String>();
        listaGeneralBiotico = new HashMap<String, List<CatalogoGeneralBiotico>>();
        codigosBioticos.add(TipoCatalogo.CODIGO_COBERTURA_VEGETAL_SD);
        codigosBioticos.add(TipoCatalogo.CODIGO_PISOS_ZOOGEOGRAFICOS_SD);
        codigosBioticos.add(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_NATIVO_SD);
        codigosBioticos.add(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_INTRODUCIDO_SD);
        codigosBioticos.add(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS_SD);
        codigosBioticos.add(TipoCatalogo.CODIGO_AREAS_SENSIBLES_SD);
        List<CatalogoGeneralBiotico> bioticos = catalogoBioticoFacade.obtenerListaBioticoTipo(codigosBioticos);
        for (CatalogoGeneralBiotico catalogoGeneralBiotico : bioticos) {
            List<CatalogoGeneralBiotico> tmp = new ArrayList<CatalogoGeneralBiotico>();
            String key = catalogoGeneralBiotico.getTipoCatalogo().getCodigo();
            if (listaGeneralBiotico.containsKey(key)) {
                tmp = listaGeneralBiotico.get(key);
            }
            tmp.add(catalogoGeneralBiotico);
            listaGeneralBiotico.put(key, tmp);
        }
    }
	
	public List<CatalogoGeneralBiotico> inicializarBiotico(String codigo) {

        if (listaGeneralBiotico.containsKey(codigo)) {
            return listaGeneralBiotico.get(codigo);
        }
        return new ArrayList<CatalogoGeneralBiotico>();
    }
	
	public void inicializarCobertura() {
		coberturaVegetalPadreList = new ArrayList<>();
		for(CatalogoGeneralBiotico cobertura : coberturaVegetalList){
			if(cobertura.getCatalogoPadre() == null){
				coberturaVegetalPadreList.add(cobertura);
			}
		}
    }
	
	public void guardarDatos(){
		try {
			boolean existeseleccionado = false;
			List<ComponenteBioticoSD> listaElementos = new ArrayList<>();
			
			List<ComponenteBioticoSD> listaCoberturaVegetalCopia = new ArrayList<ComponenteBioticoSD>();
			listaCoberturaVegetalCopia.addAll(listaBioticoCoberturaVegetal);
			
			if(coberturaVegetalPadreList != null && !coberturaVegetalPadreList.isEmpty()){
				for(CatalogoGeneralBiotico coberturaVegetal : coberturaVegetalPadreList){
					if(coberturaVegetal.getCatalogoGeneralBioticoList() != null && !coberturaVegetal.getCatalogoGeneralBioticoList().isEmpty()){
						existeseleccionado = false;
						for(CatalogoGeneralBiotico cobertura : coberturaVegetal.getCatalogoGeneralBioticoList()){														
							if(cobertura.isSeleccionado()){
								existeseleccionado = true;
								boolean guardado = false;
								for(ComponenteBioticoSD bioticoAux : listaBioticoCoberturaVegetal){
									if(bioticoAux.getCatalogoGeneralBiotico().getId().equals(cobertura.getId())){
										listaCoberturaVegetalCopia.remove(bioticoAux);
										guardado = true;
										break;
									}
								}
								if(!guardado){
									ComponenteBioticoSD componenteCobertura = new ComponenteBioticoSD();
									componenteCobertura.setCatalogoGeneralBiotico(cobertura);
									componenteCobertura.setTipoBiotico("coberturaVegetal");
									componenteCobertura.setPerforacionExplorativa(perforacionExplorativa);
									listaElementos.add(componenteCobertura);
								}								
							}
						}
						//validacion para elegir almenos un registro
						if(!existeseleccionado){
							JsfUtil.addMessageError("Debe seleccionar al menos un elemento "+coberturaVegetal.getDescripcion());
							break;
						}
					}	
				}
			}
			if(existeseleccionado){
				if(listaCoberturaVegetalCopia != null && !listaCoberturaVegetalCopia.isEmpty()){
					for(ComponenteBioticoSD coberturaEliminar : listaCoberturaVegetalCopia){
						coberturaEliminar.setEstado(false);
						componenteBioticoFacade.guardar(coberturaEliminar);
					}
				}
				
				//PISOS ZOOGEOGRAFICOS
				List<ComponenteBioticoSD> listaPisosZooGeograficosCopia = new ArrayList<ComponenteBioticoSD>();
				listaPisosZooGeograficosCopia.addAll(listaBioticoPisosZooGeograficos);
				
				if(pisosZooGeograficosList != null && !pisosZooGeograficosList.isEmpty()){
					existeseleccionado=false;
					for(CatalogoGeneralBiotico pisos : pisosZooGeograficosList){
						if(pisos.isSeleccionado()){
							existeseleccionado=true;
							boolean guardado = false;
							for(ComponenteBioticoSD bioticoAux : listaBioticoPisosZooGeograficos){
								if(bioticoAux.getCatalogoGeneralBiotico().getId().equals(pisos.getId())){
									listaPisosZooGeograficosCopia.remove(bioticoAux);
									guardado = true;
									break;
								}
							}
							
							if(!guardado){
								ComponenteBioticoSD componentePisos = new ComponenteBioticoSD();
								componentePisos.setCatalogoGeneralBiotico(pisos);
								componentePisos.setTipoBiotico("pisosZoogeograficos");
								componentePisos.setPerforacionExplorativa(perforacionExplorativa);
								listaElementos.add(componentePisos);
							}						
						}					
					}
					if(!existeseleccionado){
						JsfUtil.addMessageError("Debe seleccionar al menos un elemento de pisos zoogeográficos");
						return;
					}
				}
				
				if(listaPisosZooGeograficosCopia != null && !listaPisosZooGeograficosCopia.isEmpty()){
					for(ComponenteBioticoSD coberturaEliminar : listaPisosZooGeograficosCopia){
						coberturaEliminar.setEstado(false);
						componenteBioticoFacade.guardar(coberturaEliminar);
					}
				}
				
				//COMPONENTE BIOTICO NATIVO
				List<ComponenteBioticoSD> listaBioticoNativoCopia = new ArrayList<ComponenteBioticoSD>();
				listaBioticoNativoCopia.addAll(listaBioticoNativo);
				
				if(componenteBioticoNativoList != null && !componenteBioticoNativoList.isEmpty()){
					existeseleccionado=false;
					for(CatalogoGeneralBiotico bioticoNativo : componenteBioticoNativoList){
						if(bioticoNativo.isSeleccionado()){
							existeseleccionado=true;
							boolean guardado = false;
							for(ComponenteBioticoSD bioticoAux : listaBioticoNativo){
								if(bioticoAux.getCatalogoGeneralBiotico().getId().equals(bioticoNativo.getId())){
									listaBioticoNativoCopia.remove(bioticoAux);
									guardado = true;
									break;
								}
							}			
							
							if(!guardado){
								ComponenteBioticoSD componenteBioticoNativo = new ComponenteBioticoSD();							
								componenteBioticoNativo.setCatalogoGeneralBiotico(bioticoNativo);
								componenteBioticoNativo.setTipoBiotico("bioticoNativo");
								componenteBioticoNativo.setPerforacionExplorativa(perforacionExplorativa);
								listaElementos.add(componenteBioticoNativo);
							}						
						}
					}
					if(!existeseleccionado){
						JsfUtil.addMessageError("Debe seleccionar al menos un elemento de componente biótico nativo");
						return;
					}
				}
				
				if(listaBioticoNativoCopia != null && !listaBioticoNativoCopia.isEmpty()){
					for(ComponenteBioticoSD coberturaEliminar : listaBioticoNativoCopia){
						coberturaEliminar.setEstado(false);
						componenteBioticoFacade.guardar(coberturaEliminar);
					}
				}
				
				//Componente Biotico Introducido
				List<ComponenteBioticoSD> listaBioticoIntroducidoCopia = new ArrayList<ComponenteBioticoSD>();
				listaBioticoIntroducidoCopia.addAll(listaBioticoIntroducido);			
				
				if(componenteBioticoIntroducidoList != null && !componenteBioticoIntroducidoList.isEmpty()){
					existeseleccionado=false;
					for(CatalogoGeneralBiotico bioticoIntroducido : componenteBioticoIntroducidoList){
						if(bioticoIntroducido.isSeleccionado()){
							existeseleccionado=true;
							boolean guardado = false;
							for(ComponenteBioticoSD bioticoAux : listaBioticoIntroducido){
								if(bioticoAux.getCatalogoGeneralBiotico().getId().equals(bioticoIntroducido.getId())){
									listaBioticoIntroducidoCopia.remove(bioticoAux);
									guardado = true;
									break;
								}
							}		
							
							if(!guardado){
								ComponenteBioticoSD componenteBioticoIntroducido = new ComponenteBioticoSD();							
								componenteBioticoIntroducido.setCatalogoGeneralBiotico(bioticoIntroducido);
								componenteBioticoIntroducido.setTipoBiotico("bioticoIntroducido");
								componenteBioticoIntroducido.setPerforacionExplorativa(perforacionExplorativa);
								listaElementos.add(componenteBioticoIntroducido);
							}
						}
					}
					if(!existeseleccionado){
						JsfUtil.addMessageError("Debe seleccionar al menos un elemento de componente biótico introducido");
						return;
					}
				}
				
				if(listaBioticoIntroducidoCopia != null && !listaBioticoIntroducidoCopia.isEmpty()){
					for(ComponenteBioticoSD coberturaEliminar : listaBioticoIntroducidoCopia){
						coberturaEliminar.setEstado(false);
						componenteBioticoFacade.guardar(coberturaEliminar);
					}
				}
				
				//ASPECTOS ECOLOGICOS
				List<ComponenteBioticoSD> listaBioticoAspectoEcologicoCopia = new ArrayList<ComponenteBioticoSD>();
				listaBioticoAspectoEcologicoCopia.addAll(listaBioticoAspectoEcologico);	
				
				if(aspectosEcologicosList != null && !aspectosEcologicosList.isEmpty()){
					existeseleccionado=false;
					for(CatalogoGeneralBiotico aspectoEcologico : aspectosEcologicosList){
						if(aspectoEcologico.isSeleccionado()){
							existeseleccionado=true;
							boolean guardado = false;
							for(ComponenteBioticoSD bioticoAux : listaBioticoAspectoEcologico){
								if(bioticoAux.getCatalogoGeneralBiotico().getId().equals(aspectoEcologico.getId())){
									listaBioticoAspectoEcologicoCopia.remove(bioticoAux);
									guardado = true;
									break;
								}
							}	
							
							if(!guardado){
								ComponenteBioticoSD componenteAspectoEcologico = new ComponenteBioticoSD();							
								componenteAspectoEcologico.setCatalogoGeneralBiotico(aspectoEcologico);
								componenteAspectoEcologico.setTipoBiotico("aspectoEcologico");
								componenteAspectoEcologico.setPerforacionExplorativa(perforacionExplorativa);
								listaElementos.add(componenteAspectoEcologico);
							}						
						}					
					}
					if(!existeseleccionado){
						JsfUtil.addMessageError("Debe seleccionar al menos un elemento de aspectos ecológicos");
						return;
					}
				}
				
				if(listaBioticoAspectoEcologicoCopia != null && !listaBioticoAspectoEcologicoCopia.isEmpty()){
					for(ComponenteBioticoSD coberturaEliminar : listaBioticoAspectoEcologicoCopia){
						coberturaEliminar.setEstado(false);
						componenteBioticoFacade.guardar(coberturaEliminar);
					}
				}
				
				//AREAS SENSIBLES
				List<ComponenteBioticoSD> listaBioticoAreasSensiblesCopia = new ArrayList<ComponenteBioticoSD>();
				listaBioticoAreasSensiblesCopia.addAll(listaBioticoAreasSensibles);	
				
				if(areasSensiblesList != null && !areasSensiblesList.isEmpty()){
					existeseleccionado=false;
					for(CatalogoGeneralBiotico areaSensible : areasSensiblesList){
						if(areaSensible.isSeleccionado()){
							existeseleccionado=true;
							boolean guardado = false;
							for(ComponenteBioticoSD bioticoAux : listaBioticoAreasSensibles){
								if(bioticoAux.getCatalogoGeneralBiotico().getId().equals(areaSensible.getId())){
									listaBioticoAreasSensiblesCopia.remove(bioticoAux);
									guardado = true;
									break;
								}
							}						
							
							if(!guardado){
								ComponenteBioticoSD componenteAreaSensible = new ComponenteBioticoSD();							
								componenteAreaSensible.setCatalogoGeneralBiotico(areaSensible);
								componenteAreaSensible.setTipoBiotico("areaSensible");
								componenteAreaSensible.setPerforacionExplorativa(perforacionExplorativa);
								listaElementos.add(componenteAreaSensible);	
							}						
						}									
					}
					if(!existeseleccionado){
						JsfUtil.addMessageError("Debe seleccionar al menos un elemento de áreas sensibles");
						return;
					}
				}
				
				if(listaBioticoAreasSensiblesCopia != null && !listaBioticoAreasSensiblesCopia.isEmpty()){
					for(ComponenteBioticoSD coberturaEliminar : listaBioticoAreasSensiblesCopia){
						coberturaEliminar.setEstado(false);
						componenteBioticoFacade.guardar(coberturaEliminar);
					}
				}
				
				componenteBioticoFacade.guardarLista(listaElementos);
				
//				JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	

}
