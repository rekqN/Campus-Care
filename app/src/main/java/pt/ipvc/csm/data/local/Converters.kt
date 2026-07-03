package pt.ipvc.csm.data.local

import androidx.room.TypeConverter
import pt.ipvc.csm.model.RequestStatus
import pt.ipvc.csm.model.Role

/** Stores the enums as their names in TEXT columns. */
class Converters {
    @TypeConverter fun fromRole(role: Role): String = role.name
    @TypeConverter fun toRole(value: String): Role = Role.valueOf(value)

    @TypeConverter fun fromStatus(status: RequestStatus): String = status.name
    @TypeConverter fun toStatus(value: String): RequestStatus = RequestStatus.valueOf(value)
}
