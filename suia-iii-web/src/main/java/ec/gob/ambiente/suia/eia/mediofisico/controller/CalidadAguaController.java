package ec.gob.ambiente.suia.eia.mediofisico.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import ec.gob.ambiente.suia.domain.CalidadAgua;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.QuimicaSuelo;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.eia.mediofisico.bean.MedioFisicoBean;
import ec.gob.ambiente.suia.eia.mediofisico.facade.CalidadAguaFacade;
import ec.gob.ambiente.suia.tipocatalogo.facade.TipoCatalogoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class CalidadAguaController implements Serializable {

	/**
	 * 
	 */
	private static final Logger LOG= Logger.getLogger(CalidadAguaController.class);
	private static final long serialVersionUID = -3087059938349801636L;
	@Getter
	@Setter
	@ManagedProperty(value = "#{medioFisicoBean}")
	private MedioFisicoBean medioFisicoBean;
	@EJB
	private CalidadAguaFacade calidadAguaFacade;
	@EJB
	private CoordenadaGeneralFacade coordenadaGeneralFacade;
	@EJB
	private TipoCatalogoFacade tipoCatalogoFacade;
	@EJB
	private CatalogoGeneralFacade catalogoGeneralFacade;
	
	@Getter
	@Setter
	private Integer eiaId;
	public static final String COLOR="rojo";
	public static final String PARAMETRO="Seleccione el párametro nuevamente";
	@PostConstruct
	public void iniciar(){
		try{
		EstudioImpactoAmbiental eia = (EstudioImpactoAmbiental)JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
        setEiaId(eia.getId());
        List<CalidadAgua> calidadAguas= this.calidadAguaFacade.calidadAguaXEiaId(getEiaId());
        if(!calidadAguas.isEmpty()){
        	medioFisicoBean.setListaCalidadAgua(calidadAguas);
        	for (CalidadAgua calidadAgua:calidadAguas) {
				CoordenadaGeneral coordenadaGeneral= new CoordenadaGeneral();
				coordenadaGeneral=coordenadaGeneralFacade.coordenadasGeneralXTablaId(calidadAgua.getId(), CalidadAgua.class.getSimpleName()).get(0);
				calidadAgua.setCoordenadaGeneral(coordenadaGeneral);
				calidadAgua.setLaboratorio(catalogoGeneralFacade.obtenerCatalogoXId(calidadAgua.getIdLaboratorio()));
				calidadAgua.setParametro(catalogoGeneralFacade.obtenerCatalogoXId(calidadAgua.getIdParametro()));
				if(calidadAgua.getResultado()>calidadAgua.getLimite())
					calidadAgua.setColor(COLOR);
			}
        }
        	medioFisicoBean.setAguaBorradas(new ArrayList<CalidadAgua>());
        	medioFisicoBean.setLaboratorios(catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.LABORATORIOS));
        	cargarParametros();
        
		}catch(Exception ex){
			LOG.error("Error al iniciar Calidad Agua",ex);
		}
	}
	public void agregarAListaCalidadAgua() {
		try {
			if(medioFisicoBean.getCalidadAgua().getResultado()>medioFisicoBean.getCalidadAgua().getLimite())
				medioFisicoBean.getCalidadAgua().setColor(COLOR);
			else
				medioFisicoBean.getCalidadAgua().setColor("");
			if (!medioFisicoBean.getCalidadAgua().isEditar())
				medioFisicoBean.getListaCalidadAgua().add(medioFisicoBean.getCalidadAgua());
			RequestContext.getCurrentInstance().addCallbackParam(
					"aguaIn", true);
		} catch (Exception e) {
			RequestContext.getCurrentInstance().addCallbackParam(
					"aguaIn", false);
		}
	}

	public void agregarCalidadAgua() {
		CalidadAgua calidadAgua= new CalidadAgua();
		calidadAgua.setEditar(false);
		calidadAgua.setEiaId(getEiaId());
		medioFisicoBean.setCalidadAgua(calidadAgua);
		medioFisicoBean.getCalidadAgua().setCoordenadaGeneral(new CoordenadaGeneral());
		SelectItem item=((SelectItemGroup) medioFisicoBean.getParametrosCalidad().get(0)).getSelectItems()[0];
		medioFisicoBean.getCalidadAgua().setParametro((CatalogoGeneral) item.getValue());
		cargarUnidad();
	}
	public void seleccionarCalidadAgua(CalidadAgua calidadAgua) {
		calidadAgua.setEditar(true);
		medioFisicoBean.setCalidadAgua(calidadAgua);
	}

	public void eliminarCalidadAgua(CalidadAgua calidadAgua) {
			medioFisicoBean.getListaCalidadAgua().remove(calidadAgua);
			if (calidadAgua.getId() != null) {
				calidadAgua.setEstado(false);
				medioFisicoBean.getAguaBorradas().add(calidadAgua);
			}
		}
	
	public void limpiar(){
		medioFisicoBean.setListaQuimica(new ArrayList<QuimicaSuelo>());
	}
	
	public String cancelar() {
		medioFisicoBean.setListaCalidadAgua(new ArrayList<CalidadAgua>());
		return JsfUtil.actionNavigateTo("/pages/eia/lineaBase/calidadAgua.jsf");
	}
	
	public String  guardar(){
		try{
                    if(!medioFisicoBean.getListaCalidadAgua().isEmpty()){
                        EstudioImpactoAmbiental es = new EstudioImpactoAmbiental(getEiaId());
                        Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
                        calidadAguaFacade.guardar(medioFisicoBean.getListaCalidadAgua(),medioFisicoBean.getAguaBorradas(), es, mapOpciones.get(EiaOpciones.CALIDAD_AGUA_HIDRO));
                        JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
                        limpiar();
                        return JsfUtil.actionNavigateTo("/pages/eia/default.jsf");
                    }else{
                        JsfUtil.addMessageError("Debe ingresar registros");
                    }
		}catch(Exception ex){
			LOG.error("Error al guardar Calidad Agua",ex);
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO);
		}
	return null;
	}
	
	public void cargarParametros(){
			try {
				medioFisicoBean.setParametrosCalidad(new ArrayList<SelectItem>());
				SelectItemGroup grupo = null;
				List<TipoCatalogo> catalogosRaohe= tipoCatalogoFacade.obtenerTipoCatalogoXCodigo(TipoCatalogo.CODIGO_PARAMETRO_AGUA_RAOHE);
				List<TipoCatalogo> catalogosTulsma= tipoCatalogoFacade.obtenerTipoCatalogoXCodigo(TipoCatalogo.CODIGO_PARAMETRO_AGUA_TULSMA);
				List<TipoCatalogo> catalogos= new ArrayList<TipoCatalogo>();
				catalogos.addAll(catalogosRaohe);
				catalogos.addAll(catalogosTulsma);
				for (TipoCatalogo tipoCatalogo : catalogos) {
					grupo=new SelectItemGroup(tipoCatalogo.getTipo());
					List<CatalogoGeneral> parametros=catalogoGeneralFacade.obtenerCatalogoXTipo(tipoCatalogo.getId());
					SelectItem items[] = new SelectItem[parametros.size()];
					int i=0;
					for (CatalogoGeneral catalogoGeneral : parametros) {
						items[i] = new SelectItem(catalogoGeneral);
						i++;
					}
					grupo.setSelectItems(items);
					medioFisicoBean.getParametrosCalidad().add(grupo);
				}
				
				}
			catch (Exception e) {
				JsfUtil.addMessageError("Error al cargar paramétros de calidad de agua");
			}
	}
	
	public void cargarUnidad(){
		try{
		medioFisicoBean.getCalidadAgua().setUnidad(medioFisicoBean.getCalidadAgua().getParametro().getCodigo());
		medioFisicoBean.getCalidadAgua().setLimite(new Double(medioFisicoBean.getCalidadAgua().getParametro().getValor()));
		}catch(Exception ex){
			JsfUtil.addMessageError(PARAMETRO);
			LOG.error("Error cargar unidad", ex);
		}
	}
}
