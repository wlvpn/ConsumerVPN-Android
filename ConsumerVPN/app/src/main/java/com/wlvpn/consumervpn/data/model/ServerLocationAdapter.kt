package com.wlvpn.consumervpn.data.model

import com.google.gson.*
import com.wlvpn.consumervpn.domain.model.ServerLocation
import java.lang.reflect.Type

/**
    This adapter is used for Gson Serialization / Deserialization, making
    possible to store a specific ServerLocation data polymorphic models.
    It is worth to note that this approach is a strategic provided by
    Gson lib to work with polymorphism and objects serialization
 */
class ServerLocationAdapter :
    JsonSerializer<ServerLocation>,
    JsonDeserializer<ServerLocation> {

    override fun serialize(
        src: ServerLocation, typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {

        val retValue = JsonObject()
        val className = src::class.java.canonicalName
        retValue.addProperty(CLASSNAME, className)
        val elem = context.serialize(src)
        retValue.add(INSTANCE, elem)
        return retValue
    }

    override fun deserialize(json: JsonElement?,
                             typeOfT: Type?,
                             context: JsonDeserializationContext?): ServerLocation {
        val jsonObject = json!!.asJsonObject
        val className = jsonObject.get(CLASSNAME).asString
        val klass = try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            throw JsonParseException(e.message)
        }
        return context!!.deserialize(jsonObject.get(INSTANCE), klass)
    }

    companion object {
        private const val CLASSNAME = "CLASSNAME"
        private const val INSTANCE = "INSTANCE"
    }
}