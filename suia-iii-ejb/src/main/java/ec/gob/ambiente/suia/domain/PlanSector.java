package ec.gob.ambiente.suia.domain;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;

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

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 *
 * @author pganan
 */
@Entity
@Table(name = "plan_sectors",schema="suia_iii")
@NamedQueries({
    @NamedQuery(name = PlanSector.FIND_ID_PLAN, query = "SELECT p FROM PlanSector p WHERE p.tipoPlanManejoAmbiental.id = :planId"),
    @NamedQuery(name = PlanSector.FIND_ID_SECTOR, query = "SELECT p FROM PlanSector p WHERE p.tipoSubsector.id = :sectorId"),
    @NamedQuery(name = PlanSector.FIND_CODIGO_SECTOR, query = "SELECT p FROM PlanSector p WHERE p.tipoSubsector.codigo = :p_codigoSector")
})
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "plse_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "plse_status = 'TRUE'")
public class PlanSector extends EntidadBase implements Serializable {

	private static final long serialVersionUID = -7206970817132951758L;


	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String FIND_ID_PLAN = PAQUETE+ "PlanSector.findByIdPlan";
	public static final String FIND_ID_SECTOR = PAQUETE+ "PlanSector.findByIdSector";
	public static final String FIND_CODIGO_SECTOR = PAQUETE+ "PlanSector.findByCodeSector";
   

    @Id
    @Column(name = "plse_id")
    @SequenceGenerator(name = "PLAN_SECTOR_GENERATOR", initialValue = 1, sequenceName = "seq_plse_id", schema = "suia_iii")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLAN_SECTOR_GENERATOR")
    @Getter
    @Setter
    private Integer id;

    
    @Getter
	@Setter
	@JoinColumn(name = "empt_id", referencedColumnName = "empt_id")
	@ForeignKey(name = "fk_sector_plan_empt_id_environmental_management_plan_types_empt_id")
	@ManyToOne
	private TipoPlanManejoAmbiental tipoPlanManejoAmbiental;
    
    @Getter
 	@Setter
 	@JoinColumn(name = "secl_id", referencedColumnName = "secl_id")
 	@ForeignKey(name = "fk_sector_plan_secl_id_sectors_classification_secl_id")
 	@ManyToOne
 	private  TipoSubsector tipoSubsector;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlanSector other = (PlanSector) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
}

