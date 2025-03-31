package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.TipoEstadoFisico;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "receipt_hazardous_waste", schema = "suia_iii")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "rehw_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "rehw_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "rehw_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "rehw_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "rehw_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rehw_status = 'TRUE'")
@NamedQueries({
	@NamedQuery(name = RecepcionDesechoPeligroso.FIND_BY_ID, query = "SELECT mu FROM RecepcionDesechoPeligroso mu WHERE mu.id= :id and estado = true"),
})
public class RecepcionDesechoPeligroso extends EntidadAuditable {

    /**
     *
     */
    private static final long serialVersionUID = -9093561499577523378L;
    
    public static final String FIND_BY_ID = "ec.com.magmasoft.business.domain.RecepcionDesechoPeligroso.findById";

    @Id
    @Column(name = "rehw_id")
    @SequenceGenerator(name = "RECEIPT_HAZARDOUS_WASTE_GENERATOR", sequenceName = "seq_rehw_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECEIPT_HAZARDOUS_WASTE_GENERATOR")
    @Getter
    @Setter
    private Integer id;

    @Column(name = "rehw_feature")
    @Getter
    @Setter
    private String caracteristica;

    @Getter
    @Setter
    @JoinColumn(name = "rehw_physical_state_id", referencedColumnName = "psty_id")
    @ForeignKey(name = "fk_receipt_waste_physical_state_id_phisical_state_typespsty_id")
    @ManyToOne
    private TipoEstadoFisico estadoFisico;

    @Column(name = "rehw_observations")
    @Getter
    @Setter
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "wada_id")
    @ForeignKey(name = "waste_dangerous_receipt_hazardous_waste_fk")
    @Getter
    @Setter
    private DesechoPeligroso desecho;

    @Getter
    @Setter
    @ManyToOne()
    @JoinColumn(name = "apte_id")
    @ForeignKey(name = "fk_receipt_hazardous_waste_apte_id_approval_requirement_apte_id")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
    private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

    public RecepcionDesechoPeligroso() {
    }

    public RecepcionDesechoPeligroso(Integer id) {
        this.id = id;
    }

}
