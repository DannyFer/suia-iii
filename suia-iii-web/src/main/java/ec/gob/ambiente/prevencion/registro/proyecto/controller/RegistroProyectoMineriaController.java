package ec.gob.ambiente.prevencion.registro.proyecto.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.AreaConcesionadaBean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.AreaLibreBean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.ConcesionesBean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.MinerosArtesanalesBean;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.base.RegistroProyectoController;
import ec.gob.ambiente.suia.comun.bean.CargarCoordenadasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ConcesionMinera;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentoProyecto;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.MineroArtesanal;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoMineroArtesanal;
import ec.gob.ambiente.suia.domain.SistemasIntegrales;
import ec.gob.ambiente.suia.domain.TipoMaterial;
import ec.gob.ambiente.suia.domain.TipoPoblacion;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.sistemaIntegrado.facade.SistemaIntegradoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RegistroProyectoMineriaController extends RegistroProyectoController {

	private static final long serialVersionUID = -347059525729815624L;

	@EJB
    private SistemaIntegradoFacade sistemaIntegradoFacade;
    
    @EJB
    private CrudServiceBean crudServiceBean;

    @Getter
    @Setter
    private List<SistemasIntegrales>listSistemasIntegrales= new ArrayList<SistemasIntegrales>();
    
    @Getter
    @Setter
    private SistemasIntegrales sistemasIntegrales= new SistemasIntegrales();
    
    @Getter
    @Setter
    private boolean sistIntegrados;
    
    @Getter
    @Setter
	public boolean requerido=false;
    
    @Getter
    @Setter
	public String etiqueta;
    
    @Getter
    @Setter
	public boolean requeridoetiqueta=false;
    
    
    @Getter
    @Setter
    private String descripcionSistIntegrados;
	
	@Setter
	@ManagedProperty(value = "#{concesionesBean}")
	private ConcesionesBean concesionesBean;

	@Setter
	@ManagedProperty(value = "#{minerosArtesanalesBean}")
	private MinerosArtesanalesBean minerosArtesanalesBean;

	@Setter
	@ManagedProperty(value = "#{areaConcesionadaBean}")
	private AreaConcesionadaBean areaConcesionadaBean;

	@Setter
	@ManagedProperty(value = "#{areaLibreBean}")
	private AreaLibreBean areaLibreBean;
	
	public static final String[] MINERIA_ARTESANAL_O_LIBRE_APROVECHAMIENTO = new String[] { "21.02.06", "21.02.01.01","21.02.01.02", "21.02.06.01", "21.02.06.02" ,"21.02.06.03" };
	public static final String[] MINERIA_ARTESANAL_O_LIBRE_APROVECHAMIENTO_OTRO = new String[] { "21.02.06.03" };
	public static final String[] MINERIA_ARTESANAL = new String[] { "21.02.01.01","21.02.01.02" };
	public static final String[] MINERIA_LIBRE_APROVECHAMIENTO = new String[] { "21.02.06","21.02.01.02" ,"21.02.06.02","21.02.06.01"};//
	public static final String[] MINERIA_LIBRE_APROVECHAMIENTO_OTRO = new String[] { "21.02.01.02"};
	public static final String[] MINERIA_EXPLORACION_INICIAL = new String[] { "21.02.02.01","21.02.02.02","21.02.03.06"};
	public static final String[] MINERIA_EXPLORACION_EXPLOTAR = new String[] { "21.02.06.01","21.02.06.02","21.02.06.03","21.02.08.01"};//
	public static final String[] MINERIA_EXPLORACION_EXPLOTAR_TOTAL = new String[] { "21.02.04.01","21.02.05.01"};
	public static final String[] MINERIA_EXPLORACION_INICIAL_BENEFICIO = new String[] { "21.02.03.03","21.02.04.02","21.02.05.02","21.02.03.04" };
	public static final String[] MINERIA_EXPLORACION_RELAVES = new String[] { "21.02.07.01"};	
	public static final String[] MINERIA_EXPLORACION_RELAVESDEPOSITAR = new String[] { "21.02.07.02"};
	public static final String[] MINERIA_EXPLORACION_INICIAL_SONDEO = new String[] { "21.02.02.03", "21.02.03.05"};
	private static final String NOMBRE_CARPETA_EXPLORACION_INICIAL_SONDEO = "Exploracion_inicial_sondeo";
	
	private List<TipoMaterial> tiposMateriales;

	@PostConstruct
	private void initMineria() {
		if (registroProyectoBean.isEdicion()) {
			/*registroProyectoBean.setMostrarUbicacionGeografica(!registroProyectoBean.getProyecto()
					.isConcesionesMinerasMultiples());*/
			if (registroProyectoBean.getProyecto().isConcesionesMinerasMultiples()) {
				concesionesBean.setConcesionesMineras(new ArrayList<ConcesionMinera>(registroProyectoBean.getProyecto()
						.getConcesionesMineras()));
				concesionesBean.setHabilitarConcesion(concesionesBean.getConcesionesMineras().size()>0?false:true);
				JsfUtil.getBean(ConcesionesBean.class).editarConcesionesColindantes();
				procesarMultiplesConcesiones();
			}

			if (registroProyectoBean.getProyecto().isMinerosArtesanales()) {
				for (ProyectoMineroArtesanal proyectoMineroArtesanal : registroProyectoBean.getProyecto()
						.getProyectoMinerosArtesanales()) {
					minerosArtesanalesBean.getMinerosArtesanales().add(proyectoMineroArtesanal.getMineroArtesanal());
				}
			}

			if (isMineriaArtesanalOLibreAprovechamiento()
					&& registroProyectoBean.getProyecto().isMineriaAreasConcesionadas()) {
				areaConcesionadaBean.setConcesionMinera(registroProyectoBean.getProyecto().getConcesionMinera());
				areaConcesionadaBean.setMineroArtesanal(registroProyectoBean.getProyecto().getMineroArtesanal());
			}

			if (isMineriaArtesanalOLibreAprovechamiento()
					&& !registroProyectoBean.getProyecto().isMineriaAreasConcesionadas()) {
				areaLibreBean.setMineroArtesanal(registroProyectoBean.getProyecto().getMineroArtesanal());
			}
			
			if (isMineriaExploracionInicialSondeo()) {
				List<DocumentoProyecto> listaDocumentos = documentosFacade.getDocumentosPorIdProyecto(registroProyectoBean.getProyecto().getId());
				for (DocumentoProyecto documentoProyecto : listaDocumentos) {
					if (documentoProyecto.getDocumento().getTipoDocumento().getId() == TipoDocumentoSistema.DERECHO_MINERO.getIdTipoDocumento()) {
						catalogoActividadesBean.setDocumentName(documentoProyecto.getDocumento().getNombre());
					}
				}
			}
		}
		if(registroProyectoBean.getProyecto().getCodigo()!=null){
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("p_proyecto", registroProyectoBean.getProyecto());

            List<SistemasIntegrales> lista = crudServiceBean.findByNamedQueryGeneric(sistemasIntegrales.GET_SISTEMAS_INTEGRALES_PROYECTO,parametros);
            
                if (lista.size() > 0) {
                    listSistemasIntegrales=lista;
                    sistIntegrados=true;
                }
        }
	}

	public void guardarProyecto() throws CmisAlfrescoException {
		if(adicionarUbicacionesBean.getListParroquiasGuardar().size()>0){
			//ubicaciones = adicionarUbicacionesBean.getListParroquiasGuardar();
			adicionarUbicacionesBean.setUbicacionesSeleccionadas(adicionarUbicacionesBean.getListParroquiasGuardar());
		}
		List<UbicacionesGeografica> ubicaciones = adicionarUbicacionesBean.getUbicacionesSeleccionadas();
		UbicacionesGeografica ubicacionEnteMunicipio= new UbicacionesGeografica();
		for (UbicacionesGeografica ubicacionesGeografica : ubicaciones) {
			if(ubicacionesGeografica.getEnteAcreditadomunicipio()!=null){
				ubicacionEnteMunicipio=ubicacionesGeografica;
				break;
			}
		}
		
		List<ConcesionMinera> concesionesMineras = null;
		List<MineroArtesanal> minerosArtesanales = null;

		if (registroProyectoBean.getProyecto().isConcesionesMinerasMultiples()) {
			//ubicaciones = null;//
			concesionesMineras = concesionesBean.getConcesionesMineras();
		}

		if (registroProyectoBean.getProyecto().isMinerosArtesanales()) {
			minerosArtesanales = minerosArtesanalesBean.getMinerosArtesanales();
		}

		if (isMineriaArtesanalOLibreAprovechamiento()
				|| (!isMineriaArtesanalOLibreAprovechamiento() && registroProyectoBean.getProyecto()
						.isConcesionesMinerasMultiples()))
			registroProyectoBean.getProyecto().setCodigoMinero(null);

		if (isMineriaArtesanalOLibreAprovechamiento()
				&& registroProyectoBean.getProyecto().isMineriaAreasConcesionadas()) {
			minerosArtesanales = new ArrayList<MineroArtesanal>();
			areaConcesionadaBean.getMineroArtesanal().setUsuario(JsfUtil.getBean(LoginBean.class).getUsuario());
			minerosArtesanales.add(areaConcesionadaBean.getMineroArtesanal());
			concesionesMineras = new ArrayList<ConcesionMinera>();
			concesionesMineras.add(areaConcesionadaBean.getConcesionMinera());
		}

		if (isMineriaArtesanalOLibreAprovechamiento()
				&& !registroProyectoBean.getProyecto().isMineriaAreasConcesionadas()) {
			minerosArtesanales = new ArrayList<MineroArtesanal>();
			areaLibreBean.getMineroArtesanal().setUsuario(JsfUtil.getBean(LoginBean.class).getUsuario());
			minerosArtesanales.add(areaLibreBean.getMineroArtesanal());
		}
		registroProyectoBean.getProyecto().setTipoSector(proyectoFacade.getTipoSector(TipoSector.TIPO_SECTOR_MINERIA));
		
		if (!(catalogoActividadesBean.getActividadSistemaSeleccionada().getTipoArea().getId()==3 && (catalogoActividadesBean.getActividadSistemaSeleccionada().getCodigo().equals("21.02.08.01")
				|| catalogoActividadesBean.getActividadSistemaSeleccionada().getCodigo().equals("21.02.01.02")) &&
				(ubicacionEnteMunicipio!=null && ubicacionEnteMunicipio.getId()==663))){
			
			proyectoFacade.guardar(registroProyectoBean.getProyecto(),
			catalogoActividadesBean.getActividadSistemaSeleccionada(), ubicaciones, null, null, concesionesMineras,
			minerosArtesanales, cargarCoordenadasBean.getCoordinatesWrappers(),
			cargarCoordenadasBean.generateDocumentFromUpload(),
			registroProyectoBean.getFasesDesechosSeleccionadas(), desechoPeligrosoBean.getDesechosSeleccionados(),
			registroProyectoBean.getSustanciasQuimicasSeleccionadas(),JsfUtil.getLoggedUser().getNombre());
			registroProyectoBean.getProyecto().setFormato(cargarCoordenadasBean.getFormato());
			
			if (isMineriaExploracionInicialSondeo()) {
				try {
					subirFileAlfresco(catalogoActividadesBean.getDocumento(),registroProyectoBean.getProyecto(),TipoDocumentoSistema.DERECHO_MINERO);
				} catch (CmisAlfrescoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if(listSistemasIntegrales.size()>0){

				int i=0;
				int secuenciaIntSist=0;
				secuenciaIntSist=sistemaIntegradoFacade.obtenerSecuencia("integrated_systems_seq", "suia_iii").intValue();

				for(SistemasIntegrales sistemasIntegrales :  listSistemasIntegrales){
					if(listSistemasIntegrales.get(i).getId()==null){
						SistemasIntegrales integrales= new SistemasIntegrales();
						if(listSistemasIntegrales.get(0).getCodigo()!=null){
							integrales.setCodigo(listSistemasIntegrales.get(0).getCodigo());
						}else {
							integrales.setCodigo("PINT-"+secuenciaIntSist);    
						}
						integrales.setProyectoLicenciamientoAmbiental(registroProyectoBean.getProyecto());
						integrales.setDescripcion(sistemasIntegrales.getDescripcion());
						integrales.setSeleccionado(sistemasIntegrales.isSeleccionado());
						integrales.setEstado(true);
						sistemaIntegradoFacade.guadarSistemasIntegrados(integrales);
					}else{
						sistemasIntegrales.setProyectoLicenciamientoAmbiental(registroProyectoBean.getProyecto());
						if(sistemasIntegrales.getProyectoLicenciamientoAmbiental().getId()!=null){
							sistemasIntegrales.setCodigo(listSistemasIntegrales.get(0).getCodigo());
						}
						sistemaIntegradoFacade.guadarSistemasIntegrados(sistemasIntegrales);
					}
					i++;
				}        
			}
		}
		else
			JsfUtil.addMessageError("No se puede registrar el proyecto");
	}

	public void adicionarValidaciones(List<String> errors) {
	}

	public List<String> validar() {
		List<String> messages = new ArrayList<String>();
		if (registroProyectoBean.getProyecto().isConcesionesMinerasMultiples()
				&& concesionesBean.getConcesionesMineras().isEmpty()) {
			messages.add("No se han definido las concesiones mineras para este proyecto.");
		} else if (registroProyectoBean.getProyecto().isConcesionesMinerasMultiples()
				&& concesionesBean.getConcesionesMineras().size() < 2) {
			messages.add("Si declara que su proyecto tiene varias concesiones mineras, al menos debe ingresar 2.");
		} else if (isMineriaArtesanalOLibreAprovechamiento()) {
			if (registroProyectoBean.getProyecto().isMineriaAreasConcesionadas()
					&& !areaConcesionadaBean.isDatosValidos())
				messages.add("No se han definido los datos asociados al área concesionada para este proyecto.");
			else if (!registroProyectoBean.getProyecto().isMineriaAreasConcesionadas() && !areaLibreBean.isValid())
				messages.add("No se han definido los datos asociados al área libre para este proyecto.");
		} else if (registroProyectoBean.getProyecto().isMinerosArtesanales()
				&& minerosArtesanalesBean.getMinerosArtesanales().isEmpty())
			messages.add("No se han definido mineros artesanales para este proyecto.");
		return messages;
	}

	public List<String> validarNegocio() {
		return null;
	}

	public void procesarMultiplesConcesiones() {
		/*registroProyectoBean.setMostrarUbicacionGeografica(!registroProyectoBean.getProyecto()
				.isConcesionesMinerasMultiples());*/
		registroProyectoBean.setMostrarAreaAltitud(!registroProyectoBean.getProyecto().isConcesionesMinerasMultiples());
	}

	public boolean isMineriaArtesanalOLibreAprovechamiento() {
		boolean result = isCatalogoCategoriaCodigoInicia(MINERIA_ARTESANAL_O_LIBRE_APROVECHAMIENTO);
		return result;
	}
	
	public boolean isMineriaExploracionInicialSondeo() {
		boolean result = isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_INICIAL_SONDEO);
		return result;
	}

	public boolean isMineriaLibreAprovechamientoProyectoExPost() {
		boolean result = ((isCatalogoCategoriaCodigoInicia(MINERIA_LIBRE_APROVECHAMIENTO) && isEstudioExPost())|| isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_EXPLOTAR));
		return result;
	}
	
	public boolean  isEtiqueta() {
		String etiqueta =getLabelRegistroMineroArtesanalMRNNRMineriaAreaConcesionada();
		boolean valor =false;
		if (etiqueta.contains("Autorización para libre aprovechamiento Ministerio de Minas(.pdf)")){
			valor=true;
		}else{
			valor=false;
		}
		return valor;
	}
	public boolean  isEtiquetalibre() {
		String etiqueta =getLabelRegistroMineroArtesanalMRNNRMineriaAreaConcesionada();
		boolean valor =false;
		if (etiqueta.contains("Registro de minero artesanal MRNNR(.pdf)")){
			valor=true;
		}else{
			valor=false;
		}
		return valor;
	}

	@Override
	public List<TipoPoblacion> getTiposPoblaciones() {
		if (isCatalogoCategoriaCodigoInicia(MINERIA_ARTESANAL)) {
			if (registroProyectoBean.getTiposPoblaciones().contains(
					getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_MARITIMA)))
				registroProyectoBean.getTiposPoblaciones().remove(
						getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_MARITIMA));
			registroProyectoBean.getTiposPoblaciones().remove(
					getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_CONTINENTAL));
			registroProyectoBean.getTiposPoblaciones().remove(
					getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_NO_CONTINENTAL));
			registroProyectoBean.getTiposPoblaciones().remove(
					getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_MIXTO));
			return registroProyectoBean.getTiposPoblaciones();
		}
		return super.getTiposPoblaciones();
	}

	public String getLabelMateriales() {
		return isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_INICIAL) ? "Material a explorar"
				:isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_INICIAL_BENEFICIO) ?"Material a beneficiar"
						:isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_RELAVES)? "Material Transportado" 
								:isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_EXPLOTAR)? "Material a explotar" 
										:isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_EXPLOTAR_TOTAL)? "Material a explotar" 
										:isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_RELAVESDEPOSITAR)?"Material a Depositar":"Material a explorar/explotar";	
	}	

	public List<TipoMaterial> getTiposMateriales() {
		tiposMateriales = tiposMateriales == null ? tiposMateriales = new ArrayList<TipoMaterial>() : tiposMateriales;
		tiposMateriales.clear();
		tiposMateriales.addAll(registroProyectoBean.getTiposMateriales());
		Integer lista=0;
		if (isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_INICIAL)) {
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_CONSTRUCCION));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_RELAVESCONCENTRADO));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_RELAVES));
			lista=1;
		}
		if (isCatalogoCategoriaCodigoInicia(MINERIA_LIBRE_APROVECHAMIENTO) && isEstudioExPost()) {
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_METALICO));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_NO_METALICO));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_RELAVESCONCENTRADO));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_RELAVES));
			lista=1;
		}
		if (isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_RELAVES)) {
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_METALICO));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_NO_METALICO));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_CONSTRUCCION));
			lista=1;
		}
		if (isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_RELAVESDEPOSITAR)) {
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_METALICO));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_NO_METALICO));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_CONSTRUCCION));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_RELAVESCONCENTRADO));
			lista=1;
		}
		
		if (isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_EXPLOTAR)) {
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_RELAVESCONCENTRADO));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_RELAVES));
//			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_METALICO));
//			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_NO_METALICO));			
			lista=1;
		}
		
		if (isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_EXPLOTAR_TOTAL)) {			
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_RELAVESCONCENTRADO));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_RELAVES));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_CONSTRUCCION));
			lista=1;
		}
		
		if (lista==0) {
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_RELAVESCONCENTRADO));
			tiposMateriales.remove(new TipoMaterial(TipoMaterial.MATERIAL_RELAVES));
			
		}
		return tiposMateriales;
	}

	public String getLabelCodigoMineroMineriaAreaConcesionada() {
		String label = "Código del minero artesanal";//21.02.06.02
		if (isCatalogoCategoriaCodigoInicia(MINERIA_ARTESANAL_O_LIBRE_APROVECHAMIENTO_OTRO))
			label= "Código para libre aprovechamiento";
		if (isCatalogoCategoriaCodigoInicia(MINERIA_ARTESANAL)) 
			label = "Código de la labor de minería artesanal";
		else if (isCatalogoCategoriaCodigoInicia(MINERIA_LIBRE_APROVECHAMIENTO) && isEstudioExPost())
			label = "Código para libre aprovechamiento";
		else if (isCatalogoCategoriaCodigoInicia(MINERIA_LIBRE_APROVECHAMIENTO))
			label = "Código para libre aprovechamiento";
		return label;
	}

	public String getLabelCodigoMineroMineriaAreaLibre() {
		String label = "Código del minero artesanal"; //21.02.06.02
		if (isCatalogoCategoriaCodigoInicia(MINERIA_ARTESANAL_O_LIBRE_APROVECHAMIENTO_OTRO))
			label= "Código para libre aprovechamiento";
		else if (isCatalogoCategoriaCodigoInicia(MINERIA_LIBRE_APROVECHAMIENTO_OTRO))
			label = "Código de la labor de minería artesanal";
		else 
			if (isCatalogoCategoriaCodigoInicia(MINERIA_LIBRE_APROVECHAMIENTO) && (isEstudioExAnte()|| isEstudioExPost()))
			label = "Código del área para libre aprovechamiento";
		return label;
	}

	public String getLabelRegistroMineroArtesanalMRNNRMineriaAreaConcesionada() {
		String label = "Registro de minero artesanal MRNNR(.pdf) *";	
		if (isCatalogoCategoriaCodigoInicia(MINERIA_LIBRE_APROVECHAMIENTO_OTRO)){
			label = "Registro de minero artesanal MRNNR(.pdf) *";
		}else //21.02.06.02
		if ((isCatalogoCategoriaCodigoInicia(MINERIA_LIBRE_APROVECHAMIENTO) || (isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_EXPLOTAR))) && (isEstudioExPost() || isEstudioExAnte() )){
			label = "Autorización para libre aprovechamiento Ministerio de Minas(.pdf) *";
		}else if (isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_EXPLOTAR)){
			label = "Registro de libre aprovechamiento(.pdf)";		}
