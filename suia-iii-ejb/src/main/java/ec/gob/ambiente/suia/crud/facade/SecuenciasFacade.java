package ec.gob.ambiente.suia.crud.facade;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.LockModeType;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.SecuenciaDedicada;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class SecuenciasFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	/**
	 * Retorna una secuencia unica para cada area, para los informe tecnicos,
	 * siguiendo el formato '000089', utilizando como key el area y la cadena
	 * "_INFORME_TECNICO".
	 * 
	 * @param area
	 * @return
	 * @throws Exception
	 */
	public String getSecuenciaParaInformeTecnicoAreaResponsable(Area area) throws Exception {
		return getNextValueDedicateSequence(area.getAreaAbbreviation() + "_INFORME_TECNICO", 6);
	}

	/**
	 * Retorna una secuencia unica para cada area, para cualquier oficio emitido
	 * en el área, siguiendo el formato '000092', utilizando como key el area y
	 * la cadena "_OFICIO_GENERAL".
	 * 
	 * @param area
	 * @return
	 * @throws Exception
	 */
	public String getSecuenciaParaOficioAreaResponsable(Area area) throws Exception {
		return getNextValueDedicateSequence(area.getAreaAbbreviation() + "_OFICIO_GENERAL", 6);
	}

	/**
	 * Retorna una secuencia unica para cada area, siguiendo el formato
	 * 'MAAE-SUIA-RA-2015-5444', utilizando como key el area.
	 * 
	 * @param area
	 * @return
	 * @throws Exception
	 */
	public String getSecuenciaResolucionAreaResponsable(Area area) throws Exception {
		String nextCode = Constantes.SIGLAS_INSTITUCION + "-SUIA-RA-" + area.getAreaAbbreviation() + "-" + getCurrentYear() + "-";
		nextCode += getNextValueDedicateSequence(area.getAreaAbbreviation());
		return nextCode;
	}


	public String getSecuenciaResolucionEnteResponsableRCOA(Area area) throws Exception {
		String nextCode = area.getAreaAbbreviation()+"-SUIA-RA-" +  getCurrentYear() + "-";
		nextCode += getNextValueDedicateSequence(area.getAreaAbbreviation(), 3);
		return nextCode;
	}
	
	public String getSecuenciaResolucionAreaResponsableRCOA(Area area) throws Exception {
		String nextCode = Constantes.SIGLAS_INSTITUCION + "-SUIA-RA-" + area.getAreaAbbreviation() + "-" + getCurrentYear() + "-";
		nextCode += getNextValueDedicateSequence(area.getAreaAbbreviation(), 3);
		return nextCode;
	}

	
	public String getSecuenciaResolucionAreaResponsableNuevoFormato(Area area) throws Exception {
		String nextCode;
		String nextCodeAux;
		if (area.getTipoArea().getId()==3){
			nextCode = "-"+area.getAreaAbbreviation() + "-" + getCurrentYear() + "-CA-SUIA" ;
		}else{
			nextCode = "-" + area.getAreaAbbreviation() + "-" + getCurrentYear() + "-CA-SUIA-" + Constantes.SIGLAS_INSTITUCION ;
		}
		
		nextCodeAux=nextCode;
		nextCode = getNextValueDedicateSequence(area.getAreaAbbreviation())+ nextCodeAux;
		return nextCode;
	}

	
	/**
	 * Retorna una secuencia unica para cada area en dependencia del prefijo que
	 * se pase. Ej: Si el area es casa matriz, y el documento es EGD, se genera
	 * la secuencia para 'DNCA_EGD'.
	 * 
	 * @param area
	 * @param documento
	 * @return
	 * @throws Exception
	 */
	public String getSecuenciaAreaDocumento(Area area, String documento) throws Exception {
		return getNextValueDedicateSequence(area.getAreaAbbreviation() + "_" + documento) + "";
	}

	/**
	 * Retorna la secuencia estandar para un proyecto. Siguiendo el formato
	 * 'MAE-RA-2015-52444'.
	 * 
	 * @return
	 */
	public String getSecuenciaProyecto() {
		String nextCode = Constantes.SIGLAS_INSTITUCION + "-RA-" + getCurrentYear() + "-";
		nextCode += crudServiceBean.getSecuenceNextValue(ProyectoLicenciamientoAmbiental.SEQUENCE_CODE, "suia_iii")
				.toString();
		return nextCode;
	}

	/**
	 * Retorna el valor numérico de la secuencia utilizando como key
	 * 'sequenceName'.
	 * 
	 * @param sequenceName
	 * @return
	 * @throws Exception
	 */
	public Long getNextValueDedicateSequence(String sequenceName) throws Exception {
		SecuenciaDedicada secuenciaDedicada = getSequenciaDedicadaUnlock(sequenceName);
		crudServiceBean.getEntityManager().lock(secuenciaDedicada, LockModeType.PESSIMISTIC_READ);
		Long sequence = secuenciaDedicada.incrementSequence();
		crudServiceBean.saveOrUpdate(secuenciaDedicada);
		return sequence;
	}

	/**
	 * Retorna el valor numérico de la secuencia utilizando como key
	 * 'sequenceName'.
	 * 
	 * @param sequenceName
	 * @return
	 * @throws Exception
	 */
	public String getNextValueDedicateSequence(String sequenceName, int minDigits) throws Exception {
		SecuenciaDedicada secuenciaDedicada = getSequenciaDedicadaUnlock(sequenceName);
		crudServiceBean.getEntityManager().lock(secuenciaDedicada, LockModeType.PESSIMISTIC_READ);
		Long sequence = secuenciaDedicada.incrementSequence();
		crudServiceBean.saveOrUpdate(secuenciaDedicada);
		String sequenceString = sequence.toString();
		for (int i = sequenceString.length(); i < minDigits; i++) {
			sequenceString = "0" + sequenceString;
		}
		return sequenceString;
	}

	/**
	 * Retorna el valor numérico de una secuencia creada en la base de datos.
	 * 
	 * @param name
	 * @param schema
	 * @return
	 * @throws Exception
	 */
	public Long getSecuenceNextValue(String name, String schema) {
		return Long.parseLong(crudServiceBean.getSecuenceNextValue(name, schema).toString());
	}
	
	/**
	 * Actualiza el valor numérico de una secuencia creada en la base de datos.
	 * 
	 * @param name
	 * @param schema
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public void updateSecuenceValue(String name, String schema, long value) {
		crudServiceBean.updateSecuenceValue(name, schema, value);
	}

	/**
	 * Retorna como un String el año actual. Ej: Para el año 2015, retorna
	 * '2015'.
	 * 
	 * @return
	 */
	public String getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR) + "";
	}

	/**
	 * Retorna como un String el mes actual. Ej: Para el mes de mayo, retorna
	 * '05'.
	 * 
	 * @return
	 */
	public String getCurrentMonth() {
		String monthFormat = "";
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		if (month < 10)
			monthFormat = "0" + month;
		else
			monthFormat = month + "";
		return monthFormat;
	}

	private SecuenciaDedicada getSequenciaDedicadaUnlock(String sequenceName) throws Exception {
		int repeat = 0;
		SecuenciaDedicada secuenciaDedicada = null;
		while (repeat < 3) {
			try {
				secuenciaDedicada = getSequenciaDedicada(sequenceName);
				return secuenciaDedicada;
			} catch (Exception e) {
				Thread.sleep(2000);
				repeat++;
			}
		}
		return null;
	}

	private SecuenciaDedicada getSequenciaDedicada(String sequenceName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nombre", sequenceName);
		SecuenciaDedicada secuenciaDedicada = crudServiceBean.findByNamedQuerySingleResult(
				SecuenciaDedicada.FIND_BY_NAME, params);
		if (secuenciaDedicada == null) {
			secuenciaDedicada = new SecuenciaDedicada();
			secuenciaDedicada.setNombre(sequenceName);
			crudServiceBean.saveOrUpdate(secuenciaDedicada);
		}
		return secuenciaDedicada;
	}
	
	/**
	 * Retorna una secuencia unica para cada area, para cualquier oficio emitido
	 * en el área, siguiendo el formato '000092', utilizando como key el area y
	 * la cadena "_OFICIO_GENERAL".
	 * 
	 * @param area
	 * @return
	 * @throws Exception
	 */
	public String getSecuenciaMemorandoForestal() throws Exception {
		return getNextValueDedicateSequence("MEMORANDO_FORESTAL", 4);
	}
	
	public String getSecuenciaMemorandoBiodiversidad() throws Exception {
		return getNextValueDedicateSequence("MEMORANDO_BIODIVERSIDAD", 4);
	}
}
