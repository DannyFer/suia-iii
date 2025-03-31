package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the categories_catalog database table.
 * 
 */
@Entity
@Table(name = "waste_dangerous_incompatibilities", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wdin_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdin_status = 'TRUE'")
public class IncompatibilidadDesechoPeligroso extends EntidadBase {

	private static final long serialVersionUID = -3199091691258306081L;

	public static final String INCOMPATIBILIDAD_DESECHO_PELIGROSO_OTRO = "Otros";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "WASTE_DANGEROUS_INCOMPATIBILITIES_ID_GENERATOR", sequenceName = "seq_wdin_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_DANGEROUS_INCOMPATIBILITIES_ID_GENERATOR")
	@Column(name = "wdin_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "wdin_name")
	private String nombre;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wdin_parent_id")
	@ForeignKey(name = "fk_wdin_parent_id_wdin_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdin_status = 'TRUE'")
	private IncompatibilidadDesechoPeligroso incompatibilidadDesechoPeligroso;

	@Getter
	@Setter
	@OneToMany(mappedBy = "incompatibilidadDesechoPeligroso")
	private List<IncompatibilidadDesechoPeligroso> incompatibilidadesDesechosPeligrososHijos;

	public boolean isIncompatibilidadDesechoFinal() {
		return incompatibilidadesDesechosPeligrososHijos == null || incompatibilidadesDesechosPeligrososHijos.isEmpty();
	}

	@Override
	public String toString() {
		return this.nombre;
	}

	@Getter
	@Setter
	@Transient
	private String otroValor;
	
	public boolean isOtro() {
		return nombre != null && nombre.trim().equals(INCOMPATIBILIDAD_DESECHO_PELIGROSO_OTRO);
	}

}