//		if (label.equals("Registro de minero artesanal MRNNR(.pdf) *") || label.equals("Autorización para libre aprovechamiento Ministerio de Minas(.pdf) *")){
//			setEtiqueta(label);
//			isRequeridoetiqueta();
//			setRequeridoetiqueta(true);
//		}		else{
//			setRequeridoetiqueta(false);
//		}
		return label;
	}
	

	public String getLabelRegistroMineroArtesanalMRNNRMineriaAreaLibre() {
		String label = "Registro de minero artesanal MRNNR(.pdf)";
		if (isCatalogoCategoriaCodigoInicia(MINERIA_LIBRE_APROVECHAMIENTO_OTRO)){
			label = "Registro de minero artesanal MRNNR(.pdf)";
		} else 
			if ((isCatalogoCategoriaCodigoInicia(MINERIA_LIBRE_APROVECHAMIENTO) || (isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_EXPLOTAR))) && (isEstudioExAnte()|| isEstudioExPost())){
		isRequerido();
		setRequerido(true);
			label = "Autorización para libre aprovechamiento Ministerio de Minería(.pdf)*";
			setRequerido(true);
	}
		return label;
	}
	
	public void cargarLista() {
		sistemasIntegrales.setDescripcion(descripcionSistIntegrados);
		sistemasIntegrales.setSeleccionado(sistIntegrados);
		listSistemasIntegrales.add(sistemasIntegrales);
		sistemasIntegrales = new SistemasIntegrales();
		descripcionSistIntegrados = "";
	}

	public void quitarActividadLista(SistemasIntegrales sistemasIntegrales) {
		if (listSistemasIntegrales.size() > 0) {
			if (sistemasIntegrales.getId() != null) {
				sistemaIntegradoFacade
						.eliminarSistemaIntegrado(sistemasIntegrales);
			}
			listSistemasIntegrales.remove(sistemasIntegrales);
		}
	}
	
	private Documento subirFileAlfresco(Documento documento,ProyectoLicenciamientoAmbiental proyecto, TipoDocumentoSistema tipo) throws Exception, CmisAlfrescoException {
		DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		documentoTarea.setIdTarea(0L);
		documento.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
		documento.setIdTable(proyecto.getId());
		documentoTarea.setProcessInstanceId(0L);
		documentoTarea.setDocumento(documento);
		return documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigo(),
				NOMBRE_CARPETA_EXPLORACION_INICIAL_SONDEO, 0L, documento, tipo,
				documentoTarea);

	}
}
