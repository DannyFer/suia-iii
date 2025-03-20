package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.dto.EntidadPma;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.PlanManejoAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalSubPlanPmaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.PmaAceptadoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalSubPlanPma;
import ec.gob.ambiente.rcoa.registroAmbiental.bean.DatosFichaRegistroAmbientalBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import lombok.Getter;
import lombok.Setter;


@ManagedBean
@ViewScoped
public class PlanManejoAmbientalOperacionVerController {

    @Getter
    @Setter
	@ManagedProperty(value = "#{datosFichaRegistroAmbientalBean}")
    private DatosFichaRegistroAmbientalBean datosFichaRegistroAmbientalBean;

	@EJB
	private PlanManejoAmbientalCoaFacade planManejoAmbientalCoaFacade;
	
	@EJB
	private RegistroAmbientalSubPlanPmaCoaFacade registroAmbientalSubPlanPmaCoaFacade;
	
	@EJB
	private DocumentosRegistroAmbientalFacade documentosFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;

	@Getter
    @Setter
	private Integer tipoPlan, index;

    @Getter
    @Setter
    private List<EntidadPma> listaPlanPma;

    @Getter
    @Setter
    private List<RegistroAmbientalSubPlanPma> listaDocumentoPlanPma;
	
	@Getter
	@Setter
	private List<DocumentoRegistroAmbiental> listaDocumentoSubPlan;
	
	@Getter
	@Setter
	private String codigoRGD, tipoPlazo;
	
	
    @PostConstruct
	public void init(){
		tipoPlan = 2;
		cargarPmaPoractividades();
    	
	    // verifico si tiene rgd
	    if(datosFichaRegistroAmbientalBean.getProyectoLicenciaCoa().getGeneraDesechos()){	    	
	    	RegistroGeneradorDesechosRcoa registroGeneradorDesechos = registroGeneradorDesechosRcoaFacade.buscarRGDPorProyectoRcoa(datosFichaRegistroAmbientalBean.getProyectoLicenciaCoa().getId());
	    	if(registroGeneradorDesechos != null){
		    	codigoRGD = registroGeneradorDesechos.getCodigo();	    		    	
	    	}else{
	    		codigoRGD = "";
	    	}
	    }else{
	    	codigoRGD = "";
	    }
	    // verifico si realiza gestion propia
    	if(listaPlanPma != null){
	    	for (EntidadPma entidadPlanInicial : listaPlanPma){
	    		if(entidadPlanInicial.getPlanId().equals(5)){
		    		for (PmaAceptadoRegistroAmbiental medidaPma : entidadPlanInicial.getMedidasProyecto()) {
		    			if(datosFichaRegistroAmbientalBean.getProyectoLicenciaCoa().getEsGestionPropia() != null && datosFichaRegistroAmbientalBean.getProyectoLicenciaCoa().getEsGestionPropia()
		    					&& medidaPma.getMedidaVerificacionPma() != null 
		    					&& medidaPma.getMedidaVerificacionPma().isRgdGestioPropia()){
		    				medidaPma.getMedidaVerificacionPma().setObligatorio(true);
		    			}
		    			if(medidaPma.getMedidaVerificacionPma() != null && medidaPma.getMedidaVerificacionPma().isMostrarRGD()){
		    				medidaPma.getMedidaVerificacionPma().setFrecuencia(codigoRGD);
		    				medidaPma.setFrecuencia(codigoRGD);
		    			}
		    		}
	    		}
		    }
    	}
	}

	public void cargarPmaPoractividades(){
		// obtengo el pma registrado en el proyecto
		// obtengo los codigos de las actividades principar y complementarias
		// cargo el plan de la actividad principal
			listaPlanPma = planManejoAmbientalCoaFacade.obtenerPmaPorRegistroAmbiental(datosFichaRegistroAmbientalBean.getRegistroAmbientalRcoa(), tipoPlan, true);
			// obtengo los documentos por cada subplan
			listaDocumentoPlanPma = registroAmbientalSubPlanPmaCoaFacade.obtenerSubPlanPorProyecto(datosFichaRegistroAmbientalBean.getRegistroAmbientalRcoa());
			try{
				index = 0;
				for (EntidadPma entidadPlanInicial : listaPlanPma){
					boolean existeArchivo = false;
					for (RegistroAmbientalSubPlanPma registroAmbientalSubPlanPma : listaDocumentoPlanPma) {
						if(entidadPlanInicial.getPlanId() != null && entidadPlanInicial.getPlanId().equals(registroAmbientalSubPlanPma.getPlanManejoAmbientalPma().getId())){
							listaDocumentoSubPlan = documentosFacade.documentoXTablaIdXIdDocLista(registroAmbientalSubPlanPma.getId(), RegistroAmbientalSubPlanPma.class.getSimpleName(), TipoDocumentoSistema.RCOA_SUBPLAN_MANEJO_AMBIENTAL);	
							if(listaDocumentoSubPlan != null && listaDocumentoSubPlan.size() > 0){
								entidadPlanInicial.setListaDocumentoAdjunto(listaDocumentoSubPlan);
								existeArchivo = true;
								break;
							}
						}
					}
					if(!existeArchivo){
						entidadPlanInicial.setListaDocumentoAdjunto(new ArrayList<DocumentoRegistroAmbiental>());
					}
				}
			}catch(CmisAlfrescoException e){

			}
	}
}