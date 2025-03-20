package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProcesosDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProyectoAsociadoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;

@Stateless
public class ActualizacionAutorizacionesAdministrativasFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private ProcesosDigitalizacionFacade procesosDigitalizacionFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private ProyectoAsociadoDigitalizacionFacade proyectoAsociadoDigitalizacionFacade;
	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	
	
	/**********************************************************************************************************************
	 * 
	 * 		funciones con lazy
	 * 
	 * ******************************************************************************************************************/

	@SuppressWarnings("unchecked")
	public List<AutorizacionAdministrativaAmbiental> getProyectosDigitalizadosLazy(Integer inicio, Integer total, String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String areaTecnicoCalidad){
		String condicionSql = "";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.identificacionUsuario= '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.codigoProyecto) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.nombreProyecto) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(p.tipoSector.nombre) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql+= " and  lower(p.autorizacionAdministrativaAmbiental) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(!areaTecnicoCalidad.isEmpty()){
			condicionSql+= " and p.areaResponsableControl.id in ("+areaTecnicoCalidad+")" ;
		}
		String sqlPproyecto = "SELECT p "
				+ "FROM AutorizacionAdministrativaAmbiental p "
			    + " WHERE p.estado = true and p.finalizado = true " + condicionSql+" "
			    		+ " order by p.codigoProyecto";
		
		Query queryProyecto = crudServiceBean.getEntityManager().createQuery(sqlPproyecto).setFirstResult(inicio).setMaxResults(total);
		
		List<AutorizacionAdministrativaAmbiental> result = (List<AutorizacionAdministrativaAmbiental>) queryProyecto.getResultList();
		if(result.size()>0){
			return result;
		}
		return new ArrayList<AutorizacionAdministrativaAmbiental>();
	}
	
	public Integer contarRegistros(String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String areaTecnicoCalidad){
		String condicionSql = "";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.identificacionUsuario= '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.codigoProyecto) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.nombreProyecto) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(p.tipoSector.nombre) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql+= " and  lower(p.autorizacionAdministrativaAmbiental) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(!areaTecnicoCalidad.isEmpty()){
			condicionSql+= " and p.areaResponsableControl.id in ("+areaTecnicoCalidad+")" ;
		}
		String sqlProyectoSuia = "SELECT cast(count(p.id) as integer)  "
				+ "FROM AutorizacionAdministrativaAmbiental p "
			    + " WHERE p.estado = true  and p.finalizado = true  " + condicionSql;

		List<Object> result = (List<Object>)crudServiceBean.getEntityManager().createQuery(sqlProyectoSuia).getResultList();
		Integer total =0;
		total += (Integer) result.get(0);
		return total;
	}
	
}
