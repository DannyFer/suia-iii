package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FaseGestionDesecho;
import ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos;
import ec.gob.ambiente.suia.domain.TipoEliminacionDesecho;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class EmpresaPrestadoraServiciosBean extends RegistroGeneradorBaseBean implements Serializable {

	private static final long serialVersionUID = -6962033961699943688L;

	protected List<Integer> idFasesGestion;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@Setter
	private List<SedePrestadorServiciosDesechos> sedePrestadorServiciosDesechos;

	@Getter
	@Setter
	private DesechoPeligroso desechoSeleccionado;

	@Getter
	@Setter
	private boolean esResponsabilidadExtendida;

	@Setter
	private String filter;

	@Getter
	protected String dialogseleccionarEmpresa;

	@Getter
	protected String filtroEmpresa;

	@Getter
	protected String tableEmpresas;

	@Getter
	protected String datosEmpresaContainer;

	@Getter
	protected String btnSeleccionarEmpresa;

	@Getter
	protected String empresaBtn;

	@Getter
	protected String empresaBtnLabel;

	@Getter
	protected String tbl_empresas;

	@Getter
	protected String btnSeleccionar;

	@Getter
	protected boolean seleccionMultiple;

	@Getter
	@Setter
	private Map<String, Documento> listaSedes  = new HashMap<String, Documento>();
	
	@Getter
	@Setter
	private  boolean mostrarOtraEampresaEliminacion;

	@Getter
	@Setter
	private String nombreOtraEmpresa="";

	@Getter
	@Setter
	private Documento permisoOtraEmpresa;

	@EJB
	private UsuarioFacade usuarioFacade;

	private static final Logger LOG = Logger.getLogger(EmpresaPrestadoraServiciosBean.class);

	public String getFilter() {
		return filter == null ? "" : filter;
	}

	public void reset() {
		filter = null;
		init();
	}

	public void resetSelection() {
		setFilter(null);
		setDesechoSeleccionado(null);
		setSedePrestadorServiciosDesechos(null);
	}

	@PostConstruct
	public void init() {
		idFasesGestion = new ArrayList<Integer>();
		idFasesGestion.add(FaseGestionDesecho.FASE_TRANSPORTE);

		dialogseleccionarEmpresa = "seleccionarEmpresa";
		filtroEmpresa = "filtroEmpresa";
		tableEmpresas = "tableEmpresas";
		tbl_empresas = "tbl_empresas";
		btnSeleccionar = "btnSeleccionar";
		seleccionMultiple = false;
		mostrarOtraEampresaEliminacion = false;
	}

	public void initSedes() {
		String ruc = "";
		try {
			// ruc =
			// usuarioFacade.obtenerCedulaNaturalJuridico(JsfUtil.getBean(LoginBean.class).getUsuario().getId());

			int idTipoEliminacion = 0;
			if (!isTransporte()) {
				TipoEliminacionDesecho tipoEliminacion = JsfUtil.getBean(TipoEliminacionDesechoBean1.class)
						.getTipoEliminacionDesechoSeleccionada();
				idTipoEliminacion = tipoEliminacion.getId();
			}

			ruc = usuarioFacade.obtenerCedulaNaturalJuridico(JsfUtil.getBean(RegistroGeneradorDesechoBean.class)
					.getGeneradorDesechosPeligrosos().getUsuario().getId());

			if (ruc != null && desechoSeleccionado != null) {
				setSedePrestadorServiciosDesechos(registroGeneradorDesechosFacade.getSedesPrestadorServiciosDesechos(
						idFasesGestion, ruc, desechoSeleccionado.getId(), idTipoEliminacion, getFilter()));
			}
		} catch (ServiceException exception) {
			LOG.error("Error al recuperar la identificación del usuario", exception);
		}
	}

	public List<SedePrestadorServiciosDesechos> getSedePrestadorServiciosDesechos() {
		return sedePrestadorServiciosDesechos == null ? sedePrestadorServiciosDesechos = new ArrayList<SedePrestadorServiciosDesechos>()
				: sedePrestadorServiciosDesechos;
	}

	public List<SedePrestadorServiciosDesechos> getSedesSeleccionadas() {
		List<SedePrestadorServiciosDesechos> result = new ArrayList<SedePrestadorServiciosDesechos>();

		if(mostrarOtraEampresaEliminacion){
			SedePrestadorServiciosDesechos obj = new SedePrestadorServiciosDesechos();
			obj.setId(0); //valor para saber que es otra empresa
			result.add(obj);
			return result;
		}

		if (sedePrestadorServiciosDesechos != null) {
			for (SedePrestadorServiciosDesechos sede : sedePrestadorServiciosDesechos) {
				if (sede.isSeleccionado())
					result.add(sede);
			}
		}
		/*
		if(result.size() > 0){
			listaSedes = new HashMap<String, Documento>();
			permisoOtraEmpresa = null;
		}*/
		return result;
	}

	public void setSedesSeleccionadas(List<SedePrestadorServiciosDesechos> sedesSeleccionadas) {
		for (SedePrestadorServiciosDesechos sede : getSedePrestadorServiciosDesechos()) {
			if (sedesSeleccionadas.contains(sede))
				sede.setSeleccionado(true);
		}
	}

	public void cleanSelections() {
		for (SedePrestadorServiciosDesechos sede : sedePrestadorServiciosDesechos) {
			if (sede.isSeleccionado())
				sede.setSeleccionado(false);
		}
	}

	public void eliminarSedeSeleccionada(SedePrestadorServiciosDesechos sede) {
		// si no esta seleccionado es por que se trata de una empresa ingresada
		if (!sede.isSeleccionado()){
			mostrarOtraEampresaEliminacion = false;
			// desabilito si hay alguna seleccionada
			if (sedePrestadorServiciosDesechos != null) {
				for (SedePrestadorServiciosDesechos objSede : sedePrestadorServiciosDesechos) {
					if (objSede.isSeleccionado())
						objSede.setSeleccionado(false);
				}
			}
		}
		sede.setSeleccionado(false);
	}

	public void seleccionarSede(SedePrestadorServiciosDesechos sede) {
		cleanSelections();
		mostrarOtraEampresaEliminacion = false;
		nombreOtraEmpresa = "";
		if (sedePrestadorServiciosDesechos != null) {
			for (SedePrestadorServiciosDesechos sede1 : sedePrestadorServiciosDesechos) {
				if (sede1.equals(sede)){
					listaSedes = new HashMap<String, Documento>();
					sede1.setSeleccionado(true);
				}
			}
		}
	}

	public void setSeleccionMultiple(boolean seleccionMultiple) {
		if (seleccionMultiple == false && getSedesSeleccionadas().size() > 1)
			cleanSelections();
		this.seleccionMultiple = seleccionMultiple;
	}

	public double getCapacidadGestionAnualToneladasSedesSeleccionadas() {
		double capacidadToneladas = 0.0d;
		if (sedePrestadorServiciosDesechos != null) {
			for (SedePrestadorServiciosDesechos sede : sedePrestadorServiciosDesechos) {
				if (sede.isSeleccionado())
					capacidadToneladas += sede.getCapacidadGestionAnualToneladas();
			}
		}
		return capacidadToneladas;
	}

	public double getCapacidadGestionAnualUnidadesSedesSeleccionadas() {
		double capacidadUnidades = 0.0d;
		if (sedePrestadorServiciosDesechos != null) {
			for (SedePrestadorServiciosDesechos sede : sedePrestadorServiciosDesechos) {
				if (sede.isSeleccionado())
					capacidadUnidades += sede.getCapacidadGestionAnualUnidades();
			}
		}
		return capacidadUnidades;
	}

	public boolean isTransporte() {
		boolean istransporte = false;
		if (idFasesGestion != null) {
			if (idFasesGestion.contains(FaseGestionDesecho.FASE_TRANSPORTE))
				istransporte = true;
		}
		return istransporte;
	}

	/**
	 * Byron Burbano 2019-06-07 
	 * metodod para cargo el documento de permiso ambiental de la empresa
	 * @param event
	 */
	public void uploadListenerPermiso(FileUploadEvent event){
		Documento documento = super.uploadListener(event, SedePrestadorServiciosDesechos.class);
		String empresaId = event.getComponent().getAttributes().get("empresaId").toString();
		listaSedes = new HashMap<String, Documento>();
		listaSedes.put(empresaId, documento);
		permisoOtraEmpresa = documento;
	}

	public String permisoUpload(Integer empresaId){
		String nombrePermiso = "";
		for (Map.Entry<String, Documento> entry : listaSedes.entrySet()) {
			if (empresaId.equals(Integer.valueOf((entry.getKey())))){
				nombrePermiso = entry.getValue().getNombre();
			}
		}
		return nombrePermiso;
	}

	public void activarOtraEmpresaEliminacion(){
		mostrarOtraEampresaEliminacion = true;
		if(nombreOtraEmpresa.isEmpty()){
			listaSedes = new HashMap<String, Documento>();
			permisoOtraEmpresa = null;
		}
		
	}
}