package ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class Proyecto4CategoriasFacade {

	private static final Logger LOG = Logger.getLogger(Proyecto4CategoriasFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;
		
	private String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	private String sqlScriptProyectos(String codigoProyecto,String nombreProyecto,String sector,String categoria,String inecProvincia,boolean mostrarEstrategico,Boolean filtrarInterseca,Boolean contador,Integer limite,Integer inicio){
		codigoProyecto=codigoProyecto==null?"":codigoProyecto;
		nombreProyecto=nombreProyecto==null?"":nombreProyecto;
		sector=sector==null?"":sector;
		categoria=categoria==null?"":categoria;
		
		if (categoria.contains("Certificado")) {
			categoria = "''I''";
		}else if (categoria.contains("Registro")) {
			  categoria = "''II''";
		}else if (categoria.contains("Licencia")) {
			  categoria = "''III'',''IV''";
		}else {
			  categoria = "''I'',''II'',''III'',''IV''";
		}
		
		String CONECTAR_DBLINK="select * from dblink('"+dblinkSuiaVerde+"','";
		String SELECT_COUNT=" select count(distinct p.id)";
		String SELECT_CAMPOS=" select distinct p.id,p.nombre,p.fecharegistro,c.cata_sector,ca_categoria";
		String FROM_PROYECTO=" from proyectolicenciaambiental p ";
		String INNER_CATEGORIA=" inner join catalogo_categoria c on(c.id_catalogo=p.id_catalogo)";		
		String INNER_INTERSECA=" inner join variable ci on(ci.proyecto=p.id and ci.key=''interseca'' and ci.stringvalue=''"+(filtrarInterseca==null?"":(filtrarInterseca?"SI":"NO"))+"'')";
		String INNER_UBICACION=" inner join ubicacion u on(u.proyecto_id=p.id) inner join parroquia q on(q.id=u.parroquia and substring(q.parroquia_inec from 1 for 2)=''"+inecProvincia+"''"+(mostrarEstrategico?" or c.estrategico=TRUE":"")+")";
		String WHERE_STATUS=" where p.estadoproyecto=true";
		String AND_OCULTAR_ESTRATEGICOS=" and c.estrategico=false";		
		String AND_FILTROS=" and p.id like ''%"+codigoProyecto+"%'' and upper(p.nombre) like upper(''%"+nombreProyecto+"%'') and upper(c.cata_sector) like upper(''%"+sector+"%'') and upper(c.ca_categoria) in("+categoria+")";
		String PAGINADO=" order by 1 desc limit "+limite+" offset "+inicio;
		String AS_RETURN_COUNT=" ')as r (count integer)";
		String AS_RETURN_CAMPOS=" ')as r (id character varying,nombre character varying,fecharegistro timestamp,sector character varying,categoria character varying)";
		
		return CONECTAR_DBLINK
				+(contador?SELECT_COUNT:SELECT_CAMPOS)
				+FROM_PROYECTO
				+INNER_CATEGORIA
				+(inecProvincia==null?"":INNER_UBICACION)
				+(filtrarInterseca==null?"":INNER_INTERSECA)
				+WHERE_STATUS
				+(mostrarEstrategico?"":AND_OCULTAR_ESTRATEGICOS)
				+AND_FILTROS
				+(contador?"":PAGINADO)
				+(contador?AS_RETURN_COUNT:AS_RETURN_CAMPOS);
	}
	
	private String sqlScriptProyectosPC(String codigoProyecto,String nombreProyecto,String sector,String categoria,String inecProvincia,boolean mostrarEstrategico,Boolean filtrarInterseca,Boolean contador,Integer limite,Integer inicio){
		codigoProyecto=codigoProyecto==null?"":codigoProyecto;
		nombreProyecto=nombreProyecto==null?"":nombreProyecto;
		sector=sector==null?"":sector;
		categoria=categoria==null?"":categoria;
		
		if (categoria.contains("Certificado")) {
			categoria = "''I''";
		}else if (categoria.contains("Registro")) {
			  categoria = "''II''";
		}else if (categoria.contains("Licencia")) {
			  categoria = "''III'',''IV''";
		}else {
			  categoria = "''I'',''II'',''III'',''IV''";
		}
		
		String CONECTAR_DBLINK="select * from dblink('"+dblinkSuiaVerde+"','";
		String SELECT_COUNT=" select count(distinct p.id)";
		String SELECT_CAMPOS=" select distinct p.id,p.nombre,p.fecharegistro,c.cata_sector,ca_categoria";
		String FROM_PROYECTO=" from proyectolicenciaambiental p ";
		String INNER_CATEGORIA=" inner join catalogo_categoria c on(c.id_catalogo=p.id_catalogo)";		
		String INNER_INTERSECA=" inner join variable ci on(ci.proyecto=p.id and ci.key=''interseca'' and ci.stringvalue=''"+(filtrarInterseca==null?"":(filtrarInterseca?"SI":"NO"))+"'')";
		String INNER_UBICACION=" inner join ubicacion u on(u.proyecto_id=p.id) inner join parroquia q on(q.id=u.parroquia and substring(q.parroquia_inec from 1 for 2)=''"+inecProvincia+"''"+(mostrarEstrategico?" or c.estrategico=TRUE":"")+")";
		String WHERE_STATUS=" where p.estadoproyecto=true";
		String AND_OCULTAR_ESTRATEGICOS=" and c.estrategico=true";		
		String AND_FILTROS=" and p.id like ''%"+codigoProyecto+"%'' and upper(p.nombre) like upper(''%"+nombreProyecto+"%'') and upper(c.cata_sector) like upper(''%"+sector+"%'') and upper(c.ca_categoria) in("+categoria+")";
		String PAGINADO=" order by 1 desc limit "+limite+" offset "+inicio;
		String AS_RETURN_COUNT=" ')as r (count integer)";
		String AS_RETURN_CAMPOS=" ')as r (id character varying,nombre character varying,fecharegistro timestamp,sector character varying,categoria character varying)";
		
		return CONECTAR_DBLINK
				+(contador?SELECT_COUNT:SELECT_CAMPOS)
				+FROM_PROYECTO
				+INNER_CATEGORIA
				+(inecProvincia==null?"":INNER_UBICACION)
				+(filtrarInterseca==null?"":INNER_INTERSECA)
				+WHERE_STATUS
				+(mostrarEstrategico?"":AND_OCULTAR_ESTRATEGICOS)
				+AND_FILTROS
				+(contador?"":PAGINADO)
				+(contador?AS_RETURN_COUNT:AS_RETURN_CAMPOS);
	}
	
	private ProyectoCustom getProyectoCustom(Object[] array) {
		ProyectoCustom proyectoCustom=new ProyectoCustom();		
		proyectoCustom.setSourceType(ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA);
		proyectoCustom.setId((String)array[0]);
		proyectoCustom.setCodigo((String)array[0]);
		proyectoCustom.setNombre((String)array[1]);		
		proyectoCustom.setCategoria((String)array[4]);
		
		String categoriaNombrePublico="";
		if (proyectoCustom.getCategoria() == null)
			categoriaNombrePublico= "";
		if (proyectoCustom.getCategoria().equals("I"))
			categoriaNombrePublico= "Certificado Ambiental";
		if (proyectoCustom.getCategoria().equals("II"))
			categoriaNombrePublico= "Registro Ambiental";
		else
			categoriaNombrePublico= "Licencia Ambiental";
		
		proyectoCustom.setCategoriaNombrePublico(categoriaNombrePublico);
				
		String sector = (String)array[3];
		try {
			sector = sector.substring(0, 1).toUpperCase() + sector.substring(1);
		} catch (Exception e) {
		}
		proyectoCustom.setSector(sector);

		Date registro = (Date)array[2];
		String registroString = "";
		if (registro != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			registroString = dateFormat.format(registro);
		}
		proyectoCustom.setRegistro(registroString);
		proyectoCustom.setResponsableSiglas("N/D");
		proyectoCustom.setResponsable("No disponible");		
		return proyectoCustom;
	}
	
	private Integer contarProyectos(String codigoProyecto,String nombreProyecto,String sector,String categoria,String inecProvincia,boolean mostarEstrategico,Boolean filtraInterseca){		
		try {
			String sqlPproyecto=sqlScriptProyectos(codigoProyecto, nombreProyecto, sector, categoria,inecProvincia,mostarEstrategico,filtraInterseca,true,null,null);						
			Query query =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
			if(query.getResultList().size()>0){
				return (Integer)query.getSingleResult();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage()+": "+e.getCause());
		}
		
		return 0;
	}
	
	private Integer contarProyectosPC(String codigoProyecto,String nombreProyecto,String sector,String categoria,String inecProvincia,boolean mostarEstrategico,Boolean filtraInterseca){		
		try {
			String sqlPproyecto=sqlScriptProyectosPC(codigoProyecto, nombreProyecto, sector, categoria,inecProvincia,mostarEstrategico,filtraInterseca,true,null,null);						
			Query query =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
			if(query.getResultList().size()>0){
				return (Integer)query.getSingleResult();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage()+": "+e.getCause());
		}
		
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	private List<ProyectoCustom> listarProyectos(String codigoProyecto,String nombreProyecto,String sector,String categoria,String inecProvincia,boolean mostrarEstrategico,Boolean filtrarInterseca,Integer limite,Integer inicio){
		List<ProyectoCustom> proyectos=new ArrayList<ProyectoCustom>();
		
		try {
			String sqlPproyecto=sqlScriptProyectos(codigoProyecto, nombreProyecto, sector, categoria, inecProvincia, mostrarEstrategico, filtrarInterseca, false, limite, inicio);							
			Query query =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
			List<Object> result=query.getResultList();
			for (Object row : result) {
				Object[] obj=(Object[])row;
				ProyectoCustom proyecto=getProyectoCustom(obj);
				proyectos.add(proyecto);				
			}
		} catch (Exception e) {
			LOG.error(e.getMessage()+": "+e.getCause());
		}				
		return proyectos;
	}
	
	@SuppressWarnings("unchecked")
	private List<ProyectoCustom> listarProyectosPC(String codigoProyecto,String nombreProyecto,String sector,String categoria,String inecProvincia,boolean mostrarEstrategico,Boolean filtrarInterseca,Integer limite,Integer inicio){
		List<ProyectoCustom> proyectos=new ArrayList<ProyectoCustom>();
		
		try {
			String sqlPproyecto=sqlScriptProyectosPC(codigoProyecto, nombreProyecto, sector, categoria, inecProvincia, mostrarEstrategico, filtrarInterseca, false, limite, inicio);							
			Query query =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
			List<Object> result=query.getResultList();
			for (Object row : result) {
				Object[] obj=(Object[])row;
				ProyectoCustom proyecto=getProyectoCustom(obj);
				proyectos.add(proyecto);				
			}
		} catch (Exception e) {
			LOG.error(e.getMessage()+": "+e.getCause());
		}				
		return proyectos;
	}
	
	//TODOS
	public Integer contarProyectos(String codigoProyecto,String nombreProyecto,String sector,String categoria){		
		return contarProyectos(codigoProyecto, nombreProyecto, sector, categoria,null,true,null);	
	}	
	public List<ProyectoCustom> listarProyectos(String codigoProyecto,String nombreProyecto,String sector,String categoria,Integer limite,Integer inicio){		
		return listarProyectos(codigoProyecto, nombreProyecto, sector, categoria,null,true,null,limite,inicio);	
	}
	
	//INTERSECA
	public Integer contarProyectosPatrimonio(String codigoProyecto,String nombreProyecto,String sector,String categoria){		
		return contarProyectos(codigoProyecto, nombreProyecto, sector, categoria,null,false,true);	
	}	
	public List<ProyectoCustom> listarProyectosPatrimonio(String codigoProyecto,String nombreProyecto,String sector,String categoria,Integer limite,Integer inicio){		
		return listarProyectos(codigoProyecto, nombreProyecto, sector, categoria,null,false,true,limite,inicio);	
	}
	
	//UBICACION
	public Integer contarProyectos(String codigoProyecto,String nombreProyecto,String sector,String categoria, String inecProvincia){		
		return contarProyectos(codigoProyecto, nombreProyecto, sector, categoria,inecProvincia,true,null);	
	}	
	public List<ProyectoCustom> listarProyectos(String codigoProyecto,String nombreProyecto,String sector,String categoria, String inecProvincia,Integer limite,Integer inicio){		
		return listarProyectos(codigoProyecto, nombreProyecto, sector, categoria,inecProvincia,true,null,limite,inicio);	
	}
	
	//UBICACION, NO ESTRATEGICOS, NO INTERSECA
	public Integer contarProyectosEnte(String codigoProyecto,String nombreProyecto,String sector,String categoria, String inecProvincia){		
		return contarProyectos(codigoProyecto, nombreProyecto, sector, categoria,inecProvincia,false,false);	
	}	
	public List<ProyectoCustom> listarProyectosEnte(String codigoProyecto,String nombreProyecto,String sector,String categoria, String inecProvincia,Integer limite,Integer inicio){		
		return listarProyectos(codigoProyecto, nombreProyecto, sector, categoria,inecProvincia,false,false,limite,inicio);	
	}
	
	public Integer contarProyectosPC(String codigoProyecto,String nombreProyecto,String sector,String categoria){		
		return contarProyectosPC(codigoProyecto, nombreProyecto, sector, categoria,null,true,null);	
	}	
	public List<ProyectoCustom> listarProyectosPC(String codigoProyecto,String nombreProyecto,String sector,String categoria,Integer limite,Integer inicio){		
		return listarProyectosPC(codigoProyecto, nombreProyecto, sector, categoria,null,true,null,limite,inicio);	
	}	
}
