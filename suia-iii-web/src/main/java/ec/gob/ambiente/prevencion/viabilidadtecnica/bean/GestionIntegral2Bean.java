package ec.gob.ambiente.prevencion.viabilidadtecnica.bean;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.domain.GestionIntegral2;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaMaterialesDiagnostico_2;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaTipoMateriales;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.GestionIntegral2Facade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class GestionIntegral2Bean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger
			.getLogger(GestionIntegral2Bean.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private GestionIntegral2Facade gestionIntegral2Facade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	private File imagenFotografica;

	@Getter
	@Setter
	private int idProyecto;

	@Getter
	private File pronunciamiento;

	@Getter
	@Setter
	private boolean subido = false;

	@Getter
	private Integer valorAdjunto;

	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Setter
	private GestionIntegral2 gestionIntegral2;

	@Getter
	@Setter
	private ViabilidadTecnicaTipoMateriales viabilidadTecnicaTipoMateriales;

	@Getter
	@Setter
	private EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico;

	@Setter
	@Getter
	private ViabilidadTecnicaMaterialesDiagnostico_2 viabilidadTecnicaMaterialesDiagnostico_2;

	@Setter
	@Getter
	private EstudioViabilidadTecnica estudioViabilidadTecnica;

	@Setter
	@Getter
	private TipoDocumentoSistema tipoDocumentoSistema;

	@Getter
	@Setter
	private List<ViabilidadTecnicaTipoMateriales> listaViabilidadTecnicaTipoMateriales;

	@Getter
	@Setter
	private List<GestionIntegral2> listaGestionIntegral2;

	@Getter
	@Setter
	private Integer lnIdEstudioViabilidadTecnica;

	public GestionIntegral2 getGestionIntegral2() {
		return gestionIntegral2 == null ? gestionIntegral2 = new GestionIntegral2()
				: gestionIntegral2;
	}

	public ProyectoLicenciamientoAmbiental cargarProyecto(Integer id) {
		try {
			return proyectoLicenciamientoAmbientalFacade
					.buscarProyectosLicenciamientoAmbientalPorId(id);
		} catch (Exception e) {
			return null;
		}
	}

	public ProyectoLicenciamientoAmbiental getproyectoLicenciamientoAmbiental() {
		return proyectoLicenciamientoAmbiental == null ? proyectoLicenciamientoAmbiental = new ProyectoLicenciamientoAmbiental()
				: proyectoLicenciamientoAmbiental;
	}

	public void setValorAdjunto(Integer valor) {
		this.valorAdjunto = valor;

		System.out.println("valorAdjunto" + valor);
	}

	@PostConstruct
	public void init() {
		this.lnIdEstudioViabilidadTecnica = 41;
		gestionIntegral2 = new GestionIntegral2();
		if (lnIdEstudioViabilidadTecnica != null) {

			try {
				cargaGestionIntegral();				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		estudioViabilidadTecnicaDiagnostico = new EstudioViabilidadTecnicaDiagnostico();//
		viabilidadTecnicaTipoMateriales = new ViabilidadTecnicaTipoMateriales();//
		estudioViabilidadTecnica = new EstudioViabilidadTecnica();
		estudioViabilidadTecnica.setId(lnIdEstudioViabilidadTecnica);

		datosViabilidadTecnicaTipoMateriales();

	}

	public void guardarGestionIntegral() {
		try {
			gestionIntegral2Facade.guardarGestionIntegral2(getGestionIntegral2(), estudioViabilidadTecnica);
			guardaViabilidadTecnicaMaterialesDiagnostico();
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			JsfUtil.addMessageInfo("La información no ha sido salvada correctamente.");
			LOGGER.error("Error al guardar información", e);
		}
	}


	public void guardaViabilidadTecnicaMaterialesDiagnostico() {

		List<ViabilidadTecnicaMaterialesDiagnostico_2> lviabilidadTecnicaMaterialesDiagnosticos = new ArrayList<ViabilidadTecnicaMaterialesDiagnostico_2>();
		ViabilidadTecnicaMaterialesDiagnostico_2 vtmd = null;

		for (ViabilidadTecnicaTipoMateriales viabilidadTecnicaTipoMateriales : listaViabilidadTecnicaTipoMateriales) {

			vtmd = new ViabilidadTecnicaMaterialesDiagnostico_2();
			vtmd = gestionIntegral2Facade.cargaViabilidadTecnicaMaterialesDiagnostico(gestionIntegral2.getId(),	viabilidadTecnicaTipoMateriales.getId());
			vtmd.setValor(viabilidadTecnicaTipoMateriales.getDetalleNombre());
			vtmd.setViabilidadTecnicaTipoMateriales(viabilidadTecnicaTipoMateriales);
			vtmd.setGestionIntegral2(gestionIntegral2);
			vtmd.setEstado(true);

			try {
				gestionIntegral2Facade.guardaViabilidadTecnicaMaterialesDiagnostico(vtmd);
			} catch (Exception e) {
				JsfUtil.addMessageInfo("La información no ha sido salvada correctamente.");
				LOGGER.error("Error al guardar información", e);
			}
		}
	}

	public void cargaViabilidadTecnicaMaterialesDiagnostico(Integer idGestionIntegral, Integer idMaterialesDiagnostico) {

		viabilidadTecnicaMaterialesDiagnostico_2 = gestionIntegral2Facade.cargaViabilidadTecnicaMaterialesDiagnostico(idGestionIntegral,idMaterialesDiagnostico);
	}


	//carga los tipos de materiales
	public void datosViabilidadTecnicaTipoMateriales() {

		listaViabilidadTecnicaTipoMateriales = gestionIntegral2Facade.datosViabilidadTecnicaTipoMateriales();

		for (ViabilidadTecnicaTipoMateriales forViabilidadTecnicaTipoMateriales : listaViabilidadTecnicaTipoMateriales) {

			ViabilidadTecnicaMaterialesDiagnostico_2 datosviabilidadTecnicaMateriales_2 = new ViabilidadTecnicaMaterialesDiagnostico_2();
			datosviabilidadTecnicaMateriales_2 = gestionIntegral2Facade.cargaViabilidadTecnicaMaterialesDiagnostico(
							gestionIntegral2.getId(), forViabilidadTecnicaTipoMateriales.getId());

			forViabilidadTecnicaTipoMateriales.setDetalleNombre(datosviabilidadTecnicaMateriales_2.getValor());
		}

	}

	
	public void cargaGestionIntegral() {
		try {
			gestionIntegral2 = gestionIntegral2Facade.cargarGestionIntegral(getLnIdEstudioViabilidadTecnica());
		} catch (Exception e) {
			JsfUtil.addMessageInfo("Error al cargar información.");
			LOGGER.error("Error al cargar información", e);
		}
	}

	public void adjuntarPronunciamiento(FileUploadEvent event) {
		if (event != null) {
			pronunciamiento = JsfUtil.upload(event);
			subido = true;
			JsfUtil.addMessageInfo("El archivo "
					+ event.getFile().getFileName()
					+ " fue adjuntado correctamente.");
		}
	}

	
	public void adjuntarDocumento(FileUploadEvent event) {
		try {

			if (event != null) {
				System.out.println("evento != null");
				imagenFotografica = JsfUtil.upload(event);

				System.out.println("valorAdjunto:" + valorAdjunto);

				switch (valorAdjunto) {
				// Estudio de cantidad y calidad de los residuos y desechos sólidos
				case 3060:
					ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_ESTUDIO_CALIDAD);
					break;
				// Estructura Administrativa
				case 3061:
					ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_ESTRUCTURA_ADMINISTRATIVA);
					break;
				// Informe de encuesta socio economica
				case 3062:
					ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_INFORME_ENCUESTA);
					break;
				// Bases de diseño para el estudio de alternativas
				case 3063:
					ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_BASE_DISEÑO_ESTUDIO);
					break;
				// Estudio de alernativas
				case 3064:
					ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_ESTUDIO_ALTERNATIVAS);
					break;
				// Informe evaluación económico y financiera de la alertativa
				// seleccionada
				case 3065:
					ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_INFORME_EVALUACION_ECO);
					break;
				// Informe Factibilidad
				case 3066:
					ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_INFORME_FACTIBILIDAD);
					break;
				default:
					JsfUtil.addMessageInfo("El archivo "
							+ event.getFile().getFileName()
							+ " no fue adjuntado.");
					break;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error en el Metodo adjuntarImagen", e);
			e.printStackTrace();
		}
	}


	public Documento ingresarDocumento(File file,TipoDocumentoSistema tipoDocumento) throws Exception {
		
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		Documento documento1 = new Documento();
		documento1.setIdTable(1234);
		String ext = getExtension(file.getAbsolutePath());
		documento1.setNombre(file.getName());
		documento1.setExtesion(ext);
		documento1.setNombreTabla(EstudioViabilidadTecnicaDiagnostico.class.getSimpleName());
		documento1.setMime(mimeTypesMap.getContentType(file));
		documento1.setContenidoDocumento(data);
		DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		documentoTarea.setIdTarea(1234);
		documentoTarea.setProcessInstanceId(1234);
		JsfUtil.addMessageInfo("El archivo " + file.getName()+ " fue adjuntado correctamente.");
		return documentosFacade.guardarDocumentoAlfresco("MAE-RA-2015-211963","Viabilidad_Tecnica", new Long(1234), documento1,	tipoDocumento, documentoTarea);
	}


	private String getExtension(String fullPath) {
		String extension = "";
		int i = fullPath.lastIndexOf('.');
		if (i > 0) {
			extension = fullPath.substring(i + 1);
		}
		return extension;
	}
	
	public void consultarDsatos(){
		
	}

}
