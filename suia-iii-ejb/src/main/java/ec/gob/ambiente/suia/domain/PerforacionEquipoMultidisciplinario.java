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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


@Entity
@Table(name = "scdr_team_multidisciplinary", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "temu_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "temu_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "temu_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "temu_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "temu_user_update"))})
@Filter(name = EntidadAuditable.FILTER_ACTIVE, condition = "temu_status = 'TRUE'")
public class PerforacionEquipoMultidisciplinario extends EntidadAuditable{

	private static final long serialVersionUID = -2342671987784876785L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="temu_id")
	@Getter
    @Setter
	private Integer id;	
	
    @Getter
    @Setter
	@Column(name = "scdr_id")
    private Integer perforacionExplorativa;
	
	@Getter
	@Setter
	@Column(name = "temu_name")
	private String name;
	
	@Getter
	@Setter
	@Column(name = "temu_vocational_training")
	private String vocationalTraining;
	
	@Getter
	@Setter
	@Column(name = "temu_component")
	private String component;	

}
