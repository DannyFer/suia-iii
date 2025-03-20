package ec.gob.ambiente.rcoa.facade;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;

@Stateless
public class CertificadoAmbientalFacade {

	@EJB
	SecuenciaCertificadoAmbientalRcoaFacade secuenciaCertificadoAmbientalRcoa;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	private static final Logger LOG = Logger.getLogger(CertificadoAmbientalFacade.class);
	
	@EJB
	UsuarioFacade usuarioFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
		
	@EJB
	private ContactoFacade contactoFacade;
	
	private static final String NADA = "";
	
	
	private String getDateFormat(Date date) {
		SimpleDateFormat formateador = new SimpleDateFormat(
				"dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
		return formateador.format(date);
	}
	public String[] cargarDatosCertificadoAmbientalCompleto(
			ProyectoLicenciaCoa proyectoActivo, String userName)
			throws Exception {
		
		// No. Oficio
		// *************************************************************************

		String canton = "";
		String provincia = "";
		String ubicacion = "";
		String ubicacionCompleta = "";
		String cantonUsuario = "";
		String provinciaUsuario = "";

		String nombreEmpresaPromotor = "";
		String nombreRepresentante = "";

		try {

			List<UbicacionesGeografica> ubicaciones = proyectoLicenciaCoaUbicacionFacade.ubicacionesGeograficas(proyectoActivo)	;				
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
				.buscarUsuarioNativeQuery(usuario.getNombre());

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
		
		String sectorActividad = proyectoLicenciaCuaCiuuFacade.actividadproyecto(proyectoActivo).getCatalogoCIUU().getNombre();
		
		
		String[] parametrosO = new String[40];
		Integer i = 0;
		// ---------------------------------------------------------------------

		// Art. 2.
		// NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo.getNombreProyecto());
		// ubicación
		parametrosO[i++] = ubicacion;

		// REGISTRO AMBIENTAL PARA EL PROYECTO OBRA O ACTIVIDAD
		// NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD
		parametrosO[i++] = StringEscapeUtils.escapeHtml(proyectoActivo.getNombreProyecto());
		// ubicación
		parametrosO[i++] = ubicacion;

		// El Ministerio del Ambient INCLUIR NOMBRE DE LA EMPRESA O PROMOTOR QUE
		// LO TOMA DEL REGISTRO-----
		parametrosO[i++] = nombreEmpresaPromotor;

		// NOMBRE DE LA EMPRESA/PROMOTOR (TOMADO DEL REGISTRO USUARIO)
		parametrosO[i++] = nombreEmpresaPromotor;
		
		// DATOS TÉCNICOS
		// Descripción de la actividad TOMADO DEL REGISTRO
		parametrosO[i++] =sectorActividad;


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

		// fecha
		parametrosO[i++] = getDateFormat(new Date());


		return parametrosO;
	}
}
