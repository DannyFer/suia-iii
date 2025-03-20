package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Table(name = "shrimp",schema = "suia_iii")
@Entity
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "shri_status")) }) 
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "shri_status = 'TRUE'")

public class Camaroneras extends EntidadBase{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter
	@Setter
	@Column(name = "shri_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="shri_agreement")
	private String acuerdo;
	
	@Getter
	@Setter
	@Column(name="shri_year")
	private String anio;
	
	@Getter
	@Setter
	@Column(name="shri_extension")
	private double extension;
	
	@Getter
	@Setter
	@Column(name="shri_legal_representative")
	private String repreLeg;
	
	@Getter
	@Setter
	@Column(name="shri_used")
	private boolean usado;
	
	
	
	@Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gelo_id")
    private UbicacionesGeografica ubicaciongeografica;
	
}
