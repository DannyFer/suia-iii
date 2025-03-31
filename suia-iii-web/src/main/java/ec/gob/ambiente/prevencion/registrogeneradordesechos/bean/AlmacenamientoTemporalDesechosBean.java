package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.prevencion.registrogeneradordesechos.controller.RegistroGeneradorDesechoController;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.comun.bean.AdicionarDesechosPeligrososBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.AlmacenGeneradorDesechos;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosDesechoPeligrosoDatosGenerales;
import ec.gob.ambiente.suia.domain.TipoEstadoFisico;
import ec.gob.ambiente.suia.domain.TipoIluminacion;
import ec.gob.ambiente.suia.domain.TipoLocal;
import ec.gob.ambiente.suia.domain.TipoMaterialConstruccion;
import ec.gob.ambiente.suia.domain.TipoVentilacion;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AlmacenamientoTemporalDesechosBean extends RegistroGeneradorBaseBean {

	private static final long serialVersionUID = 4998929221465850775L;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@Setter
	private List<AlmacenGeneradorDesechos> generadoresDesechosAlmacenes;

	private AlmacenGeneradorDesechos generadorDesechosAlmacen;

	@Setter
	@Getter
	private boolean esResponsabilidadExtendida;

	@Setter
	@Getter
	private String mensajeArchivo ="";

	@Getter
	private List<TipoLocal> tiposLocal;

	@Getter
	private List<TipoVentilacion> tiposVentilacion;

	@Getter
	private List<TipoIluminacion> tiposIluminacion;

	@Getter
	private List<TipoMaterialConstruccion> tiposMaterialConstruccion;

	@PostConstruct
	private void initAlmacenamiento() {
		tiposLocal = registroGeneradorDesechosFacade.getTiposLocal();
		tiposVentilacion = registroGeneradorDesechosFacade.getTiposVentilacion();
		tiposIluminacion = registroGeneradorDesechosFacade.getTiposIluminacion();
		tiposMaterialConstruccion = registroGeneradorDesechosFacade.getTiposMaterialConstruccion();
		mensajeArchivo= "Permiso ambiental del centro de acopio temporal ";
	}

	public void aceptar() {

		generadorDesechosAlmacen.setMedidasSeguridad(JsfUtil.getBean(MedidasSeguridadBean.class)
				.getTiposMedidasSeguridadSeleccionadas());
		if (JsfUtil.getBean(MedidasSeguridadBean.class).isOtroSeleccionado()) {
			generadorDesechosAlmacen.setTextoMedidaSeguridaOpcionOtro(JsfUtil.getBean(MedidasSeguridadBean.class)
					.getOtro());
		} else
			generadorDesechosAlmacen.setTextoMedidaSeguridaOpcionOtro(null);

		if (!getGeneradoresDesechosAlmacenes().contains(generadorDesechosAlmacen))
			getGeneradoresDesechosAlmacenes().add(generadorDesechosAlmacen);

		/*LLamar guardarpaso8*/
		try {
			JsfUtil.getBean(RegistroGeneradorDesechoController.class).guardarpaso8(generadorDesechosAlmacen,getGeneradoresDesechosAlmacenes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		JsfUtil.addCallbackParam("addAlmacen");
		cancelar();


	}


	public void cancelar() {
		generadorDesechosAlmacen = null;
		JsfUtil.getBean(AdicionarDesechosAlmacenBean.class).reset();
		JsfUtil.getBean(MedidasSeguridadBean.class).reset();
	}

	public void editar(AlmacenGeneradorDesechos almacen) {
		this.generadorDesechosAlmacen = almacen;
		setEditar(true);
		JsfUtil.getBean(MedidasSeguridadBean.class)
				.setTiposMedidasSeguridadSeleccionadas(almacen.getMedidasSeguridad());

		if (almacen.getTextoMedidaSeguridaOpcionOtroSalvado().isEmpty())
			JsfUtil.getBean(MedidasSeguridadBean.class).setOtroSeleccionado(false);
		else {
			JsfUtil.getBean(MedidasSeguridadBean.class).setOtroSeleccionado(true);
			JsfUtil.getBean(MedidasSeguridadBean.class).setOtro(almacen.getTextoMedidaSeguridaOpcionOtroSalvado());
		}

		JsfUtil.getBean(AdicionarDesechosAlmacenBean.class).setDesechosPeligrososSeleccionadas(
				almacen.getDesechosPeligrosos());
		JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setModificacionesEliminaciones(true);
	}

	public void eliminar(AlmacenGeneradorDesechos almacen) {
		getGeneradoresDesechosAlmacenes().remove(almacen);
		JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setModificacionesEliminaciones(true);
		try {
			JsfUtil.getBean(RegistroGeneradorDesechoController.class).eliminarAlmacenGeneradorDesechos(almacen);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JsfUtil.addCallbackParam("addAlmacen");
		cancelar();
	}

	public void validateData(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if (getGeneradorDesechosAlmacen().getDesechosPeligrosos().isEmpty()) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar el/los desecho(s) que contendrá este almacén.", null));
		}
		
		//Byron Burbano 2019-06-07
		//validacion de documento obligatorio de registro ambiental de almacenamiento temporal si es de responsabilidad extendida (no existe proyecto)
		if (esResponsabilidadExtendida && JsfUtil.getBean(RegistroGeneradorDesechoBean.class).getGeneradorDesechosPeligrosos().getProyecto() == null  && getGeneradorDesechosAlmacen().getPermisoAlmacenamientoTmp() == null){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe adjuntar el "+mensajeArchivo+".",	null));
		}
		/*
		 * if (JsfUtil.getBean(MedidasSeguridadBean.class).
		 * getTiposMedidasSeguridadSeleccionadas().isEmpty()) {
		 * errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
		 * "Debe seleccionar, al menos, una medida de seguridad.", null)); }
		 */
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void validateAlmacenesDesechos(FacesContext context, UIComponent validate, Object value) {
		if (getGeneradoresDesechosAlmacenes().isEmpty())
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe definir, al menos, un almacén.", null));

		List<DesechoPeligroso> desechos = JsfUtil.getBean(AdicionarDesechosPeligrososBean.class)
				.getDesechosSeleccionados();
		boolean desechosAlmacen = true;
		for (DesechoPeligroso desechoPeligroso : desechos) {
			if (!isDesechoInAlmacen(desechoPeligroso)) {
				desechosAlmacen = false;
				break;
			}
		}
		if (!desechosAlmacen)
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Queda(n) desecho(s) sin ubicar en algún almacén.", null));
	}

	private boolean isDesechoInAlmacen(DesechoPeligroso desechoPeligroso) {
		for (AlmacenGeneradorDesechos almacen : getGeneradoresDesechosAlmacenes()) {
			if (almacen.getDesechosPeligrosos().contains(desechoPeligroso))
				return true;
		}
		return false;
	}

	public void seleccionarDesechos() {
		JsfUtil.getBean(AdicionarDesechosAlmacenBean.class).reset();
		for (DesechoPeligroso desecho : getGeneradorDesechosAlmacen().getDesechosPeligrosos())
			desecho.setSeleccionado(true);

		JsfUtil.getBean(AdicionarDesechosAlmacenBean.class).setCompleteOperationOnAccept(new CompleteOperation() {

			@SuppressWarnings("unchecked")
			@Override
			public Object endOperation(Object object) {
				getGeneradorDesechosAlmacen().setDesechosPeligrosos((List<DesechoPeligroso>) object);
				return null;
			}
		});
	}

	public void eliminarDesechoAlmacen(DesechoPeligroso desechoPeligroso) {
		if (getGeneradorDesechosAlmacen().getDesechosPeligrosos().contains(desechoPeligroso)){
			getGeneradorDesechosAlmacen().getDesechosPeligrosos().remove(desechoPeligroso);
		};
		getGeneradorDesechosAlmacen().setEliminardesecho(true);
	}

	public List<AlmacenGeneradorDesechos> getGeneradoresDesechosAlmacenes() {
		return generadoresDesechosAlmacenes == null ? generadoresDesechosAlmacenes = new ArrayList<>()
				: generadoresDesechosAlmacenes;
	}

	public AlmacenGeneradorDesechos getGeneradorDesechosAlmacen() {
		return generadorDesechosAlmacen == null ? generadorDesechosAlmacen = new AlmacenGeneradorDesechos()
				: generadorDesechosAlmacen;
	}

	public Date getCurrentDate() {
		return new Date();
	}

	public void validacionesDesechos(DesechoPeligroso desechoPeligroso) {
		List<AlmacenGeneradorDesechos> almacenes = new ArrayList<AlmacenGeneradorDesechos>();
		almacenes.addAll(getGeneradoresDesechosAlmacenes());

		for (AlmacenGeneradorDesechos almacenGeneradorDesechos : almacenes) {
			almacenGeneradorDesechos.getDesechosPeligrosos().remove(desechoPeligroso);
			if (almacenGeneradorDesechos.getDesechosPeligrosos().isEmpty())
				getGeneradoresDesechosAlmacenes().remove(almacenGeneradorDesechos);
		}
	}

	public boolean isVerificarFosas() {
		List<DesechoPeligroso> desechos = getGeneradorDesechosAlmacen().getDesechosPeligrosos();
		if (desechos != null) {
			for (DesechoPeligroso desechoPeligroso : desechos) {
				List<GeneradorDesechosDesechoPeligrosoDatosGenerales> datos = JsfUtil.getBean(DatosDesechosBean.class)
						.getDesechosPeligrososDatosGenerales();
				if (datos != null) {
					for (GeneradorDesechosDesechoPeligrosoDatosGenerales datosGenerales : datos) {
						if (datosGenerales.getDesechoPeligroso().equals(desechoPeligroso)) {
							if (datosGenerales.getTipoEstadoFisico().getId()
									.equals(TipoEstadoFisico.TIPO_ESTADO_LIQUIDO)
									|| datosGenerales.getTipoEstadoFisico().getId()
									.equals(TipoEstadoFisico.TIPO_ESTADO_SEMISOLIDO))
								return true;
						}
					}
				}
			}
		}
		return false;
	}

/**
 * Byron Burbano 2019-06-07
 * metodod para cargo el documento de permiso ambiental de almacenamiento temporal
 * @param event
 */
	public void uploadListenerPermisoAlmacenamiento(FileUploadEvent event) {
		Documento documento = super.uploadListener(event, AlmacenGeneradorDesechos.class);
		getGeneradorDesechosAlmacen().setPermisoAlmacenamientoTmp(documento);
	}
	
	
	/**
	 * Byron Burbano 2019-06-10
	 * descarga del documento de permiso ambiental subido para procesos dRGD con responsabilidadextendida
	 * @throws Exception 
	 */
	public StreamedContent getFilePermiso(AlmacenGeneradorDesechos almacen) throws Exception{
		if(almacen.getPermisoAlmacenamientoTmp() != null ){
			String workspace = almacen.getPermisoAlmacenamientoTmp().getIdAlfresco();
			almacen.getPermisoAlmacenamientoTmp().setContenidoDocumento(documentosFacade.descargar(workspace));
			return getStreamContent(almacen.getPermisoAlmacenamientoTmp());
		}
		return null;
	}
}
