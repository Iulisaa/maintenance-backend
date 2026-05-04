package swbg.solutions.com.maintenanceapp.email

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.util.Base64

@Component
class BrevoClient(
    @Value("\${brevo.api-key}")
    private val apiKey: String,

    @Value("\${brevo.base-url:https://api.brevo.com/v3}")
    private val baseUrl: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val fromEmail = "carp.iulisa@gmail.com"
    private val fromName = "SWBG Solutions"

    private val toEmail = "carp.iulisa@gmail.com"
    private val toName = "Test Recipient"

    private val restClient: RestClient = RestClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("api-key", apiKey)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    fun sendPdfEmail(
        subject: String,
        htmlContent: String,
        pdfFileName: String,
        pdfBytes: ByteArray
    ): String? {
        require(subject.isNotBlank()) { "Subject is required" }
        require(htmlContent.isNotBlank()) { "HTML content is required" }
        require(pdfFileName.isNotBlank()) { "PDF file name is required" }
        require(pdfBytes.isNotEmpty()) { "PDF bytes cannot be empty" }
        log.info(
            "BrevoClient initialized. apiKeyPresent={}, apiKeyPrefix={}",
            apiKey.isNotBlank(),
            apiKey.take(8)
        )
        val request = SendEmailRequest(
            sender = EmailContact(
                name = fromName,
                email = fromEmail
            ),
            to = listOf(
                EmailContact(
                    name = toName,
                    email = toEmail
                )
            ),
            subject = subject,
            htmlContent = htmlContent,
            attachment = listOf(
                EmailAttachment(
                    name = pdfFileName,
                    content = Base64.getEncoder().encodeToString(pdfBytes)
                )
            )
        )

        val response = restClient.post()
            .uri("/smtp/email")
            .body(request)
            .retrieve()
            .body(SendEmailResponse::class.java)

        log.info(
            "Brevo PDF email sent. from={}, to={}, fileName={}, messageId={}",
            fromEmail,
            toEmail,
            pdfFileName,
            response?.messageId
        )

        return response?.messageId
    }

    private data class SendEmailRequest(
        val sender: EmailContact,
        val to: List<EmailContact>,
        val subject: String,
        val htmlContent: String,
        val attachment: List<EmailAttachment>
    )

    private data class EmailContact(
        val name: String? = null,
        val email: String
    )

    private data class EmailAttachment(
        val name: String,
        val content: String
    )

    private data class SendEmailResponse(
        val messageId: String? = null
    )
}
