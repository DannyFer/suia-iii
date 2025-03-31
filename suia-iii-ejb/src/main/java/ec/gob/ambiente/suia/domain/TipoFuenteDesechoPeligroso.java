package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the categories_catalog database table.
 * 
 */
@Entity
@Table(name = "waste_dangerous_type", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wdty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdty_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = "TipoFuenteDesechosPeligrosos.findAll", query = "SELECT c FROM TipoFuenteDesechoPeligroso c") })
public class TipoFuenteDesechoPeligroso extends EntidadBase implements
		Serializable {
	private static final long serialVersionUID = 1L;
	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.TipoFuenteDesechoPeligroso.findAll";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "WASTE_DANGEROUS_TYPE_wdtyID_GENERATOR", sequenceName = "seq_wdty_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_DANGEROUS_TYPE_wdtyID_GENERATOR")
	@Column(name = "wdty_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "wdty_type", length = 255)
	private String tipo;

	@Getter
	@Setter
	@Column(name = "wdty_code", nullable = true, length = 255)
	private String codigo;

	@Getter
	@Setter
	@Column(name = "secl_id")
	private Integer sectorClasificacion;

	@Override
	public String toString() {
		return this.codigo + " | " + this.tipo;
	}

}