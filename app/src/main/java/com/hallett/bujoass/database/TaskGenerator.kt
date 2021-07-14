package com.hallett.bujoass.database

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.domain.Scope
import com.hallett.bujoass.domain.ScopeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import kotlin.math.max

class TaskGenerator(
    private val taskDao: BujoTaskDao,
) {

    private companion object {
        const val NUM_TASKS = 100
        const val DAYS_SPREAD = 30
        const val PERCENT_DAY_SCOPE = 0.50
        const val PERCENT_WEEK_SCOPE = 0.30
        const val PERCENT_MONTH_SCOPE = 0.1
        val PERCENT_NULL_SCOPE = max(1 - (PERCENT_DAY_SCOPE + PERCENT_WEEK_SCOPE + PERCENT_MONTH_SCOPE), 0.00)
    }

    private val mutableCalendar: Calendar
        get() = Calendar.getInstance()

    private fun getTaskForScope(type: ScopeType): BujoTaskEntity {
        val dayOffset = (0..DAYS_SPREAD).random()
        val date = mutableCalendar.apply {
            add(Calendar.DAY_OF_YEAR, dayOffset)
        }.time

        return BujoTaskEntity(
            taskName = sampleTasks.random(),
            scope = Scope.newInstance(type, date)
        )
    }

    suspend fun generateTasks() = withContext(Dispatchers.IO){
        val entityList = mutableListOf<BujoTaskEntity>()

        repeat((NUM_TASKS * PERCENT_NULL_SCOPE).toInt()) {
            entityList.add(
                BujoTaskEntity(
                    taskName = sampleTasks.random(),
                )
            )
        }
        repeat((NUM_TASKS * PERCENT_DAY_SCOPE).toInt()) {
            entityList.add(
                getTaskForScope(ScopeType.DAY)
            )
        }
        repeat((NUM_TASKS * PERCENT_WEEK_SCOPE).toInt()) {
            entityList.add(
                getTaskForScope(ScopeType.WEEK)
            )
        }
        repeat((NUM_TASKS * PERCENT_MONTH_SCOPE).toInt()) {
            entityList.add(
                getTaskForScope(ScopeType.MONTH)
            )
        }

        taskDao.clearTable()
        taskDao.insertAll(entityList)
    }


    private val sampleTasks = listOf(
        "Design the solution",
        "Identify resources to be monitored.",
        "Define users and workflow",
        "Identify event sources by resource type.",
        "Define the relationship between resources and business systems.",
        "Identify tasks and URLs by resource type.",
        "Define the server configuration.",
        "Prepare for implementation	Identify the implementation team.",
        "Order the server hardware for production as well as test/quality assurance (QA).",
        "Order console machines.",
        "Order prerequisite software.",
        "Identify the test LPAR.",
        "Identify production LPARs.",
        "Schedule changes as required.",
        "Create user IDs and groups.",
        "Prepare the test/QA environment	Install test and QA servers and prerequisite software.",
        "Install console machines and prerequisite software.",
        "Verify connectivity from test and QA servers to test LPAR, Tivoli Enterprise Console(R) server, and console machines.",
        "Install the product in the test/QA environment.",
        "Install Tivoli Business Systems Manager and appropriate patches on test or QA servers.",
        "Install Tivoli Business Systems Manager on console machines.",
        "Install event enablement on the Tivoli Enterprise Console server.",
        "Install Tivoli Business Systems Manager and appropriate maintenance on the test LPAR.",
        "Create configuration level objects for the test LPAR.",
        "Configure servers, Source/390 on the test LPAR, event enablement on the Tivoli Enterprise Console server, and verify connectivity.",
        "Implement distributed data feeds.",
        "Extend the data model.",
        "Configure the instance placement.",
        "Configure the Tivoli Enterprise Console rules to send events.",
        "Associate tasks and URLs with object types.",
        "Implement Source/390 data feeds on the test LPAR",
        "Configure filtering, if appropriate.",
        "Perform discovery, if required.",
        "Configure the event source.",
        "Verify the event flow.",
        "Implement a business system in the test/QA environment.",
        "Design a relatively simple business system.",
        "Create the Automated Business Systems configuration file and XML definitions for the business system.",
        "Test the Automated Business Systems file and XML definitions to verify resource inclusion and placement.",
        "Schedule jobs",
        "Source/390 rediscoveries",
        "Database backup and maintenance",
        "Install console machines and prerequisite software.",
        "Verify connectivity from production servers to the production LPAR, Tivoli Enterprise Console server, and console machines.",
        "Install the product in the production environment.	Install Tivoli Business Systems Manager and appropriate patches on production servers.",
        "Install Tivoli Business Systems Manager on console machines.",
        "Install event enablement on the Tivoli Enterprise Console server.",
        "Install Tivoli Business Systems Manager and appropriate maintenance on the production LPARs.",
        "Create configuration level objects for the production LPARs.",
        "Configure servers, Source/390 on the production LPARs, event enablement on the Tivoli Enterprise Console server, and verify connectivity.",
        "Implement distributed data feeds in the production environment.",
        "Extend the data model.",
        "Configure the instance placement.",
        "Configure the Tivoli Enterprise Console rule to send events.",
        "Associate tasks and URLs with object types.",
        "Implement Source/390 data feeds in the production environment.",
        "Configure filtering, if appropriate.",
        "Perform discovery, if required.",
        "Configure the event source.",
        "Verify the event flow.",
        "Implement a business system in the production environment.",
        "Create the Automated Business Systems configuration file and XML definitions for the business system.",
        "Test the Automated Business Systems file and XML definitions to verify resource inclusion and placement.",
        "Install the history server.",
        "Create databases on the history server.",
        "Set up and test jobs on the database server to produce the database backup.",
        "Set up and test jobs to copy backup databases to the history server.",
        "Set up and test jobs to replicate events to the history server.",
        "Install the Health Monitor.",
        "Install the Tivoli Business Systems Manager health monitor software.",
        "Customize the health monitor to match your environment.",
        "Test the health monitor client functions.",
        "Enable the problem/change interface",
        "Install your request processor on the Tivoli Business Systems Manager database server for use by the problem and change request processing function.",
        "Update the SystemConfiguration table to reflect your request processor names along with processing options for the request processors.",
        "Optionally, update the TSD_SCIM table to specify resource options for problem ticket creation.",
        "Create and present administrator and operator training information	Consider training a key group and have them train their peers.",
        "Create a solution maintenance plan",
        "Evaluate the addition and deletion of user IDs.",
        "Establish a relationship between Tivoli Business Systems Manager and change management so that as the environment changes, business systems can change with it.",
        "Monitor system performance and adjust hardware as required.",
        "Schedule jobs",
        "Stay in touch",
    )

}