package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "operator_tips", schema = "public")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "opti_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "opti_status = 'TRUE'")
@NamedQuery(name = "operator_tips.findAll", query = "SELECT o FROM OpcionesEncuesta o")

public class OpcionesEncuesta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "opti_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "opti_ordinal")
	private Integer ordinal;

	@Getter
	@Setter
	@Column(name = "opti_name")
	private String nombre;

}