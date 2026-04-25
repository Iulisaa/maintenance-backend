package swbg.solutions.com.maintenanceapp.utils

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import swbg.solutions.com.maintenanceapp.entity.MaintenanceReport
import swbg.solutions.com.maintenanceapp.entity.MaintenanceTask
import java.io.ByteArrayOutputStream

@Component
class PdfGenerator(
    private val templateEngine: TemplateEngine
) {

    fun generateMaintenanceReportPdf(
        task: MaintenanceTask,
        report: MaintenanceReport
    ): ByteArray {
        val context = Context().apply {
            setVariable("task", task)
            setVariable("report", report)
        }

        val html = templateEngine.process("maintenance-report", context)

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