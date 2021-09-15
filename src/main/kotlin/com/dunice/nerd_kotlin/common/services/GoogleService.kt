package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.errors.CustomError
import com.dunice.nerd_kotlin.common.types.GoogleSheetFields
import com.dunice.nerd_kotlin.common.types.SpreadSheetCardInfo
import com.dunice.nerd_kotlin.common.utils.DateTimeUtils
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Sheet
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.FileInputStream
import java.lang.RuntimeException
import java.util.stream.Collectors
import javax.annotation.PostConstruct

@Component
class GoogleService{

    @Value("\${service.account.path}")
    lateinit var serviceAccountAddress : String

    @Value("\${google.spreadsheet.id}")
    lateinit var spreadsheetId : String

    @Value("#{\${interviewerAlias}}")
    lateinit var interviewerAlias : Map<String, String>

    lateinit var service: Sheets

    @PostConstruct
    private fun init() {
        createCredentials()

//        println(getInformation(DateTimeUtils().getNumberOfWeek().toString()))
    }

    fun createCredentials() {
        service = Sheets.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            HttpCredentialsAdapter(GoogleCredentials.fromStream(FileInputStream(serviceAccountAddress)))
        )
            .build()
    }

    fun getParticularSheet(sheetName : String): Sheet {
        val allSheets: ArrayList<Sheet> = service.spreadsheets().get(spreadsheetId).execute().getValue("sheets") as ArrayList<Sheet>
        for (sheet in allSheets) {
            if (sheet.properties.title.contains(sheetName)) return sheet
        }
        throw CustomError("Sheet is not found")
    }

    fun readData(sheet: Sheet): MutableList<MutableList<Any>>?  =
        service.spreadsheets().values().get(spreadsheetId, sheet.properties.title).execute().getValues()

    fun getInformation(
        currentWeekNumber: String
    ): List<SpreadSheetCardInfo> {
        val data = this.readData(this.getParticularSheet("(w$currentWeekNumber)"))
            ?: throw CustomError("Sheet data not found!")

        return data.drop(1)
            .stream()
            .filter { it[GoogleSheetFields.PARTICIPANT_FULL_NAME.index] != "" }
            .map {
                val interviewer = interviewerAlias[it[GoogleSheetFields.INTERVIEWER_FULL_NAME.index]]
                    ?: throw CustomError("Alias for interviewer ${it[GoogleSheetFields.INTERVIEWER_FULL_NAME.index]} not found!")
                val assistant = interviewerAlias[it[GoogleSheetFields.ASSISTANT_NAME.index]]

                if ((it[GoogleSheetFields.ASSISTANT_NAME.index] != "") and (assistant == null))
                    throw CustomError("Alias for assistant ${it[GoogleSheetFields.ASSISTANT_NAME.index]} not found!")

                return@map SpreadSheetCardInfo(
                    it[GoogleSheetFields.DEPARTMENT.index].toString(),
                    it[GoogleSheetFields.DATE.index].toString(),
                    it[GoogleSheetFields.TIME.index].toString(),
                    it[GoogleSheetFields.PARTICIPANT_FULL_NAME.index].toString(),
                    it[GoogleSheetFields.SUBJECT.index].toString(),
                    it[GoogleSheetFields.ROOM.index].toString(),
                    interviewer,
                    assistant
                )
            }
            .collect(Collectors.toList())
    }

}
