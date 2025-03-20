/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.categoria1.facade;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CertificadoRegistroAmbiental;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.DetalleInterseccionProyecto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentoProyecto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.InterseccionProyecto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.entidadResponsable.facade.EntidadResponsableFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.facade.ReportesFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 23/01/2015]
 *          </p>
 */
@Stateless
public class Categoria1Facade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private ReportesFacade reportesFacade;

	@EJB
	private EntidadResponsableFacade entidadResponsableFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	@EJB
	private ContactoFacade contactoFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionService;
	
    @EJB
    private MensajeNotificacionFacade variableFacade;

	private static final Logger LOG = Logger.getLogger(Categoria1Facade.class);

	public void modificarRegistro(EntidadBase objeto) {
		crudServiceBean.saveOrUpdate(objeto);
	}

	public void guardarRegistro(EntidadBase objeto) {
		crudServiceBean.saveOrUpdate(objeto);
	}

	public void delete(EntidadBase objeto) {
		crudServiceBean.delete(objeto);
	}

	public CertificadoRegistroAmbiental getCertificadoRegistroAmbiental(int id) {
		return crudServiceBean.find(CertificadoRegistroAmbiental.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<CertificadoRegistroAmbiental> getCertificadoRegistroAmbientalPorIdProyecto(
			int idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);

		List<CertificadoRegistroAmbiental> result = (List<CertificadoRegistroAmbiental>) crudServiceBean
				.findByNamedQuery(CertificadoRegistroAmbiental.FIND_BY_PROJECT,
						parameters);
		if (result != null && !result.isEmpty()) {
			return result;
		}
		return null;
	}

	/**
	 * 
	 * @param proyecto
	 * @param userName
	 * @param marcaAgua
	 * @return error
	 */
	public String crearCertificadoRegistroAmbiental(ProyectoLicenciamientoAmbiental proyecto, String userName, boolean marcaAgua) {
		
		if (proyecto != null) {
			try {
				Usuario usuario = proyectoLicenciamientoAmbientalFacade.getRepresentanteProyecto(proyecto.getId());
				proyecto = proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(proyecto.getId());
				String direccionPlantillaJasperReport = "prevencion/recibirCertificadoRegistroAmbiental";

				List<String> imagenesReporte = new ArrayList<String>();
				List<String> subreportes = new ArrayList<String>();
				List<String> subreportesUrl = new ArrayList<String>();
				String cargo="AUTORIDAD AMBIENTAL";
				byte[] firma_area = null;
				byte[] logo_area = null;
				byte[] pie_area = null;
			
				List<Usuario> usuario_area =null;
				String nombre_usuario = "";

				String firma_mae= "firma__"+ proyecto.getAreaResponsable().getAreaAbbreviation().replace("/", "_") + ".png";
				if (proyecto.getCatalogoCategoria().getTipoArea().getId()==3 && proyecto.getAreaResponsable().getTipoArea().getId()==3){
						
					String logo="logo__"+ proyecto.getAreaResponsable().getAreaAbbreviation() + ".png";
					String pie="pie__"+ proyecto.getAreaResponsable().getAreaAbbreviation() + ".png";
					logo_area = documentosFacade.descargarDocumentoPorNombre(logo);
					pie_area = documentosFacade.descargarDocumentoPorNombre(pie);
					
					firma_area = documentosFacade.descargarDocumentoPorNombre(firma_mae);
					usuario_area=usuarioFacade.buscarUsuarioPorRolNombreArea(cargo, proyecto.getAreaResponsable().getAreaName());
					if(usuario_area==null || usuario_area.size()==0)
						return "Error al buscar usuario Autoridad en: "+proyecto.getAreaResponsable().getAreaName()+".";					
					nombre_usuario=usuario_area.get(0).getPersona().getNombre()+ "\n"+ proyecto.getAreaResponsable().getAreaName();
				}else{
					imagenesReporte.add("logo_mae.png");
					imagenesReporte.add("fondo-documentos.png");

					direccionPlantillaJasperReport = "prevencion/recibirCertificadoRegistroAmbientalMae";
					firma_area = documentosFacade.descargarDocumentoPorNombre(firma_mae);
					usuario_area = usuarioFacade.buscarUsuarioPorRolNombreArea(cargo, proyecto.getAreaResponsable().getAreaName());						
					if(usuario_area==null || usuario_area.size()==0)
						return "Error al buscar usuario Autoridad en: "+proyecto.getAreaResponsable().getAreaName()+".";
					nombre_usuario = usuario_area.get(0).getPersona().getNombre()+ "\n"+ cargo;
				}

				InputStream isFirmaSubsecretario = new ByteArrayInputStream(firma_area);
				InputStream islogo_area = null;
				InputStream ispie_area =null;
				if (proyecto.getCatalogoCategoria().getTipoArea().getId()==3 && proyecto.getAreaResponsable().getTipoArea().getId()==3){
					islogo_area = new ByteArrayInputStream(logo_area);
					ispie_area = new ByteArrayInputStream(pie_area);	
				}

				byte[] borrador;
				InputStream isborrador;
				borrador = documentosFacade.descargarDocumentoPorNombre("borrador"+(marcaAgua?"":"vacio")+".png");
				isborrador = new ByteArrayInputStream(borrador);
				subreportes.add("subreporteUbicacionesGeograficas");
				subreportesUrl.add("prevencion/verRegistroProyecto_listaUbicaciones");
				if (usuario != null) {
					if (proyecto != null) {
						Map<String, Object> parametrosReporte = new ConcurrentHashMap<String, Object>();

						parametrosReporte.put("proyecto", proyecto);

						String direccionRepresentanteLegal = "";
						String telefonoRepresentanteLegal = "";
						String emailRepresentanteLegal = "";
						String cedulaRepresentanteLegal = "";
						String entidadResponsable = "";
						String localizacionEntidadResponsable = "";

						Area area = proyecto.getAreaResponsable();

						if (usuario.getPersona() != null) {
							List<Contacto> listContacto= new ArrayList<Contacto>();
							listContacto= contactoFacade.buscarUsuarioNativeQuery(userName);
							for (Contacto contacto : listContacto) {
								if(contacto.getFormasContacto().getId()==FormasContacto.DIRECCION)
									direccionRepresentanteLegal=contacto.getValor();
								if(contacto.getFormasContacto().getId()==FormasContacto.TELEFONO)
									telefonoRepresentanteLegal=contacto.getValor();
								if(contacto.getFormasContacto().getId()==FormasContacto.EMAIL)
									emailRepresentanteLegal=contacto.getValor();
							}

							String cedula = determinarCedulaRepresentanteLegal(usuario);
							if (cedula != null)
								cedulaRepresentanteLegal = cedula;
						}

						if (area != null)
							entidadResponsable = area.getAreaName();
						if (area != null
								&& area.getUbicacionesGeografica() != null
								&& area.getUbicacionesGeografica()
										.getUbicacionesGeografica() != null)
							localizacionEntidadResponsable = area
									.getUbicacionesGeografica().getNombre();
						if (isFirmaSubsecretario!=null){
							if (imagenesReporte.size()<=0){
								if (islogo_area !=null && ispie_area!=null){
									parametrosReporte.put("urlImagen-0", islogo_area);
									parametrosReporte.put("urlImagen-1", ispie_area);
								}else{
								return "Falta el logo de area.";
								}
							}
								
						parametrosReporte.put("urlImagen-2", isFirmaSubsecretario);
						parametrosReporte.put("subsecretario", nombre_usuario);
						parametrosReporte.put("direccionRepresentanteLegal",direccionRepresentanteLegal);
						parametrosReporte.put("telefonoRepresentanteLegal",telefonoRepresentanteLegal);
						parametrosReporte.put("emailRepresentanteLegal",emailRepresentanteLegal);
						parametrosReporte.put("cedulaRepresentanteLegal",cedulaRepresentanteLegal);
						parametrosReporte.put("entidadResponsable",entidadResponsable);
						parametrosReporte.put("localizacionEntidadResponsable",localizacionEntidadResponsable);
						parametrosReporte.put("numeroResolucionCertificado",secuenciasFacade.getSecuenciaResolucionAreaResponsableNuevoFormato(area));
						parametrosReporte.put("urlImagen-3", isborrador);
						parametrosReporte.put("direccionMae","");
						String codigoProyecto = proyecto.getCodigo();

						long idTarea = proyecto.getId();
						String nombreProcesoDirectorioGuardar = Constantes.CARPETA_CATEGORIA_UNO;
						String nombreProcesoConcatenarNombreFichero = "Categoría Uno";
						String nombreFichero = "Certificado.pdf";
						String mime = "application/pdf";
						String extension = ".pdf";
						Integer idTabla = proyecto.getId();
						String nombreTabla = ProyectoLicenciamientoAmbiental.class.getSimpleName();
						TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_CATEGORIA_UNO;

						Documento resultado = reportesFacade
								.generarReporteGuardarAlfresco(
										parametrosReporte,
										direccionPlantillaJasperReport,
										subreportes, subreportesUrl,
										imagenesReporte, idTarea,
										codigoProyecto, idTarea,
										nombreProcesoDirectorioGuardar,
										nombreProcesoConcatenarNombreFichero,
										nombreFichero, mime, extension,
										idTabla, nombreTabla, tipoDocumento);

						if (resultado != null) {

							List<CertificadoRegistroAmbiental> certificados = getCertificadoRegistroAmbientalPorIdProyecto(proyecto
									.getId());
							if (certificados != null) {
								for (int i = 0; i < certificados.size(); i++) {
									crudServiceBean.delete(certificados.get(i));
								}
							}

							CertificadoRegistroAmbiental certificadoRegistroAmbiental = new CertificadoRegistroAmbiental();
							certificadoRegistroAmbiental
									.setDocumento(resultado);
							certificadoRegistroAmbiental.setProyecto(proyecto);

							crudServiceBean
									.saveOrUpdate(certificadoRegistroAmbiental);
							
							DocumentoProyecto documentoProyecto = new DocumentoProyecto();
						    documentoProyecto.setDocumento(resultado);
						    documentoProyecto.setProyectoLicenciamientoAmbiental(proyecto);	
						    
						    crudServiceBean.saveOrUpdate(documentoProyecto);
						}else {
							return "Error al Generar Documento.";
						}
						} else {
							return "Error de firma.";
						}
					}else {
						return "Error al buscar proyecto.";
					}
				}else {
					return "Error al buscar usuario representante.";
				}
				
				//Certificado generado correctamente (retorna error null)
				return null;
			} catch (Exception e) {
				LOG.error("Error al completar la tarea", e);
			}
		}
		return "Error al descargar el Certificado Ambiental. Comuníquese con mesa de ayuda";

	}

	public Documento guardarCertificadoRegistroAmbiental(File file,
			ProyectoLicenciamientoAmbiental proyecto) throws IOException {
		

						String codigoProyecto = proyecto.getCodigo();

						long idTarea = proyecto.getId();
						String nombreProcesoDirectorioGuardar = Constantes.CARPETA_CATEGORIA_UNO;
						String nombreProcesoConcatenarNombreFichero = "Categoría Uno";
						String nombreFichero = "Certificado.pdf";
						String mime = "application/pdf";
						String extension = ".pdf";
						Integer idTabla = proyecto.getId();
						String nombreTabla = ProyectoLicenciamientoAmbiental.class
								.getSimpleName();
						TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_CATEGORIA_UNO;

						return reportesFacade
						.guardarReporteCertificadoAmbiental(file ,idTarea, codigoProyecto, idTarea, nombreProcesoDirectorioGuardar, nombreProcesoConcatenarNombreFichero, nombreFichero, mime, extension, idTabla, nombreTabla, tipoDocumento);
	}				

						
	public byte[] descargarDocumentoAlfresco(String idAlfresco)
			throws CmisAlfrescoException {
		byte[] archivo = documentosFacade.descargar(idAlfresco);
		return archivo;
	}

	public byte[] descargarDocumentoPorNombre(String nombreDocumento)
			throws Exception {
		byte[] archivo = null;
		if (nombreDocumento != null && !nombreDocumento.isEmpty())
			archivo = documentosFacade.descargarDocumentoPorNombre(nombreDocumento);
		return archivo;
	}

	public String determinarCedulaRepresentanteLegal(Usuario usuario) {
		try {
			Organizacion organizacion = organizacionFacade.buscarPorPersona(usuario.getPersona(),usuario.getNombre());
			if (organizacion != null) {
				// "PERSONA_JURIDICA";
				return usuario.getPersona().getPin();
			}
			// PERSONA_NATURAL
			return usuario.getPin();
		} catch (ServiceException exception) {
			return "";
		}

	}
	

	public void guardarCertificadoAmbiental(ProyectoLicenciamientoAmbiental proyecto, Documento resultado) {		
		
		List<CertificadoRegistroAmbiental> certificados = getCertificadoRegistroAmbientalPorIdProyecto(proyecto
				.getId());
		if (certificados != null) {
			for (int i = 0; i < certificados.size(); i++) {
				crudServiceBean.delete(certificados.get(i));
			}
		}

		CertificadoRegistroAmbiental certificadoRegistroAmbiental = new CertificadoRegistroAmbiental();
		certificadoRegistroAmbiental
				.setDocumento(resultado);
		certificadoRegistroAmbiental.setProyecto(proyecto);

		crudServiceBean
				.saveOrUpdate(certificadoRegistroAmbiental);
		
	}	
		public boolean iniciarProcesoCertificadoAmbiental(Usuario usuario,ProyectoLicenciamientoAmbiental proyecto) throws ServiceException {

			Map<String, Object> params = new ConcurrentHashMap<String, Object>();
			params.put(Constantes.ID_PROYECTO, proyecto.getId());			
			params.put(Constantes.VARIABLE_PROCESO_TRAMITE, proyecto.getCodigo());
			params.put("usuario_proponente", proyecto.getUsuario().getNombre());
			params.put("remocion_cobertura_vegetal",proyecto.getRemocionCoberturaVegetal()!=null?proyecto.getRemocionCoberturaVegetal():false);
			
			Area areaProyecto = areaFacade.getArea(proyecto.getAreaResponsable().getId());
						
			//Intersecciones
			Boolean interseca = certificadoInterseccionService.getValueInterseccionProyectoIntersecaCapas(proyecto);
			
			if (interseca) {
				Map<String, Boolean> capasInterseca = certificadoInterseccionService.getCapasInterseccionBoolean(proyecto.getCodigo());				
				Boolean intersecaBP = capasInterseca.get(Constantes.CAPA_BP);
				Boolean intersecaPFE = capasInterseca.get(Constantes.CAPA_PFE);
				Boolean intersecaSNAP = capasInterseca.get(Constantes.CAPA_SNAP);
				Boolean intersecaRAMSARA = capasInterseca.get(Constantes.CAPA_RAMSAR_AREA);
				Boolean intersecaRAMSARP = capasInterseca.get(Constantes.CAPA_RAMSAR_PUNTO);
				
				params.put("interseca_forestal",((intersecaBP!=null && intersecaBP)||(intersecaPFE !=null && intersecaPFE))?true:false);				
				//params.put("interseca_snap",intersecaSNAP!=null?intersecaSNAP:false);	
				params.put("interseca_snap",((intersecaSNAP!=null && intersecaSNAP)||(intersecaRAMSARA !=null && intersecaRAMSARA)||(intersecaRAMSARP !=null && intersecaRAMSARP))?true:false);
				
				boolean intersecaSubsistemas=false;
				String nombreAreaSubsistema="";
				if (intersecaSNAP || intersecaRAMSARA || intersecaRAMSARP) {				
					List<InterseccionProyecto> interseccionProyecto=certificadoInterseccionService.getListaInterseccionProyectoIntersecaCapas(proyecto.getCodigo());
					
					if(intersecaSNAP){
						for (InterseccionProyecto interseccion : interseccionProyecto) {
							List<DetalleInterseccionProyecto>interseccionProyectoDetalle =certificadoInterseccionService.getDetallesInterseccion(interseccion.getId());
							for (DetalleInterseccionProyecto detalle : interseccionProyectoDetalle) {
								if(detalle.getCapaSubsistema()!=null && !detalle.getCapaSubsistema().isEmpty() && detalle.getCapaSubsistema().toUpperCase().contains("ESTATAL"))
								{								
									intersecaSubsistemas=true;
									nombreAreaSubsistema=detalle.getNombre();
									break;
								}
							}
							if(intersecaSubsistemas)
								break;
						}
					}
					
					if(!intersecaSubsistemas && (intersecaRAMSARA || intersecaRAMSARP)){
						for (InterseccionProyecto interseccion : interseccionProyecto) {
							List<DetalleInterseccionProyecto>interseccionProyectoDetalle =certificadoInterseccionService.getDetallesInterseccion(interseccion.getId());
																			
							for (DetalleInterseccionProyecto detalle : interseccionProyectoDetalle) {
								if(detalle.getInterseccionProyecto().getCapa().getId().intValue()==11 || detalle.getInterseccionProyecto().getCapa().getId().intValue()==12)
								{
									if(detalle.getNombre().toUpperCase().contains("COMPLEJO DE HUMEDALES NUCANCHI TURUPAMBA") || 
									   detalle.getNombre().toUpperCase().contains("COMPLEJO LLANGANATI") ||
									   detalle.getNombre().toUpperCase().contains("CUYABENO LAGARTOCOCHA YASUNÍ") ||
									   detalle.getNombre().toUpperCase().contains("HUMEDALES DEL SUR DE ISABELA") ||
									   detalle.getNombre().toUpperCase().contains("ISLA SANTA CLARA") ||
									   detalle.getNombre().toUpperCase().contains("ISLA SANTAY") ||
									   detalle.getNombre().toUpperCase().contains("SISTEMA LACUSTRE YACURI") ||
									   detalle.getNombre().toUpperCase().contains("PARQUE NACIONAL CAJAS") ||
									   detalle.getNombre().toUpperCase().contains("MACHALILLA") ||
									   detalle.getNombre().toUpperCase().contains("LAGUNA CUBE") || detalle.getNombre().toUpperCase().contains("LAGUNA DE CUBE") ||
									   detalle.getNombre().toUpperCase().contains("RESERVA BIOLOGICA LIMONCOCHA") || detalle.getNombre().toUpperCase().contains("RESERVA BIOLÓGICA LIMONCOCHA") ||
									   detalle.getNombre().toUpperCase().contains("RESERVA ECOLOGICA CAYAPAS - MATAJE") || detalle.getNombre().toUpperCase().contains("RESERVA ECOLÓGICA CAYAPAS - MATAJE") ||
									   detalle.getNombre().toUpperCase().contains("RESERVA ECOLOGICA EL A?NGEL") || detalle.getNombre().toUpperCase().contains("RESERVA ECOLÓGICA EL ÁNGEL") ||
									   detalle.getNombre().toUpperCase().contains("RESERVA ECOLÓGICA MANGLARES CHURUTE") || detalle.getNombre().toUpperCase().contains("MANGLARES CHURUTE") ||
									   detalle.getNombre().toUpperCase().contains("SISTEMA LACUSTRE LAGUNAS DEL COMPADRE") || detalle.getNombre().toUpperCase().contains("SISTEMA LACUSTRE LAGUNAS DEL COMPADRE (PODOCARPUS)"))
									{
										intersecaSubsistemas=true;
										nombreAreaSubsistema=detalle.getInterseccionProyecto().getCapa().getNombre()+" "+detalle.getNombre();
										break;
									}
								}								
							}						
							
							if(intersecaSubsistemas)
								break;
						}
					}			
					
					params.put("interseca_subsistemas_estatal",intersecaSubsistemas);
					if(intersecaSubsistemas)
					{
						Usuario usrJefeArea=areaFacade.getJefeArea(nombreAreaSubsistema);
						
						if(usrJefeArea!=null)
						{
							params.put("usuario_jefe_area", usrJefeArea.getNombre());
						}
						else{					
							return false;
						}
						
					}
					
				}
				
				if((boolean)params.get("interseca_forestal") || 
				  ((boolean)params.get("interseca_snap") && !(boolean)params.get("interseca_subsistemas_estatal")))
				{
					Usuario usrCoordinador = areaFacade.getCoordinadorProvincialPatrimonio(areaProyecto);					
					if(usrCoordinador!=null)
					{
						params.put("usuario_coordinador", usrCoordinador.getNombre());
					}
					else{					
						return false;
					}	
				}
				
				
			}	
			
			try {
				procesoFacade.iniciarProceso(usuario, Constantes.NOMBRE_PROCESO_CERTIFICADO_AMBIENTAL,proyecto.getCodigo(), params);				
				return true;
			} catch (Exception e) {
				throw new ServiceException(e);
			}
			
	}
	
    public String configurarPeticion(String rol, String area) {
        return "rol=" + rol + "&area=" + area;
    }
	
	
}