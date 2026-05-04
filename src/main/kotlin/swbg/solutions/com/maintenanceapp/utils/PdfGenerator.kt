package swbg.solutions.com.maintenanceapp.utils

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import swbg.solutions.com.maintenanceapp.entity.InspectionReport
import swbg.solutions.com.maintenanceapp.entity.InspectionReportItem
import java.io.ByteArrayOutputStream

@Component
class PdfGenerator(
    private val templateEngine: TemplateEngine
) {
    private val log = LoggerFactory.getLogger(PdfGenerator::class.java)
    fun generateInspectionReportPdf(
        report: InspectionReport,
        items: List<InspectionReportItem>
    ): ByteArray {
        require(items.isNotEmpty()) {
            "Cannot generate inspection report PDF without report items"
        }

        val context = Context().apply {
            setVariable("report", report)
            setVariable("items", items)
        }

        val html = templateEngine.process("maintenance-report", context)

        if (html.isBlank()) {
            error("Generated inspection report HTML is blank")
        }

        log.debug("Generated inspection report HTML: {}", html)

        return renderHtmlToPdf(html)
    }

    private fun renderHtmlToPdf(html: String): ByteArray {
        val outputStream = ByteArrayOutputStream()

        PdfRendererBuilder()
            .useFastMode()
            .withHtmlContent(html, null)
            .toStream(outputStream)
            .run()
        return outputStream.toByteArray()
    }
}
