package com.dunice.nerd_kotlin.common.services

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.FileInputStream
import javax.annotation.PostConstruct
import javax.management.Query.plus

@Component
class GoogleService{

    @Value("\${service.account.path}")
    val serviceAccountAddress : String = ""

    @Value("\${google.spreadsheet.id}")
    val spreadsheetId : String = ""

    val range = "4 августа (w35)!A1:K2"
    lateinit var service: Sheets

    fun createCredentials() {

        service = Sheets.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            HttpCredentialsAdapter(GoogleCredentials.fromStream(FileInputStream(serviceAccountAddress)))
        )
            .build()

    }

    fun readData(): MutableList<MutableList<Any>>?  =
        service.spreadsheets().values().get(spreadsheetId, range).execute().getValues()

}
