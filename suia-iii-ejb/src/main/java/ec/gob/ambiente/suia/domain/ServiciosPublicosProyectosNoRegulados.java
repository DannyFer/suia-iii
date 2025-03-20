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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import lombok.Getter;
import lombok.Setter;

	
	@Table(name = "unregulated_projects_services_public", schema = "suia_iii")
	@Entity
	@NamedQueries({
		@NamedQuery(name = ServiciosPublicosProyectosNoRegulados.FIND_PROYECTO_NO_REGULADOS, query = "SELECT u FROM ServiciosPublicosProyectosNoRegulados u WHERE u.proyectoAmbientalNoRegulado.id = :idProyNoRegul")})
	@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "unpsp_status"))})
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "unpsp_status = 'TRUE'")
public class ServiciosPublicosProyectosNoRegulados extends EntidadBase {

	    
		private static final long serialVersionUID = -3526840459286211L;
		public static final String FIND_PROYECTO_NO_REGULADOS = "ec.gob.ambiente.suia.domain.ServiciosPublicosProyectosNoRegulados.findProyectoNoRegulado";
			   

	    @Getter
	    @Setter
	    @Id
	    @SequenceGenerator(name = "ServiciosPublicosProyectosNoRegulados_generator", initialValue = 1, sequenceName = "seq_unpsp_id", schema = "suia_iii", allocationSize = 1)
	   	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ServiciosPublicosProyectosNoRegulados_generator")
	    @Column(name = "unpsp_id")
	    private Integer id;
	    
	    @Getter
		@Setter
		@Column(name = "unpsp_description")
		private String descripcion;
	    
	    @Getter
		@Setter
		@Column(name = "unpsp_educational_establishments")
		private String establecimientosEducacionales;
	    
	    @Getter
		@Setter
		@Column(name = "unpsp_health_center")
		private String centroSalud;
	    
	    @Getter
		@Setter
		@Column(name = "unpsp_means_communication_transport")
		private String mediosComunicacionTransporte;
	    
	    @Getter
		@Setter
		@Column(name = "unpsp_business_institutional_establishments")
		private String establecimientosComercialesIntitucionales;
	    	      
	    @ManyToOne
		@JoinColumn(name = "unep_id")
		@ForeignKey(name = "fk_unregulated_projects_service_public_unpsp_id_unep_id")
	    @Getter
		@Setter
		private ProyectoAmbientalNoRegulado proyectoAmbientalNoRegulado;
	    
	    @Override
		public String toString() {
			return getDescripcion();
		}
	  

	}


