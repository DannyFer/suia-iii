package ec.gob.ambiente.suia.eia.mediofisico.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.coordenadageneral.facade.CoordenadaGeneralFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.QuimicaSuelo;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.eia.mediofisico.bean.MedioFisicoBean;
import ec.gob.ambiente.suia.eia.mediofisico.facade.QuimicaSueloFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.util.Map;

@ManagedBean
@ViewScoped
public class QuimicaSueloController implements Serializable {

	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(QuimicaSueloController.class);
	private static final long serialVersionUID = -2223368098394337331L;
	@Getter
	@Setter
	@ManagedProperty(value = "#{medioFisicoBean}")
	private MedioFisicoBean medioFisicoBean;
	@EJB
	private QuimicaSueloFacade quimicaSueloFacade;
	@EJB
	private CoordenadaGeneralFacade coordenadaGeneralFacade;
	@EJB
	private CatalogoGeneralFacade catalogoGeneralFacade;
	@Getter
	@Setter
	private Integer eiaId;
	
	public static final Integer RAOHE= 1;
	public static final Integer TULSMA=2;
	public static final String ERROR_LIMITE="Seleccione el parámetro correcto";
	public static final String COLOR="rojo";
	
	
	@PostConstruct
	public void iniciar(){
		try{
		EstudioImpactoAmbiental eia = (EstudioImpactoAmbiental)JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
        setEiaId(eia.getId());
        List<QuimicaSuelo> quimicas= this.quimicaSueloFacade.quimicaXEiaId(getEiaId());
        if(!quimicas.isEmpty()){
        	medioFisicoBean.setListaQuimica(quimicas);
        	for (QuimicaSuelo quimicaSuelo:quimicas) {
				CoordenadaGeneral coordenadaGeneral= new CoordenadaGeneral();
				coordenadaGeneral=coordenadaGeneralFacade.coordenadasGeneralXTablaId(quimicaSuelo.getId(), QuimicaSuelo.class.getSimpleName()).get(0);
				quimicaSuelo.setCoordenadaGeneral(coordenadaGeneral);
				quimicaSuelo.setEditar(true);
				quimicaSuelo.setNormativa(catalogoGeneralFacade.obtenerCatalogoXId(quimicaSuelo.getIdNormativa()));
				quimicaSuelo.setLaboratorio(catalogoGeneralFacade.obtenerCatalogoXId(quimicaSuelo.getIdLaboratorio()));
				if(quimicaSuelo.getIdUso()!=null)
				quimicaSuelo.setUsoSuelo(catalogoGeneralFacade.obtenerCatalogoXId(quimicaSuelo.getIdUso()));
				if(quimicaSuelo.getResultado()>quimicaSuelo.getLimite())
					quimicaSuelo.setColor(COLOR);
				quimicaSuelo.setParametro(catalogoGeneralFacade.obtenerCatalogoXId(quimicaSuelo.getIdParametro()));
				
			}
        }
        medioFisicoBean.setQuimicaBorradas(new ArrayList<QuimicaSuelo>());
        medioFisicoBean.setNormativas(catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.NORMATIVAS));
        medioFisicoBean.setLaboratorios(catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.LABORATORIOS));
        medioFisicoBean.setUsosSuelo(catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.TIPO_USO_RAHOE));
      
        
		}catch(Exception e){
			LOG.error("Error al cargar parametros inciales", e);
		}
	}
	public void agregarAListaQuimica() {
		try {
			if(medioFisicoBean.getQuimicaSuelo().getResultado()>medioFisicoBean.getQuimicaSuelo().getLimite())
				medioFisicoBean.getQuimicaSuelo().setColor(COLOR);
			else
					medioFisicoBean.getQuimicaSuelo().setColor("");
				
			if (!medioFisicoBean.getQuimicaSuelo().isEditar())
				medioFisicoBean.getListaQuimica().add(medioFisicoBean.getQuimicaSuelo());
			RequestContext.getCurrentInstance().addCallbackParam(
					"quimicaIn", true);
		} catch (Exception e) {
			RequestContext.getCurrentInstance().addCallbackParam(
					"quimicaIn", false);
		}
	}

	public void agregarQuimica() throws Exception{
		QuimicaSuelo quimicaSuelo= new QuimicaSuelo();
		quimicaSuelo.setEditar(false);
		quimicaSuelo.setEiaId(getEiaId());
		if(quimicaSuelo.getUsoSuelo()==null)
			 medioFisicoBean.setUsosSuelo(catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.TIPO_USO_RAHOE));
		medioFisicoBean.setQuimicaSuelo(quimicaSuelo);
		medioFisicoBean.getQuimicaSuelo().setNormativa(medioFisicoBean.getNormativas().get(0));
		medioFisicoBean.getQuimicaSuelo().setUsoSuelo(medioFisicoBean.getUsosSuelo().get(0));
		cargarParametro();
		cargarUnidad();
		medioFisicoBean.getQuimicaSuelo().setCoordenadaGeneral(new CoordenadaGeneral());
	}
	public void seleccionarQuimica(QuimicaSuelo quimicaSuelo) {
		quimicaSuelo.setEditar(true);
		medioFisicoBean.setQuimicaSuelo(quimicaSuelo);
		cargarParametro();
	}

	public void eliminarQuimicaSuelo(QuimicaSuelo quimicaSuelo) {
			medioFisicoBean.getListaQuimica().remove(quimicaSuelo);
			if (quimicaSuelo.getId() != null) {
				quimicaSuelo.setEstado(false);
				medioFisicoBean.getQuimicaBorradas().add(quimicaSuelo);
			}
		}
	
	public void limpiar(){
		medioFisicoBean.setListaQuimica(new ArrayList<QuimicaSuelo>());
	}
	
	public String cancelar() {
		medioFisicoBean.setListaQuimica(new ArrayList<QuimicaSuelo>());
		return JsfUtil.actionNavigateTo("/pages/eia/lineaBase/quimicaSuelo.jsf");
	}
	
	public String  guardar(){
		try{
                    if(!medioFisicoBean.getListaQuimica().isEmpty()){
                        EstudioImpactoAmbiental es = new EstudioImpactoAmbiental(getEiaId());
                        Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
                    quimicaSueloFacade.guardar(medioFisicoBean.getListaQuimica(),medioFisicoBean.getQuimicaBorradas(), es, mapOpciones.get(EiaOpciones.CARACTERISTICAS_QUIMICAS_HIDRO));
                    JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
                    limpiar();
                      return JsfUtil.actionNavigateTo("/pages/eia/default.jsf");
                    }else{
                            JsfUtil.addMessageError("Debe ingresar registros");
                    }
		}catch(Exception ex){
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO);
			LOG.error("Error al guardar", ex);
		}
	return null;
	}

