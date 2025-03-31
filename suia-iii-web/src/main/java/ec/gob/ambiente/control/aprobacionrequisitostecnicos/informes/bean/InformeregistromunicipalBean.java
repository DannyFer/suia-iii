package ec.gob.ambiente.control.aprobacionrequisitostecnicos.informes.bean;

/*import java.io.File;
 import java.io.FileOutputStream;
 import java.nio.file.Paths;

 import javax.annotation.PostConstruct;
 import javax.ejb.EJB;

 import lombok.Getter;
 import lombok.Setter;

 import org.apache.log4j.Logger;

 import com.google.common.io.Files;
 import com.thoughtworks.xstream.io.path.Path;

 import ec.gob.ambiente.suia.domain.Informeregistroprovincial;
 import ec.gob.ambiente.suia.domain.TipoDocumento;
 import ec.gob.ambiente.suia.dto.EntityInformeregistroprovincial;
 import ec.gob.ambiente.suia.reportesgad.facade.Informeregistroprovincialfacade;
 import ec.gob.ambiente.suia.utils.JsfUtil;
 import ec.gob.ambiente.suia.utils.UtilGenerarInforme;*/

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityInformeregistroprovincial;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@Stateless
public class InformeregistromunicipalBean {
	private final Logger LOG = Logger
			.getLogger(PlantillaReporte.class);
	@Getter
	@Setter
	private File informePdf;
	@Getter
	@Setter
	private byte[] archivoInforme;
	@Getter
	@Setter
	private String informePath;
	@Getter
	@Setter
	private String nombreReporte;
	@Getter
	@Setter
	private boolean verInforme;

	@EJB
	private InformeProvincialGADFacade informeProvincialGADFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;
	@EJB
	private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private PersonaFacade personaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private CategoriaIIFacade categoriaIIFacade;
	UbicacionesGeografica canton = new UbicacionesGeografica();
	UbicacionesGeografica provincias = new UbicacionesGeografica();
	UbicacionesGeografica parroquia = new UbicacionesGeografica();
	UbicacionesGeografica cantonpro = new UbicacionesGeografica();
	UbicacionesGeografica provinciaspro = new UbicacionesGeografica();
	UbicacionesGeografica parroquiapro = new UbicacionesGeografica();
	Organizacion org = new Organizacion();
	String proponente = "";
	String cedula = "";
	String direccion = "";
	//List<Contacto> contacto;
	
	String valordireccion = "";
	String valorcorreo = "";
	String valortelefono = "";
	String representanteLegal="";
    String nombreRepresentanteLegal="";
	//Contacto con = new Contacto();

