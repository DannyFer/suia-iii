/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author ishmael
 */
@Entity
@Table(name = "areas_types", schema = "public")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "arty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "arty_status = 'TRUE'")
public class TipoArea extends EntidadBase {

	private static final long serialVersionUID = 4137546930429850972L;
	
	public static final int TIPO_AREA_ENTE_ACREDITADO = 3;

	@Getter
	@Setter
	@Column(name = "arty_id")
	@Id
	@SequenceGenerator(name = "AREAS_TYPE_GENERATOR", initialValue = 1, sequenceName = "seq_arty_id", schema = "public")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AREAS_TYPE_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "arty_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "arty_abbreviation")
	private String siglas;

	@Getter
	@Setter
	@OneToMany(mappedBy = "tipoArea", fetch = FetchType.LAZY)
	private List<Area> areasList;

}
