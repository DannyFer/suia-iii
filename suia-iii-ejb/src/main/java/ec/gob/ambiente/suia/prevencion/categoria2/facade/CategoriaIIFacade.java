/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.prevencion.categoria2.facade;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalConcesionesMinerasFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalConcesionesMineras;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.autoridadambiental.service.AutoridadAmbientalFacade;
import ec.gob.ambiente.suia.catalogocategorias.facade.CatalogoCategoriasFacade;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoBioticoFacade;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoFisicoFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.persona.service.PersonaServiceBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author jgras
 * @version Revision: 1.0
 *          <p>
 *          [Autor: jgras, Fecha: 03/02/2015]
 *          </p>
 */
@Stateless
public class CategoriaIIFacade {

	private static final Logger LOG = Logger.getLogger(CategoriaIIFacade.class);

	private static final String NADA = "";
	@EJB
	SecuenciasFacade secuenciasFacade;
	@EJB
	UsuarioFacade usuarioFacade;
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	AlfrescoServiceBean alfrescoServiceBean;
	@EJB
	DocumentosFacade documentosFacade;
	@EJB
	PersonaServiceBean personaService;
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private ContactoFacade contactoFacade;

	@EJB
	private AutoridadAmbientalFacade autoridadAmbientalFacade;

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	@EJB
	private CatalogoCategoriasFacade catalogoCategoriasFacade;

	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	
	@EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalShapeFacade;
	
	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	

	public void modificarRegistro(EntidadBase objeto) {
		crudServiceBean.saveOrUpdate(objeto);
	}

	public void guardarRegistro(EntidadBase objeto) {
		crudServiceBean.saveOrUpdate(objeto);
	}

	public void delete(EntidadBase objeto) {
		crudServiceBean.delete(objeto);
	}

	public void ingresarDocumentoCategoriaII(File file, Integer id,
			String codProyecto, long idProceso, long idTarea,
			TipoDocumentoSistema tipoDocumento, String nombreDocumento)
			throws Exception {

		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		Documento documento1 = new Documento();
		documento1.setIdTable(id);
		documento1.setNombre(nombreDocumento + ".pdf");
		documento1.setExtesion(mimeTypesMap.getContentType(file));
		documento1.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
		documento1.setMime(mimeTypesMap.getContentType(file));
		documento1.setContenidoDocumento(data);
		DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		documentoTarea.setIdTarea(idTarea);
		documentoTarea.setProcessInstanceId(idProceso);

		documentosFacade.guardarDocumentoAlfresco(codProyecto,
				"LicenciaAmbientalCategoriaII", idProceso, documento1,
				tipoDocumento, documentoTarea);
	}

	public void ingresarArchivoFotografico(File file, Integer id,
			String codProyecto, long idProceso, long idTarea,
			TipoDocumentoSistema tipoDocumento, String nombreDocumento)
			throws Exception {

		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		Documento documento1 = new Documento();
		documento1.setIdTable(id);
		documento1.setNombre(nombreDocumento + ".pdf");
		documento1.setExtesion(mimeTypesMap.getContentType(file));
		documento1.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
		documento1.setMime(mimeTypesMap.getContentType(file));
		documento1.setContenidoDocumento(data);
		DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		documentoTarea.setIdTarea(idTarea);
		documentoTarea.setProcessInstanceId(idProceso);

		documentosFacade.guardarDocumentoAlfresco(codProyecto,
				"LicenciaAmbientalCategoriaII", idProceso, documento1,
				tipoDocumento, documentoTarea);
	}

	public byte[] recuperarDocumentoCategoriaII(Integer id,
			TipoDocumentoSistema tipoDocumento) throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters
				.put("nombreTabla", CategoriaIILicencia.class.getSimpleName());
		parameters.put("idTabla", id);
		parameters.put("idTipo", tipoDocumento.getIdTipoDocumento());

