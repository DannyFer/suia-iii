package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name = "scdr_machine_equipment", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "maeq_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "maeq_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "maeq_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "maeq_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "maeq_user_update"))})
@Filter(name = EntidadAuditable.FILTER_ACTIVE, condition = "maeq_status = 'TRUE'")
public class PerforacionMaquinasEquipos extends EntidadAuditable{

	private static final long serialVersionUID = -9221079102681351879L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="maeq_id")
	@Getter
    @Setter
	private Integer id;
	
    @Getter
    @Setter
	@Column(name="scdr_id")
    private Integer perforacionExplorativa;
	
	@Getter
    @Setter
	@Column(name="maeq_name")
	private String name;
	
	@Getter
    @Setter
	@Column(name="maeq_unit")
	private String unit;
	
	@Getter
    @Setter
	@Column(name="maeq_characteristics")
	private String characteristics;
	
	@Getter
    @Setter
	@Column(name="maeq_process")
	private String process;
	
	@Transient
	@Getter
	@Setter
	private boolean modificado;
}
