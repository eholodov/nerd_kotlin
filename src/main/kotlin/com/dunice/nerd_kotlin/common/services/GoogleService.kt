package com.dunice.nerd_kotlin.common.services

import com.google.api.client.auth.oauth2.Credential
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.Collections
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp




@Service
class GoogleService {

    @Value("\${service.account.path}")
    lateinit var serviceAccountPath: String

    private val APPLICATION_NAME = "Test app"

    private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()

    private val SCOPES = listOf(SheetsScopes.SPREADSHEETS_READONLY)

    private val TOKENS_DIRECTORY_PATH = "tokens"


    fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        println(serviceAccountPath)
        val `in`: InputStream = GoogleService::class.java.getResourceAsStream(serviceAccountPath)
            ?: throw FileNotFoundException("Resource not found: $serviceAccountPath")


        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        println("Test ${clientSecrets}")
        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()
        val receiver: LocalServerReceiver = LocalServerReceiver.Builder().setPort(8888).build()

        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }


    fun getSpreadSheetId() {
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        val spreadsheetId = "1VSlFJfLjS8F4rpStJFv6JwppziXEPK0sFo6-As5sdKI"
//        val range = "Class Data!A2:E"
        val service = Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build()
        val response = service.spreadsheets().values()[spreadsheetId, null]
            .execute()
        val values = response.getValues()
        if (values == null || values.isEmpty()) {
            println("No data found.")
        } else {
            println("Name, Major")
            for (row in values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s, %s\n", row[0], row[4])
            }
        }
    }



}