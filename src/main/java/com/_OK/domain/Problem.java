package com._OK.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "problems")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdfDocumentId")
    private PDFDocument pdfDocument;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false)
    private String answer;

    @Column(nullable = false, length = 20)
    private String type;   // fill_blank, short_answer, multiple_choice

    @Column(length = 20)
    private String difficulty; // easy, medium, hard

    private String language; // ko, en, etc.

    private LocalDateTime createdAt;
}
