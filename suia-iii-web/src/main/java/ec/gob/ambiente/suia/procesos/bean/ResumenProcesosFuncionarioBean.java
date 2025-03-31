/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.procesos.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.component.panel.Panel;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.Visibility;

import ec.gob.ambiente.suia.bandeja.controllers.LazyDataListaProcesos;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.DefinicionProceso;
import ec.gob.ambiente.suia.dto.ResumenInstanciaProceso;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 31/08/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ResumenProcesosFuncionarioBean extends ResumenProcesosBean {

	private static final long serialVersionUID = -1926269939069705463L;

	private static final Logger LOG = Logger.getLogger(ResumenProcesosFuncionarioBean.class);
	
	private List<DefinicionProceso> definiciones;
	
	@EJB
	private JbpmSuiaCustomServicesFacade jbpmSuiaCustomServicesFacade;
	
	@Getter
	@Setter
    private LazyDataModel <ResumenInstanciaProceso> listarProcesoPaginador;

	@PostConstruct
	public void init() {
		try {
			process = new HashMap<String, List<ResumenInstanciaProceso>>();
			
			definiciones = jbpmSuiaCustomServicesFacade.listaDefinicionesProcesos();
			
			Boolean esUsuarioBosquesPc = false;
			Boolean esTecnicoViabilidadPc=false;
			if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() > 0){
				for (AreaUsuario areaUser : JsfUtil.getLoggedUser().getListaAreaUsuario()) {
					if(areaUser.getArea().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
						String tipoRol = "role.va.pfn.pc.tecnico.bosques";
						String rolTecnico = Constantes.getRoleAreaName(tipoRol);
						if (Usuario.isUserInRole(JsfUtil.getLoggedUser(), rolTecnico)) {
							esUsuarioBosquesPc = true;
							//break;
						}
						if (Usuario.isUserInRole(JsfUtil.getLoggedUser(), Constantes.ROL_TECNICO_VIABILIDAD_DB )) {
							esTecnicoViabilidadPc = true;
							//break;
						}
					}
				}
			}
			
			Boolean esTecnicoPlaguicidas = false;
			String tipoRol = "role.pqua.pc.tecnico";
			String rolTecnico = Constantes.getRoleAreaName(tipoRol);
			if (Usuario.isUserInRole(JsfUtil.getLoggedUser(), rolTecnico)) {
				esTecnicoPlaguicidas = true;
			}
			
			Boolean esTecnicoRSQ = false;
			String tipoRolRsq = "TÉCNICO RSQ";
//			String rolTecnicoRsq = Constantes.getRoleAreaName(tipoRolRsq);
			if (Usuario.isUserInRole(JsfUtil.getLoggedUser(), tipoRolRsq)) {
				esTecnicoRSQ = true;
			}

			if (definiciones != null) {
				for (DefinicionProceso definicionProceso : definiciones) {
					if(definicionProceso.getName().equals("Registro de generador de desechos especiales y peligrosos") && !esTecnicoViabilidadPc)
						process.put(definicionProceso.getName(), new ArrayList<ResumenInstanciaProceso>());
					if(definicionProceso.getName().equals("Registro de Generador de Residuos y Desechos Peligrosos yo Especiales") && !esTecnicoViabilidadPc)
						process.put(definicionProceso.getName(), new ArrayList<ResumenInstanciaProceso>());
					if(definicionProceso.getName().equals("Aprobacion Requisitos Tecnicos Gestion de Desechos") && !esTecnicoViabilidadPc)
						process.put(definicionProceso.getName(), new ArrayList<ResumenInstanciaProceso>());
					if(esUsuarioBosquesPc && definicionProceso.getName().equals(" Emision Viabilidad Ambiental PFN"))
						process.put(definicionProceso.getName(), new ArrayList<ResumenInstanciaProceso>()); //procesos de viabilidad forestal solo para TECNICO BOSQUES Planta Central
					if(definicionProceso.getName().equals("Viabilidad Ambiental") && esTecnicoViabilidadPc)
						process.put(definicionProceso.getName(), new ArrayList<ResumenInstanciaProceso>());
					if(definicionProceso.getName().equals("Inventario Forestal") && esTecnicoViabilidadPc)
						process.put(definicionProceso.getName(), new ArrayList<ResumenInstanciaProceso>());
					if(esTecnicoPlaguicidas && definicionProceso.getId().equals(Constantes.RCOA_PROCESO_ACTUALIZACION_ETIQUETADO_PLAGUICIDAS))
						process.put(definicionProceso.getName(), new ArrayList<ResumenInstanciaProceso>());
					if(esTecnicoRSQ && definicionProceso.getName().equals("Registro Sustancias Quimicas"))
						process.put(definicionProceso.getName(), new ArrayList<ResumenInstanciaProceso>());
					if(esTecnicoRSQ && definicionProceso.getName().equals("Registro Sustancias Quimicas (Importacion Exportacion)"))
						process.put(definicionProceso.getName(), new ArrayList<ResumenInstanciaProceso>());
					if(esTecnicoRSQ && definicionProceso.getName().equals("Registro Sustancias Quimicas Importacion Vue"))
						process.put(definicionProceso.getName(), new ArrayList<ResumenInstanciaProceso>());
				}
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError("Error al cargar el resumen de procesos.");
			LOG.error("No se puede obtener el resumen de procesos", exception);
		}
	}
	
	@Override
	public void onToggleProcess(ToggleEvent event) {
		if (event.getVisibility().equals(Visibility.HIDDEN))
			return;

		Panel panel = (Panel) event.getComponent();
		String processName = panel.getHeader();
		if (process.get(processName).isEmpty()) {
			String processId = getProcessId(processName);
			if (processId != null) {
				try {
					List<Integer> states = new ArrayList<Integer>();					
					states.add(1);
					states.add(2);					
					listarProcesoPaginador= new LazyDataListaProcesos(processId,states);

				} catch (Exception e) {
					JsfUtil.addMessageError("Error al cargar los procesos.");
					LOG.error("No se puede obtener el resumen de procesos", e);
				}
			}
		}
	}
	
	private String getProcessId(String name) {
		for (DefinicionProceso definicionProceso : definiciones) {
			if (definicionProceso.getName().equals(name))
				return definicionProceso.getId();
		}
		return null;
	}
}
