/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */ 
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/** 
 * <b>
 * Clase que representa a la tabla que contiene las actividades de los protocolos de pruebas.
 * </b>
 *  
 * @author vero
 * @version $Revision: 1.0 $ <p>[$Author: vero $, $Date: 15/06/2015 $]</p>
 * 
 */
@Entity
@Table(name = "protocol_test_activity", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prta_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prta_status = 'TRUE'")
public class ActividadProtocoloPrueba extends EntidadBase{
	
	
	/**
	* 
	*/ 
	private static final long serialVersionUID = -6201657911638411494L;

	@Getter
	@Setter
	@Id
	@Column(name = "prta_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "prta_activity")
	private String actividad;
	
	
	@Getter
	@Setter
	@Column(name = "prta_type")
	private String tipo;

}
