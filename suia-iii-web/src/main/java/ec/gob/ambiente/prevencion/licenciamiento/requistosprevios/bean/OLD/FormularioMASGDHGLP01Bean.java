package ec.gob.ambiente.prevencion.licenciamiento.requistosprevios.bean.OLD;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.Length;
import org.primefaces.component.fileupload.FileUpload;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.ClasificacionClave;
import ec.gob.ambiente.suia.domain.ClaveOperacion;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FuenteDesechoPeligroso;
import ec.gob.ambiente.suia.licenciamiento.facade.RequisitosPreviosFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean(name = "formularioMASGDHGLP01")
@ViewScoped
public class FormularioMASGDHGLP01Bean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3075267536572079767L;

	private static final Logger LOG = Logger
			.getLogger(FormularioMASGDHGLP01Bean.class);

	// CAMPOS
	@Getter
	@Setter
	private String numeroSolicitud;
	@Getter
	@Setter
	private String numeroLicenciaAmbiental;
	@Getter
	@Setter
	private String nombreResponsableTecnico;
	@Getter
	@Setter
	private String nombreEmpresa;
	@Getter
	@Setter
	private String actividadProductivaPrincipal;
	@Getter
	@Setter
	private String direccionSolicitante;
	@Getter
	@Setter
	private String direccionEstablecimiento;
	@Getter
	@Setter
	private String fechaInicioOperaciones;
	@Getter
	@Setter
	private FuenteDesechoPeligroso sector;
	@Getter
	@Setter
	private Boolean declaracion;
	@Getter
	@Setter
	private DesechoPeligroso desechoPeligroso;
	@Getter
	@Setter
	private ClaveOperacion claveManejoDesechos;
	@Getter
	@Setter
	@Length(max = 300)
	private String observaciones;
	@Getter
	@Setter
	private ClaveOperacion claveCategoria;

	// ARCHIVOS
	@Getter
	@Setter
	private FileUpload fileContrato;
	@Getter
	@Setter
	private FileUpload fileInformeRegulacionMunicipal;

	// LISTAS
	@Getter
	@Setter
	private Map<String, FileUpload> archivos;
	@Getter
	@Setter
	private List<FuenteDesechoPeligroso> sectoresSolicitante;
	@Getter
	@Setter
	private List<DesechoPeligroso> desechosPeligrosos;
	@Getter
	@Setter
	private List<SelectItem> clavesCategoria;
	@Getter
	@Setter
	private List<ModalidadManejoDesechosBean> modalidadesManejoDesechos;
	@Getter
	@Setter
	private List<SelectItem> clavesManejoDesechos;
	@Getter
	@Setter
	private List<CatalogoGeneral> estadosLocal;
	@Getter
	@Setter
	private List<CatalogoGeneral> tiposVentilacion;
	@Getter
	@Setter
	private List<CatalogoGeneral> materialesConstruccion;
	@Getter
	@Setter
	private List<CatalogoGeneral> tiposIluminacion;
	@Getter
	@Setter
	private List<CatalogoGeneral> unidadesMedida;
	@Getter
	@Setter
	private List<AlmacenamientoTemporalDesechosBean> almacenamientosTemporales; 

	// banderas
	@Getter
	@Setter
	private int index;

	//CLAVES
	final int CLAVE9 = 9;
	final int CLAVE3 = 3;
	
	//INDICES
	@Getter
	final int INDEX_DATOS_REGISTRO = 1;
	@Getter
	final int INDEX_PRESTADOR_SERVICIOS = 2;
	@Getter
	final int INDEX_GENERACION_RECEPCION = 3;
	
	// EJBs
	@EJB
	RequisitosPreviosFacade requisitosPreviosFacade;
	@EJB
	CatalogoGeneralFacade catalogoGeneralFacade;

	@PostConstruct
	public void init() {
		try {
			if (archivos == null) {
				archivos = new HashMap<String, FileUpload>();
			}

			if (modalidadesManejoDesechos == null) {
				modalidadesManejoDesechos = new ArrayList<ModalidadManejoDesechosBean>();
			}

			if (sectoresSolicitante == null) {
				cargarFuentesDesechos();
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError("No se pudo realizar la carga inicial");
		}
	}

	public void continuar(int index) {
		setIndex(index);
		
		if(index == INDEX_PRESTADOR_SERVICIOS){
			cargarClaveManejoDesechos();
			cargarClaveCategoriaDesecho();
		}
	}
	
	public void agregarModalidadManejoDesechos() {
		if (getDesechoPeligroso() != null
				&& getClaveManejoDesechos() != null) {
			ModalidadManejoDesechosBean modalidadMenejo = new ModalidadManejoDesechosBean();
			modalidadMenejo.setDesechoPeligroso(getDesechoPeligroso());
			modalidadMenejo.setClaveManejoDeschos(getClaveManejoDesechos());
			modalidadMenejo.setObservaciones(getObservaciones());

			getModalidadesManejoDesechos().add(modalidadMenejo);

			setDesechoPeligroso(null);
			setObservaciones(null);
			setClaveManejoDesechos(null);
		} else {
			JsfUtil.addMessageInfo("Debe escoger el Desecho y Modalidad de Desecho");
		}
	}

	public void eliminarModalidadManejoDesechos(
			ModalidadManejoDesechosBean modalidad) {
		if (modalidad != null) {
			getModalidadesManejoDesechos().remove(modalidad);
		}

		setDesechoPeligroso(null);
		setObservaciones(null);
		setClaveManejoDesechos(null);
	}

	public void cargarFuentesDesechos() {
		// SECTORES AL QUE PERTENECE EL SOLICITANTE
		// Acuerdo Ministerial 142 ANEXO B listado N°1
		try {
			sectoresSolicitante = requisitosPreviosFacade
					.obtenerFuentesDesechosPeligrososXTipo(1);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError("No se puede cargar lista de fuenet de desechos");
		}
	}

	public void cargarDesechosXFuente() {
		// LISTA DE DESECHOS PELIGROSOS
		// Acuerdo Ministerial 142 ANEXO B listado N°1
		try {
			desechosPeligrosos = requisitosPreviosFacade
					.obtenerDesechosPeligrososXFuente(getSector().getId());
		} catch (Exception e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError("No se puede cargar lista de desechos peligrosos");
		}
	}
	
	public void cargarClaveManejoDesechos() {
		// LISTA DE CLAVES DE MANEJO DE DESECHOS
		// Acuerdo Ministerial No.026 -- CLAVE(9)
		try {
			clavesManejoDesechos = new ArrayList<SelectItem>();
			SelectItemGroup grupo = null;
			
			List<ClasificacionClave> clasificaciones = requisitosPreviosFacade.obtenerClasificacionesClave(CLAVE9);
			for (ClasificacionClave clasificacionClave : clasificaciones) {
				grupo = new SelectItemGroup(clasificacionClave.getClasificacion());
				List<ClaveOperacion> claves = requisitosPreviosFacade.obtenerClavesOperacion(clasificacionClave.getId());
				SelectItem items[] = new SelectItem[claves.size()];
				int i = 0;
				for (ClaveOperacion claveOperacion : claves) {
					items[i] = new SelectItem(claveOperacion);
					i++;
				}
				grupo.setSelectItems(items);
				clavesManejoDesechos.add(grupo);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError("No se puede cargar lista de clave de manejo de deschos");
		}
	}
	
	public void cargarClaveCategoriaDesecho() {
		// LISTA DE CLAVES DE MANEJO DE DESECHOS
		// Acuerdo Ministerial No.026 -- CLAVE(3)
		try {
			clavesCategoria = new ArrayList<SelectItem>();
			SelectItemGroup grupo = null;
			
			List<ClasificacionClave> clasificaciones = requisitosPreviosFacade.obtenerClasificacionesClave(CLAVE3);
			for (ClasificacionClave clasificacionClave : clasificaciones) {
				grupo = new SelectItemGroup(clasificacionClave.getClasificacion());
				List<ClaveOperacion> claves = requisitosPreviosFacade.obtenerClavesOperacion(clasificacionClave.getId());
				SelectItem items[] = new SelectItem[claves.size()];
				int i = 0;
				for (ClaveOperacion claveOperacion : claves) {
					items[i] = new SelectItem(claveOperacion);
					i++;
				}
				grupo.setSelectItems(items);
				clavesCategoria.add(grupo);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError("No se puede cargar lista de clave de manejo de deschos");
		}
	}
	
	public void cargarCatalogoGeneral(int tipoCatalogo, List<CatalogoGeneral> catalogo) {
		try {
			catalogo = catalogoGeneralFacade.obtenerCatalogoXTipo(tipoCatalogo);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError("No se puede cargar catalogo de tipo: "+tipoCatalogo);
		}
	}

}
