package andres.rangel.degreeprojects.models

import java.io.Serializable

data class Project(
    var name: String = "",
    var tools: String = "",
    var description: String = "",
    var emailOne: String = "",
    var emailTwo: String = "",
    var createdBy: String = "",
    var status: StatusProject = StatusProject.UNAPPROVED
): Serializable