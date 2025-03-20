package ec.gob.ambiente.rcoa.demo.model;

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
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "demo_rangofechas", schema = "public")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "demo_rf_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "demo_rf_fecha_creacion"))
})
@NamedQuery(name = "DemoRangoFechas.findAll", query = "SELECT d FROM DemoRangoFechas d")




public class DemoRangoFechas extends EntidadBase{
	
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "demo_rf_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "demo_rf_fecha_inicio")
	private Date fecha_inicio;
	
	@Getter
	@Setter
	@Column(name = "demo_rf_fecha_fin")
	private Date fecha_fin;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	private UbicacionesGeografica ubicacionesGeografica;
	
	

}
