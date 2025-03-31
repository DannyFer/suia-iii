package ec.gob.ambiente.suia.validacionseccion.facade;

import ec.gob.ambiente.suia.domain.ValidacionSecciones;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.validacionseccion.service.ValidacionSeccionesServiceBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

/**
 * *
 * <p/>
 * <b> Facade para la entidad Usuario</b>
 * 
 * @author Frank Torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Frank Torres, Fecha: 21/05/2015]
 *          </p>
 */
@Stateless
public class ValidacionSeccionesFacade {

	@EJB
	private ValidacionSeccionesServiceBean validacionSeccionesServiceBean;

	/**
	 * Recuperar la lista de variables.
	 * 
	 * @return
	 */
	public List<ValidacionSecciones> listarValidacionSeccioneses() {
		return validacionSeccionesServiceBean.listarValidacionSecciones();
	}

	/**
	 * <b> Buscar variable por nombre. </b>
	 * 
	 * @param nombre
	 * @return List<ValidacionSecciones>
	 * @author frank
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: frank torres, Fecha: 21/05/2015]
	 *          </p>
	 */
	public List<ValidacionSecciones> buscarValidacionSecciones(String nombre, String idClase) throws ServiceException {
		return validacionSeccionesServiceBean.buscarValidacionesSecciones(nombre, idClase);
	}

	/**
	 * <b> Buscar variable por nombre. </b>
	 * 
	 * @param nombre
	 * @return ValidacionSecciones
	 * @author frank
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: frank torres, Fecha: 27/06/2015]
	 *          </p>
	 */
	public ValidacionSecciones buscarValidacionSeccionesNombreSeccion(String nombre, String seccion, String idClase)
			throws ServiceException {
		return validacionSeccionesServiceBean.buscarValidacionSeccionesNombreSeccion(nombre, seccion, idClase);
	}

	/**
	 * <b> Buscar variable por nombre. </b>
	 * 
	 * @param nombre
	 *            nombre de la clase a validar
	 * @param seccion
	 *            seccion que se desea validar
	 * @return ValidacionSecciones
	 * @author frank
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: frank torres, Fecha: 27/06/2015]
	 *          </p>
	 */

	public Boolean existeValidacionSeccionesNombreSeccion(String nombre, String seccion, String idClase) {
		return validacionSeccionesServiceBean.existeValidacionSeccionesNombreSeccion(nombre, seccion, idClase);
	}

	public void guardar(ValidacionSecciones validacionSecciones) throws ServiceException {
		validacionSeccionesServiceBean.guardarValidacionSecciones(validacionSecciones);
	}

	/**
	 * Guarda el estado de una validaciÃ³n de secciÃ³n.
	 * 
	 * @param nombreClase
	 * @param nombreSeccion
	 * @param estado
	 * @throws ServiceException
	 */
	public void guardarValidacionSeccion(String nombreClase, String nombreSeccion, String idClase, Boolean estado)
			throws ServiceException {

		if (existeValidacionSeccionesNombreSeccion(nombreClase, nombreSeccion, idClase)) {
			ValidacionSecciones validacionSecciones = validacionSeccionesServiceBean
					.buscarValidacionSeccionesNombreSeccion(nombreClase, nombreSeccion, idClase);

			if (validacionSecciones.getEstado() != estado) {
				validacionSecciones.setEstado(estado);
				validacionSeccionesServiceBean.guardarValidacionSecciones(validacionSecciones);
			}

		} else {
			ValidacionSecciones validacionSecciones = new ValidacionSecciones(nombreClase, nombreSeccion, idClase,
					estado);
			validacionSeccionesServiceBean.guardarValidacionSecciones(validacionSecciones);
		}
	}

	/**
	 * Guarda una validaciÃ³n de secciÃ³n en estado true.
	 * 
	 * @param nombreClase
	 * @param nombreSeccion
	 * @throws ServiceException
	 */
	public void guardarValidacionSeccion(String nombreClase, String nombreSeccion, String idClase)
			throws ServiceException {
		guardarValidacionSeccion(nombreClase, nombreSeccion, idClase, true);

	}

	/**
	 * <b> Buscar variable por nombre. </b>
	 * 
	 * @param nombreClase
	 * @return List<String>
	 * @author frank
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: frank torres, Fecha: 21/05/2015]
	 *          </p>
	 */
	public List<String> listaSeccionesPorClase(String nombreClase, String idClase) {
		List<String> secciones = new ArrayList<String>();
		try {
			List<ValidacionSecciones> validaciones = validacionSeccionesServiceBean.buscarValidacionesSecciones(
					nombreClase, idClase);

			for (ValidacionSecciones validacion : validaciones) {
				secciones.add(validacion.getValorSeccion());
			}
		} catch (Exception e) {

		}

		return secciones;
	}

}
