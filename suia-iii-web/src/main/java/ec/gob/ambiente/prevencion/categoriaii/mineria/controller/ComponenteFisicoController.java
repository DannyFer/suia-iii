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

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoFisicoFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.ComponenteBioticoSD;
import ec.gob.ambiente.suia.domain.ComponenteFisicoPendienteSD;
import ec.gob.ambiente.suia.domain.ComponenteFisicoSD;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.ComponenteFisicoSDFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class ComponenteFisicoController implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private CatalogoFisicoFacade catalogoFisicoFacade;
	
	@EJB
	private ComponenteFisicoSDFacade componenteFisicoFacade;
	
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineriaFacade;
	
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
	private ComponenteFisicoSD componenteFisico;
	
	private Map<String, List<CatalogoGeneralFisico>> listaGeneralFisico;
	
	@Getter
    @Setter
    private List<CatalogoGeneralFisico> pendienteSuelo;
	
	@Getter
	@Setter
	private List<CatalogoGeneralFisico> pendientesSeleccionadas = new ArrayList<CatalogoGeneralFisico>();
	
	@Getter
	@Setter
	private Integer perforacionExplorativa;
	
	
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	
	@PostConstruct
	public void init(){
		try{		
			if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getId() != null) {
				perforacionExplorativa = fichaAmbientalMineria020Facade.cargarPerforacionExplorativaRcoa(proyectosBean.getProyectoRcoa().getId()).getId();
			} else {
				perforacionExplorativa =fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyectosBean.getProyecto()).getId();
			}
			componenteFisico = componenteFisicoFacade.buscarComponenteFisicoPorPerforacionExplorativa(perforacionExplorativa);
			if(componenteFisico == null){
				componenteFisico = new ComponenteFisicoSD();
				componenteFisico.setPerforacionExplorativa(perforacionExplorativa);
			}		
			inicializarDatosGeneralesFisico();
			pendienteSuelo = inicializarFisico(TipoCatalogo.CODIGO_PENDIENTE);
			loadData();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	private void loadData(){
		try {
			if(componenteFisico.getId()!=null)
			{
				for(ComponenteFisicoPendienteSD pendiente : componenteFisico.getComponenteFisicoPendienteList()){
					for(CatalogoGeneralFisico pendienteCat : pendienteSuelo){
						if(pendiente.getCatalogoFisico().getId().equals(pendienteCat.getId()) && pendiente.getEstado() == true){
							pendienteCat.setSeleccionado(true);
						}
					}				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void inicializarDatosGeneralesFisico() {
        List<String> codigosFisicos = new ArrayList<String>();
        listaGeneralFisico = new HashMap<String, List<CatalogoGeneralFisico>>();
        codigosFisicos.add(TipoCatalogo.CODIGO_PENDIENTE);
        List<CatalogoGeneralFisico> fisicos = catalogoFisicoFacade.obtenerListaFisicoTipo(codigosFisicos);
        for (CatalogoGeneralFisico catalogoGeneralFisico : fisicos) {
            List<CatalogoGeneralFisico> tmp = new ArrayList<CatalogoGeneralFisico>();
            String key = catalogoGeneralFisico.getTipoCatalogo().getCodigo();
            if (listaGeneralFisico.containsKey(key)) {
                tmp = listaGeneralFisico.get(key);
            }
            tmp.add(catalogoGeneralFisico);
            listaGeneralFisico.put(key, tmp);
        }
    }
	
	public List<CatalogoGeneralFisico> inicializarFisico(String codigo) {

        if (listaGeneralFisico.containsKey(codigo)) {
            return listaGeneralFisico.get(codigo);
        }
        return new ArrayList<CatalogoGeneralFisico>();
    }	
	
	public void guardarDatos(){
		try{
			boolean existeseleccionado = false;
			List<ComponenteFisicoPendienteSD> pendientes = new ArrayList<ComponenteFisicoPendienteSD>();			
			ComponenteFisicoSD componente= componenteFisicoFacade.guardarComponente(componenteFisico);			
			if(pendienteSuelo!= null && !pendienteSuelo.isEmpty()){
				List<ComponenteFisicoPendienteSD> pendientesEliminar = new ArrayList<ComponenteFisicoPendienteSD>();
				if(componenteFisico.getComponenteFisicoPendienteList() != null && !componenteFisico.getComponenteFisicoPendienteList().isEmpty()){					
					pendientesEliminar.addAll(componenteFisico.getComponenteFisicoPendienteList());
				}				
				
				for(CatalogoGeneralFisico catalogoPen : pendienteSuelo){
					if(catalogoPen.isSeleccionado()){
						boolean guardado = false;
						existeseleccionado = true;
						if(componenteFisico.getComponenteFisicoPendienteList() != null && 
								!componenteFisico.getComponenteFisicoPendienteList().isEmpty()){
							for(ComponenteFisicoPendienteSD catalogoAux : componenteFisico.getComponenteFisicoPendienteList()){
								if(catalogoAux.getCatalogoFisico().getId().equals(catalogoPen.getId())){
									pendientesEliminar.remove(catalogoAux);
									guardado = true;
									break;
								}								
							}
						}						
						if(!guardado){
							ComponenteFisicoPendienteSD pendiente = new ComponenteFisicoPendienteSD();
							pendiente.setComponenteFisicoSD(componente);
							pendiente.setCatalogoFisico(catalogoPen);									
							pendientes.add(pendiente);
						}						
					}
				}
				//validacion para elegir almenos un registro
				if(!existeseleccionado){
					JsfUtil.addMessageError("Debe seleccionar al menos un elemento de Geomorfología");
				}else {
					componenteFisicoFacade.guardarPendiente(pendientes);
					if(pendientesEliminar != null && !pendientesEliminar.isEmpty()){
						for(ComponenteFisicoPendienteSD fisicoEliminar : pendientesEliminar){
							fisicoEliminar.setEstado(false);
							componenteFisicoFacade.guardarPendiente(fisicoEliminar);
						}
					}
					componenteFisico = componenteFisicoFacade.buscarComponenteFisicoPorPerforacionExplorativa(perforacionExplorativa);
//					JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