		@SuppressWarnings("unchecked")
		List<Documento> documentos = (List<Documento>) crudServiceBean
				.findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA_TIPO,
						parameters);

		if (!documentos.isEmpty()) {
			Documento doc = documentos.get(0);
			byte[] archivo = alfrescoServiceBean.downloadDocumentById(doc
					.getIdAlfresco());
			return archivo;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public CategoriaIILicencia getCategoriaIILicenciaPorIdProyecto(
			Integer idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);

		List<CategoriaIILicencia> result = (List<CategoriaIILicencia>) crudServiceBean
				.findByNamedQuery(CategoriaIILicencia.FIND_BY_PROJECT,
						parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

	public void inicarProcesoCategoriaII(Usuario usuario,
			ProyectoLicenciamientoAmbiental proyecto) throws ServiceException {
		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
		params = getParametrosIniciarProcesoCategoriaII(usuario.getNombre(),
				proyecto, false);
		params.put(Constantes.ID_PROYECTO, proyecto.getId());
		try {
			procesoFacade.iniciarProceso(usuario,
					Constantes.NOMBRE_PROCESO_CATEGORIA2, proyecto.getCodigo(),
					params);
		} catch (JbpmException e) {
			throw new ServiceException(e);
		}
	}

	public Map<String, Object> getParametrosIniciarProcesoCategoriaII(
			String nombreUsuario, ProyectoLicenciamientoAmbiental proyecto,
			boolean isIniciadoPorCertificadoViabilidad) {
		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
		params.put(
				(isIniciadoPorCertificadoViabilidad) ? "nombreFormularioPMACatII"
						: "nombreFormularioPMA",
				getPageRegistroAmbientalPorSector(proyecto));
		params.put("u_Promotor", nombreUsuario);
		params.put("codigoProyecto", proyecto.getCodigo());
		params.put("exentoPago", getExentoPago(proyecto));
		return params;
	}

	public Boolean getExentoPago(ProyectoLicenciamientoAmbiental proyecto) {
		try {
			CatalogoCategoriaSistema pay = (CatalogoCategoriaSistema) crudServiceBean
					.getEntityManager()
					.createQuery(
							"From CatalogoCategoriaSistema c where c.id =:catCatId and c.estado = true and c.pagoServiciosAdministrativos = true")
					.setParameter("catCatId",
							proyecto.getCatalogoCategoria().getId())
					.getSingleResult();
		} catch (NoResultException nre) {
			return true;// Si es una actividad que no paga
		}
		// Si es categoría III o IV
		String categoria = proyecto.getCatalogoCategoria().getCategoria()
				.getCodigo();
		if (categoria.equals("III") || categoria.equals("IV")) {
			// Si el usuario pertenece a una organizacion
			try {
				Usuario user = usuarioFacade.buscarUsuarioPorId(proyecto
						.getUsuario().getId());
				if (user.getPersona().getOrganizaciones().size() > 0) {
					String tipoOrganizacion = proyecto.getUsuario()
							.getPersona().getOrganizaciones().get(0)
							.getTipoOrganizacion().getDescripcion();

					// Si es Empresa Pública o Mixta
					if (tipoOrganizacion.equals("EP")
							|| tipoOrganizacion.equals("EM")) {
						return true; // No paga
					} else {
						// Verifica acuerdo MAE

						List<CatalogoCategoriaAcuerdo> acuerdosOrganizacion = catalogoCategoriasFacade
								.buscarCatalogoCategoriaAcuerdoPorRucOrganizacion(proyecto
										.getUsuario().getPersona()
										.getOrganizaciones().get(0).getRuc());

						if (acuerdosOrganizacion != null
								&& acuerdosOrganizacion.size() > 0) {
							return true; // No paga
						}
						return false;
					}
				}
			} catch (Exception e) {
				LOG.error(
						"Error al obtener la organización e iniciar el proceso",
						e);
			}
		}

		// Para el resto se debe pagar
		return false;
	}

	private String getPageRegistroAmbientalPorSector(
			ProyectoLicenciamientoAmbiental proyecto) {
		if (proyecto.getCatalogoCategoria().getCodigo().equals("21.02.01.01")||proyecto.getCatalogoCategoria().getCodigo().equals("21.02.01.02")) {
			return "/prevencion/categoria2/fichaMineria/default.jsf";
		}
		return "/prevencion/categoria2/fichaAmbiental/default.jsf";
	}

	private String getDateFormat(Date date) {
		SimpleDateFormat formateador = new SimpleDateFormat(
				"dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
		return formateador.format(date);
	}

	public String generarTablaCoordenadas(
			ProyectoLicenciamientoAmbiental proyecto) {
		String strTable = "<table border=\"1\" cellpadding=\"10\" cellspacing=\"0\" class=\"w600Table\" style=\"font-family:arial;font-size:12px;\">"
				+ "<tbody><tr BGCOLOR=\"#B2E6FF\">"
				+ "<td><strong>COORDENADA X</strong></td><td><strong>COORDENADA Y</strong></td><td><strong>DESCRIPCIÓN</strong></td><td><strong>FORMA</strong></td></tr>";

		for (FormaProyecto frmProyecto : proyecto.getFormasProyectos()) {
			for (Coordenada coor : frmProyecto.getCoordenadas()) {
				strTable += "<tr>";
				strTable += "<td>" + coor.getX() + "</td>";
				strTable += "<td>" + coor.getY() + "</td>";
				strTable += "<td>" + coor.getDescripcion() + "</td>";
				strTable += "<td>" + frmProyecto.getTipoForma().getNombre()
						+ "</td>";
				strTable += "</tr>";
			}
		}
		strTable += "</tbody></table>";
		strTable = strTable.replace("&", "&amp;");
		return strTable;
	}
	
	public String generarTablaCoordenadas(ProyectoLicenciaCoa proyecto) {
		String strTable = "<table border=\"1\" cellpadding=\"10\" cellspacing=\"0\" class=\"w600Table\" style=\"font-family:arial;font-size:12px;\">"
				+ "<tbody><tr BGCOLOR=\"#B2E6FF\">"
				+ "<td><strong>COORDENADA X</strong></td><td><strong>COORDENADA Y</strong></td><td><strong>DESCRIPCIÓN</strong></td><td><strong>FORMA</strong></td></tr>";
		List<ProyectoLicenciaAmbientalCoaShape> formasImplantacion = proyectoLicenciaAmbientalShapeFacade.buscarFormaGeograficaPorProyecto(proyecto, 1, 0); //coordenadas implantacion		
		if(formasImplantacion != null) {
			for(ProyectoLicenciaAmbientalCoaShape forma : formasImplantacion){
				List<CoordenadasProyecto> coordenadasGeograficasImplantacion = coordenadasProyectoCoaFacade.buscarPorForma(forma);
				for(CoordenadasProyecto coor: coordenadasGeograficasImplantacion)
				{				
				strTable += "<tr>";
				strTable += "<td>" + coor.getX() + "</td>";
				strTable += "<td>" + coor.getY() + "</td>";
				strTable += "<td> </td>";
				strTable += "<td>" + forma.getTipoForma().getNombre()
						+ "</td>";
				strTable += "</tr>";
				}
			}
		}	
		strTable += "</tbody></table>";
		strTable = strTable.replace("&", "&amp;");
		return strTable;
	}

	public String[] cargarDatosLicenciaAmbiental(
			ProyectoLicenciamientoAmbiental proyectoActivo, String userName)
			throws Exception {

		/*
		 * 1. (AUTOLLENADO – EJEMPLO: DIRECCIÓN PROVINCIAL DE LOJA) 2.
		 * (AUTOLLENADO – EJEMPLO: DIRECCIÓN PROVINCIAL DE LOJA) 3. Ficha
		 * Ambiental y PMA emitida mediante Oficio
		 * No.MAE-SUIA-UAP-DPAL-20XX-XXXXX 4. con fecha DD de MMMM del AAAA. 5.
		 * (AUTOLLENADO - NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD) 6. Licencia
		 * Ambiental Categoría II registrada con el
		 * No.XXXXX-XX-20XX-FA-UAF-DPLA-MAE 7. Código proyecto (en base al
		 * CCAN): 8. Nombre de la actividad (en base al CCAN): AUTOLLENADO 9.
		 * Ubicación Geográfica: 10. Nombre del representante legal: TRATAMIENTO
		 * TITULO APELLIDOS 11. Dirección: AUTOLLENADO 12. Teléfono: AUTOLLENADO
		 * 13. Email: AUTOLLENADO 14. Código del Proyecto: MAE-RA-20XX-XXXX 15.
		 * Tratamiento Título Nombres Apellidos 16. CARGO 17. (AUTOLLENADO -
		 * NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD) 18. Coordenadas
		 */
		String[] parametrosO = new String[25];
		Integer i = 0;

		// Institución: (AUTOLLENADO – EJEMPLO: DIRECCIÓN PROVINCIAL DE LOJA)
		parametrosO[i++] = proyectoActivo.getAreaResponsable().getAreaName();
		parametrosO[i++] = proyectoActivo.getAreaResponsable().getAreaName();

		// No. Oficio
		// *************************************************************************
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado()!=null){
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
			parametrosO[i++] = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable().getArea());
		}else{
			parametrosO[i++] = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		}else{
			parametrosO[i++] = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		

		// Fecha
		parametrosO[i++] = getDateFormat(proyectoActivo.getFechaRegistro());

		// NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
				.getNombre());

		// Número de licencia
		String numeroLicencia=null;
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado()!=null){
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
			 numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable().getArea());
		}else{
			 numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		}else{
			numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		
		parametrosO[i++] = numeroLicencia;
		FichaAmbientalPma fichaAmbientalPma = fichaAmbientalPmaFacade
				.getFichaAmbientalPorCodigoProyecto(proyectoActivo.getCodigo());
		fichaAmbientalPma.setNumeroLicencia(numeroLicencia);
		fichaAmbientalPma.setNumeroOficio(numeroLicencia);
		fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbientalPma);

		// Código proyecto (en base al CCAN):
		parametrosO[i++] = proyectoActivo.getCodigo();

		// Nombre de la actividad (en base al CCAN): AUTOLLENADO
		parametrosO[i++] = proyectoActivo.getNombre();

		// Ubicación Geográfica
		parametrosO[i++] = proyectoActivo.getProyectoUbicacionesGeograficas()
				.get(0).getUbicacionesGeografica().getNombre();

		// Titular
		Usuario usuario = proyectoActivo.getUsuario();
		usuario = usuarioFacade.buscarUsuarioPorIdFull(usuario.getId());
		Persona titular = usuario.getPersona();
		String tratoTitular = titular.getTipoTratos() == null ? "Sr.|Sra.|Sta."
				: titular.getTipoTratos().getNombre();
		parametrosO[i++] = tratoTitular + " " + titular.getNombre();

		Organizacion organizacion = null;
		try {
			organizacion = organizacionFacade.buscarPorPersona(titular,
					userName);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		List<Contacto> contactos;
		if (organizacion != null) {
			contactos = contactoFacade.buscarPorOrganizacion(organizacion);
		} else {
			contactos = contactoFacade.buscarPorPersona(titular);
		}
		String direccion = NADA;
		String telefono = NADA;
		String email = NADA;
		for (Contacto contacto : contactos) {
			if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.DIRECCION)) {
				// Dirección
				direccion = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.TELEFONO)) {
				// Teléfono
				telefono = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.EMAIL)) {
				email = contacto.getValor();
			}
		}

		// Dirección
		parametrosO[i++] = direccion;

		// Teléfono
		parametrosO[i++] = telefono;

		// Email
		parametrosO[i++] = email;

		// Código del proyecto
		parametrosO[i++] = proyectoActivo.getCodigo();

		// Persona que revisa
		// Tratamiento Título Nombres Apellidos
		Persona representante = personaService
				.getRepresentanteProyectoFull(proyectoActivo.getId());
		String trato = representante.getTipoTratos() == null ? "Sr.|Sra.|Sta."
				: representante.getTipoTratos().getNombre();
		parametrosO[i++] = trato + " " + representante.getNombre();

		// Cargo
		parametrosO[i++] = representante.getPosicion();

		// NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
				.getNombre());

		// Coordenadas
		parametrosO[i++] = generarTablaCoordenadas(proyectoActivo);
		return parametrosO;
	}

	public String[] cargarDatosLicenciaAmbientalCompleto(
			ProyectoLicenciamientoAmbiental proyectoActivo, String userName)
			throws Exception {
		// Número resolucion LAST MOD 26/01/2016
		String numeroResolucion = null;
		if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {
			Date fecha = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(fecha);
			int anno = calendar.get(Calendar.YEAR);

			numeroResolucion = proyectoActivo.getAreaResponsable()
					.getAreaAbbreviation()
					+ "-"+anno+"-"
					+ Long.toString(secuenciasFacade
							.getNextValueDedicateSequence("entes"));
		} else {
			numeroResolucion = Long
					.toString(secuenciasFacade
							.getNextValueDedicateSequence("numeroResolucionCategoriaII"));
		}

		// Número de licencia
		String numeroLicencia=null;
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado()!=null){
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
			numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable().getArea());
		}else{
			numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		} else{
			numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		
		FichaAmbientalPma fichaAmbientalPma = fichaAmbientalPmaFacade
				.getFichaAmbientalPorCodigoProyecto(proyectoActivo.getCodigo());
		fichaAmbientalPma.setNumeroLicencia(numeroLicencia);
		fichaAmbientalPma.setNumeroOficio(numeroResolucion);
		fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbientalPma);

		// No. Oficio
		// *************************************************************************
		String numeroOficio = numeroResolucion;// secuenciasFacade
		// .getSecuenciaResolucionAreaResponsable(proyectoActivo
		// .getAreaResponsable());
		String canton = "";
		String provincia = "";
		String ubicacion = "";
		String ubicacionCompleta = "";
		String cantonUsuario = "";
		String provinciaUsuario = "";

		String nombreEmpresaPromotor = "";
		String nombreRepresentante = "";

		try {

			List<UbicacionesGeografica> ubicaciones = proyectoActivo
					.getUbicacionesGeograficas();
			if (ubicaciones.size() == 1) {
				UbicacionesGeografica parroquiaU = ubicaciones.get(0);
				if (parroquiaU.getUbicacionesGeografica() != null) {
					UbicacionesGeografica cantonU = parroquiaU
							.getUbicacionesGeografica();
					canton = cantonU.getNombre();
					if (cantonU.getUbicacionesGeografica() != null) {
						UbicacionesGeografica provinciaU = cantonU
								.getUbicacionesGeografica();
						provincia = provinciaU.getNombre();
					}
				}
				ubicacionCompleta = provincia + ", " + canton + ", "
						+ parroquiaU.getNombre();
				ubicacion = " EL CANTÓN " + canton + ", PROVINCIA  "
						+ provincia;

			} else {
				ubicacion = ": <br/><table>";
				ubicacionCompleta = "<table>";

				ubicacion += "<tr><td>CANTÓN</td><td>PROVINCIA</td></tr>";
				ubicacionCompleta += "<tr><td>PROVINCIA</td><td>CANTÓN</td><td>PARROQUIA</td></tr>";
				for (UbicacionesGeografica ubicacionActual : ubicaciones) {
					UbicacionesGeografica cantonU = ubicacionActual
							.getUbicacionesGeografica();
					canton = cantonU.getNombre();
					if (cantonU.getUbicacionesGeografica() != null) {
						UbicacionesGeografica provinciaU = cantonU
								.getUbicacionesGeografica();
						provincia = provinciaU.getNombre();
					}

					ubicacion += "<tr><td>" + canton + "</td><td>" + provincia
							+ "</td></tr>";

					ubicacionCompleta += "<tr><td>" + provincia + "</td><td>"
							+ canton + "</td><td>"
							+ ubicacionActual.getNombre() + "</td></tr>";
				}

				ubicacion += "</table>";

				ubicacionCompleta += "</table>";
			}
		} catch (Exception e) {
			LOG.error("Error recuperando la ubicación del proyecto", e);
		}

		// Titular
		Usuario usuario = proyectoActivo.getUsuario();
		usuario = usuarioFacade.buscarUsuarioPorIdFull(usuario.getId());
		Persona titular = usuario.getPersona();
		String tratoTitular = titular.getTipoTratos() == null ? "Sr.|Sra.|Sta."
				: titular.getTipoTratos().getNombre();

		Organizacion organizacion = null;
		try {
			organizacion = organizacionFacade.buscarPorPersona(titular,
					userName);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		List<Contacto> contactos;
		contactos = contactoFacade
				.buscarUsuarioNativeQuery(usuario.getNombre());// buscarPorOrganizacion(organizacion);

		UbicacionesGeografica ubicacionProponente = new UbicacionesGeografica();
		if (organizacion != null) {
			ubicacionProponente = organizacion.getPersona()
					.getUbicacionesGeografica();
			nombreEmpresaPromotor = organizacion.getNombre().replace("&", "&amp;");
			nombreRepresentante = organizacion.getPersona().getNombre();
		} else {
			ubicacionProponente = titular.getUbicacionesGeografica();
			nombreEmpresaPromotor = titular.getNombre();
			nombreRepresentante = nombreEmpresaPromotor;
		}

		if (ubicacionProponente.getUbicacionesGeografica() != null) {
			cantonUsuario = ubicacionProponente.getUbicacionesGeografica()
					.getNombre();
			if (ubicacionProponente.getUbicacionesGeografica()
					.getUbicacionesGeografica() != null) {
				provinciaUsuario = ubicacionProponente
						.getUbicacionesGeografica().getUbicacionesGeografica()
						.getNombre();

			}
		}

		String direccion = NADA;
		String telefono = NADA;
		String email = NADA;
		for (Contacto contacto : contactos) {
			if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.DIRECCION)) {
				// Dirección
				direccion = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.TELEFONO)) {
				// Teléfono
				telefono = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.EMAIL)) {
				email = contacto.getValor();
			}
		}
		String sector = proyectoActivo.getCatalogoCategoria()
				.getCatalogoCategoriaPublico().getTipoSector().toString();
		String sectorActividad = proyectoActivo.getCatalogoCategoria()
				.getDescripcion();
		String[] parametrosO = new String[40];
		Integer i = 0;
		// ---------------------------------------------------------------------
		// No de la resolución
		parametrosO[i++] = numeroResolucion;
		if(proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(5) || proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(6) 
				|| proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(2)){
			parametrosO[i++] = "DIRECCIÓN ZONAL";
		}else{
			parametrosO[i++] = "SUBSECRETARIA DE CALIDAD AMBIENTAL DEL MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA";
		}

