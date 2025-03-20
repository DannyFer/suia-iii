package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.util.ArrayList;
import java.util.Arrays;
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
import javax.validation.constraints.Size;

import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.prevencion.registrogeneradordesechos.controller.RegistroGeneradorDesechoController;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosEliminador;
import ec.gob.ambiente.suia.domain.GeneradorDesechosEliminadorSede;
import ec.gob.ambiente.suia.domain.GeneradorDesechosRecolectorSede;
import ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos;
import ec.gob.ambiente.suia.domain.UnidadMedida;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class EliminacionDesechosFueraInstalacionBean extends RegistroGeneradorBaseBean {

	private static final long serialVersionUID = -1321645588667861384L;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@Setter
	private List<GeneradorDesechosEliminador> generadoresDesechosEliminadores;

	@Setter
	private GeneradorDesechosEliminador generadorDesechosEliminador;

	@Getter
	private List<UnidadMedida> unidadesMedidas;

	@Getter
	@Setter
	private boolean tieneArchvoPermiso;

	@PostConstruct
	private void initDatos() {
		unidadesMedidas = Arrays.asList(registroGeneradorDesechosFacade.getUnidadMedidaTonelada(),
				registroGeneradorDesechosFacade.getUnidadMedidaUnidad());
	}

	public List<DesechoPeligroso> getDesechosPeligrosoDisponibles() {
		List<DesechoPeligroso> result = new ArrayList<>();
		List<DesechoPeligroso> selected = JsfUtil.getBean(RecoleccionTransporteDesechosBean.class)
				.getDesechosDisponiblesTransporte();
		result.addAll(selected);
		for (GeneradorDesechosEliminador generadorDesechosEliminador : getGeneradoresDesechosEliminadores()) {
			if (isEditar() && generadorDesechosEliminador.equals(this.generadorDesechosEliminador))
				continue;
			if (generadorDesechosEliminador.getDesechoPeligroso() != null)
				result.remove(generadorDesechosEliminador.getDesechoPeligroso());
		}
		return result;
	}

	/**
	 * byron burbano  2017-06-18
	 * cargo el documento que esta cargado para guardarlo en el nuevo registro que se genera
	 */
	public void inicializarDocumentoGuardado(GeneradorDesechosEliminadorSede generadorDesechosEliminarSede, SedePrestadorServiciosDesechos sede ){
		for (Map.Entry<String, Documento> entry : JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).getListaSedes().entrySet()) {
		   if ( sede.getId().equals(Integer.valueOf((entry.getKey())))){
			   generadorDesechosEliminarSede.setPermisoAmbiental(entry.getValue());
			   generadorDesechosEliminarSede.setOtraEmpresa( JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).getNombreOtraEmpresa());
		    }
		}
	}
	
	public void aceptar() {
		List<SedePrestadorServiciosDesechos> sedesSeleccionadas = JsfUtil
				.getBean(EmpresaPrestadoraServiciosBean1.class).getSedesSeleccionadas();
		List<GeneradorDesechosEliminadorSede> generadorDesechosEliminadorSedes = new ArrayList<GeneradorDesechosEliminadorSede>();
		if (sedesSeleccionadas != null) {
			for (SedePrestadorServiciosDesechos sede : sedesSeleccionadas) {
				GeneradorDesechosEliminadorSede desechosEliminadorSede = new GeneradorDesechosEliminadorSede();
				desechosEliminadorSede.setGeneradorDesechosEliminador(generadorDesechosEliminador);
				desechosEliminadorSede.setSedePrestadorServiciosDesechos(sede);
				generadorDesechosEliminadorSedes.add(desechosEliminadorSede);
				inicializarDocumentoGuardado(desechosEliminadorSede, sede);
			}
			generadorDesechosEliminador.setGeneradoresDesechosEliminadoresSedes(generadorDesechosEliminadorSedes);
		}

		if (JsfUtil.getBean(TipoEliminacionDesechoBean1.class).getTipoEliminacionDesechoSeleccionada() != null) {
			generadorDesechosEliminador.setTipoEliminacionDesecho(JsfUtil.getBean(TipoEliminacionDesechoBean1.class)
					.getTipoEliminacionDesechoSeleccionada());
			generadorDesechosEliminador.setTextoAsociadoOpcionOtro(JsfUtil.getBean(TipoEliminacionDesechoBean1.class)
					.getTextoAsociadoOpcionOtro());
		}
		if (!getGeneradoresDesechosEliminadores().contains(generadorDesechosEliminador))
			getGeneradoresDesechosEliminadores().add(generadorDesechosEliminador);

		/*guardarpaso12 Paso 12*/
		try {
			JsfUtil.getBean(RegistroGeneradorDesechoController.class).guardarpaso12(generadorDesechosEliminador);
			JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).setNombreOtraEmpresa("");
		} catch (Exception e) {
			e.printStackTrace();
		}
		cancelar();
		JsfUtil.addCallbackParam("addEliminacionFueraInstalacion");
	}

	public void cancelar() {
		generadorDesechosEliminador = null;
		JsfUtil.getBean(TipoEliminacionDesechoBean1.class).resetSelection();
		JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).reset();
	}

	public void editar(GeneradorDesechosEliminador generadorDesechosEliminador) {
		this.generadorDesechosEliminador = generadorDesechosEliminador;
		setEditar(true);

		if (this.generadorDesechosEliminador.getTipoEliminacionDesecho() != null) {
			JsfUtil.getBean(TipoEliminacionDesechoBean1.class).setTipoEliminacionDesechoSeleccionada(
					this.generadorDesechosEliminador.getTipoEliminacionDesecho());
		}

		if (this.generadorDesechosEliminador.getGeneradoresDesechosEliminadoresSedes() != null) {
			List<SedePrestadorServiciosDesechos> auxiliar = new ArrayList<SedePrestadorServiciosDesechos>();
			for (GeneradorDesechosEliminadorSede generadorDesechosEliminadorSede : this.generadorDesechosEliminador
					.getGeneradoresDesechosEliminadoresSedes()) {
				auxiliar.add(generadorDesechosEliminadorSede.getSedePrestadorServiciosDesechos());
				/**
				 * byron burbano 2019-06-18
				 * para inicializar el archivo subido si lo hay 
				 */
				JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).setListaSedes(new HashMap<String, Documento>());
				if(generadorDesechosEliminadorSede.getPermisoAmbiental() != null){
					if(generadorDesechosEliminadorSede.getSedePrestadorServiciosDesechos() != null){
							JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).getListaSedes().put(generadorDesechosEliminadorSede.getSedePrestadorServiciosDesechos().getId().toString(), generadorDesechosEliminadorSede.getPermisoAmbiental());
							JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).setMostrarOtraEampresaEliminacion(false);
							JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).setNombreOtraEmpresa("");
					}else if (!generadorDesechosEliminadorSede.getOtraEmpresa().isEmpty()){
						JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).getListaSedes().put("0", generadorDesechosEliminadorSede.getPermisoAmbiental());
						JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).setMostrarOtraEampresaEliminacion(true);
						JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).setNombreOtraEmpresa(generadorDesechosEliminadorSede.getOtraEmpresa());
					}
				}
				
			}
			JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).setDesechoSeleccionado(
					getGeneradorDesechosEliminador().getDesechoPeligroso());
			JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).initSedes();
			JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).setSedesSeleccionadas(auxiliar);
		}

		if (this.generadorDesechosEliminador.getTextoAsociadoOpcionOtro() != null) {
			JsfUtil.getBean(TipoEliminacionDesechoBean1.class).setTextoAsociadoOpcionOtro(
					this.generadorDesechosEliminador.getTextoAsociadoOpcionOtro());
		}
	}

	public void eliminar(GeneradorDesechosEliminador eliminador) {
		if (getGeneradoresDesechosEliminadores().contains(eliminador))
			getGeneradoresDesechosEliminadores().remove(eliminador);

		/*eliminar paso 12*/
		JsfUtil.getBean(RegistroGeneradorDesechoController.class).eliminarGeneradorDesechosEliminador(eliminador);

	}

	public void modificarDesecho() {
		JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).resetSelection();
		JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).setDesechoSeleccionado(
				getGeneradorDesechosEliminador().getDesechoPeligroso());

		if (getGeneradorDesechosEliminador().getDesechoPeligroso() != null
				&& JsfUtil.getBean(TipoEliminacionDesechoBean1.class).getTipoEliminacionDesechoSeleccionada() != null)
			JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).initSedes();
	}

	public List<GeneradorDesechosEliminador> getGeneradoresDesechosEliminadores() {
		// inicializo la variable para saber si la empresa prestadora de servicio tiene o no archivo de permiso ambiental subido
		tieneArchvoPermiso = false;
        if( generadoresDesechosEliminadores != null && generadoresDesechosEliminadores.size() > 0 && generadoresDesechosEliminadores.get(0).getGeneradoresDesechosEliminadoresSedes().size() > 0){
        	for (GeneradorDesechosEliminadorSede objEliminadores : generadoresDesechosEliminadores.get(0).getGeneradoresDesechosEliminadoresSedes()) {
        		if (objEliminadores.getPermisoAmbiental() != null){
        			tieneArchvoPermiso = true;
        			break;
        		}
			}       		
        }
		return generadoresDesechosEliminadores == null ? generadoresDesechosEliminadores = new ArrayList<GeneradorDesechosEliminador>()
				: generadoresDesechosEliminadores;
	}

	public GeneradorDesechosEliminador getGeneradorDesechosEliminador() {
		return generadorDesechosEliminador == null ? generadorDesechosEliminador = new GeneradorDesechosEliminador()
				: generadorDesechosEliminador;
	}

	public void validacionesDesechos(DesechoPeligroso desechoPeligroso) {
		GeneradorDesechosEliminador eliminadorToDelete = null;
		for (GeneradorDesechosEliminador eliminador : getGeneradoresDesechosEliminadores()) {
			if (eliminador.getDesechoPeligroso().equals(desechoPeligroso)) {
				eliminadorToDelete = eliminador;
				break;
			}
		}
		if (eliminadorToDelete != null)
			getGeneradoresDesechosEliminadores().remove(eliminadorToDelete);
	}

	public void validateEliminacionFueraInstalacion(FacesContext context, UIComponent validate, Object value) {
		if ((getGeneradoresDesechosEliminadores().isEmpty() && JsfUtil.getBean(RecoleccionTransporteDesechosBean.class)
				.getDesechosDisponiblesTransporte().size() > 0)
				|| getGeneradoresDesechosEliminadores().size() < JsfUtil
				.getBean(RecoleccionTransporteDesechosBean.class).getDesechosDisponiblesTransporte().size())
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe completar, para todos los desechos seleccionados que va a transportar, los datos asociados.",
					null));
	}

	public void validateData(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();

		if (JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).getSedesSeleccionadas().isEmpty()) {
//			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
//					"El campo 'Empresa(s) prestadora(s) de servicio' es requerido.", null));
		}
		if (JsfUtil.getBean(TipoEliminacionDesechoBean1.class).getTipoEliminacionDesechoSeleccionada() == null) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Tipo de eliminación o disposición final' es requerido.", null));
		}

		//Byron Burbano 2019-06-17
		//validacion de documento obligatorio de permiso ambiental  si es de responsabilidad extendida (no existe proyecto)
		if (JsfUtil.getBean(RegistroGeneradorDesechoBean.class).getGeneradorDesechosPeligrosos().getResponsabilidadExtendida() && JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).getSedesSeleccionadas().isEmpty()) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,	"El campo 'Empresa prestadora de servicios' es requerido.", null));
		}
		if (JsfUtil.getBean(RegistroGeneradorDesechoBean.class).getGeneradorDesechosPeligrosos().getResponsabilidadExtendida() && JsfUtil.getBean(EmpresaPrestadoraServiciosBean1.class).getListaSedes().size() == 0){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe adjuntar el permiso ambiental de la empresa.",	null));
		}
		
		/*
		 * if (generadorDesechosEliminador.getDesechoPeligroso() != null &&
		 * !JsfUtil
		 * .getBean(EmpresaPrestadoraServiciosBean1.class).getSedesSeleccionadas
		 * ().isEmpty())
		 * validateCantidadesGeneradorDesechosEliminador(errorMessages);
		 */
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void validateCantidadesGeneradorDesechosEliminador(List<FacesMessage> errorMessages) {
		Map<DesechoPeligroso, Double> cantidadUnidades = JsfUtil.getBean(EliminacionDesechosInstalacionBean.class)
				.getCantidadesDesechosAEliminarDentroInstalacionUnidades();
		Map<DesechoPeligroso, Double> cantidadToneladas = JsfUtil.getBean(EliminacionDesechosInstalacionBean.class)
				.getCantidadesDesechosAEliminarDentroInstalacionToneladas();

		if ((generadorDesechosEliminador.getDesechoPeligroso().isDesechoES_04() || generadorDesechosEliminador
				.getDesechoPeligroso().isDesechoES_06())
				&& cantidadUnidades.get(generadorDesechosEliminador.getDesechoPeligroso()) != null
				&& (JsfUtil
				.getBean(DatosDesechosBean.class)
				.getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(
						generadorDesechosEliminador.getDesechoPeligroso()).getCantidadUnidades() - cantidadUnidades
				.get(generadorDesechosEliminador.getDesechoPeligroso())) > JsfUtil.getBean(
				EmpresaPrestadoraServiciosBean1.class).getCapacidadGestionAnualUnidadesSedesSeleccionadas()) {
			errorMessages
					.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"La capacidad de gestión anual(Unidades) de la empresa prestadora de servicio seleccionada, es inferior a la que se necesita, según los datos correspondientes a ese desecho, ingresados en el paso 'Datos asociados a los desechos'.",
							null));
		} else if ((generadorDesechosEliminador.getDesechoPeligroso().isDesechoES_04() || generadorDesechosEliminador
				.getDesechoPeligroso().isDesechoES_06())
				&& cantidadUnidades.get(generadorDesechosEliminador.getDesechoPeligroso()) == null
				&& JsfUtil
				.getBean(DatosDesechosBean.class)
				.getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(
						generadorDesechosEliminador.getDesechoPeligroso()).getCantidadUnidades() > JsfUtil
				.getBean(EmpresaPrestadoraServiciosBean1.class)
				.getCapacidadGestionAnualUnidadesSedesSeleccionadas()) {
			errorMessages
					.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"La capacidad de gestión anual(Unidades) de la empresa prestadora de servicio seleccionada, es inferior a la que se necesita, según los datos correspondientes a ese desecho, ingresados en el paso 'Datos asociados a los desechos'.",
							null));
		}
		if (cantidadToneladas.get(generadorDesechosEliminador.getDesechoPeligroso()) != null
				&& (JsfUtil
				.getBean(DatosDesechosBean.class)
				.getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(
						generadorDesechosEliminador.getDesechoPeligroso()).getCantidadToneladas() - cantidadToneladas
				.get(generadorDesechosEliminador.getDesechoPeligroso())) > JsfUtil.getBean(
				EmpresaPrestadoraServiciosBean1.class).getCapacidadGestionAnualToneladasSedesSeleccionadas()) {
			errorMessages
					.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"La capacidad de gestión anual(Toneladas) de la empresa prestadora de servicio seleccionada, es inferior a la que se necesita, según los datos correspondientes a ese desecho, ingresados en el paso 'Datos asociados a los desechos'.",
							null));
		} else if (cantidadToneladas.get(generadorDesechosEliminador.getDesechoPeligroso()) == null
				&& JsfUtil
				.getBean(DatosDesechosBean.class)
				.getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(
						generadorDesechosEliminador.getDesechoPeligroso()).getCantidadToneladas() > JsfUtil
				.getBean(EmpresaPrestadoraServiciosBean1.class)
				.getCapacidadGestionAnualToneladasSedesSeleccionadas()) {
			errorMessages
					.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"La capacidad de gestión anual(Toneladas) de la empresa prestadora de servicio seleccionada, es inferior a la que se necesita, según los datos correspondientes a ese desecho, ingresados en el paso 'Datos asociados a los desechos'.",
							null));
		}
	}

	/**
	 * Byron Burbano 2019-06-10
	 * descarga del documento de permiso ambiental subido para procesos dRGD con responsabilidadextendida
	 * @throws Exception 
	 */
	public StreamedContent getFilePermiso(GeneradorDesechosEliminadorSede recolector) throws Exception{
		if(recolector.getPermisoAmbiental() != null ){
			String workspace = recolector.getPermisoAmbiental().getIdAlfresco();
			recolector.getPermisoAmbiental().setContenidoDocumento(documentosFacade.descargar(workspace));
			return getStreamContent(recolector.getPermisoAmbiental());
		}
		return null;
	}

	public StreamedContent getArchivoPermiso(List<GeneradorDesechosEliminadorSede> listaRecolector) throws Exception{
		if(listaRecolector.size() > 0 &&  listaRecolector.get(0).getPermisoAmbiental() != null ){
			return getFilePermiso(listaRecolector.get(0));
		}
		return null;
	}
}
