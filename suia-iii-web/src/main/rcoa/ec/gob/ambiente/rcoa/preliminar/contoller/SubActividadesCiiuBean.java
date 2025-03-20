package ec.gob.ambiente.rcoa.preliminar.contoller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.NoneScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.rcoa.facade.CombinacionActividadesFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoCoaCiuuSubActividadesFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.CombinacionSubActividades;
import ec.gob.ambiente.rcoa.model.ProyectoCoaCiuuSubActividades;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.SubActividades;
import ec.gob.ambiente.suia.utils.Constantes;

@ManagedBean
@NoneScoped
public class SubActividadesCiiuBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private ProyectoCoaCiuuSubActividadesFacade proyectoCoaCiuuSubActividadesFacade;
	@EJB
	private CombinacionActividadesFacade combinacionActividadesFacade;
	
	@Getter
	@Setter
	private boolean bloqueoCamposGestionTransporte;
	
	@Getter
	@Setter
	private boolean mostrarItemDependiente1, mostrarItemDependiente2, mostrarItemDependiente3;
	
	@Getter
	@Setter
	private boolean habilitarItemDependiente1, habilitarItemDependiente2, habilitarItemDependiente3;
	
	@Getter
	@Setter
	private  CombinacionSubActividades combinacionSubActividadesProcesos, combinacionSubActividadesTipoEnte;
	
	@Getter
	@Setter
	private List<Integer> listaSubActividadesProcesadas; 
	
	
	public Boolean esActividadRecupercionMateriales(CatalogoCIUU catalogo) {
		try {
			String actividadesRecuperacionMateriales = Constantes.getActividadesRecuperacionMateriales();				
			List<String> actividadesRecuperacionMaterialesArray = Arrays.asList(actividadesRecuperacionMateriales.split(","));
			if(actividadesRecuperacionMaterialesArray.contains(catalogo.getCodigo()))
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
        }
	}
	
	public void buscarSubActividadesCiiu(ProyectoLicenciaCuaCiuu ciiu, List<SubActividades> listaSubActividades, Integer nroActividad) {
		List<ProyectoCoaCiuuSubActividades> ciiuSubActividades = proyectoCoaCiuuSubActividadesFacade.cargarSubActividades(ciiu);
		List<SubActividades> seleccionadas = new ArrayList<>();
		for(ProyectoCoaCiuuSubActividades ciiuActividad : ciiuSubActividades){
			seleccionadas.add(ciiuActividad.getSubActividad());
		}
		
		for (SubActividades item : listaSubActividades.get(0).getHijos()) {
				listaSubActividadesProcesadas = new ArrayList<Integer>();
				setSubActividadesGuardadas(item, seleccionadas);
		}
		
		for (SubActividades item : listaSubActividades.get(0).getHijos()) {
			listaSubActividadesProcesadas = new ArrayList<Integer>();
			verficarDependientesGuardados(item, seleccionadas, listaSubActividades, nroActividad);
		}
	}
	
	public void setSubActividadesGuardadas(SubActividades subActividad,  List<SubActividades> listaSubActividadesGuardadas){
		if(subActividad.getHijos() != null && subActividad.getHijos().size() > 0) {
			for (SubActividades item : subActividad.getHijos()) {
				setSubActividadesGuardadas(item,listaSubActividadesGuardadas);
			}
			
			if(subActividad.getSubSeleccionada() != null && subActividad.getSubSeleccionada())
				subActividad.getSubActividades().setSubSeleccionada(true);
		} else {
			SubActividades parent = subActividad.getSubActividades();
			
			if(!listaSubActividadesProcesadas.contains(parent.getId())) {
				listaSubActividadesProcesadas.add(parent.getId());
				
				for (SubActividades item : parent.getHijos()) {
					if(listaSubActividadesGuardadas.contains(item)) {
						if(item.getTipoPregunta().equals(1)) 
							parent.setHijoSeleccionado(item);
						else if(item.getTipoPregunta().equals(7))
							parent.getListaHijosSeleccionado().add(item);
						
						item.setSubSeleccionada(true);
						parent.setSubSeleccionada(true);
					}
				}
			}
		}
	}
	
	public void verficarDependientesGuardados(SubActividades subActividad,  List<SubActividades> listaSubActividadesGuardadas, List<SubActividades> listaSubActividades, Integer nroActividad){
		if(subActividad.getHijos() != null && subActividad.getHijos().size() > 0) {
			for (SubActividades item : subActividad.getHijos()) {
				verficarDependientesGuardados(item,listaSubActividadesGuardadas, listaSubActividades, nroActividad);
			}
		} else {
			SubActividades parent = subActividad.getSubActividades();
			
			if(!listaSubActividadesProcesadas.contains(parent.getId())) {
				listaSubActividadesProcesadas.add(parent.getId());
				
				for (SubActividades item : parent.getHijos()) {
					if(listaSubActividadesGuardadas.contains(item)) {
						if(item.getCategoriaDependiente() != null && item.getCategoriaDependiente().contains(","))
							validarCampoDependiente(item.getSubActividades(), listaSubActividades, nroActividad);
					}
				}
			}
		}
	}
	
	public void actualizarCamposRelacionados(SubActividades parent, SubActividades hijo, Integer nroActividad) {
		try {
			SubActividades principal = parent.getSubActividades();
			int posicionContenedor = parent.getSubActividades().getHijos().indexOf(parent);
			int posicionPrincipal = principal.getSubActividades().getHijos().indexOf(principal);
			for (SubActividades subItem : parent.getHijos()) {
				if(!subItem.getId().equals(hijo.getId())){
					subItem.setSubSeleccionada(null);
					
					RequestContext.getCurrentInstance().update("frmPreliminar:pnlGridPrincipalA"+nroActividad+":"+posicionPrincipal+":idRepeatA"+nroActividad+":"+posicionContenedor+":pnlGridContenedorA"+nroActividad);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
        }
	}
	
	public List<FacesMessage> validarDatosSubActividades(List<SubActividades> listaSubActividades, Boolean residuos, Integer nroActividad) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		Map<SubActividades, FacesMessage> erroresActividades = new HashMap<>();
		
		Boolean mostrarItemDependiente = false;
		switch (nroActividad) {
		case 1:
			mostrarItemDependiente = mostrarItemDependiente1;
			break;
		case 2:
			mostrarItemDependiente = mostrarItemDependiente2;
			break;
		case 3:
			mostrarItemDependiente = mostrarItemDependiente3;
			break;
		default:
			break;
		}
		
		Boolean actividadPrincipal = false;
		for (SubActividades item : listaSubActividades.get(0).getHijos()) {
			if(item.getSubSeleccionada() != null && item.getSubSeleccionada()) {
				listaSubActividadesProcesadas = new ArrayList<Integer>();
				setearSubactividadesSeleccionadas(item);
				
//				erroresActividades.putAll(validarDatos(item, mostrarItemDependiente));
				actividadPrincipal = true;

				if(item.getRequiereIngresoResiduos() && residuos) 
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe ingresar al menos un residuo en Identificación de residuos o desechos no peligrosos o especiales no peligrosos.", null));
			}
		}
		
		if(!actividadPrincipal) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar al menos una opción para el campo '" + listaSubActividades.get(0).getNombre()+ "'.", null));
		} else  if(erroresActividades.size() > 0)
			errorMessages = new ArrayList<FacesMessage>(erroresActividades.values());
		
		return errorMessages;		
	}

	public Map<SubActividades, FacesMessage> validarDatos(SubActividades subActividad, Boolean mostrarItemDependiente) {
		Map<SubActividades, FacesMessage> errorMessages = new HashMap<>();
		
		try{

			if(subActividad.getHijos() != null && subActividad.getHijos().size() > 0) {
				for (SubActividades item : subActividad.getHijos()) {
						errorMessages.putAll(validarDatos(item, mostrarItemDependiente));
					}
			} else {
	
				Boolean subActividadSeleccionada = false;
	
				SubActividades parent = subActividad.getSubActividades();
	
				for (SubActividades subItem : parent.getHijos()) {
					if(parent.getNumeroCombinacion().equals(11)) {
						if(mostrarItemDependiente) {
							if(subItem.getSubSeleccionada() != null && subItem.getSubSeleccionada()) {
								subActividadSeleccionada = true;
								break;
							}
						} else {
							subActividadSeleccionada = true;
							break;
						}
					} else if(subItem.getSubSeleccionada() != null && subItem.getSubSeleccionada()) {
						subActividadSeleccionada = true;
						break;
					}
				}
	
				if(!subActividadSeleccionada) {
					if(!errorMessages.containsKey(parent))
					errorMessages.put(parent, new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Debe seleccionar una opción para el campo '" + parent.getNombre()+ "'.", null));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
        }

		return errorMessages;
	}
	
	public void setearSubactividadesSeleccionadas(SubActividades subActividad){
		if(subActividad.getHijos() != null && subActividad.getHijos().size() > 0) {
			for (SubActividades item : subActividad.getHijos()) {
				setearSubactividadesSeleccionadas(item);
				}
		} else {
			SubActividades parent = subActividad.getSubActividades();
			List<SubActividades> listaSeleccionados = parent.getListaHijosSeleccionado();
			
			if(!listaSubActividadesProcesadas.contains(parent.getId())) {
				listaSubActividadesProcesadas.add(parent.getId());
				
				for (SubActividades item : parent.getHijos()) {
					if((listaSeleccionados != null && 
							listaSeleccionados.size() > 0 && 
							listaSeleccionados.contains(item)) ||
							(parent.getHijoSeleccionado() != null && 
									parent.getHijoSeleccionado().getId().equals(item.getId())))
						item.setSubSeleccionada(true);
				}
			}
		}
	}
	public void recuperarConsideracionesPorSubActividades(CatalogoCIUU actividadPrimaria) {
		try {
			combinacionSubActividadesProcesos = null;
			combinacionSubActividadesTipoEnte = null;
			
			List<String> numeroCombinacionesProcesos = new  ArrayList<>();
			bloqueoCamposGestionTransporte = false;
			SubActividades subActividadPrincipal = null;
	
			for (SubActividades item : actividadPrimaria.getSubActividades().get(0).getHijos()) {
				if(item.getSubSeleccionada() != null && item.getSubSeleccionada()) {
					listaSubActividadesProcesadas = new ArrayList<>();
					numeroCombinacionesProcesos.addAll(recuperarCombinacion(item, 1));
					if(numeroCombinacionesProcesos.size() > 0) 
						numeroCombinacionesProcesos.add(0, item.getNumeroCombinacion().toString()) ;
	
					if(item.getNumeroCombinacion().equals(1)) {
						subActividadPrincipal = item;
						break;
					}
				}
			}
			
			String combinacion = StringUtils.join(numeroCombinacionesProcesos, ",");
			combinacionSubActividadesProcesos = combinacionActividadesFacade.getPorCatalogoCombinacion(actividadPrimaria, combinacion);
			
			List<String> numeroCombinacionesTipoEnte = new  ArrayList<>();
	
			if(subActividadPrincipal != null) {
				combinacionSubActividadesTipoEnte = new CombinacionSubActividades();
				combinacionSubActividadesTipoEnte.setTipoPermiso(subActividadPrincipal.getTipoPermiso());
				combinacionSubActividadesTipoEnte.setEntidadProceso(subActividadPrincipal.getEntidadProceso());
				
				if(combinacionSubActividadesProcesos == null || !combinacionSubActividadesProcesos.getRequiereTransporteSustanciasQuimicas()){
					for (SubActividades item : actividadPrimaria.getSubActividades().get(0).getHijos()) {
						if(!item.getNumeroCombinacion().equals(1) && item.getSubSeleccionada() != null && item.getSubSeleccionada()) {
							List<String> combinacionesComplementarias = new ArrayList<>();
							listaSubActividadesProcesadas = new ArrayList<>();
							combinacionesComplementarias.addAll(recuperarCombinacion(item, 1));
							if(combinacionesComplementarias.size() > 0) 
								combinacionesComplementarias.add(0, item.getNumeroCombinacion().toString()) ;
							
							String combinacionComplementaria = StringUtils.join(combinacionesComplementarias, ",");
							CombinacionSubActividades combinacionProcesoComplementaria = combinacionActividadesFacade.getPorCatalogoCombinacion(actividadPrimaria, combinacionComplementaria);
							if(combinacionProcesoComplementaria != null && combinacionProcesoComplementaria.getRequiereTransporteSustanciasQuimicas())
								combinacionSubActividadesProcesos.setRequiereTransporteSustanciasQuimicas(true);
						}
					}
				}
			} else {
				for (SubActividades item : actividadPrimaria.getSubActividades().get(0).getHijos()) {
					if(item.getSubSeleccionada() != null && item.getSubSeleccionada()) {
						listaSubActividadesProcesadas = new ArrayList<>();
						numeroCombinacionesTipoEnte.addAll(recuperarCombinacion(item, 2));
						if(numeroCombinacionesTipoEnte.size() > 0) 
							numeroCombinacionesTipoEnte.add(0, item.getNumeroCombinacion().toString()) ;
					}
				}
				
				String combinacionEnte = StringUtils.join(numeroCombinacionesTipoEnte, ",");
				combinacionSubActividadesTipoEnte = combinacionActividadesFacade.getPorCatalogoCombinacion(actividadPrimaria, combinacionEnte);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
        }
	}

	public List<String> recuperarCombinacion(SubActividades subActividad, Integer tipo) {
		List<String> numeroCombinaciones = new  ArrayList<>();
		try {
			if (subActividad.getHijos() != null && subActividad.getHijos().size() > 0) {
				for (SubActividades item : subActividad.getHijos()) {
					numeroCombinaciones.addAll(recuperarCombinacion(item, tipo));
				}
			} else {
				SubActividades parent = subActividad.getSubActividades();
				
				if(!listaSubActividadesProcesadas.contains(parent.getId())) {
					listaSubActividadesProcesadas.add(parent.getId());
					
					if(parent.getTipoPregunta().equals(1)) {
						if(parent.getHijoSeleccionado() != null && parent.getHijoSeleccionado().getTipoSeleccionParaCombinacion().equals(tipo)){ 
							numeroCombinaciones.add(parent.getHijoSeleccionado().getNumeroCombinacion().toString());
						}
					} else if(parent.getTipoPregunta().equals(7)) {
						if(parent.getListaHijosSeleccionado() != null && parent.getListaHijosSeleccionado().size() >0) {
							for (SubActividades item : parent.getListaHijosSeleccionado()) {
								if(item.getTipoSeleccionParaCombinacion().equals(tipo))
									numeroCombinaciones.add(item.getNumeroCombinacion().toString());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
        }

		return numeroCombinaciones;
	}

	public void guardarCiiuSubACtividades(ProyectoLicenciaCuaCiuu ciiu, List<SubActividades> listaSubActividades) {
		try {
			proyectoCoaCiuuSubActividadesFacade.eliminar(ciiu);
			
			if(listaSubActividades.size() > 0) {
				for (SubActividades item : listaSubActividades.get(0).getHijos()) {
					if(item.getSubSeleccionada() != null && item.getSubSeleccionada()) {
						listaSubActividadesProcesadas = new ArrayList<>();
						guardarCiiuSubACtividadesRecursivo(ciiu, item);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
        }
	}

	public void guardarCiiuSubACtividadesRecursivo(ProyectoLicenciaCuaCiuu ciiu, SubActividades subActividad) {
		try {
			if (subActividad.getHijos() != null && subActividad.getHijos().size() > 0) {
				for (SubActividades item : subActividad.getHijos()) {
					guardarCiiuSubACtividadesRecursivo(ciiu,item);
				}
			} else {				
				SubActividades parent = subActividad.getSubActividades();
				
				if(!listaSubActividadesProcesadas.contains(parent.getId())) {
					listaSubActividadesProcesadas.add(parent.getId());
					
					if(parent.getTipoPregunta().equals(1)) {
						if(parent.getHijoSeleccionado() != null ){ 
							ProyectoCoaCiuuSubActividades proyectoCoaCiuuSubActividades = new ProyectoCoaCiuuSubActividades();
							proyectoCoaCiuuSubActividades.setProyectoLicenciaCuaCiuu(ciiu);
							proyectoCoaCiuuSubActividades.setSubActividad(parent.getHijoSeleccionado() );
			
							proyectoCoaCiuuSubActividadesFacade.guardar(proyectoCoaCiuuSubActividades);
						}
					} else if(parent.getTipoPregunta().equals(7)) {
						if(parent.getListaHijosSeleccionado() != null && parent.getListaHijosSeleccionado().size() >0) {
							for (SubActividades item : parent.getListaHijosSeleccionado()) {
								ProyectoCoaCiuuSubActividades proyectoCoaCiuuSubActividades = new ProyectoCoaCiuuSubActividades();
								proyectoCoaCiuuSubActividades.setProyectoLicenciaCuaCiuu(ciiu);
								proyectoCoaCiuuSubActividades.setSubActividad(item);
				
								proyectoCoaCiuuSubActividadesFacade.guardar(proyectoCoaCiuuSubActividades);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
        }
	}
	
	public void validarCampoDependiente(SubActividades parent, List<SubActividades> listaSubActividades, Integer nroActividad) {
		SubActividades principal = parent.getSubActividades();
		int posicionPrincipal = principal.getSubActividades().getHijos().indexOf(principal);
		
		try {
			Boolean mostrarItemDependiente, habilitarItemDependiente;
			
			if(parent.getHijos() != null && parent.getHijos().size() > 0) {
				for (SubActividades hijo : parent.getHijos()) {
					mostrarItemDependiente = false;
					habilitarItemDependiente = false;
					
					if(parent.getListaHijosSeleccionado() != null && parent.getListaHijosSeleccionado().size() > 0) {
						if(parent.getListaHijosSeleccionado().contains(hijo)) {
							mostrarItemDependiente = true;
							habilitarItemDependiente = true;
						}
					}
					
					if(hijo.getCategoriaDependiente() != null && hijo.getCategoriaDependiente().contains(",")) {
						List<String> categoriasDependientes = new ArrayList<String>(Arrays.asList(hijo.getCategoriaDependiente().split(",")));
						
						switch (nroActividad) {
						case 1:
							mostrarItemDependiente1 = mostrarItemDependiente;
							habilitarItemDependiente1 = habilitarItemDependiente;
							break;
						case 2:
							mostrarItemDependiente2 = mostrarItemDependiente;
							habilitarItemDependiente2 = habilitarItemDependiente;
							break;
						case 3:
							mostrarItemDependiente3 = mostrarItemDependiente;
							habilitarItemDependiente3 = habilitarItemDependiente;
							break;
						default:
							break;
						}
			
						if(listaSubActividades.size() > 0) {
							for (SubActividades item : listaSubActividades.get(0).getHijos()) {
								if(item.getSubSeleccionada() != null && item.getSubSeleccionada()) {
									setearSubActividadDependiente(item, habilitarItemDependiente, mostrarItemDependiente, categoriasDependientes);
								}
							}
						}
						
						RequestContext.getCurrentInstance().update("frmPreliminar:pnlGridPrincipalA"+nroActividad+":"+posicionPrincipal+":pnlGridHijosA"+nroActividad);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
        }
	}
	
	public void setearSubActividadDependiente(SubActividades subActividad, Boolean seleccionar, Boolean mostrar, List<String> dependientes) throws Exception {
		//System.out.println(subActividad.getNumeroCombinacion() + " " +subActividad.getNombre());
		if (subActividad.getHijos() != null && subActividad.getHijos().size() > 0) {
			if(dependientes.contains(subActividad.getNumeroCombinacion().toString())) {
				subActividad.setEsDependiente(!mostrar);
			}
			
			for (SubActividades item : subActividad.getHijos()) {
				setearSubActividadDependiente(item, seleccionar, mostrar, dependientes);
			}
		} else {
			if (subActividad.getNumeroCombinacion().equals(14)) {
				if(seleccionar)
					subActividad.getSubActividades().setHijoSeleccionado(subActividad);
				else
					subActividad.getSubActividades().setHijoSeleccionado(null);
				subActividad.setSubSeleccionada(seleccionar);
			}
			
//			if (subActividad.getNumeroCombinacion().equals(15) && seleccionar) {
//				subActividad.setSubSeleccionada(false);
//			}

			if (dependientes.contains(subActividad.getNumeroCombinacion().toString()) && !seleccionar) {
				subActividad.getSubActividades().setHijoSeleccionado(null);
			}
		}
	}
	
	public Boolean esActividadExcepcionTransporteSustanciasQuimicas(CatalogoCIUU catalogo) {
		try {
			String actividadesRecuperacionMateriales = Constantes.getActividadesExcepcionTransporteSustanciasQuimicas();				
			List<String> actividadesRecuperacionMaterialesArray = Arrays.asList(actividadesRecuperacionMateriales.split(","));
			if(actividadesRecuperacionMaterialesArray.contains(catalogo.getCodigo()))
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
        }
	}
	
	public Boolean esActividadGalapagos(CatalogoCIUU catalogo) {
		try {
			String activList = Constantes.getActividadesGalapagos();			
			List<String> actividadesArray = Arrays.asList(activList.split(","));
			if(actividadesArray.contains(catalogo.getCodigo()))
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
        }
	}
	
	public Boolean esActividadOperacionRellenosSanitarios(CatalogoCIUU catalogo) {
		try {
			String actividadesRecuperacionMateriales = Constantes.getActividadesRellenosSanitarios();				
			List<String> actividadesRecuperacionMaterialesArray = Arrays.asList(actividadesRecuperacionMateriales.split(","));
			if(actividadesRecuperacionMaterialesArray.contains(catalogo.getCodigo()))
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
        }
	}
	
}
