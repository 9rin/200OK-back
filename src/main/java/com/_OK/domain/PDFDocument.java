package com._OK.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "PDFDocument")
public class PDFDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pdfDocumentId;

    @Column(nullable = false)
    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String documentSummary;

    private String modelUsed;
    private String processingTime;
    private Integer pdfPagesAnalyzed;
    private Integer questionCount;

    private LocalDateTime createdAt;
}