//		// Art. 1.
//		// NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD
//		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
//				.getNombre());
//
//		// ubicación
//		parametrosO[i++] = ubicacion;
//
//		// y cuyo sector/actividad es
//		parametrosO[i++] = sector + " / " + sectorActividad;

		// Art. 2.
		// NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
				.getNombre());
		// ubicación
		parametrosO[i++] = ubicacion;

		// MINISTERIO DEL AMBIENTE / DIRECCION PROVINCIAL (No. DE REGSITRO
		// AUTOMATICO)
		parametrosO[i++] = numeroOficio;

		// REGISTRO AMBIENTAL PARA EL PROYECTO OBRA O ACTIVIDAD
		// NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
				.getNombre());
		// ubicación
		parametrosO[i++] = ubicacion;

		// El Ministerio del Ambient INCLUIR NOMBRE DE LA EMPRESA O PROMOTOR QUE
		// LO TOMA DEL REGISTRO-----
		parametrosO[i++] = nombreEmpresaPromotor;

		// cantón usuario
		//parametrosO[i++] = cantonUsuario;

		// provincia usuario
		//parametrosO[i++] = provinciaUsuario;

		// número de oficio de aprobación generado en el sistema
		// parametrosO[i++] = numeroOficio;

		// NOMBRE DE LA EMPRESA/PROMOTOR (TOMADO DEL REGISTRO USUARIO)
		parametrosO[i++] = nombreEmpresaPromotor; // tratoTitular + " " +
													// titular.getNombre();

		// Registro Ambiental emitido con el No
		parametrosO[i++] = numeroLicencia;

		// DATOS TÉCNICOS
		// Descripción de la actividad TOMADO DEL REGISTRO
		parametrosO[i++] = proyectoActivo.getCatalogoCategoria()
				.getDescripcion();

		// Sector
		parametrosO[i++] = sector;

		// Parroquia, cantón, provincia
		parametrosO[i++] = ubicacionCompleta;

		// Nombre del representante legal
		parametrosO[i++] = nombreRepresentante;

		parametrosO[i++] = direccion;
		parametrosO[i++] = telefono;
		parametrosO[i++] = email;

		// Código del Proyecto
		parametrosO[i++] = proyectoActivo.getCodigo();

		// INCLUIR NOMBRE DE LA EMPRESA O PROMOTOR, QUE LO TOMA DEL REGISTRO
		parametrosO[i++] = nombreEmpresaPromotor;

		Boolean genera = proyectoActivo.getTipoEstudio().getId() == 2
				&& proyectoActivo.getGeneraDesechos() != null
				&& proyectoActivo.getGeneraDesechos() == true;
		String dias = genera ? "30" : "60";

		parametrosO[i++] = dias;

		// fecha
		parametrosO[i++] = getDateFormat(new Date());
//		parametrosO[i++] = "11 de Junio de 2020";

		// walter
		parametrosO[39] = proyectoActivo.getCodigo();

		return parametrosO;
	}

	
	@EJB
    private AreaFacade areaFacade;

	public String[] cargarDatosLicenciaAmbientalCompletoRcoa(ProyectoLicenciaCoa proyectoActivo, String userName)
			throws Exception {
		// Número resolucion LAST MOD 26/01/2016
		String numeroResolucion = null;
		String numeroLicencia = "";
		RegistroAmbientalRcoa registroAmbientalRcoa = registroAmbientalCoaFacade.obtenerRegistroAmbientalPorProyecto(proyectoActivo);
		if(registroAmbientalRcoa != null && registroAmbientalRcoa.getNumeroOficio() == null && registroAmbientalRcoa.getNumeroResolucion() == null ){
			if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {
				Date fecha = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(fecha);
				int anno = calendar.get(Calendar.YEAR);
	
				numeroResolucion = proyectoActivo.getAreaResponsable()
						.getAreaAbbreviation()
						+ "-"+anno+"-"
						+ Long.toString(secuenciasFacade
								.getNextValueDedicateSequence("entes"));
			} else {
				numeroResolucion = Long
						.toString(secuenciasFacade
								.getNextValueDedicateSequence("numeroResolucionCategoriaII"));
			}
	
			// Número de licencia
			if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {
				numeroLicencia = secuenciasFacade
						.getSecuenciaResolucionEnteResponsableRCOA(proyectoActivo
								.getAreaResponsable());
			}else{
				Area area =  proyectoActivo.getAreaResponsable();
				if(area.getTipoArea().getSiglas().equals("DP")){
					area = proyectoActivo.getAreaResponsable();
				}else if(area.getTipoArea().getSiglas().equals("ZONALES")){
					UbicacionesGeografica provincia = proyectoActivo.getAreaResponsable().getUbicacionesGeografica();	
					area = areaFacade.getAreaCoordinacionZonal(provincia);
				}
				if(area.getTipoArea().getSiglas().equals("PC") || area.getTipoArea().getSiglas().equals("OT")){
					area = area.getArea();
				}
				numeroLicencia = secuenciasFacade
						.getSecuenciaResolucionAreaResponsableRCOA(area);
			}
			
			registroAmbientalRcoa.setNumeroResolucion(numeroLicencia);
			registroAmbientalRcoa.setNumeroOficio(numeroResolucion);
			registroAmbientalCoaFacade.guardar(registroAmbientalRcoa);
		}else{
			numeroResolucion = registroAmbientalRcoa.getNumeroOficio();
			numeroLicencia = registroAmbientalRcoa.getNumeroResolucion();
		}
		// No. Oficio
		// *************************************************************************
		String numeroOficio = numeroResolucion;// secuenciasFacade
		// .getSecuenciaResolucionAreaResponsable(proyectoActivo
		// .getAreaResponsable());
		String canton = "";
		String provincia = "";
		String ubicacion = "";
		String ubicacionCompleta = "";
		String cantonUsuario = "";
		String provinciaUsuario = "";

		String nombreEmpresaPromotor = "";
		String nombreRepresentante = "";

		
		// Titular
		Usuario usuario = proyectoActivo.getUsuario();
		usuario = usuarioFacade.buscarUsuarioPorIdFull(usuario.getId());
		Persona titular = usuario.getPersona();
		String tratoTitular = titular.getTipoTratos() == null ? "Sr.|Sra.|Sta."
				: titular.getTipoTratos().getNombre();

		Organizacion organizacion = null;
		try {
			organizacion = organizacionFacade.buscarPorPersona(titular,
					userName);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		List<Contacto> contactos;
		contactos = contactoFacade
				.buscarUsuarioNativeQuery(usuario.getNombre());// buscarPorOrganizacion(organizacion);

		UbicacionesGeografica ubicacionProponente = new UbicacionesGeografica();
		if (organizacion != null) {
			ubicacionProponente = organizacion.getPersona()
					.getUbicacionesGeografica();
			nombreEmpresaPromotor = organizacion.getNombre().replace("&", "&amp;");
			nombreRepresentante = organizacion.getPersona().getNombre();
		} else {
			ubicacionProponente = titular.getUbicacionesGeografica();
			nombreEmpresaPromotor = titular.getNombre();
			nombreRepresentante = nombreEmpresaPromotor;
		}

		if (ubicacionProponente.getUbicacionesGeografica() != null) {
			cantonUsuario = ubicacionProponente.getUbicacionesGeografica()
					.getNombre();
			if (ubicacionProponente.getUbicacionesGeografica()
					.getUbicacionesGeografica() != null) {
				provinciaUsuario = ubicacionProponente
						.getUbicacionesGeografica().getUbicacionesGeografica()
						.getNombre();

			}
		}

		String direccion = NADA;
		String telefono = NADA;
		String email = NADA;
		for (Contacto contacto : contactos) {
			if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.DIRECCION)) {
				// Dirección
				direccion = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.TELEFONO)) {
				// Teléfono
				telefono = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.EMAIL)) {
				email = contacto.getValor();
			}
		}
		/*
		String sector = proyectoActivo.getCatalogoCategoria()
				.getCatalogoCategoriaPublico().getTipoSector().toString();
		String sectorActividad = proyectoActivo.getCatalogoCategoria()
				.getDescripcion();*/
		String[] parametrosO = new String[40];
		Integer i = 0;
		// ---------------------------------------------------------------------
		// No de la resolución
		parametrosO[i++] = numeroResolucion;

//		// Art. 1.
//		// NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD
//		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
//				.getNombre());
//
//		// ubicación
//		parametrosO[i++] = ubicacion;
//
//		// y cuyo sector/actividad es
//		parametrosO[i++] = sector + " / " + sectorActividad;

		// Art. 2.
		// NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
				.getNombreProyecto());
		// ubicación
		parametrosO[i++] = ubicacion;

		// MINISTERIO DEL AMBIENTE / DIRECCION PROVINCIAL (No. DE REGSITRO
		// AUTOMATICO)
		parametrosO[i++] = numeroOficio;

		// REGISTRO AMBIENTAL PARA EL PROYECTO OBRA O ACTIVIDAD
		// NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
				.getNombreProyecto());
		// ubicación
		parametrosO[i++] = ubicacion;

		// El Ministerio del Ambient INCLUIR NOMBRE DE LA EMPRESA O PROMOTOR QUE
		// LO TOMA DEL REGISTRO-----
		parametrosO[i++] = nombreEmpresaPromotor;

		// cantón usuario
		//parametrosO[i++] = cantonUsuario;

		// provincia usuario
		//parametrosO[i++] = provinciaUsuario;

		// número de oficio de aprobación generado en el sistema
		// parametrosO[i++] = numeroOficio;

		// NOMBRE DE LA EMPRESA/PROMOTOR (TOMADO DEL REGISTRO USUARIO)
		parametrosO[i++] = nombreEmpresaPromotor; // tratoTitular + " " +
													// titular.getNombre();

		// Registro Ambiental emitido con el No
		parametrosO[i++] = numeroLicencia;

		// DATOS TÉCNICOS
		// Descripción de la actividad TOMADO DEL REGISTRO
		//parametrosO[i++] = proyectoActivo.getCatalogoCategoria().getDescripcion();

		// Sector
		/*parametrosO[i++] = sector;*/

		// Parroquia, cantón, provincia
		parametrosO[i++] = ubicacionCompleta;

		// Nombre del representante legal
		parametrosO[i++] = nombreRepresentante;

		parametrosO[i++] = direccion;
		parametrosO[i++] = telefono;
		parametrosO[i++] = email;

		// Código del Proyecto
		parametrosO[i++] = proyectoActivo.getCodigoUnicoAmbiental();

		// INCLUIR NOMBRE DE LA EMPRESA O PROMOTOR, QUE LO TOMA DEL REGISTRO
		parametrosO[i++] = nombreEmpresaPromotor;
