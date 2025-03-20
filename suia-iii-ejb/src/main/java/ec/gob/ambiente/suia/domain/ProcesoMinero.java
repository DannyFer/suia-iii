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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "mipr_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mipr_status = 'TRUE'")
@Table(name = "mining_processes", schema = "suia_iii")
public class ProcesoMinero extends EntidadBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = -128933203336960736L;    

    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "MINING_PROCESSES_ID_GENERATOR", sequenceName = "seq_mipr_id", schema = "suia_iii")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MINING_PROCESSES_ID_GENERATOR")
    @Column(name = "mipr_id")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "mipr_nombre")
    private String nombre;
    
    @Getter
    @Setter
    @Column(name = "mipr_descripcion")
    private String descripcion;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "tymc_id", referencedColumnName="tymc_id")
    @ForeignKey(name = "fk_mining_processes_tymc_id_type_material_catalog_tymc_id")
    private CatalogoTipoMaterial catalogoTipoMaterial;

}
