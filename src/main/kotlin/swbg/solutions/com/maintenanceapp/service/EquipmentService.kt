package swbg.solutions.com.maintenanceapp.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import swbg.solutions.com.maintenanceapp.common.exception.NotFoundException
import swbg.solutions.com.maintenanceapp.dto.CreateEquipmentRequest
import swbg.solutions.com.maintenanceapp.dto.EquipmentResponse
import swbg.solutions.com.maintenanceapp.entity.Equipment
import swbg.solutions.com.maintenanceapp.entity.MaintenanceRule
import swbg.solutions.com.maintenanceapp.repository.EquipmentRepository
import swbg.solutions.com.maintenanceapp.repository.MaintenanceRuleRepository
import java.util.*


@Service
class EquipmentService(
    private val equipmentRepository: EquipmentRepository,
    private val maintenanceRuleRepository: MaintenanceRuleRepository,
    private val engineerService: EngineerService

) {
    fun getAll(): List<EquipmentResponse> =
        equipmentRepository.findAll().map { equipment ->
            val rule = maintenanceRuleRepository.findByEquipmentId(requireNotNull(equipment.id))
            equipment.toResponse(rule)
        }

    fun getEntityById(id: UUID): Equipment =
        equipmentRepository.findById(id).orElseThrow { NotFoundException("Equipment $id not found") }

    fun getRuleByEquipmentId(equipmentId: UUID): MaintenanceRule =
        maintenanceRuleRepository.findByEquipmentId(equipmentId)
            ?: throw NotFoundException("Maintenance rule for equipment $equipmentId not found")

    fun getActiveRules(): List<MaintenanceRule> = maintenanceRuleRepository.findAllByEquipmentActiveTrue()

    @Transactional
    fun create(request: CreateEquipmentRequest): EquipmentResponse {
        val engineer = engineerService.getById(request.assignedEngineerId)

        val equipment = equipmentRepository.save(
            Equipment(
                name = request.name,
                code = request.code,
                active = request.active,
                seasonType = request.seasonType,
                serialNumber = request.serialNumber,
                notes = request.notes,
                assignedEngineer = engineer
            )
        )

        val rule = maintenanceRuleRepository.save(
            MaintenanceRule(
                equipment = equipment,
                recurrencePerYear = request.recurrencePerYear,
                estimatedDurationMinutes = request.estimatedDurationMinutes,
                reportTemplateCode = request.reportTemplateCode
            )
        )

        return equipment.toResponse(rule)
    }

    private fun Equipment.toResponse(rule: MaintenanceRule?) =
        EquipmentResponse(
            id = requireNotNull(id),
            name = name,
            code = code,
            active = active,
            seasonType = seasonType,
            serialNumber = serialNumber,
            notes = notes,
            assignedEngineerId = requireNotNull(assignedEngineer.id),
            assignedEngineerName = assignedEngineer.fullName,
            recurrencePerYear = rule?.recurrencePerYear,
            estimatedDurationMinutes = rule?.estimatedDurationMinutes,
            activeMonths = seasonType.activeMonths.map { it.value },
            reportTemplateCode = rule?.reportTemplateCode
        )

}