/*
		Boolean genera = proyectoActivo.getTipoEstudio().getId() == 2
				&& proyectoActivo.getGeneraDesechos() != null
				&& proyectoActivo.getGeneraDesechos() == true;
		String dias = genera ? "30" : "60";

		parametrosO[i++] = dias;*/

		// fecha
		parametrosO[i++] = getDateFormat(new Date());

		// walter
		parametrosO[39] = proyectoActivo.getCodigoUnicoAmbiental();

		return parametrosO;
	}
	
	public String[] cargarDatosLicenciaAmbientalMineroLibreAprovechamiento(String numeroResolucion,
			ProyectoLicenciamientoAmbiental proyectoActivo, Boolean minero,
			String userName) throws Exception {
		// Número resolucion LAST MOD 26/01/2016
		/*String numeroResolucion = null;
		if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {
			Date fecha = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(fecha);
			int anno = calendar.get(Calendar.YEAR);

			numeroResolucion = proyectoActivo.getAreaResponsable()
					.getAreaAbbreviation()
					+ "-"+anno+"-"
					+ Long.toString(secuenciasFacade
					.getNextValueDedicateSequence("entes"));
		} else {
			numeroResolucion = Long
					.toString(secuenciasFacade
							.getNextValueDedicateSequence("numeroResolucionCategoriaII"));
		}*/

		// Número de licencia
		String numeroLicencia=null;
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado()!=null){
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
			 numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable().getArea());
		}else{
			 numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		}else{
			numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		
		
		FichaAmbientalPma fichaAmbientalPma = fichaAmbientalPmaFacade
				.getFichaAmbientalPorCodigoProyecto(proyectoActivo.getCodigo());
		fichaAmbientalPma.setNumeroLicencia(numeroLicencia);
		fichaAmbientalPma.setNumeroOficio(numeroResolucion);
		fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbientalPma);

		// No. Oficio
		// *************************************************************************
		String numeroOficio=null;
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado()!=null){
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
			 numeroOficio = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable().getArea());
		}else{
			 numeroOficio = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		}else{
			numeroOficio = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		String canton = "";
		String provincia = "";
		String ubicacion = "";
		String ubicacionCompleta = "";

		String ubicacionDescripcion = "";
		String cantonUsuario = "";
		String provinciaUsuario = "";

		String nombreEmpresaPromotor = "";
		String nombreRepresentante = "";

		String fase = minero ? "MINERÍA ARTESANAL"
				: "LIBRES APROVECHAMIENTOS O MATERIALES DE CONSTRUCCI&Oacute;N PARA OBRAS P&Uacute;BLICAS";

		String faseN = minero ? "LAS LABORES DE MINER&Iacute;A ARTESANAL PARA LA EXPLOTACI&Oacute;N DE"
				: "LIBRE APROVECHAMIENTO DE MATERIALES DE CONSTRUCCI&Oacute;N PARA OBRAS P&Uacute;BLICA";
		// LIBRE APROVECHAMIENTO DE MATERIALES DE CONSTRUCCIÓN PARA OBRAS
		// PÚBLICA
		String sector = proyectoActivo.getCatalogoCategoria()
				.getCatalogoCategoriaPublico().getTipoSector().toString();
		String tipoMaterial = "";

		if (proyectoActivo.getTipoMaterial() != null) {
			if (proyectoActivo.getTipoMaterial().getId() == TipoMaterial.MATERIAL_CONSTRUCCION) {
				tipoMaterial = " Materiales de construcción ";
			} else if (proyectoActivo.getTipoMaterial().getId() == TipoMaterial.MATERIAL_METALICO) {
				tipoMaterial = " minerales Metálicos ";
			} else if (proyectoActivo.getTipoMaterial().getId() == TipoMaterial.MATERIAL_NO_METALICO) {
				tipoMaterial = " minerales No Metálicos ";
			}
		}
		try {

			List<UbicacionesGeografica> ubicaciones = proyectoActivo
					.getUbicacionesGeograficas();
			if (ubicaciones.size() == 1) {
				UbicacionesGeografica parroquiaU = ubicaciones.get(0);
				if (parroquiaU.getUbicacionesGeografica() != null) {
					UbicacionesGeografica cantonU = parroquiaU
							.getUbicacionesGeografica();
					canton = cantonU.getNombre();
					if (cantonU.getUbicacionesGeografica() != null) {
						UbicacionesGeografica provinciaU = cantonU
								.getUbicacionesGeografica();
						provincia = provinciaU.getNombre();
					}
				}
				ubicacionCompleta = provincia + ", " + canton + ", "
						+ parroquiaU.getNombre();
				ubicacion = " LA PROVINCIA DE " + provincia;

			} else {
				ubicacionCompleta = "<table>";
				List<String> listaProvincias = new ArrayList<String>();
				ubicacionCompleta += "<tr><td>PROVINCIA</td><td>CANTÓN</td><td>PARROQUIA</td></tr>";
				for (UbicacionesGeografica ubicacionActual : ubicaciones) {
					UbicacionesGeografica cantonU = ubicacionActual
							.getUbicacionesGeografica();
					canton = cantonU.getNombre();
					boolean repetida = false;
					if (cantonU.getUbicacionesGeografica() != null) {
						UbicacionesGeografica provinciaU = cantonU
								.getUbicacionesGeografica();
						provincia = provinciaU.getNombre();
						if (listaProvincias.contains(provincia)) {
							repetida = true;
						} else {
							listaProvincias.add(provincia);
						}
					}
					if (!repetida) {
						if (ubicacion.isEmpty()) {
							ubicacion += provincia;
						} else {
							ubicacion += ", " + provincia;
						}
					}

					ubicacionCompleta += "<tr><td>" + provincia + "</td><td>"
							+ canton + "</td><td>"
							+ ubicacionActual.getNombre() + "</td></tr>";
				}

				if (listaProvincias.size() > 1) {
					ubicacion = " LAS PROVINCIAS (" + ubicacion + ")";
				} else {
					ubicacion = " LA PROVINCIA " + ubicacion;
				}
				ubicacionCompleta += "</table>";
			}
		} catch (Exception e) {
			LOG.error("Error recuperando la ubicación del proyecto", e);
		}

		// Titular
		Usuario usuario = proyectoActivo.getUsuario();
		usuario = usuarioFacade.buscarUsuarioPorIdFull(usuario.getId());
		Persona titular = usuario.getPersona();
		String tratoTitular = titular.getTipoTratos() == null ? "Sr.|Sra.|Sta."
				: titular.getTipoTratos().getNombre();

		Organizacion organizacion = null;
		try {
			organizacion = organizacionFacade.buscarPorPersona(titular,
					userName);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		List<Contacto> contactos;
		contactos = contactoFacade
				.buscarUsuarioNativeQuery(usuario.getNombre());// buscarPorOrganizacion(organizacion);

		UbicacionesGeografica ubicacionProponente = new UbicacionesGeografica();
		if (organizacion != null) {
			ubicacionProponente = organizacion.getPersona()
					.getUbicacionesGeografica();
			nombreEmpresaPromotor = organizacion.getNombre();
			nombreRepresentante = organizacion.getPersona().getNombre();
		} else {
			ubicacionProponente = titular.getUbicacionesGeografica();
			nombreEmpresaPromotor = titular.getNombre();
			nombreRepresentante = nombreEmpresaPromotor;
		}

		if (ubicacionProponente.getUbicacionesGeografica() != null) {
			cantonUsuario = ubicacionProponente.getUbicacionesGeografica()
					.getNombre();
			if (ubicacionProponente.getUbicacionesGeografica()
					.getUbicacionesGeografica() != null) {
				provinciaUsuario = ubicacionProponente
						.getUbicacionesGeografica().getUbicacionesGeografica()
						.getNombre();

			}
		}

		String direccion = NADA;
		String telefono = NADA;
		String email = NADA;
		for (Contacto contacto : contactos) {
			if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.DIRECCION)) {
				// Dirección
				direccion = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.TELEFONO)) {
				// Teléfono
				telefono = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.EMAIL)) {
				email = contacto.getValor();
			}
		}

		String mineroP = "EN EL ÁREA DENOMINADA " + proyectoActivo.getNombre()
				+ " " + proyectoActivo.getCodigo() + " A FAVOR DE " + titular;
		String mineroPLibre = "DE LA CONCESI&Oacute;N MINERA "
				+ proyectoActivo.getNombre() + " " + proyectoActivo.getCodigo();
		String[] parametrosO = new String[40];
		Integer i = 0;
		// ---------------------------------------------------------------------
		// No de la resolución
		parametrosO[i++] = numeroResolucion;

//		// Art. 1.
//		// // NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD
//		// parametrosO[i++] = proyectoActivo.getNombre();
//
//		// (LIBRES APROVECHAMIENTOS O MATERIALES DE CONSTRUCCIÓN)
//		if (minero) {
//			parametrosO[i++] = faseN + tipoMaterial+"22222222";
//			parametrosO[i++] = mineroP+"33333333";
//		} else {
//			parametrosO[i++] = faseN+"5555555555";
//			parametrosO[i++] = mineroPLibre+"66666666";
//		}
//
//		// “XXXXX(nombre del proyecto) (CÓD. XXXXX)
//		// parametrosO[i++] = "DE LA CONCESI&Oacute;N MINERA " +
//		// proyectoActivo.getNombre() + " " + proyectoActivo.getCodigo();
//
//		// ubicación
//		parametrosO[i++] = ubicacion + ".44444444444444444444";

		// Art. 2.
		if (minero) {
			parametrosO[i++] = faseN + tipoMaterial;
			parametrosO[i++] = mineroP;
		} else {
			parametrosO[i++] = faseN;
			parametrosO[i++] = mineroPLibre;
		}

		// (LIBRES APROVECHAMIENTOS O MATERIALES DE CONSTRUCCIÓN)
		// parametrosO[i++] = faseN;

		// “XXXXX(nombre del proyecto) (CÓD. XXXXX)
		// parametrosO[i++] = proyectoActivo.getNombre() + " " +
		// proyectoActivo.getCodigo();

		// ubicación
		parametrosO[i++] = ubicacion + ".";

		// /
		parametrosO[i++] = numeroResolucion;

		// REGISTRO AMBIENTAL PARA EL PROYECTO OBRA O ACTIVIDAD

		// LIBRES APROVECHAMIENTOS O MATERIALES DE CONSTRUCCIÓN
		if (minero) {
			parametrosO[i++] = faseN + tipoMaterial;
			parametrosO[i++] = mineroP;

		} else {
			parametrosO[i++] = faseN;
			parametrosO[i++] = mineroPLibre;
		}
		// XXXXX(nombre del proyecto) (CÓD. XXXXX)
		// parametrosO[i++] = proyectoActivo.getNombre() + " " +
		// proyectoActivo.getCodigo();
		// ubicación
		parametrosO[i++] = ubicacion + ".";

		// (XXXXXX INCLUIR NOMBRE DEL TITULAR MINERO),
		parametrosO[i++] = titular.getNombre();

		// LIBRES APROVECHAMIENTOS O MATERIALES DE CONSTRUCCIÓN) (MINERÍA
		// ARTESANAL)

		if (minero) {
			parametrosO[i++] = faseN + tipoMaterial;
			parametrosO[i++] = mineroP;

		} else {
			parametrosO[i++] = faseN;
			parametrosO[i++] = mineroPLibre;
		}

		// parametrosO[i++] = fase + tipoMaterial;
		// parametrosO[i++] = faseN;

		// XXXXX(nombre del proyecto) (CÓD. XXXXX)
		// parametrosO[i++] = proyectoActivo.getNombre() + " " +
		// proyectoActivo.getCodigo();

		// ubicación
		parametrosO[i++] = ubicacion;

		// (TITULAR MINERO)
