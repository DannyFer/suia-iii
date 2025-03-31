
package ec.gob.ambiente.prevencion.categoria2.v2.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ParticipacionCiudadanaDescargarController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8177524605528893493L;

	private static final Logger LOG = Logger.getLogger(ParticipacionCiudadanaDescargarController.class);

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	

	@EJB
	private DocumentosFacade documentosFacade;	
	
	public static String ACTA_APERTURA_CIERRE= "_formato_acta_apertura_cierre_centro_informacion_publica.docx";
	public static String ACTA_ASAMBLEA_PRESENTACION= "_formato_acta_asamblea_presentacion_publica.docx";
	public static String CONVOCATORIA= "_formato_convocatoria.docx";
	public static String SISTEMATIZACION_PPC= "_formato_informe_sistematizacion_ppc.docx";
	public static String INVITACION_PERSONAL= "_formato_invitacion_personal.docx";
	public static String ASISTENCIA_ASAMBLEA= "_formato_registro_asistencia_asamblea_presentacion_publica.docx";
	public static String OBSERVACIONES= "_formato_registro_asistencia_observaciones_centro_informacion_publica.docx";
	public static String APLICACION_PPC= "_guia_aplicacion_ppc_registro_ambiental.pdf";
	public static String ACUERDO_MINISTERIAL= "acuerdo_ministerial_109.pdf";
	
	private byte[] acta_apertura_cierre_byte,acta_asamblea_presentacion_byte,convocatoria_byte,sistematizacion_ppc_byte,invitacion_personal_byte,asistencia_asamblea_byte,observaciones_byte,aplicacion_ppc_byte,acuerdo_ministerial_byte;
	private boolean acta_apertura_cierre_descargado,acta_asamblea_presentacion_descargado,convocatoria_descargado,sistematizacion_ppc_descargado,invitacion_personal_descargado,asistencia_asamblea_descargado,observaciones_descargado,aplicacion_ppc_descargado,acuerdo_ministerial_descargado;
	String maeGad;
	
	@PostConstruct
	public void init() {		
		buscarDocumentos();		
	}
	
	private void buscarDocumentos() {
		try {
			
			maeGad=proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()?"gad":"mae";
			
			acta_apertura_cierre_byte = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(maeGad+ACTA_APERTURA_CIERRE, null);
			acta_asamblea_presentacion_byte = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(maeGad+ACTA_ASAMBLEA_PRESENTACION, null);
			convocatoria_byte = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(maeGad+CONVOCATORIA, null);
			sistematizacion_ppc_byte = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(maeGad+SISTEMATIZACION_PPC, null);
			invitacion_personal_byte = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(maeGad+INVITACION_PERSONAL, null);
			asistencia_asamblea_byte = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(maeGad+ASISTENCIA_ASAMBLEA, null);
			observaciones_byte = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(maeGad+OBSERVACIONES, null);
			aplicacion_ppc_byte = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(maeGad+APLICACION_PPC, null);	
			acuerdo_ministerial_byte = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(ACUERDO_MINISTERIAL, null);
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al buscar los documetos ParticipacionCiudadanaDescargarController.");
		}
	}
	
	public StreamedContent descargarActaAperturaCierre() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (acta_apertura_cierre_byte != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(acta_apertura_cierre_byte));
				content.setName(maeGad+ACTA_APERTURA_CIERRE);
				acta_apertura_cierre_descargado=true;
				}
			} catch (Exception e) {
				e.printStackTrace();			
			}
		return content;
	}
	
	public StreamedContent descargarActaAsambleaPresentacion() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (acta_asamblea_presentacion_byte != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(acta_asamblea_presentacion_byte));
				content.setName(maeGad+ACTA_ASAMBLEA_PRESENTACION);
				acta_asamblea_presentacion_descargado=true;
				}
			} catch (Exception e) {
				e.printStackTrace();			
			}
		return content;
	}
	
	public StreamedContent descargarConvocatoria() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (convocatoria_byte != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(convocatoria_byte));
				content.setName(maeGad+CONVOCATORIA);
				convocatoria_descargado=true;
				}
			} catch (Exception e) {
				e.printStackTrace();			
			}
		return content;
	}
	
	public StreamedContent descargarSistematizacionPPC() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (sistematizacion_ppc_byte != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(sistematizacion_ppc_byte));
				content.setName(maeGad+SISTEMATIZACION_PPC);
				sistematizacion_ppc_descargado=true;
				}
			} catch (Exception e) {
				e.printStackTrace();			
			}
		return content;
	 }
	
	public StreamedContent descargarInvitacionPersonal() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (invitacion_personal_byte != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(invitacion_personal_byte));
				content.setName(maeGad+INVITACION_PERSONAL);
				invitacion_personal_descargado=true;
				}
			} catch (Exception e) {
				e.printStackTrace();			
			}
		return content;
	 }
	
	public StreamedContent descargarAsistenciaAsamblea() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (asistencia_asamblea_byte != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(asistencia_asamblea_byte));
				content.setName(maeGad+ASISTENCIA_ASAMBLEA);
				asistencia_asamblea_descargado=true;
				}
			} catch (Exception e) {
				e.printStackTrace();			
			}
		return content;
	 }
	
	public StreamedContent descargarObservaciones() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (observaciones_byte != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(observaciones_byte));
				content.setName(maeGad+OBSERVACIONES);
				observaciones_descargado=true;
				}
			} catch (Exception e) {
				e.printStackTrace();			
			}
		return content;
	}
	
	public StreamedContent descargarAplicacionPPC() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (aplicacion_ppc_byte != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(aplicacion_ppc_byte));
				content.setName(maeGad+APLICACION_PPC);
				aplicacion_ppc_descargado=true;
				}
			} catch (Exception e) {
				e.printStackTrace();			
			}
		return content;
	}
	
	public StreamedContent descargarAcuerdoMinisterial() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (acuerdo_ministerial_byte != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(acuerdo_ministerial_byte));
				content.setName(ACUERDO_MINISTERIAL);
				acuerdo_ministerial_descargado=true;
				}
			} catch (Exception e) {
				e.printStackTrace();			
			}
		return content;
	}
	
	public String guardar(boolean mineria) {
		try {
            /*if (!(acta_apertura_cierre_descargado&&acta_asamblea_presentacion_descargado&&convocatoria_descargado&&sistematizacion_ppc_descargado&&invitacion_personal_descargado&&asistencia_asamblea_descargado&&observaciones_descargado&&aplicacion_ppc_descargado&&acuerdo_ministerial_descargado)){
            	 JsfUtil.addMessageError("Debe descargar todos los documentos.");
                 return "";            	
            }*/

            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            
            return "/prevencion/categoria2/v2/ficha"+(mineria?"Mineria":"Ambiental")+"/participacionCiudadanaSubir.jsf?faces-redirect=true";

		} catch (Exception e) {
			LOG.error("error al guardar ", e);
			JsfUtil.addMessageError("Ocurrió un error al guardar la tarea");
			return "";
		}
	}
	
	public boolean ppc() throws ParseException {
		Date fechaproyecto=null;
		Date fechabloqueo=null;
		Date fechabloqueoObligatorioInicio=null;
		Date fechabloqueoObligatorioFin=null;
		Date fechabloqueoOpcionalInicio=null;
		Date fechabloqueoOpcionalFin=null;
		
		Date fechabloqueoSIN=null;
		boolean bloquear=false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		fechabloqueo = sdf.parse(Constantes.getFechaProyectosSinPccAntes());
		fechabloqueoObligatorioInicio = sdf.parse(Constantes.getFechaProyectosObligatorioPccInicio());
		fechabloqueoObligatorioFin = sdf.parse(Constantes.getFechaProyectosObligatorioPccFin());	
		fechabloqueoOpcionalInicio = sdf.parse(Constantes.getFechaProyectosOpcionalPccInicio());
		fechabloqueoOpcionalFin = sdf.parse(Constantes.getFechaProyectosOpcionalPccFin());	
		
		fechabloqueoSIN = sdf.parse(Constantes.getFechaProyectosSinPpcAdelante());
		
		fechaproyecto=sdf.parse(proyectosBean.getProyecto().getFechaRegistro().toString());
				
		if (fechaproyecto.after(fechabloqueoOpcionalInicio) && fechaproyecto.before(fechabloqueoOpcionalFin)){
			return true;
		}
				
		return bloquear;
		
	}
	
}