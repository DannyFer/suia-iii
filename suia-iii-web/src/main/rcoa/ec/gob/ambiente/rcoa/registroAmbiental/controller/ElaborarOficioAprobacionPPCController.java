package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.dto.EntityOficioPronunciamientoRegistroAPPC;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.registroAmbientalPPC.facade.OficioPronunciamientoRegistroAPPCFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.OficioPronunciamientoPPC;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.registroAmbiental.bean.DatosFichaRegistroAmbientalBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarPdf;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class ElaborarOficioAprobacionPPCController {
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentosFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private OficioPronunciamientoRegistroAPPCFacade OficioPronunciamientoRegistroAPPCFacade;
	
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
	@ManagedProperty(value = "#{datosOperadorRgdBean}")
    private DatosOperadorRgdBean datosOperadorRgdBean;
	
	@Getter
	@Setter
	private RegistroAmbientalRcoa registroAmbientalRcoa;
	
	@Getter
	@Setter
	private OficioPronunciamientoPPC oficioPronunciamiento;
	
	@Getter
	@Setter
	private DocumentoRegistroAmbiental documentoAdjunto, documentoOficioAprobacionPPC;
	
	@Getter
	@Setter
	private String urlReporte, codigoProyecto;
	
	@Getter
	@Setter
	private boolean activarFinalizar;

	private PlantillaReporte plantillaReporte;
	private Usuario usuarioAutoridad;
	private String nombreArchivo = "oficio_pronunciamiento_"+codigoProyecto+".pdf";
	private byte[] archivo=null;
	private Area areaResponsable;
	private Integer tipoOficio=1; 
	
	@PostConstruct
	private void init(){
		try {
			activarFinalizar=true;
	        documentoAdjunto = new DocumentoRegistroAmbiental();
	        documentoOficioAprobacionPPC = new DocumentoRegistroAmbiental();
			registroAmbientalRcoa = datosFichaRegistroAmbientalBean.getRegistroAmbientalRcoa();
			areaResponsable = registroAmbientalRcoa.getProyectoCoa().getAreaResponsable();
			datosOperadorRgdBean.buscarDatosOperador(registroAmbientalRcoa.getProyectoCoa().getUsuario());
			codigoProyecto = registroAmbientalRcoa.getProyectoCoa().getCodigoUnicoAmbiental();
			oficioPronunciamiento = OficioPronunciamientoRegistroAPPCFacade.obtenerPorCodigoProyectoTarea(registroAmbientalRcoa.getId(), tipoOficio, bandejaTareasBean.getTarea().getTaskId());
			if(oficioPronunciamiento == null || oficioPronunciamiento.getId() == null){
				oficioPronunciamiento = new OficioPronunciamientoPPC();
				oficioPronunciamiento.setRegistroAmbiental(registroAmbientalRcoa);
				oficioPronunciamiento.setEstado(true);
				oficioPronunciamiento.setTipo(tipoOficio);
				oficioPronunciamiento.setArea(areaResponsable);
				oficioPronunciamiento.setTareaId(((Number)bandejaTareasBean.getTarea().getTaskId()).intValue());
				oficioPronunciamiento.setCodigoDocumento(generarCodigoOficio(areaResponsable));
				OficioPronunciamientoRegistroAPPCFacade.guardar(oficioPronunciamiento, loginBean.getUsuario());
			}
			// busco la autoridad  ambiental que firma el documento
			List<Usuario> listaUsuario = null;
			String rolAutoridad = Constantes.getRoleAreaName("role.registro.ambiental.autoridad");
			if(areaResponsable.getTipoArea().getSiglas().equals("OT")){
				listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, areaResponsable.getArea());
			}else{
				listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, areaResponsable);
			}
			if(listaUsuario != null && listaUsuario.size() > 0){
				usuarioAutoridad = listaUsuario.get(0);
			}else{
				usuarioAutoridad = new Usuario();
			}
			generarInforme(true);
		} catch (Exception e) {
			e.printStackTrace();
		}    
	}
	
	public void generarInforme(Boolean marcaAgua) {
		try {
			nombreArchivo = "oficio_pronunciamiento_"+codigoProyecto+".pdf";
			plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_REGISTRO_AMBIENTAL_PPC);
			
			EntityOficioPronunciamientoRegistroAPPC entity = (EntityOficioPronunciamientoRegistroAPPC) cargarDatosOficio();

			File informePdfAux = UtilGenerarPdf.generarFichero(plantillaReporte.getHtmlPlantilla(),	nombreArchivo, true, entity);
			File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);
			Path path = Paths.get(informePdf.getAbsolutePath());
			archivo= Files.readAllBytes(path);
			String reporteHtmlfinal = nombreArchivo.replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(archivo);
			file.close();
			urlReporte = JsfUtil.devolverContexto("/reportesHtml/"+nombreArchivo);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (archivo != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(archivo), "application/octet-stream");
			content.setName(nombreArchivo);
		}
		return content;
	}
	
	private EntityOficioPronunciamientoRegistroAPPC cargarDatosOficio(){
		DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		EntityOficioPronunciamientoRegistroAPPC entidad = new EntityOficioPronunciamientoRegistroAPPC();
		entidad.setNumeroOficio(oficioPronunciamiento.getCodigoDocumento());
		entidad.setUbicacion(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		entidad.setFechaEmision(formatoFechaEmision.format(new Date()));
		entidad.setNombreOperador(datosOperadorRgdBean.getDatosOperador().getRepresentanteLegal());
		entidad.setAsunto(oficioPronunciamiento.getAsunto());
		entidad.setAntecedentes(oficioPronunciamiento.getAntecedentes());
		entidad.setPronunciamiento(oficioPronunciamiento.getPronunciamiento());
		entidad.setNombreFirma(usuarioAutoridad.getPersona().getNombre());
		return entidad;
	}
	
	private String generarCodigoOficio(Area area) {
		try {			
			String nombreSecuencia = "ESIA_OFICIO_RESPUESTA_"+ secuenciasFacade.getCurrentYear() + "_"	+ area.getAreaAbbreviation();
			String valorSecuencial = secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
			String secuencia = Constantes.SIGLAS_INSTITUCION + "-" + area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
			return secuencia + "-" + valorSecuencial + "-O";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void guardar(boolean mostrarMensaje){
		try {
			OficioPronunciamientoRegistroAPPCFacade.guardar(oficioPronunciamiento, loginBean.getUsuario());
			generarInforme(true);
			activarFinalizar=false;
			if(mostrarMensaje)
				JsfUtil.addMessageInfo("La información se guardo satisfactoriamente.");
		} catch (Exception ae) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void redireccionarBandeja(){
		JsfUtil.redirectToBandeja();
	}
	
	public void completarTarea(){
		guardar(false);
		if(usuarioAutoridad == null || usuarioAutoridad.getId() == null){
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return;
		}
		Map<String, Object> parametros = new HashMap<>();
		try {			
			parametros.put("autoridadAmbiental", usuarioAutoridad.getNombre());
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), parametros);
			redireccionarBandeja();
		} catch (JbpmException e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
}