//		parametrosO[i++] = titular.getNombre()+"4444";
		parametrosO[i++] = nombreEmpresaPromotor;

		// No XXXXX (código generado por el sistema)
		parametrosO[i++] = numeroLicencia;

		// Descripción de la actividad TOMADO DEL REGISTRO
		parametrosO[i++] = proyectoActivo.getCatalogoCategoria()
				.getDescripcion();

		parametrosO[i++] = sector;
		// Parroquia, cantón, provincia
		parametrosO[i++] = ubicacionCompleta;

		// Nombre del representante legal
		parametrosO[i++] = nombreRepresentante;

		parametrosO[i++] = direccion;
		parametrosO[i++] = telefono;
		parametrosO[i++] = email;

		// Código del Proyecto
		parametrosO[i++] = proyectoActivo.getCodigo();

		// INCLUIR NOMBRE DE LA EMPRESA O PROMOTOR, QUE LO TOMA DEL REGISTRO
		parametrosO[i++] = nombreEmpresaPromotor;

		// LIBRES APROVECHAMIENTOS O MATERIALES DE CONSTRUCCIÓN
		// parametrosO[i++] = faseN;
		if (minero) {
			parametrosO[i++] = faseN + tipoMaterial;
			parametrosO[i++] = mineroP;
		} else {
			parametrosO[i++] = faseN;
			parametrosO[i++] = mineroPLibre;
		}

		// XXXXX(nombre del proyecto) (CÓD. XXXXX)
		// parametrosO[i++] = proyectoActivo.getNombre() + " " +
		// proyectoActivo.getCodigo();

		// ubicación
		parametrosO[i++] = ubicacion;

		/*
		* //LIBRES APROVECHAMIENTOS O MATERIALES DE CONSTRUCCIÓN
		* parametrosO[i++] = fase;
		* 
		* //XXXXX(nombre del proyecto) (CÓD. XXXXX) parametrosO[i++] =
		* proyectoActivo.getNombre() + " " + proyectoActivo.getCodigo();
		* 
		* // ubicación parametrosO[i++] = ubicacion;
		*/
		// Certificado de Intersección XXX(No de oficio xxxxxxx)
		CertificadoInterseccion ci = certificadoInterseccionFacade
				.recuperarCertificadoInterseccion(proyectoActivo);
		String certificadoCodigo = "";
		if (ci != null) {
			certificadoCodigo = ci.getCodigo();
		}
		parametrosO[i++] = certificadoCodigo;

		Boolean genera = proyectoActivo.getTipoEstudio().getId() == 2
				&& proyectoActivo.getGeneraDesechos() != null
				&& proyectoActivo.getGeneraDesechos() == true;
		String dias = genera ? "30" : "60";

		// dias
		parametrosO[i++] = dias;

		String normativa_4 = "";
		if (minero) {
			normativa_4 = "Presentar al Ministerio del Ambiente y Agua los Informes Ambientales de Cumplimiento, de conformidad con lo establecido el art&iacute;culo 134 de la Reforma del Reglamento Ambiental de Actividades Mineras, publicado en el Suplemento del Registro Oficial No. 213, de 27 de marzo de 2014, cuya periodicidad es anual.";
		} else {
			normativa_4 = "Presentar al Ministerio del Ambiente y Agua los Informes Ambientales de Cumplimiento, de conformidad con lo establecido el art&iacute;culo 51 de la Reforma del Reglamento Ambiental de Actividades Mineras, publicado en el Suplemento del Registro Oficial No. 213, de 27 de marzo de 2014, cuya periodicidad es anual en concordancia con el artículo 262 del Acuerdo Ministerial No. 061 de 07 de abril de 2015.";
		}
		parametrosO[i++] = normativa_4;

		String normativa_5 = "";
		if (minero) {
			normativa_5 = "Se mantendr&aacute; un programa continuo de monitoreo y seguimiento a las medidas contempladas en el Plan de Manejo Ambiental, cuyos resultados deberán ser entregados al Ministerio del Ambiente y Agua con el Informe Ambiental de Cumplimiento.";
		} else {
			normativa_5 = "Se mantendr&aacute; un programa continuo de monitoreo y seguimiento a las medidas contempladas en el Plan de Manejo Ambiental, cuyos resultados deberán ser entregados al Ministerio del Ambiente y Agua de manera trimestral para su respectiva evaluaci&oacute;n o correctivos tempranos de manejo, mediante lo establecido el art&iacute;culo 47 de la Reforma del Reglamento Ambiental de Actividades Mineras, publicado en el Suplemento del Registro Oficial No. 213 de 27 de marzo de 2014.";
		}
		parametrosO[i++] = normativa_5;

		// fecha
//		parametrosO[i++] = getDateFormat(proyectoActivo.getFechaRegistro());
		parametrosO[i++] = getDateFormat(new Date());
//		parametrosO[i++] = "11 de Junio de 2020";
		parametrosO[39]=proyectoActivo.getCodigo();
		return parametrosO;
	}

	public String[] cargarDatosLicenciaAmbientalExploracionInicial(
			ProyectoLicenciamientoAmbiental proyectoActivo, String userName)
			throws Exception {
		// Número resolucion LAST MOD 26/01/2016
		String numeroResolucion = null;
		if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {

			Date fecha = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(fecha);
			int anno = calendar.get(Calendar.YEAR);

			numeroResolucion = proyectoActivo.getAreaResponsable()
					.getAreaAbbreviation()
					+ "-"+anno+"-"
					+ Long.toString(secuenciasFacade
					.getNextValueDedicateSequence("entes"));
		} else {
			numeroResolucion = Long
					.toString(secuenciasFacade
							.getNextValueDedicateSequence("numeroResolucionCategoriaII"));
		}

		// Número de licencia
		String numeroLicencia=null;
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado()!=null){
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
			 numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable().getArea());
		}else{
			 numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		}else{
			numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		FichaAmbientalPma fichaAmbientalPma = fichaAmbientalPmaFacade
				.getFichaAmbientalPorCodigoProyecto(proyectoActivo.getCodigo());
		fichaAmbientalPma.setNumeroLicencia(numeroLicencia);
		fichaAmbientalPma.setNumeroOficio(numeroResolucion);
		fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbientalPma);
		// No. Oficio
		// *************************************************************************
		String numeroOficio=null;
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado()!=null){
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
			 numeroOficio = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable().getArea());
		}else{
			 numeroOficio = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		} else{
			numeroOficio = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		String canton = "";
		String provincia = "";
		String ubicacion = "";
		String ubicacionCompleta = "";
		String cantonUsuario = "";
		String provinciaUsuario = "";

		String nombreEmpresaPromotor = "";
		String nombreRepresentante = "";

		// String fase = minero ? "MINERÍA ARTESANAL" :
		// "LIBRES APROVECHAMIENTOS O MATERIALES DE CONSTRUCCIÓN";
		String fase = "PARA LA FASE DE EXPLORACIÓN INICIAL DE LA CONCESIÓN MINERA";

		String tipoMaterial = "";

		if (proyectoActivo.getTipoMaterial() != null) {
			if (proyectoActivo.getTipoMaterial().getId() == TipoMaterial.MATERIAL_CONSTRUCCION) {
				tipoMaterial = " Materiales de construcción ";
			} else if (proyectoActivo.getTipoMaterial().getId() == TipoMaterial.MATERIAL_METALICO) {
				tipoMaterial = " minerales Metálicos ";
			} else if (proyectoActivo.getTipoMaterial().getId() == TipoMaterial.MATERIAL_NO_METALICO) {
				tipoMaterial = " minerales No Metálicos ";
			}
		}
		try {

			List<UbicacionesGeografica> ubicaciones = proyectoActivo
					.getUbicacionesGeograficas();
			if (ubicaciones.size() == 1) {
				UbicacionesGeografica parroquiaU = ubicaciones.get(0);
				if (parroquiaU.getUbicacionesGeografica() != null) {
					UbicacionesGeografica cantonU = parroquiaU
							.getUbicacionesGeografica();
					canton = cantonU.getNombre();
					if (cantonU.getUbicacionesGeografica() != null) {
						UbicacionesGeografica provinciaU = cantonU
								.getUbicacionesGeografica();
						provincia = provinciaU.getNombre();
					}
				}
				ubicacionCompleta = provincia + ", " + canton + ", "
						+ parroquiaU.getNombre();
				ubicacion = " LA PROVINCIA DE " + provincia;

			} else {
				ubicacionCompleta = "<table>";

				ubicacionCompleta += "<tr><td>PROVINCIA</td><td>CANTÓN</td><td>PARROQUIA</td></tr>";
				List<String> listaProvincias = new ArrayList<String>();
				for (UbicacionesGeografica ubicacionActual : ubicaciones) {
					UbicacionesGeografica cantonU = ubicacionActual
							.getUbicacionesGeografica();
					canton = cantonU.getNombre();
					boolean repetida = false;
					if (cantonU.getUbicacionesGeografica() != null) {
						UbicacionesGeografica provinciaU = cantonU
								.getUbicacionesGeografica();
						provincia = provinciaU.getNombre();
						if (listaProvincias.contains(provincia)) {
							repetida = true;
						} else {
							listaProvincias.add(provincia);
						}
					}
					if (!repetida) {
						if (ubicacion.isEmpty()) {
							ubicacion += provincia;
						} else {
							ubicacion += ", " + provincia;
						}
					}

					ubicacionCompleta += "<tr><td>" + provincia + "</td><td>"
							+ canton + "</td><td>"
							+ ubicacionActual.getNombre() + "</td></tr>";
				}

				if (listaProvincias.size() > 1) {
					ubicacion = " LAS PROVINCIAS (" + ubicacion + ")";
				} else {
					ubicacion = " LA PROVINCIA " + ubicacion;
				}

				ubicacionCompleta += "</table>";
			}
		} catch (Exception e) {
			LOG.error("Error recuperando la ubicación del proyecto", e);
		}

		// Titular
		Usuario usuario = proyectoActivo.getUsuario();
		usuario = usuarioFacade.buscarUsuarioPorIdFull(usuario.getId());
		Persona titular = usuario.getPersona();
		String tratoTitular = titular.getTipoTratos() == null ? "Sr.|Sra.|Sta."
				: titular.getTipoTratos().getNombre();

		Organizacion organizacion = null;
		try {
			organizacion = organizacionFacade.buscarPorPersona(titular,
					userName);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		List<Contacto> contactos;
		contactos = contactoFacade
				.buscarUsuarioNativeQuery(usuario.getNombre());// buscarPorOrganizacion(organizacion);

		UbicacionesGeografica ubicacionProponente = new UbicacionesGeografica();
		if (organizacion != null) {
			ubicacionProponente = organizacion.getPersona()
					.getUbicacionesGeografica();
			nombreEmpresaPromotor = organizacion.getNombre();
			nombreRepresentante = organizacion.getPersona().getNombre();
		} else {
			ubicacionProponente = titular.getUbicacionesGeografica();
			nombreEmpresaPromotor = titular.getNombre();
			nombreRepresentante = nombreEmpresaPromotor;
		}

		if (ubicacionProponente.getUbicacionesGeografica() != null) {
			cantonUsuario = ubicacionProponente.getUbicacionesGeografica()
					.getNombre();
			if (ubicacionProponente.getUbicacionesGeografica()
					.getUbicacionesGeografica() != null) {
				provinciaUsuario = ubicacionProponente
						.getUbicacionesGeografica().getUbicacionesGeografica()
						.getNombre();

			}
		}

		String direccion = NADA;
		String telefono = NADA;
		String email = NADA;
		for (Contacto contacto : contactos) {
			if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.DIRECCION)) {
				// Dirección
				direccion = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.TELEFONO)) {
				// Teléfono
				telefono = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.EMAIL)) {
				email = contacto.getValor();
			}
		}

		String sector = proyectoActivo.getCatalogoCategoria()
				.getCatalogoCategoriaPublico().getTipoSector().toString();
		String codigoCM = "";
		if (proyectoActivo.isConcesionesMinerasMultiples()) {
			int cont = 0;
			int cantTotal = proyectoActivo.getConcesionesMineras().size();
			for (ConcesionMinera c : proyectoActivo.getConcesionesMineras()) {
				if (codigoCM.isEmpty()) {
					codigoCM = "las Concesiones Minera " + c.getCodigo();
				} else if (cont == cantTotal - 1) {
					codigoCM += " y " + c.getCodigo();
				} else {
					codigoCM += ", " + c.getCodigo();
				}
				cont++;
			}
		} else {
			codigoCM = "la Concesi&oacute;n Minera "
					+ proyectoActivo.getCodigoMinero();
		}
		// if(proyectoActivo.getConcesionMinera() != null &&
		// proyectoActivo.getConcesionMinera().getCodigo() != null) {
		// codigoCM = proyectoActivo.getConcesionMinera().getCodigo();
		// }

		String[] parametrosO = new String[40];
		Integer i = 0;
		// ---------------------------------------------------------------------
		// No de la resolución
		parametrosO[i++] = numeroResolucion;
