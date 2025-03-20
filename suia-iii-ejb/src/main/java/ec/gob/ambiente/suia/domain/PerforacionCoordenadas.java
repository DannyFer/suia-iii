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
@Table(name = "scdr_coordinate", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "scco_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "scco_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "scco_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "scco_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "scco_user_update"))})
@Filter(name = EntidadAuditable.FILTER_ACTIVE, condition = "scco_status = 'TRUE'")
public class PerforacionCoordenadas extends EntidadAuditable{

	private static final long serialVersionUID = 4871447483220331751L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="scco_id")
	@Getter
    @Setter
	private Integer id;	
	
	@ManyToOne
    @JoinColumn(name = "scdr_id")
    @ForeignKey(name = "scdr_id_fk")
    @Getter
    @Setter
    private PerforacionExplorativa perforacionExplorativa;
	
	@Getter
	@Setter
	@Column(name = "scco_x")
	private Double x;

	@Getter
	@Setter
	@Column(name = "scco_y")
	private Double y;
	
	@Getter
	@Setter
	@Column(name = "scco_order")
	private Integer orden;
	
}
