package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="products_pqa", schema="chemical_pesticides")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prod_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prod_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prod_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prod_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prod_user_update")) })

@NamedQueries({
@NamedQuery(name="ProductoPqua.findAll", query="SELECT p FROM ProductoPqua p"),
@NamedQuery(name=ProductoPqua.GET_POR_REGISTRO_PRODUCTO, query="SELECT p FROM ProductoPqua p where p.registroProducto = :registro and p.aprobacionFisica = false and p.estado = true order by 1"),
@NamedQuery(name=ProductoPqua.GET_PENDIENTES_POR_USUARIO_MES, query="SELECT p FROM ProductoPqua p where p.estado = true and p.cedulaRuc = :usuario and p.registroProducto.mesIngreso = :mesIngreso and p.productoActualizado = false and p.registroProducto.estado = true and p.aprobacionFisica = false order by 1"),
@NamedQuery(name=ProductoPqua.GET_REGISTROS_FISICOS, query="SELECT p FROM ProductoPqua p where p.estado = true and p.aprobacionFisica = true  order by 1")
})
public class ProductoPqua extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_POR_REGISTRO_PRODUCTO = PAQUETE + "ProductoPqua.getPorRegistroProducto";
	public static final String GET_PENDIENTES_POR_USUARIO_MES = PAQUETE + "ProductoPqua.getPendientesPorUsuarioMes";
	public static final String GET_REGISTROS_FISICOS = PAQUETE + "ProductoPqua.getRegistrosFisicos";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prod_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prod_ruc")
	private String cedulaRuc;

	@Getter
	@Setter
	@Column(name="prod_operator_name")
	private String nombreOperador;

	@Getter
	@Setter
	@Column(name="prod_legal_representative")
	private String representanteLegal;

	@Getter
	@Setter
	@Column(name="prod_register_number")
	private String numeroRegistro;
	
	@Getter
	@Setter
	@Column(name="prod_register_date")
	private Date fechaRegistro;

	@Getter
	@Setter
	@Column(name="prod_trade_name_product")
	private String nombreComercialProducto;

	@Getter
	@Setter
	@Column(name="prod_product_composition")
	private String composicionProducto;

	@Getter
	@Setter
	@Column(name="prod_value")
	private String valor;

	@Getter
	@Setter
	@Column(name="prod_unit_measurement")
	private String unidadMedida;

	@Getter
	@Setter
	@Column(name="prod_formulation")
	private String formulacion;

	@Getter
	@Setter
	@Column(name="prod_formulation_abbreviation")
	private String abreviaturaFormulacion;

	@Getter
	@Setter
	@Column(name="prod_toxicological_category")
	private String categoriaToxicologica;
	
	@Getter
	@Setter
	@Column(name="prod_extension")
	private Boolean prorroga;

	@Getter
	@Setter
	@Column(name="prod_status_updated")
	private Boolean productoActualizado;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id")	
	private CatalogoGeneralCoa colorFranja;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="prre_id")
	private RegistroProducto registroProducto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id_product_type_final")
	private CatalogoGeneralCoa tipoProducto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id_container_type_final")
	private CatalogoGeneralCoa tipoRecipiente;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id_toxicological_category_final")
	private CatalogoGeneralCoa categoriaFinal;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id_stripe_color_final")
	private CatalogoGeneralCoa colorFranjaFinal;

	@Getter
	@Setter
	@Column(name="prod_other_product_type")
	private String otroTipoProducto;

	@Getter
	@Setter
	@Column(name="prod_is_physical")
	private Boolean aprobacionFisica = false;

	@Getter
	@Setter
	@Column(name="prod_is_registered_pma")
	private Boolean tieneRegistroPma = false;

	@Getter
	@Setter
	@Transient
	private DocumentoPqua documentoJustificacion;


	public String getTablaComposicionProducto() {
		String tblComposicion = "";
		
		if(this.id != null) {
			String[] composicionList = this.composicionProducto.split(";");
			String[] valorList = this.valor.split(";");
			String[] unidadList = this.unidadMedida.split(";");
			
			tblComposicion = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 60%; border-collapse:collapse;\">";
			
			for (int i = 0; i < composicionList.length; i++) {
				tblComposicion += "<tr>" 
						+ "<td>" + composicionList[i] + "</td>"
						+ "<td>" + valorList[i] + " " + unidadList[i] + "</td>"
						+ "</tr>";
			}
			
			tblComposicion += "</table>";
		}
		
		return tblComposicion;
	}
	
	public String getInfoIngredienteActivo() {
		String ingredienteActivo = "";
		
		if(this.id != null) {
			String[] composicionList = this.composicionProducto.split(";");
			String[] valorList = this.valor.split(";");
			String[] unidadList = this.unidadMedida.split(";");
			
			for (int i = 0; i < composicionList.length; i++) {
				String ingrediente = composicionList[i] + " " + valorList[i] + " " + unidadList[i];
				ingredienteActivo += (ingredienteActivo.equals("")) ? ingrediente : ", " + ingrediente;
			}
		}
		
		return ingredienteActivo;
	}

}