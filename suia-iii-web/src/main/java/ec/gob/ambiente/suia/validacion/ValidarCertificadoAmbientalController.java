package ec.gob.ambiente.suia.validacion;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoCertificadoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.AutorizacionCatalogo.facade.AutorizacionCatalogoFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CertificadoInterseccion;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.PermisoAmbiental;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.PermisoAmbientalFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.EncriptarUtil;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ValidarCertificadoAmbientalController implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private DocumentosCoaFacade documentosCoaFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private IntegracionFacade integracionFacade;
	@EJB
	private AutorizacionCatalogoFacade autorizacionCatalogoFacade;
	@EJB
	private PermisoAmbientalFacade permisoAmbientalFacade;
	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProyectoCertificadoAmbientalFacade proyectoCertificadoAmbientalFacade;
	

	@Getter
	@Setter
	private List<Documento> documentosProyecto, documentosActualizados;
	
	@Getter
	@Setter
	private boolean enTramite = false;

	@Getter
	@Setter
	private Boolean mostrarInfo;
	
	@Getter
	@Setter
	private boolean archivado = false;

	@Getter
	@Setter
	private List<PermisoAmbiental> listaPermisosAmbientales;

	@Getter
	@Setter
	private PermisoAmbiental permisoAmbientalSeleccionado;
	
	@Getter
	@Setter
	private String codigoProyecto, codigoCI, fechaCI, resultadoInterseccion;
	
	@Getter
	@Setter
	private String tipoTramite;
	
	@Getter
	@Setter
	private Integer tipoProyecto, nroActualizacion;
	
	@Getter
	@Setter
	private Boolean mostrarCertificadoRcoa;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;
	
	@PostConstruct
	public void init() {
		try {
			mostrarInfo = false;
			
			String param = JsfUtil.getRequestParameter("p");
			if (param != null && !param.isEmpty()) {
				final String secretKey = "PassSistema5U14*-+/";
				String parametro = EncriptarUtil.desencriptar(param, secretKey);
				
				List<String> paramArray = Arrays.asList(parametro.split(";"));
				if(paramArray.size() > 0) {
					codigoProyecto = paramArray.get(0);
					tipoProyecto = Integer.parseInt(paramArray.get(1));
					
					recuperarInfoProyecto(tipoProyecto);
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void recuperarInfoProyecto(Integer tipo) throws Exception {
		
		if(tipo.equals(3)){
			mostrarCertificadoRcoa = true;
			List<PermisoAmbiental> proyectosRcoa = permisoAmbientalFacade.getProyectoRcoaPorCodigo(codigoProyecto);
			if(proyectosRcoa.size() == 1) {
				permisoAmbientalSeleccionado = proyectosRcoa.get(0);
			}
			
			ProyectoCertificadoAmbiental certificadoAmbiental = new ProyectoCertificadoAmbiental();
			ProyectoLicenciaCoa proyecto =  proyectoLicenciaCoaFacade.buscarProyecto(codigoProyecto);
			certificadoAmbiental = proyectoCertificadoAmbientalFacade.getProyectoCertificadoAmbientalPorCodigoProyecto(proyecto);
			
			CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
					
			Area area = areaFacade.getArea(certificadoAmbiental.getIdArea());
			
			if(area.getTipoArea().getSiglas().equals("OT")){
				permisoAmbientalSeleccionado.setAbreviacionArea(area.getArea().getAreaName());
			}else{
				permisoAmbientalSeleccionado.setAbreviacionArea(area.getAreaName());
			}
			
			
			setCodigoCI(certificadoAmbiental.getCodigoCertificado());
			
			DateFormat fecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
			
			fechaCI = fecha.format(certificadoAmbiental.getFechaCreacion());	
			
			Usuario autoridad = usuarioFacade.buscarUsuarioPorId(certificadoAmbiental.getIdUsuario());
			
			permisoAmbientalSeleccionado.setAutoridadResponsable(autoridad.getPersona().getNombre());				
					
			tipoTramite = "Certificado Ambiental";
			nroActualizacion = oficioCI.getNroActualizacion();
			TipoDocumentoSistema tipoDocumentoCertificado = (nroActualizacion == 0) ? TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO : TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO_ACTUALIZADO;
			TipoDocumentoSistema tipoDocumentoMapa = (nroActualizacion == 0) ? TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA : TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA_ACTUALIZADO;
			TipoDocumentoSistema tipoDocumentoCoordGeo = (nroActualizacion == 0) ? TipoDocumentoSistema.RCOA_COORDENADA_GEOGRAFICA : TipoDocumentoSistema.RCOA_COORDENADA_GEOGRAFICA_ACTUALIZADA;
			TipoDocumentoSistema tipoDocumentoCoordImp = (nroActualizacion == 0) ? TipoDocumentoSistema.RCOA_COORDENADA_IMPLANTACION : TipoDocumentoSistema.RCOA_COORDENADA_IMPLANTACION_ACTUALIZADA;
			
			documentosActualizados= new ArrayList<Documento>();
			List<DocumentosCOA> listaDocumentosInt = documentosCoaFacade.documentoXTablaIdXIdDocXNroActualizacion(oficioCI.getId(), tipoDocumentoCertificado, "CertificadoInterseccionOficioCoa", nroActualizacion);
			if (listaDocumentosInt.size() > 0) {
				DocumentosCOA documentoRcoa = listaDocumentosInt.get(0);
				Documento doc = new Documento();
				doc.setIdAlfresco(documentoRcoa.getIdAlfresco());
				doc.setNombre(documentoRcoa.getNombreDocumento());
				doc.setFechaCreacion(documentoRcoa.getFechaCreacion());
				documentosActualizados.add(doc);
			}
			List<DocumentosCOA> listaDocumentosMap = documentosCoaFacade.documentoXTablaIdXIdDocXNroActualizacion(permisoAmbientalSeleccionado.getId(), tipoDocumentoMapa, "ProyectoLicenciaCoa", nroActualizacion);		
			if (listaDocumentosMap.size() > 0) {
				DocumentosCOA documentoRcoa = listaDocumentosMap.get(0);
				Documento doc = new Documento();
				doc.setIdAlfresco(documentoRcoa.getIdAlfresco());
				doc.setNombre(documentoRcoa.getNombreDocumento());
				doc.setFechaCreacion(documentoRcoa.getFechaCreacion());
				documentosActualizados.add(doc);
			}
			List<DocumentosCOA> listaDocumentosCoordenadaG = documentosCoaFacade.documentoXTablaIdXIdDocXNroActualizacion(permisoAmbientalSeleccionado.getId(), tipoDocumentoCoordGeo, "ProyectoLicenciaCoa", nroActualizacion);		
			if (listaDocumentosCoordenadaG.size() > 0) {
				DocumentosCOA documentoRcoa = listaDocumentosCoordenadaG.get(0);
				Documento doc = new Documento();
				doc.setIdAlfresco(documentoRcoa.getIdAlfresco());
				doc.setNombre(documentoRcoa.getNombreDocumento());
				doc.setFechaCreacion(documentoRcoa.getFechaCreacion());
				documentosActualizados.add(doc);
			}
			
			List<DocumentosCOA> listaDocumentosCoordenadaI = documentosCoaFacade.documentoXTablaIdXIdDocXNroActualizacion(permisoAmbientalSeleccionado.getId(), tipoDocumentoCoordImp, "ProyectoLicenciaCoa", nroActualizacion);		
			if (listaDocumentosCoordenadaI.size() > 0) {
				DocumentosCOA documentoRcoa = listaDocumentosCoordenadaI.get(0);
				Documento doc = new Documento();
				doc.setIdAlfresco(documentoRcoa.getIdAlfresco());
				doc.setNombre(documentoRcoa.getNombreDocumento());
				doc.setFechaCreacion(documentoRcoa.getFechaCreacion());
				documentosActualizados.add(doc);
			}
			
			
			mostrarInfo = true;
		}else{
			//suia anterior
			mostrarCertificadoRcoa = false;
			List<PermisoAmbiental> proyectos = permisoAmbientalFacade.getProyectoSuiaPorcodigo(codigoProyecto);
			
			if(proyectos.size() == 1) {
				permisoAmbientalSeleccionado = proyectos.get(0);
			}
			
			ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigoSinFiltro(permisoAmbientalSeleccionado.getCodigo());
			permisoAmbientalSeleccionado.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
						
				
			Area area = proyectoLicenciamientoAmbiental.getAreaResponsable(); //areaFacade.getArea(certificadoInterseccion.getAreaUsuarioFirma());
			
			Documento documentoCertificado = new Documento();
			if(area.getTipoArea().getSiglas().equals("EA")){
				documentoCertificado = documentosFacade.documentoXTablaIdXIdDocUnico(proyectoLicenciamientoAmbiental.getId(), "ProyectoLicenciamientoAmbiental", TipoDocumentoSistema.TIPO_CERTIFICADO_CATEGORIA_UNO);
			}else{
				documentoCertificado = documentosFacade.documentoXTablaIdXIdDocUnico(proyectoLicenciamientoAmbiental.getId(), "ProyectoLicenciamientoAmbiental", TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_MAE);
			}
			
			
			if(documentoCertificado != null && documentoCertificado.getId() != null){
				setCodigoCI(documentoCertificado.getCodigoPublico());
				DateFormat fecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
				
				fechaCI = fecha.format(documentoCertificado.getFechaCreacion());
				
				Usuario usuario = usuarioFacade.buscarUsuario(documentoCertificado.getUsuarioCreacion());
				
				permisoAmbientalSeleccionado.setAutoridadResponsable(usuario.getPersona().getNombre());
			}
			tipoTramite = "Certificado Ambiental";
			
			permisoAmbientalSeleccionado.setAbreviacionArea(area.getAreaName());
							
			documentosActualizados= new ArrayList<Documento>();
	    	List<Integer> listaTipos = new ArrayList<Integer>();
			listaTipos.add(TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA.getIdTipoDocumento());
			listaTipos.add(TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_OFICIO.getIdTipoDocumento());
			listaTipos.add(TipoDocumentoSistema.TIPO_COORDENADAS.getIdTipoDocumento());
			
			List<Documento> documentos = documentosFacade.recuperarDocumentosPorTipo(
					permisoAmbientalSeleccionado.getId(), ProyectoLicenciamientoAmbiental.class.getSimpleName(), listaTipos);
			
			for (Documento documento : documentos) {
				documentosActualizados.add(documento);
			}
				
			mostrarInfo = true;
		}	
		
	}
	
	public Documento descargarDocumentoActualizado(Documento documento) throws CmisAlfrescoException {
        byte[] documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null){        	
        	documentoContenido = documentosCoaFacade.descargar(documento.getIdAlfresco());
        }
        	
        if (documentoContenido != null)
            documento.setContenidoDocumento(documentoContenido);
        
        try {
        	if(documento.getNombre().contains("pdf"))
        		JsfUtil.descargarPdf(documentoContenido, documento.getNombre().replace(".pdf", ""));
        	else 
        		UtilDocumento.descargarExcel(documentoContenido, "Coordenadas del proyecto");
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        return documento;
    }

}
