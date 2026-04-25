package swbg.solutions.com.maintenanceapp.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import swbg.solutions.com.maintenanceapp.utils.EquipmentSeasonType
import java.time.Month

@Component
@ConfigurationProperties(prefix = "app.maintenance")
data class MaintenancePlanningProperties(
    val seasonActiveMonthsOverride: Map<EquipmentSeasonType, Set<Month>> = emptyMap()
)
