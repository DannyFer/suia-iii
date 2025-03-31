package ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.suia.comun.bean.AdicionarUbicacionesBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class UbicacionPuntosBean extends UbicacionPuntosBaseBean {
	
	@Setter
    private List<PuntoRecuperacionRgdRcoa> puntosRecuperacion;
	
    private PuntoRecuperacionRgdRcoa puntoRecuperacion;

    @Getter
    @Setter
    private boolean tipoDocumentoCertificadoCompatibilidad = true;
    
    public PuntoRecuperacionRgdRcoa getPuntoRecuperacion() {
        return puntoRecuperacion == null ? puntoRecuperacion = new PuntoRecuperacionRgdRcoa() : puntoRecuperacion;
    }


    public void aceptar() {
  
        puntoRecuperacion.setUbicacionesGeografica(JsfUtil.getBean(AdicionarUbicacionesBean.class).getUbicacionSeleccionada());
//        puntoRecuperacion.setFormasPuntoRecuperacionRgdRcoa(getFormasPuntoRecuperacion());
        

        if (!puntosRecuperacion.contains(puntoRecuperacion))
            puntosRecuperacion.add(puntoRecuperacion);
        /**
         * autor: vear
         * fecha 24/02/2016
         * objetivo: aumentado para persistir el paso 3 del wizard
         */
//        try {
//            JsfUtil.getBean(RegistroGeneradorDesechoController.class).guardarPaso3(puntoRecuperacion);
//        } catch (CmisAlfrescoException e) {
//            e.printStackTrace();
//        }
        cancelar();
        JsfUtil.addCallbackParam("addPunto");

    }

    public void cancelar() {
        puntoRecuperacion = null;
        tipoDocumentoCertificadoCompatibilidad = true;
        JsfUtil.getBean(AdicionarUbicacionesBean.class).resetSelections();
//        JsfUtil.getBean(CargarCoordenadasBean.class).reset();
    }
    
	public boolean validarDocumentoUsoSuelos(){

//    	GeneradorDesechosPeligrosos desechosPeligrosos= new GeneradorDesechosPeligrosos();    	
//    	desechosPeligrosos=JsfUtil.getBean(RegistroGeneradorDesechoBean.class).getGeneradorDesechosPeligrosos();
//        SimpleDateFormat simpleDate= new SimpleDateFormat("yyyy-MM-dd");
//        Date dateRGD;
//        Date dateCompare;
//		try {
//			dateRGD = simpleDate.parse(desechosPeligrosos.getFechaCreacion().toString());
//			SimpleDateFormat simple= new SimpleDateFormat("yyyy-MM-dd");
//			//fecha para quitar los cetificados de uso de suelos según el artículo 061 
//			dateCompare=simple.parse(Constantes.getFechaAcuerdoRgdUsoSuelos());
//	    	if(dateRGD.after(dateCompare)){
//	    		return false;
//	    	}
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return true;
    }

//    public void editar(PuntoRecuperacion puntoRecuperacion) {
//        this.puntoRecuperacion = puntoRecuperacion;
//        this.setEditar(true);
//        JsfUtil.getBean(AdicionarUbicacionesBean.class).setUbicacionSeleccionada(
//                puntoRecuperacion.getUbicacionesGeografica(), 3);
//        setFormasPuntoRecuperacion(puntoRecuperacion.getFormasPuntoRecuperacion());
//        if (puntoRecuperacion.getCertificadoUsoSuelos() != null)
//            tipoDocumentoCertificadoCompatibilidad = true;
//        else
//            tipoDocumentoCertificadoCompatibilidad = false;
//        JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setModificacionesEliminaciones(true);
//    }

//    public void eliminar(PuntoRecuperacion puntoRecuperacion) {
//        puntosRecuperacion.remove(puntoRecuperacion);
//        JsfUtil.getBean(JustificacionProponenteBean.class).setSeEliminoPuntoGeneracionODesecho(true);
//        JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setModificacionesEliminaciones(true);
//        /**
//         * autor: vear
//         * fecha: 03/03/2016
//         * objetivo: eliminar de la lista y de la bdd tambien
//         */
//        JsfUtil.getBean(RegistroGeneradorDesechoController.class).eliminarPuntoRecuperacion(puntoRecuperacion);
//    }




//    public void uploadListenerCertificadoCompatibilidad(FileUploadEvent event) {
//        Documento documento = super.uploadListener(event, PuntoRecuperacion.class);
//        getPuntoRecuperacion().setCertificadoUsoSuelos(documento);
//    }
//
//    public void uploadListenerCertificadoEmisionCompatibilidad(FileUploadEvent event) {
//        Documento documento = super.uploadListener(event, PuntoRecuperacion.class);
//        getPuntoRecuperacion().setCertificadoCompatibilidadUsoSuelos(documento);
//    }
//
//    public void validateData(FacesContext context, UIComponent validate, Object value) {
//        if(!validarDocumentoUsoSuelos())
//        	return;
//        
//        List<FacesMessage> errorMessages = new ArrayList<>();
//        if (JsfUtil.getBean(CargarCoordenadasBean.class).getCoordinatesWrappers().isEmpty()) {
//            errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                    "No se han definido las coordenadas de este punto de recuperación/generación.", null));
//        }
//        if (getPuntoRecuperacion().getCertificadoUsoSuelos() == null && tipoDocumentoCertificadoCompatibilidad) {
//            errorMessages
//                    .add(new FacesMessage(
//                            FacesMessage.SEVERITY_ERROR,
//                            "Para este punto de recuperación/generación debe adjuntar el Certificado/informe de compatibilidad de uso de suelos.",
//                            null));
//        }
//        if (getPuntoRecuperacion().getCertificadoCompatibilidadUsoSuelos() == null
//                && !tipoDocumentoCertificadoCompatibilidad) {
//            errorMessages
//                    .add(new FacesMessage(
//                            FacesMessage.SEVERITY_ERROR,
//                            "Para este punto de recuperación/generación debe adjuntar la solicitud de emisión de informe de compatibilidad de uso de suelo o afines.",
//                            null));
//        }
//        if (!errorMessages.isEmpty())
//            throw new ValidatorException(errorMessages);
//    }

    public void validatePuntosRecuperacion(FacesContext context, UIComponent validate, Object value) {
        if (puntosRecuperacion.isEmpty())
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Debe adicionar, al menos, un punto de recuperación/generación.", null));
    }

//    public List<PuntoRecuperacion> getPuntosRecuperacion() {
//        return puntosRecuperacion == null ? puntosRecuperacion = new ArrayList<>() : puntosRecuperacion;
//    }
//
//    public PuntoRecuperacion getPuntoRecuperacion() {
//        return puntoRecuperacion == null ? puntoRecuperacion = new PuntoRecuperacion() : puntoRecuperacion;
//    }
//
//    private List<FormaPuntoRecuperacionRgdRcoa> getFormasPuntoRecuperacion() {
//        List<FormaPuntoRecuperacionRgdRcoa> result = new ArrayList<FormaPuntoRecuperacionRgdRcoa>();
//        Iterator<CoordinatesWrapper> coords = JsfUtil.getBean(CargarCoordenadasBean.class).getCoordinatesWrappers()
//                .iterator();
//        while (coords.hasNext()) {
//            CoordinatesWrapper coordinatesWrapper = coords.next();
//            FormaPuntoRecuperacion formaPuntoRecuperacion = new FormaPuntoRecuperacion();
//            formaPuntoRecuperacion.setTipoForma(coordinatesWrapper.getTipoForma());
//            formaPuntoRecuperacion.setCoordenadas(coordinatesWrapper.getCoordenadas());
//            result.add(formaPuntoRecuperacion);
//        }
//        return result;
//    }
//
//    private void setFormasPuntoRecuperacion(List<FormaPuntoRecuperacion> formas) {
//        List<CoordinatesWrapper> coords = new ArrayList<>();
//        for (FormaPuntoRecuperacion formaPuntoRecuperacion : formas) {
//            CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper();
//            coordinatesWrapper.setTipoForma(formaPuntoRecuperacion.getTipoForma());
//            coordinatesWrapper.setCoordenadas(formaPuntoRecuperacion.getCoordenadas());
//            coords.add(coordinatesWrapper);
//        }
//        JsfUtil.getBean(CargarCoordenadasBean.class).setCoordinatesWrappers(coords);
//    }
//
//    public List<Integer> getIdCantonesPuntosRecuperacion() {
//        List<Integer> idsCantones = new ArrayList<Integer>();
//        for (PuntoRecuperacion puntoRecuperacion : getPuntosRecuperacion()) {
//            idsCantones.add(puntoRecuperacion.getUbicacionesGeografica().getUbicacionesGeografica().getId());
//        }
//        return idsCantones;
//    }

}