//		// Art. 1.
//
//		// “XXXXX(nombre del proyecto) (CÓD. XXXXX)
//		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
//				.getNombre()) + " " + proyectoActivo.getCodigo();
//
//		// ubicación
//		parametrosO[i++] = ubicacion;
//
//		// , cuyo representante legal es
//		parametrosO[i++] = nombreRepresentante;
		// Art. 2.
		// “XXXXX(nombre del proyecto) (CÓD. XXXXX)
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
				.getNombre()) + " " + proyectoActivo.getCodigo();

		// ubicación
		parametrosO[i++] = ubicacion;
		// , cuyo representante legal es
		parametrosO[i++] = nombreRepresentante;

		// REGISTRO AMBIENTAL PARA EL PROYECTO OBRA O ACTIVIDAD
		// No de la resolución
		parametrosO[i++] = numeroResolucion;

		// LIBRES APROVECHAMIENTOS O MATERIALES DE CONSTRUCCIÓN
		parametrosO[i++] = proyectoActivo.getNombre() + " "
				+ proyectoActivo.getCodigo();
		// ubicación
		parametrosO[i++] = ubicacion + ".";

		// (XXXXXX INCLUIR NOMBRE DEL TITULAR MINERO),
		parametrosO[i++] = titular.getNombre();

		// (Materiales de construcción) O (minerales Metálicos) O (minerales No
		// Metálicos)
		parametrosO[i++] = tipoMaterial;

		// XXXXX(nombre del proyecto) (CÓD. XXXXX)
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
				.getNombre()) + " " + proyectoActivo.getCodigo();

		// ubicación
		parametrosO[i++] = ubicacion;

		//
		// //XXXXX(nombre del proyecto) (CÓD. XXXXX)
		// parametrosO[i++] = proyectoActivo.getNombre() + " " +
		// proyectoActivo.getCodigo();
		//
		// // ubicación
		// parametrosO[i++] = ubicacion;

		// (TITULAR MINERO)
//		parametrosO[i++] = titular.getNombre()+"sdddgghhhhhh";
		parametrosO[i++] = nombreEmpresaPromotor;

		// No XXXXX (código generado por el sistema)
		parametrosO[i++] = numeroLicencia;

		// DATOS TÉCNICOS:
		// Descripción de la actividad TOMADO DEL REGISTRO
		parametrosO[i++] = proyectoActivo.getCatalogoCategoria()
				.getDescripcion();

		// Sector
		parametrosO[i++] = sector;

		// Parroquia, cantón, provincia
		parametrosO[i++] = ubicacionCompleta;

		// Nombre del representante legal
		parametrosO[i++] = nombreRepresentante;

		parametrosO[i++] = direccion;
		parametrosO[i++] = telefono;
		parametrosO[i++] = email;

		// Código del Proyecto
		parametrosO[i++] = proyectoActivo.getCodigo();

		// INCLUIR NOMBRE DE LA EMPRESA O PROMOTOR, QUE LO TOMA DEL REGISTRO
		parametrosO[i++] = nombreEmpresaPromotor;
		// como titular de la Concesión Minera
		parametrosO[i++] = codigoCM;
		Boolean genera = proyectoActivo.getTipoEstudio().getId() == 2
				&& proyectoActivo.getGeneraDesechos() != null
				&& proyectoActivo.getGeneraDesechos() == true;
		String dias = genera ? "30" : "60";

		// dias
		parametrosO[i++] = dias;

		// fechas
//		parametrosO[i++] = getDateFormat(proyectoActivo.getFechaRegistro());
		parametrosO[i++] = getDateFormat(new Date());
