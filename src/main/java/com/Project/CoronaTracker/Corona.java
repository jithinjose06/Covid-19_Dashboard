package com.Project.CoronaTracker;

import lombok.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "corona")
public class Corona {

    @Id
    @Column(name="id",updatable = false,unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String combinedKey;
    private String country;
    private String province;
    private Long active;
    private Long confirmed;
    private Long recovered;
    private Long deaths;
    private LocalDateTime lastUpdate;
    private Double latitude;
    private Double longitude;

}

