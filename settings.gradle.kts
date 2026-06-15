pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "AuraMusicNative"
include(":app")
include(":core")
include(":core-ui")
include(":core-design")
include(":core-domain")
include(":core-database")
include(":core-media")
include(":core-network")
include(":core-common")
include(":feature-home")
include(":feature-library")
include(":feature-search")
include(":feature-player")
include(":feature-playlists")
include(":feature-downloads")
include(":feature-settings")
include(":feature-lyrics")
include(":sync")
include(":analytics")
