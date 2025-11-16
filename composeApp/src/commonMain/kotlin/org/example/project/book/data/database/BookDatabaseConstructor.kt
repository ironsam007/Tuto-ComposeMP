package org.example.project.book.data.database

import androidx.room.RoomDatabaseConstructor

/* This is needed in KMP project for Room to work: Call the correct platform code to instantiate the room database
Room generates platform-specific DB implementations, but does not generate the platform-specific code to create the database,
    => so for creation/instantiation: we define an expect constructor in shared code and implement it with actual declarations for each platform,
    while suppressing missing-actual errors until Room code is generated.
*/
@Suppress("NO_ACTUAL_FOR_EXPECT") //prohibit compiler from complaining about "missing actual" at source level cuz room compiler will provide them in generated code (when declare expect, kt requires actual implementation for each target )
expect object BookDatabaseConstructor: RoomDatabaseConstructor<FavoriteBookDatabase> { //instruct Kotlin that There WILL be an implementation of database construction, but it depends on the platform.
    override fun initialize(): FavoriteBookDatabase // initialize out database
}