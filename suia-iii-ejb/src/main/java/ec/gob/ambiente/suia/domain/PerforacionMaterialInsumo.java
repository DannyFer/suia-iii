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
@Table(name = "scdr_material_supplies", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "masu_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "masu_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "masu_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "masu_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "masu_user_update"))})
@Filter(name = EntidadAuditable.FILTER_ACTIVE, condition = "masu_status = 'TRUE'")
public class PerforacionMaterialInsumo extends EntidadAuditable{

	private static final long serialVersionUID = 3370601747414648626L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="masu_id")
	@Getter
    @Setter
	private Integer id;
	
    @Getter
    @Setter
    @Column(name="scdr_id")
    private Integer perforacionExplorativa;
	
	@Getter
    @Setter
    @Column(name="masu_material")
    private String material;
	@Getter
    @Setter
    @Column(name="masu_unit")
    private String unit;
	@Getter
    @Setter
    @Column(name="masu_process")
    private String process;
	@Getter
    @Setter
    @Column(name="masu_casonu")
    private String casonu;
	@Getter
    @Setter
    @Column(name="masu_value")
    private Double value;	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="dach_id")
	@ForeignKey(name = "fk_scdr_material_supplies_dan_chem_subs")
	private SustanciaQuimicaPeligrosa sustanciaQuimicaPeligrosa;
	@Getter
    @Setter
    @Column(name="masu_other_material")
    private String otraSustancia;
	
	@Transient
	@Getter
	@Setter
	private boolean modificado;
	
}
