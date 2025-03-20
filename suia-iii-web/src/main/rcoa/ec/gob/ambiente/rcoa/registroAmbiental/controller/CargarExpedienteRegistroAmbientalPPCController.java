package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.registroAmbiental.bean.DatosFichaRegistroAmbientalBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class CargarExpedienteRegistroAmbientalPPCController {
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentosFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
	@ManagedProperty(value = "#{datosFichaRegistroAmbientalBean}")
    private DatosFichaRegistroAmbientalBean datosFichaRegistroAmbientalBean;
	
	@Getter
	@Setter
	private RegistroAmbientalRcoa registroAmbientalRcoa;
	
	@Getter
	@Setter
	private List<DocumentoRegistroAmbiental> listaDocumentoFicha, listaDocumentoResolucion, listaDocumentoFactura;
	
	@Getter
	@Setter
	private DocumentoRegistroAmbiental documentoAdjunto;
	
	@Getter
	@Setter
	private String tipoDocumentoSelected;
	
	@Getter
	@Setter
	private boolean activarEnviar;

	private TipoDocumentoSistema tipoDocumentoSistema;
		
	@PostConstruct
	private void init(){
		try {
			activarEnviar=false;
			listaDocumentoFicha = new ArrayList<DocumentoRegistroAmbiental>();
			listaDocumentoResolucion = new ArrayList<DocumentoRegistroAmbiental>();
			listaDocumentoFactura = new ArrayList<DocumentoRegistroAmbiental>();
	        documentoAdjunto = new DocumentoRegistroAmbiental();
			registroAmbientalRcoa = datosFichaRegistroAmbientalBean.getRegistroAmbientalRcoa();
			listaDocumentoFicha = documentosFacade.documentoXTablaIdXIdDocLista(registroAmbientalRcoa.getId(), RegistroAmbientalRcoa.class.getSimpleName(), TipoDocumentoSistema.REGISTRO_AMBIENTAL_PPC_FISICO);
			listaDocumentoResolucion = documentosFacade.documentoXTablaIdXIdDocLista(registroAmbientalRcoa.getId(), RegistroAmbientalRcoa.class.getSimpleName(), TipoDocumentoSistema.RESOLUCION_AMBIENTAL_PPC_FISICO);
			listaDocumentoFactura = documentosFacade.documentoXTablaIdXIdDocLista(registroAmbientalRcoa.getId(), RegistroAmbientalRcoa.class.getSimpleName(), TipoDocumentoSistema.FACTURA_REGISTRO_AMBIENTAL_PPC);
			cargarDocumentos(listaDocumentoFicha);
			cargarDocumentos(listaDocumentoResolucion);
			cargarDocumentos(listaDocumentoFactura);
		} catch (Exception e) {
			e.printStackTrace();
		}    
	}

	public void uploadDodumento(FileUploadEvent file) {	
        String[] split=file.getFile().getContentType().split("/");
        String extension = "."+split[split.length-1];
        documentoAdjunto = new DocumentoRegistroAmbiental();
        documentoAdjunto.setNombre(file.getFile().getFileName());
        documentoAdjunto.setMime(file.getFile().getContentType());
        documentoAdjunto.setContenidoDocumento(file.getFile().getContents());
        documentoAdjunto.setExtesion(extension);
        documentoAdjunto.setSubido(false);
        documentoAdjunto.setNombreTabla(RegistroAmbientalRcoa.class.getSimpleName());
    }
	
	public void guardarAdjunto(){
		try {
			switch (tipoDocumentoSelected) {
			case "REGISTRO":
				tipoDocumentoSistema = TipoDocumentoSistema.REGISTRO_AMBIENTAL_PPC_FISICO;
				break;
			case "RESOLUCION":
				tipoDocumentoSistema = TipoDocumentoSistema.RESOLUCION_AMBIENTAL_PPC_FISICO;
				break;
			case "FACTURA":
				tipoDocumentoSistema = TipoDocumentoSistema.FACTURA_REGISTRO_AMBIENTAL_PPC;
				break;

			default:
				break;
			}
			if(documentoAdjunto != null && documentoAdjunto.getContenidoDocumento() != null && !documentoAdjunto.getSubido()){
				byte[] byteContenido = documentoAdjunto.getContenidoDocumento();
				documentoAdjunto.setIdTable(registroAmbientalRcoa.getId());
				documentoAdjunto.setRegistroAmbientalId(registroAmbientalRcoa.getId());
				documentoAdjunto = documentosFacade.ingresarDocumento(documentoAdjunto, registroAmbientalRcoa.getId(), registroAmbientalRcoa.getProyectoCoa().getCodigoUnicoAmbiental(), tipoDocumentoSistema, documentoAdjunto.getNombre(), RegistroAmbientalRcoa.class.getSimpleName(), bandejaTareasBean.getProcessId());
				documentoAdjunto.setSubido(true);
				documentoAdjunto.setContenidoDocumento(byteContenido);
			}
			cargarDocumentos();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void cargarDocumentos(){
		try {
			switch (tipoDocumentoSelected) {
				case "REGISTRO":
					tipoDocumentoSistema = TipoDocumentoSistema.REGISTRO_AMBIENTAL_PPC_FISICO;
					listaDocumentoFicha = documentosFacade.documentoXTablaIdXIdDocLista(registroAmbientalRcoa.getId(), RegistroAmbientalRcoa.class.getSimpleName(), tipoDocumentoSistema);
					cargarDocumentos(listaDocumentoFicha);
					break;
				case "RESOLUCION":
					tipoDocumentoSistema = TipoDocumentoSistema.RESOLUCION_AMBIENTAL_PPC_FISICO;
					listaDocumentoResolucion = documentosFacade.documentoXTablaIdXIdDocLista(registroAmbientalRcoa.getId(), RegistroAmbientalRcoa.class.getSimpleName(), tipoDocumentoSistema);
					cargarDocumentos(listaDocumentoResolucion);
					break;
				case "FACTURA":
					tipoDocumentoSistema = TipoDocumentoSistema.FACTURA_REGISTRO_AMBIENTAL_PPC;
					listaDocumentoFactura = documentosFacade.documentoXTablaIdXIdDocLista(registroAmbientalRcoa.getId(), RegistroAmbientalRcoa.class.getSimpleName(), tipoDocumentoSistema);
					cargarDocumentos(listaDocumentoFactura);
					break;
		
				default:
					break;
				}
		} catch (CmisAlfrescoException e) {
			e.printStackTrace();
		}
	}
	
	private void cargarDocumentos(List<DocumentoRegistroAmbiental> listaDocumento){
		try {
			if(listaDocumento != null && listaDocumento .size() > 0){
				for (DocumentoRegistroAmbiental objDocumento : listaDocumento) {
					objDocumento.setContenidoDocumento(documentosFacade.descargar(objDocumento.getAlfrescoId()));
				}
			}
		} catch (CmisAlfrescoException e) {
			e.printStackTrace();
		}
	}
	
	public StreamedContent getDocumentoDownload(DocumentoRegistroAmbiental objDocumento){
			try {
				if (objDocumento != null && objDocumento.getContenidoDocumento() !=null) {
					StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(objDocumento.getContenidoDocumento()),objDocumento.getMime(),objDocumento.getNombre());
					return streamedContent;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
	}

	public void eliminarDocumentoList(DocumentoRegistroAmbiental objDocumento, String tipo){
			try {
				objDocumento.setEstado(false);
				documentosFacade.guardar(objDocumento);
				switch (tipo) {
				case "REGISTRO":
					listaDocumentoFicha.remove(objDocumento);
					break;
				case "RESOLUCION":
					listaDocumentoResolucion.remove(objDocumento);
					break;
				case "FACTURA":
					listaDocumentoFactura.remove(objDocumento);
					break;

				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public void seleccionaDocumento(String tipo){
        documentoAdjunto = new DocumentoRegistroAmbiental();
		tipoDocumentoSelected = tipo;
	}

	public boolean validateDatosIngreso() {
		boolean corrector = true;
		if(listaDocumentoFicha == null || listaDocumentoFicha.size() == 0){
			JsfUtil.addMessageError("La Ficha Ambiental otorgada es requerida.");
			corrector = false;
		}
		if(listaDocumentoResolucion == null || listaDocumentoResolucion.size() == 0){
			JsfUtil.addMessageError("La Resolución de Registro Ambiental otorgada es requerida.");
			corrector = false;
		}if(listaDocumentoFactura == null || listaDocumentoFactura.size() == 0){
			JsfUtil.addMessageError("La Factura por emisión de Autorización Administrativa Ambiental es requerida.");
			corrector = false;
		}
		return corrector;
	}
	
	public void guardar(boolean mostrarMensaje){
		try {
			//valido que no este duplicado el numero de resolucion
			if(validarResolucion()){
				JsfUtil.addMessageError("El número de resolución ingresado ya existe.");
				return;
			}
			activarEnviar=false;
			registroAmbientalCoaFacade.guardar(registroAmbientalRcoa);
			if(mostrarMensaje)
				JsfUtil.addMessageInfo("La información se guardo satisfactoriamente.");
			activarEnviar=true;
		} catch (Exception ae) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	private boolean validarResolucion(){
		Integer numeroregistros =  registroAmbientalCoaFacade.contarRegistroAmbientalPorResolucion(registroAmbientalRcoa.getId() != null ? registroAmbientalRcoa.getId() : 0, registroAmbientalRcoa.getResolucionAmbietalFisica());
		return numeroregistros > 0;
	}
	
	public void redireccionarBandeja(){
		JsfUtil.redirectToBandeja();
	}
	
	public void completarTarea(){
		if(!validateDatosIngreso())
			return;
		guardar(false);
		Map<String, Object> parametros = new HashMap<>();
		try {			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), parametros);
			redireccionarBandeja();
		} catch (JbpmException e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
}