public void cargarParametro(){
	try {

		medioFisicoBean.setParametrosCalidad(new ArrayList<SelectItem>());
		SelectItemGroup grupo = null;
		if(medioFisicoBean.getQuimicaSuelo().getNormativa().getId()==RAOHE){
			List<CatalogoGeneral> parametros=new ArrayList<CatalogoGeneral>();
			 if(medioFisicoBean.getQuimicaSuelo().getUsoSuelo()==null){
				 parametros=catalogoGeneralFacade.obtenerCatalogoXPadreOrdenado(medioFisicoBean.getUsosSuelo().get(0).getId());
				 medioFisicoBean.getQuimicaSuelo().setUsoSuelo(medioFisicoBean.getUsosSuelo().get(0));
			 	}
			 else
				parametros=catalogoGeneralFacade.obtenerCatalogoXPadreOrdenado(medioFisicoBean.getQuimicaSuelo().getUsoSuelo().getId());
		
		medioFisicoBean.getQuimicaSuelo().setParametro(parametros.get(0));
		medioFisicoBean.setUsosSuelo(catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.TIPO_USO_RAHOE));
		grupo=new SelectItemGroup(medioFisicoBean.getQuimicaSuelo().getUsoSuelo().getDescripcion());
		SelectItem items[] = new SelectItem[parametros.size()];
		int i=0;
		for (CatalogoGeneral parametro : parametros) {
				items[i] = new SelectItem(parametro);
				i++;
		}
		grupo.setSelectItems(items);
		medioFisicoBean.getParametrosCalidad().add(grupo);
	}
	else
		{
			List<CatalogoGeneral> criterios=catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.CRITERIOS_CALIDAD_SUELO);
			medioFisicoBean.getQuimicaSuelo().setUsoSuelo(null);
			for (CatalogoGeneral catalogoGeneral : criterios) {
				grupo=new SelectItemGroup(catalogoGeneral.getDescripcion());
				List<CatalogoGeneral> parametros=catalogoGeneralFacade.obtenerCatalogoXPadre(catalogoGeneral.getId());
				SelectItem items[] = new SelectItem[parametros.size()];
				int i=0;
				for (CatalogoGeneral parametro : parametros) {
					items[i] = new SelectItem(parametro);
					i++;
				}
				grupo.setSelectItems(items);
				medioFisicoBean.getParametrosCalidad().add(grupo);
		}
	}	
		
	} catch (Exception e) {
		LOG.error("Error al cargar parametros", e);
	}
	
}

	public void cargarUnidad() {
		try{
		medioFisicoBean.getQuimicaSuelo().setUnidad(medioFisicoBean.getQuimicaSuelo().getParametro().getCodigo());
			medioFisicoBean.getQuimicaSuelo().setLimite(new Double(medioFisicoBean.getQuimicaSuelo().getParametro().getValor()));
		}catch(Exception ex){
			JsfUtil.addMessageError(ERROR_LIMITE);
		}
	}

}
