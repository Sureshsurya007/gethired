package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "resume_profile")
data class ResumeProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Alex Morgan",
    val targetRole: String = "Associate Software Engineer / Tech Analyst",
    val email: String = "alex.morgan@alumni.edu",
    val phone: String = "(312) 555-0194",
    val location: String = "Chicago, IL (Open to Relocate)",
    val linkedin: String = "linkedin.com/in/alexmorgan-grad",
    val github: String = "github.com/alexmorgan-code",
    val portfolio: String = "alexmorgan.dev",
    val summary: String = "Recent Computer Science & Business Analytics graduate with hands-on internship experience in full-stack mobile development, cloud services, and collaborative agile workflows. Eager to contribute disciplined problem-solving and rapid learning to an innovative entry-level engineering team.",
    val educationJson: String = "",
    val experienceJson: String = "",
    val projectsJson: String = "",
    val skillsTechnical: String = "Kotlin, Jetpack Compose, Java, Python, SQL, REST APIs, Git, Docker, Spring Boot",
    val skillsSoft: String = "Cross-functional Collaboration, Agile / Scrum, Technical Communication, Rapid Prototyping",
    val certifications: String = "AWS Certified Cloud Practitioner, Google Career Certificate: Cybersecurity",
    val lastUpdated: Long = System.currentTimeMillis()
)

data class EducationItem(
    val degree: String,
    val major: String,
    val institution: String,
    val gradYear: String,
    val gpa: String,
    val honors: String
)

data class ExperienceItem(
    val role: String,
    val company: String,
    val location: String,
    val dates: String,
    val highlights: String // newline separated bullet points
)

data class ProjectItem(
    val title: String,
    val techStack: String,
    val description: String,
    val link: String
)

object ResumeConverters {
    fun parseEducation(jsonStr: String): List<EducationItem> {
        if (jsonStr.isBlank()) return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<EducationItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    EducationItem(
                        degree = obj.optString("degree"),
                        major = obj.optString("major"),
                        institution = obj.optString("institution"),
                        gradYear = obj.optString("gradYear"),
                        gpa = obj.optString("gpa"),
                        honors = obj.optString("honors")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun serializeEducation(list: List<EducationItem>): String {
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("degree", it.degree)
            obj.put("major", it.major)
            obj.put("institution", it.institution)
            obj.put("gradYear", it.gradYear)
            obj.put("gpa", it.gpa)
            obj.put("honors", it.honors)
            array.put(obj)
        }
        return array.toString()
    }

    fun parseExperience(jsonStr: String): List<ExperienceItem> {
        if (jsonStr.isBlank()) return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<ExperienceItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ExperienceItem(
                        role = obj.optString("role"),
                        company = obj.optString("company"),
                        location = obj.optString("location"),
                        dates = obj.optString("dates"),
                        highlights = obj.optString("highlights")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun serializeExperience(list: List<ExperienceItem>): String {
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("role", it.role)
            obj.put("company", it.company)
            obj.put("location", it.location)
            obj.put("dates", it.dates)
            obj.put("highlights", it.highlights)
            array.put(obj)
        }
        return array.toString()
    }

    fun parseProjects(jsonStr: String): List<ProjectItem> {
        if (jsonStr.isBlank()) return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<ProjectItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ProjectItem(
                        title = obj.optString("title"),
                        techStack = obj.optString("techStack"),
                        description = obj.optString("description"),
                        link = obj.optString("link")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun serializeProjects(list: List<ProjectItem>): String {
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("title", it.title)
            obj.put("techStack", it.techStack)
            obj.put("description", it.description)
            obj.put("link", it.link)
            array.put(obj)
        }
        return array.toString()
    }
}