//		parametrosO[i++] = "11 de Junio de 2020";
		parametrosO[39]=proyectoActivo.getCodigo();
		return parametrosO;
	}

	public String[] cargarDatosAnexosLicenciaAmbiental(
			ProyectoLicenciamientoAmbiental proyectoActivo) throws Exception {

		String[] parametrosO = new String[20];
		Integer i = 0;
		// NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD
		parametrosO[i++] = proyectoActivo.getNombre();
		// Coordenadas
		parametrosO[i++] = generarTablaCoordenadas(proyectoActivo);
		return parametrosO;
	}

	public String[] cargarDatosAnexosCompletoLicenciaAmbiental(
			ProyectoLicenciamientoAmbiental proyectoActivo) throws Exception {

		String[] parametrosO = new String[20];
		Integer i = 0;
		// nombre del proponente
		parametrosO[i++] = proyectoActivo.getUsuario().getPersona().getNombre();
		// cédula de proponente)
		parametrosO[i++] = proyectoActivo.getUsuario().getPersona().getPin();

		// nombre del proponente
		parametrosO[i++] = proyectoActivo.getUsuario().getPersona().getNombre();
		// cédula de proponente)
		parametrosO[i++] = proyectoActivo.getUsuario().getPersona().getPin();

		// NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
				.getNombre());
		// Coordenadas
		parametrosO[i++] = generarTablaCoordenadas(proyectoActivo);
		return parametrosO;
	}

	public Persona getPersonaAutoridadAmbiental() throws ServiceException {
		return autoridadAmbientalFacade
				.getDirectorPrevencionContaminacionAmbiental();
	}

	public Persona getPersonaSubsecretarioCalidadAmbiental()
			throws ServiceException {
		return autoridadAmbientalFacade.getSubsecretarioCalidadAmbiental();
	}

	public Usuario getPersonaSubsecretarioCalidadAmbiental(Area areaResponsable)
			throws ServiceException {
		return autoridadAmbientalFacade
				.getSubsecretarioCalidadAmbiental(areaResponsable);
	}

	public byte[] getFirmaAutoridadPrevencion() throws ServiceException,
			CmisAlfrescoException {
		try {
			return documentosFacade
					.descargarDocumentoPorNombre("firmaDirectorPrevencion.jpg");

		} catch (RuntimeException e) {
			throw new ServiceException(
					"Error al recuperar la firma para el certificado", e);
		}
	}

	public byte[] getFirmaSubsecretarioCalidadAmbiental() {
		try {
			return documentosFacade
					.descargarDocumentoPorNombre("firmaSubsecretarioCalidadAmbiental.png");
		} catch (Exception e) {
			return null;
		}
	}
	
	public byte[] getLogo(Area area)throws ServiceException, CmisAlfrescoException  {
		try {
			return documentosFacade.descargarDocumentoPorNombre("logo__"
					+ area.getAreaAbbreviation().replace("/", "_") + ".png");
		} catch (RuntimeException e) {
			throw new ServiceException(
					"Error al recuperar logo para documento", e);
		}
	}

	public byte[] getFirmaSubsecretarioCalidadAmbiental(Area area)
			throws ServiceException, CmisAlfrescoException {
		try {
			return documentosFacade.descargarDocumentoPorNombre("firma__"
					+ area.getAreaAbbreviation().replace("/", "_") + ".png");
		} catch (RuntimeException e) {
			throw new ServiceException(
					"Error al recuperar la firma para el certificado", e);
		}
	}

	/**
	 * Nombre:SUIA Descripción: Obterner la autoridad ambiental de acuerdo al
	 * área del ente.
	 * 
	 * @param area
	 * @return
	 * @throws ServiceException
	 *             Fecha:16/08/2015
	 */
	public Persona getPersonaAutoridadAmbientalEnte(String area)
			throws ServiceException {
		return autoridadAmbientalFacade.getAutoridadAmbientalEnte(area);
	}

	/**
	 * FIN Obterner la autoridad ambiental de acuerdo al área del ente.
	 */
	/**
	 * Nombre:SUIA Descripción: validación de la firma y autoridad ambiental
	 * entes
	 * 
	 * @param firma
	 * @return Fecha:16/08/2015
	 */
	public byte[] getFirmaAutoridadAmbientalEntes(String firma) {
		try {
			return documentosFacade.descargarDocumentoPorNombre(firma);
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * FIN validación de la firma y autoridad ambiental entes.
	 */
	
	
	public String[] cargarDatosLicenciaAmbientalPerforacionExplorativa(
			ProyectoLicenciamientoAmbiental proyectoActivo, String userName)
			throws Exception {
		// Número resolucion LAST MOD 26/01/2016
		String numeroResolucion = null;
		if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {

			Date fecha = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(fecha);
			int anno = calendar.get(Calendar.YEAR);

			numeroResolucion = proyectoActivo.getAreaResponsable()
					.getAreaAbbreviation()
					+ "-"+anno+"-"
					+ Long.toString(secuenciasFacade
					.getNextValueDedicateSequence("entes"));
		} else {
			numeroResolucion = Long
					.toString(secuenciasFacade
							.getNextValueDedicateSequence("numeroResolucionCategoriaII"));
		}

		// Número de licencia
		String numeroLicencia=null;
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado()!=null){
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
			 numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable().getArea());
		}else{
			 numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		}else{
			numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		FichaAmbientalPma fichaAmbientalPma = fichaAmbientalPmaFacade
				.getFichaAmbientalPorCodigoProyecto(proyectoActivo.getCodigo());
		fichaAmbientalPma.setNumeroLicencia(numeroLicencia);
		fichaAmbientalPma.setNumeroOficio(numeroResolucion);
		fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbientalPma);
		// No. Oficio
		// *************************************************************************
		String numeroOficio=null;
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado()!=null){
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
			 numeroOficio = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable().getArea());
		}else{
			 numeroOficio = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		}else{
			numeroOficio = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		
		String canton = "";
		String provincia = "";
		String ubicacion = "";
		String ubicacionCompleta = "";
		String cantonUsuario = "";
		String provinciaUsuario = "";

		String nombreEmpresaPromotor = "";
		String nombreRepresentante = "";

		// String fase = minero ? "MINERÍA ARTESANAL" :
		// "LIBRES APROVECHAMIENTOS O MATERIALES DE CONSTRUCCIÓN";
		String fase = "PARA LA FASE DE EXPLORACIÓN INICIAL DE LA CONCESIÓN MINERA";

		String tipoMaterial = "";

		if (proyectoActivo.getTipoMaterial() != null) {
			if (proyectoActivo.getTipoMaterial().getId() == TipoMaterial.MATERIAL_CONSTRUCCION) {
				tipoMaterial = " Materiales de construcción ";
			} else if (proyectoActivo.getTipoMaterial().getId() == TipoMaterial.MATERIAL_METALICO) {
				tipoMaterial = " minerales Metálicos ";
			} else if (proyectoActivo.getTipoMaterial().getId() == TipoMaterial.MATERIAL_NO_METALICO) {
				tipoMaterial = " minerales No Metálicos ";
			}
		}
		try {

			List<UbicacionesGeografica> ubicaciones = proyectoActivo
					.getUbicacionesGeograficas();
			if (ubicaciones.size() == 1) {
				UbicacionesGeografica parroquiaU = ubicaciones.get(0);
				if (parroquiaU.getUbicacionesGeografica() != null) {
					UbicacionesGeografica cantonU = parroquiaU
							.getUbicacionesGeografica();
					canton = cantonU.getNombre();
					if (cantonU.getUbicacionesGeografica() != null) {
						UbicacionesGeografica provinciaU = cantonU
								.getUbicacionesGeografica();
						provincia = provinciaU.getNombre();
					}
				}
				ubicacionCompleta = provincia + ", " + canton + ", "
						+ parroquiaU.getNombre();
				ubicacion = " LA PROVINCIA DE " + provincia;

			} else {
				ubicacionCompleta = "<table>";

				ubicacionCompleta += "<tr><td>PROVINCIA</td><td>CANTÓN</td><td>PARROQUIA</td></tr>";
				List<String> listaProvincias = new ArrayList<String>();
				for (UbicacionesGeografica ubicacionActual : ubicaciones) {
					UbicacionesGeografica cantonU = ubicacionActual
							.getUbicacionesGeografica();
					canton = cantonU.getNombre();
					boolean repetida = false;
					if (cantonU.getUbicacionesGeografica() != null) {
						UbicacionesGeografica provinciaU = cantonU
								.getUbicacionesGeografica();
						provincia = provinciaU.getNombre();
						if (listaProvincias.contains(provincia)) {
							repetida = true;
						} else {
							listaProvincias.add(provincia);
						}
					}
					if (!repetida) {
						if (ubicacion.isEmpty()) {
							ubicacion += provincia;
						} else {
							ubicacion += ", " + provincia;
						}
					}

					ubicacionCompleta += "<tr><td>" + provincia + "</td><td>"
							+ canton + "</td><td>"
							+ ubicacionActual.getNombre() + "</td></tr>";
				}

				if (listaProvincias.size() > 1) {
					ubicacion = " LAS PROVINCIAS (" + ubicacion + ")";
				} else {
					ubicacion = " LA PROVINCIA " + ubicacion;
				}

				ubicacionCompleta += "</table>";
			}
		} catch (Exception e) {
			LOG.error("Error recuperando la ubicación del proyecto", e);
		}

		// Titular
		Usuario usuario = proyectoActivo.getUsuario();
		usuario = usuarioFacade.buscarUsuarioPorIdFull(usuario.getId());
		Persona titular = usuario.getPersona();
		String tratoTitular = titular.getTipoTratos() == null ? "Sr.|Sra.|Sta."
				: titular.getTipoTratos().getNombre();

		Organizacion organizacion = null;
		try {
			organizacion = organizacionFacade.buscarPorPersona(titular,
					userName);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		List<Contacto> contactos;
		contactos = contactoFacade
				.buscarUsuarioNativeQuery(usuario.getNombre());// buscarPorOrganizacion(organizacion);

		UbicacionesGeografica ubicacionProponente = new UbicacionesGeografica();
		if (organizacion != null) {
			ubicacionProponente = organizacion.getPersona()
					.getUbicacionesGeografica();
			nombreEmpresaPromotor = organizacion.getNombre();
			nombreRepresentante = organizacion.getPersona().getNombre();
		} else {
			ubicacionProponente = titular.getUbicacionesGeografica();
			nombreEmpresaPromotor = titular.getNombre();
			nombreRepresentante = nombreEmpresaPromotor;
		}

		if (ubicacionProponente.getUbicacionesGeografica() != null) {
			cantonUsuario = ubicacionProponente.getUbicacionesGeografica()
					.getNombre();
			if (ubicacionProponente.getUbicacionesGeografica()
					.getUbicacionesGeografica() != null) {
				provinciaUsuario = ubicacionProponente
						.getUbicacionesGeografica().getUbicacionesGeografica()
						.getNombre();

			}
		}

		String direccion = NADA;
		String telefono = NADA;
		String email = NADA;
		for (Contacto contacto : contactos) {
			if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.DIRECCION)) {
				// Dirección
				direccion = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.TELEFONO)) {
				// Teléfono
				telefono = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.EMAIL)) {
				email = contacto.getValor();
			}
		}

		String sector = proyectoActivo.getCatalogoCategoria()
				.getCatalogoCategoriaPublico().getTipoSector().toString();
		String codigoCM = "";
		if (proyectoActivo.isConcesionesMinerasMultiples()) {
			int cont = 0;
			int cantTotal = proyectoActivo.getConcesionesMineras().size();
			for (ConcesionMinera c : proyectoActivo.getConcesionesMineras()) {
				if (codigoCM.isEmpty()) {
					codigoCM = "las Concesiones Minera " + c.getCodigo();
				} else if (cont == cantTotal - 1) {
					codigoCM += " y " + c.getCodigo();
				} else {
					codigoCM += ", " + c.getCodigo();
				}
				cont++;
			}
		} else {
			codigoCM = "la Concesi&oacute;n Minera "
					+ proyectoActivo.getCodigoMinero();
		}
		// if(proyectoActivo.getConcesionMinera() != null &&
		// proyectoActivo.getConcesionMinera().getCodigo() != null) {
		// codigoCM = proyectoActivo.getConcesionMinera().getCodigo();
		// }

		String[] parametrosO = new String[40];
		Integer i = 0;
		// ---------------------------------------------------------------------
		// No de la resolución
		parametrosO[i++] = numeroResolucion;
//		// Art. 1.
//
//		// “XXXXX(nombre del proyecto) (CÓD. XXXXX)
//		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
//				.getNombre()) + " " + proyectoActivo.getCodigo();
//
//		// ubicación
//		parametrosO[i++] = ubicacion;
//
//		// , cuyo representante legal es
//		parametrosO[i++] = nombreRepresentante;
		// Art. 2.
		// “XXXXX(nombre del proyecto) (CÓD. XXXXX)
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
				.getNombre()) + " " + proyectoActivo.getCodigo();

		// ubicación
		parametrosO[i++] = ubicacion;
		// , cuyo representante legal es
		parametrosO[i++] = nombreRepresentante;

		// REGISTRO AMBIENTAL PARA EL PROYECTO OBRA O ACTIVIDAD
		// No de la resolución
		parametrosO[i++] = numeroResolucion;

		// LIBRES APROVECHAMIENTOS O MATERIALES DE CONSTRUCCIÓN
		parametrosO[i++] = proyectoActivo.getNombre() + " "
				+ proyectoActivo.getCodigo();
		// ubicación
		parametrosO[i++] = ubicacion + ".";

		// (XXXXXX INCLUIR NOMBRE DEL TITULAR MINERO),
		parametrosO[i++] = titular.getNombre();

		// (Materiales de construcción) O (minerales Metálicos) O (minerales No
		// Metálicos)
		parametrosO[i++] = tipoMaterial;

		// XXXXX(nombre del proyecto) (CÓD. XXXXX)
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo
				.getNombre()) + " " + proyectoActivo.getCodigo();

		// ubicación
		parametrosO[i++] = ubicacion;

		//
		// //XXXXX(nombre del proyecto) (CÓD. XXXXX)
		// parametrosO[i++] = proyectoActivo.getNombre() + " " +
		// proyectoActivo.getCodigo();
		//
		// // ubicación
		// parametrosO[i++] = ubicacion;

		// (TITULAR MINERO)
//		parametrosO[i++] = titular.getNombre()+"sdddgghhhhhh";
		parametrosO[i++] = nombreEmpresaPromotor;

		// No XXXXX (código generado por el sistema)
		parametrosO[i++] = numeroLicencia;

		// DATOS TÉCNICOS:
		// Descripción de la actividad TOMADO DEL REGISTRO
		parametrosO[i++] = proyectoActivo.getCatalogoCategoria()
				.getDescripcion();

		// Sector
		parametrosO[i++] = sector;

		// Parroquia, cantón, provincia
		parametrosO[i++] = ubicacionCompleta;

		// Nombre del representante legal
		parametrosO[i++] = nombreRepresentante;

		parametrosO[i++] = direccion;
		parametrosO[i++] = telefono;
		parametrosO[i++] = email;

		// Código del Proyecto
		parametrosO[i++] = proyectoActivo.getCodigo();

		// INCLUIR NOMBRE DE LA EMPRESA O PROMOTOR, QUE LO TOMA DEL REGISTRO
		parametrosO[i++] = nombreEmpresaPromotor;
		// como titular de la Concesión Minera
		parametrosO[i++] = codigoCM;
		Boolean genera = proyectoActivo.getTipoEstudio().getId() == 2
				&& proyectoActivo.getGeneraDesechos() != null
				&& proyectoActivo.getGeneraDesechos() == true;
		String dias = genera ? "30" : "60";

		// dias
		parametrosO[i++] = dias;

		// fechas
//		parametrosO[i++] = getDateFormat(proyectoActivo.getFechaRegistro());
		parametrosO[i++] = getDateFormat(new Date());
