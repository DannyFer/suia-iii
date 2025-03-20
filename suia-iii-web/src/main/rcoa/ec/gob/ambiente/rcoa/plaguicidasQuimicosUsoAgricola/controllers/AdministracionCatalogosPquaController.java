package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.CultivoFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.GrupoObjetivoFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.PlagaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.TemaCapacitacionFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.Cultivo;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.GrupoObjetivo;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.Plaga;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.TemaCapacitacion;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AdministracionCatalogosPquaController {
	
	@ManagedProperty(value = "#{administrarRegistroPgiBean}")
	@Getter
	@Setter
	private AdministrarRegistroPgiBean administrarRegistroPgiBean;
	
	@ManagedProperty(value = "#{administrarProductoSgaPmaBean}")
	@Getter
	@Setter
	private AdministrarProductoSgaPmaBean administrarProductoSgaPmaBean;
	
	@EJB
	private CultivoFacade cultivoFacade;
	@EJB
	private PlagaFacade plagaFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	@EJB
    private TemaCapacitacionFacade temaCapacitacionFacade;
	@EJB
    private GrupoObjetivoFacade grupoObjetivoFacade;
	
	@Getter
	@Setter
	private List<Cultivo> listaCultivos;
	@Getter
	@Setter
	private List<Plaga> listaPlagas;
	@Getter
	@Setter
	private List<CatalogoGeneralCoa> listaTipoCultivo, listaFamiliaCultivo;
	@Getter
	@Setter
	private List<TemaCapacitacion> listaTemasCapacitacion;
	@Getter
	@Setter
	private List<GrupoObjetivo> listaGrupoObjetivo;
	
	@Getter
	@Setter
	private Cultivo cultivo;
	@Getter
	@Setter
	private Plaga plaga;
	@Getter
	@Setter
	private TemaCapacitacion temaSeleccionado;
	@Getter
	@Setter
	private GrupoObjetivo grupoSeleccionado;
	
	@Getter
	@Setter
	private String headerCultivo, headerPlaga;
	
	@Getter
	@Setter
	private Integer indexTab, indexTabSec;
	
	@PostConstruct
	public void init(){
		
	}
	
	public void onTabChangeContent(TabChangeEvent event) {
		try {
			String idTab = event.getTab().getId();
			switch (idTab) {
			case "tabCultivos":
				cargarCultivos();
				break;
			case "tabPlagas":
				cargarPlagas();
				break;
			case "tabRegistroPGI":
				administrarRegistroPgiBean.cargarRegistroPgi();
				break;
			case "tabCapacitacion":
				cargarTemasCapacitacion();
				break;
			case "tabGrupoObjetivo":
				cargarGrupoObjetivo();
				break;
			case "tabSgaPma":
				administrarProductoSgaPmaBean.cargarProductosSgaPma();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void cargarCultivos(){
		try {
			cultivo = new Cultivo();
			
			listaCultivos = cultivoFacade.listaCultivos();
			
			listaTipoCultivo = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.PQUA_TIPO_CULTIVO);
			listaFamiliaCultivo = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.PQUA_FAMILIA_CULTIVO);
			
			RequestContext.getCurrentInstance().update(":form:tab:tabCultivos");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void agregarCultivo(){
		headerCultivo = "Adicionar Cultivo";
		this.cultivo = new Cultivo();
	}
	
	public void editarCultivo(Cultivo cultivo){
		headerCultivo = "Editar Cultivo";
		this.cultivo = cultivo;
	}
	
	public void limpiarCultivo() {
		cultivo = new Cultivo();
	}

	public void aceptarCultivo() {
		try {
			cultivoFacade.guardar(cultivo);
			
			listaCultivos = cultivoFacade.listaCultivos();
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void eliminarCultivo(Cultivo cultivo){
		cultivo.setEstado(false);
		cultivoFacade.guardar(cultivo);
		
		listaCultivos = cultivoFacade.listaCultivos();
	}
	
	
	public void cargarPlagas(){
		try {
			plaga = new Plaga();
			
			listaPlagas = plagaFacade.listaPlagas();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void agregarPlaga(){
		headerPlaga = "Adicionar Plaga";
		this.plaga = new Plaga();
	}
	
	public void editarPlaga(Plaga plaga){
		headerPlaga = "Editar Plaga";
		this.plaga = plaga;
	}
	
	public void limpiarPlaga() {
		plaga = new Plaga();
	}

	public void aceptarPlaga() {
		try {
			plagaFacade.guardar(plaga);
			
			listaPlagas = plagaFacade.listaPlagas();
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void eliminarPlaga(Plaga plaga){
		plaga.setEstado(false);
		plagaFacade.guardar(plaga);
		
		listaPlagas = plagaFacade.listaPlagas();
	}
	
	
	public void cargarTemasCapacitacion(){
		try {
			listaTemasCapacitacion = temaCapacitacionFacade.listaTemasPadre();

			indexTab = -1;
			indexTabSec = -1;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setIndiceTab(TemaCapacitacion temaPadre, Integer nivel) {
		indexTab = -1;
		indexTabSec = -1;
		
		if(nivel.equals(1)) {
			setIndiceTabActivo(temaPadre);
		} else {
			setIndiceTabActivoSecundario(temaPadre);
			setIndiceTabActivo(temaPadre.getTemaPadre());
		}
	}
	public void setIndiceTabActivo(TemaCapacitacion tema) {
		int index = -1;
		for (TemaCapacitacion item : listaTemasCapacitacion) {
			index += 1;
			if(item.getId().equals(tema.getId())) {
				break;
			}
		}
		indexTab = index;
	}
	public void setIndiceTabActivoSecundario(TemaCapacitacion tema) {
		int index = -1;
		for (TemaCapacitacion item : tema.getTemaPadre().getTemas()) {
			index += 1;
			if(item.getId().equals(tema.getId())) {
				break;
			}
		}
		indexTabSec = index;
	}
	
	public void agregarTema(TemaCapacitacion temaPadre, Integer nivel) {
		setIndiceTab(temaPadre, nivel);
		
		temaSeleccionado = new TemaCapacitacion();
		temaSeleccionado.setTemaPadre(temaPadre);
		temaSeleccionado.setNodoFinal(true);
	}
	
	public void editarTema(TemaCapacitacion tema, Integer nivel) {
		setIndiceTab(tema.getTemaPadre(), nivel);
		
		temaSeleccionado = tema;
	}
	
	public void eliminarTema(TemaCapacitacion tema) {
		try {
			tema.setEstado(false);
			temaCapacitacionFacade.guardar(tema);
			
			listaTemasCapacitacion = temaCapacitacionFacade.listaTemasPadre();
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void limpiarTema() {
		temaSeleccionado = new TemaCapacitacion();
	}

	public void aceptarTema() {
		try {
			
			temaCapacitacionFacade.guardar(temaSeleccionado);
			
			listaTemasCapacitacion = temaCapacitacionFacade.listaTemasPadre();

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
			JsfUtil.addCallbackParam("addTema");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	
	public void cargarGrupoObjetivo(){
		try {
			listaGrupoObjetivo = grupoObjetivoFacade.listaGruposActivos();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void agregarGrupo() {
		grupoSeleccionado = new GrupoObjetivo();
	}
	
	public void editarGrupo(GrupoObjetivo grupo) {
		grupoSeleccionado = grupo;
	}
	
	public void eliminarGrupo(GrupoObjetivo grupo) {
		try {
			grupo.setEstado(false);
			grupoObjetivoFacade.guardar(grupo);
			
			listaGrupoObjetivo = grupoObjetivoFacade.listaGruposActivos();
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void limpiarGrupo() {
		grupoSeleccionado = new GrupoObjetivo();
	}

	public void aceptarGrupo() {
		try {
			
			grupoObjetivoFacade.guardar(grupoSeleccionado);
			
			listaGrupoObjetivo = grupoObjetivoFacade.listaGruposActivos();

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
			JsfUtil.addCallbackParam("addGrupo");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
}
