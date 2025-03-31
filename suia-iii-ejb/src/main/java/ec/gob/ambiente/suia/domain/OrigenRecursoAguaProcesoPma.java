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
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;


/**
 * The persistent class for the fapma_process_water_origin database table.
 * 
 */
@Entity
@Table(name="fapma_process_water_origin", schema = "suia_iii")
@NamedQuery(name="OrigenRecursoAguaProcesoPma.findAll", query="SELECT o FROM OrigenRecursoAguaProcesoPma o")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fapwo_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fapwo_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fapwo_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fapwo_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fapwo_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapwo_status = 'TRUE'")
public class OrigenRecursoAguaProcesoPma extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "PROCCES_WATER_ORIGIN_PMA_FAPDID_GENERATOR", sequenceName = "fapma_process_water_origin_fapr_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROCCES_WATER_ORIGIN_PMA_FAPDID_GENERATOR")
	@Column(name="fapwo_id")
	@Getter
	@Setter
	private Integer id;
		
	@Column(name="fapwo_river_volume")
	@Getter
	@Setter
	private Integer volumenRios;
	
	@Column(name="fapwo_lake_volume")
	@Getter
	@Setter
	private Integer volumenLagos;
	
	@Column(name="fapwo_lagoon_volume")
	@Getter
	@Setter
	private Integer volumenLagunas;
	
	@Column(name="fapwo_well_volume")
	@Getter
	@Setter
	private Integer volumenPozos;
		
	@Column(name="fapwo_packaged_water_volume")
	@Getter
	@Setter
	private Integer volumenAguaEnvasada;
		
	@Column(name="fapwo_rainwater_volume")
	@Getter
	@Setter
	private Integer volumenAguaLluvia;
		
	@Column(name="fapwo_seawater_volume")
	@Getter
	@Setter
	private Integer volumenAguaMar;
	
	@Column(name="fapwo_groundwater_volume")
	@Getter
	@Setter
	private Integer volumenAguaSubterranea;
	
	//bi-directional many-to-one association to CatiiFapma
	@ManyToOne
	@JoinColumn(name="cafa_id")
	@ForeignKey(name = "catii_fapma_process_water_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;

}