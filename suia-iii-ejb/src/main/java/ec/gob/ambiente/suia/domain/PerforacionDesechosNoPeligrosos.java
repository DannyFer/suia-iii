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
@Table(name = "scdr_nonhazardous_waste", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "nowa_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "nowa_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "nowa_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "nowa_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "nowa_user_update"))})
@Filter(name = EntidadAuditable.FILTER_ACTIVE, condition = "nowa_status = 'TRUE'")
public class PerforacionDesechosNoPeligrosos extends EntidadAuditable{


	private static final long serialVersionUID = -986859692989012919L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="nowa_id")
	@Getter
    @Setter
	private Integer id;
	
    @Getter
    @Setter
    @Column(name="scdr_id")
    private Integer perforacionExplorativa;
	
	@Getter
    @Setter
    @Column(name="nowa_waste_type")
    private String wasteType;
	@Getter
    @Setter
    @Column(name="nowa_unit")
    private String unit;
	@Getter
    @Setter
    @Column(name="nowa_treatment")
    private String treatment;
	@Getter
    @Setter
    @Column(name="nowa_final_arrangement")
    private String finalArrangement;
	@Getter
    @Setter
    @Column(name="nowa_value")
    private Double value;
	
	@Transient
	@Getter
	@Setter
	private boolean modificado;

}
