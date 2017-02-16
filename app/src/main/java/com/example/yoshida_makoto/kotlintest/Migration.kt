package com.example.yoshida_makoto.kotlintest

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

/**
 * Created by yoshida_makoto on 2017/02/14.
 */

class Migration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion = oldVersion
        // During a migration, a DynamicRealm is exposed. A DynamicRealm is an untyped variant of a normal Realm, but
        // with the same object creation and query capabilities.
        // A DynamicRealm uses Strings instead of Class references because the Classes might not even exist or have been
        // renamed.

        // Access the Realm schema in order to create, modify or delete classes and their fields.
        val schema = realm.schema

        /************************************************
         * class UserPlayList                   // add a new model class
         * @Primary Long id;
         * @Required String title;
        */
        // Migrate from version 1 to version 2
        if (oldVersion.equals(2)) {
            // Create a new class
            schema.create("UserPlayList")
                    .addField("id", Long::class.java, FieldAttribute.PRIMARY_KEY)
                    .addField("title", String::class.java, FieldAttribute.REQUIRED)
            oldVersion.inc()
        }

    }
}
