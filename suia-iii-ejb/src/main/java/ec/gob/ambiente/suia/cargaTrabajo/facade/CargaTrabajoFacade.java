package ec.gob.ambiente.suia.cargaTrabajo.facade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CargaTrabajo;
import ec.gob.ambiente.suia.domain.CargaTrabajoRevision;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.service.UsuarioServiceBean;
import ec.gob.ambiente.suia.cargaTrabajo.service.CargaTrabajoServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * *
 * <p/>
 * <b> Facade para la entidad CargaTrabajo</b>
 * 
 * @author bburbano
 * @version Revision: 1.0
 * fecha 2019-04-01
 */
@LocalBean
@Stateless
public class CargaTrabajoFacade {

	@EJB
	private UsuarioServiceBean usuarioServiceBean;

	@EJB
	private CargaTrabajoServiceBean cargaTrabajoServiceBean;
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();

	public void guardar(CargaTrabajo cargaTrabajo) throws ServiceException {
		crudServiceBean.saveOrUpdate(cargaTrabajo);
	}

	public void guardarRevision(CargaTrabajoRevision cargaTrabajoRevision) throws ServiceException {
		crudServiceBean.saveOrUpdate(cargaTrabajoRevision);
	}

	public List<CargaTrabajo> listarCargarTrabajo(Usuario usuario) throws ServiceException {
		return cargaTrabajoServiceBean.listarCargarTrabajo(usuario);
	}

	public List<CargaTrabajo> listarCargarTrabajoTodos() throws ServiceException {
		return cargaTrabajoServiceBean.listarCargarTrabajoTodos();
	}
	
	public List<CargaTrabajoRevision> listarCargarTrabajoRevisiones(Integer cargaTrabajoId) throws ServiceException{
		return cargaTrabajoServiceBean.listarCargarTrabajoRevisiones(cargaTrabajoId);
	}
	
	public List<CargaTrabajoRevision> listarCargarTrabajoRevisionesAtrazadas(Usuario usuario) throws ServiceException{
		return cargaTrabajoServiceBean.listarCargarTrabajoRevisionesAtrazadas(usuario);
	}
	
	public List<CargaTrabajo> listarCargarTrabajoObligacionesAtrazadas(Usuario usuario)throws ServiceException{
		return cargaTrabajoServiceBean.listarCargarTrabajoObligacionesAtrazadas(usuario);
	}
	
	public List<CargaTrabajo> consultarExisteCargaTrabajo(String codigo, Integer id) throws ServiceException {
		return cargaTrabajoServiceBean.consultarExisteCargaTrabajo(codigo, id);
	}

	public void guardarCodigotramite(Integer cargaTrabajoId){
		cargaTrabajoServiceBean.guardarCodigotramite(cargaTrabajoId);
	}
	
	public void guardarCargaTrabajohistorico(Integer cargaTrabajoId) throws ServiceException {
		cargaTrabajoServiceBean.guardarCargaTrabajohistorico(cargaTrabajoId);
	}
	
	public void guardarCargaTrabajohistoricoRevision(Integer cargaTrabajoId) throws ServiceException {
		cargaTrabajoServiceBean.guardarCargaTrabajohistoricoRevision(cargaTrabajoId);
	}

	public ProyectoLicenciamientoAmbiental obtenerProyecto4CategoriasPorCodigo(String codigo) throws ServiceException {
		ProyectoLicenciamientoAmbiental objProyecto = new ProyectoLicenciamientoAmbiental();
		String sqlPproyecto="select id, nombre, provincia, fecha, categoria from dblink('"+dblinkSuiaVerde+"','"+
				"with tarea as ( "+
						" SELECT max(end_) fecha , ''"+codigo+"'' codigo  FROM jbpm4_hist_task "+
						" where   execution_ like ''%"+codigo+"'' " +  
						" ) "+
						" select p.id, p.nombre, pro.provincia_inec, t.fecha, ca.cata_sector from proyectolicenciaambiental p inner join ubicacion u on p.id=u.proyecto_id inner join parroquia pr on u.parroquia = pr.id inner join canton c on pr.canton = c.id inner join provincia pro on c.provincia = pro.id inner join catalogo_categoria ca on p.id_catalogo = ca.id_catalogo, tarea t where p.id=''"+codigo+"''') as t1 (id character varying(255), nombre character varying, provincia character varying, fecha timestamp, categoria character varying)";		
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		List<Object>  resultPro = new ArrayList<Object>();
		resultPro= queryProyecto.getResultList();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        
		if (resultPro!=null) {
			for(Object a: resultPro)
			{
				objProyecto.setCodigo(codigo);
				objProyecto.setNombre(((Object[]) a)[1].toString());
				objProyecto.setDireccionProyecto(((Object[]) a)[2].toString());//almaceno el codigo inec de la provincia
				try{
					if (((Object[]) a)[3] != null ){
						Date date = formatter.parse(((Object[]) a)[3].toString());
						objProyecto.setFechaFin(date);
					}
				}catch(Exception e){
					
				}
				objProyecto.setCodigoMinero(((Object[]) a)[4].toString());//almaceno el sector al que pertenece en el acmpo codigo minero para validar si es hidrocarburos
			}
		}
		return objProyecto;
	}
}