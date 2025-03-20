package ec.gob.ambiente.suia.eia.mediofisico.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.mediofisico.facade.IdentificacionSitiosContaminadosFuentesContaminacionFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;

@ManagedBean
@ViewScoped
public class MedioFisicoBean implements Serializable {

	private static final long serialVersionUID = 6168855739870999426L;
	@Getter
	@Setter
	private List<CuerpoHidrico> listaCuerpoHidricos;
	@Getter
	@Setter
	private List<CuerpoHidrico> cuerpoHidricosBorradas;

	@Getter
	@Setter
	private CuerpoHidrico cuerpoHidrico;

	@Getter
	@Setter
	private List<FisicoMecanicaSuelo> listaFisicoMecanica;

	@Getter
	@Setter
	private EstudioImpactoAmbiental estudio;

	@Getter
	@Setter
	private List<FisicoMecanicaSuelo> fisicoMecanicaBorradas;
	@Getter
	@Setter
	private FisicoMecanicaSuelo fisicoMecanica;

	@Getter
	@Setter
	private List<Ruido> listaRuido;
	@Getter
	@Setter
	private List<Ruido> ruidoBorradas;

	@Getter
	@Setter
	private Ruido ruido;
	@Getter
	@Setter
	List<CatalogoGeneral> listaCatalogoGeneral;
	@EJB
	private CatalogoGeneralFacade catalogoGeneralFacade;
	@Getter
	@Setter
	private Integer eiaId;
	@Getter
	@Setter
	private Documento documento;
	@Getter
	@Setter
	private boolean visibleDocumento;
	@Getter
	@Setter
	private StreamedContent file;
	@Getter
	@Setter
	private DocumentosTareasProceso documentoTarea;
	@Getter
	@Setter
	private Radiacion radiacion;
	@Getter
	@Setter
	private List<Radiacion> listaRadiaciones;
	@Getter
	@Setter
	List<Radiacion> radiacionBorradas;
	@Getter
	@Setter
	private QuimicaSuelo quimicaSuelo;
	@Getter
	@Setter
	private List<QuimicaSuelo> listaQuimica;
	@Getter
	@Setter
	private List<QuimicaSuelo> quimicaBorradas;
	@Getter
	@Setter
	private CalidadAgua calidadAgua;
	@Getter
	@Setter
	private List<CalidadAgua> listaCalidadAgua;
	@Getter
	@Setter
	private List<CalidadAgua> aguaBorradas;
	@Getter
	@Setter
	private CalidadAire calidadAire;
	@Getter
	@Setter
	private List<CalidadAire> listaCalidadAire;
	@Getter
	@Setter
	private List<CalidadAire> aireBorradas;
	@Getter
	@Setter
	private List<CatalogoGeneral> normativas;
	@Getter
	@Setter
	private List<CatalogoGeneral> parametros;
	@Getter
	@Setter
	private List<CatalogoGeneral> laboratorios;
	@Getter
	@Setter
	private List<CatalogoGeneral> usosSuelo;
	@Getter
	@Setter
	private String unidad;

	@Getter
	@Setter
	private String justificacionSitiosContaminados;

	@Getter
	@Setter
	private List<UbicacionesGeografica> provincias;
	@Getter
	@Setter
	private List<UbicacionesGeografica> cantones;
	@Getter
	@Setter
	private List<UbicacionesGeografica> parroquias;
	@Getter
	@Setter
	private String idParroquia;

	@Getter
	@Setter
	private IdentificacionSitiosContaminadosFuentesContaminacion identificacionSitiosContaminadosFuentesContaminacion;
	@Getter
	@Setter
	private List<IdentificacionSitiosContaminadosFuentesContaminacion> listaIdentificacionSitiosContaminadosFuentesContaminacion;
	@Getter
	@Setter
	private List<IdentificacionSitiosContaminadosFuentesContaminacion> identificacionSCFCBorrados;


	@Getter
	@Setter
	private List<SelectItem> parametrosCalidad;

	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
	@EJB
	private IdentificacionSitiosContaminadosFuentesContaminacionFacade identificacionSitiosContaminadosFuentesContaminacionFacade;



	@PostConstruct
	public void iniciar() {

		estudio = estudioImpactoAmbientalFacade.obtenerEIAPorId(((EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT)).getId());
		if(estudio.getTieneIdentificacionSitiosContaminados()==null){
			estudio.setTieneIdentificacionSitiosContaminados(false);
		}
		identificacionSitiosContaminadosFuentesContaminacion  = new IdentificacionSitiosContaminadosFuentesContaminacion();
		identificacionSitiosContaminadosFuentesContaminacion.setEist_id(estudio.getId());


		listaCuerpoHidricos = new ArrayList<CuerpoHidrico>();
		listaFisicoMecanica= new ArrayList<FisicoMecanicaSuelo>();
		listaRuido= new ArrayList<Ruido>();
		listaRadiaciones= new ArrayList<Radiacion>();
		listaQuimica= new ArrayList<QuimicaSuelo>();
		listaCalidadAgua= new ArrayList<CalidadAgua>();
		listaCalidadAire=new ArrayList<CalidadAire>();
		listaIdentificacionSitiosContaminadosFuentesContaminacion=identificacionSitiosContaminadosFuentesContaminacionFacade.getIdentificacionesSitiosContaminadosSuentesContaminacion(estudio);
		justificacionSitiosContaminados = estudio.getJustificacionIdentificacionSitiosContaminados();
		documento= new Documento();
		try {
			listaCatalogoGeneral=this.catalogoGeneralFacade.obtenerCatalogoXTipo(30);
		} catch (Exception e) {
			e.getMessage();
		}
	}
	public void limpiar() {
		identificacionSitiosContaminadosFuentesContaminacion  = new IdentificacionSitiosContaminadosFuentesContaminacion();
		identificacionSitiosContaminadosFuentesContaminacion.setEist_id(estudio.getId());
	}

	public void vaciarListaIdentificacionSitiosContaminadosFuentesContaminacion() {
		listaIdentificacionSitiosContaminadosFuentesContaminacion=identificacionSitiosContaminadosFuentesContaminacionFacade.getIdentificacionesSitiosContaminadosSuentesContaminacion(estudio);
		justificacionSitiosContaminados = estudio.getJustificacionIdentificacionSitiosContaminados();
	}



}
