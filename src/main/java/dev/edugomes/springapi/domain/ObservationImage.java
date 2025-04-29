package dev.edugomes.springapi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "observation_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservationImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private Date uploadedAt;
}
