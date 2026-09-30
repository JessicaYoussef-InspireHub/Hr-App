package net.inspirehub.hr.check_in_out.data

import android.content.Context
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.sign_in.data.Address
import net.inspirehub.hr.sign_in.data.Company

suspend fun checkLocationUpdatesRaw(
    context: Context,
    token: String
): String? {
    return try {
        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()

        val response: HttpResponse =
            httpClient.post("$companyUrl/api/check_location_updates") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "params" to mapOf(
                            "employee_token" to token
                        )
                    )
                )
            }

        val body = response.bodyAsText()
        println("📍 API RESPONSE Update location: $body")

        body

    } catch (e: Exception) {
        println("🔴 ERROR: ${e.message}")
        null
    }
}

/**
 * Asks the server for the work sites and saves them when the server says they changed.
 *
 * Used when the check in/out screen opens and when the company_location_update push
 * arrives. The check in/out screen listens for the saved values, so it follows the new
 * sites by itself; the reminders read them on their next alarm.
 *
 * Returns true only when new sites were saved.
 */
suspend fun refreshCompanyLocations(
    context: Context,
    token: String
): Boolean {

    val response = checkLocationUpdatesRaw(context, token)
    println("📍 FINAL RESPONSE Update location: $response")

    if (response == null) return false

    return try {
        val sharedPref = SharedPrefManager(context)

        val json = org.json.JSONObject(response)
        val result = json.getJSONObject("result")

        val changed = result.optBoolean("changed", false)

        println("Update location: 📦 BEFORE UPDATE:")

        println("Update location: Allowed IDs (old): ${sharedPref.getAllowedLocationsIds()}")
        println("Update location: Companies (old): ${sharedPref.getCompaniesLatLng()}")

        if (!changed) {
            println("Update location:📍 No changes in locations")
            return false
        }

        println("Update location:✅ Locations changed → updating...")

        // ✅ 1. allowed_locations_ids
        val idsJson = result.optJSONArray("allowed_locations_ids")
        val idsList = mutableListOf<Int>()

        if (idsJson != null) {
            for (i in 0 until idsJson.length()) {
                idsList.add(idsJson.getInt(i))
            }
        }

        // ✅ 2. company_locations
        val companiesJson = result.getJSONArray("company_locations")

        val companies = mutableListOf<Company>()

        for (i in 0 until companiesJson.length()) {
            val item = companiesJson.getJSONObject(i)
            val name = item.getString("name")

            val address = item.getJSONObject("address")

            val company = Company(
                name = name,
                address = Address(
                    id = address.getInt("id"),
                    street = address.optString("street", ""),
                    city = address.optString("city", ""),
                    zip = address.optString("zip", ""),
                    country = address.optString("country", ""),
                    latitude = address.getDouble("latitude"),
                    longitude = address.getDouble("longitude"),
                    allowed_distance = address.getDouble("allowed_distance")
                )
            )

            companies.add(company)
        }

        // Saved only after both lists were read, so a broken answer leaves the old sites whole.
        sharedPref.saveAllowedLocationsIds(idsList)
        sharedPref.saveCompaniesLatLng(companies)

        println("Update location: 🆕 AFTER UPDATE:")

        println("Update location: Allowed IDs (new): ${sharedPref.getAllowedLocationsIds()}")
        println("Update location: Companies (new): ${sharedPref.getCompaniesLatLng()}")

        println("Update location: ✅ Locations saved successfully")

        true

    } catch (e: Exception) {
        println("Update location: 🔴 Error parsing update locations: ${e.message}")
        false
    }
}
