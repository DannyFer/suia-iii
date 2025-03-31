package ec.gob.ambiente.suia.coordenadageneral.facade;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.coordenadageneral.service.CoordenadaGeneralService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.crud.service.Generico;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.FormaProyecto;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class CoordenadaGeneralFacade extends Generico<CoordenadaGeneral> {
	@EJB
	private CoordenadaGeneralService coordenadaGeneralService;
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public CoordenadaGeneralFacade() {
		super(CoordenadaGeneral.class);
	}

	/**
	 * 
	 * <b> Método para guardar una entidad de tipo coordenada general. </b>
	 * 
	 * @author pganan
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: pganan, Fecha: 03/02/2015]
	 *          </p>
	 * @param coordenadaGeneral
	 */
	public void guardar(CoordenadaGeneral coordenadaGeneral) {
		coordenadaGeneralService.guardar(coordenadaGeneral);
	}

	/**
	 * 
	 * <b> Método para guardar una lista de coordenada general. </b>
	 * 
	 * @author pganan
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: pganan, Fecha: 03/02/2015]
	 *          </p>
	 * @param listaCoordenadaGeneral
	 */
	public void saveOrUpdate(List<CoordenadaGeneral> listaCoordenadaGeneral) {
		coordenadaGeneralService.saveOrUpdate(listaCoordenadaGeneral);
	}

	public List<CoordenadaGeneral> coordenadasGeneralXTablaId(Integer idTabla,
			String nombreTabla) {
		return coordenadaGeneralService.coordenadasGeneralXTablaId(idTabla,
				nombreTabla);
	}

	public void modificarCoordenada(CoordenadaGeneral coordenadaGeneral) {
		coordenadaGeneralService.modificar(coordenadaGeneral);
	}
	
	public void eliminar(CoordenadaGeneral coordenadaGeneral) {
		coordenadaGeneralService.modificar(coordenadaGeneral);
	}

	public CoordenadaGeneral convertirCoordenadaCordenadaGeneral(
			Coordenada coordenada, Integer idTable, String nombreTabla,
			TipoForma tipoForma) {
		CoordenadaGeneral coordenadaGeneral = new CoordenadaGeneral();

		if (coordenada.getDescripcion() != null)
			coordenadaGeneral.setDescripcion(coordenada.getDescripcion());

		coordenadaGeneral.setIdTable(idTable);
		coordenadaGeneral.setNombreTabla(nombreTabla);
		if (coordenada.getOrden() != null)
			coordenadaGeneral.setOrden(coordenada.getOrden());

		coordenadaGeneral.setTipoForma(tipoForma);
		if (coordenada.getX() != null)
			coordenadaGeneral.setX(BigDecimal.valueOf(coordenada.getX()));
		if (coordenada.getY() != null)
			coordenadaGeneral.setY(BigDecimal.valueOf(coordenada.getY()));

		return coordenadaGeneral;
	}

	public Coordenada convertirCoordenadaGeneralCordenada(
			CoordenadaGeneral coordenadaGeneral,
			ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental) {
		Coordenada coordenada = new Coordenada();

		if (coordenadaGeneral.getDescripcion() != null)
			coordenada.setDescripcion(coordenadaGeneral.getDescripcion());

		if (coordenadaGeneral.getOrden() != null)
			coordenada.setOrden(coordenadaGeneral.getOrden());
		FormaProyecto formasProyecto = new FormaProyecto();
		formasProyecto
				.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
		formasProyecto.setTipoForma(coordenadaGeneral.getTipoForma());
		coordenada.setFormasProyecto(formasProyecto);
		// (coordenadaGeneral.getTipoForma());
		if (coordenadaGeneral.getX() != null)
			coordenada.setX(Double.valueOf(coordenadaGeneral.getX().intValue()));
		if (coordenadaGeneral.getY() != null)
			coordenada.setY(Double.valueOf(coordenadaGeneral.getY().intValue()));

		return coordenada;
	}
	
	/**
	 * Cristina Flores coordenadas historico
	 */
	public List<CoordenadaGeneral> buscarCoordenadasHistorico(Integer idCoordenada){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT c FROM CoordenadaGeneral c WHERE c.idHistorico = :idCoordenada order by 1 desc");
			query.setParameter("idCoordenada", idCoordenada);

			return (List<CoordenadaGeneral>) query.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	public List<CoordenadaGeneral> buscarCoordenadasPorActividad(Integer idActividad){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT c FROM CoordenadaGeneral c WHERE c.actividadLicenciamiento.id = :idActividad and c.idHistorico = null order by 1");
			query.setParameter("idActividad", idActividad);

			return (List<CoordenadaGeneral>) query.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	public List<CoordenadaGeneral> buscarCoordenadasPorActividadEnBdd(Integer idActividad){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT c FROM CoordenadaGeneral c WHERE c.actividadLicenciamiento.id = :idActividad order by 1");
			query.setParameter("idActividad", idActividad);

			return (List<CoordenadaGeneral>) query.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	public List<CoordenadaGeneral> buscarCoordenadasModificadasPorActividad(Integer idActividad, Integer numeroNotificacion){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT c FROM CoordenadaGeneral c WHERE c.actividadLicenciamiento.id = :idActividad "
							+ "and c.numeroNotificacion = :numeroNotificacion order by id, idHistorico desc");
			query.setParameter("idActividad", idActividad);
			query.setParameter("numeroNotificacion", numeroNotificacion);

			return (List<CoordenadaGeneral>) query.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	public List<CoordenadaGeneral> coordenadaGeneralConHistorico(Integer idTabla,
			String nombreTabla) {
		return coordenadaGeneralService.coordenadaGeneralConHistorico(idTabla,
				nombreTabla);
	}

	/**
	 * Cris F: consulta para obtener todos los historiales de las coordenadas
	 */
	public List<CoordenadaGeneral> buscarCoordenadasHistoricasPorActividad(Integer idActividad){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT c FROM CoordenadaGeneral c WHERE c.actividadLicenciamiento.id = :idActividad order by id, idHistorico desc");
			query.setParameter("idActividad", idActividad);

			return (List<CoordenadaGeneral>) query.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	/**
	 * Busca coordenadas existentes en bdd por tabla 
	 * @param nombreTabla
	 * @param idTabla
	 * @return
	 * @throws ServiceException
	 */
	public List<CoordenadaGeneral> listarCoordenadaGeneralEnBdd(String nombreTabla,
			Integer idTabla) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nombreTabla", nombreTabla);
		params.put("idTabla", idTabla);
		return (List<CoordenadaGeneral>) crudServiceBean.findByNamedQuery(
				CoordenadaGeneral.LISTAR_TODOS_POR_ID_NOMBRE_TABLA, params);
	}
}
