package ec.gob.ambiente.rcoa.usuario.controllers;

import java.util.Date;

import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * Plantilla Html TerminosCondicionesHtml
 * @author carlos.guevara
 *
 */
public class TerminosCondicionesHtml {		
	
	private String nombreOperador;
	
	public TerminosCondicionesHtml(){}
	
	public TerminosCondicionesHtml(String nombreOperador)
	{		
		this.nombreOperador=nombreOperador;
	}
			
	public String getNombreOperador()
	{
		return nombreOperador;
	}	
		
	public String getFecha()
	{
		return JsfUtil.getDateFormat(new Date());
	}	
}