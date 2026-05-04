package swbg.solutions.com.maintenanceapp.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import swbg.solutions.com.maintenanceapp.dto.CreateEngineerRequest
import swbg.solutions.com.maintenanceapp.dto.EngineerResponse
import swbg.solutions.com.maintenanceapp.dto.UpdateEngineerRequest
import swbg.solutions.com.maintenanceapp.service.EngineerService
import java.util.*

@RestController
@RequestMapping("/api/engineers")
class EngineerController(
    private val engineerService: EngineerService
) {

    @GetMapping
    fun getAll(
        @RequestParam(required = false) active: Boolean?
    ): List<EngineerResponse> {
        return engineerService.getAll(active)
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID
    ): EngineerResponse {
        return engineerService.getByIdResponse(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody @Valid request: CreateEngineerRequest
    ): EngineerResponse {
        return engineerService.create(request)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid request: UpdateEngineerRequest
    ): EngineerResponse {
        return engineerService.update(id, request)
    }

    @PatchMapping("/{id}/activate")
    fun activate(
        @PathVariable id: UUID
    ): EngineerResponse {
        return engineerService.activate(id)
    }

    @PatchMapping("/{id}/deactivate")
    fun deactivate(
        @PathVariable id: UUID
    ): EngineerResponse {
        return engineerService.deactivate(id)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: UUID
    ) {
        engineerService.deactivate(id)
    }
}