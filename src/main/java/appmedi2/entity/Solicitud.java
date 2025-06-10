package appmedi2.entity;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Column(nullable = false)
    private LocalDateTime fechaSolicitud;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    // Texto exacto reconocido por voz
    private String textoReconocido;

    // Enum para estados
    public enum EstadoSolicitud {
        SOLICITADO,
        IMPRESO,
        CANCELADO,
        NO_DISPONIBLE
    }

    // Constructores
    public Solicitud() {
        this.fechaSolicitud = LocalDateTime.now();
    }

    public Solicitud(Medicamento medicamento, String textoReconocido, EstadoSolicitud estado) {
        this();
        this.medicamento = medicamento;
        this.textoReconocido = textoReconocido;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public String getTextoReconocido() {
        return textoReconocido;
    }

    public void setTextoReconocido(String textoReconocido) {
        this.textoReconocido = textoReconocido;
    }

    // toString
    @Override
    public String toString() {
        return "Solicitud{" +
                "id=" + id +
                ", medicamento=" + medicamento +
                ", fechaSolicitud=" + fechaSolicitud +
                ", estado=" + estado +
                ", textoReconocido='" + textoReconocido + '\'' +
                '}';
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Solicitud solicitud = (Solicitud) o;

        return id != null ? id.equals(solicitud.id) : solicitud.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}