package swbg.solutions.com.maintenanceapp.repository

import org.springframework.data.jpa.repository.JpaRepository
import swbg.solutions.com.maintenanceapp.entity.InspectionReport
import java.util.*

interface InspectionReportRepository : JpaRepository<InspectionReport, UUID> {

}