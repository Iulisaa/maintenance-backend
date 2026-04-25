package swbg.solutions.com.maintenanceapp.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import swbg.solutions.com.maintenanceapp.common.exception.NotFoundException
import swbg.solutions.com.maintenanceapp.dto.CreateEngineerRequest
import swbg.solutions.com.maintenanceapp.dto.EngineerResponse
import swbg.solutions.com.maintenanceapp.dto.UpdateEngineerRequest
import swbg.solutions.com.maintenanceapp.entity.Engineer
import swbg.solutions.com.maintenanceapp.repository.EngineerRepository
import java.util.*

@Service
class EngineerService(
    private val engineerRepository: EngineerRepository,
) {
    fun getAll(): List<EngineerResponse> =
        engineerRepository.findAll().map { it.toResponse() }

    fun getById(id: UUID): Engineer =
        engineerRepository.findById(id).orElseThrow { NotFoundException("Engineer $id not found") }

    @Transactional
    fun create(request: CreateEngineerRequest): EngineerResponse {
        require(request.maxTasksPerDay > 0) { "maxTasksPerDay must be greater than 0" }

        return engineerRepository.save(
            Engineer(
                fullName = request.fullName,
                email = request.email,
                active = request.active,
                maxTasksPerDay = request.maxTasksPerDay
            )
        ).toResponse()
    }

    @Transactional
    fun update(id: UUID, request: UpdateEngineerRequest): EngineerResponse {
        require(request.maxTasksPerDay > 0) { "maxTasksPerDay must be greater than 0" }

        val engineer = getById(id)
        engineer.fullName = request.fullName
        engineer.email = request.email
        engineer.active = request.active
        engineer.maxTasksPerDay = request.maxTasksPerDay

        return engineerRepository.save(engineer).toResponse()
    }

    fun getMaxTasksPerDay(engineerId: UUID): Int =
        getById(engineerId).maxTasksPerDay

    private fun Engineer.toResponse() =
        EngineerResponse(
            id = requireNotNull(id),
            fullName = fullName,
            email = email,
            active = active,
            maxTasksPerDay = maxTasksPerDay
        )
}