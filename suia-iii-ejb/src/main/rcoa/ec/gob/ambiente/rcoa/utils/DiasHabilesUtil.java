package ec.gob.ambiente.rcoa.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.domain.Holiday;

@Stateless
public class DiasHabilesUtil {
	
	@EJB
	private FeriadosFacade feriadosFacade;
	
	/**
	 * Agrega el total de dias HABILES a la fecha inicial
	 * @param fechaInicial
	 * @param diasAdicionales
	 * @return fechaFinal
	 */
	public Date recuperarFechaFinal(Date fecha, int diasAdicionales){		
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaInicio = formatter.parse(formatter.format(fecha));
			
			Calendar fechaInicial = Calendar.getInstance();
			fechaInicial.setTime(fechaInicio);
			
			Calendar fechaFin = Calendar.getInstance();
			fechaFin.setTime(fechaInicio);
			while (diasAdicionales != 0) {
				fechaFin.add(Calendar.DATE, 1);
				if (fechaFin.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
						 && fechaFin.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
					diasAdicionales --;
				}
			}
			
			Integer diasFeriados = feriadosFacade.getDiasFeriadosNacionalesPorRangoFechas(fechaInicio, fechaFin.getTime());
			if(diasFeriados > 0)
				return recuperarFechaFinal(fechaFin.getTime(), diasFeriados);

			return fechaFin.getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Date recuperarFechaHabil(Date fechaInicial, int diasAdicionales, Boolean horaInicio) throws Exception{		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar fechaAuxiliar = Calendar.getInstance();
		fechaAuxiliar.setTime(fechaInicial);
		
		if(horaInicio != null) {
			if(horaInicio) {
				fechaAuxiliar.set(Calendar.HOUR_OF_DAY,0);
				fechaAuxiliar.set(Calendar.MINUTE,0);
				fechaAuxiliar.set(Calendar.SECOND,0);
				fechaAuxiliar.set(Calendar.MILLISECOND,0);
			} else {
				fechaAuxiliar.set(Calendar.HOUR_OF_DAY,23);
				fechaAuxiliar.set(Calendar.MINUTE,59);
				fechaAuxiliar.set(Calendar.SECOND,59);
				fechaAuxiliar.set(Calendar.MILLISECOND,0);
			}
		}
		
		int i = 0;
		while(i < diasAdicionales){
			fechaAuxiliar.add(Calendar.DATE, 1);
			
			Integer anio = fechaAuxiliar.get(Calendar.YEAR);
			Integer mes = fechaAuxiliar.get(Calendar.MONTH) + 1;
			Integer dia = fechaAuxiliar.get(Calendar.DATE);
		
			String fechaFeriado = anio.toString() + "-" + mes.toString() + "-"+dia.toString();
			Date fechaComparar = dateFormat.parse(fechaFeriado);
		
			List<Holiday> listaFeriados = feriadosFacade.listarFeriadosNacionalesPorRangoFechas(fechaComparar, fechaComparar);
			
			if(fechaAuxiliar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fechaAuxiliar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY){
				if(listaFeriados == null)
					i++;
			}
		}
	
		Date fechaFinal = new Date();
		fechaFinal = fechaAuxiliar.getTime();
		return fechaFinal;
	}
	
}