//		parametrosO[i++] = "11 de Junio de 2020";
		parametrosO[39]=proyectoActivo.getCodigo();
		return parametrosO;
	}
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private ProyectoLicenciaAmbientalConcesionesMinerasFacade proyectoConcesionesMinerasFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	public String[] cargarDatosLicenciaAmbientalPerforacionExplorativaRCOA(
			ProyectoLicenciaCoa proyectoActivo, String userName)
			throws Exception {
		// Número resolucion LAST MOD 26/01/2016
		String numeroResolucion = null;
		if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {

			Date fecha = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(fecha);
			int anno = calendar.get(Calendar.YEAR);

			numeroResolucion = proyectoActivo.getAreaResponsable()
					.getAreaAbbreviation()
					+ "-"+anno+"-"
					+ Long.toString(secuenciasFacade
					.getNextValueDedicateSequence("entes"));
		} else {
			numeroResolucion = Long
					.toString(secuenciasFacade
							.getNextValueDedicateSequence("numeroResolucionCategoriaII"));
		}

		// Número de licencia
		String numeroLicencia=null;
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado()!=null){
		if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
			 numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable().getArea());
		}else{
			 numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		}else{
			numeroLicencia = secuenciasFacade
					.getSecuenciaResolucionAreaResponsable(proyectoActivo
							.getAreaResponsable());
		}
		
		String canton = "";
		String provincia = "";
		String ubicacion = "";
		String ubicacionCompleta = "";
		String cantonUsuario = "";
		String provinciaUsuario = "";

		String nombreEmpresaPromotor = "";
		String nombreRepresentante = "";
		
		String fase = "PARA LA FASE DE EXPLORACIÓN INICIAL DE LA CONCESIÓN MINERA";

		String tipoMaterial = "";

//		if (proyectoActivo.getTipoMaterial() != null) {
//			if (proyectoActivo.getTipoMaterial().getId() == TipoMaterial.MATERIAL_CONSTRUCCION) {
//				tipoMaterial = " Materiales de construcción ";
//			} else if (proyectoActivo.getTipoMaterial().getId() == TipoMaterial.MATERIAL_METALICO) {
//				tipoMaterial = " minerales Metálicos ";
//			} else if (proyectoActivo.getTipoMaterial().getId() == TipoMaterial.MATERIAL_NO_METALICO) {
//				tipoMaterial = " minerales No Metálicos ";
//			}
//		}
		try {

			List<UbicacionesGeografica> ubicaciones = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoActivo);
			if (ubicaciones.size() == 1) {
				UbicacionesGeografica parroquiaU = ubicaciones.get(0);
				if (parroquiaU.getUbicacionesGeografica() != null) {
					UbicacionesGeografica cantonU = parroquiaU
							.getUbicacionesGeografica();
					canton = cantonU.getNombre();
					if (cantonU.getUbicacionesGeografica() != null) {
						UbicacionesGeografica provinciaU = cantonU
								.getUbicacionesGeografica();
						provincia = provinciaU.getNombre();
					}
				}
				ubicacionCompleta = provincia + ", " + canton + ", "
						+ parroquiaU.getNombre();
				ubicacion = " LA PROVINCIA DE " + provincia;

			} else {
				ubicacionCompleta = "<table>";

				ubicacionCompleta += "<tr><td>PROVINCIA</td><td>CANTÓN</td><td>PARROQUIA</td></tr>";
				List<String> listaProvincias = new ArrayList<String>();
				for (UbicacionesGeografica ubicacionActual : ubicaciones) {
					UbicacionesGeografica cantonU = ubicacionActual
							.getUbicacionesGeografica();
					canton = cantonU.getNombre();
					boolean repetida = false;
					if (cantonU.getUbicacionesGeografica() != null) {
						UbicacionesGeografica provinciaU = cantonU
								.getUbicacionesGeografica();
						provincia = provinciaU.getNombre();
						if (listaProvincias.contains(provincia)) {
							repetida = true;
						} else {
							listaProvincias.add(provincia);
						}
					}
					if (!repetida) {
						if (ubicacion.isEmpty()) {
							ubicacion += provincia;
						} else {
							ubicacion += ", " + provincia;
						}
					}

					ubicacionCompleta += "<tr><td>" + provincia + "</td><td>"
							+ canton + "</td><td>"
							+ ubicacionActual.getNombre() + "</td></tr>";
				}

				if (listaProvincias.size() > 1) {
					ubicacion = " LAS PROVINCIAS (" + ubicacion + ")";
				} else {
					ubicacion = " LA PROVINCIA " + ubicacion;
				}

				ubicacionCompleta += "</table>";
			}
		} catch (Exception e) {
			LOG.error("Error recuperando la ubicación del proyecto", e);
		}

		// Titular
		Usuario usuario = proyectoActivo.getUsuario();
		usuario = usuarioFacade.buscarUsuarioPorIdFull(usuario.getId());
		Persona titular = usuario.getPersona();
		String tratoTitular = titular.getTipoTratos() == null ? "Sr.|Sra.|Sta."
				: titular.getTipoTratos().getNombre();

		Organizacion organizacion = null;
		try {
			organizacion = organizacionFacade.buscarPorPersona(titular,
					userName);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		List<Contacto> contactos;
		contactos = contactoFacade
				.buscarUsuarioNativeQuery(usuario.getNombre());// buscarPorOrganizacion(organizacion);

		UbicacionesGeografica ubicacionProponente = new UbicacionesGeografica();
		if (organizacion != null) {
			ubicacionProponente = organizacion.getPersona()
					.getUbicacionesGeografica();
			nombreEmpresaPromotor = organizacion.getNombre();
			nombreRepresentante = organizacion.getPersona().getNombre();
		} else {
			ubicacionProponente = titular.getUbicacionesGeografica();
			nombreEmpresaPromotor = titular.getNombre();
			nombreRepresentante = nombreEmpresaPromotor;
		}

		if (ubicacionProponente.getUbicacionesGeografica() != null) {
			cantonUsuario = ubicacionProponente.getUbicacionesGeografica()
					.getNombre();
			if (ubicacionProponente.getUbicacionesGeografica()
					.getUbicacionesGeografica() != null) {
				provinciaUsuario = ubicacionProponente
						.getUbicacionesGeografica().getUbicacionesGeografica()
						.getNombre();

			}
		}

		String direccion = NADA;
		String telefono = NADA;
		String email = NADA;
		for (Contacto contacto : contactos) {
			if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.DIRECCION)) {
				// Dirección
				direccion = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.TELEFONO)) {
				// Teléfono
				telefono = contacto.getValor();
			} else if (contacto.getFormasContacto().getId()
					.equals(FormasContacto.EMAIL)) {
				email = contacto.getValor();
			}
		}

		ProyectoLicenciaCuaCiuu actividadPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoActivo);
		
		String sector = actividadPrincipal.getCatalogoCIUU().getTipoSector().getNombre();
		String codigoCM = "";		
		List<ProyectoLicenciaAmbientalConcesionesMineras> concesionesMinerasRcoa = proyectoConcesionesMinerasFacade.cargarConcesiones(proyectoActivo);
		if (concesionesMinerasRcoa.size()>1) {
			int cont = 0;
			int cantTotal = concesionesMinerasRcoa.size();
			for (ProyectoLicenciaAmbientalConcesionesMineras c: concesionesMinerasRcoa) {
				if (codigoCM.isEmpty()) {
					codigoCM = "las Concesiones Minera " + c.getCodigo();
				} else if (cont == cantTotal - 1) {
					codigoCM += " y " + c.getCodigo();
				} else {
					codigoCM += ", " + c.getCodigo();
				}
				cont++;
			}
		} else {
			codigoCM = "la Concesi&oacute;n Minera " + concesionesMinerasRcoa.get(0).getCodigo();
		}
		

		String[] parametrosO = new String[40];
		Integer i = 0;
		// ---------------------------------------------------------------------
		// No de la resolución
		parametrosO[i++] = numeroResolucion;
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo.getNombreProyecto()) + " " + proyectoActivo.getCodigoUnicoAmbiental();
		parametrosO[i++] = ubicacion;
		parametrosO[i++] = nombreRepresentante;
		parametrosO[i++] = numeroResolucion;
		parametrosO[i++] = proyectoActivo.getNombreProyecto() + " "	+ proyectoActivo.getCodigoUnicoAmbiental();
		parametrosO[i++] = ubicacion + ".";
		parametrosO[i++] = titular.getNombre();
		parametrosO[i++] = tipoMaterial;
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo.getNombreProyecto()) + " " + proyectoActivo.getCodigoUnicoAmbiental();
		parametrosO[i++] = ubicacion;
		parametrosO[i++] = nombreEmpresaPromotor;
		parametrosO[i++] = numeroLicencia;
		parametrosO[i++] = actividadPrincipal.getCatalogoCIUU().getNombre();
		parametrosO[i++] = sector;
		parametrosO[i++] = ubicacionCompleta;
		parametrosO[i++] = nombreRepresentante;
		parametrosO[i++] = direccion;
		parametrosO[i++] = telefono;
		parametrosO[i++] = email;
		parametrosO[i++] = proyectoActivo.getCodigoUnicoAmbiental();
		parametrosO[i++] = nombreEmpresaPromotor;		
		parametrosO[i++] = codigoCM;
		Boolean genera = proyectoActivo.getGeneraDesechos() != null	&& proyectoActivo.getGeneraDesechos() == true;
		String dias = genera ? "30" : "60";
		parametrosO[i++] = dias;
		parametrosO[i++] = getDateFormat(new Date());
		parametrosO[39]=proyectoActivo.getCodigoUnicoAmbiental();
		return parametrosO;
	}
	
	@EJB
    private DocumentosCoaFacade documentosRcoaFacade;
		
	public void ingresarDocumentoCoa(File file, Integer id,
			ProyectoLicenciaCoa proyecto, long idProceso, long idTarea,
			TipoDocumentoSistema tipoDocumento, String nombreDocumento)
			throws Exception {
		
		
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		DocumentosCOA documentoFicha = new DocumentosCOA(); 
		documentoFicha.setContenidoDocumento(data);
		documentoFicha.setNombreDocumento(nombreDocumento + ".pdf");
		documentoFicha.setExtencionDocumento(mimeTypesMap.getContentType(file));		
		documentoFicha.setTipo(mimeTypesMap.getContentType(file));
		documentoFicha.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
		documentoFicha.setIdTabla(proyecto.getId());
		documentoFicha.setProyectoLicenciaCoa(proyecto);
		documentoFicha.setIdProceso(idProceso);
		
		documentoFicha = documentosRcoaFacade.guardarDocumentoAlfresco(
				proyecto.getCodigoUnicoAmbiental(),
				"LicenciaAmbientalCategoriaII", idProceso,
				documentoFicha, tipoDocumento);
	}
	
	public void ingresarDocumentoCoa(byte[] file,
			ProyectoLicenciaCoa proyecto, long idProceso,
			TipoDocumentoSistema tipoDocumento, String nombreDocumento)
			throws Exception {		
        byte[] data = file;
		DocumentosCOA documentoFicha = new DocumentosCOA(); 
		documentoFicha.setContenidoDocumento(data);
		documentoFicha.setNombreDocumento(nombreDocumento);
		documentoFicha.setExtencionDocumento(".pdf");		
		documentoFicha.setTipo("application/pdf");
		documentoFicha.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
		documentoFicha.setIdTabla(proyecto.getId());
		documentoFicha.setProyectoLicenciaCoa(proyecto);
		documentoFicha.setIdProceso(idProceso);
		
		documentoFicha = documentosRcoaFacade.guardarDocumentoAlfresco(
				proyecto.getCodigoUnicoAmbiental(),
				"LicenciaAmbientalCategoriaII", idProceso,
				documentoFicha, tipoDocumento);
	}

}
