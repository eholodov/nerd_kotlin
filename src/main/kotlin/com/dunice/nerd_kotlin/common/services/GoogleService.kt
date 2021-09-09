package com.dunice.nerd_kotlin.common.services

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.auth.oauth2.ServiceAccountCredentials
import org.springframework.beans.factory.annotation.Value
import java.io.FileInputStream
import javax.management.Query.plus


class GoogleService{

    @Value("\${service.account.path=/Users/dunice/VSCProjects/nerd_kotlin/ServiceAccount.json}")
    var serviceAccountAddress : String = ""

fun createCredentials() {
    val httpTransport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()


    var credentials: ServiceAccountCredentials = ServiceAccountCredentials.fromStream(FileInputStream(serviceAccountAddress))

}
}
