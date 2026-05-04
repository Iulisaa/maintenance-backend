package swbg.solutions.com.maintenanceapp.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "inspection_reports",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_inspection_reports_report_number",
            columnNames = ["report_number"]
        ),
        UniqueConstraint(
            name = "uk_inspection_reports_storage_path",
            columnNames = ["storage_path"]
        )
    ],
    indexes = [
        Index(name = "idx_inspection_reports_status", columnList = "status"),
        Index(name = "idx_inspection_reports_generated_at", columnList = "generated_at")
    ]
)
class InspectionReport(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "report_number", nullable = false, length = 50)
    var reportNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 30)
    var reportType: InspectionReportType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: InspectionReportStatus = InspectionReportStatus.DRAFT,

    @Column(name = "report_template_code", nullable = false, length = 100)
    var reportTemplateCode: String = "DEFAULT",

    @Column(name = "file_name", length = 255)
    var fileName: String? = null,

    @Column(name = "content_type", length = 100)
    var contentType: String? = null,

    @Column(name = "storage_path", columnDefinition = "TEXT")
    var storagePath: String? = null,

    @Column(name = "file_size_bytes")
    var fileSizeBytes: Long? = null,

    @Column(name = "generated_at")
    var generatedAt: LocalDateTime? = null,

    @Column(name = "finalized_at")
    var finalizedAt: LocalDateTime? = null

) : AuditableEntity()