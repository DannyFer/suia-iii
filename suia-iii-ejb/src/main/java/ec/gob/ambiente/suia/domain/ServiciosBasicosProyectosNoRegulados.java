package ec.gob.ambiente.suia.domain;


import ec.gob.ambiente.suia.domain.base.EntidadBase;

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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import lombok.Getter;
import lombok.Setter;

	
	@Table(name = "unregulated_projects_services_basic", schema = "suia_iii")
	@Entity
	@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "unpsp_status"))})
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "unpsb_status = 'TRUE'")
public class ServiciosBasicosProyectosNoRegulados extends EntidadBase {

	    
		private static final long serialVersionUID = 1222448338338518996L;

		@Getter
	    @Setter
	    @Id
	    @SequenceGenerator(name = "ServiciosBasicosProyectosNoRegulados_generator", initialValue = 1, sequenceName = "seq_unpsb_id", schema = "suia_iii", allocationSize = 1)
	   	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ServiciosBasicosProyectosNoRegulados_generator")
	    @Column(name = "unpsb_id")
	    private Integer id;
	   	   
	    
	    @Getter
		@Setter
		@Column(name = "unpsb_light_coverager_rural", precision=8, scale=2)
		private double luzCoberturaRural;
	    
	    @Getter
		@Setter
		@Column(name = "unpsb_light_coverager_urbana", precision=8, scale=2)
		private double luzCoberturaUrbana;
	    
	    @Getter
		@Setter
		@Column(name = "unpsb_light_rate_rural", precision=8, scale=2)
		private double luzTarifaRural;
	    
	    @Getter
		@Setter
		@Column(name = "unpsb_light_rate_urbana", precision=8, scale=2)
		private double luzTarifaUrbana;
	    
	    
	    //////////////////////////////////////////////////////
	    @Getter
		@Setter
		@Column(name = "unpsb_water_coverager_rural", precision=8, scale=2)
		private double aguaCoberturaRural;
	    
	    @Getter
		@Setter
		@Column(name = "unpsb_water_coverager_urbana", precision=8, scale=2)
		private double aguaCoberturaUrbana;
	    
	    @Getter
		@Setter
		@Column(name = "unpsb_water_rate_rural", precision=8, scale=2)
		private double aguaTarifaRural;
	    
	    @Getter
		@Setter
		@Column(name = "unpsb_water_rate_urbana", precision=8, scale=2)
		private double aguaTarifaUrbana;
	    ///////////////////////////////////////////////////////
	    @Getter
		@Setter
		@Column(name = "unpsb_sewer_coverager_rural", precision=8, scale=2)
		private double alcantarilladoCoberturaRural;
	    
	    @Getter
		@Setter
		@Column(name = "unpsb_sewer_coverager_urbana", precision=8, scale=2)
		private double alcantarilladoCoberturaUrbana;
	    
	    @Getter
		@Setter
		@Column(name = "unpsb_sewer_rate_rural", precision=8, scale=2)
		private double alcantarilladoTarifaRural;
	    
	    @Getter
		@Setter
		@Column(name = "unpsb_sewer_rate_urbana", precision=8, scale=2)
		private double alcantarilladoTarifaUrbana;
	    
	    ///////////////////////////////////////////////////////////////////
	    @Getter
		@Setter
		@Column(name = "unpsb_phone_coverager_rural", precision=8, scale=2)
		private double telefonoCoberturaRural;
	    
	    @Getter
		@Setter
		@Column(name = "unpsb_phone_coverager_urbana", precision=8, scale=2)
		private double telefonoCoberturaUrbana;
	    
	    @Getter
		@Setter
		@Column(name = "unpsb_phone_rate_rural", precision=8, scale=2)
		private double telefonoTarifaRural;
	    
	    @Getter
		@Setter
		@Column(name = "unpsb_phone_rate_urbana", precision=8, scale=2)
		private double telefonoTarifaUrbana;
	    
	    ///////////////////////////////////////////////////////////////////
	    	      
	    @ManyToOne
		@JoinColumn(name = "unep_id")
		@ForeignKey(name = "fk_unregulated_projects_service_public_unpsp_id_unep_id")
	    @Getter
		@Setter
		private ProyectoAmbientalNoRegulado proyectoAmbientalNoRegulado;
	    
	}