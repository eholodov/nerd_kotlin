package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.types.GoogleSheetFields
import com.dunice.nerd_kotlin.common.types.SpreadSheetCardInfo
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
import javax.annotation.PostConstruct

@Component
class GoogleService{

    @Value("\${service.account.path}")
    val serviceAccountAddress : String = ""

    @Value("\${google.spreadsheet.id}")
    val spreadsheetId : String = ""

    @Value("#{\${interviewerAlias}}")
    var interviewerAlias : Map<String, String> = mapOf()

    lateinit var service: Sheets

    @PostConstruct
    fun init() {
        createCredentials()
        getInformation("35")
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
        var allSheets: ArrayList<Sheet> = service.spreadsheets().get(spreadsheetId).execute().getValue("sheets") as ArrayList<Sheet>
        for (sheet in allSheets) {
            if (sheetName in sheet.properties.title) return sheet
        }
        throw RuntimeException()
    }

    fun readData(sheet: Sheet): MutableList<MutableList<Any>>?  =
        service.spreadsheets().values().get(spreadsheetId, sheet.properties.title).execute().getValues()

    fun getInformation(
        currentWeekNumber: String
    ) {
        var data = this.readData(this.getParticularSheet("(w$currentWeekNumber)"))
        var listOfSpreadSheets = ArrayList<SpreadSheetCardInfo>()
        if (data != null) {
            for (singleRow in data.drop(1)) {
                singleRow[GoogleSheetFields.PARTICIPANT_FULL_NAME.index].let {
                    val interviewer = interviewerAlias[singleRow[GoogleSheetFields.INTERVIEWER_FULL_NAME.index]]
                    val assistant = interviewerAlias[singleRow[GoogleSheetFields.ASSISTANT_NAME.index]]
//TODO Change Errors
                    if (interviewer == null) throw RuntimeException()

                    if ((singleRow[GoogleSheetFields.ASSISTANT_NAME.index] != null) and (assistant == null))
                        throw RuntimeException()

                    listOfSpreadSheets.add(
                        SpreadSheetCardInfo(
                            singleRow[GoogleSheetFields.DEPARTMENT.index].toString(),
                            singleRow[GoogleSheetFields.DATE.index].toString(),
                            singleRow[GoogleSheetFields.TIME.index].toString(),
                            singleRow[GoogleSheetFields.PARTICIPANT_FULL_NAME.index].toString(),
                            singleRow[GoogleSheetFields.SUBJECT.index].toString(),
                            singleRow[GoogleSheetFields.ROOM.index].toString(),
                            singleRow[GoogleSheetFields.INTERVIEWER_FULL_NAME.index].toString(),
                            singleRow[GoogleSheetFields.ASSISTANT_NAME.index].toString()
                        )
                    )
                }

            }
                println()
        }

    }

}
