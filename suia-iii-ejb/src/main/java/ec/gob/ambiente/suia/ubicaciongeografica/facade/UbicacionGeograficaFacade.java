package ec.gob.ambiente.suia.ubicaciongeografica.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Region;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.ubicaciongeografica.service.UbicacionGeograficaServiceBean;

/**
 * 
 * <b> Facade para servicios de Ubicación Geográfica. </b>
 * 
 * @author pganan
 * @version Revision: 1.0
 *          <p>
 *          [Autor: pganan, Fecha: 20/12/2014]
 *          </p>
 */
@Stateless
public class UbicacionGeograficaFacade {

	@EJB
	private UbicacionGeograficaServiceBean ubicacionGeograficaServiceBean;

	/**
	 * <b> Lista las ubicaciones geográficas por el id del Padre. </b>
	 * 
	 * @author pganan
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: pganan, Fecha: 20/12/2014]
	 *          </p>
	 * @param id
	 *            - Padre
	 * @return Lista de Ubicaciones Geográficas
	 */
	public List<UbicacionesGeografica> buscarUbicacionGeografica(Integer id) {
		List<UbicacionesGeografica> ubicaciones = ubicacionGeograficaServiceBean.buscarUbicacionGeograficaPorId(id);
		return ubicaciones;
	}

	//
	// public UbicacionesGeografica ubicacionGeografica(Integer id) {
	// UbicacionesGeografica ubicacion = ubicacionGeograficaServiceBean
	// .ubicacionGeograficaPorId(id);
	// return ubicacion;
	// }
	/**
	 * <b> Obtiene la ubicación geográfica por el nombre. </b>
	 * 
	 * @author pganan
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: pganan, Fecha: 20/12/2014]
	 *          </p>
	 * @param nombre
	 * @return Ubicación Geográfica
	 */
	public UbicacionesGeografica buscarUbicacionGeograficaPorNombre(String nombre) {
		UbicacionesGeografica ubicacionesGeografica = ubicacionGeograficaServiceBean
				.buscarUbicacionGeograficaPorNombre(nombre);
		return ubicacionesGeografica;
	}

	/**
	 * <b> Lista los paises registrados en ubicación geográfica. </b>
	 * 
	 * @author pganan
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: pganan, Fecha: 20/12/2014]
	 *          </p>
	 * @return Lista de Paises
	 */
	public List<UbicacionesGeografica> listarPais() {
		List<UbicacionesGeografica> paises = ubicacionGeograficaServiceBean.listarPais();
		return paises;
	}

	public List<UbicacionesGeografica> getProvincias() {
		return ubicacionGeograficaServiceBean.buscarUbicacionGeograficaPorId(1);
	}

	public List<UbicacionesGeografica> getCantonesParroquia(UbicacionesGeografica ubicacion) {
		List<UbicacionesGeografica> listParroquias= new ArrayList<UbicacionesGeografica>();
		for (UbicacionesGeografica ubicacionesGeografica : ubicacionGeograficaServiceBean.buscarUbicacionGeograficaPorId(ubicacion.getId())) {
			ubicacionesGeografica.setOrden(ubicacion.getOrden());
			listParroquias.add(ubicacionesGeografica);
		}
		return listParroquias;
	}
	
	public List<UbicacionesGeografica> getUbicacionPorPadre(UbicacionesGeografica ubicacion) {

		return ubicacionGeograficaServiceBean.buscarUbicacionGeograficaPorPadre(ubicacion);
	}
	
	public List<UbicacionesGeografica> getUbicacionPadre(UbicacionesGeografica ubicacion) {
		List<UbicacionesGeografica>listaParroquiasXPadre=new ArrayList<UbicacionesGeografica>();
		for (UbicacionesGeografica ubicacionesGeografica : ubicacionGeograficaServiceBean.buscarUbicacionGeograficaPadre(ubicacion)) {
			ubicacionesGeografica.setOrden(ubicacion.getOrden());
			listaParroquiasXPadre.add(ubicacionesGeografica);
		}
		
		return listaParroquiasXPadre;
	}

	public List<UbicacionesGeografica> listarProvincia() throws ServiceException {
		return ubicacionGeograficaServiceBean.listarProvincia();
	}

	public List<UbicacionesGeografica> listarPorPadre(UbicacionesGeografica padre) throws ServiceException {
		return ubicacionGeograficaServiceBean.listarPorPadre(padre);
	}

	public UbicacionesGeografica buscarPorId(Integer id) throws ServiceException {
		return ubicacionGeograficaServiceBean.buscarPorId(id);
	}

	public List<UbicacionesGeografica> listarPorProyecto(final ProyectoLicenciamientoAmbiental proyecto) {
		return ubicacionGeograficaServiceBean.listarPorProyecto(proyecto,false);
	}
	
	public List<UbicacionesGeografica> listarPorProyectoConConcesionesMineras(final ProyectoLicenciamientoAmbiental proyecto) {
		return ubicacionGeograficaServiceBean.listarPorProyecto(proyecto,true);
	}

	public List<Region> listaRegionesEcuador() {
		return ubicacionGeograficaServiceBean.listaRegionesEcuador();
	}

	public UbicacionesGeografica ubicacionCompleta(Integer idParroquia) throws ServiceException {
		UbicacionesGeografica ugParroquia = buscarPorId(idParroquia);
		UbicacionesGeografica ugCanton = buscarPorId(ugParroquia.getUbicacionesGeografica().getId());
		UbicacionesGeografica ugProvincia = buscarPorId(ugCanton.getUbicacionesGeografica().getId());
		ugParroquia.setUbicacionesGeografica(ugCanton);
		ugCanton.setUbicacionesGeografica(ugProvincia);
		return ugParroquia;
	}

	public List<UbicacionesGeografica> getProvinciasAguasTerritoriales() {
		return ubicacionGeograficaServiceBean.getProvinciasAguasTerritoriales();
	}

	public UbicacionesGeografica buscarUbicacionPorCodigoInec(String codigoInec) {
		return ubicacionGeograficaServiceBean.buscarUbicacionPorCodigoInec(codigoInec);
	}
	
	public UbicacionesGeografica nivelNAcional(Integer id){
		return ubicacionGeograficaServiceBean.nivelNAcional(id);
	}
	
	public List<UbicacionesGeografica> getListaPaises(){
		return ubicacionGeograficaServiceBean.getListaPaises();
	}
	
	public List<UbicacionesGeografica> buscarUbicacionGeograficaPorParroquia(String inecParroquia){
		return ubicacionGeograficaServiceBean.buscarUbicacionGeograficaPorParroquia(inecParroquia);
	}
	
}