	public String visualizarOficio(String codigo, String resolucion,
			String oficio, String licencia) {
		List<Contacto> contacto = new ArrayList<Contacto>();
		List<String> direccionRepresentanteLegal = new ArrayList<String>();
		List<String> telefonoRepresentanteLegal = new ArrayList<String>();
		List<String> emailRepresentanteLegal = new ArrayList<String>();
		
		String pathPdf = null;
		String html = null;
		
		String valordireccion = "";
		String valorcorreo = "";
		// String valortelefono = "";
		String telefono = "";
		String email = "";

		// String pathPdf = null;
		// String html = null;
		try {

			ProyectoLicenciaAmbientalFacade dr = new ProyectoLicenciaAmbientalFacade();
			this.proyectoLicenciamientoAmbiental = proyectoLicenciaAmbientalFacade

			.getProyectoPorCodigo(codigo.toString());

			parroquia = proyectoLicenciamientoAmbiental
					.getProyectoUbicacionesGeograficas().get(0)
					.getUbicacionesGeografica();
			canton = ubicacionGeograficaFacade.buscarPorId(parroquia
					.getUbicacionesGeografica().getId());
			provincias = ubicacionGeograficaFacade.buscarPorId(canton
					.getUbicacionesGeografica().getId());
			org = organizacionFacade.buscarPorPersona(this.proyectoLicenciamientoAmbiental
							.getUsuario().getPersona(), this.proyectoLicenciamientoAmbiental
							.getUsuario().getNombre());
			if (org == null) {
				proponente = this.proyectoLicenciamientoAmbiental.getUsuario()
						.getPersona().getNombre().toString();
				cedula = this.proyectoLicenciamientoAmbiental.getUsuario()
						.getPin().toString();
				cantonpro = ubicacionGeograficaFacade.buscarPorId(parroquia
						.getId());
				provinciaspro = ubicacionGeograficaFacade.buscarPorId(canton
						.getUbicacionesGeografica().getId());
				contacto = contactoFacade
						.buscarPorPersona(proyectoLicenciamientoAmbiental
								.getUsuario().getPersona());
				representanteLegal="";
				nombreRepresentanteLegal=this.proyectoLicenciamientoAmbiental.getUsuario().getPersona().getNombre().toString();
			} else {
				proponente = org.getNombre();
				cedula = org.getRuc();
				parroquiapro = ubicacionGeograficaFacade.buscarPorId(org
						.getIdUbicacionGeografica());
				cantonpro = ubicacionGeograficaFacade.buscarPorId(parroquia
						.getId());
				provinciaspro = ubicacionGeograficaFacade.buscarPorId(canton
						.getUbicacionesGeografica().getId());
				direccion = org.getPersona().getDireccion();
				contacto = contactoFacade.buscarPorOrganizacion(org);
				representanteLegal=org.getPersona().getNombre();
                nombreRepresentanteLegal=org.getPersona().getNombre();
			}
			for (Contacto cont : contacto) {
				if (cont.getFormasContacto().getId() == 2
						&& cont.getEstado().equals(true))
					direccionRepresentanteLegal.add(cont.getValor());
				if (cont.getFormasContacto().getId() == 6
						&& cont.getEstado().equals(true))
					telefonoRepresentanteLegal.add(cont.getValor());
				if (cont.getFormasContacto().getId() == 5
						&& cont.getEstado().equals(true))
					emailRepresentanteLegal.add(cont.getValor());
			}

			for (int i = 0; i < telefonoRepresentanteLegal.size(); i++) {
				if (i == 0)
					telefono = telefonoRepresentanteLegal.get(i);
				else
					telefono = telefono + ", "
							+ telefonoRepresentanteLegal.get(i);
			}
			for (int i = 0; i < direccionRepresentanteLegal.size(); i++) {
				if (i == 0)
					valordireccion = direccionRepresentanteLegal.get(i);
				else
					valordireccion = valordireccion + ", "
							+ direccionRepresentanteLegal.get(i);
			}
			for (int i = 0; i < emailRepresentanteLegal.size(); i++) {
				if (i == 0)
					email = emailRepresentanteLegal.get(i);
				else
					email = email + ", " + emailRepresentanteLegal.get(i);
			}
			verInforme = false;

			PlantillaReporte plantillaReporte = this.informeProvincialGADFacade
					.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_SINMI_MUNICIPAL_GAD
							.getIdTipoDocumento());
			EntityInformeregistroprovincial entityInforme = new EntityInformeregistroprovincial();
			entityInforme.setNumeroResolucion("verificarnumeroresolucion");
			entityInforme.setProvincia("provincia");
			entityInforme
					.setNombreproyecto(this.proyectoLicenciamientoAmbiental
							.getNombre().toString());
			entityInforme.setActividad(this.proyectoLicenciamientoAmbiental
					.getTipoSector().toString());
			entityInforme.setCodigoproy(this.proyectoLicenciamientoAmbiental
					.getCodigo().toString());
			entityInforme.setCantonproy(canton.getNombre().toString());
			entityInforme.setProvinciaproy(provincias.getNombre().toString());
			entityInforme.setParroquiaproy(parroquia.getNombre().toString());
			entityInforme.setCedulausu(cedula);
			entityInforme.setPromotor(proponente);
			entityInforme.setCantonpromotor(cantonpro.getNombre().toString());
			entityInforme.setProvinciapromotor(provincias.getNombre()
					.toString());
			entityInforme.setSector(this.proyectoLicenciamientoAmbiental
					.getCatalogoCategoria().getDescripcion().toString());
			entityInforme.setDireccionpromotor(valordireccion.toString());
			entityInforme.setTelefonopromotro(telefono);
			entityInforme.setEmailpromotor(email);
			entityInforme.setRegistroambiental(licencia);
			entityInforme.setCoordenadas(categoriaIIFacade
					.generarTablaCoordenadas(proyectoLicenciamientoAmbiental));
			SimpleDateFormat fecha = new SimpleDateFormat(
					"dd 'de' MMMM 'de' yyyy", new Locale("es"));
			entityInforme.setFechaactual(canton.getNombre().toString() + ", a "
					+ fecha.format(new java.util.Date()));
//			entityInforme.setFechaactual(canton.getNombre().toString() + ", a 11 de Junio de 2020");
			nombreReporte = "InformeTecnico.pdf";
			entityInforme.setNumeroResolucion(resolucion);
			entityInforme.setRegistroambiental(licencia);
			entityInforme.setProvincia(provincias.getNombre()
					.toString());
			entityInforme.setRepresentanteLegal(representanteLegal);
            entityInforme.setNombreRepresentanteLegal(nombreRepresentanteLegal);			
			html = UtilGenerarInforme.generarFicheroHtml(
					plantillaReporte.getHtmlPlantilla(), nombreReporte,
					Boolean.valueOf(true), entityInforme);

			setInformePdf(informePdf);
		} catch (Exception e) {
			this.LOG.error("Error al visualizar el informe técnico", e);
			JsfUtil.addMessageError("Error al visualizar el informe técnico");
		}
		return html;
	}

}

