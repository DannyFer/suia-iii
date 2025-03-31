package ec.gob.ambiente.suia.domain;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
@Table(name = "plan_record_details",schema="suia_iii")
@AttributeOverrides({
@AttributeOverride(name = "estado", column = @Column(name = "prde_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prde_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = DetalleFichaPlan.LISTAR_POR_ID_FICHA, query = "SELECT d FROM DetalleFichaPlan d WHERE d.estado=true AND d.fichaAmbientalPma.id = :fichaId order by d.id"),
	@NamedQuery(name = DetalleFichaPlan.LISTAR_POR_ID_FICHA_MINERIA, query = "SELECT d FROM DetalleFichaPlan d WHERE d.estado=true AND d.fichaAmbientalMineria.id = :fichaId order by d.id")})
public class DetalleFichaPlan extends EntidadBase implements Serializable {
	private static final long serialVersionUID = 4699975911360179627L;
	
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_ID_FICHA = PAQUETE+ "DetalleFichaPlan.findByIdFicha";
	public static final String LISTAR_POR_ID_FICHA_MINERIA = PAQUETE+ "DetalleFichaPlan.findByIdFichaMineria";

	@Id
    @Column(name = "prde_id")
    @SequenceGenerator(name = "PLAN_RECORD_DETAILS_GENERATOR", initialValue = 1, sequenceName = "seq_prde_id", schema = "suia_iii", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLAN_RECORD_DETAILS_GENERATOR")
    @Getter
    @Setter
    private Integer id;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name="prde_start_date")
	private Date fechaInicio;
	
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name="prde_end_date")
	private Date fechaFin;
    
    @Getter
	@Setter
	@JoinColumn(name = "plse_id", referencedColumnName = "plse_id")
	@ForeignKey(name = "fk_plan_record_details_plse_id_sector_plan_plse_id")
	@ManyToOne
	private PlanSector planSector;
    
    @Getter
 	@Setter
 	@JoinColumn(name = "cafa_id", referencedColumnName = "cafa_id")
 	@ForeignKey(name = "fk_sector_plan_cafa_id_catii_fapma_cafa_id")
 	@ManyToOne
 	private  FichaAmbientalPma fichaAmbientalPma;
    
    @Getter
 	@Setter
 	@JoinColumn(name = "mien_id", referencedColumnName = "mien_id")
 	@ForeignKey(name = "fk_sector_plan_mien_id_catii_fapma_mien_id")
 	@ManyToOne
 	private  FichaAmbientalMineria fichaAmbientalMineria;

    @Override
    public String toString() {
        if(this.planSector != null){
            return this.planSector.getTipoPlanManejoAmbiental().getTipo();
        }
        return "DetalleFichaPlan{" + "fechaFin=" + fechaFin + '}';
    }
    

}

