package swbg.solutions.com.maintenanceapp.service

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import swbg.solutions.com.maintenanceapp.common.exception.NotFoundException
import swbg.solutions.com.maintenanceapp.dto.CreateEquipmentRequest
import swbg.solutions.com.maintenanceapp.dto.EquipmentResponse
import swbg.solutions.com.maintenanceapp.dto.UpdateEquipmentRequest
import swbg.solutions.com.maintenanceapp.entity.Equipment
import swbg.solutions.com.maintenanceapp.entity.EquipmentActiveMonth
import swbg.solutions.com.maintenanceapp.entity.EquipmentActiveMonthId
import swbg.solutions.com.maintenanceapp.entity.EquipmentSeasonType
import swbg.solutions.com.maintenanceapp.mapper.toMonthNumbers
import swbg.solutions.com.maintenanceapp.mapper.toResponse
import swbg.solutions.com.maintenanceapp.repository.EquipmentActiveMonthRepository
import swbg.solutions.com.maintenanceapp.repository.EquipmentCategoryRepository
import swbg.solutions.com.maintenanceapp.repository.EquipmentRepository
import java.util.*

@Service
class EquipmentService(
    private val equipmentRepository: EquipmentRepository,
    private val equipmentActiveMonthRepository: EquipmentActiveMonthRepository,
    private val equipmentCategoryRepository: EquipmentCategoryRepository,
    private val engineerService: EngineerService,
    private val eventPublisher: ApplicationEventPublisher
) {

    fun getAll(active: Boolean? = null): List<EquipmentResponse> {
        val equipments = when (active) {
            null -> equipmentRepository.findAll()
            true -> equipmentRepository.findAllByActiveTrue()
            false -> equipmentRepository.findAllByActiveFalse()
        }

        return equipments.map { equipment ->
            val equipmentId = requireNotNull(equipment.id)

            val activeMonths = equipmentActiveMonthRepository
                .findAllByEquipmentId(equipmentId)
                .toMonthNumbers()

            equipment.toResponse(activeMonths)
        }
    }

    fun getById(id: UUID): EquipmentResponse {
        val equipment = getEntityById(id)

        val activeMonths = equipmentActiveMonthRepository
            .findAllByEquipmentId(id)
            .toMonthNumbers()

        return equipment.toResponse(activeMonths)
    }

    @Transactional
    fun activate(id: UUID): EquipmentResponse {
        val equipment = getEntityById(id)
        equipment.active = true

        val savedEquipment = equipmentRepository.save(equipment)

        val activeMonths = equipmentActiveMonthRepository
            .findAllByEquipmentId(id)
            .toMonthNumbers()

        return savedEquipment.toResponse(activeMonths)
    }

    @Transactional
    fun deactivate(id: UUID): EquipmentResponse {
        val equipment = getEntityById(id)
        equipment.active = false

        val savedEquipment = equipmentRepository.save(equipment)

        val activeMonths = equipmentActiveMonthRepository
            .findAllByEquipmentId(id)
            .toMonthNumbers()

        return savedEquipment.toResponse(activeMonths)
    }

    fun getEntityById(id: UUID): Equipment {
        return equipmentRepository.findById(id)
            .orElseThrow { NotFoundException("Equipment $id not found") }
    }

    fun getAllActive(): List<Equipment> {
        return equipmentRepository.findAllByActiveTrue()
    }

    @Transactional
    fun create(request: CreateEquipmentRequest): EquipmentResponse {
        validateEquipmentRequest(request)

        val defaultEngineer = request.defaultEngineerId
            ?.let { engineerService.getById(it) }

        val normalizedActiveMonths = normalizeActiveMonths(
            seasonType = request.seasonType,
            requestedMonths = request.activeMonths
        )
        val category = equipmentCategoryRepository.findById(request.categoryId)
            .orElseThrow { IllegalArgumentException("Equipment category not found.") }
        val equipment = equipmentRepository.save(
            Equipment(
                name = request.name.trim(),
                code = request.code.trim(),
                active = request.active,
                seasonType = request.seasonType,
                frequencyPerYear = request.frequencyPerYear,
                estimatedDurationMinutes = request.estimatedDurationMinutes,
                serialNumber = request.serialNumber?.trim(),
                notes = request.notes?.trim(),
                defaultEngineer = defaultEngineer,
                category = category,
            )
        )

        saveActiveMonths(
            equipment = equipment,
            activeMonths = normalizedActiveMonths
        )

        eventPublisher.publishEvent(
            EquipmentCreatedEvent(
                equipmentId = requireNotNull(equipment.id)
            )
        )

        return equipment.toResponse(normalizedActiveMonths)
    }

    @Transactional
    fun update(id: UUID, request: UpdateEquipmentRequest): EquipmentResponse {
        validateEquipmentRequest(request)

        val equipment = getEntityById(id)
        val category = equipmentCategoryRepository.findById(request.categoryId)
            .orElseThrow {
                IllegalArgumentException("Equipment category not found: ${request.categoryId}")
            }
        val defaultEngineer = request.defaultEngineerId
            ?.let { engineerService.getById(it) }

        val normalizedActiveMonths = normalizeActiveMonths(
            seasonType = request.seasonType,
            requestedMonths = request.activeMonths
        )

        equipment.name = request.name.trim()
        equipment.code = request.code.trim()
        equipment.active = request.active
        equipment.category = category
        equipment.seasonType = request.seasonType
        equipment.frequencyPerYear = request.frequencyPerYear
        equipment.estimatedDurationMinutes = request.estimatedDurationMinutes
        equipment.serialNumber = request.serialNumber?.trim()
        equipment.notes = request.notes?.trim()
        equipment.defaultEngineer = defaultEngineer

        val savedEquipment = equipmentRepository.save(equipment)

        equipmentActiveMonthRepository.deleteAllByEquipmentId(id)

        saveActiveMonths(
            equipment = savedEquipment,
            activeMonths = normalizedActiveMonths
        )

        return savedEquipment.toResponse(normalizedActiveMonths)
    }

    private fun saveActiveMonths(
        equipment: Equipment,
        activeMonths: Set<Int>
    ) {
        val rows = activeMonths.map { month ->
            EquipmentActiveMonth(
                id = EquipmentActiveMonthId(monthNumber = month),
                equipment = equipment
            )
        }

        equipmentActiveMonthRepository.saveAll(rows)
    }

    private fun normalizeActiveMonths(
        seasonType: EquipmentSeasonType,
        requestedMonths: Set<Int>
    ): Set<Int> {
        return when (seasonType) {
            EquipmentSeasonType.UNIVERSAL -> (1..12).toSet()
            EquipmentSeasonType.COLD,
            EquipmentSeasonType.HEAT -> requestedMonths
        }
    }

    private fun validateEquipmentRequest(request: CreateEquipmentRequest) {
        validateEquipmentFields(
            name = request.name,
            code = request.code,
            frequencyPerYear = request.frequencyPerYear,
            estimatedDurationMinutes = request.estimatedDurationMinutes,
            seasonType = request.seasonType,
            activeMonths = request.activeMonths
        )
    }

    private fun validateEquipmentRequest(request: UpdateEquipmentRequest) {
        validateEquipmentFields(
            name = request.name,
            code = request.code,
            frequencyPerYear = request.frequencyPerYear,
            estimatedDurationMinutes = request.estimatedDurationMinutes,
            seasonType = request.seasonType,
            activeMonths = request.activeMonths
        )
    }

    private fun validateEquipmentFields(
        name: String,
        code: String,
        frequencyPerYear: Int,
        estimatedDurationMinutes: Int,
        seasonType: EquipmentSeasonType,
        activeMonths: Set<Int>
    ) {
        require(name.isNotBlank()) {
            "name must not be blank"
        }

        require(code.isNotBlank()) {
            "code must not be blank"
        }

        require(frequencyPerYear > 0) {
            "frequencyPerYear must be greater than 0"
        }

        require(estimatedDurationMinutes > 0) {
            "estimatedDurationMinutes must be greater than 0"
        }

        require(activeMonths.all { it in 1..12 }) {
            "activeMonths must contain only values between 1 and 12"
        }

        if (seasonType != EquipmentSeasonType.UNIVERSAL) {
            require(activeMonths.isNotEmpty()) {
                "activeMonths must not be empty for $seasonType equipment"
            }
        }
    }

    data class EquipmentCreatedEvent(
        val equipmentId: UUID
    )

}
