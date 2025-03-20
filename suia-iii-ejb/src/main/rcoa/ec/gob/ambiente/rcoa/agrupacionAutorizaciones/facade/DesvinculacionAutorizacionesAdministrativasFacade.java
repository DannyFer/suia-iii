package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.jbpm.process.audit.ProcessInstanceLog;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProcesosDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProyectoAsociadoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.ProyectoAsociadoDigitalizacion;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class DesvinculacionAutorizacionesAdministrativasFacade {

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
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	public String dblinkSuiaHidro=Constantes.getDblinkBpmsHyD();
	
	public String dblinkSectorSubsector=Constantes.getDblinkSectorSubsector();
	
	public String dblinkSuiaBpm=Constantes.getDblinkBpmsSuiaiii();
	
	
	/**********************************************************************************************************************
	 * 
	 * 		funciones con lazy
	 * 
	 * ******************************************************************************************************************/

	@SuppressWarnings("unchecked")
	public List<AutorizacionAdministrativa> getProyectosDigitalizadosLazy(Integer inicio, Integer total, String documentoOperador, String codigo, String resolucion, String fechaRegistro, String nombreProyecto, String sector, String tipoProyecto, String areaTecnicoCalidad){
		String condicionSql = "", condicionSqlAsociado="", sqlProyectoAsociados="";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.enaa_ci_user= '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.enaa_project_code) like '%"+codigo.toLowerCase()+"%'" ;
			condicionSqlAsociado+= " and  lower(x.enaa_project_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(resolucion!= null && !resolucion.isEmpty()){
			condicionSql+= " and  lower(p.enaa_resolution) like '%"+resolucion.toLowerCase()+"%'" ;
			condicionSqlAsociado+= " and  lower(x.enaa_resolution) like '%"+resolucion.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(p.enaa_resolution_date as varchar) like '%"+fechaRegistro.toLowerCase()+"%'" ;
			condicionSqlAsociado+= " and  cast(x.enaa_resolution_date as varchar) like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.enaa_project_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
			condicionSqlAsociado+= " and  lower(x.enaa_project_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(c.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql+= " and  lower(p.enaa_environmental_administrative_authorization) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(!areaTecnicoCalidad.isEmpty()){
			condicionSql+= " and p.area_id_ercs in ("+areaTecnicoCalidad+")" ;
		}
		String sqlPproyecto = "SELECT distinct p.enaa_id, p.enaa_project_code, p.enaa_project_name, p.enaa_ci_user, p.enaa_name_user, "
				+ " p.enaa_environmental_administrative_authorization, p.enaa_register_date, p.enaa_register_finalized_date, p.enaa_project_summary, p.enaa_resolution, p.enaa_resolution_date, p.enaa_status_finalized, p.enaa_system, c.sety_name "
				+ "FROM coa_digitalization_linkage.associated_projects a inner join coa_digitalization_linkage.environmental_administrative_authorizations p on a.enaa_id = p.enaa_id"
				+ "	inner join suia_iii.sector_types c on p.sety_id = c.sety_id "
			    + " WHERE p.enaa_status = true  " + condicionSql;

		if(!condicionSqlAsociado.isEmpty()){
			if(documentoOperador!= null && !documentoOperador.isEmpty()){
				condicionSqlAsociado = " and  x.enaa_ci_user= '"+documentoOperador+"'" + condicionSqlAsociado;
			}
			if(!areaTecnicoCalidad.isEmpty()){
				condicionSqlAsociado+= " and p.area_id_ercs in ("+areaTecnicoCalidad+")" ;
			}
			sqlProyectoAsociados = " union "
					+ "SELECT distinct p.enaa_id, p.enaa_project_code, p.enaa_project_name, p.enaa_ci_user, p.enaa_name_user, "
					+ " p.enaa_environmental_administrative_authorization, p.enaa_register_date, p.enaa_register_finalized_date, p.enaa_project_summary, p.enaa_resolution, p.enaa_resolution_date, p.enaa_status_finalized, p.enaa_system, c.sety_name "
					+ "FROM coa_digitalization_linkage.associated_projects a inner join coa_digitalization_linkage.environmental_administrative_authorizations p on a.enaa_id = p.enaa_id "
					+ " inner join coa_digitalization_linkage.environmental_administrative_authorizations x on a.aspr_code_id = x.enaa_id "
					+ "	inner join suia_iii.sector_types c on p.sety_id = c.sety_id "
				    + " WHERE p.enaa_status = true  " + condicionSqlAsociado;
		}
		String sql = "select distinct * "
				+ " from ( "+sqlPproyecto + sqlProyectoAsociados +") as totalregistros "
						+ "order by enaa_project_code";
		
		Query queryProyecto = crudServiceBean.getEntityManager().createNativeQuery(sql).setFirstResult(inicio).setMaxResults(total);
		
		List<Object> result = (List<Object>) queryProyecto.getResultList();
		if(result.size()>0){
			List<AutorizacionAdministrativa> projects = new ArrayList<AutorizacionAdministrativa>();
			for (Object object : result) {
				Object[] array = (Object[]) object;
				AutorizacionAdministrativa aaa = new AutorizacionAdministrativa();
				aaa.setIdDigitalizacion((Integer) array[0]);
				aaa.setId((Integer) array[0]);
				aaa.setCodigo((String) array[1]);
				aaa.setNombre((String) array[2]);
				aaa.setCedulaProponente((String) array[3]);
				aaa.setNombreProponente((String) array[4]);
				aaa.setCategoria((String) array[5]);
				aaa.setResolucion((String) array[9]);

				aaa.setFechaResolucion(array[10].toString());
				aaa.setSector((String) array[13]);
				aaa.setEstado("Completado");
				
				// cargo la lista de proyectos asociados cuando se lista los proyectos
				if(aaa.getIdDigitalizacion() != null ){
					List<ProyectoAsociadoDigitalizacion> listaProyectosAsociados = buscarProyectosAsociados(aaa);
					aaa.setListaProyectosAsociadosVer(listaProyectosAsociados);
				}
				
				projects.add(aaa);
			}
			return projects;
		}
		return new ArrayList<AutorizacionAdministrativa>();
	}
	
	public Integer contarRegistros(String documentoOperador, String codigo, String resolucion, String fechaRegistro, String nombreProyecto, String sector, String tipoProyecto, String areaTecnicoCalidad){
		String condicionSql = "", condicionSqlAsociado="", sqlProyectoAsociados="";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.enaa_ci_user= '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.enaa_project_code) like '%"+codigo.toLowerCase()+"%'" ;
			condicionSqlAsociado+= " and  lower(x.enaa_project_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(resolucion!= null && !resolucion.isEmpty()){
			condicionSql+= " and  lower(p.enaa_resolution) like '%"+resolucion.toLowerCase()+"%'" ;
			condicionSqlAsociado+= " and  lower(x.enaa_resolution) like '%"+resolucion.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(p.enaa_resolution_date as varchar) like '%"+fechaRegistro.toLowerCase()+"%'" ;
			condicionSqlAsociado+= " and  cast(x.enaa_resolution_date as varchar) like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.enaa_project_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
			condicionSqlAsociado+= " and  lower(x.enaa_project_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(c.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql+= " and  lower(p.enaa_environmental_administrative_authorization) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(!areaTecnicoCalidad.isEmpty()){
			condicionSql+= " and p.area_id_ercs in ("+areaTecnicoCalidad+")" ;
		}
		String sqlProyectoSuia = "SELECT a.enaa_id "
				+ "FROM coa_digitalization_linkage.associated_projects a inner join coa_digitalization_linkage.environmental_administrative_authorizations p on a.enaa_id = p.enaa_id "
				+ "	inner join suia_iii.sector_types c on p.sety_id = c.sety_id "
			    + " WHERE p.enaa_status = true  " + condicionSql;

		if(!condicionSqlAsociado.isEmpty()){
			if(documentoOperador!= null && !documentoOperador.isEmpty()){
				condicionSqlAsociado = " and  p.enaa_ci_user= '"+documentoOperador+"'" + condicionSqlAsociado;
			}
			if(!areaTecnicoCalidad.isEmpty()){
				condicionSqlAsociado+= " and p.area_id_ercs in ("+areaTecnicoCalidad+")" ;
			}
			sqlProyectoAsociados = " union "
					+ "SELECT a.enaa_id "
					+ "FROM coa_digitalization_linkage.associated_projects a inner join coa_digitalization_linkage.environmental_administrative_authorizations p on a.enaa_id = p.enaa_id "
					+ " inner join coa_digitalization_linkage.environmental_administrative_authorizations x on a.aspr_code_id = x.enaa_id "
					+ "	inner join suia_iii.sector_types c on p.sety_id = c.sety_id "
				    + " WHERE p.enaa_status = true  " + condicionSqlAsociado;
		}
		String sql = "select cast(count(distinct enaa_id) as integer) "
				+ " from ( "+sqlProyectoSuia + sqlProyectoAsociados +") as totalregistros";
		List<Object> result = (List<Object>)crudServiceBean.getEntityManager().createNativeQuery(sql).getResultList();
		Integer total =0;
		total += (Integer) result.get(0);
		return total;
	}
	
	public List<ProyectoAsociadoDigitalizacion> buscarProyectosAsociados(AutorizacionAdministrativa proyectoPrincipal){
		try {
			List<ProyectoAsociadoDigitalizacion> listaProyectosAsociados = new ArrayList<ProyectoAsociadoDigitalizacion>();
			AutorizacionAdministrativaAmbiental autorizacion = new AutorizacionAdministrativaAmbiental();
			autorizacion = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(proyectoPrincipal.getCodigo());
			if(autorizacion != null && autorizacion.getId() != null){
				listaProyectosAsociados = proyectoAsociadoDigitalizacionFacade.buscarProyectosAsociados(autorizacion.getId());
			}
			for (ProyectoAsociadoDigitalizacion proyectoAsociadoDigitalizacion : listaProyectosAsociados) {
				AutorizacionAdministrativa aaa = new AutorizacionAdministrativa();
				if(proyectoAsociadoDigitalizacion.getSistemaOriginal() != null){
					AutorizacionAdministrativaAmbiental objAutorizacion = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(proyectoAsociadoDigitalizacion.getProyectoAsociadoId());
					if(objAutorizacion != null){
						aaa.setId(objAutorizacion.getIdProyecto());
						aaa.setFuente(objAutorizacion.getSistema().toString());
						if("0".equals(objAutorizacion.getSistema().toString()))
							aaa.setId(objAutorizacion.getId());
						aaa.setIdDigitalizacion(objAutorizacion.getId());
						aaa.setTipoProyecto("Proyecto Inclusión");
						aaa.setCodigoDocumento(objAutorizacion.getResolucion());
						aaa.setCodigo(objAutorizacion.getCodigoProyecto());
						aaa.setNombre(objAutorizacion.getNombreProyecto());
						aaa.setSector(objAutorizacion.getTipoSector().getNombre());
						aaa.setCategoria(objAutorizacion.getAutorizacionAdministrativaAmbiental());
						aaa.setAreaEmisora(objAutorizacion.getAreaEmisora().getAreaAbbreviation());
						aaa.setAreaControl(objAutorizacion.getAreaResponsableControl().getAreaAbbreviation());
						if(objAutorizacion.getFechaRegistro() != null)
							aaa.setFecha(objAutorizacion.getFechaRegistro().toString().substring(0, 10));
						if(objAutorizacion.getFechaFinalizacionRegistro()!= null)
							aaa.setFechaFin(objAutorizacion.getFechaFinalizacionRegistro().toString().substring(0, 10));
						if(proyectoAsociadoDigitalizacion.isEsActualizacionCI())
							aaa.setTipoProyecto("Actualización de Certificado de Intersección");
						if("0".equals(objAutorizacion.getSistema().toString()) || "1".equals(objAutorizacion.getSistema().toString())){
							if(objAutorizacion.getFechaInicioResolucion() != null)
								aaa.setFecha(objAutorizacion.getFechaInicioResolucion().toString().substring(0, 10));
							if(objAutorizacion.getFechaResolucion() != null)
								aaa.setFechaFin(objAutorizacion.getFechaResolucion().toString().substring(0, 10));
						}
					}else{
						aaa.setTipoProyecto("Actualización de Certificado de Intersección");
						aaa.setCodigo(proyectoAsociadoDigitalizacion.getCodigo());
						aaa.setFuente("CI");
					}
				}else{
					switch (proyectoAsociadoDigitalizacion.getTipoProyecto().toString()) {
					case "3":  //ART
						AprobacionRequisitosTecnicos art = aprobacionRequisitosTecnicosFacade.getAprobacionRequisitosTecnicosPorId(proyectoAsociadoDigitalizacion.getProyectoAsociadoId());
						if(art != null){
							aaa.setId(art.getId());
							aaa.setFuente("11");
							aaa.setTipoProyecto("ART Asociado");
							aaa.setAreaEmisora(art.getAreaResponsable().getAreaAbbreviation());
							aaa.setCodigoDocumento(art.getNombreProyecto());
							aaa.setCodigo(art.getSolicitud());
							// busco las fecha de inicio y fin del proceso
							aaa = buscarFechasFin(aaa, "SUIA");
						}
						break;
					case "4":  //RGD
						switch (proyectoAsociadoDigitalizacion.getNombreTabla()) {
						case "suia_iii.hazardous_wastes_generators":
							GeneradorDesechosPeligrosos rgd = registroGeneradorDesechosFacade.get(proyectoAsociadoDigitalizacion.getProyectoAsociadoId());
							if(rgd != null){
								aaa.setId(rgd.getId());
								aaa.setNombre(rgd.getUsuario().getNombre());
								aaa.setFuente("10");
								aaa.setTipoProyecto("RGD Asociado");
								aaa.setAreaEmisora(rgd.getAreaResponsable().getAreaName());
								aaa.setCodigoDocumento(rgd.getCodigo());
								aaa.setCodigo(rgd.getSolicitud());
								// busco las fecha de uinicio y fin del proceso
								aaa = buscarFechasFin(aaa, "SUIA");
							}
							break;
						case "coa_waste_generator_record.waste_generator_record_coa":
							RegistroGeneradorDesechosRcoa registroGeneradorDesechos = registroGeneradorFacade.findById(proyectoAsociadoDigitalizacion.getProyectoAsociadoId());
							if(registroGeneradorDesechos != null){
								aaa.setId(registroGeneradorDesechos.getId());
								aaa.setNombre(registroGeneradorDesechos.getUsuarioCreacion());
								aaa.setFuente("10");
								aaa.setTipoProyecto("RGD Asociado");
								if(registroGeneradorDesechos.getAreaResponsable() != null)
									aaa.setAreaEmisora(registroGeneradorDesechos.getAreaResponsable().getAreaName());
								aaa.setCodigoDocumento(registroGeneradorDesechos.getCodigo());
								aaa.setCodigo(registroGeneradorDesechos.getCodigo());
								aaa.setIdDigitalizacion(proyectoAsociadoDigitalizacion.getAutorizacionAdministrativaAmbiental().getId());
								// busco las fecha de uinicio y fin del proceso
								aaa = buscarFechasFin(aaa, "SUIA");
							}
							break;

						default:
							break;
						}
						
						break;

					default:
						break;
					}
				}
				proyectoAsociadoDigitalizacion.setDatosProyectosAsociados(aaa);
			}
			return listaProyectosAsociados;
		} catch (Exception e) {
			return new ArrayList<ProyectoAsociadoDigitalizacion>();
		}
	}
	
	
	private AutorizacionAdministrativa buscarFechasFin(AutorizacionAdministrativa aaa, String sistema) throws JbpmException{
		Long procesoId = getProcessInstanceId(aaa);
		List<Object> object = procesosDigitalizacionFacade.getProcesoFechas("", procesoId, sistema);
		if(object != null && object.size() > 0){
			for (Object object2 : object) {
				Object[] array = (Object[]) object2;
				if(array[2] != null){
					Date fecha = (Date)array[2];
					aaa.setFecha(fecha.toString().substring(0, 10));
				}
				if(array[3] != null){
					Date fecha = (Date)array[3];
					aaa.setFechaFin(fecha.toString().substring(0, 10));
				}
			}
		}
		return aaa;
	}
	
	private Long getProcessInstanceId(AutorizacionAdministrativa proyectoAsociado) throws JbpmException{
		List<ProcessInstanceLog> listProcessProject = new ArrayList<ProcessInstanceLog>();
		List<String> listaProcesso = new ArrayList<String>();
		Usuario usuario = usuarioFacade.buscarUsuario(proyectoAsociado.getNombre());
		switch (proyectoAsociado.getFuente()) {
		case "4": // proyectos SUIA
			listaProcesso.add(Constantes.NOMBRE_PROCESO_CATEGORIA2V2);
			listaProcesso.add(Constantes.NOMBRE_PROCESO_CATEGORIA2);
			listaProcesso.add(Constantes.NOMBRE_PROCESO_LICENCIA_AMBIENTAL);
			listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(usuario, Constantes.ID_PROYECTO, proyectoAsociado.getId().toString());
			break;
		case "10":  // RGD
			listaProcesso.add(Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS);
			listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(usuario, "idRegistroGenerador", proyectoAsociado.getId().toString());
			if(listProcessProject == null || listProcessProject.size() == 0){
				// RGD
				listaProcesso.add(Constantes.RCOA_REGISTRO_GENERADOR_DESECHOS);
				listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(usuario, "idProyecto", proyectoAsociado.getIdDigitalizacion().toString());
			}
			break;
		case "11":  // ART
			listaProcesso.add(Constantes.NOMBRE_PROCESO_APROBACION_REQUISITOS_TECNICOS);
			listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(usuario, "numeroSolicitud", proyectoAsociado.getCodigo());
			break;

		default:
			break;
		}
		Long proceso = 0L;
		Collections.sort(listProcessProject, new Comparator<ProcessInstanceLog>() {
			@Override
			public int compare(ProcessInstanceLog o1, ProcessInstanceLog o2) {
				return new Long(o1.getProcessInstanceId()).compareTo(new Long(o2.getProcessInstanceId()));
			}
		});
		
		if (listProcessProject != null) {
			for (int j = 0; j < listProcessProject.size(); j++) {
				if (listProcessProject.get(j).getStatus() != 4) {
					String nombreproceso = listProcessProject.get(j).getProcessId();
					if(listaProcesso.contains(nombreproceso)){
						proceso = listProcessProject.get(j).getProcessInstanceId();
					}
				}
			}
		}
		return proceso;
	}
}
