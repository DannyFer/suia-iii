package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.ReporteSustancias;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.retce.model.TecnicoResponsable;
import ec.gob.ambiente.retce.services.CatalogoSustanciasRetceFacade;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.retce.services.ReporteSustanciasFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ReporteSustanciasRetceController implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private InformacionProyectoFacade informacionProyectoFacade;
	
	@EJB
	private ReporteSustanciasFacade reporteSustanciasFacade;
	
	@EJB
	private CatalogoSustanciasRetceFacade catalogoSustanciasRetceFacade;
	
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	
	@Getter
	@Setter
	private List<SubstanciasRetce> listaSustanciasRetce;
	
	@Getter
	@Setter
	private List<ReporteSustancias> listaReporteSustanciasRetce;
	
	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> tiposLaboratorio;
	
	@Getter
	@Setter
	private List<Integer> listaAnios;
	
	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	private ReporteSustancias reporteSustancias;
	
	@Getter
	@Setter
	private TecnicoResponsable tecnicoResponsable;
	
	@Getter
	@Setter
	private SubstanciasRetce sustanciaRetce;
	
	@Getter
	@Setter
	private Boolean verDetalleReporte, agregarNuevo, reporteHabilitado;
	
	@Getter
	@Setter
	private String inicioReporte, finReporte;
	
	@Getter
	@Setter
	private Integer ordenMedicionDirecta = 1;

	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			
			Integer idInformacionBasica =(Integer)(JsfUtil.devolverObjetoSession(InformacionProyecto.class.getSimpleName()));
			
			if (idInformacionBasica != null) {
				informacionProyecto = informacionProyectoFacade.findById(idInformacionBasica);
				JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName(), null);
			} else {
				return;
			}
			
			DetalleCatalogoGeneral periodoRegistro = detalleCatalogoGeneralFacade.findByCodigo("periodo.sustancias_retce");
			
			Calendar fecha = Calendar.getInstance();
			String fechaActualString = fecha.get(Calendar.DAY_OF_MONTH) + "/" + (fecha.get(Calendar.MONTH) + 1)  + "/" + fecha.get(Calendar.YEAR); //mes se suma 1 xq retorna enero 0
			Date fechaActual = new SimpleDateFormat("dd/MM/yyyy").parse(fechaActualString);
			
			Integer anioActual = JsfUtil.getYearFromDate(fechaActual);
			String inicioString = periodoRegistro.getParametro() + "/" + anioActual;
			String finString = periodoRegistro.getParametro2() + "/" + anioActual;
			Date fechaInicio = new SimpleDateFormat("dd/MM/yyyy").parse(inicioString);
			Date fechaFin = new SimpleDateFormat("dd/MM/yyyy").parse(finString);
			
			if(fechaActual.compareTo(fechaInicio) >= 0 && fechaActual.compareTo(fechaFin) <= 0) 
				reporteHabilitado = true;
			else
				reporteHabilitado = false;
			
			inicioReporte = JsfUtil.devuelveDiaMesEnLetras(fechaInicio);
			finReporte = JsfUtil.devuelveDiaMesEnLetras(fechaFin);
			
			cargarInicio();
			cargarInfoReporte();
			cargarListaAnios();
			
			tiposLaboratorio = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("laboratorio.tipo_laboratorio");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void cargarInicio() {
		tecnicoResponsable = new TecnicoResponsable();
		reporteSustancias = (reporteSustancias == null)  ? new ReporteSustancias() : reporteSustancias;

		verDetalleReporte = false;
		agregarNuevo = false;
		
		listaReporteSustanciasRetce = new ArrayList<>();		
	}
	
	private void cargarInfoReporte() {
		listaReporteSustanciasRetce = reporteSustanciasFacade.getByInformacionProyecto(informacionProyecto.getId());
	}
	
	private void cargarListaAnios() {
		Date nuevaFecha = new Date();
		Integer i= JsfUtil.getYearFromDate(nuevaFecha);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, -1);
		nuevaFecha = cal.getTime();
		listaAnios = new ArrayList<Integer>();

		for ( i=2019; i<= JsfUtil.getYearFromDate(nuevaFecha); i++){
			listaAnios.add(i);
		}
	}
	
	public void agregarReporte(){
		if (!reporteHabilitado) {
			JsfUtil.addMessageError("Está fuera del periodo de registro.");
			
			return;
		}
		
		reporteSustancias = new ReporteSustancias();
		
		verDetalleReporte = false;
		agregarNuevo = true;
	}
	
	public String seleccionarAnioReporte() {
		validarExisteReporte();
		
		return null;
	}
	
	public String iniciarReporte(){
		
		if(reporteSustancias.getId() == null) {
			if(!validarExisteReporte()) {
				reporteSustancias.setCodigoTramite(reporteSustanciasFacade.generarCodigoTramite());
				reporteSustancias.setInformacionProyecto(informacionProyecto);
				reporteSustancias.setRegistroFinalizado(false);
					
				reporteSustanciasFacade.guardarReporte(reporteSustancias);
			}
		}
		
		JsfUtil.cargarObjetoSession(ReporteSustancias.class.getSimpleName(), reporteSustancias.getId());
		return JsfUtil.actionNavigateTo("/control/retce/sustanciasRetce/sustanciasRetce.jsf");
	}
	
	public Boolean validarExisteReporte() {
		ReporteSustancias reporteRecursos =  reporteSustanciasFacade.getByInformacionProyectoAnio(informacionProyecto.getId(), reporteSustancias.getAnioDeclaracion());
		
		if(reporteRecursos != null) {
			if (reporteRecursos.getRegistroFinalizado()) {
				JsfUtil.addMessageError("Usted ya generó el reporte de Sustancias para el año seleccionado.");
				
				JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName(), informacionProyecto.getId());
				JsfUtil.redirectTo("/control/retce/sustanciasRetce.jsf");
			} 
			
			reporteSustancias = reporteRecursos;
			
			return true;
		}
		return false;
	}
	
	public String verInfoReporte(ReporteSustancias reporte, Integer accion){
		if(accion == 0){
			JsfUtil.cargarObjetoSession(ReporteSustancias.class.getSimpleName(), reporte.getId());
			return JsfUtil.actionNavigateTo("/control/retce/sustanciasRetce/sustanciasRetce.jsf");
		} else {
			reporteSustancias = reporte;
			tecnicoResponsable = reporte.getTecnicoResponsable();
			listaSustanciasRetce = reporte.getListaSustanciasRetce();
			
			if(listaSustanciasRetce != null) {				
				for (SubstanciasRetce sustancia : listaSustanciasRetce) {						
					if (sustancia.getDatosLaboratorio() != null) {
						List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(
								sustancia.getDatosLaboratorio().getId(),
								ReporteSustancias.class.getSimpleName(),
								TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO);
						
						if (documentos.size() > 0)
							sustancia.getDatosLaboratorio().setDocumentoLaboratorio(documentos.get(0));
					} 
				}
			}
			
			//cargar los datos de las sustancias
			
			verDetalleReporte = true;
			agregarNuevo = false;
			return null;
		}
	}
	
	public void eliminarReporte(ReporteSustancias reporte) {
		reporte.setEstado(false);
		reporteSustanciasFacade.eliminarConsumo(reporte);
		
		listaReporteSustanciasRetce.remove(reporte);
	}
	
	public StreamedContent descargar(Documento documento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documento.getIdAlfresco());
			} else if (documento.getContenidoDocumento() != null) {
				documentoContent = documento.getContenidoDocumento();
			}
			
			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void cerrarReporte() {
		verDetalleReporte = false;
	}
	
	public void cerrarNuevoReporte() {
		agregarNuevo = false;
	}

	public void validarData() {
		if (informacionProyecto == null) {
			JsfUtil.redirectTo("/control/retce/informacionBasica.jsf");
		}
	}
	
	public void verLaboratorio(SubstanciasRetce sustancia) {
		sustanciaRetce = sustancia;
	}
}
