package swbg.solutions.com.maintenanceapp.entity

enum class EquipmentSeasonType {
    COLD,
    HEAT,
    UNIVERSAL
}

enum class TaskStatus {
    PLANNED,
    ASSIGNED,
    COMPLETED,
    CANCELLED,
    SKIPPED
}

enum class InspectionTaskFilter {
    ALL,
    PLANNED,
    COMPLETED,
    FAILED,
    FOLLOW_UP
}

enum class TaskSource {
    GENERATED,
    MANUAL
}

enum class InspectionResult {
    PASSED,
    FAILED,
    FOLLOW_UP
}

enum class InspectionReportType {
    SINGLE_EQUIPMENT,
    MULTI_EQUIPMENT
}

enum class InspectionReportStatus {
    DRAFT,
    FINALIZED
}
