package org.johdan.user.data.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.johdan.user.enums.BusStatus;
import org.johdan.user.enums.BusType;

@Entity
@Getter
@Setter
@Table(name = "buses")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String busNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusStatus status;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

}
