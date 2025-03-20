package ec.gob.ambiente.rcoa.digitalizacion.facade;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class ProcesosDigitalizacionFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private DocumentoDigitalizacionFacade documentoDigitalizacionFacade;
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	public String dblinkSectorSubsector=Constantes.getDblinkSectorSubsector();
	
	public String dblinkSuiaBpm=Constantes.getDblinkBpmsSuiaiii();
	
	@SuppressWarnings("unchecked")
	public List<Object> getProcesoFechas(String codigoProyecto, Long procesosId, String sistema){
		String sqlPproyecto ="";
		switch (sistema) {
		case "4CAT": // 4 categorias
			sqlPproyecto = "SELECT * "
					+ "from dblink ('"+dblinkSuiaVerde+"', "
					+ "'SELECT dbid_, procdefid_, start_, end_ "
					+ " FROM jbpm4_hist_procinst "
					+ " where key_ = ''"+codigoProyecto+"'' "
					+ "  ')"
					+ " as t1 (id integer, tipoProceso character varying(255), iniciotarea timestamp, fintarea timestamp) ";
			break;
		case "SUIA": // SUIA
			sqlPproyecto = "SELECT * "
					+ "from dblink ('"+dblinkSuiaBpm+"', "
					+ "'SELECT id, processid, start_date, end_date  "
					+ " FROM processinstancelog "
					+ " where processinstanceid = "+procesosId+" "
					+ "  ')"
					+ " as t1 (id integer, tipoProceso character varying(255), iniciotarea timestamp, fintarea timestamp) ";
			break;
		case "SECTOR": // sector / subsector
			sqlPproyecto = "SELECT * "
					+ "from dblink ('"+dblinkSectorSubsector+"', "
					+ "'SELECT dbid_, procdefid_, start_, end_ "
					+ " FROM jbpm4_hist_procinst "
					+ " where key_ = ''"+codigoProyecto+"'' "
					+ "  ')"
					+ " as t1 (id integer, tipoProceso character varying(255), iniciotarea timestamp, fintarea timestamp) ";
			break;

		default:
			break;
		}
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		List<Object> result = queryProyecto.getResultList();
		if(result.size()>0){
			return result;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Tarea> getTareas4Categorias(String codigoProyecto, String sistema){
		String sqlPproyecto = "SELECT * "
				+ "from dblink ('"+(sistema.equals("2")?dblinkSuiaVerde:dblinkSectorSubsector)+"', "
				+ "'SELECT b.dbid_, a.key_, b.activity_name_, b.start_, b.end_"
				+ " FROM jbpm4_hist_procinst a inner join jbpm4_hist_actinst b on a.dbid_ = b.hproci_"
				+ " where key_ = ''"+codigoProyecto+"'' "
				+ " order by b.dbid_ desc  ')"
				+ " as t1 (id integer, codigo character varying(255), actividad character varying(255), iniciotarea timestamp, fintarea timestamp) ";
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		List<Object> result = queryProyecto.getResultList();
		if(result.size()>0){
			List<Tarea> tareas = new ArrayList<Tarea>();
			for (Object object : result) {
				Object[] array = (Object[]) object;
				Tarea tareaAux = new Tarea();
				Integer id = (array[0] == null) ? 0 : ((Integer) array[0]);
				tareaAux.setId(Long.valueOf(id));
				//String nombreTarea = Constantes.getCategoriaName((String)array[2]);
				String nombreTarea = (String)array[2];
				tareaAux.setNombre(nombreTarea);
				if(array[3] != null){
					Date fecha = (Date)array[3];
					tareaAux.setFechaInicio(fecha);
				}
				if(array[4] != null){
					Date fecha = (Date)array[4];
					tareaAux.setFechaFin(fecha);
					tareaAux.setEstado("Completado");
				}
				tareas.add(tareaAux);
			}
			return tareas;
		}
		return new ArrayList<Tarea>();
	}
	
	@SuppressWarnings("unchecked")
	public List<Documento> getDocumentos4Categorias(String codigoProyecto, String sistema) throws ParseException{
		String sqlPproyecto = "";
		if(sistema.equals("1")){
			sqlPproyecto = "SELECT * "
				+ "from dblink ('"+dblinkSuiaVerde+"', "
				+ "'SELECT lic_secuencial, licdoc_nombre, licdoc_extension, licdoc_descripcion, cast(licdoc_create_date as date), licdoc_tipo, licdoc_alfresco_id "
				+ " FROM licencia_ambiental_fisica_documento "
				+ " where  lic_secuencial = "+codigoProyecto+" "
				+ "   ')"
				+ " as t1 (id integer, nombre text, extension text, descripcion text, fecha timestamp, key text, urlalfresco text) ";
		}else{
			sqlPproyecto = "SELECT * "
				+ "from dblink ('"+(sistema.equals("2")?dblinkSuiaVerde:dblinkSectorSubsector)+"', "
				+ "'SELECT id, nombre, extension, descripcion, cast(fecha as date), key, urlalfresco "
				+ " FROM documento "
				+ " where visible and proyecto = ''"+codigoProyecto+"'' "
				+ "   ')"
				+ " as t1 (id integer, nombre text, extension text, descripcion text, fecha timestamp, key text, urlalfresco text) ";
		}
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		List<Object> result = queryProyecto.getResultList();
		if(result.size()>0){
			List<Documento> listaDocumentos = new ArrayList<Documento>();
			for (Object object : result) {
				Object[] array = (Object[]) object;
				Documento documentoAux = new Documento();
				Integer id = (array[0] == null) ? 0 : ((Integer) array[0]);
				documentoAux.setId(id);
				documentoAux.setNombre((String)array[1]);
				documentoAux.setExtesion((String)array[2]);
				documentoAux.setDescripcion((String)array[3]);
				if(array[4] != null){
					Date fecha = (Date)array[4];
					documentoAux.setFechaCreacion(fecha);
				}
				if(array[6] != null)
					documentoAux.setIdAlfresco((String)array[6]);
				//busco el archivo si esta en el alfresco
				byte[] byteDocumento = null;
				try{
					if(documentoAux.getIdAlfresco() != null){
						byteDocumento = documentoDigitalizacionFacade.descargar(documentoAux.getIdAlfresco());
					}else{
						//documentoAux.setId(8869925);
						String workSpaces = documentoDigitalizacionFacade.getUrl(documentoAux.getId().toString(), documentoAux.getFechaCreacion().toString());
						byteDocumento = documentoDigitalizacionFacade.descargar(workSpaces);
					}
					if(byteDocumento != null){
						documentoAux.setContenidoDocumento(byteDocumento);
					}
				} catch (CmisAlfrescoException e) {
					
				}
				
				listaDocumentos.add(documentoAux);
			}
			return listaDocumentos;
		}
		return new ArrayList<Documento>();
	}
}