package org.johdan.user.data.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "feedback")
public class FeedBack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String busId;

    @Column(nullable = false, length = 1000)
    private String comment;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @PrePersist
    protected void onSubmit() {
        this.submittedAt = LocalDateTime.now();
    }
}
