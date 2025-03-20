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
@Table(name = "scdr_schedule_pma", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "scpm_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "scpm_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "scpm_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "scpm_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "scpm_user_update"))})
@Filter(name = EntidadAuditable.FILTER_ACTIVE, condition = "scpm_status = 'TRUE'")
public class PerforacionCronogramaPma extends EntidadAuditable{

	private static final long serialVersionUID = -7282722850470853270L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="scpm_id")
	@Getter
    @Setter
	private Integer id;
	
    @Getter
    @Setter
    @Column(name="scdr_id")
    private Integer perforacionExplorativa;
	
	@ManyToOne
    @JoinColumn(name = "empt_id")
    @ForeignKey(name = "fk_spma_id_to_empt_id")
    @Getter
    @Setter
    private TipoPlanManejoAmbiental TipoPlanManejoAmbiental;
	@Getter
    @Setter
    @Column(name="scpm_month1")
    private Boolean month1;
	@Getter
    @Setter
    @Column(name="scpm_month2")
    private Boolean month2;
	@Getter
    @Setter
    @Column(name="scpm_month3")
    private Boolean month3;
	@Getter
    @Setter
    @Column(name="scpm_month4")
    private Boolean month4;
	@Getter
    @Setter
    @Column(name="scpm_month5")
    private Boolean month5;
	@Getter
    @Setter
    @Column(name="scpm_month6")
    private Boolean month6;
	@Getter
    @Setter
    @Column(name="scpm_month7")
    private Boolean month7;
	@Getter
    @Setter
    @Column(name="scpm_month8")
    private Boolean month8;
	@Getter
    @Setter
    @Column(name="scpm_month9")
    private Boolean month9;
	@Getter
    @Setter
    @Column(name="scpm_month10")
    private Boolean month10;
	@Getter
    @Setter
    @Column(name="scpm_month11")
    private Boolean month11;
	@Getter
    @Setter
    @Column(name="scpm_month12")
    private Boolean month12;
	@Getter
    @Setter
    @Column(name="scpm_budget")
    private Double budget;	
	
}
