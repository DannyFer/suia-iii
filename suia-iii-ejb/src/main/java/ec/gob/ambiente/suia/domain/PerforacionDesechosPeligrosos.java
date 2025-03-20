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
@Table(name = "scdr_special_hazardous_waste", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "sphw_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "sphw_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "sphw_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "sphw_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "sphw_user_update"))})
@Filter(name = EntidadAuditable.FILTER_ACTIVE, condition = "sphw_status = 'TRUE'")
public class PerforacionDesechosPeligrosos extends EntidadAuditable{

	private static final long serialVersionUID = 8411943098278348386L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="sphw_id")
	@Getter
    @Setter
	private Integer id;
	
    @Getter
    @Setter
    @Column(name="scdr_id")
    private Integer perforacionExplorativa;
	@Getter
    @Setter
    @Column(name="sphw_waste_type")
    private String wasteType;
	
	@Getter
    @Setter
    @Column(name="sphw_code")
    private String code;
	@Getter
    @Setter
    @Column(name="sphw_crtib")
    private String crtib;
	@Getter
    @Setter
    @Column(name="sphw_unit")
    private String unit;
	@Getter
    @Setter
    @Column(name="sphw_process")
    private String process;
	@Getter
    @Setter
    @Column(name="sphw_final_arrangement")
    private String finalArrangement;	
	@Getter
    @Setter
    @Column(name="sphw_value")
    private Double value;
	
	@Transient
	@Getter
	@Setter
	private boolean modificado;

}
