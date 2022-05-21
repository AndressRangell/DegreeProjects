package andres.rangel.degreeprojects

import android.Manifest
import android.content.Context
import android.net.Uri
import pub.devrel.easypermissions.EasyPermissions

class Utils {

    companion object {
        var imageUri: Uri? = null
        var email: String? = null
        var name: String? = null
        var document: String? = null
        var phone: String? = null
        var career: String? = null

        fun hasStoragePermissions(context: Context) =
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
    }

}