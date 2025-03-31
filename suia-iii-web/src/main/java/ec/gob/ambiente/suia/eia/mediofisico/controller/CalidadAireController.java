package ec.gob.ambiente.suia.eia.mediofisico.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.coordenadageneral.facade.CoordenadaGeneralFacade;
import ec.gob.ambiente.suia.domain.CalidadAire;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.QuimicaSuelo;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.eia.mediofisico.bean.MedioFisicoBean;
import ec.gob.ambiente.suia.eia.mediofisico.facade.CalidadAireFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.util.Map;

@ManagedBean
@ViewScoped
public class CalidadAireController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4310482971806811266L;
	@Getter
	@Setter
	@ManagedProperty(value = "#{medioFisicoBean}")
	private MedioFisicoBean medioFisicoBean;
	@EJB
	private CalidadAireFacade calidadAireFacade;
	@EJB
	private CoordenadaGeneralFacade coordenadaGeneralFacade;
	@EJB
	private CatalogoGeneralFacade catalogoGeneralFacade;
	public static final String COLOR="rojo";
	public static final String UNIDAD_ERROR="Unidad incorrecta.";
	@Getter
	@Setter
	private Integer eiaId;
	
	@PostConstruct
	public void iniciar(){
		try{
		EstudioImpactoAmbiental eia = (EstudioImpactoAmbiental)JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
        setEiaId(eia.getId());
        List<CalidadAire> calidadAires= this.calidadAireFacade.calidadAireXEiaId(getEiaId());
        if(!calidadAires.isEmpty()){
        	medioFisicoBean.setListaCalidadAire(calidadAires);
        	for (CalidadAire calidadAire:calidadAires) {
				CoordenadaGeneral coordenadaGeneral= new CoordenadaGeneral();
				coordenadaGeneral=coordenadaGeneralFacade.coordenadasGeneralXTablaId(calidadAire.getId(), CalidadAire.class.getSimpleName()).get(0);
				calidadAire.setCoordenadaGeneral(coordenadaGeneral);
				calidadAire.setLaboratorio(catalogoGeneralFacade.obtenerCatalogoXId(calidadAire.getIdLaboratorio()));
				calidadAire.setParametro(catalogoGeneralFacade.obtenerCatalogoXId(calidadAire.getIdParametro()));
				if(calidadAire.getResultado()>calidadAire.getLimite())
					calidadAire.setColor(COLOR);
			}
        }
        	
        	medioFisicoBean.setAireBorradas(new ArrayList<CalidadAire>());
            medioFisicoBean.setLaboratorios(catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.LABORATORIOS));
            medioFisicoBean.setParametros(catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.PARAMETRO_CALIDAD_AGUA));
        
		}catch(Exception e){
			e.getMessage();
		}
	}
	public void agregarAListaCalidadAire() {
		try {
			if(medioFisicoBean.getCalidadAire().getResultado()>medioFisicoBean.getCalidadAire().getLimite())
				medioFisicoBean.getCalidadAire().setColor(COLOR);
			else
				medioFisicoBean.getCalidadAire().setColor("");
			if (!medioFisicoBean.getCalidadAire().isEditar())
				medioFisicoBean.getListaCalidadAire().add(medioFisicoBean.getCalidadAire());
			RequestContext.getCurrentInstance().addCallbackParam(
					"aireIn", true);
		} catch (Exception e) {
			RequestContext.getCurrentInstance().addCallbackParam(
					"aireIn", false);
		}
	}

	public void agregarCalidadAire() {
		CalidadAire calidadAire= new CalidadAire();
		calidadAire.setEditar(false);
		calidadAire.setEiaId(getEiaId());
		medioFisicoBean.setCalidadAire(calidadAire);
		medioFisicoBean.getCalidadAire().setParametro(medioFisicoBean.getParametros().get(0));
		medioFisicoBean.getCalidadAire().setCoordenadaGeneral(new CoordenadaGeneral());
	}
	public void seleccionarCalidadAire(CalidadAire calidadAire) {
		calidadAire.setEditar(true);
		medioFisicoBean.setCalidadAire(calidadAire);
	}

	public void eliminarcalidadAire(CalidadAire calidadAire) {
			medioFisicoBean.getListaCalidadAire().remove(calidadAire);
			if (calidadAire.getId() != null) {
				calidadAire.setEstado(false);
				medioFisicoBean.getAireBorradas().add(calidadAire);
			}
		}
	
	public void limpiar(){
		medioFisicoBean.setListaQuimica(new ArrayList<QuimicaSuelo>());
	}
	
	public String cancelar() {
		medioFisicoBean.setListaCalidadAire(new ArrayList<CalidadAire>());
		return JsfUtil.actionNavigateTo("/pages/pages/eia/lineaBase/calidadAire.jsf");
	}
	
	public String  guardar(){
		try{
                    if(!medioFisicoBean.getListaCalidadAire().isEmpty()){
                        EstudioImpactoAmbiental es = new EstudioImpactoAmbiental(getEiaId());
                        Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
                        calidadAireFacade.guardar(medioFisicoBean.getListaCalidadAire(),medioFisicoBean.getAireBorradas(), es, mapOpciones.get(EiaOpciones.CALIDAD_AIRE_HIDRO));
                        JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
                        limpiar();
                        return JsfUtil.actionNavigateTo("/pages/eia/default.jsf");
                    }else{
                        JsfUtil.addMessageError("Debe ingresar registros");
                    }
		}catch(Exception ex){
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO);
		}
	return null;
	}

	public void cargarUnidad()
	{
		try{
		medioFisicoBean.getCalidadAire().setUnidad(medioFisicoBean.getCalidadAire().getParametro().getCodigo());
		medioFisicoBean.getCalidadAire().setLimite(new Double(medioFisicoBean.getCalidadAire().getParametro().getValor()));
		}catch(Exception ex){
			JsfUtil.addMessageError(UNIDAD_ERROR);
		}
		
	}
}
