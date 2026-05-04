package swbg.solutions.com.maintenanceapp.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import swbg.solutions.com.maintenanceapp.common.exception.NotFoundException
import swbg.solutions.com.maintenanceapp.dto.CreateEngineerRequest
import swbg.solutions.com.maintenanceapp.dto.EngineerResponse
import swbg.solutions.com.maintenanceapp.dto.UpdateEngineerRequest
import swbg.solutions.com.maintenanceapp.entity.Engineer
import swbg.solutions.com.maintenanceapp.mapper.toResponse
import swbg.solutions.com.maintenanceapp.repository.EngineerRepository
import java.util.*

@Service
class EngineerService(
    private val engineerRepository: EngineerRepository,
) {

    fun getById(id: UUID): Engineer {
        return engineerRepository.findById(id)
            .orElseThrow { NotFoundException("Engineer $id not found") }
    }

    fun getByIdResponse(id: UUID): EngineerResponse {
        return getById(id).toResponse()
    }

    fun getAll(active: Boolean? = null): List<EngineerResponse> {
        val engineers = when (active) {
            null -> engineerRepository.findAll()
            true -> engineerRepository.findAllByActiveTrue()
            false -> engineerRepository.findAllByActiveFalse()
        }

        return engineers.map { it.toResponse() }
    }

    fun create(request: CreateEngineerRequest): EngineerResponse {
        require(request.name.isNotBlank()) { "Engineer name is required." }
        require(request.email.isNotBlank()) { "Engineer email is required." }
        require(request.maxTasksPerDay > 0) { "Max tasks per day must be greater than zero." }

        val engineer = Engineer(
            fullName = request.name.trim(),
            email = request.email.trim(),
            active = true,
            maxTasksPerDay = request.maxTasksPerDay
        )

        return engineerRepository.save(engineer).toResponse()
    }

    @Transactional
    fun update(id: UUID, request: UpdateEngineerRequest): EngineerResponse {
        validateEngineerRequest(
            fullName = request.fullName,
            email = request.email,
            maxTasksPerDay = request.maxTasksPerDay
        )

        val normalizedEmail = request.email.trim().lowercase()

        if (engineerRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, id)) {
            throw IllegalArgumentException("Engineer with email $normalizedEmail already exists")
        }

        val engineer = getById(id)

        engineer.fullName = request.fullName.trim()
        engineer.email = normalizedEmail
        engineer.active = request.active
        engineer.maxTasksPerDay = request.maxTasksPerDay

        return engineerRepository.save(engineer).toResponse()
    }

    @Transactional
    fun activate(id: UUID): EngineerResponse {
        val engineer = getById(id)
        engineer.active = true
        return engineerRepository.save(engineer).toResponse()
    }

    @Transactional
    fun deactivate(id: UUID): EngineerResponse {
        val engineer = getById(id)
        engineer.active = false
        return engineerRepository.save(engineer).toResponse()
    }

    fun getMaxTasksPerDay(engineerId: UUID): Int {
        return getById(engineerId).maxTasksPerDay
    }

    private fun validateEngineerRequest(
        fullName: String,
        email: String,
        maxTasksPerDay: Int
    ) {
        require(fullName.isNotBlank()) {
            "fullName must not be blank"
        }

        require(email.isNotBlank()) {
            "email must not be blank"
        }

        require(maxTasksPerDay > 0) {
            "maxTasksPerDay must be greater than 0"
        }
    }

}
