package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.prevencion.registrogeneradordesechos.controller.RegistroGeneradorDesechoController;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.comun.bean.AdicionarDesechosPeligrososBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosRecolector;
import ec.gob.ambiente.suia.domain.GeneradorDesechosRecolectorSede;
import ec.gob.ambiente.suia.domain.GeneradorDesechosRecoletorUbicacionGeografica;
import ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RecoleccionTransporteDesechosBean extends RegistroGeneradorBaseBean {

	private static final long serialVersionUID = -2455044468083886861L;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	@Setter
	private boolean tipoPermisoLicencia = true;

	@Getter
	@Setter
	private boolean tieneArchvoPermiso;

	/*
	 * @Getter
	 * 
	 * @Setter private boolean eliminaDesechosDentroInstalacion = true;
	 */

	@Setter
	private GeneradorDesechosRecolector generadorDesechosRecolector;

	@Setter
	private List<GeneradorDesechosRecolector> generadoresDesechosRecolectores;

	@Setter
	@Getter
	private List<GeneradorDesechosRecolector> generadores_aux;

	@Setter
	@Getter
	private List<UbicacionesGeografica> provincias;

	@Setter
	@Getter
	private List<UbicacionesGeografica> cantones;

	@Setter
	@Getter
	private UbicacionesGeografica provinciaDestino;

	@Setter
	@Getter
	private UbicacionesGeografica cantonDestino;
	
	@Getter
	@Setter
	private boolean checknivelnacional;
	

	@PostConstruct
	public void init() {
		setProvincias(ubicacionGeograficaFacade.getProvincias());
	}

	public void cargarCantones() {
		setCantones(new ArrayList<UbicacionesGeografica>());
		if (provinciaDestino != null)
			setCantones(ubicacionGeograficaFacade.getCantonesParroquia(provinciaDestino));
		else
			setCantones(new ArrayList<UbicacionesGeografica>());
	}
	
	public void nivelnacional() {		
		if (checknivelnacional)
			cantonDestino=ubicacionGeograficaFacade.nivelNAcional(1);
		else
			cantonDestino=null;
	}

	public GeneradorDesechosRecolector getGeneradorDesechosRecolector() {
		return generadorDesechosRecolector == null ? generadorDesechosRecolector = new GeneradorDesechosRecolector()
				: generadorDesechosRecolector;
	}

	public List<DesechoPeligroso> getDesechosPeligrosoDisponibles() {
		List<DesechoPeligroso> result = new ArrayList<>();

		List<DesechoPeligroso> selected = getDesechosDisponiblesTransporte();
		result.addAll(selected);
		for (GeneradorDesechosRecolector generadorDesechosRecolector : getGeneradoresDesechosRecolectores()) {
			if (isEditar() && generadorDesechosRecolector.equals(this.generadorDesechosRecolector)){
				getGeneradoresDesechosRecolectores().remove(generadorDesechosRecolector);
				break;
				//continue;
			}
			if (generadorDesechosRecolector.getDesechoPeligroso() != null)
				result.remove(generadorDesechosRecolector.getDesechoPeligroso());
		}
		return result;
	}

	public void resetearValores()
	{
		//JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).reset();
		JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).resetSelection();
		//JsfUtil.getBean(AdicionarUbicacionesBean.class).setMostrarParroquias(false);
		if(JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).getProvinciaDestino()!=null)
			JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).getProvinciaDestino().setRegion(null);
		//JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).getProvinciaDestino().setEstado(false);
		JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).setProvinciaDestino(null);
		JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).setCantonDestino(null);
		JsfUtil.getBean(AdicionarUbicacionesEmpresaPrestadoraServiciosBean.class).getUbicacionesSeleccionadas().clear();

	}

	public List<DesechoPeligroso> getDesechosDisponiblesTransporte() {
		if (JsfUtil.getBean(EliminacionDesechosInstalacionBean.class) == null
				|| JsfUtil.getBean(EliminacionDesechosInstalacionBean.class).getDesechosAEliminarDentroInstalacion() == null
				|| JsfUtil.getBean(EliminacionDesechosInstalacionBean.class).getDesechosAEliminarDentroInstalacion()
				.isEmpty())
			return JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).getDesechosSeleccionados();
		else {
			List<DesechoPeligroso> result = new ArrayList<DesechoPeligroso>();
			List<DesechoPeligroso> desechosSeleccionados = JsfUtil.getBean(AdicionarDesechosPeligrososBean.class)
					.getDesechosSeleccionados();
			int longitud = desechosSeleccionados.size();
			List<DesechoPeligroso> desechosAEliminarDentroInstalacion = JsfUtil.getBean(
					EliminacionDesechosInstalacionBean.class).getDesechosAEliminarDentroInstalacion();
			for (int i = 0; i < longitud; i++) {
				if (!desechosAEliminarDentroInstalacion.contains(desechosSeleccionados.get(i)))
					result.add(desechosSeleccionados.get(i));
			}
			return result;
		}
	}

	public List<GeneradorDesechosRecolector> getGeneradoresDesechosRecolectores() {
		// inicializo la variable para saber si la empresa prestadora de servicio tiene o no archivo de permiso ambiental subido
		tieneArchvoPermiso = false;
        if( generadoresDesechosRecolectores != null && generadoresDesechosRecolectores.size() > 0 &&  generadoresDesechosRecolectores.get(0).getGeneradoresDesechosRecolectoresSedes().size() > 0){
        	for (GeneradorDesechosRecolectorSede objRecolector : generadoresDesechosRecolectores.get(0).getGeneradoresDesechosRecolectoresSedes()) {
        		if (objRecolector.getPermisoAmbiental() != null){
        			tieneArchvoPermiso = true;
        			break;
        		}
			}       		
        }
		return generadoresDesechosRecolectores == null ? generadoresDesechosRecolectores = new ArrayList<GeneradorDesechosRecolector>()
				: generadoresDesechosRecolectores;
	}

	public void aceptar() {
		List<SedePrestadorServiciosDesechos> sedesSeleccionadas = JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class)
				.getSedesSeleccionadas();
		List<GeneradorDesechosRecolectorSede> generadorDesechosRecolectorSedes = new ArrayList<GeneradorDesechosRecolectorSede>();
		if (sedesSeleccionadas != null) {
			for (SedePrestadorServiciosDesechos sede : sedesSeleccionadas) {
				GeneradorDesechosRecolectorSede generadorDesechosRecolectorSede = new GeneradorDesechosRecolectorSede();
				generadorDesechosRecolectorSede.setGeneradorDesechosRecolector(generadorDesechosRecolector);
				generadorDesechosRecolectorSede.setSedePrestadorServiciosDesechos(sede);
				inicializarDocumentoGuardado(generadorDesechosRecolectorSede, sede);
				generadorDesechosRecolectorSedes.add(generadorDesechosRecolectorSede);
			}
			generadorDesechosRecolector.setGeneradoresDesechosRecolectoresSedes(generadorDesechosRecolectorSedes);
		}

		if (JsfUtil.getBean(AdicionarUbicacionesEmpresaPrestadoraServiciosBean.class).getUbicacionesSeleccionadas() != null) {
			int longitudUbicaciones = JsfUtil.getBean(AdicionarUbicacionesEmpresaPrestadoraServiciosBean.class)
					.getUbicacionesSeleccionadas().size();
			List<GeneradorDesechosRecoletorUbicacionGeografica> generadorDesechosRecoletorUbicacionGeograficasLista = new ArrayList<GeneradorDesechosRecoletorUbicacionGeografica>();
			for (int i = 0; i < longitudUbicaciones; i++) {
				GeneradorDesechosRecoletorUbicacionGeografica generadorDesechosRecoletorUbicacionGeografica = new GeneradorDesechosRecoletorUbicacionGeografica();
				generadorDesechosRecoletorUbicacionGeografica
						.setGeneradorDesechosRecolector(generadorDesechosRecolector);
				generadorDesechosRecoletorUbicacionGeografica.setUbicacionesGeografica(JsfUtil
						.getBean(AdicionarUbicacionesEmpresaPrestadoraServiciosBean.class)
						.getUbicacionesSeleccionadas().get(i));
				generadorDesechosRecoletorUbicacionGeograficasLista.add(generadorDesechosRecoletorUbicacionGeografica);
			}
			generadorDesechosRecolector
					.setGeneradorDesechosRecoletorUbicacionesGeograficas(generadorDesechosRecoletorUbicacionGeograficasLista);
		}

		if (cantonDestino != null)
			generadorDesechosRecolector.setUbicacionesGeograficaDestino(cantonDestino);

		if (!getGeneradoresDesechosRecolectores().contains(generadorDesechosRecolector))
			getGeneradoresDesechosRecolectores().add(generadorDesechosRecolector);

		/*LLamar guardarpaso11*/
		setGeneradores_aux(this.generadoresDesechosRecolectores);
		try {
			JsfUtil.getBean(RegistroGeneradorDesechoController.class).guardarpaso11(generadorDesechosRecolector,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JsfUtil.addCallbackParam("addRecoleccionTransporte");
		cancelar_operacion();
		getGeneradorDesechosRecolector();




	}

	public void editar(GeneradorDesechosRecolector generadorDesechosRecolector) {
		this.generadorDesechosRecolector = generadorDesechosRecolector;
		setEditar(true);

		List<SedePrestadorServiciosDesechos> sedes = new ArrayList<SedePrestadorServiciosDesechos>();
		for (GeneradorDesechosRecolectorSede generadorDesechosRecolectorSede : this.generadorDesechosRecolector
				.getGeneradoresDesechosRecolectoresSedes()) {
			sedes.add(generadorDesechosRecolectorSede.getSedePrestadorServiciosDesechos());
			/**
			 * byron burbano 2019-06-18
			 * para inicializar el archivo subido si lo hay 
			 */
			JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).setListaSedes(new HashMap<String, Documento>());
			if(generadorDesechosRecolectorSede.getPermisoAmbiental() != null){
				if(generadorDesechosRecolectorSede.getSedePrestadorServiciosDesechos() != null){
						JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).getListaSedes().put(generadorDesechosRecolectorSede.getSedePrestadorServiciosDesechos().getId().toString(), generadorDesechosRecolectorSede.getPermisoAmbiental());
						JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).setMostrarOtraEampresaEliminacion(false);
						JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).setNombreOtraEmpresa("");
				}else if (!generadorDesechosRecolectorSede.getOtraEmpresa().isEmpty()){
					JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).getListaSedes().put("0", generadorDesechosRecolectorSede.getPermisoAmbiental());
					JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).setMostrarOtraEampresaEliminacion(true);
					JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).setNombreOtraEmpresa(generadorDesechosRecolectorSede.getOtraEmpresa());
				}
			}
		}
		JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).setDesechoSeleccionado(
				getGeneradorDesechosRecolector().getDesechoPeligroso());
		JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).initSedes();
		JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).setSedesSeleccionadas(sedes);

		List<UbicacionesGeografica> ubicaciones = new ArrayList<UbicacionesGeografica>();
		for (GeneradorDesechosRecoletorUbicacionGeografica generadorDesechosRecoletorUbicacionGeografica : this.generadorDesechosRecolector
				.getGeneradorDesechosRecoletorUbicacionesGeograficas()) {
			ubicaciones.add(generadorDesechosRecoletorUbicacionGeografica.getUbicacionesGeografica());
		}
		JsfUtil.getBean(AdicionarUbicacionesEmpresaPrestadoraServiciosBean.class).setUbicacionesSeleccionadas(
				ubicaciones);

		setProvinciaDestino(this.generadorDesechosRecolector.getUbicacionesGeograficaDestino()
				.getUbicacionesGeografica());
		cargarCantones();
		setCantonDestino(this.generadorDesechosRecolector.getUbicacionesGeograficaDestino());
	}

	public void cancelar_operacion() {
		generadorDesechosRecolector = null;
		//setGeneradorDesechosRecolector((GeneradorDesechosRecolector) getDesechosDisponiblesTransporte());
		JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).reset();
		JsfUtil.getBean(AdicionarUbicacionesEmpresaPrestadoraServiciosBean.class).getUbicacionesSeleccionadas().clear();
		provinciaDestino = null;
		cantonDestino = null;
		checknivelnacional = false;

	}

	public void PuedeEliminarcancelar() {
		generadorDesechosRecolector = null;
		JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).reset();
		JsfUtil.getBean(AdicionarUbicacionesEmpresaPrestadoraServiciosBean.class).getUbicacionesSeleccionadas().clear();
		provinciaDestino = null;
		cantonDestino = null;
	}

	public void cancelar() {
		List<SedePrestadorServiciosDesechos> sedesSeleccionadas = JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class)
				.getSedesSeleccionadas();
		List<GeneradorDesechosRecolectorSede> generadorDesechosRecolectorSedes = new ArrayList<GeneradorDesechosRecolectorSede>();
		if (sedesSeleccionadas != null) {
			for (SedePrestadorServiciosDesechos sede : sedesSeleccionadas) {
				GeneradorDesechosRecolectorSede generadorDesechosRecolectorSede = new GeneradorDesechosRecolectorSede();
				generadorDesechosRecolectorSede.setGeneradorDesechosRecolector(generadorDesechosRecolector);
				generadorDesechosRecolectorSede.setSedePrestadorServiciosDesechos(sede);
				generadorDesechosRecolectorSedes.add(generadorDesechosRecolectorSede);
				inicializarDocumentoGuardado(generadorDesechosRecolectorSede, sede);
			}
			generadorDesechosRecolector.setGeneradoresDesechosRecolectoresSedes(generadorDesechosRecolectorSedes);
		}

		if (JsfUtil.getBean(AdicionarUbicacionesEmpresaPrestadoraServiciosBean.class).getUbicacionesSeleccionadas() != null) {
			int longitudUbicaciones = JsfUtil.getBean(AdicionarUbicacionesEmpresaPrestadoraServiciosBean.class)
					.getUbicacionesSeleccionadas().size();
			List<GeneradorDesechosRecoletorUbicacionGeografica> generadorDesechosRecoletorUbicacionGeograficasLista = new ArrayList<GeneradorDesechosRecoletorUbicacionGeografica>();
			for (int i = 0; i < longitudUbicaciones; i++) {
				GeneradorDesechosRecoletorUbicacionGeografica generadorDesechosRecoletorUbicacionGeografica = new GeneradorDesechosRecoletorUbicacionGeografica();
				generadorDesechosRecoletorUbicacionGeografica
						.setGeneradorDesechosRecolector(generadorDesechosRecolector);
				generadorDesechosRecoletorUbicacionGeografica.setUbicacionesGeografica(JsfUtil
						.getBean(AdicionarUbicacionesEmpresaPrestadoraServiciosBean.class)
						.getUbicacionesSeleccionadas().get(i));
				generadorDesechosRecoletorUbicacionGeograficasLista.add(generadorDesechosRecoletorUbicacionGeografica);
			}
			generadorDesechosRecolector
					.setGeneradorDesechosRecoletorUbicacionesGeograficas(generadorDesechosRecoletorUbicacionGeograficasLista);
		}

		if (cantonDestino != null)
			generadorDesechosRecolector.setUbicacionesGeograficaDestino(cantonDestino);

		if (!getGeneradoresDesechosRecolectores().contains(generadorDesechosRecolector))
			getGeneradoresDesechosRecolectores().add(generadorDesechosRecolector);

		/*LLamar guardarpaso11*/
		setGeneradores_aux(this.generadoresDesechosRecolectores);
		try {
			JsfUtil.getBean(RegistroGeneradorDesechoController.class).guardarpaso11(generadorDesechosRecolector,false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JsfUtil.addCallbackParam("addRecoleccionTransporte");
		cancelar_operacion();



	}

	public void eliminar(GeneradorDesechosRecolector generadorDesechosRecolector) {
		if (getGeneradoresDesechosRecolectores().contains(generadorDesechosRecolector)){
			JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setModificacionesEliminaciones(true);
			getGeneradoresDesechosRecolectores().remove(generadorDesechosRecolector);
		}
		if (generadorDesechosRecolector.getDesechoPeligroso() != null)
			JsfUtil.getBean(EliminacionDesechosFueraInstalacionBean.class).validacionesDesechos(
					generadorDesechosRecolector.getDesechoPeligroso());

		/*eliminar paso 11*/
		JsfUtil.getBean(RegistroGeneradorDesechoController.class).eliminarGeneradorDesechosRecolector(generadorDesechosRecolector);
	}



	public void validacionesDesechos(DesechoPeligroso desechoPeligroso) {
		GeneradorDesechosRecolector generadorDesechosRecolectorToDelete = null;
		for (GeneradorDesechosRecolector generadorDesechosRecolector : getGeneradoresDesechosRecolectores()) {
			if (generadorDesechosRecolector.getDesechoPeligroso().equals(desechoPeligroso)) {
				generadorDesechosRecolectorToDelete = generadorDesechosRecolector;
				break;
			}
		}
		if (generadorDesechosRecolectorToDelete != null)
			getGeneradoresDesechosRecolectores().remove(generadorDesechosRecolectorToDelete);
	}

	public List<DesechoPeligroso> getDesechosATransportar() {
		int longitud = getGeneradoresDesechosRecolectores().size();
		List<DesechoPeligroso> result = new ArrayList<DesechoPeligroso>();
		for (int i = 0; i < longitud; i++) {
			if (getGeneradoresDesechosRecolectores().get(i).getDesechoPeligroso() != null)
				result.add(getGeneradoresDesechosRecolectores().get(i).getDesechoPeligroso());
		}
		return result;
	}

	public void validateRecoleccionTransporteDesechos(FacesContext context, UIComponent validate, Object value) {
		if ((getGeneradoresDesechosRecolectores().isEmpty() && getDesechosDisponiblesTransporte().size() > 0)
				|| (getGeneradoresDesechosRecolectores().size() < getDesechosDisponiblesTransporte().size()))
			throw new ValidatorException(
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe completar, para todos los desechos seleccionados que no va a eliminar dentro de la instalación, los datos asociados.",
							null));
	}

	public void validateData(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();

		if (JsfUtil.getBean(RegistroGeneradorDesechoBean.class).getGeneradorDesechosPeligrosos().getResponsabilidadExtendida() && JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).getSedesSeleccionadas().isEmpty()) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,	"El campo 'Empresa prestadora de servicios' es requerido.", null));
		}

		if (JsfUtil.getBean(AdicionarUbicacionesEmpresaPrestadoraServiciosBean.class).getUbicacionesSeleccionadas() == null) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar, al menos, una ubicación geográfica de origen.", null));
		}

		//Byron Burbano 2019-06-17
		//validacion de documento obligatorio de permiso ambiental  si es de responsabilidad extendida (no existe proyecto)
		if (JsfUtil.getBean(RegistroGeneradorDesechoBean.class).getGeneradorDesechosPeligrosos().getResponsabilidadExtendida() && JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).getListaSedes().size() == 0l){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe adjuntar el permiso ambiental de la empresa.",	null));
		}
		
		/*if (generadorDesechosRecolector.getDesechoPeligroso() != null
				&& !JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).getSedesSeleccionadas().isEmpty())
			validateCantidadesGeneradorDesechosRecolector(errorMessages);*/

		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void modificarDesecho() {
		JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).resetSelection();
		JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).setDesechoSeleccionado(
				getGeneradorDesechosRecolector().getDesechoPeligroso());
		JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).initSedes();
	}

	public void validateCantidadesGeneradorDesechosRecolector(List<FacesMessage> errorMessages) {
		Map<DesechoPeligroso, Double> cantidadUnidades = JsfUtil.getBean(EliminacionDesechosInstalacionBean.class)
				.getCantidadesDesechosAEliminarDentroInstalacionUnidades();
		Map<DesechoPeligroso, Double> cantidadToneladas = JsfUtil.getBean(EliminacionDesechosInstalacionBean.class)
				.getCantidadesDesechosAEliminarDentroInstalacionToneladas();

		if ((generadorDesechosRecolector.getDesechoPeligroso().isDesechoES_04() || generadorDesechosRecolector
				.getDesechoPeligroso().isDesechoES_06())
				&& cantidadUnidades.get(generadorDesechosRecolector.getDesechoPeligroso()) != null
				&& (JsfUtil
				.getBean(DatosDesechosBean.class)
				.getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(
						generadorDesechosRecolector.getDesechoPeligroso()).getCantidadUnidades() - cantidadUnidades
				.get(generadorDesechosRecolector.getDesechoPeligroso())) > JsfUtil.getBean(
				EmpresaPrestadoraServiciosBean.class).getCapacidadGestionAnualUnidadesSedesSeleccionadas()) {
			errorMessages
					.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"La capacidad de gestión anual(Unidades) de la empresa prestadora de servicio seleccionada, es inferior a la que se necesita, según los datos correspondientes a ese desecho, ingresados en el paso 'Datos asociados a los desechos'.",
							null));
		} else if ((generadorDesechosRecolector.getDesechoPeligroso().isDesechoES_04() || generadorDesechosRecolector
				.getDesechoPeligroso().isDesechoES_06())
				&& cantidadUnidades.get(generadorDesechosRecolector.getDesechoPeligroso()) == null
				&& JsfUtil
				.getBean(DatosDesechosBean.class)
				.getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(
						generadorDesechosRecolector.getDesechoPeligroso()).getCantidadUnidades() > JsfUtil
				.getBean(EmpresaPrestadoraServiciosBean.class)
				.getCapacidadGestionAnualUnidadesSedesSeleccionadas()) {
			errorMessages
					.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"La capacidad de gestión anual(Unidades) de la empresa prestadora de servicio seleccionada, es inferior a la que se necesita, según los datos correspondientes a ese desecho, ingresados en el paso 'Datos asociados a los desechos'.",
							null));
		}
		if (cantidadToneladas.get(generadorDesechosRecolector.getDesechoPeligroso()) != null
				&& (JsfUtil
				.getBean(DatosDesechosBean.class)
				.getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(
						generadorDesechosRecolector.getDesechoPeligroso()).getCantidadToneladas() - cantidadToneladas
				.get(generadorDesechosRecolector.getDesechoPeligroso())) > JsfUtil.getBean(
				EmpresaPrestadoraServiciosBean.class).getCapacidadGestionAnualToneladasSedesSeleccionadas()) {
			errorMessages
					.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"La capacidad de gestión anual(Toneladas) de la empresa prestadora de servicio seleccionada, es inferior a la que se necesita, según los datos correspondientes a ese desecho, ingresados en el paso 'Datos asociados a los desechos'.",
							null));
		} else if (cantidadToneladas.get(generadorDesechosRecolector.getDesechoPeligroso()) == null
				&& JsfUtil
				.getBean(DatosDesechosBean.class)
				.getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(
						generadorDesechosRecolector.getDesechoPeligroso()).getCantidadToneladas() > JsfUtil
				.getBean(EmpresaPrestadoraServiciosBean.class)
				.getCapacidadGestionAnualToneladasSedesSeleccionadas()) {
			errorMessages
					.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"La capacidad de gestión anual(Toneladas) de la empresa prestadora de servicio seleccionada, es inferior a la que se necesita, según los datos correspondientes a ese desecho, ingresados en el paso 'Datos asociados a los desechos'.",
							null));
		}
	}

	/**
	 * byron burbano  2017-06-18
	 * cargo el documento que esta cargado para guardarlo en el nuevo registro que se genera
	 */
	public void inicializarDocumentoGuardado(GeneradorDesechosRecolectorSede generadorDesechosRecolectorSede, SedePrestadorServiciosDesechos sede ){
		for (Map.Entry<String, Documento> entry : JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).getListaSedes().entrySet()) {
		   if ( sede.getId().equals(Integer.valueOf((entry.getKey())))){
				generadorDesechosRecolectorSede.setPermisoAmbiental(entry.getValue());
				generadorDesechosRecolectorSede.setOtraEmpresa( JsfUtil.getBean(EmpresaPrestadoraServiciosBean.class).getNombreOtraEmpresa());
		    }
		}
		
	}
	/**
	 * Byron Burbano 2019-06-10
	 * descarga del documento de permiso ambiental subido para procesos dRGD con responsabilidadextendida
	 * @throws Exception 
	 */
	public StreamedContent getFilePermiso(GeneradorDesechosRecolectorSede recolector) throws Exception{
		if(recolector.getPermisoAmbiental() != null ){
			String workspace = recolector.getPermisoAmbiental().getIdAlfresco();
			recolector.getPermisoAmbiental().setContenidoDocumento(documentosFacade.descargar(workspace));
			return getStreamContent(recolector.getPermisoAmbiental());
		}
		return null;
	}
}