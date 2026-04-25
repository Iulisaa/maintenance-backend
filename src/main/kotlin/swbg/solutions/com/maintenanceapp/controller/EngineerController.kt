package swbg.solutions.com.maintenanceapp.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import swbg.solutions.com.maintenanceapp.dto.CreateEngineerRequest
import swbg.solutions.com.maintenanceapp.dto.EngineerResponse
import swbg.solutions.com.maintenanceapp.service.EngineerService

@RestController
@RequestMapping("/api/engineers")
class EngineerController(
    private val engineerService: EngineerService
) {
    @GetMapping
    fun getAll(): List<EngineerResponse> = engineerService.getAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid request: CreateEngineerRequest): EngineerResponse =
        engineerService.create(request)

}