/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.validacion;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.suia.AutorizacionCatalogo.facade.AutorizacionCatalogoFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreasAutorizadasCatalogo;
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

/**
 * <b> Clase que carga la información de un proyecto mediante la url. </b>
 */
@ManagedBean
@ViewScoped
public class ValidarCertificadoBean implements Serializable {

	/**
	* 
	*/
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
					codigoCI = paramArray.get(1);
					tipoProyecto = Integer.parseInt(paramArray.get(2));
					nroActualizacion = Integer.parseInt(paramArray.get(3));
					
					recuperarInfoProyecto();
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void recuperarInfoProyecto() throws Exception {
		switch (tipoProyecto) {
		case 1:
			List<PermisoAmbiental> proyectosSuia = permisoAmbientalFacade.getProyectoSuiaPorcodigo(codigoProyecto);
			if(proyectosSuia.size() == 1) {
				permisoAmbientalSeleccionado = proyectosSuia.get(0);
			}
				
			ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigoSinFiltro(permisoAmbientalSeleccionado.getCodigo());
			permisoAmbientalSeleccionado.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
			
			CertificadoInterseccion certificadoInterseccion = certificadoInterseccionFacade.getCertificadoInterseccionCodigo(codigoCI);
			
			if(certificadoInterseccion != null) {
				DateFormat fecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
				
				fechaCI = fecha.format(certificadoInterseccion.getFechaCreacion());
				
				Usuario autoridad = usuarioFacade.buscarUsuarioWithOutFilters(certificadoInterseccion.getUsuarioFirma());
				permisoAmbientalSeleccionado.setAutoridadResponsable(autoridad.getPersona().getNombre());
				
				Area area = areaFacade.getArea(certificadoInterseccion.getAreaUsuarioFirma());
				if(area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
					area = area.getArea();
				permisoAmbientalSeleccionado.setAbreviacionArea(area.getAreaName());
	
				String comentarioInter = certificadoInterseccionFacade
						.getComentarioInterseccionActualizacion(
								permisoAmbientalSeleccionado.getCodigo(),
								DocumentoPDFPlantillaHtml
										.getValorResourcePlantillaInformes("comentario_proyecto_no_intersecta_actualizacion"),
								DocumentoPDFPlantillaHtml
										.getValorResourcePlantillaInformes("comentario_proyecto_si_intersecta"), nroActualizacion);
				if (comentarioInter.contains("SI INTERSECTA"))
					comentarioInter = comentarioInter.substring(0,
							comentarioInter.length() - 1);
				resultadoInterseccion = comentarioInter;
	
				if (permisoAmbientalSeleccionado.getEstado() == false) {
					archivado = true;
				}
				
				documentosActualizados= new ArrayList<Documento>();
		    	List<Integer> listaTipos = new ArrayList<Integer>();
				listaTipos.add(TipoDocumentoSistema.TIPO_COORDENADAS_ACTUALIZACION.getIdTipoDocumento());
				listaTipos.add(TipoDocumentoSistema.TIPO_CI_MAPA_ACTUALIZACION.getIdTipoDocumento());
				listaTipos.add(TipoDocumentoSistema.TIPO_CI_OFICIO_ACTUALIZACION.getIdTipoDocumento());
				
				List<Documento> documentos = documentosFacade.recuperarDocumentosPorTipo(
						permisoAmbientalSeleccionado.getId(), ProyectoLicenciamientoAmbiental.class.getSimpleName(), listaTipos);
				
				for (Documento documento : documentos) {
					if(documento.getDescripcion() != null && documento.getDescripcion().contains(nroActualizacion.toString()))
						documentosActualizados.add(documento);
				}
				
				mostrarInfo = true;
			}
			break;
		case 2:
			//4 cat
			List<PermisoAmbiental> proyectos4Cat = permisoAmbientalFacade.getProyectos4cat(codigoProyecto, null, null, null);
			if(proyectos4Cat.size() == 1) {
				permisoAmbientalSeleccionado = proyectos4Cat.get(0);
			}
			
			CertificadoInterseccion certificadoInterseccion4Cat = certificadoInterseccionFacade.getCertificadoInterseccionCodigo(codigoCI);
			
			if(certificadoInterseccion4Cat != null) {
				DateFormat fecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
				
				fechaCI = fecha.format(certificadoInterseccion4Cat.getFechaCreacion());
				
				Usuario autoridad = usuarioFacade.buscarUsuarioWithOutFilters(certificadoInterseccion4Cat.getUsuarioFirma());
				permisoAmbientalSeleccionado.setAutoridadResponsable(autoridad.getPersona().getNombre());
				
				Area area = areaFacade.getArea(certificadoInterseccion4Cat.getAreaUsuarioFirma());
				if(area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
					area = area.getArea();
				permisoAmbientalSeleccionado.setAbreviacionArea(area.getAreaName());
	
				String comentarioInter = certificadoInterseccionFacade
						.getComentarioInterseccionActualizacion(
								permisoAmbientalSeleccionado.getCodigo(),
								DocumentoPDFPlantillaHtml
										.getValorResourcePlantillaInformes("comentario_proyecto_no_intersecta_actualizacion"),
								DocumentoPDFPlantillaHtml
										.getValorResourcePlantillaInformes("comentario_proyecto_si_intersecta"), nroActualizacion);
				if (comentarioInter.contains("SI INTERSECTA"))
					comentarioInter = comentarioInter.substring(0,
							comentarioInter.length() - 1);
				resultadoInterseccion = comentarioInter;
	
				if (permisoAmbientalSeleccionado.getEstado() == false) {
					archivado = true;
				}
				
				documentosActualizados= new ArrayList<Documento>();
				String tiposDocumento = "''CertificadoInterseccionAutomatico'', ''coordUbicacion'', ''MapaCIFinal'', ''coordUbicacionImp''";
				List<Object[]> documentos = null;
				documentos = permisoAmbientalFacade.getDocumentosProyecto4cat(
						permisoAmbientalSeleccionado.getCodigo(),
						tiposDocumento,
						permisoAmbientalSeleccionado.getFuente());
				for (int i = 0; i < documentos.size(); i++) {
					Object[] documento = documentos.get(i);
					Integer actualizacion = Integer.parseInt(documento[5].toString());
					if(actualizacion.equals(nroActualizacion)) {
						String urlfile = null;
						Date fechaDocumento = new SimpleDateFormat("yyyy-MM-dd")
								.parse(documento[2].toString());

						if (documento[4] == null) {
							urlfile = documentosFacade.direccionDescarga(
									documento[0].toString(), fechaDocumento);
						} else {
							urlfile = documento[4].toString();
						}
						if (urlfile != "") {
							Documento doc = new Documento();
							doc.setNombre(documento[1].toString() + "." + documento[6].toString());
							doc.setIdAlfresco(urlfile);
							doc.setFechaCreacion(fechaDocumento);
							documentosActualizados.add(doc);
						}
					}
				}
				
				mostrarInfo = true;
			}
			break;
		case 3:
			//rcoa
			List<PermisoAmbiental> proyectosRcoa = permisoAmbientalFacade.getProyectoRcoaPorCodigo(codigoProyecto);
			if(proyectosRcoa.size() == 1) {
				permisoAmbientalSeleccionado = proyectosRcoa.get(0);
			}
			
			CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorProyectoNroActualizacion(codigoProyecto, nroActualizacion);
			if(oficioCI != null) {
				DateFormat fecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
				
				fechaCI = fecha.format((oficioCI.getFechaOficio() == null) ? new Date() : oficioCI.getFechaOficio());
				codigoCI = oficioCI.getCodigo();
				
				Usuario autoridad = usuarioFacade.buscarUsuarioWithOutFilters(oficioCI.getUsuarioFirma());
				permisoAmbientalSeleccionado.setAutoridadResponsable(autoridad.getPersona().getNombre());
				
				Area area = areaFacade.getArea(oficioCI.getAreaUsuarioFirma());
				if(area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
					area = area.getArea();
				permisoAmbientalSeleccionado.setAbreviacionArea(area.getAreaName());
	
				String comentarioInter = "falta";
				if (oficioCI.getInterseccionViabilidad() == null || oficioCI.getInterseccionViabilidad().isEmpty()) {
					comentarioInter = "<strong>NO INTERSECA</strong> con el Sistema Nacional de Áreas Protegidas (SNAP), Patrimonio Forestal Nacional y Zonas Intangibles.";
				} else {
					comentarioInter = "<strong>SI INTERSECA</strong> con el: <br />" + oficioCI.getInterseccionViabilidad();
				}
				resultadoInterseccion = comentarioInter;
	
				if (permisoAmbientalSeleccionado.getEstado() == false) {
					archivado = true;
				}
				
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
			}
			
			break;

		default:
			break;
		}
		
		getTipoRegistroAmbiental();
	}

	public void getTipoRegistroAmbiental() {
		if (permisoAmbientalSeleccionado.getCategoria() != null) {
			switch (permisoAmbientalSeleccionado.getCategoria()) {
			case "I":
				tipoTramite = "Certificado Ambiental";
				break;
			case "II":
				tipoTramite = "Registro Ambiental";
				break;
			case "III":
				tipoTramite = "Licencia Ambiental";
				break;
			case "IV":
				tipoTramite = "Licencia Ambiental";
				break;
			case "1":
				tipoTramite = "Certificado Ambiental";
				break;
			case "2":
				tipoTramite = "Registro Ambiental";
				break;
			case "3":
				tipoTramite = "Licencia Ambiental";
				break;
			case "4":
				tipoTramite = "Licencia Ambiental";
				break;
			default:
				tipoTramite = permisoAmbientalSeleccionado.getCategoria();
				break;
			}
		}
	}

	public Boolean esHidrocarburos(PermisoAmbiental proyecto) {
		Boolean isHidrocarburos = false;
		try {

			Usuario usuario = usuarioFacade.buscarUsuarioCompleta(proyecto
					.getCedulaProponente());
			if (usuario == null) {
				usuario = new Usuario();
				usuario.setNombre(proyecto.getCedulaProponente());
			}

			isHidrocarburos = integracionFacade.isProjectHydrocarbons(
					proyecto.getCodigo(), usuario.getNombre(),
					usuario.getPasswordSha1Base64());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ProyectoLicenciamientoAmbiental proyectoLicenciamiento = proyectoLicenciamientoAmbientalFacade
				.getProyectoPorCodigo(proyecto.getCodigo());
		if (proyectoLicenciamiento != null) {
			AreasAutorizadasCatalogo estacionServicio = autorizacionCatalogoFacade
					.getaAreasAutorizadasCatalogo(
							proyectoLicenciamiento.getCatalogoCategoria(),
							proyectoLicenciamiento.getAreaResponsable());
			if (estacionServicio != null) {
				if ((proyectoLicenciamientoAmbientalFacade.consultaTaskhyd4cat(proyecto.getCodigo()) > 0)) {
					isHidrocarburos = false;
				} else {
					if ((proyectoLicenciamientoAmbientalFacade.consultaTaskhyd(proyecto.getCodigo()) > 0)) {
						isHidrocarburos = true;
					} else {
						if (!estacionServicio.getActividadBloqueada()) {
							isHidrocarburos = true;
						}
					}
				}
			}
		}

		return isHidrocarburos;
	}
	
	public Documento descargarDocumentoActualizado(Documento documento) throws CmisAlfrescoException {
        byte[] documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null)
        	if(tipoProyecto.equals(3))
        		documentoContenido = documentosCoaFacade.descargar(documento.getIdAlfresco());
        	else
        		documentoContenido = documentosFacade.descargarContenido(documento.getIdAlfresco());
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
