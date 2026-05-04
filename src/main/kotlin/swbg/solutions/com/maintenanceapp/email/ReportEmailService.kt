package swbg.solutions.com.maintenanceapp.email

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import swbg.solutions.com.maintenanceapp.entity.InspectionReportItem

@Service
class ReportEmailService(
    private val brevoClient: BrevoClient
) {
    private val log = LoggerFactory.getLogger(ReportEmailService::class.java)
    fun sendInspectionReportEmail(
        items: List<InspectionReportItem>,
        pdfFileName: String,
        pdfBytes: ByteArray
    ) {
        val subject = "Proces verbal inspectie"

        val htmlContent = """
            <p>Buna ziua,</p>
            <p>Va transmitem atasat procesul verbal de inspectie generat in sistem.</p>
            <p><strong>Fisier:</strong> ${escapeHtml(pdfFileName)}</p>
            <p>Cu stima,<br/>KYS Maintenance</p>
        """.trimIndent()

        val messageId = brevoClient.sendPdfEmail(
            subject = subject,
            htmlContent = htmlContent,
            pdfFileName = pdfFileName,
            pdfBytes = pdfBytes
        )

        log.info(
            "Inspection report email sent. hardcodedRecipient=true, fileName={}, sizeBytes={}, messageId={}",
            pdfFileName,
            pdfBytes.size,
            messageId
        )
    }

    private fun escapeHtml(value: String): String {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}
