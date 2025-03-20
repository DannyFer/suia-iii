package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class TestEsIAController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(TestEsIAController.class);
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;

	@PostConstruct
	private void iniciar() {
		
	}
	
	public void iniciarProceso(Integer idProyecto) {
		
		ProyectoLicenciaCoa proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);

		TestProcesoEsIAController procesoViabilidadController=JsfUtil.getBean(TestProcesoEsIAController.class);
		if(procesoViabilidadController.iniciarProceso(proyectoLicenciaCoa)){				
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		}
	}
	
	
}
