package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

import java.io.Serializable;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name="detail_pesticide_project", schema = "chemical_pesticides")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "depe_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "depe_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "depe_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "depe_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "depe_user_update")) })

@NamedQueries({
@NamedQuery(name="ProyectoPlaguicidasDetalle.findAll", query="SELECT a FROM ProyectoPlaguicidasDetalle a"),
@NamedQuery(name=ProyectoPlaguicidasDetalle.GET_POR_ID_PROYECTO, query="SELECT a FROM ProyectoPlaguicidasDetalle a where a.proyecto.id = :idProyecto and a.estado = true order by id"),
@NamedQuery(name=ProyectoPlaguicidasDetalle.GET_POR_ID_PRODUCTO, query="SELECT a FROM ProyectoPlaguicidasDetalle a where a.producto.id = :idProducto and a.estado = true order by id")
})

public class ProyectoPlaguicidasDetalle extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_POR_ID_PROYECTO = PAQUETE + "ProyectoPlaguicidasDetalle.getPorIdProyecto";
	public static final String GET_POR_ID_PRODUCTO = PAQUETE + "ProyectoPlaguicidasDetalle.getPorIdProducto";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="depe_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="chpe_id")	
	private ProyectoPlaguicidas proyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="crop_id")
	private Cultivo cultivo;

	@Getter
	@Setter
	@Column(name="depe_scientific_name")
	private String nombreCientificoCultivo;

	@Getter
	@Setter
	@OneToMany(mappedBy = "idDetalleProyecto")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "crpe_status = 'TRUE'")
	private List<PlagaCultivo> listaPlagaCultivo;

	@Getter
	@Setter
	@OneToMany(mappedBy = "idDetalleProyecto")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cudo_status = 'TRUE'")
	private List<DosisCultivo> listaDosisCultivo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="prod_id")	
	private ProductoPqua producto;

	@Getter
	@Setter
	@Column(name="depe_is_physical")
	private Boolean registroFisico = false;

	public String getDetallePlagas() {
		String plagas = "";
		for (PlagaCultivo item : this.listaPlagaCultivo) {
			String dato = item.getNombreComunPlaga() + " <i>(" + item.getPlaga().getNombreCientifico() + ")</i>";
			plagas += (plagas.equals("")) ? dato : "<br/>" + dato;
		}

		return plagas;
	}

	public String getDetalleDosis() {
		String dosis = "";
		for (DosisCultivo item : this.listaDosisCultivo) {
			String dato = item.getDosis() + " " + item.getUnidad();
			dosis += (dosis.equals("")) ? dato : "<br/>" + dato;
		}

		return dosis;
	}

}